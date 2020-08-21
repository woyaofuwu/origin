package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;

public class OneCardMultiNoHandleActiveAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String userId = mainTrade.getString("USER_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        String inModecode = mainTrade.getString("IN_MODE_CODE");
        
        //只允许营业厅渠道办理
        if("0".equals(inModecode)){
        	 IDataset commparaInfos6707 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","6707",null,null);
	    	 if(IDataUtil.isNotEmpty(commparaInfos6707)){
	    		 IDataset tradeRelationInfo = TradeRelaInfoQry.getTradeUURelaByTradeId(tradeId);
	    		 if(IDataUtil.isNotEmpty(tradeRelationInfo)){
	    			 for(int i=0;i<tradeRelationInfo.size();i++){
	    				 String modify = tradeRelationInfo.getData(i).getString("MODIFY_TAG");
	    				 if(BofConst.MODIFY_TAG_ADD.equals(modify))
	    				 {
	    					 String tag = tradeRelationInfo.first().getString("RSRV_TAG3");
	    	    			 if("Y".equals(tag)){
	    	    				 IData saleactiveData = new DataMap();
	    		    	         saleactiveData.put("SERIAL_NUMBER",serialNumber);
	    		    	         saleactiveData.put("PRODUCT_ID", commparaInfos6707.getData(0).getString("PARAM_CODE"));
	    		    	         saleactiveData.put("PACKAGE_ID", commparaInfos6707.getData(0).getString("PARA_CODE1"));
	    		    	         saleactiveData.put("NO_TRADE_LIMIT", "TRUE");
	    		    	         saleactiveData.put("SKIP_RULE", "TRUE");
	    		    		     CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
	    		    		     break;
	    	    			 }
	    				 }
	    				 
	    			 }
	    			
	    		 }
	    		 
	    	 }
        	
        }
        
        
	}
	
}
