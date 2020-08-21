
package com.asiainfo.veris.crm.iorder.web.igroup.minorec.audit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class MinorecAuditDataLineWorkForm extends CSBasePage
{

    public void querySurveyList(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
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
        IDataOutput output = CSViewCall.callPage(this, "SS.QryAuditInfoSVC.qryDataLineWorkForm", data, this.getPagination("olcomnav"));
        IDataset myWorkformInfos = output.getData();
        setInfoCount(output.getDataCount());
        this.setInfos(myWorkformInfos);
        this.setCondition(data);
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setInfoCount(long infosCount);

}
