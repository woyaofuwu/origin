package com.asiainfo.veris.crm.iorder.soa.group.param.gdzy;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryGdzyAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitGdzyCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryCrtGdzyAttrParamBean.queryGdzyParamAttrForCrtInit(input);
    }
    
    public IData getInitGdzyChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgGdzyAttrParamBean.queryGdzyParamAttrForChgInit(input);
    }
}
