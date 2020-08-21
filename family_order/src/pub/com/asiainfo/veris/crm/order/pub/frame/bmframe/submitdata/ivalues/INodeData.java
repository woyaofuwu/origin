/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues;

import java.io.Serializable;
import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.valuebean.JsonObject;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: INodeData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-25 上午10:55:40 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-25 chengxf2 v1.0.0 修改原因
 */
public interface INodeData extends Serializable
{

    void addJsonArray(IJsonArray jsonArray);

    void addJsonObject(JsonObject jsonObject);

    void addSubmitData(ISubmitData submitData);

    boolean containsJsonArray(String pJsonName);

    boolean containsJsonObject(String pJsonName);

    IData getAttrData();

    String getAttrValue(String pParaName);

    IJsonArray getJsonArray(String pJsonName);

    List<IJsonArray> getJsonArrayList();

    IJsonObject getJsonObject(String pJsonName);

    List<IJsonObject> getJsonObjectList();

    String getPageId();

    String getPageInstId();

    List<ISubmitData> getSubmitDataList();
}
