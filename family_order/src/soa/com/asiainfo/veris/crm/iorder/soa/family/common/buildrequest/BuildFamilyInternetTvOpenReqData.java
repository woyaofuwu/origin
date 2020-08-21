package com.asiainfo.veris.crm.iorder.soa.family.common.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata.FamilyInternetTvOpenRequestData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.buildrequest.BuildInternetTvOpenReqData;

/**
 * @author zhangxi
 */
public class BuildFamilyInternetTvOpenReqData extends BuildInternetTvOpenReqData implements IBuilder {

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {

        super.buildBusiRequestData(param, brd);

        FamilyInternetTvOpenRequestData reqData = (FamilyInternetTvOpenRequestData) brd;

        reqData.setFamilyMemberInstId(param.getString("FAMILY_MEMBER_INST_ID"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance() {
        return new FamilyInternetTvOpenRequestData();
    }
}
