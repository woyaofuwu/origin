
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserImeiInfoQry
{
    public static IDataset getProductInfoByImei(String imei, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("IMEI", imei);

        return Dao.qryByCode("TF_F_USER_IMEI", "SEL_PRODUCT_BY_IMEI", param, routeId);
    }

    public static IDataset getUserImeiAllByUserId(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_IMEI", "SEL_IMEI_BY_USERID", param);
    }

    /**
     * @Function: getUserImeiInfoByUserId
     * @Description: 该函数的功能描述
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午9:49:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getUserImeiInfoByUserId(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_IMEI", "SEL_USERIMEI_BY_STARTDATE", param);
    }

    public static IDataset getUserImeiInfoByUserIdAndImei(String userId, String imei) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("IMEI", imei);

        return Dao.qryByCode("TF_F_USER_IMEI", "SEL_BY_USERID_IMEI", param);
    }

    public static IDataset qryUserImeiInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_IMEI", "SEL_IMEI_BY_USERID", param);
    }

    public static IDataset queryAllByUserId(IData inparam) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_IMEI", "SEL_ALL_BY_USERID", inparam);
    }

    /**
     * * 根据IMEI号查询有效和未生效的记录
     * 
     * @param imei
     * @return
     * @throws Exception
     */
    public static IDataset queryByImei(String imei) throws Exception
    {
        IData param = new DataMap();
        param.put("IMEI", imei);

        return Dao.qryByCode("TF_F_USER_IMEI", "SEL_USER_BY_IMEI", param);
    }

    /**
     * 改号修改用户imei信息
     * 
     * @param newSerialNumber
     * @param userId
     * @return
     * @throws Exception
     */
    public static int updateSNByUserId(String newSerialNumber, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", newSerialNumber);
        param.put("USER_ID", userId);
        return Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "UPD_SN_BY_ID", param);
    }
}
