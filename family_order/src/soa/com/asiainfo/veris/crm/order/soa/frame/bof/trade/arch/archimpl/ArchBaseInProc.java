package com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch.archimpl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch.IArchProc;
/**
 * TF_B_TRADE_BFAS_IN 的归档实现类
 * @author Administrator
 *
 */
public class ArchBaseInProc implements IArchProc {

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
		String tradeRoute = sqlParam.getString("TRADE_ROUTE",Route.getJourDbDefault());
		String userRoute = sqlParam.getString("USER_ROUTE");
		
		IData inParam = new DataMap();
		inParam.put("TRADE_ID", tradeId);
		inParam.put("ACCEPT_MONTH", acceptMonth);
		inParam.put("CANCEL_TAG", cancelTag);
		
		IDataset tabTradeInfoSet = Dao.qryBySql(selSql, inParam,tradeRoute);

		for(int i=0;i<tabTradeInfoSet.size();i++){
			IData tabTradeInfo = tabTradeInfoSet.getData(i);
			
			//海南此表不要归档，addSql为空
			if (StringUtils.isNotBlank(addSql))
			{
			    Dao.executeUpdate(addSql, tabTradeInfo,userRoute);
			}
		}
	}

}
