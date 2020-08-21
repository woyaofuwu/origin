package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.biz.exception.BizErr;
import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class DestroyBBossGroupMemberPageDataTrans extends PageDataTrans {
	@Override
	public IData transformData() throws Exception {
		IData data = super.transformData();

		// 成员用户信息
		IData memSubscriber = getMemSubscriber();
		if (DataUtils.isEmpty(memSubscriber)) {
			BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到成员用户信息数据结构！");
		}
		data.put("SERIAL_NUMBER", memSubscriber.getString("SERIAL_NUMBER"));

		// 集团用户信息
		IData ecSubscriber = getEcSubscriber();
		if (DataUtils.isEmpty(ecSubscriber)) {
			BizException.bizerr(BizErr.BIZ_ERR_1, "没有获取到集团用户信息数据结构！");
		}
		data.put("USER_ID", ecSubscriber.getString("USER_ID"));

		IData commonInfo = getCommonData();
		if (DataUtils.isNotEmpty(commonInfo)) {
			data.putAll(commonInfo);
		}

		// 通过产品user_id查询商品的user_id
		String userIdB = ecSubscriber.getString("USER_ID");

		IData input = new DataMap();
		input.put("USER_ID_B", userIdB);

		// 通过成员产品编码查询集团产品编码
		String mebOfferId = getOfferList().first().getString("OFFER_ID");
		IDataset productOfferList = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(null,mebOfferId,"1");

		String grpMerchPID = productOfferList.first().getString("OFFER_ID");		
		IDataset merchOfferList = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(null,grpMerchPID,"4");

		input.put("RELATION_TYPE_CODE", IUpcViewCall.getRelationTypeCodeByOfferId(merchOfferList.first().getString("OFFER_ID")));
		input.put("ROUTE_EPARCHY_CODE", Route.CONN_CRM_CG);
 		IDataInput inputInparams = new DataInput();
		inputInparams.getData().putAll(input);

		IDataOutput subOffersInfos = ServiceFactory.call("CS.RelaBBInfoQrySVC.qryRelaBBInfoByUserIdBRelaTypeCode", inputInparams);

		IDataset subOffersInfo = subOffersInfos.getData();

		data.put("USER_ID", subOffersInfo.first().getString("USER_ID_A"));

		// 路由信息
		String eparchyCode = memSubscriber.getString("EPARCHY_CODE");
		data.put("ROUTE_EPARCHY_CODE", eparchyCode);
		//data.put("EPARACHY_CODE", eparchyCode);

		return data;
	}

	public void setServiceName() throws Exception {
		setSvcName("CS.DestroyBBossMemSVC.dealBBossMebBiz");

	}

}
