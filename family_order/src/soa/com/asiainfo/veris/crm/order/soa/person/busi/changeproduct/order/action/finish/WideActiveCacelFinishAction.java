package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import com.ailk.bizservice.callpf.auto.expression.function.SystemFunctions;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;

/**
 * @Description: 产品变更完判断是否取消宽带1+活动
 * @author: lizj
 * @date: 2018-10-22
 */
public class WideActiveCacelFinishAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String endFlg = mainTrade.getString("RSRV_STR10");
    	IDataset productTrades = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
    	if (IDataUtil.isNotEmpty(productTrades))
        {
    		if(endFlg!=null)
    		{
    			for(int i=0;i<productTrades.size();i++){
        			String offerCode = productTrades.getData(i).getString("PRODUCT_ID");
        			//String startDate = productTrades.getData(i).getString("START_DATE");
        			
        			IDataset commparaInfos9221 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9921",offerCode,null);
            		if(IDataUtil.isNotEmpty(commparaInfos9221)){
            			for (int j = 0; j < commparaInfos9221.size(); j++) {
            				 String productId = commparaInfos9221.getData(i).getString("PARA_CODE1");
    	        			 SystemFunctions str =new SystemFunctions();
    	        			 if(!str.isEmpty(productId)){
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
    		                           if("Y".equals(endFlg)){
    		                        	   endActiveParam.put("END_DATE_VALUE", "0"); 
    		                           }else{
    		                        	   endActiveParam.put("END_DATE_VALUE", "3"); 
    		                           }
//    		                           if(SysDateMgr.dayInterval(startDate,SysDateMgr.date2String(new Date(),"yyyy-MM-dd"))==0){
//    		                        	   endActiveParam.put("END_DATE_VALUE", "0"); 
//    		                           }else{
//    		                        	   endActiveParam.put("END_DATE_VALUE", "3"); 
//    		                           }
    		                           endActiveParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
    		                           endActiveParam.put("END_MONTH_LAST", "Y");
    		                           endActiveParam.put("NO_TRADE_LIMIT", "TRUE");
    		                           endActiveParam.put("SKIP_RULE", "TRUE");
    		                           endActiveParam.put("PAGE_SOURCE", "N");
    		                           CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
    	        				 }
    	                     }
            			}
            		}
        			
        			
        		}
    		}
    		
    		
                	
        }
		
	}
}
