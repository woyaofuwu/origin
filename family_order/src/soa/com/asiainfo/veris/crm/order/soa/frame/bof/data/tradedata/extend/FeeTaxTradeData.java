
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class FeeTaxTradeData extends BaseTradeData
{

    private String userId;

    private String feeMode;

    private String feeTypeCode;

    private String busiFee;

    private String salePrice;

    private String discount;

    private String type;

    private String rate;

    private String fee1;

    private String fee2;

    private String fee3;

    private String goodsName;

    private String count;

    private String unit;

    private String scoreValue;

    private String remark;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvDate1;

    private String rsrvTag1;

    public FeeTaxTradeData()
    {

    }

    public FeeTaxTradeData(IData data)
    {
        this.userId = data.getString("USER_ID");
        this.feeMode = data.getString("FEE_MODE");
        this.feeTypeCode = data.getString("FEE_TYPE_CODE");
        this.busiFee = data.getString("BUSI_FEE");
        this.salePrice = data.getString("SALE_PRICE");
        this.discount = data.getString("DISCOUNT");
        this.type = data.getString("TYPE");
        this.rate = data.getString("RATE");
        this.fee1 = data.getString("FEE1");
        this.fee2 = data.getString("FEE2");
        this.fee3 = data.getString("FEE3");
        this.goodsName = data.getString("GOODS_NAME");
        this.count = data.getString("COUNT");
        this.unit = data.getString("UNIT");
        this.scoreValue = data.getString("SCORE_VALUE");
        this.remark = data.getString("REMARK");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
    }

    public FeeTaxTradeData clone()
    {
        FeeTaxTradeData feeTaxTradeData = new FeeTaxTradeData();
        feeTaxTradeData.setUserId(this.getUserId());
        feeTaxTradeData.setFeeMode(this.getFeeMode());
        feeTaxTradeData.setFeeTypeCode(this.getFeeTypeCode());
        feeTaxTradeData.setBusiFee(this.getBusiFee());
        feeTaxTradeData.setSalePrice(this.getSalePrice());
        feeTaxTradeData.setDiscount(this.getDiscount());
        feeTaxTradeData.setType(this.getType());
        feeTaxTradeData.setRate(this.getRate());
        feeTaxTradeData.setFee1(this.getFee1());
        feeTaxTradeData.setFee2(this.getFee2());
        feeTaxTradeData.setFee3(this.getFee3());
        feeTaxTradeData.setGoodsName(this.getGoodsName());
        feeTaxTradeData.setCount(this.getCount());
        feeTaxTradeData.setUnit(this.getUnit());
        feeTaxTradeData.setScoreValue(this.getScoreValue());
        feeTaxTradeData.setRemark(this.getRemark());
        feeTaxTradeData.setRsrvStr1(this.getRsrvStr1());
        feeTaxTradeData.setRsrvStr2(this.getRsrvStr2());
        feeTaxTradeData.setRsrvStr3(this.getRsrvStr3());
        feeTaxTradeData.setRsrvNum1(this.getRsrvNum1());
        feeTaxTradeData.setRsrvNum2(this.getRsrvNum2());
        feeTaxTradeData.setRsrvNum3(this.getRsrvNum3());
        feeTaxTradeData.setRsrvDate1(this.getRsrvDate1());
        feeTaxTradeData.setRsrvTag1(this.getRsrvTag1());

        return feeTaxTradeData;
    }

    public String getBusiFee()
    {
        return this.busiFee;
    }

    public String getCount()
    {
        return this.count;
    }

    public String getDiscount()
    {
        return this.discount;
    }

    public String getFee1()
    {
        return this.fee1;
    }

    public String getFee2()
    {
        return this.fee2;
    }

    public String getFee3()
    {
        return this.fee3;
    }

    public String getFeeMode()
    {
        return this.feeMode;
    }

    public String getFeeTypeCode()
    {
        return this.feeTypeCode;
    }

    public String getGoodsName()
    {
        return this.goodsName;
    }

    public String getRate()
    {
        return this.rate;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public String getRsrvDate1()
    {
        return this.rsrvDate1;
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

    public String getSalePrice()
    {
        return this.salePrice;
    }

    public String getScoreValue()
    {
        return this.scoreValue;
    }

    public String getTableName()
    {
        return "TF_B_TRADEFEE_TAX";
    }

    public String getType()
    {
        return this.type;
    }

    public String getUnit()
    {
        return this.unit;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setBusiFee(String busiFee)
    {
        this.busiFee = busiFee;
    }

    public void setCount(String count)
    {
        this.count = count;
    }

    public void setDiscount(String discount)
    {
        this.discount = discount;
    }

    public void setFee1(String fee1)
    {
        this.fee1 = fee1;
    }

    public void setFee2(String fee2)
    {
        this.fee2 = fee2;
    }

    public void setFee3(String fee3)
    {
        this.fee3 = fee3;
    }

    public void setFeeMode(String feeMode)
    {
        this.feeMode = feeMode;
    }

    public void setFeeTypeCode(String feeTypeCode)
    {
        this.feeTypeCode = feeTypeCode;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public void setRate(String rate)
    {
        this.rate = rate;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
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

    public void setSalePrice(String salePrice)
    {
        this.salePrice = salePrice;
    }

    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("USER_ID", this.userId);
        data.put("FEE_MODE", this.feeMode);
        data.put("FEE_TYPE_CODE", this.feeTypeCode);
        data.put("BUSI_FEE", this.busiFee);
        data.put("SALE_PRICE", this.salePrice);
        data.put("DISCOUNT", this.discount);
        data.put("TYPE", this.type);
        data.put("RATE", this.rate);
        data.put("FEE1", this.fee1);
        data.put("FEE2", this.fee2);
        data.put("FEE3", this.fee3);
        data.put("GOODS_NAME", this.goodsName);
        data.put("COUNT", this.count);
        data.put("UNIT", this.unit);
        data.put("SCORE_VALUE", this.scoreValue);
        data.put("REMARK", this.remark);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_TAG1", this.rsrvTag1);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
