
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userreservecombo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUserReserveComboSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IDataset queryUserReserveDiscnt(IData data) throws Exception
    {
        QueryUserReserveComboBean bean = (QueryUserReserveComboBean) BeanManager.createBean(QueryUserReserveComboBean.class);
        return bean.queryUserReserveDiscnt(data, getPagination());
    }

    public IDataset queryUserReserveProduct(IData data) throws Exception
    {
        QueryUserReserveComboBean bean = (QueryUserReserveComboBean) BeanManager.createBean(QueryUserReserveComboBean.class);
        return bean.queryUserReserveProduct(data, getPagination());
    }

    public IDataset queryUserReserveService(IData data) throws Exception
    {
        QueryUserReserveComboBean bean = (QueryUserReserveComboBean) BeanManager.createBean(QueryUserReserveComboBean.class);
        return bean.queryUserReserveService(data, getPagination());
    }

    /**
     * 功能：用户预约产品查询 作者：GongGuang
     */
    public IDataset queryUserReserveTrade(IData data) throws Exception
    {
        QueryUserReserveComboBean bean = (QueryUserReserveComboBean) BeanManager.createBean(QueryUserReserveComboBean.class);
        return bean.queryUserReserveTrade(data, getPagination());
    }
}
