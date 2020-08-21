package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.payment;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PaymentCall;

public class PaymentSVC extends CSBizService
{
	private static final long serialVersionUID = 8234696776703775166L;
	
	/**
	 * 整笔order支付
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData payOrder(IData param)throws Exception
	{
		PaymentBean bean = BeanManager.createBean(PaymentBean.class);
		return bean.payOrder(param);
	}
	/**
	 * 单笔trade支付
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData payTrade(IData param)throws Exception
	{
		PaymentBean bean = BeanManager.createBean(PaymentBean.class);
		return bean.payTrade(param);
	}
	
	public IDataset getTradeFees(IData param) throws Exception
	{
		PaymentBean bean = BeanManager.createBean(PaymentBean.class);
		return bean.getTradeFees(param);
	}
	
	public IDataset getOrderFees(IData param) throws Exception
	{
		PaymentBean bean = BeanManager.createBean(PaymentBean.class);
		return bean.getOrderFees(param);
	}
	
	public IDataset createPaymentOrder(IData param) throws Exception
	{
		return PaymentCall.createPaymentOrder(param);
	}
	
	public IDataset getTradePayMoney(IData param) throws Exception
	{
		PaymentBean bean = BeanManager.createBean(PaymentBean.class);
		return bean.getTradePayMoney(param);
	}
	
	public IDataset getLoginMessage(IData param) throws Exception
	{
		PaymentBean bean = BeanManager.createBean(PaymentBean.class);
		return bean.getLoginMessage(param);
	}

	public IDataset getLoginMessageNew(IData param) throws Exception
	{
		PaymentBean bean = BeanManager.createBean(PaymentBean.class);
		return bean.getLoginMessageNew(param);
	}
}
