package com.asiainfo.veris.crm.iorder.soa.group.param.dataline;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryDatalineAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitDatalineCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryDatalineAttrParamBean.queryDatalineParamAttrForChgInit(input);
    }
    
    public IData getInitDatalineChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsDatalineAttrParamBean.queryDatalineParamAttrForChgInit(input);
    }
}
