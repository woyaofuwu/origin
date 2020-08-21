
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

/**
 * 平台业务局数据管理
 * 
 * @author xiekl
 */
public class PlatOfficeDataQry
{

    /**
     * SP业务校验
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IData bizCheck(IData param) throws Exception
    {
        param.put("X_RESULTCODE", "0");
        param.put("X_RESULTINFO", "Trade OK");
        String[] inParam =
        { "v_serv_type", "v_sp_code", "v_operator_code", "v_operator_name", "v_other_bal_obj1", "v_other_bal_obj2", "v_bill_flag", "v_fee", "v_valid_date", "v_expire_date", "v_bal_prop", "v_count", "v_serv_attr", "v_is_third_validate",
                "v_re_confirm", "v_serv_property", "v_biz_status", "v_oper_type", "v_resultcode", "v_resultinfo" };

        IData data = new DataMap();
        data.put("v_serv_type", param.getString("SERV_TYPE"));
        data.put("v_sp_code", param.getString("SP_CODE"));
        data.put("v_operator_code", param.getString("BIZ_CODE"));
        data.put("v_operator_name", param.getString("OPERATOR_NAME"));
        data.put("v_other_bal_obj1", param.getString("OTHER_BAL_OBJ1"));
        data.put("v_other_bal_obj2", param.getString("OTHER_BAL_OBJ2"));
        data.put("v_bill_flag", param.getString("BILL_FLAG"));
        data.put("v_fee", param.getString("FEE"));
        data.put("v_valid_date", param.getString("VALID_DATE"));
        data.put("v_expire_date", param.getString("EXPIRE_DATE"));
        data.put("v_bal_prop", param.getString("BAL_PROP"));
        data.put("v_count", param.getString("COUNT"));
        data.put("v_serv_attr", param.getString("SERV_ATTR"));
        data.put("v_is_third_validate", param.getString("IS_THIRD_VALIDATE"));
        data.put("v_re_confirm", param.getString("RE_CONFIRM"));
        data.put("v_serv_property", param.getString("SERV_PROPERTY"));
        data.put("v_biz_status", param.getString("BIZ_STATUS"));
        data.put("v_oper_type", param.getString("OPER_TYPE", ""));

        IData spParam = new DataMap();
        spParam.put("BIZ_TYPE_CODE", param.getString("BIZ_TYPE", ""));
        StringBuilder sql = new StringBuilder("SELECT T.ORG_DOMAIN FROM td_b_platsvc_param T where T.BIZ_TYPE_CODE=:BIZ_TYPE_CODE");
        IDataset platParam = Dao.qryBySql(sql, spParam, Route.CONN_CRM_CEN);
        String orgDomain = "";
        if (platParam.size() > 0)
        {
            orgDomain = platParam.getData(0).getString("ORG_DOMAIN");
        }

        if (orgDomain != null && orgDomain.equals("DSMP"))
        {
            Dao.callProc("PKG_CMS_BUREDATA.p_dsmp_biz_check", inParam, data, Route.CONN_CRM_CEN);
            data.put("X_RESULTCODE", data.get("v_resultcode"));
            data.put("X_RESULTINFO", data.get("v_resultinfo"));
        }
        // else
        // {
        // Dao.callProc("PKG_CMS_BUREDATA.p_biz_check", inParam, data, Route.CONN_CRM_CEN);
        // }

        param.putAll(data);
        return param;
    }

    /**
     * 处理业务信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IData bizDeal(IData param) throws Exception
    {

        String[] inParam =
        { "v_serv_type", "v_sp_code", "v_operator_code", "v_operator_name", "v_other_bal_obj1", "v_other_bal_obj2", "v_bill_flag", "v_fee", "v_valid_date", "v_expire_date", "v_bal_prop", "v_count", "v_serv_attr", "v_is_third_validate",
                "v_re_confirm", "v_serv_property", "v_biz_status", "v_oper_type", "v_file_type", "v_resultcode", "v_resultinfo" };

        IData data = new DataMap();

        data.put("v_serv_type", param.getString("SERV_TYPE", ""));
        data.put("v_sp_code", param.getString("SP_CODE", ""));
        data.put("v_operator_code", param.getString("OPERATOR_CODE", ""));
        data.put("v_operator_name", param.getString("OPERATOR_NAME", ""));
        data.put("v_other_bal_obj1", param.getString("OTHER_BAL_OBJ1", ""));
        data.put("v_other_bal_obj2", param.getString("OTHER_BAL_OBJ2", ""));
        data.put("v_bill_flag", param.getString("BILL_FLAG", ""));
        data.put("v_fee", param.getString("FEE", ""));
        data.put("v_valid_date", param.getString("VALID_DATE", ""));
        data.put("v_expire_date", param.getString("EXPIRE_DATE", ""));
        data.put("v_bal_prop", param.getString("BAL_PROP", ""));
        data.put("v_count", param.getString("COUNT", ""));
        data.put("v_serv_attr", param.getString("SERV_ATTR", ""));
        data.put("v_is_third_validate", param.getString("IS_THIRD_VALIDATE", ""));
        data.put("v_re_confirm", param.getString("RE_CONFIRM", ""));
        data.put("v_serv_property", param.getString("SERV_PROPERTY", ""));
        data.put("v_biz_status", param.getString("BIZ_STATUS", ""));
        data.put("v_oper_type", param.getString("OPER_TYPE", ""));

        Dao.callProc("PKG_CMS_BUREDATA.p_single_biz_deal", inParam, data, Route.CONN_CRM_CEN);
        data.put("X_RESULTCODE", data.get("v_resultcode"));
        data.put("X_RESULTINFO", data.get("v_resultinfo"));

        param.putAll(data);
        return param;
    }

    /**
     * 查询SP业务批量信息
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryBizBat(IData param, Pagination pagination) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("IMPORT_ID", param.getString("IMPORT_ID"));
        qryParam.put("IMPORT_STAFF_ID", param.getString("IMPORT_STAFF_ID"));
        qryParam.put("IMPORT_DATE_START", param.getString("IMPORT_DATE_START"));
        qryParam.put("IMPORT_DATE_END", param.getString("IMPORT_DATE_END"));
        qryParam.put("DEAL_FLAG", param.getString("DEAL_FLAG"));
        qryParam.put("DATA_TYPE", "BIZ_INFO");
        return UpcCall.qryBureDataBatImport(qryParam);
    }

    public static IDataset queryBizBatDtl(IData param, Pagination pagination) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("IMPORT_ID", param.getString("IMPORT_ID"));
        qryParam.put("DEAL_FLAG", param.getString("DEAL_FLAG"));
        qryParam.put("IMPORT_STAFF_ID", param.getString("IMPORT_STAFF_ID"));
        qryParam.put("IMPORT_DATE_START", param.getString("IMPORT_DATE_START"));
        qryParam.put("IMPORT_DATE_END", param.getString("IMPORT_DATE_END"));
        qryParam.put("SP_CODE", param.getString("SP_CODE"));
        qryParam.put("BIZ_CODE", param.getString("BIZ_CODE"));
        qryParam.put("BIZ_NAME", param.getString("BIZ_NAME"));

        return Dao.qryByCodeParser("TD_B_BUREDATA_IMPORT_BIZDTL", "SEL_BY_IMPORT", qryParam, pagination);
    }

    /**
     * 查询SP业务信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySPBiz(IData param, Pagination pagination) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("SP_CODE", param.getString("SP_CODE"));
        qryParam.put("BIZ_CODE", param.getString("BIZ_CODE"));
        qryParam.put("BIZ_TYPE_CODE", param.getString("BIZ_TYPE_CODE"));
        qryParam.put("BIZ_NAME", param.getString("BIZ_NAME"));
        qryParam.put("BIZ_ATTR", param.getString("BIZ_ATTR"));
        qryParam.put("BIZ_STATE_CODE", param.getString("BIZ_STATE_CODE"));
        return Dao.qryByCodeParser("TD_M_SP_BIZ", "SEL_BY_SPBIZ", qryParam, pagination);
    }

    /**
     * 查询SP信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySPInfo(IData param, Pagination pagination) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("SP_CODE", param.getString("SP_CODE"));
        qryParam.put("PLAT_CODE", param.getString("PLAT_CODE"));
        qryParam.put("SP_NAME", param.getString("SP_NAME"));
        qryParam.put("SP_TYPE", param.getString("SP_TYPE"));
        qryParam.put("SP_ATTR", param.getString("SP_ATTR"));
        qryParam.put("SP_STATUS", param.getString("SP_STATUS"));
        qryParam.put("UPDATE_STAFF_ID", param.getString("UPDATE_STAFF_ID"));
        qryParam.put("START_DATE", param.getString("START_DATE"));
        qryParam.put("END_DATE", param.getString("END_DATE"));

        return Dao.qryByCodeParser("TD_M_SP_INFO", "SEL_BY_SP_INFO", qryParam, pagination);
    }

    /**
     * 查询SP批量信息
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset querySPInfoBat(IData param, Pagination pagination) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("IMPORT_ID", param.getString("IMPORT_ID"));
        qryParam.put("IMPORT_STAFF_ID", param.getString("IMPORT_STAFF_ID"));
        qryParam.put("IMPORT_DATE_START", param.getString("IMPORT_DATE_START"));
        qryParam.put("IMPORT_DATE_END", param.getString("IMPORT_DATE_END"));
        qryParam.put("DEAL_FLAG", param.getString("DEAL_FLAG"));
        qryParam.put("DATA_TYPE", "SP_INFO");
        return UpcCall.qryBureDataBatImport(qryParam);

    }

    public static IDataset querySPInfoBatDtl(IData param, Pagination pagination) throws Exception
    {
        IData qryParam = new DataMap();
        qryParam.put("IMPORT_ID", param.getString("IMPORT_ID"));
        qryParam.put("DEAL_FLAG", param.getString("DEAL_FLAG"));
        qryParam.put("IMPORT_STAFF_ID", param.getString("IMPORT_STAFF_ID"));
        qryParam.put("IMPORT_DATE_START", param.getString("IMPORT_DATE_START"));
        qryParam.put("IMPORT_DATE_END", param.getString("IMPORT_DATE_END"));
        qryParam.put("SP_STATUS", param.getString("SP_STATUS"));
        qryParam.put("SP_CODE", param.getString("SP_CODE"));
        qryParam.put("SP_TYPE", param.getString("SP_TYPE"));
        qryParam.put("SP_ATTR", param.getString("SP_ATTR"));

        return Dao.qryByCodeParser("TD_B_BUREDATA_IMPORT_SPDTL", "SEL_BY_IMPORT", qryParam, pagination);
    }

    /**
     * 校验SP信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IData spInfoCheck(IData param) throws Exception
    {
        param.put("X_RESULTCODE", "0");
        param.put("X_RESULTINFO", "Trade OK");
        String[] inParam =
        { "v_serv_type", "v_sp_code", "v_sp_name", "v_sp_type", "v_serv_code", "v_prov_code", "v_bal_prov", "v_dev_code", "v_valid_date", "v_expire_date", "v_sp_desc", "v_sp_attr", "v_oper_type", "v_province_id", "v_resultcode", "v_resultinfo" };

        IData data = new DataMap();
        data.put("v_serv_type", param.getString("SERV_TYPE", ""));
        data.put("v_sp_code", param.getString("SP_CODE", ""));
        data.put("v_sp_name", param.getString("SP_NAME", ""));
        data.put("v_sp_type", param.getString("SP_TYPE", ""));
        data.put("v_serv_code", param.getString("SERV_CODE", ""));
        data.put("v_prov_code", param.getString("PROV_CODE", ""));
        data.put("v_bal_prov", param.getString("BAL_PROV", ""));
        data.put("v_dev_code", param.getString("DEV_CODE", ""));
        data.put("v_valid_date", param.getString("VALID_DATE", ""));
        data.put("v_expire_date", param.getString("EXPIRE_DATE", ""));
        data.put("v_description", param.getString("DESCRIPTION", ""));
        data.put("v_sp_attr", param.getString("SP_ATTR", ""));
        data.put("v_oper_type", param.getString("OPER_TYPE", ""));
        data.put("v_province_id", "898");

        //IData spParam = new DataMap();
        //spParam.put("BIZ_TYPE_CODE", param.getString("BIZ_TYPE_CODE", ""));
        //StringBuilder sql = new StringBuilder("SELECT T.ORG_DOMAIN FROM td_b_platsvc_param T where T.BIZ_TYPE_CODE=:BIZ_TYPE_CODE");
        //IDataset platParam = Dao.qryBySql(sql, spParam, Route.CONN_CRM_CEN);
        IDataset platParam = UpcCall.querySpServiceParamByCond(null, null, param.getString("BIZ_TYPE_CODE", ""), null);
        String orgDomain = "";
        if (platParam.size() > 0)
        {
            orgDomain = platParam.getData(0).getString("ORG_DOMAIN");
        }
        if (orgDomain != null && orgDomain.equals("DSMP"))
        {
            Dao.callProc("PKG_CMS_BUREDATA.p_dsmp_spinfo_check", inParam, data, Route.CONN_CRM_CEN);
            data.put("X_RESULTCODE", data.get("v_resultcode"));
            data.put("X_RESULTINFO", data.get("v_resultinfo"));
        }
        // else
        // {
        // Dao.callProc("PKG_CMS_BUREDATA.p_spinfo_check", inParam, data, Route.CONN_CRM_CEN);
        // }

        param.putAll(data);
        return param;
    }

    /**
     * SP信息处理
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IData spInfoDeal(IData param) throws Exception
    {

        String[] inParam =
        { "v_serv_type", "v_sp_code", "v_sp_name", "v_sp_type", "v_serv_code", "v_prov_code", "v_bal_prov", "v_dev_code", "v_valid_date", "v_expire_date", "v_description", "v_sp_attr", "v_oper_type", "v_import_date", "v_import_sequence",
                "v_province_id", "v_resultcode", "v_resultinfo" };

        IData data = new DataMap();
        data.put("v_serv_type", param.get("SERV_TYPE"));
        data.put("v_sp_code", param.get("SP_CODE"));
        data.put("v_sp_name", param.get("SP_NAME"));
        data.put("v_sp_type", param.get("SP_TYPE"));
        data.put("v_serv_code", param.get("SERV_CODE"));
        data.put("v_prov_code", param.get("PROV_CODE"));
        data.put("v_bal_prov", param.get("BAL_PROV"));
        data.put("v_dev_code", param.get("DEV_CODE"));
        data.put("v_valid_date", param.get("VALID_DATE"));
        data.put("v_expire_date", param.get("EXPIRE_DATE"));
        data.put("v_description", param.get("DESCRIPTION"));
        data.put("v_sp_attr", param.get("SP_ATTR"));
        data.put("v_oper_type", param.getString("OPER_TYPE", ""));
        data.put("v_import_date", param.get("IMPORT_DATE"));
        data.put("v_import_sequence", param.get("IMPORT_SEQUENCE"));
        data.put("v_province_id", param.get("PROVINCE_ID"));

        Dao.callProc("PKG_CMS_BUREDATA.p_single_biz_deal", inParam, data, Route.CONN_CRM_CEN);

        data.put("X_RESULTCODE", data.get("v_resultcode"));
        data.put("X_RESULTINFO", data.get("v_resultinfo"));

        param.putAll(data);
        return param;
    }
}
