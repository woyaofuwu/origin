
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class TrainScoreDeductRequestData extends BaseReqData
{

    private String scoreValue;

    private String channelTradeId;

    private String channelAcceptTime;

    private String para1;// 商户号

    private String para2;// 商品号

    private String para3;// 商户名称

    private String para4;// 商品名称

    private String para5;// 商品单价

    private String para6;// 商品数

    private String orderNo;// 大订单号

    private String oprt;// 客户订购时间

    public String getChannelAcceptTime()
    {
        return channelAcceptTime;
    }

    public String getChannelTradeId()
    {
        return channelTradeId;
    }

    public String getOprt()
    {
        return oprt;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public String getPara1()
    {
        return para1;
    }

    public String getPara2()
    {
        return para2;
    }

    public String getPara3()
    {
        return para3;
    }

    public String getPara4()
    {
        return para4;
    }

    public String getPara5()
    {
        return para5;
    }

    public String getPara6()
    {
        return para6;
    }

    public String getScoreValue()
    {
        return scoreValue;
    }

    public void setChannelAcceptTime(String channelAcceptTime)
    {
        this.channelAcceptTime = channelAcceptTime;
    }

    public void setChannelTradeId(String channelTradeId)
    {
        this.channelTradeId = channelTradeId;
    }

    public void setOprt(String oprt)
    {
        this.oprt = oprt;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public void setPara1(String para1)
    {
        this.para1 = para1;
    }

    public void setPara2(String para2)
    {
        this.para2 = para2;
    }

    public void setPara3(String para3)
    {
        this.para3 = para3;
    }

    public void setPara4(String para4)
    {
        this.para4 = para4;
    }

    public void setPara5(String para5)
    {
        this.para5 = para5;
    }

    public void setPara6(String para6)
    {
        this.para6 = para6;
    }

    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }

}
