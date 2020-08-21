
package com.asiainfo.veris.crm.order.soa.person.busi.np.npoutapply.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.np.npoutapply.order.requestdata.NpOutApplyReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: NpOutApplyTrade.java
 * @Description: 携出申请登记类
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-5 上午10:08:32 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-5 lijm3 v1.0.0 修改原因
 */
public class NpOutApplyTrade extends BaseTrade implements ITrade
{


    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        NpOutApplyReqData reqData = (NpOutApplyReqData) btd.getRD();
        System.out.println("测试结果返回");

        createNpTradeData(btd);
        // SELECT TO_DATE('2014-05-19 14:50:50', 'yyyy-mm-dd hh24:mi:ss') + 10 / 24 / 60 + 1 / 240 FROM DUAL;
        // TO_DATE(:VEXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') + :VRSRV_NUM1 / 24 / 60 + 1 / 240
        int rsrvNum1 = Integer.parseInt(reqData.getRsrvNum1());
        String execTime = SysDateMgr.getOtherSecondsOfSysDate(60);
        if("AUTHCODE_REQ".equals(reqData.getAuthTag())){        	
        	execTime = SysDateMgr.END_DATE_FOREVER;
        }
        btd.getMainTradeData().setExecTime(execTime);
        String rsrvStr1 = reqData.getRsrvStr1();
        if("AUTOSTOP".equals(rsrvStr1))
        {
        	btd.getMainTradeData().setRsrvStr1("1");
        }
        else
        {
        	btd.getMainTradeData().setRsrvStr1("0");        	
        }
        AddTransferJob(btd);
        sendNpApplySms(btd);


    }

    public void createNpTradeData(BusiTradeData btd) throws Exception
    {

        NpOutApplyReqData reqData = (NpOutApplyReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();

        NpTradeData nptd = new NpTradeData();
        nptd.setUserId(reqData.getUca().getUserId());
        nptd.setTradeTypeCode("41");
        nptd.setNpServiceType(reqData.getNpServiceType());
        nptd.setSerialNumber(serialNumber);
        nptd.setFlowId(reqData.getFlowId());
        nptd.setMessageId(reqData.getMessageId());
        nptd.setBrcId("");
        nptd.setMsgCmdCode(reqData.getCommandCode());
        nptd.setMd5("");
        nptd.setPortInNetid(reqData.getPortInNetID());
        nptd.setPortOutNetid(reqData.getPortOutNetID());
        nptd.setHomeNetid(reqData.getHomeNetID());

        String prepayTag = "0";
        String bNpCardType = prepayTag == "0" ? "20000000" : "10000000";

        nptd.setBNpCardType(bNpCardType);
        nptd.setANpCardType("");// 携入后用户类型：采用八位数字编码，用来标识用户号码携带前后的用户类型，例如用户为预付费还是后付费。其中第一位为付费方式标识（0表示本字段无效，1为预付费，2为后付费，3-9备用）；第2~8位备用，默认填为0。
        nptd.setCustName(reqData.getCustName());
        nptd.setCredType(reqData.getCredType());
        nptd.setPsptId(reqData.getPsptId());
        nptd.setPhone(reqData.getPhone());
        nptd.setActorCustName(reqData.getActorCustName());
        nptd.setActorCredType(reqData.getActorCredType());
        nptd.setActorPsptId(reqData.getActorPsptId());
        nptd.setNpStartDate("");
        nptd.setCreateTime(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        nptd.setSendTimes("0");
        nptd.setBookSendTime(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        nptd.setResultCode("200");
        if("AUTHCODE_REQ".equals(reqData.getAuthTag())){ 
        	//老流程中，工信部发起的携出申请响应时回复的ACK会将状态改为000，所以这里需要将状态做修改与老流程区分开
        	nptd.setState("222");        	
        }else{        	
        	nptd.setState("210");
        }
        nptd.setCancelTag("0");
        nptd.setResultMessage("成功");
        nptd.setRsrvStr3(reqData.getRsrvStr3());
        String saveflg = reqData.getRsrvStr1();
		
		if ("NOSTOPOUT".equals(saveflg)) 
		{
			nptd.setRsrvStr1("NOSTOPOUT");
		} 
		else if ("AUTOPASS".equals(saveflg)) 
		{
			nptd.setRsrvStr1("AUTOPASS");
		}
		else if ("ALLPASS".equals(saveflg)) 
		{
			nptd.setRsrvStr1("ALLPASS");
		}
		else 
		{
			nptd.setRsrvStr1("AUTOSTOP");
			nptd.setResultCode("611");
			nptd.setResultMessage(reqData.getRsrvStr2());
		}
        // nptd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        // nptd.setNpTag("3");
        // nptd.setApplyDate(reqData.getAcceptTime());
        // nptd.setRemark("携出中");
        // nptd.setNpDestroyTime("");

		//新增受理员工信息 add by dengyi5
        nptd.setUpdateDepartId(CSBizBean.getVisit().getDepartId());
        nptd.setUpdateStaffId(CSBizBean.getVisit().getStaffId());
        nptd.setEparchyCode(CSBizBean.getTradeEparchyCode());
        btd.add(serialNumber, nptd);
    }

    /**
     * @Function: sendNpApplySms
     * @Description: 携出申请短信告知
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月26日 下午4:14:40
     */
    public void sendNpApplySms(BusiTradeData btd) throws Exception
    {
        NpOutApplyReqData reqData = (NpOutApplyReqData) btd.getRD();
        String userId = reqData.getUca().getUserId();
        
        
        IDataset ids=new DatasetList();
        
        /**
         * 
         *REQ201609060018_优化携转短信内容
         * <br/>
         * @author zhuoyingzhi
         * 20161009
         */
        IDataset userCityList=NpOutApplyBean.qryUserCityInfo(userId);
        if(IDataUtil.isNotEmpty(userCityList)){
        	 String userCityCode=userCityList.getData(0).getString("CITY_CODE");
        	 IDataset  commparaByUserCityCode=CommparaInfoQry.getCommpara("CSM", "171", userCityCode, btd.getMainTradeData().getEparchyCode());
        	 if(IDataUtil.isNotEmpty(commparaByUserCityCode)){
        		 ids=commparaByUserCityCode;
        	 }else{
        	      String cityCode = reqData.getUca().getUser().getCityCode();
        	      ids = CommparaInfoQry.getCommpara("CSM", "171", cityCode, btd.getMainTradeData().getEparchyCode());
        	 }
        }
       /*****************end*****************************/
        if (IDataUtil.isNotEmpty(ids))
        {
            String strPortInNetid = reqData.getPortInNetID();
            strPortInNetid = strPortInNetid.substring(0, 3);
            String strPortInNet = "";
            if ("001".equals(strPortInNetid))
            {
                strPortInNet = "中国电信";
            }
            else if ("002".equals(strPortInNetid))
            {
                strPortInNet = "中国移动";
            }
            else if ("003".equals(strPortInNetid))
            {
                strPortInNet = "中国联通";
            }
            else
            {
                strPortInNet = strPortInNetid;
            }

            IDataset custVips = CustVipInfoQry.getCustVipClassByUserId(userId);
            String strClassName = "普通";
            if (IDataUtil.isNotEmpty(custVips))
            {
                strClassName = custVips.getData(0).getString("CLASS_NAME");
            }

            List<NpTradeData> ntdsDatas = btd.get("TF_B_TRADE_NP");
            String strBookSendTime = reqData.getAcceptTime();
            String strCreatTime = reqData.getAcceptTime();
            if(ntdsDatas !=null && ntdsDatas.size()>0){
                strBookSendTime = ntdsDatas.get(0).getBookSendTime();
                strCreatTime = ntdsDatas.get(0).getCreateTime();
            }
            
            String serialNumber = reqData.getUca().getSerialNumber();
            String strSMSContent = "携号转网申请记录：";
            strSMSContent = strSMSContent + "号码（" + serialNumber + "）；姓名（" + reqData.getUca().getCustPerson().getCustName() + "）；携入方（" + strPortInNet + "）；上发时间（" + strBookSendTime + "）；受理时间（" + strCreatTime + "）；大客户级别（" + strClassName + "）。";
            String pre_type= btd.getRD().getPreType();
            String in_mode_code= getVisit().getInModeCode();
            
            boolean isNeedSend=false;
            if(pre_type!=null&&"1".equals(pre_type)){
            	//2、客户发送短信预审核短信预警短信修改为：“号码：xxxx，姓名：xxxx ，于XXX年XX月XX日 XX时XX分XX秒 进行了携转短信预审核，请关注！” 
            	//3、客户通过10086咨询触发的预警短信修改为：：“号码：xxxx，姓名：xxxx ，于XXX年XX月XX日 XX时XX分XX秒 进行了携转业务咨询，请关注！” 
               if("0".equals(in_mode_code)){
            	   strSMSContent = "号码：" + serialNumber + "，姓名：" + reqData.getUca().getCustPerson().getCustName() +" ，于" + strCreatTime + "进行了携转业务咨询，请关注！";
               }else{
            	   isNeedSend=true;
            	   strSMSContent = "号码：" + serialNumber + "，姓名：" + reqData.getUca().getCustPerson().getCustName() + "，于" + strCreatTime + "进行了携转短信预审核，请关注！";
               }
            }
            
            
            for (int i = 0,count=ids.size(); i < count; i++)
            {
                IData data = new DataMap();
                data.put("RECV_OBJECT", ids.getData(i).getString("PARA_CODE1"));
                data.put("NOTICE_CONTENT", strSMSContent);
                data.put("BRAND_CODE", "");
                data.put("RECV_ID", userId);
                data.put("REMARK", "携出申请短信告知");
                SmsSend.insSms(data);
            }
            
       
        }
    }

    public void AddTransferFailJob(BusiTradeData btd) throws Exception
    {

        NpOutApplyReqData reqData = (NpOutApplyReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();
        IDataset ids = CustomerInfoQry.getNPUNotFitUser(serialNumber, serialNumber, serialNumber, serialNumber, serialNumber);
        if (IDataUtil.isNotEmpty(ids))
        {
            IData data = ids.getData(0);
            String userId = data.getString("USER_ID");
            IDataset userScores = AcctCall.queryUserScore(userId);
            if (IDataUtil.isNotEmpty(userScores))
            {
                data.put("SCORE_VALUE", userScores.getData(0).getString("SUM_SCORE"));
            }
            IData productInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(productInfo))
            {
                data.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
                data.put("BRAND_CODE", productInfo.getString("BRAND_CODE"));
                data.put("PORT_OUT_NETID", reqData.getPortInNetID());
                data.put("RERROR_MESSAGE", reqData.getxResultinfo());
                SccCall.createTransferFailJob(data);
            }
        }
    }

    public void AddTransferJob(BusiTradeData btd) throws Exception
    {
        NpOutApplyReqData reqData = (NpOutApplyReqData) btd.getRD();
        String userId = reqData.getUca().getUserId();
        IDataset ids =  new DatasetList();//CustomerInfoQry.getNPUFitUser(userId, userId, userId, userId);
        IData id = new DataMap();
        id.put("SCORE_VALUE","0");
        ids.add(id);
        if (IDataUtil.isNotEmpty(ids))
        {
            IData data = ids.getData(0);
           /* IDataset userScores = AcctCall.queryUserScore(userId);
            if (IDataUtil.isNotEmpty(userScores))
            {
                data.put("SCORE_VALUE", userScores.getData(0).getString("SUM_SCORE"));
            }*/
            data.put("SCORE_VALUE", "");
            IData productInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(productInfo))
            {
                data.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
                data.put("BRAND_CODE", productInfo.getString("BRAND_CODE"));
                data.put("PORT_OUT_NETID", reqData.getPortInNetID());
                data.put("CUST_CODE", reqData.getUca().getSerialNumber());
                data.put("MESSAGE_ID", "");
                SccCall.createTransferJob(data);
            }
        }
    }

}
