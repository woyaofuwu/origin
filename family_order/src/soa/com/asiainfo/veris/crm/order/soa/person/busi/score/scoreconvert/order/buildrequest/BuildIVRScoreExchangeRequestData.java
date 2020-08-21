
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.requestdata.ScoreConvertRequestData;

public class BuildIVRScoreExchangeRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ScoreConvertRequestData reqData = (ScoreConvertRequestData) brd;
        reqData.setItemId(param.getString("RULE_ID"));
        reqData.setItemNum(param.getString("COUNT"));

        reqData.setDelivProvince(param.getString("DELIV_PROVINCE"));
        reqData.setCity(param.getString("CITY"));
        reqData.setDistrict(param.getString("DISTRICT"));
        reqData.setCusAddcode(param.getString("CUS_ADDCODE"));
        reqData.setCusTel(param.getString("CUS_TEL"));
        reqData.setDelivTimeReq(param.getString("DELIV_TIME_REQ"));
        reqData.setCusAdd(param.getString("CUS_ADD"));

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new ScoreConvertRequestData();
    }

}
