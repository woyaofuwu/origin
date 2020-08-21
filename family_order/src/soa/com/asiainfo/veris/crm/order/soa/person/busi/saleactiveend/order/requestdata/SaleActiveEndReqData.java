
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata.BaseSaleActiveReqData;

public class SaleActiveEndReqData extends BaseSaleActiveReqData
{
    private String relationTradeId;

    private String terminalReturnTag = "0";

    private String forceEndDate;

    private int intervalMonth;

    private String callType;

    private String endDateValue;
    
    private String returnfee ; //实收
    
    private String ysreturnfee ; //应收
    
    private String intface ;
    
    private String trueReturnFeeCode;//违约成本金
    
    private String trueReturnFeePrice;//违约金
    
    private String srcPage;
    
    private String wideYearActiveBackFee ; //移机时，需要对宽带包年活动的费用做转存折处理（宽带包年活动变更预存回退存折）

    private String backTerm;//是否退还摄像头
    
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
    
    public String getYSReturnfee()
    {
        return ysreturnfee;
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
    
    public void setYSReturnfee(String ysreturnfee)
    {
        this.ysreturnfee = ysreturnfee;
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

	public String getTrueReturnFeeCode()
	{
		return trueReturnFeeCode;
	}

	public void setTrueReturnFeeCode(String trueReturnFeeCode)
	{
		this.trueReturnFeeCode = trueReturnFeeCode;
	}

	public String getTrueReturnFeePrice()
	{
		return trueReturnFeePrice;
	}

	public void setTrueReturnFeePrice(String trueReturnFeePrice)
	{
		this.trueReturnFeePrice = trueReturnFeePrice;
	}

	public String getSrcPage()
	{
		return srcPage;
	}

	public void setSrcPage(String srcPage)
	{
		this.srcPage = srcPage;
	}

	public String getBackTerm() {
		return backTerm;
	}

	public void setBackTerm(String backTerm) {
		this.backTerm = backTerm;
	}
	
	
    
}
