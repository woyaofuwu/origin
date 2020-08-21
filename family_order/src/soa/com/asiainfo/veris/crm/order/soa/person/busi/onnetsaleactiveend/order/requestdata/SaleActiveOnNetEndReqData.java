
package com.asiainfo.veris.crm.order.soa.person.busi.onnetsaleactiveend.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata.BaseSaleActiveReqData;

public class SaleActiveOnNetEndReqData extends BaseSaleActiveReqData
{
    private String relationTradeId;

    private String terminalReturnTag = "0";

    private String forceEndDate;

    private int intervalMonth;

    private String callType;

    private String endDateValue;
    
    private String returnfee ;
    
    private String intface ;
    
    private String wideYearActiveBackFee ; //移机时，需要对宽带包年活动的费用做转存折处理（宽带包年活动变更预存回退存折）

    public String getCallType()
    {
        return callType;
    }

    public String getEndDateValue()
    {
        return endDateValue;
    }

    public String getForceEndDate()
    {
        return forceEndDate;
    }

    public int getIntervalMonth()
    {
        return intervalMonth;
    }

    public String getRelationTradeId()
    {
        return relationTradeId;
    }
    
    public String getReturnfee()
    {
        return returnfee;
    }
    
    public String getIntface()
    {
        return intface;
    }
    
    public String getTerminalReturnTag()
    {
        return terminalReturnTag;
    }

    public String getWideYearActiveBackFee()
    {
        return wideYearActiveBackFee;
    }
    
    public void setReturnfee(String returnfee)
    {
        this.returnfee = returnfee;
    }
    
    public void setIntface(String intface)
    {
        this.intface = intface ;
    }

    public void setCallType(String callType)
    {
        this.callType = callType;
    }

    public void setEndDateValue(String endDateValue)
    {
        this.endDateValue = endDateValue;
    }

    public void setForceEndDate(String forceEndDate)
    {
        this.forceEndDate = forceEndDate;
    }

    public void setIntervalMonth(int intervalMonth)
    {
        this.intervalMonth = intervalMonth;
    }

    public void setRelationTradeId(String relationTradeId)
    {
        this.relationTradeId = relationTradeId;
    }

    public void setTerminalReturnTag(String terminalReturnTag)
    {
        this.terminalReturnTag = terminalReturnTag;
    }

    public void setWideYearActiveBackFee(String wideYearActiveBackFee)
    {
        this.wideYearActiveBackFee = wideYearActiveBackFee;
    }

}
