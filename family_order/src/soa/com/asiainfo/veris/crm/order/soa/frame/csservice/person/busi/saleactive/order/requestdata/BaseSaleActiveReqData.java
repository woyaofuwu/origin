package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

public class BaseSaleActiveReqData extends BaseReqData
{
    private String productId;

    private String packageId;

    private String campnId;

    private String campnCode;

    private String campnType;

    private String startDate;

    private String endDate;

    private String giftSerialNumber;

    private String saleGoodsImei;

    private String saleStaffId;

    private String netOrderId;
    
    private String iphone6Imei; //IPHONE6活动处理 20141022
    
    private String allMoneyName; //REQ201505150014 20150515 by songlm
    
    private String giftCode; //REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151210
    
    private String imeiCode; //REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151210

    private List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();

    public void addPmd(ProductModuleData pmd)
    {
        this.pmds.add(pmd);
    }

    public String getCampnCode()
    {
        return campnCode;
    }

    public String getCampnId()
    {
        return campnId;
    }

    public String getCampnType()
    {
        return campnType;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getGiftSerialNumber()
    {
        return giftSerialNumber;
    }

    public String getNetOrderId()
    {
        return netOrderId;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public List<ProductModuleData> getPmds()
    {
        return pmds;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getSaleGoodsImei()
    {
        return saleGoodsImei;
    }

    public String getSaleStaffId()
    {
        return saleStaffId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setCampnCode(String campnCode)
    {
        this.campnCode = campnCode;
    }

    public void setCampnId(String campnId)
    {
        this.campnId = campnId;
    }

    public void setCampnType(String campnType)
    {
        this.campnType = campnType;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setGiftSerialNumber(String giftSerialNumber)
    {
        this.giftSerialNumber = giftSerialNumber;
    }

    public void setNetOrderId(String netOrderId)
    {
        this.netOrderId = netOrderId;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setPmds(List<ProductModuleData> pmds)
    {
        this.pmds = pmds;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setSaleGoodsImei(String saleGoodsImei)
    {
        this.saleGoodsImei = saleGoodsImei;
    }

    public void setSaleStaffId(String saleStaffId)
    {
        this.saleStaffId = saleStaffId;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

	public String getIphone6Imei() {
		return iphone6Imei;
	}

	public void setIphone6Imei(String iphone6Imei) {
		this.iphone6Imei = iphone6Imei;
	}

	public String getAllMoneyName() {
		return allMoneyName;
	}

	public void setAllMoneyName(String allMoneyName) {
		this.allMoneyName = allMoneyName;
	}

	public String getGiftCode() {
		return giftCode;
	}

	public void setGiftCode(String giftCode) {
		this.giftCode = giftCode;
	}

	public String getImeiCode() {
		return imeiCode;
	}

	public void setImeiCode(String imeiCode) {
		this.imeiCode = imeiCode;
	}
}