
package com.asiainfo.veris.crm.order.soa.person.busi.coupons.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class CouponsReqData extends BaseReqData
{
	private String ticketCode;
	private String ticketValue;
	private String spendValue;
	
	private String repairNO; 
	
	public String getTicketCode()
    {
        return ticketCode;
    } 
	public void setTicketCode(String ticketCode)
    {
        this.ticketCode = ticketCode;
    }
	
	public String getTicketValue()
    {
        return ticketValue;
    } 
	public void setTicketValue(String ticketValue)
    {
        this.ticketValue = ticketValue;
    }
	
	public String getSpendValue()
    {
        return spendValue;
    } 
	public void setSpendValue(String spendValue)
    {
        this.spendValue = spendValue;
    }
	
	public String getRepairNO()
    {
        return repairNO;
    } 
	public void setRepairNO(String repairNO)
    {
        this.repairNO = repairNO;
    }
	 
}
