
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class AddrTradeData extends BaseTradeData
{
    private String instId;

    private String userId;

    private String addrId;

    private String addrName;

    private String addrDesc;

    private String mofficeId;

    private String modifyTag;

    private String startDate;

    private String endDate;

    private String remark;

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

    public AddrTradeData()
    {
    }

    public AddrTradeData(IData data)
    {
        this.instId = data.getString("INST_ID");
        this.userId = data.getString("USER_ID");
        this.addrId = data.getString("ADDR_ID");
        this.addrName = data.getString("ADDR_NAME");
        this.addrDesc = data.getString("ADDR_DESC");
        this.mofficeId = data.getString("MOFFICE_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.remark = data.getString("REMARK");
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
    }

    public AddrTradeData clone()
    {
        AddrTradeData addrTradeData = new AddrTradeData();
        addrTradeData.setInstId(this.getInstId());
        addrTradeData.setUserId(this.getUserId());
        addrTradeData.setAddrId(this.getAddrId());
        addrTradeData.setAddrName(this.getAddrName());
        addrTradeData.setAddrDesc(this.getAddrDesc());
        addrTradeData.setMofficeId(this.getMofficeId());
        addrTradeData.setModifyTag(this.getModifyTag());
        addrTradeData.setStartDate(this.getStartDate());
        addrTradeData.setEndDate(this.getEndDate());
        addrTradeData.setRemark(this.getRemark());
        addrTradeData.setRsrvStr1(this.getRsrvStr1());
        addrTradeData.setRsrvStr2(this.getRsrvStr2());
        addrTradeData.setRsrvStr3(this.getRsrvStr3());
        addrTradeData.setRsrvStr4(this.getRsrvStr4());
        addrTradeData.setRsrvStr5(this.getRsrvStr5());
        addrTradeData.setRsrvTag1(this.getRsrvTag1());
        addrTradeData.setRsrvTag2(this.getRsrvTag2());
        addrTradeData.setRsrvTag3(this.getRsrvTag3());
        addrTradeData.setRsrvDate1(this.getRsrvDate1());
        addrTradeData.setRsrvDate2(this.getRsrvDate2());
        addrTradeData.setRsrvDate3(this.getRsrvDate3());

        return addrTradeData;
    }

    public String getAddrDesc()
    {
        return this.addrDesc;
    }

    public String getAddrId()
    {
        return this.addrId;
    }

    public String getAddrName()
    {
        return this.addrName;
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

    public String getMofficeId()
    {
        return this.mofficeId;
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

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_ADDR";
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setAddrDesc(String addrDesc)
    {
        this.addrDesc = addrDesc;
    }

    public void setAddrId(String addrId)
    {
        this.addrId = addrId;
    }

    public void setAddrName(String addrName)
    {
        this.addrName = addrName;
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

    public void setMofficeId(String mofficeId)
    {
        this.mofficeId = mofficeId;
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
        data.put("INST_ID", this.instId);
        data.put("USER_ID", this.userId);
        data.put("ADDR_ID", this.addrId);
        data.put("ADDR_NAME", this.addrName);
        data.put("ADDR_DESC", this.addrDesc);
        data.put("MOFFICE_ID", this.mofficeId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("REMARK", this.remark);
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

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
