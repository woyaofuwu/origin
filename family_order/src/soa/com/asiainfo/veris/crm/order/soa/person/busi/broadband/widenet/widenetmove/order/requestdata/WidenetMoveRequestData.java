
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;

public class WidenetMoveRequestData extends BaseReqData
{
    private String newStandAddress;
    private String newStandAddressCode;
    private String NewAreaCode;
    private String newDetailAddress;
    private String newPhone;
    private String newContact;
    private String newContactPhone;
    private String userIdA;
    private String gponUserId;
    private String gponSerialNumber;
    private String rsrvStr2;
    private String wideType;
    private String newWideType;
    private String deviceId;
    private String userIdMobileA;
    private String suggestDate;
    public String getUserIdMobileA() {
		return userIdMobileA;
	}
	public void setUserIdMobuleA(String userIdMobileA) {
		this.userIdMobileA = userIdMobileA;
	}
    public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
    public String getNewWideType() {
		return newWideType;
	}
	public void setNewWideType(String newWideType) {
		this.newWideType = newWideType;
	}
    public String getWideType() {
		return wideType;
	}
	public void setWideType(String wideType) {
		this.wideType = wideType;
	}
	public String getRsrvStr2() {
		return rsrvStr2;
	}
	public void setRsrvStr2(String rsrvStr2) {
		this.rsrvStr2 = rsrvStr2;
	}
	private String preWideType;
    public String getGponSerialNumber()
    {
        return gponSerialNumber;
    }
    public String getGponUserId()
    {
        return gponUserId;
    }

    public String getNewAreaCode()
    {
        return NewAreaCode;
    }
    public String getNewContact()
    {
        return newContact;
    }
    public String getNewContactPhone()
    {
        return newContactPhone;
    }
    public String getNewDetailAddress()
    {
        return newDetailAddress;
    }
    public String getNewPhone()
    {
        return newPhone;
    }
    public String getNewStandAddress()
    {
        return newStandAddress;
    }
    public String getNewStandAddressCode()
    {
        return newStandAddressCode;
    }
    public String getPreWideType()
    {
        return preWideType;
    }
    public String getUserIdA()
    {
        return userIdA;
    }
    public void setGponSerialNumber(String gponSerialNumber)
    {
        this.gponSerialNumber = gponSerialNumber;
    }
    public void setGponUserId(String gponUserId)
    {
        this.gponUserId = gponUserId;
    }
    public void setNewAreaCode(String newAreaCode)
    {
        NewAreaCode = newAreaCode;
    }
    public void setNewContact(String newContact)
    {
        this.newContact = newContact;
    }
    public void setNewContactPhone(String newContactPhone)
    {
        this.newContactPhone = newContactPhone;
    }
    public void setNewDetailAddress(String newDetailAddress)
    {
        this.newDetailAddress = newDetailAddress;
    }
    public void setNewPhone(String newPhone)
    {
        this.newPhone = newPhone;
    }
    public void setNewStandAddress(String newStandAddress)
    {
        this.newStandAddress = newStandAddress;
    }
    public void setNewStandAddressCode(String newStandAddressCode)
    {
        this.newStandAddressCode = newStandAddressCode;
    }
    public void setPreWideType(String preWideType)
    {
        this.preWideType = preWideType;
    }
    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }
    
    //海南移动宽带业务综合优化项目新增，主要针对移机是附加的产品变更
    protected boolean chgProd = false;// 是否有产品变更
    protected boolean effectNow = false;// 是否立即生效
    protected boolean yearDiscnt = false;// 是否有年套餐
    protected String remark;// 备注
    protected boolean bookingTag = false;// 是否预约：【预约：true】
    protected String bookingDate; // 预约时间
    private ProductData newMainProduct;
    protected List<ProductData> plusProducts;
    protected List<ProductModuleData> productElements;// 本次变化的元素列表
    protected boolean chgModel = false;// 是否变更光猫
    protected String modelMode; // 光猫模式
    protected String modelDeposit; // 光猫押金
    protected String exchangeModel; // 光猫押金
    protected String modelPurchase; // 购买光猫
    protected String modelOldNew;
    protected boolean isBusiness = false;// 是否商务宽带
    
    public String getExchangeModel()
    {
        return exchangeModel;
    }
    public String getBookingDate()
    {
        return bookingDate;
    }
    public ProductData getNewMainProduct()
    {
        return newMainProduct;
    }
    private List<ProductData> getPlusProducts()
    {
        return plusProducts;
    }
    public List<ProductModuleData> getProductElements()
    {
        return productElements;
    }
    public boolean isBookingTag()
    {
        return bookingTag;
    }
    public boolean isEffectNow()
    {
        return effectNow;
    }
    public boolean isChgProd()
    {
        return chgProd;
    }
    public void setExchangeModel(String exchangeModel)
    {
        this.exchangeModel = exchangeModel;
    }
    public void setBookingDate(String bookingDate)
    {
        this.bookingDate = bookingDate;
    }
    public void setBookingTag(boolean bookingTag)
    {
        this.bookingTag = bookingTag;
    }
    public void setYearDiscnt(boolean yearDiscnt)
    {
        this.yearDiscnt = yearDiscnt;
    }
    public void setChgProd(boolean chgProd)
    {
        this.chgProd = chgProd;
    }
    public void setEffectNow(boolean effectNow)
    {
        this.effectNow = effectNow;
    }
    public void setNewMainProduct(String newProductId) throws Exception
    {
        this.newMainProduct = new ProductData(newProductId);
    }
    private void setPlusProducts(List<ProductData> plusProducts)
    {
        this.plusProducts = plusProducts;
    }
    public void setProductElements(List<ProductModuleData> productElements)
    {
        this.productElements = productElements;
    }

    public void setIsBusiness(boolean isBusiness)
    {
        this.isBusiness = isBusiness;
    }
    public boolean isIsBusiness()
    {
        return isBusiness;
    }
    
    //光猫管理
    public boolean isChgModel()
    {
        return chgModel;
    }
    public void setChgModel(boolean chgModel)
    {
        this.chgModel = chgModel;
    }
    public void setModelMode(String modelMode)
    {
        this.modelMode = modelMode;
    }
    public String getModelMode()
    {
        return modelMode;
    }
    public void setModelDeposit(String modelDeposit)
    {
        this.modelDeposit = modelDeposit;
    }
    public String getModelDeposit()
    {
        return modelDeposit;
    }
    public void setModelPurchase(String modelPurchase)
    {
        this.modelPurchase = modelPurchase;
    }
    public String getModelPurchase()
    {
        return modelPurchase;
    }
    public boolean isYearDiscnt(){
    	return yearDiscnt;
    }
    public void setModelOldNew(String modelOldNew)
    {
        this.modelOldNew = modelOldNew;
    }
    public String getModelOldNew()
    {
        return modelOldNew;
    }
    
    //海南移动宽带业务综合优化项目新增，主要针对移机是附加的营销活动
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
    private boolean effActive;
    private boolean effYearDnt;
    private String effActiveInfo;
    private String effYearDiscnt;

    public void setEffActiveInfo(String effActiveInfo)
    {
        this.effActiveInfo = effActiveInfo;
    }
    public String getEffActiveInfo()
    {
        return effActiveInfo;
    }
    public void setEffYearDiscnt(String effYearDiscnt)
    {
        this.effYearDiscnt = effYearDiscnt;
    }
    public String getEffYearDiscnt()
    {
        return effYearDiscnt;
    }
    public void setEffActive(boolean effActive){
    	this.effActive = effActive;
    }
    public boolean isEffActive(){
    	return effActive;
    }
    public void setEffYearDnt(boolean effYearDnt){
    	this.effYearDnt = effYearDnt;
    }
    public boolean isEffYearDnt(){
    	return effYearDnt;
    }
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
    
    protected boolean saleactive = false;// 是否有营销活动
    public boolean isSaleactive()
    {
        return saleactive;
    }
    public void setSaleactive(boolean saleactive)
    {
        this.saleactive = saleactive;
    }

    //全量数据存储
    private String widenetMoveNew;
    public void setWidenetMoveNew(String widenetMoveNew)
    {
        this.widenetMoveNew = widenetMoveNew;
    }
    public String getWidenetMoveNew()
    {
        return this.widenetMoveNew;
    }
	public String getSuggestDate() {
		return suggestDate;
	}
	public void setSuggestDate(String suggestDate) {
		this.suggestDate = suggestDate;
	}
    //BUS201907310012关于开发家庭终端调测费的需求
    private String saleActiveId2;    
    private String saleActiveFee2;
    private boolean saleactive2 = false;// 是否有宽带调试费活动
    public boolean isSaleactive2()
    {
        return saleactive2;
    }
    public void setSaleactive2(boolean saleactive2)
    {
        this.saleactive2 = saleactive2;
    }
    public String getSaleActiveId2()
    {
        return saleActiveId2;
    }
    public void setSaleActiveId2(String saleActiveId2)
    {
        this.saleActiveId2 = saleActiveId2;
    }
    public String getSaleActiveFee2()
    {
        return saleActiveFee2;
    }

    public void setSaleActiveFee2(String saleActiveFee2)
    {
        this.saleActiveFee2 = saleActiveFee2;
    }
    //BUS201907310012关于开发家庭终端调测费的需求

}
