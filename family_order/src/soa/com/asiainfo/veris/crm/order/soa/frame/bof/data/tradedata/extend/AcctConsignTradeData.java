
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class AcctConsignTradeData extends BaseTradeData
{
    private String acctBalanceId;

    private String acctId;

    private String actTag;

    private String assistantTag;

    private String bankAcctName;

    private String bankAcctNo;

    private String bankCode;

    private String cityCode;

    private String consignMode;

    private String contact;

    private String contactPhone;

    private String contractId;

    private String contractName;

    private String endCycleId;

    private String eparchyCode;

    private String modifyTag;

    private String payFeeModeCode;

    private String paymentId;

    private String payModeCode;

    private String postAddress;

    private String postCode;

    private String priority;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr10;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String startCycleId;

    private String superBankCode;

    private String instId;

    public AcctConsignTradeData()
    {

    }

    public AcctConsignTradeData(IData data)
    {
        this.acctBalanceId = data.getString("ACCT_BALANCE_ID");
        this.acctId = data.getString("ACCT_ID");
        this.actTag = data.getString("ACT_TAG");
        this.assistantTag = data.getString("ASSISTANT_TAG");
        this.bankAcctName = data.getString("BANK_ACCT_NAME");
        this.bankAcctNo = data.getString("BANK_ACCT_NO");
        this.bankCode = data.getString("BANK_CODE");
        this.cityCode = data.getString("CITY_CODE");
        this.consignMode = data.getString("CONSIGN_MODE");
        this.contact = data.getString("CONTACT");
        this.contactPhone = data.getString("CONTACT_PHONE");
        this.contractId = data.getString("CONTRACT_ID");
        this.contractName = data.getString("CONTRACT_NAME");
        this.endCycleId = data.getString("END_CYCLE_ID");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.paymentId = data.getString("PAYMENT_ID");
        this.payFeeModeCode = data.getString("PAY_FEE_MODE_CODE");
        this.payModeCode = data.getString("PAY_MODE_CODE");
        this.postAddress = data.getString("POST_ADDRESS");
        this.postCode = data.getString("POST_CODE");
        this.instId = data.getString("INST_ID");
        this.priority = data.getString("PRIORITY");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.startCycleId = data.getString("START_CYCLE_ID");
        this.superBankCode = data.getString("SUPER_BANK_CODE");
        this.instId = data.getString("INST_ID");
    }

    @Override
    public AcctConsignTradeData clone()
    {
        AcctConsignTradeData acctConsignTradeData = new AcctConsignTradeData();

        acctConsignTradeData.setAcctBalanceId(this.getAcctBalanceId());
        acctConsignTradeData.setAcctId(this.getAcctId());
        acctConsignTradeData.setActTag(this.getActTag());
        acctConsignTradeData.setAssistantTag(this.getAssistantTag());
        acctConsignTradeData.setBankAcctName(this.getBankAcctName());
        acctConsignTradeData.setBankAcctNo(this.getBankAcctNo());
        acctConsignTradeData.setBankCode(this.getBankCode());
        acctConsignTradeData.setCityCode(this.getCityCode());
        acctConsignTradeData.setConsignMode(this.getConsignMode());
        acctConsignTradeData.setContact(this.getContact());
        acctConsignTradeData.setContactPhone(this.getContactPhone());
        acctConsignTradeData.setContractId(this.getContractId());
        acctConsignTradeData.setContractName(this.getContractName());
        acctConsignTradeData.setEndCycleId(this.getEndCycleId());
        acctConsignTradeData.setEparchyCode(this.getEparchyCode());
        acctConsignTradeData.setModifyTag(this.getModifyTag());
        acctConsignTradeData.setPaymentId(this.getPaymentId());
        acctConsignTradeData.setPayFeeModeCode(this.getPayFeeModeCode());
        acctConsignTradeData.setPayModeCode(this.getPayModeCode());
        acctConsignTradeData.setPostAddress(this.getPostAddress());
        acctConsignTradeData.setInstId(this.getInstId());
        acctConsignTradeData.setPostCode(this.getPostCode());
        acctConsignTradeData.setPriority(this.getPriority());
        acctConsignTradeData.setRemark(this.getRemark());
        acctConsignTradeData.setRsrvStr1(this.getRsrvStr1());
        acctConsignTradeData.setRsrvStr10(this.getRsrvStr10());
        acctConsignTradeData.setRsrvStr2(this.getRsrvStr2());
        acctConsignTradeData.setRsrvStr3(this.getRsrvStr3());
        acctConsignTradeData.setRsrvStr4(this.getRsrvStr4());
        acctConsignTradeData.setRsrvStr5(this.getRsrvStr5());
        acctConsignTradeData.setRsrvStr6(this.getRsrvStr6());
        acctConsignTradeData.setRsrvStr7(this.getRsrvStr7());
        acctConsignTradeData.setRsrvStr8(this.getRsrvStr8());
        acctConsignTradeData.setRsrvStr9(this.getRsrvStr9());
        acctConsignTradeData.setStartCycleId(this.getStartCycleId());
        acctConsignTradeData.setSuperBankCode(this.getSuperBankCode());
        acctConsignTradeData.setInstId(this.getInstId());

        return acctConsignTradeData;
    }

    public String getAcctBalanceId()
    {
        return acctBalanceId;
    }

    public String getAcctId()
    {
        return acctId;
    }

    public String getActTag()
    {
        return actTag;
    }

    public String getAssistantTag()
    {
        return assistantTag;
    }

    public String getBankAcctName()
    {
        return bankAcctName;
    }

    public String getBankAcctNo()
    {
        return bankAcctNo;
    }

    public String getBankCode()
    {
        return bankCode;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getConsignMode()
    {
        return consignMode;
    }

    public String getContact()
    {
        return contact;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public String getContractId()
    {
        return contractId;
    }

    public String getContractName()
    {
        return contractName;
    }

    public String getEndCycleId()
    {
        return endCycleId;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getPayFeeModeCode()
    {
        return payFeeModeCode;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public String getPayModeCode()
    {
        return payModeCode;
    }

    public String getPostAddress()
    {
        return postAddress;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public String getPriority()
    {
        return priority;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return rsrvStr10;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public String getRsrvStr6()
    {
        return rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return rsrvStr9;
    }

    public String getStartCycleId()
    {
        return startCycleId;
    }

    public String getSuperBankCode()
    {
        return superBankCode;
    }

    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_ACCT_CONSIGN";
    }

    public void setAcctBalanceId(String acctBalanceId)
    {
        this.acctBalanceId = acctBalanceId;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setActTag(String actTag)
    {
        this.actTag = actTag;
    }

    public void setAssistantTag(String assistantTag)
    {
        this.assistantTag = assistantTag;
    }

    public void setBankAcctName(String bankAcctName)
    {
        this.bankAcctName = bankAcctName;
    }

    public void setBankAcctNo(String bankAcctNo)
    {
        this.bankAcctNo = bankAcctNo;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setConsignMode(String consignMode)
    {
        this.consignMode = consignMode;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public void setContractId(String contractId)
    {
        this.contractId = contractId;
    }

    public void setContractName(String contractName)
    {
        this.contractName = contractName;
    }

    public void setEndCycleId(String endCycleId)
    {
        this.endCycleId = endCycleId;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setPayFeeModeCode(String payFeeModeCode)
    {
        this.payFeeModeCode = payFeeModeCode;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public void setPayModeCode(String payModeCode)
    {
        this.payModeCode = payModeCode;
    }

    public void setPostAddress(String postAddress)
    {
        this.postAddress = postAddress;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr10(String rsrvStr10)
    {
        this.rsrvStr10 = rsrvStr10;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
    }

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
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

    public void setStartCycleId(String startCycleId)
    {
        this.startCycleId = startCycleId;
    }

    public void setSuperBankCode(String superBankCode)
    {
        this.superBankCode = superBankCode;
    }

    @Override
    public IData toData()
    {
        IData datamap = new DataMap();

        datamap.put("ACCT_BALANCE_ID", this.acctBalanceId);
        datamap.put("ACCT_ID", this.acctId);
        datamap.put("ACT_TAG", this.actTag);
        datamap.put("ASSISTANT_TAG", this.assistantTag);
        datamap.put("BANK_ACCT_NAME", this.bankAcctName);
        datamap.put("BANK_ACCT_NO", this.bankAcctNo);
        datamap.put("BANK_CODE", this.bankCode);
        datamap.put("CITY_CODE", this.cityCode);
        datamap.put("CONSIGN_MODE", this.consignMode);
        datamap.put("CONTACT", this.contact);
        datamap.put("CONTACT_PHONE", this.contactPhone);
        datamap.put("CONTRACT_ID", this.contractId);
        datamap.put("CONTRACT_NAME", this.contractName);
        datamap.put("END_CYCLE_ID", this.endCycleId);
        datamap.put("EPARCHY_CODE", this.eparchyCode);
        datamap.put("MODIFY_TAG", this.modifyTag);
        datamap.put("PAYMENT_ID", this.paymentId);
        datamap.put("PAY_FEE_MODE_CODE", this.payFeeModeCode);
        datamap.put("PAY_MODE_CODE", this.payModeCode);
        datamap.put("POST_ADDRESS", this.postAddress);
        datamap.put("POST_CODE", this.postCode);
        datamap.put("PRIORITY", this.priority);
        datamap.put("INST_ID", this.instId);
        datamap.put("REMARK", this.remark);
        datamap.put("RSRV_STR1", this.rsrvStr1);
        datamap.put("RSRV_STR10", this.rsrvStr10);
        datamap.put("RSRV_STR2", this.rsrvStr2);
        datamap.put("RSRV_STR3", this.rsrvStr3);
        datamap.put("RSRV_STR4", this.rsrvStr4);
        datamap.put("RSRV_STR5", this.rsrvStr5);
        datamap.put("RSRV_STR6", this.rsrvStr6);
        datamap.put("RSRV_STR7", this.rsrvStr7);
        datamap.put("RSRV_STR8", this.rsrvStr8);
        datamap.put("RSRV_STR9", this.rsrvStr9);
        datamap.put("START_CYCLE_ID", this.startCycleId);
        datamap.put("SUPER_BANK_CODE", this.superBankCode);
        datamap.put("INST_ID", this.instId);

        return datamap;
    }

    @Override
    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
