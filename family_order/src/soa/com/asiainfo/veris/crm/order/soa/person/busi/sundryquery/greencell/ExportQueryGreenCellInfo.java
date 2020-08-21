
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.greencell;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SundryQry;

public class ExportQueryGreenCellInfo extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData input, Pagination pagination) throws Exception
    {
        IData parmaMap = new DataMap();
        parmaMap.put("DISCNT_CODE", input.getString("cond_DISCNT_CODE"));
        parmaMap.put("AREA_CODE", input.getString("cond_AREA_CODE"));
        // 服务返回结果集
        IDataset result = new DatasetList();

        result = SundryQry.queryGreenCellInfo(parmaMap, pagination);

        // 设置页面返回数据

        return result;
    }

}
