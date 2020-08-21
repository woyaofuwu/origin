
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userblackwhiteinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UserblackwhiteIntfViewUtil
{
	
	public static IDataset qryUserblackwhiteCountByEcUserId(IBizCommon bc, String userId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("EC_USER_ID", userId);
        return CSViewCall.call(bc, "CS.UserBlackWhiteInfoQrySVC.getBlackWhiteCountByEcUserId", inparam);
    }
    

}
