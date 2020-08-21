
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryusernpinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUserNpInfoSVC extends CSBizService
{
    public IDataset getUserNpTradeInfos(IData param) throws Exception
    {
        QueryUserNpInfoBean bean = (QueryUserNpInfoBean) BeanManager.createBean(QueryUserNpInfoBean.class);
        return bean.getUserNpTradeInfos(param, getPagination());
    }

    public IData updateDealTag(IData param) throws Exception
    {
        QueryUserNpInfoBean bean = (QueryUserNpInfoBean) BeanManager.createBean(QueryUserNpInfoBean.class);
        return bean.updateDealTag(param);
    }
}
