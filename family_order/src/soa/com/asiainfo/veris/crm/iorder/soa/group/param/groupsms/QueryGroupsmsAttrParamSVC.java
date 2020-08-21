package com.asiainfo.veris.crm.iorder.soa.group.param.groupsms;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryGroupsmsAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitGroupsmsChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsGroupsmsAttrParamBean.queryGroupsmsAttrForChgInit(input);
    }

}
