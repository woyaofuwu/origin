
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.requestdata.PostRepairInfoReqData;

/**
 * 邮寄信息补录信息数据请求处理类
 * 
 * @author liutt
 */
public class BuildPostRepairInfoReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        PostRepairInfoReqData reqData = (PostRepairInfoReqData) brd;
        reqData.setRepairReason(param.getString("REPAIR_REASON"));// 补寄原因编码
        reqData.setRepairMonth(param.getString("REPAIR_MONTH"));// 补寄月份
        reqData.setPostAddress(param.getString("POST_ADDRESS"));// 邮寄地址
        reqData.setPostCode(param.getString("POST_CODE"));// 邮递邮编
        reqData.setPostContent(param.getString("POST_CONTENT"));// 邮政投递内容拼串
        reqData.setEmailContent(param.getString("EMAIL_CONTENT"));
        reqData.setPostEmail(param.getString("EMAIL"));// Email地址
        reqData.setPostName(param.getString("POST_NAME"));// 邮寄名称
        reqData.setPostFaxNbr(param.getString("FAX_NBR"));// 传真号码

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new PostRepairInfoReqData();
    }

}
