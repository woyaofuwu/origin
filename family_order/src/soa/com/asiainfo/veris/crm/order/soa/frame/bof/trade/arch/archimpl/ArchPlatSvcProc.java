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
 * TF_B_TRADE_PLATSVC的归档实现类
 * @author Administrator
 *
 */
public class ArchPlatSvcProc implements IArchProc {

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
		StringBuilder delSql = new StringBuilder(sqlParam.getString("DEL_SQL",""));
		delSql.append(sqlParam.getString("DEL_SQL1",""));
		String tradeRoute = sqlParam.getString("TRADE_ROUTE",Route.getJourDbDefault());
		String userRoute = sqlParam.getString("USER_ROUTE");
		
		IData inParam = new DataMap();
		inParam.put("TRADE_ID", tradeId);
		inParam.put("ACCEPT_MONTH", acceptMonth);
		inParam.put("CANCEL_TAG", cancelTag);
		
		IDataset tabTradeInfoSet = Dao.qryBySql(selSql, inParam,tradeRoute);
		
		String operCodeSet = "04,05,06,07,19,20,90,91";
		IDataset traceSqlParams = BofQuery.getArchSqlByTabName("TF_F_USER_PLATSVC_TRACE");
		if(IDataUtil.isEmpty(traceSqlParams)){
			//抛异常：根据tabName找不到默认的归档sql配置
			CSAppException.apperr(CrmCommException.CRM_COMM_1175,"TF_F_USER_PLATSVC_TRACE");
		}
		StringBuilder traceAddSql = new StringBuilder(traceSqlParams.first().getString("ADD_SQL"));
		StringBuilder traceUpdSql = new StringBuilder(traceSqlParams.first().getString("UPD_SQL"));
		for(int i=0;i<tabTradeInfoSet.size();i++){
			IData tabTradeInfo = tabTradeInfoSet.getData(i);
			String modifyTag = tabTradeInfo.getString("MODIFY_TAG");
			if(StringUtils.equals(modifyTag, BofConst.MODIFY_TAG_ADD)){//新增
				Dao.executeUpdate(addSql, tabTradeInfo,userRoute);
			}else if(StringUtils.equals(modifyTag, BofConst.MODIFY_TAG_UPD)){
				Dao.executeUpdate(updSql, tabTradeInfo,userRoute);
			}else if(StringUtils.equals(modifyTag, BofConst.MODIFY_TAG_DEL)){
				if(StringUtils.isBlank(delSql.toString())){
					Dao.executeUpdate(updSql, tabTradeInfo,userRoute);//如果delSql为空则进行逻辑删除
				}else{
					Dao.executeUpdate(delSql, tabTradeInfo,userRoute);
				}
				
			}
			/*****************************特殊逻辑start****************************************************/
			
			String operCode = tabTradeInfo.getString("OPER_CODE");
			if(StringUtils.contains(operCodeSet, operCode)){
				Dao.executeUpdate(traceUpdSql, tabTradeInfo,userRoute);//先upd再add
				
				Dao.executeUpdate(traceAddSql, tabTradeInfo,userRoute);
			}
		}
		
	}
	
	

}
