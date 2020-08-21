
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class GroupBBossBizEcQueryExportTask extends ExportTaskExecutor
{
    /**
     * BBOSS产品用户状态查询
     * 
     * @author liuxx3
     * @date 2014-7-10
     */
    @Override
    public IDataset executeExport(IData idata, Pagination pagination) throws Exception
    {
        IData param = idata.subData("cond", true);

        IDataset reslut = CSAppCall.call("CS.BbossQueryBizSVC.qryBBossBizEc", param);

        return reslut;
    }

}
