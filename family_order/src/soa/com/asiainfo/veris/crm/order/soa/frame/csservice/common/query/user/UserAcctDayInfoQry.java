
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;

public class UserAcctDayInfoQry
{

    /**
     * 查询帐户结帐日表数据
     * 
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset getAccountAcctDay(String acctId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        IDataset accountAcctDays = Dao.qryByCode("TF_F_ACCOUNT_ACCTDAY", "SEL_VALID_BY_ACCTID", param);
        return accountAcctDays;
    }

    /**
     * 查询帐户结帐日表有效数据
     * 
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IData getAccountValidAcctDay(String acctId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        IDataset accountAcctDays = Dao.qryByCode("TF_F_ACCOUNT_ACCTDAY", "SEL_BY_ACCTID", param);
        if (accountAcctDays != null && accountAcctDays.size() > 0)
        {
            return accountAcctDays.getData(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * 根据USER_ID获取开户用户用户账期数据
     * 
     * @param userId
     *            用户USER_ID
     * @param acctDay
     *            新的结账日
     * @return
     * @throws Exception
     */
    public static IDataset getNewUserAcctDayByOpen(String userId, String acctDay) throws Exception
    {
        IData params = new DataMap();
        params.put("ACCT_DAY", acctDay);
        params.put("USER_ID", userId);

        IDataset useracctdays = new DiversifyAcctUtil().getNewAcctDayByOpenUser(params);
        if (useracctdays != null && useracctdays.size() > 0)
        {
            for (int i = 0; i < useracctdays.size(); i++)
            {
                IData useracctday = useracctdays.getData(0);
                useracctday.put("INST_ID", SeqMgr.getInstId());
            }
        }
        return useracctdays;
    }

    /**
     * 根据USER_ID查询用户的结账日以及首次出账日
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserAcctDay(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_ACCTDAY", "SEL_ACCTDAY_BY_USERID", param);
    }

    public static IData getUserAcctDayAndFirstDateInfo(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        String nowDate = SysDateMgr.getSysTime();

        IDataset userAcctDayList = UcaInfoQry.qryUserAcctDaysByUserId(userId);

        IData userAcctDay = new DataMap();
        if (IDataUtil.isEmpty(userAcctDayList))
        {
            IData userInfoData = UcaInfoQry.qryUserInfoByUserId(userId);
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

        if (StringUtils.isNotEmpty(nextAcctDay))
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

        userAcctDay.put("FIRST_DAY_THISACCT", firstDayThisAcct);
        userAcctDay.put("LAST_DAY_THISACCT", lastDayThisAcct);
        userAcctDay.put("FIRST_DAY_NEXTACCT", firstDayNextAcct);

        return userAcctDay;
    }

    /**
     * 根据USER_ID查询用户账户信息
     */
    public static IDataset getUserAcctDays(String userId, String eparchy_code) throws Exception
    {

        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        if (StringUtils.isBlank(eparchy_code))
        {
            return UcaInfoQry.qryUserAcctDaysByUserId(userId);
        }
        else
        {
            return UcaInfoQry.qryUserAcctDaysByUserId(userId, eparchy_code);
        }

    }

    /**
     * 根据USER_ID查询有效用户账期信息
     */
    public static IDataset getValidUserAcctDays(String userId, String nowDate) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", userId);
        inparams.put("NOW_DATE", nowDate);
        return Dao.qryByCode("TF_F_USER_ACCTDAY", "SEL_BY_VALID_USERID", inparams);
    }

    /**
     * 获取新的账期数据,并设置AcctTimeEnv相应的参数值
     * 
     * @param userId
     *            USER_ID
     * @param chgAcctDay
     *            新账期日
     * @param chgType
     *            0：主动变更,1：被动变更
     * @throws Exception
     */
    public static void setAcctTimeEnvByAcctDayChg(String userId, String chgAcctDay, String chgType) throws Exception
    {
        IData param = new DataMap();
        param.put("CHG_TYPE", chgType);// 0：主动变更,1：被动变更
        param.put("ACCT_DAY", chgAcctDay);// 新账期日
        param.put("USER_ID", userId);// USER_ID

        IDataset returnNewAcctData = CSAppCall.call("SS.ChangeAcctDaySVC.getNewAcctDayByModify", param);

        String acctDay = "";
        String firstDate = "";
        String startDate = "";
        String nextAcctDay = "";
        String nextFirstDate = "";
        String nextStartDate = "";

        if (!returnNewAcctData.isEmpty())
        {
            for (int i = 0; i < returnNewAcctData.size(); i++)
            {
                if ("ADD".equals(returnNewAcctData.getData(i).getString("STATE")))
                {
                    nextAcctDay = returnNewAcctData.getData(i).getString("ACCT_DAY");
                    nextStartDate = returnNewAcctData.getData(i).getString("START_DATE");
                    nextFirstDate = returnNewAcctData.getData(i).getString("FIRST_DATE");
                }
                if ("DEL".equals(returnNewAcctData.getData(i).getString("STATE")))
                {
                    acctDay = returnNewAcctData.getData(i).getString("ACCT_DAY");
                    startDate = returnNewAcctData.getData(i).getString("START_DATE");
                    firstDate = returnNewAcctData.getData(i).getString("FIRST_DATE");
                }
            }
        }

        AcctTimeEnv env = new AcctTimeEnv(acctDay, firstDate, nextAcctDay, nextFirstDate, startDate, nextStartDate);
        AcctTimeEnvManager.setAcctTimeEnv(env);
    }
}
