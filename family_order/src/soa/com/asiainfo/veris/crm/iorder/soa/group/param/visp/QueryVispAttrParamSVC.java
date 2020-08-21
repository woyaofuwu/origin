package com.asiainfo.veris.crm.iorder.soa.group.param.visp;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryVispAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitVispCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryVispAttrParamBean.queryVispParamAttrForChgInit(input);
    }

    public IData getInitVispChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsVispAttrParamBean.queryVispParamAttrForChgInit(input);
    }
}
