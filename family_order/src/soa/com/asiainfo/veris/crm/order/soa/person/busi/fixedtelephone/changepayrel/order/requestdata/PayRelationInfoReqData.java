
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changepayrel.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class PayRelationInfoReqData extends BaseReqData
{
    private String chgAcctType;// 变更账户类型

    private String superBankCode;// 变更上级银行编号

    private String bankCode;// 变更银行编号

    private String bankAcctNo;// 变更银行账户

    private String changeAll;// 将原帐户下所有用户都转到新帐户下

    private String payModeCode;// 目标账户类别新建账户时用到

    private String payName;// 目标账户名称新建账户时用到

    private String bankAgreementNo;// 银行协议号

    /************ trade过程中生成使用 开始 ***************/
    private String startCycleId;// 生效账期

    private String newAcctId; // 新帐户ID;

    /************* trade过程中生成使用 结束 ***************/

    private String changeAcctId;// 变换后的acctId;

    private String newSeriNum;

    public String getBankAcctNo()
    {
        return bankAcctNo;
    }

    public String getBankAgreementNo()
    {
        return bankAgreementNo;
    }

    public String getBankCode()
    {
        return bankCode;
    }

    public String getChangeAcctId()
    {
        return changeAcctId;
    }

    public String getChangeAll()
    {
        return changeAll;
    }

    public String getChgAcctType()
    {
        return chgAcctType;
    }

    public String getNewAcctId()
    {
        return newAcctId;
    }

    public String getNewSeriNum()
    {
        return newSeriNum;
    }

    public String getPayModeCode()
    {
        return payModeCode;
    }

    public String getPayName()
    {
        return payName;
    }

    public String getStartCycleId()
    {
        return startCycleId;
    }

    public String getSuperBankCode()
    {
        return superBankCode;
    }

    public void setBankAcctNo(String bankAcctNo)
    {
        this.bankAcctNo = bankAcctNo;
    }

    public void setBankAgreementNo(String bankAgreementNo)
    {
        this.bankAgreementNo = bankAgreementNo;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public void setChangeAcctId(String changeAcctId)
    {
        this.changeAcctId = changeAcctId;
    }

    public void setChangeAll(String changeAll)
    {
        this.changeAll = changeAll;
    }

    public void setChgAcctType(String chgAcctType)
    {
        this.chgAcctType = chgAcctType;
    }

    public void setNewAcctId(String newAcctId)
    {
        this.newAcctId = newAcctId;
    }

    public void setNewSeriNum(String newSeriNum)
    {
        this.newSeriNum = newSeriNum;
    }

    public void setPayModeCode(String payModeCode)
    {
        this.payModeCode = payModeCode;
    }

    public void setPayName(String payName)
    {
        this.payName = payName;
    }

    public void setStartCycleId(String startCycleId)
    {
        this.startCycleId = startCycleId;
    }

    public void setSuperBankCode(String superBankCode)
    {
        this.superBankCode = superBankCode;
    }

}
