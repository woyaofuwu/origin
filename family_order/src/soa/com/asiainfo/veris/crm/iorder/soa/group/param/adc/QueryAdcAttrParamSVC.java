package com.asiainfo.veris.crm.iorder.soa.group.param.adc;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryAdcAttrParamSVC extends CSBizService{

private static final long serialVersionUID = 1L;
    
    public IData getInitAdcCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryAdcAttrParamBean.queryAdcParamAttrForCrtUsInit(input);
    }
    
    public IData getInitAdcChgUsAttrParam(IData input) throws Exception
    {
    	return QueryAdcAttrParamBean.queryAdcParamAttrForChgUsInit(input);
    }
    
    public IData getInitAdcCrtMbAttrParam(IData input) throws Exception
    {
        return  QueryAdcAttrParamBean.queryAdcParamAttrForCrtMbInit(input);
    }
    
    public IData getInitAdcChgMbAttrParam(IData input) throws Exception
    {
    	return QueryAdcAttrParamBean.queryAdcParamAttrForChgMbInit(input);
    }
    
    //ADC一卡通产品参数信息
    public IData getInitAdcYiKaTongCrtUsParam(IData input) throws Exception
    {
    	return QueryAdcAttrParamBean.queryAdcYiKaTongParamForCrtUsInit(input);
    }
    
    public IData getInitAdcYiKaTongChgUsParam(IData input) throws Exception
    {
    	return QueryAdcAttrParamBean.queryAdcYiKaTongParamForChgUsInit(input);
    }
    
    //和校园（原校讯通）成员产品参数信息
    public IData getInitXxtCrtMbParam(IData input) throws Exception
    {
    	return QueryAdcAttrParamBean.queryXxtParamForCrtMbInit(input);
    }
    
    public IData getInitXxtChgMbParam(IData input) throws Exception
    {
    	return QueryAdcAttrParamBean.queryXxtParamForChgMbInit(input);
    }
}
