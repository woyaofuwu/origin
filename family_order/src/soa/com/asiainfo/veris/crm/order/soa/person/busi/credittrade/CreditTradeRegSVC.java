
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.TradeCreditException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.ChangeSvcStateRegSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.DestroyUserNowRegSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.fixedtelephonedemolish.order.FixTelDemolishRegSVC;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CreditTradeRegSVC.java
 * @Description: 信控业务接口服务类（提供给信控，里面按照具体的业务类型再做分发）
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-12 上午9:46:05
 */
public class CreditTradeRegSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private static transient final Logger logger = Logger.getLogger(CreditTradeRegSVC.class);

    /**
     *将正常业务类型转换成宽带业务类型
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public static void transWidenetTradeType(IData input) throws Exception
    {
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "");
        // 信控停机\开机\欠停\缴开\欠销等业务需对宽带用户进行相同的操作 7321特开转开通
        if ("7220".equals(tradeTypeCode) || "7110".equals(tradeTypeCode) || "7101".equals(tradeTypeCode) || "7321".equals(tradeTypeCode) || "7301".equals(tradeTypeCode) || "7303".equals(tradeTypeCode) || "7240".equals(tradeTypeCode)
                || "7317".equals(tradeTypeCode))
        {
            String serialNumber = input.getString("SERIAL_NUMBER");
            IData userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
            if (IDataUtil.isEmpty(userInfo))
            {
                return;
            }
            String userId = userInfo.getString("USER_ID");
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(userId);
            if (IDataUtil.isNotEmpty(widenetInfos))
            {
                String widenetType = widenetInfos.getData(0).getString("RSRV_STR2");
                if (StringUtils.isBlank(widenetType))
                {
                    widenetType = "1";
                }
                // 如果宽带用户有拆机工单，则没有必要再发起信控工单
                IDataset destoryInfos = TradeInfoQry.getDestoryTradeWidenetInfos(userId);
                if (IDataUtil.isNotEmpty(destoryInfos))
                {
                    return;
                }

                // 手机高额半停机、高额停机、欠费停机
                if ("7220".equals(tradeTypeCode) || "7110".equals(tradeTypeCode) || "7101".equals(tradeTypeCode))
                {
                    // 手机用户欠停时，如宽带用户已报停或有报停工单，则不再发起宽带欠停工单
                    String userStateCode = userInfo.getString("USER_STATE_CODESET");
                    if ("1".equals(userStateCode) || "01".equals(userStateCode) || "5".equals(userStateCode) || "05".equals(userStateCode))
                    {
                        return;
                    }
                    IDataset svcStateInfos = TradeSvcStateInfoQry.queryTradeWidenetUserMainSvcState("0", "1", userId, "0");
                    if (IDataUtil.isNotEmpty(svcStateInfos))
                    {// 已有未执行的宽带停机工单
                        String newSvcState = svcStateInfos.getData(0).getString("STATE_CODE");
                        if ("1".equals(newSvcState) || "5".equals(newSvcState))
                        {// 存在有效的宽带报停工单，不再生成产宽带欠停工单
                            return;
                        }
                    }
                    tradeTypeCode = "7221";// GPON宽带欠费停机
                    if ("2".equals(widenetType))
                    {

                        tradeTypeCode = "7222";// ADSL宽带欠费停机

                    }
                    else if ("3".equals(widenetType))
                    {

                        tradeTypeCode = "7223";// 光纤宽带欠费停机

                    }
                    else if ("4".equals(widenetType))
                    {

                        tradeTypeCode = "7224";// 校园宽带欠费停机
                    }
                }
                else if ("7301".equals(tradeTypeCode) || "7303".equals(tradeTypeCode) || "7317".equals(tradeTypeCode) || "7321".equals(tradeTypeCode))// 手机缴费开机、高额开机
                // 7321
                // 特开转开通
                {
                    // 手机用户缴开时，如宽带用户已报停或有报停工单，则不再发起宽带缴开工单

                    String userStateCode = userInfo.getString("USER_STATE_CODESET");
                    if ("1".equals(userStateCode) || "01".equals(userStateCode))
                    {
                        return;
                    }
                    IDataset svcStateInfos = TradeSvcStateInfoQry.queryTradeWidenetUserMainSvcState("0", "1", userId, "0");
                    if (IDataUtil.isNotEmpty(svcStateInfos))
                    {// 已有未执行的宽带停机工单
                        String newSvcState = svcStateInfos.getData(0).getString("STATE_CODE");
                        if ("1".equals(newSvcState))
                        {// 存在有效的宽带报停工单，不再生成产宽带欠停工单
                            return;
                        }
                    }
                    // TODO 预约停机工单判段 ，j2ee没有预约工单 pboss判
                    tradeTypeCode = "7306";// GPON宽带缴费开机
                    if ("2".equals(widenetType))
                    {

                        tradeTypeCode = "7307";// ADSL宽带缴费开机

                    }
                    else if ("3".equals(widenetType))
                    {

                        tradeTypeCode = "7308";// 光纤宽带缴费开机

                    }
                    else if ("4".equals(widenetType))
                    {

                        tradeTypeCode = "7309";// 校园宽带缴费开机
                    }
                }
                else if ("7240".equals(tradeTypeCode))
                {

                    tradeTypeCode = "7241";// GPON宽带欠费销号
                    if ("2".equals(widenetType))
                    {

                        tradeTypeCode = "7242";// ADSL宽带欠费销号

                    }
                    else if ("3".equals(widenetType))
                    {

                        tradeTypeCode = "7243";// FTTH宽带欠费销号

                    }
                    else if ("4".equals(widenetType))
                    {

                        tradeTypeCode = "7244";// 校园宽带宽带欠费销号
                    }
                }

                // REQ201212070013关联宽带欠停和缴开优化
                if ("7221".equals(tradeTypeCode) || "7222".equals(tradeTypeCode) || "7223".equals(tradeTypeCode) || "7224".equals(tradeTypeCode) || "7306".equals(tradeTypeCode) || "7307".equals(tradeTypeCode) || "7308".equals(tradeTypeCode)
                        || "7309".equals(tradeTypeCode))
                {

                    IDataset userSvcstateInfos = UserSvcStateInfoQry.getUserValidSvcStateByUserId(userId);
                    String userSvcState = "";
                    for (int i = 0, size = userSvcstateInfos.size(); i < size; i++)
                    {
                        userSvcState += userSvcstateInfos.getData(i).getString("STATE_CODE");
                    }

                    // 如果宽带用户已经是欠停状态，手机发起欠费停机，直接返回，不再发起宽带欠费停机
                    if (("7221".equals(tradeTypeCode) || "7222".equals(tradeTypeCode) || "7223".equals(tradeTypeCode) || "7224".equals(tradeTypeCode)) && "5".equals(userSvcState))
                    {
                        return;
                    }
                    // 如果宽带用户已经是开通状态，手机发起缴费开机，直接返回，不再发起宽带缴费开机
                    if (("7306".equals(tradeTypeCode) || "7307".equals(tradeTypeCode) || "7308".equals(tradeTypeCode) || "7309".equals(tradeTypeCode)) && "0".equals(userSvcState))
                    {
                        return;
                    }

                }

                input.put("SERIAL_NUMBER", "KD_" + serialNumber);
                input.put("USER_ID", userId);
                input.put("TRADE_TYPE_CODE", tradeTypeCode);
            }
            else
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_22);
            }
        }

    }

    /**
     *宽带信控处理
     * 
     * @author chenzm
     * @param input
     * @throws Exception
     */
    public static void widenetTradeReg(IData input) throws Exception
    {
        transWidenetTradeType(input);
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        if (StringUtils.equals(tradeTypeCode, "7306") // GPON宽带缴费开机
                || StringUtils.equals(tradeTypeCode, "7307") // ADSL宽带缴费开机
                || StringUtils.equals(tradeTypeCode, "7308") // 光纤宽带欠费停机
                || StringUtils.equals(tradeTypeCode, "7309")// 校园宽带缴费开机
                || StringUtils.equals(tradeTypeCode, "7221") // GPON宽带欠费停机
                || StringUtils.equals(tradeTypeCode, "7222") // ADSL宽带欠费停机
                || StringUtils.equals(tradeTypeCode, "7223") // 光纤宽带欠费停机
                || StringUtils.equals(tradeTypeCode, "7224")// 校园宽带欠费停机
        )
        {
            CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", input);

        }
        else if (StringUtils.equals(tradeTypeCode, "7241") // GPON宽带欠费拆机
                || StringUtils.equals(tradeTypeCode, "7242") // ADSL宽带欠费拆机
                || StringUtils.equals(tradeTypeCode, "7243") // 光纤宽带欠费拆机
                || StringUtils.equals(tradeTypeCode, "7244")) // 校园宽带欠费拆机
        {
            CSAppCall.call("SS.DestroyWidenetUserNowRegSVC.tradeReg", input);
        }
    }

    // 入参检查
    private void checkParams(IData input) throws Exception
    {
        String tradeType = input.getString("TRADE_TYPE_CODE", "");
        if (StringUtils.isEmpty(tradeType))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_230);
        }

        String userId = input.getString("USER_ID", "");
        if (StringUtils.isEmpty(userId))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_901);
        }

        String serialNumber = input.getString("SERIAL_NUMBER", "");
        if (StringUtils.isEmpty(serialNumber))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_88);
        }
    }

    /**
     * 信控业务流量控制
     * 
     * @param input
     * @throws Exception
     */
    private void creditTradeControl(String tradeTypeCode) throws Exception
    {
        // 缴费开机、缴费复机不控制
        if (StringUtils.equals("7301", tradeTypeCode) || StringUtils.equals("7302", tradeTypeCode))
            return;

        int configCnt = BizEnv.getEnvInt("credit.control.trade.cnt", 0);
        if (configCnt <= 0)
            return;// 没有配置则不判断

        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        String memCahceKey = CacheKey.getCreditTradeCtrl(tradeEparchyCode);
        Object needControlObj = SharedCache.get(memCahceKey);
        boolean flag = false;
        if (null == needControlObj)// 没有数据
        {
            // 查询配置的预警工单数 和 缓存刷新时间
            int refreshTime = BizEnv.getEnvInt("credit.control.cache.time", 1200);// 单位秒
            int unFinishCnt = TradeInfoQry.qryUnFinishTradeCount();
            if (unFinishCnt >= configCnt)// 当前未完工单大于配置数据量了
            {
                flag = true;
            }
            SharedCache.set(memCahceKey, flag, refreshTime);
        }
        else
        {
            flag = (Boolean) needControlObj;
        }

        if (flag)// 大于控制的数据量
        {
            CSAppException.apperr(TradeCreditException.CRM_TRADECREDIT_999);
        }

    }

    // 信控业务类型分发
    // {SERIAL_NUMBER=["15912511771"], USER_ID=["7108020214040434"], TRADE_STAFF_ID=["0000"],
    // TRADE_ID=["7109072382656414"], TRADE_TYPE_CODE=["7230"], TRADE_CITY_CODE=["0000"],
    // TRADE_DEPART_ID=["0000"], TRADE_EPARCHY_CODE=["0871"], ROUTE_EPARCHY_CODE=["0731"],
    public IDataset tradeReg(IData input) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>进入tradeReg入参为：>>>>>>>>>>>>>>>>>>>>>>" + input.toString());
        }

        this.checkParams(input);// 入参检查
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE", "");
        this.creditTradeControl(tradeTypeCode);// 流量控制

        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        // 查询tradeTypeCode信息
        String tradeType = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode, tradeEparchyCode);
        if (StringUtils.isBlank(tradeType))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_325, tradeTypeCode, tradeEparchyCode, input.toString());// 业务类型[%d]不存在
        }

        // 查询用户信息
        String userId = input.getString("USER_ID", "");
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(input.getString("USER_ID"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_180, userId);// 根据用户编码[%s]，查找用户资料不存在！
        }

        // 查询客户信息
        String custId = userInfo.getString("CUST_ID");
        IData custSet = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isEmpty(custSet))
        {
            CSAppException.apperr(CustException.CRM_CUST_111);// 查找客户资料无数据
        }

        IDataset retInfos = null;
        String custType = custSet.getString("CUST_TYPE", "");// 0=个人客户 1=集团客户
        // 判断客户类型
        if (StringUtils.equals(custType, "1") || StringUtils.equals(tradeTypeCode, "7130"))// 集团用户
        {
            // 由集团提供方法 TCS_CreditGrpTradeReg
            return CSAppCall.call("SS.GrpCreditSVC.grpCreditTradeReg", input);
        }
        else
        // 个人用户
        {
            if (StringUtils.equals(tradeTypeCode, "7240") // 欠费销号
                    || StringUtils.equals(tradeTypeCode, "47") // 携出欠费注销
                    || StringUtils.equals(tradeTypeCode, "48")) // 携出欠费注销(携出方)
            {
                // 走前台立即销户流程
                DestroyUserNowRegSVC destroy = new DestroyUserNowRegSVC();
                retInfos = destroy.tradeReg(input);
            }
            else if (StringUtils.equals(tradeTypeCode, "7325"))// 取消统一付费关系[信控]
            {
                CreditEndMoreCardsOneAcctRegSVC creditEndMoreCardsOneAcctRegSVC = new CreditEndMoreCardsOneAcctRegSVC();
                retInfos = creditEndMoreCardsOneAcctRegSVC.tradeReg(input);
            }
            else if (StringUtils.equals(tradeTypeCode, "7506") // 关键时刻缴费提醒
                    || StringUtils.equals(tradeTypeCode, "7507") // 关键时刻GPRS流量上限提醒
                    || StringUtils.equals(tradeTypeCode, "7508")) // 关键时刻客户即将停机提醒
            {
                // TCS_CreditCrucialPromptReg
                CreditCrucialPromptRegSVC creditCrucialPromptRegSVC = new CreditCrucialPromptRegSVC();
                retInfos = creditCrucialPromptRegSVC.tradeReg(input);
            }
            else if (StringUtils.equals("7801", tradeTypeCode) || StringUtils.equals("7802", tradeTypeCode) || StringUtils.equals("7803", tradeTypeCode))
            {
                // 固话缴费开机、固话欠费半停机、固话欠费停机
                ChangeSvcStateRegSVC modiSvcState = new ChangeSvcStateRegSVC();
                retInfos = modiSvcState.tradeReg(input);
            }
            else if (StringUtils.equals("7804", tradeTypeCode))
            {
                // 固话欠费销号
                FixTelDemolishRegSVC fixTelDemolishRegSVC = new FixTelDemolishRegSVC();
                input.put("ORDER_TYPE_CODE", "7804");
                retInfos = fixTelDemolishRegSVC.tradeReg(input);
            }
            else
            // 默认状态类
            {
                // 携转用户相关信息处理
                this.userNetNpDeal(userInfo, tradeTypeCode);
                ChangeSvcStateRegSVC modiSvcState = new ChangeSvcStateRegSVC();
                retInfos = modiSvcState.tradeReg(input);
            }
            // add by chenzm 宽带连带处理
            // widenetTradeReg(input);改掉action
            return retInfos;
            // 1判断是否集团总机成员 （由集团提供服务）
            // 1.1是：调集团服务处理集团总机（由集团提供服务）
            // 1.2否：判断是否需要做铁通停开机
            // 1.21是：掉铁通停开机接口进行处理（王觉晓提供接口）
        }
    }

    private void userNetNpDeal(IData userInfo, String tradeTypeCode) throws Exception
    {
        // TODO Auto-generated method stub
        if (StringUtils.equals("46", tradeTypeCode))// 携出方缴费开机，携出方特殊处理
        {
            // 通过USERID获取TF_B_TRADE_NP表中最近的欠费停机的工单
            IDataset tradeNpDataset = TradeNpQry.getTradeNpByUserIdTradeTypeCodeCancelTag(userInfo.getString("USER_ID"), "44", "0");
            if (IDataUtil.isEmpty(tradeNpDataset))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "获取用户的携出方欠费停机(44)工单异常, " + userInfo.getString("SERIAL_NUMBER") + "携出方欠费停机工单不存在！");
            }
            String sysDate = SysDateMgr.getSysDate();
            for (int i = 0, count = tradeNpDataset.size(); i < count; i++)
            {
                IData param = new DataMap();
                String tradeId = tradeNpDataset.getData(i).getString("TRADE_ID");
                param.put("TRADE_ID", tradeId);
                param.put("CANCEL_TAG", "1");
                param.put("CANCEL_DATE", sysDate);
                param.put("CANCEL_STAFF_ID", getVisit().getStaffId());
                param.put("CANCEL_DEPART_ID", getVisit().getDepartId());
                param.put("CANCEL_CITY_CODE", getVisit().getCityCode());
                param.put("CANCEL_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_CANCEL_TAG", param, Route.getJourDb(BizRoute.getRouteId()));

                param.clear();
                param.put("TRADE_ID", tradeId);
                param.put("CANCEL_TAG", "1");
                Dao.executeUpdateByCodeCode("TF_B_TRADE_NP", "UPD_NP_CANCEL_TAG", param, Route.getJourDb(BizRoute.getRouteId()));
            }
        }
    }
}
