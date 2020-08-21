
package com.asiainfo.veris.crm.order.soa.person.busi.rejectmessage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class RejectMessageInfoExportTask extends CSExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {

        IDataset ret = new DatasetList();
        String dealStr = paramIData.getString("TAB_DATA");
        if (StringUtils.isNotBlank(dealStr) && StringUtils.isNotEmpty(dealStr))
        {
            ret = new DatasetList(dealStr);
            return ret;
        }
        else
        {
            return ret;
        }
    }

}
