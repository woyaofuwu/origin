
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.order.requestdata.CPEActiveMemberData;

public class FTTHBusiModemApplyReqData extends BaseReqData
{ 
	private IDataset memberList; 
	public void setMemberList(IDataset memberList)
    {
        this.memberList = memberList;
    }
	
	public IDataset getMemberList(){
		return memberList;
	}
}
