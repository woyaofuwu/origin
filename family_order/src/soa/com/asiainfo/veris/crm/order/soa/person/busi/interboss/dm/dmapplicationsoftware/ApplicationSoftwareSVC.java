
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmapplicationsoftware;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ApplicationSoftwareSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset querySoftwareInfo(IData input) throws Exception
    {
        ApplicationSoftwareBean softwareBean = BeanManager.createBean(ApplicationSoftwareBean.class);

        return softwareBean.getApplicationSoftwareDs(input);
    }

    public IDataset submitConfirmForm(IData input) throws Exception
    {
        ApplicationSoftwareBean softwareBean = BeanManager.createBean(ApplicationSoftwareBean.class);

        return softwareBean.submitConfirmForm(input);
    }
}
