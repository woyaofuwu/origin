
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class FreezeUserQry
{

    public static IDataset addCheckFreezeUser(IData data) throws Exception
    {
        SQLParser sql = new SQLParser(data);
        sql.addSQL("SELECT PARTITION_ID,USER_ID,SERIAL_NUMBER,NUMBER_TYPE,STATUS,START_DATE,END_DATE, ");
        sql.addSQL("UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK FROM TF_F_USER_DANDELION_FREEZE ");
        sql.addSQL("WHERE 1=1 ");
        sql.addSQL("AND USER_ID = :USER_ID ");
        sql.addSQL("AND NUMBER_TYPE = :NUMBER_TYPE ");
        sql.addSQL("AND END_DATE > SYSDATE");
        return Dao.qryByParse(sql);
    }

    public static IDataset addCheckFreezeUser2(IData data) throws Exception
    {
        SQLParser sql = new SQLParser(data);
        sql.addSQL("SELECT PARTITION_ID,USER_ID,SERIAL_NUMBER,NUMBER_TYPE,STATUS,START_DATE,END_DATE, ");
        sql.addSQL("UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK FROM TF_F_USER_DANDELION_FREEZE ");
        sql.addSQL("WHERE 1=1 ");
        sql.addSQL("AND USER_ID = :USER_ID ");
        sql.addSQL("AND NUMBER_TYPE = :NUMBER_TYPE ");
        sql.addSQL("AND START_DATE = TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')");
        return Dao.qryByParse(sql);
    }

    public static IDataset queryUserFreeze(IData params, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_DANDELION_FREEZE", "SEL_USER_FREEZE", params, pagination);
    }

}
