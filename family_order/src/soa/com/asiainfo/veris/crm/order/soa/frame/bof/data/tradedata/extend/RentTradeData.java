
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class RentTradeData extends BaseTradeData
{
    private String brandCode;

    private String endDate;

    private String imsi;

    private String modifyTag;

    private String nationalityAreacode;

    private String netTypeCode;

    private String openDepartId;

    private String openStaffId;

    private String productId;

    private String remark;

    private String rentDeviceNo;

    private String rentImei;

    private String rentImsi;

    private String rentSerialNumber;

    private String rentSimCard;

    private String rentTag;

    private String rentTypeCode;

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

    private String serialNumber;

    private String simCardNo;

    private String startDate;

    private String userId;

    private String instId;

    public RentTradeData()
    {

    }

    public RentTradeData(IData data)
    {
        this.brandCode = data.getString("BRAND_CODE");
        this.endDate = data.getString("END_DATE");
        this.imsi = data.getString("IMSI");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.nationalityAreacode = data.getString("NATIONALITY_AREACODE");
        this.netTypeCode = data.getString("NET_TYPE_CODE");
        this.openDepartId = data.getString("OPEN_DEPART_ID");
        this.openStaffId = data.getString("OPEN_STAFF_ID");
        this.productId = data.getString("PRODUCT_ID");
        this.remark = data.getString("REMARK");
        this.rentDeviceNo = data.getString("RENT_DEVICE_NO");
        this.rentImei = data.getString("RENT_IMEI");
        this.rentImsi = data.getString("RENT_IMSI");
        this.rentSerialNumber = data.getString("RENT_SERIAL_NUMBER");
        this.rentSimCard = data.getString("RENT_SIM_CARD");
        this.rentTag = data.getString("RENT_TAG");
        this.rentTypeCode = data.getString("RENT_TYPE_CODE");
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
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.simCardNo = data.getString("SIM_CARD_NO");
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.instId = data.getString("INST_ID");
    }

    public RentTradeData clone()
    {
        RentTradeData rentTradeData = new RentTradeData();
        rentTradeData.setBrandCode(this.getBrandCode());
        rentTradeData.setEndDate(this.getEndDate());
        rentTradeData.setImsi(this.getImsi());
        rentTradeData.setModifyTag(this.getModifyTag());
        rentTradeData.setNationalityAreacode(this.getNationalityAreacode());
        rentTradeData.setNetTypeCode(this.getNetTypeCode());
        rentTradeData.setOpenDepartId(this.getOpenDepartId());
        rentTradeData.setOpenStaffId(this.getOpenStaffId());
        rentTradeData.setProductId(this.getProductId());
        rentTradeData.setRemark(this.getRemark());
        rentTradeData.setRentDeviceNo(this.getRentDeviceNo());
        rentTradeData.setRentImei(this.getRentImei());
        rentTradeData.setRentImsi(this.getRentImsi());
        rentTradeData.setRentSerialNumber(this.getRentSerialNumber());
        rentTradeData.setRentSimCard(this.getRentSimCard());
        rentTradeData.setRentTag(this.getRentTag());
        rentTradeData.setRentTypeCode(this.getRentTypeCode());
        rentTradeData.setRsrvDate1(this.getRsrvDate1());
        rentTradeData.setRsrvDate2(this.getRsrvDate2());
        rentTradeData.setRsrvDate3(this.getRsrvDate3());
        rentTradeData.setRsrvNum1(this.getRsrvNum1());
        rentTradeData.setRsrvNum2(this.getRsrvNum2());
        rentTradeData.setRsrvNum3(this.getRsrvNum3());
        rentTradeData.setRsrvNum4(this.getRsrvNum4());
        rentTradeData.setRsrvNum5(this.getRsrvNum5());
        rentTradeData.setRsrvStr1(this.getRsrvStr1());
        rentTradeData.setRsrvStr2(this.getRsrvStr2());
        rentTradeData.setRsrvStr3(this.getRsrvStr3());
        rentTradeData.setRsrvStr4(this.getRsrvStr4());
        rentTradeData.setRsrvStr5(this.getRsrvStr5());
        rentTradeData.setRsrvTag1(this.getRsrvTag1());
        rentTradeData.setRsrvTag2(this.getRsrvTag2());
        rentTradeData.setRsrvTag3(this.getRsrvTag3());
        rentTradeData.setSerialNumber(this.getSerialNumber());
        rentTradeData.setSimCardNo(this.getSimCardNo());
        rentTradeData.setStartDate(this.getStartDate());
        rentTradeData.setUserId(this.getUserId());
        rentTradeData.setInstId(this.getInstId());
        return rentTradeData;
    }

    public String getBrandCode()
    {
        return brandCode;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getImsi()
    {
        return imsi;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getNationalityAreacode()
    {
        return nationalityAreacode;
    }

    public String getNetTypeCode()
    {
        return netTypeCode;
    }

    public String getOpenDepartId()
    {
        return openDepartId;
    }

    public String getOpenStaffId()
    {
        return openStaffId;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRentDeviceNo()
    {
        return rentDeviceNo;
    }

    public String getRentImei()
    {
        return rentImei;
    }

    public String getRentImsi()
    {
        return rentImsi;
    }

    public String getRentSerialNumber()
    {
        return rentSerialNumber;
    }

    public String getRentSimCard()
    {
        return rentSimCard;
    }

    public String getRentTag()
    {
        return rentTag;
    }

    public String getRentTypeCode()
    {
        return rentTypeCode;
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

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getSimCardNo()
    {
        return simCardNo;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_RENT";
    }

    public String getUserId()
    {
        return userId;
    }

    public void setBrandCode(String brandCode)
    {
        this.brandCode = brandCode;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setImsi(String imsi)
    {
        this.imsi = imsi;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setNationalityAreacode(String nationalityAreacode)
    {
        this.nationalityAreacode = nationalityAreacode;
    }

    public void setNetTypeCode(String netTypeCode)
    {
        this.netTypeCode = netTypeCode;
    }

    public void setOpenDepartId(String openDepartId)
    {
        this.openDepartId = openDepartId;
    }

    public void setOpenStaffId(String openStaffId)
    {
        this.openStaffId = openStaffId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRentDeviceNo(String rentDeviceNo)
    {
        this.rentDeviceNo = rentDeviceNo;
    }

    public void setRentImei(String rentImei)
    {
        this.rentImei = rentImei;
    }

    public void setRentImsi(String rentImsi)
    {
        this.rentImsi = rentImsi;
    }

    public void setRentSerialNumber(String rentSerialNumber)
    {
        this.rentSerialNumber = rentSerialNumber;
    }

    public void setRentSimCard(String rentSimCard)
    {
        this.rentSimCard = rentSimCard;
    }

    public void setRentTag(String rentTag)
    {
        this.rentTag = rentTag;
    }

    public void setRentTypeCode(String rentTypeCode)
    {
        this.rentTypeCode = rentTypeCode;
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

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setSimCardNo(String simCardNo)
    {
        this.simCardNo = simCardNo;
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

        data.put("BRAND_CODE", this.brandCode);
        data.put("END_DATE", this.endDate);
        data.put("IMSI", this.imsi);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("NATIONALITY_AREACODE", this.nationalityAreacode);
        data.put("NET_TYPE_CODE", this.netTypeCode);
        data.put("OPEN_DEPART_ID", this.openDepartId);
        data.put("OPEN_STAFF_ID", this.openStaffId);
        data.put("PRODUCT_ID", this.productId);
        data.put("REMARK", this.remark);
        data.put("RENT_DEVICE_NO", this.rentDeviceNo);
        data.put("RENT_IMEI", this.rentImei);
        data.put("RENT_IMSI", this.rentImsi);
        data.put("RENT_SERIAL_NUMBER", this.rentSerialNumber);
        data.put("RENT_SIM_CARD", this.rentSimCard);
        data.put("RENT_TAG", this.rentTag);
        data.put("RENT_TYPE_CODE", this.rentTypeCode);
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
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("SIM_CARD_NO", this.simCardNo);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("INST_ID", this.instId);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
