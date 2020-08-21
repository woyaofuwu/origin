
package com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression;

import java.util.HashMap;
import java.util.Map;

import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.auto.expression.datameta.Variable;

/**
 * 表达式上下文变量容器 使用本地线程对象，传递上下文变量映射表
 */
public class VariableContainer
{

    private static ThreadLocal<Map<String, Variable>> variableMapThreadLocal = new ThreadLocal<Map<String, Variable>>();

    public static void addVariable(Variable variable)
    {
        if (variable != null)
        {
            getVariableMap().put(variable.getVariableName(), variable);
        }
    }

    public static Variable getVariable(String variableName)
    {
        if (variableName != null)
        {
            return getVariableMap().get(variableName);
        }
        else
        {
            return null;
        }
    }

    public static Map<String, Variable> getVariableMap()
    {
        Map<String, Variable> variableMap = variableMapThreadLocal.get();
        if (variableMap == null)
        {
            variableMap = new HashMap<String, Variable>();
            variableMapThreadLocal.set(variableMap);
        }
        return variableMap;
    }

    public static Variable removeVariable(String variableName)
    {
        return getVariableMap().remove(variableName);
    }

    public static Variable removeVariable(Variable variable)
    {
        if (variable != null)
        {
            return getVariableMap().remove(variable.getVariableName());
        }
        else
        {
            return null;
        }

    }

    public static void removeVariableMap()
    {
        variableMapThreadLocal.remove();
    }

}
