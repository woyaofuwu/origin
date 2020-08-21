package com.asiainfo.veris.crm.order.soa.group.task.exp;

import java.util.Iterator;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class DataLineAuditInfoExportTask extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination page) throws Exception {
        String ibsysid = data.getString("cond_IBSYSID");
        IDataset productAttrs = new DatasetList();
        IData input = new DataMap();
        input.put("IBSYSID", ibsysid);
        input.put("NODE_ID", "apply");
        IDataset attrs = CSAppCall.call("SS.WorkformAttrSVC.getNewLineInfoList", input);
        if(attrs != null && attrs.size() > 0) {
            for (Object obj : attrs) {
                IData attr = (IData) obj;
                IData productAttr = new DataMap();
                Iterator<String> it = attr.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    String value = attr.getString(key);
                    if(key.startsWith("pattr_")) {
                        key = key.substring(6);
                    }
                    productAttr.put(key, value);
                }
                productAttrs.add(productAttr);
            }
        }
        return productAttrs;
    }

}
