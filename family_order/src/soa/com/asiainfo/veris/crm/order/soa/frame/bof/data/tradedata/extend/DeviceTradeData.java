
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class DeviceTradeData extends BaseTradeData
{
    private String deviceNoE;

    private String deviceNoS;

    private String deviceNum;

    private String devicePrice;

    private String deviceTypeCode;

    private String feeTypeCode;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr10;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String salePrice;

    public DeviceTradeData()
    {

    }

    public DeviceTradeData(IData data)
    {
        this.deviceNoE = data.getString("DEVICE_NO_E");
        this.deviceNoS = data.getString("DEVICE_NO_S");
        this.deviceNum = data.getString("DEVICE_NUM");
        this.devicePrice = data.getString("DEVICE_PRICE");
        this.deviceTypeCode = data.getString("DEVICE_TYPE_CODE");
        this.feeTypeCode = data.getString("FEE_TYPE_CODE");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.salePrice = data.getString("SALE_PRICE");
    }

    public DeviceTradeData clone()
    {
        DeviceTradeData deviceTradeData = new DeviceTradeData();

        deviceTradeData.setDeviceNoE(this.getDeviceNoE());
        deviceTradeData.setDeviceNoS(this.getDeviceNoS());
        deviceTradeData.setDeviceNum(this.getDeviceNum());
        deviceTradeData.setDevicePrice(this.getDevicePrice());
        deviceTradeData.setDeviceTypeCode(this.getDeviceTypeCode());
        deviceTradeData.setFeeTypeCode(this.getFeeTypeCode());
        deviceTradeData.setRemark(this.getRemark());
        deviceTradeData.setRsrvStr1(this.getRsrvStr1());
        deviceTradeData.setRsrvStr10(this.getRsrvStr10());
        deviceTradeData.setRsrvStr2(this.getRsrvStr2());
        deviceTradeData.setRsrvStr3(this.getRsrvStr3());
        deviceTradeData.setRsrvStr4(this.getRsrvStr4());
        deviceTradeData.setRsrvStr5(this.getRsrvStr5());
        deviceTradeData.setRsrvStr6(this.getRsrvStr6());
        deviceTradeData.setRsrvStr7(this.getRsrvStr7());
        deviceTradeData.setRsrvStr8(this.getRsrvStr8());
        deviceTradeData.setRsrvStr9(this.getRsrvStr9());
        deviceTradeData.setSalePrice(this.getSalePrice());

        return deviceTradeData;
    }

    public String getDeviceNoE()
    {
        return deviceNoE;
    }

    public String getDeviceNoS()
    {
        return deviceNoS;
    }

    public String getDeviceNum()
    {
        return deviceNum;
    }

    public String getDevicePrice()
    {
        return devicePrice;
    }

    public String getDeviceTypeCode()
    {
        return deviceTypeCode;
    }

    public String getFeeTypeCode()
    {
        return feeTypeCode;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return rsrvStr10;
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

    public String getRsrvStr9()
    {
        return rsrvStr9;
    }

    public String getSalePrice()
    {
        return salePrice;
    }

    public String getTableName()
    {
        return "TF_B_TRADEFEE_DEVICE";
    }

    public void setDeviceNoE(String deviceNoE)
    {
        this.deviceNoE = deviceNoE;
    }

    public void setDeviceNoS(String deviceNoS)
    {
        this.deviceNoS = deviceNoS;
    }

    public void setDeviceNum(String deviceNum)
    {
        this.deviceNum = deviceNum;
    }

    public void setDevicePrice(String devicePrice)
    {
        this.devicePrice = devicePrice;
    }

    public void setDeviceTypeCode(String deviceTypeCode)
    {
        this.deviceTypeCode = deviceTypeCode;
    }

    public void setFeeTypeCode(String feeTypeCode)
    {
        this.feeTypeCode = feeTypeCode;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr10(String rsrvStr10)
    {
        this.rsrvStr10 = rsrvStr10;
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

    public void setRsrvStr9(String rsrvStr9)
    {
        this.rsrvStr9 = rsrvStr9;
    }

    public void setSalePrice(String salePrice)
    {
        this.salePrice = salePrice;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("DEVICE_NO_E", this.deviceNoE);
        data.put("DEVICE_NO_S", this.deviceNoS);
        data.put("DEVICE_NUM", this.deviceNum);
        data.put("DEVICE_PRICE", this.devicePrice);
        data.put("DEVICE_TYPE_CODE", this.deviceTypeCode);
        data.put("FEE_TYPE_CODE", this.feeTypeCode);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("SALE_PRICE", this.salePrice);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
