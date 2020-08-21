package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ExportCheckinWorkForm extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination pag) throws Exception {
        IData map = new DataMap();
        String beginDate = data.getString("BEGIN_DATE");
        String endDate = data.getString("END_DATE");
        map.put("BEGIN_DATE", beginDate);
        map.put("END_DATE", endDate);
        IDataset output = CSAppCall.call("SS.EsopOrderQuerySVC.queryCheckinForm", map);
        return output;
    }

}
