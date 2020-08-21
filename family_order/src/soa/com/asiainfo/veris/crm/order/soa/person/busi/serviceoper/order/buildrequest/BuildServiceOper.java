
package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.requestdata.ServiceOperReqData;

public class BuildServiceOper extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ServiceOperReqData req = (ServiceOperReqData) brd;
        String elementId = param.getString("SERVICE_ID");
        if (StringUtils.isBlank(elementId))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_203);
        }
        String operCode = param.getString("OPER_CODE");
        if (StringUtils.isBlank(operCode))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_449);
        }
        req.setServiceId(elementId);
        req.setOperCode(operCode);
        req.setServType(param.getString("SERV_TYPE"));
        req.setSendFlag(param.getString("SEND_FLAG"));
        req.setGprsTotal(param.getString("GPRS_TOTAL", ""));
    }

    @Override
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        if ("770".equals(tradeTypeCode))
        {
            super.checkBefore(input, reqData);
        }

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ServiceOperReqData();
    }

}
