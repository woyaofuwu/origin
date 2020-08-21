package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;

/**
 * 拆分sql取数据
 * 对应 :TF_F_USER_SVC_USERIDB
 * @author duhj
 *
 */
public class GetNPUserSvcUserIdB implements ICallPfDeal{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		String userId = input.getString("USER_ID");
		IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_NP", input, Route.getJourDb());
		IDataset userOfferRels = BofQuery.queryNPUserAllOfferRelByUserId(userId, BizRoute.getRouteId());
		IDataset result = OfferUtil.fillStructAndFilterForPf(userSvcs, userOfferRels);
		System.out.println("----------GetNPUserSvcUserIdB---mqx--------------result="+result);
		if(IDataUtil.isNotEmpty(result))
		{
			result = DataHelper.distinct(result, "USER_ID,USER_ID_A,SERVICE_ID,PRODUCT_ID,PACKAGE_ID,START_DATE", ",");
		}
		System.out.println("----------GetNPUserSvcUserIdB---mqx-----afterdistinct---------result="+result);
		return result;
	}
}
