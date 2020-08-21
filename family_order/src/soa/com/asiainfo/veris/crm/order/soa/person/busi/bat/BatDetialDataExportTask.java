
package com.asiainfo.veris.crm.order.soa.person.busi.bat;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;

public class BatDetialDataExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData data, Pagination arg1) throws Exception
    {
        IDataset detial = BatDealInfoQry.batchDetialQuery(data);
        IDataset returnInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(detial))
        {
            for (int i = 0; i < detial.size(); i++)
            {
                IData returnInfo = detial.getData(i);
                String staffId = detial.getData(i).getString("CANCEL_STAFF_ID");
                String staffName = UStaffInfoQry.getStaffNameByStaffId(staffId);
                returnInfo.put("CANCEL_STAFF_NAME", staffName);
                returnInfos.add(returnInfo);
            }
        }
        return returnInfos;
    }

}
