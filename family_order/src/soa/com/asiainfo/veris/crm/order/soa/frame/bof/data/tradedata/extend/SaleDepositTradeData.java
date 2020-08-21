
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.IAcctDayChangeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;

public class SaleDepositTradeData extends ProductModuleTradeData implements IAcctDayChangeData
{
    private String aDiscntCode;

    private String depositType;

    private String fee;

    private String inDepositCode;

    private String limitMoney;

    private String months;

    private String outDepositCode;

    private String paymentId;

    private String payMode;

    private String relationTradeId;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

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

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String remark;

    private String userIdA;

    public SaleDepositTradeData()
    {
        this.elementType = BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT;
    }

    public SaleDepositTradeData(IData data)
    {
        this.elementType = BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT;
        this.aDiscntCode = data.getString("A_DISCNT_CODE");
        this.campnId = data.getString("CAMPN_ID");
        this.depositType = data.getString("DEPOSIT_TYPE");
        this.elementId = data.getString("DISCNT_GIFT_ID", data.getString("ELEMENT_ID"));
        this.endDate = data.getString("END_DATE");
        this.fee = data.getString("FEE");
        this.instId = data.getString("INST_ID");
        this.inDepositCode = data.getString("IN_DEPOSIT_CODE");
        this.limitMoney = data.getString("LIMIT_MONEY");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.months = data.getString("MONTHS");
        this.outDepositCode = data.getString("OUT_DEPOSIT_CODE");
        this.packageId = data.getString("PACKAGE_ID");
        this.paymentId = data.getString("PAYMENT_ID");
        this.payMode = data.getString("PAY_MODE");
        this.productId = data.getString("PRODUCT_ID");
        this.relationTradeId = data.getString("RELATION_TRADE_ID");
        this.remark = data.getString("REMARK");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
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
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.userIdA = data.getString("USER_ID_A");
    }

    public SaleDepositTradeData clone()
    {
        SaleDepositTradeData saleDepositTradeData = new SaleDepositTradeData();
        saleDepositTradeData.setADiscntCode(this.getADiscntCode());
        saleDepositTradeData.setCampnId(this.getCampnId());
        saleDepositTradeData.setDepositType(this.getDepositType());
        saleDepositTradeData.setElementId(this.getElementId());
        saleDepositTradeData.setEndDate(this.getEndDate());
        saleDepositTradeData.setFee(this.getFee());
        saleDepositTradeData.setInstId(this.getInstId());
        saleDepositTradeData.setInDepositCode(this.getInDepositCode());
        saleDepositTradeData.setLimitMoney(this.getLimitMoney());
        saleDepositTradeData.setModifyTag(this.getModifyTag());
        saleDepositTradeData.setMonths(this.getMonths());
        saleDepositTradeData.setOutDepositCode(this.getOutDepositCode());
        saleDepositTradeData.setPackageId(this.getPackageId());
        saleDepositTradeData.setPaymentId(this.getPaymentId());
        saleDepositTradeData.setPayMode(this.getPayMode());
        saleDepositTradeData.setProductId(this.getProductId());
        saleDepositTradeData.setRelationTradeId(this.getRelationTradeId());
        saleDepositTradeData.setRemark(this.getRemark());
        saleDepositTradeData.setRsrvDate1(this.getRsrvDate1());
        saleDepositTradeData.setRsrvDate2(this.getRsrvDate2());
        saleDepositTradeData.setRsrvDate3(this.getRsrvDate3());
        saleDepositTradeData.setRsrvNum1(this.getRsrvNum1());
        saleDepositTradeData.setRsrvNum2(this.getRsrvNum2());
        saleDepositTradeData.setRsrvNum3(this.getRsrvNum3());
        saleDepositTradeData.setRsrvNum4(this.getRsrvNum4());
        saleDepositTradeData.setRsrvNum5(this.getRsrvNum5());
        saleDepositTradeData.setRsrvStr1(this.getRsrvStr1());
        saleDepositTradeData.setRsrvStr2(this.getRsrvStr2());
        saleDepositTradeData.setRsrvStr3(this.getRsrvStr3());
        saleDepositTradeData.setRsrvStr4(this.getRsrvStr4());
        saleDepositTradeData.setRsrvStr5(this.getRsrvStr5());
        saleDepositTradeData.setRsrvTag1(this.getRsrvTag1());
        saleDepositTradeData.setRsrvTag2(this.getRsrvTag2());
        saleDepositTradeData.setRsrvTag3(this.getRsrvTag3());
        saleDepositTradeData.setStartDate(this.getStartDate());
        saleDepositTradeData.setUserId(this.getUserId());
        saleDepositTradeData.setUserIdA(this.getUserIdA());
        return saleDepositTradeData;
    }

    public String getADiscntCode()
    {
        return aDiscntCode;
    }

    public String getDepositType()
    {
        return depositType;
    }

    public String getFee()
    {
        return fee;
    }

    public String getInDepositCode()
    {
        return inDepositCode;
    }

    public String getLimitMoney()
    {
        return limitMoney;
    }

    public String getMonths()
    {
        return months;
    }

    public String getOutDepositCode()
    {
        return outDepositCode;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public String getPayMode()
    {
        return payMode;
    }

    public String getRelationTradeId()
    {
        return relationTradeId;
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

    public String getRsrvNum1()
    {
        return rsrvNum1;
    }

    public String getRsrvNum2()
    {
        return rsrvNum2;
    }

    public String getRsrvNum3()
    {
        return rsrvNum3;
    }

    public String getRsrvNum4()
    {
        return rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return rsrvNum5;
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

    public String getRsrvTag1()
    {
        return rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_SALE_DEPOSIT";
    }

    public String getUserIdA()
    {
        return userIdA;
    }

    public void setADiscntCode(String discntCode)
    {
        aDiscntCode = discntCode;
    }

    public void setDepositType(String depositType)
    {
        this.depositType = depositType;
    }

    public void setFee(String fee)
    {
        this.fee = fee;
    }

    public void setInDepositCode(String inDepositCode)
    {
        this.inDepositCode = inDepositCode;
    }

    public void setLimitMoney(String limitMoney)
    {
        this.limitMoney = limitMoney;
    }

    public void setMonths(String months)
    {
        this.months = months;
    }

    public void setOutDepositCode(String outDepositCode)
    {
        this.outDepositCode = outDepositCode;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public void setPayMode(String payMode)
    {
        this.payMode = payMode;
    }

    public void setRelationTradeId(String relationTradeId)
    {
        this.relationTradeId = relationTradeId;
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

    public void setUserIdA(String userIdA)
    {
        this.userIdA = userIdA;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("A_DISCNT_CODE", this.aDiscntCode);
        data.put("CAMPN_ID", this.campnId);
        data.put("DEPOSIT_TYPE", this.depositType);
        data.put("DISCNT_GIFT_ID", this.elementId);
        data.put("END_DATE", this.endDate);
        data.put("FEE", this.fee);
        data.put("INST_ID", this.instId);
        data.put("IN_DEPOSIT_CODE", this.inDepositCode);
        data.put("LIMIT_MONEY", this.limitMoney);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("MONTHS", this.months);
        data.put("OUT_DEPOSIT_CODE", this.outDepositCode);
        data.put("PACKAGE_ID", this.packageId);
        data.put("PAYMENT_ID", this.paymentId);
        data.put("PAY_MODE", this.payMode);
        data.put("PRODUCT_ID", this.productId);
        data.put("RELATION_TRADE_ID", this.relationTradeId);
        data.put("REMARK", this.remark);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
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
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("USER_ID_A", this.userIdA);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
