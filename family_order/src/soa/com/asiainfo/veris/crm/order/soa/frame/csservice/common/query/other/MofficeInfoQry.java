
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MofficeInfoQry extends CSBizBean
{

    public static IDataset getMsisdnCityInfo(String msisdn) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", msisdn);
        return Dao.qryByCodeParser("TD_M_MOFFICE", "SEL_BY_NUM", param, Route.CONN_RES);
    }

    public static IDataset getPhoneMofficeByImsi(String imsi) throws Exception
    {
        IData params = new DataMap();
        params.put("IMSI", imsi);

        return Dao.qryByCodeParser("TD_M_MOFFICE", "SEL_BY_IMSI", params, Route.CONN_RES);
    }

    public static IDataset getPhoneMofficeBySn(String serialNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCodeParser("TD_M_MOFFICE", "SEL_MOFFICE_BY_SN", params, Route.CONN_RES);
    }

}
