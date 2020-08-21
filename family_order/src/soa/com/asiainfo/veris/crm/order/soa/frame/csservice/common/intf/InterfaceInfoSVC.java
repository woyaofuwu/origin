
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.intf.bean.InterfaceInfoBean;

public class InterfaceInfoSVC extends CSBizService
{

    public IDataset getInterfaceParam(IData data) throws Exception
    {
        InterfaceInfoBean bean = (InterfaceInfoBean) BeanManager.createBean(InterfaceInfoBean.class);
        return bean.getInterfaceParam(data);
    }

    public IDataset getSceneById(IData data) throws Exception
    {
        InterfaceInfoBean bean = (InterfaceInfoBean) BeanManager.createBean(InterfaceInfoBean.class);
        return bean.getSceneById(data);
    }

    public IDataset getSceneInfoById(IData data) throws Exception
    {
        InterfaceInfoBean bean = (InterfaceInfoBean) BeanManager.createBean(InterfaceInfoBean.class);
        return bean.getSceneInfoById(data);
    }

    public IDataset invokeInterface(IData data) throws Exception
    {
        InterfaceInfoBean bean = (InterfaceInfoBean) BeanManager.createBean(InterfaceInfoBean.class);
        return bean.invokeInterface(data);
    }

    public IDataset logInterface(IData data) throws Exception
    {
        InterfaceInfoBean bean = (InterfaceInfoBean) BeanManager.createBean(InterfaceInfoBean.class);
        bean.logInterface(data);
        return null;
    }

    public IDataset queryAllInterfaces(IData input) throws Exception
    {
        InterfaceInfoBean bean = (InterfaceInfoBean) BeanManager.createBean(InterfaceInfoBean.class);
        return bean.queryAllInterfaceInfos(input);
    }

    public IDataset queryInterfaceById(IData input) throws Exception
    {
        InterfaceInfoBean bean = (InterfaceInfoBean) BeanManager.createBean(InterfaceInfoBean.class);
        return bean.queryInterfaceById(input);
    }

    public boolean upInterfaceResultById(IData data) throws Exception
    {
        InterfaceInfoBean bean = (InterfaceInfoBean) BeanManager.createBean(InterfaceInfoBean.class);
        return bean.upInterfaceResultById(data);
    }
}
