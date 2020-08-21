
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.prosecution;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：用于垃圾短信查询的导出 作者：GongGuang
 */
public class ExportQueryProsecution extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataset res = CSAppCall.call("SS.QueryProsecutionSVC.queryProsecution", data);
        return res;
    }
}
