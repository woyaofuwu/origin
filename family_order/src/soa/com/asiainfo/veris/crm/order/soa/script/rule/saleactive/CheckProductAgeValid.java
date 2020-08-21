package com.asiainfo.veris.crm.order.soa.script.rule.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * 验证用户的年龄是否足够参加营销活动
 *
 */
public class CheckProductAgeValid extends BreBase implements IBREScript{

private static final long serialVersionUID = 2987559940602315670L;
	
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
		String serialNumber=databus.getString("SERIAL_NUMBER");
		IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
		
		//用户信息为空，说明是从开户那边进入
		if(IDataUtil.isEmpty(userInfo)){
			return false;
		}
		
		String custId=userInfo.getString("CUST_ID");
		String saleProductId = databus.getString("PRODUCT_ID");
		String packageId=databus.getString("PACKAGE_ID");
		
		IData custInfo=UcaInfoQry.qryCustomerInfoByCustId(custId);
		String psptTypeCode=custInfo.getString("PSPT_TYPE_CODE","");
		String psptId=custInfo.getString("PSPT_ID","");
		
		
		//查询营销活动是否存在年龄限制的配置
		IDataset ageRules=CommparaInfoQry.getEnableCommparaInfoByCode19("CSM", "5212", "SALE_ACTIVE_AGE", saleProductId, packageId);
		if(IDataUtil.isNotEmpty(ageRules))
		{
			
			if(!(psptTypeCode.equals("0") || psptTypeCode.equals("1") || psptTypeCode.equals("2")))
			{	//只有身份证开户的用户才能办理营销活动
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "14087621", "用户的开户证件类型不为身份证或户口本，无法办理营销活动！");
				return true;
			}
			
			//从身份证里获取用户的出生年
			String birthday=obtainBirthdayYearFromDoc(psptId);
			
			
			IData ageRule=ageRules.getData(0);
			
			/*
			 * 年龄的配置可以配置三个档次
			 */
			String paraCode2=ageRule.getString("PARA_CODE2","");
			String paraCode3=ageRule.getString("PARA_CODE3","");
			
			String paraCode4=ageRule.getString("PARA_CODE4","");
			String paraCode5=ageRule.getString("PARA_CODE5","");
			
			String paraCode6=ageRule.getString("PARA_CODE6","");
			String paraCode7=ageRule.getString("PARA_CODE7","");
			
			boolean result=false;
			
			int firstEmpty=0;
			int secondEmpty=0;
			int thirdEmpty=0;
			
			//如果配置不为空
			if(!(paraCode2.trim().equals("")&&paraCode3.trim().equals(""))){
				result=commparaAgeValid(birthday, paraCode2, paraCode3);
			}else{
				firstEmpty=1;
			}
			if(!result){
				if(!(paraCode4.trim().equals("")&&paraCode5.trim().equals(""))){
					result=commparaAgeValid(birthday, paraCode4, paraCode5);
				}else{
					secondEmpty=1;
				}
			}
			if(!result){
				if(!(paraCode6.trim().equals("")&&paraCode7.trim().equals(""))){
					result=commparaAgeValid(birthday, paraCode6, paraCode7);
				}else{
					thirdEmpty=1;
				}
			}
			
			if(!(firstEmpty==1&&secondEmpty==1&&thirdEmpty==1)){		//如果不是全部为空
				if(!result){	//存在验证不通过的进行提示
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,"14087620", "用户的年龄不够开通此营销活动！");
					return true;
				}
			}
			
			
		}
		
		return false;
		
    }
	
	
	public boolean commparaAgeValid(String userAge, String beginAge, String endAge)throws Exception{
		
		boolean result=false;
		
		long userAgeInt=Long.parseLong(userAge);
		
		if(!beginAge.trim().equals("")&&!endAge.trim().equals("")){		
			long beginAgeInt=Long.parseLong(beginAge);
			long endAgeInt=Long.parseLong(endAge);
			
			if(userAgeInt>=beginAgeInt&&userAgeInt<=endAgeInt){
				result=true;
			}
			
		}else if(!beginAge.trim().equals("")&&endAge.trim().equals("")){
			long beginAgeInt=Long.parseLong(beginAge);
			
			if(userAgeInt>=beginAgeInt){
				result=true;
			}
			
		}else if(beginAge.trim().equals("")&&!endAge.trim().equals("")){
			long endAgeInt=Long.parseLong(endAge);
			
			if(userAgeInt<=endAgeInt){
				result=true;
			}
		}
//		else if(beginAge.trim().equals("")&&endAge.trim().equals("")){		//没有配置的话，算是不限制
//			result=true;
//		}
		
		return result;
	}
	
	
	public String obtainBirthdayYearFromDoc(String psptId)throws Exception{
		String tmpStr="";
		
		if(psptId.length()==15) {
			tmpStr= "19" + psptId.substring(6,12);
		}else {
	    	tmpStr = psptId.substring(6,14);
		}
		return tmpStr.substring(0,4) + tmpStr.substring(4,6) + tmpStr.substring(6);
	}
	
	
	public int calculateAge(String birthdayYear)throws Exception{
		String curDate=SysDateMgr.getSysDate();
		String curYear=curDate.substring(0, 4);
		
		int curYearInt=Integer.parseInt(curYear);
		int birthdayYearInt=Integer.parseInt(birthdayYear);
		
		
		return curYearInt-birthdayYearInt;
	}
}
