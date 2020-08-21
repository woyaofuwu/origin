
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RubbishSmsWhiteQry
{
    public static IDataset queryWhiteByNum(String SERIAL_NUMBER) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", SERIAL_NUMBER);
        IDataset set = Dao.qryByCode("TF_F_RUBBISHSMS_WHITE", "SEL_BY_NUM", param, Route.CONN_CRM_CEN);
        return set;
    }

    public static IDataset queryWhiteByPK(String SERIAL_NUMBER_S, String SERIAL_NUMBER_E, String VALID_FLAG, String START_DATE, String END_DATE, String PROVINCE_CODE, String USER_TYPE, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_S", SERIAL_NUMBER_S);
        param.put("SERIAL_NUMBER_E", SERIAL_NUMBER_E);
        param.put("VALID_FLAG", VALID_FLAG);
        param.put("START_DATE", START_DATE);
        param.put("END_DATE", END_DATE);
        param.put("PROVINCE_CODE", PROVINCE_CODE);
        param.put("USER_TYPE", USER_TYPE);
        IDataset set = Dao.qryByCodeParser("TF_F_RUBBISHSMS_WHITE", "SEL_ALL_INFO", param, pagination, Route.CONN_CRM_CEN);
        return set;
    }

}
