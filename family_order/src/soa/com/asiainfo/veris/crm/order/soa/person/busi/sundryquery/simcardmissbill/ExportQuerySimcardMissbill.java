
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.simcardmissbill;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：SIM卡漏账查询导出 作者：GongGuang
 */
public class ExportQuerySimcardMissbill extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataset res = CSAppCall.call("SS.QuerySimcardMissbillSVC.querySimcardMissbill", data);
        return res;
    }
}
