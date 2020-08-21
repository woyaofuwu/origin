
package com.asiainfo.veris.crm.order.soa.person.busi.uec.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class UECLotteryActiveDealReqData extends BaseReqData
{

    private String preTradeId;// 活动流水

    private String execName;// 兑换的礼品名称

    public String getExecName()
    {
        return execName;
    }

    public String getPreTradeId()
    {
        return preTradeId;
    }

    public void setExecName(String execName)
    {
        this.execName = execName;
    }

    public void setPreTradeId(String preTradeId)
    {
        this.preTradeId = preTradeId;
    }
}
