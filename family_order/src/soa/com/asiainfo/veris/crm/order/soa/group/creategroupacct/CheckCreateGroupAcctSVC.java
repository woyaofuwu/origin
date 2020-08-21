
package com.asiainfo.veris.crm.order.soa.group.creategroupacct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CheckCreateGroupAcctSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public void checkHasMoreAcct(IData inparam) throws Exception
    {
        CreateGroupAcctBean bean = new CreateGroupAcctBean();
        bean.checkHasMoreAcct(inparam);
    }

    public IDataset checkNumAcct(IData inparam) throws Exception
    {
        CreateGroupAcctBean bean = new CreateGroupAcctBean();
        IData data = bean.checkNumAcct(inparam);
        IDataset result = new DatasetList();
        result.add(data);
        return result;
    }

    public void checkTradeAcct(IData inparam) throws Exception
    {
        CreateGroupAcctBean bean = new CreateGroupAcctBean();
        bean.checkBankRela(inparam);//验证付费银行和收款银行关系 
        bean.checkTradeAcct(inparam);
    }

}
