package com.asiainfo.veris.crm.iorder.soa.group.param.imsvpn;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryImsvpnAttrParamSVC extends CSBizService

{

    /**getInitImsvpnCrtMbAttrParam
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitImsvpnCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryImsvpnAttrParamBean.queryImsvpnParamAttrForChgInit(input);
    }

    public IData getInitImsvpnChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsImsvpnAttrParamBean.queryImsvpnParamAttrForChgInit(input);
    }

    public IData getInitImsvpnCrtMbAttrParam(IData input) throws Exception
    {
        return  QueryCrtMbImsvpnAttrParamBean.queryImsvpnParamAttrForChgInit(input);
    }
    public IData getInitImsvpnChgMbAttrParam(IData input) throws Exception
    {
        return  QueryChgMbImsvpnAttrParamBean.queryImsvpnParamAttrForChgInit(input);
    }
}
