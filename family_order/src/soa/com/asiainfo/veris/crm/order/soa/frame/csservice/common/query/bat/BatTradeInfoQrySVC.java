
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;

public class BatTradeInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /** 根据返销状态等查询详细 */
    public IDataset batchDetialQuery(IData data) throws Exception
    {
        return BatTradeInfoQry.batchDetialAllQuery(data, this.getPagination());
    }

    /** 根据返销状态等查询详细 */
    public IDataset batchDetialQueryVPMN(IData data) throws Exception
    {
        return BatTradeInfoQry.batchDetialQueryAllVPMN(data, this.getPagination(), Route.getJourDb(Route.CONN_CRM_CG));
    }

    public IDataset exportbatchDetialQuery(IData data) throws Exception
    {

        return BatTradeInfoQry.batchDetialAllQuery(data, new Pagination());
    }

    public IDataset exportbatchDetialQueryHYYYK(IData data) throws Exception
    {
        return BatTradeInfoQry.batchDetialQueryHYYYK(data, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
    }

    public IDataset exportbatchDetialQueryHYYYKOpen(IData data) throws Exception
    {
        return BatTradeInfoQry.batchDetialQueryHYYYKOpen(data, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
    }

    public IDataset exportbatchDetialQueryVPMN(IData data) throws Exception
    {

        return BatTradeInfoQry.batchDetialQueryVPMN(data, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
    }

    public IDataset exportbatchDetialQueryYDZF(IData data) throws Exception
    {

        return BatTradeInfoQry.batchDetialQueryYDZF(data, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
    }

    public IDataset exportbatchDetialQueryYDZFConfirm(IData data) throws Exception
    {

        return BatTradeInfoQry.batchDetialQueryYDZFConfirm(data, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
    }

    public IDataset exportbatchDetialQueryYDZFOpen(IData data) throws Exception
    {

        return BatTradeInfoQry.batchDetialQueryYDZFOpen(data, new Pagination(), Route.getJourDb(Route.CONN_CRM_CG));
    }

    // 根据批量任务查询批次号
    public IDataset getBatchIdByBatchTaskId(IData idata) throws Exception
    {
        String batchTaskId = idata.getString("BATCH_TASK_ID");
        String batchId = BatTradeInfoQry.getBatchIdByBatchTaskId(batchTaskId);
        IData data = new DataMap();
        data.put("BATCH_ID", batchId);

        IDataset dataSet = new DatasetList();
        dataSet.add(data);
        return dataSet;
    }

    public IDataset getBatSumInfo(IData data) throws Exception
    {
        IData idata = BatTradeInfoQry.getBatSumInfo(data);
        IDataset idataset = new DatasetList();
        idataset.add(idata);
        return idataset;
    }

    public IDataset qryTradeBatByPK(IData data) throws Exception
    {
        IData batData = BatTradeInfoQry.qryTradeBatByPK(data);
        IDataset dataset = new DatasetList();
        dataset.add(batData);
        return dataset;
    }

    /**
     * 根据页面条件查询批次详细信息
     * 
     * @throws Exception
     */
    public IDataset queryBatchInfo(IData data) throws Exception
    {

        return BatTradeInfoQry.queryBatchInfo(data, this.getPagination(), Route.getJourDb(Route.CONN_CRM_CG));
    }

    public IDataset queryBatchTypeParamsEx(IData idata) throws Exception
    {
        IData data = BatTradeInfoQry.queryBatchTypeParamsEx(idata.getString("BATCH_OPER_CODE", ""), CSBizBean.getTradeEparchyCode());
        IDataset dataSet = new DatasetList();
        dataSet.add(data);
        return dataSet;
    }

    /**
     * 依据TRADE_ATTR:2 查询集团批量类型
     */
    public IDataset queryBatchTypes(IData idata) throws Exception
    {
        IDataset dataSet = BatTradeInfoQry.queryBatchTypes();
        return dataSet;
    }

    public IDataset queryBatTasks(IData iData) throws Exception
    {
        String batchTaskId = iData.getString("BATCH_TASK_ID");
        IDataset retDataSet = BatTradeInfoQry.queryBatTasks(batchTaskId, null);
        return retDataSet;

    }

    public IDataset queryBatTypeByPK(IData idata) throws Exception
    {
        IData data = BatTradeInfoQry.queryBatTypeByPK(idata);
        IDataset dataSet = new DatasetList();
        dataSet.add(data);
        return dataSet;
    }

    public IDataset updateBatDealStartToRun(IData data) throws Exception
    {
        BatDealBean.updateBatDealStartToRun(data);
        IDataset retList = new DatasetList();
        return retList;
    }
}
