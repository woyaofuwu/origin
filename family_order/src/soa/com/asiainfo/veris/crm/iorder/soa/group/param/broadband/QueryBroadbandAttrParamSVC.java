package com.asiainfo.veris.crm.iorder.soa.group.param.broadband;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryBroadbandAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitBroadbandCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryBroadbandAttrParamBean.queryBroadbandParamAttrForChgInit(input);
    }
    public IData getInitBroadbandChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsBroadbandAttrParamBean.queryBroadbandAttrForChgInit(input);
    }
}
