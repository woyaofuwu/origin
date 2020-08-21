
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserTdExpactQry
{
    /**
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset qryUserTdExpactInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT SERIAL_NUMBER,ACCEPT_MONTH,CITY_CODE,USER_TYPE_CODE,PRIZE_TYPE_CODE, ");
        parser.addSQL("GET_START_DATE,ACTIVE_FLAG,EFFECTIVE_DATE,INEFFECTIVE_DATE, ");
        parser.addSQL("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,RSRV_STR1,RSRV_STR2,RSRV_STR3 ");
        parser.addSQL("FROM TF_O_USER_TDEXPACT ");
        parser.addSQL("WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL("AND ACCEPT_MONTH=TO_CHAR(SYSDATE,'MM') ");
        parser.addSQL("AND ACTIVE_FLAG IS NULL ");
        parser.addSQL("AND PRIZE_TYPE_CODE IS NOT NULL ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
}
