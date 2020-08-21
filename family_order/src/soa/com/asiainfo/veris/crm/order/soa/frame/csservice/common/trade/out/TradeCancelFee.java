package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out;

import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PaymentCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;

public final class TradeCancelFee
{
	private final static Logger logger = Logger.getLogger(TradeCancelFee.class);

	public static void cancelRecvFee(IData mainTrade, IData pgData) throws Exception
	{
		boolean isFound = false;
		String cancelType = pgData.getString("CANCEL_TYPE");

		String tradeId = mainTrade.getString("TRADE_ID");
		String tradetypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String inModeCode = "";//mainTrade.getString("IN_MODE_CODE","");
		IDataset tradeHisInfo = TradeHistoryInfoQry.query_TF_B_TRADE_ByTradeId(tradeId);
		if(tradeHisInfo!=null&&tradeHisInfo.size()>0){
			inModeCode=tradeHisInfo.getData(0).getString("IN_MODE_CODE");
		}

		IDataset tradefeeSubs = TradefeeSubInfoQry.getTradefeeSubByTradeMode(tradeId, "2");

		if (IDataUtil.isNotEmpty(tradefeeSubs))
		{
			isFound = true;
		}

		if (!isFound)
		{
			// BofConst.OTHERFEE_SAME_TRANS
			IDataset otherFeeList = TradeOtherFeeInfoQry.getTradeOtherFeeByTradeId(tradeId);
			if (IDataUtil.isNotEmpty(otherFeeList))
			{
				for (int i = 0, size = otherFeeList.size(); i < size; i++)
				{
					IData otherFee = otherFeeList.getData(i);
					String operType = otherFee.getString("OPER_TYPE");
					String operFee = otherFee.getString("OPER_FEE","");
					if ((BofConst.OTHERFEE_SAME_TRANS.equals(operType) || BofConst.OTHERFEE_ROAM_TRANS.equals(operType) || BofConst.OTHERFEE_CAMPUS_TRANS.equals(operType))
							&& !"0".equals(operFee))
						isFound = true;
					break;
				}

			}
		}

		if (isFound)
		{
			// 如果存在记录，则需要调用缴费返销流程TAM_WOM_AMTOCSCANCELFEE
			if (StringUtils.isNotBlank(cancelType) && "1".equals(cancelType))
			{
				// 如果传了CANCEL_TYPE=1，则调强制费用返销，可返销成负存折
				AcctCall.recvFeeForceCancel(tradeId);
			} else
			{
				AcctCall.recvFeeCancel(tradeId);
			}
		}

		IDataset payFees = TradefeeSubInfoQry.getTradeFeeSubByTradeId(tradeId);
		if (IDataUtil.isNotEmpty(payFees))
		{
			/**
			 * 问题排查 <br/>
			 * 过滤割接前的数据,对于2016-06-04 23：59：59 之前的数据不处理
			 * 
			 * @author zhuoyingzhi
			 * @date 20170616
			 */
			// 获取受理时间(2017/2/8 14:34:07)
			String acceptDate = payFees.getData(0).getString("UPDATE_TIME", "2050-6-8 14:34:07");
			if (!"".equals(acceptDate) && acceptDate != null)
			{
				if (isG3StartDate(acceptDate, tradetypeCode,inModeCode))
				{
				    //如果是吉祥号码营销活动终止的返销则不调支付中心
					IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(tradeId);
					if(tradeSaleActive!=null&&tradeSaleActive.size()>0){
						String productId=tradeSaleActive.getData(0).getString("PRODUCT_ID");
                        System.out.println("----TradeCancelFee----是吉祥号码营销活动终止的返销则不调支付中心-------productId="+productId+";tradetypeCode="+tradetypeCode);
                        if ("69900703".equals(productId) && "237".equals(tradetypeCode)){
                            return;
                        }
					}

					//  信用购机活动中配置营业款的活动终止不需要返销支付中心记录
					if(tradeSaleActive!=null&&tradeSaleActive.size()>0) {
						String productId = tradeSaleActive.getData(0).getString("PRODUCT_ID");
						String packageId = tradeSaleActive.getData(0).getString("PACKAGE_ID");
						if (isMoreTerminalSaleActive(productId, packageId, tradetypeCode)) {
							return;
						}
					}
					// 返销支付中心记录
					PaymentCall.doTradeRefund(tradeId);
				}
			}
			/************** end *********************/
		}

	}

	private static boolean isMoreTerminalSaleActive(String productId, String packageId, String tradetypeCode) throws Exception{
		boolean flag = false;
		IDataset packageIdInfo = CommparaInfoQry.getCommparaByCode4("CSM","3119",productId,packageId,"0898");
		if (CollectionUtils.isNotEmpty(packageIdInfo)){
			String paraCode10 = packageIdInfo.getData(0).getString("PARA_CODE10","");
			String paraCode8 = packageIdInfo.getData(0).getString("PARA_CODE8","");

			if (!paraCode10.equals("") && "Y".equals(paraCode8) ){// 该活动属于有营业款的 信用购机活动，不需要返销支付中心
				flag = true;
			}else {// 无预存款的活动
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 判断业务受理日期是否是第三代上线后的日期 <br/>
	 * true 表示受理日期是第三代上线后的业务受理日期 false 表示受理日期是第三代上线之前的受理日期
	 * 
	 * @param accept
	 *            格式：2017-06-03 14:34:07
	 * @return
	 * @throws Exception
	 * @author zhuoyingzhi
	 * @date 20170616
	 */
	public static boolean isG3StartDate(String accept, String tradetypecode,String inModeCode) throws Exception
	{
		try
		{
			// 第三代测试上线时间(2017-06-04 23:59:59)
			IDataset dataset = CommparaInfoQry.getCommpara("CSM", "1706", "20170604", "ZZZZ");
			if (IDataUtil.isNotEmpty(dataset))
			{

				String[] condValues = dataset.getData(0).getString("PARA_CODE2", "").split("\\|");
				boolean falg = false;
				for (String val : condValues)
				{
					if (val.equals(tradetypecode))
					{
						falg = true;
						break;
					}
				}
				
				//加上inModeCode判断
				if(!falg){
					String[] condValues1 = dataset.getData(0).getString("PARA_CODE3", "").split("\\|");
					for (String val : condValues1)
					{
						if (val.equals(inModeCode))
						{
							falg = true;
							break;
						}
					}
				}

				if (falg)
				{
					return false;
				}

				// 第三代上线时间
				String g3Time = dataset.getData(0).getString("PARA_CODE1", "");
				// 受理时间

				return accept.compareTo(g3Time) > 0 ? true : false;
			} else
			{
				return true;
			}
		} catch (Exception e)
		{
			throw e;
		}
	}
}
