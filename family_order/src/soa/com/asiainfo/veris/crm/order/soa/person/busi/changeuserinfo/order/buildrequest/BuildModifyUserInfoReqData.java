
package com.asiainfo.veris.crm.order.soa.person.busi.changeuserinfo.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeuserinfo.order.requestdata.ModifyUserInfoReqData;

/**
 * 用户资料修改请求数据处理类
 * 
 * @author liutt
 */
public class BuildModifyUserInfoReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData inParam, BaseReqData brd) throws Exception
    {
        ModifyUserInfoReqData reqData = (ModifyUserInfoReqData) brd;

        if (StringUtils.isBlank(inParam.getString("USER_TYPE_CODE")))// 判断一下是否存在，考虑接口没有该值传入的时候用老值
        {
            reqData.setUserTypeCode(reqData.getUca().getUser().getUserTypeCode());
        }
        else
        {
            reqData.setUserTypeCode(inParam.getString("USER_TYPE_CODE"));
        }

        // 校验担保人信息是否完整--全为空 或者 全有值
        if (((inParam.getString("ASSURE_PSPT_TYPE_CODE", "").equals("")) && (inParam.getString("ASSURE_PSPT_ID", "").equals("")) && (inParam.getString("ASSURE_NAME", "").equals("")))
                || ((!inParam.getString("ASSURE_PSPT_TYPE_CODE", "").equals("")) && (!inParam.getString("ASSURE_PSPT_ID", "").equals("")) && (!inParam.getString("ASSURE_NAME", "").equals(""))))
        {
        	
        	if(inParam.getString("ASSURE_NAME").contains("*") || inParam.getString("ASSURE_PSPT_ID").contains("*")){ 
            	String assureCustId = reqData.getUca().getUser().getAssureCustId(); 
                if(StringUtils.isNotBlank(assureCustId)){
                	IData custData = UcaInfoQry.qryCustInfoByCustId(assureCustId);            	
                	inParam.put("ASSURE_NAME",inParam.getString("ASSURE_NAME","").contains("*") ? custData.getString("CUST_NAME"):inParam.getString("ASSURE_NAME"));
                	inParam.put("ASSURE_PSPT_ID",inParam.getString("ASSURE_PSPT_ID","").contains("*") ? custData.getString("PSPT_ID"):inParam.getString("ASSURE_PSPT_ID"));
                }
        	}
            reqData.setAssureDate(inParam.getString("ASSURE_DATE"));
            reqData.setAssureName(inParam.getString("ASSURE_NAME"));
            reqData.setAssurePsptId(inParam.getString("ASSURE_PSPT_ID"));
            reqData.setAssurePsptTypeCode(inParam.getString("ASSURE_PSPT_TYPE_CODE"));
            reqData.setAssureTypeCode(inParam.getString("ASSURE_TYPE_CODE"));

        }
        else
        {
            CSAppException.apperr(CustException.CRM_CUST_14);// 尊敬的客户，您输入的担保人信息不完整，请验证后，重新输入！
        }
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new ModifyUserInfoReqData();
    }
}
