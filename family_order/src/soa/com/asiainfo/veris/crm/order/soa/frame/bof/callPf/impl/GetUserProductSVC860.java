package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * 拆分sql取数据
 * 对应 :TF_F_USER_PRODUCT_MAIN_USER_ID_A_SVC860_TRADESVC
 *
 */
public class GetUserProductSVC860 implements ICallPfDeal 
{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		String tradeId = input.getString("TRADE_ID");
		IDataset result = new DatasetList();
		
		IDataset tradeSvcs = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
		
		if(IDataUtil.isNotEmpty(tradeSvcs))
		{
			for(int i=0,size =tradeSvcs.size();i<size;i++)
			{
				IData tradeSvc = tradeSvcs.getData(i);
				String userId = tradeSvc.getString("USER_ID");
				
				IDataset userSvcs = UserSvcInfoQry.qryUserSvcByUserSvcId(userId, "860");
				
				if(IDataUtil.isNotEmpty(userSvcs))
				{
					for(int j=0,sizej =userSvcs.size();j<sizej;j++)
					{
						IData userSvc = userSvcs.getData(j);
						String userIda = userSvc.getString("USER_ID_A");
						
						result.addAll(this.getUserProductInfo(userIda));
					}
				}
			}
		}
		if(IDataUtil.isNotEmpty(result))
		{
			result = DataHelper.distinct(result, "USER_ID,USER_ID_A,PRODUCT_ID,PRODUCT_MODE,BRAND_CODE,START_DATE,END_DATE,UPDATE_STAFF_ID", ",");
		}
		
		return result;
	}
	
	public IDataset getUserProductInfo(String userIda) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userIda);
		
		StringBuilder sql = new StringBuilder(500);
		sql.append("SELECT DISTINCT TO_CHAR(P.USER_ID) USER_ID, ");
		sql.append("                P.PARTITION_ID, ");
		sql.append("                TO_CHAR(P.USER_ID_A) USER_ID_A, ");
		sql.append("                P.PRODUCT_ID, ");
		sql.append("                P.PRODUCT_MODE, ");
		sql.append("                P.BRAND_CODE, ");
		sql.append("                TO_CHAR(P.INST_ID) INST_ID, ");
		sql.append("                TO_CHAR(P.CAMPN_ID) CAMPN_ID, ");
		sql.append("                TO_CHAR(P.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("                TO_CHAR(P.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
		sql.append("                TO_CHAR(P.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("                P.UPDATE_STAFF_ID, ");
		sql.append("                P.UPDATE_DEPART_ID, ");
		sql.append("                P.REMARK, ");
		sql.append("                P.RSRV_NUM1, ");
		sql.append("                P.RSRV_NUM2, ");
		sql.append("                P.RSRV_NUM3, ");
		sql.append("                TO_CHAR(P.RSRV_NUM4) RSRV_NUM4, ");
		sql.append("                TO_CHAR(P.RSRV_NUM5) RSRV_NUM5, ");
		sql.append("                P.RSRV_STR1, ");
		sql.append("                P.RSRV_STR2, ");
		sql.append("                P.RSRV_STR3, ");
		sql.append("                P.RSRV_STR4, ");
		sql.append("                P.RSRV_STR5, ");
		sql.append("                TO_CHAR(P.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		sql.append("                TO_CHAR(P.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		sql.append("                TO_CHAR(P.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
		sql.append("                P.RSRV_TAG1, ");
		sql.append("                P.RSRV_TAG2, ");
		sql.append("                P.RSRV_TAG3, ");
		sql.append("                P.MAIN_TAG ");
		sql.append(" FROM TF_F_USER_PRODUCT P ");
		sql.append(" WHERE P.USER_ID = :USER_ID ");
		sql.append("   AND P.PARTITION_ID = MOD(:USER_ID, 10000) ");
		sql.append("   AND P.MAIN_TAG = '1' ");
		sql.append("   AND SYSDATE BETWEEN P.START_DATE AND P.END_DATE ");
		
		return Dao.qryBySql(sql, param);
	}

}
