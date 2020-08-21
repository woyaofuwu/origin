package com.asiainfo.veris.crm.order.soa.person.busi.electricBusiness;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryInfoUtil;

public class ShoppingOrderSVC extends CSBizService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6900069722460340843L;
	static final Logger logger = Logger.getLogger(ShoppingOrderSVC.class);
	
	public IDataset queryListByTabRef(IData data) throws Exception{
		//ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
		return QueryInfoUtil.queryListByTid(data);
		
	}
	
	
	public IDataset submitFeedBack(IData data)throws Exception{
		
		ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
		return bean.submitFeedBack(data);
		
	}
	/**************************add by ouyk start**********************************************************/
	/**
	 * 查询销售订单
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryB2COrder(IData input)throws Exception{
		ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
		return bean.queryB2COrder(input,getPagination());
	}
	/**
	 * 查询销售子订单
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset querySubOrderInfo(IData input)throws Exception{
		ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
		return bean.querySubOrderInfo(input);
	}
	
	public IDataset queryOrderProductInfo(IData input)throws Exception{
		ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
		return bean.queryOrderProductInfo(input);
	}
	/**
	 * 查询退款订单
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryReturnOrderInfo(IData input)throws Exception{
		ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
		return bean.queryReturnOrderInfo(input,getPagination());
	}
	/**
	 * 查询退款子订单
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryReturnSubOrder(IData input)throws Exception{
		ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
		return bean.queryReturnSubOrder(input);
	}
	public IDataset queryProOrderByCtrmType(IData input)throws Exception{
		ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
		return bean.queryProOrderByCtrmType(input);
	}

	public IDataset updCtrmOrderProduct(IData input)throws Exception{
		ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
		return bean.updCtrmOrderProduct(input);
	}
	public IDataset updCtrmRefundSubById(IData input)throws Exception{
		ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
		return bean.updCtrmRefundSubById(input);
	}
	public IDataset returnOrder2IBoss(IData input) throws Exception{
			//调iBoss退款订单反馈接口
    	IBossCall.dealInvokeUrl("BIP3B511_T3000514_0_0", "IBOSS", input);
		return null;
	}
	/**************************add by ouyk end**********************************************************/
	
	public IDataset feedbackReturnOrder(IData input) throws Exception{
		ShoppingOrderBean bean = BeanManager.createBean(ShoppingOrderBean.class);
	    bean.feedbackReturnOrder(input);
	    return null;
	}
	
}
