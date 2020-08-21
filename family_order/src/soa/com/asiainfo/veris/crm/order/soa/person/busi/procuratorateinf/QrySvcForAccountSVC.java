
package com.asiainfo.veris.crm.order.soa.person.busi.procuratorateinf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QrySvcForAccountSVC extends CSBizService
{

    /**
     * 订购关系查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset selectOrderRelation(IData data) throws Exception
    {
        QrySvcForAccountBean bean = BeanManager.createBean(QrySvcForAccountBean.class);
        return bean.getOrderRelation(data);
    }

    /**
     * 查询无线音乐会员级别
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset selLeverByServiceId(IData data) throws Exception
    {
        QrySvcForAccountBean bean = BeanManager.createBean(QrySvcForAccountBean.class);
        return bean.getServiceAttr(data);
    }

}
