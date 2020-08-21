package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ExportMinorecAuditDataLineWorkForm extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination pag) throws Exception {
        String staffId = data.getString("cond_STAFF_ID");
        if(StringUtils.isBlank(staffId)) {
            staffId = data.getString("cond_STAFFID");
        }
        String groupId = data.getString("cond_GROUP_ID");
        if(StringUtils.isBlank(groupId)) {
            groupId = data.getString("cond_GROUPID");
        }
        data.put("ACCEPT_START", data.getString("cond_ACCEPT_START"));
        data.put("ACCEPT_END", data.getString("cond_ACCEPT_END"));
        data.put("IBSYSID", data.getString("cond_IBSYSID"));
        data.put("STAFF_ID", staffId);
        data.put("GROUP_ID", groupId);
        data.put("AUDIT_RESULT", data.getString("cond_AUDIT_RESULT"));
        IDataset output = CSAppCall.call("SS.QryAuditInfoSVC.qryDataLineWorkForm", data);
        return output;
    }

}
