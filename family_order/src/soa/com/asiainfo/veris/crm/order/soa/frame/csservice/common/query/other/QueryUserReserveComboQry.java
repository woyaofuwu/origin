
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryUserReserveComboQry extends CSBizBean
{
    public static IDataset queryUserReserveDiscnt(String tradeId, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("EPARCHY_CODE", routeEparchyCode);
        indata.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_TRD_ID_RSRV_QRY", indata, pagination, routeEparchyCode);
    }

    public static IDataset queryUserReserveProduct(String tradeId, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("EPARCHY_CODE", routeEparchyCode);
        indata.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_BY_PK_RSRV_QRY", indata, pagination, routeEparchyCode);
    }

    public static IDataset queryUserReserveService(String tradeId, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("EPARCHY_CODE", routeEparchyCode);
        indata.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_SVC", "SEL_BY_TRADE_RSRV_QRY", indata, pagination, routeEparchyCode);
    }
}
