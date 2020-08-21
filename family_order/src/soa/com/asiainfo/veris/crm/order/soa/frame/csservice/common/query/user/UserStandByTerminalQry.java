
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserStandByTerminalQry
{

    public static int insertTerminalInfo(IData inparam) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_USER_STANDBY_TERMINAL", "INSERT_ALL", inparam);
    }

    public static IDataset queryTerminalAllBySerialnum(String serialNumber, Pagination pagination) throws Exception
    {
        IData idata = new DataMap();
        idata.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_USER_STANDBY_TERMINAL", "SEL_ALL_BY_SERNUMBER", idata, pagination);

    }

    /**
     * 查询返库信息
     * 
     * @return IDataset
     * @throws Exception
     */
    public static IDataset queryTerminalBack(IData inparam) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_STANDBY_TERMINAL", "SEL_ALL_BY_STANDBYIMEI", inparam);
    }

    public static IDataset queryTerminalBySerialnumEndTime(String serialNumber, Pagination pagination) throws Exception
    {
        IData idata = new DataMap();
        idata.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_USER_STANDBY_TERMINAL", "SEL_ALL_BY_SERNUMBER_ENDTIME", idata, pagination);
    }

    public static IDataset queryTerminalInfoByType(String serialNumber, String rsrvStr1, Pagination pagination) throws Exception
    {
        IData idata = new DataMap();
        idata.put("SERIAL_NUMBER", serialNumber);
        idata.put("RSRV_STR1", rsrvStr1);
        return Dao.qryByCode("TF_F_USER_STANDBY_TERMINAL", "SEL_BY_QUERYTYPE", idata, pagination);
    }

    /**
     * 更新终端串号
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static int updateImei(IData inparam) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "INS_ALL", inparam);
    }

    /**
     * 更新返库信息结束时间
     * 
     * @return IDataset
     * @throws Exception
     */
    public static int updateTerminalEndTime(IData inparam) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_USER_STANDBY_TERMINAL", "UPD_ENDTIME_BY_STANDBYIMEI", inparam);
    }

    // 结束原纪录
    public static int updUserImeiEndDate(IData inparam) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "UPD_USERIMEI_ENDDATE", inparam);
    }
}
