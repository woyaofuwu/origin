package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ExportLineWorkform extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination pag) throws Exception {
        String[] keys=data.getNames();
        for(String key :keys){
        	if(key.startsWith("cond_")&&key.length()>5){
        		data.put(key.substring(5), data.get(key));
        	}
        }
		IDataset output = CSAppCall.call("SS.LineWorkformSVC.qryLineWorkform", data);
        return output;
    }

}
