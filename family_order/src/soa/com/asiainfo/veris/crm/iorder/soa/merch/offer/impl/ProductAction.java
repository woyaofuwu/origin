package com.asiainfo.veris.crm.iorder.soa.merch.offer.impl;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.merch.offer.DisposeAction;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public class ProductAction implements DisposeAction
{

	@Override
	public IDataset execute(IData data) throws Exception
	{
		boolean isCheckRule = BizEnv.getEnvBoolean("MERCH_OFFER_CHECK_RULE_P", false);
		IDataset products = new DatasetList(data.getString("OFFERS"));
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		param.put("SELECTED_ELEMENTS", "[]");
		param.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
		param.put("TRADE_TYPE_CODE", "110");
		for (int i = 0; i < products.size(); i++)
		{
			IData product = products.getData(i);
			String productId = product.getString("OFFER_CODE");
			String hasGroup = product.getString("HAVE_COMGROUP", "0");
			String tagValue = product.getString("TAG_VALUE");
			if ("USED".equals(tagValue) || "ORDER".equals(tagValue))// 已订购或者购物车
			{
				product.put("DISABLED", "true");
				continue;
			}
			if ("1".equals(hasGroup))
			{
				product.put("DISABLED_ORDER", "true");
				product.put("DISABLED_ADD_SHOPPING_CART", "true");
			}
			
//			if(!StaffPrivUtil.isProdPriv(CSBizBean.getVisit().getStaffId(), productId))
//			{
//				product.put("DISABLED", "true");
//				product.put("TAG_VALUE", "无权限");
//			}
			
			if (isCheckRule && StringUtils.isNotBlank(productId) && "0".equals(hasGroup))
			{
				param.put("NEW_PRODUCT_ID", productId);
				try
				{
					IDataset ds = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", param);
				}
				catch (Exception e)
				{
					product.put("DISABLED", "true");
				}
			}
		}
		return products;
	}
}
