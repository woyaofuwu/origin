package com.asiainfo.veris.crm.order.web.person.electricBusiness;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;


public abstract class QueryAndDealB2COrder extends PersonQueryPage{
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData data);
	public abstract void setCount(long count);
	public abstract void setCondition(IData condition);
	public abstract void setProductInfo(IDataset infos); 
	
	/**
	 * 查询销售订单信息
	 * @param cycle
	 * @throws Exception
	 */
	public void queryInfo(IRequestCycle cycle) throws Exception{
		IData data = this.getData();
		IDataOutput results = CSViewCall.callPage(this, "SS.ShoppingOrderSVC.queryB2COrder", data,getPagination("qryInfoNav"));
        setInfos(results.getData());
        setCount(results.getDataCount());
	}
	/**
	 * 查询销售子订单信息
	 * @param cycle
	 * @throws Exception
	 */
	public void querySubOrderInfo(IRequestCycle cycle) throws Exception {
		IData data = this.getData();
		IDataset results = CSViewCall.call(this, "SS.ShoppingOrderSVC.querySubOrderInfo", data);
        setInfos(results);
	}
	public void queryOrderProductInfo(IRequestCycle cycle)throws Exception {
		IData data = this.getData();
		IDataset results = CSViewCall.call(this, "SS.ShoppingOrderSVC.queryOrderProductInfo", data);
		setProductInfo(results);
	}
	/**
	 * 查询退款订单信息
	 * @param cycle
	 * @throws Exception
	 */
	public void queryReturnOrderInfo(IRequestCycle cycle) throws Exception {
		IData data = this.getData("cond");
		IDataOutput results = CSViewCall.callPage(this, "SS.ShoppingOrderSVC.queryReturnOrderInfo", data,getPagination("qryInfoNav"));
        setInfos(results.getData());
        setCount(results.getDataCount());
	}
	
	
	public void feedbackReturnOrder(IRequestCycle cycle) throws Exception {
		
		
		IData data = this.getData();
		
		String[] allIds = data.getString("inforecvid").split(",");
		if (allIds == null || allIds.length <= 0) {
			return ;
		}
		
		String refundId = allIds[0].split("_")[0];
		
		CSViewCall.call(this, "SS.ShoppingOrderSVC.feedbackReturnOrder", data);
		
		//刷新当前页面
		IData newData = new DataMap();
		newData.put("REFUND_ID", refundId);
		newData.put("CHANNEL_ID", data.getString("CHANNEL_ID"));
		newData.put("REFUND_TYPE", data.getString("REFUND_TYPE"));
		IDataset results = CSViewCall.call(this, "SS.ShoppingOrderSVC.queryReturnSubOrder", newData);
		setInfos(results);
		IData condition = new DataMap();
		condition.put("CHANNEL_ID", newData.getString("CHANNEL_ID"));
		condition.put("REFUND_TYPE", newData.getString("REFUND_TYPE"));
		setCondition(condition);
		
	}
	
	public void queryReturnSubOrder(IRequestCycle cycle) throws Exception {
		IData data = this.getData();
		IDataset results = CSViewCall.call(this, "SS.ShoppingOrderSVC.queryReturnSubOrder", data);
		setInfos(results);
		IData param = new DataMap();
		param.put("CHANNEL_ID", data.getString("CHANNEL_ID"));
		param.put("REFUND_TYPE", data.getString("REFUND_TYPE"));
		setCondition(param);
	}
}
