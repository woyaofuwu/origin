package com.asiainfo.veris.crm.iorder.soa.group.param.cttline;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryCttlineAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitCttlineCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryCttlineAttrParamBean.queryCttlineParamAttrForChgInit(input);
    }
    public IData getInitCttlineChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsCttlineAttrParamBean.queryCttlineParamAttrForChgInit(input);
    }
}
