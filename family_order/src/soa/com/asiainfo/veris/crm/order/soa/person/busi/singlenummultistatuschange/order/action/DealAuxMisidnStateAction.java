package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.action;

import java.util.List;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange.order.requestdata.SingleNumMultiDeviceStatusChangeReqData;

/**
 * 业务暂停、恢复处理副设备停开机状态
 *
 */
public class DealAuxMisidnStateAction implements ITradeAction
 {

	public void executeAction(BusiTradeData btd) throws Exception {
		
		List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
		
		if(relationTradeDatas == null || relationTradeDatas.isEmpty())
		{
			return ;
		}
		
		SingleNumMultiDeviceStatusChangeReqData requestData = ((SingleNumMultiDeviceStatusChangeReqData)btd.getRD());
		String oprFlag = requestData.getOprFlag();// 1:前台暂停 2：前台恢复 3：欠费暂停 4：缴费恢复  5.其他停机：主动停机 6.其他开机：主动开机
		
		if(null!=oprFlag && !"".equals(oprFlag))
		{
			if("3".equals(oprFlag) || "4".equals(oprFlag))
        	{
        		//如果为 欠费停机/缴费开机 信控接口不做副号码停开机处理 
        		return ;
        	}
			else if("1".equals(oprFlag) || "5".equals(oprFlag))
        	{
        		//oprFlag=1:业务暂停（前台） 将需要暂停的副号码停机
        		stopAuxOMData(btd);
        	}
			else if("2".equals(oprFlag) || "6".equals(oprFlag))
        	{
        		//oprFlag=2:业务恢复 （前台）
        		recoverAuxOMData(btd);
        	}
		}
		
	}

	private void recoverAuxOMData(BusiTradeData btd) throws Exception {
		List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
		for (RelationTradeData relationTradeData : relationTradeDatas) {

			IData data = new DataMap();
			data.put("SERIAL_NUMBER", relationTradeData.getSerialNumberB());
			data.put("TRADE_TYPE_CODE", "133");
			data.put("ORDER_TYPE_CODE", "133");
			CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", data);
		}
		
	}

	private void stopAuxOMData(BusiTradeData btd) throws Exception 
	{
		List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
		for (RelationTradeData relationTradeData : relationTradeDatas) 
		{
			IData data = new DataMap();
			data.put("SERIAL_NUMBER", relationTradeData.getSerialNumberB());
			data.put("TRADE_TYPE_CODE", "131");
			data.put("ORDER_TYPE_CODE", "131");
			CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", data);
		}
	}
}
