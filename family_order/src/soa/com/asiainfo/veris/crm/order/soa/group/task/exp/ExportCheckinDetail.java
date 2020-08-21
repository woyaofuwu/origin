package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ExportCheckinDetail extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination pag) throws Exception {
        String cols = data.getString("COLS");
        String rows = data.getString("ROWS");
        String startDate = data.getString("cond_START_DATE");
        String endDate = data.getString("cond_END_DATE");
        IData param = new DataMap();
        param.put("COLS", cols);
        param.put("ROWS", rows);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        IDataset infos = new DatasetList();
        if("TOTAL".equals(rows)) {//如果点击的“合计”
            if("TOTAL".equals(cols))//如果是发起总量
            {
                infos = CSAppCall.call("SS.EsopOrderQuerySVC.showCheckinDetailTotalAll", param);
            } else if("UNCHECKIN".equals(cols))//如果是未归档
            {
                infos = CSAppCall.call("SS.EsopOrderQuerySVC.showCheckinDetailUncheckinAll", param);
            } else if("CHECKIN".equals(cols))//如果是已归档
            {
                infos = CSAppCall.call("SS.EsopOrderQuerySVC.showCheckinDetailCheckinAll", param);
            }
        } else//如果点击的某个类型工单，如RESOURCECONFIRM（开通勘察）、CHANGERESOURCECONFIRM（变更勘察）等
        {
            if("TOTAL".equals(cols))//如果是发起总量
            {
                infos = CSAppCall.call("SS.EsopOrderQuerySVC.showCheckinDetailTotal", param);
            } else if("UNCHECKIN".equals(cols))//如果是未归档
            {
                infos = CSAppCall.call("SS.EsopOrderQuerySVC.showCheckinDetailUncheckin", param);
            } else if("CHECKIN".equals(cols))//如果是已归档
            {
                infos = CSAppCall.call("SS.EsopOrderQuerySVC.showCheckinDetailCheckin", param);
            }
        }
        return infos;
    }

}
