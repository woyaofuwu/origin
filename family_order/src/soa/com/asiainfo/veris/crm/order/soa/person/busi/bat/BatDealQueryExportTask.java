
package com.asiainfo.veris.crm.order.soa.person.busi.bat;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: BatQueryResultImportTask.java
 * @Description: 批量任務結果查詢導出後台TASK类
 * @version: v1.0.0
 * @author: xiangyc
 * @date: 2013-9-24 下午7:38:22 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-9-24 xiangyc v1.0.0 修改原因
 */

public class BatDealQueryExportTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", paramIData.getString("SERIAL_NUMBER"));
        param.put("BATCH_ID", paramIData.getString("BATCH_ID"));
        param.put("START_TIME", paramIData.getString("START_TIME"));
        param.put("END_TIME", paramIData.getString("END_TIME"));
        IDataset ret = BatTradeInfoQry.queryBatDealBySN(param, null);
        IDataset returnInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(ret))
        {
            for (int i = 0; i < ret.size(); i++)
            {
                IData returnInfo = ret.getData(i);
                String dealState = returnInfo.getString("DEAL_STATE");
                String cancelTag = returnInfo.getString("CANCEL_TAG");
                returnInfo.put("DEAL_STATE_NAME", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new java.lang.String[]
                { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
                { "BAT_TASK_STATE_TAG", dealState }));
                returnInfo.put("CANCEL_TAG_NAME", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new java.lang.String[]
                { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new java.lang.String[]
                { "BATDEAL_CANCELTAG", cancelTag }));
                returnInfos.add(returnInfo);
            }
        }
        return returnInfos;
    }

}
