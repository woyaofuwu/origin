
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.reg;


import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.requestdata.DestroyUserNowRequestData;

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
    	String widetype = rd.getWideType();
    	if ("1".equals(widetype) || "2".equals(widetype) || "4".equals(widetype) || "6".equals(widetype))
    	{
    		//因现在的业务类型统一使用原GPON宽带的，需要把action表中业务类型为625的reg等配置改为605
    		//需要在这里重新判断下宽带类型，只允许FTTH有光猫的宽带类型执行
    		return ;
    	}
    	
    	if (!"0".equals(rd.getModemMode()))
        {
         	return ;//非租赁光猫，不需要处理光猫退订
        }

    	String serialNumber = rd.getSerialNumberA();
    	
    	if(serialNumber.length()>11)//集团宽带不处理
    		return;
    	
    	String tradeFee = rd.getModemFee();
    	
    	IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
    	if(userInfos.isEmpty()){
    		logger.error("FTTH宽带拆机时，查询用户信息失败！"+serialNumber);
    		return;
    	}
    	
        String eparchyCode = userInfos.getData(0).getString("EPARCHY_CODE");
        String cityCode = userInfos.getData(0).getString("CITY_CODE");
        //查询租赁光猫信息
        IDataset userOthersInfos = UserOtherInfoQry.getModemRentByCodeUserId(userInfos.getData(0).getString("USER_ID"),"FTTH");
        if(userOthersInfos != null && userOthersInfos.size() > 0)
        {
        	tradeFee = userOthersInfos.getData(0).getString("RSRV_STR2","0");
        	String outTradeId = userOthersInfos.getData(0).getString("RSRV_STR8","");
        	String modemFeeState = userOthersInfos.getData(0).getString("RSRV_STR7","");
        	if(!"0".equals(modemFeeState))
        	{
        		return ;
        	}
        	//start add by xuzh5 REQ201806280004宽带有未完工单主号码已经销号问题优化 2018-9-25 9:42:20
        	  //获取宽带光猫押金存折 9002
            int balance9002 = 0;
        	IDataset allUserMoney = AcctCall.queryAccountDepositBySn(serialNumber);
        	for(int i=0;i<allUserMoney.size();i++){
        		if("9002".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE"))){
        			String balance1 = allUserMoney.getData(i).getString("DEPOSIT_BALANCE","0");
                    int balance2 = Integer.parseInt(balance1);
                    balance9002 = balance9002 + balance2;
        		}
        	}
        	List<OtherTradeData> otherTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
             if (otherTradeDatas != null && otherTradeDatas.size() > 0)
             {
            	 for(int i = 0 ; i < otherTradeDatas.size() ; i++)
                 {
                     OtherTradeData otherTradeData = otherTradeDatas.get(i);
                     
                     String rsrvValueCode = otherTradeData.getRsrvValueCode() ;
                     //光猫押金
                     if(rsrvValueCode != null && rsrvValueCode.equals("FTTH")){
                    	 otherTradeData.setRemark("光猫押金存折金额为"+balance9002);
                     	}
                     }
            	 
             }
        	//end add by xuzh5 REQ201806280004宽带有未完工单主号码已经销号问题优化 2018-9-25 9:42:20
        	
            if (!"".equals(tradeFee)&&Integer.parseInt(tradeFee) > 0 && balance9002>0)
            {
            	if(rd.getModermReturn().equals("1"))//不退光猫 做资金沉淀
            	{
            		IData param = new DataMap();
                    param.put("TRADE_FEE", tradeFee);
                    param.put("EPARCHY_CODE", eparchyCode);
                    param.put("CITY_CODE", cityCode);
                    param.put("SERIAL_NUMBER", serialNumber);
            		param.put("OUTER_TRADE_ID", outTradeId);
            		param.put("DEPOSIT_CODE_OUT", "9002");
            		param.put("CHANNEL_ID", "15000");
            		param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
               		param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            		param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
               		param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
               		param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
               		
            		//退光猫，押金转存	
    	            IData result = AcctCall.transFeeOutADSL(param);
    	            if(result.getString("RESULT_CODE","").equals("1"))
    	            {
    	            	if (logger.isDebugEnabled())
    	                {
    	                    logger.error("FTTH宽带拆机时，光猫押金处理失败！"+serialNumber);
    	                }
    	            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账务接口退订光猫押金失败:" + result.getString("RESULT_INFO"));
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
