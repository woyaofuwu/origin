
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.pos;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.fee.PosLogInfoQry;

public class PosMgrSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 获取POS机交易流水号
     * 
     * @return
     * @throws Exception
     */
    public IDataset getPosTradeId(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("TRADE_POS_ID", "50"+SeqMgr.getPosLogId().substring(2));
        dataset.add(data);
        return dataset;
    }

    /**
     * 根据流水号查询POS刷卡记录
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryPosLog(IData input) throws Exception
    {
        return PosLogInfoQry.queryPosLog(input.getString("TRADE_ID"));
    }

    /**
     * 记录POS刷卡撤销台账
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset recordCancelPosLog(IData input) throws Exception
    {
        PosBean bean = BeanManager.createBean(PosBean.class);
        IDataset dataset = new DatasetList();
        dataset.add(bean.recordCancelPosLog(input));
        return dataset;
    }

    /**
     * 记录POS刷卡缴费台账
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset recordPosLog(IData input) throws Exception
    {
        PosBean bean = BeanManager.createBean(PosBean.class);
        IDataset dataset = new DatasetList();
        dataset.add(bean.recordPosLog(input));
        return dataset;
    }
    
    /**
     * 查询POS成功刷卡记录
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryPosCost(IData input) throws Exception
    {
        PosBean bean = BeanManager.createBean(PosBean.class);
        return bean.queryPosCost(input, getPagination());
    }
    
    /**
     * 查询POS手工调账单
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getPosReceipt(IData input) throws Exception
    {
        PosBean bean = BeanManager.createBean(PosBean.class);
        IDataset dataset = new DatasetList();
        dataset.add(bean.getPosReceipt(input));
        return dataset;
    }
}
