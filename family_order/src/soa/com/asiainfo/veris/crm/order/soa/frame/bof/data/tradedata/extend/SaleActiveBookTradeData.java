
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.IAcctDayChangeData;

public class SaleActiveBookTradeData extends BaseTradeData implements IAcctDayChangeData
{
    private static final long serialVersionUID = -6862386734723129356L;

    private String serialNumber;

    private String serialNumberB;

    private String startDate;

    private String userId;

    private String instId;

    private String advancePay;

    private String apprStaffId;

    private String campnCode;

    private String campnId;

    private String campnName;

    private String campnType;

    private String bookType;

    private String deviceBrandCode;

    private String resCode;

    private String deviceBrand;

    private String deviceModelCode;

    private String deviceModel;

    private String productIdB;

    private String packageIdB;

    private String acceptTradeId;

    private String dealStateCode;

    private String scoreChanged;

    private String cancelCause;

    private String cancelDate;

    private String cancelStaffId;

    private String contractId;

    private String creditValue;

    private String endDate;

    private String endMode;

    private String foregift;

    private String modifyTag;

    private String months;

    private String operFee;

    private String packageId;

    private String packageName;

    private String processTag;

    private String productId;

    private String productMode;

    private String productName;

    private String relationTradeId;

    private String remark;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvNum1;

    private String rsrvNum2;

    private String rsrvNum3;

    private String rsrvNum4;

    private String rsrvNum5;

    private String rsrvStr1;

    private String rsrvStr10;

    private String rsrvStr11;

    private String rsrvStr12;

    private String rsrvStr13;

    private String rsrvStr14;

    private String rsrvStr15;

    private String rsrvStr16;

    private String rsrvStr17;

    private String rsrvStr18;

    private String rsrvStr19;

    private String rsrvStr2;

    private String rsrvStr20;

    private String rsrvStr21;

    private String rsrvStr22;

    private String rsrvStr23;

    private String rsrvStr24;

    private String rsrvStr25;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    public SaleActiveBookTradeData()
    {

    }

    public SaleActiveBookTradeData(IData data)
    {
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.serialNumberB = data.getString("SERIAL_NUMBER_B");
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.instId = data.getString("INST_ID");
        this.advancePay = data.getString("ADVANCE_PAY");
        this.apprStaffId = data.getString("APPR_STAFF_ID");
        this.campnCode = data.getString("CAMPN_CODE");
        this.campnId = data.getString("CAMPN_ID");
        this.campnName = data.getString("CAMPN_NAME");
        this.campnType = data.getString("CAMPN_TYPE");
        this.bookType = data.getString("BOOK_TYPE");
        this.acceptTradeId = data.getString("ACCEPT_TRADE_ID");
        this.deviceBrandCode = data.getString("DEVICE_BRAND_CODE");
        this.resCode = data.getString("RES_CODE");
        this.deviceBrand = data.getString("DEVICE_BRAND");
        this.deviceModelCode = data.getString("DEVICE_MODEL_CODE");
        this.deviceModel = data.getString("DEVICE_MODEL");
        this.productIdB = data.getString("PRODUCT_ID_B");
        this.packageIdB = data.getString("PACKAGE_ID_B");
        this.dealStateCode = data.getString("DEAL_STATE_CODE");
        this.scoreChanged = data.getString("SCORE_CHANGED");
        this.cancelCause = data.getString("CANCEL_CAUSE");
        this.cancelDate = data.getString("CANCEL_DATE");
        this.cancelStaffId = data.getString("CANCEL_STAFF_ID");
        this.contractId = data.getString("CONTRACT_ID");
        this.creditValue = data.getString("CREDIT_VALUE");
        this.endDate = data.getString("END_DATE");
        this.endMode = data.getString("END_MODE");
        this.foregift = data.getString("FOREGIFT");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.months = data.getString("MONTHS");
        this.operFee = data.getString("OPER_FEE");
        this.packageId = data.getString("PACKAGE_ID");
        this.packageName = data.getString("PACKAGE_NAME");
        this.processTag = data.getString("PROCESS_TAG");
        this.productId = data.getString("PRODUCT_ID");
        this.productMode = data.getString("PRODUCT_MODE");
        this.productName = data.getString("PRODUCT_NAME");
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
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvStr11 = data.getString("RSRV_STR11");
        this.rsrvStr12 = data.getString("RSRV_STR12");
        this.rsrvStr13 = data.getString("RSRV_STR13");
        this.rsrvStr14 = data.getString("RSRV_STR14");
        this.rsrvStr15 = data.getString("RSRV_STR15");
        this.rsrvStr16 = data.getString("RSRV_STR16");
        this.rsrvStr17 = data.getString("RSRV_STR17");
        this.rsrvStr18 = data.getString("RSRV_STR18");
        this.rsrvStr19 = data.getString("RSRV_STR19");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr20 = data.getString("RSRV_STR20");
        this.rsrvStr21 = data.getString("RSRV_STR21");
        this.rsrvStr22 = data.getString("RSRV_STR22");
        this.rsrvStr23 = data.getString("RSRV_STR23");
        this.rsrvStr24 = data.getString("RSRV_STR24");
        this.rsrvStr25 = data.getString("RSRV_STR25");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");

    }

    public SaleActiveBookTradeData clone()
    {
        SaleActiveBookTradeData saleActiveBookTradeData = new SaleActiveBookTradeData();
        saleActiveBookTradeData.setAdvancePay(this.getAdvancePay());
        saleActiveBookTradeData.setApprStaffId(this.getApprStaffId());
        saleActiveBookTradeData.setCampnCode(this.getCampnCode());
        saleActiveBookTradeData.setCampnId(this.getCampnId());
        saleActiveBookTradeData.setCampnName(this.getCampnName());
        saleActiveBookTradeData.setCampnType(this.getCampnType());
        saleActiveBookTradeData.setBookType(this.getBookType());
        saleActiveBookTradeData.setDealStateCode(this.getDealStateCode());
        saleActiveBookTradeData.setDeviceBrand(this.getDeviceBrand());
        saleActiveBookTradeData.setResCode(this.getResCode());
        saleActiveBookTradeData.setDeviceBrandCode(this.getDeviceBrandCode());
        saleActiveBookTradeData.setDeviceModel(this.getDeviceModel());
        saleActiveBookTradeData.setDeviceModelCode(this.getDeviceModelCode());
        saleActiveBookTradeData.setAcceptTradeId(this.getAcceptTradeId());
        saleActiveBookTradeData.setProductIdB(this.getPackageIdB());
        saleActiveBookTradeData.setPackageIdB(this.getPackageIdB());
        saleActiveBookTradeData.setCancelCause(this.getCancelCause());
        saleActiveBookTradeData.setCancelDate(this.getCancelDate());
        saleActiveBookTradeData.setCancelStaffId(this.getCancelStaffId());
        saleActiveBookTradeData.setContractId(this.getContractId());
        saleActiveBookTradeData.setCreditValue(this.getCreditValue());
        saleActiveBookTradeData.setEndDate(this.getEndDate());
        saleActiveBookTradeData.setEndMode(this.getEndMode());
        saleActiveBookTradeData.setForegift(this.getForegift());
        saleActiveBookTradeData.setModifyTag(this.getModifyTag());
        saleActiveBookTradeData.setMonths(this.getMonths());
        saleActiveBookTradeData.setOperFee(this.getOperFee());
        saleActiveBookTradeData.setPackageId(this.getPackageId());
        saleActiveBookTradeData.setPackageName(this.getPackageName());
        saleActiveBookTradeData.setProcessTag(this.getProcessTag());
        saleActiveBookTradeData.setProductId(this.getProductId());
        saleActiveBookTradeData.setProductMode(this.getProductMode());
        saleActiveBookTradeData.setProductName(this.getProductName());
        saleActiveBookTradeData.setRelationTradeId(this.getRelationTradeId());
        saleActiveBookTradeData.setRemark(this.getRemark());
        saleActiveBookTradeData.setRsrvDate1(this.getRsrvDate1());
        saleActiveBookTradeData.setRsrvDate2(this.getRsrvDate2());
        saleActiveBookTradeData.setRsrvDate3(this.getRsrvDate3());
        saleActiveBookTradeData.setRsrvNum1(this.getRsrvNum1());
        saleActiveBookTradeData.setRsrvNum2(this.getRsrvNum2());
        saleActiveBookTradeData.setRsrvNum3(this.getRsrvNum3());
        saleActiveBookTradeData.setRsrvNum4(this.getRsrvNum4());
        saleActiveBookTradeData.setRsrvNum5(this.getRsrvNum5());
        saleActiveBookTradeData.setRsrvStr1(this.getRsrvStr1());
        saleActiveBookTradeData.setRsrvStr10(this.getRsrvStr10());
        saleActiveBookTradeData.setRsrvStr11(this.getRsrvStr11());
        saleActiveBookTradeData.setRsrvStr12(this.getRsrvStr12());
        saleActiveBookTradeData.setRsrvStr13(this.getRsrvStr13());
        saleActiveBookTradeData.setRsrvStr14(this.getRsrvStr14());
        saleActiveBookTradeData.setRsrvStr15(this.getRsrvStr15());
        saleActiveBookTradeData.setRsrvStr16(this.getRsrvStr16());
        saleActiveBookTradeData.setRsrvStr17(this.getRsrvStr17());
        saleActiveBookTradeData.setRsrvStr18(this.getRsrvStr18());
        saleActiveBookTradeData.setRsrvStr19(this.getRsrvStr19());
        saleActiveBookTradeData.setRsrvStr2(this.getRsrvStr2());
        saleActiveBookTradeData.setRsrvStr20(this.getRsrvStr20());
        saleActiveBookTradeData.setRsrvStr21(this.getRsrvStr21());
        saleActiveBookTradeData.setRsrvStr22(this.getRsrvStr22());
        saleActiveBookTradeData.setRsrvStr23(this.getRsrvStr23());
        saleActiveBookTradeData.setRsrvStr24(this.getRsrvStr24());
        saleActiveBookTradeData.setRsrvStr25(this.getRsrvStr25());
        saleActiveBookTradeData.setRsrvStr3(this.getRsrvStr3());
        saleActiveBookTradeData.setRsrvStr4(this.getRsrvStr4());
        saleActiveBookTradeData.setRsrvStr5(this.getRsrvStr5());
        saleActiveBookTradeData.setRsrvStr6(this.getRsrvStr6());
        saleActiveBookTradeData.setRsrvStr7(this.getRsrvStr7());
        saleActiveBookTradeData.setRsrvStr8(this.getRsrvStr8());
        saleActiveBookTradeData.setRsrvStr9(this.getRsrvStr9());
        saleActiveBookTradeData.setRsrvTag1(this.getRsrvTag1());
        saleActiveBookTradeData.setRsrvTag2(this.getRsrvTag2());
        saleActiveBookTradeData.setRsrvTag3(this.getRsrvTag3());
        saleActiveBookTradeData.setSerialNumber(this.getSerialNumber());
        saleActiveBookTradeData.setSerialNumberB(this.getSerialNumberB());
        saleActiveBookTradeData.setStartDate(this.getStartDate());
        saleActiveBookTradeData.setUserId(this.getUserId());
        saleActiveBookTradeData.setInstId(this.getInstId());
        return saleActiveBookTradeData;
    }

    public String getAcceptTradeId()
    {
        return acceptTradeId;
    }

    public String getAdvancePay()
    {
        return advancePay;
    }

    public String getApprStaffId()
    {
        return apprStaffId;
    }

    public String getBookType()
    {
        return bookType;
    }

    public String getCampnCode()
    {
        return campnCode;
    }

    public String getCampnId()
    {
        return campnId;
    }

    public String getCampnName()
    {
        return campnName;
    }

    public String getCampnType()
    {
        return campnType;
    }

    public String getCancelCause()
    {
        return cancelCause;
    }

    public String getCancelDate()
    {
        return cancelDate;
    }

    public String getCancelStaffId()
    {
        return cancelStaffId;
    }

    public String getContractId()
    {
        return contractId;
    }

    public String getCreditValue()
    {
        return creditValue;
    }

    public String getDealStateCode()
    {
        return dealStateCode;
    }

    public String getDeviceBrand()
    {
        return deviceBrand;
    }

    public String getDeviceBrandCode()
    {
        return deviceBrandCode;
    }

    public String getDeviceModel()
    {
        return deviceModel;
    }

    public String getDeviceModelCode()
    {
        return deviceModelCode;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getEndMode()
    {
        return endMode;
    }

    public String getForegift()
    {
        return foregift;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getMonths()
    {
        return months;
    }

    public String getOperFee()
    {
        return operFee;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public String getPackageIdB()
    {
        return packageIdB;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getProcessTag()
    {
        return processTag;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getProductIdB()
    {
        return productIdB;
    }

    public String getProductMode()
    {
        return productMode;
    }

    public String getProductName()
    {
        return productName;
    }

    public String getRelationTradeId()
    {
        return relationTradeId;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getResCode()
    {
        return resCode;
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

    public String getRsrvStr10()
    {
        return rsrvStr10;
    }

    public String getRsrvStr11()
    {
        return rsrvStr11;
    }

    public String getRsrvStr12()
    {
        return rsrvStr12;
    }

    public String getRsrvStr13()
    {
        return rsrvStr13;
    }

    public String getRsrvStr14()
    {
        return rsrvStr14;
    }

    public String getRsrvStr15()
    {
        return rsrvStr15;
    }

    public String getRsrvStr16()
    {
        return rsrvStr16;
    }

    public String getRsrvStr17()
    {
        return rsrvStr17;
    }

    public String getRsrvStr18()
    {
        return rsrvStr18;
    }

    public String getRsrvStr19()
    {
        return rsrvStr19;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr20()
    {
        return rsrvStr20;
    }

    public String getRsrvStr21()
    {
        return rsrvStr21;
    }

    public String getRsrvStr22()
    {
        return rsrvStr22;
    }

    public String getRsrvStr23()
    {
        return rsrvStr23;
    }

    public String getRsrvStr24()
    {
        return rsrvStr24;
    }

    public String getRsrvStr25()
    {
        return rsrvStr25;
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

    public String getScoreChanged()
    {
        return scoreChanged;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getSerialNumberB()
    {
        return serialNumberB;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_SALEACTIVE_BOOK";
    }

    public String getUserId()
    {
        return userId;
    }

    public void setAcceptTradeId(String acceptTradeId)
    {
        this.acceptTradeId = acceptTradeId;
    }

    public void setAdvancePay(String advancePay)
    {
        this.advancePay = advancePay;
    }

    public void setApprStaffId(String apprStaffId)
    {
        this.apprStaffId = apprStaffId;
    }

    public void setBookType(String bookType)
    {
        this.bookType = bookType;
    }

    public void setCampnCode(String campnCode)
    {
        this.campnCode = campnCode;
    }

    public void setCampnId(String campnId)
    {
        this.campnId = campnId;
    }

    public void setCampnName(String campnName)
    {
        this.campnName = campnName;
    }

    public void setCampnType(String campnType)
    {
        this.campnType = campnType;
    }

    public void setCancelCause(String cancelCause)
    {
        this.cancelCause = cancelCause;
    }

    public void setCancelDate(String cancelDate)
    {
        this.cancelDate = cancelDate;
    }

    public void setCancelStaffId(String cancelStaffId)
    {
        this.cancelStaffId = cancelStaffId;
    }

    public void setContractId(String contractId)
    {
        this.contractId = contractId;
    }

    public void setCreditValue(String creditValue)
    {
        this.creditValue = creditValue;
    }

    public void setDealStateCode(String dealStateCode)
    {
        this.dealStateCode = dealStateCode;
    }

    public void setDeviceBrand(String deviceBrand)
    {
        this.deviceBrand = deviceBrand;
    }

    public void setDeviceBrandCode(String deviceBrandCode)
    {
        this.deviceBrandCode = deviceBrandCode;
    }

    public void setDeviceModel(String deviceModel)
    {
        this.deviceModel = deviceModel;
    }

    public void setDeviceModelCode(String deviceModelCode)
    {
        this.deviceModelCode = deviceModelCode;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setEndMode(String endMode)
    {
        this.endMode = endMode;
    }

    public void setForegift(String foregift)
    {
        this.foregift = foregift;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setMonths(String months)
    {
        this.months = months;
    }

    public void setOperFee(String operFee)
    {
        this.operFee = operFee;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setPackageIdB(String packageIdB)
    {
        this.packageIdB = packageIdB;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public void setProcessTag(String processTag)
    {
        this.processTag = processTag;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setProductIdB(String productIdB)
    {
        this.productIdB = productIdB;
    }

    public void setProductMode(String productMode)
    {
        this.productMode = productMode;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public void setRelationTradeId(String relationTradeId)
    {
        this.relationTradeId = relationTradeId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setResCode(String resCode)
    {
        this.resCode = resCode;
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

    public void setRsrvStr10(String rsrvStr10)
    {
        this.rsrvStr10 = rsrvStr10;
    }

    public void setRsrvStr11(String rsrvStr11)
    {
        this.rsrvStr11 = rsrvStr11;
    }

    public void setRsrvStr12(String rsrvStr12)
    {
        this.rsrvStr12 = rsrvStr12;
    }

    public void setRsrvStr13(String rsrvStr13)
    {
        this.rsrvStr13 = rsrvStr13;
    }

    public void setRsrvStr14(String rsrvStr14)
    {
        this.rsrvStr14 = rsrvStr14;
    }

    public void setRsrvStr15(String rsrvStr15)
    {
        this.rsrvStr15 = rsrvStr15;
    }

    public void setRsrvStr16(String rsrvStr16)
    {
        this.rsrvStr16 = rsrvStr16;
    }

    public void setRsrvStr17(String rsrvStr17)
    {
        this.rsrvStr17 = rsrvStr17;
    }

    public void setRsrvStr18(String rsrvStr18)
    {
        this.rsrvStr18 = rsrvStr18;
    }

    public void setRsrvStr19(String rsrvStr19)
    {
        this.rsrvStr19 = rsrvStr19;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr20(String rsrvStr20)
    {
        this.rsrvStr20 = rsrvStr20;
    }

    public void setRsrvStr21(String rsrvStr21)
    {
        this.rsrvStr21 = rsrvStr21;
    }

    public void setRsrvStr22(String rsrvStr22)
    {
        this.rsrvStr22 = rsrvStr22;
    }

    public void setRsrvStr23(String rsrvStr23)
    {
        this.rsrvStr23 = rsrvStr23;
    }

    public void setRsrvStr24(String rsrvStr24)
    {
        this.rsrvStr24 = rsrvStr24;
    }

    public void setRsrvStr25(String rsrvStr25)
    {
        this.rsrvStr25 = rsrvStr25;
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

    public void setScoreChanged(String scoreChanged)
    {
        this.scoreChanged = scoreChanged;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setSerialNumberB(String serialNumberB)
    {
        this.serialNumberB = serialNumberB;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("SERIAL_NUMBER_B", this.serialNumberB);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("INST_ID", this.instId);
        data.put("ADVANCE_PAY", this.advancePay);
        data.put("APPR_STAFF_ID", this.apprStaffId);
        data.put("CAMPN_CODE", this.campnCode);
        data.put("CAMPN_ID", this.campnId);
        data.put("CAMPN_NAME", this.campnName);
        data.put("CAMPN_TYPE", this.campnType);
        data.put("BOOK_TYPE", this.bookType);
        data.put("BOOK_TYPE", this.bookType);
        data.put("ACCEPT_TRADE_ID", this.acceptTradeId);
        data.put("DEVICE_BRAND_CODE", this.deviceBrandCode);
        data.put("RES_CODE", this.resCode);
        data.put("DEVICE_BRAND", this.deviceBrand);
        data.put("DEVICE_MODEL_CODE", this.deviceModelCode);
        data.put("DEVICE_MODEL", this.deviceModel);
        data.put("PRODUCT_ID_B", this.productIdB);
        data.put("PACKAGE_ID_B", this.packageIdB);
        data.put("DEAL_STATE_CODE", this.dealStateCode);
        data.put("SCORE_CHANGED", this.scoreChanged);
        data.put("CANCEL_CAUSE", this.cancelCause);
        data.put("CANCEL_DATE", this.cancelDate);
        data.put("CANCEL_STAFF_ID", this.cancelStaffId);
        data.put("CONTRACT_ID", this.contractId);
        data.put("CREDIT_VALUE", this.creditValue);
        data.put("END_DATE", this.endDate);
        data.put("END_MODE", this.endMode);
        data.put("FOREGIFT", this.foregift);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("MONTHS", this.months);
        data.put("OPER_FEE", this.operFee);
        data.put("PACKAGE_ID", this.packageId);
        data.put("PACKAGE_NAME", this.packageName);
        data.put("PROCESS_TAG", this.processTag);
        data.put("PRODUCT_ID", this.productId);
        data.put("PRODUCT_MODE", this.productMode);
        data.put("PRODUCT_NAME", this.productName);
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
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_STR11", this.rsrvStr11);
        data.put("RSRV_STR12", this.rsrvStr12);
        data.put("RSRV_STR13", this.rsrvStr13);
        data.put("RSRV_STR14", this.rsrvStr14);
        data.put("RSRV_STR15", this.rsrvStr15);
        data.put("RSRV_STR16", this.rsrvStr16);
        data.put("RSRV_STR17", this.rsrvStr17);
        data.put("RSRV_STR18", this.rsrvStr18);
        data.put("RSRV_STR19", this.rsrvStr19);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR20", this.rsrvStr20);
        data.put("RSRV_STR21", this.rsrvStr21);
        data.put("RSRV_STR22", this.rsrvStr22);
        data.put("RSRV_STR23", this.rsrvStr23);
        data.put("RSRV_STR24", this.rsrvStr24);
        data.put("RSRV_STR25", this.rsrvStr25);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
