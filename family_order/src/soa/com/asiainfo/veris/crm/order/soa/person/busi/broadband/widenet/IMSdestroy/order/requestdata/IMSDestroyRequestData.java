
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.IMSdestroy.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @author think
 */
public class IMSDestroyRequestData extends BaseReqData
{

    private String removeReason = "";// 销户原因
    
    private String wideSerialNumber = ""; //宽带对应的手机号码
    
    private String serialNumber = "";  //固话号码
    
	
	public String getRemoveReason() {
		return removeReason;
	}

	public void setRemoveReason(String removeReason) {
		this.removeReason = removeReason;
	}

	public String getWideSerialNumber() {
		return wideSerialNumber;
	}

	public void setWideSerialNumber(String wideSerialNumber) {
		this.wideSerialNumber = wideSerialNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

}
