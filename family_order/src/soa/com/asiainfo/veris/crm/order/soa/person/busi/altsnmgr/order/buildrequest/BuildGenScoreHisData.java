
package com.asiainfo.veris.crm.order.soa.person.busi.altsnmgr.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.altsnmgr.order.requestdata.GenScoreHisReqData;

public class BuildGenScoreHisData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        GenScoreHisReqData reqData = (GenScoreHisReqData) brd;
        String scoreValue = param.getString("SCORE_VALUE");
        String scoreChanged = param.getString("SCORE_CHANGED");
        String valueChanged = param.getString("VALUE_CHANGED");

        reqData.setScoreValue(scoreValue);
        reqData.setScoreChanged(scoreChanged);
        reqData.setValueChanged(valueChanged);

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new GenScoreHisReqData();
    }

}
