package com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class MerchShoppingCartSVC extends CSBizService {
	private static final long serialVersionUID = 7473216621296735024L;

	/**
	 * 加载购物车明细，目前只展示到order级。前台的操作也只是到order级。
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public IData loadShoppingCartInfo(IData param) throws Exception {
		MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
		return shoppingCartBean.getShoppingSetByStaffUserId(param);
	}
	
	/**
	 * 根据DETAIL_ORDER_ID删除购物车明细订单。
	 * 
	 * @param param
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public IData deleteOrder(IData param) throws Exception {
		String shoppingCartId = param.getString("SHOPPING_CART_ID");
		String detailOrderId = param.getString("DETAIL_ORDER_ID");
		String serialNumber = param.getString("SERIAL_NUMBER");
		MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
		IDataset ruleInfos = shoppingCartBean.checkBoxAction(param);
		if(IDataUtil.isNotEmpty(ruleInfos))
		{
			String errorInfo = ruleInfos.getData(0).getString("ERROR_INFO");
			if(StringUtils.isNotBlank(errorInfo))
			{
				CSAppException.appError("-1", errorInfo);
			}
		}
		
		//营销活动检验
	    shoppingCartBean.checkSaleBoxAction(param);
		
		shoppingCartBean.deleteOrder(detailOrderId, shoppingCartId);
		
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", serialNumber);
		inparam.put("USER_ID", UcaInfoQry.qryUserInfoBySn(serialNumber).getString("USER_ID"));
		inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
		IData cartInfos = loadShoppingCartInfo(inparam);
		return cartInfos;
	}
	
	/**
	 * 购物车结算后修改购物车的数据
	 * 
	 * @param params
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void dealCartBySubmit(IData params) throws Exception {
		String shoppingCartId = params.getString("SHOPPING_CART_ID");
		String orderId = params.getString("ORDER_ID");
		String detailOrderIds = params.getString("DETAIL_ORDER_IDS");
		MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
		shoppingCartBean.dealCartBySubmit(shoppingCartId, orderId, detailOrderIds);
	}
	
	/**
	 * 清理购物车订单
	 * 
	 * @param param
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public void clearShoppingCartOrder(IData param) throws Exception {
		MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
		shoppingCartBean.clearShoppingCartOrder();
	}
	
	/**
	 * 获取购物车里面的所有变更的元素
	 * 
	 * @param param
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public IDataset getShoppingCartAllElements(IData param) throws Exception {
		MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
		return shoppingCartBean.getShoppingCartAllElements(param);
	}
	
	/**
	 * 提交前校验提示元素的修改时间
	 * 
	 * @param param
	 * @throws Exception
	 * @author liujian7
	 * @date
	 */
	public IDataset submitShoppingCartBeforeCheck(IData param) throws Exception {
		MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
		return shoppingCartBean.submitShoppingCartBeforeCheck(param);
	}
	
	public IDataset checkBoxAction(IData param) throws Exception
	{
		MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
	      //营销活动检验
        shoppingCartBean.checkSaleBoxAction(param);
		return shoppingCartBean.checkBoxAction(param);
	}
}
