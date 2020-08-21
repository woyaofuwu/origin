
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.ReturnActiveGetReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.sub.ReturnActiveGetCardData;

public class BuildReturnActiveGetReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ReturnActiveGetReqData reqData = (ReturnActiveGetReqData) brd;
        IDataset cardList = new DatasetList(param.getString("CARD_LIST", "[]"));
        for (int i = 0, size = cardList.size(); i < size; i++)
        {
            IData card = cardList.getData(i);
            ReturnActiveGetCardData cardData = new ReturnActiveGetCardData();
            cardData.setGgCardNo(card.getString("GGCARD_NO"));
            cardData.setRemark(card.getString("REMARK"));

            reqData.addCard(cardData);
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ReturnActiveGetReqData();
    }

}
