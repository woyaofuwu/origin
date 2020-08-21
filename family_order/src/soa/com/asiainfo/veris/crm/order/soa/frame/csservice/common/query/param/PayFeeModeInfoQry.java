
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PayFeeModeInfoQry
{

    public static IDataset getByCode(String payMoneyCode) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("PAY_MONEY_CODE", payMoneyCode);

        IDataset dataset = Dao.qryByCode("TD_SD_PAYFEEMODE", "SEL_CODE2_BY_PAYMONEYCODE", inparams, Route.CONN_CRM_CEN);

        return dataset;
    }
}
