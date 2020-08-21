
package com.asiainfo.veris.crm.order.soa.person.busi.getsaleactivegift.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class GetSaleActiveGiftReqData extends BaseReqData
{
    private String resCode = "";// 礼品ID

    private String relationTradeId = "";// 关联营销活动订单ID

    public String getRelationTradeId()
    {
        return relationTradeId;
    }

    public String getResCode()
    {
        return resCode;
    }

    public void setRelationTradeId(String relationTradeId)
    {
        this.relationTradeId = relationTradeId;
    }

    public void setResCode(String resCode)
    {
        this.resCode = resCode;
    }
}
