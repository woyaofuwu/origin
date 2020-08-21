/**
 * 充值卡充值
 */

package com.asiainfo.veris.crm.order.soa.person.busi.rechargedcard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RechargedCardSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * ITF_CRM_LocalCardRecharge
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset localRecharge(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        LocalRechargedCardBean bean = BeanManager.createBean(LocalRechargedCardBean.class);
        dataset.add(bean.recharge(input));
        return dataset;
    }

    /**
     * ITF_CRM_PrepaidCardRecharge
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset recharge(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        RechargedCardBean bean = BeanManager.createBean(RechargedCardBean.class);
        dataset.add(bean.recharge(input));
        return dataset;
    }

}
