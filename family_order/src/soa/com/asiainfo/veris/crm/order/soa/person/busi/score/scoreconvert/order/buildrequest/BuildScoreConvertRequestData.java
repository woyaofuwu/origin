
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.requestdata.ScoreConvertRequestData;

public class BuildScoreConvertRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ScoreConvertRequestData reqData = (ScoreConvertRequestData) brd;
        reqData.setItemId(param.getString("POP_ITEM_ID"));
        reqData.setItemNum(param.getString("POP_ITEM_NUM"));

        reqData.setDelivProvince(param.getString("POP_DELIV_PROVINCE"));
        reqData.setCity(param.getString("POP_CITY"));
        reqData.setDistrict(param.getString("POP_DISTRICT"));
        reqData.setCusAddcode(param.getString("POP_CUS_ADDCODE"));
        reqData.setCusTel(param.getString("POP_CUS_TEL"));
        reqData.setDelivTimeReq(param.getString("POP_DELIV_TIME_REQ"));
        reqData.setCusAdd(param.getString("POP_CUS_ADD"));
        reqData.setRemark(param.getString("REMARK"));

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new ScoreConvertRequestData();
    }

}
