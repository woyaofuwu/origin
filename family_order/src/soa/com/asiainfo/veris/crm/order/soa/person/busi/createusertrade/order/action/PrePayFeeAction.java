package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


/**
 * @Description: REQ201906170001 新增“京东物流专项套餐”
 * @author: lizj
 * @date: 2019-6-15
 */
public class PrePayFeeAction implements ITradeAction{
	
	private static  Logger logger = Logger.getLogger(PrePayFeeAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		System.out.println("PrepaidFinishAction标志reg");
		List<ProductTradeData> productTrade = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
		 if(productTrade != null && productTrade.size() > 0)
	        {
	            for(ProductTradeData product : productTrade)
	            {
	                if(BofConst.MODIFY_TAG_ADD.equals(product.getModifyTag()))
	                {
	                	IDataset commparaInfos2588 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","2588",null,product.getProductId());
	            		if(IDataUtil.isNotEmpty(commparaInfos2588))
	            		{
	            			String remark = commparaInfos2588.getData(0).getString("REMARK");
	    		    		String tradeFee = commparaInfos2588.getData(0).getString("PARA_CODE7");
	    		    		String channelId = commparaInfos2588.getData(0).getString("PARA_CODE5","15001");
	    		    		String paymentId = commparaInfos2588.getData(0).getString("PARA_CODE8");
	    		    		String payFeeModeCode = commparaInfos2588.getData(0).getString("PARA_CODE6");
	    		    		if(!"N".equals(commparaInfos2588.getData(0).getString("PARA_CODE10"))){
	    		    			FeeTradeData feeTradeData = new FeeTradeData();
		        				feeTradeData.setFee(tradeFee);
		        				feeTradeData.setFeeMode("2");
		        				feeTradeData.setFeeTypeCode(paymentId);
		        				feeTradeData.setOldfee(tradeFee);
		        				feeTradeData.setRemark(remark);
		        				feeTradeData.setUserId(btd.getRD().getUca().getUserId());
		        				btd.add(btd.getRD().getUca().getSerialNumber(), feeTradeData);
    		    		    }
	            		}
	                	
	                }
		
	            }
	        }
		
	}
}
