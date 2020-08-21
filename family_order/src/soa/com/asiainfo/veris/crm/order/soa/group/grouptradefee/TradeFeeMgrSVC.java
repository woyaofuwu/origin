
package com.asiainfo.veris.crm.order.soa.group.grouptradefee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bizrule.feemgr.TradeFeeMgr;

public class TradeFeeMgrSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 获取集团一次性费用
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getGrpOneOffFee(IData inparam) throws Exception
    {
        return TradeFeeMgr.getGrpOneOffFee(inparam);
    }

}
