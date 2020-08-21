package com.asiainfo.veris.crm.iorder.web.igroup.esop.acceptanceProgressReportClear;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class AcceptanceProgressReportClear extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setInfosCount(long infosCount);

    /**
     * 专线开通统计报表查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryReportClear(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.WorkFormSVC.queryReportClear", data, this.getPagination("olcomnav"));
        IDataset eomsInfos = output.getData();
        setInfosCount(output.getDataCount());
        this.setInfos(eomsInfos);
        this.setCondition(data);
    }
}
