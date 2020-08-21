
package com.asiainfo.veris.crm.order.soa.group.gfffmanage;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class GfffUserMaxGprsSetReqData extends GroupReqData
{

    private String gprsMax;
    private String otherInstId;
	public String getGprsMax() {
		return gprsMax;
	}
	public void setGprsMax(String gprsMax) {
		this.gprsMax = gprsMax;
	}
	public String getOtherInstId() {
		return otherInstId;
	}
	public void setOtherInstId(String otherInstId) {
		this.otherInstId = otherInstId;
	}
	
}
