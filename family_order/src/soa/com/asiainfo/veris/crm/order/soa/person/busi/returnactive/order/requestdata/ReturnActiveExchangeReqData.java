
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.sub.ReturnActiveExchangeCardData;

public class ReturnActiveExchangeReqData extends BaseReqData
{

    List<ReturnActiveExchangeCardData> cardList = new ArrayList<ReturnActiveExchangeCardData>();

    public void addCardData(ReturnActiveExchangeCardData cardData)
    {
        this.cardList.add(cardData);
    }

    public List<ReturnActiveExchangeCardData> getCardList()
    {
        return cardList;
    }

    public void setCardList(List<ReturnActiveExchangeCardData> cardList)
    {
        this.cardList = cardList;
    }
}
