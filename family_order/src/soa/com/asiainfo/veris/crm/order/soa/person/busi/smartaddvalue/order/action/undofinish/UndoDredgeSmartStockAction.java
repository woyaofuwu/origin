package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.action.undofinish;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;

public class UndoDredgeSmartStockAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		// TODO Auto-generated method stub
		
		String tradeId = mainTrade.getString("TRADE_ID");
		String staffId = mainTrade.getString("TRADE_STAFF_ID");
		String eparchyCode = mainTrade.getString("TRADE_EPARCHY_CODE");
		String cityCode = mainTrade.getString("TRADE_CITY_CODE");
		IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
		 for (int i = 0; i < discntTrades.size(); i++)
         {
			 IData tradeDiscnt = discntTrades.getData(i);
             String modifyTag = tradeDiscnt.getString("MODIFY_TAG");
             String discntCode = tradeDiscnt.getString("DISCNT_CODE");
			 if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) )
             {
				 IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",discntCode,null);
	            	if(IDataUtil.isNotEmpty(commparaInfos9211)){
	            		String paraCode12 = commparaInfos9211.first().getString("PARA_CODE12");
	            		String resKindCode = discntCode.substring(discntCode.length()-4, discntCode.length());
	            		if("VIP".equals(paraCode12)){
	            			IDataset result = ActiveStockInfoQry.queryByResKind(resKindCode, CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getCityCode(), CSBizBean.getTradeEparchyCode());
	            			if(IDataUtil.isNotEmpty(result)){
	            				IData cond = new DataMap();
	                	        cond.put("RES_KIND_CODE", resKindCode);
	                	        cond.put("STAFF_ID", staffId);
	                	        cond.put("EPARCHY_CODE", eparchyCode);
	                	        cond.put("CITY_CODE", cityCode);
	                	        StringBuilder sql = new StringBuilder(200);
	                	        sql.append(" UPDATE TF_F_ACTIVE_STOCK");
	                	        sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U - 1");
	                	        sql.append(" WHERE STAFF_ID = :STAFF_ID");
	                	        sql.append(" AND EPARCHY_CODE = :EPARCHY_CODE");
	                	        sql.append(" AND RES_KIND_CODE = :RES_KIND_CODE");
	                	        sql.append(" AND CITY_CODE = :CITY_CODE");
	                	        Dao.executeUpdate(sql, cond);
	            			}
	            		}
	            	}
			 
             }
         }
	}

}
