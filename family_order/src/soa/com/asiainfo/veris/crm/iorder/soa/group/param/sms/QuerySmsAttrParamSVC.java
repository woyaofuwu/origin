package com.asiainfo.veris.crm.iorder.soa.group.param.sms;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QuerySmsAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitSmsCrtUsAttrParam(IData input) throws Exception
    {
        return  QuerySmsAttrParamBean.querySmsParamAttrForChgInit(input);
    }
    
    public IData getInitSmsChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsSmsAttrParamBean.querySmsAttrForChgInit(input);
    }
}
