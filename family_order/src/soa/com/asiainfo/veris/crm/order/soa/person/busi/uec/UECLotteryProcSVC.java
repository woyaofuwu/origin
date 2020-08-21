
package com.asiainfo.veris.crm.order.soa.person.busi.uec;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.database.dbconn.DBConnection;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UecLotteryInfoQry;

public class UECLotteryProcSVC extends CSBizService
{
    private byte[] lock = new byte[0];

    public IData checkLottryRight(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "ACTIVITY_NUMBER");
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "USER_ID");

        String inParam[] = new String[]
        { "in_v_acitiy_number", "in_v_user_id", "out_v_resultcode", "out_v_resultinfo", "out_v_remainnum" };
        IData param = new DataMap();
        param.put("in_v_acitiy_number", data.getInt("ACTIVITY_NUMBER"));
        param.put("in_v_user_id", data.getString("USER_ID"));

        Dao.callProc("P_UECLOTTERY_CHECKRIGHT", inParam, param);
        data.put("X_RESULTCODE", param.getString("ov_v_resultcode"));
        data.put("X_RESULTINFO", param.getString("ov_v_resultinfo"));

        return data;
    }

    public IData lottery(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        IDataUtil.chkParam(data, "ACTIVITY_NUMBER");
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "USER_ID");
        IDataUtil.chkParam(data, "RADIX");
        IDataUtil.chkParam(data, "PRIZE_ODDS_1");
        IDataUtil.chkParam(data, "PRIZE_ODDS_2");
        IDataUtil.chkParam(data, "PRIZE_ODDS_3");
        IDataUtil.chkParam(data, "PRIZE_ODDS_4");
        IDataUtil.chkParam(data, "PRIZE_ODDS_5");
        IDataUtil.chkParam(data, "TIME_LIMIT");

        // 是否可以抽奖判断
        IData checkResult = checkLottryRight(data);
        if (!"0".equals(checkResult.getString("X_RESULTCODE")))
        {
            return checkResult;
        }
        String detailTime = UecLotteryInfoQry.getSysDateAndMonth(data.getString("TIME_LIMIT"));
        IDataset results = UecLotteryInfoQry.qryLotteryPrizelist(data.getString("ACTIVITY_NUMBER"), detailTime, data.getString("CITY_CODE"));

        // 可抽中的奖项一数量
        int prizeNumber1 = Integer.parseInt(results.getData(0).getString("PRIZE_1", "0"));
        // 可抽中的奖项二数量
        int prizeNumber2 = Integer.parseInt(results.getData(0).getString("PRIZE_2", "0"));
        // 可抽中的奖项三数量
        int prizeNumber3 = Integer.parseInt(results.getData(0).getString("PRIZE_3", "0"));
        // 可抽中的奖项四数量
        int prizeNumber4 = Integer.parseInt(results.getData(0).getString("PRIZE_4", "0"));
        // 可抽中的奖项五数量
        int prizeNumber5 = Integer.parseInt(results.getData(0).getString("PRIZE_5", "0"));

        // 接口返回结果
        IData result = new DataMap();
        boolean winFlag = false;

        // 抽奖
        if (prizeNumber1 != 0 || prizeNumber2 != 0 || prizeNumber3 != 0 || prizeNumber4 != 0 || prizeNumber5 != 0)
        {
            // 在最后时间内不考虑中奖概率，先到先得
            if ("0".equals(detailTime.substring(24, 25)))
            {
                if (!winFlag && prizeNumber1 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "1");
                    result.put("X_RESULTINFO", "抽中奖项一");
                    result.put("LEVEL", "1");
                    winFlag = true;
                }
                if (!winFlag && prizeNumber2 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "2");
                    result.put("X_RESULTINFO", "抽中奖项二");
                    result.put("LEVEL", "2");
                    winFlag = true;
                }
                if (!winFlag && prizeNumber3 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "3");
                    result.put("X_RESULTINFO", "抽中奖项三");
                    result.put("LEVEL", "3");
                    winFlag = true;
                }
                if (!winFlag && prizeNumber4 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "4");
                    result.put("X_RESULTINFO", "抽中奖项四");
                    result.put("LEVEL", "4");
                    winFlag = true;
                }
                if (!winFlag && prizeNumber5 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "5");
                    result.put("X_RESULTINFO", "抽中奖项五");
                    result.put("LEVEL", "5");
                    winFlag = true;
                }
            }
            else
            {
                int radix = Integer.parseInt(data.getString("RADIX", "1000000"));
                int prizeOdds1 = Integer.parseInt(data.getString("PRIZE_ODDS_1", "0"));
                int prizeOdds2 = Integer.parseInt(data.getString("PRIZE_ODDS_2", "0"));
                int prizeOdds3 = Integer.parseInt(data.getString("PRIZE_ODDS_3", "0"));
                int prizeOdds4 = Integer.parseInt(data.getString("PRIZE_ODDS_4", "0"));
                int prizeOdds5 = Integer.parseInt(data.getString("PRIZE_ODDS_5", "0"));

                Random random = new Random();
                int usrRandom = random.nextInt(radix) + 1;
                data.put("RANDOM_NUM", usrRandom);

                if (usrRandom <= prizeOdds1)
                {
                    if (prizeNumber1 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "1");
                        result.put("X_RESULTINFO", "抽中奖项一");
                        result.put("LEVEL", "1");
                        winFlag = true;
                    }
                }
                else if (prizeOdds1 < usrRandom && usrRandom <= prizeOdds2)
                {
                    if (prizeNumber2 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "2");
                        result.put("X_RESULTINFO", "抽中奖项二");
                        result.put("LEVEL", "2");
                        winFlag = true;
                    }
                }
                else if (prizeOdds2 < usrRandom && usrRandom <= prizeOdds3)
                {
                    if (prizeNumber3 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "3");
                        result.put("X_RESULTINFO", "抽中奖项三");
                        result.put("LEVEL", "3");
                        winFlag = true;
                    }
                }
                else if (prizeOdds3 < usrRandom && usrRandom <= prizeOdds4)
                {
                    if (prizeNumber4 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "4");
                        result.put("X_RESULTINFO", "抽中奖项四");
                        result.put("LEVEL", "4");
                        winFlag = true;
                    }
                }
                else if (prizeOdds4 < usrRandom && usrRandom <= prizeOdds5)
                {
                    if (prizeNumber5 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "5");
                        result.put("X_RESULTINFO", "抽中奖项五");
                        result.put("LEVEL", "5");
                        winFlag = true;
                    }
                }
            }
        }

        if (!winFlag)
        {
            data.put("PRIZE_TYPE_CODE", "0");
            data.put("DEAL_FLAG", "0");
            result.put("X_RESULTCODE", "800200");
            result.put("X_RESULTINFO", "没有中奖");
        }
        else
        {
            data.put("DEAL_FLAG", "1");
            data.put("EXEC_FLAG", "0");
            result.put("X_RESULTCODE", "0");
        }
        synchronized (lock)
        {
            DBConnection conn = null;
            // PreparedStatement stmt = null;
            try
            {
                conn = new DBConnection(Route.ROUTE_EPARCHY_CODE, true, false);
                if (Dao.executeUpdateByCodeCode("SMS", "UPD_UEC_LOTTERY_PRIZESET", data) == 0)
                {

                    data.put("EXEC_FLAG", "");
                    data.put("DEAL_TAG", "0");
                    data.put("PRIZE_TYPE_CODE", "0");

                    result.put("X_RESULTCODE", "800200");
                    result.put("X_RESULTINFO", "没有中奖");
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    result.remove("LEVEL");
                }

                // TODO:充值奖项需可配置
                boolean rechargeFlag = false;
                if ("2".equals(data.getString("PRIZE_TYPE_CODE", "0")))
                {
                    data.put("EXEC_FLAG", "1");
                    data.put("EXEC_TIME", data.getString("DETAIL_TIME", ""));
                    rechargeFlag = true;
                }
                String tradeId = SeqMgr.getTradeId();
                data.put("TRADE_ID", tradeId);
                data.put("TRADE_TYPE_CODE", "305");
                // 记录子台账
                recordSubTrade(data);
                // 记录台账
                recordHisMainTrade(data);
                // 是否充值
                if (rechargeFlag)
                {
                    payFeeSubmit(data);
                }

                conn.commit();
            }
            catch (Exception e)
            {
                // TODO: handle exception
                if (null != conn)
                {
                    conn.rollback();
                }

                Utility.print(e);
                // errorInfo = Utility.getBottomException(e).getMessage();
            }
            finally
            {

                if (null != conn)
                {
                    conn.close();
                }
            }
        }

        return result;
    }

    public IDataset payFeeSubmit(IData data) throws Exception
    {

        /*
         * IData param = new DataMap(); IDataset ids = new DatasetList(); param.put("PROVINCE_CODE",
         * data.getString("PROVINCE_CODE", "")); param.put("TRADE_DEPART_ID", getVisit().getDepartId());
         * param.put("TRADE_STAFF_ID", getVisit().getStaffId()); param.put("TRADE_CITY_CODE", getVisit().getCityCode());
         * param.put("TRADE_EPARCHY_CODE", getTradeEparchyCode()); param.put("IN_MODE_CODE",
         * data.getString("IN_MODE_CODE", "")); param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));// 服务号码
         * param.put("WRITEOFF_MODE", "1");//'2'-按用户 '1'-按帐户 param.put("RECOVER_TAG", "0"); //复机标志
         * param.put("TRADE_FEE", "200"); //收费金额 param.put("PAYMENT_ID", "260"); //费用来源编码 param.put("PAY_FEE_MODE_CODE",
         * "260"); //收费方式 param.put("DEPOSIT_CODE", "260"); param.put("REMOVE_TAG", "0"); //销号标志 param.put("CHANNEL_ID",
         * data.getString("CHANNEL_ID", "150004"));
         */

        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String tradeId = data.getString("TRADE_ID");
        String channelId = data.getString("CHANNEL_ID", "150004");
        String remark = data.getString("REMARK", "");

        // ids = TuxedoHelper.callTuxSvc(pd, "TAM_RECV_FEE", param) 账务缴费接口 !@#
        IDataset ids = AcctCall.recvFee(serialNumber, tradeId, "200", channelId, "260", "260", remark);
        if (ids.size() < 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "缴费异常");
        }
        return ids;
    }

    private void recordHisMainTrade(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        IData iparam = new DataMap();

        iparam.put("TRADE_ID", data.getString("TRADE_ID", ""));

        iparam.put("BATCH_ID", "");

        iparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("TRADE_ID", "")));

        iparam.put("ORDER_ID", "");

        iparam.put("PROD_ORDER_ID", "");

        iparam.put("BPM_ID", "");

        iparam.put("CAMPN_ID", "");

        iparam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE", ""));

        iparam.put("PRIORITY", "0");

        iparam.put("SUBSCRIBE_TYPE", "0");

        iparam.put("SUBSCRIBE_STATE", "9");

        iparam.put("NEXT_DEAL_TAG", "0");

        iparam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", ""));

        iparam.put("USER_ID", data.getString("USER_ID", ""));

        iparam.put("CUST_NAME", data.getString("CUST_NAME", ""));

        iparam.put("ACCT_ID", data.getString("ACCT_ID", ""));

        iparam.put("CUST_ID", data.getString("CUST_ID", ""));

        iparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));

        iparam.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE", "00"));

        iparam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", ""));

        iparam.put("CITY_CODE", data.getString("CITY_CODE", ""));

        iparam.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));

        iparam.put("BRAND_CODE", data.getString("BRAND_CODE", ""));

        iparam.put("CUST_ID_B", "");

        iparam.put("ACCT_ID_B", "");

        iparam.put("USER_ID_B", "");

        iparam.put("SERIAL_NUMBER_B", "");

        iparam.put("CUST_CONTACT_ID", "");

        iparam.put("SERV_REQ_ID", "");

        iparam.put("INTF_ID", "");

        iparam.put("ACCEPT_DATE", data.getString("DETAIL_TIME", ""));

        iparam.put("TRADE_STAFF_ID", getVisit().getStaffId());

        iparam.put("TRADE_DEPART_ID", getVisit().getDepartId());

        iparam.put("TRADE_CITY_CODE", getVisit().getCityCode());

        iparam.put("TRADE_EPARCHY_CODE", getTradeEparchyCode());

        iparam.put("TERM_IP", getVisit().getLoginIP());

        iparam.put("OPER_FEE", "0");

        iparam.put("FOREGIFT", "0");

        iparam.put("ADVANCE_PAY", "0");

        iparam.put("INVOICE_NO", "");

        iparam.put("FEE_STATE", "0");

        iparam.put("FEE_TIME", "");

        iparam.put("FEE_STAFF_ID", "");

        iparam.put("PROCESS_TAG_SET", "0");

        iparam.put("OLCOM_TAG", "0");

        iparam.put("FINISH_DATE", data.getString("DETAIL_TIME", ""));

        iparam.put("EXEC_TIME", data.getString("DETAIL_TIME", ""));

        iparam.put("EXEC_ACTION", "0");

        iparam.put("EXEC_RESULT", "");

        iparam.put("EXEC_DESC", data.getString("EXEC_DESC", ""));

        iparam.put("CANCEL_TAG", "0");

        iparam.put("CANCEL_DATE", "");

        iparam.put("CANCEL_STAFF_ID", "");

        iparam.put("CANCEL_DEPART_ID", "");

        iparam.put("CANCEL_CITY_CODE", "");

        iparam.put("CANCEL_EPARCHY_CODE", "");

        iparam.put("UPDATE_TIME", data.getString("DETAIL_TIME", ""));

        iparam.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID", ""));

        iparam.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID", ""));

        iparam.put("REMARK", data.getString("RADIX", "")); // 总数

        iparam.put("RSRV_STR1", data.getString("EXPLODE_PRIZE_ODDS", "")); // 劲爆奖中奖区间

        iparam.put("RSRV_STR2", data.getString("SURPRISE_PRIZE_ODDS", "")); // 惊喜奖中奖区间

        iparam.put("RSRV_STR3", data.getString("LUCK_PRIZE_ODDS", "")); // 幸运奖中奖区间

        iparam.put("RSRV_STR4", data.getString("RANDOM_NUM", "")); // 随机数

        iparam.put("RSRV_STR5", data.getString("TIME_LIMIT", "")); // 最后时间
        iparam.put("RSRV_STR6", data.getString("EXEC_FLAG", "")); // 领奖标识
        iparam.put("RSRV_STR7", data.getString("PRIZE_TYPE_CODE", "")); // 中奖类型

        iparam.put("RSRV_STR8", data.getString("EXEC_TIME", "")); // 领奖时间

        Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_ALL", iparam);
    }

    public void recordSubTrade(IData data) throws Exception
    {

        // 子台帐
        IData subTrade = new DataMap();
        subTrade.put("TRADE_ID", data.getString("TRADE_ID", ""));
        subTrade.put("ACTIVITY_NUMBER", data.getString("ACTIVITY_NUMBER"));
        subTrade.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(data.getString("DETAIL_TIME")));
        subTrade.put("USER_ID", data.getString("USER_ID", ""));
        subTrade.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        subTrade.put("CITY_CODE", data.getString("CITY_CODE", ""));
        subTrade.put("DEAL_FLAG", data.getString("DEAL_FLAG", "0"));
        subTrade.put("ACCEPT_DATE", data.getString("ACCEPT_DATE", ""));
        subTrade.put("PRIZE_TYPE_CODE", data.getString("PRIZE_TYPE_CODE", "0"));
        subTrade.put("EXEC_FLAG", data.getString("EXEC_FLAG", "0"));
        subTrade.put("EXEC_TIME", data.getString("EXEC_TIME", ""));
        subTrade.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        subTrade.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        subTrade.put("RANDOM_NUM", data.getString("RANDOM_NUM", ""));
        subTrade.put("RADIX", data.getString("RADIX", ""));
        subTrade.put("PRIZE_ODDS_1", data.getString("PRIZE_ODDS_1", ""));
        subTrade.put("PRIZE_ODDS_2", data.getString("PRIZE_ODDS_2", ""));
        subTrade.put("PRIZE_ODDS_3", data.getString("PRIZE_ODDS_3", ""));
        subTrade.put("PRIZE_ODDS_4", data.getString("PRIZE_ODDS_4", ""));
        subTrade.put("PRIZE_ODDS_5", data.getString("PRIZE_ODDS_5", ""));

        Dao.executeUpdateByCodeCode("SMS", "INS_UEC_LOTTERY_TRADE", subTrade);
    }

    // set转换
    private String setToString(HashSet<String> set)
    {
        String returnStr = "";
        for (Iterator it = set.iterator(); it.hasNext();)
        {
            String element = (String) it.next();
            if (element != null && !"".equals(element))
            {
                returnStr = returnStr + "," + element;
            }
        }
        if (returnStr.length() > 1)
            returnStr = returnStr.substring(1);
        return returnStr;
    }

    private String setToString(HashSet<String> set, String quotes)
    {
        String returnStr = "";
        for (Iterator it = set.iterator(); it.hasNext();)
        {
            String element = (String) it.next();
            if (element != null && !"".equals(element))
            {
                returnStr = returnStr + "," + quotes + element + quotes;
            }
        }
        if (returnStr.length() > 1)
            returnStr = returnStr.substring(1);
        return returnStr;
    }
}
