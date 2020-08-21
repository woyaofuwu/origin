
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.usertradescore;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUserTradeScoreSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IData queryInitCondition(IData param) throws Exception
    {
        IData result = new DataMap();
        String startDate = SysDateMgr.getFirstDayOfThisMonth();
        String endDate = SysDateMgr.getSysDate();
        result.put("START_DATE", startDate);
        result.put("END_DATE", endDate);
        return result;
    }

    public IDataset queryScoreDetailInfoByTradeId(IData input) throws Exception
    {
        QueryUserTradeScoreBean userTradeScoreBean = (QueryUserTradeScoreBean) BeanManager.createBean(QueryUserTradeScoreBean.class);
        return userTradeScoreBean.queryScoreDetailInfoByTradeId(input, getPagination());
    }

    public IDataset querySNByTradeId(IData input) throws Exception
    {
        QueryUserTradeScoreBean userTradeScoreBean = (QueryUserTradeScoreBean) BeanManager.createBean(QueryUserTradeScoreBean.class);
        return userTradeScoreBean.querySNByTradeId(input, getPagination());
    }

    /**
     * 功能：用于积分兑换明细查询 作者：GongGuang
     */
    public IDataset queryUserTradeScore(IData data) throws Exception
    {
        QueryUserTradeScoreBean bean = (QueryUserTradeScoreBean) BeanManager.createBean(QueryUserTradeScoreBean.class);
        return bean.queryUserTradeScore(data, getPagination());
    }
}
