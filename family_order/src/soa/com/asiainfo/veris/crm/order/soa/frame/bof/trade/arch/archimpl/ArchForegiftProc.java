package com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch.archimpl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch.IArchProc;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
/**
 * TF_B_TRADEFEE_SUB 的归档实现类
 * @author Administrator
 *
 */
public class ArchForegiftProc implements IArchProc {

	@Override
	public void arch(String tabName, String tradeId, String acceptMonth,
			String cancelTag) throws Exception {
		// TODO Auto-generated method stub
		IDataset sqlParams = BofQuery.getArchSqlByTabName(tabName);
		if(IDataUtil.isEmpty(sqlParams)){
			//抛异常：根据tabName找不到默认的归档sql配置
			CSAppException.apperr(CrmCommException.CRM_COMM_1175,tabName);
		}
		IData sqlParam = sqlParams.first();
		StringBuilder selSql = new StringBuilder(sqlParam.getString("SEL_SQL",""));
		selSql.append(sqlParam.getString("SEL_SQL1",""));
		StringBuilder addSql = new StringBuilder(sqlParam.getString("ADD_SQL",""));
		addSql.append(sqlParam.getString("ADD_SQL1",""));
		StringBuilder updSql = new StringBuilder(sqlParam.getString("UPD_SQL",""));
		updSql.append(sqlParam.getString("UPD_SQL1",""));

		String tradeRoute = sqlParam.getString("TRADE_ROUTE",Route.getJourDbDefault());
		String userRoute = sqlParam.getString("USER_ROUTE");
		
		IData inParam = new DataMap();
		inParam.put("TRADE_ID", tradeId);
		inParam.put("ACCEPT_MONTH", acceptMonth);
		inParam.put("CANCEL_TAG", cancelTag);
		
		IDataset tabTradeInfoSet = Dao.qryBySql(selSql, inParam,tradeRoute);

		for(int i=0;i<tabTradeInfoSet.size();i++){
			IData tabTradeInfo = tabTradeInfoSet.getData(i);
			
			IDataset mainTrades = TradeInfoQry.getMainTradeByTradeId(tradeId);
			
			tabTradeInfo.put("CUST_NAME", mainTrades.getData(0).getString("CUST_NAME"));
			
			String userId = tabTradeInfo.getString("USER_ID");
			String partitionId = tabTradeInfo.getString("PARTITION_ID");
			String feeTypeCode = tabTradeInfo.getString("FEE_TYPE_CODE");
			long count = BofQuery.getGiftCountByGiftCode(userId, partitionId, feeTypeCode);
			if(count==0){
				Dao.executeUpdate(addSql, tabTradeInfo,userRoute);//新增
			}else{
				Dao.executeUpdate(updSql, tabTradeInfo,userRoute);//修改
			}
		}
		
	}

}
