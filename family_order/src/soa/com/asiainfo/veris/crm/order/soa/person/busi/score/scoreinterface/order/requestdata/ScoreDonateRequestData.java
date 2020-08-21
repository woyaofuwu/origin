
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class ScoreDonateRequestData extends BaseReqData
{
    private UcaData donatedUca;// 被转赠号码UCA

    private String donateScore;// 转赠积分

    public UcaData getDonatedUca()
    {
        return donatedUca;
    }

    public String getDonateScore()
    {
        return donateScore;
    }

    public void setDonatedUca(UcaData donatedUca)
    {
        this.donatedUca = donatedUca;
    }

    public void setDonateScore(String donateScore)
    {
        this.donateScore = donateScore;
    }

}
