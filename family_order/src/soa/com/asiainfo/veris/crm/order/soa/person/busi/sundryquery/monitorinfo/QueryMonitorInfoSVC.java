
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.MonitorInfoQry;

public class QueryMonitorInfoSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryMonitorInfos(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        String query_type = input.getString("QUERY_TYPE");
        // 处理查询条件
        SpecialBizQryDealInput.condParamDeal(input);
        if (query_type.equals("") || query_type == null)
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_2002);
        }
        else if (query_type.equals(PersonConst.QUERY_TYPE_NORMAL))
        {
            // 普通查询
            result = MonitorInfoQry.queryMonitorInfosByNormal(input, getPagination());
        }
        else if (query_type.equals(PersonConst.QUERY_TYPE_REPORT))
        {
            // 日报表查询
            result = MonitorInfoQry.queryMonitorInfosByDay(input, getPagination());
        }
        else if (query_type.equals(PersonConst.QUERY_TYPE_REPORT_BETWEEN))
        {
            // 时段表查询
            result = MonitorInfoQry.queryMonitorInfosByHour(input, getPagination());
        }

        return result;
    }
}
