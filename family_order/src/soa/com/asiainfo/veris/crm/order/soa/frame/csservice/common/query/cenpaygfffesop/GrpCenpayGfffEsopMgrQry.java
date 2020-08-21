
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cenpaygfffesop;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;

public class GrpCenpayGfffEsopMgrQry
{
    /**
     * 通过在主台账查询自由充集团产品的未执行的工单
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpCenpayGfffByTradeId(IData param) 
    	throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT TO_CHAR(A.TRADE_ID) TRADE_ID, ");
        parser.addSQL("       A.ACCEPT_MONTH ACCEPT_MONTH, ");
        parser.addSQL("       TO_CHAR(A.ORDER_ID) ORDER_ID, ");
        parser.addSQL("       A.TRADE_TYPE_CODE TRADE_TYPE_CODE ");
        parser.addSQL("  FROM TF_B_TRADE A, TF_B_ORDER B ");
        parser.addSQL(" WHERE A.TRADE_ID = :TRADE_ID ");
        parser.addSQL("   AND A.ORDER_ID = B.ORDER_ID ");
        parser.addSQL("   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        parser.addSQL("   AND A.SUBSCRIBE_STATE = '0' ");
        parser.addSQL("   AND B.ACCEPT_MONTH = TO_NUMBER(SUBSTR(B.ORDER_ID, 5, 2)) ");
        parser.addSQL("   AND B.ORDER_STATE = '0' ");
        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 根据tradeId修改trade表的执行时间为当前时间
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateGrpGfffTradeByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE T  ");
	    sql.append(" SET T.EXEC_TIME = SYSDATE ");
	    sql.append(" 	WHERE ");
	    sql.append(" 	T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH =TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据orderId修改order表的执行时间为当前时间
     * @param orderId
     * @return
     * @throws Exception
     */
    public static int updateGrpGfffOrderByOrderId(String orderId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("ORDER_ID", orderId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_ORDER T  ");
	    sql.append(" SET T.EXEC_TIME = SYSDATE ");
	    sql.append(" 	WHERE T.ORDER_ID = :ORDER_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:ORDER_ID, 5, 2)) ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId把trade表数据搬历史表
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int insertGrpGfffBhTradeByTradeId(String tradeId)
    	throws Exception
    {
    	IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" INSERT INTO TF_BH_TRADE  ");
	    sql.append("  SELECT * ");
	    sql.append(" FROM TF_B_TRADE T ");
	    sql.append("  WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * 根据orderId把order表的数据搬历史表
     * @param orderId
     * @return
     * @throws Exception
     */
    public static int insertGrpGfffBhOrderByOrderId(String orderId)
		throws Exception
	{
		IData param = new DataMap();
	    param.put("ORDER_ID", orderId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" INSERT INTO TF_BH_ORDER  ");
	    sql.append("  SELECT * ");
	    sql.append(" FROM TF_B_ORDER T ");
	    sql.append("  WHERE T.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:ORDER_ID, 5, 2)) ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId把trade表数据删除
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int deleteGrpGfffBTradeByTradeId(String tradeId)
    	throws Exception
    {
    	IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" DELETE FROM TF_B_TRADE T  ");
	    sql.append("  WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * 根据orderId把order表的数据删除
     * @param orderId
     * @return
     * @throws Exception
     */
    public static int deleteGrpGfffBOrderByOrderId(String orderId)
		throws Exception
	{
		IData param = new DataMap();
	    param.put("ORDER_ID", orderId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" DELETE FROM TF_B_ORDER T  ");
	    sql.append("  WHERE T.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:ORDER_ID, 5, 2)) ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}

    /**
     * 根据tradeId,modifyTag=0修改优惠表的开始时间为当前时间
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeDiscntNowByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_DISCNT T  ");
	    sql.append(" SET T.START_DATE = SYSDATE ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '0' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=0修改服务表的开始时间为当前时间
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeSvcNowByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_SVC T  ");
	    sql.append(" SET T.START_DATE = SYSDATE ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '0' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=0修改属性表的开始时间为当前时间
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeAttrNowByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_ATTR T  ");
	    sql.append(" SET T.START_DATE = SYSDATE ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '0' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=0修改GRP_CENPAY表的开始时间为当前时间
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeGrpCenpayNowByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_GRP_CENPAY T  ");
	    sql.append(" SET T.START_DATE = SYSDATE ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '0' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=0的条件修改优惠表的开始时间为下个月1号
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeDiscntNextDateByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_DISCNT T  ");
	    sql.append(" SET T.START_DATE = TRUNC(LAST_DAY(SYSDATE) + 1) ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '0' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=0的条件修改服务表的开始时间为下个月1号
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeSvcNextDateByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_SVC T  ");
	    sql.append(" SET T.START_DATE = TRUNC(LAST_DAY(SYSDATE) + 1) ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '0' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=0,attrCode的条件修改属性表的开始时间为下个月1号
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeAttrNextDateByTradeId(String tradeId,String attrCode) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    param.put("ATTR_CODE", attrCode);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_ATTR T  ");
	    sql.append(" SET T.START_DATE = TRUNC(LAST_DAY(SYSDATE) + 1) ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '0' ");
	    sql.append(" 	AND T.ATTR_CODE = :ATTR_CODE ");
	    sql.append(" 	AND T.INST_TYPE = 'D' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=0的条件修改属性表的开始时间为下个月1号
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeAttrNextDateByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_ATTR T  ");
	    sql.append(" SET T.START_DATE = TRUNC(LAST_DAY(SYSDATE) + 1) ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '0' ");
	    sql.append(" 	AND T.INST_TYPE = 'D' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=1修改属性表的开始时间为当前时间,结束时间为月底
     * @param tradeId
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static int updateAllTradeAttrDateByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_ATTR T  ");
	    sql.append(" SET T.START_DATE = SYSDATE, ");
	    sql.append(" T.END_DATE = LAST_DAY(TRUNC(SYSDATE,'MM')) + 1 -1/86400 ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '1' ");
	    sql.append(" 	AND T.INST_TYPE = 'D' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=1,attrCode修改属性表的开始时间为当前时间,结束时间为月底
     * @param tradeId
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static int updateTradeAttrNowNextByTradeId(String tradeId,String attrCode) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    param.put("ATTR_CODE", attrCode);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_ATTR T  ");
	    sql.append(" SET T.START_DATE = SYSDATE, ");
	    sql.append(" T.END_DATE = LAST_DAY(TRUNC(SYSDATE,'MM')) + 1 -1/86400 ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '1' ");
	    sql.append(" 	AND T.ATTR_CODE = :ATTR_CODE  ");
	    sql.append(" 	AND T.INST_TYPE = 'D' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=1修改服务表的开始时间为当前时间,结束时间为月底
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeSvcNowNextByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_SVC T  ");
	    sql.append(" SET T.START_DATE = SYSDATE, ");
	    sql.append(" T.END_DATE = LAST_DAY(TRUNC(SYSDATE,'MM')) + 1 -1/86400 ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '1' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 根据tradeId,modifyTag=1修改优惠表的开始时间为当前时间,结束时间为月底
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeDiscntNowNextByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_DISCNT T  ");
	    sql.append(" SET T.START_DATE = SYSDATE, ");
	    sql.append(" T.END_DATE = LAST_DAY(TRUNC(SYSDATE,'MM')) + 1 -1/86400 ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '1' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    
    /**
     * 根据tradeId,modifyTag=1修改cenpay表的开始时间为当前时间,结束时间为月底
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static int updateTradeCenpayNewNextByTradeId(String tradeId) 
		throws Exception
	{
	    IData param = new DataMap();
	    param.put("TRADE_ID", tradeId);
	    StringBuilder sql = new StringBuilder(500);
	    sql.append(" UPDATE TF_B_TRADE_GRP_CENPAY T  ");
	    sql.append(" SET T.START_DATE = SYSDATE, ");
	    sql.append(" T.END_DATE = LAST_DAY(TRUNC(SYSDATE,'MM')) + 1 -1/86400 ");
	    sql.append(" 	WHERE T.TRADE_ID = :TRADE_ID ");
	    sql.append(" 	AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
	    sql.append(" 	AND T.MODIFY_TAG = '1' ");
	    return Dao.executeUpdate(sql, param,Route.getJourDb(BizRoute.getRouteId()));
	}
    
    /**
     * 通过TRADE_ID在台帐表中查询合同信息和服务号码
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryContractAndSerialNumberInfos(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param); 
        parser.addSQL(" SELECT A.CONTRACT_ID,DECODE(:GET_TYPE,'0', A.CONTRACT_ID||'', DECODE(A.MODIFY_TAG,'0','[新增]|','1','[删除]|','2','[修改为]|','')||A.CONTRACT_ID) CONTRACT_INFO, ");
        parser.addSQL("       DECODE(:GET_TYPE,'0', A.SERIAL_NUMBER, DECODE(A.MODIFY_TAG,'0','[新增]|','1','[删除]|','')||A.SERIAL_NUMBER) SERIAL_NUMBER ");
        parser.addSQL("  FROM TF_B_TRADE_USER A ");
        parser.addSQL(" WHERE A.TRADE_ID = :TRADE_ID ");
        parser.addSQL("   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        
        IDataset userInfos = Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
        for(int i=0;i<userInfos.size();i++){
        	String contractId = userInfos.getData(i).getString("CONTRACT_ID","");
        	if(!contractId.equals("")){
	        	param.put("CONTRACT_ID", contractId);
	        	SQLParser parser1 = new SQLParser(param);
	        	parser1.addSQL(" SELECT CONTRACT_NAME FROM TF_F_CUST_CONTRACT " );
	        	parser1.addSQL(" WHERE CONTRACT_ID = :CONTRACT_ID ");
	        	IDataset contracts = Dao.qryByParse(parser1);
	        	if(!contracts.isEmpty()){
	        		userInfos.getData(i).put("CONTRACT_INFO", userInfos.getData(i).getString("CONTRACT_INFO", "")+"|"+contracts.getData(0).getString("CONTRACT_NAME", ""));
	        	}	
        	}
        }
        return userInfos;
    }
    
    /**
     * 通过USER_ID在资料表中查询合同信息和服务号码
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryContractAndSerialNumberInfos2(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.CONTRACT_ID,A.CONTRACT_ID CONTRACT_INFO,A.SERIAL_NUMBER ");
        parser.addSQL("  FROM TF_F_USER A ");
        parser.addSQL(" WHERE A.USER_ID = :USER_ID ");
        parser.addSQL("   AND A.PARTITION_ID = MOD(:USER_ID, 10000) ");
        IDataset userInfos = Dao.qryByParse(parser);
        for(int i=0;i<userInfos.size();i++){
        	String contractId = userInfos.getData(i).getString("CONTRACT_ID","");
        	if(!contractId.equals("")){
	        	param.put("CONTRACT_ID", contractId);
	        	SQLParser parser1 = new SQLParser(param);
	        	parser1.addSQL(" SELECT CONTRACT_NAME FROM TF_F_CUST_CONTRACT WHERE CONTRACT_ID = :CONTRACT_ID");
	        	IDataset contracts = Dao.qryByParse(parser1);
	        	if(!contracts.isEmpty()){
	        		userInfos.getData(i).put("CONTRACT_INFO", userInfos.getData(i).getString("CONTRACT_INFO", "")+"|"+contracts.getData(0).getString("CONTRACT_NAME", ""));
	        	}	
        	}
        }
        return userInfos;
    }
    
    /**
     * 通过TRADE_ID在台帐表中查询合同信息和服务号码和拆除原因及备注，用于拆除
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryContractAndSerialNumberInfos3(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.CONTRACT_ID,DECODE(:GET_TYPE,'0', A.CONTRACT_ID||'', DECODE(A.MODIFY_TAG,'0','[新增]|','1','[删除]|','2','[修改为]|','')||A.CONTRACT_ID) CONTRACT_INFO, ");
        parser.addSQL("       DECODE(:GET_TYPE,'0', A.SERIAL_NUMBER, DECODE(A.MODIFY_TAG,'0','[新增]|','1','[删除]|','2','[修改为]|','')||A.SERIAL_NUMBER) SERIAL_NUMBER, ");
        parser.addSQL("       A.REMARK||'|'||C.REMARK REMARK ");
        parser.addSQL("  FROM TF_B_TRADE_USER A, TF_B_TRADE C ");
        parser.addSQL(" WHERE A.TRADE_ID = :TRADE_ID ");
        parser.addSQL("   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        parser.addSQL("   AND A.TRADE_ID = C.TRADE_ID ");
        IDataset userInfos = Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
        for(int i=0;i<userInfos.size();i++){
        	String contractId = userInfos.getData(i).getString("CONTRACT_ID","");
        	if(!contractId.equals("")){
	        	param.put("CONTRACT_ID", contractId);
	        	SQLParser parser1 = new SQLParser(param);
	        	parser1.addSQL(" SELECT CONTRACT_NAME FROM TF_F_CUST_CONTRACT WHERE CONTRACT_ID = :CONTRACT_ID");
	        	IDataset contracts = Dao.qryByParse(parser1);
	        	if(!contracts.isEmpty()){
	        		userInfos.getData(i).put("CONTRACT_INFO", userInfos.getData(i).getString("CONTRACT_INFO", "")+"|"+contracts.getData(0).getString("CONTRACT_NAME", ""));
	        	}	
        	}
        }
        return userInfos;
    }
    
    /**
     * 通过TRADE_ID在台帐表中查询产品信息
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryProductInfos(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.PRODUCT_ID,DECODE(:GET_TYPE,'0', A.PRODUCT_ID||'', DECODE(A.MODIFY_TAG,'0','[新增]|','1','[删除]|','2','[修改为]|','')||A.PRODUCT_ID) PRODUCT_INFO ");
        parser.addSQL("  FROM TF_B_TRADE_PRODUCT A");
        parser.addSQL(" WHERE A.TRADE_ID = :TRADE_ID ");
        parser.addSQL("   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        IDataset userInfos = Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
        if(!userInfos.isEmpty()){
        	for(int i=0;i<userInfos.size();i++){
        		userInfos.getData(i).put("PRODUCT_INFO", userInfos.getData(i).getString("PRODUCT_ID", "")+"|"+UProductInfoQry.getProductNameByProductId(userInfos.getData(i).getString("PRODUCT_ID", "")));
        	}
        }
        return userInfos;
    }
    
    /**
     * 通过USER_ID在资料表中查询产品信息
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryProductInfos2(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.PRODUCT_ID ");
        parser.addSQL("  FROM TF_F_USER_PRODUCT A ");
        parser.addSQL(" WHERE A.USER_ID = :USER_ID ");
        parser.addSQL("   AND A.PARTITION_ID = MOD(:USER_ID, 10000) ");
        
        IDataset userInfos = Dao.qryByParse(parser);
        if(!userInfos.isEmpty()){
        	for(int i=0;i<userInfos.size();i++){
        		userInfos.getData(i).put("PRODUCT_INFO", userInfos.getData(i).getString("PRODUCT_ID", "")+"|"+UProductInfoQry.getProductNameByProductId(userInfos.getData(i).getString("PRODUCT_ID", "")));
        	}
        }
        return userInfos;
    }
    
    /**
     * 通过TRADE_ID在台帐表中查询元素信息
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryElementInfos(IData param) throws Exception
    {
        IDataset elementInfos = new DatasetList();
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.SERVICE_ID,DECODE(:GET_TYPE,'0', A.SERVICE_ID || '|服务|', DECODE(A.MODIFY_TAG,'0','[新增]|','1','[删除]|','2','[修改为]|','')|| A.SERVICE_ID || '|服务|') ELEMENT_INFO ");
        parser.addSQL("  FROM TF_B_TRADE_SVC A ");
        parser.addSQL(" WHERE A.TRADE_ID = :TRADE_ID ");
        parser.addSQL("   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        IDataset tradesvc = Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
        for(int i=0;i<tradesvc.size();i++){
        	String svcId = tradesvc.getData(i).getString("SERVICE_ID","");
        	if(!svcId.equals("")){
	        	tradesvc.getData(i).put("ELEMENT_INFO", tradesvc.getData(i).getString("ELEMENT_INFO", "")+USvcInfoQry.getSvcNameBySvcId(svcId));
	        }	
        }
        elementInfos.addAll(tradesvc);
        
        SQLParser parser1 = new SQLParser(param);
        parser1.addSQL(" SELECT A.DISCNT_CODE,B.ATTR_CODE,B.ATTR_VALUE,DECODE(:GET_TYPE, '0', A.DISCNT_CODE || '|优惠|', ");
        parser1.addSQL("  	   				DECODE(A.MODIFY_TAG, '0', '[新增]|', '1', '[删除]|', '2', '[变更后]|', '') || A.DISCNT_CODE || '|优惠|' ) ELEMENT_INFO ");
        parser1.addSQL("  FROM TF_B_TRADE_DISCNT A,TF_B_TRADE_ATTR B ");
        parser1.addSQL(" WHERE A.MODIFY_TAG IN ('0','2') ");
        parser1.addSQL("   AND A.TRADE_ID = :TRADE_ID ");
        parser1.addSQL("   AND A.TRADE_ID = B.TRADE_ID(+) ");
        parser1.addSQL(" AND A.DISCNT_CODE = B.ELEMENT_ID(+) ");
        parser1.addSQL("   AND A.MODIFY_TAG = '0' ");
        parser1.addSQL("   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        IDataset discnts1 = Dao.qryByParse(parser1,Route.getJourDb(BizRoute.getRouteId()));
        for(int i=0;i<discnts1.size();i++){
        	String discntCode = discnts1.getData(i).getString("DISCNT_CODE","");
        	String attrCode = discnts1.getData(i).getString("ATTR_CODE","");
        	if(!discntCode.equals("")){
        		discnts1.getData(i).put("ELEMENT_INFO", discnts1.getData(i).getString("ELEMENT_INFO", "")+UDiscntInfoQry.getDiscntExplainByDiscntCode(discntCode));
	        }
        	IDataset itema = UItemAInfoQry.queryOfferChaByIdAndIdType("D",discntCode,"ZZZZ");
        	for(int j=0;j<itema.size();j++){
        		if(attrCode.equals(itema.getData(j).getString("ATTR_CODE", ""))&&itema.getData(j).getString("ATTR_LABLE", "").equals("折扣率")){
        			discnts1.getData(i).put("ELEMENT_INFO", discnts1.getData(i).getString("ELEMENT_INFO", "")+"|折扣率："+discnts1.getData(i).getString("ATTR_VALUE", ""));
        		}
        		if(attrCode.equals(itema.getData(j).getString("ATTR_CODE", ""))&&itema.getData(j).getString("ATTR_LABLE", "").equals("流量份数")){
        			discnts1.getData(i).put("ELEMENT_INFO", discnts1.getData(i).getString("ELEMENT_INFO", "")+"|流量份数："+discnts1.getData(i).getString("ATTR_VALUE", ""));
        		}
        		if(attrCode.equals(itema.getData(j).getString("ATTR_CODE", ""))&&itema.getData(j).getString("ATTR_LABLE", "").equals("流量总额(M)")){
        			long nub = Long.parseLong(discnts1.getData(i).getString("ATTR_VALUE", "0"));
        			discnts1.getData(i).put("ELEMENT_INFO", discnts1.getData(i).getString("ELEMENT_INFO", "")+"|流量总额(M)："+nub/1024/1024);
        		}
        	}
        }
        elementInfos.addAll(discnts1);
        
        SQLParser parser2 = new SQLParser(param);
        parser2.addSQL(" SELECT A.DISCNT_CODE,B.ATTR_CODE,B.ATTR_VALUE,DECODE(:GET_TYPE, '0', A.DISCNT_CODE || '|优惠|', ");
        parser2.addSQL("  	   				DECODE(A.MODIFY_TAG, '0', '[新增]|', '1', '[删除]|', '2', '[变更前]|', '') || A.DISCNT_CODE || '|优惠|' ) ELEMENT_INFO ");
        parser2.addSQL("  FROM TF_B_TRADE_DISCNT A,TF_B_TRADE_ATTR B ");
        parser2.addSQL(" WHERE A.MODIFY_TAG IN ('1','2') ");
        parser2.addSQL("   AND A.TRADE_ID = :TRADE_ID ");
        parser2.addSQL("   AND A.TRADE_ID = B.TRADE_ID(+) ");
        parser2.addSQL(" AND A.DISCNT_CODE = B.ELEMENT_ID(+) ");
        parser2.addSQL("   AND A.MODIFY_TAG = '1' ");
        parser2.addSQL("   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");  
        IDataset discnts2 = Dao.qryByParse(parser2,Route.getJourDb(BizRoute.getRouteId()));
        for(int i=0;i<discnts2.size();i++){
        	String discntCode = discnts2.getData(i).getString("DISCNT_CODE","");
        	String attrCode = discnts2.getData(i).getString("ATTR_CODE","");
        	if(!discntCode.equals("")){
        		discnts2.getData(i).put("ELEMENT_INFO", discnts2.getData(i).getString("ELEMENT_INFO", "")+UDiscntInfoQry.getDiscntExplainByDiscntCode(discntCode));
	        }
        	IDataset itema = UItemAInfoQry.queryOfferChaByIdAndIdType("D",discntCode,"ZZZZ");
        	for(int j=0;j<itema.size();j++){
        		if(attrCode.equals(itema.getData(j).getString("ATTR_CODE", ""))&&itema.getData(j).getString("ATTR_LABLE", "").equals("折扣率")){
        			discnts2.getData(i).put("ELEMENT_INFO", discnts2.getData(i).getString("ELEMENT_INFO", "")+"|折扣率："+discnts2.getData(i).getString("ATTR_VALUE", ""));
        		}
        		if(attrCode.equals(itema.getData(j).getString("ATTR_CODE", ""))&&itema.getData(j).getString("ATTR_LABLE", "").equals("流量份数")){
        			discnts2.getData(i).put("ELEMENT_INFO", discnts2.getData(i).getString("ELEMENT_INFO", "")+"|流量份数："+discnts2.getData(i).getString("ATTR_VALUE", ""));
        		}
        		if(attrCode.equals(itema.getData(j).getString("ATTR_CODE", ""))&&itema.getData(j).getString("ATTR_LABLE", "").equals("流量总额(M)")){
        			long nub = Long.parseLong(discnts2.getData(i).getString("ATTR_VALUE", "0"));
        			discnts2.getData(i).put("ELEMENT_INFO", discnts2.getData(i).getString("ELEMENT_INFO", "")+"|流量总额(M)："+nub/1024/1024);
        		}
        	}
        }
        elementInfos.addAll(discnts2);
        
        return elementInfos;
    }
    
    /**
     * 通过USER_ID在资料表中查询元素信息
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryElementInfos2(IData param) throws Exception
    {
    	IDataset elementInfos = new DatasetList();

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.SERVICE_ID,B.ATTR_CODE,B.ATTR_VALUE,A.SERVICE_ID || '|服务|'  ");
        parser.addSQL(" FROM TF_F_USER_SVC A, TF_F_USER_ATTR B ");
        parser.addSQL(" WHERE A.USER_ID = :USER_ID ");
        parser.addSQL(" AND A.SERVICE_ID = B.ELEMENT_ID(+) ");
        parser.addSQL(" AND A.USER_ID = B.USER_ID(+) ");
        parser.addSQL(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ");
        
        IDataset tradesvc = Dao.qryByParse(parser);
        for(int i=0;i<tradesvc.size();i++){
        	String svcId = tradesvc.getData(i).getString("SERVICE_ID","");
        	if(!svcId.equals("")){
	        	tradesvc.getData(i).put("ELEMENT_INFO", tradesvc.getData(i).getString("ELEMENT_INFO", "")+USvcInfoQry.getSvcNameBySvcId(svcId));
	        }	
        }
        elementInfos.addAll(tradesvc);
        
        SQLParser parser1 = new SQLParser(param);
        parser1.addSQL(" SELECT A.DISCNT_CODE,B.ATTR_CODE,B.ATTR_VALUE,A.DISCNT_CODE || '|优惠|'  ");
        parser1.addSQL(" FROM TF_F_USER_DISCNT A, TF_F_USER_ATTR B ");
        parser1.addSQL(" WHERE A.USER_ID = :USER_ID ");
        parser1.addSQL(" AND A.DISCNT_CODE = B.ELEMENT_ID(+) ");
        parser1.addSQL(" AND A.USER_ID = B.USER_ID(+) ");
        parser1.addSQL(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ");
        
        IDataset discnts1 = Dao.qryByParse(parser1);
        for(int i=0;i<discnts1.size();i++){
        	String discntCode = discnts1.getData(i).getString("DISCNT_CODE","");
        	String attrCode = discnts1.getData(i).getString("ATTR_CODE","");
        	if(!discntCode.equals("")){
        		discnts1.getData(i).put("ELEMENT_INFO", discnts1.getData(i).getString("ELEMENT_INFO", "")+UDiscntInfoQry.getDiscntExplainByDiscntCode(discntCode));
	        }
        	IDataset itema = UItemAInfoQry.queryOfferChaByIdAndIdType("D",discntCode,"ZZZZ");
        	for(int j=0;j<itema.size();j++){
        		if(attrCode.equals(itema.getData(j).getString("ATTR_CODE", ""))&&itema.getData(j).getString("ATTR_LABLE", "").equals("折扣率")){
        			discnts1.getData(i).put("ELEMENT_INFO", discnts1.getData(i).getString("ELEMENT_INFO", "")+"|折扣率："+discnts1.getData(i).getString("ATTR_VALUE", ""));
        		}
        		if(attrCode.equals(itema.getData(j).getString("ATTR_CODE", ""))&&itema.getData(j).getString("ATTR_LABLE", "").equals("流量份数")){
        			discnts1.getData(i).put("ELEMENT_INFO", discnts1.getData(i).getString("ELEMENT_INFO", "")+"|流量份数："+discnts1.getData(i).getString("ATTR_VALUE", ""));
        		}
        		if(attrCode.equals(itema.getData(j).getString("ATTR_CODE", ""))&&itema.getData(j).getString("ATTR_LABLE", "").equals("流量总额(M)")){
        			long nub = Long.parseLong(discnts1.getData(i).getString("ATTR_VALUE", "0"));
        			discnts1.getData(i).put("ELEMENT_INFO", discnts1.getData(i).getString("ELEMENT_INFO", "")+"|流量总额(M)："+nub/1024/1024);
        		}
        	}
        }
        elementInfos.addAll(discnts1);
        
        return elementInfos;
    }
    
    /**
     * 通过TRADE_ID在台帐表中查询成员附加产品信息
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpProductInfos(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT A.ELEMENT_ID,A.ELEMENT_TYPE_CODE,DECODE(:GET_TYPE,'0', A.ELEMENT_ID || '|优惠|', DECODE(A.MODIFY_TAG,'0','[新增]|','1','[删除]|','2','[修改为]|','')||A.ELEMENT_ID || '|优惠|') GRP_INFO ");
        parser.addSQL("  FROM TF_B_TRADE_GRP_PACKAGE A ");
        parser.addSQL(" WHERE A.ELEMENT_TYPE_CODE = 'D' ");
        parser.addSQL("   AND A.TRADE_ID = :TRADE_ID ");
        parser.addSQL("   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT A.ELEMENT_ID,A.ELEMENT_TYPE_CODE,DECODE(:GET_TYPE,'0', A.ELEMENT_ID || '|服务|', DECODE(A.MODIFY_TAG,'0','[新增]|','1','[删除]|','2','[修改为]|','')||A.ELEMENT_ID || '|服务|') GRP_INFO ");
        parser.addSQL("  FROM TF_B_TRADE_GRP_PACKAGE A ");
        parser.addSQL(" WHERE A.ELEMENT_TYPE_CODE = 'S' ");
        parser.addSQL("   AND A.TRADE_ID = :TRADE_ID ");
        parser.addSQL("   AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        IDataset elementInfos = Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
        
        for(int i=0;i<elementInfos.size();i++){
        	String elementId = elementInfos.getData(i).getString("ELEMENT_ID","");
        	if(!elementId.equals("")&&elementInfos.getData(i).getString("ELEMENT_TYPE_CODE","").equals("S")){
        		elementInfos.getData(i).put("GRP_INFO", elementInfos.getData(i).getString("GRP_INFO", "")+USvcInfoQry.getSvcNameBySvcId(elementId));
	        }else{
	        	elementInfos.getData(i).put("GRP_INFO", elementInfos.getData(i).getString("GRP_INFO", "")+UDiscntInfoQry.getDiscntExplainByDiscntCode(elementId));
	        }
        }   
        return elementInfos;
    }
    
    /**
     * 通过USER_ID在资料表中查询成员附加产品信息
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpProductInfos2(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  A.ELEMENT_ID,A.ELEMENT_TYPE_CODE,A.ELEMENT_ID || '|优惠|' GRP_INFO ");
        parser.addSQL(" FROM TF_F_USER_GRP_PACKAGE A ");
        parser.addSQL(" WHERE A.ELEMENT_TYPE_CODE = 'D' ");
        parser.addSQL(" AND A.USER_ID = :USER_ID ");
        parser.addSQL(" AND A.PARTITION_ID = MOD(:USER_ID, 10000)");
        parser.addSQL(" UNION ALL ");
        parser.addSQL(" SELECT A.ELEMENT_ID, A.ELEMENT_TYPE_CODE,A.ELEMENT_ID || '|服务|' GRP_INFO ");
        parser.addSQL(" FROM TF_F_USER_GRP_PACKAGE A ");
        parser.addSQL(" WHERE A.ELEMENT_TYPE_CODE = 'S' ");
        parser.addSQL(" AND A.USER_ID = :USER_ID ");
        parser.addSQL(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ");
        IDataset elementInfos = Dao.qryByParse(parser);
        
        for(int i=0;i<elementInfos.size();i++){
        	String elementId = elementInfos.getData(i).getString("ELEMENT_ID","");
        	if(!elementId.equals("")&&elementInfos.getData(i).getString("ELEMENT_TYPE_CODE","").equals("S")){
        		elementInfos.getData(i).put("GRP_INFO", elementInfos.getData(i).getString("GRP_INFO", "")+USvcInfoQry.getSvcNameBySvcId(elementId));
	        }else{
	        	elementInfos.getData(i).put("GRP_INFO", elementInfos.getData(i).getString("GRP_INFO", "")+UDiscntInfoQry.getDiscntExplainByDiscntCode(elementId));
	        }
        }   
        return elementInfos;
    }
    
    /**
     * 通过TRADE_ID在台帐表中查询账户信息
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryAccountInfos(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT A.ACCT_ID,DECODE(:GET_TYPE,'0', A.ACCT_ID||'', DECODE(A.MODIFY_TAG,'0','[关系新增]|','1','[关系解除]|','2','[修改为]|','')||A.ACCT_ID) ACCOUNT_INFO ");
        parser.addSQL(" 	FROM TF_B_TRADE_PAYRELATION A ");
        parser.addSQL(" WHERE A.TRADE_ID = :TRADE_ID ");
        parser.addSQL(" 	AND A.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        IDataset payrelations = Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
        for(int i=0;i<payrelations.size();i++){
        	String acctId = payrelations.getData(i).getString("ACCT_ID","");
        	if(!acctId.equals("")){
	        	param.put("ACCT_ID", acctId);
	        	SQLParser parser1 = new SQLParser(param);
	        	parser1.addSQL(" SELECT PAY_NAME FROM TF_F_ACCOUNT WHERE ACCT_ID = :ACCT_ID");
	        	IDataset contracts = Dao.qryByParse(parser1);
	        	if(!contracts.isEmpty()){
	        		payrelations.getData(i).put("ACCOUNT_INFO", payrelations.getData(i).getString("ACCOUNT_INFO", "")+"|"+contracts.getData(0).getString("PAY_NAME", ""));
	        	}	
        	}
        }
        return payrelations;
    }

    /**
     * 通过PRODUCT_OFFERING_ID获取用户TF_F_USER_GRP_CENPAY的信息
     * @param userId,productOfferingId
     * @return
     * @throws Exception
     */
	public static IDataset getUserGrpCenPayByUserIdProductOfferId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		//param.put("PRODUCT_OFFER_ID", productOfferingId);
		IDataset userGrpCenPayInfo = new DatasetList();
		userGrpCenPayInfo = Dao.qryByCodeParser("TF_F_USER_GRP_CENPAY", "SEL_BY_PRODUCTOFFERID", param);
		return userGrpCenPayInfo;
	}

	/**
     * 通过PRODUCT_OFFERING_ID修改TF_F_USER_GRP_CENPAY的违规接口字段RSRV_STR5
     * @param userId,rsrvStr5
     * @return
     * @throws Exception
     */
	public static void updateFoulTagByProductOfferId(String userId,String rsrvStr5) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RSRV_STR5", rsrvStr5);
		Dao.executeUpdateByCodeCode("TF_F_USER_GRP_CENPAY", "UPD_RSRVSTR5_BY_PRODUCTOFFERID", param);
	}
    
}
