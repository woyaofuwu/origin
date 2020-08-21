package com.asiainfo.veris.crm.iorder.soa.group.param.bboss;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryBBossAttrParamSVC extends CSBizService{

private static final long serialVersionUID = 1L;
    
/**
 * 获得bboss受理初始化参数（业务代码等）
 * @param input
 * @return
 * @throws Exception
 */
public IData queryBBossUserAttrForChaInit(IData input) throws Exception{
    return QueryBBossAttrParamBean.queryBBossUserAttrForChaInit(input);
}
/**
 * 获得bboss初始化参数
 * @param idata
 * @return
 * @throws Exception
 */
public IData queryBBossUserAttrForChgInit(IData input) throws Exception{
    return QueryBBossAttrParamBean.queryBBossUserAttrForChgInit(input);
}

}
