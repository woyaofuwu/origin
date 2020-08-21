package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.noPhoneRenewTopSetBox.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.noPhoneRenewTopSetBox.order.requestdata.NoPhoneRenewTopSetBoxReqData;

public class NoPhoneRenewTopSetBoxRegSVC extends OrderService{

	public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", "4908");
    }

    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "4908");
    }

	@Override
	public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception {
;		NoPhoneRenewTopSetBoxReqData req = (NoPhoneRenewTopSetBoxReqData) btd.getRD();
		
		//魔百和欠费停机时需要进行开机处理
		if(req.getTopSetBoxStateTag().equals("2"))
		{
	        //通过后台获取 147号码（魔百和绑定在147手机号码上）
	        input.put("SERIAL_NUMBER", req.getSerialNumber());  //手机号码
	        input.put("USER_ID", req.getUserId());
	        input.put("WIDE_USER_ID", req.getWideUserId());
	        input.put("WIDE_SERIAL_NUMBER", req.getWideSerialNumber());
			
	        input.put("INTERNET_TV_SOURCE", "TOPSETBOX_STOP");		//用来标记是做报停
	        input.put("START_ACTION", "0");		//报开标志：0：主动报开，1：续费报开
	        input.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
	        IDataset dataset = CSAppCall.call("SS.NoPhoneStartTopSetBoxRegSVC.tradeReg", input);
		}
	}
}
