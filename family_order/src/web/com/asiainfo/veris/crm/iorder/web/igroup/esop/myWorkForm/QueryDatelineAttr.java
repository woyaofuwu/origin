package com.asiainfo.veris.crm.iorder.web.igroup.esop.myWorkForm;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class QueryDatelineAttr extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public void queryData(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        IDataset eomsInfos = CSViewCall.call(this, "SS.WorkFormSVC.queryDatelineAttr", data);
        if (IDataUtil.isNotEmpty(eomsInfos)) {
            // 统计专线详情数量
            Iterator<Object> eomsInfo = eomsInfos.iterator();
            while (eomsInfo.hasNext()) {
                IData eomsDateline = (IData) eomsInfo.next();
                String dataLineSum = eomsDateline.getString("DATELINE_SUM", "");
                if (StringUtils.isNotBlank(dataLineSum)) {
                    data.put("DATELINE_SUM", dataLineSum);
                    data.put("DATELINE_CANCEL", eomsDateline.getString("DATELINE_CANCEL", ""));
                    data.put("DATALINE_HANG", eomsDateline.getString("DATALINE_HANG", ""));
                    data.put("DATALINE_CHECKIN", eomsDateline.getString("DATALINE_CHECKIN", ""));
                    eomsInfo.remove();
                }
            }
        }
        setInfos(eomsInfos);
        setCondition(data);
    }

    public void summarizeEomsQuery(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        CSViewCall.call(this, "SS.WorkFormSVC.querySummarizeEoms", data);
    }
}
