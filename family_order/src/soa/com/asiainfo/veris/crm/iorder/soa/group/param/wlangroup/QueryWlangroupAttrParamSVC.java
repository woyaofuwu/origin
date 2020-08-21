package com.asiainfo.veris.crm.iorder.soa.group.param.wlangroup;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryWlangroupAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitWlangroupChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsWlangroupAttrParamBean.queryWlangroupAttrForChgInit(input);
    }

}
