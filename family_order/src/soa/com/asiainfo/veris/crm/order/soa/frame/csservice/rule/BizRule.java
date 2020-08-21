
package com.asiainfo.veris.crm.order.soa.frame.csservice.rule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.rule.log.BreBizLog;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.svc.BreEngine;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.productlimit.CheckProductTradeMgr;

public class BizRule extends BreBase
{
    public static Logger logger = Logger.getLogger(BizRule.class);

    private static IData BeforeCheck(IData databus) throws Exception
    {
        long lstartTime = BreBizLog.getStartTime();

        String actionType = databus.getString("ACTION_TYPE");
        // String ruleKindCode = databus.getString("RULE_BIZ_KIND_CODE");

        if (StringUtils.isBlank(actionType))
        {
            actionType = "TradeCheckBefore";
        }
        /*
         * if (StringUtils.isBlank(ruleKindCode)) { ruleKindCode = "TradeCheckSuperLimit"; }
         */
        databus.put("ACTION_TYPE", actionType);
        // databus.put("RULE_BIZ_KIND_CODE", ruleKindCode);

        // 进行参数校验
        boolean bParamVerify = true;

        if (databus.getString("ACTION_TYPE").equals(BreFactory.CHECK_TRADE_BEFORE))
        {
            for (int idx = 0; idx < BreFactory.BEFORE_FORCE_PARAM.length; idx++)
            {
                if (BreFactory.BEFORE_FORCE_PARAM[idx].equals("X_CHOICE_TAG"))
                {
                    continue;
                }
                if (databus.isNoN(BreFactory.BEFORE_FORCE_PARAM[idx]))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, "-1", "规则参数[" + BreFactory.BEFORE_FORCE_PARAM[idx] + "]是必须!");
                    bParamVerify = false;
                }
            }
        }

        // 如果参数校验失败
        if (!bParamVerify)
        {
            IData result = new DataMap();
            result.put("RULE_INFO", databus.getDataset("RULE_INFO"));
            return result;
        }

        // 执行规则
        IData result = BreEngine.bre4SuperLimit(databus);

        BreBizLog.log(databus, "BizRule.BeforeCheck", lstartTime);

        return result;
    }

    /**
     * 产品依赖互斥
     * 
     * @param databus
     * @return
     * @throws Exception
     */
    public static IData bre4ProductLimit(IData databus) throws Exception
    {
        long lstartTime = BreBizLog.getStartTime();

        CheckProductTradeMgr cpt = new CheckProductTradeMgr();
        cpt.checkProductTradeMgr(databus);

        BreBizLog.log(databus, "BizRule.bre4ProductLimit", lstartTime);
        return databus;
    }

    /**
     * 产品依赖互斥
     * 
     * @param databus
     * @return
     * @throws Exception
     */
    public static IData bre4ProductLimitNeedFormat(IData databus) throws Exception
    {
        long lstartTime = BreBizLog.getStartTime();

        CheckProductTradeMgr cpt = new CheckProductTradeMgr();
        cpt.checkProductTradeMgr(databus);

        IData map = BreTipsHelp.formatInfo(databus);
        BreBizLog.log(databus, "BizRule.bre4ProductLimitNeedFormat", lstartTime);
        return map;
    }

    public static IData bre4SuperLimit(IData databus) throws Exception
    {
        return BreEngine.bre4SuperLimit(databus);
    }

    /**
     * 规则总入口
     * 
     * @param databus
     * @return
     * @throws Exception
     */
    public static IData bre4UniteInterface(IData databus) throws Exception
    {
        long lstartTime = BreBizLog.getStartTime();

        /* 调用产品依赖互斥 */
        bre4ProductLimit(databus);

        /* 调用业务规则检查 */
        IData result = BreEngine.bre4SuperLimit(databus);
        BreBizLog.log(databus, "BizRule.bre4UniteInterface", lstartTime);
        return result;
    }

    public static IData Check0Error(IData databus) throws Exception
    {
        databus.put("TIPS_TYPE", "0");
        return BreEngine.bre4SuperLimit(databus);
    }

    public static IData Check4Error(IData databus) throws Exception
    {
        databus.put("TIPS_TYPE", "0|4");
        return BreEngine.bre4SuperLimit(databus);
    }

    public static IData Check4Tips(IData databus) throws Exception
    {
        databus.put("TIPS_TYPE", "1|2");

        return BreEngine.bre4SuperLimit(databus);
    }

    /**
     * 前台告知查何种类型的规则
     * 
     * @param databus
     * @return
     * @throws Exception
     */
    public static IData CheckTipByTipType(IData databus) throws Exception
    {
        databus.put("TIPS_TYPE", databus.getString("TIPS_TYPE"));
        return BreEngine.bre4SuperLimit(databus);
    }

    /**
     * 自己测试用
     * 
     * @param databus
     * @return
     * @throws Exception
     */
    public static IData ruleTest(IData databus) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ruleTest ");
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(databus);
        }

        if (databus.getString("RULE_BIZ_TYPE_CODE").equals(BreFactory.CHECK_TRADE_AFTER) || "RuleTest".equals(databus.getString("RULE_BIZ_TYPE_CODE")))
        {
            if ("".equals(databus.getString("TRADE_ID", "")))
            {
                databus.put("TRADE_ID", "3113030100018103");
            }

            databus.putAll(BreQueryHelp.getMainTrade(databus));
        }

        if (databus.getString("RULE_BIZ_TYPE_CODE").equals(BreFactory.CHECK_TRADE_BEFORE))
        {
            databus.put("TRADE_TYPE_CODE", "110");
            databus.put("TRADE_EPARCHY_CODE", Route.getCrmDefaultDb());
            databus.put("TRADE_STAFF_ID", "SUPERUSR");
            databus.put("TRADE_CITY_CODE", "A311");
            databus.put("FEE", "-100");
            databus.put("ID", "3193050200010461");
            databus.put("ID_TYPE", "1");
            databus.put("IN_MODE_CODE", "0");
            databus.put("PROVINCE_CODE", "HNAN");
            databus.put("REDUSER_TAG", "0");
            databus.put("X_CHOICE_TAG", "1");
            databus.put("BRAND_CODE", "VPGN");
            databus.put("PRODUCT_ID", "2222");
            databus.put("USER_ID", "3193050200010461");
            databus.put("CUST_ID", "7400000016000046");
        }

        IData result = bre4UniteInterface(databus);

        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ruleTest ");

        return result;
    }

    public static IData TradeAfterCheck(IData databus) throws Exception
    {
        long lstartTime = BreBizLog.getStartTime();

        /* 调用产品依赖互斥 */
        bre4ProductLimit(databus);

        /* 调用业务规则检查 */
        databus.put("RULE_BIZ_TYPE_CODE", "TradeCheckAfter");
        databus.put("RULE_BIZ_KIND_CODE", "TradeCheckSuperLimit");

        IData result = BreEngine.bre4SuperLimit(databus);
        BreBizLog.log(databus, "BizRule.TradeAfterCheck", lstartTime);
        return result;
    }

    public static IData TradeBeforeCheck(IData databus) throws Exception
    {
        String tag = databus.getString("X_CHOICE_TAG", "0");
        if (tag.equals("0"))
        {
            databus.put("TIPS_TYPE", "0|1|2|4");// 提示中断
        }
        else if (tag.equals("1"))
        {
            databus.put("TIPS_TYPE", "1|2");// 提示继续，询问是否
        }
        return BeforeCheck(databus);
    }

    public static IData TradeBeforeCheck4Error(IData databus) throws Exception
    {
        databus.put("TIPS_TYPE", "0|4");
        return BeforeCheck(databus);
    }

    public static IData TradeBeforeCheck4Tips(IData databus) throws Exception
    {
        databus.put("TIPS_TYPE", "1|2");
        return BeforeCheck(databus);
    }
}
