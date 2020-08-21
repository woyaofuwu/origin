package com.asiainfo.veris.crm.iorder.soa.group.param.tt800;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryTt800AttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    
    public IData getInitTt800ChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsTt800AttrParamBean.queryTt800AttrForChgInit(input);
    }

}
