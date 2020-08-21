
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * 改号完工
 * 
 * @author wangf
 */
public class CancelBankSignAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
    	 IDataset tradeInfo = TradeResInfoQry.getTradeRes(mainTrade.getString("TRADE_ID"), "0", "1");
         if(IDataUtil.isNotEmpty(tradeInfo)){
         	IData param = new DataMap();
         	param.put("SERIAL_NUMBER", tradeInfo.getData(0).getString("RES_CODE",""));
         	//取消银行总对总销户
         	CSAppCall.call("SS.BankPaymentManageIntfSVC.autoDestroyBankSign", param);
         }
    }

}
