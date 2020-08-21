package com.asiainfo.veris.crm.order.soa.person.busi.destroyTopSetBox.order.requestdata;


import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class DestroyTopSetBoxRequestData extends BaseReqData{

	private String SerialNumber;
	
	private String isReturnTopsetBox;	
	
	/**
     * 是否是宽带融合开户撤单发起的魔百和退订业务
     */
    private String isMergeWideCancel;

	public String getSerialNumber() {
		return SerialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}

	public String getIsReturnTopsetBox() {
		return isReturnTopsetBox;
	}

	public void setIsReturnTopsetBox(String isReturnTopsetBox) {
		this.isReturnTopsetBox = isReturnTopsetBox;
	}

    public String getIsMergeWideCancel()
    {
        return isMergeWideCancel;
    }

    public void setIsMergeWideCancel(String isMergeWideCancel)
    {
        this.isMergeWideCancel = isMergeWideCancel;
    }
	
}
