
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class CNoteInfoTradeData extends BaseTradeData
{
    private String noteType;

    private String noticeContent;

    private String receiptInfo1;

    private String receiptInfo2;

    private String receiptInfo3;

    private String receiptInfo4;

    private String receiptInfo5;

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

    public CNoteInfoTradeData()
    {

    }

    public CNoteInfoTradeData(IData data)
    {
        this.noteType = data.getString("NOTE_TYPE");
        this.noticeContent = data.getString("NOTICE_CONTENT");
        this.receiptInfo1 = data.getString("RECEIPT_INFO1");
        this.receiptInfo2 = data.getString("RECEIPT_INFO2");
        this.receiptInfo3 = data.getString("RECEIPT_INFO3");
        this.receiptInfo4 = data.getString("RECEIPT_INFO4");
        this.receiptInfo5 = data.getString("RECEIPT_INFO5");
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
    }

    public CNoteInfoTradeData clone()
    {
        CNoteInfoTradeData cNoteInfoTradeData = new CNoteInfoTradeData();
        cNoteInfoTradeData.setNoteType(this.getNoteType());
        cNoteInfoTradeData.setNoticeContent(this.getNoticeContent());
        cNoteInfoTradeData.setReceiptInfo1(this.getReceiptInfo1());
        cNoteInfoTradeData.setReceiptInfo2(this.getReceiptInfo2());
        cNoteInfoTradeData.setReceiptInfo3(this.getReceiptInfo3());
        cNoteInfoTradeData.setReceiptInfo4(this.getReceiptInfo4());
        cNoteInfoTradeData.setReceiptInfo5(this.getReceiptInfo5());
        cNoteInfoTradeData.setRemark(this.getRemark());
        cNoteInfoTradeData.setRsrvDate1(this.getRsrvDate1());
        cNoteInfoTradeData.setRsrvDate2(this.getRsrvDate2());
        cNoteInfoTradeData.setRsrvDate3(this.getRsrvDate3());
        cNoteInfoTradeData.setRsrvNum1(this.getRsrvNum1());
        cNoteInfoTradeData.setRsrvNum2(this.getRsrvNum2());
        cNoteInfoTradeData.setRsrvNum3(this.getRsrvNum3());
        cNoteInfoTradeData.setRsrvNum4(this.getRsrvNum4());
        cNoteInfoTradeData.setRsrvNum5(this.getRsrvNum5());
        cNoteInfoTradeData.setRsrvStr1(this.getRsrvStr1());
        cNoteInfoTradeData.setRsrvStr2(this.getRsrvStr2());
        cNoteInfoTradeData.setRsrvStr3(this.getRsrvStr3());
        cNoteInfoTradeData.setRsrvStr4(this.getRsrvStr4());
        cNoteInfoTradeData.setRsrvStr5(this.getRsrvStr5());
        cNoteInfoTradeData.setRsrvTag1(this.getRsrvTag1());
        cNoteInfoTradeData.setRsrvTag2(this.getRsrvTag2());
        cNoteInfoTradeData.setRsrvTag3(this.getRsrvTag3());

        return cNoteInfoTradeData;
    }

    public String getNoteType()
    {
        return noteType;
    }

    public String getNoticeContent()
    {
        return noticeContent;
    }

    public String getReceiptInfo1()
    {
        return receiptInfo1;
    }

    public String getReceiptInfo2()
    {
        return receiptInfo2;
    }

    public String getReceiptInfo3()
    {
        return receiptInfo3;
    }

    public String getReceiptInfo4()
    {
        return receiptInfo4;
    }

    public String getReceiptInfo5()
    {
        return receiptInfo5;
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

    public String getTableName()
    {
        return "TF_B_TRADE_CNOTE_INFO";
    }

    public void setNoteType(String noteType)
    {
        this.noteType = noteType;
    }

    public void setNoticeContent(String noticeContent)
    {
        this.noticeContent = noticeContent;
    }

    public void setReceiptInfo1(String receiptInfo1)
    {
        this.receiptInfo1 = receiptInfo1;
    }

    public void setReceiptInfo2(String receiptInfo2)
    {
        this.receiptInfo2 = receiptInfo2;
    }

    public void setReceiptInfo3(String receiptInfo3)
    {
        this.receiptInfo3 = receiptInfo3;
    }

    public void setReceiptInfo4(String receiptInfo4)
    {
        this.receiptInfo4 = receiptInfo4;
    }

    public void setReceiptInfo5(String receiptInfo5)
    {
        this.receiptInfo5 = receiptInfo5;
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

    public IData toData()
    {
        IData data = new DataMap();

        data.put("NOTE_TYPE", this.noteType);
        data.put("NOTICE_CONTENT", this.noticeContent);
        data.put("RECEIPT_INFO1", this.receiptInfo1);
        data.put("RECEIPT_INFO2", this.receiptInfo2);
        data.put("RECEIPT_INFO3", this.receiptInfo3);
        data.put("RECEIPT_INFO4", this.receiptInfo4);
        data.put("RECEIPT_INFO5", this.receiptInfo5);
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

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
