package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend;

 
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SaleActiveEndActiveSVC extends CSBizService
{ 
	private static transient Logger logger = Logger.getLogger(SaleActiveEndActiveSVC.class);
	/**
	 * REQ201603170014 关于新增集团机惠专享积分购机活动的需求（终止积分过期活动）
	 * chenxy3
	 * 终止营销活动：SS.SaleActiveEndRegSVC.tradeReg
	 * */
    public IDataset endUserSaleActive(IData input) throws Exception
    {	
//    	//获取上月需要终止的用户信息
//    	String acceptMon=SysDateMgr.getlastMonthFirstDate().substring(5,7);
//    	String lastMonLastDay=SysDateMgr.getLastMonthLastDate();
//    	input.put("STATUS", "0");
//    	input.put("ACCEPT_MONTH", acceptMon);
//    	IDataset endSet=SaleActiveEndActiveBean.queryNeedEndActiveUser(input);
//    	for(int k=0;k<endSet.size();k++){
//    		IData infoData=endSet.getData(k); 
    		String userId=input.getString("USER_ID","");
    		String serialNum=input.getString("SERIAL_NUMBER","");
    		String productId=input.getString("PRODUCT_ID","");
    		String packageId=input.getString("PACKAGE_ID","");
    		String campnType=input.getString("CAMPN_TYPE","");
    		String relationTradeid=input.getString("RELATION_TRADE_ID","");
    		String acceptMon=input.getString("ACCEPT_MONTH","");
    		
    		IData updData=new DataMap();
    		updData.put("USER_ID", userId);
    		updData.put("SERIAL_NUMBER", serialNum);
    		updData.put("RELATION_TRADE_ID", relationTradeid);
    		updData.put("ACCEPT_MONTH", acceptMon);
    		try{
	    		//2、调用终止接口
	    		IData callParam=new DataMap();
	    		callParam.put("SERIAL_NUMBER", serialNum);
	    		callParam.put("PRODUCT_ID", productId);
	            callParam.put("PACKAGE_ID", packageId);
	            callParam.put("RELATION_TRADE_ID", relationTradeid);
	            callParam.put("CAMPN_TYPE", campnType);
	            callParam.put("REMARK", "终止积分过期活动");
	            callParam.put("RETURNFEE","0");
	            callParam.put("INTERFACE","0");
	            callParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
	            callParam.put("END_DATE_VALUE","0"); 
	            
	            callParam.put("TRADE_EPARCHY_CODE", "0898");// 受理地州
	            callParam.put("PROVINCE_CODE", "0898");
	    		callParam.put("EPARCHY_CODE", "0898");
	    		callParam.put("CITY_CODE", "HNSJ");
	    		callParam.put("TRADE_CITY_CODE","HNSJ");
	    		callParam.put("TRADE_DEPART_ID","36601");
	    		callParam.put("TRADE_STAFF_ID","SUPERUSR");
	    		callParam.put("IN_MODE_CODE","0");
	    		
	    		callParam.put("STAFF_ID","SUPERUSR");
	    		callParam.put("STAFF_NAME","AEE调用");
	    		callParam.put("LOGIN_EPARCHY_CODE","0898");
	    		callParam.put("STAFF_EPARCHY_CODE","0898");
	    		callParam.put("DEPART_ID","36601");
	    		callParam.put("DEPART_CODE","HNSJ0000");
	    		
	    		CSBizBean.getVisit().setStaffId("SUPERUSR");
	            CSBizBean.getVisit().setCityCode("0898");
	            CSBizBean.getVisit().setDepartId("36601");
	            CSBizBean.getVisit().setInModeCode("0");
	    		
	    		CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg", callParam);
	    		updData.put("STATUS", "1");//成功
	    		updData.put("REMARK", "");
	    		updData.put("RSRV_STR5", "成功！"); 
	    		
    		}catch(Exception e){
    			if(logger.isInfoEnabled()) logger.info(e);
//    			StringWriter sw=new StringWriter();
//    			PrintWriter pw =new PrintWriter(sw);
//    			e.printStackTrace(pw);
    			String errInfo=e.getMessage();
    			long errLength=errInfo.length();
    			if(errLength>1000){
    				errInfo=errInfo.substring(0,1000);
    			}
    			updData.put("STATUS", "9");//失败
    			updData.put("RSRV_STR5", errInfo);
    		}
    		//3、更新标记 
    		SaleActiveEndActiveBean.updEndActiveUserTag(updData);
    	
    	return new DatasetList();
    } 
}
