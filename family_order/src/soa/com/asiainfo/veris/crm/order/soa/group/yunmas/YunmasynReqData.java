package com.asiainfo.veris.crm.order.soa.group.yunmas;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUserReqData;

public class YunmasynReqData extends CreateGroupUserReqData
{
    private IData grpPlatsvc;

    public IData getgrpPlatsvc()
    {
        return grpPlatsvc;
    }

    public void setgrpPlatsvc(IData map)
    {
        this.grpPlatsvc = map;
    }
}
