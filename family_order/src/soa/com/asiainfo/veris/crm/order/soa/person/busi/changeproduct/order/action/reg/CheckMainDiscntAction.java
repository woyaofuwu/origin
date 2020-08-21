package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class CheckMainDiscntAction implements ITradeAction{
	private static final Logger log = Logger.getLogger(CheckMainDiscntAction.class); 

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		/**
		 * step.1 查询当前台账产品数据
		 */
    	String productId=btd.getMainTradeData().getProductId();
		List<ProductTradeData> changeProducts=btd.get("TF_B_TRADE_PRODUCT"); 
    	if(changeProducts.size() > 0 && changeProducts != null)
    	{
    		return;
		}
    	//System.out.println("--------------CheckMainDiscntAction---------------productId:"+productId);
    	List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		if(discntTradeDatas.size() > 0 && discntTradeDatas != null)
		{
			for(int i=0,size=discntTradeDatas.size();i<size;i++){
				DiscntTradeData discntTradeData=discntTradeDatas.get(i);
				String discntCode=discntTradeData.getDiscntCode();
				
				String modifyTag = discntTradeData.getModifyTag();
				//System.out.println("--------------CheckMainDiscntAction---------------discntCode:"+discntCode);

				IDataset ids = UpcCall.qryRelOfferByComRelOfferIdRelOfferId(productId,"P",discntCode,"D");
				//System.out.println("--------------CheckMainDiscntAction---------------ids:"+ids);
				if(IDataUtil.isNotEmpty(ids) && modifyTag.equals(BofConst.MODIFY_TAG_DEL))
				{
	                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您有没有变更主产品，不能删除基础主套餐。");

				}
			}
		}
	}
}
