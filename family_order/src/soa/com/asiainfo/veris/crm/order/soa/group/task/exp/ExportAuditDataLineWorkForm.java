package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ExportAuditDataLineWorkForm extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination pag) throws Exception {
        data.put("ACCEPT_START", data.getString("cond_ACCEPT_START"));
        data.put("ACCEPT_END", data.getString("cond_ACCEPT_END"));
        data.put("IBSYSID", data.getString("cond_IBSYSID"));
        data.put("STAFF_ID", data.getString("cond_STAFFID"));
        data.put("GROUP_ID", data.getString("cond_GROUPID"));
        data.put("AUDIT_RESULT", data.getString("cond_AUDIT_RESULT"));
        IDataset output = CSAppCall.call("SS.WorkFormSVC.queryAuditWorkform", data);
        return output;
    }

}
