
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class ImpuTradeData extends BaseTradeData
{
    private String endDate;

    private String impi;

    private String imsPassword;

    private String imsUserId;

    private String modifyTag;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String sipUrl;

    private String startDate;

    private String telUrl;

    private String userId;

    private String instId;

    public ImpuTradeData()
    {

    }

    public ImpuTradeData(IData data)
    {
        this.endDate = data.getString("END_DATE");
        this.impi = data.getString("IMPI");
        this.imsPassword = data.getString("IMS_PASSWORD");
        this.imsUserId = data.getString("IMS_USER_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.sipUrl = data.getString("SIP_URL");
        this.startDate = data.getString("START_DATE");
        this.telUrl = data.getString("TEL_URL");
        this.userId = data.getString("USER_ID");
        this.instId = data.getString("INST_ID");
    }

    public ImpuTradeData clone()
    {
        ImpuTradeData ImpuTradeData = new ImpuTradeData();
        ImpuTradeData.setEndDate(this.getEndDate());
        ImpuTradeData.setImpi(this.getImpi());
        ImpuTradeData.setImsPassword(this.getImsPassword());
        ImpuTradeData.setImsUserId(this.getImsUserId());
        ImpuTradeData.setModifyTag(this.getModifyTag());
        ImpuTradeData.setRsrvStr1(this.getRsrvStr1());
        ImpuTradeData.setRsrvStr2(this.getRsrvStr2());
        ImpuTradeData.setRsrvStr3(this.getRsrvStr3());
        ImpuTradeData.setRsrvStr4(this.getRsrvStr4());
        ImpuTradeData.setRsrvStr5(this.getRsrvStr5());
        ImpuTradeData.setSipUrl(this.getSipUrl());
        ImpuTradeData.setStartDate(this.getStartDate());
        ImpuTradeData.setTelUrl(this.getTelUrl());
        ImpuTradeData.setUserId(this.getUserId());
        ImpuTradeData.setInstId(this.getInstId());
        return ImpuTradeData;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getImpi()
    {
        return impi;
    }

    public String getImsPassword()
    {
        return imsPassword;
    }

    public String getImsUserId()
    {
        return imsUserId;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getModifyTag()
    {
        return modifyTag;
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

    public String getSipUrl()
    {
        return sipUrl;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_IMPU";
    }

    public String getTelUrl()
    {
        return telUrl;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setImpi(String impi)
    {
        this.impi = impi;
    }

    public void setImsPassword(String imsPassword)
    {
        this.imsPassword = imsPassword;
    }

    public void setImsUserId(String imsUserId)
    {
        this.imsUserId = imsUserId;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
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

    public void setSipUrl(String sipUrl)
    {
        this.sipUrl = sipUrl;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setTelUrl(String telUrl)
    {
        this.telUrl = telUrl;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("END_DATE", this.endDate);
        data.put("IMPI", this.impi);
        data.put("IMS_PASSWORD", this.imsPassword);
        data.put("IMS_USER_ID", this.imsUserId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("SIP_URL", this.sipUrl);
        data.put("START_DATE", this.startDate);
        data.put("TEL_URL", this.telUrl);
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
