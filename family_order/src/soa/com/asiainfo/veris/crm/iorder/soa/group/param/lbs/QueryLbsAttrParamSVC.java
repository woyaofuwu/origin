package com.asiainfo.veris.crm.iorder.soa.group.param.lbs;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryLbsAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitLbsChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsLbsAttrParamBean.queryLbsParamAttrForChgInit(input);
    }

}
