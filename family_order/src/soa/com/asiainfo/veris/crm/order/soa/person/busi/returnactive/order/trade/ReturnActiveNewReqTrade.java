
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ReturnActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.ReturnActiveUtilBean;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.ReturnActiveNewReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.sub.ReturnActiveGGCardData;

public class ReturnActiveNewReqTrade extends BaseTrade implements ITrade
{

    /**
     * 校验用户现金存折
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     * @author wukw3
     * @date 2013-9-22
     */
    public static IData checkMoney(BusiTradeData bd, int money) throws Exception
    {

        ReturnActiveNewReqData rd = (ReturnActiveNewReqData) bd.getRD();
        // inparam.put("NET_TYPE_CODE","00");
        // inparam.put("REMOVE_TAG", "0");

        IData result = new DataMap();

        int cash = 0;// 现金账户余额
        String cashBalanceId = "";// 现金账本标识
        String depositCode = "";

        // QAM_OWEFEE_QUERY !@#
        // IDataset Infos = CSAppCall.callAcct("AM_CRM_CalcOweFee", inparam, false).getData();
        IDataset Infos = AcctCall.getCalcOweFeeByUserAcctId(rd.getUca().getUserId(), rd.getUca().getAcctId(), "1");

        IDataset depositSet = Infos.getData(0).getDataset("DEPOSIT_DATA");

        // if (null != Infos && Infos.get(0, "X_RESULTCODE").equals("0"))
        if (DataSetUtils.isNotBlank(Infos) && DataSetUtils.isNotBlank(depositSet))
        {
            for (int i = 0; i < depositSet.size(); i++)
            {
                depositCode = depositSet.get(i, "DEPOSIT_CODE", "0").toString();

                if ("0".equals(depositCode)) // 现金存折
                {
                    int cash1 = Integer.parseInt(depositSet.get(i, "X_CANUSE_VALUE", "0").toString());
                    cash = cash + cash1;
                    if ((cash > 0 && cash1 > 0) || (cash == 0 && cash1 == 0))
                    {
                        cashBalanceId = depositSet.get(i, "ACCT_BALANCE_ID", "").toString();
                    }
                }
            }
            /*
             * for(int i=0;i<Infos.size();i++) { depositCode =Infos.get(i, "DEPOSIT_CODE","0").toString();
             * if("0".equals(depositCode)) //现金存折 { int cash1 =Integer.parseInt(Infos.get(i,
             * "ODD_MONEY","0").toString())+Integer.parseInt(Infos.get(i, "EVEN_MONEY","0").toString());
             * cash=cash+cash1; if((cash>0&&cash1>0)||(cash==0&&cash1==0)) { cashBalanceId=Infos.get(i,
             * "ACCT_BALANCE_ID","").toString(); } } }
             */
        }
        else
        {
            // common.error("658625:获取现金账户余额出错！");
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_12, "获取现金账户余额出错！");
        }

        result.put("ACCT_BALANCE_ID", cashBalanceId);
        if (money <= cash)
        {
            result.put("MONEY", money);
        }
        else
        {
            result.put("MONEY", cash);
        }

        return result;
    }

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        ReturnActiveNewReqData reqData = (ReturnActiveNewReqData) bd.getRD();
        UcaData uca = reqData.getUca();

        String in_mode = reqData.getInMode();
        String doMode = reqData.getDoMode();
        List<ReturnActiveGGCardData> cardList = reqData.getCardList();

        if (in_mode.equals("1"))
        {
            reqData.setPreType("true");
        }

        if (StringUtils.isBlank(doMode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "办理方式");
        }
        if (cardList.isEmpty() && in_mode.equals("")) // 接口不需要传领卡数量
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "领取刮刮卡列表");
        }

        IData returndata = new DataMap();

        // if("0".equals(doMode)){
        // this.doZzModeTrade(reqData); //转账方式办理台账处理
        // }
        // ----------------预存方式办理-------------------
        // else
        if ("1".equals(doMode))
        {
            returndata = doYcModeTrade(bd);
        }
        else if ("2".equals(doMode))
        { // 缴费兑换办理方式
            returndata = doPayModeTrade(bd);
        }
        else if ("3".equals(doMode))
        { // 积分兑换办理方式
            returndata = doScoreModeTrade(bd);
        }
        else if ("4".equals(doMode))
        { // 入网有礼
            returndata = doJoinModeTrade(bd);
        }
        else if ("5".equals(doMode))
        { // 流量王领卡
            returndata = genGetGGCardNumTradeOther(bd);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_695, "办理方式传入不正确");
        }

        String acceptNum = returndata.getString("ACCEPTNUM", ""); // 实际领卡数量
        String haveNum = returndata.getString("HAVENUM", ""); // 可领卡数量

        MainTradeData mainTD = bd.getMainTradeData();
        mainTD.setRsrvStr1(doMode);
        if (doMode.equals("3") && "2".equals(in_mode))
        { // 判断接口 1校验 2办理
            // 接口办理不领卡
            mainTD.setRsrvStr2("0");
        }
        else
        {
            mainTD.setRsrvStr2(acceptNum);
        }
        mainTD.setRsrvStr3(haveNum);

        if (StringUtils.isBlank(in_mode))
        { // 前台办理需要登记other台帐
            createTradeOther(bd, returndata);
        }
    }

    /**
     * 生成其他子表台账（领卡）
     * 
     * @param pd
     * @param td
     * @throws Exception
     * @author chenzg
     * @date 2013-1-5
     */
    public void createTradeOther(BusiTradeData bd, IData inparam) throws Exception
    {

        ReturnActiveNewReqData rd = (ReturnActiveNewReqData) bd.getRD();
        List<ReturnActiveGGCardData> ds = rd.getCardList(); // 领卡数据

        String tradeId = bd.getTradeId();

        // REQ201308160011关于花积分、存话费抽大奖活动的需求@add by wukw3----------------------start--------------------
        String doMode = rd.getDoMode();

        // REQ201308160011关于花积分、存话费抽大奖活动的需求@add by wukw3----------------------end----------------------
        for (int i = 0, size = ds.size(); i < size; i++)
        {
            OtherTradeData tradeOtherData = new OtherTradeData();

            ReturnActiveGGCardData GGData = ds.get(i);
            tradeOtherData.setRsrvValueCode("RAGGCARD");
            tradeOtherData.setRsrvValue("刮刮卡领奖");
            tradeOtherData.setUserId(rd.getUca().getUserId());

            tradeOtherData.setStartDate(rd.getAcceptTime());
            tradeOtherData.setEndDate(SysDateMgr.getTheLastTime());
            tradeOtherData.setStaffId(CSBizBean.getVisit().getStaffId());
            tradeOtherData.setDepartId(CSBizBean.getVisit().getDepartId());
            tradeOtherData.setRsrvStr1(GGData.getGgCardNo());
            tradeOtherData.setRsrvStr2(tradeId);
            tradeOtherData.setRsrvStr4(doMode);
            tradeOtherData.setInstId(SeqMgr.getInstId());

            // 关于花积分、存话费抽大奖活动的需求
            if ("2".equals(doMode))
            {
                // tradeOtherData.put("RSRV_STR30", checkPayData.getString("CHARGEIDLIST"));
                tradeOtherData.setRsrvStr30(inparam.getString("CHARGEIDLIST"));
            }
            // 流量王活动记录台帐
            if ("4".equals(doMode))
            {
                tradeOtherData.setRsrvStr30("SALEGGCRAD");
            }

            // 关于花积分、存话费抽大奖活动的需求
            tradeOtherData.setProcessTag("0");
            tradeOtherData.setRemark(GGData.getRemark());
            tradeOtherData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            bd.add(rd.getUca().getSerialNumber(), tradeOtherData);

        }

        resCardModify(bd);// 资源状态修改
    }

    /**
     * 生成积分子表台账（积分减扣）
     * 
     * @param pd
     * @param td
     * @throws Exception
     * @author wukw3
     * @date 2013-9-9
     */
    public void createTradeScore(BusiTradeData bd, IData checkScoreData) throws Exception
    {

        ReturnActiveNewReqData rd = (ReturnActiveNewReqData) bd.getRD();
        // IData tempScore = new DataMap();
        ScoreTradeData scoretradedata = new ScoreTradeData();

        int acceptNum = checkScoreData.getInt("acceptNum"); // 实际领卡数量
        int score_changed = acceptNum * checkScoreData.getInt("LIMITSCORE");
        int value_changed = checkScoreData.getInt("SCORE_VALUE") - score_changed;

        scoretradedata.setUserId(rd.getUca().getUserId());
        scoretradedata.setSerialNumber(rd.getUca().getSerialNumber());
        scoretradedata.setIdType("0");
        scoretradedata.setScoreTypeCode("ZZ");
        scoretradedata.setYearId("ALL");
        scoretradedata.setStartCycleId("-1");
        scoretradedata.setEndCycleId("-1");
        scoretradedata.setScore(checkScoreData.getString("SCORE_VALUE"));
        scoretradedata.setScoreChanged(String.valueOf(-score_changed));
        scoretradedata.setScoreTag("1");
        scoretradedata.setRuleId("");
        scoretradedata.setGoodsName("积分办理方式");
        scoretradedata.setCancelTag("0");
        scoretradedata.setRemark("");
        scoretradedata.setRsrvStr3(checkScoreData.getString("HAVE_NUM_SCORE"));
        // td.addTradeDetailSeg(td.getTradeId(), tempScore, X_TRADE_DATA.X_TRADE_SCORE); // 积分台帐
        bd.add(rd.getUca().getSerialNumber(), scoretradedata);

        // 调用积分扣减接口 !@# 使用action
        /*
         * if(!rd.getInMode().equals("1")){ //接口校验不需要调接口 //HTSM_ScoreAdjust AcctCall.userScoreModify(uca.getUserId(),
         * "ALL", "ZZ", bd.getTradeTypeCode(),-score_changed, bd.getTradeId()); }
         */
    }

    /**
     * 入网有礼
     * 
     * @param bd
     * @param checkScoreData
     * @throws Exception
     */
    public IData doJoinModeTrade(BusiTradeData bd) throws Exception
    {

        ReturnActiveNewReqData rd = (ReturnActiveNewReqData) bd.getRD();
        int acceptNum = rd.getCardList().size();
        IData checkJoinData = ReturnActiveUtilBean.checkJoinActiveTrade(rd.getUca().getUserId());
        int haveNum = checkJoinData.getInt("HAVE_NUM_JOIN");
        if (acceptNum != haveNum)
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_12, acceptNum, haveNum);
        }
        genJoinTradeActive(bd);

        IData returnData = new DataMap();
        returnData.put("ACCEPTNUM", rd.getCardList().size());
        returnData.put("HAVENUM", checkJoinData.getString("HAVE_NUM_JOIN"));

        return returnData;
    }

    /*
     * 其他缴费渠道办理
     */
    private IData doPayModeTrade(BusiTradeData bd) throws Exception
    {

        ReturnActiveNewReqData rd = (ReturnActiveNewReqData) bd.getRD();
        IData inparam = new DataMap();
        inparam.put("USER_ID", rd.getUca().getUserId());
        inparam.put("SERIAL_NUMBER", rd.getUca().getSerialNumber());
        IData checkPayData = ReturnActiveUtilBean.checkPayReturnActiveTrade(inparam);

        int haveNum = checkPayData.getInt("HAVE_NUM_PAY"); // 可领卡数量

        int acceptNum = rd.getCardList().size(); // 实际领卡数量
        if (rd.getInMode().equals("2"))
        { // 接口领卡
            acceptNum = Integer.parseInt(rd.getAcceptNum());
        }
        String ALL_ACCT_BALANCE_ID = checkPayData.getString("ALL_ACCT_BALANCE_ID"); // 存折串
        String ALL_MONEY = checkPayData.getString("ALL_MONEY"); // 钱数串
        if (acceptNum != haveNum)
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_12, acceptNum, haveNum);
        }

        MainTradeData mainTrade = bd.getMainTradeData();
        // mainTrade.setRsrvStr2(String.valueOf(acceptNum));
        // mainTrade.setRsrvStr3(String.valueOf(haveNum));
        mainTrade.setRsrvStr4(ALL_ACCT_BALANCE_ID);
        mainTrade.setRsrvStr5(ALL_MONEY);
        IData moneyData = checkMoney(bd, haveNum * 5000);
        // IData moneyData = new DataMap();

        // 模拟数据
        /*
         * moneyData.put("MONEY", 3213); moneyData.put("ACCT_BALANCE_ID", 5435345);
         */

        mainTrade.setRsrvStr6(moneyData.getString("MONEY"));
        mainTrade.setRsrvStr7(moneyData.getString("ACCT_BALANCE_ID"));

        if (moneyData.getInt("MONEY") > 0)
        {
            // 登记调账务转账接口 !@# TAM_TRANS_FEE 使用action
            // AcctCall.transFee(bd.getTradeId(), moneyData.getString("MONEY"), moneyData.getString("ACCT_BALANCE_ID"),
            // rd.getUca());
            OtherFeeTradeData feeData = new OtherFeeTradeData();
            feeData.setUserId(rd.getUca().getUserId());
            feeData.setUserId2(rd.getUca().getUserId());
            feeData.setOutDepositCode("0");
            feeData.setInDepositCode("187");
            feeData.setOperFee(moneyData.getString("MONEY"));
            feeData.setRemark(rd.getRemark());
            feeData.setOperType(BofConst.OTHERFEE_SAME_TRANS);
            feeData.setPaymentId("-1");
            // feeData.setOutDepositCode();
            // AcctCall.TransFee(outUserId, inUserId, outDepositeCode, inDepositeCode, fee, remark);
            bd.add(rd.getUca().getSerialNumber(), feeData);

        }

        IData returnData = new DataMap();
        returnData.put("ACCEPTNUM", acceptNum);
        returnData.put("HAVENUM", haveNum);
        returnData.put("CHARGEIDLIST", checkPayData.getString("CHARGEIDLIST"));

        return returnData;

    }

    /*
     * 积分办理
     */
    private IData doScoreModeTrade(BusiTradeData bd) throws Exception
    {

        ReturnActiveNewReqData rd = (ReturnActiveNewReqData) bd.getRD();
        IData inparam = new DataMap();
        inparam.put("USER_ID", rd.getUca().getUserId());
        inparam.put("SERIAL_NUMBER", rd.getUca().getSerialNumber());
        IData checkScoreData = ReturnActiveUtilBean.checkScoreReturnActiveTrade(inparam);

        int haveNum = checkScoreData.getInt("HAVE_NUM_SCORE"); // 可领卡数量
        int acceptNum = rd.getCardList().size(); // 实际领卡数量
        if (rd.getInMode().equals("2"))
        { // 接口领卡
            acceptNum = Integer.parseInt(rd.getAcceptNum());
        }
        // int newhaveNum= Integer.parseInt(checkData.get("HAVE_NUM_SCORE", "0").toString());
        String SCORE_VALUE = checkScoreData.getString("SCORE_VALUE");
        if (acceptNum > haveNum)
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_13, acceptNum, haveNum);
        }

        checkScoreData.put("acceptNum", acceptNum);
        createTradeScore(bd, checkScoreData);

        IData returnData = new DataMap();

        returnData.put("ACCEPTNUM", acceptNum);
        returnData.put("HAVENUM", haveNum);

        return returnData;
    }

    /*
     * 直接预存办理
     */
    private IData doYcModeTrade(BusiTradeData bd) throws Exception
    {

        ReturnActiveNewReqData rd = (ReturnActiveNewReqData) bd.getRD();
        // int haveNum = rd.getAccept_num_yc();

        int haveNum = Integer.parseInt(bd.getAdvanceFee()) / ReturnActiveUtilBean.FINAL_LIMIT_MONEY;// 可领卡数量

        int acceptNum = rd.getCardList().size(); // 实际领卡数量
        if (rd.getInMode().equals("2"))
        { // 接口领卡
            acceptNum = Integer.parseInt(rd.getAcceptNum());
        }
        // int totalFee = haveNum*ReturnActiveUtilBean.FINAL_LIMIT_MONEY; //总费用
        if (acceptNum != haveNum)
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_12, acceptNum, haveNum);
        }
        IData returnData = new DataMap();
        returnData.put("ACCEPTNUM", acceptNum);
        returnData.put("HAVENUM", haveNum);

        return returnData;
    }

    /**
     * 流量王活动记录台帐
     * 
     * @param bd
     * @throws Exception
     */
    public IData genGetGGCardNumTradeOther(BusiTradeData bd) throws Exception
    {

        ReturnActiveNewReqData reqData = (ReturnActiveNewReqData) bd.getRD();
        IData flowActiveinfo = ReturnActiveUtilBean.getSaleactiveGGCrad(reqData.getUca().getUserId());
        IDataset flowActiveinfos = flowActiveinfo.getDataset("activeInfos");
        int flowActive_num = flowActiveinfo.getInt("flowActive_num", 0);
        int acceptNum = reqData.getCardList().size(); // 实际领卡数量
        if (acceptNum != flowActive_num)
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_12, acceptNum, flowActive_num);
        }
        for (int i = 0, size = flowActiveinfos.size(); i < size; i++)
        {
            IData each = flowActiveinfos.getData(i);
            OtherTradeData tradeOtherData = new OtherTradeData();

            tradeOtherData.setRsrvValueCode("GET_GGCARD_NUM");
            tradeOtherData.setRsrvValue("流量王营销活动领取刮刮卡数量");
            tradeOtherData.setUserId(reqData.getUca().getUserId());
            tradeOtherData.setStartDate(reqData.getAcceptTime());
            tradeOtherData.setEndDate(SysDateMgr.getTheLastTime());

            tradeOtherData.setRsrvDate1(each.getString("START_DATE", ""));
            tradeOtherData.setRsrvDate2(each.getString("END_DATE", ""));
            tradeOtherData.setStaffId(CSBizBean.getVisit().getStaffId());
            tradeOtherData.setDepartId(CSBizBean.getVisit().getDepartId());

            tradeOtherData.setRsrvStr1(each.getString("PARAM_CODE", "")); // 营销互动产品id
            tradeOtherData.setRsrvStr2(each.getString("PARA_CODE1", "")); // 营销活动包id
            tradeOtherData.setRsrvStr3(each.getString("PARA_CODE2", "")); // 领取数量
            tradeOtherData.setRsrvStr4(each.getString("PARA_CODE3", "")); // 活动类别：1-tf_f_user_sale_active, 2-
            // tf_f_user_sale_new
            tradeOtherData.setRsrvStr5(each.getString("RELATION_TRADE_ID", "")); // 活动相关流水
            tradeOtherData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            tradeOtherData.setInstId(SeqMgr.getInstId());
            bd.add(reqData.getUca().getSerialNumber(), tradeOtherData);

        }
        IData returnData = new DataMap();
        returnData.put("ACCEPTNUM", acceptNum);
        returnData.put("HAVENUM", flowActive_num);

        return returnData;

    }

    /**
     * 入网有礼营销活动
     * 
     * @param bd
     * @param checkScoreData
     * @throws Exception
     */
    public void genJoinTradeActive(BusiTradeData bd) throws Exception
    {

        ReturnActiveNewReqData rd = (ReturnActiveNewReqData) bd.getRD();
        IDataset tradeActiveDs = new DatasetList();
        String userId = rd.getUca().getUserId();
        String doMode = rd.getDoMode();
        if (doMode.equals("4"))
        {
            IDataset userOtherData = ReturnActiveUtilBean.getJoinActive(userId);
            if (DataSetUtils.isNotBlank(userOtherData))
            {
                for (int i = 0, s = userOtherData.size(); i < s; i++)
                {
                    IData tmp = userOtherData.getData(i);
                    tmp.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                    tmp.put("UPDATE_DEPART_ID", getVisit().getDepartId());
                    tmp.put("UPDATE_TIME", SysDateMgr.getSysDate());
                    tmp.put("RSRV_TAG3", "1");// 已经领取卡
                    tradeActiveDs.add(tmp);
                }
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "558636：用户的活动信息已修改，请刷新资料后再办理!");
            }
            ReturnActiveUtilBean.updateSaleActive(tradeActiveDs);
        }

    }

    public int resCardModify(BusiTradeData bd) throws Exception
    {
        // ReturnActiveGet
        ReturnActiveNewReqData rd = (ReturnActiveNewReqData) bd.getRD();

        List<ReturnActiveGGCardData> ds = rd.getCardList(); // 领卡数据
        IData param = new DataMap();

        for (int i = 0; i < ds.size(); i++)
        {
            ReturnActiveGGCardData GGData = ds.get(i);
            param.put("RES_TYPE_CODE", "3");
            param.put("PARA_CODE1", GGData.getGgCardNo());
            param.put("PARA_CODE2", GGData.getGgCardNo());
            param.put("PARA_VALUE1", "");
            param.put("PARA_VALUE2", "");
            param.put("PARA_VALUE3", "");
            param.put("PARA_VALUE4", "");
            param.put("PARA_VALUE5", "");
            param.put("PARA_VALUE6", "");
            param.put("PARA_VALUE7", "");
            param.put("PARA_VALUE10", "");
            param.put("RES_NO", "");
            param.put("SUBSYS_CODE", "");
            param.put("INTRADE_ID", "");
            param.put("RES_TRADE_CODE", "IValueCardModifyState");
            param.put("SALE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("X_GETMODE", 1);
            param.put("PARA_VALUE8", "0");
            param.put("PARA_VALUE9", "1");
            param.put("TRADE_TYPE_CODE", "428");

            // 调用资源修改接口 !@#
            ResCall.modifyVitualCardSaleInfo(GGData.getGgCardNo(), GGData.getGgCardNo(), getVisit().getDepartId(), rd.getUca().getUserEparchyCode(), getTradeEparchyCode(), "1", "428");

        }
        return 0;
    }

}
