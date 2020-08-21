/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.valuebean;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.IJsonArray;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.IJsonObject;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.INodeData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.ISubmitData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: NodeDataBean.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-25 上午10:59:41 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-25 chengxf2 v1.0.0 修改原因
 */
public class NodeDataBean implements INodeData
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String pageId;

    private String pageInstId;

    private IData attrData = new DataMap();

    private List<IJsonObject> jsonObjectList = new ArrayList<IJsonObject>();

    private List<IJsonArray> jsonArrayList = new ArrayList<IJsonArray>();

    private List<ISubmitData> submitDataList = new ArrayList<ISubmitData>();

    public void addJsonArray(IJsonArray jsonArray)
    {
        this.jsonArrayList.add(jsonArray);
    }

    public void addJsonObject(JsonObject jsonObject)
    {
        this.jsonObjectList.add(jsonObject);
    }

    public void addSubmitData(ISubmitData submitData)
    {
        this.submitDataList.add(submitData);
    }

    public boolean containsJsonArray(String pJsonName)
    {
        for (int i = 0; i < this.jsonArrayList.size(); i++)
        {
            IJsonArray jsonArray = this.jsonArrayList.get(i);
            if (jsonArray.getName().equals(pJsonName))
            {
                return true;
            }
        }
        return false;
    }

    public boolean containsJsonObject(String pJsonName)
    {
        for (int i = 0; i < this.jsonObjectList.size(); i++)
        {
            IJsonObject jsonObject = this.jsonObjectList.get(i);
            if (jsonObject.getName().equals(pJsonName))
            {
                return true;
            }
        }
        return false;
    }

    public IData getAttrData()
    {
        return this.attrData;
    }

    public String getAttrValue(String pParaName)
    {
        return this.attrData.getString(pParaName);
    }

    public IJsonArray getJsonArray(String pJsonName)
    {
        for (int i = 0; i < this.jsonArrayList.size(); i++)
        {
            if (this.jsonArrayList.get(i).getName().equals(pJsonName))
            {
                return this.jsonArrayList.get(i);
            }
        }
        return null;
    }

    public List<IJsonArray> getJsonArrayList()
    {
        return this.jsonArrayList;
    }

    public IJsonObject getJsonObject(String pJsonName)
    {
        for (int i = 0; i < this.jsonObjectList.size(); i++)
        {
            if (this.jsonObjectList.get(i).getName().equals(pJsonName))
            {
                return this.jsonObjectList.get(i);
            }
        }
        return null;
    }

    public List<IJsonObject> getJsonObjectList()
    {
        return this.jsonObjectList;
    }

    public String getPageId()
    {
        return this.pageId;
    }

    public String getPageInstId()
    {
        return this.pageInstId;
    }

    public List<ISubmitData> getSubmitDataList()
    {
        return this.submitDataList;
    }

    public void setAttrData(IData attrData)
    {
        this.attrData = attrData;
    }

    public void setAttrValue(String pName, String pValue)
    {
        this.attrData.put(pName, pValue);
    }

    public void setPageId(String pageId)
    {
        this.pageId = pageId;
    }

    public void setPageInstId(String pageInstId)
    {
        this.pageInstId = pageInstId;
    }

}
