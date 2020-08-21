
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.specialsn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SpecialSerialNumberInfoQry
{

    // todo
    public static IDataset queryQuaUser(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select * ");
        parser.addSQL(" from UCR_UEC.TD_B_SPECIALNUMBER a");
        parser.addSQL(" where 1 = 1");
        parser.addSQL(" and serial_number = :SERIAL_NUMBER");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
