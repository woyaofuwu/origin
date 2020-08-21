
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class IPLaterPayQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {

        String snA = inParam.getString("cond_SERIAL_NUMBER_A");
        String snB = inParam.getString("cond_SERIAL_NUMBER");

        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param.put("SERIAL_NUMBER_A", snA);
        param.put("SERIAL_NUMBER", snB);

        IDataset dataset = new DatasetList();
        if (StringUtils.isNotEmpty(snA))
        {
            dataset = CSAppCall.call("SS.GroupInfoQuerySVC.qryIPLaterPayInfoA", param);
        }
        else if (StringUtils.isNotEmpty(snB))
        {
            dataset = CSAppCall.call("SS.GroupInfoQuerySVC.qryIPLaterPayInfoB", param);
        }

        return dataset;
    }
}
