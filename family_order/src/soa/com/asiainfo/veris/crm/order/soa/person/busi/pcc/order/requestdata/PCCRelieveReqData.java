
package com.asiainfo.veris.crm.order.soa.person.busi.pcc.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData; 

public class PCCRelieveReqData extends BaseReqData
{ 
	private String relieveFlag;
	
	public String getRelieveFlag()
    {
        return relieveFlag;
    }
	
	public void setRelieveFlag(String relieveFlag)
    {
        this.relieveFlag = relieveFlag;
    }
}
