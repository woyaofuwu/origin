
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class WidenetOtherTradeData extends BaseTradeData
{

    private String modifyTag;

    private String userId;

    private String serialNumber;

    private String noteId;

    private String instId;

    private String startDate;

    private String endDate;

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

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvDate4;

    private String rsrvDate5;

    public WidenetOtherTradeData()
    {

    }

    public WidenetOtherTradeData(IData data)
    {
        this.modifyTag = data.getString("MODIFY_TAG");
        this.userId = data.getString("USER_ID");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.noteId = data.getString("NOTE_ID");
        this.instId = data.getString("INST_ID");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
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
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvDate4 = data.getString("RSRV_DATE4");
        this.rsrvDate5 = data.getString("RSRV_DATE5");
    }

    public WidenetOtherTradeData clone()
    {
        WidenetOtherTradeData widenetOtherTradeData = new WidenetOtherTradeData();
        widenetOtherTradeData.setModifyTag(this.getModifyTag());
        widenetOtherTradeData.setUserId(this.getUserId());
        widenetOtherTradeData.setSerialNumber(this.getSerialNumber());
        widenetOtherTradeData.setNoteId(this.getNoteId());
        widenetOtherTradeData.setInstId(this.getInstId());
        widenetOtherTradeData.setStartDate(this.getStartDate());
        widenetOtherTradeData.setEndDate(this.getEndDate());
        widenetOtherTradeData.setRemark(this.getRemark());
        widenetOtherTradeData.setRsrvNum1(this.getRsrvNum1());
        widenetOtherTradeData.setRsrvNum2(this.getRsrvNum2());
        widenetOtherTradeData.setRsrvNum3(this.getRsrvNum3());
        widenetOtherTradeData.setRsrvNum4(this.getRsrvNum4());
        widenetOtherTradeData.setRsrvNum5(this.getRsrvNum5());
        widenetOtherTradeData.setRsrvStr1(this.getRsrvStr1());
        widenetOtherTradeData.setRsrvStr2(this.getRsrvStr2());
        widenetOtherTradeData.setRsrvStr3(this.getRsrvStr3());
        widenetOtherTradeData.setRsrvStr4(this.getRsrvStr4());
        widenetOtherTradeData.setRsrvStr5(this.getRsrvStr5());
        widenetOtherTradeData.setRsrvTag1(this.getRsrvTag1());
        widenetOtherTradeData.setRsrvTag2(this.getRsrvTag2());
        widenetOtherTradeData.setRsrvTag3(this.getRsrvTag3());
        widenetOtherTradeData.setRsrvDate1(this.getRsrvDate1());
        widenetOtherTradeData.setRsrvDate2(this.getRsrvDate2());
        widenetOtherTradeData.setRsrvDate3(this.getRsrvDate3());
        widenetOtherTradeData.setRsrvDate4(this.getRsrvDate4());
        widenetOtherTradeData.setRsrvDate5(this.getRsrvDate5());

        return widenetOtherTradeData;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getInstId()
    {
        return this.instId;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getNoteId()
    {
        return this.noteId;
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

    public String getRsrvDate4()
    {
        return this.rsrvDate4;
    }

    public String getRsrvDate5()
    {
        return this.rsrvDate5;
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

    public String getSerialNumber()
    {
        return this.serialNumber;
    }

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_WIDENET_OTHER";
    }

    public String getUserId()
    {
        return this.userId;
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

    public void setNoteId(String noteId)
    {
        this.noteId = noteId;
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

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("USER_ID", this.userId);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("NOTE_ID", this.noteId);
        data.put("INST_ID", this.instId);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
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
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_DATE4", this.rsrvDate4);
        data.put("RSRV_DATE5", this.rsrvDate5);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
