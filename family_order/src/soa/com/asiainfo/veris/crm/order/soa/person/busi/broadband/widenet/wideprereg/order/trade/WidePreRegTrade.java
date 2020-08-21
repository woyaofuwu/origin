package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg.order.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg.order.requestdata.WidePreRegRequestData;

public class WidePreRegTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		WidePreRegRequestData rd = (WidePreRegRequestData)btd.getRD();
		this.updateMainTradeData(btd,rd);
	}
	
	private void updateMainTradeData(BusiTradeData btd, WidePreRegRequestData reqData) throws Exception
    {
		List mainList = btd.get(TradeTableEnum.TRADE_MAIN.getValue());
	    MainTradeData mainTD = (MainTradeData) mainList.get(0);
//		String eparchyCode = this.getTradeEparchyCode();
		
		mainTD.setSerialNumberB(reqData.getContact_sn());//联系人电话
		mainTD.setRsrvStr9(reqData.getCust_name());//联系人姓名
		mainTD.setRsrvStr8(reqData.getArea_code());//地区
		mainTD.setRsrvStr7(reqData.getReg_status());//登记状态
		mainTD.setRsrvStr6(reqData.getPre_cause());//预装原因
		mainTD.setRsrvStr4(reqData.getWbbw());//申请宽带带宽
		mainTD.setRsrvStr2(reqData.getSet_addr());//装机详细地址
		mainTD.setRsrvStr1(reqData.getHome_addr());//装机四级地址、用于汇总预警
		mainTD.setRsrvStr10(reqData.getAddr_code());//四级地址编码，每一级地址之间用 "," 分隔
		mainTD.setRemark(reqData.getRemark());
    }
	

}
