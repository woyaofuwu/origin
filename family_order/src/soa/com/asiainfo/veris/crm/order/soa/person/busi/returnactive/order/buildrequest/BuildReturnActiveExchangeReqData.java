
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.ReturnActiveExchangeReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.sub.ReturnActiveExchangeCardData;

public class BuildReturnActiveExchangeReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ReturnActiveExchangeReqData reqData = (ReturnActiveExchangeReqData) brd;
        IDataset cardList = new DatasetList(param.getString("CARD_LIST", "[]"));
        for (int i = 0, size = cardList.size(); i < size; i++)
        {
            IData card = cardList.getData(i);
            ReturnActiveExchangeCardData cardData = new ReturnActiveExchangeCardData();
            cardData.setCardCode(card.getString("CARD_CODE"));
            cardData.setCardPassWord(card.getString("CARD_PASS_WORD"));
            cardData.setGiftCode(card.getString("GIFT_CODE"));
            cardData.setResCode(card.getString("RES_CODE"));
            cardData.setRemark(card.getString("REMARK"));

            reqData.addCardData(cardData);
        }

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ReturnActiveExchangeReqData();
    }

}
