
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.requestdata.ModifyPostInfoReqData;

public class BuildModifypostInfoReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ModifyPostInfoReqData reqData = (ModifyPostInfoReqData) brd;
        reqData.setPostAddress(param.getString("POST_ADDRESS"));// 邮寄地址
        reqData.setPostCode(param.getString("POST_CODE"));// 邮递邮编
        reqData.setPostContent(param.getString("POST_CONTENT"));// 邮政投递内容拼串
        reqData.setMMScontent(param.getString("MMS_CONTENT"));
        reqData.setEmailContent(param.getString("EMAIL_CONTENT"));
        reqData.setPostCyc(param.getString("POST_CYC")); // 邮递周期
        reqData.setPostEmail(param.getString("EMAIL"));// Email地址
        reqData.setPostName(param.getString("POST_NAME"));// 邮寄名称
        reqData.setPostTag(param.getString("POST_TAG", "0"));// 邮寄标志
        reqData.setPostType(param.getString("POST_TYPE"));// 邮寄方式拼串
        reqData.setPostFaxNbr(param.getString("FAX_NBR"));// 传真号码
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ModifyPostInfoReqData();
    }

}
