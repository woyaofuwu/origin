
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class ShareTradeData extends BaseTradeData
{

    private String shareInstId;

    private String shareId;

    private String userId;

    private String relaInstId;

    private String discntCode;

    private String discntCodeA;

    private String shareType;

    private String endDate;

    private String startDate;

    private String modifyTag;

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

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    public ShareTradeData()
    {

    }

    public ShareTradeData(IData data)
    {
        this.shareInstId = data.getString("SHARE_INST_ID");
        this.shareId = data.getString("SHARE_ID");
        this.userId = data.getString("USER_ID");
        this.relaInstId = data.getString("RELA_INST_ID");
        this.discntCode = data.getString("DISCNT_CODE");
        this.discntCodeA = data.getString("DISCNT_CODE_A");
        this.shareType = data.getString("SHARE_TYPE");
        this.endDate = data.getString("END_DATE");
        this.startDate = data.getString("START_DATE");
        this.modifyTag = data.getString("MODIFY_TAG");
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
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
    }

    public ShareTradeData clone()
    {
        ShareTradeData shareTradeData = new ShareTradeData();
        shareTradeData.setShareInstId(this.getShareInstId());
        shareTradeData.setShareId(this.getShareId());
        shareTradeData.setUserId(this.getUserId());
        shareTradeData.setRelaInstId(this.getRelaInstId());
        shareTradeData.setDiscntCode(this.getDiscntCode());
        shareTradeData.setDiscntCodeA(this.getDiscntCodeA());
        shareTradeData.setShareType(this.getShareType());
        shareTradeData.setEndDate(this.getEndDate());
        shareTradeData.setStartDate(this.getStartDate());
        shareTradeData.setModifyTag(this.getModifyTag());
        shareTradeData.setRemark(this.getRemark());
        shareTradeData.setRsrvNum1(this.getRsrvNum1());
        shareTradeData.setRsrvNum2(this.getRsrvNum2());
        shareTradeData.setRsrvNum3(this.getRsrvNum3());
        shareTradeData.setRsrvNum4(this.getRsrvNum4());
        shareTradeData.setRsrvNum5(this.getRsrvNum5());
        shareTradeData.setRsrvStr1(this.getRsrvStr1());
        shareTradeData.setRsrvStr2(this.getRsrvStr2());
        shareTradeData.setRsrvStr3(this.getRsrvStr3());
        shareTradeData.setRsrvStr4(this.getRsrvStr4());
        shareTradeData.setRsrvStr5(this.getRsrvStr5());
        shareTradeData.setRsrvDate1(this.getRsrvDate1());
        shareTradeData.setRsrvDate2(this.getRsrvDate2());
        shareTradeData.setRsrvDate3(this.getRsrvDate3());
        shareTradeData.setRsrvTag1(this.getRsrvTag1());
        shareTradeData.setRsrvTag2(this.getRsrvTag2());
        shareTradeData.setRsrvTag3(this.getRsrvTag3());

        return shareTradeData;
    }

    public String getDiscntCode()
    {
        return this.discntCode;
    }

    public String getDiscntCodeA()
    {
        return this.discntCodeA;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getRelaInstId()
    {
        return this.relaInstId;
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

    public String getShareId()
    {
        return this.shareId;
    }

    public String getShareInstId()
    {
        return this.shareInstId;
    }

    public String getShareType()
    {
        return this.shareType;
    }

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_SHARE";
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setDiscntCode(String discntCode)
    {
        this.discntCode = discntCode;
    }

    public void setDiscntCodeA(String discntCodeA)
    {
        this.discntCodeA = discntCodeA;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

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

    public void setShareId(String shareId)
    {
        this.shareId = shareId;
    }

    public void setShareInstId(String shareInstId)
    {
        this.shareInstId = shareInstId;
    }

    public void setShareType(String shareType)
    {
        this.shareType = shareType;
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
        data.put("SHARE_INST_ID", this.shareInstId);
        data.put("SHARE_ID", this.shareId);
        data.put("USER_ID", this.userId);
        data.put("RELA_INST_ID", this.relaInstId);
        data.put("DISCNT_CODE", this.discntCode);
        data.put("DISCNT_CODE_A", this.discntCodeA);
        data.put("SHARE_TYPE", this.shareType);
        data.put("END_DATE", this.endDate);
        data.put("START_DATE", this.startDate);
        data.put("MODIFY_TAG", this.modifyTag);
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
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
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
