
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata;

public class ScoreExchangeRequestData extends BaseScoreExchangeRequestData
{
    private String hhCardId;// 海航会员编号

    private String hhCardName;// 海航会员名称

    public String getHhCardId()
    {
        return hhCardId;
    }

    public String getHhCardName()
    {
        return hhCardName;
    }

    public void setHhCardId(String hhCardId)
    {
        this.hhCardId = hhCardId;
    }

    public void setHhCardName(String hhCardName)
    {
        this.hhCardName = hhCardName;
    }
}
