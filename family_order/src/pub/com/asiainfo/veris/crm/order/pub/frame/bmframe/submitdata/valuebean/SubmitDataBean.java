/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.valuebean;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.INodeData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.ISubmitData;

public class SubmitDataBean implements ISubmitData
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private IData attrData = new DataMap();

    private List<INodeData> nodeInfoList = new ArrayList<INodeData>();

    public void addNodeInfo(INodeData nodeInfo)
    {
        this.nodeInfoList.add(nodeInfo);
    }

    public IData getAttrData()
    {
        return this.attrData;
    }

    public String getAttrValue(String attrCode)
    {
        return this.attrData.getString(attrCode);
    }

    public List<INodeData> getNodeDataList()
    {
        return this.nodeInfoList;
    }

    public void setAttrData(IData attrData)
    {
        this.attrData = attrData;
    }
}
