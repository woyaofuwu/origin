
package com.asiainfo.veris.crm.order.soa.person.busi.oceanfront.requsetdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;


public class OceanFrontReqData extends BaseReqData
{
	
	//报停\报开类型
    private String openStopType;//0报停    1 报开
    
    private String oceanRmark;//备注
    

	public String getOpenStopType() {
		return openStopType;
	}

	public void setOpenStopType(String openStopType) {
		this.openStopType = openStopType;
	}

	public String getOceanRmark() {
		return oceanRmark;
	}

	public void setOceanRmark(String oceanRmark) {
		this.oceanRmark = oceanRmark;
	}
	
}
