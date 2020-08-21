
package com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ChangeCustOwnerReqData extends BaseReqData
{

    /******** 新客户资料 **************/
    private String newCustId;// 新生成 的客户ID，在trade过程中产生的

    private String newCustName;// 新客户名称

    private String newPsptTypeCode;// 新证件类型

    private String newPsptId;// 新证件类型号码

    private String newPsptEndDate;// 新证件结束时间

    private String newPsptAddr;// 新证件地址

    private String newSex;// 新性别

    private String newCityCode;// 业务区

    private String newBirthday;// 生日 BIRTHDAY;

    private String newPostAddress;// POST_ADDRESS

    private String newPostCode;// POST_CODE

    private String newPhone;// PHONE

    private String newFaxNbr;// FAX_NBR

    private String newEmail;//

    private String newHomeAddr;// HOME_ADDRESS

    private String newWorkName;//

    private String newWorkKind;// WORK_KIND

    private String newWorkDepart;//

    private String newContact;

    private String newContactPhone;//

    private String newContactTypeCode;//

    private String newNationalityCode;//

    private String newLanguageCode;//

    private String newFolkCode;//

    private String newReligionCode;

    private String newJobTypeCode;

    private String newEducateDegreeCode;//

    private String newMarriage;//

    private String newCommunityId;

    private String newIsRealName;// 是否实名制

    /********************** 新账户信息 **************************/
    private String newAcctId;// 新账户ID，在trade过程中生成

    private String newPayName;//

    private String newPayModeCode;// PAY_MODE_CODE

    private String newSuperBankCode;// BANK_CODE

    private String newBankCode;// BANK_CODE

    private String newBankAcctNo;// BANK_ACCT_NO

    private String newContractNo;// 合同号

    private String newAcctDay;// ACCT_DAY

    private String newBankAgreementNo;// RSRV_STR6 银行协议号

    /***** 新担保人资料 ***************/
    private String newAssureCustId;// 担保人客户ID

    private String newAssureTypeCode;// 担保人类型

    private String newAssureDate;// 担保人时间

    private String newAssurePsptTypeCode;// ASSURE_PSPT_TYPE_CODE

    private String newAssurePsptId;// ASSURE_PSPT_ID

    private String newAssureName;// ASSURE_NAME

    private String newAssurePhone;// ASSURE_PHONE

    private String newPassword;// 用户设置的新密码

    private String agentCustName;// 经办人名称

    private String agentPsptTypeCode;// 经办人证件类型

    private String agentPsptId;// 经办人证件号码

    private String agentPsptAddr;// 经办人证件地址
    
    /********使用人信息 20160725*************/
    private String useName;//使用人姓名
    
    private String usePsptTypeCode;//使用人证件类型
    
    private String usePsptId;//使用人证件号码
    
    private String usePsptAddr;//使用人证件地址
    
    
    
    /********IMS固话信息 2018*************/
    private String destroyIMSTag;   //IMS拆机标识
    
    private String IMSTag;     //IMS标识
 
    private String IMSSerialNumber;   //ims号码
    
    private String IMSBrand;   //ims 品牌
    
    private String IMSProduct;  //ims产品
    
  //REQ201911080010 关于实名入网办理日志留存的改造通知 - add by guonj -20200305 
    private String devRead ;//是否设备自动读取0:否1:是
    
    private String readRuslt ;//读取结果0:失败  1:成功

    private String comparisonIs ;//是否设备自动读取0:否1:是
    
    private String comparisonRuslt ;//读取结果0:失败  1:成功
    
    private String comparisonSeq ;//seq

    private String authenticityIs ;//是否联网验证真实性0:否1:是
    
    private String authenticitySeq ;//seq
    
    private String authenticityRuslt ;//是否联网验证真实性读取结果0:失败  1:成功
    
    private String provenumIs ;//是否进行一证五号查验0:否1:是
    
    private String provenumRuslt ;//是否进行一证五号查验读取结果0:失败  1:成功
    
    private String provenumSeq ;//seq

    public String getDevRead() {
		return devRead;
	}

	public void setDevRead(String devRead) {
		this.devRead = devRead;
	}

	public String getReadRuslt() {
		return readRuslt;
	}

	public void setReadRuslt(String readRuslt) {
		this.readRuslt = readRuslt;
	}

	public String getComparisonIs() {
		return comparisonIs;
	}

	public void setComparisonIs(String comparisonIs) {
		this.comparisonIs = comparisonIs;
	}

	public String getComparisonRuslt() {
		return comparisonRuslt;
	}

	public void setComparisonRuslt(String comparisonRuslt) {
		this.comparisonRuslt = comparisonRuslt;
	}

	public String getComparisonSeq() {
		return comparisonSeq;
	}

	public void setComparisonSeq(String comparisonSeq) {
		this.comparisonSeq = comparisonSeq;
	}

	public String getAuthenticityIs() {
		return authenticityIs;
	}

	public void setAuthenticityIs(String authenticityIs) {
		this.authenticityIs = authenticityIs;
	}

	public String getAuthenticitySeq() {
		return authenticitySeq;
	}

	public void setAuthenticitySeq(String authenticitySeq) {
		this.authenticitySeq = authenticitySeq;
	}

	public String getAuthenticityRuslt() {
		return authenticityRuslt;
	}

	public void setAuthenticityRuslt(String authenticityRuslt) {
		this.authenticityRuslt = authenticityRuslt;
	}

	public String getProvenumIs() {
		return provenumIs;
	}

	public void setProvenumIs(String provenumIs) {
		this.provenumIs = provenumIs;
	}

	public String getProvenumRuslt() {
		return provenumRuslt;
	}

	public void setProvenumRuslt(String provenumRuslt) {
		this.provenumRuslt = provenumRuslt;
	}

	public String getProvenumSeq() {
		return provenumSeq;
	}

	public void setProvenumSeq(String provenumSeq) {
		this.provenumSeq = provenumSeq;
	}

	public String getAgentCustName()
    {
        return agentCustName;
    }

    public String getAgentPsptAddr()
    {
        return agentPsptAddr;
    }

    public String getAgentPsptId()
    {
        return agentPsptId;
    }

    public String getAgentPsptTypeCode()
    {
        return agentPsptTypeCode;
    }

    public String getNewAcctDay()
    {
        return newAcctDay;
    }

    public String getNewAcctId()
    {
        return newAcctId;
    }

    public String getNewAssureCustId()
    {
        return newAssureCustId;
    }

    public String getNewAssureDate()
    {
        return newAssureDate;
    }

    public String getNewAssureName()
    {
        return newAssureName;
    }

    public String getNewAssurePhone()
    {
        return newAssurePhone;
    }

    public String getNewAssurePsptId()
    {
        return newAssurePsptId;
    }

    public String getNewAssurePsptTypeCode()
    {
        return newAssurePsptTypeCode;
    }

    public String getNewAssureTypeCode()
    {
        return newAssureTypeCode;
    }

    public String getNewBankAcctNo()
    {
        return newBankAcctNo;
    }

    public String getNewBankAgreementNo()
    {
        return newBankAgreementNo;
    }

    public String getNewBankCode()
    {
        return newBankCode;
    }

    public String getNewBirthday()
    {
        return newBirthday;
    }

    public String getNewCityCode()
    {
        return newCityCode;
    }

    public String getNewCommunityId()
    {
        return newCommunityId;
    }

    public String getNewContact()
    {
        return newContact;
    }

    public String getNewContactPhone()
    {
        return newContactPhone;
    }

    public String getNewContactTypeCode()
    {
        return newContactTypeCode;
    }

    public String getNewContractNo()
    {
        return newContractNo;
    }

    public String getNewCustId()
    {
        return newCustId;
    }

    public String getNewCustName()
    {
        return newCustName;
    }

    public String getNewEducateDegreeCode()
    {
        return newEducateDegreeCode;
    }

    public String getNewEmail()
    {
        return newEmail;
    }

    public String getNewFaxNbr()
    {
        return newFaxNbr;
    }

    public String getNewFolkCode()
    {
        return newFolkCode;
    }

    public String getNewHomeAddr()
    {
        return newHomeAddr;
    }

    public String getNewIsRealName()
    {
        return newIsRealName;
    }

    public String getNewJobTypeCode()
    {
        return newJobTypeCode;
    }

    public String getNewLanguageCode()
    {
        return newLanguageCode;
    }

    public String getNewMarriage()
    {
        return newMarriage;
    }

    public String getNewNationalityCode()
    {
        return newNationalityCode;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public String getNewPayModeCode()
    {
        return newPayModeCode;
    }

    public String getNewPayName()
    {
        return newPayName;
    }

    public String getNewPhone()
    {
        return newPhone;
    }

    public String getNewPostAddress()
    {
        return newPostAddress;
    }

    public String getNewPostCode()
    {
        return newPostCode;
    }

    public String getNewPsptAddr()
    {
        return newPsptAddr;
    }

    public String getNewPsptEndDate()
    {
        return newPsptEndDate;
    }

    public String getNewPsptId()
    {
        return newPsptId;
    }

    public String getNewPsptTypeCode()
    {
        return newPsptTypeCode;
    }

    public String getNewReligionCode()
    {
        return newReligionCode;
    }

    public String getNewSex()
    {
        return newSex;
    }

    public String getNewSuperBankCode()
    {
        return newSuperBankCode;
    }

    public String getNewWorkDepart()
    {
        return newWorkDepart;
    }

    public String getNewWorkKind()
    {
        return newWorkKind;
    }

    public String getNewWorkName()
    {
        return newWorkName;
    }

    public void setAgentCustName(String agentCustName)
    {
        this.agentCustName = agentCustName;
    }

    public void setAgentPsptAddr(String agentPsptAddr)
    {
        this.agentPsptAddr = agentPsptAddr;
    }

    public void setAgentPsptId(String agentPsptId)
    {
        this.agentPsptId = agentPsptId;
    }

    public void setAgentPsptTypeCode(String agentPsptTypeCode)
    {
        this.agentPsptTypeCode = agentPsptTypeCode;
    }

    public void setNewAcctDay(String newAcctDay)
    {
        this.newAcctDay = newAcctDay;
    }

    public void setNewAcctId(String newAcctId)
    {
        this.newAcctId = newAcctId;
    }

    public void setNewAssureCustId(String newAssureCustId)
    {
        this.newAssureCustId = newAssureCustId;
    }

    public void setNewAssureDate(String newAssureDate)
    {
        this.newAssureDate = newAssureDate;
    }

    public void setNewAssureName(String newAssureName)
    {
        this.newAssureName = newAssureName;
    }

    public void setNewAssurePhone(String newAssurePhone)
    {
        this.newAssurePhone = newAssurePhone;
    }

    public void setNewAssurePsptId(String newAssurePsptId)
    {
        this.newAssurePsptId = newAssurePsptId;
    }

    public void setNewAssurePsptTypeCode(String newAssurePsptTypeCode)
    {
        this.newAssurePsptTypeCode = newAssurePsptTypeCode;
    }

    public void setNewAssureTypeCode(String newAssureTypeCode)
    {
        this.newAssureTypeCode = newAssureTypeCode;
    }

    public void setNewBankAcctNo(String newBankAcctNo)
    {
        this.newBankAcctNo = newBankAcctNo;
    }

    public void setNewBankAgreementNo(String newBankAgreementNo)
    {
        this.newBankAgreementNo = newBankAgreementNo;
    }

    public void setNewBankCode(String newBankCode)
    {
        this.newBankCode = newBankCode;
    }

    public void setNewBirthday(String newBirthday)
    {
        this.newBirthday = newBirthday;
    }

    public void setNewCityCode(String newCityCode)
    {
        this.newCityCode = newCityCode;
    }

    public void setNewCommunityId(String newCommunityId)
    {
        this.newCommunityId = newCommunityId;
    }

    public void setNewContact(String newContact)
    {
        this.newContact = newContact;
    }

    public void setNewContactPhone(String newContactPhone)
    {
        this.newContactPhone = newContactPhone;
    }

    public void setNewContactTypeCode(String newContactTypeCode)
    {
        this.newContactTypeCode = newContactTypeCode;
    }

    public void setNewContractNo(String newContractNo)
    {
        this.newContractNo = newContractNo;
    }

    public void setNewCustId(String newCustId)
    {
        this.newCustId = newCustId;
    }

    public void setNewCustName(String newCustName)
    {
        this.newCustName = newCustName;
    }

    public void setNewEducateDegreeCode(String newEducateDegreeCode)
    {
        this.newEducateDegreeCode = newEducateDegreeCode;
    }

    public void setNewEmail(String newEmail)
    {
        this.newEmail = newEmail;
    }

    public void setNewFaxNbr(String newFaxNbr)
    {
        this.newFaxNbr = newFaxNbr;
    }

    public void setNewFolkCode(String newFolkCode)
    {
        this.newFolkCode = newFolkCode;
    }

    public void setNewHomeAddr(String newHomeAddr)
    {
        this.newHomeAddr = newHomeAddr;
    }

    public void setNewIsRealName(String newIsRealName)
    {
        this.newIsRealName = newIsRealName;
    }

    public void setNewJobTypeCode(String newJobTypeCode)
    {
        this.newJobTypeCode = newJobTypeCode;
    }

    public void setNewLanguageCode(String newLanguageCode)
    {
        this.newLanguageCode = newLanguageCode;
    }

    public void setNewMarriage(String newMarriage)
    {
        this.newMarriage = newMarriage;
    }

    public void setNewNationalityCode(String newNationalityCode)
    {
        this.newNationalityCode = newNationalityCode;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    public void setNewPayModeCode(String newPayModeCode)
    {
        this.newPayModeCode = newPayModeCode;
    }

    public void setNewPayName(String newPayName)
    {
        this.newPayName = newPayName;
    }

    public void setNewPhone(String newPhone)
    {
        this.newPhone = newPhone;
    }

    public void setNewPostAddress(String newPostAddress)
    {
        this.newPostAddress = newPostAddress;
    }

    public void setNewPostCode(String newPostCode)
    {
        this.newPostCode = newPostCode;
    }

    public void setNewPsptAddr(String newPsptAddr)
    {
        this.newPsptAddr = newPsptAddr;
    }

    public void setNewPsptEndDate(String newPsptEndDate)
    {
        this.newPsptEndDate = newPsptEndDate;
    }

    public void setNewPsptId(String newPsptId)
    {
        this.newPsptId = newPsptId;
    }

    public void setNewPsptTypeCode(String newPsptTypeCode)
    {
        this.newPsptTypeCode = newPsptTypeCode;
    }

    public void setNewReligionCode(String newReligionCode)
    {
        this.newReligionCode = newReligionCode;
    }

    public void setNewSex(String newSex)
    {
        this.newSex = newSex;
    }

    public void setNewSuperBankCode(String newSuperBankCode)
    {
        this.newSuperBankCode = newSuperBankCode;
    }

    public void setNewWorkDepart(String newWorkDepart)
    {
        this.newWorkDepart = newWorkDepart;
    }

    public void setNewWorkKind(String newWorkKind)
    {
        this.newWorkKind = newWorkKind;
    }

    public void setNewWorkName(String newWorkName)
    {
        this.newWorkName = newWorkName;
    }

	public String getUseName() {
		return useName;
	}

	public void setUseName(String useName) {
		this.useName = useName;
	}

	public String getUsePsptTypeCode() {
		return usePsptTypeCode;
	}

	public void setUsePsptTypeCode(String usePsptTypeCode) {
		this.usePsptTypeCode = usePsptTypeCode;
	}

	public String getUsePsptId() {
		return usePsptId;
	}

	public void setUsePsptId(String usePsptId) {
		this.usePsptId = usePsptId;
	}

	public String getUsePsptAddr() {
		return usePsptAddr;
	}

	public void setUsePsptAddr(String usePsptAddr) {
		this.usePsptAddr = usePsptAddr;
	}

	public String getDestroyIMSTag() {
		return destroyIMSTag;
	}

	public void setDestroyIMSTag(String destroyIMSTag) {
		this.destroyIMSTag = destroyIMSTag;
	}

	public String getIMSTag() {
		return IMSTag;
	}

	public void setIMSTag(String iMSTag) {
		IMSTag = iMSTag;
	}

	public String getIMSSerialNumber() {
		return IMSSerialNumber;
	}

	public void setIMSSerialNumber(String iMSSerialNumber) {
		IMSSerialNumber = iMSSerialNumber;
	}

	public String getIMSBrand() {
		return IMSBrand;
	}

	public void setIMSBrand(String iMSBrand) {
		IMSBrand = iMSBrand;
	}

	public String getIMSProduct() {
		return IMSProduct;
	}

	public void setIMSProduct(String iMSProduct) {
		IMSProduct = iMSProduct;
	}

}
