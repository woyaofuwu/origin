
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ReturnActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class ReturnActiveUtilBean extends CSBizBean
{

    public static final int FINAL_LIMIT_MONEY = 5000; // 单位分

    public static final int FINAL_LIMIT_SCORE = 200; // 积分

    /**
     * 入网领卡校验
     * 
     * @param userid
     * @return
     * @throws Exception
     */
    public static IData checkJoinActiveTrade(String userId) throws Exception
    {

        IData outData = new DataMap();
        int haveNum = 0;
        IDataset results = getJoinActive(userId);

        if (DataSetUtils.isNotBlank(results))
        {
            for (int i = 0, s = results.size(); i < s; i++)
            {
                IData tmp = results.getData(i);
                String packageId = tmp.getString("PACKAGE_ID");
                if (packageId.equals("60010175"))
                    haveNum += 1;
                else if (packageId.equals("60010176"))
                    haveNum += 2;
                else if (packageId.equals("60010177"))
                    haveNum += 4;
            }
        }
        outData.put("HAVE_NUM_JOIN", String.valueOf(haveNum));
        return outData;
    }

    /**
     * 根据缴费记录校验用户是否可以参加活动
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     * @author xiaobin
     * @date 2013-9-5
     */
    public static IData checkPayReturnActiveTrade(IData data) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("USER_ID", data.getString("USER_ID"));
        String begin_time = SysDateMgr.getYesterdayDate();
        String end_time = SysDateMgr.getSysDate();
        begin_time = SysDateMgr.suffixDate(begin_time, 0);
        end_time = SysDateMgr.suffixDate(end_time, 1);
        inparam.put("BEGIN_TIME", begin_time);
        inparam.put("END_TIME", end_time);
        inparam.put("CANCEL_TAG", "0");

        IData outData = new DataMap();

        int limitMonry = FINAL_LIMIT_MONEY;

        int allCount = 0;// 可以兑换次数
        int intCount = 0;// 单笔缴费记录兑换
        String chargeIdList = "";
        IDataset payDataset = new DatasetList();

        IDataset Infos = new DatasetList();
        // IDataset Infos = TuxedoHelper.callTuxSvc(pd, "QAM_PAYLOG_BY_USERID",inparam); !@# 账务接口
        Infos = AcctCall.querySnPaymentPayLogInfo(data.getString("SERIAL_NUMBER"));

        if (Infos != null)
        {
            for (int i = 0; i < Infos.size(); i++)
            {

                IData fee = Infos.getData(i);
                String strRecvFee = fee.getString("RECV_FEE");
                if (strRecvFee != null)
                {
                    int RECV_FEE = Integer.parseInt(strRecvFee);

                    if (RECV_FEE >= limitMonry)
                    {
                        IData inparam1 = new DataMap();
                        // 判断是不是有效缴费记录,payment_id/pay_fee_mode_code
                        // 分别是42/0，37/19，1/0，1/6，1/20，1/21，1/22，3/6，然后过滤网厅缴费和手机支付用户，RECV_STAFF_ID 等于 ('ITFWEB00',
                        // 'ITFWEB02', 'ITFLD000','IBOSS000')为网厅、手机支付用户缴费
                        inparam1.clear();
                        inparam1.put("SUBSYS_CODE", "CSM");
                        inparam1.put("PARAM_ATTR", "101");
                        inparam1.put("PARA_CODE1", fee.getString("PAYMENT_ID", "-1"));
                        inparam1.put("PARA_CODE2", fee.getString("PAY_FEE_MODE_CODE", "-1"));
                        // IDataset results1 = dao.queryListByCodeCodeParser("TD_S_COMMPARA", "SEL_BY_PARA_CODE3",
                        // inparam1);
                        IDataset results1 = CommparaInfoQry.getInfoParaCode1_2("CSM", "101", fee.getString("PAYMENT_ID", "-1"), fee.getString("PAY_FEE_MODE_CODE", "-1"));

                        if (DataSetUtils.isNotBlank(results1))
                        {
                            inparam1.clear();
                            IDataset results2 = CommparaInfoQry.getInfoParaCode1_2("CSM", "102", fee.getString("RECV_STAFF_ID", "-1"), null);

                            if (results2 == null || results2.size() <= 0)
                            {
                                // 判断是否用过
                                inparam1.clear();
                                // inparam1.put("USER_ID", td.getUserId());
                                // inparam1.put("RSRV_VALUE_CODE", "RAGGCARD");
                                // inparam1.put("RSRV_STR30", fee.getString("CHARGE_ID"));
                                // IDataset results3 = dao.queryListByCodeCode("TF_F_USER_OTHER", "SEL_BY_USERID_RSRV3",
                                // inparam1);
                                IDataset results3 = UserOtherInfoQry.getInfoByRsrv3(inparam.getString("USER_ID"), "RAGGCARD", fee.getString("CHARGE_ID"));

                                if (DataSetUtils.isBlank(results3))
                                {
                                    intCount = RECV_FEE / limitMonry;
                                    allCount += intCount;
                                    fee.put("COUNT", intCount);
                                    if (chargeIdList == "")
                                    {
                                        chargeIdList = fee.getString("CHARGE_ID");
                                    }
                                    else
                                    {
                                        chargeIdList = chargeIdList + "," + fee.getString("CHARGE_ID");
                                    }
                                    // 转账处理
                                    String acctBalanctId = results1.getData(0).getString("PARA_CODE3");
                                    if (acctBalanctId == null)
                                    {
                                        // common.error("855004", "取PAYMENT_ID = " + fee.getString("PAYMENT_ID","-1") +
                                        // "转账存折的时候有问题！");
                                        CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_10, fee.getString("PAYMENT_ID", "-1"));
                                    }
                                    IData payData = new DataMap();
                                    payData.put("MONEY", intCount * limitMonry);
                                    payData.put("ACCT_BALANCE_ID", acctBalanctId);
                                    boolean flag = false;
                                    if (payDataset == null || payDataset.size() <= 0)
                                    {
                                        payDataset.add(0, payData);
                                    }
                                    for (int j = 0; j < payDataset.size(); j++)
                                    {
                                        if (acctBalanctId.equals(payDataset.getData(j).getString("ACCT_BALANCE_ID")))
                                        {
                                            flag = true;
                                            // 同样的存折叠加
                                            int tmp = Integer.parseInt(payDataset.getData(j).getString("ACCT_BALANCE_ID")) + Integer.parseInt(payData.getString("MONEY"));
                                            payDataset.getData(j).put("MONEY", tmp);
                                        }
                                    }
                                    if (!flag)
                                    {
                                        payDataset.add(payDataset.size(), payData);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_11);
        }

        String ALL_ACCT_BALANCE_ID = "";
        String ALL_MONEY = "";
        if (payDataset != null && payDataset.size() > 0)
        {
            for (int i = 0; i < payDataset.size(); i++)
            {
                if (ALL_ACCT_BALANCE_ID == "")
                {
                    ALL_ACCT_BALANCE_ID = payDataset.getData(i).getString("ACCT_BALANCE_ID");
                    ALL_MONEY = payDataset.getData(i).getString("ALL_MONEY");
                }
                else
                {
                    ALL_ACCT_BALANCE_ID = ALL_ACCT_BALANCE_ID + "," + payDataset.getData(i).getString("ACCT_BALANCE_ID");
                    ALL_MONEY = ALL_MONEY + "," + payDataset.getData(i).getString("ALL_MONEY");
                }
            }
        }

        outData.put("ALLCOUNT", allCount);
        outData.put("LIMITMONRY", limitMonry);
        outData.put("CHARGEIDLIST", chargeIdList);

        outData.put("HAVE_NUM_PAY", allCount <= 0 ? 0 : allCount);

        outData.put("ALL_ACCT_BALANCE_ID", ALL_ACCT_BALANCE_ID);
        outData.put("ALL_MONEY", ALL_MONEY);

        return outData;
    }

    /*
     * 校验用户是否可以参加活动 (停用)
     */
    public static IData checkReturnActiveTrade(IData inparam) throws Exception
    {

        IData data = new DataMap();
        inparam.put("NET_TYPE_CODE", "00");
        inparam.put("REMOVE_TAG", "0");
        inparam.put("SERIAL_NUMBER", data.getString(""));

        IData outData = new DataMap();

        int limitMoney = FINAL_LIMIT_MONEY;

        int cash = 0;// 现金账户余额
        int card = 0;// 充值卡账户余额
        int phone = 0;// 手机支付存折账户余额
        int all_new_balance = 0;// 实时结余
        String depositCode = "";
        String cashBalanceId = "";// 现金账本标识
        String cardBalanceId = "";// 充值卡账本标识
        String phoneBalanceId = "";// 手机支付存折账本标识
        // IDataset Infos = TuxedoHelper.callTuxSvc(pd, "QAM_OWEFEE_QUERY",inparam); !@#
        IDataset Infos = CSAppCall.callAcct("AM_CRM_QueryRealFee", inparam, false).getData();

        if (null != Infos && Infos.get(0, "X_RESULTCODE").equals("0"))
        {
            for (int i = 0; i < Infos.size(); i++)
            {
                depositCode = Infos.get(i, "DEPOSIT_CODE", "0").toString();
                if ("0".equals(depositCode)) // 现金存折
                {
                    int cash1 = Integer.parseInt(Infos.get(i, "ODD_MONEY", "0").toString()) + Integer.parseInt(Infos.get(i, "EVEN_MONEY", "0").toString());
                    cash = cash + cash1;
                    if ((cash > 0 && cash1 > 0) || (cash == 0 && cash1 == 0))
                    {
                        cashBalanceId = Infos.get(i, "ACCT_BALANCE_ID", "").toString();
                    }
                }
                else if ("1".equals(depositCode)) // 缴费卡存折
                {
                    int card1 = Integer.parseInt(Infos.get(i, "ODD_MONEY", "0").toString()) + Integer.parseInt(Infos.get(i, "EVEN_MONEY", "0").toString());
                    card = card + card1;
                    if ((card > 0 && card1 > 0) || (card == 0 && card1 == 0))
                    {
                        cardBalanceId = Infos.get(i, "ACCT_BALANCE_ID", "").toString();
                    }
                }
                else if ("184".equals(depositCode)) // 手机支付存折(184)
                {
                    int phone1 = Integer.parseInt(Infos.get(i, "ODD_MONEY", "0").toString()) + Integer.parseInt(Infos.get(i, "EVEN_MONEY", "0").toString());
                    phone = phone + phone1;
                    if ((phone > 0 && phone1 > 0) || (phone == 0 && phone1 == 0))
                    {
                        phoneBalanceId = Infos.get(i, "ACCT_BALANCE_ID", "").toString();
                    }
                }
            }

            all_new_balance = Integer.parseInt(Infos.get(0, "ALL_NEW_BALANCE", "0").toString());
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取现金、充值卡账户余额出错！");
        }

        outData.put("CASH_ACCT_BALANCE_ID", cashBalanceId);
        outData.put("CARD_ACCT_BALANCE_ID", cardBalanceId);
        outData.put("PHONE_ACCT_BALANCE_ID", phoneBalanceId);
        outData.put("CASH", cash);
        outData.put("CARD", card);
        outData.put("PHONE", phone);
        outData.put("LIMITMONEY", limitMoney);
        int haveNum = 0;
        /*
         * if((cash+card+phone)<all_new_balance) {
         */
        if (all_new_balance > 0)
        {
            haveNum = (cash + card + phone) / limitMoney;
        }

        outData.put("HAVE_NUM", haveNum <= 0 ? 0 : haveNum);
        return outData;
    }

    /**
     * 规则校验：只能是同类型、同面值的卡的刮刮卡
     * 
     * @param dataset1
     * @throws Exception
     */
    public static void checkRule(IDataset dataset1) throws Exception
    {
        String kindCode = dataset1.get(0, "RES_KIND_CODE").toString();
        // String valueCode = dataset1.get(0, "VALUE_CODE").toString();
        // String cardKindCode = dataset1.get(0, "CARD_KIND_CODE")==null?"":dataset1.get(0,
        // "CARD_KIND_CODE").toString();
        for (int i = 0; i < dataset1.size(); i++)
        {
            if (!kindCode.equals(dataset1.get(i, "RES_KIND_CODE")))
            {
                CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_14, dataset1.get(i, "VALUE_CARD_NO").toString());

            }
            if (!"31m".equals(dataset1.get(i, "RES_TYPE_CODE")))
            {
                CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_15, dataset1.get(i, "VALUE_CARD_NO").toString());
            }
            /*
             * if(!valueCode.equals(dataset1.get(i, "VALUE_CODE"))) {
             * CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_16, dataset1.get(i,
             * "VALUE_CARD_NO").toString()); } if(null!=dataset1.get(i, "CARD_KIND_CODE")){
             * if(!cardKindCode.equals(dataset1.get(i, "CARD_KIND_CODE"))) {
             * CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_17, dataset1.get(i,
             * "VALUE_CARD_NO").toString()); } }
             */
        }
    }

    /**
     * 根据积分记录校验用户是否可以参加活动
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     * @date 2013-9-5
     */
    public static IData checkScoreReturnActiveTrade(IData data) throws Exception
    {

        IData outData = new DataMap();

        String score_value = "0";
        int limitScore = FINAL_LIMIT_SCORE;
        int score = 0;

        // 增加积分接口调用
        try
        {
            IData score_param = new DataMap();
            score_param.put("USER_ID", data.getString("USER_ID"));
            score_param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            // IDataset dataset = (IDataset)HttpHelper.callHttpSvc(pd, "HQSM_SerialnumScore", score_param) ; !@#
            IDataset dataset = AcctCall.queryUserScore(data.getString("USER_ID"));

            if (DataSetUtils.isNotBlank(dataset))
            {
                IData score_info = dataset.getData(0);
                score_value = score_info.getString("SUM_SCORE");
                if (score_value != null)
                {
                    score = Integer.parseInt(score_value);
                }
            }
        }
        catch (Exception e)
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_20, "用户积分");
        }

        outData.put("LIMITSCORE", limitScore);
        int haveNumScore = 0;

        if (score > 0)
        {
            haveNumScore = score / limitScore;
        }

        outData.put("HAVE_NUM_SCORE", haveNumScore <= 0 ? 0 : haveNumScore);
        outData.put("SCORE_VALUE", score_value);

        // 模拟数据
        // outData.put("HAVE_NUM_SCORE", 8);
        // outData.put("SCORE_VALUE", 1700);
        //

        return outData;
    }

    /**
     * 获取入网有礼用户活动信息
     * 
     * @param userid
     * @return
     * @throws Exception
     */
    public static IDataset getJoinActive(String userId) throws Exception
    {

        IDataset results = UserSaleActiveInfoQry.querySaleActiveByUserIdPrdId(userId, "69900817");
        return results;
    }

    public static IDataset getResInfo(String tradeTypeCode, String cardCode_s, String cardCode_e) throws Exception
    {

        // IDataset dataset = TuxedoHelper.callTuxSvc(pd, "QRM_IGetResInfo", data, false); 调用资源校验接口 !@#
        IDataset dataset = ResCall.checkVirtualGGCard(cardCode_s, cardCode_e, getVisit().getDepartId(), "1", "428");

        if (dataset == null || dataset.size() == 0)
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_20, "刮刮卡信息");
        }

        checkRule(dataset);
        return dataset;

    }

    /**
     * 缴费有福活动查询活动信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData getReturnActiveNewInfo(IData data) throws Exception
    {
        IData checkPayData = checkPayReturnActiveTrade(data); // 缴费领卡校验
        IData checkScoreData = checkScoreReturnActiveTrade(data); // 预存领卡校验
        IData checkJoinData = checkJoinActiveTrade(data.getString("USER_ID"));

        IData activeInfo = new DataMap();
        // commInfo.put("HAVE_NUM",checkData.getString("HAVE_NUM", "0"));
        // REQ201308160011关于花积分、存话费抽大奖活动的需求
        activeInfo.put("HAVE_NUM_PAY", checkPayData.getString("HAVE_NUM_PAY", "0"));
        activeInfo.put("HAVE_NUM_SCORE", checkScoreData.getString("HAVE_NUM_SCORE", "0"));
        activeInfo.put("CHARGEIDLIST", checkPayData.getString("CHARGEIDLIST", ""));
        activeInfo.put("ALL_ACCT_BALANCE_ID", checkPayData.getString("ALL_ACCT_BALANCE_ID", ""));
        activeInfo.put("ALL_MONEY", checkPayData.getString("ALL_MONEY", ""));
        activeInfo.put("HAVE_NUM_SCORE", checkScoreData.getString("HAVE_NUM_SCORE"));
        activeInfo.put("SCORE_VALUE", checkScoreData.getString("SCORE_VALUE"));
        activeInfo.put("SCORE_DATA", checkScoreData);
        // REQ201308160011关于花积分、存话费抽大奖活动的需求
        activeInfo.put("HAVE_NUM_YC", "0"); // 预存可办理次数，默认为0
        // activeInfo.put("cond_DO_MODE", "1");

        // 入网有礼活动
        activeInfo.put("HAVE_NUM_JOIN", checkJoinData.getString("HAVE_NUM_JOIN"));

        // 流量王活动校验
        IData flowActiveinfo = getSaleactiveGGCrad(data.getString("USER_ID"));
        IDataset flowActiveinfos = flowActiveinfo.getDataset("activeInfos");
        int flowActive_num = flowActiveinfo.getInt("flowActive_num", 0);

        activeInfo.put("HAVE_NUM_FLOW", flowActive_num);

        IData result = new DataMap();
        result.put("RETURNACTIVE_INFO", activeInfo);
        if (DataSetUtils.isNotBlank(flowActiveinfos))
        {
            result.put("FLOWACTIVE_INFO", flowActiveinfos);
        }

        return result;
    }

    /*
     * 流量王刮刮卡活动校验
     */
    public static IData getSaleactiveGGCrad(String userid) throws Exception
    {

        // String userid = data.getString("USER_ID");
        IData flowActiveInfo = new DataMap();
        IDataset qryList = CommparaInfoQry.qrySaleactiveGGcard(userid);

        if (DataSetUtils.isBlank(qryList))
        {
            // CSAppException.apperr(CrmCommException.CRM_COMM_103, "您尚未办理可以领取刮刮卡的营销活动！");
            return flowActiveInfo;
        }
        // --------------获取已领取的卡数量------------------
        int totalHavaNum = 0;
        IDataset returnList = new DatasetList();

        for (int i = 0, size = qryList.size(); i < size; i++)
        {
            IData each = qryList.getData(i);
            IDataset ds = CommparaInfoQry.qryGotGGcardNum(userid, each.getString("PARAM_CODE", ""), each.getString("PARA_CODE1", ""));

            if (DataSetUtils.isNotBlank(ds))
            {
                int oldHaveNum = each.getInt("PARA_CODE2"); // 活动配置的可领取数量
                int havedNum = ds.getData(0).getInt("HAVED_NUM", 0); // 已领取数量
                int newHaveNum = oldHaveNum - havedNum;
                // if(newHaveNum<0){
                // newHaveNum = 0;
                // }
                // if(newHaveNum<=0){
                // qryList.remove(each);
                // i--;
                // continue;
                // }
                if (newHaveNum > 0)
                {
                    // i--;
                    each.put("PARA_CODE2", newHaveNum); // 用户实际可以领取的数量
                    each.put("PARAM_NAME", each.getString("PARAM_NAME", "") + "(" + newHaveNum + "张)");
                    totalHavaNum += newHaveNum;
                    returnList.add(each);
                }
            }
        }
        // ------------判断用户可以领卡数量是否足够----------------
        if (totalHavaNum <= 0)
        {
            // CSAppException.apperr(CrmCommException.CRM_COMM_103, "您尚未办理可以领取刮刮卡的营销活动！"); 页面合并，不能直接提示错误
            return flowActiveInfo;
        }
        // -----------提交业务办理登记台账时重新校验用户的可领卡次数--------------
        // 省去

        flowActiveInfo.put("activeInfos", returnList);
        flowActiveInfo.put("flowActive_num", totalHavaNum);
        return flowActiveInfo;
    }

    public static boolean updateSaleActive(IDataset params) throws Exception
    {

        boolean flag = true;
        for (int i = 0, s = params.size(); i < s; i++)
        {
            flag = flag && 1 == Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_SALEACTIVE_TAG3", params.getData(i)) ? true : false;
            if (!flag)
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "更新营销活动表出错！");
        }
        return flag;
    }

}
