
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UTradeTypePFInfoQry
{
    public static IDataset qryTradePfByDataName(String dataName) throws Exception
    {
        IData param = new DataMap();
        param.put("DATA_NAME", dataName);
        return Dao.qryByCode("TD_S_TRADETYPE_PF_DATA", "SEL_BY_DATA_NAME", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryTradePfByObjInfo(String objName, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("OBJ_NAME", objName);
        param.put("TRADE_TYPE_CODE", tradeTypeCode); // TRADE_TYPE_CODE放到查询结果中，不是做查询条件
        return Dao.qryByCode("TD_S_TRADETYPE_PF_OBJ", "SEL_BY_OBJ_NAME", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryTradePfByTradeTypeCode(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        return Dao.qryByCode("TD_S_TRADETYPE_PF", "SEL_BY_TRADE_TYPE_CODE", param, Route.CONN_CRM_CEN);
    }

}
