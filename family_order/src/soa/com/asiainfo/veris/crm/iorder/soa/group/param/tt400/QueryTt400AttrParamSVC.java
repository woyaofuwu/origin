package com.asiainfo.veris.crm.iorder.soa.group.param.tt400;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryTt400AttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitTt400ChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsTt400AttrParamBean.queryTt400AttrForChgInit(input);
    }

}
