package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.administrator.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.administrator.requestdata.AdministratorChangeReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @auther : lixx9
 * @createDate :  2020/8/3
 * @describe :
 */
public class BuildAdministratorChangeReqData extends BaseBuilder implements IBuilder {


    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {
        AdministratorChangeReqData reqData = (AdministratorChangeReqData) brd;

        reqData.setMemberSerialNumber(param.getString("MEMBER_SERIAL_NUMBER",""));
        reqData.setFamilyUserId(param.getString("FAMILY_USER_ID",""));

    }

    @Override
    public BaseReqData getBlankRequestDataInstance() {
        return new AdministratorChangeReqData();
    }

}
