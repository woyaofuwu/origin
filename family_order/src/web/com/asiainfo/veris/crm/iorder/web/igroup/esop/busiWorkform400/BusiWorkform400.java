package com.asiainfo.veris.crm.iorder.web.igroup.esop.busiWorkform400;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class BusiWorkform400 extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setInfoCount(long infoCount);

    public void queryBusiWorkformInfos(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.WorkFormSVC.queryBusiWorkformInfos", data, this.getPagination("navbar1"));
        setInfoCount(output.getDataCount());
        this.setInfos(output.getData());
        this.setCondition(data);

    }

}
