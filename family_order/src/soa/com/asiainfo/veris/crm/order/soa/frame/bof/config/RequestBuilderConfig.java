
package com.asiainfo.veris.crm.order.soa.frame.bof.config;

import java.lang.reflect.Proxy;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.proxy.BuilderProxy;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

/**
 * @author Administrator
 */
public final class RequestBuilderConfig
{
    private static IData requestBuilderExpression = new DataMap();

    private static IData requestBuilderMap = new DataMap();

    static
    {
        try
        {
            // 获取产品模型附加动作信息
            IDataset builders = BofQuery.getAllReqBuilder();
            int size = builders.size();

            // 加载到缓存里
            for (int i = 0; i < size; i++)
            {
                try
                {
                    IData build = builders.getData(i);

                    String configClassName = build.getString("CLASS_NAME").trim();
                    String configExpression = build.getString("EXPRESSION");

                    // 加入到缓存里
                    if (!requestBuilderMap.containsKey(configClassName))
                    {
                        IBuilder obj = (IBuilder) Class.forName(configClassName).newInstance();
                        BuilderProxy proxy = new BuilderProxy(obj);
                        requestBuilderMap.put(configClassName, Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), proxy));
                    }
                    if (StringUtils.isNotBlank(configExpression))
                    {
                        if (!requestBuilderExpression.containsKey(configExpression))
                        {
                            CompiledTemplate compiled = TemplateCompiler.compileTemplate(configExpression);
                            requestBuilderExpression.put(configExpression, compiled);
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
        finally
        {

        }
    }

    public static IBuilder getRequestBuilder(String tradeTypeCode, String orderTypeCode, IData input) throws Exception
    {
        input.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        String className = "";
        IBuilder iBuild = null;
        boolean findFlag = false;
        IDataset builders = new DatasetList();

        IDataset result = BofQuery.getReqBuilderByPK(tradeTypeCode, orderTypeCode, CSBizBean.getVisit().getInModeCode());
        int size = result.size();
        for (int i = 0; i < size; i++)
        {
            IData builder = result.getData(i);
            String expression = builder.getString("EXPRESSION");
            if (StringUtils.isNotBlank(expression))
            {
                // 执行表达式
                CompiledTemplate compiled = null;
                if (requestBuilderExpression.containsKey(expression))
                {
                    compiled = (CompiledTemplate) requestBuilderExpression.get(expression);
                }
                else
                {
                    compiled = TemplateCompiler.compileTemplate(expression);
                    requestBuilderExpression.put(expression, compiled);
                }
                boolean flag = ((Boolean) TemplateRuntime.execute(compiled, input)).booleanValue();
                if (!flag)
                {
                    // 不匹配则执行下一个
                    continue;
                }
            }
            builders.add(builder);
        }
        if (IDataUtil.isEmpty(builders))
        {
            CSAppException.apperr(BofException.CRM_BOF_014, tradeTypeCode);
        }
        size = builders.size();
        for (int i = 0; i < size; i++)
        {
            String expression = builders.getData(i).getString("EXPRESSION");
            if (StringUtils.isNotBlank(expression))
            {
                findFlag = true;
                className = builders.getData(i).getString("CLASS_NAME");
                break;
            }
        }
        if (!findFlag)
        {
            DataHelper.sort(builders, "ORDER_NO", IDataset.TYPE_INTEGER, IDataset.ORDER_ASCEND);
            className = builders.getData(0).getString("CLASS_NAME").trim();
        }

        if (requestBuilderMap.containsKey(className))
        {
            iBuild = (IBuilder) requestBuilderMap.get(className);
        }
        else
        {
            IBuilder obj = (IBuilder) Class.forName(className).newInstance();
            BuilderProxy proxy = new BuilderProxy(obj);
            iBuild = (IBuilder) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), proxy);
            requestBuilderMap.put(className, iBuild);
        }

        input.remove("IN_MODE_CODE");

        return iBuild;
    }
}
