
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class ShareMebLimitaTradeData extends BaseTradeData
{

    private String instId;

    private String shareId;

    private String shareInstId;

    private String userIdB;

    private String shareTypeCode;

    private String shareLimit;

    private String startDate;

    private String endDate;

    private String remark;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvTag1;

    private String rsrvTag2;

    public ShareMebLimitaTradeData()
    {

    }

    public ShareMebLimitaTradeData(IData data)
    {
        this.instId = data.getString("INST_ID");
        this.shareId = data.getString("SHARE_ID");
        this.shareInstId = data.getString("SHARE_INST_ID");
        this.userIdB = data.getString("USER_ID_B");
        this.shareTypeCode = data.getString("SHARE_TYPE_CODE");
        this.shareLimit = data.getString("SHARE_LIMIT");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.remark = data.getString("REMARK");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
    }

    public ShareMebLimitaTradeData clone()
    {
        ShareMebLimitaTradeData shareMebLimitaTradeData = new ShareMebLimitaTradeData();
        shareMebLimitaTradeData.setInstId(this.getInstId());
        shareMebLimitaTradeData.setShareId(this.getShareId());
        shareMebLimitaTradeData.setShareInstId(this.getShareInstId());
        shareMebLimitaTradeData.setUserIdB(this.getUserIdB());
        shareMebLimitaTradeData.setShareTypeCode(this.getShareTypeCode());
        shareMebLimitaTradeData.setShareLimit(this.getShareLimit());
        shareMebLimitaTradeData.setStartDate(this.getStartDate());
        shareMebLimitaTradeData.setEndDate(this.getEndDate());
        shareMebLimitaTradeData.setRemark(this.getRemark());
        shareMebLimitaTradeData.setRsrvNum1(this.getRsrvNum1());
        shareMebLimitaTradeData.setRsrvNum2(this.getRsrvNum2());
        shareMebLimitaTradeData.setRsrvNum3(this.getRsrvNum3());
        shareMebLimitaTradeData.setRsrvStr1(this.getRsrvStr1());
        shareMebLimitaTradeData.setRsrvStr2(this.getRsrvStr2());
        shareMebLimitaTradeData.setRsrvStr3(this.getRsrvStr3());
        shareMebLimitaTradeData.setRsrvDate1(this.getRsrvDate1());
        shareMebLimitaTradeData.setRsrvDate2(this.getRsrvDate2());
        shareMebLimitaTradeData.setRsrvTag1(this.getRsrvTag1());
        shareMebLimitaTradeData.setRsrvTag2(this.getRsrvTag2());

        return shareMebLimitaTradeData;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getInstId()
    {
        return this.instId;
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

    public String getRsrvTag1()
    {
        return this.rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return this.rsrvTag2;
    }

    public String getShareId()
    {
        return this.shareId;
    }

    public String getShareInstId()
    {
        return this.shareInstId;
    }

    public String getShareLimit()
    {
        return this.shareLimit;
    }

    public String getShareTypeCode()
    {
        return this.shareTypeCode;
    }

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_SHARE_MEBLIMIT";
    }

    public String getUserIdB()
    {
        return this.userIdB;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
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

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public void setShareId(String shareId)
    {
        this.shareId = shareId;
    }

    public void setShareInstId(String shareInstId)
    {
        this.shareInstId = shareInstId;
    }

    public void setShareLimit(String shareLimit)
    {
        this.shareLimit = shareLimit;
    }

    public void setShareTypeCode(String shareTypeCode)
    {
        this.shareTypeCode = shareTypeCode;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserIdB(String userIdB)
    {
        this.userIdB = userIdB;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("INST_ID", this.instId);
        data.put("SHARE_ID", this.shareId);
        data.put("SHARE_INST_ID", this.shareInstId);
        data.put("USER_ID_B", this.userIdB);
        data.put("SHARE_TYPE_CODE", this.shareTypeCode);
        data.put("SHARE_LIMIT", this.shareLimit);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("REMARK", this.remark);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
