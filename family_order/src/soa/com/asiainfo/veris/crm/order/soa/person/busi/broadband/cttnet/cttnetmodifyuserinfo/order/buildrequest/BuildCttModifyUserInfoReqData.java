
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyuserinfo.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyuserinfo.order.requestdata.CttModifyUserInfoReqData;

/**
 * 用户资料修改请求数据处理类
 */
public class BuildCttModifyUserInfoReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData inParam, BaseReqData brd) throws Exception
    {
        CttModifyUserInfoReqData reqData = (CttModifyUserInfoReqData) brd;
        reqData.setUserTypeCode(inParam.getString("USER_TYPE_CODE"));
        reqData.setRemark(inParam.getString("REMARK"));
        reqData.setAssureDate(inParam.getString("ASSURE_DATE"));
        reqData.setAssureName(inParam.getString("ASSURE_NAME").contains("*") ? reqData.getUca().getCustomer().getCustName() : inParam.getString("ASSURE_NAME"));
        reqData.setAssurePsptId(inParam.getString("ASSURE_PSPT_ID").contains("*") ? reqData.getUca().getCustomer().getCustId() : inParam.getString("ASSURE_PSPT_ID"));
        reqData.setAssurePsptTypeCode(inParam.getString("ASSURE_PSPT_TYPE_CODE"));
        reqData.setAssureTypeCode(inParam.getString("ASSURE_TYPE_CODE"));
        reqData.setAreaType(inParam.getString("AREA_TYPE"));
        reqData.setClearAccount(inParam.getString("CLEAR_ACCOUNT"));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttModifyUserInfoReqData();
    }
}
