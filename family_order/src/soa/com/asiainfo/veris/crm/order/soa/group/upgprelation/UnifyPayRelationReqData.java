
package com.asiainfo.veris.crm.order.soa.group.upgprelation;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class UnifyPayRelationReqData extends MemberReqData
{

    private String mebFileShow;
    
    private String mebFileList;
    
    public String getMebFileShow() 
    {
        return mebFileShow;
    }

    public void setMebFileShow(String mebFileShow) 
    {
        this.mebFileShow = mebFileShow;
    }

    public String getMebFileList() 
    {
        return mebFileList;
    }

    public void setMebFileList(String mebFileList) 
    {
        this.mebFileList = mebFileList;
    }
    
}
