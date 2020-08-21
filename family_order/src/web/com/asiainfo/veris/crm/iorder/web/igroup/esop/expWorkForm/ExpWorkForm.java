package com.asiainfo.veris.crm.iorder.web.igroup.esop.expWorkForm;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class ExpWorkForm extends EopBasePage {

    public abstract void setInfos(IDataset infos);

    public abstract void setCondition(IData info);

    public abstract void setInfo(IData info);

    public abstract void setInfosCount(long infosCount);

    public void initPage(IRequestCycle cycle) throws Exception {
        IData condData = this.getData();
        IDataOutput dataOutput = CSViewCall.callPage(this, "SS.WorkFormSVC.getExpWorkForms", condData, this.getPagination("navt"));
        IDataset setInfo = dataOutput.getData();
        setInfosCount(dataOutput.getDataCount());
        this.setInfos(setInfo);
        this.setCondition(condData);

    }

    public void submit(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        CSViewCall.call(this, "SS.WorkFormSVC.updateEweStepByState", data);

    }

    public void initPageExp(IRequestCycle cycle) throws Exception {
        IData condData = this.getData();
        IDataset dataOutput = CSViewCall.call(this, "SS.WorkFormSVC.getExpWorkForms", condData);
        this.setInfos(dataOutput);
        this.setCondition(condData);

    }

}
