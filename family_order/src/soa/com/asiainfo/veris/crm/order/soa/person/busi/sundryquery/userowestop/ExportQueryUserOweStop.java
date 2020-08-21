
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userowestop;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：执行欠费停机用户查询的导出 作者：GongGuang
 */
public class ExportQueryUserOweStop extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData param = paramIData.subData("cond", true);
        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset res = CSAppCall.call("SS.QueryUserOweStopSVC.queryUserOweStop", param);
        return res;
    }
}
