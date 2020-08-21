
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class MSpInfoQry
{

    /**
     * 根据sp_code,bize_code,获取表TD_M_SP_BIZ中的局数据
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset querySpBizInfoDataset(IData param) throws Exception
    {
        String spBizInfoSql = "SELECT SP_CODE,BIZ_CODE,BIZ_NAME,BIZ_TYPE,BIZ_TYPE_CODE,BIZ_ATTR,BIZ_DESC,BIZ_PROCESS_TAG,BIZ_STATE_CODE,BIZ_STATUS,"
                + " SERV_CODE,USAGE_DESC,INTRO_URL,PRODUCT_NO,ACCESS_MODE,ORDER_MODE,PROV_ADDR,PROV_PORT,COUNT_SIDE1,COUNT_SIDE2," + " BILL_TYPE,PRICE,COUNT_PCT,NUM_TIME,NUM_DAY,SERVICE_PHONE,CONTACT,to_char(FIRST_DATE,'yyyy-mm-dd') FIRST_DATE ,"
                + " FOREGIFT_TYPE,FOREGIFT,OPR_SOURCE, SMS_PROCESS_TAG,RECOGNIZE_CODE,NET_TAG,FILE_NAME,to_char(START_DATE,'yyyy-mm-dd') START_DATE,"
                + " to_char(END_DATE,'yyyy-mm-dd') END_DATE,to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,"
                + " UPDATE_DEPART_ID,REMARK,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8, RSRV_STR9,RSRV_STR10 " + " FROM TD_M_SP_BIZ WHERE 1=1 and SP_CODE = :SP_CODE and BIZ_CODE=:BIZ_CODE ";

        return Dao.qryBySql(new StringBuilder(spBizInfoSql), param, Route.CONN_CRM_CEN);
    }

    public static IDataset querySpBySpCode1(String spCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SP_CODE", spCode);
        return Dao.qryByCode("TD_M_SP_INFO", "SEL_BY_SPCODE1", param, Route.CONN_CRM_CEN);
    }

    public static IDataset querySpByTypeCode(String bizTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("BIZ_TYPE_CODE", bizTypeCode);
        return Dao.qryByCode("TD_M_SP_INFO", "SEL_PLAT_SP_TYPECODE", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询SP企业信息信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset querySpInfos(IData inParam) throws Exception
    {

        IData param = new DataMap();
        param.put("BIZ_TYPE_CODE", inParam.getString("BIZ_TYPE_CODE", "").trim());
        param.put("BIZ_CODE", inParam.getString("BIZ_CODE", "").trim());
        param.put("BIZ_NAME", inParam.getString("BIZ_NAME", "").trim());
        param.put("SP_CODE", inParam.getString("SP_CODE", "").trim());

        return Dao.qryByCode("TD_M_SP_INFO", "SEL_BY_BIZ_TYPECODE", param);
    }

    /**
     * 根据业务类型查询SP企业信息信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset querySpInfosByBizTypeCode(String biz_type_code) throws Exception
    {

        StringBuilder sql = new StringBuilder();
        sql.append("select a.sp_code, '(' || a.sp_code  || ')' || a.sp_name sp_name " + "From td_m_sp_info a " + "where exists (select 1 from td_b_platsvc b where a.sp_code=b.sp_code and b.biz_type_code= '" + biz_type_code);
        sql.append("') and a.sp_status <> 'N' " + "order by a.sp_code");
        return Dao.qryBySql(sql, new DataMap());
        // return new DatasetList();
    }

    /**
     * 100%承载局数据查询
     * 
     * @param spName
     * @param bizName
     * @param bizTypeCode
     * @param maxRetrue
     * @return
     * @throws Exception
     */
    public static IDataset queryTerrace(String spName, String bizName, String bizTypeCode, String maxRetrue) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SP_NAME", spName);
        cond.put("BIZ_NAME", bizName);
        cond.put("BIZ_TYPE_CODE", bizTypeCode);
        cond.put("MAX_RETRUE", maxRetrue);
        //return Dao.qryByCode("TD_M_SP_INFO", "SEL_BY_QueryTerrace", cond);
        IDataset upcDatas = UpcCall.queryTerrace(spName, bizName, bizTypeCode, maxRetrue);
        if(ArrayUtil.isNotEmpty(upcDatas))
        {
        	for(int i = 0 ; i < upcDatas.size() ; i++)
        	{
        		IData upcData = upcDatas.getData(i);
        		upcData.remove("SERVICE_ID");
        		upcData.remove("SP_CODE");
        	}
        }
        return upcDatas;
    }

}
