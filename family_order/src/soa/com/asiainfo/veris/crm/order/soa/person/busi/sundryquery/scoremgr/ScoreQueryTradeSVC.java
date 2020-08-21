
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.scoremgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ScoreQueryTradeSVC extends CSBizService
{
    public IData getCommInfo(IData input) throws Exception
    {
        ScoreQueryTradeBean scoreQueryTradeBean = (ScoreQueryTradeBean) BeanManager.createBean(ScoreQueryTradeBean.class);
        return scoreQueryTradeBean.getCommInfo(input);
    }

    public IDataset queryScoreBizInfos(IData input) throws Exception
    {
        ScoreQueryTradeBean scoreQueryTradeBean = (ScoreQueryTradeBean) BeanManager.createBean(ScoreQueryTradeBean.class);
        return scoreQueryTradeBean.queryScoreBizInfos(input, getPagination());
    }

    public IDataset queryScoreDetail(IData input) throws Exception
    {
        ScoreQueryTradeBean scoreQueryTradeBean = (ScoreQueryTradeBean) BeanManager.createBean(ScoreQueryTradeBean.class);
        return scoreQueryTradeBean.queryScoreDetail(input, getPagination());
    }

    public IDataset queryScoreExchangeYear(IData input) throws Exception
    {
        ScoreQueryTradeBean scoreQueryTradeBean = (ScoreQueryTradeBean) BeanManager.createBean(ScoreQueryTradeBean.class);
        return scoreQueryTradeBean.queryScoreExchangeYear(input);
    }

    public IDataset queryScoreType(IData input) throws Exception
    {
        ScoreQueryTradeBean scoreQueryTradeBean = (ScoreQueryTradeBean) BeanManager.createBean(ScoreQueryTradeBean.class);
        input.put("IN_MODE_CODE", input.getString("X_IN_MODE_CODE"));
        return scoreQueryTradeBean.queryScoreType(input);
    }

    public IDataset queryYearSumScore(IData input) throws Exception
    {
        ScoreQueryTradeBean scoreQueryTradeBean = (ScoreQueryTradeBean) BeanManager.createBean(ScoreQueryTradeBean.class);
        return scoreQueryTradeBean.queryYearSumScore(input);
    }
}
