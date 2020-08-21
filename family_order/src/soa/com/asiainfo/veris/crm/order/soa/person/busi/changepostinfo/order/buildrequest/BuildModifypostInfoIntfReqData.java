
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPostInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo.order.requestdata.ModifyPostInfoReqData;

/**
 * 邮寄信息变更接口build类
 * 
 * @author liutt
 */
public class BuildModifypostInfoIntfReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ModifyPostInfoReqData reqData = (ModifyPostInfoReqData) brd;
        reqData.setPostAddress(param.getString("POST_ADDRESS"));// 邮寄地址
        reqData.setPostCode(param.getString("POST_CODE"));// 邮递邮编
        reqData.setPostCyc(param.getString("POST_CYC")); // 邮递周期
        reqData.setPostEmail(param.getString("EMAIL"));// Email地址
        reqData.setPostName(param.getString("POST_NAME"));// 邮寄名称
        reqData.setPostTag(param.getString("POST_TAG", "0"));// 邮寄标志
        reqData.setPostFaxNbr(param.getString("FAX_NBR"));// 传真号码

        // 对于接口来说每次请求只有一种邮寄方式,而且不管是哪种方式，投递内容都放在POST_CONTENT字段中
        reqData.setPostType(param.getString("POST_TYPESET"));// 邮寄方式
        if (StringUtils.equals("1", param.getString("POST_TAG", "0")))
        {
            IDataUtil.chkParam(param, "POST_TYPESET");// 邮寄方式不能为空
            if (StringUtils.equals("0", reqData.getPostType()))
            {
                if (StringUtils.isBlank(param.getString("POST_ADDRESS")))
                {
                    // 邮寄方式为邮政投递，邮寄地址不能为空!
                    CSAppException.apperr(CrmCommException.CRM_COMM_1144);
                }
                reqData.setPostContent(param.getString("POST_CONTENT"));// 邮政投递内容拼串
            }
            else if (StringUtils.equals("2", reqData.getPostType()))
            {
                if (StringUtils.isBlank(param.getString("EMAIL")))
                {
                    // 邮寄方式为EMAIL投递，Email地址不能为空!
                    CSAppException.apperr(CrmCommException.CRM_COMM_1145);
                }
                reqData.setEmailContent(param.getString("POST_CONTENT"));// EMAIL投递内容拼串
            }

            this.dealParam(reqData, brd);
        }
    }

    public void dealParam(ModifyPostInfoReqData reqData, BaseReqData brd) throws Exception
    {
        IDataset dataset = UserPostInfoQry.qryUserPostInfo(brd.getUca().getUserId(), "1");
        boolean flag = false;
        IData postData = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                if (dataset.getData(i).getString("POST_TYPESET").equals(reqData.getPostType()))
                {
                    postData = dataset.getData(i);
                    flag = true;// 代表数据库中存在正在生效的此种邮寄方式
                    break;
                }
            }
        }
        // 电子渠道 传过来的数据 新增的时候表中不能为空的字段是必填的
        if (flag)
        {
            if (StringUtils.isBlank(reqData.getPostCode()))
            {
                reqData.setPostCode(postData.getString("POST_CODE"));
            }
            if (StringUtils.isBlank(reqData.getPostAddress()))
            {
                reqData.setPostAddress(postData.getString("POST_ADDRESS"));
            }
            if (StringUtils.isBlank(reqData.getPostEmail()))
            {
                reqData.setPostEmail(postData.getString("EMAIL"));
            }
            if (StringUtils.isBlank(reqData.getPostName()))
            {
                reqData.setPostName(postData.getString("POST_NAME"));
            }
            if (StringUtils.isBlank(reqData.getPostFaxNbr()))
            {
                reqData.setPostFaxNbr(postData.getString("FAX_NBR"));
            }
            if (StringUtils.isBlank(reqData.getPostTag()))
            {
                reqData.setPostTag(postData.getString("POST_TAG"));
            }
            if (StringUtils.isBlank(reqData.getPostContent()))
            {
                reqData.setPostContent(postData.getString("POST_CONTENT"));
            }
            if (StringUtils.isBlank(reqData.getPostType()))
            {
                reqData.setPostType(postData.getString("POST_TYPESET"));
            }
            if (StringUtils.isBlank(reqData.getPostCyc()))
            {
                reqData.setPostCyc(postData.getString("POST_CYC"));
            }
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ModifyPostInfoReqData();
    }

}
