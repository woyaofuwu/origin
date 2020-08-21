
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SalePackagesFilteSVC extends CSBizService
{

    private static final long serialVersionUID = 7764319528361882884L;

    /**
     * 根据终端的特殊配置，过滤用户可办理的营销包！
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset filterPackagesByTerminalConfig(IData input) throws Exception
    {
        SalePackagesFilteBean filteBean = BeanManager.createBean(SalePackagesFilteBean.class);
        return filteBean.filterPackagesByTerminalConfig(input);
    }
}
