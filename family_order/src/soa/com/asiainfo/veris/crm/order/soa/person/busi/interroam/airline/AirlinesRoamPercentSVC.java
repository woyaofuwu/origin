
package com.asiainfo.veris.crm.order.soa.person.busi.interroam.airline;

import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;

public class AirlinesRoamPercentSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset loadDiscntInfo(IData userInfo) throws Exception
    {
    	AirlinesRoamPercentBean bean = (AirlinesRoamPercentBean) BeanManager.createBean(AirlinesRoamPercentBean.class);
        return bean.loadDiscntInfo(userInfo);
    }

    public IDataset submit(IData userInfo) throws Exception
    {
    	AirlinesRoamPercentBean bean = (AirlinesRoamPercentBean) BeanManager.createBean(AirlinesRoamPercentBean.class);
        return bean.submit(userInfo);
    }
}
