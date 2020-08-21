
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.common.data.IData;


/**
 * @author Administrator
 */
public class ResData 
{
	private String resTypeCode;
	
	private String resCode;
	
	private String operCode;
	
	private String subscriberInsId;
	
	private String subscriberInsIdA;
	
	public void setResTypeCode(String resTypeCode){
		
		this.resTypeCode = resTypeCode;
	}
	
	public String getResTypeCode(){
		
		return resTypeCode;
	}
	
	public void setResCode(String resCode){
		
		this.resCode = resCode;
	}
	
	public String getResCode(){
		
		return resCode;
	}
	
	
	public void setSubscriberInsId(String subscriberInsId){
		
		this.subscriberInsId = subscriberInsId;
	}
	
	public String getSubscriberInsId(){
		
		return subscriberInsId;
	}
	
	public void setSubscriberInsIdA(String subscriberInsIdA){
		
		this.subscriberInsId = subscriberInsIdA;
	}
	
	public String getSubscriberInsIdA(){
		
		return subscriberInsId;
	}
	
	public void setOperCode(String operCode){
		
		this.operCode = operCode;
	}
	
	public String getOperCode(){
		
		return operCode;
	}
	
	public static ResData getInstance(IData resData){
		if(resData == null)
			return null;
		ResData res = new ResData();
		
		String operCode = resData.getString("OPER_CODE");
        if(StringUtils.isEmpty(operCode)){
        	operCode = resData.getString("MODIFY_TAG");
        }
        String resTypeCode = resData.getString("RES_TYPE_CODE");
        String resCode = resData.getString("RES_CODE");
        
        res.setOperCode(operCode);
        res.setResCode(resCode);
        res.setResTypeCode(resTypeCode);
        res.setSubscriberInsId(resData.getString("SUBSCRIBER_INS_ID"));
        res.setSubscriberInsIdA(resData.getString("SUBSCRIBER_INS_ID_A"));
        return res;
        
	}
}
