
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.IAcctDayChangeData;

/**
 * @author Administrator
 */
public class AttrTradeData extends BaseTradeData implements IAcctDayChangeData
{
    private String attrCode;

    private String attrValue;

    private String endDate;

    private String instId;

    private String instType;

    private String modifyTag;

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

    private String startDate;

    private String userId;

    private String relaInstId;

    private String elementId;

    public AttrTradeData()
    {

    }

    public AttrTradeData(IData data)
    {
        this.attrCode = data.getString("ATTR_CODE");
        this.attrValue = data.getString("ATTR_VALUE");
        this.endDate = data.getString("END_DATE");
        this.instId = data.getString("INST_ID");
        this.instType = data.getString("INST_TYPE");
        this.modifyTag = data.getString("MODIFY_TAG");
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
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.relaInstId = data.getString("RELA_INST_ID");
        this.elementId = data.getString("ELEMENT_ID");
    }

    @Override
    public AttrTradeData clone()
    {
        AttrTradeData attrTradeData = new AttrTradeData();
        attrTradeData.setAttrCode(this.getAttrCode());
        attrTradeData.setAttrValue(this.getAttrValue());
        attrTradeData.setEndDate(this.getEndDate());
        attrTradeData.setInstId(this.getInstId());
        attrTradeData.setInstType(this.getInstType());
        attrTradeData.setModifyTag(this.getModifyTag());
        attrTradeData.setRemark(this.getRemark());
        attrTradeData.setRsrvDate1(this.getRsrvDate1());
        attrTradeData.setRsrvDate2(this.getRsrvDate2());
        attrTradeData.setRsrvDate3(this.getRsrvDate3());
        attrTradeData.setRsrvNum1(this.getRsrvNum1());
        attrTradeData.setRsrvNum2(this.getRsrvNum2());
        attrTradeData.setRsrvNum3(this.getRsrvNum3());
        attrTradeData.setRsrvNum4(this.getRsrvNum4());
        attrTradeData.setRsrvNum5(this.getRsrvNum5());
        attrTradeData.setRsrvStr1(this.getRsrvStr1());
        attrTradeData.setRsrvStr2(this.getRsrvStr2());
        attrTradeData.setRsrvStr3(this.getRsrvStr3());
        attrTradeData.setRsrvStr4(this.getRsrvStr4());
        attrTradeData.setRsrvStr5(this.getRsrvStr5());
        attrTradeData.setRsrvTag1(this.getRsrvTag1());
        attrTradeData.setRsrvTag2(this.getRsrvTag2());
        attrTradeData.setRsrvTag3(this.getRsrvTag3());
        attrTradeData.setStartDate(this.getStartDate());
        attrTradeData.setUserId(this.getUserId());
        attrTradeData.setRelaInstId(this.getRelaInstId());
        attrTradeData.setElementId(this.getElementId());
        return attrTradeData;
    }

    public String getAttrCode()
    {
        return attrCode;
    }

    public String getAttrValue()
    {
        return attrValue;
    }

    public String getElementId()
    {
        return elementId;
    }

    @Override
    public String getEndDate()
    {
        return endDate;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getInstType()
    {
        return instType;
    }

    @Override
    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getRelaInstId()
    {
        return relaInstId;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvDate1()
    {
        return rsrvDate1;
    }

    @Override
    public String getRsrvDate2()
    {
        return rsrvDate2;
    }

    @Override
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

    @Override
    public String getStartDate()
    {
        return startDate;
    }

    /*
     * (non-Javadoc)
     * @see com.ailk.personserv.bof.tradedata.BaseTradeData#getTableName()
     */
    @Override
    public String getTableName()
    {
        return "TF_B_TRADE_ATTR";
    }

    public String getUserId()
    {
        return userId;
    }

    public void setAttrCode(String attrCode)
    {
        this.attrCode = attrCode;
    }

    public void setAttrValue(String attrValue)
    {
        this.attrValue = attrValue;
    }

    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    @Override
    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setInstType(String instType)
    {
        this.instType = instType;
    }

    @Override
    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setRelaInstId(String relaInstId)
    {
        this.relaInstId = relaInstId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    @Override
    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    @Override
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

    @Override
    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    /*
     * (non-Javadoc)
     * @see com.ailk.personserv.bof.tradedata.BaseTradeData#toData()
     */
    @Override
    public IData toData()
    {
        IData data = new DataMap();

        data.put("ATTR_CODE", this.attrCode);
        data.put("ATTR_VALUE", this.attrValue);
        data.put("END_DATE", this.endDate);
        data.put("INST_ID", this.instId);
        data.put("INST_TYPE", this.instType);
        data.put("MODIFY_TAG", this.modifyTag);
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
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("RELA_INST_ID", this.relaInstId);
        data.put("ELEMENT_ID", this.elementId);
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
