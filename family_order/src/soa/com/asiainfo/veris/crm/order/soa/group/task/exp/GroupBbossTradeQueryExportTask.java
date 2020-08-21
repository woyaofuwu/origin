
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class GroupBbossTradeQueryExportTask extends ExportTaskExecutor
{
    /**
     * BBOSS产品订购反馈结果查询
     */
    @Override
    public IDataset executeExport(IData idata, Pagination pagination) throws Exception
    {
        IData param = idata.subData("cond", true);

        IDataset reslut = CSAppCall.call("CS.BbossQueryBizSVC.qryBBossTradeInfo", param);

        return reslut;
    }
}
