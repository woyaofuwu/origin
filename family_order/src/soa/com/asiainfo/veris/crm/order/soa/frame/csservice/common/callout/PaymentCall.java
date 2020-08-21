package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class PaymentCall
{
	
	/**
	 * 支付结果查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPayResult(IData param) throws Exception
	{
		IData input = new DataMap();  
		input.put("PEER_ORDER_ID",param.getString("PEER_ORDER_ID"));
		input.put("ORDER_ID", param.getString("ORDER_ID"));
		IDataset dataset = call("payment.order.IPayAccessSV.queryPayResult", input);

		return dataset;
		
	}
	/**
	 * 按单笔子订单退款
	 * 无返回值,采用工单形式,不报错代表退款成功
	 * @param tradeId
	 * @throws Exception
	 */
	public static void doTradeRefund(String tradeId) throws Exception
	{
		IData input = new DataMap();  
		input.put("OLD_PEER_TRADE_ID",tradeId);
		//input.put("REFUND_AMOUNT", param.getString("FEE"));
		call("payment.order.IUnifiedRefundSV.doTradeRefund", input);
	}
	/**
	 * 按子订单流水号退款
	 * 不传按子订单金额退款,不能大于可退款金额,一笔子订单只能退一次
	 * 无返回值,采用工单形式,不报错代表退款成功
	 * @param tradeId
	 * @param fee
	 * @throws Exception
	 */
	public static void doTradeRefund(String tradeId,String fee) throws Exception
	{
		IData input = new DataMap();  
		input.put("OLD_PEER_TRADE_ID",tradeId);
		input.put("REFUND_AMOUNT", fee);
		call("payment.order.IUnifiedRefundSV.doTradeRefund", input);
	}
	/**
	 * 
	 * @param tradeId
	 * @param fee
	 * @throws Exception
	 */
	public static void doOrderRefund(String orderId,String fee) throws Exception
	{
		IData input = new DataMap();  
		input.put("OLD_PEER_ORDER_ID",orderId);//外部流水号
		//input.put("OLD_ORDER_ID",tradeId);//支付中心流水
		input.put("REFUND_AMOUNT", fee);
		call("payment.order.IUnifiedRefundSV.doOrderRefund", input);
	}
	/**
	 * 支付下单
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset createPaymentOrder(IData param) throws Exception
	{

		IData input = new DataMap();
		
		int allFee =0;
		IDataset goodLists = new DatasetList();
		IDataset feeList = new DatasetList(param.getString("X_TRADE_FEESUB","[]"));
		for(int i=0;i<feeList.size();i++)
		{
			IData data = feeList.getData(i);
			int fee = data.getInt("FEE", 0);
			if(fee ==0)
			{
				continue;
			}
			allFee +=fee;
			IData temp =new DataMap();
			temp.put("PEER_TRADE_ID", data.getString("TRADE_ID"));
			temp.put("GOODS_NAME", StaticUtil.getStaticValue("FEE_MODE", data.getString("FEE_MODE")));
			temp.put("GOODS_PRICE", data.getString("FEE"));
			temp.put("GOODS_NUM", "1");
			temp.put("TOTAL_MONEY", data.getString("FEE"));
			goodLists.add(temp);
		}

		input.put("PEER_ORDER_ID", param.getString("ORDER_ID"));
		input.put("ORDER_FEE", allFee+"");
		input.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
		input.put("ORDER_DESC","订单总费用");
		input.put("MERCHANT_ID", "1000");
		input.put("GOODS_LIST", goodLists.toString());

		IDataset dataset = call("payment.order.IPayAccessSV.createPayDetail", input);

		return dataset;
	}

	public final static IDataset call(String svcName, IData input)
			throws Exception
	{
		return CSAppCall.callPayment(svcName, input, true).getData();
	}

}
