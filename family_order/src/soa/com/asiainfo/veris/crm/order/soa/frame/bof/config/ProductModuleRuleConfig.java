
package com.asiainfo.veris.crm.order.soa.frame.bof.config;

import java.util.ArrayList;
import java.util.List;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.rule.IProductModuleAfterRule;
import com.asiainfo.veris.crm.order.soa.frame.bof.rule.IProductModuleBeforeRule;

/**
 * @author Administrator
 */
public class ProductModuleRuleConfig
{

    private static IData productRuleClassMap = new DataMap();

    private static IData productRuleExpressionMap = new DataMap();

    static
    {
        try
        {
            // 获取产品模型附加动作信息
            IDataset rules = BofQuery.queryAllProductModuleRules();
            int size = rules.size();
            // 加载到缓存里
            for (int i = 0; i < size; i++)
            {
                try
                {
                    IData rule = rules.getData(i);
                    String className = rule.getString("CLASS_NAME").trim();
                    String expression = rule.getString("EXPRESSION");

                    if (!productRuleClassMap.containsKey(className))
                    {
                        productRuleClassMap.put(className, Class.forName(className).newInstance());
                    }
                    if (StringUtils.isNotBlank(expression))
                    {
                        if (!productRuleExpressionMap.containsKey(expression))
                        {
                            CompiledTemplate compiled = TemplateCompiler.compileTemplate(expression);
                            productRuleExpressionMap.put(expression, compiled);
                        }
                    }
                }
                catch (Exception e)
                {

                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static List<IProductModuleAfterRule> getAfterProductRule(ProductModuleTradeData pmtd, BusiTradeData btd) throws Exception
    {
        List<IProductModuleAfterRule> rtnRules = new ArrayList<IProductModuleAfterRule>();
        OrderDataBus dataBus = DataBusManager.getDataBus();
        String elementTypeCode = pmtd.getElementType();
        String elementId = pmtd.getElementId();
        String tradeTypeCode = btd.getRD().getTradeType().getTradeTypeCode();
        String orderTypeCode = dataBus.getOrderTypeCode();
        IDataset temps = BofQuery.queryProductModuleRulesByPK(elementTypeCode, elementId, tradeTypeCode, orderTypeCode, BofConst.RULE_TAG_AFTER);

        // 根据表达式过滤
        for (int i = 0; i < temps.size(); i++)
        {
            IData productModuleRule = (IData) temps.get(i);
            if (StringUtils.isNotBlank(productModuleRule.getString("EXPRESSION")))
            {
                // 执行表达式
                CompiledTemplate compiled = (CompiledTemplate) productRuleExpressionMap.get(productModuleRule.getString("EXPRESSION"));
                boolean flag = ((Boolean) TemplateRuntime.execute(compiled, pmtd)).booleanValue();
                if (!flag)
                {
                    // 不匹配则执行下一个
                    continue;
                }
            }

            if (productRuleClassMap.containsKey(productModuleRule.getString("CLASS_NAME")))
            {
                rtnRules.add((IProductModuleAfterRule) productRuleClassMap.get(productModuleRule.getString("CLASS_NAME")));
            }
        }

        return rtnRules;
    }

    public static List<IProductModuleBeforeRule> getBeforeProductRule(ProductModuleData pmd, BusiTradeData btd) throws Exception
    {
        List<IProductModuleBeforeRule> rtnRules = new ArrayList<IProductModuleBeforeRule>();
        OrderDataBus dataBus = DataBusManager.getDataBus();
        String elementTypeCode = pmd.getElementType();
        String elementId = pmd.getElementId();
        String tradeTypeCode = btd.getRD().getTradeType().getTradeTypeCode();
        String orderTypeCode = dataBus.getOrderTypeCode();
        IDataset temps = BofQuery.queryProductModuleRulesByPK(elementTypeCode, elementId, tradeTypeCode, orderTypeCode, BofConst.RULE_TAG_BEFORE);

        // 根据表达式过滤
        for (int i = 0; i < temps.size(); i++)
        {
            IData productModuleRule = (IData) temps.get(i);
            if (StringUtils.isNotBlank(productModuleRule.getString("EXPRESSION")))
            {
                // 执行表达式
                CompiledTemplate compiled = (CompiledTemplate) productRuleExpressionMap.get(productModuleRule.getString("EXPRESSION"));
                boolean flag = ((Boolean) TemplateRuntime.execute(compiled, pmd)).booleanValue();
                if (!flag)
                {
                    // 不匹配则执行下一个
                    continue;
                }
            }
            if (productRuleClassMap.containsKey(productModuleRule.getString("CLASS_NAME")))
            {
                rtnRules.add((IProductModuleBeforeRule) productRuleClassMap.get(productModuleRule.getString("CLASS_NAME")));
            }
        }

        return rtnRules;
    }
}
