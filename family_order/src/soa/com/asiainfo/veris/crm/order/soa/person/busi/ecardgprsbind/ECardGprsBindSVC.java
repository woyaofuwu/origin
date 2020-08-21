
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ECardGprsBindSVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset checkEcardPhone(IData input) throws Exception
    {
        ECardGprsBindBean bean = (ECardGprsBindBean) BeanManager.createBean(ECardGprsBindBean.class);

        return bean.checkEcardPhone(input);
    }

    public IDataset checkPhone(IData input) throws Exception
    {
        ECardGprsBindBean bean = (ECardGprsBindBean) BeanManager.createBean(ECardGprsBindBean.class);

        return bean.checkPhone(input);
    }

    public IDataset queryCrmInfos(IData input) throws Exception
    {
        ECardGprsBindBean bean = (ECardGprsBindBean) BeanManager.createBean(ECardGprsBindBean.class);
        return bean.queryCrmInfos(input);
    }

}
