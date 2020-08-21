
package com.asiainfo.veris.crm.order.soa.person.busi.ipexpressnobind;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class IpExpressNoBindSVC extends CSBizService
{
    public IDataset getIpExpressInfo(IData userInfo) throws Exception
    {
        IpExpressNoBindBean bean = BeanManager.createBean(IpExpressNoBindBean.class);
        IDataset returnInfos = new DatasetList();
        returnInfos.add(bean.getIpInfo(userInfo));
        return returnInfos;
    }
}
