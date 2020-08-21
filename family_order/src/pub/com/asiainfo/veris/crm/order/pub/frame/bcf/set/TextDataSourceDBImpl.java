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
 * @ClassName: TextDataSourceDBImpl.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-2 上午10:50:16 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-2 chengxf2 v1.0.0 修改原因
 */

public class TextDataSourceDBImpl implements TextDataSourceDB
{

    private List<Map<String, String>> paraList;

    private String methodName;

    public TextDataSourceDBImpl()
    {
        this.paraList = new ArrayList<Map<String, String>>();
        this.methodName = null;
    }

    public void addPara(String aName, String aType, String aValue)
    {
        Map<String, String> param = new HashMap<String, String>();
        param.put("NAME", aName);
        param.put("TYPE", aType);
        param.put("VALUE", aValue);
        this.paraList.add(param);
    }

    public String getMethodName()
    {
        return this.methodName;
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

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }
}
