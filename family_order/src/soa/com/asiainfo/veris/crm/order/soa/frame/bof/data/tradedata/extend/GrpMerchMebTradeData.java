
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class GrpMerchMebTradeData extends BaseTradeData
{
    private String instId;

    private String userId;

    private String serilaNumber;

    private String serviceId;

    private String ecUserId;

    private String scSerilaNumber;

    private String productOradeId;

    private String productOfferId;

    private String statu;

    private String startDate;

    private String endDate;

    private String modifyTag;

    private String updateTime;

    private String updateStaffId;

    private String updateDepartId;

    private String remark;

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

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    public GrpMerchMebTradeData()
    {

    }

    public GrpMerchMebTradeData(IData data)
    {
        this.instId = data.getString("INST_ID");
        this.userId = data.getString("USER_ID");
        this.serilaNumber = data.getString("SERIAL_NUMBER");
        this.serviceId = data.getString("SERVICE_ID");
        this.ecUserId = data.getString("EC_USER_ID");
        this.scSerilaNumber = data.getString("EC_SERIAL_NUMBER");
        this.productOradeId = data.getString("PRODUCT_ORDER_ID");
        this.productOfferId = data.getString("PRODUCT_OFFER_ID");
        this.statu = data.getString("STATUS");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.updateTime = data.getString("UPDATE_TIME");
        this.updateStaffId = data.getString("UPDATE_STAFF_ID");
        this.updateDepartId = data.getString("UPDATE_DEPART_ID");
        this.remark = data.getString("REMARK");
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
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");

    }

    public GrpMerchMebTradeData clone()
    {
        GrpMerchMebTradeData grpMerchMebTradeData = new GrpMerchMebTradeData();
        grpMerchMebTradeData.setInstId(this.getInstId());
        grpMerchMebTradeData.setUserId(this.getUserId());
        grpMerchMebTradeData.setSerilaNumber(this.getSerilaNumber());
        grpMerchMebTradeData.setServiceId(this.getServiceId());
        grpMerchMebTradeData.setEcUserId(this.getEcUserId());
        grpMerchMebTradeData.setScSerilaNumber(this.getScSerilaNumber());
        grpMerchMebTradeData.setProductOradeId(this.getProductOradeId());
        grpMerchMebTradeData.setProductOfferId(this.getProductOfferId());
        grpMerchMebTradeData.setStatu(this.getStatu());
        grpMerchMebTradeData.setStartDate(this.getStartDate());
        grpMerchMebTradeData.setEndDate(this.getEndDate());
        grpMerchMebTradeData.setModifyTag(this.getModifyTag());
        grpMerchMebTradeData.setUpdateTime(this.getUpdateTime());
        grpMerchMebTradeData.setUpdateStaffId(this.getUpdateStaffId());
        grpMerchMebTradeData.setUpdateDepartId(this.getUpdateDepartId());
        grpMerchMebTradeData.setRemark(this.getRemark());
        grpMerchMebTradeData.setRsrvNum1(this.getRsrvNum1());
        grpMerchMebTradeData.setRsrvNum2(this.getRsrvNum2());
        grpMerchMebTradeData.setRsrvNum3(this.getRsrvNum3());
        grpMerchMebTradeData.setRsrvNum4(this.getRsrvNum4());
        grpMerchMebTradeData.setRsrvNum5(this.getRsrvNum5());
        grpMerchMebTradeData.setRsrvStr1(this.getRsrvStr1());
        grpMerchMebTradeData.setRsrvStr2(this.getRsrvStr2());
        grpMerchMebTradeData.setRsrvStr3(this.getRsrvStr3());
        grpMerchMebTradeData.setRsrvStr4(this.getRsrvStr4());
        grpMerchMebTradeData.setRsrvStr5(this.getRsrvStr5());
        grpMerchMebTradeData.setRsrvDate1(this.getRsrvDate1());
        grpMerchMebTradeData.setRsrvDate2(this.getRsrvDate2());
        grpMerchMebTradeData.setRsrvDate3(this.getRsrvDate3());
        grpMerchMebTradeData.setRsrvTag1(this.getRsrvTag1());
        grpMerchMebTradeData.setRsrvTag2(this.getRsrvTag2());
        grpMerchMebTradeData.setRsrvTag3(this.getRsrvTag3());
        return grpMerchMebTradeData;
    }

    public String getEcUserId()
    {
        return ecUserId;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getProductOfferId()
    {
        return productOfferId;
    }

    public String getProductOradeId()
    {
        return productOradeId;
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

    public String getScSerilaNumber()
    {
        return scSerilaNumber;
    }

    public String getSerilaNumber()
    {
        return serilaNumber;
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getStatu()
    {
        return statu;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_GRP_MERCH_MEB";
    }

    public String getUpdateDepartId()
    {
        return updateDepartId;
    }

    public String getUpdateStaffId()
    {
        return updateStaffId;
    }

    public String getUpdateTime()
    {
        return updateTime;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setEcUserId(String ecUserId)
    {
        this.ecUserId = ecUserId;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setProductOfferId(String productOfferId)
    {
        this.productOfferId = productOfferId;
    }

    public void setProductOradeId(String productOradeId)
    {
        this.productOradeId = productOradeId;
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

    public void setScSerilaNumber(String scSerilaNumber)
    {
        this.scSerilaNumber = scSerilaNumber;
    }

    public void setSerilaNumber(String serilaNumber)
    {
        this.serilaNumber = serilaNumber;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setStatu(String statu)
    {
        this.statu = statu;
    }

    public void setUpdateDepartId(String updateDepartId)
    {
        this.updateDepartId = updateDepartId;
    }

    public void setUpdateStaffId(String updateStaffId)
    {
        this.updateStaffId = updateStaffId;
    }

    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("INST_ID", this.instId);
        data.put("USER_ID", this.userId);
        data.put("SERIAL_NUMBER", this.serilaNumber);
        data.put("SERVICE_ID", this.serviceId);
        data.put("EC_USER_ID", this.ecUserId);
        data.put("EC_SERIAL_NUMBER", this.scSerilaNumber);
        data.put("PRODUCT_ORDER_ID", this.productOradeId);
        data.put("PRODUCT_OFFER_ID", this.productOfferId);
        data.put("STATUS", this.statu);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("UPDATE_TIME", this.updateTime);
        data.put("UPDATE_STAFF_ID", this.updateStaffId);
        data.put("UPDATE_DEPART_ID", this.updateDepartId);
        data.put("REMARK", this.remark);
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
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
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
