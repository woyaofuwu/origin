package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;

public class MergeWideUserCreateRequestData extends BaseReqData {

    /**
     * 标准地址
     */
    private String standAddress;

    /**
     * 标准地址编码
     */
    private String standAddressCode;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 区分宽带类型
     */
    private String wideType;

    /**
     * 宽带保底优惠
     */
    private String lowDiscntCode;

    /**
     * 区域
     */
    private String areaCode;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 用户密码
     */
    private String userPasswd;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 证件号码
     */
    private String psptId;

    /**
     * 宽带开户时间
     */
    private String openDate;

    /**
     * MODEM方式
     */
    private String modemStyle;

    /**
     * MODEM押金金额
     */
    private int modemDeposit;

    /**
     * MODEM型号
     */
    private String modemNumeric;

    /**
     * 手机用户userId
     */
    private String normalUserId;

    /**
     * 虚拟宽带营销活动ID
     */
    private String saleActiveId;

    /**
     * 虚拟宽带附加营销活动ID
     */
    private String saleActiveIdAttr;

    /**
     * 虚拟魔百和营销活动ID
     */
    private String topSetBoxSaleActiveId;

    /**
     * 营销活动预存
     */
    private String saleActiveFee;

    /**
     * 魔百和营销活动预存
     */
    private String topSetBoxSaleActiveFee;

    /**
     * 建议装机时间
     */
    private String suggestDate;

    /**
     * 手机号码
     */
    private String normalSerialNumber;

    /**
     * 魔百和 产品ID
     */
    private String topSetBoxProductId;

    /**
     * 魔百和 基础包
     */
    private String topSetBoxBasePkgs;

    /**
     * 魔百和 可选包
     */
    private String topSetBoxOptionPkgs;
    
    /**
     * 魔百和 必选优惠包
     */
    private String topSetBoxPlatSvcPkgs;
    
    /**
     * 宽带 可选优惠包
     */
    private String topSetBoxPlatSvcPkgs2;

    /**
     * 魔百和押金
     */
    private int topSetBoxDeposit;

    /**
     * 魔百和是否需要上门服务
     */
    private String artificialServices;

    /**
     * IMS固话号码
     */
    private String fixNumber;

    /**
     * IMS固话产品ID
     */
    private String imsProductId;

    /**
     * IMS营销活动包ID
     */
    private String imsSaleActiveId;

    /**
     * IMS营销活动产品ID
     */
    private String imsSaleActiveProductId;

    /**
     * IMS营销活动预存费用
     */
    private String imsSaleActiveFee;

    /**
     * 和目营销活动包ID
     */
    private String heMuSaleActiveId;

    /**
     * 和目营销活动产品ID
     */
    private String heMuSaleActiveProductId;

    /**
     * 和目营销活动终端串码
     */
    private String heMuResId;

    /**
     * IMS营销活动费用
     */
    private String heMuSaleActiveFee;

    /**
     * 标准地址设备ID
     */
    private String deviceId;

    /**
     * 是否海工商宽带开户
     */
    private String isHGS;

    private String virtualUserId;

    private ProductData mainProduct;

    private String rsrvStr10;

    private List<ProductModuleData> productElements;

    /**
     * 是否是商务宽带
     */
    private boolean businessWide = false;

    /**
     * 宽带开户支付模式：P：立即支付 A：先装后付
     */
    private String widenetPayMode;

    /**
     * 申领的机顶盒数量
     */
    private String topSetBoxNum;

    // REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧 by mqx
    // 增加客戶身份信息，保存到trade_other表,后续调用生成台账使用
    private String custName;

    private String psptTypeCode;

    private String custPsptId;

    private String psptEndDate;

    private String psptAddr;

    private String sex;

    private String birthday;

    private String postAddress;

    private String postCode;

    private String custPhone;

    private String faxNbr;

    private String email;

    private String homeAddress;

    private String workName;

    private String workDepart;

    private String agentCustName;

    private String custContact;

    private String custContactPhone;

    private String agentPsptTypeCode;

    private String agentPsptId;

    private String agentPsptAddr;

    private String legalperson;

    private String startdate;

    private String termstartdate;

    private String termenddate;

    private String callingTypeCode;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    // REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧 by mqx end

    /**
     * 是否铁通迁移固话,1是
     */
    private String isTTtransfer;

    // BUS201907310012关于开发家庭终端调测费的需求
    private String saleActiveId2;

    private String saleActiveFee2;

    private String topSetBoxSaleActiveId2;

    private String topSetBoxSaleActiveFee2;

    // 集团中小企业改造
    private String ecUserId;

    private String ecSerialNumber;

    private String ibsysId;

    private String nodeId;

    private String recordNum;

    private String busiformId;

    public String getBusiformId() {
        return busiformId;
    }

    public void setBusiformId(String busiformId) {
        this.busiformId = busiformId;
    }

    public String getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum;
    }

    public String getIbsysId() {
        return ibsysId;
    }

    public void setIbsysId(String ibsysId) {
        this.ibsysId = ibsysId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getEcUserId() {
        return ecUserId;
    }

    public void setEcUserId(String ecUserId) {
        this.ecUserId = ecUserId;
    }

    public String getEcSerialNumber() {
        return ecSerialNumber;
    }

    public void setEcSerialNumber(String ecSerialNumber) {
        this.ecSerialNumber = ecSerialNumber;
    }

    public String getIsHGS() {
        return isHGS;
    }

    public void setIsHGS(String isHGS) {
        this.isHGS = isHGS;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getContact() {
        return contact;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public String getLowDiscntCode() {
        return lowDiscntCode;
    }

    public ProductData getMainProduct() {
        return mainProduct;
    }

    public String getModemNumeric() {
        return modemNumeric;
    }

    public String getModemStyle() {
        return modemStyle;
    }

    public String getNormalSerialNumber() {
        return normalSerialNumber;
    }

    public String getNormalUserId() {
        return normalUserId;
    }

    public String getOpenDate() {
        return openDate;
    }

    public String getPhone() {
        return phone;
    }

    public List<ProductModuleData> getProductElements() {
        return productElements;
    }

    public String getPsptId() {
        return psptId;
    }

    public String getStandAddress() {
        return standAddress;
    }

    public String getStandAddressCode() {
        return standAddressCode;
    }

    public String getUserPasswd() {
        return userPasswd;
    }

    public String getVirtualUserId() {
        return virtualUserId;
    }

    public String getWideType() {
        return wideType;
    }

    public String getRsrvStr10() {
        return rsrvStr10;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public void setLowDiscntCode(String lowDiscntCode) {
        this.lowDiscntCode = lowDiscntCode;
    }

    public void setMainProduct(String productId) throws Exception {
        this.mainProduct = new ProductData(productId);
    }

    public void setModemNumeric(String modemNumeric) {
        this.modemNumeric = modemNumeric;
    }

    public void setModemStyle(String modemStyle) {
        this.modemStyle = modemStyle;
    }

    public void setNormalSerialNumber(String normalSerialNumber) {
        this.normalSerialNumber = normalSerialNumber;
    }

    public void setNormalUserId(String normalUserId) {
        this.normalUserId = normalUserId;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setProductElements(List<ProductModuleData> productElements) {
        this.productElements = productElements;
    }

    public void setPsptId(String psptId) {
        this.psptId = psptId;
    }

    public void setStandAddress(String standAddress) {
        this.standAddress = standAddress;
    }

    public void setStandAddressCode(String standAddressCode) {
        this.standAddressCode = standAddressCode;
    }

    public void setUserPasswd(String userPasswd) {
        this.userPasswd = userPasswd;
    }

    public void setVirtualUserId(String virtualUserId) {
        this.virtualUserId = virtualUserId;
    }

    public void setWideType(String wideType) {
        this.wideType = wideType;
    }

    public void setRsrvStr10(String rsrvStr10) {
        this.rsrvStr10 = rsrvStr10;
    }

    public int getModemDeposit() {
        return modemDeposit;
    }

    public void setModemDeposit(int modemDeposit) {
        this.modemDeposit = modemDeposit;
    }

    public String getSaleActiveId() {
        return saleActiveId;
    }

    public String getSaleActiveIdAttr() {
        return saleActiveIdAttr;
    }

    public void setSaleActiveId(String saleActiveId) {
        this.saleActiveId = saleActiveId;
    }

    public void setSaleActiveIdAttr(String saleActiveIdAttr) {
        this.saleActiveIdAttr = saleActiveIdAttr;
    }

    public String getSuggestDate() {
        return suggestDate;
    }

    public void setSuggestDate(String suggestDate) {
        this.suggestDate = suggestDate;
    }

    public String getTopSetBoxProductId() {
        return topSetBoxProductId;
    }

    public void setTopSetBoxProductId(String topSetBoxProductId) {
        this.topSetBoxProductId = topSetBoxProductId;
    }

    public String getTopSetBoxBasePkgs() {
        return topSetBoxBasePkgs;
    }

    public void setTopSetBoxBasePkgs(String topSetBoxBasePkgs) {
        this.topSetBoxBasePkgs = topSetBoxBasePkgs;
    }

    public String getTopSetBoxPlatSvcPkgs() {
		return topSetBoxPlatSvcPkgs;
	}

	public void setTopSetBoxPlatSvcPkgs(String topSetBoxPlatSvcPkgs) {
		this.topSetBoxPlatSvcPkgs = topSetBoxPlatSvcPkgs;
	}

	public String getTopSetBoxPlatSvcPkgs2() {
		return topSetBoxPlatSvcPkgs2;
	}

	public void setTopSetBoxPlatSvcPkgs2(String topSetBoxPlatSvcPkgs2) {
		this.topSetBoxPlatSvcPkgs2 = topSetBoxPlatSvcPkgs2;
	}

	public String getTopSetBoxOptionPkgs() {
        return topSetBoxOptionPkgs;
    }

    public void setTopSetBoxOptionPkgs(String topSetBoxOptionPkgs) {
        this.topSetBoxOptionPkgs = topSetBoxOptionPkgs;
    }

    public int getTopSetBoxDeposit() {
        return topSetBoxDeposit;
    }

    public void setTopSetBoxDeposit(int topSetBoxDeposit) {
        this.topSetBoxDeposit = topSetBoxDeposit;
    }

    public String getArtificialServices() {
        return artificialServices;
    }

    public void setArtificialServices(String artificialServices) {
        this.artificialServices = artificialServices;
    }

    public String getTopSetBoxSaleActiveId() {
        return topSetBoxSaleActiveId;
    }

    public void setTopSetBoxSaleActiveId(String topSetBoxSaleActiveId) {
        this.topSetBoxSaleActiveId = topSetBoxSaleActiveId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isBusinessWide() {
        return businessWide;
    }

    public void setBusinessWide(boolean businessWide) {
        this.businessWide = businessWide;
    }

    public String getSaleActiveFee() {
        return saleActiveFee;
    }

    public void setSaleActiveFee(String saleActiveFee) {
        this.saleActiveFee = saleActiveFee;
    }

    public String getTopSetBoxSaleActiveFee() {
        return topSetBoxSaleActiveFee;
    }

    public void setTopSetBoxSaleActiveFee(String topSetBoxSaleActiveFee) {
        this.topSetBoxSaleActiveFee = topSetBoxSaleActiveFee;
    }

    public String getWidenetPayMode() {
        return widenetPayMode;
    }

    public void setWidenetPayMode(String widenetPayMode) {
        this.widenetPayMode = widenetPayMode;
    }

    public String getFixNumber() {
        return fixNumber;
    }

    public void setFixNumber(String fixNumber) {
        this.fixNumber = fixNumber;
    }

    public String getImsProductId() {
        return imsProductId;
    }

    public void setImsProductId(String imsProductId) {
        this.imsProductId = imsProductId;
    }

    public String getImsSaleActiveId() {
        return imsSaleActiveId;
    }

    public void setImsSaleActiveId(String imsSaleActiveId) {
        this.imsSaleActiveId = imsSaleActiveId;
    }

    public String getImsSaleActiveProductId() {
        return imsSaleActiveProductId;
    }

    public void setImsSaleActiveProductId(String imsSaleActiveProductId) {
        this.imsSaleActiveProductId = imsSaleActiveProductId;
    }

    public String getImsSaleActiveFee() {
        return imsSaleActiveFee;
    }

    public void setImsSaleActiveFee(String imsSaleActiveFee) {
        this.imsSaleActiveFee = imsSaleActiveFee;
    }

    public String getHeMuSaleActiveId() {
        return heMuSaleActiveId;
    }

    public void setHeMuSaleActiveId(String heMuSaleActiveId) {
        this.heMuSaleActiveId = heMuSaleActiveId;
    }

    public String getHeMuSaleActiveProductId() {
        return heMuSaleActiveProductId;
    }

    public void setHeMuSaleActiveProductId(String heMuSaleActiveProductId) {
        this.heMuSaleActiveProductId = heMuSaleActiveProductId;
    }

    public String getHeMuResId() {
        return heMuResId;
    }

    public void setHeMuResId(String heMuResId) {
        this.heMuResId = heMuResId;
    }

    public String getHeMuSaleActiveFee() {
        return heMuSaleActiveFee;
    }

    public void setHeMuSaleActiveFee(String heMuSaleActiveFee) {
        this.heMuSaleActiveFee = heMuSaleActiveFee;
    }

    public void setMainProduct(ProductData mainProduct) {
        this.mainProduct = mainProduct;
    }

    public String getTopSetBoxNum() {
        return topSetBoxNum;
    }

    public void setTopSetBoxNum(String topSetBoxNum) {
        this.topSetBoxNum = topSetBoxNum;
    }

    // REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧 by mqx
    // 增加客戶身份信息，保存到trade_other表,后续调用生成台账使用
    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getPsptTypeCode() {
        return psptTypeCode;
    }

    public void setPsptTypeCode(String psptTypeCode) {
        this.psptTypeCode = psptTypeCode;
    }

    public String getCustPsptId() {
        return custPsptId;
    }

    public void setCustPsptId(String custPsptId) {
        this.custPsptId = custPsptId;
    }

    public String getPsptEndDate() {
        return psptEndDate;
    }

    public void setPsptEndDate(String psptEndDate) {
        this.psptEndDate = psptEndDate;
    }

    public String getPsptAddr() {
        return psptAddr;
    }

    public void setPsptAddr(String psptAddr) {
        this.psptAddr = psptAddr;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    public String getFaxNbr() {
        return faxNbr;
    }

    public void setFaxNbr(String faxNbr) {
        this.faxNbr = faxNbr;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getWorkDepart() {
        return workDepart;
    }

    public void setWorkDepart(String workDepart) {
        this.workDepart = workDepart;
    }

    public String getAgentCustName() {
        return agentCustName;
    }

    public void setAgentCustName(String agentCustName) {
        this.agentCustName = agentCustName;
    }

    public String getCustContact() {
        return custContact;
    }

    public void setCustContact(String custContact) {
        this.custContact = custContact;
    }

    public String getCustContactPhone() {
        return custContactPhone;
    }

    public void setCustContactPhone(String custContactPhone) {
        this.custContactPhone = custContactPhone;
    }

    public String getAgentPsptTypeCode() {
        return agentPsptTypeCode;
    }

    public void setAgentPsptTypeCode(String agentPsptTypeCode) {
        this.agentPsptTypeCode = agentPsptTypeCode;
    }

    public String getAgentPsptId() {
        return agentPsptId;
    }

    public void setAgentPsptId(String agentPsptId) {
        this.agentPsptId = agentPsptId;
    }

    public String getAgentPsptAddr() {
        return agentPsptAddr;
    }

    public void setAgentPsptAddr(String agentPsptAddr) {
        this.agentPsptAddr = agentPsptAddr;
    }

    public String getLegalperson() {
        return legalperson;
    }

    public void setLegalperson(String legalperson) {
        this.legalperson = legalperson;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getTermstartdate() {
        return termstartdate;
    }

    public void setTermstartdate(String termstartdate) {
        this.termstartdate = termstartdate;
    }

    public String getTermenddate() {
        return termenddate;
    }

    public void setTermenddate(String termenddate) {
        this.termenddate = termenddate;
    }

    public String getCallingTypeCode() {
        return callingTypeCode;
    }

    public void setCallingTypeCode(String callingTypeCode) {
        this.callingTypeCode = callingTypeCode;
    }

    public String getRsrvStr5() {
        return rsrvStr5;
    }

    public void setRsrvStr5(String rsrvStr5) {
        this.rsrvStr5 = rsrvStr5;
    }

    public String getRsrvStr6() {
        return rsrvStr6;
    }

    public void setRsrvStr6(String rsrvStr6) {
        this.rsrvStr6 = rsrvStr6;
    }

    public String getRsrvStr7() {
        return rsrvStr7;
    }

    public void setRsrvStr7(String rsrvStr7) {
        this.rsrvStr7 = rsrvStr7;
    }

    public String getRsrvStr8() {
        return rsrvStr8;
    }

    public void setRsrvStr8(String rsrvStr8) {
        this.rsrvStr8 = rsrvStr8;
    }

    // REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧 by mqx
    // 增加客戶身份信息，保存到trade_other表,后续调用生成台账使用 end

    public void setIsTTtransfer(String isTTtransfer) {
        this.isTTtransfer = isTTtransfer;
    }

    public String getIsTTtransfer() {
        return isTTtransfer;
    }

    // BUS201907310012关于开发家庭终端调测费的需求
    public String getSaleActiveFee2() {
        return saleActiveFee2;
    }

    public void setSaleActiveFee2(String saleActiveFee2) {
        this.saleActiveFee2 = saleActiveFee2;
    }

    public String getTopSetBoxSaleActiveFee2() {
        return topSetBoxSaleActiveFee2;
    }

    public void setTopSetBoxSaleActiveFee2(String topSetBoxSaleActiveFee2) {
        this.topSetBoxSaleActiveFee2 = topSetBoxSaleActiveFee2;
    }

    public String getSaleActiveId2() {
        return saleActiveId2;
    }

    public void setSaleActiveId2(String saleActiveId2) {
        this.saleActiveId2 = saleActiveId2;
    }

    public String getTopSetBoxSaleActiveId2() {
        return topSetBoxSaleActiveId2;
    }

    public void setTopSetBoxSaleActiveId2(String topSetBoxSaleActiveId2) {
        this.topSetBoxSaleActiveId2 = topSetBoxSaleActiveId2;
    }
    // BUS201907310012关于开发家庭终端调测费的需求
}
