package com.asiainfo.veris.crm.iorder.soa.group.param.pushemail;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryPushemailAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitPushemailChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsPushemailAttrParamBean.queryPushemailAttrForChgInit(input);
    }

}
