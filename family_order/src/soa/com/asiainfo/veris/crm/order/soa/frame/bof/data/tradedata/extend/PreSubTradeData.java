
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class PreSubTradeData extends BaseTradeData
{

    private String tradeId;

    private String month;

    private String attrValue;

    private String attrCode;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    public PreSubTradeData()
    {

    }

    public PreSubTradeData(IData data)
    {
        this.tradeId = data.getString("TRADE_ID");
        this.month = data.getString("MONTH");
        this.attrCode = data.getString("ATTR_CODE");
        this.attrValue = data.getString("ATTR_VALUE");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
    }

    public PreSubTradeData clone()
    {
        PreSubTradeData preSubTradeData = new PreSubTradeData();
        preSubTradeData.setTradeId(this.getTradeId());
        ;
        preSubTradeData.setMonth(this.getMonth());
        preSubTradeData.setAttrCode(this.getAttrCode());
        preSubTradeData.setAttrValue(this.getAttrValue());
        preSubTradeData.setRsrvStr1(this.getRsrvStr1());
        preSubTradeData.setRsrvStr2(this.getRsrvStr2());
        preSubTradeData.setRsrvStr3(this.getRsrvStr3());
        preSubTradeData.setRsrvDate1(this.getRsrvDate1());
        preSubTradeData.setRsrvDate2(this.getRsrvDate2());
        preSubTradeData.setRsrvDate3(this.getRsrvDate3());

        return preSubTradeData;
    }

    public String getAttrCode()
    {
        return attrCode;
    }

    public String getAttrValue()
    {
        return attrValue;
    }

    public String getMonth()
    {
        return month;
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

    @Override
    public String getTableName()
    {
        // TODO Auto-generated method stub
        return "TF_B_PRE_TRADE_SUB";
    }

    public String getTradeId()
    {
        return tradeId;
    }

    public void setAttrCode(String attrCode)
    {
        this.attrCode = attrCode;
    }

    public void setAttrValue(String attrValue)
    {
        this.attrValue = attrValue;
    }

    public void setMonth(String month)
    {
        this.month = month;
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

    public void setTradeId(String tradeId)
    {
        this.tradeId = tradeId;
    }

    @Override
    public IData toData()
    {
        // TODO Auto-generated method stub
        IData data = new DataMap();

        data.put("TRADE_ID", this.tradeId);
        data.put("MONTH", this.month);
        data.put("ATTR_CODE", this.attrCode);
        data.put("ATTR_VALUE", this.attrValue);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
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
