
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.UTradeTypePFInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.ExpressionEvaluator;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.Variable;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.CallPfDealProxy;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;

/**
 * 给查询对象赋值
 * 
 * @author
 */
public class GetObjValue
{

    /**
     * 获取配置项中的全部参数 <BR>
     * 
     * @param params
     * @return
     */
    public static String[] getParams(String params)
    {
        if (StringUtils.isBlank(params))
        {
            return null;
        }
        List<String> paramList = new ArrayList<String>();
        int startPoint = -2;
        do
        {
            startPoint = params.indexOf("<<", startPoint + 2);
            int endpoint = params.indexOf(">>", startPoint + 1);
            if ((endpoint > startPoint) && (startPoint >= 0))
            {
                String param_code = params.substring(startPoint + 2, endpoint);
                if (StringUtils.isNotBlank(param_code) && !paramList.contains(param_code))
                {
                    paramList.add(param_code);
                }
            }
        }
        while (startPoint >= 0);

        return paramList.size() > 0 ? paramList.toArray(new String[paramList.size()]) : null;
    }

    /**
     * @param
     * @return
     * @throws Exception
     */
    public static Object getSelParamValue(String param_code, IData constantParamsBuffer, IData paramsBuffer, String dbSrc) throws Exception
    {

        String constantParamValue = constantParamsBuffer.getString(param_code);
        if (StringUtils.isNotBlank(constantParamValue))
        {
            return constantParamValue;
        }

        // 对于已经取过值的参数之间从缓存中获取
        // 当一个order多个trade时，中间变量都是上一个trade_id的。不能取缓存
        String paramValue = paramsBuffer.getString(param_code);
        if (constantParamsBuffer.getInt("TRADE_SIZE", 1) == 1 && StringUtils.isNotBlank(paramValue))
        {
            return paramValue;
        }

        // 查询得到关于该参数的所有配置
        IDataset infos = UTradeTypePFInfoQry.qryTradePfByDataName(param_code);

        if (IDataUtil.isEmpty(infos))
        {
            CSAppException.apperr(BizException.CRM_BIZ_66, param_code + "取值时,CRM侧获取 TD_S_TRADETYPE_PF_DATA 配置为空");
        }

        // 循环取值
        for (int i = 0; i < infos.size(); i++)
        {
            IData info = infos.getData(i);

            // 获取生效条件表达式
            String expression = info.getString("OPTION_PARAMS");
            // 获取生效条件表达式中的待转换参数，并替换到表达式中
            String[] option_params = getParams(expression);

            List<Variable> variables = new ArrayList<Variable>();
            if (option_params != null)
            {
                for (String optionParam : option_params)
                {
                    // 递归获取子参数
                    if (param_code.equals(optionParam))
                    {
                        // 配置存在问题，导致递归无法进行。
                        CSAppException.apperr(BizException.CRM_BIZ_66, "TD_S_TRADETYPE_PF_DATA.OPTION_PARAMS配置错误");
                    }
                    String optionValue = (String) getSelParamValue(optionParam, constantParamsBuffer, paramsBuffer, dbSrc);
                    // 对$字符的特殊处理
                    if (StringUtils.isNotBlank(optionValue))
                    {
                        if (optionValue.indexOf("\\$") >= 0)
                        {
                            optionValue = optionValue.replaceAll("\\$", "ToBeChangeDollar");
                        }
                    }
                    expression = expression.replaceAll("<<" + optionParam + ">>", "\"" + optionValue + "\"");
                }
            }

            // 执行生效条件表达式
            Object result = ExpressionEvaluator.evaluate(expression, variables);

            if (result instanceof Boolean)
            {
                // 符合生效条件则进行自动取值，若取值不为空则返回，否则继续执行其它符合生效条件的配置记录。
                if ((Boolean) result)
                {
                    // 获取自动取值方式
                    String selMode = info.getString("SEL_MODE");

                    // 固定值直接返回配置值
                    if ("1".equals(selMode))
                    {
                        String optionValueExpression = info.getString("OPTION_VALUE");
                        String[] option_value_params = getParams(optionValueExpression);

                        List<Variable> valueVariables = new ArrayList<Variable>();
                        if (option_value_params != null)
                        {
                            for (String optionValueParam : option_value_params)
                            {
                                String optionValue = null;
                                if (StringUtils.isNotBlank(optionValueParam) && ":".equals(optionValueParam.substring(0, 1)))
                                {
                                    // 递归获取子参数
                                    if (param_code.equals(optionValueParam))
                                    {
                                        // 配置存在问题，导致递归无法进行。
                                        CSAppException.apperr(BizException.CRM_BIZ_66, "TD_S_TRADETYPE_PF_DATA.OPTION_PARAMS配置错误");
                                    }
                                    optionValue = (String) getSelParamValue(optionValueParam, constantParamsBuffer, paramsBuffer, dbSrc);

                                }
                                // 参数为空，则转为字符空
                                if (StringUtils.isBlank(optionValue))
                                {
                                    optionValue = "";
                                }
                                if (optionValue.indexOf("\\$") >= 0)
                                {
                                    optionValue = optionValue.replaceAll("\\$", "ToBeChangeDollar");
                                }
                                optionValueExpression = optionValueExpression.replaceAll("<<" + optionValueParam + ">>", "\"" + optionValue + "\"");

                            }
                        }
                        Object valueResult = null;
                        if (StringUtils.isNotBlank(optionValueExpression))
                        {
                            valueResult = ExpressionEvaluator.evaluate(optionValueExpression, valueVariables);
                        }
                        String valueStr = null;
                        if (valueResult != null && (StringUtils.isNotBlank(valueResult.toString())))
                        {
                            valueStr = "" + valueResult;
                            if (valueStr.indexOf("ToBeChangeDollar") >= 0)
                            {
                                valueStr = valueStr.replaceAll("ToBeChangeDollar", "\\$");
                            }

                            if (constantParamsBuffer.getInt("TRADE_SIZE", 1) == 1)
                            {
                                paramsBuffer.put(param_code, valueStr);
                            }
                            return valueStr;
                        }
                    }else if(StringUtils.equals("2", selMode)){

                        // 查询方式则继续执行。
                        // CODE_CODE表查询用TAB_NAME和SQL_REF
                        String tab_name = info.getString("TAB_NAME");
                        String sql_ref = info.getString("SQL_REF");
                        IData inParams = new DataMap();

                        // 查询参数
                        String selParamsStr = info.getString("QUERY_PARAMS");
                        if (StringUtils.isNotBlank(selParamsStr))
                        {
                            String[] sel_params = getParams(selParamsStr);
                            if (sel_params != null)
                            {
                                for (String selParam : sel_params)
                                {
                                    // 递归获取子参数
                                    if (param_code.equals(selParam))
                                    {
                                        // 配置存在问题，导致递归无法进行。
                                        CSAppException.apperr(BizException.CRM_BIZ_66, "TD_S_TRADETYPE_PF_DATA.OPTION_PARAMS配置错误");
                                    }
                                    String selValue = (String) getSelParamValue(selParam, constantParamsBuffer, paramsBuffer, dbSrc);

                                    selParamsStr = selParamsStr.replaceAll("<<" + selParam + ">>", selValue);
                                }
                            }
                            // 分隔查询条件
                            String[] params = selParamsStr.split(";");
                            for (String param : params)
                            {
                                if (StringUtils.isNotBlank(param) && (param.split("=").length == 2))
                                {
                                    // 分隔查询条件参数名和值
                                    String[] name2value = param.split("=");
                                    inParams.put(name2value[0], name2value[1]);
                                }
                            }
                        }
                        // 获取查询结果
                        IDataset resultinfos = new DatasetList();
//                        String routeIdStr = info.getString("ROUTE_ID");
//                        if (StringUtils.isNotBlank(routeIdStr))
//                        {
//                        	if(StringUtils.equalsIgnoreCase("JOUR", routeIdStr))
//                        	{
//                        		resultinfos = Dao.qry	ByCode(tab_name, sql_ref, inParams, Route.getJourDb());
//                        	}
//                        	else
//                        	{
//                        		resultinfos = Dao.qryByCode(tab_name, sql_ref, inParams, info.getString("ROUTE_ID"));
//                        	}
//                        }
//                        else
//                        {
//                            resultinfos = Dao.qryByCode(tab_name, sql_ref, inParams, dbSrc);
//                        }
                        ICallPfDeal iCallPfDeal = CallPfDealProxy.getInstance(tab_name);//此时tab_name实际为className
                        resultinfos = iCallPfDeal.dealPfData(inParams);
                        String strOptionValue = info.getString("OPTION_VALUE");
                        // OPTION_VALUE值是QUERY_RESULT且有值，则返回整个查询记录
                        if ("QUERY_RESULT".equals(strOptionValue) && IDataUtil.isNotEmpty(resultinfos))
                        {
                            return resultinfos;
                        }

                        String valueStr = "";
                        // 将查询记录数加入结果集，对于查询结果为空的情况，默认增加一条数据，并填入查询记录数为0
                        int size = 0;
                        if ((!"QUERY_RESULT".equals(strOptionValue)) && IDataUtil.isEmpty(resultinfos))
                        {
                            IData nullInfo = new DataMap();
                            nullInfo.put("resultCount", 0);
                            resultinfos.add(nullInfo);
                        }
                        else
                        {
                            size = resultinfos.size();
                        }
                        if (!"QUERY_RESULT".equals(strOptionValue))
                        {
                            for (int j = 0; j < resultinfos.size(); j++)
                            {
                                if (j == 0)
                                { // 不能每行都放这个值，要不然在表达式中取的时候,字符会连起来.超过整数范围时还会报错
                                    resultinfos.getData(j).put("resultCount", size);
                                }
                                resultinfos.getData(j).put("resultIndex", j);
                                String optionValueExpression = strOptionValue;
                                String[] option_value_params = getParams(optionValueExpression);

                                List<Variable> valueVariables = new ArrayList<Variable>();
                                if (option_value_params != null)
                                {
                                    for (String optionValueParam : option_value_params)
                                    {
                                        String optionValue = null;
                                        if (StringUtils.isNotBlank(optionValueParam) && ":".equals(optionValueParam.substring(0, 1)))
                                        {
                                            // 递归获取子参数
                                            if (param_code.equals(optionValueParam))
                                            {
                                                // 配置存在问题，导致递归无法进行。
                                                CSAppException.apperr(BizException.CRM_BIZ_66, "TD_S_TRADETYPE_PF_DATA.OPTION_PARAMS配置错误");
                                            }
                                            optionValue = (String) getSelParamValue(optionValueParam, constantParamsBuffer, paramsBuffer, dbSrc);
                                        }
                                        else
                                        {
                                            // 非“:”开头的参数均认为是可以从查询结果集中获取的字段名
                                            optionValue = resultinfos.getData(j).getString(optionValueParam);
                                        }
                                        if (StringUtils.isBlank(optionValue))
                                        {
                                            optionValue = "";
                                        }
                                        // 对$字符的特殊处理
                                        if (optionValue.indexOf("\\$") >= 0)
                                        {
                                            optionValue = optionValue.replaceAll("\\$", "ToBeChangeDollar");
                                        }
                                        optionValueExpression = optionValueExpression.replaceAll("<<" + optionValueParam + ">>", "\"" + optionValue + "\"");
                                    }
                                }

                                // 执行取值表达式
                                Object valueResult = null;
                                if (StringUtils.isNotBlank(optionValueExpression))
                                {
                                    valueResult = ExpressionEvaluator.evaluate(optionValueExpression, valueVariables);
                                    if (valueResult != null)
                                    {
                                        valueStr += "" + valueResult;
                                        // 对$字符的特殊处理
                                        if (valueStr.indexOf("ToBeChangeDollar") >= 0)
                                        {
                                            valueStr = valueStr.replaceAll("ToBeChangeDollar", "\\$");
                                        }
                                    }
                                }
                            }
                        }
                        if (StringUtils.isNotBlank(valueStr))
                        {
                            if (constantParamsBuffer.getInt("TRADE_SIZE", 1) == 1)
                            {
                                paramsBuffer.put(param_code, valueStr);
                            }
                            return valueStr;
                        }
                    
                    }
                    else
                    {
                        // 查询方式则继续执行。
                        // CODE_CODE表查询用TAB_NAME和SQL_REF
                        String tab_name = info.getString("TAB_NAME");
                        String sql_ref = info.getString("SQL_REF");
                        IData inParams = new DataMap();

                        // 查询参数
                        String selParamsStr = info.getString("QUERY_PARAMS");
                        if (StringUtils.isNotBlank(selParamsStr))
                        {
                            String[] sel_params = getParams(selParamsStr);
                            if (sel_params != null)
                            {
                                for (String selParam : sel_params)
                                {
                                    // 递归获取子参数
                                    if (param_code.equals(selParam))
                                    {
                                        // 配置存在问题，导致递归无法进行。
                                        CSAppException.apperr(BizException.CRM_BIZ_66, "TD_S_TRADETYPE_PF_DATA.OPTION_PARAMS配置错误");
                                    }
                                    String selValue = (String) getSelParamValue(selParam, constantParamsBuffer, paramsBuffer, dbSrc);

                                    selParamsStr = selParamsStr.replaceAll("<<" + selParam + ">>", selValue);
                                }
                            }
                            // 分隔查询条件
                            String[] params = selParamsStr.split(";");
                            for (String param : params)
                            {
                                if (StringUtils.isNotBlank(param) && (param.split("=").length == 2))
                                {
                                    // 分隔查询条件参数名和值
                                    String[] name2value = param.split("=");
                                    inParams.put(name2value[0], name2value[1]);
                                }
                            }
                        }
                        // 获取查询结果
                        IDataset resultinfos = new DatasetList();
                        String routeIdStr = info.getString("ROUTE_ID");
                        if (StringUtils.isNotBlank(routeIdStr))
                        {
                        	if(StringUtils.equalsIgnoreCase("JOUR", routeIdStr))
                        	{
                        		resultinfos = Dao.qryByCode(tab_name, sql_ref, inParams, Route.getJourDb());
                        	}
                        	else
                        	{
                        		resultinfos = Dao.qryByCode(tab_name, sql_ref, inParams, info.getString("ROUTE_ID"));
                        	}
                        }
                        else
                        {
                            resultinfos = Dao.qryByCode(tab_name, sql_ref, inParams, dbSrc);
                        }

                        String strOptionValue = info.getString("OPTION_VALUE");
                        // OPTION_VALUE值是QUERY_RESULT且有值，则返回整个查询记录
                        if ("QUERY_RESULT".equals(strOptionValue) && IDataUtil.isNotEmpty(resultinfos))
                        {
                            return resultinfos;
                        }

                        String valueStr = "";
                        // 将查询记录数加入结果集，对于查询结果为空的情况，默认增加一条数据，并填入查询记录数为0
                        int size = 0;
                        if ((!"QUERY_RESULT".equals(strOptionValue)) && IDataUtil.isEmpty(resultinfos))
                        {
                            IData nullInfo = new DataMap();
                            nullInfo.put("resultCount", 0);
                            resultinfos.add(nullInfo);
                        }
                        else
                        {
                            size = resultinfos.size();
                        }
                        if (!"QUERY_RESULT".equals(strOptionValue))
                        {
                            for (int j = 0; j < resultinfos.size(); j++)
                            {
                                if (j == 0)
                                { // 不能每行都放这个值，要不然在表达式中取的时候,字符会连起来.超过整数范围时还会报错
                                    resultinfos.getData(j).put("resultCount", size);
                                }
                                resultinfos.getData(j).put("resultIndex", j);
                                String optionValueExpression = strOptionValue;
                                String[] option_value_params = getParams(optionValueExpression);

                                List<Variable> valueVariables = new ArrayList<Variable>();
                                if (option_value_params != null)
                                {
                                    for (String optionValueParam : option_value_params)
                                    {
                                        String optionValue = null;
                                        if (StringUtils.isNotBlank(optionValueParam) && ":".equals(optionValueParam.substring(0, 1)))
                                        {
                                            // 递归获取子参数
                                            if (param_code.equals(optionValueParam))
                                            {
                                                // 配置存在问题，导致递归无法进行。
                                                CSAppException.apperr(BizException.CRM_BIZ_66, "TD_S_TRADETYPE_PF_DATA.OPTION_PARAMS配置错误");
                                            }
                                            optionValue = (String) getSelParamValue(optionValueParam, constantParamsBuffer, paramsBuffer, dbSrc);
                                        }
                                        else
                                        {
                                            // 非“:”开头的参数均认为是可以从查询结果集中获取的字段名
                                            optionValue = resultinfos.getData(j).getString(optionValueParam);
                                        }
                                        if (StringUtils.isBlank(optionValue))
                                        {
                                            optionValue = "";
                                        }
                                        // 对$字符的特殊处理
                                        if (optionValue.indexOf("\\$") >= 0)
                                        {
                                            optionValue = optionValue.replaceAll("\\$", "ToBeChangeDollar");
                                        }
                                        optionValueExpression = optionValueExpression.replaceAll("<<" + optionValueParam + ">>", "\"" + optionValue + "\"");
                                    }
                                }

                                // 执行取值表达式
                                Object valueResult = null;
                                if (StringUtils.isNotBlank(optionValueExpression))
                                {
                                    valueResult = ExpressionEvaluator.evaluate(optionValueExpression, valueVariables);
                                    if (valueResult != null)
                                    {
                                        valueStr += "" + valueResult;
                                        // 对$字符的特殊处理
                                        if (valueStr.indexOf("ToBeChangeDollar") >= 0)
                                        {
                                            valueStr = valueStr.replaceAll("ToBeChangeDollar", "\\$");
                                        }
                                    }
                                }
                            }
                        }
                        if (StringUtils.isNotBlank(valueStr))
                        {
                            if (constantParamsBuffer.getInt("TRADE_SIZE", 1) == 1)
                            {
                                paramsBuffer.put(param_code, valueStr);
                            }
                            return valueStr;
                        }
                    }
                }
            }
        }

        return null;
    }
}
