
package com.asiainfo.veris.crm.order.soa.person.busi.bat.batextractnoactivation;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ExtractNoActivationQry;

public class ExportQueryExtractNoActivationInfo extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData input, Pagination pagination) throws Exception
    {
        IData parmaMap = new DataMap();
        parmaMap.put("START_SERIALNUMBER", input.getString("cond_START_SERIALNUMBER"));
        parmaMap.put("END_SERIALNUMBER", input.getString("cond_END_SERIALNUMBER"));
        // 服务返回结果集
        IDataset result = new DatasetList();

        result = ExtractNoActivationQry.queryExtractnoactivationInfo(parmaMap, pagination);

        // 设置页面返回数据

        return result;
    }

}
