package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;

public class OneCardMultiNoCancelActiveAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String userId = mainTrade.getString("USER_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        System.out.println("进入OneCardMultiNoCancelActiveAction"+tradeId);
    	IDataset commparaInfos6707 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","6707",null,null);
    	if(IDataUtil.isNotEmpty(commparaInfos6707)){
    		String productId = commparaInfos6707.first().getString("PARAM_CODE");
    		String packageId = commparaInfos6707.first().getString("PARA_CODE1");
    		 IDataset tradeRelationInfo = TradeRelaInfoQry.getTradeUURelaByTradeId(tradeId);
    		 System.out.println("6707标志"+commparaInfos6707+";"+tradeRelationInfo);
    		 if(IDataUtil.isNotEmpty(tradeRelationInfo)){
    			 for(int i=0;i<tradeRelationInfo.size();i++){
    				 String modify = tradeRelationInfo.getData(i).getString("MODIFY_TAG");
    				 if(BofConst.MODIFY_TAG_DEL.equals(modify))
    				 {
    					 String userIdB = tradeRelationInfo.getData(i).getString("USER_ID_B");
    		    		 IDataset relaUUInfo = RelaUUInfoQry.qryByRelaUserIdBRouteId(userIdB,"M2",Route.CONN_CRM_CG);
    		    			if(IDataUtil.isNotEmpty(relaUUInfo)){
    		    				String tag = relaUUInfo.first().getString("RSRV_TAG3");
    		    				if("Y".equals(tag)){
    		        				IData params=new DataMap();
    		    				    params.put("USER_ID", userId);
    		    				    params.put("PRODUCT_ID", productId);
    		    				    params.put("PACKAGE_ID", packageId);
    		    				    IDataset iDataset= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params);  
    		    				    if(IDataUtil.isNotEmpty(iDataset)){
    		    				    	IData endActiveParam = new DataMap();
    		    	                    endActiveParam.put("SERIAL_NUMBER", serialNumber);
    		    	                    endActiveParam.put("PRODUCT_ID", productId);
    		    	                    endActiveParam.put("PACKAGE_ID", packageId);
    		    	                    endActiveParam.put("RELATION_TRADE_ID", iDataset.getData(0).getString("RELATION_TRADE_ID"));
    		    	                    endActiveParam.put("CHECK_MODE", "F");
    		    	                    endActiveParam.put("CAMPN_TYPE", commparaInfos6707.first().getString("PARA_CODE2","YX04"));
    		    	                    endActiveParam.put("RETURNFEE", "0");
    		    	                    endActiveParam.put("YSRETURNFEE", "0");
    		    	                    endActiveParam.put("TRUE_RETURNFEE_COST", "0");
    		    	                    endActiveParam.put("TRUE_RETURNFEE_PRICE", "0");
    		    	                    endActiveParam.put("END_DATE_VALUE", "0"); 
    		    	                    endActiveParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
    		    	                    endActiveParam.put("END_MONTH_LAST", "Y");
    		    	                    endActiveParam.put("NO_TRADE_LIMIT", "TRUE");
    		    	                    endActiveParam.put("SKIP_RULE", "TRUE");
    		    	                    CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
    		    	                    break;
    		    				    }
    		        				
    		        			}
    		    			}
    				 }
    				 
    			 }
    			
    		 }
    		
    	}
        
		
	}

}
