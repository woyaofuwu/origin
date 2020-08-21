
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.req.unionunite;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMemberReqData;

public class UniteGrpAccountReqData extends CreateGroupMemberReqData
{

    private String bankName;

    private String bankAcctNo;

    private String bankCode;

    private String contractNo;

    private String payModeCode;

    private String superBankCode;

    private String mebEparchyCode;

    private String targetSerialNumber;

    private String targetCustId;

    private String targetUserId;

    private IDataset accountList = new DatasetList();

    private IDataset acctConsignList = new DatasetList();

    public IDataset getAccountList()
    {
        return accountList;
    }

    public IDataset getAcctConsignList()
    {
        return acctConsignList;
    }

    public String getBankAcctNo()
    {
        return bankAcctNo;
    }

    public String getBankCode()
    {
        return bankCode;
    }

    public String getBankName()
    {
        return bankName;
    }

    public String getContractNo()
    {
        return contractNo;
    }

    public String getMebEparchyCode()
    {
        return mebEparchyCode;
    }

    public String getPayModeCode()
    {
        return payModeCode;
    }

    public String getSuperBankCode()
    {
        return superBankCode;
    }

    public String getTargetCustId()
    {
        return targetCustId;
    }

    public String getTargetSerialNumber()
    {
        return targetSerialNumber;
    }

    public String getTargetUserId()
    {
        return targetUserId;
    }

    public void setBankAcctNo(String bankAcctNo)
    {
        this.bankAcctNo = bankAcctNo;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public void setContractNo(String contractNo)
    {
        this.contractNo = contractNo;
    }

    public void setMebEparchyCode(String mebEparchyCode)
    {
        this.mebEparchyCode = mebEparchyCode;
    }

    public void setPayModeCode(String payModeCode)
    {
        this.payModeCode = payModeCode;
    }

    public void setSuperBankCode(String superBankCode)
    {
        this.superBankCode = superBankCode;
    }

    public void setTargetCustId(String targetCustId)
    {
        this.targetCustId = targetCustId;
    }

    public void setTargetSerialNumber(String targetSerialNumber)
    {
        this.targetSerialNumber = targetSerialNumber;
    }

    public void setTargetUserId(String targetUserId)
    {
        this.targetUserId = targetUserId;
    }

}
