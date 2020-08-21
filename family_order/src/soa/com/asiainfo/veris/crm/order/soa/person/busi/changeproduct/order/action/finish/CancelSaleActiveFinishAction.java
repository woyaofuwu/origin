package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import org.apache.log4j.Logger;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;


/**
 * @Description: 产品变更非指定范围优惠时终止指定营销活动
 * @author: lizj
 * @date: 2019-4-25
 */
public class CancelSaleActiveFinishAction implements ITradeFinishAction{
	
	private static transient Logger logger = Logger.getLogger(CancelSaleActiveFinishAction.class);

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		IDataset productTradesAdd = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
    	IDataset productTradesDel = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_DEL);
    	IDataset DiscntTradesAdd = TradeDiscntInfoQry.queryTradeDiscntByTradeIdAndTag(tradeId, BofConst.MODIFY_TAG_ADD,userId);
    	IDataset DiscntTradesDel = TradeDiscntInfoQry.queryTradeDiscntByTradeIdAndTag(tradeId, BofConst.MODIFY_TAG_DEL,userId);
    	logger.debug("进入CacelSaleActiveFinishAction"+DiscntTradesAdd+";"+DiscntTradesDel);
    	if (IDataUtil.isNotEmpty(productTradesAdd)&&IDataUtil.isNotEmpty(productTradesDel))
        {
    		IDataset commparaInfos9933 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9933",null,null);
			System.out.println("CacelSaleActiveFinishAction标志"+commparaInfos9933);
    		if(IDataUtil.isNotEmpty(commparaInfos9933)){
        			for (int j = 0; j < commparaInfos9933.size(); j++) {
        				 String productId = commparaInfos9933.getData(j).getString("PARAM_CODE");
        				 String products = commparaInfos9933.getData(j).getString("PARA_CODE20");//档次范围的产品
        				 String products2 = commparaInfos9933.getData(j).getString("PARA_CODE21");//档次范围的产品扩展
        				 String paraCode6 = commparaInfos9933.getData(j).getString("PARA_CODE6","3");//终止时间 0:当前  3：月底
        				 logger.debug("CacelSaleActiveFinishAction参数;productId:"+productId+";products:"+products);
	        			 if(StringUtils.isNotBlank(productId)&&StringUtils.isNotBlank(products)){
	        				 products = products + products2;
	        				 boolean addFlg =false,delFlg =false;
        					 for(int i=0;i<DiscntTradesDel.size();i++){
	        					 String discntCode = DiscntTradesDel.getData(i).getString("DISCNT_CODE");
//	        					 if(products.contains(discntCode)){
//	        						 delFlg = true;
//	        						 break;
//	        					 }
	        					 if(StringCompareString(discntCode,products)){
	        						 delFlg = true;
	        						 break;
	        					 }
	        				 }
        					 
        					 
        					 
        					 
        					 for(int k=0;k<DiscntTradesAdd.size();k++){
	        					 String discntCode = DiscntTradesAdd.getData(k).getString("DISCNT_CODE");
//	        					 if(products.contains(discntCode)){
//	        						 addFlg = true;
//	        						 break;
//	        					 }
	        					 if(StringCompareString(discntCode,products)){
	        						 addFlg = true;
	        						 break;
	        					 }
	        					 
	        				 }
	        				 
	        				 
	        		         if(delFlg&&!addFlg){
	        		        	 IData params=new DataMap();
    	        				 params.put("USER_ID", userId);
    	        				 params.put("PRODUCT_ID", productId);
    	        				 IDataset iDataset= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_SALE_ACTIVE_IFUSE2", params); // SEL_SALE_ACTIVE_UP_IFUSE少个RELATION_TRADE_ID参数
    	        				 if(iDataset.size()>0){
    	        					   IData endActiveParam = new DataMap();
    		                           endActiveParam.put("SERIAL_NUMBER", serialNumber);
    		                           endActiveParam.put("PRODUCT_ID", productId);
    		                           endActiveParam.put("PACKAGE_ID", iDataset.getData(0).getString("PACKAGE_ID"));
    		                           endActiveParam.put("RELATION_TRADE_ID", iDataset.getData(0).getString("RELATION_TRADE_ID"));
    		                           endActiveParam.put("CHECK_MODE", "F");
    		                           endActiveParam.put("CAMPN_TYPE", iDataset.getData(0).getString("CAMPN_TYPE"));
    		                           endActiveParam.put("RETURNFEE", "0");
    		                           endActiveParam.put("YSRETURNFEE", "0");
    		                           endActiveParam.put("TRUE_RETURNFEE_COST", "0");
    		                           endActiveParam.put("TRUE_RETURNFEE_PRICE", "0");		                         
   		                        	   endActiveParam.put("END_DATE_VALUE", paraCode6); //终止到月底
//    		                           if(SysDateMgr.dayInterval(startDate,SysDateMgr.date2String(new Date(),"yyyy-MM-dd"))==0){
//    		                        	   endActiveParam.put("END_DATE_VALUE", "0"); 
//    		                           }else{
//    		                        	   endActiveParam.put("END_DATE_VALUE", "3"); 
//    		                           }
    		                           endActiveParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
    		                           //endActiveParam.put("END_MONTH_LAST", "Y");
    		                           endActiveParam.put("NO_TRADE_LIMIT", "TRUE");
    		                           endActiveParam.put("SKIP_RULE", "TRUE");
    		                           System.out.println("完工接口参数"+endActiveParam);
    		                           CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
    	        				 }
	        		         }
	        		         
	        		         if(delFlg){
	        		        	 break;
	        		         }
	        				
	                     }
        			}
    			
    		   
    			
    		}
                	
        }
		
	}
	
	//拆分以 ; 隔开的字符串
	public  boolean StringCompareString (String str,String allStr){
		if(StringUtils.isBlank(str)||StringUtils.isBlank(allStr)){
			return false;
		}
		
		boolean flag = false;
		for(String s:allStr.split("\\|")){
			 if(str.equals(s)){
				 flag = true;
				 break;
			 }
		 }
		
		return flag;
	}
	
	
	
}
