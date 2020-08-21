
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bi;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BiResultInfoQry
{

    /**
     * 已审核的记录
     * 
     * @param eparchyCode
     * @param judgeBat
     * @param updateStaffId
     * @return
     * @throws Exception
     */
    public static IDataset getDealNum(String eparchyCode, String judgeBat, String updateStaffId) throws Exception
    {
        IData param = new DataMap();
        param.put("JUDGE_BAT", judgeBat);
        param.put("UPDATE_STAFF_ID", updateStaffId);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCodeParser("TI_BI_RESULT_INFO", "SEL_BY_DEAL_SMS", param, eparchyCode);

    }

    /**
     * 未审核的记录
     * 
     * @param eparchyCode
     * @param judgeBat
     * @param updateStaffId
     * @return
     * @throws Exception
     */
    public static IDataset getNoDealNum(String eparchyCode, String judgeBat, String updateStaffId) throws Exception
    {
        IData param = new DataMap();
        param.put("JUDGE_BAT", judgeBat);
        param.put("UPDATE_STAFF_ID", updateStaffId);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCodeParser("TI_BI_RESULT_INFO", "SEL_BY_NODEAL_SMS", param, eparchyCode);

    }

    /**
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryHighDangerByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TI_BI_HIGH_DANGER", "SEL_BY_USERID", param);
    }

    /**
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset queryInterfaceInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TI_BI_INTERFACE_INFO", "SEL_ALL_BY_USERID", param);
    }

    /**
     * @param serialNumber
     * @param startTime
     * @param endTime
     * @param eparchyCode
     * @param dealTag
     * @param rownum
     * @return
     * @throws Exception
     */
    public static IDataset queryResultInfo(String serialNumber, String startTime, String endTime, String eparchyCode, String dealTag, String rownum) throws Exception
    {
        IData param = new DataMap();
        param.put("DEAL_TAG", dealTag);
        param.put("ROWNUM", rownum);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("START_TIME", startTime);
        param.put("END_TIME", endTime);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TI_BI_RESULT_INFO", "SEL_BY_ID", param, eparchyCode);

    }

    public static void updataBySendTime(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TI_BI_RESULT_INFO", "UPD_BY_SENDTIME", param, param.getString("EPARCHY_CODE"));
    }
}
