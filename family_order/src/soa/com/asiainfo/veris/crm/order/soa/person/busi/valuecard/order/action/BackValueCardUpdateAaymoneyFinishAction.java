
package com.asiainfo.veris.crm.order.soa.person.busi.valuecard.order.action;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;


/**
 * REQ201802260006_有价卡退卡增加付款方式选项的优化
 * @author zhuoyingzhi
 * @date 20180428
 *
 */
public class BackValueCardUpdateAaymoneyFinishAction implements ITradeFinishAction
{
	private static final Logger log = Logger.getLogger(BackValueCardUpdateAaymoneyFinishAction.class);
	
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        
    	String tradeTypeCode= mainTrade.getString("TRADE_TYPE_CODE","");
    	String tradeId= mainTrade.getString("TRADE_ID","");
    	log.debug("---BackValueCardUpdateAaymoneyFinishAction----tradeTypeCode:"+tradeTypeCode+",tradeId:"+tradeId);
		if ("419".equals(tradeTypeCode)) {
			//有价卡退卡
			String backPayMoneyCode= mainTrade.getString("RSRV_STR10","");
			
	    	log.debug("---BackValueCardUpdateAaymoneyFinishAction----backPayMoneyCode:"+backPayMoneyCode);
			if(!"".equals(backPayMoneyCode) && backPayMoneyCode!=null){
				if(backPayMoneyCode.length() == 1){
					//避免update报错
					IData param=new DataMap();
					      param.put("TRADE_ID", tradeId);
					      param.put("PAY_MONEY_CODE", backPayMoneyCode);
					 Dao.executeUpdateByCodeCode("TF_B_TRADEFEE_PAYMONEY", "UPD_PAY_MONEY_CODE", param,Route.getJourDb());
				}
			}
		}
   }
}
