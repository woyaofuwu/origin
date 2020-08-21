package com.asiainfo.veris.crm.iorder.web.igroup.esop.filingstatistics;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;

public abstract class FilingStatistics extends EopBasePage {

    public abstract void setInfos(IDataset infos) throws Exception;

    public abstract void setInfoCount(long infoCount) throws Exception;

    public abstract void setCondition(IData condition) throws Exception;

    public abstract void setDetailCondition(IData detailCondition) throws Exception;

    public abstract void setOrderInfos(IDataset orderInfos) throws Exception;

    public void qryInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String beginDate = data.getString("BEGIN_DATE");
        String endDate = data.getString("END_DATE");
        IData param = new DataMap();
        param.put("BEGIN_DATE", beginDate);
        param.put("END_DATE", endDate);
        IDataset output = CSViewCall.call(this, "SS.EsopOrderQuerySVC.queryCheckinForm", param);
        //IDataOutput output = CSViewCall.callPage(this, "SS.EsopOrderQuerySVC.queryCheckinForm", param, this.getPagination("navbar1"));
        //setInfoCount(output.getDataCount());
        setInfos(output);

        IData condition = new DataMap();
        condition.put("cond_BEGIN_DATE", beginDate);
        condition.put("cond_END_DATE", endDate);
        setCondition(condition);
    }

    public void qryDetailInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String cols = data.getString("COLS", "");//列，TOTAL（发起总量）、UNCHECKIN（未归档）、CHECKIN（已归档）等
        String rows = data.getString("ROWS", "");//行，RESOURCECONFIRM（开通勘察）、CHANGERESOURCECONFIRM（变更勘察）、合计等
        IData param = new DataMap();
        param.put("COLS", cols);
        param.put("ROWS", rows);
        param.put("START_DATE", data.getString("START_DATE"));
        param.put("END_DATE", data.getString("END_DATE"));
        IDataset infos = new DatasetList();
        if("TOTAL".equals(rows)) {//如果点击的“合计”
            if("TOTAL".equals(cols))//如果是发起总量
            {
                infos = CSViewCall.call(this, "SS.EsopOrderQuerySVC.showCheckinDetailTotalAll", param);
            } else if("UNCHECKIN".equals(cols))//如果是未归档
            {
                infos = CSViewCall.call(this, "SS.EsopOrderQuerySVC.showCheckinDetailUncheckinAll", param);
            } else if("CHECKIN".equals(cols))//如果是已归档
            {
                infos = CSViewCall.call(this, "SS.EsopOrderQuerySVC.showCheckinDetailCheckinAll", param);
            }
        } else//如果点击的某个类型工单，如RESOURCECONFIRM（开通勘察）、CHANGERESOURCECONFIRM（变更勘察）等
        {
            if("TOTAL".equals(cols))//如果是发起总量
            {
                infos = CSViewCall.call(this, "SS.EsopOrderQuerySVC.showCheckinDetailTotal", param);
            } else if("UNCHECKIN".equals(cols))//如果是未归档
            {
                infos = CSViewCall.call(this, "SS.EsopOrderQuerySVC.showCheckinDetailUncheckin", param);
            } else if("CHECKIN".equals(cols))//如果是已归档
            {
                infos = CSViewCall.call(this, "SS.EsopOrderQuerySVC.showCheckinDetailCheckin", param);
            }
        }
        setDetailCondition(param);
        setOrderInfos(infos);
    }

}
