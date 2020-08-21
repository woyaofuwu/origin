
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.iot;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/***
 * 物联网实例查询
 * 
 * @author bobo
 */
public class InstancePfQuery
{

    public static IData queryPkgInstIdByInstId(String instId, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("INST_ID", instId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT INST_ID,PROD_INST_ID FROM TF_F_INSTANCE_PF WHERE INST_ID = :INST_ID AND INST_TYPE = 'P' AND USER_ID = '-1'");
        IDataset result = Dao.qryByParse(parser, routeId);
        if (result != null && !result.isEmpty())
        {
            return result.getData(0);
        }
        return null;
    }
    
    public static IData queryInstanceByInstId(String strUserid, String strInstId, String strInstType, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", strUserid);
        param.put("INST_ID", strInstId);
        param.put("INST_TYPE", strInstType);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT T.INST_ID,T.PROD_INST_ID, T.INST_TYPE, T.USER_ID, T.SUBS_ID, ");
        parser.addSQL("T.TRADE_ID, T.PLAT_CODE, T.CREATE_TIME, T.REMARK, ");
        parser.addSQL("T.RSRV_STR1, T.RSRV_STR2, T.RSRV_STR3, T.RSRV_STR4, T.RSRV_STR5, T.RSRV_STR6 ");
        parser.addSQL("FROM TF_F_INSTANCE_PF T ");
        parser.addSQL("WHERE T.INST_ID = :INST_ID AND T.INST_TYPE = :INST_TYPE AND T.USER_ID = :USER_ID");
        IDataset result = Dao.qryByParse(parser, routeId);
        if (result != null && !result.isEmpty())
        {
            return result.getData(0);
        }
        return null;
    }
    
    public static IData queryUserBySubsIdAndInstType(String subsId, String instType, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBS_ID", subsId);
        param.put("INST_TYPE", instType);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT INST_ID,PROD_INST_ID,INST_TYPE, USER_ID,SUBS_ID,PLAT_CODE,RSRV_STR1,RSRV_STR2,RSRV_STR3"
                + " FROM TF_F_INSTANCE_PF WHERE SUBS_ID = :SUBS_ID AND INST_TYPE = :INST_TYPE");
        IDataset result = Dao.qryByParse(parser, routeId);
        if (result != null && !result.isEmpty())
        {
            return result.getData(0);
        }
        return null;
    }
    
    public static IData queryInstanceRelByInstId(String strUserid, String strInstId, String strInstType, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", strUserid);
        param.put("INST_ID", strInstId);
        param.put("INST_TYPE", strInstType);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT T.INST_ID,T.PROD_INST_ID, T.INST_TYPE, T.USER_ID, T.SUBS_ID, ");
        parser.addSQL("T.TRADE_ID, T.PLAT_CODE, T.CREATE_TIME, T.REMARK, ");
        parser.addSQL("T.RSRV_STR1, T.RSRV_STR2, T.RSRV_STR3, T.RSRV_STR4, T.RSRV_STR5, T.RSRV_STR6 ");
        parser.addSQL("FROM TF_F_INSTANCE_PF_REL T ");
        parser.addSQL("WHERE T.INST_ID = :INST_ID AND T.INST_TYPE = :INST_TYPE AND T.USER_ID = :USER_ID");
        IDataset result = Dao.qryByParse(parser, routeId);
        if (result != null && !result.isEmpty())
        {
            return result.getData(0);
        }
        return null;
    }
    /**
     * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param i
     * @param j
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2017-12-25
     */
	public static IDataset queryAllUserToStop(int i,int j) throws Exception {
		IData param = new DataMap();
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT U.SERIAL_NUMBER FROM TF_F_USER_PRODUCT P,TF_F_USER U ");
		parser.addSQL("WHERE U.USER_ID = P.USER_ID ");
		parser.addSQL("AND U.USER_STATE_CODESET = '0' ");
		parser.addSQL("AND U.REMOVE_TAG = '0'  ");
		parser.addSQL("AND P.PRODUCT_ID IN  (SELECT PARAM_CODE FROM TD_S_COMMPARA WHERE PARAM_ATTR=9015 AND PARA_CODE20='NB_PRODUCT' ) ");
		parser.addSQL("AND SYSDATE BETWEEN P.START_DATE AND P.END_DATE ");
		parser.addSQL("AND NOT EXISTS( ");
		parser.addSQL("    SELECT 1 FROM TF_F_USER_DISCNT T ");
		parser.addSQL("    WHERE U.USER_ID = T.USER_ID ");
		parser.addSQL("    AND T.DISCNT_CODE IN (SELECT PARAM_CODE FROM TD_S_COMMPARA WHERE PARAM_ATTR=9013 AND (PARA_CODE20='NB_TEST' OR PARA_CODE20='NB_GPRS') )  ");
		parser.addSQL("    AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ) ");
		parser.addSQL("AND (U.PARTITION_ID>="+i+" AND U.PARTITION_ID<"+j+") ");
		IDataset result = Dao.qryByParseAllCrm(parser,true);
		return result;
	}
	
	/**
     * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param i
     * @param j
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2017-12-25
     */
	public static IDataset queryUserProductDiscnt(int i) throws Exception 
	{
		IData param = new DataMap();
		String sqlSring = "SEL_USER_PRODUCT_DISCNT" + i;
		IDataset result = Dao.qryByCode("TF_F_USER", sqlSring, param);
		return result;
	}
	
	/**
	 * 根据PBOSS用户编码查询CRM的用户编码
	 * @param subsId
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	 public static IData queryUserIdBySubsId(String subsId, String routeId) throws Exception
	    {
	        IData param = new DataMap();
	        param.put("SUBS_ID", subsId);
	        SQLParser parser = new SQLParser(param);
	        parser.addSQL("SELECT USER_ID FROM TF_F_INSTANCE_PF WHERE SUBS_ID = :SUBS_ID AND INST_TYPE = 'U'");
	        IDataset result = Dao.qryByParse(parser, routeId);
	        if (result != null && !result.isEmpty())
	        {
	            return result.getData(0);
	        }
	        return null;
	 }
	 
	 /**
	  * 根据PROD_INST_ID、USER_ID获取对应原子产品的INST_ID
	  * @param userId
	  * @return
	  * @throws Exception
	  */
	 public static IDataset queryInstancePfInfoByUserIds(String userId,String instType,String prodIntstId) throws Exception	{
		 IData param = new DataMap();
		 param.put("USER_ID", userId);
		 param.put("INST_TYPE", instType);
		 param.put("PROD_INST_ID", prodIntstId);
		 SQLParser parser = new SQLParser(param);
		 parser.addSQL(" SELECT INST_ID,PROD_INST_ID,INST_TYPE, USER_ID,SUBS_ID,PLAT_CODE,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,REMARK FROM TF_F_INSTANCE_PF P WHERE P.USER_ID = :USER_ID AND P.INST_TYPE = :INST_TYPE AND P.PROD_INST_ID = :PROD_INST_ID");
		 return Dao.qryByParse(parser,Route.CONN_CRM_CG);
	 }
	 
}
