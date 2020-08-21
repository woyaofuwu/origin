package com.asiainfo.veris.crm.iorder.soa.group.param.mas;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.soa.group.param.adc.QueryAdcAttrParamBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryMasAttrParamSVC extends CSBizService{

private static final long serialVersionUID = 1L;
    
    public IData getInitMasCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryMasAttrParamBean.queryMasParamAttrForCrtUsInit(input);
    }
    
    public IData getInitMasChgUsAttrParam(IData input) throws Exception
    {
    	return QueryMasAttrParamBean.queryMasParamAttrForChgUsInit(input);
    }
    
    public IData getInitMasCrtMbAttrParam(IData input) throws Exception
    {
        return  QueryMasAttrParamBean.queryMasParamAttrForCrtMbInit(input);
    }
    
    public IData getInitMasChgMbAttrParam(IData input) throws Exception
    {
    	return QueryMasAttrParamBean.queryMasParamAttrForChgMbInit(input);
    }
}
