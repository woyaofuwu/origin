
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class PoDefProductPhoneQry
{
    public static IDataset getDefProductPhoneInfos(String strSn) throws Exception
    {
        IData data = new DataMap();
        data.put("PHONECODE_S", strSn);
        return Dao.qryByCode("TD_B_DEFPRODUCT_PHONE", "SEL_BY_PHONE", data, Route.CONN_CRM_CEN);
    }
}
