
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata.ExchangeData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata.ScoreExchangeRequestData;

public class BuildScoreExchangeIntfReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // 入参校验
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "RULE_ID");
        IDataUtil.chkParam(param, "COUNT");

        ExchangeData data = new ExchangeData();
        List<ExchangeData> exchangeDatas = new ArrayList<ExchangeData>();
        data.setRuleId(param.getString("RULE_ID"));
        data.setCount(param.getString("COUNT"));
        exchangeDatas.add(data);
        ScoreExchangeRequestData reqData = (ScoreExchangeRequestData) brd;
        // 接口传的值
        reqData.setExchangeDatas(exchangeDatas);
        reqData.setHhCardId(param.getString("CARD_ID"));
        reqData.setHhCardName(param.getString("CARD_KIND_NAME"));
        // 接口传的值
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ScoreExchangeRequestData();
    }

}
