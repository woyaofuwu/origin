
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;

public class SaleGoodsTradeData extends ProductModuleTradeData
{
    private String cancelDate;

    private String destroyFlag;

    private String deviceBrand;

    private String deviceBrandCode;

    private String deviceCost;

    private String deviceModel;

    private String deviceModelCode;

    private String giftMode;

    private String goodsName;

    private String goodsNum;

    private String goodsState;

    private String goodsValue;

    private String postAddress;

    private String postCode;

    private String postName;

    private String relationTradeId;

    private String remark;

    private String resCode;

    private String resId;

    private String resTag;

    private String resTypeCode;

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

    private String rsrvStr2;

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

    private String serialNumberB;

    public SaleGoodsTradeData()
    {
        this.elementType = BofConst.ELEMENT_TYPE_CODE_SALEGOODS;
    }

    public SaleGoodsTradeData(IData data)
    {
        this.elementType = BofConst.ELEMENT_TYPE_CODE_SALEGOODS;
        this.campnId = data.getString("CAMPN_ID");
        this.cancelDate = data.getString("CANCEL_DATE");
        this.destroyFlag = data.getString("DESTROY_FLAG");
        this.deviceBrand = data.getString("DEVICE_BRAND");
        this.deviceBrandCode = data.getString("DEVICE_BRAND_CODE");
        this.deviceCost = data.getString("DEVICE_COST");
        this.deviceModel = data.getString("DEVICE_MODEL");
        this.deviceModelCode = data.getString("DEVICE_MODEL_CODE");
        this.giftMode = data.getString("GIFT_MODE");
        this.elementId = data.getString("GOODS_ID", data.getString("ELEMENT_ID"));
        this.goodsName = data.getString("GOODS_NAME");
        this.goodsNum = data.getString("GOODS_NUM");
        this.goodsState = data.getString("GOODS_STATE");
        this.goodsValue = data.getString("GOODS_VALUE");
        this.instId = data.getString("INST_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.packageId = data.getString("PACKAGE_ID");
        this.postAddress = data.getString("POST_ADDRESS");
        this.postCode = data.getString("POST_CODE");
        this.postName = data.getString("POST_NAME");
        this.productId = data.getString("PRODUCT_ID");
        this.relationTradeId = data.getString("RELATION_TRADE_ID");
        this.remark = data.getString("REMARK");
        this.resCode = data.getString("RES_CODE");
        this.resId = data.getString("RES_ID");
        this.resTag = data.getString("RES_TAG");
        this.resTypeCode = data.getString("RES_TYPE_CODE");
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
        this.rsrvStr2 = data.getString("RSRV_STR2");
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
        this.serialNumberB = data.getString("SERIAL_NUMBER_B");
        this.userId = data.getString("USER_ID");
    }

    public SaleGoodsTradeData clone()
    {
        SaleGoodsTradeData saleGoodsTradeData = new SaleGoodsTradeData();
        saleGoodsTradeData.setCampnId(this.getCampnId());
        saleGoodsTradeData.setCancelDate(this.getCancelDate());
        saleGoodsTradeData.setDestroyFlag(this.getDestroyFlag());
        saleGoodsTradeData.setDeviceBrand(this.getDeviceBrand());
        saleGoodsTradeData.setDeviceBrandCode(this.getDeviceBrandCode());
        saleGoodsTradeData.setDeviceCost(this.getDeviceCost());
        saleGoodsTradeData.setDeviceModel(this.getDeviceModel());
        saleGoodsTradeData.setDeviceModelCode(this.getDeviceModelCode());
        saleGoodsTradeData.setGiftMode(this.getGiftMode());
        saleGoodsTradeData.setElementId(this.getElementId());
        saleGoodsTradeData.setGoodsName(this.getGoodsName());
        saleGoodsTradeData.setGoodsNum(this.getGoodsNum());
        saleGoodsTradeData.setGoodsState(this.getGoodsState());
        saleGoodsTradeData.setGoodsValue(this.getGoodsValue());
        saleGoodsTradeData.setInstId(this.getInstId());
        saleGoodsTradeData.setModifyTag(this.getModifyTag());
        saleGoodsTradeData.setPackageId(this.getPackageId());
        saleGoodsTradeData.setPostAddress(this.getPostAddress());
        saleGoodsTradeData.setPostCode(this.getPostCode());
        saleGoodsTradeData.setPostName(this.getPostName());
        saleGoodsTradeData.setProductId(this.getProductId());
        saleGoodsTradeData.setRelationTradeId(this.getRelationTradeId());
        saleGoodsTradeData.setRemark(this.getRemark());
        saleGoodsTradeData.setResCode(this.getResCode());
        saleGoodsTradeData.setResId(this.getResId());
        saleGoodsTradeData.setResTag(this.getResTag());
        saleGoodsTradeData.setResTypeCode(this.getResTypeCode());
        saleGoodsTradeData.setRsrvDate1(this.getRsrvDate1());
        saleGoodsTradeData.setRsrvDate2(this.getRsrvDate2());
        saleGoodsTradeData.setRsrvDate3(this.getRsrvDate3());
        saleGoodsTradeData.setRsrvNum1(this.getRsrvNum1());
        saleGoodsTradeData.setRsrvNum2(this.getRsrvNum2());
        saleGoodsTradeData.setRsrvNum3(this.getRsrvNum3());
        saleGoodsTradeData.setRsrvNum4(this.getRsrvNum4());
        saleGoodsTradeData.setRsrvNum5(this.getRsrvNum5());
        saleGoodsTradeData.setRsrvStr1(this.getRsrvStr1());
        saleGoodsTradeData.setRsrvStr10(this.getRsrvStr10());
        saleGoodsTradeData.setRsrvStr2(this.getRsrvStr2());
        saleGoodsTradeData.setRsrvStr3(this.getRsrvStr3());
        saleGoodsTradeData.setRsrvStr4(this.getRsrvStr4());
        saleGoodsTradeData.setRsrvStr5(this.getRsrvStr5());
        saleGoodsTradeData.setRsrvStr6(this.getRsrvStr6());
        saleGoodsTradeData.setRsrvStr7(this.getRsrvStr7());
        saleGoodsTradeData.setRsrvStr8(this.getRsrvStr8());
        saleGoodsTradeData.setRsrvStr9(this.getRsrvStr9());
        saleGoodsTradeData.setRsrvTag1(this.getRsrvTag1());
        saleGoodsTradeData.setRsrvTag2(this.getRsrvTag2());
        saleGoodsTradeData.setRsrvTag3(this.getRsrvTag3());
        saleGoodsTradeData.setSerialNumberB(this.getSerialNumberB());
        saleGoodsTradeData.setUserId(this.getUserId());
        return saleGoodsTradeData;
    }

    public String getCancelDate()
    {
        return cancelDate;
    }

    public String getDestroyFlag()
    {
        return destroyFlag;
    }

    public String getDeviceBrand()
    {
        return deviceBrand;
    }

    public String getDeviceBrandCode()
    {
        return deviceBrandCode;
    }

    public String getDeviceCost()
    {
        return deviceCost;
    }

    public String getDeviceModel()
    {
        return deviceModel;
    }

    public String getDeviceModelCode()
    {
        return deviceModelCode;
    }

    public String getGiftMode()
    {
        return giftMode;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public String getGoodsNum()
    {
        return goodsNum;
    }

    public String getGoodsState()
    {
        return goodsState;
    }

    public String getGoodsValue()
    {
        return goodsValue;
    }

    public String getPostAddress()
    {
        return postAddress;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public String getPostName()
    {
        return postName;
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

    public String getResId()
    {
        return resId;
    }

    public String getResTag()
    {
        return resTag;
    }

    public String getResTypeCode()
    {
        return resTypeCode;
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

    public String getSerialNumberB()
    {
        return serialNumberB;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_SALE_GOODS";
    }

    public void setCancelDate(String cancelDate)
    {
        this.cancelDate = cancelDate;
    }

    public void setDestroyFlag(String destroyFlag)
    {
        this.destroyFlag = destroyFlag;
    }

    public void setDeviceBrand(String deviceBrand)
    {
        this.deviceBrand = deviceBrand;
    }

    public void setDeviceBrandCode(String deviceBrandCode)
    {
        this.deviceBrandCode = deviceBrandCode;
    }

    public void setDeviceCost(String deviceCost)
    {
        this.deviceCost = deviceCost;
    }

    public void setDeviceModel(String deviceModel)
    {
        this.deviceModel = deviceModel;
    }

    public void setDeviceModelCode(String deviceModelCode)
    {
        this.deviceModelCode = deviceModelCode;
    }

    public void setGiftMode(String giftMode)
    {
        this.giftMode = giftMode;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public void setGoodsNum(String goodsNum)
    {
        this.goodsNum = goodsNum;
    }

    public void setGoodsState(String goodsState)
    {
        this.goodsState = goodsState;
    }

    public void setGoodsValue(String goodsValue)
    {
        this.goodsValue = goodsValue;
    }

    public void setPostAddress(String postAddress)
    {
        this.postAddress = postAddress;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public void setPostName(String postName)
    {
        this.postName = postName;
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

    public void setResId(String resId)
    {
        this.resId = resId;
    }

    public void setResTag(String resTag)
    {
        this.resTag = resTag;
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

    public void setSerialNumberB(String serialNumberB)
    {
        this.serialNumberB = serialNumberB;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("CAMPN_ID", this.campnId);
        data.put("CANCEL_DATE", this.cancelDate);
        data.put("DESTROY_FLAG", this.destroyFlag);
        data.put("DEVICE_BRAND", this.deviceBrand);
        data.put("DEVICE_BRAND_CODE", this.deviceBrandCode);
        data.put("DEVICE_COST", this.deviceCost);
        data.put("DEVICE_MODEL", this.deviceModel);
        data.put("DEVICE_MODEL_CODE", this.deviceModelCode);
        data.put("GIFT_MODE", this.giftMode);
        data.put("GOODS_ID", this.elementId);
        data.put("GOODS_NAME", this.goodsName);
        data.put("GOODS_NUM", this.goodsNum);
        data.put("GOODS_STATE", this.goodsState);
        data.put("GOODS_VALUE", this.goodsValue);
        data.put("INST_ID", this.instId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("PACKAGE_ID", this.packageId);
        data.put("POST_ADDRESS", this.postAddress);
        data.put("POST_CODE", this.postCode);
        data.put("POST_NAME", this.postName);
        data.put("PRODUCT_ID", this.productId);
        data.put("RELATION_TRADE_ID", this.relationTradeId);
        data.put("REMARK", this.remark);
        data.put("RES_CODE", this.resCode);
        data.put("RES_ID", this.resId);
        data.put("RES_TAG", this.resTag);
        data.put("RES_TYPE_CODE", this.resTypeCode);
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
        data.put("RSRV_STR2", this.rsrvStr2);
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
        data.put("SERIAL_NUMBER_B", this.serialNumberB);
        data.put("USER_ID", this.userId);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
