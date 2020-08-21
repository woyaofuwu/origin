
package com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SaleActiveTransSVC extends CSBizService
{

    private static final long serialVersionUID = -2314913934170417753L;

    public IDataset queryTransSaleActives(IData params) throws Exception
    {
        String serialNumber = params.getString("SERIAL_NUMBER");
        String eparchyCode = params.getString(Route.ROUTE_EPARCHY_CODE);
        SaleActiveTransBean saleActiveTransBean = BeanManager.createBean(SaleActiveTransBean.class);
        return saleActiveTransBean.queryTransSaleActives(serialNumber, eparchyCode);
    }

}
