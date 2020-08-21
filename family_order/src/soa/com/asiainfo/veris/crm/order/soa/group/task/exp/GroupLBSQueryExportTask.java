
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AsynDealVisitUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class GroupLBSQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData param, Pagination arg1) throws Exception
    {
        AsynDealVisitUtil.dealVisitInfo(param);

        String snA = param.getString("cond_SERIAL_NUMBER_B");
        String snB = param.getString("cond_SERIAL_NUMBER");

        IData inputParam = new DataMap();
        inputParam.put("SERIAL_NUMBER_B", snA);
        inputParam.put("SERIAL_NUMBER", snB);

        IDataset dataOutput = null;

        if (StringUtils.isNotBlank(snA) && StringUtils.isNotBlank(snB))
        {// 按产品编码和手机号码查询
            dataOutput = CSAppCall.call("SS.GroupInfoQuerySVC.qryGroupLBSInfo", inputParam);
        }
        else if (StringUtils.isNotBlank(snA) && StringUtils.isBlank(snB))
        {// 按产品编码查询
            dataOutput = CSAppCall.call("SS.GroupInfoQuerySVC.qryGroupLBSByProductId", inputParam);
        }
        else if (StringUtils.isBlank(snA) && StringUtils.isNotBlank(snB))
        {// 按手机号码查询
            dataOutput = CSAppCall.call("SS.GroupInfoQuerySVC.qryGroupLBSBySN", inputParam);
        }

        return dataOutput;
    }

}
