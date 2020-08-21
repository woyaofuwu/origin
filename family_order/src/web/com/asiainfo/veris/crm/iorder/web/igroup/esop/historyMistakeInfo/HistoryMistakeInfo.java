package com.asiainfo.veris.crm.iorder.web.igroup.esop.historyMistakeInfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class HistoryMistakeInfo extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setInfosCount(long infosCount);

    public abstract void setStaffInfo(IData staffInfo);

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void initPage(IRequestCycle cycle) throws Exception {
        IData staffInfo = new DataMap();
        String staffId = getVisit().getStaffId();
        String staffName = getVisit().getStaffName();
        staffInfo.put("STAFF_ID", staffId);
        staffInfo.put("STAFF_NAME", staffName);
        IData eomsInfos = CSViewCall.callone(this, "SS.WorkFormSVC.queryProductAndBusiType", staffInfo);
        staffInfo.putAll(eomsInfos);
        this.setStaffInfo(staffInfo);

    }

    public void queryHistoryMistakeInfo(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.WorkFormSVC.queryHistoryMistakeInfo", data, this.getPagination("olcomnav"));
        IDataset myWorkformInfos = output.getData();
        setInfosCount(output.getDataCount());
        this.setInfos(myWorkformInfos);
        this.setCondition(data);
    }

}
