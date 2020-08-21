
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RecommparaQry
{

    public static IDataset getUserRecommPara(String recomm_type, String element_id, String recomm_source, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("RECOMM_TYPE", recomm_type);
        param.put("RECOMM_SOURCE", recomm_source);
        param.put("EPARCHY_CODE", eparchy_code);
        param.put("ELEMENT_ID", element_id);

        return Dao.qryByCodeParser("TD_B_RECOMMPARA", "SEL_RECOMM_CONTENT", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getUserRecommparaDiscnt(String discntCode, String trade_eparchy_code, String trade_staff_id) throws Exception
    {
        IData param = new DataMap();
       // param.put("PRODUCT_ID", product_id);
        param.put("DISCNT_CODE", discntCode);//对应element_id
        param.put("TRADE_EPARCHY_CODE", trade_eparchy_code);
        param.put("TRADE_STAFF_ID", trade_staff_id);
        return Dao.qryByCode("TD_B_RECOMMPARA", "SEL_RECOMMPARA_DISCNT_NEW", param, Route.CONN_CRM_CEN);//原sql SEL_RECOMMPARA_DISCNT
    }

    public static IDataset getUserRecommparaService(String serviceId, String trade_eparchy_code, String trade_staff_id) throws Exception
    {
        IData param = new DataMap();
      //  param.put("PRODUCT_ID", product_id);
        param.put("SERVICE_ID", serviceId);//对应element_id
        param.put("TRADE_EPARCHY_CODE", trade_eparchy_code);
        param.put("TRADE_STAFF_ID", trade_staff_id);

        return Dao.qryByCode("TD_B_RECOMMPARA", "SEL_RECOMMPARA_SERVICE_NEW", param, Route.CONN_CRM_CEN);//SEL_RECOMMPARA_SERVICE
    }
}
