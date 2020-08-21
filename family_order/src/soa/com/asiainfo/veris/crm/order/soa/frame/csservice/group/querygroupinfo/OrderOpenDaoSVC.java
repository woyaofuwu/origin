
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss.OrderOpenDao;

public class OrderOpenDaoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 记录跨省工单状态
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset inserPotradeState(IData idata) throws Exception
    {
        idata.put("SYNC_SEQUENCE", SeqMgr.getSYnTradeIdForGrp()); // 同步序列
        IData result = new DataMap();
        boolean flag = OrderOpenDao.inserPotradeState(idata);
        result.put("result", flag);
        return new DatasetList(result);
    }

    /**
     * 记录跨省工单状态信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset inserPotradeStateAttr(IData idata) throws Exception
    {
        OrderOpenDao.inserPotradeStateAttr(idata.getDataset("insert"));
        return null;
    }

    /**
     * 记录跨省工单状态产品
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset inserProductTrade(IData idata) throws Exception
    {
        OrderOpenDao.inserProductTrade(idata);
        return null;
    }

    /**
     * 设置工单完工
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset modifyOrderInfoState(IData idata) throws Exception
    {
        OrderOpenDao orderOpenDao = new OrderOpenDao();
        orderOpenDao.modifyOrderInfoState(idata.getString("ORDER_NO"));
        return null;
    }
}
