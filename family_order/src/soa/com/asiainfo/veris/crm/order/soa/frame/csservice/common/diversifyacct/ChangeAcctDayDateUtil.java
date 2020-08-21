
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: ChangeAcctDayDateUtil.java
 * @Description: 获取用户账期变更时候时间API Modification History: Date Author Version Description
 *               ---------------------------------------------------------* 2013-9-12 maoke v1.0.0 修改原因
 */

public class ChangeAcctDayDateUtil
{
    /**
     * 获取用户新账期数据下END_CYCLE_ID【变更后的END_CYCLE_ID】
     * 
     * @param serialNumber
     * @param userId
     * @param chgAcctDay
     * @param chgType
     * @return
     * @throws Exception
     */
    public static String getEndCycleIdByNewAcctData(String serialNumber, String userId, String chgAcctDay, String chgType) throws Exception
    {
        String endCycleId = getEndDateByNewAcctData(serialNumber, userId, chgAcctDay, chgType);

        if (StringUtils.isNotBlank(endCycleId))
        {
            endCycleId = SysDateMgr.decodeTimestamp(endCycleId, SysDateMgr.PATTERN_TIME_YYYYMMDD);
        }
        return endCycleId;
    }

    /**
     * 获取用户新账期数据下END_DATE
     * 
     * @param userId
     *            USER_ID
     * @param chgAcctDay
     *            新账期日
     * @param chgType
     *            0：主动变更,1：被动变更
     * @return
     * @throws Exception
     */
    public static String getEndDateByNewAcctData(String serialNumber, String userId, String chgAcctDay, String chgType) throws Exception
    {
        return getNewAcctDatasByChgAcctDay(serialNumber, userId, chgAcctDay, chgType).getString("END_DATE");
    }

    /**
     * 获取用户新账期数据下FIRST_DATE
     * 
     * @param userId
     *            USER_ID
     * @param chgAcctDay
     *            新账期日
     * @param chgType
     *            0：主动变更,1：被动变更
     * @return
     * @throws Exception
     */
    public static String getFirstDateByNewAcctData(String serialNumber, String userId, String chgAcctDay, String chgType) throws Exception
    {
        return getNewAcctDatasByChgAcctDay(serialNumber, userId, chgAcctDay, chgType).getString("FIRST_DATE");
    }

    /**
     * 获取新的账期数据,并设置公用参数值
     * 
     * @param serialNumber
     * @param userId
     *            USER_ID
     * @param chgAcctDay
     *            新账期日
     * @param chgType
     *            0：主动变更,1：被动变更
     * @return
     * @throws Exception
     */
    public static IData getNewAcctDatasByChgAcctDay(String serialNumber, String userId, String chgAcctDay, String chgType) throws Exception
    {
        IData newAcctDay = new DataMap();
        newAcctDay.clear();

        String acctDay = "";
        String firstDate = "";
        String startDate = "";
        String endDate = "";
        String nextAcctDay = "";
        String nextFirstDate = "";
        String nextStartDate = "";

        IData param = new DataMap();
        param.put("CHG_TYPE", chgType);// 0：主动变更,1：被动变更
        param.put("ACCT_DAY", chgAcctDay);// 新账期日
        param.put("USER_ID", userId);// USER_ID
        param.put("SERIAL_NUMBER", serialNumber);

        IDataset returnNewAcctData = CSAppCall.call("SS.ChangeAcctDaySVC.getNewAcctDayByModify", param);

        if (!returnNewAcctData.isEmpty())
        {
            for (int i = 0; i < returnNewAcctData.size(); i++)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(returnNewAcctData.getData(i).getString("MODIFY_TAG")))
                {
                    nextAcctDay = returnNewAcctData.getData(i).getString("ACCT_DAY");
                    nextStartDate = returnNewAcctData.getData(i).getString("START_DATE");
                    nextFirstDate = returnNewAcctData.getData(i).getString("FIRST_DATE");
                }
                if (BofConst.MODIFY_TAG_UPD.equals(returnNewAcctData.getData(i).getString("MODIFY_TAG")))
                {
                    acctDay = returnNewAcctData.getData(i).getString("ACCT_DAY");
                    startDate = returnNewAcctData.getData(i).getString("START_DATE");
                    endDate = returnNewAcctData.getData(i).getString("END_DATE");
                    firstDate = returnNewAcctData.getData(i).getString("FIRST_DATE");
                }

                newAcctDay.put("ACCT_DAY", acctDay);
                newAcctDay.put("START_DATE", startDate);
                newAcctDay.put("END_DATE", endDate);
                newAcctDay.put("FIRST_DATE", firstDate);
                newAcctDay.put("NEXT_ACCT_DAY", nextAcctDay);
                newAcctDay.put("NEXT_START_DATE", nextStartDate);
                newAcctDay.put("NEXT_FIRST_DATE", nextFirstDate);
            }
        }

        return newAcctDay;
    }

    /**
     * 获取用户新账期数据下NEXT_FIRST_DATE【变更后的FIRST_DATE】
     * 
     * @param userId
     *            USER_ID
     * @param chgAcctDay
     *            新账期日
     * @param chgType
     *            0：主动变更,1：被动变更
     * @return
     * @throws Exception
     */
    public static String getNextFirstDateByNewAcctData(String serialNumber, String userId, String chgAcctDay, String chgType) throws Exception
    {
        return getNewAcctDatasByChgAcctDay(serialNumber, userId, chgAcctDay, chgType).getString("NEXT_FIRST_DATE");
    }

    /**
     * 获取用户新账期数据下START_CYCLE_ID【变更后的START_CYCLE_ID】
     * 
     * @param serialNumber
     * @param userId
     * @param chgAcctDay
     * @param chgType
     * @return
     * @throws Exception
     */
    public static String getNextStartCycleIdByNewAcctData(String serialNumber, String userId, String chgAcctDay, String chgType) throws Exception
    {
        String startCycleId = getNextStartDateByNewAcctData(serialNumber, userId, chgAcctDay, chgType);

        if (StringUtils.isNotBlank(startCycleId))
        {
            startCycleId = SysDateMgr.decodeTimestamp(startCycleId, SysDateMgr.PATTERN_TIME_YYYYMMDD);
        }
        return startCycleId;
    }

    /**
     * 获取用户新账期数据下NEXT_START_DATE【变更后的START_DATE】
     * 
     * @param userId
     *            USER_ID
     * @param chgAcctDay
     *            新账期日
     * @param chgType
     *            0：主动变更,1：被动变更
     * @return
     * @throws Exception
     */
    public static String getNextStartDateByNewAcctData(String serialNumber, String userId, String chgAcctDay, String chgType) throws Exception
    {
        return getNewAcctDatasByChgAcctDay(serialNumber, userId, chgAcctDay, chgType).getString("NEXT_START_DATE");
    }

    /**
     * 获取用户新账期数据下START_DATE
     * 
     * @param userId
     *            USER_ID
     * @param chgAcctDay
     *            新账期日
     * @param chgType
     *            0：主动变更,1：被动变更
     * @return
     * @throws Exception
     */
    public static String getStartDateByNewAcctData(String serialNumber, String userId, String chgAcctDay, String chgType) throws Exception
    {
        return getNewAcctDatasByChgAcctDay(serialNumber, userId, chgAcctDay, chgType).getString("START_DATE");
    }
}
