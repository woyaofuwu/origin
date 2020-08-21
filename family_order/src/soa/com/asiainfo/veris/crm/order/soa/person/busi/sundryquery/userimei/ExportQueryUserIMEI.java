
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userimei;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：用于用户IMEI查询的导出 作者：GongGuang
 */
public class ExportQueryUserIMEI extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData param = paramIData.subData("cond", true);
        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataset res = CSAppCall.call("SS.QueryUserImeiSVC.queryUserImei", param);
        return res;
    }
}
