package com.asiainfo.veris.crm.iorder.soa.group.param.swbsim;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QuerySwbsimAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitSwbsimChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsSwbsimAttrParamBean.querySwbsimAttrForChgInit(input);
    }

}
