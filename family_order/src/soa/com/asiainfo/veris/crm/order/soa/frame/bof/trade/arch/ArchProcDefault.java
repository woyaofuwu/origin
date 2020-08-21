package com.asiainfo.veris.crm.order.soa.frame.bof.trade.arch;
        
import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

public class ArchProcDefault implements IArchProc{

	@Override
	public void arch(String tabName, String tradeId, String acceptMonth,String cancelTag)
			throws Exception {
		// TODO Auto-generated method stub
		IDataset sqlParams = BofQuery.getArchSqlByTabName(tabName);
		
		//如果为空不抛异常直接返回跟存储过程保持一致，TF_B_TRADE_SMS 在归档过程中没有处理
		if(IDataUtil.isEmpty(sqlParams)){
			//抛异常：根据tabName找不到默认的归档sql配置
//			CSAppException.apperr(CrmCommException.CRM_COMM_1175,tabName);
		    
		    return ;
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
		}
		
	}

}
