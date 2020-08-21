
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.dm.dmfirmwareupgrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class FirmwareUpgradeSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getFirmwareUpgradeDs(IData input) throws Exception
    {
        FirmwareUpgradeBean firmwareBean = BeanManager.createBean(FirmwareUpgradeBean.class);

        return firmwareBean.getFirmwareUpgradeDs(input);
    }

    public IDataset submitConfirmForm(IData input) throws Exception
    {
        FirmwareUpgradeBean firmwareBean = BeanManager.createBean(FirmwareUpgradeBean.class);

        return firmwareBean.submitConfirmForm(input);
    }
}
