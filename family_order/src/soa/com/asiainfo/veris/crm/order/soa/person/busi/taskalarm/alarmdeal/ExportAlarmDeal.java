
package com.asiainfo.veris.crm.order.soa.person.busi.taskalarm.alarmdeal;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：业务风险处理清单的导出 作者：GongGuang
 */
public class ExportAlarmDeal extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataset res = CSAppCall.call("SS.AlarmDealSVC.queryAlarmByCond", data);
        if (IDataUtil.isNotEmpty(res))
        {
            for (int i = 0; i < res.size(); i++)
            {
                String handleState = res.getData(i).getString("HANDLE_STATE");
                String rsrvStr2 = res.getData(i).getString("RSRV_STR2");
                if ("1".equals(rsrvStr2))
                {
                    res.getData(i).put("RSRV_STR2", "已关闭");
                }
                else if ("".equals(rsrvStr2) || "0".equals(rsrvStr2))
                {
                    res.getData(i).put("RSRV_STR2", "未关闭");
                }
                if ("0".equals(handleState))
                {
                    res.getData(i).put("HANDLE_STATE", "未处理");
                }
                else if ("1".equals(handleState))
                {
                    res.getData(i).put("HANDLE_STATE", "已通知");
                }
                else if ("2".equals(handleState))
                {
                    res.getData(i).put("HANDLE_STATE", "已处理");
                }

            }
        }
        return res;
    }
}
