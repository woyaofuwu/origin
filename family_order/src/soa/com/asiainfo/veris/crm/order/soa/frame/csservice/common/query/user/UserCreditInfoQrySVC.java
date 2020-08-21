package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;

public class UserCreditInfoQrySVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /*
     * 根据user_id查询查询用户的信用等级信息 liaolc 2014/8/6
     */
    public static IDataset userCreditInfo(IData inparams) throws Exception
    {
        String userId = inparams.getString("USER_ID");
        IDataset result = new DatasetList();
        IData userCreditInfo = CreditCall.queryUserCreditInfos(userId);
        if (IDataUtil.isNotEmpty(userCreditInfo))
        {
            result.add(userCreditInfo);
        }
        return result;
    }
    
    /*
     * 根据user_id查询查询用户的信用等级信息 songlm 2015/5/4
     */
    public static IData getUserCreditInfos(String userId) throws Exception
    {
        IDataset userCreditInfo = CreditCall.getCreditInfo(userId, "0");
        if (IDataUtil.isEmpty(userCreditInfo))
        {
            // 报错
            CSAppException.apperr(BofException.CRM_BOF_018);
        }
        IData creditInfo = userCreditInfo.getData(0);
        return creditInfo;
    }

}
