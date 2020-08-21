
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.seq;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class SeqMgrSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 获取批量任务序列
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset getBatchId(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_id", SeqMgr.getBatchId());
        idataset.add(result);
        return idataset;
    }

    /**
     * chenyi 2014-6-13 获取bboss商品订单号
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset getBBossMerchIdForGrp(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_BBOSS_merch_id", SeqMgr.getBBossMerchIdForGrp());
        idataset.add(result);
        return idataset;
    }

    /**
     * cheny 2014-6-13 获取bboss产品订单号
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset getBBossProductIdForGrp(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_BBOSS_product_id", SeqMgr.getBBossProductIdForGrp());
        idataset.add(result);
        return idataset;
    }

    public IDataset getGrpMolist(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("SEQ_GRP_MOLIST", SeqMgr.getGrpMolist());
        idataset.add(result);
        return idataset;
    }

    public IDataset getMaxNumberLine(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_id", SeqMgr.getMaxNumberLine());
        idataset.add(result);
        return idataset;

    }

    public IDataset getPbssBizProdInstId(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_id", SeqMgr.getPbssBizProdInstId());
        idataset.add(result);
        return idataset;
    }

    public IDataset getPbssBizSubsId(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_id", SeqMgr.getPbssBizSubsId());
        idataset.add(result);
        return idataset;
    }

    /**
     * 获取序列
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset getSYnTradeIdForGrp(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_id", SeqMgr.getSYnTradeIdForGrp());
        idataset.add(result);
        return idataset;
    }

    public IDataset getTradeId(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_id", SeqMgr.getTradeId());
        idataset.add(result);
        return idataset;
    }

    public IDataset getUserId(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_id", SeqMgr.getUserId());
        idataset.add(result);
        return idataset;
    }

    public IDataset getVpmnIdIdForGrp(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_id", SeqMgr.getVpmnIdIdForGrp());
        idataset.add(result);
        return idataset;
    }

    public IDataset getWlwBizCode(IData idata) throws Exception
    {
        IDataset idataset = new DatasetList();
        IData result = new DataMap();
        result.put("seq_id", SeqMgr.getWlwBizCode());
        idataset.add(result);
        return idataset;
    }
}
