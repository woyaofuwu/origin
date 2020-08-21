package com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch.archimpl;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch.IArchProc;
/**
 * TF_B_TRADE_DATAPCK 的归档实现类
 * @author Administrator
 *
 */
public class ArchDataPckProc implements IArchProc{

	@Override
	public void arch(String tabName, String tradeId, String acceptMonth,
			String cancelTag) throws Exception {
		// TODO Auto-generated method stub

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
		StringBuilder delSql = new StringBuilder(sqlParam.getString("DEL_SQL",""));
		delSql.append(sqlParam.getString("DEL_SQL1",""));
		String tradeRoute = sqlParam.getString("TRADE_ROUTE",Route.getJourDbDefault());
		String userRoute = sqlParam.getString("USER_ROUTE");
		
		IDataset stockSqlParams = BofQuery.getArchSqlByTabName("TF_F_CUST_DATASTOCK");
		if(IDataUtil.isEmpty(stockSqlParams)){
			//抛异常：根据tabName找不到默认的归档sql配置
			CSAppException.apperr(CrmCommException.CRM_COMM_1175,"TF_F_CUST_DATASTOCK");
		}
		StringBuilder stockAddSql = new StringBuilder(stockSqlParams.first().getString("ADD_SQL"));
		IData inParam = new DataMap();
		inParam.put("TRADE_ID", tradeId);
		inParam.put("ACCEPT_MONTH", acceptMonth);
		inParam.put("CANCEL_TAG", cancelTag);
		
		IDataset tabTradeInfoSet = Dao.qryBySql(selSql, inParam,tradeRoute);

		for(int i=0;i<tabTradeInfoSet.size();i++){
			IData tabTradeInfo = tabTradeInfoSet.getData(i);
			String modifyTag = tabTradeInfo.getString("MODIFY_TAG");
			if(StringUtils.equals(modifyTag, BofConst.MODIFY_TAG_ADD)){//新增
				Dao.executeUpdate(addSql, tabTradeInfo,userRoute);
				//还需插流量库存余额表
				Dao.executeUpdate(stockAddSql, tabTradeInfo,userRoute);
			}else if(StringUtils.equals(modifyTag, BofConst.MODIFY_TAG_UPD)){
				Dao.executeUpdate(updSql, tabTradeInfo,userRoute);
			}else if(StringUtils.equals(modifyTag, BofConst.MODIFY_TAG_DEL)){
				if(StringUtils.isBlank(delSql.toString())){
					Dao.executeUpdate(updSql, tabTradeInfo,userRoute);//如果delSql为空则进行逻辑删除
				}else{
					Dao.executeUpdate(delSql, tabTradeInfo,userRoute);
				}
				
			}
		}
		
	
	}

}
