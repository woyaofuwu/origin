
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveactivate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SaleActiveActivateSVC extends CSBizService
{
    private static final long serialVersionUID = 6461122740648103556L;

    public IDataset activateActives(IData params) throws Exception
    {
        SaleActiveActivateBean saleActiveActivateBean = BeanManager.createBean(SaleActiveActivateBean.class);
        IDataset returnDataset = saleActiveActivateBean.activateActives(params);
        saleActiveActivateBean.updateActiveIntfInfo(params);
        return returnDataset;
    }

    public IDataset queryActivePackages(IData params) throws Exception
    {
        SaleActiveActivateBean saleActiveActivateBean = BeanManager.createBean(SaleActiveActivateBean.class);
        return saleActiveActivateBean.queryActivePackages(params.getString(Route.ROUTE_EPARCHY_CODE));
    }

    public IDataset queryActives(IData params) throws Exception
    {
        SaleActiveActivateBean saleActiveActivateBean = BeanManager.createBean(SaleActiveActivateBean.class);
        return saleActiveActivateBean.queryActives(params, this.getPagination());
    }
}
