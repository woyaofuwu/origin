
package com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.BaseCreateUserRequestData;

public class CreateFixTelUserRequestData extends BaseCreateUserRequestData
{
    private String noteType; // 发票类型

    private String cityCode; // 归属业务区

    private String resKindName; // 卡类型名称

    private String xTradeRes; // 资源信息

    private String acctDiscount; // 账户优惠

    private String acctTag; // 出账标志

    private String openMode; // 开户方式

    private String inPhone; // 代理商号码

    private String superBankCode; // 上级银行

    private ProductData mainProduct;

    // ---------------------------隐藏信息---------------------------------//
    private String openType; // 开户方式

    private String openLimitTag; // 开户限制标记

    private String openLimitCount; // 默认开户用户数

    private String custNameLimit; // 客户名称限制标志

    private int defaultPwdMode; // 默认密码的使用方式

    private String defaultPwd; // 默认密码

    private int defaultPwdLength; // 密码长度

    private String defaultUserType; // 默认用户类型

    private String defaultPsptType; // 默认证件类型

    private String defaultPayMode; // 默认帐户类型

    private String chrBlackCheckMode; // 黑名单提示方式标记

    private String chrCheckOweFeeByPspt; // 是否根据证件号码判断欠费标记

    // ---------------------------产品信息---------------------------------//

    private String chrCheckOweFeeByPsptAllUser; // 根据证件号码判断欠费用户数

    // ---------------------------其他信息---------------------------------//

    private String resCheckByDepart; // 代理商开户是否只使用操作员部门

    private String chrAutoPasswd; // 代理商开户是否设置用户密码

    private String provOpenAdvancePayFlag; // 省内跨区开户默认预存款标记

    private String provOpenAdvancePay; // 省内跨区开户默认预存款

    private String provOpenOperFeeFlag; // 省内跨区开户默认卡费标记

    private String provOpenOperFee; // 省内跨区开户默认卡费

    private String chrUserGgCard; // 是否可以同时输入刮刮卡号标志

    private String chrCheckSeleNum; // 已检查号码选占数据标记

    private String oldSerialNumber; // 原号码

    private String oldSimCardNo; // 原SIM卡号

    private String oldPsptTypeCode; // 原证件类型

    private String oldPsptId; // 原证件号码

    private String checkResultCode; // 号码和sim卡校验通过标志

    private String checkPsptCode; // 证件校验通过标志

    private IDataset deviceRes; // 固话设备资源

    private String standAddress; // 固话标准地址

    private String detailAddress; // 固话详细地址

    private String standAddressCode; // 固话标准地址编码

    private String signPath; // 固话局向

    private String secret; // 固话保密设置

    private String resTypeCode; // 固话号码类型编码

    private String resCode; // 固话号码编码

    private String switchId; // 交换机编码

    private String switchType; // 交换机编码

    private String resKindCode; // 号码资源小类

    private String areaType; // 地域

    private String clearAccount; // 清算

    private String reversePolarity; // 反极

    private int mainServNumFlg;// 代表号，千群百号业务专用

    private String mainUserId;// 代表号userId，千群百号业务专用

    private String mainAcctId;// 代表号AcctId，千群百号业务专用

    private String mainCustId;// 代表号custId，千群百号业务专用

    private String isBat;// 代表号custId，千群百号业务专用

    private String mainSerNum;// 代表号码,千群百号业务专用
    
    private String paperType;//账单类型
    
    private String agentCustName;// 经办人名称

    private String agentPsptTypeCode;// 经办人证件类型

    private String agentPsptId;// 经办人证件号码

    private String agentPsptAddr;// 经办人证件地址

    public String getPaperType() {
		return paperType;
	}

	public void setPaperType(String paperType) {
		this.paperType = paperType;
	}

	/* 产品元素信息 */
    private List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();

    public void addPmd(ProductModuleData pmd)
    {
        this.pmds.add(pmd);
    }

    public String getAcctDiscount()
    {
        return acctDiscount;
    }

    public String getAcctTag()
    {
        return acctTag;
    }

    public String getAreaType()
    {
        return areaType;
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

    public String getChrUserGgCard()
    {
        return chrUserGgCard;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getClearAccount()
    {
        return clearAccount;
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

    public String getDetailAddress()
    {
        return detailAddress;
    }

    public IDataset getDeviceRes()
    {
        return deviceRes;
    }

    public String getInPhone()
    {
        return inPhone;
    }

    public String getIsBat()
    {
        return isBat;
    }

    public String getMainAcctId()
    {
        return mainAcctId;
    }

    public String getMainCustId()
    {
        return mainCustId;
    }

    public ProductData getMainProduct()
    {
        return mainProduct;
    }

    public String getMainSerNum()
    {
        return mainSerNum;
    }

    public int getMainServNumFlg()
    {
        return mainServNumFlg;
    }

    public String getMainUserId()
    {
        return mainUserId;
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

    public String getOpenType()
    {
        return openType;
    }

    public List<ProductModuleData> getPmds()
    {
        return pmds;
    }

    public String getProvOpenAdvancePay()
    {
        return provOpenAdvancePay;
    }

    public String getProvOpenAdvancePayFlag()
    {
        return provOpenAdvancePayFlag;
    }

    public String getProvOpenOperFee()
    {
        return provOpenOperFee;
    }

    public String getProvOpenOperFeeFlag()
    {
        return provOpenOperFeeFlag;
    }

    public String getResCheckByDepart()
    {
        return resCheckByDepart;
    }

    public String getResCode()
    {
        return resCode;
    }

    public String getResKindCode()
    {
        return resKindCode;
    }

    public String getResKindName()
    {
        return resKindName;
    }

    public String getResTypeCode()
    {
        return resTypeCode;
    }

    public String getReversePolarity()
    {
        return reversePolarity;
    }

    public String getSecret()
    {
        return secret;
    }

    public String getSignPath()
    {
        return signPath;
    }

    public String getStandAddress()
    {
        return standAddress;
    }

    public String getStandAddressCode()
    {
        return standAddressCode;
    }

    public String getSuperBankCode()
    {
        return superBankCode;
    }

    public String getSwitchId()
    {
        return switchId;
    }

    public String getSwitchType()
    {
        return switchType;
    }

    public String getxTradeRes()
    {
        return xTradeRes;
    }

    public String getXTradeRes()
    {
        return xTradeRes;
    }

    public void setAcctDiscount(String acctDiscount)
    {
        this.acctDiscount = acctDiscount;
    }

    public void setAcctTag(String acctTag)
    {
        this.acctTag = acctTag;
    }

    public void setAreaType(String areaType)
    {
        this.areaType = areaType;
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

    public void setChrUserGgCard(String chrUserGgCard)
    {
        this.chrUserGgCard = chrUserGgCard;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setClearAccount(String clearAccount)
    {
        this.clearAccount = clearAccount;
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

    public void setDetailAddress(String detailAddress)
    {
        this.detailAddress = detailAddress;
    }

    public void setDeviceRes(IDataset deviceRes)
    {
        this.deviceRes = deviceRes;
    }

    public void setInPhone(String inPhone)
    {
        this.inPhone = inPhone;
    }

    public void setIsBat(String isBat)
    {
        this.isBat = isBat;
    }

    public void setMainAcctId(String mainAcctId)
    {
        this.mainAcctId = mainAcctId;
    }

    public void setMainCustId(String mainCustId)
    {
        this.mainCustId = mainCustId;
    }

    public void setMainProduct(ProductData mainProduct)
    {
        this.mainProduct = mainProduct;
    }

    public void setMainProduct(String productId) throws Exception
    {
        this.mainProduct = new ProductData(productId);
    }

    public void setMainSerNum(String mainSerNum)
    {
        this.mainSerNum = mainSerNum;
    }

    public void setMainServNumFlg(int mainServNumFlg)
    {
        this.mainServNumFlg = mainServNumFlg;
    }

    public void setMainUserId(String mainUserId)
    {
        this.mainUserId = mainUserId;
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

    public void setOpenType(String openType)
    {
        this.openType = openType;
    }

    public void setPmds(List<ProductModuleData> pmds)
    {
        this.pmds = pmds;
    }

    public void setProvOpenAdvancePay(String provOpenAdvancePay)
    {
        this.provOpenAdvancePay = provOpenAdvancePay;
    }

    public void setProvOpenAdvancePayFlag(String provOpenAdvancePayFlag)
    {
        this.provOpenAdvancePayFlag = provOpenAdvancePayFlag;
    }

    public void setProvOpenOperFee(String provOpenOperFee)
    {
        this.provOpenOperFee = provOpenOperFee;
    }

    public void setProvOpenOperFeeFlag(String provOpenOperFeeFlag)
    {
        this.provOpenOperFeeFlag = provOpenOperFeeFlag;
    }

    public void setResCheckByDepart(String resCheckByDepart)
    {
        this.resCheckByDepart = resCheckByDepart;
    }

    public void setResCode(String resCode)
    {
        this.resCode = resCode;
    }

    public void setResKindCode(String resKindCode)
    {
        this.resKindCode = resKindCode;
    }

    public void setResKindName(String resKindName)
    {
        this.resKindName = resKindName;
    }

    public void setResTypeCode(String resTypeCode)
    {
        this.resTypeCode = resTypeCode;
    }

    public void setReversePolarity(String reversePolarity)
    {
        this.reversePolarity = reversePolarity;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public void setSignPath(String signPath)
    {
        this.signPath = signPath;
    }

    public void setStandAddress(String standAddress)
    {
        this.standAddress = standAddress;
    }

    public void setStandAddressCode(String standAddressCode)
    {
        this.standAddressCode = standAddressCode;
    }

    public void setSuperBankCode(String superBankCode)
    {
        this.superBankCode = superBankCode;
    }

    public void setSwitchId(String switchId)
    {
        this.switchId = switchId;
    }

    public void setSwitchType(String switchType)
    {
        this.switchType = switchType;
    }

    public void setxTradeRes(String xTradeRes)
    {
        this.xTradeRes = xTradeRes;
    }

    public void setXTradeRes(String tradeRes)
    {
        xTradeRes = tradeRes;
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
}
