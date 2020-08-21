
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.userconn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserConnQry extends CSBizBean
{

    public static void deleteUserConnection(String userId, String userIdB, String type) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID_A", userId);
        params.put("USER_ID_B", userIdB);
        params.put("TYPE", type);
        Dao.executeUpdateByCodeCode("TF_F_USER_CONNECTION", "DEL_BY_PK", params);
    }

    /**
     * 根据userid查询固话信息
     * 
     * @author dengyong3
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset getConnByUserIdAndType(String userId, String type) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID_B", userId);
        params.put("TYPE", type);
        return Dao.qryByCode("TF_F_USER_CONNECTION", "SEL_BY_USER_B", params);
    }

    /**
     * 根据userid查询固话信息
     * 
     * @author dengyong3
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public static IDataset getConnInfosByIdA(String userId, String type) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID_A", userId);
        params.put("TYPE", type);
        return Dao.qryByCode("TF_F_USER_CONNECTION", "SEL_BY_USER_A", params);
    }
}
