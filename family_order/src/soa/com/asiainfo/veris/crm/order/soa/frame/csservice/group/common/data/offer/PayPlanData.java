
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.common.data.IData;


/**
 * @author Administrator
 */
public class PayPlanData 
{
	private String planTypeCode;
	
	private String operCode;
	
	
	public void setPlanTypeCode(String planTypeCode){
		
		this.planTypeCode = planTypeCode;
	}
	
	public String getPlanTypeCode(){
		
		return planTypeCode;
	}
	
	public void setOperCode(String operCode){
		
		this.operCode = operCode;
	}
	
	public String getOperCode(){
		
		return operCode;
	}
	
	public static PayPlanData getInstance(IData payPlanData){
		
		if(payPlanData == null)
			return null;
		PayPlanData payPlan = new PayPlanData();
		
		String operCode = payPlanData.getString("OPER_CODE");
        if(StringUtils.isEmpty(operCode)){
        	operCode = payPlanData.getString("MODIFY_TAG");
        }
        
        payPlan.setOperCode(operCode);
        
        String planTypeCode = payPlanData.getString("PLAN_TYPE_CODE");
        if(StringUtils.isEmpty(planTypeCode))
            planTypeCode = payPlanData.getString("PLAN_TYPE");
        
        payPlan.setPlanTypeCode(planTypeCode);
        
        return payPlan;
        
	}
}
