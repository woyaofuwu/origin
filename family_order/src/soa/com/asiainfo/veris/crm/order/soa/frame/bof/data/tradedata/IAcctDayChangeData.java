
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata;

public interface IAcctDayChangeData
{

    public IAcctDayChangeData clone();

    public String getEndDate();

    public String getInstId();

    public String getModifyTag();

    public String getRsrvDate2();

    public String getRsrvDate3();

    public String getStartDate();

    public String getTableName();

    public void setEndDate(String endDate);

    public void setInstId(String instId);

    public void setModifyTag(String modifyTag);

    public void setRsrvDate2(String rsrvDate2);

    public void setRsrvDate3(String rsrvDate3);

    public void setStartDate(String startDate);
}
