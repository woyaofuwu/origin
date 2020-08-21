
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AsynDealVisitUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class GroupNetworkQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData param, Pagination arg1) throws Exception
    {
        AsynDealVisitUtil.dealVisitInfo(param);
        IData inputParam = new DataMap();
        String groupId = param.getString("cond_GROUP_ID");
        String productId = param.getString("cond_PRODUCT_ID");
        inputParam.put("GROUP_ID", groupId);
        inputParam.put("PRODUCT_ID", productId);
        IDataset dataOutput = CSAppCall.call("SS.GroupInfoQuerySVC.qryGroupNetworkInfo", inputParam);
        return dataOutput;
    }

}
