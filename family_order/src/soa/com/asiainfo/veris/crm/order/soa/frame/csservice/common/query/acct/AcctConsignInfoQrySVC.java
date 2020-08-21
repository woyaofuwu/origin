
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AcctConsignInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 根据USER_ID查询集团用户默认付费帐户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getConsignInfoByAcctId(IData input) throws Exception
    {
        String acctId = input.getString("ACCT_ID");
        IDataset data = AcctConsignInfoQry.getConsignInfoByAcctId(acctId);

        return data;
    }

    /**
     * 根据账户标识 ACCT_ID 获取账户托收信息
     * 
     * @author fengsl
     * @date 2013-03-18
     * @param input
     * @return
     * @throws Exception
     */

    public IDataset getConsignInfoByAcctIdForGrp(IData input) throws Exception
    {
        String acctId = input.getString("ACCT_ID");
        IDataset data = AcctConsignInfoQry.getConsignInfoByAcctIdForGrp(acctId);
        return data;
    }

}
