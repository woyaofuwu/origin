
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;

public class WideUserCreateRequestData extends BaseReqData
{
    private String standAddress; // 标准地址

    private String standAddressCode; // 标准地址编码

    private String detailAddress; // 详细地址

    private String wideType;// 区分宽带类型

    private String preWideType; // 子账号类型

    private String lowDiscntCode; // 宽带保底优惠

    private String areaCode;

    private String contactPhone; // 联系人电话

    private String userPasswd; // 用户密码

    private String contact; // 联系人

    private String phone; // 联系电话

    private String psptId; // 证件号码

    private String openDate; // 宽带开户时间

    private String modemStyle; // MODEM方式

    private String modemNumeric;// MODEM型号

    private String studentNumber;// 学生学号

    private String normalUserId; // 手机号码userId

    private String normalSerialNumber; // 手机号码

    private String virtualUserId;

    private ProductData mainProduct;

    private String userIdA;

    private String gponUserId;

    private String gponSerialNumber;
    
	private String rsrvStr10;

    private List<ProductModuleData> productElements;

    public String getAreaCode()
    {
        return areaCode;
    }

    public String getContact()
    {
        return contact;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public String getDetailAddress()
    {
        return detailAddress;
    }

    public String getGponSerialNumber()
    {
        return gponSerialNumber;
    }

    public String getGponUserId()
    {
        return gponUserId;
    }

    public String getLowDiscntCode()
    {
        return lowDiscntCode;
    }

    public ProductData getMainProduct()
    {
        return mainProduct;
    }

    public String getModemNumeric()
    {
        return modemNumeric;
    }

    public String getModemStyle()
    {
        return modemStyle;
    }

    public String getNormalSerialNumber()
    {
        return normalSerialNumber;
    }

    public String getNormalUserId()
    {
        return normalUserId;
    }

    public String getOpenDate()
    {
        return openDate;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getPreWideType()
    {
        return preWideType;
    }

    public List<ProductModuleData> getProductElements()
    {
        return productElements;
    }

    public String getPsptId()
    {
        return psptId;
    }

    public String getStandAddress()
    {
        return standAddress;
    }

    public String getStandAddressCode()
    {
        return standAddressCode;
    }

    public String getStudentNumber()
    {
        return studentNumber;
    }

    public String getUserIdA()
    {
        return userIdA;
    }

    public String getUserPasswd()
    {
        return userPasswd;
    }

    public String getVirtualUserId()
    {
        return virtualUserId;
    }

    public String getWideType()
    {
        return wideType;
    }

    public String getRsrvStr10() {
		return rsrvStr10;
	}
    
    public void setAreaCode(String areaCode)
    {
        this.areaCode = areaCode;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public void setDetailAddress(String detailAddress)
    {
        this.detailAddress = detailAddress;
    }

    public void setGponSerialNumber(String gponSerialNumber)
    {
        this.gponSerialNumber = gponSerialNumber;
    }

    public void setGponUserId(String gponUserId)
    {
        this.gponUserId = gponUserId;
    }

    public void setLowDiscntCode(String lowDiscntCode)
    {
        this.lowDiscntCode = lowDiscntCode;
    }

    public void setMainProduct(String productId) throws Exception
    {
        this.mainProduct = new ProductData(productId);
    }

    public void setModemNumeric(String modemNumeric)
    {
        this.modemNumeric = modemNumeric;
    }

    public void setModemStyle(String modemStyle)
    {
        this.modemStyle = modemStyle;
    }

    public void setNormalSerialNumber(String normalSerialNumber)
    {
        this.normalSerialNumber = normalSerialNumber;
    }

    public void setNormalUserId(String normalUserId)
    {
        this.normalUserId = normalUserId;
    }

    public void setOpenDate(String openDate)
    {
        this.openDate = openDate;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setPreWideType(String preWideType)
    {
        this.preWideType = preWideType;
    }

    public void setProductElements(List<ProductModuleData> productElements)
    {
        this.productElements = productElements;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setStandAddress(String standAddress)
    {
        this.standAddress = standAddress;
    }

    public void setStandAddressCode(String standAddressCode)
    {
        this.standAddressCode = standAddressCode;
    }

    public void setStudentNumber(String studentNumber)
    {
        this.studentNumber = studentNumber;
    }

    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }

    public void setUserPasswd(String userPasswd)
    {
        this.userPasswd = userPasswd;
    }

    public void setVirtualUserId(String virtualUserId)
    {
        this.virtualUserId = virtualUserId;
    }

    public void setWideType(String wideType)
    {
        this.wideType = wideType;
    }

	public void setRsrvStr10(String rsrvStr10) {
		this.rsrvStr10 = rsrvStr10;
	}

}
