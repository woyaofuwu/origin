
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class WideNetTradeData extends BaseTradeData
{
    private String acctPasswd;

    private String contact;

    private String contactPhone;

    private String detailAddress;

    private String endDate;

    private String instId;

    private String modifyTag;

    private String oldDetailAddress;

    private String oldStandAddress;

    private String oldStandAddressCode;

    private String phone;

    private String portType;

    private String remark;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

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

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String signPath;

    private String standAddress;

    private String standAddressCode;
    
    private String constructionAddr;
    
    private String constStaffId;
    
    private String constPhone;

    private String startDate;

    private String suggestDate;

    private String userId;

    public WideNetTradeData()
    {

    }

    public WideNetTradeData(IData data)
    {
        this.acctPasswd = data.getString("ACCT_PASSWD");
        this.contact = data.getString("CONTACT");
        this.contactPhone = data.getString("CONTACT_PHONE");
        this.detailAddress = data.getString("DETAIL_ADDRESS");
        this.endDate = data.getString("END_DATE");
        this.instId = data.getString("INST_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.oldDetailAddress = data.getString("OLD_DETAIL_ADDRESS");
        this.oldStandAddress = data.getString("OLD_STAND_ADDRESS");
        this.oldStandAddressCode = data.getString("OLD_STAND_ADDRESS_CODE");
        this.phone = data.getString("PHONE");
        this.portType = data.getString("PORT_TYPE");
        this.remark = data.getString("REMARK");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
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
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.signPath = data.getString("SIGN_PATH");
        this.standAddress = data.getString("STAND_ADDRESS");
        this.standAddressCode = data.getString("STAND_ADDRESS_CODE");
        this.startDate = data.getString("START_DATE");
        this.suggestDate = data.getString("SUGGEST_DATE");
        this.userId = data.getString("USER_ID");
        this.constructionAddr = data.getString("CONSTRUCTION_ADDR");
        this.constStaffId = data.getString("CONST_STAFF_ID");
        this.constPhone = data.getString("CONST_PHONE");
    }

    @Override
    public WideNetTradeData clone()
    {
        WideNetTradeData wideNetTradeData = new WideNetTradeData();
        wideNetTradeData.setAcctPasswd(this.getAcctPasswd());
        wideNetTradeData.setContact(this.getContact());
        wideNetTradeData.setContactPhone(this.getContactPhone());
        wideNetTradeData.setDetailAddress(this.getDetailAddress());
        wideNetTradeData.setEndDate(this.getEndDate());
        wideNetTradeData.setInstId(this.getInstId());
        wideNetTradeData.setModifyTag(this.getModifyTag());
        wideNetTradeData.setOldDetailAddress(this.getOldDetailAddress());
        wideNetTradeData.setOldStandAddress(this.getOldStandAddress());
        wideNetTradeData.setOldStandAddressCode(this.getOldStandAddressCode());
        wideNetTradeData.setPhone(this.getPhone());
        wideNetTradeData.setPortType(this.getPortType());
        wideNetTradeData.setRemark(this.getRemark());
        wideNetTradeData.setRsrvDate1(this.getRsrvDate1());
        wideNetTradeData.setRsrvDate2(this.getRsrvDate2());
        wideNetTradeData.setRsrvDate3(this.getRsrvDate3());
        wideNetTradeData.setRsrvNum1(this.getRsrvNum1());
        wideNetTradeData.setRsrvNum2(this.getRsrvNum2());
        wideNetTradeData.setRsrvNum3(this.getRsrvNum3());
        wideNetTradeData.setRsrvNum4(this.getRsrvNum4());
        wideNetTradeData.setRsrvNum5(this.getRsrvNum5());
        wideNetTradeData.setRsrvStr1(this.getRsrvStr1());
        wideNetTradeData.setRsrvStr2(this.getRsrvStr2());
        wideNetTradeData.setRsrvStr3(this.getRsrvStr3());
        wideNetTradeData.setRsrvStr4(this.getRsrvStr4());
        wideNetTradeData.setRsrvStr5(this.getRsrvStr5());
        wideNetTradeData.setRsrvTag1(this.getRsrvTag1());
        wideNetTradeData.setRsrvTag2(this.getRsrvTag2());
        wideNetTradeData.setRsrvTag3(this.getRsrvTag3());
        wideNetTradeData.setSignPath(this.getSignPath());
        wideNetTradeData.setStandAddress(this.getStandAddress());
        wideNetTradeData.setStandAddressCode(this.getStandAddressCode());
        wideNetTradeData.setStartDate(this.getStartDate());
        wideNetTradeData.setSuggestDate(this.getSuggestDate());
        wideNetTradeData.setUserId(this.getUserId());
        wideNetTradeData.setConstructionAddr(this.constructionAddr);
        wideNetTradeData.setConstStaffId(this.constStaffId);
        wideNetTradeData.setConstPhone(this.constPhone);
        return wideNetTradeData;
    }

    public String getAcctPasswd()
    {
        return acctPasswd;
    }

    public String getContact()
    {
        return contact;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public String getDetailAddress()
    {
        return detailAddress;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getOldDetailAddress()
    {
        return oldDetailAddress;
    }

    public String getOldStandAddress()
    {
        return oldStandAddress;
    }

    public String getOldStandAddressCode()
    {
        return oldStandAddressCode;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getPortType()
    {
        return portType;
    }

    public String getRemark()
    {
        return remark;
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

    public String getStartDate()
    {
        return startDate;
    }

    public String getSuggestDate()
    {
        return suggestDate;
    }

    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_WIDENET";
    }

    public String getUserId()
    {
        return userId;
    }

    public void setAcctPasswd(String acctPasswd)
    {
        this.acctPasswd = acctPasswd;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public void setDetailAddress(String detailAddress)
    {
        this.detailAddress = detailAddress;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setOldDetailAddress(String oldDetailAddress)
    {
        this.oldDetailAddress = oldDetailAddress;
    }

    public void setOldStandAddress(String oldStandAddress)
    {
        this.oldStandAddress = oldStandAddress;
    }

    public void setOldStandAddressCode(String oldStandAddressCode)
    {
        this.oldStandAddressCode = oldStandAddressCode;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setPortType(String portType)
    {
        this.portType = portType;
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

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setSuggestDate(String suggestDate)
    {
        this.suggestDate = suggestDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getConstructionAddr()
    {
        return constructionAddr;
    }

    public void setConstructionAddr(String constructionAddr)
    {
        this.constructionAddr = constructionAddr;
    }

    public String getConstStaffId()
    {
        return constStaffId;
    }

    public void setConstStaffId(String constStaffId)
    {
        this.constStaffId = constStaffId;
    }

    public String getConstPhone()
    {
        return constPhone;
    }

    public void setConstPhone(String constPhone)
    {
        this.constPhone = constPhone;
    }

    @Override
    public IData toData()
    {
        IData data = new DataMap();

        data.put("ACCT_PASSWD", this.acctPasswd);
        data.put("CONTACT", this.contact);
        data.put("CONTACT_PHONE", this.contactPhone);
        data.put("DETAIL_ADDRESS", this.detailAddress);
        data.put("END_DATE", this.endDate);
        data.put("INST_ID", this.instId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("OLD_DETAIL_ADDRESS", this.oldDetailAddress);
        data.put("OLD_STAND_ADDRESS", this.oldStandAddress);
        data.put("OLD_STAND_ADDRESS_CODE", this.oldStandAddressCode);
        data.put("PHONE", this.phone);
        data.put("PORT_TYPE", this.portType);
        data.put("REMARK", this.remark);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
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
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("SIGN_PATH", this.signPath);
        data.put("STAND_ADDRESS", this.standAddress);
        data.put("STAND_ADDRESS_CODE", this.standAddressCode);
        data.put("START_DATE", this.startDate);
        data.put("SUGGEST_DATE", this.suggestDate);
        data.put("USER_ID", this.userId);
        data.put("CONSTRUCTION_ADDR", this.constructionAddr);
        data.put("CONST_STAFF_ID", this.constStaffId);
        data.put("CONST_PHONE", this.constPhone);

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
