
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
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

/**
 * @author Administrator
 */
public class ProductModuleActionConfig
{

    private static IData productActionClassMap = new DataMap();

    private static IData productActionExpressionMap = new DataMap();

    static
    {
        try
        {
            // 获取产品模型附加动作信息
            IDataset actions = BofQuery.queryAllProductModuleActions();
            int size = actions.size();
            // 加载到缓存里
            for (int i = 0; i < size; i++)
            {
                try
                {
                    IData action = actions.getData(i);

                    String className = action.getString("CLASS_NAME").trim();
                    String expression = action.getString("EXPRESSION");

                    if (!productActionClassMap.containsKey(className))
                    {
                        productActionClassMap.put(className, Class.forName(className).newInstance());
                    }

                    if (StringUtils.isNotBlank(expression))
                    {
                        if (!productActionExpressionMap.containsKey(expression))
                        {
                            CompiledTemplate compiled = TemplateCompiler.compileTemplate(expression);
                            productActionExpressionMap.put(expression, compiled);
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

    public static List<IProductModuleAction> getProductAction(ProductModuleTradeData pmtd, BusiTradeData btd) throws Exception
    {
        List<IProductModuleAction> rtnActions = new ArrayList<IProductModuleAction>();
        OrderDataBus dataBus = DataBusManager.getDataBus();
        String elementTypeCode = pmtd.getElementType();
        String elementId = pmtd.getElementId();
        String tradeTypeCode = btd.getRD().getTradeType().getTradeTypeCode();
        String orderTypeCode = dataBus.getOrderTypeCode();

        IDataset temps = BofQuery.queryProductModuleActionsByPK(elementTypeCode, elementId, tradeTypeCode, orderTypeCode);

        // 根据表达式过滤
        for (int i = 0; i < temps.size(); i++)
        {
            IData productModuleAction = (IData) temps.get(i);
            if (StringUtils.isNotBlank(productModuleAction.getString("EXPRESSION")))
            {
                // 执行表达式
                String expression = productModuleAction.getString("EXPRESSION");
                CompiledTemplate compiled = null;
                if (productActionExpressionMap.containsKey(expression))
                {
                    compiled = (CompiledTemplate) productActionExpressionMap.get(expression);
                }
                else
                {
                    compiled = TemplateCompiler.compileTemplate(expression);
                    productActionExpressionMap.put(expression, compiled);// 加到缓存里
                }

                boolean flag = ((Boolean) TemplateRuntime.execute(compiled, pmtd)).booleanValue();
                if (!flag)
                {
                    // 不匹配则执行下一个
                    continue;
                }
            }
            String className = productModuleAction.getString("CLASS_NAME").trim();
            if (productActionClassMap.containsKey(className))
            {
                rtnActions.add((IProductModuleAction) productActionClassMap.get(className));
            }
            else
            {
                IProductModuleAction iPmdAction = (IProductModuleAction) Class.forName(className).newInstance();
                rtnActions.add(iPmdAction);
                productActionClassMap.put(className, iPmdAction);
            }
        }

        return rtnActions;
    }
}
