
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.mdo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryMdoInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询用户mdo订购关系
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUserMdoSvcInfo(IData input) throws Exception
    {

        QueryMdoInfoBean bean = (QueryMdoInfoBean) BeanManager.createBean(QueryMdoInfoBean.class);
        return bean.queryUserMdoSvcInfo(input, getPagination());
    }

}
