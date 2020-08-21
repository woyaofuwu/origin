
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RentCodeInfoQry
{
    public static IDataset queryRentMobile(String rentTypeCode, String paraCode, String cityCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("RENT_TYPE_CODE", rentTypeCode);
        cond.put("PARA_CODE", paraCode);
        cond.put("CITY_CODE", cityCode);
        return Dao.qryByCode("TI_O_RENTCODE", "SEL_GETRENTNO", cond);
    }

    public static IDataset queryRentMobileByRentSN(String rentTypeCode, String rentSerialNumber) throws Exception
    {
        IData cond = new DataMap();
        cond.put("RENT_TYPE_CODE", rentTypeCode);
        cond.put("RENT_SERIAL_NUMBER", rentSerialNumber);
        return Dao.qryByCode("TI_O_RENTCODE", "SEL_INFO_RENTNO", cond, Route.CONN_CRM_CEN);
    }
}
