package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class OrderStatusUpdateSVC extends CSBizService 
{
	private static transient Logger logger = Logger.getLogger(OrderStatusUpdateSVC.class);
    private static StringBuilder getInterFaceSQL;

	static
	{
		getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' "); 
	}
	public IData callAbilityIntf(IData input) throws Exception
	{
		IData returnData=new DataMap();
		//String apiAddress = BizEnv.getEnvString(input.getString("getUrl",""));
		
		IData param1 = new DataMap();
    	param1.put("PARAM_NAME", input.getString("getUrl",""));
    	IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
    	String Abilityurl = "";
    	if (Abilityurls != null && Abilityurls.size() > 0)
    	{
    		Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
    	}
    	else
    	{
    		throw new Exception("["+input.getString("getUrl","")+"]接口地址未在TD_S_BIZENV表中配置");
    	}
    	String apiAddress = Abilityurl;
		
		if(StringUtils.isBlank(input.getString("oprTime","")))
		{
			input.getString("oprTime",SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
		}

		if(StringUtils.isBlank(input.getString("numberActivateTime","")))
		{
			input.getString("numberActivateTime",SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
		}
        if(input.containsKey("getUrl")){
        	input.remove("getUrl");
        }
        if(input.containsKey("TRADE_EPARCHY_CODE")){
        	input.remove("TRADE_EPARCHY_CODE");
        }
		IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,input);
		String resCode=stopResult.getString("resCode");
		String resMsg=stopResult.getString("resMsg");
		String X_RSPCODE="0000";
		String X_RSPDESC="成功";
		if(!"00000".equals(resCode))
		{
			X_RSPCODE=resCode;
			X_RSPDESC=resMsg;
			throw new Exception(resMsg);
		}

		returnData.put("RESULT_CODE", X_RSPCODE);
		returnData.put("RESULT_INFO", X_RSPDESC);
		return returnData;
	}
    public IData updateOrderStatus(IData input) throws Exception
    {
    	IData returnData=new DataMap();
    	returnData.put("RESULT_CODE", "0000");
    	returnData.put("RESULT_INFO", "成功。");
    	IData param1 = new DataMap();
    	param1.put("PARAM_NAME", "crm.ABILITY.UP");
    	IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
    	String Abilityurl = "";
    	if (Abilityurls != null && Abilityurls.size() > 0)
    	{
    		Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
    	}
    	else
    	{
    		CSAppException.appError("-1", "crm.ABILITY.UP接口地址未在TD_S_BIZENV表中配置");
    	}
    	String apiAddress = Abilityurl;
    	//能力编码
    	//String abilityCode = "CIP00065";
    	String appId=StaticUtil.getStaticValue("ABILITY_APP_ID", "1");// 应用ID
    	IDataset orderList =getOrderStatus();
    	if(IDataUtil.isNotEmpty(orderList))
    	{
    		for (int i = 0, size = orderList.size(); i < size; i++)
    		{
    			IData info = orderList.getData(i);
    			IData result = new DataMap();
                String status=info.getString("STATE","");
                result.put("UPDATE_STAFF_ID",info.getString("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId()));
                result.put("UPDATE_DEPART_ID", info.getString("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId()));
                result.put("ORDER_ID", info.getString("ORDER_ID",""));//订单编号/等订单和退订接口表建好后改表字段
                result.put("SUBORDER_ID", info.getString("SUBORDER_ID",""));//子订单编号
                //result.put("returnId", info.getString("OID",""));//退单编号
                
                //OAO需求只对STATUS=AF、FA、AC做回传
     		    if(StringUtils.equals("20",info.getString("NUMBER_OPRTYPE"))
     				   &&(!StringUtils.equals("FA", info.getString("STATE")))
     				   &&(!StringUtils.equals("AF", info.getString("STATE")))
     				   &&(!StringUtils.equals("AC", info.getString("STATE")))){
     		       updateOrderRsrvDate(result);	
     			   continue;
     		    }
     		   
                try   
                {     
                	IData abilityData = new DataMap();
                	abilityData.put("OrderId", info.getString("ORDER_ID",""));//订单编号/等订单和退订接口表建好后改表字段
                	abilityData.put("SubOrderId", info.getString("SUBORDER_ID",""));//子订单编号
                	abilityData.put("returnId", "");//退单编号
                	abilityData.put("OrderTime", SysDateMgr.decodeTimestamp(info.getString("CREATE_TIME",""), SysDateMgr.PATTERN_STAND_SHORT));//业务订购时间
                	abilityData.put("Status", info.getString("STATE",""));//订单状态
					if(StringUtils.isNotBlank(info.getString("STATUS_DESC"))){
						abilityData.put("statusDesc",info.getString("STATUS_DESC"));//订单状态描述
					}
					if(StringUtils.equals("20",info.getString("NUMBER_OPRTYPE"))){//OAO需求上传京东工号
	    				abilityData.put("OpContactNo", info.getString("OP_CONTACT_NO",""));
	    			}
                	abilityData.put("UpdateTime",  SysDateMgr.decodeTimestamp(info.getString("UPDATE_TIME",""), SysDateMgr.PATTERN_STAND_SHORT));//状态变更时间
                	if("SE".equals(status))
                	{//已发货状态时需反馈物流信息
                		IData shipInfo=new DataMap();
//                		shipInfo.put("ShipmentCompanyCode", StaticUtil.getStaticValue("SHIP_CONPANY_NO", "1"));//物流公司编码
//                		shipInfo.put("ShipmentCompany", StaticUtil.getStaticValue("SHIP_COMPANY_NAME", "1"));//物流公司名称
//                		shipInfo.put("ShipmentNo",StaticUtil.getStaticValue("SHIP_NO", "1"));//物流单号/运单编码
						if(StringUtils.isNotBlank(info.getString("SHIPMENT_COMPANY_CODE"))){
							String shipmentCpmpanyCode = info.getString("SHIPMENT_COMPANY_CODE","");
							shipInfo.put("ShipmentCompany", StaticUtil.getStaticValue("SHIP_COMPANY_NO", shipmentCpmpanyCode));//物流公司名称
							shipInfo.put("ShipmentCompanyCode",shipmentCpmpanyCode);//物流公司编码
						}else{
							shipInfo.put("ShipmentCompanyCode",StaticUtil.getStaticValue("SHIP_CONPANY_NO", "1"));//物流公司编码
							shipInfo.put("ShipmentCompany", StaticUtil.getStaticValue("SHIP_COMPANY_NAME", "1"));//物流公司名称
						}
						if(StringUtils.isNotBlank(info.getString("SHIPMENT_NO"))){
							shipInfo.put("ShipmentNo",info.getString("SHIPMENT_NO"));//物流单号/运单编码
						}else{
							shipInfo.put("ShipmentNo",StaticUtil.getStaticValue("SHIP_NO", "1"));//物流单号/运单编码
						}
                		abilityData.put("ShipmentInfo", shipInfo);
                	}
                	if("IN".equals(status))
                	{//订单状态status=IN（安装中）时，省公司需要返回additionalInfo字段
                		abilityData.put("OpContactName", StaticUtil.getStaticValue("ADDITION_NAME", "1"));//订单受理联系人姓名
                		abilityData.put("OpContactNo", StaticUtil.getStaticValue("ADDITION_STAFF_ID", "1"));//订单受理联系人工号
                		abilityData.put("OpContactPhone", StaticUtil.getStaticValue("ADDITION_PHONE", "1"));//订单受理联系人电话
                		abilityData.put("OpComments", StaticUtil.getStaticValue("ADDITION_REMARK", "1"));//订单受理备注信息
                	}    
                	logger.debug("panwj2========================"+abilityData);
                	IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,abilityData);
                	String resCode=stopResult.getString("resCode");
                	String X_RSPCODE="";
                	String X_RSPDESC="";
                	if(!"00000".equals(resCode))
                	{	
                		X_RSPCODE=stopResult.getString("resCode");  
                		X_RSPDESC=stopResult.getString("resMsg");
                	}    
                	else  
                	{
                		IData out=stopResult.getData("result");
                		X_RSPCODE=out.getString("BizCode");
                		X_RSPDESC=out.getString("BizDesc");
                	}
                	result.put("RESULT_CODE", X_RSPCODE);
                	result.put("RESULT_INFO", X_RSPDESC);
                	returnData.put("RESULT_CODE", X_RSPCODE);
                	returnData.put("RESULT_INFO", X_RSPDESC);
                }
                catch (Exception e)
                {	
                	logger.debug("---"+e.getMessage());
                	result.put("RESULT_CODE", "-1");
                	result.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage()));     
                	returnData.put("RESULT_CODE", "9999");
                	returnData.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage())); 
                }   
                int nR1 = i + 1;
                result.put("RSRV_STR1", nR1);
                updateOrderStatusInfo(result);
             }
       }
       IDataset returndList=getReturndStatus();
       logger.debug("panwj2========================"+returndList);
       if(IDataUtil.isNotEmpty(returndList))
       {
    	   for (int i = 0, size = returndList.size(); i < size; i++)
    	   {
    		   IData info = returndList.getData(i);
    		   IData result = new DataMap();
    		   String status=info.getString("STATUS","");
    		   result.put("OPR_NUM", info.getString("OPR_NUM",""));//退单编号
    		   result.put("UPDATE_STAFF_ID",info.getString("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId()));
    		   result.put("UPDATE_DEPART_ID", info.getString("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId()));
    		   result.put("ORDER_ID", info.getString("ORDER_ID",""));//订单编号/等订单和退订接口表建好后改表字段
    		   result.put("SUB_ORDER_ID", info.getString("SUB_ORDER_ID",""));//子订单编号
    		   result.put("RETURN_ID", info.getString("RETURN_ID",""));//退单编号
    		   try
    		   {  
    			   IData abilityData = new DataMap();
    			   abilityData.put("OrderId", info.getString("ORDER_ID",""));//订单编号/等订单和退订接口表建好后改表字段
    			   abilityData.put("SubOrderId", info.getString("SUB_ORDER_ID",""));//子订单编号
    			   abilityData.put("ReturnId", info.getString("RETURN_ID",""));//退单编号
    			   abilityData.put("OrderTime", SysDateMgr.decodeTimestamp(info.getString("ACCEPT_DATE",""), SysDateMgr.PATTERN_STAND_SHORT));//业务订购时间
    			   abilityData.put("Status", info.getString("STATUS",""));//订单状态
    			   //abilityData.put("statusDesc", "");//订单状态描述
    			   abilityData.put("UpdateTime", SysDateMgr.decodeTimestamp(info.getString("UPDATE_TIME",""), SysDateMgr.PATTERN_STAND_SHORT));//状态变更时间
    			   if("SE".equals(status))
    			   {//已发货状态时需反馈物流信息
    				   IData shipInfo=new DataMap();
    				   shipInfo.put("ShipmentCompanyCode", StaticUtil.getStaticValue("SHIP_CONPANY_NO", "1"));//物流公司编码
    				   shipInfo.put("ShipmentCompany", StaticUtil.getStaticValue("SHIP_COMPANY_NAME", "1"));//物流公司名称
    				   shipInfo.put("ShipmentNo",StaticUtil.getStaticValue("SHIP_NO", "1"));//物流单号/运单编码
    				   abilityData.put("ShipmentInfo", shipInfo);
    			   }
    			   if("IN".equals(status))
    			   {//订单状态status=IN（安装中）时，省公司需要返回additionalInfo字段
    				   abilityData.put("OpContactName", StaticUtil.getStaticValue("ADDITION_NAME", "1"));//订单受理联系人姓名
    				   abilityData.put("OpContactNo", StaticUtil.getStaticValue("ADDITION_STAFF_ID", "1"));//订单受理联系人工号
    				   abilityData.put("OpContactPhone", StaticUtil.getStaticValue("ADDITION_PHONE", "1"));//订单受理联系人电话
    				   abilityData.put("OpComments", StaticUtil.getStaticValue("ADDITION_REMARK", "1"));//订单受理备注信息
    			   }
    			   if("0".equals(status.trim()))
				   {
					   abilityData.put("Status", "RC");
				   }

    			   logger.debug("panwj2========================" + abilityData);
    			   IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress, abilityData);
    			   String resCode = stopResult.getString("resCode");
    			   String X_RSPCODE = "";
    			   String X_RSPDESC = "";
    			   if(!"00000".equals(resCode))
    			   {
    				   X_RSPCODE = stopResult.getString("resCode");
    				   X_RSPDESC = stopResult.getString("resMsg");
    			   }
    			   else
    			   {	
    				   IData out = stopResult.getData("result");
    				   X_RSPCODE = out.getString("BizCode");
    				   X_RSPDESC = out.getString("BizDesc");
    			   }
    			   result.put("RESULT_CODE", X_RSPCODE);
    			   result.put("RESULT_INFO", X_RSPDESC);
    			   returnData.put("RESULT_CODE", X_RSPCODE);
    			   returnData.put("RESULT_INFO", X_RSPDESC);
    			   returnData.putAll(stopResult);
    		   } 
    		   catch (Exception e)
    		   {
    			   logger.debug("---"+e.getMessage());
    			   result.put("RESULT_CODE", "-1");
    			   result.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage()));
    			   returnData.put("RESULT_CODE", "9999");
    			   returnData.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage()));
    		   }
    		   int nR1 = i + 1;
    		   result.put("RSRV_STR1", nR1);
    		   updateReturndStatusInfo(result);
    	   }
       }
       return returnData;
    }
    
    public IDataset getOrderStatus() throws Exception
    {
    	return Dao.qryByCode("TF_B_CTRM_GERLSUBORDER", "SEL_ORDER_INFO", null,Route.CONN_CRM_CEN);
    }
    
    public IDataset getReturndStatus() throws Exception
    {
    	return Dao.qryByCode("TF_B_CTRM_RETURN", "SEL_RETURND_INFO", null,Route.CONN_CRM_CEN);
    }
    
    public static int updateOrderStatusInfo(IData result)throws Exception
    {
    	SQLParser sqlParser = new SQLParser(result);
		sqlParser.addSQL("  update TF_B_CTRM_GERLSUBORDER  set  RSRV_STR1=:RSRV_STR1 ");
		sqlParser.addSQL(", DEAL_STATE = :RESULT_CODE ,DEAL_DESC = :RESULT_INFO  ");
		sqlParser.addSQL(" ,UPDATE_TIME = sysdate ,UPDATE_STAFF_ID  = :UPDATE_STAFF_ID, UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
		sqlParser.addSQL(" WHERE ORDER_ID = :ORDER_ID  AND SUBORDER_ID = :SUBORDER_ID ");
		return Dao.executeUpdate(sqlParser,Route.CONN_CRM_CEN);
	}
    
    public static int updateReturndStatusInfo(IData result)throws Exception
    {
		SQLParser sqlParser = new SQLParser(result);
		sqlParser.addSQL("  update TF_B_CTRM_RETURN  set  RSRV_STR1=:RSRV_STR1 ,IS_SYNC='1' ");
		sqlParser.addSQL(", RSRV_STR2=:RESULT_CODE ,RSRV_STR3=:RESULT_INFO  ");
		sqlParser.addSQL(" ,UPDATE_TIME=sysdate ,UPDATE_STAFF_ID=:UPDATE_STAFF_ID ,UPDATE_DEPART_ID=:UPDATE_DEPART_ID ");
		sqlParser.addSQL(" WHERE OPR_NUM=:OPR_NUM ");
		return Dao.executeUpdate(sqlParser,Route.CONN_CRM_CEN);
	}
    
    public static int updateOrderRsrvDate(IData result)throws Exception
    {
    	SQLParser sqlParser = new SQLParser(result);
		sqlParser.addSQL("  update TF_B_CTRM_GERLSUBORDER  set  RSRV_DATE=SYSDATE ");
		sqlParser.addSQL(" WHERE ORDER_ID = :ORDER_ID  AND SUBORDER_ID = :SUBORDER_ID ");
		return Dao.executeUpdate(sqlParser,Route.CONN_CRM_CEN);
	}
    
    /**
     * 同步一级电渠补换卡订单状态===========================================================================
     * @param input
     * @return
     * @throws Exception
     */
    public IData updateReplaceCardOrderStatus(IData input) throws Exception
    {
    	IData returnData=new DataMap();
    	returnData.put("RESULT_CODE", "0000");
    	returnData.put("RESULT_INFO", "成功。");
    	IData param1 = new DataMap();
    	param1.put("PARAM_NAME", "crm.ABILITY.SyncReplaceCardOrder");
    	IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
    	String Abilityurl = "";
    	if (Abilityurls != null && Abilityurls.size() > 0)
    	{
    		Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
    	}
    	else
    	{
    		CSAppException.appError("-1", "crm.ABILITY.SyncReplaceCardOrder接口地址未在TD_S_BIZENV表中配置");
    	}
    	String apiAddress = Abilityurl;
    	IDataset orderList =getReplaceCardOrderStatus();
    	if(IDataUtil.isNotEmpty(orderList))
    	{
    		for (int i = 0, size = orderList.size(); i < size; i++)
    		{
    			IData info = orderList.getData(i);
    			IData result = new DataMap();
                result.put("ORDER_ID", info.getString("ORDER_ID",""));//订单编号
     		   
                try   
                {     
                	String operatorType="";
                	if("PK".equals(info.getString("STATE"))){
                		operatorType="CM";//确认
                	}else if("PD".equals(info.getString("STATE"))){
                		operatorType="PK";//备货
                	}else if("PS".equals(info.getString("STATE"))){
                		operatorType="DR";//发货
                	}else if("SS".equals(info.getString("STATE"))){
                		operatorType="SN";//完成
                	}else if("CL".equals(info.getString("STATE"))){
                		operatorType="MC";//取消
                	}
                	IData abilityData = new DataMap();
                	abilityData.put("order_id", info.getString("ORDER_ID",""));//订单编号
                	abilityData.put("status", operatorType);//操作类型
                	abilityData.put("logistics_id", info.getString("LOGISTICS_NUMBER",""));//物流单号
                	abilityData.put("logicom_code", info.getString("LOGISTICS_COMPANY",""));//物流公司
                	logger.debug("panwj2========================"+abilityData);
                	IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,abilityData);
                	String resCode=stopResult.getString("resCode");
                	String X_RSPCODE="";
                	String X_RSPDESC="";
                	if(!"00000".equals(resCode))
                	{	
                		X_RSPCODE=stopResult.getString("resCode");  
                		X_RSPDESC=stopResult.getString("resMsg");
                	}    
                	else  
                	{
                		IData out=stopResult.getData("result");
                		X_RSPCODE=out.getString("BizCode");
                		X_RSPDESC=out.getString("BizDesc");
                	}
                	result.put("RESULT_CODE", X_RSPCODE);
                	result.put("RESULT_INFO", X_RSPDESC);
                	returnData.put("RESULT_CODE", X_RSPCODE);
                	returnData.put("RESULT_INFO", X_RSPDESC);
                }
                catch (Exception e)
                {	
                	logger.debug("---"+e.getMessage());
                	result.put("RESULT_CODE", "-1");
                	result.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage()));     
                	returnData.put("RESULT_CODE", "9999");
                	returnData.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage())); 
                }   
                updateReplaceCardOrderStatusInfo(result);
             }
       }
       return returnData;
    }
    public IDataset getReplaceCardOrderStatus() throws Exception
    {
    	StringBuilder sql=new StringBuilder();
    	sql.append("SELECT * FROM TF_B_ORDER_DETAIL T WHERE T.STATE IN('PK','PD','PS','SS','CL') AND T.RSRV_TAG1 IS NULL");
    	return Dao.qryBySql(sql, new DataMap());
    }
    public static int updateReplaceCardOrderStatusInfo(IData result)throws Exception
    {
    	SQLParser sqlParser = new SQLParser(result);
		sqlParser.addSQL("  update TF_B_ORDER_DETAIL  set  RSRV_TAG1='1' ");
		sqlParser.addSQL(", DEAL_STATE = :RESULT_CODE ,DEAL_RESULT = :RESULT_INFO  ");
		sqlParser.addSQL(" ,UPDATE_TIME = sysdate ");
		sqlParser.addSQL(" WHERE ORDER_ID = :ORDER_ID ");
		return Dao.executeUpdate(sqlParser);
	}
    /**
     * 一级电渠订单通知省侧接口
     */
    public IData notifyReplaceCardOrderStatus(IData input) throws Exception
    {
    	input=new DataMap(input.toString());
    	IData returnData=new DataMap();
    	returnData.put("resultCode", "0");
    	returnData.put("resultInfo", "成功");
    	
    	if(StringUtils.isEmpty(input.getString("order_id"))){
    		returnData.put("resultCode", "-1");
        	returnData.put("resultInfo", "order_id不能为空");
        	return returnData;
    	}
    	if(StringUtils.isEmpty(input.getString("status"))){
    		returnData.put("resultCode", "-1");
        	returnData.put("resultInfo", "status不能为空");
        	return returnData;
    	}
    	
    	try{
    		IData param=new DataMap();
    		param.put("ORDER_ID", input.getString("order_id"));
    		param.put("STATE", input.getString("status"));
    		param.put("CANCEL_TYPE", input.getString("cancel_type",""));
    		param.put("CANCEL_REASON", input.getString("cancel_reason",""));
    		param.put("EXCHANGE_TYPE", input.getString("exchange_type",""));
    		param.put("EXCHANGE_REASON", input.getString("exchange_reason",""));
    		
    		SQLParser sqlParser = new SQLParser(param);
    		sqlParser.addSQL("  update TF_B_ORDER_DETAIL  set STATE = :STATE  ");
    		sqlParser.addSQL(" ,CANCEL_TYPE = :CANCEL_TYPE  ");
    		sqlParser.addSQL(", CANCEL_REASON = :CANCEL_REASON ,EXCHANGE_TYPE = :EXCHANGE_TYPE  ");
    		sqlParser.addSQL(", EXCHANGE_REASON = :EXCHANGE_REASON   ");
    		sqlParser.addSQL(" ,UPDATE_TIME = sysdate,RSRV_TAG1='2' ");
    		sqlParser.addSQL(" WHERE ORDER_ID = :ORDER_ID ");
    		Dao.executeUpdate(sqlParser);
    		
    		 return returnData;
    	}catch(Exception e){
    		returnData.put("resultCode", "-1");
        	returnData.put("resultInfo", e.getMessage());
        	return returnData;
    	}
    }
    /**
     * 一级电渠补换卡订单激活完成，同步数据到能开===========================================================================
     * @param input
     * @return
     * @throws Exception
     */
    public IData synReplaceCardOrder(IData input) throws Exception
    {
    	IData returnData=new DataMap();
    	returnData.put("RESULT_CODE", "0");
    	returnData.put("RESULT_INFO", "成功。");
    	IData param1 = new DataMap();
    	param1.put("PARAM_NAME", "crm.ABILITY.SyncReplaceCardOrder1");
    	IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
    	String Abilityurl = "";
    	if (Abilityurls != null && Abilityurls.size() > 0)
    	{
    		Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
    	}
    	else
    	{
    		CSAppException.appError("-1", "crm.ABILITY.SyncReplaceCardOrder1接口地址未在TD_S_BIZENV表中配置");
    	}
    	String apiAddress = Abilityurl;
    	IDataset orderList =getActivateReplaceCardOrderInfo();
    	if(IDataUtil.isNotEmpty(orderList))
    	{
    		for (int i = 0, size = orderList.size(); i < size; i++)
    		{
    			IData info = orderList.getData(i);
    			IData result = new DataMap();
    			String orderId = info.getString("ORDER_ID","");
                result.put("ORDER_ID", orderId);//订单编号
                try   
                {     
                	IData abilityData = new DataMap();
                	abilityData.put("OrderId", orderId);//订单编号
                	//根据orderId查询手机号码
        			IData data = new DataMap();
        			data.put("ORDER_ID", orderId);
        			IDataset dataList = getMobileByOrderId(data);
        			if(IDataUtil.isNotEmpty(dataList)){
        				abilityData.put("MobileNo", dataList.getData(0).getString("MOBILE"));//手机号码
        			}
                	abilityData.put("NumActiveStatus", "AD");//激活状态 AD表示 已激活
                	Date date = new Date();
            		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                	abilityData.put("StatusChangeDate", sdf.format(date));//状态修改时间 格式为2016-03-25 15:55:34.000
                	abilityData.put("Operator", "1000076");
                	logger.debug("panwj2========================"+abilityData);
                	IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,abilityData);
                	logger.debug("stopResult===========stopResult============="+stopResult);
                	//////////////////////////////////////////////////////////////////////
                	String resCode=stopResult.getString("resCode");
                	String X_RSPCODE="";
                	String X_RSPDESC="";
                	if(!"00000".equals(resCode))
                	{	
                		X_RSPCODE=stopResult.getString("resCode");  
                		X_RSPDESC=stopResult.getString("resMsg");
                	}    
                	else  
                	{
                		IData out=stopResult.getData("result");
                		X_RSPCODE=out.getString("bizCode");
                		X_RSPDESC=out.getString("bizDesc");
                	}
                	result.put("RESULT_CODE", X_RSPCODE);
                	result.put("RESULT_INFO", X_RSPDESC);
                	returnData.put("RESULT_CODE", X_RSPCODE);
                	returnData.put("RESULT_INFO", X_RSPDESC);
                }
                catch (Exception e)
                {	
                	logger.debug("---"+e.getMessage());
                	result.put("RESULT_CODE", "-1");
                	result.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage()));     
                	returnData.put("RESULT_CODE", "9999");
                	returnData.put("RESULT_INFO", "调能力平台异常导致反馈失败:" + (e.getMessage() == null ? "" : e.getMessage().length() > 128 ? e.getMessage().substring(0, 128) : e.getMessage())); 
                }   
                updateReplaceCardOrderActivateInfo(result);
             }
       }
       return returnData;
    }
    
    public IDataset getActivateReplaceCardOrderInfo() throws Exception
    {
    	StringBuilder sql=new StringBuilder();
    	sql.append("SELECT * FROM TF_B_ORDER_DETAIL T WHERE T.RSRV_STR1 = '4' AND T.RSRV_TAG5 IS NULL");
    	return Dao.qryBySql(sql, new DataMap());
    }
    public IDataset getMobileByOrderId(IData data) throws Exception
    {
    	StringBuilder sql=new StringBuilder();
    	sql.append("SELECT * FROM TF_B_ORDER_ITEM T WHERE T.ORDER_ID = :ORDER_ID ");
    	return Dao.qryBySql(sql, data);
    }
    public static int updateReplaceCardOrderActivateInfo(IData result)throws Exception
    {
    	SQLParser sqlParser = new SQLParser(result);
		sqlParser.addSQL("  update TF_B_ORDER_DETAIL  set  RSRV_TAG5='1' ");
		sqlParser.addSQL(", DEAL_STATE = :RESULT_CODE ,DEAL_RESULT = :RESULT_INFO  ");
		sqlParser.addSQL(" ,UPDATE_TIME = sysdate ");
		sqlParser.addSQL(" WHERE ORDER_ID = :ORDER_ID ");
		return Dao.executeUpdate(sqlParser);
	}
    
    
}