
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata;

public class CreatePersonUserRequestData extends CreateUserRequestData
{
	private String msisdn_type;  // 副号码类型  --->和多号业务需求的改造

    // 海南特殊信息

    // private String preOpenTag; // 预约开户标记

    // private String sysDate; // 开户时间

    private String agentDepartId; // 开户代理商

    private String activeTag;// 激活标记

    private String bindSaleTag; // 绑定营销活动标志

    private String bindDefaultDiscnt; // 绑定优惠

    private String agentCustName; // 经办人名称

    private String agentPsptTypeCode; // 经办人证件类型

    private String agentPsptId; // 经办人证件号码

    private String agentPsptAddr; // 经办人证件地址

    private String interNationalTag; // 是否订购国际漫游标记

    private String realName; // 是否实名制登记

    private String callingTypeCode;// 联网新增应用行业类型

    private String payModeCode;// 账户类型

    private String saleProductId;// 绑定的营销活动ID

    private String salePackageId;// 绑定的营销包ID

    private String invoiceNo;// 发票号

    private String agentFee; // 代办费--新买断开户使用

    // ---------------------------隐藏信息---------------------------------//
    private String acctTag; // 出账标志

    private String openMode; // 开户方式

    private String openLimitTag; // 开户限制标记

    private String openLimitCount; // 默认开户用户数

    private String custNameLimit; // 客户名称限制标志

    private String defaultPsptType; // 默认证件类型

    private String defaultPayMode; // 默认帐户类型

    private String chrBlackCheckMode; // 黑名单提示方式标记

    private String chrCheckOweFeeByPspt; // 是否根据证件号码判断欠费标记

    private String chrCheckOweFeeByPsptAllUser; // 根据证件号码判断欠费用户数

    private String resCheckByDepart; // 代理商开户是否只使用操作员部门

    private String chrAutoPasswd; // 代理商开户是否设置用户密码

    private String chrCheckSeleNum; // 已检查号码选占数据标记

    private String PassCode; // 密码因子

    private String netTypeCode; // 网别，接口专用

    private String CardPasswd; // 初始密码

    private String noteType; // 发票类型

    private String cityCode; // 归属业务区

    private String DefaultPwdFlag; // 是否使用初始化密码

    private boolean bOpenSale; // 用户营销开户标记

    private String xTradeGiftFee; // 批开赠送费用

    // ---------------------------其他信息---------------------------------//

    private String oldSerialNumber; // 原号码

    private String oldSimCardNo; // 原SIM卡号

    private String oldPsptTypeCode; // 原证件类型

    private String oldPsptId; // 原证件号码

    private String checkResultCode; // 号码和sim卡校验通过标志

    private String checkPsptCode; // 证件校验通过标志

    private String superBankCode; // 上级银行

    private int defaultPwdMode; // 默认密码的使用方式

    private String defaultPwd; // 默认密码

    private int defaultPwdLength; // 密码长度

    private String defaultUserType; // 默认用户类型

    private String operateId; // 接口操作编码 用于处理trade的BATCH_ID
    
	private String agentPresentFee; //赠送预存话费
	
	private String m2mTag; //行业应用卡批量开户标记,是否M2M类型，0不是，1是
    private String jumpCheckPsptId;// 是否免验证 1jump

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

    public String getJumpCheckPsptId()
    {
        return jumpCheckPsptId;
    }
    
    public void setJumpCheckPsptId(String jumpCheckPsptId)
    {
        this.jumpCheckPsptId = jumpCheckPsptId;
    }

	public String getAcctTag()
    {
        return acctTag;
    }

    public String getActiveTag()
    {
        return activeTag;
    }

    public String getAgentCustName()
    {
        return agentCustName;
    }

    public String getAgentDepartId()
    {
        return agentDepartId;
    }

    public String getAgentFee()
    {
        return agentFee;
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

    public String getBindDefaultDiscnt()
    {
        return bindDefaultDiscnt;
    }

    public String getBindSaleTag()
    {
        return bindSaleTag;
    }

    public String getCallingTypeCode()
    {
        return callingTypeCode;
    }

    public String getCardPasswd()
    {
        return CardPasswd;
    }

    public String getCheckPsptCode()
    {
        return checkPsptCode;
    }

    public String getCheckResultCode()
    {
        return checkResultCode;
    }

    public String getChrAutoPasswd()
    {
        return chrAutoPasswd;
    }

    public String getChrBlackCheckMode()
    {
        return chrBlackCheckMode;
    }

    public String getChrCheckOweFeeByPspt()
    {
        return chrCheckOweFeeByPspt;
    }

    public String getChrCheckOweFeeByPsptAllUser()
    {
        return chrCheckOweFeeByPsptAllUser;
    }

    public String getChrCheckSeleNum()
    {
        return chrCheckSeleNum;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getCustNameLimit()
    {
        return custNameLimit;
    }

    public String getDefaultPayMode()
    {
        return defaultPayMode;
    }

    public String getDefaultPsptType()
    {
        return defaultPsptType;
    }

    public String getDefaultPwd()
    {
        return defaultPwd;
    }

    public String getDefaultPwdFlag()
    {
        return DefaultPwdFlag;
    }

    public int getDefaultPwdLength()
    {
        return defaultPwdLength;
    }

    public int getDefaultPwdMode()
    {
        return defaultPwdMode;
    }

    public String getDefaultUserType()
    {
        return defaultUserType;
    }

    public String getInterNationalTag()
    {
        return interNationalTag;
    }

    public String getInvoiceNo()
    {
        return invoiceNo;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public String getNoteType()
    {
        return noteType;
    }

    public String getOldPsptId()
    {
        return oldPsptId;
    }

    public String getOldPsptTypeCode()
    {
        return oldPsptTypeCode;
    }

    public String getOldSerialNumber()
    {
        return oldSerialNumber;
    }

    public String getOldSimCardNo()
    {
        return oldSimCardNo;
    }

    public String getOpenLimitCount()
    {
        return openLimitCount;
    }

    public String getOpenLimitTag()
    {
        return openLimitTag;
    }

    public String getOpenMode()
    {
        return openMode;
    }

    public String getOperateId()
    {
        return operateId;
    }

    public String getPassCode()
    {
        return PassCode;
    }

    public String getPayModeCode()
    {
        return payModeCode;
    }

    public String getRealName()
    {
        return realName;
    }

    public String getResCheckByDepart()
    {
        return resCheckByDepart;
    }

    public String getSalePackageId()
    {
        return salePackageId;
    }

    public String getSaleProductId()
    {
        return saleProductId;
    }

    public String getSuperBankCode()
    {
        return superBankCode;
    }

    public String getXTradeGiftFee()
    {
        return xTradeGiftFee;
    }

    public void setAcctTag(String acctTag)
    {
        this.acctTag = acctTag;
    }

    public void setActiveTag(String activeTag)
    {
        this.activeTag = activeTag;
    }

    public void setAgentCustName(String agentCustName)
    {
        this.agentCustName = agentCustName;
    }

    public void setAgentDepartId(String agentDepartId)
    {
        this.agentDepartId = agentDepartId;
    }

    public void setAgentFee(String agentFee)
    {
        this.agentFee = agentFee;
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

    /*
     * public void setPreOpenTag(String preOpenTag) { this.preOpenTag = preOpenTag; } public void setSysDate(String
     * sysDate) { this.sysDate = sysDate; }
     */

    public void setBindDefaultDiscnt(String bindDefaultDiscnt)
    {
        this.bindDefaultDiscnt = bindDefaultDiscnt;
    }

    public void setBindSaleTag(String bindSaleTag)
    {
        this.bindSaleTag = bindSaleTag;
    }

    public void setCallingTypeCode(String callingTypeCode)
    {
        this.callingTypeCode = callingTypeCode;
    }

    public void setCardPasswd(String CardPasswd)
    {
        this.CardPasswd = CardPasswd;
    }

    public void setCheckPsptCode(String checkPsptCode)
    {
        this.checkPsptCode = checkPsptCode;
    }

    public void setCheckResultCode(String checkResultCode)
    {
        this.checkResultCode = checkResultCode;
    }

    public void setChrAutoPasswd(String chrAutoPasswd)
    {
        this.chrAutoPasswd = chrAutoPasswd;
    }

    public void setChrBlackCheckMode(String chrBlackCheckMode)
    {
        this.chrBlackCheckMode = chrBlackCheckMode;
    }

    public void setChrCheckOweFeeByPspt(String chrCheckOweFeeByPspt)
    {
        this.chrCheckOweFeeByPspt = chrCheckOweFeeByPspt;
    }

    public void setChrCheckOweFeeByPsptAllUser(String chrCheckOweFeeByPsptAllUser)
    {
        this.chrCheckOweFeeByPsptAllUser = chrCheckOweFeeByPsptAllUser;
    }

    public void setChrCheckSeleNum(String chrCheckSeleNum)
    {
        this.chrCheckSeleNum = chrCheckSeleNum;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setCustNameLimit(String custNameLimit)
    {
        this.custNameLimit = custNameLimit;
    }

    public void setDefaultPayMode(String defaultPayMode)
    {
        this.defaultPayMode = defaultPayMode;
    }

    public void setDefaultPsptType(String defaultPsptType)
    {
        this.defaultPsptType = defaultPsptType;
    }

    public void setDefaultPwd(String defaultPwd)
    {
        this.defaultPwd = defaultPwd;
    }

    public void setDefaultPwdFlag(String DefaultPwdFlag)
    {
        this.DefaultPwdFlag = DefaultPwdFlag;
    }

    public void setDefaultPwdLength(int defaultPwdLength)
    {
        this.defaultPwdLength = defaultPwdLength;
    }

    public void setDefaultPwdMode(int defaultPwdMode)
    {
        this.defaultPwdMode = defaultPwdMode;
    }

    public void setDefaultUserType(String defaultUserType)
    {
        this.defaultUserType = defaultUserType;
    }

    public void setInterNationalTag(String interNationalTag)
    {
        this.interNationalTag = interNationalTag;
    }

    public void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setNoteType(String noteType)
    {
        this.noteType = noteType;
    }

    public void setOldPsptId(String oldPsptId)
    {
        this.oldPsptId = oldPsptId;
    }

    public void setOldPsptTypeCode(String oldPsptTypeCode)
    {
        this.oldPsptTypeCode = oldPsptTypeCode;
    }

    public void setOldSerialNumber(String oldSerialNumber)
    {
        this.oldSerialNumber = oldSerialNumber;
    }

    public void setOldSimCardNo(String oldSimCardNo)
    {
        this.oldSimCardNo = oldSimCardNo;
    }

    public void setOpenLimitCount(String openLimitCount)
    {
        this.openLimitCount = openLimitCount;
    }

    public void setOpenLimitTag(String openLimitTag)
    {
        this.openLimitTag = openLimitTag;
    }

    public void setOpenMode(String openMode)
    {
        this.openMode = openMode;
    }

    public void setOperateId(String operateId)
    {
        this.operateId = operateId;
    }

    public void setPassCode(String PassCode)
    {
        this.PassCode = PassCode;
    }

    public void setPayModeCode(String payModeCode)
    {
        this.payModeCode = payModeCode;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public void setResCheckByDepart(String resCheckByDepart)
    {
        this.resCheckByDepart = resCheckByDepart;
    }

    public void setSalePackageId(String salePackageId)
    {
        this.salePackageId = salePackageId;
    }

    public void setSaleProductId(String saleProductId)
    {
        this.saleProductId = saleProductId;
    }

    public void setSuperBankCode(String superBankCode)
    {
        this.superBankCode = superBankCode;
    }

    public void setXTradeGiftFee(String tradeGiftFee)
    {
        xTradeGiftFee = tradeGiftFee;
    }

	public String getAgentPresentFee() {
		return agentPresentFee;
	}

	public void setAgentPresentFee(String agentPresentFee) {
		this.agentPresentFee = agentPresentFee;
	}

	public String getM2mTag() {
		return m2mTag;
	}

	public void setM2mTag(String m2mTag) {
		this.m2mTag = m2mTag;
	}

	public String getMsisdn_type() {
		return msisdn_type;
	}

	public void setMsisdn_type(String msisdn_type) {
		this.msisdn_type = msisdn_type;
	}


}
