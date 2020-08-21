package com.asiainfo.veris.crm.iorder.web.person.unpaidorderdeal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UnPaidOrderDealNew extends PersonBasePage
{
    public abstract void setCondition(IData condition);

    public abstract void setUnpaidTradeList(IDataset unpaidTrade);
    
	public void init(IRequestCycle cycle)throws Exception
	{
		IData condition = new DataMap();
		boolean privPayOther = false;
		condition.put("cond_PAY_OTHER_STAFF", privPayOther);
		condition.put("cond_STAFF_ID", this.getVisit().getStaffId());
		condition.put("cond_START_DATE", SysDateMgr.getAddHoursDate(SysDateMgr.getSysDate(), -72).substring(0,10));
		condition.put("cond_END_DATE", SysDateMgr.getSysDate());
		this.setCondition(condition);
	}
	
	public void queryUnpaidTrade(IRequestCycle cycle)throws Exception
	{
		IData data = this.getData();
		IData condition = new DataMap();
		condition.put("TRADE_STAFF_ID", data.getString("cond_STAFF_ID"));
		condition.put("START_DATE", data.getString("cond_START_DATE"));
		condition.put("END_DATE", data.getString("cond_END_DATE"));
		condition.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER"));
		condition.put("SUBSCRIBE_STATE", "X");
		
		condition.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));

        IDataset unPaidTradeInfo = CSViewCall.call(this, "SS.UnpaidOrderDealSVC.getUnpaidOrderInfo", condition);
        
        this.setUnpaidTradeList(unPaidTradeInfo);
	}
	
	public void cancelTrade(IRequestCycle cycle)throws Exception
	{
		IData pageData = this.getData();
		pageData.put(Route.ROUTE_EPARCHY_CODE, pageData.getString("TRADE_EPARCHY_CODE", getTradeEparchyCode()));
		
		IDataset datas = CSViewCall.call(this, "SS.UnpaidOrderDealSVC.cancelTrade", pageData);
		this.setAjax(datas);
	}
	
	public void payTrade(IRequestCycle cycle)throws Exception
	{
		IData data = this.getData();
		data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", getTradeEparchyCode()));
    	data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
    	IDataset infos = CSViewCall.call(this, "CS.PaymentSVC.payTrade", data);
    	IData info = new DataMap();
    	if (null != infos && infos.size() > 0) 
    	{
    		info = infos.getData(0);
    	}
        this.setAjax(info);
		
	}
	
	public void printTradeReceipt(IRequestCycle cycle)throws Exception
	{
		IData data = getData();
		String prtTradeTag = "0";
        if(!StringUtils.equals(data.getString("TRADE_TYPE_CODE",  ""), "")){
        	if (!data.containsKey("EPARCHY_CODE") || "".equals(data.getString("EPARCHY_CODE", "")))
            {
                data.put("EPARCHY_CODE", getTradeEparchyCode());
            }
        	IDataset tradeTypes = CSViewCall.call(this, "CS.TradeTypeInfoQrySVC.getTradeType", data);
            if (IDataUtil.isNotEmpty(tradeTypes))
            {
                prtTradeTag = tradeTypes.getData(0).getString("PRT_TRADEFF_TAG", "0");
            }
        }
        
        if(StringUtils.equals("1", prtTradeTag))
        {
        	data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            IDataset results = CSViewCall.call(this, "SS.UnpaidOrderDealSVC.printTradeReceipt", data);
            this.setAjax(results);
        }
	}
	
	public void printTradeNote(IRequestCycle cycle)throws Exception
	{
		IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.UnpaidOrderDealSVC.printTradeNote", data);
        this.setAjax(results);
	}
	
	public void createPaySubmit(IRequestCycle cycle)throws Exception
	{
		IData pageData = this.getData();
		pageData.put(Route.ROUTE_EPARCHY_CODE, pageData.getString("TRADE_EPARCHY_CODE", getTradeEparchyCode()));
		
		IDataset unPaidTradeFees = CSViewCall.call(this, "CS.PaymentSVC.getTradeFees", pageData);

		int fee =0;
		IDataset goodLists = new DatasetList();
		if(IDataUtil.isNotEmpty(unPaidTradeFees))
		{

			/*pageData.put("X_TRADE_FEESUB", unPaidTradeFees);
			IDataset dataset = unPaidTradeFees = CSViewCall.call(this, "CS.PaymentSVC.createPaymentOrder", pageData);
			this.setAjax(dataset);*/
			
			for(int i=0;i<unPaidTradeFees.size();i++)
			{
				IData data = unPaidTradeFees.getData(i);
				fee += data.getInt("FEE", 0);
				IData temp =new DataMap();
				temp.put("PEER_TRADE_ID", data.getString("TRADE_ID"));
				temp.put("GOODS_NAME",  data.getString("TRADE_NAME","业务费用"));
				temp.put("GOODS_PRICE", data.getString("FEE"));
				temp.put("GOODS_NUM", "1");
				temp.put("TOTAL_MONEY", data.getString("FEE"));
				goodLists.add(temp);
			}
			IData input = new DataMap();
			input.put("PEER_ORDER_ID", pageData.getString("ORDER_ID"));
			input.put("ORDER_FEE", fee+"");
			String sn = pageData.getString("SERIAL_NUMBER");
			if("undefined".equals(sn) || StringUtils.isBlank(sn))
			{
				sn = "无号码";
			}
			input.put("SERIAL_NUMBER", sn);
			input.put("ORDER_DESC","订单总费用");
			input.put("MERCHANT_ID", "1000");
			input.put("GOODS_LIST", goodLists.toString());

			IDataset dataset = CSViewCall.call(this, "payment.order.IPayAccessSV.createPayDetail", input);
			this.setAjax(dataset);
		}
	}
}
