package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class CreateGroupEspReqData extends GroupReqData{
	private String GroupID; // 集团ID
	
	private IData productOrderInfo; //订购信息

	public String getGroupID() {
		return GroupID;
	}

	public void setGroupID(String groupID) {
		GroupID = groupID;
	}

	public IData getProductOrderInfo() {
		return productOrderInfo;
	}

	public void setProductOrderInfo(IData productOrderInfo) {
		this.productOrderInfo = productOrderInfo;
	}

}
