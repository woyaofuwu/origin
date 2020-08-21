
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.score;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryScoreInfoSVC extends CSBizService
{
    public IData getCommInfo(IData input) throws Exception
    {
        QueryScoreInfoBean QueryScoreInfoBean = (QueryScoreInfoBean) BeanManager.createBean(QueryScoreInfoBean.class);
        return QueryScoreInfoBean.getCommInfo(input);
    }

    public IDataset queryScoreBizInfos(IData input) throws Exception
    {
        QueryScoreInfoBean QueryScoreInfoBean = (QueryScoreInfoBean) BeanManager.createBean(QueryScoreInfoBean.class);
        return QueryScoreInfoBean.queryScoreBizInfos(input, getPagination());
    }

    public IDataset queryScoreDetail(IData input) throws Exception
    {
        QueryScoreInfoBean QueryScoreInfoBean = (QueryScoreInfoBean) BeanManager.createBean(QueryScoreInfoBean.class);
        return QueryScoreInfoBean.queryScoreDetail(input, getPagination());
    }

    public IDataset queryScoreExchangeYear(IData input) throws Exception
    {
        QueryScoreInfoBean QueryScoreInfoBean = (QueryScoreInfoBean) BeanManager.createBean(QueryScoreInfoBean.class);
        return QueryScoreInfoBean.queryScoreExchangeYear(input);
    }

    public IDataset queryScoreType(IData input) throws Exception
    {
        QueryScoreInfoBean QueryScoreInfoBean = (QueryScoreInfoBean) BeanManager.createBean(QueryScoreInfoBean.class);
        input.put("IN_MODE_CODE", input.getString("X_IN_MODE_CODE"));
        return QueryScoreInfoBean.queryScoreType(input);
    }

    public IDataset queryYearSumScore(IData input) throws Exception
    {
        QueryScoreInfoBean QueryScoreInfoBean = (QueryScoreInfoBean) BeanManager.createBean(QueryScoreInfoBean.class);
        return QueryScoreInfoBean.queryYearSumScore(input);
    }
}
