package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class SplitGroupProductFromAcctSVC extends GroupOrderService
{
    private static final long serialVersionUID = 5426198235477911579L;
    
    public IDataset crtTrade(IData data) throws Exception
    {
        SplitGroupProductFromAcctBean bean = new SplitGroupProductFromAcctBean();
        return bean.crtTrade(data);
    }
    
    public IDataset queryDefPayRelationByCustIdAndAcctId(IData data) throws Exception
    {
        String custId = data.getString("CUST_ID");
        String acctId = data.getString("ACCT_ID");
        SplitGroupProductFromAcctBean bean = new SplitGroupProductFromAcctBean();
        return bean.queryDefPayRelationByCustIdAndAcctId(acctId,custId);
    }
}
