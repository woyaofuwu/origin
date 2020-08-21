
package com.asiainfo.veris.crm.order.soa.person.busi.customerclub.order.requsetdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * //REQ201708300021+俱乐部会员页面增加入会协议需求 by mnegqx 20180711
 * @author mqx
 *
 */
public class CustomerClubReqData extends BaseReqData
{
	private String custName;
    
    private String clubType;
    

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getClubType() {
		return clubType;
	}

	public void setClubType(String clubType) {
		this.clubType = clubType;
	}

}
