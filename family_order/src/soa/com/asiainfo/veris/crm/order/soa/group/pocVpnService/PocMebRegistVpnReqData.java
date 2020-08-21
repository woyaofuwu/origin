package com.asiainfo.veris.crm.order.soa.group.pocVpnService;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class PocMebRegistVpnReqData extends MemberReqData
{
    private IData svc;

    public IData getSvc()
    {
        return svc;
    }

    public void setSvc(IData map)
    {
        this.svc = map;
    }
}
