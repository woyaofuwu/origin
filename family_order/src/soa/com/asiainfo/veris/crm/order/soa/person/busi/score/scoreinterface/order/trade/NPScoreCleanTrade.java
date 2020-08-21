
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.NPScoreCleanReqData;

/**
 * 新增“携出积分清零”业务//REQ201912220001_关于调整携转限制内容以及在查询携转条件和申请授权码后面追加风险短信 by mnegqx 20200221
 * @author mengqx
 * 
 */
public class NPScoreCleanTrade extends BaseTrade implements ITrade
{
	@Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
		NPScoreCleanReqData rd = (NPScoreCleanReqData) btd.getRD();
		
		MainTradeData mainTradeData = btd.getMainTradeData();
		String tradeTypeCode = mainTradeData.getTradeTypeCode();
		String tradeId = btd.getTradeId();
		String userId = rd.getUca().getUser().getUserId();

		System.out.println("NPScoreCleanTrade---tradeTypeCode:"+tradeTypeCode+";tradeId:"+tradeId+";userId:"+userId);
		AcctCall.cancelScoreValue(tradeTypeCode, tradeId, userId);
    }

}
