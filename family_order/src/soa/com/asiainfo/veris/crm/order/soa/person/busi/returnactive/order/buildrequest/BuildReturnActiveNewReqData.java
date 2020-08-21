
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.ReturnActiveNewReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.sub.ReturnActiveGGCardData;

public class BuildReturnActiveNewReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub

        ReturnActiveNewReqData reqData = (ReturnActiveNewReqData) brd;
        String doMode = param.getString("DO_MODE", "");
        String inMode = param.getString("IN_MODE", "");
        String acceptNum = param.getString("ACCEPT_NUM", "");
        IDataset cardList = new DatasetList(param.getString("CARD_LIST", "[]"));
        // int accept_num_yc = param.getInt("ACCEPT_NUM_YC",0);

        // reqData.setAccept_num_yc(accept_num_yc);
        reqData.setDoMode(doMode);
        reqData.setInMode(inMode);
        reqData.setAcceptNum(acceptNum);
        for (int i = 0, size = cardList.size(); i < size; i++)
        {
            IData card = cardList.getData(i);
            ReturnActiveGGCardData cardData = new ReturnActiveGGCardData();
            cardData.setGgCardNo(card.getString("col_GGCARD_NO"));
            cardData.setRemark(card.getString("col_REMARK"));

            reqData.addCard(cardData);
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ReturnActiveNewReqData();
    }

}
