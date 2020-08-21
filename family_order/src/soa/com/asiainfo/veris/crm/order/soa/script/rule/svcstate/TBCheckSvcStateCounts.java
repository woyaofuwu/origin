package com.asiainfo.veris.crm.order.soa.script.rule.svcstate;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class TBCheckSvcStateCounts extends BreBase implements IBREScript{
	
	private static Logger logger = Logger.getLogger(TBCheckSvcStateCounts.class);
	
	 public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
	    {
		   if (logger.isDebugEnabled())
	            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckSvcStateCounts() >>>>>>>>>>>>>>>>>>");
		   		boolean bResult = false;
		   		String stateCode = "";
	        	IDataset listTradeSvcStates = databus.getDataset("TF_B_TRADE_SVCSTATE");
	        	 if (IDataUtil.isNotEmpty(listTradeSvcStates))
	             {
	        		 for(int i=0;i<=listTradeSvcStates.size()-1;i++){
	        			 String modTag = listTradeSvcStates.getData(i).getString("MODIFY_TAG","");
	        			 if("0".equals(modTag)){
	        				 stateCode = listTradeSvcStates.getData(i).getString("STATE_CODE","");
	        				 break;
	        			 }
	        		 }
	        		String strUserId = databus.getString("USER_ID");
	        		String sn = databus.getString("SERIAL_NUMBER");
	 		        IData param = new DataMap();
	 		        param.put("USER_ID", strUserId);
	 		        param.put("STATE_CODE", stateCode);
	 		        IDataset dataset = Dao.qryByCode("TF_B_TRADE_SVCSTATE", "SEL_COUNT_BY_UID", param,Route.getJourDb());
	 		        String rowNum = "";
	 		        if(IDataUtil.isNotEmpty(dataset)){
	 		        	rowNum = dataset.getData(0).getString("ROW_NUM","0");
	 		        }
	 		        if("1".equals(rowNum)){
	 		        	sendSMS(strUserId,sn,stateCode);
	 		        }
	 		        if(Integer.parseInt(rowNum)>=3){
	 		        	if("2".equals(stateCode)){
		 					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "011801", "已暂停三次，今天已无法使用，请明日再点播");
		 	                return true;
	 		        	}else{
		 					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "011802", "已恢复三次，今天已无法使用，请明日再点播");
		 	                return true;
	 		        	}
	 		        }
	             }
	        return bResult;
	    }
	 
	  private void sendSMS(String userId, String sn, String flag) throws Exception
	    {
		  
		 String content ="";
	     if("2".equals(flag)){
	    	 content ="尊敬的客户，“流量暂停”指令一天只能使用三次，您今天已经使用两次，请注意使用。";
	     }else{
	    	 content ="尊敬的客户，“流量恢复”指令一天只能使用三次，您今天已经使用两次，请注意使用。";
	     }
	     IData smsData = new DataMap();
	     smsData.put("RECV_ID", userId);
	     smsData.put("RECV_OBJECT", sn);
	     smsData.put("NOTICE_CONTENT", content);
	     smsData.put("SMS_KIND_CODE", "08");
	     smsData.put("SMS_PRIORITY", "50");
	     smsData.put("SMS_TYPE_CODE", "I1");     
	     smsData.put("RECV_OBJECT_TYPE", "00");
	     smsData.put("IN_MODE_CODE", "0");
	     SmsSend.insSms(smsData);
	    }

}
