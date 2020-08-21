
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UipInfoSVC extends CSBizService
{

    public IData getEFormInfo(IData inparam) throws Exception
    {
        UipInfoBean uipBean = BeanManager.createBean(UipInfoBean.class);
        IData result = uipBean.getEFormInfo(inparam);
        return result;
    }
    
    //工号是否已打印受理单查询接口
    public IData queryIsPrint(IData inparam) throws Exception
    {
        UipInfoBean uipBean = BeanManager.createBean(UipInfoBean.class);
        IData result = uipBean.queryIsPrint(inparam);
        return result;
    }
    
    //电子受理单补打查询接口
    public IData queryPrintCnote(IData inparam) throws Exception
    {
        UipInfoBean uipBean = BeanManager.createBean(UipInfoBean.class);
        IData result = uipBean.queryPrintCnote(inparam);
        return result;
    }
    
    //工号已打印生成受理单处理接口
    public IData dealPrintTag(IData inparam) throws Exception
    {
        UipInfoBean uipBean = BeanManager.createBean(UipInfoBean.class);
        IData result = uipBean.dealPrintTag(inparam);
        return result;
    }
}
