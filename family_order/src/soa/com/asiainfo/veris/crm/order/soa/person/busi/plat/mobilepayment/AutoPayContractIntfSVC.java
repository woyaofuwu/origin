
package com.asiainfo.veris.crm.order.soa.person.busi.plat.mobilepayment;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AutoPayContractIntfSVC extends CSBizService
{

    public IData autoExpendContract(IData param) throws Exception
    {
        return AutoPayContractIntfBean.autoExpendContract(param);
    }

    // public IData getAutoContractInfo(IData param) throws Exception
    // {
    // AutoPayContractIntfBean bean = (AutoPayContractIntfBean) BeanManager.createBean(AutoPayContractIntfBean.class);
    // return bean.getAutoContractInfo(param);
    // }

    /**
     * 手机支付签约变更
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData changeAutoContractInfo(IData param) throws Exception
    {
        return AutoPayContractIntfBean.changeAutoContractInfo(param);
    }

    /**
     * 手机支付签约删除
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData removeAutoContract(IData param) throws Exception
    {
        return AutoPayContractIntfBean.removeAutoContract(param);
    }
}
