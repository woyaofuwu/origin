package com.asiainfo.veris.crm.order.soa.group.stopgroupbroadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;



public class BatBroadbandMebDealBean  extends CSBizBean
{

    /**
     * 
     * @param relaList
     * @throws Exception
     */
    public static void createGrpBroadbandMeb(IDataset relaList) throws Exception 
    {
        if(IDataUtil.isNotEmpty(relaList))
        {
            Dao.insert("TF_F_GROUPKDMEM_UU", relaList);
        }
    }
    
    /**
     * 
     * @param relaList
     * @throws Exception
     */
    public static void deleteGrpBroadbandMeb(IDataset relaList) throws Exception 
    {
        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE FROM TF_F_GROUPKDMEM_UU T ");
        sql.append("   WHERE T.PARTITION_ID = MOD(:USER_ID_B, 10000) ");
        sql.append("   AND T.USER_ID_B = :USER_ID_B ");
        sql.append("   AND T.REMOVE_TAG = '0' ");
        Dao.executeBatch(sql, relaList);
    }
    
    /**
     * 
     * @param userIdb
     * @return
     * @throws Exception
     */
    public static IDataset queryGrpKdMeb(String userIdb) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID_B", userIdb);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT T.* FROM TF_F_GROUPKDMEM_UU T ");
        sql.append("   WHERE T.PARTITION_ID = MOD(:USER_ID_B, 10000) ");
        sql.append("   AND T.USER_ID_B = :USER_ID_B ");
        sql.append("   AND T.REMOVE_TAG = '0' ");
        return Dao.qryBySql(sql, param);
    }

}
