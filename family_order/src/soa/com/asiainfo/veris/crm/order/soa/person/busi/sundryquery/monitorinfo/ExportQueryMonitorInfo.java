
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.monitorinfo;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MonitorInfoQry;

public class ExportQueryMonitorInfo extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData input, Pagination pagination) throws Exception
    {
        SpecialBizQryDealInput.condParamDeal(input);
        input.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        // 服务返回结果集
        IDataset result = new DatasetList();
        String query_type = input.getString("QUERY_TYPE");
        if (query_type.equals("") || query_type == null)
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_2002);
        }
        else if (query_type.equals(PersonConst.QUERY_TYPE_NORMAL))
        {
            // 普通查询
            result = MonitorInfoQry.queryMonitorInfosByNormal(input, pagination);
        }
        else if (query_type.equals(PersonConst.QUERY_TYPE_REPORT))
        {
            // 日报表查询
            result = MonitorInfoQry.queryMonitorInfosByDay(input, pagination);
        }
        else if (query_type.equals(PersonConst.QUERY_TYPE_REPORT_BETWEEN))
        {
            // 时段表查询
            result = MonitorInfoQry.queryMonitorInfosByHour(input, pagination);
        }

        return result;
    }

}
