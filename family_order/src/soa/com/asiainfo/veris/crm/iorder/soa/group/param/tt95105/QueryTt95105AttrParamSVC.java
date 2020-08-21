package com.asiainfo.veris.crm.iorder.soa.group.param.tt95105;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryTt95105AttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitTt95105ChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsTt95105AttrParamBean.queryTt95105AttrForChgInit(input);
    }

}
