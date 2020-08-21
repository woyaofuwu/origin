
package com.asiainfo.veris.crm.order.soa.frame.bre.svc;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage *
 * 
 * @ClassName: GetRuleListBySp
 * @Description: SP规则获取实现
 * @version: v1.0.0
 * @author: xiaocl
 */
public class GetRuleListBySp extends AbstractIGETRuleListRuleList
{
    private static final Logger logger = Logger.getLogger(BreQueryHelp.class);

    public IDataset getRuleFlow(IData param) throws Exception
    {
        String strTradeTypeCode = param.getString("TRADE_TYPE_CODE");
        String strOrderTypeCode = param.getString("ORDER_TYPE_CODE");

        IDataset listAllRule = null;

        if ("*".equals(strOrderTypeCode) || strTradeTypeCode.equals(strOrderTypeCode))
        {
            listAllRule = Dao.qryByCode("TD_S_CPARAM", "SEL_RULE_SP", param, Route.CONN_CRM_CEN);
        }
        else
        // order_type_code不等于trade_type_code情况
        {
            IDataset orderBizRuleInfos = Dao.qryByCode("TD_S_CPARAM", "SEL_ORDER_TRADE_BIZ_RULEID", param, Route.CONN_CRM_CEN);
            if (IDataUtil.isEmpty(orderBizRuleInfos))// 该order_type_code/trade_type_code不存在任何配置数据，则执行trade_type_code所有的规则
            {
                listAllRule = Dao.qryByCode("TD_S_CPARAM", "SEL_RULE_SP", param, Route.CONN_CRM_CEN);
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
        return listAllRule;
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
        param.put("TRADE_TYPE_CODE", databus.getString("TRADE_TYPE_CODE"));
        param.put("PRODUCT_ID", databus.getString("PRODUCT_ID"));
        param.put("RULE_BIZ_KIND_CODE", ruleBizKindCode);

        // xiekl 增加平台业务特殊参数
        param.put("BIZ_TYPE_CODE", databus.getString("BIZ_TYPE_CODE", "-1"));
        param.put("BRAND_CODE", databus.getString("BRAND_CODE", "-1"));
        param.put("SERVICE_ID", databus.getString("SERVICE_ID", "-1"));
        param.put("SP_CODE", databus.getString("SP_CODE", "-1"));
        param.put("BIZ_CODE", databus.getString("BIZ_CODE", "-1"));
        // end
    }
}
