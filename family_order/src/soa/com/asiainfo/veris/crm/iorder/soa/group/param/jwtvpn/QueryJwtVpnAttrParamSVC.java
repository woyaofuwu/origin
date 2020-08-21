package com.asiainfo.veris.crm.iorder.soa.group.param.jwtvpn;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryJwtVpnAttrParamSVC extends CSBizService
{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	public IData getInitJwtVpnCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryJwtVpnAttrParamBean.queryJwtVpnParamAttrForChgInit(input);
    }
    
    public IData getInitJwtVpnChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsJwtvpnAttrParamBean.queryJwtVpnAttrForChgInit(input);
    }
    
    public IData getInitJwtVpnCrtMbAttrParam(IData input) throws Exception
    {
        return  QueryCrtMbJwtVpnAttrParamBean.queryJwtVpnAttrForChgInit(input);
    }
    
    public IData getInitJwtVpnChgMbAttrParam(IData input) throws Exception
    {
        return  QueryChgMbJwtVpnAttrParamBean.queryJwtVpnAttrForChgInit(input);
    }
}

