
package com.asiainfo.veris.crm.order.soa.person.busi.fastauth.fastauthapply;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 功能：授权业务申请清单的导出 作者：GongGuang
 */
public class ExportFastAuthApply extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData data = paramIData.subData("cond", true);
        data.put("EPARCHY_CODE", paramIData.getString("TRADE_EPARCHY_CODE"));
        data.put("ASK_STAFF_ID", paramIData.getString("TRADE_STAFF_ID"));
        data.put("ASK_DEPART_ID", paramIData.getString("TRADE_DEPART_ID"));
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        IDataset res = CSAppCall.call("SS.FastAuthApplySVC.queryApplyTrade", data);
        if (IDataUtil.isNotEmpty(res))
        {
            for (int i = 0; i < res.size(); i++)
            {
                String awsState = StaticUtil.getStaticValue("FASTAUTH_STATE", res.getData(i).getString("AWS_STATE"));
                res.getData(i).put("AWS_STATE", awsState);
            }
        }
        return res;
    }
}
