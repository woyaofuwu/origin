
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QuerySnByUsrpidSVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public IDataset querySnByUsrpid(IData input) throws Exception
    {
        QuerySnByUsrpidBean bean = BeanManager.createBean(QuerySnByUsrpidBean.class);

        return bean.querySnByUsrpid(input, getPagination());
    }
    
    /**
     * 
     * @param input
     * @return
     * @throws Exception
     * @author chenhh6
     */
    public IDataset broadquerySnByUsrpid(IData input) throws Exception
    {
        QuerySnByUsrpidBean bean = BeanManager.createBean(QuerySnByUsrpidBean.class);

        return bean.broadquerySnByUsrpid(input, getPagination());
    }

}
