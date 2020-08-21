
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;

public class WidenetTradeQuery
{
    public static IDataset getProductParam(IData param) throws Exception
    {
//        IDataset wideProductSet = Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PRODUCTMODE", param, Route.CONN_CRM_CEN);
        IDataset wideProductSet = ProductInfoQry.getWidenetProductInfo(param.getString("PRODUCT_MODE"), param.getString("EPARCHY_CODE"));
        return wideProductSet;
    }

    public static IData getUserWidenetInfo(String serial_number) throws Exception
    {

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        IDataset reses = Dao.qryByCode("TF_F_USER_WIDENET", "SEL_BY_SERIAL_NUMBER_WIDENET", param);
        if (reses == null || reses.size() == 0)
            return null;
        else
            return reses.getData(0);
    }
}
