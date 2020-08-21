package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;


/**
 * 拆分sql取数据
 * 对应 :TF_F_USER_PRODUCT_MAIN_USER_ID_A_SVC860_TRADESVC
 *
 */
public class GetUserSvc801BySvc860 implements ICallPfDeal 
{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		String tradeId = input.getString("TRADE_ID");
		IDataset result = new DatasetList();

		IDataset tradeSvcs = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);

		if (IDataUtil.isNotEmpty(tradeSvcs)) 
		{
			String userId = "";
			for (int i = 0, size = tradeSvcs.size(); i < size; i++) {
				IData tradeSvc = tradeSvcs.getData(i);
				userId = tradeSvc.getString("USER_ID");

				IDataset userSvcs = UserSvcInfoQry.qryUserSvcByUserSvcId(userId, "860");

				if (IDataUtil.isNotEmpty(userSvcs))
				{
					for (int j = 0, sizej = userSvcs.size(); j < sizej; j++)
					{
						IData userSvc = userSvcs.getData(j);
						String userIda = userSvc.getString("USER_ID_A");
						
						IDataset userIdaSvc = this.getUserSvcInfo(userIda);
						IDataset userOfferRels = BofQuery.queryUserAllOfferRelByUserId(userIda, BizRoute.getRouteId());

						result.addAll(OfferUtil.fillStructAndFilterForPf(userIdaSvc, userOfferRels));
					}
				}
			}
		
			if (IDataUtil.isNotEmpty(result))
			{
				result = DataHelper.distinct(result,"USER_ID,USER_ID_A,SERVICE_ID,MAIN_TAG,INST_ID,START_DATE,END_DATE,UPDATE_STAFF_ID",",");
			}

			return result;
		
		}

		return result;
	}
	
	public IDataset getUserSvcInfo(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		
		StringBuilder sql = new StringBuilder(500);
		sql.append("SELECT DISTINCT TO_CHAR(UA.USER_ID) USER_ID, ");
		sql.append("                UA.PARTITION_ID, ");
		sql.append("                TO_CHAR(UA.USER_ID_A) USER_ID_A, ");
		sql.append("                UA.SERVICE_ID, ");
		sql.append("                UA.MAIN_TAG, ");
		sql.append("                UA.INST_ID, ");
		sql.append("                UA.CAMPN_ID, ");
		sql.append("                TO_CHAR(UA.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("                TO_CHAR(UA.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
		sql.append("                TO_CHAR(UA.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("                UA.UPDATE_STAFF_ID, ");
		sql.append("                UA.UPDATE_DEPART_ID, ");
		sql.append("                UA.REMARK, ");
		sql.append("                UA.RSRV_NUM1, ");
		sql.append("                UA.RSRV_NUM2, ");
		sql.append("                UA.RSRV_NUM3, ");
		sql.append("                UA.RSRV_NUM4, ");
		sql.append("                UA.RSRV_NUM5, ");
		sql.append("                UA.RSRV_STR1, ");
		sql.append("                UA.RSRV_STR2, ");
		sql.append("                UA.RSRV_STR3, ");
		sql.append("                UA.RSRV_STR4, ");
		sql.append("                UA.RSRV_STR5, ");
		sql.append("                UA.RSRV_DATE1, ");
		sql.append("                UA.RSRV_DATE2, ");
		sql.append("                UA.RSRV_DATE3, ");
		sql.append("                UA.RSRV_TAG1, ");
		sql.append("                UA.RSRV_TAG2, ");
		sql.append("                UA.RSRV_TAG3 ");
		sql.append("  FROM TF_F_USER_SVC UA ");
		sql.append(" WHERE UA.USER_ID = :USER_ID ");
		sql.append("   AND UA.PARTITION_ID = MOD(:USER_ID, 10000) ");
		sql.append("   AND UA.SERVICE_ID = '801' ");
		
		return Dao.qryBySql(sql, param);
	}

}
