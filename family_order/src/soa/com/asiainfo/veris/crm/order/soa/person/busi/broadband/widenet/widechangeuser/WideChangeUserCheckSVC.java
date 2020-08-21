
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WideChangeUserCheckSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 验证新号码能否办理宽带业务
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkNewNumForChangeUser(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        WideChangeUserCheckBean checkBean = BeanManager.createBean(WideChangeUserCheckBean.class);
        IData res = checkBean.checkNewNumForChangeUser(input);
        result.add(res);
        return result;
    }

    /**
     * 验证老号码能否把宽带业务变更走
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkOldNumForChangeUser(IData input) throws Exception
    {
        WideChangeUserCheckBean checkBean = BeanManager.createBean(WideChangeUserCheckBean.class);
        IData res = checkBean.checkOldNumForChangeUser(input);
        IDataset rtnSet=new DatasetList();
        rtnSet.add(res);
        return rtnSet;
    }
}
