
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestory.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestory.order.requestdata.DestroyUserNowRequestData;

public class AjustFeeAction implements ITradeAction
{

	private static transient Logger logger = Logger.getLogger(AjustFeeAction.class);
    /**
     * 调用账务接口进行调账
     * 将“宽带光猫押金存折”转移到现金存折
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	
    	DestroyUserNowRequestData rd = (DestroyUserNowRequestData)btd.getRD();
    	String serialNumber = rd.getSerialNumberA();
    	
    	if(serialNumber.length()>11)//集团宽带不处理
    		return;
    	
    	String tradeFee = rd.getModemFee();
    	
    	IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
    	if(userInfos.isEmpty()){
    		logger.error("FTTH宽带拆机时，查询用户信息失败！"+serialNumber);
    		return;
    	}
    	 String userId = userInfos.getData(0).getString("USER_ID");
         String custId = userInfos.getData(0).getString("CUST_ID");
         String eparchyCode = userInfos.getData(0).getString("EPARCHY_CODE");
         String cityCode = userInfos.getData(0).getString("CITY_CODE");
         
         
    	IDataset acctInfos = AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
        if(acctInfos.isEmpty()){
        	logger.error("FTTH宽带拆机时，查询账户信息失败！"+serialNumber);
        	return;
        }
    	
        String acctId = acctInfos.getData(0).getString("ACCT_ID");
        
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CUST_ID", custId);
        param.put("ACCT_ID", acctId);
        param.put("TRADE_FEE", tradeFee);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("CITY_CODE", cityCode);
        param.put("SERIAL_NUMBER", serialNumber);
        
        if (!"".equals(tradeFee)&&Integer.parseInt(tradeFee) > 0)
        {
        	if(rd.getModermReturn().equals("0"))//不退光猫 做资金沉淀
        	{
        		param.put("CHANNEL_ID", "15000");	//根据资金沉淀业务设定
        		param.put("PAYMENT_ID", "100021");	//根据资金沉淀业务设定
        		param.put("PAYMENT_OP", "16001");	//根据资金沉淀业务设定
        		IData result = AcctCall.AMBackFee(param);
	            if(result.getString("RESULT_CODE","").equals("1"))
	            {
	            	if (logger.isDebugEnabled())
	                {
	                    logger.error("FTTH宽带拆机时，光猫押金沉淀处理失败！"+serialNumber);
	                }
	            }
        	}
        	else//退光猫，押金转存
        	{
	            IData result = AcctCall.adjustFee(param);
	            if(result.getString("RESULT_CODE","").equals("1"))
	            {
	            	if (logger.isDebugEnabled())
	                {
	                    logger.error("FTTH宽带拆机时，光猫押金处理失败！"+serialNumber);
	                }
	            }
	            /**
	             * REQ201512160017 光猫押金释放后下发告知短信的开发需求
	             * chenxy3
	             * 如果是个人拆机则存在光猫押金，退还时候需要发短信。
	             * */
	            else{
	            	IData smsData=new DataMap();
	            	String phoneNum=serialNumber;
	            	IData userInfo=UserInfoQry.getUsersBySn(phoneNum).first();
	            	String phoneUserId=userInfo.getString("USER_ID","0");
	            	smsData.put("PHONE_NUM", phoneNum);//手机号码，不是KD_开头的号码
	            	smsData.put("TRADE_FEE", tradeFee);
	            	smsData.put("PHONE_NUM_USERID", phoneUserId);
	            	genRecipientTradeSms(btd,smsData);
	            }
        	}
        }
    } 
    
    /**
     * REQ201512160017 光猫押金释放后下发告知短信的开发需求
     * chenxy3
     * 如果是个人拆机则存在光猫押金，退还时候需要发短信。
     * */
    private void genRecipientTradeSms(BusiTradeData<BaseTradeData> btd, IData smsData) throws Exception
    {
        String phoneNum=smsData.getString("PHONE_NUM");
        String tradeFee=smsData.getString("TRADE_FEE");
        String userId=smsData.getString("PHONE_NUM_USERID");
    	SmsTradeData std = new SmsTradeData();

        std.setSmsNoticeId(SeqMgr.getSmsSendId());
        std.setEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
        std.setBrandCode("WDBD");
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag("0");
        std.setChanId("11");
        std.setSendObjectCode("6");
        std.setSendTimeCode("1");
        std.setSendCountCode("1");
        std.setRecvObjectType("00");

        std.setRecvId(userId);
        std.setSmsTypeCode("20");
        std.setSmsKindCode("08");
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
        std.setRemark("");
        std.setRevc1(smsData.getString("REVC1", ""));
        std.setRevc2(smsData.getString("REVC2", ""));
        std.setRevc3(smsData.getString("REVC3", ""));
        std.setRevc4(smsData.getString("REVC4", ""));
        std.setMonth(SysDateMgr.getSysTime().substring(5, 7));
        std.setDay(SysDateMgr.getSysTime().substring(8, 10));
        std.setCancelTag("0");

        // 短信截取
        String year=SysDateMgr.getSysDateYYYYMMDDHHMMSS().substring(0,4);
        String month=SysDateMgr.getSysDateYYYYMMDDHHMMSS().substring(4, 6);
        String day=SysDateMgr.getSysDateYYYYMMDDHHMMSS().substring(6,8);
        String hour=SysDateMgr.getSysDateYYYYMMDDHHMMSS().substring(8, 10);
        String min=SysDateMgr.getSysDateYYYYMMDDHHMMSS().substring(10,12);
        
        String content="尊敬的客户，您之前缴纳的光猫押金"+Integer.parseInt(tradeFee)*0.01+"元，已于"+year+"年"+month+"月"+day+"日"+hour+"时"+min+"分释放并存入您的话费账户，请及时查询，详情请咨询当地移动营业厅或拨打10086。中国移动海南公司。";
        std.setNoticeContent(content);

        std.setRecvObject(phoneNum);// 发送号码

        btd.add(phoneNum, std);
    } 
}
