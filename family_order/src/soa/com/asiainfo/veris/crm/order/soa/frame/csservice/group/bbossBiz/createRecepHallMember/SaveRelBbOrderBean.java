package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createRecepHallMember;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;

public class SaveRelBbOrderBean extends GroupOrderBaseBean{
	@Override
	public void actOrderDataOther(IData map) throws Exception
    {
		IData merchInfo = getMerchInfo();
		SaveRelBbTradeBean merchBean=new SaveRelBbTradeBean();
		if(IDataUtil.isNotEmpty(merchInfo)){
			IData merchUUInfo=new DataMap();
			merchUUInfo.put("SERIAL_NUMBER", merchInfo.getString("SERIAL_NUMBER"));//成员号码
			merchUUInfo.put("USER_ID", merchInfo.getString("USER_ID"));//集团商品userid
			merchUUInfo.put("TRADE_TYPE_CODE", merchInfo.getString("TRADE_TYPE_CODE"));
			merchUUInfo.put("MODIFY_TAG", merchInfo.getString("MODIFY_TAG"));
			merchBean.crtTrade(merchUUInfo);
		}
		SaveRelBbTradeBean orderBean=new SaveRelBbTradeBean();
		IDataset orderInfo=getOrderInfo();
		IData orderUUInfo=new DataMap();
	    orderUUInfo.put("SERIAL_NUMBER",orderInfo.getData(0).getString("SERIAL_NUMBER"));//成员号码
        orderUUInfo.put("USER_ID",orderInfo.getData(0).getString("USER_ID"));//集团产品userid
        orderUUInfo.put("TRADE_TYPE_CODE", orderInfo.getData(0).getString("TRADE_TYPE_CODE"));
        orderUUInfo.put("MODIFY_TAG", orderInfo.getData(0).getString("MODIFY_TAG"));
        orderBean.crtTrade(orderUUInfo);
    }
	
	protected void cmtOrderData_() throws Exception
    {
        super.cmtOrderData_();
    }

	@Override
	// 订单类型order_type_code
	protected String setOrderTypeCode() throws Exception
	{
		return "2300";
	}

}
