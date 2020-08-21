package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ExportIntfErrorFormDetail extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination pagination) throws Exception {
        String beginDate = data.getString("BEGIN_DATE");
        String endDate = data.getString("END_DATE");
        String subTypeCode = data.getString("SUB_TYPE_CODE");
        String queryType = data.getString("QUERY_TYPE");
        IData param = new DataMap();
        param.put("BEGIN_DATE", beginDate);
        param.put("END_DATE", endDate);
        param.put("SUB_TYPE_CODE", subTypeCode);
        param.put("QUERY_TYPE", queryType);
        String subType = StaticUtil.getStaticValue("EOP_SUB_TYPE_CODE", subTypeCode);
        IDataset subTypeList = CSAppCall.call("SS.EsopOrderQuerySVC.queryDetailIntfErrorForm", param);
        if(IDataUtil.isNotEmpty(subTypeList)) {
            for (int i = 0; i < subTypeList.size(); i++) {
                IData subTypeData = subTypeList.getData(i);
                subTypeData.put("SUB_TYPE", subType);
            }
        }
        return subTypeList;
    }

}
