package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WeChatScoreQry
{
	/**
	 * 赠送次数，**次/每个用户/步长，在‘活动开始时间’与‘活动结束时间’字段值内
	 * 步长：天
	 */
	public static IDataset queryCountByDay(String activityId, String userId) throws Exception
	{
		IData param = new DataMap();
        param.put("ACTIVITY_ID", activityId);
        param.put("USER_ID", userId);
        
		return Dao.qryByCodeParser("TL_B_WECHAT_SCORE", "QRY_COUNT_BY_DAY",param);
	}
	
	/**
         *‘活动名额/每天’字段，指在规定的‘活动开始时间’与‘活动结束时间’字段值内，每天成功参与活动能支持最大的用户数(剔重) 
	 */
	public static IDataset queryAllUserCountByDay(String activityId) throws Exception
	{
		IData param = new DataMap();
        param.put("ACTIVITY_ID", activityId);        
		return Dao.qryByCodeParser("TL_B_WECHAT_SCORE", "QRY_ALLUSER_COUNT_BY_DAY",param);
	}
	
	/**
         *‘活动名额/每月’字段，指在规定的‘活动开始时间’与‘活动结束时间’字段值内，每月（自然月）成功参与活动能支持最大的用户数(剔重) 
	 */
	public static IDataset queryAllUserCountByMonth(String activityId) throws Exception
	{
		IData param = new DataMap();
        param.put("ACTIVITY_ID", activityId);        
		return Dao.qryByCodeParser("TL_B_WECHAT_SCORE", "QRY_ALLUSER_COUNT_BY_MONTH",param);
	}
	
	/**
	 * 赠送次数，**次/每个用户/步长，在‘活动开始时间’与‘活动结束时间’字段值内
	 * 步长：周
	 */
	public static IDataset queryCountByWeek(String activityId, String userId) throws Exception
	{
		IData param = new DataMap();
        param.put("ACTIVITY_ID", activityId);
        param.put("USER_ID", userId);
        
		return Dao.qryByCodeParser("TL_B_WECHAT_SCORE", "QRY_COUNT_BY_WEEK",param);
	}
	
	/**
	 * 赠送次数，**次/每个用户/步长，在‘活动开始时间’与‘活动结束时间’字段值内
	 * 步长：月
	 */
	public static IDataset queryCountByMonth(String activityId, String userId) throws Exception
	{
		IData param = new DataMap();
        param.put("ACTIVITY_ID", activityId);
        param.put("USER_ID", userId);
        
		return Dao.qryByCodeParser("TL_B_WECHAT_SCORE", "QRY_COUNT_BY_MONTH",param);
	}
	
	/**
	 * 总赠送次数，**次/每个用户/每年
	 */
	public static IDataset queryTotalCountByUser(String activityId, String userId) throws Exception
	{
		IData param = new DataMap();
        param.put("ACTIVITY_ID", activityId);
        param.put("USER_ID", userId);
        
		return Dao.qryByCodeParser("TL_B_WECHAT_SCORE", "QRY_COUNT_BY_USERID",param);
	}
	
	/**
	 * 某活动能支持最大的用户数
	 */
	public static IDataset queryActivityCount(String activityId) throws Exception
	{
		IData param = new DataMap();
        param.put("ACTIVITY_ID", activityId);
        
		return Dao.qryByCodeParser("TL_B_WECHAT_SCORE", "QRY_COUNT_BY_ACTIVITYID",param);
	}
	
	/**
	 * 某类型活动的已办理用户数
	 */
	public static IDataset queryTypeActivityCount(String paraCode10) throws Exception
	{
		IData param = new DataMap();
        param.put("PARA_CODE10", paraCode10);
        
		return Dao.qryByCodeParser("TL_B_WECHAT_SCORE", "QRY_COUNT_BY_TYPEACTIVITYID",param);
	}
}
