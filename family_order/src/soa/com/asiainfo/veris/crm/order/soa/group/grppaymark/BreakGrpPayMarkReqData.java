
package com.asiainfo.veris.crm.order.soa.group.grppaymark;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupReqData;

public class BreakGrpPayMarkReqData extends GroupReqData
{

    private String markFlag;
    
    private String startDates;
    
    private String endDates;
    
    private String remarkOther;

    public String getMarkFlag()
    {
        return markFlag;
    }

    public void setMarkFlag(String markFlag)
    {
        this.markFlag = markFlag;
    }

	public String getStartDates() {
		return startDates;
	}

	public void setStartDates(String startDates) {
		this.startDates = startDates;
	}

	public String getEndDates() {
		return endDates;
	}

	public void setEndDates(String endDates) {
		this.endDates = endDates;
	}

	public String getRemarkOther() {
		return remarkOther;
	}

	public void setRemarkOther(String remarkOther) {
		this.remarkOther = remarkOther;
	}
	
}
