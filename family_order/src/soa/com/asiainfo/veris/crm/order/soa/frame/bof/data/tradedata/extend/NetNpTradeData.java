
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class NetNpTradeData extends BaseTradeData
{
    private String applyDate;

    private String cancelTag;

    private String inEparchyCode;

    private String inUserId;

    private String modifyTag;

    private String netnpDepartId;

    private String netnpStaffId;

    private String outEparchyCode;

    private String outUserId;

    private String portInDate;

    private String portOutDate;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String serialNumber;

    private String state;

    private String tradeTypeCode;

    private String inCityCode;

    private String outCityCode;

    private String instId;

    public NetNpTradeData()
    {

    }

    public NetNpTradeData(IData data)
    {
        this.applyDate = data.getString("APPLY_DATE");
        this.cancelTag = data.getString("CANCEL_TAG");
        this.inEparchyCode = data.getString("IN_EPARCHY_CODE");
        this.inUserId = data.getString("IN_USER_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.netnpDepartId = data.getString("NETNP_DEPART_ID");
        this.netnpStaffId = data.getString("NETNP_STAFF_ID");
        this.outEparchyCode = data.getString("OUT_EPARCHY_CODE");
        this.outUserId = data.getString("OUT_USER_ID");
        this.portInDate = data.getString("PORT_IN_DATE");
        this.portOutDate = data.getString("PORT_OUT_DATE");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.state = data.getString("STATE");
        this.tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        this.inCityCode = data.getString("IN_CITY_CODE");
        this.outCityCode = data.getString("OUT_CITY_CODE");
        this.instId = data.getString("INST_ID");
    }

    public NetNpTradeData clone()
    {
        NetNpTradeData NetNpTradeData = new NetNpTradeData();
        NetNpTradeData.setApplyDate(this.getApplyDate());
        NetNpTradeData.setCancelTag(this.getCancelTag());
        NetNpTradeData.setInEparchyCode(this.getInEparchyCode());
        NetNpTradeData.setInUserId(this.getInUserId());
        NetNpTradeData.setModifyTag(this.getModifyTag());
        NetNpTradeData.setNetnpDepartId(this.getNetnpDepartId());
        NetNpTradeData.setNetnpStaffId(this.getNetnpStaffId());
        NetNpTradeData.setOutEparchyCode(this.getOutEparchyCode());
        NetNpTradeData.setOutUserId(this.getOutUserId());
        NetNpTradeData.setPortInDate(this.getPortInDate());
        NetNpTradeData.setPortOutDate(this.getPortOutDate());
        NetNpTradeData.setRemark(this.getRemark());
        NetNpTradeData.setRsrvStr1(this.getRsrvStr1());
        NetNpTradeData.setRsrvStr2(this.getRsrvStr2());
        NetNpTradeData.setRsrvStr3(this.getRsrvStr3());
        NetNpTradeData.setRsrvStr4(this.getRsrvStr4());
        NetNpTradeData.setRsrvStr5(this.getRsrvStr5());
        NetNpTradeData.setSerialNumber(this.getSerialNumber());
        NetNpTradeData.setState(this.getState());
        NetNpTradeData.setTradeTypeCode(this.getTradeTypeCode());
        NetNpTradeData.setInCityCode(this.getInCityCode());
        NetNpTradeData.setOutCityCode(this.getOutCityCode());
        NetNpTradeData.setInstId(this.getInstId());
        return NetNpTradeData;
    }

    public String getApplyDate()
    {
        return applyDate;
    }

    public String getCancelTag()
    {
        return cancelTag;
    }

    public String getInCityCode()
    {
        return inCityCode;
    }

    public String getInEparchyCode()
    {
        return inEparchyCode;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getInUserId()
    {
        return inUserId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getNetnpDepartId()
    {
        return netnpDepartId;
    }

    public String getNetnpStaffId()
    {
        return netnpStaffId;
    }

    public String getOutCityCode()
    {
        return outCityCode;
    }

    public String getOutEparchyCode()
    {
        return outEparchyCode;
    }

    public String getOutUserId()
    {
        return outUserId;
    }

    public String getPortInDate()
    {
        return portInDate;
    }

    public String getPortOutDate()
    {
        return portOutDate;
    }

    public String getRemark()
    {
        return remark;
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

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getState()
    {
        return state;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_NETNP";
    }

    public String getTradeTypeCode()
    {
        return tradeTypeCode;
    }

    public void setApplyDate(String applyDate)
    {
        this.applyDate = applyDate;
    }

    public void setCancelTag(String cancelTag)
    {
        this.cancelTag = cancelTag;
    }

    public void setInCityCode(String inCityCode)
    {
        this.inCityCode = inCityCode;
    }

    public void setInEparchyCode(String inEparchyCode)
    {
        this.inEparchyCode = inEparchyCode;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setInUserId(String inUserId)
    {
        this.inUserId = inUserId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setNetnpDepartId(String netnpDepartId)
    {
        this.netnpDepartId = netnpDepartId;
    }

    public void setNetnpStaffId(String netnpStaffId)
    {
        this.netnpStaffId = netnpStaffId;
    }

    public void setOutCityCode(String outCityCode)
    {
        this.outCityCode = outCityCode;
    }

    public void setOutEparchyCode(String outEparchyCode)
    {
        this.outEparchyCode = outEparchyCode;
    }

    public void setOutUserId(String outUserId)
    {
        this.outUserId = outUserId;
    }

    public void setPortInDate(String portInDate)
    {
        this.portInDate = portInDate;
    }

    public void setPortOutDate(String portOutDate)
    {
        this.portOutDate = portOutDate;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
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

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public void setTradeTypeCode(String tradeTypeCode)
    {
        this.tradeTypeCode = tradeTypeCode;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("APPLY_DATE", this.applyDate);
        data.put("CANCEL_TAG", this.cancelTag);
        data.put("IN_EPARCHY_CODE", this.inEparchyCode);
        data.put("IN_USER_ID", this.inUserId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("NETNP_DEPART_ID", this.netnpDepartId);
        data.put("NETNP_STAFF_ID", this.netnpStaffId);
        data.put("OUT_EPARCHY_CODE", this.outEparchyCode);
        data.put("OUT_USER_ID", this.outUserId);
        data.put("PORT_IN_DATE", this.portInDate);
        data.put("PORT_OUT_DATE", this.portOutDate);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("STATE", this.state);
        data.put("TRADE_TYPE_CODE", this.tradeTypeCode);
        data.put("IN_CITY_CODE", this.inCityCode);
        data.put("OUT_CITY_CODE", this.outCityCode);
        data.put("INST_ID", this.instId);
        return data;
    }
}
