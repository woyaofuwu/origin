
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.sub.ReturnActiveGGCardData;

public class ReturnActiveNewReqData extends BaseReqData
{

    private String doMode;

    private String acceptNum;

    private String inMode;// 接口进入时办理方式:1-校验，2-办理，为空表示前台办理

    private List<ReturnActiveGGCardData> cardList = new ArrayList<ReturnActiveGGCardData>();

    public void addCard(ReturnActiveGGCardData card)
    {
        this.cardList.add(card);
    }

    public String getAcceptNum()
    {
        return acceptNum;
    }

    public List<ReturnActiveGGCardData> getCardList()
    {
        return cardList;
    }

    public String getDoMode()
    {
        return doMode;
    }

    public String getInMode()
    {
        return inMode;
    }

    public void setAcceptNum(String acceptNum)
    {
        this.acceptNum = acceptNum;
    }

    public void setCardList(List<ReturnActiveGGCardData> cardList)
    {
        this.cardList = cardList;
    }

    public void setDoMode(String doMode)
    {
        this.doMode = doMode;
    }

    public void setInMode(String inMode)
    {
        this.inMode = inMode;
    }

}
