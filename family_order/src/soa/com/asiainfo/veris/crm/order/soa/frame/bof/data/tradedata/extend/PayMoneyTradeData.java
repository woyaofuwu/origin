
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class PayMoneyTradeData extends BaseTradeData
{

    private String payMoneyCode;

    private String money;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String rsrvStr10;

    private String orderId;

    public PayMoneyTradeData()
    {

    }

    public PayMoneyTradeData(IData data)
    {
        this.payMoneyCode = data.getString("PAY_MONEY_CODE");
        this.money = data.getString("MONEY");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.orderId = data.getString("ORDER_ID");
    }

    public PayMoneyTradeData clone()
    {
        PayMoneyTradeData payMoneyTradeData = new PayMoneyTradeData();
        payMoneyTradeData.setPayMoneyCode(this.getPayMoneyCode());
        payMoneyTradeData.setMoney(this.getMoney());
        payMoneyTradeData.setRemark(this.getRemark());
        payMoneyTradeData.setRsrvStr1(this.getRsrvStr1());
        payMoneyTradeData.setRsrvStr2(this.getRsrvStr2());
        payMoneyTradeData.setRsrvStr3(this.getRsrvStr3());
        payMoneyTradeData.setRsrvStr4(this.getRsrvStr4());
        payMoneyTradeData.setRsrvStr5(this.getRsrvStr5());
        payMoneyTradeData.setRsrvStr6(this.getRsrvStr6());
        payMoneyTradeData.setRsrvStr7(this.getRsrvStr7());
        payMoneyTradeData.setRsrvStr8(this.getRsrvStr8());
        payMoneyTradeData.setRsrvStr9(this.getRsrvStr9());
        payMoneyTradeData.setRsrvStr10(this.getRsrvStr10());
        payMoneyTradeData.setOrderId(this.getOrderId());
        return payMoneyTradeData;
    }

    public String getMoney()
    {
        return this.money;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getPayMoneyCode()
    {
        return this.payMoneyCode;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public String getRsrvStr1()
    {
        return this.rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return this.rsrvStr10;
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

    public String getRsrvStr6()
    {
        return this.rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return this.rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return this.rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return this.rsrvStr9;
    }

    public String getTableName()
    {
        return "TF_B_TRADEFEE_PAYMONEY";
    }

    public void setMoney(String money)
    {
        this.money = money;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setPayMoneyCode(String payMoneyCode)
    {
        this.payMoneyCode = payMoneyCode;
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

    public IData toData()
    {
        IData data = new DataMap();
        data.put("PAY_MONEY_CODE", this.payMoneyCode);
        data.put("MONEY", this.money);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("ORDER_ID", this.orderId);
        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
