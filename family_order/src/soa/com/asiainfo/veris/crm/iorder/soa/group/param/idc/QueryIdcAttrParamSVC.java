package com.asiainfo.veris.crm.iorder.soa.group.param.idc;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.soa.group.param.visp.QueryChgUsVispAttrParamBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryIdcAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitIdcCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryIdcAttrParamBean.queryIdcParamAttrForChgInit(input);
    }
    
    public IData getInitIdcChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsIdcAttrParamBean.queryIdcParamAttrForChgInit(input);
    }

}
