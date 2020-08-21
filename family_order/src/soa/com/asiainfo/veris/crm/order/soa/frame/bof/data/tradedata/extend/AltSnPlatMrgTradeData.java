
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class AltSnPlatMrgTradeData extends BaseTradeData
{

    private String tradeId;

    private String month;

    private String serialNumber;

    private String relaSerialNumber;

    private String relaType;

    private String isLocalBase;

    private String eparchyCode;

    private String preAcceptTime;

    private String activateTime;

    private String platSvcId;

    private String needTransfer;

    private String platDealTag;

    private String platUpdateTime;

    private String recBelongFileName;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    public AltSnPlatMrgTradeData()
    {

    }

    public AltSnPlatMrgTradeData(IData data)
    {
        this.tradeId = data.getString("TRADE_ID");
        this.month = data.getString("MONTH");
        this.serialNumber = data.getString("SERIAL_NUMBER");
        this.relaSerialNumber = data.getString("RELA_SERIAL_NUMBER");
        this.relaType = data.getString("RELA_TYPE");
        this.isLocalBase = data.getString("IS_LOCAL_BASE");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.preAcceptTime = data.getString("PRE_ACCEPT_TIME");
        this.activateTime = data.getString("ACTIVATE_TIME");
        this.platSvcId = data.getString("PLAT_SVC_ID");
        this.needTransfer = data.getString("NEED_TRANSFER");
        this.platDealTag = data.getString("PLAT_DEAL_TAG");
        this.platUpdateTime = data.getString("PLAT_UPDATE_TIME");
        this.recBelongFileName = data.getString("REC_BELONG_FILENAME");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
    }

    public AltSnPlatMrgTradeData clone()
    {
        AltSnPlatMrgTradeData altSnPlatMrgTradeData = new AltSnPlatMrgTradeData();

        altSnPlatMrgTradeData.setTradeId(this.getTradeId());
        ;
        altSnPlatMrgTradeData.setMonth(this.getMonth());
        altSnPlatMrgTradeData.setSerialNumber(this.getSerialNumber());
        altSnPlatMrgTradeData.setRelaSerialNumber(this.getRelaSerialNumber());
        altSnPlatMrgTradeData.setRelaType(this.getRelaType());
        altSnPlatMrgTradeData.setIsLocalBase(this.getIsLocalBase());
        altSnPlatMrgTradeData.setEparchyCode(this.getEparchyCode());
        altSnPlatMrgTradeData.setPreAcceptTime(this.getPreAcceptTime());
        altSnPlatMrgTradeData.setActivateTime(this.getActivateTime());
        altSnPlatMrgTradeData.setPlatSvcId(this.getPlatSvcId());
        altSnPlatMrgTradeData.setNeedTransfer(this.getNeedTransfer());
        altSnPlatMrgTradeData.setPlatDealTag(this.getPlatDealTag());
        altSnPlatMrgTradeData.setPlatUpdateTime(this.getPlatUpdateTime());
        altSnPlatMrgTradeData.setRecBelongFileName(this.getRecBelongFileName());
        altSnPlatMrgTradeData.setRsrvStr1(this.getRsrvStr1());
        altSnPlatMrgTradeData.setRsrvStr2(this.getRsrvStr2());
        altSnPlatMrgTradeData.setRsrvStr3(this.getRsrvStr3());
        altSnPlatMrgTradeData.setRsrvDate1(this.getRsrvDate1());
        altSnPlatMrgTradeData.setRsrvDate2(this.getRsrvDate2());
        altSnPlatMrgTradeData.setRsrvDate3(this.getRsrvDate3());

        return altSnPlatMrgTradeData;
    }

    public String getActivateTime()
    {
        return activateTime;
    }

    public String getEparchyCode()
    {
        return eparchyCode;
    }

    public String getIsLocalBase()
    {
        return isLocalBase;
    }

    public String getMonth()
    {
        return month;
    }

    public String getNeedTransfer()
    {
        return needTransfer;
    }

    public String getPlatDealTag()
    {
        return platDealTag;
    }

    public String getPlatSvcId()
    {
        return platSvcId;
    }

    public String getPlatUpdateTime()
    {
        return platUpdateTime;
    }

    public String getPreAcceptTime()
    {
        return preAcceptTime;
    }

    public String getRecBelongFileName()
    {
        return recBelongFileName;
    }

    public String getRelaSerialNumber()
    {
        return relaSerialNumber;
    }

    public String getRelaType()
    {
        return relaType;
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

    public String getSerialNumber()
    {
        return serialNumber;
    }

    @Override
    public String getTableName()
    {
        // TODO Auto-generated method stub
        return "TF_B_TRADE_ALTSN_PLATMRG";
    }

    public String getTradeId()
    {
        return tradeId;
    }

    public void setActivateTime(String activateTime)
    {
        this.activateTime = activateTime;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setIsLocalBase(String isLocalBase)
    {
        this.isLocalBase = isLocalBase;
    }

    public void setMonth(String month)
    {
        this.month = month;
    }

    public void setNeedTransfer(String needTransfer)
    {
        this.needTransfer = needTransfer;
    }

    public void setPlatDealTag(String platDealTag)
    {
        this.platDealTag = platDealTag;
    }

    public void setPlatSvcId(String platSvcId)
    {
        this.platSvcId = platSvcId;
    }

    public void setPlatUpdateTime(String platUpdateTime)
    {
        this.platUpdateTime = platUpdateTime;
    }

    public void setPreAcceptTime(String preAcceptTime)
    {
        this.preAcceptTime = preAcceptTime;
    }

    public void setRecBelongFileName(String recBelongFileName)
    {
        this.recBelongFileName = recBelongFileName;
    }

    public void setRelaSerialNumber(String relaSerialNumber)
    {
        this.relaSerialNumber = relaSerialNumber;
    }

    public void setRelaType(String relaType)
    {
        this.relaType = relaType;
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

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setTradeId(String tradeId)
    {
        this.tradeId = tradeId;
    }

    @Override
    public IData toData()
    {
        // TODO Auto-generated method stub
        IData data = new DataMap();

        data.put("TRADE_ID", this.tradeId);
        data.put("MONTH", this.month);
        data.put("SERIAL_NUMBER", this.serialNumber);
        data.put("RELA_SERIAL_NUMBER", this.relaSerialNumber);
        data.put("RELA_TYPE", this.relaType);
        data.put("IS_LOCAL_BASE", this.isLocalBase);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("PRE_ACCEPT_TIME", this.preAcceptTime);
        data.put("ACTIVATE_TIME", this.activateTime);
        data.put("PLAT_SVC_ID", this.platSvcId);
        data.put("NEED_TRANSFER", this.needTransfer);
        data.put("PLAT_DEAL_TAG", this.platDealTag);
        data.put("PLAT_UPDATE_TIME", this.platUpdateTime);
        data.put("REC_BELONG_FILENAME", this.recBelongFileName);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
