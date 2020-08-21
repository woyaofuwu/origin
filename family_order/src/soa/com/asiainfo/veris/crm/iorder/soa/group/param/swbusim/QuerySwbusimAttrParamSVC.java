package com.asiainfo.veris.crm.iorder.soa.group.param.swbusim;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QuerySwbusimAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitSwbusimChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsSwbusimAttrParamBean.querySwbusimAttrForChgInit(input);
    }

}
