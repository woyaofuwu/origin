
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.presentscore;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;

public class QueryPresentScoreBean extends CSBizBean
{

    public IDataset queryPresentScore(IData data, Pagination page) throws Exception
    {
        return TradeBhQry.queryPresentScoreBySN(data, page, data.getString(Route.ROUTE_EPARCHY_CODE)); // 沿用了老的方法，未新增，名字可能有歧义，实际SQL中和前台传入，都无SN

    }

    /**
     * 数据导出
     * 
     * @param data
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public IDataset queryPresentScore(IData data, String eparchy_code) throws Exception
    {

        return TradeBhQry.queryPresentScoreBySN(data, null, eparchy_code); // 沿用了老的方法，未新增，名字可能有歧义，实际SQL中和前台传入，都无SN
    }

}
