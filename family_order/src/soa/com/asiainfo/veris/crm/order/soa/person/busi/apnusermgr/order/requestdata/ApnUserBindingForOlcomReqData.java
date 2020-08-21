
package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;


public class ApnUserBindingForOlcomReqData extends BaseReqData
{
	
    private String apnName; //APN名称

    private String apnDesc;//APN描述

    private String apnCntxId;//APN的CNTXID

    private String apnTplId;//APNQOS的模板ID

    private String apnType; //PDN IPV4ADD类型
    
    private String apnIPV4Add;//PDN IPV4ADD类型地址

    private String apnInstId;
    
	public String getApnName() {
		return apnName;
	}

	public void setApnName(String apnName) {
		this.apnName = apnName;
	}

	public String getApnDesc() {
		return apnDesc;
	}

	public void setApnDesc(String apnDesc) {
		this.apnDesc = apnDesc;
	}

	public String getApnCntxId() {
		return apnCntxId;
	}

	public void setApnCntxId(String apnCntxId) {
		this.apnCntxId = apnCntxId;
	}

	public String getApnTplId() {
		return apnTplId;
	}

	public void setApnTplId(String apnTplId) {
		this.apnTplId = apnTplId;
	}

	public String getApnType() {
		return apnType;
	}

	public void setApnType(String apnType) {
		this.apnType = apnType;
	}

	public String getApnIPV4Add() {
		return apnIPV4Add;
	}

	public void setApnIPV4Add(String apnIPV4Add) {
		this.apnIPV4Add = apnIPV4Add;
	}

	public String getApnInstId() {
		return apnInstId;
	}

	public void setApnInstId(String apnInstId) {
		this.apnInstId = apnInstId;
	}
	
}
