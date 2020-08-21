/**
 * ITF_CRM_CheckUserPWDUacp
 */

package com.asiainfo.veris.crm.order.soa.person.busi.userident;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserIdentSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * ITF_CRM_CheckUserPWDUacp
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset loginIdent(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        UserIdentBean bean = BeanManager.createBean(UserIdentBean.class);
        dataset.add(bean.loginIdent(input));
        return dataset;
    }
    
    /**
     * REQ201806080004+by mengqx 20180828
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset loginIdentNew(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        UserIdentBean bean = BeanManager.createBean(UserIdentBean.class);
        dataset.add(bean.loginIdentNew(input));
        return dataset;
    }

}
