
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.config;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.impl.BaseTrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.proxy.TransProxy;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UReqTransInfoQry;

public class RequestTransConfig
{

    private static IData requestTransClassMap = new DataMap();

    private static IData requestTransExpressionMap = new DataMap();

    static
    {
        try
        {
            // 获取请求配置信息
            IDataset requestTransList = UReqTransInfoQry.qryAllReqTrans();
            int size = requestTransList.size();
            // 加载到缓存里
            for (int i = 0; i < size; i++)
            {
                try
                {
                    IData paramFilter = requestTransList.getData(i);
                    String className = paramFilter.getString("CLASS_NAME").trim();
                    String expression = paramFilter.getString("EXPRESSION");

                    if (!requestTransClassMap.containsKey(className))
                    {
                        requestTransClassMap.put(className, Class.forName(className).newInstance());
                    }
                    if (StringUtils.isNotBlank(expression))
                    {
                        if (!requestTransExpressionMap.containsKey(expression))
                        {
                            CompiledTemplate compiled = TemplateCompiler.compileTemplate(expression);
                            requestTransExpressionMap.put(expression, compiled);
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

    /**
     * 用于动态反射的
     * 
     * @param xTransCode
     * @param param
     * @return
     * @throws Exception
     */
    public static ITrans getBaseTrans(String xTransCode, IData param) throws Exception
    {
        String className = BaseTrans.class.getName();
        ITrans iTrans = null;

        ITrans obj = (ITrans) Class.forName(className).newInstance();
        TransProxy proxy = new TransProxy(obj);
        iTrans = (ITrans) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), proxy);

        return iTrans;
    }

    public static List<ITrans> getRequestTranss(String xTransCode, IData param) throws Exception
    {
        List<ITrans> irequestTrans = new ArrayList<ITrans>();

        String inModeCode = param.getString("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        if (StringUtils.isEmpty(inModeCode))
        {
            CSAppException.apperr(BizException.CRM_GRP_713, "当前接口IN_MODE_CODE没有取到值!");
        }

        param.put("IN_MODE_CODE", inModeCode);

        IDataset temps = UReqTransInfoQry.qryReqtTransByPK(xTransCode, CSBizBean.getVisit().getInModeCode());

        // 根据表达式过滤
        String requestTransClassName = "";
        for (int i = 0, size = temps.size(); i < size; i++)
        {
            IData requestTrans = temps.getData(i);
            if (StringUtils.isNotBlank(requestTrans.getString("EXPRESSION")))
            {
                // 执行表达式
                CompiledTemplate compiled = (CompiledTemplate) requestTransExpressionMap.get(requestTrans.getString("EXPRESSION"));
                if (compiled == null)
                {
                    compiled = TemplateCompiler.compileTemplate(requestTrans.getString("EXPRESSION"));
                    requestTransExpressionMap.put(requestTrans.getString("EXPRESSION"), compiled);
                }
                boolean flag = ((Boolean) TemplateRuntime.execute(compiled, param)).booleanValue();
                if (!flag)
                {
                    // 不匹配则执行下一个
                    continue;
                }
                else
                {
                    requestTransClassName = requestTrans.getString("CLASS_NAME");
                }
            }
            else
            {
                requestTransClassName = requestTrans.getString("CLASS_NAME");
            }

            if (StringUtils.isBlank(requestTransClassName))
            {
                continue;
            }
            else
            {
            	if (!requestTransClassMap.containsKey(requestTransClassName))
                {
                    requestTransClassMap.put(requestTransClassName, Class.forName(requestTransClassName).newInstance());
                }
                irequestTrans.add((ITrans) requestTransClassMap.get(requestTransClassName));
            }
        }

        param.remove("IN_MODE_CODE");

        return irequestTrans;
    }
}
