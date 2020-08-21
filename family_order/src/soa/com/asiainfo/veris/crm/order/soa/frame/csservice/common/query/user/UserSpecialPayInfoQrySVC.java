
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserSpecialPayInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param input
     * @return
     * @throws Exception
     */

    public IDataset qryUserSpecialPayForUPGP(IData input) throws Exception
    {        
        String userId = input.getString("USER_ID");
        String acctId = input.getString("ACCT_ID");
        String payItemCode = input.getString("PAYITEM_CODE");
        return UserSpecialPayInfoQry.qryUserSpecialPayForUPGP(userId, acctId,payItemCode);
    }
    
}
