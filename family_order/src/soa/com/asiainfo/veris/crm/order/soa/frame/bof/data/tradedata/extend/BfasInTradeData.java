
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class BfasInTradeData extends BaseTradeData
{

    private String bfasId;

    private String sysCode;

    private String logId;

    private String subLogId;

    private String partitionId;

    private String eparchyCode;

    private String cityCode;

    private String profitCenId;

    private String departId;

    private String agentCode;

    private String operStaffId;

    private String operTypeCode;

    private String saleTypeCode;

    private String payMoneyCode;

    private String campnId;

    private String feeTypeCode;

    private String feeItemTypeCode;

    private String payModeCode;

    private String acctId;

    private String collAgenCode;

    private String logoutTag;

    private String inModeCode;

    private String netTypeCode;

    private String brandCode;

    private String productId;

    private String userTypeCode;

    private String checkNumber;

    private String resTypeCode;

    private String resKindCode;

    private String capacityTypeCode;

    private String deviceTypeCode;

    private String deviceModelCode;

    private String deviceColorCode;

    private String orderId;

    private String deviceProduct;

    private String supplyType;

    private String procurementType;

    private String agencyId;

    private String incomeCode;

    private String receFee;

    private String fee;

    private String presentFee;

    private String formFee;

    private String score;

    private String accDate;

    private String operDate;

    private String cancelSubLogId;

    private String cancelDate;

    private String cancelTag;

    private String depositBeginDate;

    private String depositEndDate;

    private String procTag;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvNum4;

    private String rsrvNum5;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String noTaxFee;

    private String taxFee;

    private String rate;

    public BfasInTradeData()
    {

    }

    public BfasInTradeData(IData data)
    {
        this.bfasId = data.getString("BFAS_ID");
        this.sysCode = data.getString("SYS_CODE");
        this.logId = data.getString("LOG_ID");
        this.subLogId = data.getString("SUB_LOG_ID");
        this.partitionId = data.getString("PARTITION_ID");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.cityCode = data.getString("CITY_CODE");
        this.profitCenId = data.getString("PROFIT_CEN_ID");
        this.departId = data.getString("DEPART_ID");
        this.agentCode = data.getString("AGENT_CODE");
        this.operStaffId = data.getString("OPER_STAFF_ID");
        this.operTypeCode = data.getString("OPER_TYPE_CODE");
        this.saleTypeCode = data.getString("SALE_TYPE_CODE");
        this.payMoneyCode = data.getString("PAY_MONEY_CODE");
        this.campnId = data.getString("CAMPN_ID");
        this.feeTypeCode = data.getString("FEE_TYPE_CODE");
        this.feeItemTypeCode = data.getString("FEE_ITEM_TYPE_CODE");
        this.payModeCode = data.getString("PAY_MODE_CODE");
        this.acctId = data.getString("ACCT_ID");
        this.collAgenCode = data.getString("COLL_AGEN_CODE");
        this.logoutTag = data.getString("LOGOUT_TAG");
        this.inModeCode = data.getString("IN_MODE_CODE");
        this.netTypeCode = data.getString("NET_TYPE_CODE");
        this.brandCode = data.getString("BRAND_CODE");
        this.productId = data.getString("PRODUCT_ID");
        this.userTypeCode = data.getString("USER_TYPE_CODE");
        this.checkNumber = data.getString("CHECK_NUMBER");
        this.resTypeCode = data.getString("RES_TYPE_CODE");
        this.resKindCode = data.getString("RES_KIND_CODE");
        this.capacityTypeCode = data.getString("CAPACITY_TYPE_CODE");
        this.deviceTypeCode = data.getString("DEVICE_TYPE_CODE");
        this.deviceModelCode = data.getString("DEVICE_MODEL_CODE");
        this.deviceColorCode = data.getString("DEVICE_COLOR_CODE");
        this.orderId = data.getString("ORDER_ID");
        this.deviceProduct = data.getString("DEVICE_PRODUCT");
        this.supplyType = data.getString("SUPPLY_TYPE");
        this.procurementType = data.getString("PROCUREMENT_TYPE");
        this.agencyId = data.getString("AGENCY_ID");
        this.incomeCode = data.getString("INCOME_CODE");
        this.receFee = data.getString("RECE_FEE");
        this.fee = data.getString("FEE");
        this.presentFee = data.getString("PRESENT_FEE");
        this.formFee = data.getString("FORM_FEE");
        this.score = data.getString("SCORE");
        this.accDate = data.getString("ACC_DATE");
        this.operDate = data.getString("OPER_DATE");
        this.cancelSubLogId = data.getString("CANCEL_SUB_LOG_ID");
        this.cancelDate = data.getString("CANCEL_DATE");
        this.cancelTag = data.getString("CANCEL_TAG");
        this.depositBeginDate = data.getString("DEPOSIT_BEGIN_DATE");
        this.depositEndDate = data.getString("DEPOSIT_END_DATE");
        this.procTag = data.getString("PROC_TAG");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvNum4 = data.getString("RSRV_NUM4");
        this.rsrvNum5 = data.getString("RSRV_NUM5");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.noTaxFee = data.getString("NO_TAX_FEE");
        this.taxFee = data.getString("TAX_FEE");
        this.rate = data.getString("RATE");
    }

    public BfasInTradeData clone()
    {
        BfasInTradeData bfasInTradeData = new BfasInTradeData();
        bfasInTradeData.setBfasId(this.getBfasId());
        bfasInTradeData.setSysCode(this.getSysCode());
        bfasInTradeData.setLogId(this.getLogId());
        bfasInTradeData.setSubLogId(this.getSubLogId());
        bfasInTradeData.setPartitionId(this.getPartitionId());
        bfasInTradeData.setEparchyCode(this.getEparchyCode());
        bfasInTradeData.setCityCode(this.getCityCode());
        bfasInTradeData.setProfitCenId(this.getProfitCenId());
        bfasInTradeData.setDepartId(this.getDepartId());
        bfasInTradeData.setAgentCode(this.getAgentCode());
        bfasInTradeData.setOperStaffId(this.getOperStaffId());
        bfasInTradeData.setOperTypeCode(this.getOperTypeCode());
        bfasInTradeData.setSaleTypeCode(this.getSaleTypeCode());
        bfasInTradeData.setPayMoneyCode(this.getPayMoneyCode());
        bfasInTradeData.setCampnId(this.getCampnId());
        bfasInTradeData.setFeeTypeCode(this.getFeeTypeCode());
        bfasInTradeData.setFeeItemTypeCode(this.getFeeItemTypeCode());
        bfasInTradeData.setPayModeCode(this.getPayModeCode());
        bfasInTradeData.setAcctId(this.getAcctId());
        bfasInTradeData.setCollAgenCode(this.getCollAgenCode());
        bfasInTradeData.setLogoutTag(this.getLogoutTag());
        bfasInTradeData.setInModeCode(this.getInModeCode());
        bfasInTradeData.setNetTypeCode(this.getNetTypeCode());
        bfasInTradeData.setBrandCode(this.getBrandCode());
        bfasInTradeData.setProductId(this.getProductId());
        bfasInTradeData.setUserTypeCode(this.getUserTypeCode());
        bfasInTradeData.setCheckNumber(this.getCheckNumber());
        bfasInTradeData.setResTypeCode(this.getResTypeCode());
        bfasInTradeData.setResKindCode(this.getResKindCode());
        bfasInTradeData.setCapacityTypeCode(this.getCapacityTypeCode());
        bfasInTradeData.setDeviceTypeCode(this.getDeviceTypeCode());
        bfasInTradeData.setDeviceModelCode(this.getDeviceModelCode());
        bfasInTradeData.setDeviceColorCode(this.getDeviceColorCode());
        bfasInTradeData.setOrderId(this.getOrderId());
        bfasInTradeData.setDeviceProduct(this.getDeviceProduct());
        bfasInTradeData.setSupplyType(this.getSupplyType());
        bfasInTradeData.setProcurementType(this.getProcurementType());
        bfasInTradeData.setAgencyId(this.getAgencyId());
        bfasInTradeData.setIncomeCode(this.getIncomeCode());
        bfasInTradeData.setReceFee(this.getReceFee());
        bfasInTradeData.setFee(this.getFee());
        bfasInTradeData.setPresentFee(this.getPresentFee());
        bfasInTradeData.setFormFee(this.getFormFee());
        bfasInTradeData.setScore(this.getScore());
        bfasInTradeData.setAccDate(this.getAccDate());
        bfasInTradeData.setOperDate(this.getOperDate());
        bfasInTradeData.setCancelSubLogId(this.getCancelSubLogId());
        bfasInTradeData.setCancelDate(this.getCancelDate());
        bfasInTradeData.setCancelTag(this.getCancelTag());
        bfasInTradeData.setDepositBeginDate(this.getDepositBeginDate());
        bfasInTradeData.setDepositEndDate(this.getDepositEndDate());
        bfasInTradeData.setProcTag(this.getProcTag());
        bfasInTradeData.setRsrvTag1(this.getRsrvTag1());
        bfasInTradeData.setRsrvTag2(this.getRsrvTag2());
        bfasInTradeData.setRsrvTag3(this.getRsrvTag3());
        bfasInTradeData.setRsrvStr1(this.getRsrvStr1());
        bfasInTradeData.setRsrvStr2(this.getRsrvStr2());
        bfasInTradeData.setRsrvStr3(this.getRsrvStr3());
        bfasInTradeData.setRsrvStr4(this.getRsrvStr4());
        bfasInTradeData.setRsrvStr5(this.getRsrvStr5());
        bfasInTradeData.setRsrvStr6(this.getRsrvStr6());
        bfasInTradeData.setRsrvStr7(this.getRsrvStr7());
        bfasInTradeData.setRsrvNum1(this.getRsrvNum1());
        bfasInTradeData.setRsrvNum2(this.getRsrvNum2());
        bfasInTradeData.setRsrvNum3(this.getRsrvNum3());
        bfasInTradeData.setRsrvNum4(this.getRsrvNum4());
        bfasInTradeData.setRsrvNum5(this.getRsrvNum5());
        bfasInTradeData.setRsrvDate1(this.getRsrvDate1());
        bfasInTradeData.setRsrvDate2(this.getRsrvDate2());
        bfasInTradeData.setRsrvDate3(this.getRsrvDate3());
        bfasInTradeData.setNoTaxFee(this.getNoTaxFee());
        bfasInTradeData.setTaxFee(this.getTaxFee());
        bfasInTradeData.setRate(this.getRate());

        return bfasInTradeData;
    }

    public String getAccDate()
    {
        return this.accDate;
    }

    public String getAcctId()
    {
        return this.acctId;
    }

    public String getAgencyId()
    {
        return this.agencyId;
    }

    public String getAgentCode()
    {
        return this.agentCode;
    }

    public String getBfasId()
    {
        return this.bfasId;
    }

    public String getBrandCode()
    {
        return this.brandCode;
    }

    public String getCampnId()
    {
        return this.campnId;
    }

    public String getCancelDate()
    {
        return this.cancelDate;
    }

    public String getCancelSubLogId()
    {
        return this.cancelSubLogId;
    }

    public String getCancelTag()
    {
        return this.cancelTag;
    }

    public String getCapacityTypeCode()
    {
        return this.capacityTypeCode;
    }

    public String getCheckNumber()
    {
        return this.checkNumber;
    }

    public String getCityCode()
    {
        return this.cityCode;
    }

    public String getCollAgenCode()
    {
        return this.collAgenCode;
    }

    public String getDepartId()
    {
        return this.departId;
    }

    public String getDepositBeginDate()
    {
        return this.depositBeginDate;
    }

    public String getDepositEndDate()
    {
        return this.depositEndDate;
    }

    public String getDeviceColorCode()
    {
        return this.deviceColorCode;
    }

    public String getDeviceModelCode()
    {
        return this.deviceModelCode;
    }

    public String getDeviceProduct()
    {
        return this.deviceProduct;
    }

    public String getDeviceTypeCode()
    {
        return this.deviceTypeCode;
    }

    public String getEparchyCode()
    {
        return this.eparchyCode;
    }

    public String getFee()
    {
        return this.fee;
    }

    public String getFeeItemTypeCode()
    {
        return this.feeItemTypeCode;
    }

    public String getFeeTypeCode()
    {
        return this.feeTypeCode;
    }

    public String getFormFee()
    {
        return this.formFee;
    }

    public String getIncomeCode()
    {
        return this.incomeCode;
    }

    public String getInModeCode()
    {
        return this.inModeCode;
    }

    public String getLogId()
    {
        return this.logId;
    }

    public String getLogoutTag()
    {
        return this.logoutTag;
    }

    public String getNetTypeCode()
    {
        return this.netTypeCode;
    }

    public String getNoTaxFee()
    {
        return this.noTaxFee;
    }

    public String getOperDate()
    {
        return this.operDate;
    }

    public String getOperStaffId()
    {
        return this.operStaffId;
    }

    public String getOperTypeCode()
    {
        return this.operTypeCode;
    }

    public String getOrderId()
    {
        return this.orderId;
    }

    public String getPartitionId()
    {
        return this.partitionId;
    }

    public String getPayModeCode()
    {
        return this.payModeCode;
    }

    public String getPayMoneyCode()
    {
        return this.payMoneyCode;
    }

    public String getPresentFee()
    {
        return this.presentFee;
    }

    public String getProcTag()
    {
        return this.procTag;
    }

    public String getProcurementType()
    {
        return this.procurementType;
    }

    public String getProductId()
    {
        return this.productId;
    }

    public String getProfitCenId()
    {
        return this.profitCenId;
    }

    public String getRate()
    {
        return this.rate;
    }

    public String getReceFee()
    {
        return this.receFee;
    }

    public String getResKindCode()
    {
        return this.resKindCode;
    }

    public String getResTypeCode()
    {
        return this.resTypeCode;
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

    public String getRsrvNum4()
    {
        return this.rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return this.rsrvNum5;
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

    public String getRsrvStr6()
    {
        return this.rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return this.rsrvStr7;
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

    public String getSaleTypeCode()
    {
        return this.saleTypeCode;
    }

    public String getScore()
    {
        return this.score;
    }

    public String getSubLogId()
    {
        return this.subLogId;
    }

    public String getSupplyType()
    {
        return this.supplyType;
    }

    public String getSysCode()
    {
        return this.sysCode;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_BFAS_IN";
    }

    public String getTaxFee()
    {
        return this.taxFee;
    }

    public String getUserTypeCode()
    {
        return this.userTypeCode;
    }

    public void setAccDate(String accDate)
    {
        this.accDate = accDate;
    }

    public void setAcctId(String acctId)
    {
        this.acctId = acctId;
    }

    public void setAgencyId(String agencyId)
    {
        this.agencyId = agencyId;
    }

    public void setAgentCode(String agentCode)
    {
        this.agentCode = agentCode;
    }

    public void setBfasId(String bfasId)
    {
        this.bfasId = bfasId;
    }

    public void setBrandCode(String brandCode)
    {
        this.brandCode = brandCode;
    }

    public void setCampnId(String campnId)
    {
        this.campnId = campnId;
    }

    public void setCancelDate(String cancelDate)
    {
        this.cancelDate = cancelDate;
    }

    public void setCancelSubLogId(String cancelSubLogId)
    {
        this.cancelSubLogId = cancelSubLogId;
    }

    public void setCancelTag(String cancelTag)
    {
        this.cancelTag = cancelTag;
    }

    public void setCapacityTypeCode(String capacityTypeCode)
    {
        this.capacityTypeCode = capacityTypeCode;
    }

    public void setCheckNumber(String checkNumber)
    {
        this.checkNumber = checkNumber;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setCollAgenCode(String collAgenCode)
    {
        this.collAgenCode = collAgenCode;
    }

    public void setDepartId(String departId)
    {
        this.departId = departId;
    }

    public void setDepositBeginDate(String depositBeginDate)
    {
        this.depositBeginDate = depositBeginDate;
    }

    public void setDepositEndDate(String depositEndDate)
    {
        this.depositEndDate = depositEndDate;
    }

    public void setDeviceColorCode(String deviceColorCode)
    {
        this.deviceColorCode = deviceColorCode;
    }

    public void setDeviceModelCode(String deviceModelCode)
    {
        this.deviceModelCode = deviceModelCode;
    }

    public void setDeviceProduct(String deviceProduct)
    {
        this.deviceProduct = deviceProduct;
    }

    public void setDeviceTypeCode(String deviceTypeCode)
    {
        this.deviceTypeCode = deviceTypeCode;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setFee(String fee)
    {
        this.fee = fee;
    }

    public void setFeeItemTypeCode(String feeItemTypeCode)
    {
        this.feeItemTypeCode = feeItemTypeCode;
    }

    public void setFeeTypeCode(String feeTypeCode)
    {
        this.feeTypeCode = feeTypeCode;
    }

    public void setFormFee(String formFee)
    {
        this.formFee = formFee;
    }

    public void setIncomeCode(String incomeCode)
    {
        this.incomeCode = incomeCode;
    }

    public void setInModeCode(String inModeCode)
    {
        this.inModeCode = inModeCode;
    }

    public void setLogId(String logId)
    {
        this.logId = logId;
    }

    public void setLogoutTag(String logoutTag)
    {
        this.logoutTag = logoutTag;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setNoTaxFee(String noTaxFee)
    {
        this.noTaxFee = noTaxFee;
    }

    public void setOperDate(String operDate)
    {
        this.operDate = operDate;
    }

    public void setOperStaffId(String operStaffId)
    {
        this.operStaffId = operStaffId;
    }

    public void setOperTypeCode(String operTypeCode)
    {
        this.operTypeCode = operTypeCode;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setPartitionId(String partitionId)
    {
        this.partitionId = partitionId;
    }

    public void setPayModeCode(String payModeCode)
    {
        this.payModeCode = payModeCode;
    }

    public void setPayMoneyCode(String payMoneyCode)
    {
        this.payMoneyCode = payMoneyCode;
    }

    public void setPresentFee(String presentFee)
    {
        this.presentFee = presentFee;
    }

    public void setProcTag(String procTag)
    {
        this.procTag = procTag;
    }

    public void setProcurementType(String procurementType)
    {
        this.procurementType = procurementType;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setProfitCenId(String profitCenId)
    {
        this.profitCenId = profitCenId;
    }

    public void setRate(String rate)
    {
        this.rate = rate;
    }

    public void setReceFee(String receFee)
    {
        this.receFee = receFee;
    }

    public void setResKindCode(String resKindCode)
    {
        this.resKindCode = resKindCode;
    }

    public void setResTypeCode(String resTypeCode)
    {
        this.resTypeCode = resTypeCode;
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

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
    }

    public void setRsrvStr7(String rsrvStr7)
    {
        this.rsrvStr7 = rsrvStr7;
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

    public void setSaleTypeCode(String saleTypeCode)
    {
        this.saleTypeCode = saleTypeCode;
    }

    public void setScore(String score)
    {
        this.score = score;
    }

    public void setSubLogId(String subLogId)
    {
        this.subLogId = subLogId;
    }

    public void setSupplyType(String supplyType)
    {
        this.supplyType = supplyType;
    }

    public void setSysCode(String sysCode)
    {
        this.sysCode = sysCode;
    }

    public void setTaxFee(String taxFee)
    {
        this.taxFee = taxFee;
    }

    public void setUserTypeCode(String userTypeCode)
    {
        this.userTypeCode = userTypeCode;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("BFAS_ID", this.bfasId);
        data.put("SYS_CODE", this.sysCode);
        data.put("LOG_ID", this.logId);
        data.put("SUB_LOG_ID", this.subLogId);
        data.put("PARTITION_ID", this.partitionId);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("CITY_CODE", this.cityCode);
        data.put("PROFIT_CEN_ID", this.profitCenId);
        data.put("DEPART_ID", this.departId);
        data.put("AGENT_CODE", this.agentCode);
        data.put("OPER_STAFF_ID", this.operStaffId);
        data.put("OPER_TYPE_CODE", this.operTypeCode);
        data.put("SALE_TYPE_CODE", this.saleTypeCode);
        data.put("PAY_MONEY_CODE", this.payMoneyCode);
        data.put("CAMPN_ID", this.campnId);
        data.put("FEE_TYPE_CODE", this.feeTypeCode);
        data.put("FEE_ITEM_TYPE_CODE", this.feeItemTypeCode);
        data.put("PAY_MODE_CODE", this.payModeCode);
        data.put("ACCT_ID", this.acctId);
        data.put("COLL_AGEN_CODE", this.collAgenCode);
        data.put("LOGOUT_TAG", this.logoutTag);
        data.put("IN_MODE_CODE", this.inModeCode);
        data.put("NET_TYPE_CODE", this.netTypeCode);
        data.put("BRAND_CODE", this.brandCode);
        data.put("PRODUCT_ID", this.productId);
        data.put("USER_TYPE_CODE", this.userTypeCode);
        data.put("CHECK_NUMBER", this.checkNumber);
        data.put("RES_TYPE_CODE", this.resTypeCode);
        data.put("RES_KIND_CODE", this.resKindCode);
        data.put("CAPACITY_TYPE_CODE", this.capacityTypeCode);
        data.put("DEVICE_TYPE_CODE", this.deviceTypeCode);
        data.put("DEVICE_MODEL_CODE", this.deviceModelCode);
        data.put("DEVICE_COLOR_CODE", this.deviceColorCode);
        data.put("ORDER_ID", this.orderId);
        data.put("DEVICE_PRODUCT", this.deviceProduct);
        data.put("SUPPLY_TYPE", this.supplyType);
        data.put("PROCUREMENT_TYPE", this.procurementType);
        data.put("AGENCY_ID", this.agencyId);
        data.put("INCOME_CODE", this.incomeCode);
        data.put("RECE_FEE", this.receFee);
        data.put("FEE", this.fee);
        data.put("PRESENT_FEE", this.presentFee);
        data.put("FORM_FEE", this.formFee);
        data.put("SCORE", this.score);
        data.put("ACC_DATE", this.accDate);
        data.put("OPER_DATE", this.operDate);
        data.put("CANCEL_SUB_LOG_ID", this.cancelSubLogId);
        data.put("CANCEL_DATE", this.cancelDate);
        data.put("CANCEL_TAG", this.cancelTag);
        data.put("DEPOSIT_BEGIN_DATE", this.depositBeginDate);
        data.put("DEPOSIT_END_DATE", this.depositEndDate);
        data.put("PROC_TAG", this.procTag);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_NUM4", this.rsrvNum4);
        data.put("RSRV_NUM5", this.rsrvNum5);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("NO_TAX_FEE", this.noTaxFee);
        data.put("TAX_FEE", this.taxFee);
        data.put("RATE", this.rate);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
