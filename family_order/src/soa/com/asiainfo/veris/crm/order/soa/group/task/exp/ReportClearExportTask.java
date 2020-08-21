package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ReportClearExportTask extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData inParam, Pagination arg1) throws Exception {
        IData map = new DataMap();
        String subTypeOpen = inParam.getString("SUB_TYPE_OPEN");
        String ibsysId = inParam.getString("IBSYSID");
        String acceptEnd = inParam.getString("ACCEPT_END");
        String acceptStart = inParam.getString("ACCEPT_START");
        map.put("SUB_TYPE_OPEN", subTypeOpen);
        map.put("IBSYSID", ibsysId);
        map.put("ACCEPT_END", acceptEnd);
        map.put("ACCEPT_START", acceptStart);
        IDataset eomsInfos = CSAppCall.call("SS.WorkFormSVC.queryClearList", map);
        if (IDataUtil.isNotEmpty(eomsInfos)) {
            for (Object object : eomsInfos) {
                IData clearList = (IData) object;
                String cityCode1 = StaticUtil.getStaticValue("EOP_CUST_CITY_CODE", clearList.getString("CITY_CODE"));
                clearList.put("CUST_CITY_CODE", cityCode1);
            }
        }
        return eomsInfos;
    }

}
