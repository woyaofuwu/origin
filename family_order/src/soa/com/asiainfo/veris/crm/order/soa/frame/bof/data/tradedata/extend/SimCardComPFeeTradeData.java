
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class SimCardComPFeeTradeData extends BaseTradeData
{
    private String endValue;

    private String fee;

    private String payMoneyCode;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String simcardnum;

    private String simTypeCode;

    private String startValue;

    private String totalFee;

    public SimCardComPFeeTradeData()
    {

    }

    public SimCardComPFeeTradeData(IData data)
    {
        this.endValue = data.getString("END_VALUE");
        this.fee = data.getString("FEE");
        this.payMoneyCode = data.getString("PAY_MONEY_CODE");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.simcardnum = data.getString("SIMCARDNUM");
        this.simTypeCode = data.getString("SIM_TYPE_CODE");
        this.startValue = data.getString("START_VALUE");
        this.totalFee = data.getString("TOTAL_FEE");
    }

    public SimCardComPFeeTradeData clone()
    {
        SimCardComPFeeTradeData simCardComPFeeTradeData = new SimCardComPFeeTradeData();
        simCardComPFeeTradeData.setEndValue(this.getEndValue());
        simCardComPFeeTradeData.setFee(this.getFee());
        simCardComPFeeTradeData.setPayMoneyCode(this.getPayMoneyCode());
        simCardComPFeeTradeData.setRemark(this.getRemark());
        simCardComPFeeTradeData.setRsrvStr1(this.getRsrvStr1());
        simCardComPFeeTradeData.setRsrvStr2(this.getRsrvStr2());
        simCardComPFeeTradeData.setRsrvStr3(this.getRsrvStr3());
        simCardComPFeeTradeData.setSimcardnum(this.getSimcardnum());
        simCardComPFeeTradeData.setSimTypeCode(this.getSimTypeCode());
        simCardComPFeeTradeData.setStartValue(this.getStartValue());
        simCardComPFeeTradeData.setTotalFee(this.getTotalFee());
        return simCardComPFeeTradeData;
    }

    public String getEndValue()
    {
        return endValue;
    }

    public String getFee()
    {
        return fee;
    }

    public String getPayMoneyCode()
    {
        return payMoneyCode;
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

    public String getSimcardnum()
    {
        return simcardnum;
    }

    public String getSimTypeCode()
    {
        return simTypeCode;
    }

    public String getStartValue()
    {
        return startValue;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_SIMCARDCOMPFEE";
    }

    public String getTotalFee()
    {
        return totalFee;
    }

    public void setEndValue(String endValue)
    {
        this.endValue = endValue;
    }

    public void setFee(String fee)
    {
        this.fee = fee;
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

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setSimcardnum(String simcardnum)
    {
        this.simcardnum = simcardnum;
    }

    public void setSimTypeCode(String simTypeCode)
    {
        this.simTypeCode = simTypeCode;
    }

    public void setStartValue(String startValue)
    {
        this.startValue = startValue;
    }

    public void setTotalFee(String totalFee)
    {
        this.totalFee = totalFee;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("END_VALUE", this.endValue);
        data.put("FEE", this.fee);
        data.put("PAY_MONEY_CODE", this.payMoneyCode);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("SIMCARDNUM", this.simcardnum);
        data.put("SIM_TYPE_CODE", this.simTypeCode);
        data.put("START_VALUE", this.startValue);
        data.put("TOTAL_FEE", this.totalFee);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
