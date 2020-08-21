/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.util;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.ISubmitData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.valuebean.JsonArray;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.valuebean.JsonObject;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.valuebean.NodeDataBean;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.valuebean.SubmitDataBean;

public final class SubmitDataParseUtil
{

    private static final Logger log = Logger.getLogger(SubmitDataParseUtil.class);

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-3-25 下午12:02:51 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-25 chengxf2 v1.0.0 修改原因
     */
    private static NodeDataBean parseNodeData(Element element) throws Exception
    {
        NodeDataBean nodeInfo = new NodeDataBean();
        nodeInfo.setPageId(element.attributeValue("id"));
        nodeInfo.setPageInstId(element.attributeValue("instid"));
        for (Iterator<?> j = element.elementIterator(); j.hasNext();)
        {
            Element nodeElement = (Element) j.next();
            if (nodeElement.getName().equals("ud"))
            {
                String userDataStr = nodeElement.getText();
                nodeInfo.setAttrData(new DataMap(userDataStr));
            }
            if (nodeElement.getName().equals("jsonobject"))
            {
                JsonObject jsonObject = new JsonObject();
                jsonObject.setName(nodeElement.attributeValue("name"));
                jsonObject.setValue(nodeElement.getText());
                nodeInfo.addJsonObject(jsonObject);
            }
            if (nodeElement.getName().equals("jsonarray"))
            {
                JsonArray jsonArray = new JsonArray();
                jsonArray.setName(nodeElement.attributeValue("name"));
                jsonArray.setValue(nodeElement.getText());
                nodeInfo.addJsonArray(jsonArray);
            }
            if (nodeElement.getName().equals("submitdata"))
            {
                ISubmitData childsubmitData = parseSubmitData(nodeElement.asXML());
                nodeInfo.addSubmitData(childsubmitData);
            }
        }
        return nodeInfo;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-25 上午11:02:10 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-25 chengxf2 v1.0.0 修改原因
     */
    public static ISubmitData parseSubmitData(IData request) throws Exception
    {
        // String xmlStr = HttpUtil.getStringFromBufferedReader(reader);
        String xmlStr = request.getString("POST_PARAM");
        return parseSubmitData(xmlStr);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-25 上午11:04:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-25 chengxf2 v1.0.0 修改原因
     */
    public static ISubmitData parseSubmitData(String xmlStr) throws Exception
    {
        ISubmitData submitData = new SubmitDataBean();
        Document doc = DocumentHelper.parseText(xmlStr);
        Element root = doc.getRootElement();
        for (Iterator<?> i = root.elementIterator(); i.hasNext();)
        {
            Element element = (Element) i.next();
            if (element.getName().equals("ud"))
            {
                String userDataStr = element.getText();
                submitData.setAttrData(new DataMap(userDataStr));
            }
            if (element.getName().equals("nodeinfo"))
            {
                NodeDataBean nodeInfo = parseNodeData(element);
                submitData.addNodeInfo(nodeInfo);
            }
        }
        return (ISubmitData) submitData;
    }

}
