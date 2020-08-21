package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;

/**
 * 无线固话开户当月只能办理一次立即生效的产品变更，由于无线固话的产品只能办理立即生效的产品变更。
 * 所以本规则就是开户当月只能办理一次产品变更
 * @author tz
 *
 */
public class OpenMonthOnlyChangeLimitRule extends BreBase implements IBREScript
{
	
	private static final long serialVersionUID = 2953760713086327586L;
	private static Logger logger = Logger.getLogger(OpenMonthOnlyChangeLimitRule.class);
	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
	        String tradetypecode = databus.getString("TRADE_TYPE_CODE","");
	        if (logger.isDebugEnabled())
            	logger.debug(" >>>>>>>>>进入 OpenMonthOnlyChangeLimitRule(1)>>>>>>>>> tradetypecode:"+tradetypecode);
	        if ("110".equals(tradetypecode) || "3803".equals(tradetypecode) )
	        {
	        	String userId = databus.getString("USER_ID");
	        	UcaData uca = UcaDataFactory.getUcaByUserId(userId);
	        	String brandCode = uca.getBrandCode();
	        	String openMonth = uca.getUser().getOpenDate().substring(0,7);
	        	String currentTime = SysDateMgr.getSysDate();
	        	String currentMonth = currentTime.substring(0,7);
	        	if(!currentMonth.equals(openMonth)){
	        		return false;
	        	}
	        	if(!"TDYD".equals(brandCode)){
	        		return false;
	        	}
	        	String acceptDate = SysDateMgr.getAddMonthsLastDayNoEnv(-1,SysDateMgr.getSysTime());
	        	String month = SysDateMgr.getCurMonth();
	        	IDataset tradeData110 = TradeHistoryInfoQry.queryHisTradeAfterAcceptDateAndMonth(userId,"110",acceptDate,month);
	        	IDataset tradeData3803 = TradeHistoryInfoQry.queryHisTradeAfterAcceptDateAndMonth(userId,"3803",acceptDate,month);
	        	if(IDataUtil.isNotEmpty(tradeData110)||IDataUtil.isNotEmpty(tradeData3803)){
	        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20190704", "当月开户的用户只能进行立即生效的产品变更1次");
	        	}
	        }
	        return false;
	    }

}
