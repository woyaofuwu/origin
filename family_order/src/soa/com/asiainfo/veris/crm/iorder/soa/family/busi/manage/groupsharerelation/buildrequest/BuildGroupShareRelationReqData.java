package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.groupsharerelation.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.groupsharerelation.requestdata.GroupShareRelationReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @auther : lixx9
 * @createDate :  2020/7/20
 * @describe :
 */
public class BuildGroupShareRelationReqData extends BaseBuilder implements IBuilder {

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {

        GroupShareRelationReqData reqData = (GroupShareRelationReqData) brd;

        reqData.setFamilySerialNumber(param.getString("FAMILY_SERIAL_NUMER",""));
        reqData.setMemberRelInstId(param.getString("MEMBER_REL_INST_ID",""));
        reqData.setMemberRoleCode(param.getString("MEMBER_ROLE_CODE",""));
        reqData.setTag(param.getString("TAG",""));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance() {
        return new GroupShareRelationReqData();
    }
}
