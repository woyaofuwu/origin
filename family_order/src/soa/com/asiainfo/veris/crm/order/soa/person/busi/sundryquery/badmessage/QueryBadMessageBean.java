
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.badmessage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryBadMessageQry;

/**
 * 不良短信查询
 * 
 * @author steven
 */
public class QueryBadMessageBean extends CSBizBean
{

    /**
     * 不良短信查询
     * 
     * @param data
     * @param page
     * @return
     * @throws Exception
     */
    public IDataset queryBadMessage(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String msISDN = data.getString("MSISDN", "");
        String content = data.getString("CONTENT", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");

        IDataset dataSet = QueryBadMessageQry.queryBadMessage(msISDN, content, startDate, endDate, routeEparchyCode, page);
        return dataSet;
    }

}
