
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class AccountTradeData extends BaseTradeData
{
    private String acctDiffCode;

    private String acctId;

    private String acctPasswd;

    private String acctTag;

    private String bankAcctNo;

    private String bankCode;

    private String basicCreditValue;

    private String cityCode;

    private String contractNo;

    private String creditClassId;

    private String creditValue;

    private String custId;

    private String debutyCode;

    private String debutyUserId;

    private String depositPriorRuleId;

    private String eparchyCode;

    private String itemPriorRuleId;

    private String modifyTag;

    private String netTypeCode;

    private String openDate;

    private String payModeCode;

    private String payName;

    private String remark;

    private String removeDate;

    private String removeTag;

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

    private String scoreValue;

    public AccountTradeData()
    {

    }

    public AccountTradeData(IData data)
    {
        this.setAcctDiffCode(data.getString("ACCT_DIFF_CODE"));
        this.setAcctId(data.getString("ACCT_ID"));
        this.setAcctPasswd(data.getString("ACCT_PASSWD"));
        this.setAcctTag(data.getString("ACCT_TAG"));
        this.setBankAcctNo(data.getString("BANK_ACCT_NO"));
        this.setBankCode(data.getString("BANK_CODE"));
        this.setBasicCreditValue(data.getString("BASIC_CREDIT_VALUE"));
        this.setCityCode(data.getString("CITY_CODE"));
        this.setContractNo(data.getString("CONTRACT_NO"));
        this.setCreditClassId(data.getString("CREDIT_CLASS_ID"));
        this.setCreditValue(data.getString("CREDIT_VALUE"));
        this.setCustId(data.getString("CUST_ID"));
        this.setDebutyCode(data.getString("DEBUTY_CODE"));
        this.setDebutyUserId(data.getString("DEBUTY_USER_ID"));
        this.setDepositPriorRuleId(data.getString("DEPOSIT_PRIOR_RULE_ID"));
        this.setEparchyCode(data.getString("EPARCHY_CODE"));
        this.setItemPriorRuleId(data.getString("ITEM_PRIOR_RULE_ID"));
        this.setModifyTag(data.getString("MODIFY_TAG"));
        this.setNetTypeCode(data.getString("NET_TYPE_CODE"));
        this.setOpenDate(data.getString("OPEN_DATE"));
        this.setPayModeCode(data.getString("PAY_MODE_CODE"));
        this.setPayName(data.getString("PAY_NAME"));
        this.setRemark(data.getString("REMARK"));
        this.setRemoveDate(data.getString("REMOVE_DATE"));
        this.setRemoveTag(data.getString("REMOVE_TAG"));
        this.setRsrvStr1(data.getString("RSRV_STR1"));
        this.setRsrvStr10(data.getString("RSRV_STR10"));
        this.setRsrvStr2(data.getString("RSRV_STR2"));
        this.setRsrvStr3(data.getString("RSRV_STR3"));
        this.setRsrvStr4(data.getString("RSRV_STR4"));
        this.setRsrvStr5(data.getString("RSRV_STR5"));
        this.setRsrvStr6(data.getString("RSRV_STR6"));
        this.setRsrvStr7(data.getString("RSRV_STR7"));
        this.setRsrvStr8(data.getString("RSRV_STR8"));
        this.setRsrvStr9(data.getString("RSRV_STR9"));
        this.setScoreValue(data.getString("SCORE_VALUE"));
    }

    public AccountTradeData clone()
    {
        AccountTradeData accountTradeData = new AccountTradeData();
        accountTradeData.setAcctDiffCode(this.getAcctDiffCode());
        accountTradeData.setAcctId(this.getAcctId());
        accountTradeData.setAcctPasswd(this.getAcctPasswd());
        accountTradeData.setAcctTag(this.getAcctTag());
        accountTradeData.setBankAcctNo(this.getBankAcctNo());
        accountTradeData.setBankCode(this.getBankCode());
        accountTradeData.setBasicCreditValue(this.getBasicCreditValue());
        accountTradeData.setCityCode(this.getCityCode());
        accountTradeData.setContractNo(this.getContractNo());
        accountTradeData.setCreditClassId(this.getCreditClassId());
        accountTradeData.setCreditValue(this.getCreditValue());
        accountTradeData.setCustId(this.getCustId());
        accountTradeData.setDebutyCode(this.getDebutyCode());
        accountTradeData.setDebutyUserId(this.getDebutyUserId());
        accountTradeData.setDepositPriorRuleId(this.getDepositPriorRuleId());
        accountTradeData.setEparchyCode(this.getEparchyCode());
        accountTradeData.setItemPriorRuleId(this.getItemPriorRuleId());
        accountTradeData.setModifyTag(this.getModifyTag());
        accountTradeData.setNetTypeCode(this.getNetTypeCode());
        accountTradeData.setOpenDate(this.getOpenDate());
        accountTradeData.setPayModeCode(this.getPayModeCode());
        accountTradeData.setPayName(this.getPayName());
        accountTradeData.setRemark(this.getRemark());
        accountTradeData.setRemoveDate(this.getRemoveDate());
        accountTradeData.setRemoveTag(this.getRemoveTag());
        accountTradeData.setRsrvStr1(this.getRsrvStr1());
        accountTradeData.setRsrvStr2(this.getRsrvStr2());
        accountTradeData.setRsrvStr3(this.getRsrvStr3());
        accountTradeData.setRsrvStr4(this.getRsrvStr4());
        accountTradeData.setRsrvStr5(this.getRsrvStr5());
        accountTradeData.setRsrvStr6(this.getRsrvStr6());
        accountTradeData.setRsrvStr7(this.getRsrvStr7());
        accountTradeData.setRsrvStr8(this.getRsrvStr8());
        accountTradeData.setRsrvStr9(this.getRsrvStr9());
        accountTradeData.setRsrvStr10(this.getRsrvStr10());
        accountTradeData.setScoreValue(this.getScoreValue());

        return accountTradeData;
    }

    public String getAcctDiffCode()
    {
        return acctDiffCode;
    }

    public String getAcctId()
    {
        return acctId;
    }

    public String getAcctPasswd()
    {
        return acctPasswd;
    }

    public String getAcctTag()
    {
        return acctTag;
    }

    public String getBankAcctNo()
    {
        return bankAcctNo;
    }

    public String getBankCode()
    {
        return bankCode;
    }

    public String getBasicCreditValue()
    {
        return basicCreditValue;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getContractNo()
    {
        return contractNo;
    }

    public String getCreditClassId()
    {
        return creditClassId;
    }

    public String getCreditValue()
    {
        return creditValue;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getDebutyCode()
    {
        return debutyCode;
    }

    public String getDebutyUserId()
    {
        return debutyUserId;
    }

    public String getDepositPriorRuleId()
    {
        return depositPriorRuleId;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getItemPriorRuleId()
    {
        return itemPriorRuleId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public String getOpenDate()
    {
        return openDate;
    }

    public String getPayModeCode()
    {
        return payModeCode;
    }

    public String getPayName()
    {
        return payName;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRemoveDate()
    {
        return removeDate;
    }

    public String getRemoveTag()
    {
        return removeTag;
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

    public String getScoreValue()
    {
        return scoreValue;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_ACCOUNT";
    }

    public void setAcctDiffCode(String acctDiffCode)
    {
        this.acctDiffCode = acctDiffCode;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setAcctPasswd(String acctPasswd)
    {
        this.acctPasswd = acctPasswd;
    }

    public void setAcctTag(String acctTag)
    {
        this.acctTag = acctTag;
    }

    public void setBankAcctNo(String bankAcctNo)
    {
        this.bankAcctNo = bankAcctNo;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public void setBasicCreditValue(String basicCreditValue)
    {
        this.basicCreditValue = basicCreditValue;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setContractNo(String contractNo)
    {
        this.contractNo = contractNo;
    }

    public void setCreditClassId(String creditClassId)
    {
        this.creditClassId = creditClassId;
    }

    public void setCreditValue(String creditValue)
    {
        this.creditValue = creditValue;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setDebutyCode(String debutyCode)
    {
        this.debutyCode = debutyCode;
    }

    public void setDebutyUserId(String debutyUserId)
    {
        this.debutyUserId = debutyUserId;
    }

    public void setDepositPriorRuleId(String depositPriorRuleId)
    {
        this.depositPriorRuleId = depositPriorRuleId;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setItemPriorRuleId(String itemPriorRuleId)
    {
        this.itemPriorRuleId = itemPriorRuleId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setOpenDate(String openDate)
    {
        this.openDate = openDate;
    }

    public void setPayModeCode(String payModeCode)
    {
        this.payModeCode = payModeCode;
    }

    public void setPayName(String payName)
    {
        this.payName = payName;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRemoveDate(String removeDate)
    {
        this.removeDate = removeDate;
    }

    public void setRemoveTag(String removeTag)
    {
        this.removeTag = removeTag;
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

    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("ACCT_DIFF_CODE", this.acctDiffCode);
        data.put("ACCT_ID", this.acctId);
        data.put("ACCT_PASSWD", this.acctPasswd);
        data.put("ACCT_TAG", this.acctTag);
        data.put("BANK_ACCT_NO", this.bankAcctNo);
        data.put("BANK_CODE", this.bankCode);
        data.put("BASIC_CREDIT_VALUE", this.basicCreditValue);
        data.put("CITY_CODE", this.cityCode);
        data.put("CONTRACT_NO", this.contractNo);
        data.put("CREDIT_CLASS_ID", this.creditClassId);
        data.put("CREDIT_VALUE", this.creditValue);
        data.put("CUST_ID", this.custId);
        data.put("DEBUTY_CODE", this.debutyCode);
        data.put("DEBUTY_USER_ID", this.debutyUserId);
        data.put("DEPOSIT_PRIOR_RULE_ID", this.depositPriorRuleId);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("ITEM_PRIOR_RULE_ID", this.itemPriorRuleId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("NET_TYPE_CODE", this.netTypeCode);
        data.put("OPEN_DATE", this.openDate);
        data.put("PAY_MODE_CODE", this.payModeCode);
        data.put("PAY_NAME", this.payName);
        data.put("REMARK", this.remark);
        data.put("REMOVE_DATE", this.removeDate);
        data.put("REMOVE_TAG", this.removeTag);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("SCORE_VALUE", this.scoreValue);

        return data;
    }

    @Override
    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }

}
