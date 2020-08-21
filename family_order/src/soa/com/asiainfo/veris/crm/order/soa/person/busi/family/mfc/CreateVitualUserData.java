
package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class CreateVitualUserData extends GroupReqData
{
	private IDataset discntInfo;
	
	private IDataset serviceInfo;
	
	private String tradeId;

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public IDataset getDiscntInfo() {
		return discntInfo;
	}

	public void setDiscntInfo(IDataset discntInfo) {
		this.discntInfo = discntInfo;
	}

	public IDataset getServiceInfo() {
		return serviceInfo;
	}

	public void setServiceInfo(IDataset serviceInfo) {
		this.serviceInfo = serviceInfo;
	}
	

}
