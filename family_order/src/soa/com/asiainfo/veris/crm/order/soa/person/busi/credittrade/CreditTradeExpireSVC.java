package com.asiainfo.veris.crm.order.soa.person.busi.credittrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;

/**
 * Copyright: Copyright 2015 Asiainfo
 * @ClassName: CreditTradeExpireSVC.java
 * @Description: 信控7101，7508业务，关键时刻到期处理接口
 * @version: v1.0.0
 * @author: yanwu
 * @date: 2015-07-16 11:11:11 
 */
@SuppressWarnings("serial")
public class CreditTradeExpireSVC extends CSBizService {

	
	public IDataset dealExpire(IData mainTrade) throws Exception
	{
		IDataset result = new DatasetList();
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String userId = mainTrade.getString("USER_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
    	//String strTradeid = mainTrade.getString("TRADE_ID");
    	//String strOrderid = mainTrade.getString("ORDER_ID");
    	
    	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
    	if( uca == null ){
    		/*IData param = new DataMap();
            param.put("RSRV_STR1", "call MS result: 三户资料为  Null");
            param.put("TRADE_ID", strTradeid);
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_RSRVSTR1_BY_ID", param);*/
            /*IData data = new DataMap();
	        data.put("TRADE_ID", strTradeid);
	        data.put("RSRV_STR1", "call MS result: 三户资料为  Null");
	        Dao.update("TF_BH_TRADE", data, new String[] { "TRADE_ID"});*/
            IData returnData = new DataMap();
			returnData.clear();
			returnData.put("X_RESULTCODE", "-1");
			returnData.put("X_RESULTINFO", "call MS result: 三户资料为  Null ");
			result.add(returnData);
    		return result;
    	}
    	String strMoney = uca.getAccount().getPayModeCode();
    	String creditClass = uca.getUserCreditClass();
    	
    	/*IData p = mainTrade;
    	if ( uca.isRedUser() ){ p.put("RSRV_STR1", "isRedUser : 是" ); }else{ p.put("RSRV_STR1", "isRedUser : 否" ); }
        p.put("RSRV_STR2", "strMoney : " + strMoney ); p.put("RSRV_STR3", "creditClass : " + creditClass ); 
        p.put("TRADE_ID", strTradeid); Dao.update("TF_B_TRADE", p, new String[] { "TRADE_ID" });*/
        
    	if( "".equals(creditClass) || StringUtils.isBlank(creditClass) ){
    		/*IData param = new DataMap();
            param.put("RSRV_STR1", "call MS result: creditClass Null");
            param.put("TRADE_ID", strTradeid);
            Dao.executeUpdateByCodeCode("TF_BH_TRADE", "UPD_RSRVSTR1_BY_ID", param);*/
    		/*IData data = new DataMap();
	        data.put("TRADE_ID", strTradeid);
	        data.put("RSRV_STR1", "call MS result: creditClass Null");
	        Dao.update("TF_BH_TRADE", data, new String[] { "TRADE_ID"});*/
            IData returnData = new DataMap();
			returnData.clear();
			returnData.put("X_RESULTCODE", "-2");
			returnData.put("X_RESULTINFO", "call MS result: creditClass Null ");
			result.add(returnData);
    		return result;
    	}else{
    		int cc = Integer.parseInt(creditClass);
    		if ( cc <= 5 || !"0".equals(strMoney) ){
    			/*IData param = new DataMap();
    			String strRstr = "call MS result: 客户星级：" + creditClass + ", 缴费方式：" + strMoney ;
                param.put("RSRV_STR1", strRstr);
                param.put("TRADE_ID", strTradeid);
                Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_RSRVSTR1_BY_ID", param);*/
    			String strRstr = "call MS result: 客户星级：" + creditClass + ", 缴费方式：" + strMoney ;
    			/*IData data = new DataMap();
    	        data.put("TRADE_ID", strTradeid);
    	        data.put("RSRV_STR1", strRstr);
    	        Dao.update("TF_BH_TRADE", data, new String[] { "TRADE_ID"});*/
                IData returnData = new DataMap();
    			returnData.clear();
    			returnData.put("X_RESULTCODE", "-3");
    			returnData.put("X_RESULTINFO", strRstr);
    			result.add(returnData);
        		return result;
    		}
    		if ( uca.isRedUser() ){
    			/*IData param = new DataMap();
                param.put("RSRV_STR1", "call MS result: isRedUser Yes");
                param.put("TRADE_ID", strTradeid);
                Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_RSRVSTR1_BY_ID", param);*/
    			/*IData data = new DataMap();
    	        data.put("TRADE_ID", strTradeid);
    	        data.put("RSRV_STR1", "call MS result: isRedUser Yes");
    	        Dao.update("TF_BH_TRADE", data, new String[] { "TRADE_ID"});*/
                IData returnData = new DataMap();
    			returnData.clear();
    			returnData.put("X_RESULTCODE", "-4");
    			returnData.put("X_RESULTINFO", "call MS result: isRedUser Yes ");
    			result.add(returnData);
        		return result;
    		}
    	}
    	
        String custId = mainTrade.getString("CUST_ID");
        String custName = mainTrade.getString("CUST_NAME");
        
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String brandCode = mainTrade.getString("BRAND_CODE");
        
        IData acctData = AcctCall.getOweFeeByUserId(userId);
        String rsrvNum1 = acctData.getString("LAST_OWE_FEE");
        
        String rsrvDate1 = "";
        String rsrvDate2 = "";
        if ("7101".equals(tradeTypeCode))
        {
        	// 普通用户走7101 单停时间即工单执行时间 双停时间是单停加上24小时
            String execTime = mainTrade.getString("EXEC_TIME");
            String stopTime = SysDateMgr.endDateOffset(execTime, "1", "0");
            rsrvDate1 = execTime;
            rsrvDate2 = stopTime; 
        }
        else if ("7508".equals(tradeTypeCode))
        {
            // 金钻卡用户走7508 单双停时间相同 都是次月1日零点
            String execTime = mainTrade.getString("EXEC_TIME");
            String stopTime = SysDateMgr.getFirstDayOfNextMonth(execTime);
            if (stopTime.length() < 19)
            {
                stopTime = stopTime + SysDateMgr.START_DATE_FOREVER;
            }
            rsrvDate1 = execTime;
            rsrvDate2 = stopTime; 
        }
        
        /*IData p = mainTrade; p.put("RSRV_STR2", "execTime : " + execTime ); p.put("RSRV_STR3", "stopTime : " + stopTime ); 
        p.put("RSRV_STR4", "rsrvNum1 : " + rsrvNum1 ); p.put("TRADE_ID", strTradeid); Dao.update("TF_B_TRADE", p, new String[] { "TRADE_ID" });*/
        
        String useCustName = "";
        String custManagerId = "";
        String custType = "";
        IDataset custVips = CustVipInfoQry.getCustVipInfoByCustId(custId, "0");
        // 查询大客户 
        if ( IDataUtil.isNotEmpty(custVips) )
        {
        	IData custVip = custVips.getData(0);
        	useCustName = custVip.getString("USECUST_NAME");
            custManagerId = custVip.getString("CUST_MANAGER_ID");
            custType = custVip.getString("CUST_TYPE");
        }
        // ITF_CRM_DownTimeJob客户欠费即将停机时刻工作生成接口
        result = SccCall.createDownTimeJob(serialNumber, userId, custId, custName, custType, eparchyCode, brandCode, useCustName, custManagerId, rsrvDate1, rsrvDate2, rsrvNum1);
        
        /*String resultInfo = "null";
        if (IDataUtil.isNotEmpty(result))
        {
            resultInfo = result.getData(0).getString("X_RESULTINFO");
            IData param = new DataMap();
            param.put("RSRV_STR1", "call MS result: " + resultInfo);
            param.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
            Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_RSRVSTR1_BY_ID", param);
        }*/
        
		return result;
	}
}
