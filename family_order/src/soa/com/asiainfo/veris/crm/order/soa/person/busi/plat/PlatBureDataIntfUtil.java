
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

public class PlatBureDataIntfUtil
{
    public IData bizCheck(IData param) throws Exception
    {

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

        String bizTypeCode = param.getString("BIZ_TYPE_CODE", "");
        IDataset platParam = ParamInfoQry.getPlatsvcParam(bizTypeCode);

        String orgDomain = "";
        if (platParam.size() > 0)
        {
            orgDomain = platParam.getData(0).getString("ORG_DOMAIN");
        }
        if (orgDomain != null && orgDomain.equals("DSMP"))
        {
            // DaoHelper.callProc(pd.getDBConn(BaseFactory.CENTER_CONNECTION_NAME),"PKG_CMS_BUREDATA.p_dsmp_biz_check",
            // inParam, data);
        }
        else
        {
            // DaoHelper.callProc(pd.getDBConn(BaseFactory.CENTER_CONNECTION_NAME),"PKG_CMS_BUREDATA.p_biz_check",
            // inParam, data);
        }

        data.put("X_RESULTCODE", data.get("v_resultcode"));
        data.put("X_RESULTINFO", data.get("v_resultinfo"));

        param.putAll(data);
        return param;
    }

    public IData checkspInfoCheck(IData param) throws Exception
    {

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
        data.put("v_province_id", CSBizBean.getVisit().getProvinceCode());

        String bizTypeCode = param.getString("BIZ_TYPE_CODE", "");
        IDataset platParam = ParamInfoQry.getPlatsvcParam(bizTypeCode);
        String orgDomain = "";
        if (platParam.size() > 0)
        {
            orgDomain = platParam.getData(0).getString("ORG_DOMAIN");
        }
        if (orgDomain != null && orgDomain.equals("DSMP"))
        {
            // DaoHelper.callProc(pd.getDBConn(BaseFactory.CENTER_CONNECTION_NAME),"PKG_CMS_BUREDATA.p_dsmp_spinfo_check",
            // inParam, data);
        }
        else
        {
            // DaoHelper.callProc(pd.getDBConn(BaseFactory.CENTER_CONNECTION_NAME),"PKG_CMS_BUREDATA.p_spinfo_check",
            // inParam, data);
        }// if ( log.isDebugEnabled()){
        // }
        data.put("X_RESULTCODE", data.get("v_resultcode"));
        data.put("X_RESULTINFO", data.get("v_resultinfo"));

        param.putAll(data);
        return param;
    }

}
