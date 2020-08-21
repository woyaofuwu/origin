package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class DelGroupNetMemberReqData extends BaseReqData{
	
	 private List<GroupMemberData> mebUcaList = new ArrayList<GroupMemberData>();
	 
	 private String cancellMeb;// 成员销户标识
	
	 public void addMebUca(GroupMemberData mebUca)
	 {
	        this.mebUcaList.add(mebUca);
	 }

	 public List<GroupMemberData> getMebUcaList()
	 {
	        return mebUcaList;
	 }

     public void setMebUcaList(List<GroupMemberData> mebUcaList)
     {
        this.mebUcaList = mebUcaList;
     }

	public String getCancellMeb() {
		return cancellMeb;
	}

	public void setCancellMeb(String cancellMeb) {
		this.cancellMeb = cancellMeb;
	}
}
