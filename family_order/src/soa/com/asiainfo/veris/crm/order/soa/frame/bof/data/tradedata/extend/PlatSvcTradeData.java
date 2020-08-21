
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;

public class PlatSvcTradeData extends ProductModuleTradeData
{
    private String activeTag;

    private String allTag;

    private String bizStateCode;

    private String endDate;

    private String entityCardNo;

    private String firstDate;

    private String firstDateMon;

    private String giftSerialNumber;

    private String giftUserId;

    private String inCardNo;

    private String intfTradeId;

    private String isNeedPf;

    private String operCode;

    private String operTime;

    private String oprSource;

    private String packageId;

    private String pkgSeq;

    private String productId;

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

    private String subscribeId;

    private String udsum;

    public PlatSvcTradeData()
    {
        this.elementType = BofConst.ELEMENT_TYPE_CODE_PLATSVC;
    }

    public PlatSvcTradeData(IData data)
    {
        this.setAllTag(data.getString("ALL_TAG"));
        this.setActiveTag(data.getString("ACTIVE_TAG"));
        this.setIntfTradeId(data.getString("INTF_TRADE_ID"));
        this.setIsNeedPf(data.getString("IS_NEED_PF"));
        this.setOperCode(data.getString("OPER_CODE"));
        this.setOprSource(data.getString("OPR_SOURCE"));
        this.setPkgSeq(data.getString("PKGSEQ"));
        this.setOperTime(data.getString("OPER_TIME"));
        this.setSubscribeId(data.getString("SUBSCRIBE_ID"));
        this.setUdsum(data.getString("UDSUM"));
        this.setBizStateCode(data.getString("BIZ_STATE_CODE"));
        this.setEndDate(data.getString("END_DATE"));
        this.setEntityCardNo(data.getString("ENTITY_CARD_NO"));
        this.setFirstDate(data.getString("FIRST_DATE"));
        this.setFirstDateMon(data.getString("FIRST_DATE_MON"));
        this.setGiftSerialNumber(data.getString("GIFT_SERIAL_NUMBER"));
        this.setGiftUserId(data.getString("GIFT_USER_ID"));
        this.setInstId(data.getString("INST_ID"));
        this.setInCardNo(data.getString("IN_CARD_NO"));
        this.setModifyTag(data.getString("MODIFY_TAG"));
        this.setPackageId(data.getString("PACKAGE_ID"));
        this.setProductId(data.getString("PRODUCT_ID"));
        this.setRemark(data.getString("REMARK"));
        this.setRsrvDate1(data.getString("RSRV_DATE1"));
        this.setRsrvDate2(data.getString("RSRV_DATE2"));
        this.setRsrvDate3(data.getString("RSRV_DATE3"));
        this.setRsrvNum1(data.getString("RSRV_NUM1"));
        this.setRsrvNum2(data.getString("RSRV_NUM2"));
        this.setRsrvNum3(data.getString("RSRV_NUM3"));
        this.setRsrvNum4(data.getString("RSRV_NUM4"));
        this.setRsrvNum5(data.getString("RSRV_NUM5"));
        this.setRsrvStr1(data.getString("RSRV_STR1"));
        this.setRsrvStr10(data.getString("RSRV_STR10"));
        this.setRsrvStr2(data.getString("RSRV_STR2"));
        this.setRsrvStr3(data.getString("RSRV_STR3"));
        this.setRsrvStr4(data.getString("RSRV_STR4"));
        this.setRsrvStr5(data.getString("RSRV_STR5"));
        this.setRsrvStr6(data.getString("RSRV_STR6"));
        this.setRsrvStr7(data.getString("RSRV_STR7"));
        this.setRsrvStr8(data.getString("RSRV_STR8"));
        this.setRsrvStr9(data.getString("RSRV_STR9"));
        this.setRsrvTag1(data.getString("RSRV_TAG1"));
        this.setRsrvTag2(data.getString("RSRV_TAG2"));
        this.setRsrvTag3(data.getString("RSRV_TAG3"));
        if (StringUtils.isNotBlank(data.getString("SERVICE_ID")))
        {
            this.elementId = data.getString("SERVICE_ID");
        }
        else
        {
            this.elementId = data.getString("ELEMENT_ID");
        }
        this.setStartDate(data.getString("START_DATE"));
        this.setUserId(data.getString("USER_ID"));
    }

    @Override
    public PlatSvcTradeData clone()
    {
        PlatSvcTradeData platSvcTradeData = new PlatSvcTradeData();
        platSvcTradeData.setBizStateCode(this.getBizStateCode());
        platSvcTradeData.setEndDate(this.getEndDate());
        platSvcTradeData.setEntityCardNo(this.getEntityCardNo());
        platSvcTradeData.setFirstDate(this.getFirstDate());
        platSvcTradeData.setFirstDateMon(this.getFirstDateMon());
        platSvcTradeData.setGiftSerialNumber(this.getGiftSerialNumber());
        platSvcTradeData.setGiftUserId(this.getGiftUserId());
        platSvcTradeData.setInstId(this.getInstId());
        platSvcTradeData.setInCardNo(this.getInCardNo());
        platSvcTradeData.setModifyTag(this.getModifyTag());
        platSvcTradeData.setPackageId(this.getPackageId());
        platSvcTradeData.setProductId(this.getProductId());
        platSvcTradeData.setRemark(this.getRemark());
        platSvcTradeData.setRsrvDate1(this.getRsrvDate1());
        platSvcTradeData.setRsrvDate2(this.getRsrvDate2());
        platSvcTradeData.setRsrvDate3(this.getRsrvDate3());
        platSvcTradeData.setRsrvNum1(this.getRsrvNum1());
        platSvcTradeData.setRsrvNum2(this.getRsrvNum2());
        platSvcTradeData.setRsrvNum3(this.getRsrvNum3());
        platSvcTradeData.setRsrvNum4(this.getRsrvNum4());
        platSvcTradeData.setRsrvNum5(this.getRsrvNum5());
        platSvcTradeData.setRsrvStr1(this.getRsrvStr1());
        platSvcTradeData.setRsrvStr10(this.getRsrvStr10());
        platSvcTradeData.setRsrvStr2(this.getRsrvStr2());
        platSvcTradeData.setRsrvStr3(this.getRsrvStr3());
        platSvcTradeData.setRsrvStr4(this.getRsrvStr4());
        platSvcTradeData.setRsrvStr5(this.getRsrvStr5());
        platSvcTradeData.setRsrvStr6(this.getRsrvStr6());
        platSvcTradeData.setRsrvStr7(this.getRsrvStr7());
        platSvcTradeData.setRsrvStr8(this.getRsrvStr8());
        platSvcTradeData.setRsrvStr9(this.getRsrvStr9());
        platSvcTradeData.setRsrvTag1(this.getRsrvTag1());
        platSvcTradeData.setRsrvTag2(this.getRsrvTag2());
        platSvcTradeData.setRsrvTag3(this.getRsrvTag3());
        platSvcTradeData.setElementId(this.getElementId());
        platSvcTradeData.setStartDate(this.getStartDate());
        platSvcTradeData.setUserId(this.getUserId());
        platSvcTradeData.setActiveTag(this.getActiveTag());
        platSvcTradeData.setAllTag(this.getAllTag());
        platSvcTradeData.setIntfTradeId(this.getIntfTradeId());
        platSvcTradeData.setIsNeedPf(this.getIsNeedPf());
        platSvcTradeData.setOperCode(this.getOperCode());
        platSvcTradeData.setOprSource(this.getOprSource());
        platSvcTradeData.setPkgSeq(this.getPkgSeq());
        platSvcTradeData.setOperTime(this.getOperTime());
        platSvcTradeData.setSubscribeId(this.getSubscribeId());
        platSvcTradeData.setUdsum(this.getUdsum());
        return platSvcTradeData;
    }

    public String getActiveTag()
    {
        return activeTag;
    }

    public String getAllTag()
    {
        return allTag;
    }

    public String getBizStateCode()
    {
        return bizStateCode;
    }

    @Override
    public String getEndDate()
    {
        return endDate;
    }

    public String getEntityCardNo()
    {
        return entityCardNo;
    }

    public String getFirstDate()
    {
        return firstDate;
    }

    public String getFirstDateMon()
    {
        return firstDateMon;
    }

    public String getGiftSerialNumber()
    {
        return giftSerialNumber;
    }

    public String getGiftUserId()
    {
        return giftUserId;
    }

    public String getInCardNo()
    {
        return inCardNo;
    }

    @Override
    public String getInstId()
    {
        return instId;
    }

    public String getIntfTradeId()
    {
        return intfTradeId;
    }

    public String getIsNeedPf()
    {
        return isNeedPf;
    }

    @Override
    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getOperCode()
    {
        return operCode;
    }

    public String getOperTime()
    {
        return operTime;
    }

    public String getOprSource()
    {
        return oprSource;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public String getPkgSeq()
    {
        return pkgSeq;
    }

    public String getProductId()
    {
        return productId;
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

    @Override
    public String getStartDate()
    {
        return startDate;
    }

    public String getSubscribeId()
    {
        return subscribeId;
    }

    @Override
    public String getTableName()
    {
        // TODO Auto-generated method stub
        return "TF_B_TRADE_PLATSVC";
    }

    public String getUdsum()
    {
        return udsum;
    }

    @Override
    public String getUserId()
    {
        return userId;
    }

    public void setActiveTag(String activeTag)
    {
        this.activeTag = activeTag;
    }

    public void setAllTag(String allTag)
    {
        this.allTag = allTag;
    }

    public void setBizStateCode(String bizStateCode)
    {
        this.bizStateCode = bizStateCode;
    }

    @Override
    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setEntityCardNo(String entityCardNo)
    {
        this.entityCardNo = entityCardNo;
    }

    public void setFirstDate(String firstDate)
    {
        this.firstDate = firstDate;
    }

    public void setFirstDateMon(String firstDateMon)
    {
        this.firstDateMon = firstDateMon;
    }

    public void setGiftSerialNumber(String giftSerialNumber)
    {
        this.giftSerialNumber = giftSerialNumber;
    }

    public void setGiftUserId(String giftUserId)
    {
        this.giftUserId = giftUserId;
    }

    public void setInCardNo(String inCardNo)
    {
        this.inCardNo = inCardNo;
    }

    @Override
    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setIntfTradeId(String intfTradeId)
    {
        this.intfTradeId = intfTradeId;
    }

    public void setIsNeedPf(String isNeedPf)
    {
        this.isNeedPf = isNeedPf;
    }

    @Override
    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public void setOperTime(String operTime)
    {
        this.operTime = operTime;
    }

    public void setOprSource(String oprSource)
    {
        this.oprSource = oprSource;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setPkgSeq(String pkgSeq)
    {
        this.pkgSeq = pkgSeq;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
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

    @Override
    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setSubscribeId(String subscribeId)
    {
        this.subscribeId = subscribeId;
    }

    public void setUdsum(String udsum)
    {
        this.udsum = udsum;
    }

    @Override
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    @Override
    public IData toData()
    {
        IData data = new DataMap();
        data.put("ALL_TAG", this.allTag);
        data.put("ACTIVE_TAG", this.activeTag);
        data.put("INTF_TRADE_ID", this.intfTradeId);
        data.put("IS_NEED_PF", this.isNeedPf);
        data.put("OPER_CODE", this.operCode);
        data.put("OPR_SOURCE", this.oprSource);
        data.put("PKGSEQ", this.pkgSeq);
        data.put("OPER_TIME", this.operTime);
        data.put("SUBSCRIBE_ID", this.subscribeId);
        data.put("UDSUM", this.udsum);
        data.put("BIZ_STATE_CODE", this.bizStateCode);
        data.put("END_DATE", this.endDate);
        data.put("ENTITY_CARD_NO", this.entityCardNo);
        data.put("FIRST_DATE", this.firstDate);
        data.put("FIRST_DATE_MON", this.firstDateMon);
        data.put("GIFT_SERIAL_NUMBER", this.giftSerialNumber);
        data.put("GIFT_USER_ID", this.giftUserId);
        data.put("INST_ID", this.instId);
        data.put("IN_CARD_NO", this.inCardNo);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("PACKAGE_ID", this.packageId);
        data.put("PRODUCT_ID", this.productId);
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
        data.put("SERVICE_ID", this.elementId);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        return data;
    }

}
