
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;

public class SpecialChangeProductReqData extends BaseChangeProductReqData
{

    protected String flowItems; //流量分配明细
	
    protected String groupId;// 集团编码
    
    protected String outOrderId;// 外部订单流水
    
    public String getGroupId()
    {
        return groupId;
    }
    
    public String getOutOrderId()
    {
        return outOrderId;
    }

    public String setGroupID(String groupId)
    {
        return this.groupId = groupId;
    }
    
    public String setOutOrderId(String outOrderId)
    {
        return this.outOrderId = outOrderId;
    }

    
    public String getFlowItems()
    {
        return flowItems;
    }

    public String setFlowItems(String flowItems)
    {
        return this.flowItems = flowItems;
    }

}
