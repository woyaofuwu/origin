package com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class SpecialCustSmsFinishAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
	
		String custId=mainTrade.getString("RSRV_STR2");
		String objSerianlNumber=mainTrade.getString("SERIAL_NUMBER");
		
		
		IDataset custInfos=CustomerInfoQry.querySamePsIdAndNameCust(custId);
		if(IDataUtil.isNotEmpty(custInfos)){
			//获取短信内容
            IData param=new DataMap();
            param.put("SUBSYS_CODE", "CSM");
            param.put("PARAM_ATTR", "7898");
            param.put("PARAM_CODE", "7898");
            IDataset smsContentData = Dao.qryByCodeParser("TD_S_COMMPARA", "QUERY_AUTUMN_ACTIVITY_SMS_CONTENT", param, Route.CONN_CRM_CEN);
            
            String smsContentTemplate = "温馨提醒：尊敬的@NULL1@客户，您证件下的@NULL2@号码已办理过户业务";
            if(smsContentData!=null&&smsContentData.size()>0){
            	smsContentTemplate=smsContentData.getData(0).getString("SMS_CONTENT");
            }
			
			
			for(int i=0,size=custInfos.size();i<size;i++){
				String serialNumber=custInfos.getData(i).getString("SERIAL_NUMBER");
				String userId=custInfos.getData(i).getString("USER_ID");
				String custName=custInfos.getData(i).getString("CUST_NAME");
				
				IData smsData = new DataMap();
                smsData.clear();
                
                String smsContent=smsContentTemplate.replaceAll("@NULL1@", custName).
                			replaceAll("@NULL2@", objSerianlNumber);
                
                smsData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                smsData.put("RECV_OBJECT", serialNumber);
                smsData.put("RECV_ID", userId);
                smsData.put("NOTICE_CONTENT", smsContent);
                smsData.put("REMARK", "过户单方特殊过户");
                SmsSend.insSms(smsData);
			}
		}
		
		
	}
}
