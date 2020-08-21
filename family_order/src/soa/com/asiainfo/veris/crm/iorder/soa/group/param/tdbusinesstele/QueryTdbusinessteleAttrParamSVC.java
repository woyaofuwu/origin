package com.asiainfo.veris.crm.iorder.soa.group.param.tdbusinesstele;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryTdbusinessteleAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitTdbusinessteleChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsTdbusinessteleAttrParamBean.queryTdbusinessteleAttrForChgInit(input);
    }

}
