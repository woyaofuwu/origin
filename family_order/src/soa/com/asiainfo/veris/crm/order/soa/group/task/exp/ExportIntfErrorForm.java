package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ExportIntfErrorForm extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination pag) throws Exception {
        String beginDate = data.getString("cond_BEGIN_DATE");
        String endDate = data.getString("cond_END_DATE");
        IData param = new DataMap();
        param.put("BEGIN_DATE", beginDate);
        param.put("END_DATE", endDate);
        IDataset subTypeList = StaticUtil.getList(getVisit(), "TD_S_STATIC", "DATA_ID", "DATA_NAME", "TYPE_ID", "EOP_SUB_TYPE_CODE");
        if(IDataUtil.isEmpty(subTypeList)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查询到流程信息，请检查TD_S_STATIC[TYPE_ID=EOP_SUB_TYPE_CODE]表配置信息！");
        }
        IDataset qryList = new DatasetList();
        for (int i = 0; i < subTypeList.size(); i++) {
            IData subTypeData = subTypeList.getData(i);
            param.put("SUB_TYPE_CODE", subTypeData.getString("DATA_ID"));
            IDataset output = CSAppCall.call("SS.EsopOrderQuerySVC.queryIntfErrorForm", param);
            if(IDataUtil.isNotEmpty(output)) {
                for (int j = 0; j < output.size(); j++) {
                    output.getData(j).put("SUB_TYPE", subTypeData.getString("DATA_NAME"));
                }
            }
            qryList.addAll(output);
        }
        return qryList;
    }

}
