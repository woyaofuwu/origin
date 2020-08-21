
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class BankSubSignTradeData extends BaseTradeData
{
    private String signId;

    private String mainUserType;

    private String mainUserValue;

    private String mainSignType;

    private String subUserType;

    private String subUserValue;

    private String subSignType;

    private String mainEparchyCode;

    private String chnlType;

    private String modifyTag;

    private String startDate;

    private String endDate;

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

    public BankSubSignTradeData()
    {
    }

    public BankSubSignTradeData(IData data)
    {
        this.signId = data.getString("SIGN_ID");
        this.mainUserType = data.getString("MAIN_USER_TYPE");
        this.mainUserValue = data.getString("MAIN_USER_VALUE");
        this.mainSignType = data.getString("MAIN_SIGN_TYPE");
        this.subUserType = data.getString("SUB_USER_TYPE");
        this.subUserValue = data.getString("SUB_USER_VALUE");
        this.subSignType = data.getString("SUB_SIGN_TYPE");
        this.mainEparchyCode = data.getString("MAIN_EPARCHY_CODE");
        this.chnlType = data.getString("CHNL_TYPE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
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

    @Override
    public BankSubSignTradeData clone()
    {
        BankSubSignTradeData bankSubSignTradeData = new BankSubSignTradeData();
        bankSubSignTradeData.setSignId(this.getSignId());
        bankSubSignTradeData.setMainUserType(this.getMainUserType());
        bankSubSignTradeData.setMainUserValue(this.getMainUserValue());
        bankSubSignTradeData.setMainSignType(this.getMainSignType());
        bankSubSignTradeData.setSubUserType(this.getSubUserType());
        bankSubSignTradeData.setSubUserValue(this.getSubUserValue());
        bankSubSignTradeData.setSubSignType(this.getSubSignType());
        bankSubSignTradeData.setMainEparchyCode(this.getMainEparchyCode());
        bankSubSignTradeData.setChnlType(this.getChnlType());
        bankSubSignTradeData.setModifyTag(this.getModifyTag());
        bankSubSignTradeData.setStartDate(this.getStartDate());
        bankSubSignTradeData.setEndDate(this.getEndDate());
        bankSubSignTradeData.setUpdateDate(this.getUpdateDate());
        bankSubSignTradeData.setRemark(this.getRemark());
        bankSubSignTradeData.setRsrvNum1(this.getRsrvNum1());
        bankSubSignTradeData.setRsrvNum2(this.getRsrvNum2());
        bankSubSignTradeData.setRsrvNum3(this.getRsrvNum3());
        bankSubSignTradeData.setRsrvNum4(this.getRsrvNum4());
        bankSubSignTradeData.setRsrvNum5(this.getRsrvNum5());
        bankSubSignTradeData.setRsrvStr1(this.getRsrvStr1());
        bankSubSignTradeData.setRsrvStr2(this.getRsrvStr2());
        bankSubSignTradeData.setRsrvStr3(this.getRsrvStr3());
        bankSubSignTradeData.setRsrvStr4(this.getRsrvStr4());
        bankSubSignTradeData.setRsrvStr5(this.getRsrvStr5());
        bankSubSignTradeData.setRsrvStr6(this.getRsrvStr6());
        bankSubSignTradeData.setRsrvStr7(this.getRsrvStr7());
        bankSubSignTradeData.setRsrvStr8(this.getRsrvStr8());
        bankSubSignTradeData.setRsrvStr9(this.getRsrvStr9());
        bankSubSignTradeData.setRsrvStr10(this.getRsrvStr10());
        bankSubSignTradeData.setRsrvDate1(this.getRsrvDate1());
        bankSubSignTradeData.setRsrvDate2(this.getRsrvDate2());
        bankSubSignTradeData.setRsrvDate3(this.getRsrvDate3());
        bankSubSignTradeData.setRsrvTag1(this.getRsrvTag1());
        bankSubSignTradeData.setRsrvTag2(this.getRsrvTag2());
        bankSubSignTradeData.setRsrvTag3(this.getRsrvTag3());
        bankSubSignTradeData.setInstId(this.instId);
        return bankSubSignTradeData;
    }

    public String getChnlType()
    {
        return this.chnlType;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getMainEparchyCode()
    {
        return this.mainEparchyCode;
    }

    public String getMainSignType()
    {
        return this.mainSignType;
    }

    public String getMainUserType()
    {
        return this.mainUserType;
    }

    public String getMainUserValue()
    {
        return this.mainUserValue;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
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

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getSubSignType()
    {
        return this.subSignType;
    }

    public String getSubUserType()
    {
        return this.subUserType;
    }

    public String getSubUserValue()
    {
        return this.subUserValue;
    }

    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_BANK_SUBSIGN";
    }

    public String getUpdateDate()
    {
        return this.updateDate;
    }

    public void setChnlType(String chnlType)
    {
        this.chnlType = chnlType;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setMainEparchyCode(String mainEparchyCode)
    {
        this.mainEparchyCode = mainEparchyCode;
    }

    public void setMainSignType(String mainSignType)
    {
        this.mainSignType = mainSignType;
    }

    public void setMainUserType(String mainUserType)
    {
        this.mainUserType = mainUserType;
    }

    public void setMainUserValue(String mainUserValue)
    {
        this.mainUserValue = mainUserValue;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
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

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setSubSignType(String subSignType)
    {
        this.subSignType = subSignType;
    }

    public void setSubUserType(String subUserType)
    {
        this.subUserType = subUserType;
    }

    public void setSubUserValue(String subUserValue)
    {
        this.subUserValue = subUserValue;
    }

    public void setUpdateDate(String updateDate)
    {
        this.updateDate = updateDate;
    }

    @Override
    public IData toData()
    {
        IData data = new DataMap();
        data.put("SIGN_ID", this.signId);
        data.put("MAIN_USER_TYPE", this.mainUserType);
        data.put("MAIN_USER_VALUE", this.mainUserValue);
        data.put("MAIN_SIGN_TYPE", this.mainSignType);
        data.put("SUB_USER_TYPE", this.subUserType);
        data.put("SUB_USER_VALUE", this.subUserValue);
        data.put("SUB_SIGN_TYPE", this.subSignType);
        data.put("MAIN_EPARCHY_CODE", this.mainEparchyCode);
        data.put("CHNL_TYPE", this.chnlType);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
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

    @Override
    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
