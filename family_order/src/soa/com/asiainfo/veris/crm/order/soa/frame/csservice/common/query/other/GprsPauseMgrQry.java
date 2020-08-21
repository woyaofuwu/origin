
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GprsPauseMgrQry
{

    public static IDataset getGprsUserSvcState(String userid) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userid);
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "SEL_BY_WAPNETSVC", param);
    }

    public static IDataset queryuserWlan(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT a.USER_ID,a.SERVICE_ID,a.FIRST_DATE,a.START_DATE,a.END_DATE,a.BIZ_STATE_CODE ");
        parser.addSQL("FROM TF_F_USER_PLATSVC a,TF_F_USER b ");
        parser.addSQL("WHERE a.USER_ID=b.USER_ID AND a.PARTITION_ID=b.PARTITION_ID ");
        parser.addSQL("AND b.REMOVE_TAG='0' AND b.SERIAL_NUMBER =:SERIAL_NUMBER ");
        parser.addSQL("AND sysdate BETWEEN a.START_DATE AND a.END_DATE ");
        parser.addSQL("AND　a.BIZ_STATE_CODE <> 'E' AND a.SERVICE_ID IN (98000201,17310004)");
        return Dao.qryByParse(parser);
    }
}
