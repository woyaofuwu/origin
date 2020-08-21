
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.fee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.fee.FeeListMgr;

public class FeeMgrSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 获取费用配置,主要供框架调用
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getFeeConfigInfos(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        returnSet.add(FeeListMgr.getFeeConfigInfos(input));
        return returnSet;
    }

    /**
     * 业务初始化 加载费用列表
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getTradeFee(IData input) throws Exception
    {
        return FeeListMgr.getTradeOperFee(input);
    }
}
