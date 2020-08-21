package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class SchoolActiveAction  implements ITradeAction {
	
	@SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SaleActiveReqData req = (SaleActiveReqData) btd.getRD();
        String tradeTypeCode =  btd.getMainTradeData().getTradeTypeCode();
        String schoolName = req.getSchoolName();
        String studentName = req.getStudentName();
        
        if("240".endsWith(tradeTypeCode)&&StringUtils.isNotEmpty(schoolName)&&StringUtils.isNotEmpty(studentName))
    	{
        	List<SaleActiveTradeData> saleActiveTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
        	for (int i = 0, size = saleActiveTradeDatas.size(); i < size; i++)
            {
                SaleActiveTradeData saleActiveTradeData = saleActiveTradeDatas.get(i);
                saleActiveTradeData.setRsrvStr19(schoolName);
                saleActiveTradeData.setRsrvStr20(studentName);
            }
    	}
    }

}
