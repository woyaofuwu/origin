package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.group.esop.workform.WorkFormBean;

public class ReportSurveyProgressExportTask extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData inParam, Pagination arg1) throws Exception {
        // TODO Auto-generated method stub
        IDataset eomsInfos = WorkFormBean.queryReportSurvey(inParam, arg1);
        return eomsInfos;
    }

}
