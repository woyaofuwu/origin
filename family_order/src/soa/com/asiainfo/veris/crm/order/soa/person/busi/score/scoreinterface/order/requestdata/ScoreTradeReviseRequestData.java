
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ScoreTradeReviseRequestData extends BaseReqData
{
    private String scoreValue;// 冲正积分

    private String orderId;// /大订单编号

    private String subscribeId;// 子订单编号

    private String oprt;// 冲正时间

    public String getOprt()
    {
        return oprt;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getScoreValue()
    {
        return scoreValue;
    }

    public String getSubscribeId()
    {
        return subscribeId;
    }

    public void setOprt(String oprt)
    {
        this.oprt = oprt;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }

    public void setSubscribeId(String subscribeId)
    {
        this.subscribeId = subscribeId;
    }
}
