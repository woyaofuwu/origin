
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class DestroyGroupMemberReqData extends MemberReqData
{
    private String reMark;

    private String mebFileShow;
    
    private String mebFileList;
    
    public String getReMark()
    {
        return reMark;
    }

    public void setReMark(String reMark)
    {
        this.reMark = reMark;
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
