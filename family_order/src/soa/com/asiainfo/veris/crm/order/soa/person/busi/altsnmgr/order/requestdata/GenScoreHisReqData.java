
package com.asiainfo.veris.crm.order.soa.person.busi.altsnmgr.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class GenScoreHisReqData extends BaseReqData
{

    private String scoreValue;

    private String scoreChanged;

    private String valueChanged;

    public String getScoreChanged()
    {
        return scoreChanged;
    }

    public String getScoreValue()
    {
        return scoreValue;
    }

    public String getValueChanged()
    {
        return valueChanged;
    }

    public void setScoreChanged(String scoreChanged)
    {
        this.scoreChanged = scoreChanged;
    }

    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }

    public void setValueChanged(String valueChanged)
    {
        this.valueChanged = valueChanged;
    }

}
