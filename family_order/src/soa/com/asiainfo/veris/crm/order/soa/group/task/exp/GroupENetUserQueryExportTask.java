
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AsynDealVisitUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class GroupENetUserQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        IData inputParam = new DataMap();

        String ecCode = inParam.getString("cond_GROUP_ID");
        String memSeial = inParam.getString("cond_SERIAL_NUMBER");
        String startDate = inParam.getString("cond_START_DATE");
        String endDate = inParam.getString("cond_END_DATE");
        String stateFlag = inParam.getString("cond_ENET_INFO_QUERY_STATE");

        if (inParam.containsKey("cond_START_DATE") && !startDate.equals(""))
        {
            inputParam.put("VSTART_DATE", startDate + SysDateMgr.START_DATE_FOREVER);
        }
        if (inParam.containsKey("cond_END_DATE") && !endDate.equals(""))
        {
            inputParam.put("VEND_DATE", endDate + SysDateMgr.END_DATE);
        }
        IDataset dataOutput = null;
        inputParam.put("GROUP_ID", ecCode);
        inputParam.put("SERIAL_NUMBER", memSeial);
        AsynDealVisitUtil.dealVisitInfo(inParam);

        if ("1".equals(stateFlag) || "2".equals(stateFlag))
        {// 已注销 或 未注销
            String destroyFlag = "1".equals(stateFlag) ? "NOT_DESTORYED" : "DESTORYED";
            inputParam.put("destroyFlag", destroyFlag);
            dataOutput = CSAppCall.call("SS.GroupInfoQuerySVC.qryGroupENetInfo", inputParam);
        }
        else
        { // 全部
            dataOutput = CSAppCall.call("SS.GroupInfoQuerySVC.qryAllGroupENetInfo", inputParam);
        }
        return dataOutput;
    }

}
