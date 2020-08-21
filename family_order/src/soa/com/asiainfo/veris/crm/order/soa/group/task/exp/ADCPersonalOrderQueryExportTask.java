
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ADCPersonalOrderQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        String sn = inParam.getString("cond_SERIAL_NUMBER");
        String bizCode = inParam.getString("cond_BIZ_CODE");
        String ecSn = inParam.getString("cond_EC_SERIAL_NUMBER");

        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param.put("SERIAL_NUMBER", sn);
        param.put("SERV_CODE", bizCode);
        param.put("EC_SERIAL_NUMBER", ecSn);

        IDataset dataset = CSAppCall.call("SS.GroupInfoQuerySVC.qryADCPersonalOrderInfo", param);
        return dataset;

    }
}
