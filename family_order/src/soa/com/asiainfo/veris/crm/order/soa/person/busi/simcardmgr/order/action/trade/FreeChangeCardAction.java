
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeeTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata.ModifyPhoneCodeReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.requestdata.RestoreUserReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.SimCardBean;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

/**
 * 免费换卡登记 大客户\全球通免费换卡\在网3年以上老客户免费补换卡
 * 
 * @author
 */
public class FreeChangeCardAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        String oldSimCardNo = "";
        String newSimCardNo = "";
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String isScore = "";
        if ("142".equals(tradeTypeCode) || "3821".equals(tradeTypeCode))
        {
            SimCardReqData simCardRD = (SimCardReqData) btd.getRD();
            oldSimCardNo = simCardRD.getOldSimCardInfo().getSimCardNo();
            newSimCardNo = simCardRD.getNewSimCardInfo().getSimCardNo();
            isScore = simCardRD.getIsScore();
        }
        if ("143".equals(tradeTypeCode))
        {
            ModifyPhoneCodeReqData simCardRD = (ModifyPhoneCodeReqData) btd.getRD();
            oldSimCardNo = simCardRD.getOldSimCardInfo().getSimCardNo();
            newSimCardNo = simCardRD.getNewSimCardInfo().getSimCardNo();
        }

        SimCardBean bean = BeanManager.createBean(SimCardBean.class);
        
        IData priceData = new DataMap();
        if ("310".equals(tradeTypeCode) || "3813".equals(tradeTypeCode))
        {
            RestoreUserReqData restoreUserReqData = (RestoreUserReqData) btd.getRD();
            oldSimCardNo = restoreUserReqData.getOldSimCardNo();
            newSimCardNo = restoreUserReqData.getNewSimCardNo();
            if (StringUtils.isEmpty(newSimCardNo))
            {
                return;
            }
            UserTradeData userTradeData = btd.getRD().getUca().getUser();
            String productId = btd.getRD().getUca().getProductId();
            priceData = bean.getSimCardPrice(oldSimCardNo,newSimCardNo,serialNumber,
                    btd.getTradeTypeCode(),userTradeData.toData(),productId);
        }else {
            priceData = bean.getSimCardPrice(oldSimCardNo, newSimCardNo,serialNumber, btd.getTradeTypeCode());
        }


        if ("0".equals(priceData.getString("FEE_TAG")) && StringUtils.isNotEmpty(priceData.getString("RSRV_VALUE_CODE")))
        {// 换卡免费
            OtherTradeData otherTD = new OtherTradeData();
            otherTD.setInstId(SeqMgr.getInstId());
            otherTD.setUserId(btd.getRD().getUca().getUserId());
            otherTD.setRsrvStr3(serialNumber);
            otherTD.setRsrvStr4(oldSimCardNo);
            otherTD.setRsrvStr5(newSimCardNo);
            otherTD.setRsrvStr6(btd.getTradeId());
            otherTD.setStartDate(btd.getRD().getAcceptTime());
            otherTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
            otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTD.setProcessTag("0");
            otherTD.setRsrvValue(priceData.getString("RSRV_VALUE"));
            otherTD.setRsrvValueCode(priceData.getString("RSRV_VALUE_CODE"));
            btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
        }

        if ("0".equals(isScore))
        {// 积分换卡
            IDataset commData = CommparaInfoQry.getCommPkInfo("CSM", "1422", "SCOREFORCHANGECARD", "0898");
            if (IDataUtil.isEmpty(commData))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取积分配置数据失败！");
            }
            ScoreTradeData scoreData = new ScoreTradeData();
            scoreData.setInstId(SeqMgr.getInstId());
            scoreData.setUserId(btd.getRD().getUca().getUserId());
            scoreData.setSerialNumber(serialNumber);
            scoreData.setIdType("0");
            scoreData.setScoreTypeCode("ZZ");
            scoreData.setYearId("ZZZZ");
            scoreData.setStartCycleId("-1");
            scoreData.setRsrvStr1(commData.getData(0).getString("PARA_CODE3"));
            scoreData.setEndCycleId("-1");
            scoreData.setScore(priceData.getString("USER_SCORE"));
            scoreData.setScoreChanged("-" + priceData.getString("NEED_SCORE"));
            scoreData.setValueChanged("0");
            scoreData.setScoreTag("1");
            scoreData.setRuleId(commData.getData(0).getString("PARA_CODE2"));
            scoreData.setActionCount("1");
            scoreData.setResId("");
            scoreData.setGoodsName(commData.getData(0).getString("PARAM_NAME"));
            scoreData.setCancelTag(BofConst.CANCEL_TAG_NO);
            scoreData.setRemark("积分补换卡");
            scoreData.setRsrvStr7(commData.getData(0).getString("PARA_CODE4"));
            IDataset taxset = TradeFeeTaxInfoQry.qryTradeFeeTaxByTradeId(btd.getTradeId());
            if (IDataUtil.isNotEmpty(taxset))
            {
                scoreData.setRsrvStr9(taxset.getData(0).getString("RATE", ""));
            }
            scoreData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            scoreData.setStartDate(btd.getRD().getAcceptTime());
            scoreData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            btd.add(btd.getRD().getUca().getSerialNumber(), scoreData);
            
            //积分兑换短信
            SmsTradeData std = new SmsTradeData();
            
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
            std.setRemark("积分兑换补换卡短信");
            std.setRevc1("");
            std.setRevc2("");
            std.setRevc3("");
            std.setRevc4("");
            std.setMonth(SysDateMgr.getSysTime().substring(5, 7));
            std.setDay(SysDateMgr.getSysTime().substring(8, 10));
            std.setCancelTag("0");

            // 短信截取
            String strContent = "您已成功使用" + priceData.getString("NEED_SCORE") + "积分兑换办理补换卡业务!";
            std.setNoticeContent(strContent);

            std.setRecvObject(btd.getRD().getUca().getSerialNumber());// 发送号码
            
            btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
            
        }
    }
}
