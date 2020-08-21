package com.asiainfo.veris.crm.iorder.soa.group.param.ecard;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryEcardAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitEcardChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsEcardAttrParamBean.queryEcardAttrForChgInit(input);
    }
    public IData getInitEcardChgMbAttrParam(IData input) throws Exception
    {
        return  QueryChgMbEcardAttrParamBean.queryEcardAttrForChgInit(input);
    }
}
