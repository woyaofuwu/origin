
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.queryvipcustinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryVIPCustInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 2394740971982801892L;

    /**
     * 异地大客户信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryVIPCustInfo(IData input) throws Exception
    {
        QueryVIPCustInfoBean bean = (QueryVIPCustInfoBean) BeanManager.createBean(QueryVIPCustInfoBean.class);
        return bean.queryVIPCustInfo(input);
    }

}
