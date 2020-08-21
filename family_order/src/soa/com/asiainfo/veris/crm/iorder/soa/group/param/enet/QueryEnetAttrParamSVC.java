package com.asiainfo.veris.crm.iorder.soa.group.param.enet;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryEnetAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitEnetChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsEnetAttrParamBean.queryEnetAttrForChgInit(input);
    }
    public IData getInitEnetChgMbAttrParam(IData input) throws Exception
    {
        return  QueryChgMbEnetAttrParamBean.queryEnetAttrForChgInit(input);
    }
}
