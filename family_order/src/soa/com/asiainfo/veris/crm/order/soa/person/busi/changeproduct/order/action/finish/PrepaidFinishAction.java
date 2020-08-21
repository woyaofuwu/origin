package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;


/**
 * @Description: REQ201901300008移动花卡预充值方式的优化
 * @author: lizj
 * @date: 2019-2-15
 */
public class PrepaidFinishAction implements ITradeFinishAction{
	
	private static  Logger logger = Logger.getLogger(PrepaidFinishAction.class);

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		System.out.println("PrepaidFinishAction标志1");
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
    	IDataset productTrades = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
    	if (IDataUtil.isNotEmpty(productTrades)){
    		for(int i=0;i<productTrades.size();i++){
    			String offerCode = productTrades.getData(i).getString("PRODUCT_ID");
    			IDataset commparaInfos2578 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","2578","1",offerCode);   		  
		    	if(IDataUtil.isNotEmpty(commparaInfos2578)){
		    		  if(!"1".equals(commparaInfos2578.getData(0).getString("PARA_CODE6"))){
   				        AcctCall.recvFee(serialNumber,tradeId, commparaInfos2578.getData(0).getString("PARA_CODE7"), "15001", commparaInfos2578.getData(0).getString("PARA_CODE8"), "0", "产品变更移动花卡预充值");				
   			          }
    		    }	
    		}
    	}
		
	}

}
