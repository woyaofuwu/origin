
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryWidenetInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 宽带用户根据产品查询产品服务接口
     * 
     * @param params
     * @return IDataset
     * @throws Exception
     * @author zhouwu
     * @date 2014-06-24 11:03:02
     */
    public IDataset getServiceByProduct(IData params) throws Exception
    {
        QueryWidenetInfoBean bean = BeanManager.createBean(QueryWidenetInfoBean.class);
        return bean.getServiceByProduct(params);
    }

    /**
     * 查询用户宽带账号资料
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getWidenetCustAcctInfo(IData data) throws Exception
    {
        QueryWidenetInfoBean bean = BeanManager.createBean(QueryWidenetInfoBean.class);
        return bean.getWidenetCustAcctInfo(data);
    }

    /**
     * 查询所有宽带产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getWidenetProductInfo(IData data) throws Exception
    {
        QueryWidenetInfoBean bean = BeanManager.createBean(QueryWidenetInfoBean.class);
        return bean.getAllWideNetProduct(data);
    }
}
