
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.EmergencyOpenReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.foregiftmgr.ForeGiftBean;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: GuaranteeOpenOtherTradeAction.java
 * @Description: 担保开机 记录 担保用户和被担保用户，用户后续规则查询
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-03-20 09:59:10
 */
public class GuaranteeOpenOtherTradeAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        EmergencyOpenReqData reqData = (EmergencyOpenReqData) btd.getRD();
        OtherTradeData otherTrade = new OtherTradeData();
        String guaranteeUserId = reqData.getGuaranteeUserId();

        if ("496".equals(btd.getTradeTypeCode()))
        { // 担保开机
            otherTrade.setInstId(SeqMgr.getInstId());
            otherTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTrade.setRsrvValueCode("GUAT");
            otherTrade.setRsrvNum1(reqData.getOpenHours());
            otherTrade.setRemark("客户担保开机信息记录");
            otherTrade.setRsrvValue(reqData.getUca().getSerialNumber());// 被担保开机用户
            otherTrade.setRsrvNum2(reqData.getOpenAmount());
            otherTrade.setRsrvNum3(reqData.getCreditClass());//放置担保客户的星级
            //保存当前用户余额
            ForeGiftBean foreGiftBean = new ForeGiftBean();
            String fee = foreGiftBean.getUserBalance(reqData.getUca().getUser().getUserId());
            otherTrade.setRsrvNum4(fee);
          
        }
        else if ("492".equals(btd.getTradeTypeCode()))
        { // 大客户担保开机
            otherTrade.setRsrvValueCode("DBKJ");
            otherTrade.setRsrvNum1(reqData.getOpenHours());
            otherTrade.setRemark("大客户担保开机信息记录");
            // 查询本月是否有担保开机信息
            IDataset userOtherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(guaranteeUserId, "DBKJ");
            if (IDataUtil.isNotEmpty(userOtherInfos))
            {
                String assureCount = userOtherInfos.getData(0).getString("RSRV_VALUE", "1");
                otherTrade = new OtherTradeData(userOtherInfos.getData(0));
                otherTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
                int count = Integer.parseInt(assureCount) + 1;
                otherTrade.setRsrvValue(String.valueOf(count)); // 记录担保次数
            }
            else
            {
                otherTrade.setInstId(SeqMgr.getInstId());
                otherTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
                otherTrade.setRsrvValue("1"); // 记录担保次数
            }
        }
        else if ("497".equals(btd.getTradeTypeCode()))
        { // 紧急开机
            otherTrade.setInstId(SeqMgr.getInstId());
            otherTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTrade.setRsrvValueCode("EMER");
            otherTrade.setRsrvNum1(reqData.getOpenHours());
            otherTrade.setRsrvNum2(reqData.getOpenAmount());
            otherTrade.setRemark("紧急开机信息记录");
            otherTrade.setRsrvNum5(reqData.getCreditClass());//放置担保客户的星级
            //保存当前用户余额
            ForeGiftBean foreGiftBean = new ForeGiftBean();
            String fee = foreGiftBean.getUserBalance(reqData.getUca().getUser().getUserId());
            otherTrade.setRsrvNum4(fee);
        }

        otherTrade.setUserId(guaranteeUserId);// 担保用户
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(reqData.getGuaranteeUserId());
        if (IDataUtil.isNotEmpty(userInfo))
        {
            otherTrade.setRsrvStr1(userInfo.getString("SERIAL_NUMBER"));// 记录担保用户手机号,用于短信模板取值
        }
        // otherTrade.setRsrvStr2(reqData.getUca().getUserId());//记录被担保用户user_id
        otherTrade.setStartDate(reqData.getAcceptTime());
        otherTrade.setEndDate(SysDateMgr.getLastDateThisMonth());
        btd.add(reqData.getUca().getSerialNumber(), otherTrade);     
        //给担保用户下发短信
        if ("496".equals(btd.getTradeTypeCode()))
        {
        	sendSmsForGuarantee(btd);
        }
        
    }

	/**
	 * @Description：构造担保用户短信内容
	 * @param:@param btd
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-27下午05:12:35
	 */
	private void sendSmsForGuarantee(BusiTradeData btd) throws Exception {
		SmsTradeData std = new SmsTradeData();
		
		String templateId2 = "CRM_SMS_PER_COMM_3151";
        IData templateInfo = TemplateQry.qryTemplateContentByTempateId(templateId2);
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());   
        exector.prepare(btd);// 模板变量解析
        String templateContent = exector.applyTemplate(templateInfo.getString("TEMPLATE_CONTENT1",""));
        
        
        EmergencyOpenReqData reqData = (EmergencyOpenReqData) btd.getRD();
        String guaranteeUserId = reqData.getGuaranteeUserId();
        String recvObject ="";
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(guaranteeUserId);
        if (IDataUtil.isNotEmpty(userInfo))
        {
        	recvObject = userInfo.getString("SERIAL_NUMBER");// 
        }
        std.setSmsNoticeId(SeqMgr.getSmsSendId());
        std.setEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
        std.setBrandCode(btd.getRD().getUca().getBrandCode());
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag("0");
        std.setChanId("11");
        std.setSendObjectCode("6");
        std.setSendTimeCode("1");
        std.setSendCountCode("1");
        std.setRecvObjectType("00");

        std.setRecvId(btd.getRD().getUca().getUserId());
        std.setSmsTypeCode("20");
        std.setSmsKindCode("02");
        std.setNoticeContentType("0");
        std.setReferedCount("0");
        std.setForceReferCount("1");
        std.setForceObject("");
        std.setForceStartTime("");
        std.setForceEndTime("");
        std.setSmsPriority("50");
        std.setReferTime(SysDateMgr.getSysTime());
        std.setReferDepartId(CSBizBean.getVisit().getDepartId());
        std.setReferStaffId(CSBizBean.getVisit().getStaffId());
        std.setDealTime(SysDateMgr.getSysTime());
        std.setDealStaffid(CSBizBean.getVisit().getStaffId());
        std.setDealDepartid(CSBizBean.getVisit().getDepartId());
        std.setDealState("0");// 处理状态，0：未处理
        std.setRemark("担保开机担保用户短信");
        std.setRevc1("");
        std.setRevc2("");
        std.setRevc3("");
        std.setRevc4("");
        std.setMonth(SysDateMgr.getSysTime().substring(5, 7));
        std.setDay(SysDateMgr.getSysTime().substring(8, 10));
        std.setCancelTag("0");

        std.setNoticeContent(templateContent);

        std.setRecvObject(recvObject);// 发送号码
        
        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
		
	}
}
