
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonechangeuser.order.requestdata;
 
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class NoPhoneWideChangeUserRequestData extends BaseReqData
{

    private IData wideChangeInfo;

    public IData getWideChangeInfo()
    {
        return wideChangeInfo;
    }

    public void setWideChangeInfo(IData wideChangeInfo)
    {
        this.wideChangeInfo = wideChangeInfo;
    }
}
