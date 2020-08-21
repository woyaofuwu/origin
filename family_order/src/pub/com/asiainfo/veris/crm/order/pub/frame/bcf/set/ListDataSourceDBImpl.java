/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ListDataSourceDBImpl.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:33:29 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public class ListDataSourceDBImpl implements ListDataSourceDB
{

    private String listener;

    private String valueAttr;

    private String textAttr;

    private List<Map<String, String>> paraList;

    public ListDataSourceDBImpl()
    {
        this.paraList = new ArrayList<Map<String, String>>();
        this.listener = null;
    }

    public void addPara(String aName, String aType, String aValue)
    {
        Map<String, String> param = new HashMap<String, String>();
        param.put("NAME", aName);
        param.put("TYPE", aType);
        param.put("VALUE", aValue);
        this.paraList.add(param);
    }

    public String getListener()
    {
        return this.listener;
    }

    public int getParaCount()
    {
        if (this.paraList != null)
        {
            return this.paraList.size();
        }
        return 0;
    }

    public String getParaName(int i)
    {
        if (this.paraList.get(i) != null)
        {
            return this.paraList.get(i).get("NAME");
        }
        return "";
    }

    public String getParaType(int i)
    {
        if (this.paraList.get(i) != null)
        {
            return this.paraList.get(i).get("TYPE");
        }
        return "";
    }

    public String getParaValue(int i)
    {
        if (this.paraList.get(i) != null)
        {
            return this.paraList.get(i).get("VALUE");
        }
        return "";
    }

    public String getTextAttr()
    {
        return this.textAttr;
    }

    public String getValueAttr()
    {
        return this.valueAttr;
    }

    public void removePara(int aIndex)
    {
        this.paraList.remove(aIndex);
    }

    public void removePara(String aName)
    {
        for (int i = 0; i < this.paraList.size(); i++)
        {
            String name = this.paraList.get(i).get("NAME");
            if (aName.equals(name))
            {
                this.paraList.remove(i);
                break;
            }
        }
    }

    public void setListener(String listener)
    {
        this.listener = listener;
    }

    public void setTextAttr(String textAttr)
    {
        this.textAttr = textAttr;
    }

    public void setValueAttr(String valueAttr)
    {
        this.valueAttr = valueAttr;
    }
}
