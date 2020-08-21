
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetbook.requsetdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;


public class WideNetBookReqData extends BaseReqData
{
	
	
    private String validCode;//验证码
    
    private String rmark;//备注
    

	public String getValidCode() {
		return validCode;
	}

	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}

	public String getRmark() {
		return rmark;
	}

	public void setRmark(String rmark) {
		this.rmark = rmark;
	}
	
}
