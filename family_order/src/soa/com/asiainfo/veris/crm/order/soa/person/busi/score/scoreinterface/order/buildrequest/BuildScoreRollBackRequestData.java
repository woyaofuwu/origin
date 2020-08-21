
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreRollBackRequestData;

public class BuildScoreRollBackRequestData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // 入参校验
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "ORDER_ID");
        IDataUtil.chkParam(param, "SUBSCRIBE_ID");
        IDataUtil.chkParam(param, "OPRT");
        IDataUtil.chkParam(param, "RSRV_STR3");
        IDataUtil.chkParam(param, "RSRV_STR1");

        ScoreRollBackRequestData reqData = (ScoreRollBackRequestData) brd;
        reqData.setRollScore(param.getString("RSRV_STR3"));
        reqData.setOrderNo(param.getString("ORDER_ID"));
        reqData.setSubscribeId(param.getString("SUBSCRIBE_ID"));
        reqData.setOprt(param.getString("OPRT"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ScoreRollBackRequestData();
    }

}
