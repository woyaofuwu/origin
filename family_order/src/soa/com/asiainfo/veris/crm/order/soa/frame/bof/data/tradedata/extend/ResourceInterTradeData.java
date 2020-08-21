
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class ResourceInterTradeData extends BaseTradeData
{

    private String logId;

    private String subLogId;

    private String partitionId;

    private String inventOrgId;

    private String eparchyCode;

    private String cityCode;

    private String operDepartId;

    private String operStaffId;

    private String stockIdO;

    private String stockIdN;

    private String operTag;

    private String costCenId;

    private String operTime;

    private String assignTime;

    private String resTypeCode;

    private String resKindCode;

    private String capacityTypeCode;

    private String deviceTypeCode;

    private String deviceFactoryCode;

    private String deviceModelCode;

    private String deviceColorCode;

    private String orderId;

    private String supplyId;

    private String supplyType;

    private String agencyId;

    private String procurementType;

    private String resState;

    private String operTypeCode;

    private String campnId;

    private String productId;

    private String saleTypeCode;

    private String operNum;

    private String dcTag;

    private String cancelSubLogId;

    private String cancelDate;

    private String remark;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

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

    public ResourceInterTradeData()
    {

    }

    public ResourceInterTradeData(IData data)
    {
        this.logId = data.getString("LOG_ID");
        this.subLogId = data.getString("SUB_LOG_ID");
        this.partitionId = data.getString("PARTITION_ID");
        this.inventOrgId = data.getString("INVENT_ORG_ID");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.cityCode = data.getString("CITY_CODE");
        this.operDepartId = data.getString("OPER_DEPART_ID");
        this.operStaffId = data.getString("OPER_STAFF_ID");
        this.stockIdO = data.getString("STOCK_ID_O");
        this.stockIdN = data.getString("STOCK_ID_N");
        this.operTag = data.getString("OPER_TAG");
        this.costCenId = data.getString("COST_CEN_ID");
        this.operTime = data.getString("OPER_TIME");
        this.assignTime = data.getString("ASSIGN_TIME");
        this.resTypeCode = data.getString("RES_TYPE_CODE");
        this.resKindCode = data.getString("RES_KIND_CODE");
        this.capacityTypeCode = data.getString("CAPACITY_TYPE_CODE");
        this.deviceTypeCode = data.getString("DEVICE_TYPE_CODE");
        this.deviceFactoryCode = data.getString("DEVICE_FACTORY_CODE");
        this.deviceModelCode = data.getString("DEVICE_MODEL_CODE");
        this.deviceColorCode = data.getString("DEVICE_COLOR_CODE");
        this.orderId = data.getString("ORDER_ID");
        this.supplyId = data.getString("SUPPLY_ID");
        this.supplyType = data.getString("SUPPLY_TYPE");
        this.agencyId = data.getString("AGENCY_ID");
        this.procurementType = data.getString("PROCUREMENT_TYPE");
        this.resState = data.getString("RES_STATE");
        this.operTypeCode = data.getString("OPER_TYPE_CODE");
        this.campnId = data.getString("CAMPN_ID");
        this.productId = data.getString("PRODUCT_ID");
        this.saleTypeCode = data.getString("SALE_TYPE_CODE");
        this.operNum = data.getString("OPER_NUM");
        this.dcTag = data.getString("DC_TAG");
        this.cancelSubLogId = data.getString("CANCEL_SUB_LOG_ID");
        this.cancelDate = data.getString("CANCEL_DATE");
        this.remark = data.getString("REMARK");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
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
    }

    public ResourceInterTradeData clone()
    {
        ResourceInterTradeData resourceInterTradeData = new ResourceInterTradeData();
        resourceInterTradeData.setLogId(this.getLogId());
        resourceInterTradeData.setSubLogId(this.getSubLogId());
        resourceInterTradeData.setPartitionId(this.getPartitionId());
        resourceInterTradeData.setInventOrgId(this.getInventOrgId());
        resourceInterTradeData.setEparchyCode(this.getEparchyCode());
        resourceInterTradeData.setCityCode(this.getCityCode());
        resourceInterTradeData.setOperDepartId(this.getOperDepartId());
        resourceInterTradeData.setOperStaffId(this.getOperStaffId());
        resourceInterTradeData.setStockIdO(this.getStockIdO());
        resourceInterTradeData.setStockIdN(this.getStockIdN());
        resourceInterTradeData.setOperTag(this.getOperTag());
        resourceInterTradeData.setCostCenId(this.getCostCenId());
        resourceInterTradeData.setOperTime(this.getOperTime());
        resourceInterTradeData.setAssignTime(this.getAssignTime());
        resourceInterTradeData.setResTypeCode(this.getResTypeCode());
        resourceInterTradeData.setResKindCode(this.getResKindCode());
        resourceInterTradeData.setCapacityTypeCode(this.getCapacityTypeCode());
        resourceInterTradeData.setDeviceTypeCode(this.getDeviceTypeCode());
        resourceInterTradeData.setDeviceFactoryCode(this.getDeviceFactoryCode());
        resourceInterTradeData.setDeviceModelCode(this.getDeviceModelCode());
        resourceInterTradeData.setDeviceColorCode(this.getDeviceColorCode());
        resourceInterTradeData.setOrderId(this.getOrderId());
        resourceInterTradeData.setSupplyId(this.getSupplyId());
        resourceInterTradeData.setSupplyType(this.getSupplyType());
        resourceInterTradeData.setAgencyId(this.getAgencyId());
        resourceInterTradeData.setProcurementType(this.getProcurementType());
        resourceInterTradeData.setResState(this.getResState());
        resourceInterTradeData.setOperTypeCode(this.getOperTypeCode());
        resourceInterTradeData.setCampnId(this.getCampnId());
        resourceInterTradeData.setProductId(this.getProductId());
        resourceInterTradeData.setSaleTypeCode(this.getSaleTypeCode());
        resourceInterTradeData.setOperNum(this.getOperNum());
        resourceInterTradeData.setDcTag(this.getDcTag());
        resourceInterTradeData.setCancelSubLogId(this.getCancelSubLogId());
        resourceInterTradeData.setCancelDate(this.getCancelDate());
        resourceInterTradeData.setRemark(this.getRemark());
        resourceInterTradeData.setRsrvTag1(this.getRsrvTag1());
        resourceInterTradeData.setRsrvTag2(this.getRsrvTag2());
        resourceInterTradeData.setRsrvTag3(this.getRsrvTag3());
        resourceInterTradeData.setRsrvDate1(this.getRsrvDate1());
        resourceInterTradeData.setRsrvDate2(this.getRsrvDate2());
        resourceInterTradeData.setRsrvDate3(this.getRsrvDate3());
        resourceInterTradeData.setRsrvStr1(this.getRsrvStr1());
        resourceInterTradeData.setRsrvStr2(this.getRsrvStr2());
        resourceInterTradeData.setRsrvStr3(this.getRsrvStr3());
        resourceInterTradeData.setRsrvStr4(this.getRsrvStr4());
        resourceInterTradeData.setRsrvStr5(this.getRsrvStr5());
        resourceInterTradeData.setRsrvStr6(this.getRsrvStr6());
        resourceInterTradeData.setRsrvStr7(this.getRsrvStr7());
        resourceInterTradeData.setRsrvNum1(this.getRsrvNum1());
        resourceInterTradeData.setRsrvNum2(this.getRsrvNum2());
        resourceInterTradeData.setRsrvNum3(this.getRsrvNum3());

        return resourceInterTradeData;
    }

    public String getAgencyId()
    {
        return this.agencyId;
    }

    public String getAssignTime()
    {
        return this.assignTime;
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

    public String getCapacityTypeCode()
    {
        return this.capacityTypeCode;
    }

    public String getCityCode()
    {
        return this.cityCode;
    }

    public String getCostCenId()
    {
        return this.costCenId;
    }

    public String getDcTag()
    {
        return this.dcTag;
    }

    public String getDeviceColorCode()
    {
        return this.deviceColorCode;
    }

    public String getDeviceFactoryCode()
    {
        return this.deviceFactoryCode;
    }

    public String getDeviceModelCode()
    {
        return this.deviceModelCode;
    }

    public String getDeviceTypeCode()
    {
        return this.deviceTypeCode;
    }

    public String getEparchyCode()
    {
        return this.eparchyCode;
    }

    public String getInventOrgId()
    {
        return this.inventOrgId;
    }

    public String getLogId()
    {
        return this.logId;
    }

    public String getOperDepartId()
    {
        return this.operDepartId;
    }

    public String getOperNum()
    {
        return this.operNum;
    }

    public String getOperStaffId()
    {
        return this.operStaffId;
    }

    public String getOperTag()
    {
        return this.operTag;
    }

    public String getOperTime()
    {
        return this.operTime;
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

    public String getProcurementType()
    {
        return this.procurementType;
    }

    public String getProductId()
    {
        return this.productId;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public String getResKindCode()
    {
        return this.resKindCode;
    }

    public String getResState()
    {
        return this.resState;
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

    public String getStockIdN()
    {
        return this.stockIdN;
    }

    public String getStockIdO()
    {
        return this.stockIdO;
    }

    public String getSubLogId()
    {
        return this.subLogId;
    }

    public String getSupplyId()
    {
        return this.supplyId;
    }

    public String getSupplyType()
    {
        return this.supplyType;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_RESOURCE_INTER";
    }

    public void setAgencyId(String agencyId)
    {
        this.agencyId = agencyId;
    }

    public void setAssignTime(String assignTime)
    {
        this.assignTime = assignTime;
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

    public void setCapacityTypeCode(String capacityTypeCode)
    {
        this.capacityTypeCode = capacityTypeCode;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public void setCostCenId(String costCenId)
    {
        this.costCenId = costCenId;
    }

    public void setDcTag(String dcTag)
    {
        this.dcTag = dcTag;
    }

    public void setDeviceColorCode(String deviceColorCode)
    {
        this.deviceColorCode = deviceColorCode;
    }

    public void setDeviceFactoryCode(String deviceFactoryCode)
    {
        this.deviceFactoryCode = deviceFactoryCode;
    }

    public void setDeviceModelCode(String deviceModelCode)
    {
        this.deviceModelCode = deviceModelCode;
    }

    public void setDeviceTypeCode(String deviceTypeCode)
    {
        this.deviceTypeCode = deviceTypeCode;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setInventOrgId(String inventOrgId)
    {
        this.inventOrgId = inventOrgId;
    }

    public void setLogId(String logId)
    {
        this.logId = logId;
    }

    public void setOperDepartId(String operDepartId)
    {
        this.operDepartId = operDepartId;
    }

    public void setOperNum(String operNum)
    {
        this.operNum = operNum;
    }

    public void setOperStaffId(String operStaffId)
    {
        this.operStaffId = operStaffId;
    }

    public void setOperTag(String operTag)
    {
        this.operTag = operTag;
    }

    public void setOperTime(String operTime)
    {
        this.operTime = operTime;
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

    public void setProcurementType(String procurementType)
    {
        this.procurementType = procurementType;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setResKindCode(String resKindCode)
    {
        this.resKindCode = resKindCode;
    }

    public void setResState(String resState)
    {
        this.resState = resState;
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

    public void setStockIdN(String stockIdN)
    {
        this.stockIdN = stockIdN;
    }

    public void setStockIdO(String stockIdO)
    {
        this.stockIdO = stockIdO;
    }

    public void setSubLogId(String subLogId)
    {
        this.subLogId = subLogId;
    }

    public void setSupplyId(String supplyId)
    {
        this.supplyId = supplyId;
    }

    public void setSupplyType(String supplyType)
    {
        this.supplyType = supplyType;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("LOG_ID", this.logId);
        data.put("SUB_LOG_ID", this.subLogId);
        data.put("PARTITION_ID", this.partitionId);
        data.put("INVENT_ORG_ID", this.inventOrgId);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("CITY_CODE", this.cityCode);
        data.put("OPER_DEPART_ID", this.operDepartId);
        data.put("OPER_STAFF_ID", this.operStaffId);
        data.put("STOCK_ID_O", this.stockIdO);
        data.put("STOCK_ID_N", this.stockIdN);
        data.put("OPER_TAG", this.operTag);
        data.put("COST_CEN_ID", this.costCenId);
        data.put("OPER_TIME", this.operTime);
        data.put("ASSIGN_TIME", this.assignTime);
        data.put("RES_TYPE_CODE", this.resTypeCode);
        data.put("RES_KIND_CODE", this.resKindCode);
        data.put("CAPACITY_TYPE_CODE", this.capacityTypeCode);
        data.put("DEVICE_TYPE_CODE", this.deviceTypeCode);
        data.put("DEVICE_FACTORY_CODE", this.deviceFactoryCode);
        data.put("DEVICE_MODEL_CODE", this.deviceModelCode);
        data.put("DEVICE_COLOR_CODE", this.deviceColorCode);
        data.put("ORDER_ID", this.orderId);
        data.put("SUPPLY_ID", this.supplyId);
        data.put("SUPPLY_TYPE", this.supplyType);
        data.put("AGENCY_ID", this.agencyId);
        data.put("PROCUREMENT_TYPE", this.procurementType);
        data.put("RES_STATE", this.resState);
        data.put("OPER_TYPE_CODE", this.operTypeCode);
        data.put("CAMPN_ID", this.campnId);
        data.put("PRODUCT_ID", this.productId);
        data.put("SALE_TYPE_CODE", this.saleTypeCode);
        data.put("OPER_NUM", this.operNum);
        data.put("DC_TAG", this.dcTag);
        data.put("CANCEL_SUB_LOG_ID", this.cancelSubLogId);
        data.put("CANCEL_DATE", this.cancelDate);
        data.put("REMARK", this.remark);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
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

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
