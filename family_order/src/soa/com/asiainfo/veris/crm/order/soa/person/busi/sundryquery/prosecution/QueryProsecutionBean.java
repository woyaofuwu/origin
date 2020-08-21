
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.prosecution;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryProsecutionQry;

public class QueryProsecutionBean extends CSBizBean
{
    /**
     * 功能：用于查询垃圾短信 作者：GongGuang
     */
    public IDataset queryProsecution(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String serialNum = data.getString("SERIAL_NUMBER", "");
        String prosecuteeNum = data.getString("PROSECUTEENUM", "");
        String tagProsecution = data.getString("PROSECUTION_WAY", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        if (endDate == null || endDate.equals(""))
        {
        }
        else
        {
            endDate = endDate + SysDateMgr.getEndTime235959();
        }

        if (tagProsecution.equals("10"))
        {
            tagProsecution = null;
        }
        IDataset dataSet = QueryProsecutionQry.queryProsecution(serialNum, routeEparchyCode, startDate, endDate, prosecuteeNum, tagProsecution, page);
        return dataSet;
    }
}
