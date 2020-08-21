package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserInfoChgQry
{
    public static IDataset queryInfoChgByUserIdDate(String userId, String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TIME_POINT", timePoint);
        return Dao.qryByCode("TF_F_USER_INFOCHANGE", "SEL_BY_USERID_DATETIME2", param);
    }
    
	public static IDataset getUserInfoChgByUserIdCurvalid(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);

		IDataset iDataset = Dao.qryByCode("TF_F_USER_INFOCHANGE", "SEL_BY_USERID_CURVALID2", param);
		return iDataset;
	}
    
}
