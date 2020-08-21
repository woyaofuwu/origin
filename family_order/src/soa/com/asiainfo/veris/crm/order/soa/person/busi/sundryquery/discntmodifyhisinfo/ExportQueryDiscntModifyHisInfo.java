
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.discntmodifyhisinfo;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;

/**
 * 功能：优惠变更历史查询清单的导出 作者：GongGuang
 */
public class ExportQueryDiscntModifyHisInfo extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        getVisit().setStaffId(data.getString("STAFF_ID", "SUPERUSR"));// 设置登陆员工,因为导出需要校验权限
        IDataset res = CSAppCall.call("SS.QueryDiscntModifyHisInfoSVC.queryDiscntModifyHisInfo", data);
        // 新系统不支持转中文，只能手动转
        for (int i = 0, size = res.size(); i < size; i++)
        {
            IData tempInfo = res.getData(i);
            String discntCode = tempInfo.getString("DISCNT_CODE", "");
            if (!"".equals(discntCode))
            {
                String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
                res.getData(i).put("DISCNT_CODE", discntName);
            }
        }
        return res;
    }
}
