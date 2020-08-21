package com.asiainfo.veris.crm.iorder.web.merch.merchShoppingCart;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MerchShoppingCart extends PersonBasePage {

	/**
	 * 加载用户的购物车订单
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void loadShoppingCart(IRequestCycle cycle) throws Exception {
		IData data = this.getData();
		data.put("TRADE_STAFF_ID", this.getVisit().getStaffId());
		IDataset resultSet = CSViewCall.call(this, "SS.MerchShoppingCartSVC.loadShoppingCartInfo", data);
		this.setAjax(resultSet.getData(0));
	}

	/**
	 * 根据DETAIL_ORDER_ID删除购物车明细订单
	 * 
	 * @param requestCycle
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void deleteOrder(IRequestCycle requestCycle) throws Exception {
		IData data = this.getData();
		IDataset resultSet = CSViewCall.call(this, "SS.MerchShoppingCartSVC.deleteOrder", data);
		setAjax(resultSet.getData(0));
	}
	
	/**
	 * 购物车提交前校验
	 * @param requestCycle
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void submitShoppingCartBeforeCheck(IRequestCycle requestCycle) throws Exception {
		IData data = this.getData();
		IDataset result = CSViewCall.call(this, "SS.MerchShoppingCartSVC.submitShoppingCartBeforeCheck", data);
		this.setAjax(IDataUtil.isNotEmpty(result) ? result.getData(0) : null);
	}
	
	/**
	 * 购物车订单提交
	 * 
	 * @param requestCycle
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void onTradeSubmit(IRequestCycle requestCycle) throws Exception {
		IData data = this.getData();
		IDataset result = CSViewCall.call(this, "SS.MerchShoppingCartSVC.tradeReg", data);
		this.setAjax(result);
	}
	
	public void checkBoxAction(IRequestCycle requestCycle) throws Exception {
		IData data = this.getData();
		IDataset result = CSViewCall.call(this, "SS.MerchShoppingCartSVC.checkBoxAction", data);
		this.setAjax(result);
	}
}
