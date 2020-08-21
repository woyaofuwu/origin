package com.asiainfo.veris.crm.iorder.soa.group.param.ttvpn;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryTtvpnAttrParamSVC extends CSBizService
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitTtvpnChgMbAttrParam(IData input) throws Exception
    {
        return  QueryChgMbTtvpnAttrParamBean.queryTtvpnAttrForChgInit(input);
    }
}

