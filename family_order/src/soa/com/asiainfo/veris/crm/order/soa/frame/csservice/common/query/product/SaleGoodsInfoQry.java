
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SaleGoodsInfoQry extends CSBizBean
{

    public static IDataset queryByPkgId(String packageId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PACKAGE_ID", packageId);
        return Dao.qryByCode("TD_B_SALE_GOODS", "SEL_BY_PACKID", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset queryByPkgIdAndGoodsId(String packageId, String goodsId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PACKAGE_ID", packageId);
        cond.put("GOODS_ID", goodsId);
        return Dao.qryByCode("TD_B_SALE_GOODS", "SEL_BY_PACKIDNEW", cond);
    }

    public static IDataset queryByPkgIdAndResId(String packageId, String resId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PACKAGE_ID", packageId);
        cond.put("RES_ID", resId);
        return Dao.qryByCode("TD_B_SALE_GOODS", "SEL_BY_PACKRESID", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset queryByPkgIdEparchy(String packageId, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PACKAGE_ID", packageId);
        cond.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_SALE_GOODS", "SEL_BY_PACKID_EPARCHY", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset querySaleGoodsByGoodsId(String elementId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("GOODS_ID", elementId);

        IDataset datas = Dao.qryByCode("TD_B_SALE_GOODS", "SEL_BY_GOODS_ID", cond, Route.CONN_CRM_CEN);
        return datas;
    }
}
