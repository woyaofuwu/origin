
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.PayMoneyData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayMoneyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShoppingCartDetailTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShoppingCartTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shopping.MerchShoppingCartQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.PrintAction;

public abstract class BaseTrade extends CSBizBean implements ITrade
{
	private void createBusiMainTradeData(BusiTradeData bd) throws Exception
	{
		BaseReqData brd = bd.getRD();

		MainTradeData mainTradeData = new MainTradeData();

		mainTradeData.setTradeTypeCode(brd.getTradeType().getTradeTypeCode());
		mainTradeData.setPriority(brd.getTradeType().getPriority());
		if (StringUtils.isNotBlank(brd.getUca().getUserId()))
		{
			mainTradeData.setUserId(brd.getUca().getUserId());
		}
		else
		{
			mainTradeData.setUserId("0");
		}

		mainTradeData.setCustId(brd.getUca().getCustId());
		mainTradeData.setAcctId(brd.getUca().getAcctId());
		mainTradeData.setCustName(brd.getUca().getCustomer().getCustName());

		mainTradeData.setExecTime(bd.getRD().getAcceptTime());
		mainTradeData.setSerialNumber(brd.getUca().getSerialNumber());
		mainTradeData.setSubscribeState("0");

		mainTradeData.setSubscribeType(BofConst.SUBSCRIBE_TYPE_NORMAL_NOW);
		if (StringUtils.isNotBlank(bd.getRD().getBatchId()))// 如果是批量，则subscribe_state改成100
		{
			if ("9".equals(brd.getBatchDealType()))
			{
				mainTradeData.setSubscribeType(BofConst.SUBSCRIBE_TYPE_BATCH_PF_FILE);
			}
			else
			{
				mainTradeData.setSubscribeType(BofConst.SUBSCRIBE_TYPE_BATCH_NOW);
			}
		}
		mainTradeData.setNextDealTag("0");
		mainTradeData.setInModeCode(CSBizBean.getVisit().getInModeCode());
		mainTradeData.setProcessTagSet(BofConst.PROCESS_TAG_SET);
		mainTradeData.setOlcomTag("0");// 默认不发
		mainTradeData.setCancelTag(BofConst.CANCEL_TAG_NO);
		mainTradeData.setNetTypeCode(brd.getUca().getUser().getNetTypeCode());
		mainTradeData.setOperFee("0");
		mainTradeData.setForegift("0");
		mainTradeData.setAdvancePay("0");
		mainTradeData.setBatchId(bd.getRD().getBatchId());
		mainTradeData.setOrderId(bd.getRD().getOrderId());

		// ======购物车
		String submitType = DataBusManager.getDataBus().getSubmitType();
		if (BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(submitType))
		{
			mainTradeData.setSubscribeType(BofConst.SUBSCRIBE_TYPE_SHOPPING_CART);
//			mainTradeData.setCancelTag(BofConst.CANCEL_TAG_CANCELED);
		}
		// ======购物车

		mainTradeData.setProductId(brd.getUca().getProductId());
		mainTradeData.setBrandCode(brd.getUca().getBrandCode());
		mainTradeData.setEparchyCode(brd.getUca().getUserEparchyCode());
		mainTradeData.setCityCode(brd.getUca().getUser().getCityCode());
		mainTradeData.setRemark(brd.getRemark());
		mainTradeData.setTermIp(CSBizBean.getVisit().getRemoteAddr());

		String pfWait = TradeCtrl.getCtrlString(brd.getTradeType().getTradeTypeCode(), TradeCtrl.CTRL_TYPE.PF_WAIT, "0");
		mainTradeData.setPfWait(pfWait);

		bd.add(brd.getUca().getSerialNumber(), mainTradeData);
	}

	/**
	 * 子类需要覆盖此方法创建具体的业务台帐
	 * 
	 * @param pd
	 * @param brd
	 * @return
	 * @throws Exception
	 */
	public abstract void createBusiTradeData(BusiTradeData bd) throws Exception;

	/**
	 * 创建费用台帐数据
	 * 
	 * @param pd
	 * @param brd
	 * @return
	 * @throws Exception
	 */
	public List<FeeTradeData> createFeeTrade(BaseReqData brd) throws Exception
	{
		List<FeeData> feeDatas = brd.getFeeList();
		if (feeDatas != null && feeDatas.size() > 0)
		{
			int size = feeDatas.size();
			List<FeeTradeData> feeTradeDatas = new ArrayList<FeeTradeData>();
			for (int i = 0; i < size; i++)
			{
				FeeData feeData = feeDatas.get(i);
				if (!StringUtils.isBlank(feeData.getFeeMode()) && !StringUtils.isBlank(feeData.getFeeTypeCode()))
				{
					FeeTradeData feeTradeData = new FeeTradeData();
					feeTradeData.setFeeTypeCode(feeData.getFeeTypeCode());
					feeTradeData.setFeeMode(feeData.getFeeMode());
					feeTradeData.setFee(feeData.getFee());
					feeTradeData.setOldfee(feeData.getOldFee());
					feeTradeData.setRsrvStr1(feeData.getElementId());
					feeTradeData.setDiscntGiftId(feeData.getDiscntGiftId());
					feeTradeData.setUserId(brd.getUca().getUserId());
					feeTradeDatas.add(feeTradeData);
				}
			}
			return feeTradeDatas;
		}
		else
		{
			return null;
		}
	}

	/**
	 * 创建付费台帐数据
	 * 
	 * @param pd
	 * @param brd
	 * @return
	 * @throws Exception
	 */
	public List<PayMoneyTradeData> createPayMoneyTrade(BaseReqData brd) throws Exception
	{
		List<PayMoneyData> payMoneyDatas = brd.getPayMoneyList();
		if (payMoneyDatas != null && payMoneyDatas.size() > 0)
		{
			int size = payMoneyDatas.size();
			List<PayMoneyTradeData> payMoneyTradeDatas = new ArrayList<PayMoneyTradeData>();
			for (int i = 0; i < size; i++)
			{
				PayMoneyData payMoneyData = payMoneyDatas.get(i);
				if (!"0".equals(payMoneyData.getMoney()) && !StringUtils.isBlank(payMoneyData.getPayMoneyCode()))
				{
					PayMoneyTradeData payMoneyTradeData = new PayMoneyTradeData();
					payMoneyTradeData.setMoney(payMoneyData.getMoney());
					payMoneyTradeData.setPayMoneyCode(payMoneyData.getPayMoneyCode());
					payMoneyTradeDatas.add(payMoneyTradeData);
				}
			}
			return payMoneyTradeDatas;
		}
		else
		{
			return null;
		}
	}

	private void createShopingDetailTradeData(BusiTradeData bd, String shoppingCartId) throws Exception
	{
		OrderDataBus dataBus = DataBusManager.getDataBus();
		if (!dataBus.isCreateShoppingDetail())
		{
			BaseReqData brd = bd.getRD();
			ShoppingCartDetailTradeData shoppingDetailTradeData = new ShoppingCartDetailTradeData();
			String orderTypeCode = brd.getOrderTypeCode();
			shoppingDetailTradeData.setShoppingCartId(shoppingCartId);
			shoppingDetailTradeData.setOrderTypeCode(orderTypeCode);
			shoppingDetailTradeData.setOrderId(bd.getRD().getOrderId());
			if (StringUtils.isNotBlank(brd.getUca().getUserId()))
			{
				shoppingDetailTradeData.setUserId(brd.getUca().getUserId());
			}
			else
			{
				shoppingDetailTradeData.setUserId("0");
			}
			shoppingDetailTradeData.setCustId(brd.getUca().getCustId());
			shoppingDetailTradeData.setJoinType(bd.getRD().getJoinType());
			IDataset shoppingDetails = MerchShoppingCartQry.getShoppingDetailById(shoppingCartId, brd.getUca().getUserEparchyCode());
			if (CollectionUtils.isEmpty(shoppingDetails))
			{
				shoppingDetailTradeData.setJoinSeq("1");
			}
			else
			{
				shoppingDetailTradeData.setJoinSeq(String.valueOf((shoppingDetails.size() + 1)));
			}
			shoppingDetailTradeData.setRequestData(brd.getPageRequestData().toString());
			shoppingDetailTradeData.setTradeStaffId(CSBizBean.getVisit().getStaffId());
			shoppingDetailTradeData.setTradeDepartId(CSBizBean.getVisit().getDepartId());

			shoppingDetailTradeData.setAcceptDate(dataBus.getAcceptTime());
			bd.add(brd.getUca().getSerialNumber(), shoppingDetailTradeData);
			dataBus.setCreateShoppingDetail(true);
		}
	}

	// ========购物车

	// ========购物车
	private String createShoppingCartData(BusiTradeData btd) throws Exception
	{
		String shoppingCartId = getShoppingCartId(btd.getRD().getUca().getUserId());
		OrderDataBus dataBus = DataBusManager.getDataBus();

		if (StringUtils.isBlank(shoppingCartId) && !dataBus.isCreateShoppingDetail())
		{
			shoppingCartId = SeqMgr.getOrderId();
			ShoppingCartTradeData shoppingCartData = new ShoppingCartTradeData();
			UcaData uca = btd.getRD().getUca();
			shoppingCartData.setShoppingCartId(shoppingCartId);
			shoppingCartData.setCustId(uca.getCustId());
			shoppingCartData.setUserId(uca.getUserId());
			shoppingCartData.setState("0");
			shoppingCartData.setInModeCode(CSBizBean.getVisit().getInModeCode());
			btd.add(btd.getRD().getUca().getSerialNumber(), shoppingCartData);
		}
		return shoppingCartId;
	}

	/**
	 * 创建业务台帐
	 */
	@Override
	public BusiTradeData createTrade(BaseReqData rd) throws Exception
	{
		BusiTradeData bd = new BusiTradeData();
		bd.setBrd(rd);

		//根据受理时间生成，避免跨月产生tf_b_order和tf_b_trade表不一致。
		rd.setTradeId(SeqMgr.getNewTradeIdFromDb(rd.getAcceptTime()));
		String inModeCode = CSBizBean.getVisit().getInModeCode();

		// 拼主台账
		createBusiMainTradeData(bd);
		MainTradeData mainTradeData = bd.getMainTradeData();
		// 拼装FeeTradeData
		
		List<FeeTradeData> feeTradeDatas = this.createFeeTrade(rd);
		if (feeTradeDatas != null && feeTradeDatas.size() > 0)
		{
			int feeTradeDatasSize = feeTradeDatas.size();
			for (int i = 0; i < feeTradeDatasSize; i++)
			{
				bd.add(rd.getUca().getSerialNumber(), feeTradeDatas.get(i));
			}
		}
		// end

		// =======购物车
		OrderDataBus dataBus = DataBusManager.getDataBus();
		if (BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(dataBus.getSubmitType()))
		{
			String shoppingCartId = createShoppingCartData(bd);
			createShopingDetailTradeData(bd, shoppingCartId);
		}
		// =======购物车

		// 各自业务拼的基本台账
		this.createBusiTradeData(bd);

		/*
		 * 针对开户等新建uca的情况，由于在之前调用createBusiMainTradeData的时候，uca的productId和brandCode 还没有的情况，在这里补充
		 */
		// payment  print tag set begin
		String inModecodeStr = StaticUtil.getStaticValue("NEED_PAY_CHANNELS", "0");
		String mustPrintTag = StaticUtil.getStaticValue("MUST_PRINT_SWITCH", "0");
		String sdInModeCodes = StaticUtil.getStaticValue("SD_IN_MODE_CODE", "0");
		boolean isPrint = CommparaInfoQry.isTradeMustPrint(bd.getTradeTypeCode());
		if(StringUtils.indexOf(inModecodeStr,"|"+inModeCode+"|") != -1)
		{
			if(StringUtils.equals("on", mustPrintTag))
			{
				if(!isPrint)
				{
					boolean isPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CLOSE_NO_PRINT");
					if(!isPriv){//有权限的直接就0
						if(StringUtils.equals("CRM_PAGE", rd.getSubmitSource())|| //前台过来的才做控制
								//REQ201907040041 关于推送APP上受理的电子单据到稽核系统的需求—BOSS侧 新大陆调接口生成工单需要默认置为待打印状态，新大陆打印成功回调SS.UipInfoSVC.dealPrintTag
								("SD".equals(inModeCode)&&StringUtils.indexOf(sdInModeCodes,"|"+bd.getTradeTypeCode()+"|") != -1))
						{
							mainTradeData.setSubscribeState("Y");//初始化为Y
						}
					}else{
						PrintAction.action(mainTradeData.toData());//部分业务直接操作资料
					}
				}else
				{
					PrintAction.action(mainTradeData.toData());//部分业务直接操作资料
				}
			}
			else
			{
				PrintAction.action(mainTradeData.toData());//部分业务直接操作资料
			}
		}
		else
		{
			PrintAction.action(mainTradeData.toData());//部分业务直接操作资料
		}
		if (feeTradeDatas != null && feeTradeDatas.size() > 0)
		{
			int fee = 0;
			int feeTradeDatasSize = feeTradeDatas.size();
			for (int i = 0; i < feeTradeDatasSize; i++)
			{
				fee += Integer.parseInt(feeTradeDatas.get(i).getFee());
			}
			if(fee >0)
			{
				if(StringUtils.indexOf(inModecodeStr,"|"+inModeCode+"|") != -1)
				{
					if(StringUtils.equals("CRM_PAGE", rd.getSubmitSource())||"X".equals(inModeCode))//前台过来的才做控制
					{
						mainTradeData.setSubscribeState("X");//如果有费用，则设置为X
					}
				}
			}
		}
		if (StringUtils.isNotBlank(bd.getRD().getBatchId()))// 如果是批量，则subscribe_state改成0
		{
			mainTradeData.setSubscribeState("0");//
		}
		//payment print tag set end 
		
		if (StringUtils.isBlank(mainTradeData.getProductId()))
		{
			mainTradeData.setProductId(bd.getRD().getUca().getProductId());
		}
		if (StringUtils.isBlank(mainTradeData.getBrandCode()))
		{
			mainTradeData.setBrandCode(bd.getRD().getUca().getBrandCode());
		}

		setCheckMode(bd);

		// 是否发短信
		bd.setNeedSms(rd.isNeedSms());
		
		//如果是产品变更且是短信认证码鉴权，无需打印受理单
		if(rd.getTradeType()!=null&&"110".equals(rd.getTradeType().getTradeTypeCode())&&"7".equals(rd.getCheckMode())){
			mainTradeData.setSubscribeState("0");
		}
		
		//如果是无手机宽带开户融合个人开户 ，无需打印受理单
		if("10".equals(rd.getTradeType().getTradeTypeCode()) && "N".equals(bd.getRD().getPageRequestData().getString("TEMPLATE")) ){
			mainTradeData.setSubscribeState("0");
		}

		return bd;
	}

	private IData getShoppingCart(String userId) throws Exception
	{
		String staffId = CSBizBean.getVisit().getStaffId();
		IDataset dataset = MerchShoppingCartQry.getShoppingInfoByStaffUserIds(staffId, userId, "0");
		if (CollectionUtils.isNotEmpty(dataset))
		{
			return dataset.getData(0);
		}
		return null;
	}

	private String getShoppingCartId(String userId) throws Exception
	{
		IData shoppingOrderData = getShoppingCart(userId);
		if (MapUtils.isNotEmpty(shoppingOrderData))
		{
			return shoppingOrderData.getString("SHOPPING_CART_ID");
		}
		return null;
	}

	/**
	 * 设置校验方式 0=证件号码校验 1=服务密码校验 2=SIM卡号+服务密码校验 3=服务号码+证件号码校验 4=证件号码+服务密码校验 Z=未知认证 F=免认证
	 * 
	 * @param btd
	 * @throws Exception
	 */
	private void setCheckMode(BusiTradeData btd) throws Exception
	{
		BaseReqData reqData = btd.getRD();

		String checkMode = reqData.getCheckMode();
		String inModeCode = getVisit().getInModeCode();

		if ("WB".equals(inModeCode) || "L".equals(inModeCode) || "V".equals(inModeCode) || "FX".equals(inModeCode))
		{
			btd.setMainTradeProcessTagSet(20, "1");
		}
		/**
		 * 20160906
		 * @author zhuoyingzhi
		 */
		else if ("SD".equals(inModeCode))
		{
			String mode="1";
			if(checkMode != null && !"".equals(checkMode)&&!"Z".equals(checkMode)){
				mode=checkMode;
			}
			btd.setMainTradeProcessTagSet(20, mode);
		}
		else if ("5".equals(inModeCode))
		{
			btd.setMainTradeProcessTagSet(20, "0");
		}
		else
		{
			if (StringUtils.isBlank(checkMode) || StringUtils.length(checkMode) > 1)
			{
				btd.setMainTradeProcessTagSet(20, "Z");
			}
			else
			{
				btd.setMainTradeProcessTagSet(20, checkMode);
			}
		}
	}
}
