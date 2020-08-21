
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class MSpBizQry
{

    /**
     * 根据tab_name,sql_ref,eparchy_code查询平台服务信息
     */
    public static IDataset getSPInfoBySpCode(IData inparams) throws Exception
    {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT 1 FROM TD_M_SP_INFO WHERE SP_CODE = '" + inparams.getString("SP_CODE") + "' AND SP_STATUS = 'A'");

        return Dao.qryBySql(sql, new DataMap());
    }

    /**
     * 查询平台业务信息通过SP_CODE,BIZ_CODE
     * 
     * @param spCode
     * @param bizCode
     * @return
     * @throws Exception
     */
    public static IDataset queryBizInfoBySpcodeBizCode(String spCode, String bizCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SP_CODE", spCode);
        param.put("BIZ_CODE", bizCode);
        //return Dao.qryByCodeParser("TD_M_SP_BIZ", "SEL_BY_SPBIZCODE", param, Route.CONN_CRM_CEN);
        return UpcCall.queryBizInfoBySpcodeBizCode(spCode, bizCode);
    }

    /**
     * 通过SP_NAME,BIZ_NAME模糊查询平台业务信息
     * 
     * @param spName
     * @param bizName
     * @return
     * @throws Exception
     */
    public static IDataset queryBizInfoBySpNameBizName(String spName, String bizName) throws Exception
    {
        IData param = new DataMap();
        param.put("SP_NAME", spName);
        param.put("BIZ_NAME", bizName);
        //return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_SPBIZCODE_3", param, Route.CONN_CRM_CEN);
        return UpcCall.queryBizInfoBySpNameBizName(spName, bizName);
    }

    public static IDataset queryQuickPlatSvcs(String bizTypeCode, String rsrvStr1) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("RSRV_STR1", rsrvStr1);
        return Dao.qryByCodeParser("TD_M_SP_BIZ", "SEL_BY_CUST_SERV", param);
        // return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_CUST_SERV", param);
    }

    /**
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */

    public static IDataset querySPBiz(IData param, Pagination pagination) throws Exception
    {
        String sql = "SELECT SP_CODE,BIZ_CODE,BIZ_NAME,BIZ_TYPE,BIZ_TYPE_CODE,BIZ_ATTR,BIZ_DESC,BIZ_PROCESS_TAG,BIZ_STATE_CODE,BIZ_STATUS,SERV_CODE,USAGE_DESC,INTRO_URL,PRODUCT_NO,ACCESS_MODE,ORDER_MODE,PROV_ADDR,PROV_PORT,COUNT_SIDE1,COUNT_SIDE2,BILL_TYPE,PRICE,COUNT_PCT,NUM_TIME,NUM_DAY,SERVICE_PHONE,CONTACT,FIRST_DATE,FOREGIFT_TYPE,FOREGIFT,OPR_SOURCE,SMS_PROCESS_TAG,RECOGNIZE_CODE,NET_TAG,FILE_NAME,START_DATE,END_DATE,UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10  "
                + " FROM TD_M_SP_BIZ WHERE 1=1 "
                + " AND (SP_CODE = :SP_CODE OR :SP_CODE IS NULL) "
                + " AND (BIZ_CODE = :BIZ_CODE OR  :BIZ_CODE IS NULL) "
                + " AND (BIZ_TYPE_CODE = :BIZ_TYPE_CODE OR  :BIZ_TYPE_CODE IS NULL) "
                + " AND (BIZ_NAME LIKE '%'|| :BIZ_NAME || '%'  OR  :BIZ_NAME IS NULL) " + " AND (BIZ_ATTR = :BIZ_ATTR   OR  :BIZ_ATTR IS NULL) " + " AND (BIZ_STATE_CODE = :BIZ_STATE_CODE   OR  :BIZ_STATE_CODE IS NULL) ";
        return Dao.qryBySql(new StringBuilder(sql), param, pagination, Route.CONN_CRM_CEN);
    }

    public static IDataset querySpBizInfo(IData iData) throws Exception
    {

        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_PK", iData, Route.CONN_CRM_CEN);
    }

    public static IDataset querySpByBizCode(String bizCode) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_CODE", bizCode);
        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_BIZCODE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset querySpByBizCodeCnt(String bizCode) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_CODE", bizCode);
        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_BIZCODE_CNT", param, Route.CONN_CRM_CEN);
    }

    public static IDataset querySpBySpBizCode(String spCode, String bizCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SP_CODE", spCode);
        param.put("BIZ_CODE", bizCode);

        return Dao.qryByCodeParser("TD_M_SP_BIZ", "SEL_BY_SPBIZCODE", param, Route.CONN_CRM_CEN);

    }

    public static IDataset querySpBySpCode(String spCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SP_CODE", spCode);
        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_SPCODE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset querySpBySpCodeAndBizCode(String spCode, String bizCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SP_CODE", spCode);
        param.put("BIZ_CODE", bizCode);

        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_SPBIZCODE_NO_STATE", param, Route.CONN_CRM_CEN);

    }

    public static IDataset querySpBySpCodeCnt(String spCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SP_CODE", spCode);
        return Dao.qryByCode("TD_M_SP_BIZ", "SEL_BY_SPCODE_CNT", param, Route.CONN_CRM_CEN);
    }

}
