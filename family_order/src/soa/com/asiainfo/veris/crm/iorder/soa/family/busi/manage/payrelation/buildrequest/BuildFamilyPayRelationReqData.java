package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.payrelation.buildrequest;

import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.groupsharerelation.buildrequest.BuildGroupShareRelationReqData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.payrelation.requestdata.FamilyPayRelationReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @auther : lixx9
 * @createDate :  2020/8/4
 * @describe :
 */
public class BuildFamilyPayRelationReqData extends BuildGroupShareRelationReqData implements IBuilder {


    @Override
    public BaseReqData getBlankRequestDataInstance() {
        return new FamilyPayRelationReqData();
    }

}
