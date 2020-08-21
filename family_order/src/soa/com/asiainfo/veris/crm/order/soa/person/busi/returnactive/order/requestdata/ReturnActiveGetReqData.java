
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.sub.ReturnActiveGetCardData;

public class ReturnActiveGetReqData extends BaseReqData
{

    private List<ReturnActiveGetCardData> cardList = new ArrayList<ReturnActiveGetCardData>();

    public void addCard(ReturnActiveGetCardData card)
    {
        this.cardList.add(card);
    }

    public List<ReturnActiveGetCardData> getCardList()
    {
        return cardList;
    }

    public void setCardList(List<ReturnActiveGetCardData> cardList)
    {
        this.cardList = cardList;
    }
}
