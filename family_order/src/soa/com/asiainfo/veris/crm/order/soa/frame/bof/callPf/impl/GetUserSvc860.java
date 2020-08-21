package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;

/**
 * 拆分sql取数据
 * 对应 :USER_ID_A_SVC860
 * duhj
 */
public class GetUserSvc860  implements ICallPfDeal
{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		String userId = input.getString("USER_ID");
		
		IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERSVC_ID", input, BizRoute.getRouteId());
		IDataset userOfferRels = BofQuery.queryUserAllOfferRelByUserId(userId, BizRoute.getRouteId());
		IDataset result = OfferUtil.fillStructAndFilterForPf(userSvcs, userOfferRels);
		if(IDataUtil.isNotEmpty(result))
		{
			result = DataHelper.distinct(result, "USER_ID,PARTITION_ID,USER_ID_A,SERVICE_ID,PRODUCT_ID,PACKAGE_ID,START_DATE", ",");
		}
		return result;
	}
}
