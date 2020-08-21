
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RubbishSmsInfoQry
{

    public static IDataset querySmsBySnAndProvince(String serialNumber, String provinceCode) throws Exception
    {
        IData idata = new DataMap();
        idata.put("SERIAL_NUMBER", serialNumber);
        idata.put("PROVINCE_CODE", provinceCode);
        IDataset ids = Dao.qryByCode("TF_F_RUBBISHSMS_WHITE", "SEL_BY_NUMANDPROVINCE", idata, Route.CONN_CRM_CEN);

        return ids;
    }
}
