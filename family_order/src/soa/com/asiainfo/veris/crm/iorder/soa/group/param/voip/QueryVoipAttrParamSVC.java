package com.asiainfo.veris.crm.iorder.soa.group.param.voip;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryVoipAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitVoipCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryVoipAttrParamBean.queryVoipParamAttrForChgInit(input);
    }
    
    public IData getInitVoipChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsVoipAttrParamBean.queryVoipParamAttrForChgInit(input);
    }

}
