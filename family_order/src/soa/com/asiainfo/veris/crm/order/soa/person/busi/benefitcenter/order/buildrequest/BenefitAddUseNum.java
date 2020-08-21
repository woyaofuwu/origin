package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.requestdata.BenefitAddUseNumReqDate;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/1/15 20:17
 */
public class BenefitAddUseNum extends BaseBuilder implements IBuilder {

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {
        BenefitAddUseNumReqDate request=(BenefitAddUseNumReqDate)brd;
        request.setDiscntCode(param.getString("DISCNT_CODE"));
        request.setRightId(param.getString("RIGHT_ID"));
        request.setModifyTag(param.getString("MODIFY_TAG"));
        request.setAddUseNum(param.getString("ADD_USE_NUM"));
        request.setAddUseNumType(param.getString("ADD_USE_NUM_TYPE"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance() {
         return new BenefitAddUseNumReqDate();
    }
}
