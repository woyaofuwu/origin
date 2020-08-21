package com.asiainfo.veris.crm.iorder.soa.group.param.wlw;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryWlwAttrParamSVC extends CSBizService{

private static final long serialVersionUID = 1L;
    
    public IData getInitWlwCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryWlwAttrParamBean.queryWlwParamAttrForCrtUsInit(input);
    }
    
    public IData getInitWlwChgUsAttrParam(IData input) throws Exception
    {
    	return QueryWlwAttrParamBean.queryWlwParamAttrForChgUsInit(input);
    }
    
    public IData getInitWlwCrtMbAttrParam(IData input) throws Exception
    {
    	return QueryWlwAttrParamBean.queryWlwParamAttrForCrtMbInit(input);
    }
    
    public IData getInitWlwChgMbAttrParam(IData input) throws Exception
    {
    	return QueryWlwAttrParamBean.queryWlwParamAttrForChgMbInit(input);
    }
    
//    public IData getInitWlwMbProductParam(IData input) throws Exception
//    {
//    	return QueryWlwAttrParamBean.queryWlwProductParam(input);
//    }
}
