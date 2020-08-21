package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class DestroyFamilyEndSaleActiveAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String tradeId = mainTrade.getString("TRADE_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        System.out.println("进入DestroyFamilyEndSaleActiveAction");
        IDataset commparaInfos6700 = CommparaInfoQry.getCommparaByCode4to6("CSM","6700",null,"Y",null,null,"0898");
        if(IDataUtil.isNotEmpty(commparaInfos6700)){
        	 IData params=new DataMap();
			 params.put("USER_ID", userId);
			 IDataset saleActives = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId,"0898");
			 boolean flg = false;
			 for(int i=0;i<saleActives.size();i++){
				 IData  saleActive = saleActives.getData(i);
				 String productId = saleActive.getString("PRODUCT_ID");
				 String packageId = saleActive.getString("PACKAGE_ID");
				 for(int j=0;j<commparaInfos6700.size();j++){
					 IData commpara = commparaInfos6700.getData(j);
					 if(productId.equals(commpara.getString("PARAM_CODE")))
					 {
						 flg = true;
						 break;
					 }
						
				 }
				 if(flg){
					 IData endActiveParam = new DataMap();
                     endActiveParam.put("SERIAL_NUMBER", serialNumber);
                     endActiveParam.put("PRODUCT_ID", productId);
                     endActiveParam.put("PACKAGE_ID", packageId);
                     endActiveParam.put("RELATION_TRADE_ID", saleActive.getString("RELATION_TRADE_ID"));
                     endActiveParam.put("CHECK_MODE", "F");
                     endActiveParam.put("CAMPN_TYPE", saleActive.getString("CAMPN_TYPE"));
                     endActiveParam.put("RETURNFEE", "0");
                     endActiveParam.put("YSRETURNFEE", "0");
                     endActiveParam.put("TRUE_RETURNFEE_COST", "0");
                     endActiveParam.put("TRUE_RETURNFEE_PRICE", "0");		                         
                 	 endActiveParam.put("END_DATE_VALUE", "0"); //终止到月底
                     endActiveParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
                     //endActiveParam.put("END_MONTH_LAST", "Y");
                     endActiveParam.put("NO_TRADE_LIMIT", "TRUE");
                     endActiveParam.put("SKIP_RULE", "TRUE");
                     CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
                     
                     break;
				 }
				 
			 }
        	
        }
		
		
	}

}
