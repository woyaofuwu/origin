
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
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IPrintFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.share.IElementCalDateAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

/**
 * @author Administrator
 */
public class TradeActionConfig
{

    private static IData tradeActionClassMap = new DataMap();

    private static IData tradeActionExpressionMap = new DataMap();

    static
    {
        try
        {
            // 获取产品模型附加动作信息
            IDataset actions = BofQuery.queryAllTradeActions();
            int size = actions.size();
            // 加载到缓存里
            for (int i = 0; i < size; i++)
            {
                try
                {
                    IData action = actions.getData(i);
                    String className = action.getString("CLASS_NAME").trim();
                    String expression = action.getString("EXPRESSION");

                    // 加入到缓存里
                    if (!tradeActionClassMap.containsKey(className))
                    {
                        tradeActionClassMap.put(className, Class.forName(className).newInstance());
                    }
                    if (StringUtils.isNotBlank(expression))
                    {
                        if (!tradeActionExpressionMap.containsKey(expression))
                        {
                            CompiledTemplate compiled = TemplateCompiler.compileTemplate(expression);
                            tradeActionExpressionMap.put(expression, compiled);
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

    public static List<IElementCalDateAction> getElementCalDateAction(String tradeTypeCode) throws Exception
    {
        List<IElementCalDateAction> rtActions = new ArrayList<IElementCalDateAction>();
        IDataset temps = BofQuery.queryCalElementDateActions(tradeTypeCode);

        for (int i = 0, isize = temps.size(); i < isize; i++)
        {
            IData act = temps.getData(i);

            String className = act.getString("CLASS_NAME").trim();

            if (tradeActionClassMap.containsKey(className))
            {
                rtActions.add((IElementCalDateAction) tradeActionClassMap.get(className));
            }
            else
            {
                tradeActionClassMap.put(className, Class.forName(className).newInstance());
                rtActions.add((IElementCalDateAction) tradeActionClassMap.get(className));
            }
        }

        return rtActions;
    }

    public static List<ITradeAction> getTradeActions(BusiTradeData btd) throws Exception
    {
        List<ITradeAction> rtActions = new ArrayList<ITradeAction>();
        OrderDataBus dataBus = DataBusManager.getDataBus();
        String tradeTypeCode = btd.getRD().getTradeType().getTradeTypeCode();
        String orderTypeCode = dataBus.getOrderTypeCode();

        IDataset temps = BofQuery.queryTradeRegActionsByPK(tradeTypeCode, orderTypeCode);

        // 遍历所有的rule，过滤与表达式不匹配的
        for (int i = 0; i < temps.size(); i++)
        {
            IData tradeAction = (IData) temps.get(i);
            if (StringUtils.isNotBlank(tradeAction.getString("EXPRESSION")))
            {
                // 执行表达式
                CompiledTemplate compiled = null;
                if (tradeActionExpressionMap.containsKey(tradeAction.getString("EXPRESSION")))
                {
                    compiled = (CompiledTemplate) tradeActionExpressionMap.get(tradeAction.getString("EXPRESSION"));
                }
                else
                {
                    compiled = TemplateCompiler.compileTemplate(tradeAction.getString("EXPRESSION"));
                    tradeActionExpressionMap.put(tradeAction.getString("EXPRESSION"), compiled);
                }
                boolean flag = ((Boolean) TemplateRuntime.execute(compiled, btd)).booleanValue();
                if (!flag)
                {
                    // 不匹配则执行下一个
                    continue;
                }
            }
            String className = tradeAction.getString("CLASS_NAME").trim();
            if (tradeActionClassMap.containsKey(className))
            {
                rtActions.add((ITradeAction) tradeActionClassMap.get(className));
            }
            else
            {
                tradeActionClassMap.put(className, Class.forName(className).newInstance());
                rtActions.add((ITradeAction) tradeActionClassMap.get(className));
            }
        }

        return rtActions;
    }

    public static List<ITradeFinishAction> getTradeFinishActions(String tradeTypeCode, String type) throws Exception
    {
        List<ITradeFinishAction> rtActions = new ArrayList<ITradeFinishAction>();
        IDataset temps = null;

        if ("beforeFinish".equals(type))
        {
            temps = BofQuery.queryTradeBeforeFinishActionsByPK(tradeTypeCode);
        }
        else if ("undofinish".equals(type))
        {
            temps = BofQuery.queryTradeUndoFinishActionsByPK(tradeTypeCode);
        }
        else if ("cancelfinish".equals(type))
        {
            temps = BofQuery.queryTradeCancelFinishActionsByPK(tradeTypeCode);
        }
        else
        // finish
        {
            temps = BofQuery.queryTradeFinishActionsByPK(tradeTypeCode);
        }

        for (int i = 0, isize = temps.size(); i < isize; i++)
        {
            IData act = temps.getData(i);

            String className = act.getString("CLASS_NAME").trim();

            if (tradeActionClassMap.containsKey(className))
            {
                rtActions.add((ITradeFinishAction) tradeActionClassMap.get(className));
            }
            else
            {
                tradeActionClassMap.put(className, Class.forName(className).newInstance());
                rtActions.add((ITradeFinishAction) tradeActionClassMap.get(className));
            }
        }

        return rtActions;
    }
    
    public static List<IPrintFinishAction> getPrintFinishActions(String tradeTypeCode) throws Exception
    {
        List<IPrintFinishAction> ptActions = new ArrayList<IPrintFinishAction>();
        IDataset temps = BofQuery.queryPrintFinishActionsByPK(tradeTypeCode);

        for (int i = 0, isize = temps.size(); i < isize; i++)
        {
            IData act = temps.getData(i);

            String className = act.getString("CLASS_NAME").trim();

            if (tradeActionClassMap.containsKey(className))
            {
            	ptActions.add((IPrintFinishAction) tradeActionClassMap.get(className));
            }
            else
            {
                tradeActionClassMap.put(className, Class.forName(className).newInstance());
                ptActions.add((IPrintFinishAction) tradeActionClassMap.get(className));
            }
        }

        return ptActions;
    }
}
