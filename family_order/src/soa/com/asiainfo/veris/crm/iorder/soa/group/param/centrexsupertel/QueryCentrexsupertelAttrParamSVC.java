package com.asiainfo.veris.crm.iorder.soa.group.param.centrexsupertel;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryCentrexsupertelAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitCentrexsupertelCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryCentrexsupertelAttrParamBean.queryCentrexsupertelAttrForChgInit(input);
    }
    
    public IData getInitCentrexsupertelChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsCentrexsupertelAttrParamBean.queryCentrexsupertelAttrForChgInit(input);
    }

    public IData getInitCentrexsupertelCrtMbAttrParam(IData input) throws Exception
    {
        return  QueryCrtMbCentrexsupertelAttrParamBean.queryCentrexsupertelAttrForChgInit(input);
    }
    
    public IData getInitCentrexsupertelChgMbAttrParam(IData input) throws Exception
    {
        return  QueryChgMbCentrexsupertelAttrParamBean.queryCentrexsupertelAttrForChgInit(input);
    }
}
