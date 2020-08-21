
package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;


public class FlowExchangeCancelReqData extends BaseReqData
{
	private String discntname;       // 套餐名称

	private String discntcode;       // 套餐编码

	private String itemtype;         // 赠送类别

	private String itemvalue;        // 使用情况

	private String balance;          // 剩余情况
	    
	private String startdate;        // 套餐生效时间
	    
	private String enddate;        	 // 套餐失效时间
	    
	private String carryovertag;     // 是否可结转
	    
	private String detailitem;       // 明细帐目名称
	    
	private String userbegindate;    // 账期开始日
	    
	private String userenddate;      // 账期结束日
	    
	private String resinsid;         // 实例编码
	
	private String returnvalue;         // 实例编码
	

	public String getDiscntname() {
		return discntname;
	}

	public void setDiscntname(String discntname) {
		this.discntname = discntname;
	}

	public String getDiscntcode() {
		return discntcode;
	}

	public void setDiscntcode(String discntcode) {
		this.discntcode = discntcode;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public String getItemvalue() {
		return itemvalue;
	}

	public void setItemvalue(String itemvalue) {
		this.itemvalue = itemvalue;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getCarryovertag() {
		return carryovertag;
	}

	public void setCarryovertag(String carryovertag) {
		this.carryovertag = carryovertag;
	}

	public String getDetailitem() {
		return detailitem;
	}

	public void setDetailitem(String detailitem) {
		this.detailitem = detailitem;
	}

	public String getUserbegindate() {
		return userbegindate;
	}

	public void setUserbegindate(String userbegindate) {
		this.userbegindate = userbegindate;
	}

	public String getUserenddate() {
		return userenddate;
	}

	public void setUserenddate(String userenddate) {
		this.userenddate = userenddate;
	}

	public String getResinsid() {
		return resinsid;
	}

	public void setResinsid(String resinsid) {
		this.resinsid = resinsid;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}   
}
