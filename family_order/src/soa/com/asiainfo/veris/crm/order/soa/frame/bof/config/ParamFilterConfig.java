
package com.asiainfo.veris.crm.order.soa.frame.bof.config;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

public class ParamFilterConfig
{
    private static IData paramFilterClassMap = new DataMap();

    private static IData paramFilterExpressionMap = new DataMap();

    static
    {
        try
        {
            // 获取产品模型附加动作信息
            IDataset paramFilterList = BofQuery.queryAllParamFilter();
            int size = paramFilterList.size();
            // 加载到缓存里
            for (int i = 0; i < size; i++)
            {
                try
                {
                    IData paramFilter = paramFilterList.getData(i);
                    String className = paramFilter.getString("CLASS_NAME").trim();
                    String expression = paramFilter.getString("EXPRESSION");

                    if (!paramFilterClassMap.containsKey(className))
                    {
                        paramFilterClassMap.put(className, Class.forName(className).newInstance());
                    }
                    if (StringUtils.isNotBlank(expression))
                    {
                        if (!paramFilterExpressionMap.containsKey(expression))
                        {
                            CompiledTemplate compiled = TemplateCompiler.compileTemplate(expression);
                            paramFilterExpressionMap.put(expression, compiled);
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static IFilterException getExceptionParamFilter(String xTransCode, IData param) throws Exception
    {
        param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        IFilterException filter = null;
        IDataset temps = BofQuery.queryParamFiltersByPK(xTransCode, BofConst.PARAM_FILTER_TYPE_EXCEPTION);

        // 根据表达式过滤
        String filterClassName = "";
        for (int i = 0; i < temps.size(); i++)
        {
            IData paramFilter = (IData) temps.get(i);
            if (StringUtils.isNotBlank(paramFilter.getString("EXPRESSION")))
            {
                // 执行表达式
                CompiledTemplate compiled = (CompiledTemplate) paramFilterExpressionMap.get(paramFilter.getString("EXPRESSION"));
                if (compiled == null)
                {
                    compiled = TemplateCompiler.compileTemplate(paramFilter.getString("EXPRESSION"));
                    paramFilterExpressionMap.put(paramFilter.getString("EXPRESSION"), compiled);
                }
                boolean flag = ((Boolean) TemplateRuntime.execute(compiled, param)).booleanValue();
                if (!flag)
                {
                    // 不匹配则执行下一个
                    continue;
                }
                else
                {
                    filterClassName = paramFilter.getString("CLASS_NAME");
                    break;
                }
            }
            else
            {
                filterClassName = paramFilter.getString("CLASS_NAME");
            }
        }

        if (StringUtils.isBlank(filterClassName))
        {
            return null;
        }

        if (paramFilterClassMap.containsKey(filterClassName))
        {
            filter = (IFilterException) paramFilterClassMap.get(filterClassName);
        }
        else
        {
            filter = (IFilterException) Class.forName(filterClassName).newInstance();
            paramFilterClassMap.put(filterClassName, filter);
        }

        param.remove("IN_MODE_CODE");

        return filter;
    }

    public static IFilterIn getInParamFilter(String xTransCode, IData param) throws Exception
    {
        param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        IFilterIn filter = null;
        IDataset temps = BofQuery.queryParamFiltersByPK(xTransCode, BofConst.PARAM_FILTER_TYPE_IN);

        // 根据表达式过滤
        String filterClassName = "";
        for (int i = 0; i < temps.size(); i++)
        {
            IData paramFilter = (IData) temps.get(i);
            if (StringUtils.isNotBlank(paramFilter.getString("EXPRESSION")))
            {
                // 执行表达式
                CompiledTemplate compiled = (CompiledTemplate) paramFilterExpressionMap.get(paramFilter.getString("EXPRESSION"));
                if (compiled == null)
                {
                    compiled = TemplateCompiler.compileTemplate(paramFilter.getString("EXPRESSION"));
                    paramFilterExpressionMap.put(paramFilter.getString("EXPRESSION"), compiled);
                }
                boolean flag = ((Boolean) TemplateRuntime.execute(compiled, param)).booleanValue();
                if (!flag)
                {
                    // 不匹配则执行下一个
                    continue;
                }
                else
                {
                    filterClassName = paramFilter.getString("CLASS_NAME");
                    break;
                }
            }
            else
            {
                filterClassName = paramFilter.getString("CLASS_NAME");
            }
        }

        if (StringUtils.isBlank(filterClassName))
        {
            return null;
        }

        if (paramFilterClassMap.containsKey(filterClassName))
        {
            filter = (IFilterIn) paramFilterClassMap.get(filterClassName);
        }
        else
        {
            filter = (IFilterIn) Class.forName(filterClassName).newInstance();
            paramFilterClassMap.put(filterClassName, filter);
        }

        param.remove("IN_MODE_CODE");
        return filter;
    }

    public static IFilterOut getOutParamFilter(String xTransCode, IData param) throws Exception
    {
        param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        IFilterOut filter = null;
        IDataset temps = BofQuery.queryParamFiltersByPK(xTransCode, BofConst.PARAM_FILTER_TYPE_OUT);

        // 根据表达式过滤
        String filterClassName = "";
        for (int i = 0; i < temps.size(); i++)
        {
            IData paramFilter = (IData) temps.get(i);
            if (StringUtils.isNotBlank(paramFilter.getString("EXPRESSION")))
            {
                // 执行表达式
                CompiledTemplate compiled = (CompiledTemplate) paramFilterExpressionMap.get(paramFilter.getString("EXPRESSION"));
                if (compiled == null)
                {
                    compiled = TemplateCompiler.compileTemplate(paramFilter.getString("EXPRESSION"));
                    paramFilterExpressionMap.put(paramFilter.getString("EXPRESSION"), compiled);
                }
                boolean flag = ((Boolean) TemplateRuntime.execute(compiled, param)).booleanValue();
                if (!flag)
                {
                    // 不匹配则执行下一个
                    continue;
                }
                else
                {
                    filterClassName = paramFilter.getString("CLASS_NAME");
                    break;
                }
            }
            else
            {
                filterClassName = paramFilter.getString("CLASS_NAME");
            }
        }

        if (StringUtils.isBlank(filterClassName))
        {
            return null;
        }

        if (paramFilterClassMap.containsKey(filterClassName))
        {
            filter = (IFilterOut) paramFilterClassMap.get(filterClassName);
        }
        else
        {
            filter = (IFilterOut) Class.forName(filterClassName).newInstance();
            paramFilterClassMap.put(filterClassName, filter);
        }

        param.remove("IN_MODE_CODE");

        return filter;
    }
}
