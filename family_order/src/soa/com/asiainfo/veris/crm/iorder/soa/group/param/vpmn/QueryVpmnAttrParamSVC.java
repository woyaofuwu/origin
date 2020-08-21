package com.asiainfo.veris.crm.iorder.soa.group.param.vpmn;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryVpmnAttrParamSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    //集团受理初始化
    public IData getInitVpmnCrtUsAttrParam(IData input) throws Exception
    {
        return  QueryVpmnAttrParamBean.queryVpnParamAttrForChgInit(input);
    }
    //集团变更初始化
    public IData getInitVpmnChgUsAttrParam(IData input) throws Exception
    {
        return  QueryChgUsVpmnAttrParamBean.queryVpnParamAttrForChgInit(input);
    }
    
    //成员新增初始化
    public IData getInitVpmnCrtMbAttrParam(IData input) throws Exception
    {
        return  QueryCrtMbVpmnAttrParamBean.queryVpnParamAttrForCrtInit(input);
    }
    
  //成员变更初始化
    public IData getInitVpmnChgMbAttrParam(IData input) throws Exception
    {
        return  QueryChgMbVpmnAttrParamBean.queryVpnParamAttrForChgInit(input);
    }

}
