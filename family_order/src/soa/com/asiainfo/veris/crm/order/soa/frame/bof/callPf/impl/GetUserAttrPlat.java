package com.asiainfo.veris.crm.order.soa.frame.bof.callPf.impl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.callPf.ICallPfDeal;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;

/**
 * 拆分sql取数据
 * 对应 :TF_F_USER_ATTR_PLAT
 *
 */
public class GetUserAttrPlat implements ICallPfDeal 
{

	@Override
	public IDataset dealPfData(IData input) throws Exception
	{
		// TODO Auto-generated method stub
		IDataset result = new DatasetList();
		
		String userId = input.getString("USER_ID");
		String tradeId = input.getString("TRADE_ID");
		
		IDataset tradePlatSvcs = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
		
		if(IDataUtil.isNotEmpty(tradePlatSvcs))
		{
			for(int i=0,size =tradePlatSvcs.size();i<size;i++)
			{
				IData tradePlatSvc = tradePlatSvcs.getData(i);
				String instId = tradePlatSvc.getString("INST_ID");
				result.addAll(this.getUserAttrInfo(userId, tradeId, instId));
			}
		}

		if(IDataUtil.isNotEmpty(result))
		{
			DataHelper.distinct(result, "USER_ID,INST_TYPE,RELA_INST_ID,INST_ID,ATTR_CODE,ATTR_VALUE,START_DATE,END_DATE,UPDATE_STAFF_ID", ",");
		}
		
		return result;
	}
	
	public IDataset getUserAttrInfo(String userId,String tradeId,String instId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_ID", tradeId);
		param.put("INST_ID", instId);
		
		StringBuilder sql = new StringBuilder(1000);
		sql.append("SELECT A.PARTITION_ID PARTITION_ID, ");
		sql.append("       TO_CHAR(A.USER_ID) USER_ID, ");
		sql.append("       A.INST_TYPE INST_TYPE, ");
		sql.append("       A.RELA_INST_ID RELA_INST_ID, ");
		sql.append("       A.INST_ID INST_ID, ");
		sql.append("       A.ATTR_CODE ATTR_CODE, ");
		sql.append("       A.ATTR_VALUE ATTR_VALUE, ");
		sql.append("       TO_CHAR(A.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
		sql.append("       TO_CHAR(A.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE, ");
		sql.append("       TO_CHAR(A.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
		sql.append("       A.UPDATE_STAFF_ID UPDATE_STAFF_ID, ");
		sql.append("       A.UPDATE_DEPART_ID UPDATE_DEPART_ID, ");
		sql.append("       A.REMARK REMARK, ");
		sql.append("       A.RSRV_NUM1 RSRV_NUM1, ");
		sql.append("       A.RSRV_NUM2 RSRV_NUM2, ");
		sql.append("       A.RSRV_NUM3 RSRV_NUM3, ");
		sql.append("       TO_CHAR(A.RSRV_NUM4) RSRV_NUM4, ");
		sql.append("       TO_CHAR(A.RSRV_NUM5) RSRV_NUM5, ");
		sql.append("       A.RSRV_STR1 RSRV_STR1, ");
		sql.append("       A.RSRV_STR2 RSRV_STR2, ");
		sql.append("       A.RSRV_STR3 RSRV_STR3, ");
		sql.append("       A.RSRV_STR4 RSRV_STR4, ");
		sql.append("       A.RSRV_STR5 RSRV_STR5, ");
		sql.append("       TO_CHAR(A.RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1, ");
		sql.append("       TO_CHAR(A.RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
		sql.append("       TO_CHAR(A.RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3, ");
		sql.append("       A.RSRV_TAG1 RSRV_TAG1, ");
		sql.append("       A.RSRV_TAG2 RSRV_TAG2, ");
		sql.append("       A.RSRV_TAG3 RSRV_TAG3, ");
		sql.append("       :TRADE_ID TRADE_ID ");
		sql.append("  FROM TF_F_USER_ATTR A ");
		sql.append(" WHERE A.USER_ID = TO_NUMBER(:USER_ID) ");
		sql.append("   AND A.RELA_INST_ID = :INST_ID ");
		sql.append("   AND A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
		sql.append("   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE ");

		
		return Dao.qryBySql(sql, param);
	}

}
