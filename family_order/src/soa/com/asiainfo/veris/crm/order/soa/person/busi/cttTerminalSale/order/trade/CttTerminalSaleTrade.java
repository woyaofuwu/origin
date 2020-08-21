package com.asiainfo.veris.crm.order.soa.person.busi.cttTerminalSale.order.trade;

import java.math.BigDecimal;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeviceTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;

public class CttTerminalSaleTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
		
		CttTerminalSaleReqData reqData = (CttTerminalSaleReqData) bd.getRD();
		
		//单价
		double terminalPrice=Double.parseDouble(reqData.getTerminalPrice());
		BigDecimal priceD = new BigDecimal(terminalPrice*100);
		String terminalPriceStr=priceD.toString();
		
		//总价
		double totalTerminalPrice=Double.parseDouble(reqData.getTerminalTotalPrice());
		BigDecimal totalPriceD = new BigDecimal(totalTerminalPrice*100);
		String terminalTotalPriceStr=totalPriceD.toString();
		
		DeviceTradeData deviceTradeData = new DeviceTradeData();
		deviceTradeData.setFeeTypeCode(reqData.getFeeTypeCode());
        deviceTradeData.setDeviceTypeCode(reqData.getTerminalType());
        deviceTradeData.setDeviceNoS(reqData.getTerminalCode());
        deviceTradeData.setDeviceNoE(reqData.getTerminalCode());
        deviceTradeData.setDeviceNum(reqData.getTerminalNumber());
        deviceTradeData.setDevicePrice(terminalPriceStr);
        deviceTradeData.setSalePrice(terminalTotalPriceStr);
        bd.add(bd.getRD().getUca().getUser().getSerialNumber(), deviceTradeData);
        
        
        //设置
        MainTradeData mainTrade=bd.getMainTradeData();
        //下面4个字段值用于打印到发票上，价格要计算到元
        mainTrade.setRsrvStr1(reqData.getTerminalTypeName());
        mainTrade.setRsrvStr2(reqData.getTerminalCodeName());
        mainTrade.setRsrvStr3(reqData.getTerminalNumber());
        mainTrade.setRsrvStr4(reqData.getTerminalPrice());
	}
}
