
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class BaseScoreExchangeRequestData extends BaseReqData
{

    private UcaData convertUca;

    private List<ExchangeData> exchangeDatas;// 兑换列表

    private List<String> valueCardNos;// 有价卡

    public UcaData getConvertUca()
    {
        return convertUca;
    }

    public List<ExchangeData> getExchangeDatas()
    {
        return exchangeDatas;
    }

    public List<String> getValueCardNos()
    {
        return valueCardNos;
    }

    public void setConvertUca(UcaData otherUca)
    {
        this.convertUca = otherUca;
    }

    public void setExchangeDatas(List<ExchangeData> exchangeDatas)
    {
        this.exchangeDatas = exchangeDatas;
    }

    public void setValueCardNos(List<String> valueCardNos)
    {
        this.valueCardNos = valueCardNos;
    }

}
