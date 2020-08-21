package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;

/**
 * 同步用户数据，忽略产品的开始时间
 * 对应 :TF_F_USER_DISCNT
 * @author tanzheng
 *
 */
public class GetUserDiscntIgnoreStartDate implements ICallPfDeal
{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		String userId = input.getString("USER_ID");
		
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_IGNORE_START", input, BizRoute.getRouteId());
		IDataset userOfferRels = BofQuery.queryUserAllOfferRelByUserId(userId, BizRoute.getRouteId());
		return OfferUtil.fillStructAndFilterForPf(userDiscnts, userOfferRels);
	}
}
