package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class UserGrpMerchpInfoNewQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    
    public static IDataset qryMerchpInfoByUserId(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");
    	return UserGrpMerchpInfoNewQry.qryMerchpInfoByUserId(user_id);
    }
    
    
    public static IDataset getAllBySvcIdStateCode(IData param) throws Exception
    {
        String user_id = param.getString("USER_ID");
        String route_id= param.getString("ROUTE_ID");
    	return UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(user_id,route_id);
    }
}
