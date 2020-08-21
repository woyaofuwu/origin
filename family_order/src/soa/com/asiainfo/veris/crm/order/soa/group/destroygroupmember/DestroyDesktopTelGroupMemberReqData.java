
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMemberReqData;

public class DestroyDesktopTelGroupMemberReqData extends DestroyGroupMemberReqData
{
    private IDataset shorts; // 缩位号

    public IDataset getShorts()
    {
        return shorts;
    }

    public void setShorts(IDataset shorts)
    {
        this.shorts = shorts;
    }
}
