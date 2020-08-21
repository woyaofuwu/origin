
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class ScoreRollBackRequestData extends BaseReqData
{

    private String rollScore;// 回退积分值

    private String orderNo;// 大订单编号

    private String subscribeId;// 子订单编号

    private String oprt;// 积分回退时间

    public String getOprt()
    {
        return oprt;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public String getRollScore()
    {
        return rollScore;
    }

    public String getSubscribeId()
    {
        return subscribeId;
    }

    public void setOprt(String oprt)
    {
        this.oprt = oprt;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public void setRollScore(String rollScore)
    {
        this.rollScore = rollScore;
    }

    public void setSubscribeId(String subscribeId)
    {
        this.subscribeId = subscribeId;
    }
}
