
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class ChangeMemElementReqData extends MemberReqData
{

    private String memRoleB;

    private String mebFileShow;
    
    private String mebFileList;
    
    public String getMemRoleB()
    {
        return memRoleB;
    }

    public void setMemRoleB(String memRoleB)
    {
        this.memRoleB = memRoleB;
    }

	public String getMebFileShow() {
		return mebFileShow;
	}

	public void setMebFileShow(String mebFileShow) {
		this.mebFileShow = mebFileShow;
	}

	public String getMebFileList() {
		return mebFileList;
	}

	public void setMebFileList(String mebFileList) {
		this.mebFileList = mebFileList;
	}
    
}
