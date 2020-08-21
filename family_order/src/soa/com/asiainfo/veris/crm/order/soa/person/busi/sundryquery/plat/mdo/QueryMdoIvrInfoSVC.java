
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.mdo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryMdoIvrInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * IVR拨打记录查询
     * 
     * @param input
     * @return
     * @throws Exception
     * @author xiekl
     */
    public IDataset queryUserMdoIvrInfo(IData input) throws Exception
    {

        QueryMdoIvrInfoBean bean = (QueryMdoIvrInfoBean) BeanManager.createBean(QueryMdoIvrInfoBean.class);
        return bean.queryUserMdoIvrInfo(input, getPagination());
    }
}
