
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class TransPhoneTradeData extends BaseTradeData
{

    private String phoneCodeA;

    private String phoneCodeB;

    private String startDate;

    private String endDate;

    private String modifyTag;

    public TransPhoneTradeData()
    {

    }

    public TransPhoneTradeData(IData data)
    {
        this.phoneCodeA = data.getString("PHONE_CODE_A");
        this.phoneCodeB = data.getString("PHONE_CODE_B");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.modifyTag = data.getString("MODIFY_TAG");
    }

    public TransPhoneTradeData clone()
    {
        TransPhoneTradeData transPhoneTradeData = new TransPhoneTradeData();
        transPhoneTradeData.setPhoneCodeA(this.getPhoneCodeA());
        transPhoneTradeData.setPhoneCodeB(this.getPhoneCodeB());
        transPhoneTradeData.setStartDate(this.getStartDate());
        transPhoneTradeData.setEndDate(this.getEndDate());
        transPhoneTradeData.setModifyTag(this.getModifyTag());

        return transPhoneTradeData;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getModifyTag()
    {
        return this.modifyTag;
    }

    public String getPhoneCodeA()
    {
        return this.phoneCodeA;
    }

    public String getPhoneCodeB()
    {
        return this.phoneCodeB;
    }

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_TRANS_PHONE";
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setPhoneCodeA(String phoneCodeA)
    {
        this.phoneCodeA = phoneCodeA;
    }

    public void setPhoneCodeB(String phoneCodeB)
    {
        this.phoneCodeB = phoneCodeB;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("PHONE_CODE_A", this.phoneCodeA);
        data.put("PHONE_CODE_B", this.phoneCodeB);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
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
