
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreTradeReviseRequestData;

public class BuildScoreTradeReviseRequestData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ScoreTradeReviseRequestData reqData = (ScoreTradeReviseRequestData) brd;
        reqData.setScoreValue(param.getString("SCORE_VALUE"));
        reqData.setOrderId(param.getString("ORDER_ID"));
        reqData.setSubscribeId(param.getString("SUBSCRIBE_ID"));
        reqData.setOprt(param.getString("OPRT"));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ScoreTradeReviseRequestData();
    }

}
