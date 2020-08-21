
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class CreateAdcXxtGroupMemberReqData extends MemberReqData
{
    private IData mainTrade; // 第-笔的台账数据

    private IData blackWhithOut;// 网外号码黑白名单表数据

    public IData getBlackWhithOut()
    {
        return blackWhithOut;
    }

    public IData getMainTrade()
    {
        return mainTrade;
    }

    public void setBlackWhithOut(IData blackWhithOut)
    {
        this.blackWhithOut = blackWhithOut;
    }

    public void setMainTrade(IData mainTrade)
    {
        this.mainTrade = mainTrade;
    }

}
