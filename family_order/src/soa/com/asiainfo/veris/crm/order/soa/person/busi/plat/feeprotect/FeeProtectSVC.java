
package com.asiainfo.veris.crm.order.soa.person.busi.plat.feeprotect;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FeeProtectSVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(FeeProtectSVC.class);

    private static final long serialVersionUID = 1L;

    public IDataset getFeeProtectDiscntInfo(IData userInfo) throws Exception
    {
        FeeProtectBean bean = (FeeProtectBean) BeanManager.createBean(FeeProtectBean.class);
        return bean.getFeeProtectDiscntInfo(userInfo);
    }
    
}
