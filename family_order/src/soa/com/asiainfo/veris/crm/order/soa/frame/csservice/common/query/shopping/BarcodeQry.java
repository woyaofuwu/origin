
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BarcodeQry extends CSBizBean
{
    public static IDataset getBarcodeConfigInfoById(String barcodeId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("BARCODE_ID", barcodeId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_BARCODE_CONFIG", "SEL_BY_ID", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getBarcodeInfoById(String barcodeId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("BARCODE_ID", barcodeId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_BARCODE", "SEL_BY_ID", param, Route.CONN_CRM_CEN);
    }
}
