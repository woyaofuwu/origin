
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class ShareRelaTradeData extends BaseTradeData
{

    private String instId;

    private String shareId;

    private String serialNumber;

    private String userIdB;

    private String eparchyCode;

    private String roleCode;

    private String startDate;

    private String endDate;

    private String modifyTag;

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

    public ShareRelaTradeData()
    {

    }

    public ShareRelaTradeData(IData data)
    {
        this.instId = data.getString("INST_ID");
        this.shareId = data.getString("SHARE_ID");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.userIdB = data.getString("USER_ID_B");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.roleCode = data.getString("ROLE_CODE");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.modifyTag = data.getString("MODIFY_TAG");
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

    public ShareRelaTradeData clone()
    {
        ShareRelaTradeData shareRelaTradeData = new ShareRelaTradeData();
        shareRelaTradeData.setInstId(this.getInstId());
        shareRelaTradeData.setShareId(this.getShareId());
        shareRelaTradeData.setSerialNumber(this.getSerialNumber());
        shareRelaTradeData.setUserIdB(this.getUserIdB());
        shareRelaTradeData.setEparchyCode(this.getEparchyCode());
        shareRelaTradeData.setRoleCode(this.getRoleCode());
        shareRelaTradeData.setStartDate(this.getStartDate());
        shareRelaTradeData.setEndDate(this.getEndDate());
        shareRelaTradeData.setModifyTag(this.getModifyTag());
        shareRelaTradeData.setRemark(this.getRemark());
        shareRelaTradeData.setRsrvNum1(this.getRsrvNum1());
        shareRelaTradeData.setRsrvNum2(this.getRsrvNum2());
        shareRelaTradeData.setRsrvNum3(this.getRsrvNum3());
        shareRelaTradeData.setRsrvStr1(this.getRsrvStr1());
        shareRelaTradeData.setRsrvStr2(this.getRsrvStr2());
        shareRelaTradeData.setRsrvStr3(this.getRsrvStr3());
        shareRelaTradeData.setRsrvDate1(this.getRsrvDate1());
        shareRelaTradeData.setRsrvDate2(this.getRsrvDate2());
        shareRelaTradeData.setRsrvTag1(this.getRsrvTag1());
        shareRelaTradeData.setRsrvTag2(this.getRsrvTag2());

        return shareRelaTradeData;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getEparchyCode()
    {
        return this.eparchyCode;
    }

    public String getInstId()
    {
        return this.instId;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public String getRoleCode()
    {
        return this.roleCode;
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

    public String getSerialNumber()
    {
        return this.serialNumber;
    }

    public String getShareId()
    {
        return this.shareId;
    }

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_SHARE_RELA";
    }

    public String getUserIdB()
    {
        return this.userIdB;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRoleCode(String roleCode)
    {
        this.roleCode = roleCode;
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

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setShareId(String shareId)
    {
        this.shareId = shareId;
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
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("USER_ID_B", this.userIdB);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("ROLE_CODE", this.roleCode);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("MODIFY_TAG", this.modifyTag);
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
