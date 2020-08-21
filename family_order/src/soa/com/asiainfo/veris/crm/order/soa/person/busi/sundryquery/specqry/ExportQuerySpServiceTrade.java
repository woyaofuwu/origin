
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.specqry;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SundryQry;

public class ExportQuerySpServiceTrade extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData input, Pagination pagination) throws Exception
    {
        // input.put(Route.CONN_CRM_CEN, );
        input.put("SP_SVC_ID", input.getString("cond_SP_SVC_ID", ""));
        input.put("BIZ_STATUS", input.getString("cond_BIZ_STATUS", ""));
        input.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        // 服务返回结果集
        IDataset result = new DatasetList();

        result = SundryQry.querySpServiceTrade(input, pagination);

        // 设置页面返回数据
        return result;
    }

}
