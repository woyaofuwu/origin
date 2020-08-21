package com.asiainfo.veris.crm.iorder.soa.group.param.etype;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryEtypeAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitEtypeChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsEtypeAttrParamBean.queryEtypeAttrForChgInit(input);
    }
    public IData getInitEtypeChgMbAttrParam(IData input) throws Exception
    {
        return  QueryChgMbEtypeAttrParamBean.queryEtypeAttrForChgInit(input);
    }
}
