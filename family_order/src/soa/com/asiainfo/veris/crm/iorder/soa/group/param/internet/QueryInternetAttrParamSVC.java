package com.asiainfo.veris.crm.iorder.soa.group.param.internet;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryInternetAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitInternetCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryInternetAttrParamBean.queryInternetParamAttrForChgInit(input);
    }
    
    public IData getInitInternetChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsInternetAttrParamBean.queryInternetParamAttrForChgInit(input);
    }

}
