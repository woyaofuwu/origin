package com.asiainfo.veris.crm.order.soa.person.busi.data.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class DataDonateRequestData extends BaseReqData {
	    private String objSerialNumber;// 被转赠号码

	    private String donateData;// 转赠流量
	    
	    private String commID;// 商品ID
	    
	    private String dataType;// 转赠类型
	    
	    private String balance;// 用户流量余额
	    
	    private String effectiveDate;// 生效时间
	    
	    private String expireDate;// 失效时间
	    
	    private String discntCode;// 失效时间

	    public String getObjSerialNumber()
	    {
	        return objSerialNumber;
	    }

	    public String getDonateData()
	    {
	        return donateData;
	    }

	    public void setObjSerialNumber(String objSerialNumber)
	    {
	        this.objSerialNumber = objSerialNumber;
	    }

	    public void setDonateData(String donateData)
	    {
	        this.donateData = donateData;
	    }
	    
	    public String getCommID()
	    {
	        return commID;
	    }

	    public void setCommID(String commID)
	    {
	        this.commID = commID;
	    }
	    
	    public String getDataType()
	    {
	        return dataType;
	    }
	    public void setDataType(String dataType)
	    {
	        this.dataType = dataType;
	    }
	    
	    public void setEffectiveDate(String effectiveDate)
	    {
	        this.effectiveDate = effectiveDate;
	    }
	    
	    public String getEffectiveDate()
	    {
	        return effectiveDate;
	    }
	    
	    public String getExpireDate()
	    {
	        return expireDate;
	    }

	    public void setExpireDate(String expireDate)
	    {
	        this.expireDate = expireDate;
	    }
	    
	    public String getBalance()
	    {
	        return balance;
	    }

	    public void setBalance(String balance)
	    {
	        this.balance = balance;
	    }
	    public String getDiscntCode()
	    {
	        return discntCode;
	    }

	    public void setDiscntCode(String discntCode)
	    {
	        this.discntCode = discntCode;
	    }
}
