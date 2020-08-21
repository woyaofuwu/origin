package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.requestdata;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;

public class IMSLandLineRequestData extends BaseReqData
{
	private List<ProductModuleData> productElements;
	
	/**
     * 用户密码
     */
    private String userPasswd;
    
    private String usermPasswd;
    
    private ProductData mainProduct;
    
    /**
     *  宽带开户时间
     */
    private String openDate;
    
    /**
     * 手机用户userId
     */
    private String normalUserId;
    
    /**
     * 手机号码
     */
    private String normalSerialNumber;
    
    private String virtualUserId;
    
    /**
     * 宽带保底优惠
     */
    private String lowDiscntCode; 
    
    private String imsProductName;
    
    private String wideProductName;
    
    /**
     * 是否是宽带融合开户调用IMS固话开户  1：是
     */
    private String isMergeWideUserCreate;
    
    private String isTTtransfer;//是否铁通迁移固话,1是
    //家庭项目新增 start
    private String familyImsRoleCode;//固话角色

    private String deviceId ;

    private String wideType;

    private String areaCode;

    private String standAddressCode;

    private String standAddress;

    private String detailAddress;

    private String contact;

    private String contactPhone;

    private String phone;
    //家庭项目新增 end

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getWideType() {
        return wideType;
    }

    public void setWideType(String wideType) {
        this.wideType = wideType;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getStandAddressCode() {
        return standAddressCode;
    }

    public void setStandAddressCode(String standAddressCode) {
        this.standAddressCode = standAddressCode;
    }

    public String getStandAddress() {
        return standAddress;
    }

    public void setStandAddress(String standAddress) {
        this.standAddress = standAddress;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFamilyImsRoleCode() {
        return familyImsRoleCode;
    }

    public void setFamilyImsRoleCode(String familyImsRoleCode) {
        this.familyImsRoleCode = familyImsRoleCode;
    }

    public List<ProductModuleData> getProductElements()
    {
        return productElements;
    }
	
	public String getUserPasswd()
    {
        return userPasswd;
    }
	
	public String getUsermPasswd()
    {
        return usermPasswd;
    }
	
	public ProductData getMainProduct()
    {
        return mainProduct;
    }
	
	public String getOpenDate()
    {
        return openDate;
    }
		
	public String getNormalUserId()
    {
        return normalUserId;
    }
	
	public String getNormalSerialNumber()
    {
        return normalSerialNumber;
    }
	
	public String getVirtualUserId()
    {
        return virtualUserId;
    }
	
	public String getLowDiscntCode()
    {
        return lowDiscntCode;
    }
	
	public String getIMSProductName()
    {
        return imsProductName;
    }
	
	public String getWideProductName()
    {
        return wideProductName;
    }
	
	public void setProductElements(List<ProductModuleData> productElements)
    {
        this.productElements = productElements;
    }
	
	public void setUserPasswd(String userPasswd)
    {
        this.userPasswd = userPasswd;
    }
	
	public void setUsermPasswd(String usermPasswd)
    {
        this.usermPasswd = usermPasswd;
    }
	
	public void setMainProduct(String productId) throws Exception
    {
        this.mainProduct = new ProductData(productId);
    }
	
	public void setOpenDate(String openDate)
    {
        this.openDate = openDate;
    }
	
	public void setNormalUserId(String normalUserId)
    {
        this.normalUserId = normalUserId;
    }
	
	public void setNormalSerialNumber(String normalSerialNumber)
    {
        this.normalSerialNumber = normalSerialNumber;
    }
	
	public void setVirtualUserId(String virtualUserId)
    {
        this.virtualUserId = virtualUserId;
    }
	
	public void setLowDiscntCode(String lowDiscntCode)
    {
        this.lowDiscntCode = lowDiscntCode;
    }
	
	public void setIMSProductName(String imsProductName)
    {
        this.imsProductName = imsProductName;
    }
	
	public void setWideProductName(String wideProductName)
    {
        this.wideProductName = wideProductName;
    }
	
	/**
     * 魔百和营销活动产品编码
     */
    private String moProductId;
    
    public String getMoProductId() {
		return moProductId;
	}

	public void setMoProductId(String moProductId) {
		this.moProductId = moProductId;
	}
    
    /**
     * 魔百和营销活动包编码
     */
    private String moPackageId;

	public String getMoPackageId() {
		return moPackageId;
	}

	public void setMoPackageId(String moPackageId) {
		this.moPackageId = moPackageId;
	}
    
    /**
     * 魔百和营销活动预存
     */
    private String moFee;
    
    public String getMoFee() {
		return moFee;
	}

	public void setMoFee(String moFee) {
		this.moFee = moFee;
	}

	public String getIsMergeWideUserCreate() {
		return isMergeWideUserCreate;
	}

	public void setIsMergeWideUserCreate(String isMergeWideUserCreate) {
		this.isMergeWideUserCreate = isMergeWideUserCreate;
	}

	public void setIsTTtransfer(String isTTtransfer) {
		this.isTTtransfer = isTTtransfer;
	}

	public String getIsTTtransfer() {
		return isTTtransfer;
	}
	
	public String resId;//终端串号

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}
	
}
