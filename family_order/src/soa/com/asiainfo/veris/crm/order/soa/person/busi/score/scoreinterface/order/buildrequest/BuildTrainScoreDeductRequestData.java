
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.TrainScoreDeductRequestData;

public class BuildTrainScoreDeductRequestData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // 入参校验
        IDataUtil.chkParam(param, "SCORE_VALUE");
        IDataUtil.chkParam(param, "CHANNEL_TRADE_ID");
        IDataUtil.chkParam(param, "CHANNEL_ACCEPT_TIME");
        IDataUtil.chkParam(param, "PARA1");
        IDataUtil.chkParam(param, "PARA2");
        IDataUtil.chkParam(param, "PARA3");
        IDataUtil.chkParam(param, "PARA4");
        IDataUtil.chkParam(param, "PARA5");
        IDataUtil.chkParam(param, "PARA6");

        TrainScoreDeductRequestData reqData = (TrainScoreDeductRequestData) brd;
        reqData.setScoreValue(param.getString("SCORE_VALUE"));
        reqData.setChannelTradeId(param.getString("CHANNEL_TRADE_ID"));
        reqData.setChannelAcceptTime(param.getString("CHANNEL_ACCEPT_TIME"));
        reqData.setPara1(param.getString("PARA1"));
        reqData.setPara2(param.getString("PARA2"));
        reqData.setPara3(param.getString("PARA3"));
        reqData.setPara4(param.getString("PARA4"));
        reqData.setPara5(param.getString("PARA5"));
        reqData.setPara6(param.getString("PARA6"));
        reqData.setOrderNo(param.getString("ORDER_ID"));
        reqData.setOprt("OPRT");

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new TrainScoreDeductRequestData();
    }

}
