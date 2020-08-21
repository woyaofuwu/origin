
package com.asiainfo.veris.crm.iorder.web.igroup.esop.audit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class AuditDataLineWorkForm extends CSBasePage
{

    public void querySurveyList(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put("ACCEPT_START", data.getString("cond_ACCEPT_START"));
        data.put("ACCEPT_END", data.getString("cond_ACCEPT_END"));
        data.put("IBSYSID", data.getString("cond_IBSYSID"));
        data.put("STAFF_ID", data.getString("cond_STAFF_ID"));
        data.put("GROUP_ID", data.getString("cond_GROUP_ID"));
        data.put("AUDIT_RESULT", data.getString("cond_AUDIT_RESULT"));
        IDataOutput output = CSViewCall.callPage(this, "SS.WorkFormSVC.queryAuditWorkform", data, this.getPagination("olcomnav"));
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
