
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ChangeSvcStateReqData extends BaseReqData
{
    /**
     * 是否关联停宽带  Y：是,N:否
     */
    private String isStopWide = "";
    
    /**
     * 是否关联开宽带  Y：是,N:否
     */
    private String isOpenWide = "";

    public String getIsStopWide()
    {
        return isStopWide;
    }

    public void setIsStopWide(String isStopWide)
    {
        this.isStopWide = isStopWide;
    }

    public String getIsOpenWide()
    {
        return isOpenWide;
    }

    public void setIsOpenWide(String isOpenWide)
    {
        this.isOpenWide = isOpenWide;
    }
    
}
