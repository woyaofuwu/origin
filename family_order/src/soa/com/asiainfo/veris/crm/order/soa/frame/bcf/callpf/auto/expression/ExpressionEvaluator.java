
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.Constant;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.Variable;

/**

 */
public class ExpressionEvaluator
{

    /**
     * 逐个添加表达式上下文变量
     * 
     * @param variable
     */
    public static void addVarible(Variable variable)
    {
        // 添加变来到脚本变量容器
        VariableContainer.addVariable(variable);
    }

    /**
     * 批量添加表达式上下文变量
     * 
     * @param variables
     */
    public static void addVaribles(Collection<Variable> variables)
    {
        // 获取上下文的变量，设置到脚本执行器中
        if (variables != null && variables.size() > 0)
        {
            for (Variable var : variables)
            {
                // 添加变来到脚本变量容器
                VariableContainer.addVariable(var);
            }
        }
    }

    /**
     * 验证表达式
     * 
     * @param expression
     * @return
     * @throws Exception
     */
    public static String compile(String expression) throws Exception
    {
        return compile(expression, null);
    }

    /**
     * 验证表达式
     * 
     * @param expression
     * @param variables
     * @return
     * @throws Exception
     */
    public static String compile(String expression, Collection<Variable> variables) throws Exception
    {
        if (expression == null)
        {
            throw new RuntimeException("表达式为空");
        }

        ExpressionExecutor ee = new ExpressionExecutor();
        try
        {
            // 获取上下文的变量，设置到脚本执行器中
            if (variables != null && variables.size() > 0)
            {
                for (Variable var : variables)
                {
                    // 添加变来到脚本变量容器
                    VariableContainer.addVariable(var);
                }
            }
            // 解析表达式词元
            List<ExpressionToken> expTokens = ee.analyze(expression);
            // 转化RPN，并验证
            expTokens = ee.compile(expTokens);
            // 以字符串形式输出RPN
            return ee.tokensToString(expTokens);
        }
        finally
        {
            // 释放脚本变量容器
            VariableContainer.removeVariableMap();
        }
    }

    /**
     * 执行无变量表达式
     * 
     * @param expression
     * @return
     * @throws Exception
     */
    public static Object evaluate(String expression) throws Exception
    {
        return evaluate(expression, null);
    }

    /**
     * 根据流程上下文，执行公式语言
     * 
     * @param expression
     * @param variables
     * @return
     * @throws Exception
     */
    public static Object evaluate(String expression, Collection<Variable> variables) throws Exception
    {
        if (expression == null)
        {
            return null;
        }

        ExpressionExecutor ee = new ExpressionExecutor();
        try
        {
            // 获取上下文的变量，设置到脚本执行器中
            if (variables != null && variables.size() > 0)
            {
                for (Variable var : variables)
                {
                    // 添加变来到脚本变量容器
                    VariableContainer.addVariable(var);
                }
            }
            // 解析表达式词元
            List<ExpressionToken> expTokens = ee.analyze(expression);
            // 转化RPN，并验证
            expTokens = ee.compile(expTokens);
            // 执行RPN
            Constant constant = ee.execute(expTokens);
            return constant.toJavaObject();

        }
        catch (IllegalExpressionException e)
        {
            e.printStackTrace();
            throw new RuntimeException("表达式：\"" + expression + "\" 执行异常");
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            throw new RuntimeException("表达式：\"" + expression + "\" 执行异常");
        }
        finally
        {
            // 释放脚本变量容器
            VariableContainer.removeVariableMap();
        }
    }

}
