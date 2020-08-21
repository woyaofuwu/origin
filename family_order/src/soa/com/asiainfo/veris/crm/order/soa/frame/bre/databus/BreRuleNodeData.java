
package com.asiainfo.veris.crm.order.soa.frame.bre.databus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Copyright: Copyright 2014/5/15 Asiainfo-Linkage
 * 
 * @Description: 共享操作对象实例
 * @author: xiaocl
 */
public class BreRuleNodeData extends AbstractBRESQLRuleNodeData
{

    public static enum RESPONSE_DATA
    {
        OPERATIONTAG("OPERATIONTAG_LIST"), TABLEANME("TABELANME_DATA_0"), WHERE_IFS("WHERE_IF_LIST_0"), TABLEANME_1("TABELANME_DATA_1"), WHERE_IFS_1("WHERE_IF_LIST_1");

        private final String value;

        RESPONSE_DATA(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        public String toString()
        {
            return value;
        }
    }

    // private static ArrayList alOperationSymbol = new ArrayList();
    private static Map<Object, Object> hasmap = new HashMap<Object, Object>();

    private static Map<Object, Object> o = new TreeMap<Object, Object>();

    private static LinkedList<Object> wList = new LinkedList<Object>();

    public static LinkedList getBreList(String name)
    {
        Object value = hasmap.get(name);
        if (value == null)
            return null;
        if (value instanceof LinkedList)
            return (LinkedList) value;
        else
            return null;
    }

    public static HashMap getBreMap(String name)
    {
        Object value = hasmap.get(name);
        if (value == null)
            return null;
        if (value instanceof HashMap)
            return (HashMap) value;
        else
            return null;
    }

    public static HashMap getTabeNames()
    {
        return getBreMap(RESPONSE_DATA.TABLEANME.value);
    }

    public static String getTableNameA()
    {
        return (String) getTabeNames().get("TABLE_NAMEA");
    }

    public static String getTableNameB()
    {
        return (String) getTabeNames().get("TABLE_NAMEB");
    }

    public static LinkedList getWhereIfl()
    {
        return getBreList(RESPONSE_DATA.WHERE_IFS.value);
    }

    public static void setTabeNames(HashMap map)
    {
        getTabeNames().clear();
        hasmap.put("RESPONSE_DATA.TABLEANME.value", map);
    }

    public static void setTableNameA(String table1)
    {
        getTabeNames().put("TABLE_NAMEA", table1);
    }

    public static void setTableNameB(String table2)
    {
        getTabeNames().put("TABLE_NAMEB", table2);
    }

    public static void setWhereIfl(LinkedList _l)
    {

        hasmap.put(RESPONSE_DATA.WHERE_IFS.value, _l);
    }

    public static void setWhereIfs(String strIf)
    {
        wList.add(strIf);
        setWhereIfl(wList);
    }

    // Map oo = Collections.synchronizedMap(new TreeMap<String, String>()) ;
    // private static ArrayList list = new ArrayList();
    public BreRuleNodeData()
    {
        hasmap.put(RESPONSE_DATA.TABLEANME.value, new HashMap());
        hasmap.put(RESPONSE_DATA.WHERE_IFS.value, new LinkedList());
    }

}
