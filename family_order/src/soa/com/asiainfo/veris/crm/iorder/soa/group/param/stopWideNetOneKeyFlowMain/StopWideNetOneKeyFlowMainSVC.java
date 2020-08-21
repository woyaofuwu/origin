package com.asiainfo.veris.crm.iorder.soa.group.param.stopWideNetOneKeyFlowMain;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class StopWideNetOneKeyFlowMainSVC extends CSBizService

{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public IData StopWideNetOneKeyFlowMainParam(IData input) throws Exception
    {
        return  StopWideNetOneKeyFlowMainBean.queryUserInfosForInit(input);
    }
    
    public IData qryWaittingPayOrderByStaffId(IData input) throws Exception
    {
        return  StopWideNetOneKeyFlowMainBean.qryWaittingPayOrderByStaffId(input);
    }
  

}
