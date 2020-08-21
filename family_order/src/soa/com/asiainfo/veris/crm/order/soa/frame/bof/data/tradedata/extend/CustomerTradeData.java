
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class CustomerTradeData extends BaseTradeData
{
    private String basicCreditValue;

    private String cityCode;

    private String cityCodeA;

    private String creditClass;

    private String creditValue;

    private String custId;

    private String custKind;

    private String custName;

    private String custPasswd;

    private String custState;

    private String custType;

    private String developDepartId;

    private String developStaffId;

    private String eparchyCode;

    private String inDate;

    private String inDepartId;

    private String inStaffId;

    private String isRealName;

    private String modifyTag;

    private String openLimit;

    private String psptId;

    private String psptTypeCode;

    private String remark;

    private String removeChange;

    private String removeDate;

    private String removeStaffId;

    private String removeTag;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvDate4;

    private String rsrvDate5;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvNum4;

    private String rsrvNum5;

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

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String rsrvTag4;

    private String rsrvTag5;

    private String scoreValue;

    private String simpleSpell;

    public CustomerTradeData()
    {

    }

    public CustomerTradeData(IData data)
    {
        this.setBasicCreditValue(data.getString("BASIC_CREDIT_VALUE"));
        this.setCityCode(data.getString("CITY_CODE"));
        this.setCityCodeA(data.getString("CITY_CODE_A"));
        this.setCreditClass(data.getString("CREDIT_CLASS"));
        this.setCreditValue(data.getString("CREDIT_VALUE"));
        this.setCustId(data.getString("CUST_ID"));
        this.setCustKind(data.getString("CUST_KIND"));
        this.setCustName(data.getString("CUST_NAME"));
        this.setCustPasswd(data.getString("CUST_PASSWD"));
        this.setCustState(data.getString("CUST_STATE"));
        this.setCustType(data.getString("CUST_TYPE"));
        this.setDevelopDepartId(data.getString("DEVELOP_DEPART_ID"));
        this.setDevelopStaffId(data.getString("DEVELOP_STAFF_ID"));
        this.setEparchyCode(data.getString("EPARCHY_CODE"));
        this.setInDate(data.getString("IN_DATE"));
        this.setInDepartId(data.getString("IN_DEPART_ID"));
        this.setInStaffId(data.getString("IN_STAFF_ID"));
        this.setIsRealName(data.getString("IS_REAL_NAME"));
        this.setModifyTag(data.getString("MODIFY_TAG"));
        this.setOpenLimit(data.getString("OPEN_LIMIT"));
        this.setPsptId(data.getString("PSPT_ID"));
        this.setPsptTypeCode(data.getString("PSPT_TYPE_CODE"));
        this.setRemark(data.getString("REMARK"));
        this.setRemoveChange(data.getString("REMOVE_CHANGE"));
        this.setRemoveDate(data.getString("REMOVE_DATE"));
        this.setRemoveStaffId(data.getString("REMOVE_STAFF_ID"));
        this.setRemoveTag(data.getString("REMOVE_TAG"));
        this.setRsrvDate1(data.getString("RSRV_DATE1"));
        this.setRsrvDate2(data.getString("RSRV_DATE2"));
        this.setRsrvDate3(data.getString("RSRV_DATE3"));
        this.setRsrvDate4(data.getString("RSRV_DATE4"));
        this.setRsrvDate5(data.getString("RSRV_DATE5"));
        this.setRsrvNum1(data.getString("RSRV_NUM1"));
        this.setRsrvNum2(data.getString("RSRV_NUM2"));
        this.setRsrvNum3(data.getString("RSRV_NUM3"));
        this.setRsrvNum4(data.getString("RSRV_NUM4"));
        this.setRsrvNum5(data.getString("RSRV_NUM5"));
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
        this.setRsrvTag1(data.getString("RSRV_TAG1"));
        this.setRsrvTag2(data.getString("RSRV_TAG2"));
        this.setRsrvTag3(data.getString("RSRV_TAG3"));
        this.setRsrvTag4(data.getString("RSRV_TAG4"));
        this.setRsrvTag5(data.getString("RSRV_TAG5"));
        this.setScoreValue(data.getString("SCORE_VALUE"));
        this.setSimpleSpell(data.getString("SIMPLE_SPELL"));
    }

    public CustomerTradeData clone()
    {
        CustomerTradeData customerTradeData = new CustomerTradeData();
        customerTradeData.setBasicCreditValue(this.getBasicCreditValue());
        customerTradeData.setCityCode(this.getCityCode());
        customerTradeData.setCityCodeA(this.getCityCodeA());
        customerTradeData.setCreditClass(this.getCreditClass());
        customerTradeData.setCreditValue(this.getCreditValue());
        customerTradeData.setCustId(this.getCustId());
        customerTradeData.setCustKind(this.getCustKind());
        customerTradeData.setCustName(this.getCustName());
        customerTradeData.setCustPasswd(this.getCustPasswd());
        customerTradeData.setCustState(this.getCustState());
        customerTradeData.setCustType(this.getCustType());
        customerTradeData.setDevelopDepartId(this.getDevelopDepartId());
        customerTradeData.setDevelopStaffId(this.getDevelopStaffId());
        customerTradeData.setEparchyCode(this.getEparchyCode());
        customerTradeData.setInDate(this.getInDate());
        customerTradeData.setInDepartId(this.getInDepartId());
        customerTradeData.setInStaffId(this.getInStaffId());
        customerTradeData.setIsRealName(this.getIsRealName());
        customerTradeData.setModifyTag(this.getModifyTag());
        customerTradeData.setOpenLimit(this.getOpenLimit());
        customerTradeData.setPsptId(this.getPsptId());
        customerTradeData.setPsptTypeCode(this.getPsptTypeCode());
        customerTradeData.setRemark(this.getRemark());
        customerTradeData.setRemoveChange(this.getRemoveChange());
        customerTradeData.setRemoveDate(this.getRemoveDate());
        customerTradeData.setRemoveStaffId(this.getRemoveStaffId());
        customerTradeData.setRemoveTag(this.getRemoveTag());
        customerTradeData.setRsrvDate1(this.getRsrvDate1());
        customerTradeData.setRsrvDate2(this.getRsrvDate2());
        customerTradeData.setRsrvDate3(this.getRsrvDate3());
        customerTradeData.setRsrvDate4(this.getRsrvDate4());
        customerTradeData.setRsrvDate5(this.getRsrvDate5());
        customerTradeData.setRsrvNum1(this.getRsrvNum1());
        customerTradeData.setRsrvNum2(this.getRsrvNum2());
        customerTradeData.setRsrvNum3(this.getRsrvNum3());
        customerTradeData.setRsrvNum4(this.getRsrvNum4());
        customerTradeData.setRsrvNum5(this.getRsrvNum5());
        customerTradeData.setRsrvStr1(this.getRsrvStr1());
        customerTradeData.setRsrvStr10(this.getRsrvStr10());
        customerTradeData.setRsrvStr2(this.getRsrvStr2());
        customerTradeData.setRsrvStr3(this.getRsrvStr3());
        customerTradeData.setRsrvStr4(this.getRsrvStr4());
        customerTradeData.setRsrvStr5(this.getRsrvStr5());
        customerTradeData.setRsrvStr6(this.getRsrvStr6());
        customerTradeData.setRsrvStr7(this.getRsrvStr7());
        customerTradeData.setRsrvStr8(this.getRsrvStr8());
        customerTradeData.setRsrvStr9(this.getRsrvStr9());
        customerTradeData.setRsrvTag1(this.getRsrvTag1());
        customerTradeData.setRsrvTag2(this.getRsrvTag2());
        customerTradeData.setRsrvTag3(this.getRsrvTag3());
        customerTradeData.setRsrvTag4(this.getRsrvTag4());
        customerTradeData.setRsrvTag5(this.getRsrvTag5());
        customerTradeData.setScoreValue(this.getScoreValue());
        customerTradeData.setSimpleSpell(this.getSimpleSpell());
        return customerTradeData;
    }

    public String getBasicCreditValue()
    {
        return basicCreditValue;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public String getCityCodeA()
    {
        return cityCodeA;
    }

    public String getCreditClass()
    {
        return creditClass;
    }

    public String getCreditValue()
    {
        return creditValue;
    }

    public String getCustId()
    {
        return custId;
    }

    public String getCustKind()
    {
        return custKind;
    }

    public String getCustName()
    {
        return custName;
    }

    public String getCustPasswd()
    {
        return custPasswd;
    }

    public String getCustState()
    {
        return custState;
    }

    public String getCustType()
    {
        return custType;
    }

    public String getDevelopDepartId()
    {
        return developDepartId;
    }

    public String getDevelopStaffId()
    {
        return developStaffId;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getInDate()
    {
        return inDate;
    }

    public String getInDepartId()
    {
        return inDepartId;
    }

    public String getInStaffId()
    {
        return inStaffId;
    }

    public String getIsRealName()
    {
        return isRealName;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getOpenLimit()
    {
        return openLimit;
    }

    public String getPsptId()
    {
        return psptId;
    }

    public String getPsptTypeCode()
    {
        return psptTypeCode;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRemoveChange()
    {
        return removeChange;
    }

    public String getRemoveDate()
    {
        return removeDate;
    }

    public String getRemoveStaffId()
    {
        return removeStaffId;
    }

    public String getRemoveTag()
    {
        return removeTag;
    }

    public String getRsrvDate1()
    {
        return rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return rsrvDate3;
    }

    public String getRsrvDate4()
    {
        return rsrvDate4;
    }

    public String getRsrvDate5()
    {
        return rsrvDate5;
    }

    public String getRsrvNum1()
    {
        return rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return rsrvNum3;
    }

    public String getRsrvNum4()
    {
        return rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return rsrvNum5;
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

    public String getRsrvTag1()
    {
        return rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public String getRsrvTag4()
    {
        return rsrvTag4;
    }

    public String getRsrvTag5()
    {
        return rsrvTag5;
    }

    public String getScoreValue()
    {
        return scoreValue;
    }

    public String getSimpleSpell()
    {
        return simpleSpell;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_CUSTOMER";
    }

    public void setBasicCreditValue(String basicCreditValue)
    {
        this.basicCreditValue = basicCreditValue;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setCityCodeA(String cityCodeA)
    {
        this.cityCodeA = cityCodeA;
    }

    public void setCreditClass(String creditClass)
    {
        this.creditClass = creditClass;
    }

    public void setCreditValue(String creditValue)
    {
        this.creditValue = creditValue;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public void setCustKind(String custKind)
    {
        this.custKind = custKind;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public void setCustPasswd(String custPasswd)
    {
        this.custPasswd = custPasswd;
    }

    public void setCustState(String custState)
    {
        this.custState = custState;
    }

    public void setCustType(String custType)
    {
        this.custType = custType;
    }

    public void setDevelopDepartId(String developDepartId)
    {
        this.developDepartId = developDepartId;
    }

    public void setDevelopStaffId(String developStaffId)
    {
        this.developStaffId = developStaffId;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setInDate(String inDate)
    {
        this.inDate = inDate;
    }

    public void setInDepartId(String inDepartId)
    {
        this.inDepartId = inDepartId;
    }

    public void setInStaffId(String inStaffId)
    {
        this.inStaffId = inStaffId;
    }

    public void setIsRealName(String isRealName)
    {
        this.isRealName = isRealName;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setOpenLimit(String openLimit)
    {
        this.openLimit = openLimit;
    }

    public void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public void setPsptTypeCode(String psptTypeCode)
    {
        this.psptTypeCode = psptTypeCode;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRemoveChange(String removeChange)
    {
        this.removeChange = removeChange;
    }

    public void setRemoveDate(String removeDate)
    {
        this.removeDate = removeDate;
    }

    public void setRemoveStaffId(String removeStaffId)
    {
        this.removeStaffId = removeStaffId;
    }

    public void setRemoveTag(String removeTag)
    {
        this.removeTag = removeTag;
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

    public void setRsrvDate4(String rsrvDate4)
    {
        this.rsrvDate4 = rsrvDate4;
    }

    public void setRsrvDate5(String rsrvDate5)
    {
        this.rsrvDate5 = rsrvDate5;
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

    public void setRsrvTag4(String rsrvTag4)
    {
        this.rsrvTag4 = rsrvTag4;
    }

    public void setRsrvTag5(String rsrvTag5)
    {
        this.rsrvTag5 = rsrvTag5;
    }

    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }

    public void setSimpleSpell(String simpleSpell)
    {
        this.simpleSpell = simpleSpell;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("BASIC_CREDIT_VALUE", this.basicCreditValue);
        data.put("CITY_CODE", this.cityCode);
        data.put("CITY_CODE_A", this.cityCodeA);
        data.put("CREDIT_CLASS", this.creditClass);
        data.put("CREDIT_VALUE", this.creditValue);
        data.put("CUST_ID", this.custId);
        data.put("CUST_KIND", this.custKind);
        data.put("CUST_NAME", this.custName);
        data.put("CUST_PASSWD", this.custPasswd);
        data.put("CUST_STATE", this.custState);
        data.put("CUST_TYPE", this.custType);
        data.put("DEVELOP_DEPART_ID", this.developDepartId);
        data.put("DEVELOP_STAFF_ID", this.developStaffId);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("IN_DATE", this.inDate);
        data.put("IN_DEPART_ID", this.inDepartId);
        data.put("IN_STAFF_ID", this.inStaffId);
        data.put("IS_REAL_NAME", this.isRealName);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("OPEN_LIMIT", this.openLimit);
        data.put("PSPT_ID", this.psptId);
        data.put("PSPT_TYPE_CODE", this.psptTypeCode);
        data.put("REMARK", this.remark);
        data.put("REMOVE_CHANGE", this.removeChange);
        data.put("REMOVE_DATE", this.removeDate);
        data.put("REMOVE_STAFF_ID", this.removeStaffId);
        data.put("REMOVE_TAG", this.removeTag);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_DATE4", this.rsrvDate4);
        data.put("RSRV_DATE5", this.rsrvDate5);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_NUM4", this.rsrvNum4);
        data.put("RSRV_NUM5", this.rsrvNum5);
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
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("RSRV_TAG4", this.rsrvTag4);
        data.put("RSRV_TAG5", this.rsrvTag5);
        data.put("SCORE_VALUE", this.scoreValue);
        data.put("SIMPLE_SPELL", this.simpleSpell);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
