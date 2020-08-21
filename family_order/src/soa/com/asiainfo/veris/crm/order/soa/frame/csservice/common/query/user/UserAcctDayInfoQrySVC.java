
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserAcctDayInfoQrySVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /*
     * 根据user_id查询用户的账期信息（结账日，下账期开始时间，当前账期结束时间等）
     */
    public static IDataset getUserAcctDayAndFirstDateInfo(IData inparams) throws Exception
    {
        String userId = inparams.getString("USER_ID");
        IDataset result = new DatasetList();
        IData acctDayInfo = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);
        if (IDataUtil.isNotEmpty(acctDayInfo))
        {
            result.add(acctDayInfo);
        }
        return result;
    }

}
