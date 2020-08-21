
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UecLotteryInfoQry
{

    public static String getSysDateAndMonth(String time) throws Exception
    {
        IData data = new DataMap();
        data.put("TIME", time);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("select to_char(sysdate, 'YYYYMMDDHH24MISS')");
        sql.append("||to_char(last_day(sysdate),'dd')");
        sql.append("||to_char(last_day(sysdate)+1, 'YYYYMMDD')");
        sql.append("||decode(SIGN(((to_date(to_char(last_day(sysdate)+1, 'YYYYMMDD'),'YYYYMMDDHH24MISS') - :TIME/24) -sysdate)),'0',1,'1',1,0)");
        sql.append("||to_char(add_months(sysdate,-1), 'YYYYMMDDHH24MISS')");
        sql.append(" OUTSTR from dual");
        IDataset ids = Dao.qryBySql(sql, data);
        return ids.get(0, "OUTSTR").toString();
    }

    public static IDataset qryLotteryPrizelist(String activityNumber, String detailTime, String cityCode) throws Exception
    {
        IData param = new DataMap();
        param.put("ACTIVITY_NUMBER", activityNumber);
        param.put("START_DATE", detailTime.substring(0, 6) + "01");
        param.put("END_DATE", detailTime.substring(0, 8));
        param.put("CITY_CODE", cityCode);

        return Dao.qryByCode("SMS", "SEL_UEC_LOTTERY_PRIZESET", param);
    }

    /**
     * 读取用户在CHECK_DATE之后办理特定TRADE_TYPE_CODE业务的记录数
     * 
     * @param pd
     * @param userData
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeCountAll(String userId, String acceptMonth, String checkDate, String tradeTypeCodeParam) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCEPT_MONTH", acceptMonth);
        param.put("CHECK_DATE", checkDate);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT COUNT(*) BH_TRADE_COUNT FROM TF_BH_TRADE WHERE 1=1");
        sql.append(" AND USER_ID=:USER_ID");
        sql.append(" AND ACCEPT_MONTH=:ACCEPT_MONTH");
        sql.append(" AND IN_MODE_CODE='5'");
        sql.append(" AND TRADE_TYPE_CODE in (" + tradeTypeCodeParam + ")");
        sql.append(" AND UPDATE_TIME > TO_DATE(:VCHECK_DATE,'YYYY-MM-DD HH24:MI:SS')");

        IDataset ids = Dao.qryBySql(sql, param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return ids;
    }

    /**
     * 读取用户在CHECK_DATE之后办理特定TRADE_TYPE_CODE业务的记录数
     * 
     * @param pd
     * @param userData
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeCountDcntID(String userId, String acceptMonth, String diccantCodeParam) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCEPT_MONTH", acceptMonth);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT COUNT(DISTINCT T2.DISCNT_CODE) TRADECOUNT FROM TF_BH_TRADE T1,TF_B_TRADE_DISCNT T2 WHERE 1=1");
        sql.append(" AND T1.TRADE_ID=T2.TRADE_ID");
        sql.append(" AND T1.USER_ID=:USER_ID");
        sql.append(" AND T1.ACCEPT_MONTH=:ACCEPT_MONTH");
        sql.append(" AND T1.IN_MODE_CODE='5'");
        sql.append(" AND T1.TRADE_TYPE_CODE=110");
        sql.append(" AND SUBSTR(T1.PROCESS_TAG_SET,9,1)='2'");
        sql.append(" AND T2.DISCNT_CODE in (" + diccantCodeParam + ")");

        IDataset ids = Dao.qryBySql(sql, param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return ids;
    }

    public static IDataset qryTradeCountPlsvcID(String userId, String acceptMonth, String platServiceIdParam) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCEPT_MONTH", acceptMonth);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT COUNT(DISTINCT T2.SERVICE_ID) TRADECOUNT FROM TF_BH_TRADE T1,TF_B_TRADE_PLATSVC T2 WHERE 1=1");
        sql.append(" AND T1.TRADE_ID=T2.TRADE_ID");
        sql.append(" AND T1.USER_ID=:USER_ID");
        sql.append(" AND T1.ACCEPT_MONTH=:ACCEPT_MONTH");
        sql.append(" AND T1.IN_MODE_CODE='5'");
        sql.append(" AND T1.TRADE_TYPE_CODE=3700");
        sql.append(" AND SUBSTR(T1.PROCESS_TAG_SET,9,1)='4'");
        sql.append(" AND T2.SERVICE_ID in (" + platServiceIdParam + ")");
        IDataset ids = Dao.qryBySql(sql, param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return ids;
    }

    public static IDataset qryTradeCountPrdtID(String userId, String acceptMonth, String productIdParam) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCEPT_MONTH", acceptMonth);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT COUNT(DISTINCT T2.PRODUCT_ID) TRADECOUNT FROM TF_BH_TRADE T1,TF_B_TRADE_PRODUCT T2 WHERE 1=1");
        sql.append(" AND T1.TRADE_ID=T2.TRADE_ID");
        sql.append(" AND T1.USER_ID=:USER_ID");
        sql.append(" AND T1.ACCEPT_MONTH=:ACCEPT_MONTH");
        sql.append(" AND T1.IN_MODE_CODE='5'");
        sql.append(" AND T1.TRADE_TYPE_CODE=110");
        sql.append(" AND SUBSTR(T1.PROCESS_TAG_SET,9,1)='4'");
        sql.append(" AND T2.PRODUCT_ID in (" + productIdParam + ")");
        IDataset ids = Dao.qryBySql(sql, param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return ids;
    }

    /**
     * 读取用户在CHECK_DATE之后办理特定TRADE_TYPE_CODE业务的记录数
     * 
     * @param pd
     * @param userData
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeCountSvcID(String userId, String acceptMonth, String serviceIdParam) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCEPT_MONTH", acceptMonth);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT COUNT(DISTINCT T2.SERVICE_ID) TRADECOUNT FROM TF_BH_TRADE T1,TF_B_TRADE_SVC T2 WHERE 1=1");
        sql.append(" AND T1.TRADE_ID=T2.TRADE_ID");
        sql.append(" AND T1.USER_ID=:USER_ID");
        sql.append(" AND T1.ACCEPT_MONTH=:ACCEPT_MONTH");
        sql.append(" AND T1.IN_MODE_CODE='5'");
        sql.append(" AND T1.TRADE_TYPE_CODE=110");
        sql.append(" AND SUBSTR(T1.PROCESS_TAG_SET,9,1) in ('1','3')");
        sql.append(" AND T2.SERVICE_ID in (" + serviceIdParam + ")");

        IDataset ids = Dao.qryBySql(sql, param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return ids;
    }

    /**
     * 读取用户在CHECK_DATE之后办理特定TRADE_TYPE_CODE业务的记录数
     * 
     * @param pd
     * @param userData
     * @return
     * @throws Exception
     */
    public static IDataset qryTradeCountTrade(String userId, String acceptMonth, String tradeTypeCodeParam) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCEPT_MONTH", acceptMonth);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT COUNT(DISTINCT TRADE_TYPE_CODE) TRADECOUNT FROM TF_BH_TRADE WHERE 1=1");
        sql.append(" AND USER_ID=:USER_ID");
        sql.append(" AND ACCEPT_MONTH=:ACCEPT_MONTH");
        sql.append(" AND IN_MODE_CODE='5'");
        sql.append(" AND TRADE_TYPE_CODE in (" + tradeTypeCodeParam + ")");

        IDataset ids = Dao.qryBySql(sql, param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        return ids;
    }

    /**
     * 判断用户是否为移动员工或代理商
     * 
     * @param pd
     * @param userData
     * @return
     * @throws Exception
     */
    public static IDataset qryUserFlag(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT COUNT(*) FLAG FROM TF_F_USER_DISCNT WHERE 1=1");
        sql.append(" AND USER_ID=:USER_ID");
        sql.append(" AND DISCNT_CODE IN (270,655)");
        sql.append(" AND END_DATE>SYSDATE");

        IDataset ids = Dao.qryBySql(sql, param);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }
        return ids;
    }

    public static IDataset queryUecLotteryByTrade(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        return Dao.qryByCode("SMS", "SEL_UEC_LOTTERY_BY_TRADE", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset queryUecLotteryConfig(String activityNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("ACTIVITY_NUMBER", activityNumber);

        return Dao.qryByCode("SMS", "SEL_UEC_LOTTERY_CONFIG", param);
    }

    public static IDataset queryUecLotteryInfo(String partitionId, String userId, String month, String activityNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("PARTITION_ID", partitionId);
        param.put("USER_ID", userId);
        param.put("MONTH", month);
        param.put("ACTIVITY_NUMBER", activityNumber);

        return Dao.qryByCode("SMS", "SEL_USER_UECLOTTERY", param);
    }

    public static IDataset queryUecLotteryInfos(String userId, String dealFlag, String execFlag, String activityNumber, String prizeTypeCode, String beginDate, String endDate, String rowNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DEAL_FLAG", dealFlag);
        param.put("EXEC_FLAG", execFlag);
        param.put("ACTIVITY_NUMBER", activityNumber);
        param.put("PRIZE_TYPE_CODE", prizeTypeCode);
        param.put("BEGIN_DATE", beginDate);
        param.put("END_DATE", endDate);
        param.put("ROW_NUMBER", rowNumber);

        return Dao.qryByCode("SMS", "SEL_UEC_LOTTERY_FOR_BOSS", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset queryUecLotteryTradeCount(String userId, String accept_month, String activityNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("ACCEPT_MONTH", accept_month);
        param.put("ACTIVITY_NUMBER", activityNumber);

        return Dao.qryByCode("SMS", "SEL_UEC_LOTTERY_TRADE_COUNT", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

}
