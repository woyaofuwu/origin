
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmfirmwarereturn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FirmwareReturnSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getFirmwareReturnDs(IData input) throws Exception
    {
        FirmwareReturnBean firmwareBean = BeanManager.createBean(FirmwareReturnBean.class);

        return firmwareBean.getFirmwareReturnDs(input);
    }

    public IDataset submitConfirmForm(IData input) throws Exception
    {
        FirmwareReturnBean firmwareBean = BeanManager.createBean(FirmwareReturnBean.class);

        return firmwareBean.submitConfirmForm(input);
    }
}
