package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.requestdata.BenefitUseReqData;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/10 9:53
 */
public class BuildBenefitUse extends BaseBuilder implements IBuilder {
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception{
        BenefitUseReqData request=(BenefitUseReqData)brd;
        request.setDiscntCode(param.getString("DISCNT_CODE"));
        request.setRelId(param.getString("REL_ID"));
        request.setRightId(param.getString("RIGHT_ID"));
        request.setStartDate(param.getString("START_DATE"));
        request.setEndDate(param.getString("END_DATE"));
        request.setModifyTag(param.getString("MODIFY_TAG"));
        request.setRightUseInfo(param.getData("RIGHT_USE_INFO"));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance(){
        return new BenefitUseReqData();
    }

}

