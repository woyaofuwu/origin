package com.asiainfo.veris.crm.order.soa.group.minorec.quickorder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QuickOrderTradeSVC extends CSBizService{

	private static final long serialVersionUID = 1L;

	/**
	 * 
	* @Title: qryTradeInfosByProductId 
	* @Description: 查询当前产品的所有订单信息 
	* @param param
	* @return
	* @throws Exception IDataset
	* @author zhangzg
	* @date 2019年10月18日下午3:44:37
	 */
	public static IDataset qryTradeInfosByProductId(IData param) throws Exception
	{
		return QuickOrderTradeBean.qryTradeInfosByProductId(param);
	}
	
	/**
	 * 
	* @Title: qryTradeInfoByUserId 
	* @Description: 根据userid查询订单信息 
	* @param param
	* @return
	* @throws Exception IDataset
	* @author zhangzg
	* @date 2019年10月18日下午3:44:22
	 */
	public static IDataset qryTradeInfoByUserId(IData param) throws Exception
	{
	    return QuickOrderTradeBean.qryTradeInfoByUserId(param);
	}
	
	public static IDataset qryMemberInfosTradeByUserId(IData param) throws Exception
	{
	    return QuickOrderTradeBean.qryMemberInfosTradeByUserId(param);
	}
	
	public static IDataset qryMemberInfosDataByUserId(IData param) throws Exception
	{
	    return QuickOrderTradeBean.qryMemberInfosDataByUserId(param);
	}
    
}
