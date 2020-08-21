
package com.asiainfo.veris.crm.order.soa.frame.bre.svc;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage *
 * 
 * @ClassName: GetRuleListByBase
 * @Description: 通用规则获取实现
 * @version: v1.0.0
 * @author: xiaocl
 */
public class GetRuleListByBase extends AbstractIGETRuleListRuleList
{
    private static final Logger logger = Logger.getLogger(GetRuleListByBase.class);

    public IDataset getRuleFlow(IData param) throws Exception
    {
        String strTradeTypeCode = param.getString("TRADE_TYPE_CODE");
        String strOrderTypeCode = param.getString("ORDER_TYPE_CODE");

        IDataset listAllRule = null;

        if ("*".equals(strOrderTypeCode) || strTradeTypeCode.equals(strOrderTypeCode))
        {
            listAllRule = Dao.qryByCode("TD_S_CPARAM", "SEL_RULE_BASE", param, Route.CONN_CRM_CEN);
        }
        else
        // order_type_code不等于trade_type_code情况
        {
            IDataset orderBizRuleInfos = null;// Dao.qryByCode("TD_S_CPARAM", "SEL_ORDER_TRADE_BIZ_RULEID", param,
            // Route.CONN_CRM_CEN);
            if (IDataUtil.isEmpty(orderBizRuleInfos))// 该order_type_code/trade_type_code不存在任何配置数据，则执行trade_type_code所有的规则
            {
                listAllRule = Dao.qryByCode("TD_S_CPARAM", "SEL_RULE_BASE", param, Route.CONN_CRM_CEN);
            }
            else
            {
                // 存在配置为-1的数据，则代表不需要执行任何规则 直接返回,注意sql中有对rule_biz_id升序排序
                if (StringUtils.equals("-1", orderBizRuleInfos.getData(0).getString("RULE_BIZ_ID")))
                {
                    return new DatasetList();
                }
                else
                {
                    if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                        logger.debug("在TD_BRE_ORDER_RULE中找到配置，走SEL_ALL_RULE_BY_ORDER_CFG查询条件");
                    listAllRule = Dao.qryByCode("TD_S_CPARAM", "SEL_ALL_RULE_BY_ORDER_CFG", param, Route.CONN_CRM_CEN);
                }
            }
        }

        /* 如果没有找到规则就直接返回 */
        if (listAllRule.size() == 0)
        {
            return listAllRule;
        }
        else
        {
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" CCCCCCCCCCCCCC 查询到规则数据[" + listAllRule.size() + "]");
        }

        /* 判断产品， 品牌，superusr， 和员工权限 */
        String strRightCode = "";
        String strRuleBrandCode = "";
        /*
         * String strRuleProductId = ""; String strRulePackageId = "";
         */
        IDataset idsRet = new DatasetList();
        boolean priv = false;
        boolean isSupserUser = "SUPERUSR".equals(getVisit().getStaffId());
        String strBrandCode = param.getString("BRAND_CODE");

        /*
         * String strProductId = param.getString("PRODUCT_ID"); String strBrandCode = param.getString("BRAND_CODE");
         * String strPackageId = param.getString("PACKAGE_ID");
         */

        /*
         * if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled")) {
         * logger.debug("UUUUUUUUUUU 用户信息 BRAND_CODE=[" + strBrandCode + "];PRODUCT_ID=[" + strProductId +
         * "];PACKAGE_ID=[" + strPackageId + "]"); }
         */

        for (Iterator iterator = listAllRule.iterator(); iterator.hasNext();)
        {
            IData bizrule = (IData) iterator.next();

            /* 先判断产品和品牌 */

            strRuleBrandCode = bizrule.getString("BRAND_CODE", "");
            /*
             * strRulePackageId = bizrule.getString("PACKAGE_ID"); strRuleProductId = bizrule.getString("PRODUCT_ID");
             */
            /*
             * if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled")) {
             * logger.debug("UUUUUUUUUUU BREBIZCFG BRAND_CODE=[" + strRuleBrandCode + "];PRODUCT_ID=[" +
             * strRuleProductId + "];PACKAGE_ID=[" + strRulePackageId + "]"); }
             */

            /*
             * if (("-1".equals(strRuleProductId) || strRuleProductId.equals(strProductId)) &&
             * ("ZZZZ".equals(strRuleBrandCode) || strRuleBrandCode.equals(strBrandCode)) &&
             * ("-1".equals(strRulePackageId) || strRulePackageId.equals(strPackageId))) {
             */
            if ("ZZZZ".equals(strRuleBrandCode) || strRuleBrandCode.equals(strBrandCode))
            {
                strRightCode = bizrule.getString("RIGHT_CODE", "");

                /* SUPERUSR 工号的特殊处理 */
                if (isSupserUser)
                {
                    if (bizrule.getString("RIGHT_CODE") == null || StringUtils.isBlank(bizrule.getString("RIGHT_CODE")))
                    {
                        idsRet.add(bizrule);
                    }
                }
                else
                {
                    if (StringUtils.isBlank(strRightCode))
                    {
                        idsRet.add(bizrule);
                        continue;
                    }

                    /* 判断数据权限 */
                    priv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), strRightCode);

                    /* 员工有权限就跳过，不判断规则 */
                    if (!priv)
                    {
                        idsRet.add(bizrule);
                    }
                    else
                    {
                        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                            logger.debug(" QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ [" + bizrule.getString("RULE_BIZ_ID") + "][" + strRightCode + "]");
                    }
                }
            }
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" GGGGGGGGGGGGGGGGGGG 过滤产品，品牌，SUPERUSR,和权限后， 剩下规则数据[" + idsRet.size() + "]");

        return idsRet;
    }

    public IDataset getRuleList(IData databus, Object o /* String strRuleTwigCode */) throws Exception
    {
        return super.getRuleList(databus, o);
    }

    @Override
    public void orgDataBus(IData databus, String strRuleTwigCode, IData param)
    {
        // TODO Auto-generated method stub
        String ruleBizKindCode = databus.getString("RULE_BIZ_KIND_CODE", "");

        if (StringUtils.isBlank(ruleBizKindCode))
        {
            ruleBizKindCode = "TradeCheckSuperLimit";
        }
        if (databus.getString("ACTION_TYPE") == null)
        {
        	if (logger.isDebugEnabled())
            logger.debug("规则入口请配置动作类型参数！");
        }

        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        if ("110".equals(strTradeTypeCode))
        {
            IDataset listTemp = new DatasetList();

            if (databus.getDataset("TF_B_TRADE_PRODUCT", listTemp).size() > 0 && !databus.getString("PRODUCT_ID", "-1").equals(databus.getString("RSRV_STR2", "-1")))
            {
                strTradeTypeCode = "110";
                databus.put("TRADE_TYPE_CODE", "110");
            }
            else if (databus.getDataset("TF_B_TRADE_DISCNT", listTemp).size() > 0 && databus.getDataset("TF_B_TRADE_SVC", listTemp).size() == 0)
            {
                strTradeTypeCode = "150";
                databus.put("TRADE_TYPE_CODE", "150");
            }
            else
            {
                strTradeTypeCode = "120";
                databus.put("TRADE_TYPE_CODE", "120");
            }

            // 报错类 TradeCheckBefore 业务类型全部走110
            if (StringUtils.isNotBlank(databus.getString("ACTION_TYPE")) && ("TRADEALL.TradeCheckBefore".equals(databus.getString("ACTION_TYPE")) || "TradeCheckBefore".equals(databus.getString("ACTION_TYPE"))))
            {
                strTradeTypeCode = "110";
                databus.put("TRADE_TYPE_CODE", strTradeTypeCode);
            }
        }
        param.put("TRADE_TYPE_CODE", strTradeTypeCode);
        param.put("PRODUCT_ID", databus.getString("PRODUCT_ID"));
        param.put("RULE_BIZ_KIND_CODE", ruleBizKindCode);
    }

}
