
package com.asiainfo.veris.crm.order.soa.person.busi.np.npapplycancelout.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class NpApplyCancelOutReqData extends BaseReqData
{

    public String cancelTag;

    public String cancelType;

    public String cancelTradeId;// 需要取消台账

    public final String getCancelTag()
    {
        return cancelTag;
    }

    public final String getCancelTradeId()
    {
        return cancelTradeId;
    }

    public final String getCancelType()
    {
        return cancelType;
    }

    public final void setCancelTag(String cancelTag)
    {
        this.cancelTag = cancelTag;
    }

    public final void setCancelTradeId(String cancelTradeId)
    {
        this.cancelTradeId = cancelTradeId;
    }

    public final void setCancelType(String cancelType)
    {
        this.cancelType = cancelType;
    }

}
