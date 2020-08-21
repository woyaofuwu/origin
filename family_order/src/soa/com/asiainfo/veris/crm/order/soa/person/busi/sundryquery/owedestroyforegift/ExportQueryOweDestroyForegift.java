
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.owedestroyforegift;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：欠费拆机用户押金清单的导出 作者：GongGuang
 */
public class ExportQueryOweDestroyForegift extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        getVisit().setStaffId(data.getString("STAFF_ID", "SUPERUSR"));// 设置登陆员工,因为导出需要校验权限
        IDataset res = CSAppCall.call("SS.QueryOweDestroyForegiftSVC.queryOweDestroyForegift", data);
        return res;
    }
}
