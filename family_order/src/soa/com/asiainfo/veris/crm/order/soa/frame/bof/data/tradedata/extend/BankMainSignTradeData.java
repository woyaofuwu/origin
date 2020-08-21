
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class BankMainSignTradeData extends BaseTradeData
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private String signId;

    private String userType;

    private String userValue;

    private String homeArea;

    private String bankAcctId;

    private String bankAcctType;

    private String bankId;

    private String chnlType;

    private String payType;

    private String rechThreshold;

    private String rechAmount;

    private String signState;

    private String modifyTag;

    private String applyDate;

    private String startDate;

    private String endDate;

    private String userName;

    private String idType;

    private String idValue;

    private String updateDate;

    private String remark;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvNum4;

    private String rsrvNum5;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String rsrvStr10;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String instId;

    public BankMainSignTradeData()
    {
    }

    public BankMainSignTradeData(IData data)
    {
        this.signId = data.getString("SIGN_ID");
        this.userType = data.getString("USER_TYPE");
        this.userValue = data.getString("USER_VALUE");
        this.homeArea = data.getString("HOME_AREA");
        this.bankAcctId = data.getString("BANK_ACCT_ID");
        this.bankAcctType = data.getString("BANK_ACCT_TYPE");
        this.bankId = data.getString("BANK_ID");
        this.chnlType = data.getString("CHNL_TYPE");
        this.payType = data.getString("PAY_TYPE");
        this.rechThreshold = data.getString("RECH_THRESHOLD");
        this.rechAmount = data.getString("RECH_AMOUNT");
        this.signState = data.getString("SIGN_STATE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.applyDate = data.getString("APPLY_DATE");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.userName = data.getString("USER_NAME");
        this.idType = data.getString("ID_TYPE");
        this.idValue = data.getString("ID_VALUE");
        this.updateDate = data.getString("UPDATE_DATE");
        this.remark = data.getString("REMARK");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvNum4 = data.getString("RSRV_NUM4");
        this.rsrvNum5 = data.getString("RSRV_NUM5");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.instId = data.getString("INST_ID");
    }

    public BankMainSignTradeData clone()
    {
        BankMainSignTradeData bankMainSignTradeData = new BankMainSignTradeData();
        bankMainSignTradeData.setSignId(this.getSignId());
        bankMainSignTradeData.setUserType(this.getUserType());
        bankMainSignTradeData.setUserValue(this.getUserValue());
        bankMainSignTradeData.setHomeArea(this.getHomeArea());
        bankMainSignTradeData.setBankAcctId(this.getBankAcctId());
        bankMainSignTradeData.setBankAcctType(this.getBankAcctType());
        bankMainSignTradeData.setBankId(this.getBankId());
        bankMainSignTradeData.setChnlType(this.getChnlType());
        bankMainSignTradeData.setPayType(this.getPayType());
        bankMainSignTradeData.setRechThreshold(this.getRechThreshold());
        bankMainSignTradeData.setRechAmount(this.getRechAmount());
        bankMainSignTradeData.setSignState(this.getSignState());
        bankMainSignTradeData.setModifyTag(this.getModifyTag());
        bankMainSignTradeData.setApplyDate(this.getApplyDate());
        bankMainSignTradeData.setStartDate(this.getStartDate());
        bankMainSignTradeData.setEndDate(this.getEndDate());
        bankMainSignTradeData.setUserName(this.getUserName());
        bankMainSignTradeData.setIdType(this.getIdType());
        bankMainSignTradeData.setIdValue(this.getIdValue());
        bankMainSignTradeData.setUpdateDate(this.getUpdateDate());
        bankMainSignTradeData.setRemark(this.getRemark());
        bankMainSignTradeData.setRsrvNum1(this.getRsrvNum1());
        bankMainSignTradeData.setRsrvNum2(this.getRsrvNum2());
        bankMainSignTradeData.setRsrvNum3(this.getRsrvNum3());
        bankMainSignTradeData.setRsrvNum4(this.getRsrvNum4());
        bankMainSignTradeData.setRsrvNum5(this.getRsrvNum5());
        bankMainSignTradeData.setRsrvStr1(this.getRsrvStr1());
        bankMainSignTradeData.setRsrvStr2(this.getRsrvStr2());
        bankMainSignTradeData.setRsrvStr3(this.getRsrvStr3());
        bankMainSignTradeData.setRsrvStr4(this.getRsrvStr4());
        bankMainSignTradeData.setRsrvStr5(this.getRsrvStr5());
        bankMainSignTradeData.setRsrvStr6(this.getRsrvStr6());
        bankMainSignTradeData.setRsrvStr7(this.getRsrvStr7());
        bankMainSignTradeData.setRsrvStr8(this.getRsrvStr8());
        bankMainSignTradeData.setRsrvStr9(this.getRsrvStr9());
        bankMainSignTradeData.setRsrvStr10(this.getRsrvStr10());
        bankMainSignTradeData.setRsrvDate1(this.getRsrvDate1());
        bankMainSignTradeData.setRsrvDate2(this.getRsrvDate2());
        bankMainSignTradeData.setRsrvDate3(this.getRsrvDate3());
        bankMainSignTradeData.setRsrvTag1(this.getRsrvTag1());
        bankMainSignTradeData.setRsrvTag2(this.getRsrvTag2());
        bankMainSignTradeData.setRsrvTag3(this.getRsrvTag3());
        bankMainSignTradeData.setInstId(this.getInstId());
        return bankMainSignTradeData;
    }

    public String getApplyDate()
    {
        return this.applyDate;
    }

    public String getBankAcctId()
    {
        return this.bankAcctId;
    }

    public String getBankAcctType()
    {
        return this.bankAcctType;
    }

    public String getBankId()
    {
        return this.bankId;
    }

    public String getChnlType()
    {
        return this.chnlType;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getHomeArea()
    {
        return this.homeArea;
    }

    public String getIdType()
    {
        return this.idType;
    }

    public String getIdValue()
    {
        return this.idValue;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getPayType()
    {
        return this.payType;
    }

    public String getRechAmount()
    {
        return this.rechAmount;
    }

    public String getRechThreshold()
    {
        return this.rechThreshold;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public String getRsrvDate1()
    {
        return this.rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return this.rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return this.rsrvDate3;
    }

    public String getRsrvNum1()
    {
        return this.rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return this.rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return this.rsrvNum3;
    }

    public String getRsrvNum4()
    {
        return this.rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return this.rsrvNum5;
    }

    public String getRsrvStr1()
    {
        return this.rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return this.rsrvStr10;
    }

    public String getRsrvStr2()
    {
        return this.rsrvStr2;
    }

    public String getRsrvStr3()
    {
        return this.rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return this.rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return this.rsrvStr5;
    }

    public String getRsrvStr6()
    {
        return this.rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return this.rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return this.rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return this.rsrvStr9;
    }

    public String getRsrvTag1()
    {
        return this.rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return this.rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return this.rsrvTag3;
    }

    public String getSignId()
    {
        return this.signId;
    }

    public String getSignState()
    {
        return this.signState;
    }

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_BANK_MAINSIGN";
    }

    public String getUpdateDate()
    {
        return this.updateDate;
    }

    public String getUserName()
    {
        return this.userName;
    }

    public String getUserType()
    {
        return this.userType;
    }

    public String getUserValue()
    {
        return this.userValue;
    }

    public void setApplyDate(String applyDate)
    {
        this.applyDate = applyDate;
    }

    public void setBankAcctId(String bankAcctId)
    {
        this.bankAcctId = bankAcctId;
    }

    public void setBankAcctType(String bankAcctType)
    {
        this.bankAcctType = bankAcctType;
    }

    public void setBankId(String bankId)
    {
        this.bankId = bankId;
    }

    public void setChnlType(String chnlType)
    {
        this.chnlType = chnlType;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setHomeArea(String homeArea)
    {
        this.homeArea = homeArea;
    }

    public void setIdType(String idType)
    {
        this.idType = idType;
    }

    public void setIdValue(String idValue)
    {
        this.idValue = idValue;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setPayType(String payType)
    {
        this.payType = payType;
    }

    public void setRechAmount(String rechAmount)
    {
        this.rechAmount = rechAmount;
    }

    public void setRechThreshold(String rechThreshold)
    {
        this.rechThreshold = rechThreshold;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    public void setRsrvDate3(String rsrvDate3)
    {
        this.rsrvDate3 = rsrvDate3;
    }

    public void setRsrvNum1(String rsrvNum1)
    {
        this.rsrvNum1 = rsrvNum1;
    }

    public void setRsrvNum2(String rsrvNum2)
    {
        this.rsrvNum2 = rsrvNum2;
    }

    public void setRsrvNum3(String rsrvNum3)
    {
        this.rsrvNum3 = rsrvNum3;
    }

    public void setRsrvNum4(String rsrvNum4)
    {
        this.rsrvNum4 = rsrvNum4;
    }

    public void setRsrvNum5(String rsrvNum5)
    {
        this.rsrvNum5 = rsrvNum5;
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

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }

    public void setSignId(String signId)
    {
        this.signId = signId;
    }

    public void setSignState(String signState)
    {
        this.signState = signState;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUpdateDate(String updateDate)
    {
        this.updateDate = updateDate;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public void setUserValue(String userValue)
    {
        this.userValue = userValue;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("SIGN_ID", this.signId);
        data.put("USER_TYPE", this.userType);
        data.put("USER_VALUE", this.userValue);
        data.put("HOME_AREA", this.homeArea);
        data.put("BANK_ACCT_ID", this.bankAcctId);
        data.put("BANK_ACCT_TYPE", this.bankAcctType);
        data.put("BANK_ID", this.bankId);
        data.put("CHNL_TYPE", this.chnlType);
        data.put("PAY_TYPE", this.payType);
        data.put("RECH_THRESHOLD", this.rechThreshold);
        data.put("RECH_AMOUNT", this.rechAmount);
        data.put("SIGN_STATE", this.signState);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("APPLY_DATE", this.applyDate);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("USER_NAME", this.userName);
        data.put("ID_TYPE", this.idType);
        data.put("ID_VALUE", this.idValue);
        data.put("UPDATE_DATE", this.updateDate);
        data.put("REMARK", this.remark);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_NUM4", this.rsrvNum4);
        data.put("RSRV_NUM5", this.rsrvNum5);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("INST_ID", this.instId);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
