
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserPccInfoQry
{
    public static IDataset getPccUserInfByUserId(String user_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT USER_ID,SERIAL_NUMBER,SERIAL_NUMBER_A,IMSI, ");
        parser.addSQL(" BILL_TYPE, USER_GRADE,USER_STATUS,USER_BILLCYCLEDATE,INSERT_DATE, ");
        parser.addSQL(" OPER_CODE, REMARK,DEAL_STATE,DEAL_DATE,DEAL_RESULT, ");
        parser.addSQL(" RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5, ");
        parser.addSQL(" RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_DATE1,RSRV_DATE2,ROWID ROWKEY ");
        parser.addSQL(" FROM TF_F_USER_PCC WHERE USER_ID=:USER_ID ");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
    }

}
