package com.asiainfo.veris.crm.iorder.soa.group.param.colorring;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryColorringAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitColorringCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryColorringAttrParamBean.queryColorringParamAttrForChgInit(input);
    }
    public IData getInitColorringChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsColorringAttrParamBean.queryColorringAttrForChgInit(input);
    }
    public IData getInitColorringChgMbAttrParam(IData input) throws Exception
    {
        return  QueryChgMbColorringAttrParamBean.queryColorringAttrForChgInit(input);
    }
    
}
