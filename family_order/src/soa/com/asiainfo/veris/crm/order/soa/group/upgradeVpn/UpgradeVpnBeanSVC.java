
package com.asiainfo.veris.crm.order.soa.group.upgradeVpn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class UpgradeVpnBeanSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtBatUptoIms(IData inParam) throws Exception
    {
        UpgradeVpnBean bean = new UpgradeVpnBean();

        return bean.batDealImsVpmnInfo(inParam);
    }

    /**
     * VPN升级为跨省VPN或订购了“漫游短号服务”升级
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtBatUptoCountrywide(IData inParam) throws Exception
    {
        UpVpnToCountrywideBean bean = new UpVpnToCountrywideBean();
        return bean.batDealVpmnInfo(inParam);
    }

}
