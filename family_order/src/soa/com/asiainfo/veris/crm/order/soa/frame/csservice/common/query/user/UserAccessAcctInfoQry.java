
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserAccessAcctInfoQry
{

    public static IDataset qryInfoByAccessAcct(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_ACCESS_ACCT", "SEL_BY_PK", param);
    }

    public static IDataset qryInfoByAccessAcctId(String accessId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCESS_ACCT", accessId);
        return Dao.qryByCode("TF_F_USER_ACCESS_ACCT", "SEL_BY_BROADBAND_ACCT", param);
    }
    
    public static IDataset qryInfoByUidTimePoint(String userid, String timePoint) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userid);
        param.put("TIME_POINT", timePoint);
        return Dao.qryByCode("TF_F_USER_ACCESS_ACCT", "SEL_BY_UID_DATE", param);
    }
    
    //guozhao 宽带查询
    public static IDataset qryWidenetByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_BY_USERID_NOW", param);
    }

	//guozhao 宽带账号查询
	public static IDataset qryWidenetActByUserId(String user_id) throws Exception 
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);		
		return Dao.qryByCodeParser("TF_F_USER_WIDENET_ACT", "SEL_BY_USERID", param);
	}
	
	//guozhao 
	public static IDataset qrySynOrderInfoByTradeId(String Trade_Id) throws Exception 
	{
		IData param = new DataMap();
		param.put("TRADE_ID", Trade_Id);		
		return Dao.qryByCodeParser("TF_B_INTELLIGENTNET", "SEL_BY_TRADEID", param);
	}
	
}
