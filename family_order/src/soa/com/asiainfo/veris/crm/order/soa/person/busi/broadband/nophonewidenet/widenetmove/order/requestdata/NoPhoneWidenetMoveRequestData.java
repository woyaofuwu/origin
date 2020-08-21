package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;

public class NoPhoneWidenetMoveRequestData extends BaseReqData
{
    private String newStandAddress;//标准地址
    private String newStandAddressCode;//标准地址CODE
    private String newDetailAddress;//详细地址
    private String newAreaCode;//地区，AREA_CODE
    private String newContact;//联系人
    private String newPhone;//联系电话
    private String newContactPhone;//联系人电话
    private String rsrvStr2;//作用未知，貌似是老宽带的productMode
    private String wideType;//作用未知，貌似是老的宽带类型
    private String newWideType;//移机后的新宽带类型   1-移动FTTB 2-铁通ADSL 3-移动FTTH 5-铁通FTTH 6-铁通FTTB
    private String deviceId;//设备号
    
    public String getNewStandAddress()
    {
        return newStandAddress;
    }
    
    public void setNewStandAddress(String newStandAddress)
    {
        this.newStandAddress = newStandAddress;
    }
    
    public String getNewStandAddressCode()
    {
        return newStandAddressCode;
    }
    
    public void setNewStandAddressCode(String newStandAddressCode)
    {
        this.newStandAddressCode = newStandAddressCode;
    }
    
    public String getNewDetailAddress()
    {
        return newDetailAddress;
    }
    
    public void setNewDetailAddress(String newDetailAddress)
    {
        this.newDetailAddress = newDetailAddress;
    }
    
    public String getNewAreaCode()
    {
        return newAreaCode;
    }
    
    public void setNewAreaCode(String newAreaCode)
    {
    	this.newAreaCode = newAreaCode;
    }
    
    public String getNewContact()
    {
        return newContact;
    }
    
    public void setNewContact(String newContact)
    {
        this.newContact = newContact;
    }
    
    public String getNewPhone()
    {
        return newPhone;
    }
    
    public void setNewPhone(String newPhone)
    {
        this.newPhone = newPhone;
    }
    
    public String getNewContactPhone()
    {
        return newContactPhone;
    }
    
    public void setNewContactPhone(String newContactPhone)
    {
        this.newContactPhone = newContactPhone;
    }
    
    public String getRsrvStr2() {
		return rsrvStr2;
	}
    
	public void setRsrvStr2(String rsrvStr2) {
		this.rsrvStr2 = rsrvStr2;
	}
    
	public String getWideType() {
		return wideType;
	}
	
	public void setWideType(String wideType) {
		this.wideType = wideType;
	}
	
	public String getNewWideType() {
		return newWideType;
	}
	
	public void setNewWideType(String newWideType) {
		this.newWideType = newWideType;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
    
	
    //海南移动宽带业务综合优化项目新增，主要针对移机是附加的产品变更
    protected boolean chgProd = false;//是否有产品变更
    protected boolean effectNow = false;//是否立即生效
    protected boolean bookingTag = false;//是否预约：【预约：true】
    protected boolean chgModel = false;//是否变更光猫
    
    protected String bookingDate;//预约时间
    protected String modelMode;//光猫模式
    protected String modelDeposit;//光猫押金
    protected String exchangeModel;//是否更换光猫
    protected String modelPurchase;//购买光猫
    
    protected List<ProductModuleData> productElements;// 本次变化的元素列表
    private ProductData newMainProduct;
    
    public boolean isChgProd()
    {
        return chgProd;
    }
    public void setChgProd(boolean chgProd)
    {
        this.chgProd = chgProd;
    }
    
    public boolean isEffectNow()
    {
        return effectNow;
    }
    public void setEffectNow(boolean effectNow)
    {
        this.effectNow = effectNow;
    }
    
    public boolean isBookingTag()
    {
        return bookingTag;
    }
    public void setBookingTag(boolean bookingTag)
    {
        this.bookingTag = bookingTag;
    }
    
    public boolean isChgModel()
    {
        return chgModel;
    }
    public void setChgModel(boolean chgModel)
    {
        this.chgModel = chgModel;
    }
    
    public String getBookingDate()
    {
        return bookingDate;
    }
    public void setBookingDate(String bookingDate)
    {
        this.bookingDate = bookingDate;
    }
    
    public String getModelMode()
    {
        return modelMode;
    }
    public void setModelMode(String modelMode)
    {
        this.modelMode = modelMode;
    }
    
    public String getModelDeposit()
    {
        return modelDeposit;
    }
    public void setModelDeposit(String modelDeposit)
    {
        this.modelDeposit = modelDeposit;
    }
    
    public String getExchangeModel()
    {
        return exchangeModel;
    }
    public void setExchangeModel(String exchangeModel)
    {
        this.exchangeModel = exchangeModel;
    }
    
    public String getModelPurchase()
    {
        return modelPurchase;
    }
    public void setModelPurchase(String modelPurchase)
    {
        this.modelPurchase = modelPurchase;
    }
    
    public List<ProductModuleData> getProductElements()
    {
        return productElements;
    }
    public void setProductElements(List<ProductModuleData> productElements)
    {
        this.productElements = productElements;
    }
    
    public ProductData getNewMainProduct()
    {
        return newMainProduct;
    }
    public void setNewMainProduct(String newProductId) throws Exception
    {
        this.newMainProduct = new ProductData(newProductId);
    }

    //全量数据存储
    private String widenetMoveNew;
    
    public String getWidenetMoveNew()
    {
        return this.widenetMoveNew;
    }
    public void setWidenetMoveNew(String widenetMoveNew)
    {
        this.widenetMoveNew = widenetMoveNew;
    }
    
}
