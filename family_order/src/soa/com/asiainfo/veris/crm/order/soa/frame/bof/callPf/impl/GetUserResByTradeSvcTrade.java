package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;

/**
 * 拆分sql取数据
 * 对应 :TF_F_USER_RES_TRADESVC
 *
 */
public class GetUserResByTradeSvcTrade  implements ICallPfDeal
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
				result.addAll(this.getUserResInfo(userId));
			}
		}

		if(IDataUtil.isNotEmpty(result))
		{
			result = DataHelper.distinct(result, "USER_ID,USER_ID_A,RES_TYPE_CODE,RES_CODE,IMSI,KI,INST_ID,START_DATE,END_DATE,UPDATE_STAFF_ID", ",");
		}
		
		return result;
	}
	
	public IDataset getUserResInfo(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		
		StringBuilder sql = new StringBuilder(500);
		sql.append("SELECT DISTINCT TO_CHAR(R.USER_ID) USER_ID,R.PARTITION_ID, ");
		sql.append("                TO_CHAR(R.USER_ID_A) USER_ID_A, ");
		sql.append("                R.RES_TYPE_CODE,R.RES_CODE,R.IMSI,R.KI, ");
		sql.append("                TO_CHAR(R.INST_ID) INST_ID, ");
		sql.append("                TO_CHAR(R.CAMPN_ID) CAMPN_ID, ");
		sql.append("                TO_CHAR(R.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("                TO_CHAR(R.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
		sql.append("                TO_CHAR(R.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("                R.UPDATE_STAFF_ID,R.UPDATE_DEPART_ID,R.REMARK, ");
		sql.append("                R.RSRV_NUM1,R.RSRV_NUM2,R.RSRV_NUM3, ");
		sql.append("                TO_CHAR(R.RSRV_NUM4) RSRV_NUM4, ");
		sql.append("                TO_CHAR(R.RSRV_NUM5) RSRV_NUM5, ");
		sql.append("                R.RSRV_STR1,R.RSRV_STR2,R.RSRV_STR3,R.RSRV_STR4,R.RSRV_STR5, ");
		sql.append("                TO_CHAR(R.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		sql.append("                TO_CHAR(R.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		sql.append("                TO_CHAR(R.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
		sql.append("                R.RSRV_TAG1,R.RSRV_TAG2,R.RSRV_TAG3 ");
		sql.append("  FROM TF_F_USER_RES R ");
		sql.append(" WHERE R.USER_ID = :USER_ID ");
		sql.append("   AND R.PARTITION_ID = MOD(:USER_ID, 10000) ");
		sql.append("   AND R.END_DATE >= SYSDATE ");

		return Dao.qryBySql(sql, param);
	}

}
