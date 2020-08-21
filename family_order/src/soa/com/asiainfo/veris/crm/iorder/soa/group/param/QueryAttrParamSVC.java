package com.asiainfo.veris.crm.iorder.soa.group.param;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryAttrParamSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IData queryOfferChaForInit(IData input) throws Exception
    {
        return QueryAttrParamBean.queryOfferChaForInit(input);
    }
    
    public IData queryUserAttrForChgInit(IData input) throws Exception
    {
        return QueryAttrParamBean.queryUserAttrForChgInit(input);
    }
    
    
}
