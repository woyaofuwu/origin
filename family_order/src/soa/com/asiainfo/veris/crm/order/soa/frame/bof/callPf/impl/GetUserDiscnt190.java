package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;

/**
 * 拆分sql取数据
 * 对应 :TF_F_USER_DISCNT_190
 * duhj
 */
public class GetUserDiscnt190  implements ICallPfDeal
{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		String userId = input.getString("USER_ID");
		
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "QRY_USER_DISCNT_190_NEW", input, BizRoute.getRouteId());
		IDataset userOfferRels = BofQuery.queryUserAllOfferRelByUserId(userId, BizRoute.getRouteId());
		return OfferUtil.fillStructAndFilterForPf(userDiscnts, userOfferRels);
	}
}
