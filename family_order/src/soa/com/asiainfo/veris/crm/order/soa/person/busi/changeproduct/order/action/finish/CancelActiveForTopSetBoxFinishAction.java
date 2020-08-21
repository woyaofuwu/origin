package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

/**
 * @Description: 新主产品不在《魔百和权益活动最新套餐范围》，终止用户的“魔百和业务体验基础包”活动
 * @author: lizj
 * @date: 2020-3-23
 */
public class CancelActiveForTopSetBoxFinishAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");
		String acceptDate = mainTrade.getString("ACCEPT_DATE");
    	IDataset productTrades = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
    	if (IDataUtil.isNotEmpty(productTrades))
        {
			for(int i=0;i<productTrades.size();i++){
				IData productTrade = productTrades.getData(i);
				String offerCode = productTrades.getData(i).getString("PRODUCT_ID");
				String startDate=productTrade.getString("START_DATE");
			    	
		    	IDataset commparaInfos2324 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","2324",offerCode,null);
	    		if(IDataUtil.isEmpty(commparaInfos2324)){
	    		     String productId = "66005203";
        			 if(StringUtils.isNotBlank(productId)){
        				 IData params=new DataMap();
        				 params.put("USER_ID", userId);
        				 params.put("PRODUCT_ID", productId);
        				 IDataset iDataset= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params);  
        				 if(iDataset.size()>0){
        					   IData endActiveParam = new DataMap();
	                           endActiveParam.put("SERIAL_NUMBER", serialNumber);
	                           endActiveParam.put("PRODUCT_ID", productId);
	                           endActiveParam.put("PACKAGE_ID", iDataset.getData(0).getString("PACKAGE_ID"));
	                           endActiveParam.put("RELATION_TRADE_ID", iDataset.getData(0).getString("RELATION_TRADE_ID"));
	                           endActiveParam.put("CHECK_MODE", "F");
	                           endActiveParam.put("CAMPN_TYPE", "YX04");
	                           endActiveParam.put("RETURNFEE", "0");
	                           endActiveParam.put("YSRETURNFEE", "0");
	                           endActiveParam.put("TRUE_RETURNFEE_COST", "0");
	                           endActiveParam.put("TRUE_RETURNFEE_PRICE", "0");
	                           endActiveParam.put("END_DATE_VALUE", "0"); 
	                           //endActiveParam.put("END_DATE_VALUE", "3"); 
	                           endActiveParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
	                           endActiveParam.put("END_MONTH_LAST", "Y");
	                           endActiveParam.put("NO_TRADE_LIMIT", "TRUE");
	                           endActiveParam.put("SKIP_RULE", "TRUE");
        					   
        					   boolean isProductBook = SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND_YYYYMMDD)
  	        						 .compareTo(SysDateMgr.decodeTimestamp(SysDateMgr.getSysDate(), SysDateMgr.PATTERN_STAND_YYYYMMDD))>0;
  	        						 
	  	        			   //判断是否产品预约变更	 
	  	        			   if(isProductBook){
	  	        				 IData paramProduct = new DataMap();
		  	       	             paramProduct.put("DEAL_ID", SeqMgr.getTradeId());
		  	       	             paramProduct.put("USER_ID", userId);
		  	       	             paramProduct.put("PARTITION_ID", userId.substring(userId.length() - 4));
		  	       	             paramProduct.put("SERIAL_NUMBER", serialNumber);
		  	       	             paramProduct.put("EPARCHY_CODE", eparchyCode);
		  	       	             paramProduct.put("IN_TIME", SysDateMgr.getSysTime());
		  	       	             paramProduct.put("DEAL_STATE", "0");//处理状态:  0未处理,L处理队列中,N正在处理,F处理完成,E处理失败
		  	       	             paramProduct.put("DEAL_TYPE", BofConst.EXP_TOPSET_ACTIVE);
		  	       	             paramProduct.put("EXEC_TIME", SysDateMgr.getAddHoursDate(startDate,1));
		  	       	             paramProduct.put("EXEC_MONTH", SysDateMgr.getMonthForDate(startDate));
		  	       	             paramProduct.put("TRADE_ID", tradeId);
		  	       	            
//		  	       	             IData dealCond = new DataMap();
//		  	       			     dealCond.put("USER_ID", userId);
//		  	       			     dealCond.put("SERIAL_NUMBER", serialNumber);
//		  	       			     dealCond.put("ACCEPT_DATE",SysDateMgr.decodeTimestamp(acceptDate, SysDateMgr.PATTERN_CHINA_DATE));
//		  	       			     dealCond.put("START_DATE", SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_CHINA_DATE));
//		  	                     dealCond.put("OFFER_TYPE","P" );
//	
//		  	       			     String productName = UProductInfoQry.getProductNameByProductId(productTrade.getString("PRODUCT_ID"));
//		  	       			     dealCond.put("TRADE_TYPE_NAME", productName);
//		  	       				 dealCond.put("OFFER_CODE", productTrade.getString("PRODUCT_ID"));
		  	       				 paramProduct.put("DEAL_COND", endActiveParam.toString());
	 
		  	       	             Dao.insert("TF_F_EXPIRE_DEAL", paramProduct);
	  	        			    	
	  	        			   }else{
	  	        				 
	  	        				 CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
	  	        			   }
  	        				 
        				 }
                     }										
		    		
			    }
				
			}
                	
        }
		
	}
}
