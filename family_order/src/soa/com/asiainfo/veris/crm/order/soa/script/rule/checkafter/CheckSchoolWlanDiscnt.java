package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright 2015 Asiainfo-Linkage 
 * 
 * @ClassName: CheckSchoolWlanDiscnt
 * @Description: 新增了的优惠：WLAN校园10元加油包（三亚学院）20141231。需要2个条件：
 *               1、只限已办理含无线包月的宽带套餐客户办理	 1M38元宽带套餐（含WLAN）20130502、
 *               2M45元宽带套餐（含WLAN）20130602、
 *               4M70元宽带套餐（含WLAN）20130702
 *               2、每月最多叠加6次。
 * @version: v1.0.0
 * @author: chenxy3
 * @date: 20150202
 */
public class CheckSchoolWlanDiscnt extends BreBase implements IBREScript
{  
    private static Logger logger = Logger.getLogger(CheckSchoolWlanDiscnt.class);

    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
    	String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
    	
    	String totalNum=param.getString(databus, "TOTAL_NUM");
    	String checkDiscnt=param.getString(databus, "CHECK_DISCNT");//需要校验的优惠
    	String relyDiscnts=param.getString(databus, "RELY_DISCNT");//依赖的优惠
    	
    	IDataset TradeDiscntset = databus.getDataset("TF_B_TRADE_DISCNT");//获取当前优惠
    	IDataset userDiscntset = databus.getDataset("TF_F_USER_DISCNT_AFTER"); //历史的优惠
    	
    	//1、先取当前办理的优惠，是否含有叠加包优惠20141231，有才继续校验。
    	String newWlanDiscnt="no_exist";
    	for (int i = 0; i <  TradeDiscntset.size(); i++){
    		String discnt_code=TradeDiscntset.getData(i).getString("DISCNT_CODE");
    		String modifyTag=TradeDiscntset.getData(i).getString("MODIFY_TAG");
    		if(checkDiscnt.equals(discnt_code) && "0".equals(modifyTag)){
    			newWlanDiscnt="exist";
    			break;
   		 	}
    	}
    	//2、循环获取是否含有办理20141231前提的三种优惠之一，有才允许继续办理。
    	String existDiscnt="no_exist";
    	int existNum=0;
    	if("exist".equals(newWlanDiscnt)){
	    	for (int i = 0; i <  userDiscntset.size(); i++){
	    		 String discnt_code=userDiscntset.getData(i).getString("DISCNT_CODE");
	    		 StringTokenizer st2=new StringTokenizer(relyDiscnts,",");
	    		 boolean ifHasRalyDiscnt=false;
	    	     for(int st=1;st2.hasMoreTokens();st++){
	    	    	 String relyDiscnt=st2.nextToken();
	    	    	 if(relyDiscnt.equals(discnt_code)){
	    	    		 String startDate=userDiscntset.getData(i).getString("START_DATE");
	    	    		 String endDate=userDiscntset.getData(i).getString("END_DATE");
	    	    		 String today = SysDateMgr.getSysDate();
	    	    		  
	    	    		  
	    	    		 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	    	    		 long start_date=sdf.parse(startDate).getTime();
	    	    		 long end_date=sdf.parse(endDate).getTime();
	    	    		 long sysdate=sdf.parse(today).getTime();
	    	    		  
	    	    		 if(sysdate<start_date || sysdate>end_date){
	    	    			 String errorMsg="WLAN校园10元加油包（三亚学院）限制：本月必须有有效的1M38元宽带套餐（含WLAN）、2M45元宽带套餐（含WLAN）、4M70元宽带套餐（含WLAN）才允许办理！";
		    	             BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 120141231, errorMsg);
		    	             return true;
	    	    		 }
	    	    		 
	    	    		 ifHasRalyDiscnt=true;
	    	    		 break;
	    	    	 }
	    	     }
	    		 if(ifHasRalyDiscnt){
	    			 existDiscnt="exist";
	    			 for (int j = 0; j <  userDiscntset.size(); j++){	
	    				 String discnt=userDiscntset.getData(j).getString("DISCNT_CODE");
	    				 if(checkDiscnt.equals(discnt)){
	    					 existNum+=1;
	    				 }
	    			 }
	    			 if(existNum>Integer.parseInt(totalNum)){
	    				 String errorMsg="WLAN校园10元加油包（三亚学院）限制：每月申请办理不能超过"+totalNum+"次！";
	    	             BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 120141231, errorMsg);
	    	             return true;
	    			 }else{
	    				 return false;
	    			 }
	    		 }
	         }
	    	 if("no_exist".equals(existDiscnt)){
	    		 String errorMsg="WLAN校园10元加油包（三亚学院）限制：只限已办理1M38元宽带套餐（含WLAN）、2M45元宽带套餐（含WLAN）、4M70元宽带套餐（含WLAN）用户办理！";
	             BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 120141231, errorMsg);
	             return true;
	    	 }else{
				 return false;
			 }
    	}
    	return false;
    }
}
