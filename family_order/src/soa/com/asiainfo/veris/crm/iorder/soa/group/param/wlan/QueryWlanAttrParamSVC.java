package com.asiainfo.veris.crm.iorder.soa.group.param.wlan;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryWlanAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitWlanCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryWlanAttrParamBean.queryWlanParamAttrForChgInit(input);
    }
    
    public IData getInitWlanChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsWlanAttrParamBean.queryWlanParamAttrForChgInit(input);
    }

}
