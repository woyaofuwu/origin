
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata.BaseSaleActiveReqData;

public class SaleActiveReqData extends BaseSaleActiveReqData
{

    private String onNetStartDate; // 约定在网开始时间

    private String onNetEndDate; // 约定在网结束时间

    private String payMoneyCode; // 付款方式

    private String deviceModelCode; // 电子商城

    private String resTypeId; // 电子商城

    private String deviceModel; // 电子商城

    private String prodOrderId; // 电子商城

    private boolean isNetStoreActive; // 电子商城

    private String chargeId; // 缴费流水

    private String callType;

    private String schoolName;
    
    private String rsrvStr9;

    private String rsrvStr10;
    
    //宽带开户tradeId,宽带开户同时办理营销活动需要传此值
    private String acceptTradeId;
    
    //宽带开户支付模式：P：预先支付  A：先装后付
    private String widePayMode;
    
    //对于提交后需要动态验证码的验证的营销活动办理，验证成功标记，1-成功
    private String smsVeriSuccess;
	
	//是否需要预占用
	private boolean isNeedPreOccupy;
	
	//对于可以办理信用购机的活动   1:勾选了信用购机.0:没勾选信用购机,正常购机活动
	private String creditPurchases;


	public String getCreditPurchases() {
		return creditPurchases;
	}

	public void setCreditPurchases(String creditPurchases) {
		this.creditPurchases = creditPurchases;
	}

	public String getRsrvStr9() {
		return rsrvStr9;
	}

	public void setRsrvStr9(String rsrvStr9) {
		this.rsrvStr9 = rsrvStr9;
	}
	
	public String getRsrvStr10() {
		return rsrvStr10;
	}

	public void setRsrvStr10(String rsrvStr10) {
		this.rsrvStr10 = rsrvStr10;
	}
    
    public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	private String studentName;
    
    public String getCallType()
    {
        return callType;
    }

    public String getChargeId()
    {
        return chargeId;
    }

    public String getDeviceModel()
    {
        return deviceModel;
    }

    public String getDeviceModelCode()
    {
        return deviceModelCode;
    }

    public String getOnNetEndDate()
    {
        return onNetEndDate;
    }

    public String getOnNetStartDate()
    {
        return onNetStartDate;
    }

    public String getPayMoneyCode()
    {
        return payMoneyCode;
    }

    public String getProdOrderId()
    {
        return prodOrderId;
    }

    public String getResTypeId()
    {
        return resTypeId;
    }

    public boolean isNetStoreActive()
    {
        return isNetStoreActive;
    }

    public void setCallType(String callType)
    {
        this.callType = callType;
    }

    public void setChargeId(String chargeId)
    {
        this.chargeId = chargeId;
    }

    public void setDeviceModel(String deviceModel)
    {
        this.deviceModel = deviceModel;
    }

    public void setDeviceModelCode(String deviceModelCode)
    {
        this.deviceModelCode = deviceModelCode;
    }

    public void setNetStoreActive(boolean isNetStoreActive)
    {
        this.isNetStoreActive = isNetStoreActive;
    }

    public void setOnNetEndDate(String onNetEndDate)
    {
        this.onNetEndDate = onNetEndDate;
    }

    public void setOnNetStartDate(String onNetStartDate)
    {
        this.onNetStartDate = onNetStartDate;
    }

    public void setPayMoneyCode(String payMoneyCode)
    {
        this.payMoneyCode = payMoneyCode;
    }

    public void setProdOrderId(String prodOrderId)
    {
        this.prodOrderId = prodOrderId;
    }

    public void setResTypeId(String resTypeId)
    {
        this.resTypeId = resTypeId;
    }

    public String getAcceptTradeId()
    {
        return acceptTradeId;
    }

    public void setAcceptTradeId(String acceptTradeId)
    {
        this.acceptTradeId = acceptTradeId;
    }

	public String getWidePayMode() {
		return widePayMode;
	}

	public void setWidePayMode(String widePayMode) {
		this.widePayMode = widePayMode;
	}

	public String getSmsVeriSuccess() {
		return smsVeriSuccess;
	}

	public void setSmsVeriSuccess(String smsVeriSuccess) {
		this.smsVeriSuccess = smsVeriSuccess;
	}

	public boolean isNeedPreOccupy() {
		return isNeedPreOccupy;
	}

	public void setNeedPreOccupy(boolean isNeedPreOccupy) {
		this.isNeedPreOccupy = isNeedPreOccupy;
	}
    
}
