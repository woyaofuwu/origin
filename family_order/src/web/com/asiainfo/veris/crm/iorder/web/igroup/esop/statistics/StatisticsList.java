package com.asiainfo.veris.crm.iorder.web.igroup.esop.statistics;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;

public abstract class StatisticsList extends EopBasePage {

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setInfoCount(long infoCount);

    public void qryInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String beginDate = data.getString("cond_BEGIN_DATE");
        String endDate = data.getString("cond_END_DATE");
        String staffId = data.getString("condi_STAFF_ID");
        IData param = new DataMap();
        param.put("START_DATE", beginDate);
        param.put("END_DATE", endDate);
        param.put("STAFF_ID", staffId);
        IDataOutput output = CSViewCall.callPage(this, "SS.EsopStaffInfoSVC.queryStaffLoginInfos", param, this.getPagination("navbar1"));
        setInfoCount(output.getDataCount());
        //System.out.println("StatisticsList.java:" + output.getData().toString());
        setInfos(output.getData());
    }
}
