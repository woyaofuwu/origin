package com.asiainfo.veris.crm.iorder.soa.group.param.parentvpn;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryParentvpnAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    //集团受理初始化
    public IData getInitParentvpnCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryParentvpnAttrParamBean.queryParentvpnParamAttrForChgInit(input);
    }
    
    public IData getInitParentvpnChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsParentvpnAttrParamBean.queryParentvpnParamAttrForChgInit(input);
    }
}
