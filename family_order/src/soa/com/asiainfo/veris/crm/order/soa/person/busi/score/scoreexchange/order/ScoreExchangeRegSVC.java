
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.requestdata.ScoreExchangeRequestData;

@SuppressWarnings("serial")
public class ScoreExchangeRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return "330";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return "330";
    }
    
    @SuppressWarnings("unchecked")
	public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
    	ScoreExchangeRequestData reqData = (ScoreExchangeRequestData) btd.getRD();
    	IData out = reqData.getPageRequestData();
    	if( IDataUtil.isNotEmpty(out) ){
    		String strEmz = out.getString("EXCHANGE_EMZ");
    		if( StringUtils.isNotBlank(strEmz) ) {
    			IData data = new DataMap(strEmz);
    			IData param = new DataMap();
        		param.put("TRADE_TYPE_CODE", "3301");
                param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                param.put("CARD_NOS", data.getString("CARD_NOS"));
                param.put("EXCHANGE_DATA", data.getString("EXCHANGE_DATA"));
                param.put("REMARK", data.getString("REMARK"));
                param.put("OBJECT_SERIAL_NUMBER", data.getString("OBJECT_SERIAL_NUMBER"));
                param.put("HH_CARD_ID", data.getString("HH_CARD_ID"));
                param.put("HH_CARD_NAME", data.getString("HH_CARD_NAME"));
                param.put("CHECK_MODE", data.getString("CHECK_MODE"));
                CSAppCall.call("SS.ScoreExchangeRegSVC.tradeReg", param);
    		}
    	}
    }

}
