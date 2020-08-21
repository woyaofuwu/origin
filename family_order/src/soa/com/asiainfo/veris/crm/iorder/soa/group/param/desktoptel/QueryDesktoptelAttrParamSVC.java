package com.asiainfo.veris.crm.iorder.soa.group.param.desktoptel;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryDesktoptelAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData getInitDesktoptelCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryDesktoptelAttrParamBean.queryDesktoptelAttrForChgInit(input);
    }

    public IData getInitDesktoptelChgUsAttrParam(IData input) throws Exception
    {
        return  QueryDesktoptelChgUsAttrParamBean.queryDesktoptelAttrForChgInit(input);
    }
    public IData getInitDesktoptelChgMbAttrParam(IData input) throws Exception
    {
        return  QueryDesktoptelChgMbAttrParamBean.queryDesktoptelAttrForChgInit(input);
    }
}
