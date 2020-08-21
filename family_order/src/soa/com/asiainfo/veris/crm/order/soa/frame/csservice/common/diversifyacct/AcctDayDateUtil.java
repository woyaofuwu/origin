
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: AcctDayDateUtil.java
 * @Description: 获取用户已有账期数据相应的时间,包含获取付费关系时间数据API Modification History: Date Author Version Description
 *               ---------------------------------------------------------* 2013-9-12 maoke v1.0.0 修改原因
 */

public class AcctDayDateUtil
{
    /**
     * 【本账期第一天,付费关系用】
     * 
     * @param userId
     * @return YYYYMMDD
     * @throws Exception
     */
    public static String getCycleIdFristDayThisAcct(String userId) throws Exception
    {
        return getFirstDayThisAcct(userId).split(" ")[0].replaceAll("-", "");
    }

    /**
     * 【上账期最后一天,付费关系用】
     * 
     * @param userId
     * @return YYYYMMDD
     * @throws Exception
     */
    public static String getCycleIdLastDayLastAcct(String userId) throws Exception
    {
        return SysDateMgr.addDays(getFirstDayThisAcct(userId), -1).split(" ")[0].replaceAll("-", "");
    }

    /**
     * 【本账期最后一天,付费关系用】
     * 
     * @param userId
     * @return YYYYMMDD
     * @throws Exception
     */
    public static String getCycleIdLastDayThisAcct(String userId) throws Exception
    {
        return getLastDayThisAcct(userId).split(" ")[0].replaceAll("-", "");
    }

    /**
     * 获取用户已有账期数据的【下账期第一天】
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static String getFirstDayNextAcct(String userId) throws Exception
    {
        return getUserAcctDayAndFirstDateInfo(userId).getString("FIRST_DAY_NEXTACCT");
    }

    /**
     * 获取用户已有账期数据的【本账期第一天】
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static String getFirstDayThisAcct(String userId) throws Exception
    {
        return getUserAcctDayAndFirstDateInfo(userId).getString("FIRST_DAY_THISACCT");
    }

    /**
     * 获取用户已有账期数据的【本账期最后一天】
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static String getLastDayThisAcct(String userId) throws Exception
    {
        return getUserAcctDayAndFirstDateInfo(userId).getString("LAST_DAY_THISACCT");
    }

    /**
     * 获取用户当前账期等相关数据
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData getUserAcctDayAndFirstDateInfo(String userId) throws Exception
    {

        return getUserAcctDayAndFirstDateInfo(userId, null);
    }

    /**
     * 获取用户当前账期等相关数据
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IData getUserAcctDayAndFirstDateInfo(String userId, String eparchy_code) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        String nowDate = SysDateMgr.getSysTime();

        IDataset userAcctDayList = UcaInfoQry.qryUserAcctDaysByUserId(userId, eparchy_code);

        IData userAcctDay = new DataMap();
        if (IDataUtil.isEmpty(userAcctDayList))
        {
            IData userInfoData = UcaInfoQry.qryUserInfoByUserId(userId, eparchy_code);
            // 用户存在, 用户结账日信息不存在
            if (IDataUtil.isNotEmpty(userInfoData))
            {
                return null;
            }
            // 用户不存在，生成用户账期数据
            param.put("ACCT_DAY", "1");
            userAcctDayList = DiversifyAcctUtil.getNewAcctDayByOpenUser(param);
        }

        // 用户结账日信息不存在
        if (IDataUtil.isEmpty(userAcctDayList))
        {
            return null;
        }

        String acctDay = "";
        String firstDate = "";
        String startDate = "";
        String endDate = "";
        String nextAcctDay = "";
        String nextFirstDate = "";
        String nextStartDate = "";
        String nextEndDate = "";

        for (int i = 0; i < userAcctDayList.size(); i++)
        {
            IData userAcctDayData = userAcctDayList.getData(i);

            if (userAcctDayData.getString("START_DATE", "").compareTo(nowDate) > 0)
            {
                nextAcctDay = userAcctDayData.getString("ACCT_DAY", "");
                nextFirstDate = userAcctDayData.getString("FIRST_DATE", "").split(" ")[0];
                nextStartDate = userAcctDayData.getString("START_DATE", "").split(" ")[0];
                nextEndDate = userAcctDayData.getString("END_DATE", "").split(" ")[0];

            }
            else
            {
                acctDay = userAcctDayData.getString("ACCT_DAY");
                firstDate = userAcctDayData.getString("FIRST_DATE").split(" ")[0];
                startDate = userAcctDayData.getString("START_DATE").split(" ")[0];
                endDate = userAcctDayData.getString("END_DATE").split(" ")[0];
            }
        }

        userAcctDay.put("SYSDATE", nowDate);
        userAcctDay.put("ACCT_DAY", acctDay);
        userAcctDay.put("FIRST_DATE", firstDate);
        userAcctDay.put("START_DATE", startDate);
        userAcctDay.put("END_DATE", endDate);

        if (StringUtils.isNotBlank(nextAcctDay))
        {
            userAcctDay.put("NEXT_ACCT_DAY", nextAcctDay);
            userAcctDay.put("NEXT_FIRST_DATE", nextFirstDate);
            userAcctDay.put("NEXT_START_DATE", nextStartDate);
            userAcctDay.put("NEXT_END_DATE", nextEndDate);
        }

        AcctTimeEnv env = new AcctTimeEnv(acctDay, firstDate, nextAcctDay, nextFirstDate, startDate, nextStartDate);
        AcctTimeEnvManager.setAcctTimeEnv(env);
        String firstDayThisAcct = SysDateMgr.getFirstDayOfThisMonth();// 本账期第一天
        String lastDayThisAcct = SysDateMgr.getLastDateThisMonth();// 本账期最后一天
        String firstDayNextAcct = SysDateMgr.getFirstDayOfNextMonth();// 下账期第一天

        userAcctDay.put("FIRST_DAY_THISACCT", firstDayThisAcct);// 本账期第一天
        userAcctDay.put("LAST_DAY_THISACCT", lastDayThisAcct);// 本账期最后一天
        userAcctDay.put("FIRST_DAY_NEXTACCT", firstDayNextAcct);// 下账期第一天

        return userAcctDay;
    }
}
