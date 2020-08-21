
package com.asiainfo.veris.crm.order.soa.person.busi.uec.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.uec.order.requestdata.UECLotteryActiveDealReqData;

public class BuildUECLotteryActiveDeal extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        UECLotteryActiveDealReqData reqData = (UECLotteryActiveDealReqData) brd;
        reqData.setExecName(param.getString("EXEC_NAME"));
        reqData.setPreTradeId(param.getString("TRADE_ID"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new UECLotteryActiveDealReqData();
    }

}
