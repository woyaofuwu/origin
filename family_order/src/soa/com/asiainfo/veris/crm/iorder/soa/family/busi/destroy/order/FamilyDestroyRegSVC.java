package com.asiainfo.veris.crm.iorder.soa.family.busi.destroy.order;

import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @Description 家庭注销登记类
 * @Auther: 吴王丰
 * @Date: 2020/8/10 16:50
 * @version: V1.0
 */
public class FamilyDestroyRegSVC extends OrderService 
{

	@Override
	public String getOrderTypeCode() throws Exception 
	{
		return input.getString(KeyConstants.ORDER_TYPE_CODE, FamilyConstants.FamilyTradeType.CANCELLATION.getValue());
	}

	@Override
	public String getTradeTypeCode() throws Exception 
	{
		return input.getString(KeyConstants.TRADE_TYPE_CODE, FamilyConstants.FamilyTradeType.CANCELLATION.getValue());
	}

}
