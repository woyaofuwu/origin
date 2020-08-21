
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.score.scoreexchange.tradecancel;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;


public class ScoreExchangeTradeCancel extends CSBizBean
{

    /**
     * REQ201501040004 关于增加和包电子券撤销功能的需求
     * */
    public static void cancelHeTickets(IData mainTrade) throws Exception
    {
    	String tradeId = mainTrade.getString("TRADE_ID");
    	//获取积分子台帐
    	IDataset tradeScoreData = TradeScoreInfoQry.qryTradeScoreInfos(tradeId, "0");
    	if(IDataUtil.isNotEmpty(tradeScoreData))
    	{
    		//获取积分兑换规则
    		String ruleId = tradeScoreData.getData(0).getString("RULE_ID","");
    		if(StringUtils.isNotBlank(ruleId))
    		{
    			//判断规则是否是和包电子券规则，同ScoreExchangeTrade.java ticketsExchange方法内的paramDs4
    			IDataset comm1001Data = CommparaInfoQry.getCommpara("CSM", "1001", ruleId, CSBizBean.getTradeEparchyCode());
    			if (IDataUtil.isNotEmpty(comm1001Data))
    	        {
    				//获取other子台帐
    				IDataset tradeOtherData = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
    				if(IDataUtil.isNotEmpty(tradeOtherData))
    				{
    					String merId = tradeOtherData.getData(0).getString("RSRV_STR10");//商户号
    					String merDate = tradeOtherData.getData(0).getString("RSRV_STR11");//商户日期 YYYYMMDD
    					String actId = tradeOtherData.getData(0).getString("RSRV_STR2");//活动编号 建立电子卷活动的活动号
    					String mobileId = mainTrade.getString("SERIAL_NUMBER");//手机号
    					String amount = tradeOtherData.getData(0).getString("RSRV_STR5");//电子卷金额
    					String ttDate = tradeOtherData.getData(0).getString("RSRV_STR12");//发放日期 电子卷发放日期YYYYMMDD
    					String ttNum = tradeOtherData.getData(0).getString("RSRV_STR7");//电子券编号 
    					
    					IDataset resultDs = IBossCall.cancelScoreExchange(merId, merDate, actId, mobileId, amount, ttDate, ttNum);
        		    	if(IDataUtil.isNotEmpty(resultDs) && "0000".equals(resultDs.getData(0).getString("RETCODE"))){
        		    		//撤销成功
        		    	}else{
        		    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "融合支付平台："+resultDs.getData(0).getString("RETMSG"));
        		    	}
    				}
    	        }
    		}
    	}

    }


}
