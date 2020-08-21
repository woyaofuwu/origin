
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreDonateRequestData;

public class BuildScoreDonateRequestData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // 入参校验
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "OBJECT_SERIAL_NUMBER");
        IDataUtil.chkParam(param, "DONATE_SCORE");
        ScoreDonateRequestData reqData = (ScoreDonateRequestData) brd;
        reqData.setDonatedUca(UcaDataFactory.getNormalUca(param.getString("OBJECT_SERIAL_NUMBER")));
        reqData.setDonateScore(param.getString("DONATE_SCORE"));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ScoreDonateRequestData();
    }

}
