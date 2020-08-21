package com.asiainfo.veris.crm.order.soa.person.busi.openAccountInfoQuery;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;

public class OpenAreaWaringExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData paramIData, Pagination arg1) throws Exception
    {
        String alertInfo = "";
        IData data = paramIData.subData("cond", true);
        data.put("RSRV_STR7", this.getVisit().getCityCode());
        System.out.print("wuhao"+data);
        IDataset openAreaWarningDs = CSAppCall.call("SS.OpenAccountInfoQuerySVC.queryOpenAccountInfo", data);
        return openAreaWarningDs;
    }
}
