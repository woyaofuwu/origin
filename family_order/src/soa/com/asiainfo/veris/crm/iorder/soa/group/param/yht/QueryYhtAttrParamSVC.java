package com.asiainfo.veris.crm.iorder.soa.group.param.yht;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryYhtAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    //集团受理初始化 
    public IData getInitYhtCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryYhtAttrParamBean.queryYhtParamAttrForChgInit(input);
    }

    public IData getInitYhtCrtMbAttrParam(IData input) throws Exception
    {
        return  QueryCrtMbYhtAttrParamBean.queryYhtParamAttrForChgInit(input);
    }

}
