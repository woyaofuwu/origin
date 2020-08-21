package com.asiainfo.veris.crm.iorder.web.igroup.esop.busiCheck400;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class BusiDetail400 extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setCheckRecordInfos(IDataset checkRecordInfos);

    public abstract void setCheckRecordInfo(IData checkRecordInfo);

    public void queryData(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        IDataset eomsInfos = CSViewCall.call(this, "SS.WorkFormSVC.getTimerTaskWorkformNewAttrInfo", data);
        IDataset datalineAttrInfo = CSViewCall.call(this, "SS.WorkFormSVC.queryCheckRecordInfos", data);
        setCheckRecordInfos(datalineAttrInfo);
        setInfo(eomsInfos.getData(0));
        setCondition(data);
    }

    public void updateCheckRecordInfo(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        CSViewCall.call(this, "SS.WorkFormSVC.updateCheckRecordInfo", data);
        IDataset datalineAttrInfo = CSViewCall.call(this, "SS.WorkFormSVC.queryCheckRecordInfos", data);
        setCheckRecordInfos(datalineAttrInfo);
    }

    public void addCheckRecordInfo(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        data.put("CHECK_STAFF_ID", getVisit().getStaffId());
        CSViewCall.call(this, "SS.WorkFormSVC.addCheckRecordInfo", data);
        IDataset datalineAttrInfo = CSViewCall.call(this, "SS.WorkFormSVC.queryCheckRecordInfos", data);
        setCheckRecordInfos(datalineAttrInfo);
    }

}
