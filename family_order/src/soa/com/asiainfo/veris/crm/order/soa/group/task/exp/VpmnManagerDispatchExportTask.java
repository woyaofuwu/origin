
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custmanager.CustManagerInfoQry;

public class VpmnManagerDispatchExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        String importId = inParam.getString("IMPORT_ID");
        String dealState = inParam.getString("cond_DEAL_STATE");
        IDataset dataset = CustManagerInfoQry.queryThisVpmnManagerInfo(importId, dealState, pg);
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                IData data = dataset.getData(i);
                String oldCustMgrId = data.getString("RSRV_STR1", "");
                String newCustMgrId = data.getString("RSRV_STR2", "");
                String dealTime = data.getString("DEAL_TIME", "");
                String dealstate = data.getString("DEAL_STATE", "");
                String dealStaffId = data.getString("DEAL_STAFF_ID", "");
                String dealDepartId = data.getString("DEAL_DEPART_ID", "");
                data.put("DEAL_TIME", !"".equals(dealTime) ? SysDateMgr.decodeTimestamp(dealTime, SysDateMgr.PATTERN_STAND) : "");
                data.put("DEAL_STATE", !"".equals(dealstate) ? StaticUtil.getStaticValue("CMSIMPORTDATA_DEALSTATE", dealstate) : "");
                data.put("DEAL_STAFF_ID", !"".equals(dealStaffId) ? UStaffInfoQry.getStaffNameByStaffId(dealStaffId) : "");
                data.put("DEAL_DEPART_ID", !"".equals(dealDepartId) ? UDepartInfoQry.getDepartNameByDepartId(dealDepartId) : "");
                data.put("RSRV_STR1_NAME", !"".equals(oldCustMgrId) ? UStaffInfoQry.getStaffNameByStaffId(oldCustMgrId) : "");
                data.put("RSRV_STR2_NAME", !"".equals(newCustMgrId) ? UStaffInfoQry.getStaffNameByStaffId(newCustMgrId) : "");
            }
        }
        return dataset;
    }

}
