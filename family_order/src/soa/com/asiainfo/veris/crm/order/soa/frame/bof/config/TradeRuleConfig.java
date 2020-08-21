
package com.asiainfo.veris.crm.order.soa.frame.bof.config;

/**
 * @author Administrator
 */
public class TradeRuleConfig
{

    // private static IData tradeRuleClassMap = new DataMap();
    //
    // private static IData tradeRuleExpressionMap = new DataMap();
    //
    // static
    // {
    // try
    // {
    // // 获取产品模型附加动作信息
    // IDataset tradeRules = TradeRuleInfoQry.queryAllTradeRules();
    // int size = tradeRules.size();
    // // 加载到缓存里
    // for (int i = 0; i < size; i++)
    // {
    // try
    // {
    // IData tradeRule = tradeRules.getData(i);
    // String className = tradeRule.getString("CLASS_NAME").trim();
    // String expression = tradeRule.getString("EXPRESSION");
    //
    // // 加入到缓存里
    // if (!tradeRuleClassMap.containsKey(className))
    // {
    // tradeRuleClassMap.put(className, Class.forName(className).newInstance());
    // }
    // if (StringUtils.isNotBlank(expression))
    // {
    // if (!tradeRuleExpressionMap.containsKey(expression))
    // {
    // CompiledTemplate compiled = TemplateCompiler.compileTemplate(expression);
    // tradeRuleExpressionMap.put(expression, compiled);
    // }
    // }
    // }
    // catch (Exception e)
    // {
    //
    // }
    // }
    // }
    // catch (Exception e)
    // {
    // e.printStackTrace();
    // }
    // finally
    // {
    // try
    // {
    // }
    // catch (Exception e)
    // {
    // e.printStackTrace();
    // }
    // }
    // }
    //
    // public static List<ITradeAfterRule> getTradeAfterRules(BusiTradeData btd) throws Exception
    // {
    // List<ITradeAfterRule> rtRules = new ArrayList<ITradeAfterRule>();
    // OrderDataBus dataBus = DataBusManager.getDataBus();
    // String tradeTypeCode = btd.getRD().getTradeType().getTradeTypeCode();
    // String orderTypeCode = dataBus.getOrderTypeCode();
    //
    // IDataset temps = TradeRuleInfoQry.queryTradeRulesByPK(tradeTypeCode, orderTypeCode, PersonConst.RULE_TAG_AFTER);
    //
    // // 遍历所有的rule，过滤与表达式不匹配的
    // for (int i = 0; i < temps.size(); i++)
    // {
    // IData tradeRule = (IData) temps.get(i);
    // if (StringUtils.isNotBlank(tradeRule.getString("EXPRESSION")))
    // {
    // // 执行表达式
    // CompiledTemplate compiled = (CompiledTemplate) tradeRuleExpressionMap.get(tradeRule.getString("EXPRESSION"));
    // boolean flag = ((Boolean) TemplateRuntime.execute(compiled, btd)).booleanValue();
    // if (!flag)
    // {
    // // 不匹配则执行下一个
    // continue;
    // }
    // }
    // if (tradeRuleClassMap.containsKey(tradeRule.getString("CLASS_NAME").trim()))
    // {
    // rtRules.add((ITradeAfterRule) tradeRuleClassMap.get(tradeRule.getString("CLASS_NAME").trim()));
    // }
    //
    // }
    //
    // return rtRules;
    // }
    //
    // public static List<ITradeBeforeRule> getTradeBeforeRules(BaseReqData brd) throws Exception
    // {
    // List<ITradeBeforeRule> rtRules = new ArrayList<ITradeBeforeRule>();
    // OrderDataBus dataBus = DataBusManager.getDataBus();
    // String tradeTypeCode = brd.getTradeType().getTradeTypeCode();
    // String orderTypeCode = dataBus.getOrderTypeCode();
    //
    // IDataset temps = TradeRuleInfoQry.queryTradeRulesByPK(tradeTypeCode, orderTypeCode, PersonConst.RULE_TAG_BEFORE);
    //
    // // 遍历所有的rule，过滤与表达式不匹配的
    // for (int i = 0; i < temps.size(); i++)
    // {
    // IData tradeRule = (IData) temps.get(i);
    // if (StringUtils.isNotBlank(tradeRule.getString("EXPRESSION")))
    // {
    // // 执行表达式
    // CompiledTemplate compiled = (CompiledTemplate) tradeRuleExpressionMap.get(tradeRule.getString("EXPRESSION"));
    // boolean flag = ((Boolean) TemplateRuntime.execute(compiled, brd)).booleanValue();
    // if (!flag)
    // {
    // // 不匹配则执行下一个
    // continue;
    // }
    // }
    // if (tradeRuleClassMap.containsKey(tradeRule.getString("CLASS_NAME").trim()))
    // {
    // rtRules.add((ITradeBeforeRule) tradeRuleClassMap.get(tradeRule.getString("CLASS_NAME").trim()));
    // }
    // }
    //
    // return rtRules;
    // }
}
