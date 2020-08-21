
package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ConfCrmImportMarketingTask extends ImportTaskExecutor
{

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception
    {
    	if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_138);
        }

        SharedCache.set("DATALINE_INFOS", dataset);
        return null;
    	
    }

}
