package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophoneschoolcreatewideuser.order.action.reg;


import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class NoPhoneSchoolProductAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		UcaData uca = btd.getRD().getUca();
		String productId = uca.getProductId();
		
		if(productId!=null){
			//获取优惠子台帐 
			List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
	           if (discntTrades != null && discntTrades.size() > 0) {
	               for (DiscntTradeData discntTrade : discntTrades) {
	                   if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
	                       String discntCode = discntTrade.getDiscntCode();

	                       if (discntCode != null) {
	                    	   IDataset discntCodeeles = CommparaInfoQry.getCommparaByCode1("CSM", "650", productId, discntCode, null);
	                    	   
	                    	   if(IDataUtil.isNotEmpty(discntCodeeles)){
	               	    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"不允许办理【"+productId+"】套餐中的【"+discntCode+"】优惠!");
	                    	   }
	                       }
	                   }
	               }
	           }
		}
		
	}

}
