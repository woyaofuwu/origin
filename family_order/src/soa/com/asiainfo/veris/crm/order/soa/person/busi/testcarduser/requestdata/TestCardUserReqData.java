
package com.asiainfo.veris.crm.order.soa.person.busi.testcarduser.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * @author zhuoyingzhi
 * 20160926
 *
 */
public class TestCardUserReqData extends BaseReqData
{
	
	private String serialNumber;//手机号码
	
	private String rsrvValue;//测试卡类型


	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getRsrvValue() {
		return rsrvValue;
	}

	public void setRsrvValue(String rsrvValue) {
		this.rsrvValue = rsrvValue;
	}

}
