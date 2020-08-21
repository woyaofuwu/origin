
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.intf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.intf.bean.InterfaceCreateBean;

public class InterfaceCreateSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 8626743679032908495L;

    public IDataset createInterfaceInfos(IData input) throws Exception
    {
        InterfaceCreateBean bean = BeanManager.createBean(InterfaceCreateBean.class);

        return bean.createInterfaceInfos(input);
    }

    public IDataset createInterfaceScenes(IData input) throws Exception
    {
        InterfaceCreateBean bean = BeanManager.createBean(InterfaceCreateBean.class);

        return bean.createInterfaceScenes(input);
    }

    public IDataset deleteInterfaceScene(IData input) throws Exception
    {
        InterfaceCreateBean bean = BeanManager.createBean(InterfaceCreateBean.class);
        bean.deleteInterfaceScene(input);

        return null;
    }

    public IDataset queryInterfaceByCode(IData input) throws Exception
    {
        InterfaceCreateBean bean = BeanManager.createBean(InterfaceCreateBean.class);

        return bean.queryInterfaceByCode(input);
    }

    public IDataset querySceneByInterId(IData input) throws Exception
    {
        InterfaceCreateBean bean = BeanManager.createBean(InterfaceCreateBean.class);

        return bean.querySceneByInterId(input);
    }

    public IDataset updateInterfaceInfos(IData input) throws Exception
    {
        InterfaceCreateBean bean = BeanManager.createBean(InterfaceCreateBean.class);
        return bean.updateInterfaceInfos(input);
    }
}
