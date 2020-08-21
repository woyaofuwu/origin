
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive;

import java.util.Random;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MobileLotteryActiveQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UecLotteryInfoQry;

public class MobileLotterySVC extends CSBizService
{

    private byte[] lock = new byte[0];

    /**
     * 月月缴费通抽奖权限校验
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkLottryRight(IData data) throws Exception
    {
        // 当月有使用手机缴费通缴单次缴费超过10元的客户。如当月不进行抽奖，则抽奖机会失效。
        IDataUtil.chkParam(data, "SERIAL_NUMBER");

        IDataset ids = null;
        IData result = new DataMap();
        ids = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER")));
        if (IDataUtil.isEmpty(ids))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户信息不存在！");
        }
        String user_id = ids.getData(0).getString("USER_ID", "");

        ids = MobileLotteryActiveQry.queryLotteryInfoByUid(user_id, SysDateMgr.getNowCyc());

        int lt = 0;
        boolean hasRight = false;
        String prizeType = "0";
        // 抽奖次数
        if (ids.size() > 0)
        {
            lt = ids.size();
            for (int i = 0; i < lt; i++)
            {
                IData tmp = ids.getData(i);
                if ("1".equals(tmp.getString("PRIZE_TYPE_CODE", "")) || "2".equals(tmp.getString("PRIZE_TYPE_CODE", "")) || "3".equals(tmp.getString("PRIZE_TYPE_CODE", "")) || "4".equals(tmp.getString("PRIZE_TYPE_CODE", ""))
                        || "5".equals(tmp.getString("PRIZE_TYPE_CODE", "")))
                {
                    hasRight = true;
                    prizeType = tmp.getString("PRIZE_TYPE_CODE", "");
                    break;
                }
            }
        }
        if (hasRight)
        {
            result.put("X_RESULTCODE", "3");// 抽中过奖，无权再抽
            result.put("X_RESULTINFO", prizeType);
            result.put("X_RSPTYPE", "2");
            result.put("X_RSPCODE", "2998");
            return result;
        }

        data.put("X_PAY_USER_ID", data.getString("USER_ID", ""));
        data.put("NET_TYPE_CODE", "00");
        data.put("CANCEL_TAG", "0");
        data.put("REMOVE_TAG", "0");
        data.put("BEGIN_TIME", SysDateMgr.getCurMonth() + "-01");
        data.put("END_TIME", SysDateMgr.getSysDate());

        IDataset idset = new DatasetList();
        // idset = TuxedoHelper.callTuxSvc(pd, "QAM_PAYLOGBYUSERID", data, false); 查询缴费记录 !@#
        idset = AcctCall.querySnPaymentPayLogInfo(data.getString("SERIAL_NUMBER"));
        if (ids.size() < 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户缴费信息异常！");
        }
        int num = 0; // 缴费次数
        for (int i = 0; i < idset.size(); i++)
        {
            IData temp = idset.getData(i);
            if ("3".equals(temp.getString("PAYMENT_ID", "")) && "10".equals(temp.getString("PAY_FEE_MODE_CODE", "")))
            {
                hasRight = true;
                if (1000 <= Integer.parseInt(temp.getString("RECV_FEE", "0")))
                {
                    num++;
                }
            }
        }
        if (hasRight)
        {
            if (num <= 0)
            {
                result.put("X_RESULTCODE", "2");
                result.put("X_RESULTINFO", "缴费过，但是缴费金额不够");
                result.put("X_RSPTYPE", "2");
                result.put("X_RSPCODE", "2998");
            }
            else
            {
                if (num > lt)
                {
                    result.put("X_RESULTCODE", "0");
                    result.put("X_RESULTINFO", "可以抽奖");
                }
                else
                {
                    result.put("X_RESULTCODE", "5");
                    result.put("X_RESULTINFO", "抽奖机会暂时用完了");
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                }
            }
        }
        else
        {
            result.put("X_RESULTCODE", "1");
            result.put("X_RESULTINFO", "无抽奖权限");
            result.put("X_RSPTYPE", "2");
            result.put("X_RSPCODE", "2998");
        }

        return result;
    }

    /**
     * 月月缴费通抽奖
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData lottery(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "REMOVE_TAG");
        IDataUtil.chkParam(data, "LIMIT");

        IData checkResult = checkLottryRight(data);
        String code = checkResult.getString("X_RESULTCODE");
        if (!code.equals("0"))
        {
            return checkResult;
        }

        int sum = Integer.parseInt(data.getString("LIMIT", "1000000"));
        int r1 = Integer.parseInt(data.getString("PARA1", "2"));
        int r2 = Integer.parseInt(data.getString("PARA2", "7"));
        int r3 = Integer.parseInt(data.getString("PARA3", "100"));
        int r4 = Integer.parseInt(data.getString("PARA4", "300"));
        int r5 = Integer.parseInt(data.getString("PARA5", "500"));

        IData result = new DataMap();

        // 劲爆奖中奖数
        int l1 = 0;
        // 一等奖中奖数
        int l2 = 0;
        // 二等奖中奖数
        int l3 = 0;
        // 三等奖中奖数
        int l4 = 0;
        // 幸运奖中奖数
        int l5 = 0;

        IDataset ids = new DatasetList();
        // ids = UserInfoQry.getMainSerialNumberInfo(data.getString("SERIAL_NUMBER"));
        ids = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER")));

        if (ids.size() <= 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户信息不存在！");
        }
        data.putAll(ids.getData(0));

        ids = MobileLotteryActiveQry.queryUserCity(data.getString("USER_ID"));
        if (ids.size() > 0)
        {
            if (StringUtils.isNotBlank(ids.getData(0).getString("CITY_CODE", "")))
            {
                data.put("CITY_CODE", ids.getData(0).getString("CITY_CODE"));
            }

        }
        String detailTime = UecLotteryInfoQry.getSysDateAndMonth(data.getString("LAST_DATE", "2"));
        ids = MobileLotteryActiveQry.queryLotteryInfoByUid(data.getString("USER_ID"), detailTime.substring(0, 6));

        if (ids.size() > 0)
        {
            for (int i = 0, s = ids.size(); i < s; i++)
            {
                IData tmp = ids.getData(i);
                if ("1".equals(tmp.getString("PRIZE_TYPE_CODE", "")) || "2".equals(tmp.getString("PRIZE_TYPE_CODE", "")) || "3".equals(tmp.getString("PRIZE_TYPE_CODE", "")) || "4".equals(tmp.getString("PRIZE_TYPE_CODE", ""))
                        || "5".equals(tmp.getString("PRIZE_TYPE_CODE", "")))
                {
                    result.put("X_RESULTCODE", "4");
                    result.put("X_RESULTINFO", "已经中过奖");
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    return result;
                }
            }
        }
        int day = Integer.parseInt(detailTime.substring(6, 8));
        int month = Integer.parseInt(detailTime.substring(14, 16));
        data.put("DETAIL_TIME", detailTime.substring(0, 14));

        IData out = new DataMap();
        boolean tag = false;

        String startDate = detailTime.substring(0, 6) + "01";
        String endDate = detailTime.substring(0, 8);
        String cityCode = data.getString("CITY_CODE");
        ids = MobileLotteryActiveQry.qryDetailNum(startDate, endDate, cityCode);
        l1 = Integer.parseInt(ids.getData(0).getString("STOCK_POS_1", "0"));
        l2 = Integer.parseInt(ids.getData(0).getString("ACTION_D1", "0"));
        l3 = Integer.parseInt(ids.getData(0).getString("ACTION_D2", "0"));
        l4 = Integer.parseInt(ids.getData(0).getString("ACTION_D3", "0"));
        l5 = Integer.parseInt(ids.getData(0).getString("STOCK_POS_2", "0"));
        // 获取用户随机号
        if (l1 <= 0 && l2 <= 0 && l3 <= 0 && l4 <= 0 && l5 <= 0)
        {
            data.put("PRIZE_TYPE_CODE", "0");
            data.put("DEAL_FLAG", "0");
            result.put("X_RESULTCODE", "0");
            result.put("X_RESULTINFO", "没有中奖");
        }
        else
        {
            // 最后截止时间LAST_DATE内不考虑中奖概率，对于剩余奖项先到先得
            if ("0".equals(detailTime.substring(24, 25)))
            {
                if (l1 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "1");
                    data.put("DEAL_FLAG", "1");
                    result.put("X_RESULTCODE", "1");
                    result.put("X_RESULTINFO", "抽中劲爆奖");
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    tag = true;
                }
                if (!tag && l2 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "2");
                    data.put("DEAL_FLAG", "1");
                    result.put("X_RESULTCODE", "2");
                    result.put("X_RESULTINFO", "抽中一等奖");
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    tag = true;
                }
                if (!tag && l3 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "3");
                    data.put("DEAL_FLAG", "1");
                    result.put("X_RESULTCODE", "3");
                    result.put("X_RESULTINFO", "抽中二等奖");
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    tag = true;
                }
                if (!tag && l4 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "4");
                    data.put("DEAL_FLAG", "1");
                    result.put("X_RESULTCODE", "4");
                    result.put("X_RESULTINFO", "抽中三等奖");
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    tag = true;
                }
                if (!tag && l5 > 0)
                {
                    data.put("PRIZE_TYPE_CODE", "5");
                    data.put("DEAL_FLAG", "1");
                    result.put("X_RESULTCODE", "5");
                    result.put("X_RESULTINFO", "抽中幸运奖");
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");
                    tag = true;
                }
                if (!tag)
                {
                    data.put("PRIZE_TYPE_CODE", "0");
                    data.put("DEAL_FLAG", "0");
                    result.put("X_RESULTCODE", "0");
                    result.put("X_RESULTINFO", "没有中奖");
                }
            }
            else
            {
                Random random = new Random();
                int ra = random.nextInt(sum) + 1;
                data.put("RANDOM_NUM", ra);
                if (ra <= r1)
                {

                    if (l1 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "1");
                        data.put("DEAL_FLAG", "1");
                        result.put("X_RESULTCODE", "1");
                        result.put("X_RESULTINFO", "抽中劲爆奖");
                        result.put("X_RSPTYPE", "2");
                        result.put("X_RSPCODE", "2998");
                    }
                    else
                    {
                        data.put("PRIZE_TYPE_CODE", "0");
                        data.put("DEAL_FLAG", "0");
                        result.put("X_RESULTCODE", "0");
                        result.put("X_RESULTINFO", "没有中奖");
                    }
                }
                else if (r1 < ra && ra <= r2)
                {
                    if (l2 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "2");
                        data.put("DEAL_FLAG", "1");
                        result.put("X_RESULTCODE", "2");
                        result.put("X_RESULTINFO", "抽中一等奖");
                        result.put("X_RSPTYPE", "2");
                        result.put("X_RSPCODE", "2998");
                    }
                    else
                    {
                        data.put("PRIZE_TYPE_CODE", "0");
                        data.put("DEAL_FLAG", "0");
                        result.put("X_RESULTCODE", "0");
                        result.put("X_RESULTINFO", "没有中奖");
                    }
                }
                else if (r2 < ra && ra <= r3)
                {
                    // 按天查询每天中奖数
                    if (l3 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "3");
                        data.put("DEAL_FLAG", "1");
                        result.put("X_RESULTCODE", "3");
                        result.put("X_RESULTINFO", "抽中二等奖");
                        result.put("X_RSPTYPE", "2");
                        result.put("X_RSPCODE", "2998");
                    }
                    else
                    {
                        data.put("PRIZE_TYPE_CODE", "0");
                        data.put("DEAL_FLAG", "0");
                        result.put("X_RESULTCODE", "0");
                        result.put("X_RESULTINFO", "没有中奖");
                    }
                }
                else if (r3 < ra && ra <= r4)
                {
                    // 按天查询每天中奖数
                    if (l4 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "4");
                        data.put("DEAL_FLAG", "1");
                        result.put("X_RESULTCODE", "4");
                        result.put("X_RESULTINFO", "抽中三等奖");
                        result.put("X_RSPTYPE", "2");
                        result.put("X_RSPCODE", "2998");
                    }
                    else
                    {
                        data.put("PRIZE_TYPE_CODE", "0");
                        data.put("DEAL_FLAG", "0");
                        result.put("X_RESULTCODE", "0");
                        result.put("X_RESULTINFO", "没有中奖");
                    }
                }
                else if (r4 < ra && ra <= r5)
                {
                    // 按天查询每天中奖数
                    if (l5 > 0)
                    {
                        data.put("PRIZE_TYPE_CODE", "5");
                        data.put("DEAL_FLAG", "1");
                        result.put("X_RESULTCODE", "5");
                        result.put("X_RESULTINFO", "抽中幸运奖");
                        result.put("X_RSPTYPE", "2");
                        result.put("X_RSPCODE", "2998");
                    }
                    else
                    {
                        data.put("PRIZE_TYPE_CODE", "0");
                        data.put("DEAL_FLAG", "0");
                        result.put("X_RESULTCODE", "0");
                        result.put("X_RESULTINFO", "没有中奖");
                    }
                }
                else
                {
                    data.put("PRIZE_TYPE_CODE", "0");
                    data.put("DEAL_FLAG", "0");
                    result.put("X_RESULTCODE", "0");
                    result.put("X_RESULTINFO", "没有中奖");
                }
            }
        }
        out.put("MONTH", data.getString("DETAIL_TIME", "").substring(0, 6));
        out.put("USER_ID", data.getString("USER_ID", ""));
        out.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        out.put("DEAL_FLAG", data.getString("DEAL_FLAG", "0"));
        out.put("ACCEPT_DATE", data.getString("DETAIL_TIME", ""));
        out.put("PRIZE_TYPE_CODE", data.getString("PRIZE_TYPE_CODE", "0"));
        out.put("EXEC_FLAG", "0");
        out.put("EXEC_TIME", "");
        out.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
        out.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
        out.put("REVC1", ""); // 上月话费
        out.put("REVC2", data.getString("CITY_CODE"));
        out.put("REVC3", "");
        out.put("REVC4", "");
        out.put("REVC5", "");
        out.put("REMARK", "");
        out.put("CITY_CODE", data.getString("CITY_CODE"));

        synchronized (lock)
        {
            // try{
            // 更改剩余奖项数据
            if (Dao.executeUpdateByCodeCode("TM_O_LOTTERY", "UPD_ALL_NUM", out) == 0)
            {
                out.put("DEAL_FLAG", "0");
                out.put("PRIZE_TYPE_CODE", "0");
                out.put("EXEC_FLAG", "");
                data.put("EXEC_FLAG", "");
                data.put("DEAL_FLAG", "0");
                data.put("PRIZE_TYPE_CODE", "0");

                result.put("X_RESULTCODE", "0");
                result.put("X_RESULTINFO", "没有中奖");
            }
            if ("4".equals(data.getString("PRIZE_TYPE_CODE", "0")))
            {
                out.put("EXEC_FLAG", "1");
                out.put("EXEC_TIME", data.getString("DETAIL_TIME", ""));
                data.put("LAST_MONTH", detailTime.substring(25, 31));

                String openDate = data.getString("OPEN_DATE", "").substring(0, 7);

                String[] tmpStr = openDate.split("-");
                String tmp = "";
                String fee = "";
                for (int i = 0; i < tmpStr.length; i++)
                {
                    tmp += tmpStr[i];
                }
                if (tmp.equals(data.getString("DETAIL_TIME").substring(0, 6)))
                {
                    fee = "10000";
                    result.put("ACCESS_TAG", "1");
                }
                else
                {
                    fee = queryLastFee(data);
                }
                out.put("REVC1", fee);
                data.put("X_FPAY_FEE", fee);
            }
            data.put("EXEC_FLAG", out.getString("EXEC_FLAG", ""));
            data.put("EXEC_TIME", out.getString("EXEC_TIME", ""));
            Dao.executeUpdateByCodeCode("TF_F_USER_LOTTERY", "INS_ALL_VALUE", out);

            data.put("TRADE_TYPE_CODE", "303");

            String tradeId = SeqMgr.getTradeId();
            data.put("TRADE_ID", tradeId);
            recordHisMainTrade(data);

            // 三等奖就直接缴纳上个月的话费 需要查询用户上月消费金额，并缴费
            if ("4".equals(data.getString("PRIZE_TYPE_CODE", "0")))
            {
                result.put("X_FPAY_FEE", data.getString("X_FPAY_FEE", ""));
                if (0 < Integer.parseInt(data.getString("X_FPAY_FEE", "0")))
                {
                    payFeeSubmit(data);
                }
            }

            // }catch (Exception e) {
            // TODO: handle exception
            /*
             * if (null != e.getMessage() && e.getMessage().startsWith("ORA-00001")) { result.put("X_RESULTCODE", "4");
             * result.put("X_RESULTINFO", "已经抽过奖"); result.put("X_RSPTYPE", "2"); result.put("X_RSPCODE", "2998");
             * return result; }else{ throw e; }
             */
            // Utility.print(e);
            // }
        }

        return result;
    }

    public IDataset payFeeSubmit(IData data) throws Exception
    {
        // >>>>>>>>>>>缴费>>>>>>>>>>
        /*
         * IData param = new DataMap(); IDataset ids = new DatasetList(); param.put("PROVINCE_CODE",
         * data.getString("PROVINCE_CODE", "")); param.put("TRADE_DEPART_ID", getVisit().getDepartId());
         * param.put("TRADE_STAFF_ID", getVisit().getStaffId()); param.put("TRADE_CITY_CODE", getVisit().getCityCode());
         * param.put("TRADE_EPARCHY_CODE", getTradeEparchyCode()); param.put("IN_MODE_CODE",
         * data.getString("IN_MODE_CODE", "")); param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));// 服务号码
         * param.put("WRITEOFF_MODE", "1");//'2'-按用户 '1'-按帐户 param.put("RECOVER_TAG", "0"); //复机标志
         * param.put("TRADE_FEE", data.getString("X_FPAY_FEE", "")); //收费金额 param.put("PAYMENT_ID", "183"); //费用来源编码
         * param.put("PAY_FEE_MODE_CODE", "183"); //收费方式 param.put("DEPOSIT_CODE", "0"); param.put("REMOVE_TAG", "0");
         * //销号标志 param.put("CHANNEL_ID", data.getString("CHANNEL_ID", "150004"));
         */

        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String tradeFee = data.getString("X_FPAY_FEE", "");
        String channelId = data.getString("CHANNEL_ID", "150004");
        String remark = data.getString("REMARK", "150004");
        String tradeId = data.getString("TRADE_ID");

        // ids = TuxedoHelper.callTuxSvc(pd, "TAM_RECV_FEE", param) 账务缴费接口 !@#
        IDataset ids = AcctCall.recvFee(serialNumber, tradeId, tradeFee, channelId, "183", "183", remark);

        if (ids.size() < 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "缴费异常");
        }
        return ids;
    }

    public String queryLastFee(IData data) throws Exception
    {

        // ">>>>>>>>>>>查询话费信息>>>>>>>>>>
        String fee = "0";
        IData param = new DataMap();
        IDataset idset = new DatasetList();
        param.put("PROVINCE_CODE", data.getString("PROVINCE_CODE", ""));
        param.put("TRADE_DEPART_ID", getVisit().getDepartId());
        param.put("TRADE_STAFF_ID", getVisit().getStaffId());
        param.put("TRADE_CITY_CODE", getVisit().getCityCode());
        param.put("TRADE_EPARCHY_CODE", getTradeEparchyCode());
        param.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", ""));

        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));// 服务号码
        param.put("START_CYCLE_ID", data.getString("LAST_MONTH", ""));// 起始账期
        param.put("END_CYCLE_ID", data.getString("LAST_MONTH", ""));// 结束账期
        param.put("REMOVE_TAG", "0");
        param.put("WRITEOFF_MODE", "2");
        param.put("X_CHOICE_TAG", "3");
        // idset = TuxedoHelper.callTuxSvc(pd, "QAM_MASTERBILLQRY", param); 调用账务接口 !@#
        idset = CSAppCall.callAcct("AM_ITF_MasterBillQry", param, false).getData();

        /*
         * IData tt = new DataMap(); tt.put("CYCLE_ID", data.getString("LAST_MONTH", "").substring(0, 6));
         * tt.put("MONTH_SUM_FEE", "1200"); idset.add(tt);
         */
        // 数据模拟

        if (idset.size() <= 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "话费信息查询异常");
        }
        else
        {
            for (int i = 0, s = idset.size(); i < s; i++)
            {
                IData tmp = idset.getData(0);
                if (tmp.getString("CYCLE_ID", "0").equals(data.getString("LAST_MONTH", "").substring(0, 6)))
                {
                    fee = tmp.getString("MONTH_SUM_FEE", "0");
                    break;
                }
            }
        }

        return fee;
    }

    /**
     * 查询中奖用户信息
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryLottryInfo(IData data) throws Exception
    {
        if ("0".equals(data.getString("X_GETMODE")))
        {
            IDataUtil.chkParam(data, "MONTH");
        }
        else
        {
            IDataUtil.chkParam(data, "START_DATE");
            IDataUtil.chkParam(data, "END_DATE");
        }
        // 中奖标识
        data.put("DEAL_FLAG", "1");
        IData result = new DataMap();
        IDataset idset = MobileLotteryActiveQry.queryCountMonth(data.getString("START_DATE"), data.getString("END_DATE"), data.getString("MONTH"));
        if (idset.size() < 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "指定区间内的中奖数据不存在！");
        }

        for (int i = 0; i < idset.size(); i++)
        {
            IData tmp = idset.getData(i);
            if ("1".equals(tmp.getString("PRIZE_TYPE_CODE", "")))
            {
                result.put("RSRV_NUM1", tmp.getString("MAX_VALUE", "0"));
            }
            else if ("2".equals(tmp.getString("PRIZE_TYPE_CODE", "")))
            {
                result.put("RSRV_NUM2", tmp.getString("MAX_VALUE", "0"));
            }
            else if ("3".equals(tmp.getString("PRIZE_TYPE_CODE", "")))
            {
                result.put("RSRV_NUM3", tmp.getString("MAX_VALUE", "0"));
            }
            else if ("4".equals(tmp.getString("PRIZE_TYPE_CODE", "")))
            {
                result.put("RSRV_NUM4", tmp.getString("MAX_VALUE", "0"));
            }
            else if ("5".equals(tmp.getString("PRIZE_TYPE_CODE", "")))
            {
                result.put("RSRV_NUM5", tmp.getString("MAX_VALUE", "0"));
            }
        }

        idset = MobileLotteryActiveQry.queryLotteryInfo(data.getString("SERIAL_NUMBER"), data.getString("USER_ID"), data.getString("MONTH"), data.getString("PRIZE_TYPE_CODE"), data.getString("DEAL_TAG"), data.getString("START_DATE"), data
                .getString("END_DATE"), null);

        if (IDataUtil.isNotEmpty(result))
        {
            if (IDataUtil.isNotEmpty(idset))
            {
                IData infoData = new DataMap();
                infoData = idset.getData(0);
                infoData.putAll(result);
            }
            else
            {
                idset.add(result);
            }
        }

        return idset;
    }

    public void recordHisMainTrade(IData data) throws Exception
    {

        IData iparam = new DataMap();
        String tradeId = data.getString("TRADE_ID");

        iparam.put("TRADE_ID", tradeId);

        iparam.put("BATCH_ID", "");

        iparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));

        iparam.put("ORDER_ID", "");

        iparam.put("PROD_ORDER_ID", "");

        iparam.put("BPM_ID", "");

        iparam.put("CAMPN_ID", "");

        iparam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE", ""));

        iparam.put("PRIORITY", "0");

        iparam.put("SUBSCRIBE_TYPE", "0");

        iparam.put("SUBSCRIBE_STATE", "9");

        iparam.put("NEXT_DEAL_TAG", "0");

        iparam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", "0"));

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

        iparam.put("REMARK", data.getString("LIMIT", "")); // 总数

        iparam.put("RSRV_STR1", data.getString("PARA1", "")); // 劲爆奖中奖区间

        iparam.put("RSRV_STR2", data.getString("PARA2", "")); // 一等奖中奖区间

        iparam.put("RSRV_STR3", data.getString("PARA3", "")); // 二等奖中奖区间

        iparam.put("RSRV_STR4", data.getString("PARA4", ""));// 三等奖中奖区间

        iparam.put("RSRV_STR5", data.getString("PARA5", "")); // 幸运奖区间

        iparam.put("RSRV_STR6", data.getString("RANDOM_NUM", "")); // 随机数

        iparam.put("RSRV_STR7", data.getString("LAST_DATE", "")); // 最后时间

        iparam.put("RSRV_STR8", data.getString("PRIZE_TYPE_CODE", "")); // 中奖类型

        iparam.put("RSRV_STR9", data.getString("EXEC_TIME", "")); // 领奖时间

        iparam.put("RSRV_STR10", data.getString("X_FPAY_FEE", "")); // 中奖金额

        Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INS_ALL", iparam);
    }

}
