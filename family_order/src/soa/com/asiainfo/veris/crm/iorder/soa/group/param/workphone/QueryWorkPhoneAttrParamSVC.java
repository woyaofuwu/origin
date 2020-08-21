package com.asiainfo.veris.crm.iorder.soa.group.param.workphone;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryWorkPhoneAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitWorkPhoneCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryWorkPhoneAttrParamBean.queryWorkPhoneParamAttrForChgInit(input);
    }
    
    public IData getInitWorkPhoneChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsWorkPhoneAttrParamBean.queryWorkPhoneAttrForChgInit(input);
    }
    
    public IData getInitWorkPhoneCrtMbAttrParam(IData input) throws Exception
    {
        return  QueryCrtMbWorkPhoneAttrParamBean.queryWorkPhoneAttrForChgInit(input);
    }
    public IData getInitWorkPhoneChgMbAttrParam(IData input) throws Exception
    {
        return  QueryChgMbWorkphoneAttrParamBean.queryWorkPhoneAttrForChgInit(input);
    }
}
