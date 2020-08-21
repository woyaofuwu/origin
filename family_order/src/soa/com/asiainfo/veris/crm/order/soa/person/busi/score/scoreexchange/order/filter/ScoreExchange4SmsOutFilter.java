
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ExchangeRuleInfoQry;

/**
 * 生活平台积分兑换输出转换
 * 
 * @author huangsl
 */
public class ScoreExchange4SmsOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
    	String ruleId = input.getString("RULE_ID");
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	IDataset ruleData = ExchangeRuleInfoQry.queryByRuleId(ruleId, CSBizBean.getTradeEparchyCode());
    	String rsrvStr1 = ruleData.getData(0).getString("RSRV_STR1","1");//获取下券数量、默认1
    	IDataset resultDs = IBossCall.scoreExchange4Plat(serialNumber,ruleId,rsrvStr1,btd.getTradeId());
    	IData resultInfo = new DataMap();
    	if(IDataUtil.isNotEmpty(resultDs) && "0000".equals(resultDs.getData(0).getString("X_RSPCODE"))){
    		resultInfo.put("TRADE_ID", btd.getTradeId());
    		resultInfo.put("X_RESULTCODE", "0");
    		resultInfo.put("X_RESULTINFO", "兑换成功！");
    	}else{
    		CSAppException.apperr(ScoreException.CRM_SCORE_6);
    	}

    	
        return resultInfo;
    }

}
