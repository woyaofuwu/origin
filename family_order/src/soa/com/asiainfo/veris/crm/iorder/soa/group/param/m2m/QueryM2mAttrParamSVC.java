package com.asiainfo.veris.crm.iorder.soa.group.param.m2m;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryM2mAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitM2mCrtMbAttrParam(IData input) throws Exception
    {
        return  QueryCrtMbM2mAttrParamBean.queryM2mAttrForChgInit(input);
    }
}
