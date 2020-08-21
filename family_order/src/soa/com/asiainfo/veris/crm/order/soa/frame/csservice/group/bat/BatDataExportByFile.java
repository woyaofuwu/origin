
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;

public class BatDataExportByFile extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData arg0, Pagination arg1) throws Exception
    {
        IDataset exportData = new DatasetList();
        String vpmnFlag = arg0.getString("vpmnFlag");// 标记判断
        if ("0".equals(vpmnFlag))
        {// 普通操作类型
            exportData = exportData(arg0, arg1);
        }
        else
        {// VPMN成员 融合总机（分机）成员 有短号的导出
            exportData = exportDataVPMN(arg0, arg1);
        }
        return exportData;
    }

    /** 导出失败数据 */
    public IDataset exportData(IData arg0, Pagination arg1) throws Exception
    {
        String batchOperType = arg0.getString("cond_BATCH_OPER_TYPE", "");
        IData param = new DataMap();
        param.put("BATCH_ID", arg0.getString("cond_BATCH_ID", ""));
        param.put("DEAL_STATE", arg0.getString("cond_DEAL_STATE", ""));
        param.put("SERIAL_NUMBER", arg0.getString("cond_SERIAL_NUMBER", ""));

        IDataset dataset = new DatasetList();// 导出数据列表
        if ("BATADDYDZFMEMINTF".equals(batchOperType))
        {
            dataset = BatTradeInfoQry.batchDetialQueryYDZF(param, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
        }
        else if ("BATCONFIRMYDZFINTF".equals(batchOperType))
        {
            dataset = BatTradeInfoQry.batchDetialQueryYDZFConfirm(param, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
        }
        else if ("BATOPENYDZFINTF".equals(batchOperType))
        {
            dataset = BatTradeInfoQry.batchDetialQueryYDZFOpen(param, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
        }
        else if ("BATADDHYYYKMEMINTF".equals(batchOperType))
        {
            dataset = BatTradeInfoQry.batchDetialQueryHYYYK(param, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
        }
        else if ("BATOPENHYYYKINTF".equals(batchOperType))
        {
            dataset = BatTradeInfoQry.batchDetialQueryHYYYKOpen(param, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
        }
        else if ("NEWXXTUSERREG".equals(batchOperType) || "NEWXXTUSERREG_SPE".equals(batchOperType) || "NEWXXTUSERCHANGE".equals(batchOperType) )
        {
        	dataset = BatTradeInfoQry.batchDetialQueryxxt(param, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
        }
        else
        {
            dataset = BatTradeInfoQry.batchDetialAllQuery(param, new Pagination());
        }

        dataset = getBatchExportDesc(dataset);
        return dataset;
    }

    /** 导出失败数据 */
    public IDataset exportDataVPMN(IData arg0, Pagination arg1) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData param = new DataMap();
        param.put("BATCH_ID", arg0.getString("cond_BATCH_ID", ""));
        param.put("DEAL_STATE", arg0.getString("cond_DEAL_STATE", ""));
        param.put("SERIAL_NUMBER", arg0.getString("cond_SERIAL_NUMBER", ""));
        dataset = BatTradeInfoQry.batchDetialQueryAllVPMN(param, new Pagination(),Route.getJourDb(Route.CONN_CRM_CG));
        dataset = getBatchExportDesc(dataset);
        return dataset;
    }

    public IDataset getBatchExportDesc(IDataset dataset) throws Exception
    {
        IDataset retDataset = new DatasetList();
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData data = dataset.getData(i);
            data.put("EXPORT_DESC", data.getString("DEAL_DESC"));
            retDataset.add(data);
        }
        return retDataset;
    }
}
