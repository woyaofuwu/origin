
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SaleCombineInfoQry extends CSBizBean
{
    public static IDataset queryByPkgIdEparchy(String packageId, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PACKAGE_ID", packageId);
        cond.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_SALE_COMBINE", "SEL_COMBINE_BY_PACKID", cond, Route.CONN_CRM_CEN);
    }
}
