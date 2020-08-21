
package com.asiainfo.veris.crm.order.soa.group.bindbroadbandmgr;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

/**
 * @author think
 *
 */
public class BindBroadBandReqData extends MemberReqData
{
	private String serialNumber;

    private String kdSerialNumber;

    private String bindTag;

    private String oldKdSerialNumber;
    
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getKdSerialNumber() {
		return kdSerialNumber;
	}

	public void setKdSerialNumber(String kdSerialNumber) {
		this.kdSerialNumber = kdSerialNumber;
	}

	public String getBindTag() {
		return bindTag;
	}

	public void setBindTag(String bindTag) {
		this.bindTag = bindTag;
	}

	public String getOldKdSerialNumber() {
		return oldKdSerialNumber;
	}

	public void setOldKdSerialNumber(String oldKdSerialNumber) {
		this.oldKdSerialNumber = oldKdSerialNumber;
	}
	
}
