
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userpostrepair;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：用户预约产品的导出 作者：GongGuang
 */
public class ExportQueryUserPostRepair extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataset res = CSAppCall.call("SS.QueryUserPostRepairSVC.queryUserPostRepair", data);
        if (IDataUtil.isNotEmpty(res))
        {
            for (int i = 0; i < res.size(); i++)
            {
                String rsrvNum1 = StaticUtil.getStaticValue("REPAIR_POST_INFO_REASON", res.getData(i).getString("RSRV_NUM1"));
                String processTag = StaticUtil.getStaticValue("REPAIR_POST_PROCESS_TAG", res.getData(i).getString("PROCESS_TAG"));
                res.getData(i).put("RSRV_NUM1", rsrvNum1);
                res.getData(i).put("PROCESS_TAG", processTag);
            }
        }
        return res;
    }
}
