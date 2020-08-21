
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class PreTradeData extends BaseTradeData
{

    private String tradeId;

    private String month;

    private String tradeTypeCode;

    private String preAcceptTime;

    private String preInvalidTime;

    private String serialNumber;

    private String status;

    private String inModeCode;

    private String updateTime;

    private String updateStaffId;

    private String updateDepartId;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    public PreTradeData()
    {

    }

    public PreTradeData(IData data)
    {
        this.tradeId = data.getString("TRADE_ID");
        this.month = data.getString("MONTH");
        this.tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        this.preAcceptTime = data.getString("PRE_ACCEPT_TIME");
        this.preInvalidTime = data.getString("PRE_INVALID_TIME");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.status = data.getString("STATUS");
        this.inModeCode = data.getString("IN_MODE_CODE");
        this.updateTime = data.getString("UPDATE_TIME");
        this.updateStaffId = data.getString("UPDATE_STAFF_ID");
        this.updateDepartId = data.getString("UPDATE_DEPART_ID");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
    }

    public PreTradeData clone()
    {
        PreTradeData preTradeData = new PreTradeData();
        preTradeData.setTradeId(this.getTradeId());
        ;
        preTradeData.setMonth(this.getMonth());
        preTradeData.setTradeTypeCode(this.getTradeTypeCode());
        preTradeData.setPreAcceptTime(this.getPreAcceptTime());
        preTradeData.setPreInvalidTime(this.getPreInvalidTime());
        preTradeData.setSerialNumber(this.getSerialNumber());
        preTradeData.setStatus(this.getStatus());
        preTradeData.setInModeCode(this.getInModeCode());
        preTradeData.setUpdateTime(this.getUpdateTime());
        preTradeData.setUpdateStaffId(this.getUpdateStaffId());
        preTradeData.setUpdateDepartId(this.getUpdateDepartId());
        preTradeData.setRemark(this.getRemark());
        preTradeData.setRsrvStr1(this.getRsrvStr1());
        preTradeData.setRsrvStr2(this.getRsrvStr2());
        preTradeData.setRsrvStr3(this.getRsrvStr3());
        preTradeData.setRsrvStr4(this.getRsrvStr4());
        preTradeData.setRsrvStr5(this.getRsrvStr5());
        preTradeData.setRsrvStr6(this.getRsrvStr6());
        preTradeData.setRsrvStr7(this.getRsrvStr7());
        preTradeData.setRsrvStr8(this.getRsrvStr8());
        preTradeData.setRsrvDate1(this.getRsrvDate1());
        preTradeData.setRsrvDate2(this.getRsrvDate2());
        preTradeData.setRsrvDate3(this.getRsrvDate3());

        return preTradeData;
    }

    public String getInModeCode()
    {
        return inModeCode;
    }

    public String getMonth()
    {
        return month;
    }

    public String getPreAcceptTime()
    {
        return preAcceptTime;
    }

    public String getPreInvalidTime()
    {
        return preInvalidTime;
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

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String getTableName()
    {
        // TODO Auto-generated method stub
        return "TF_B_PRE_TRADE";
    }

    public String getTradeId()
    {
        return tradeId;
    }

    public String getTradeTypeCode()
    {
        return tradeTypeCode;
    }

    public String getUpdateDepartId()
    {
        return updateDepartId;
    }

    public String getUpdateStaffId()
    {
        return updateStaffId;
    }

    public String getUpdateTime()
    {
        return updateTime;
    }

    public void setInModeCode(String inModeCode)
    {
        this.inModeCode = inModeCode;
    }

    public void setMonth(String month)
    {
        this.month = month;
    }

    public void setPreAcceptTime(String preAcceptTime)
    {
        this.preAcceptTime = preAcceptTime;
    }

    public void setPreInvalidTime(String preInvalidTime)
    {
        this.preInvalidTime = preInvalidTime;
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

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setTradeId(String tradeId)
    {
        this.tradeId = tradeId;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setUpdateDepartId(String updateDepartId)
    {
        this.updateDepartId = updateDepartId;
    }

    public void setUpdateStaffId(String updateStaffId)
    {
        this.updateStaffId = updateStaffId;
    }

    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
    }

    @Override
    public IData toData()
    {
        // TODO Auto-generated method stub
        IData data = new DataMap();

        data.put("TRADE_ID", this.tradeId);
        data.put("MONTH", this.month);
        data.put("TRADE_TYPE_CODE", this.tradeTypeCode);
        data.put("PRE_ACCEPT_TIME", this.preAcceptTime);
        data.put("PRE_INVALID_TIME", this.preInvalidTime);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("STATUS", this.status);
        data.put("IN_MODE_CODE", this.inModeCode);
        data.put("UPDATE_TIME", this.updateTime);
        data.put("UPDATE_STAFF_ID", this.updateStaffId);
        data.put("UPDATE_DEPART_ID", this.updateDepartId);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
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
