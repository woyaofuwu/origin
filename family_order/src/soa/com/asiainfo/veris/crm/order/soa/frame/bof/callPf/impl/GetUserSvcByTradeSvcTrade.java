package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;

/**
 * 拆分sql取数据
 * 对应:TF_F_USER_SVC_TRADESVC
 *
 */

public class GetUserSvcByTradeSvcTrade  implements ICallPfDeal
{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		IDataset result = new DatasetList();
		String tradeId = input.getString("TRADE_ID");
		
		IDataset tradeInfos = TradeInfoQry.getTradeInfobyTradeId(tradeId);
		
		if(IDataUtil.isNotEmpty(tradeInfos))
		{
			String tradeUserId = tradeInfos.getData(0).getString("USER_ID");
			IDataset tradeSvcs = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
			
			for(int i=0,size =tradeSvcs.size();i<size;i++)
			{
				IData tradeSvc = tradeSvcs.getData(i);
				String userId = tradeSvc.getString("USER_ID");
				if(StringUtils.equals(userId, tradeUserId))
				{
					continue;
				}
				IDataset userSvc = this.getUserSvcInfo(userId);
				IDataset userOfferRels = BofQuery.queryUserAllOfferRelByUserId(userId, BizRoute.getRouteId());
				
				result.addAll(OfferUtil.fillStructAndFilterForPf(userSvc, userOfferRels));
			}
		

			if(IDataUtil.isNotEmpty(result))
			{
				result = DataHelper.distinct(result, "USER_ID,USER_ID_A,SERVICE_ID,MAIN_TAG,INST_ID,START_DATE,END_DATE,UPDATE_STAFF_ID", ",");
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
		sql.append("SELECT DISTINCT TO_CHAR(U.USER_ID) USER_ID,U.PARTITION_ID, ");
		sql.append("                TO_CHAR(U.USER_ID_A) USER_ID_A, ");
		sql.append("                U.SERVICE_ID, ");
		sql.append("                U.MAIN_TAG,U.INST_ID,U.CAMPN_ID, ");
		sql.append("                TO_CHAR(U.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("                TO_CHAR(U.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
		sql.append("                TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("                U.UPDATE_STAFF_ID,U.UPDATE_DEPART_ID,U.REMARK, ");
		sql.append("                U.RSRV_NUM1,U.RSRV_NUM2,U.RSRV_NUM3,U.RSRV_NUM4,U.RSRV_NUM5, ");
		sql.append("                U.RSRV_STR1,U.RSRV_STR2,U.RSRV_STR3,U.RSRV_STR4,U.RSRV_STR5, ");
		sql.append("                U.RSRV_DATE1,U.RSRV_DATE2,U.RSRV_DATE3, ");
		sql.append("                U.RSRV_TAG1,U.RSRV_TAG2,U.RSRV_TAG3 ");
		sql.append("  FROM TF_F_USER_SVC U ");
		sql.append(" WHERE U.USER_ID = :USER_ID ");
		sql.append("   AND U.PARTITION_ID = MOD(:USER_ID, 10000) ");
		sql.append("   AND SYSDATE BETWEEN U.START_DATE AND U.END_DATE ");

		return Dao.qryBySql(sql, param);
	}

}
