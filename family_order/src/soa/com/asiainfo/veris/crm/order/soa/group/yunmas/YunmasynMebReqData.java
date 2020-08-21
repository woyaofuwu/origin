package com.asiainfo.veris.crm.order.soa.group.yunmas;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class YunmasynMebReqData extends MemberReqData
{
    private IData blackWhite;

    public IData getblackWhite()
    {
        return blackWhite;
    }

    public void setblackWhite(IData map)
    {
        this.blackWhite = map;
    }
}
