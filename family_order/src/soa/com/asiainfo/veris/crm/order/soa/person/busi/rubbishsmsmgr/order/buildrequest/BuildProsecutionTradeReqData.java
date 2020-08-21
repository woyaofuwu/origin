
package com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr.order.requestdata.ProsecutionTradeReqData;

public class BuildProsecutionTradeReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        String prosecutNum = param.getString("PROSECUTEE_NUMBER", "");
        if (StringUtils.isBlank(prosecutNum))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_323);
        }

        String sms = param.getString("SMS_CONTENT", "");
        if (StringUtils.isBlank(sms))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_455);
        }

        ProsecutionTradeReqData reqData = (ProsecutionTradeReqData) brd;
        String inModeCode = CSBizBean.getVisit().getInModeCode();
        String prosecutionWay = "";
        if (inModeCode.equals("0"))
        {
            prosecutionWay = "01";
        }
        else if (inModeCode.equals("5"))
        {
            prosecutionWay = "00";
        }
        else
        {
            prosecutionWay = "01";
        }

        reqData.setInModeCode(inModeCode);
        reqData.setProsecutionWay(prosecutionWay);
        reqData.setProsecutionNumber(prosecutNum);
        reqData.setSmsContent(sms);

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ProsecutionTradeReqData();
    }

}
