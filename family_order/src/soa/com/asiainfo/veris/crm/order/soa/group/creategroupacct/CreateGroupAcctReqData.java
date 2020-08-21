
package com.asiainfo.veris.crm.order.soa.group.creategroupacct;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class CreateGroupAcctReqData extends GroupReqData
{
    private String acctDiffCode; // 帐户类别

    private String acctId; // 账户标识

    private String acctPasswd;// 密码

    private String bankAcctNo; // 银行帐号

    private String bankCode; // 银行编码

    private String contact;// 联系人

    private String contactPhone; // 联系电话

    private String contactUserId; // 联系人userId

    private String contractId; // 合同号

    private String custId; // 集团客户标识

    private String endCycleId; // 结束周期

    private String modifyTag; // 修改标识

    private String oldbankAcctNo; // 银行帐号

    private String oldbankCode; // 银行编码

    private String oldendCycleId;

    private String oldpayModeCode; // 帐户类型

    private String oldpayName; // 银行帐号名称

    private String oldstartCycleId; // 开始时间

    private String oldsuperBankCode; // 上级银行编码

    private String openDate; // 开户时间

    private String payModeCode; // 帐户类型

    private String payName; // 帐户名称

    private String removeTag; // 注销标识

    private String rsrvStr1; // 1 表示集团托收 2 表示个人托收

    private String rsrvStr3;// 账户说明--用户群

    private String rsrvStr5; // 

    private String rsrvStr7; // 单用户托收限额

    private String rsrvStr8;// 打印模式

    private String rsrvStr9;// 发票模式

    private String state; // 状态标识

    private String superBankCode; // 上级银行编码

    private String instId; // 托收主键

    private IData ACCT_CONSIGN;// 托收

    private boolean acctIsAdd;// 账户判断标识:账户是否新增,true为新增,false为取已有的

    private String PAY_NAME_ISCHANGED;

    private String isTTGrp; // 铁通集团,"true":是 "false":否
    
    private boolean hasPayModeChgPriv = false;//特殊办理银行托收业务标识：true 是，false 否
    
    private String rsrvStr6;
    
    private String rsrvStr10;

    private String rsrvStr4;

    public String getRsrvStr4() {
        return rsrvStr4;
    }

    public void setRsrvStr4(String rsrvStr4) {
        this.rsrvStr4 = rsrvStr4;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public String getIsTTGrp()
    {
        return isTTGrp;
    }

    public void setIsTTGrp(String isTTGrp)
    {
        this.isTTGrp = isTTGrp;
    }

    public IData getACCT_CONSIGN()
    {
        return ACCT_CONSIGN;
    }

    public String getAcctDiffCode()
    {
        return this.acctDiffCode;
    }

    public String getAcctId()
    {
        return this.acctId;
    }

    public String getAcctPasswd()
    {
        return this.acctPasswd;
    }

    public String getBankAcctNo()
    {
        return this.bankAcctNo;
    }

    public String getBankCode()
    {
        return this.bankCode;
    }

    public String getContact()
    {
        return this.contact;
    }

    public String getContactPhone()
    {
        return this.contactPhone;
    }

    public String getContactUserId()
    {
        return contactUserId;
    }

    public String getContractId()
    {
        return this.contractId;
    }

    public String getCustId()
    {
        return this.custId;
    }

    public String getEndCycleId()
    {
        return this.endCycleId;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getOldbankAcctNo()
    {
        return this.oldbankAcctNo;
    }

    public String getOldbankCode()
    {
        return this.oldbankCode;
    }

    public String getOldendCycleId()
    {
        return this.oldendCycleId;
    }

    public String getOldpayModeCode()
    {
        return this.oldpayModeCode;
    }

    public String getOldpayName()
    {
        return this.oldpayName;
    }

    public String getOldstartCycleId()
    {
        return this.oldstartCycleId;
    }

    public String getOldsuperBankCode()
    {
        return this.oldsuperBankCode;
    }

    public String getOpenDate()
    {
        return this.openDate;
    }

    public String getPAY_NAME_ISCHANGED()
    {
        return PAY_NAME_ISCHANGED;
    }

    public String getPayModeCode()
    {
        return this.payModeCode;
    }

    public String getPayName()
    {
        return this.payName;
    }

    public String getRemoveTag()
    {
        return this.removeTag;
    }

    public String getRsrvStr3()
    {
        return this.rsrvStr3;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public String getRsrvStr7()
    {
        return rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return this.rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return this.rsrvStr9;
    }

    public String getState()
    {
        return this.state;
    }

    public String getSuperBankCode()
    {
        return this.superBankCode;
    }

    public boolean isAcctIsAdd()
    {
        return acctIsAdd;
    }

    public void setACCT_CONSIGN(IData aCCT_CONSIGN)
    {
        ACCT_CONSIGN = aCCT_CONSIGN;
    }

    public void setAcctDiffCode(String acctDiffCode)
    {
        this.acctDiffCode = acctDiffCode;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setAcctIsAdd(boolean acctIsAdd)
    {
        this.acctIsAdd = acctIsAdd;
    }

    public void setAcctPasswd(String acctPasswd)
    {
        this.acctPasswd = acctPasswd;
    }

    public void setBankAcctNo(String bankAcctNo)
    {
        this.bankAcctNo = bankAcctNo;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public void setContactUserId(String contactUserId)
    {
        this.contactUserId = contactUserId;
    }

    public void setContractId(String contractId)
    {
        this.contractId = contractId;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setEndCycleId(String endCycleId)
    {
        this.endCycleId = endCycleId;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setOldbankAcctNo(String oldbankAcctNo)
    {
        this.oldbankAcctNo = oldbankAcctNo;
    }

    public void setOldbankCode(String oldbankCode)
    {
        this.oldbankCode = oldbankCode;
    }

    public void setOldendCycleId(String oldendCycleId)
    {
        this.oldendCycleId = oldendCycleId;
    }

    public void setOldpayModeCode(String oldpayModeCode)
    {
        this.oldpayModeCode = oldpayModeCode;
    }

    public void setOldpayName(String oldpayName)
    {
        this.oldpayName = oldpayName;
    }

    public void setOldstartCycleId(String oldstartCycleId)
    {
        this.oldstartCycleId = oldstartCycleId;
    }

    public void setOldsuperBankCode(String oldsuperBankCode)
    {
        this.oldsuperBankCode = oldsuperBankCode;
    }

    public void setOpenDate(String openDate)
    {
        this.openDate = openDate;
    }

    public void setPAY_NAME_ISCHANGED(String pAY_NAME_ISCHANGED)
    {
        PAY_NAME_ISCHANGED = pAY_NAME_ISCHANGED;
    }

    public void setPayModeCode(String payModeCode)
    {
        this.payModeCode = payModeCode;
    }

    public void setPayName(String payName)
    {
        this.payName = payName;
    }

    public void setRemoveTag(String removeTag)
    {
        this.removeTag = removeTag;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
    }

    public void setRsrvStr7(String rsrvStr7)
    {
        this.rsrvStr7 = rsrvStr7;
    }

    public void setRsrvStr8(String rsrvStr8)
    {
        this.rsrvStr8 = rsrvStr8;
    }

    public void setRsrvStr9(String rsrvStr9)
    {
        this.rsrvStr9 = rsrvStr9;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public void setSuperBankCode(String superBankCode)
    {
        this.superBankCode = superBankCode;
    }

    public boolean isHasPayModeChgPriv()
    {
        return hasPayModeChgPriv;
    }

    public void setHasPayModeChgPriv(boolean hasPayModeChgPriv)
    {
        this.hasPayModeChgPriv = hasPayModeChgPriv;
    }
    
    public void setRsrvStr6(String rsrvStr6)
    {
    	this.rsrvStr6 = rsrvStr6;
    }
    
    public void setRsrvStr10(String rsrvStr10)
    {
    	this.rsrvStr10 = rsrvStr10;
    }
    
    public String getRsrvStr6()
    {
        return rsrvStr6;
    }

    public String getRsrvStr10()
    {
        return rsrvStr10;
    }
}
