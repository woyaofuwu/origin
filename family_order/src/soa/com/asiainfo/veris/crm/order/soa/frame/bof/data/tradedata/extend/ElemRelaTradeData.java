
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class ElemRelaTradeData extends BaseTradeData
{

    private String userId;

    private String elementTypeCode;

    private String elementId;

    private String instId;

    private String relaUserId;

    private String relaElementTypeCode;

    private String relaElementId;

    private String relaInstId;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String modifyTag;

    public ElemRelaTradeData()
    {

    }

    public ElemRelaTradeData(IData data)
    {
        this.userId = data.getString("USER_ID");
        this.elementTypeCode = data.getString("ELEMENT_TYPE_CODE");
        this.elementId = data.getString("ELEMENT_ID");
        this.instId = data.getString("INST_ID");
        this.relaUserId = data.getString("RELA_USER_ID");
        this.relaElementTypeCode = data.getString("RELA_ELEMENT_TYPE_CODE");
        this.relaElementId = data.getString("RELA_ELEMENT_ID");
        this.relaInstId = data.getString("RELA_INST_ID");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.modifyTag = data.getString("MODIFY_TAG");
    }

    public ElemRelaTradeData clone()
    {
        ElemRelaTradeData elemRelaTradeData = new ElemRelaTradeData();
        elemRelaTradeData.setUserId(this.getUserId());
        elemRelaTradeData.setElementTypeCode(this.getElementTypeCode());
        elemRelaTradeData.setElementId(this.getElementId());
        elemRelaTradeData.setInstId(this.getInstId());
        elemRelaTradeData.setRelaUserId(this.getRelaUserId());
        elemRelaTradeData.setRelaElementTypeCode(this.getRelaElementTypeCode());
        elemRelaTradeData.setRelaElementId(this.getRelaElementId());
        elemRelaTradeData.setRelaInstId(this.getRelaInstId());
        elemRelaTradeData.setRsrvStr1(this.getRsrvStr1());
        elemRelaTradeData.setRsrvStr2(this.getRsrvStr2());
        elemRelaTradeData.setRsrvStr3(this.getRsrvStr3());
        elemRelaTradeData.setRsrvStr4(this.getRsrvStr4());
        elemRelaTradeData.setRsrvStr5(this.getRsrvStr5());
        elemRelaTradeData.setModifyTag(this.getModifyTag());

        return elemRelaTradeData;
    }

    public String getElementId()
    {
        return this.elementId;
    }

    public String getElementTypeCode()
    {
        return this.elementTypeCode;
    }

    public String getInstId()
    {
        return this.instId;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getRelaElementId()
    {
        return this.relaElementId;
    }

    public String getRelaElementTypeCode()
    {
        return this.relaElementTypeCode;
    }

    public String getRelaInstId()
    {
        return this.relaInstId;
    }

    public String getRelaUserId()
    {
        return this.relaUserId;
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

    public String getTableName()
    {
        return "TF_B_TRADE_ELEM_RELA";
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    public void setElementTypeCode(String elementTypeCode)
    {
        this.elementTypeCode = elementTypeCode;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setRelaElementId(String relaElementId)
    {
        this.relaElementId = relaElementId;
    }

    public void setRelaElementTypeCode(String relaElementTypeCode)
    {
        this.relaElementTypeCode = relaElementTypeCode;
    }

    public void setRelaInstId(String relaInstId)
    {
        this.relaInstId = relaInstId;
    }

    public void setRelaUserId(String relaUserId)
    {
        this.relaUserId = relaUserId;
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

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("USER_ID", this.userId);
        data.put("ELEMENT_TYPE_CODE", this.elementTypeCode);
        data.put("ELEMENT_ID", this.elementId);
        data.put("INST_ID", this.instId);
        data.put("RELA_USER_ID", this.relaUserId);
        data.put("RELA_ELEMENT_TYPE_CODE", this.relaElementTypeCode);
        data.put("RELA_ELEMENT_ID", this.relaElementId);
        data.put("RELA_INST_ID", this.relaInstId);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("MODIFY_TAG", this.modifyTag);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
