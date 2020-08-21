
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.config.ParamFilterConfig;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayMoneyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.BomcProcess;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.DBProcess;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.LockProcess;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.OrderProcess;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.PluginProcess;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.RuleProcess;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.TradeProcess;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.BofHelper;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.pos.PosBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradeFinish;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradeMag;

/**
 * @author Administrator
 */
public abstract class OrderService extends CSBizService
{
	private static final long serialVersionUID = 4021470998262442297L;

	protected IData input;

	protected static final Logger log = Logger.getLogger(OrderService.class);

	/**
	 * 业务提交后规则校验，如有特殊场景可重载该方法
	 */
	public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
	{
		RuleProcess.checkAfterRule(tableData, btd);
	}

	/**
	 * 构建PAYMONEY台账
	 * 
	 * @param input
	 * @param mainBtd
	 * @throws Exception
	 */
	public void createPayMoneyTD(IData input, BusiTradeData mainBtd) throws Exception
	{
		IDataset payMoneyList = new DatasetList();
		if (StringUtils.isNotBlank(input.getString("X_TRADE_PAYMONEY")))
		{
			// 传了则已传的为准
			payMoneyList = new DatasetList(input.getString("X_TRADE_PAYMONEY"));
		}
		else
		{
			String advancePay = DataBusManager.getDataBus().getAdvanceFee();
			String foreGift = DataBusManager.getDataBus().getForeGift();
			String operFee = DataBusManager.getDataBus().getOperFee();

			// 这个是用户总共缴纳的费用
			int totalPayMoney = Integer.parseInt(advancePay) + Integer.parseInt(foreGift) + Integer.parseInt(operFee);
			if (totalPayMoney > 0
					&&(!("X".equals(getVisit().getInModeCode()))))//排除自助终端
			{
				IData cashPayMoney = new DataMap();
				cashPayMoney.put("MONEY", String.valueOf(totalPayMoney));
				cashPayMoney.put("PAY_MONEY_CODE", input.getString("X_PAY_MONEY_CODE", "0"));
				payMoneyList.add(cashPayMoney);
			}
		}

		for (int i = 0, size = payMoneyList.size(); i < size; i++)
		{
			IData payMoneyData = payMoneyList.getData(i);
			payMoneyData.put("ORDER_ID", DataBusManager.getDataBus().getOrderId());
			PayMoneyTradeData payMoneyTD = new PayMoneyTradeData(payMoneyData);
			
			if("237".equals(input.getString("TRADE_TYPE_CODE")) || "2370".equals(input.getString("TRADE_TYPE_CODE")))
            {
                payMoneyTD.setPayMoneyCode(input.getString("PAYMONEYEND", "0"));
            }
			
			mainBtd.add(mainBtd.getRD().getUca().getSerialNumber(), payMoneyTD);

			// POS机刷卡
			if (payMoneyData.getString("PAY_MONEY_CODE").equals(PosBean.POS_PAY_CODE))
			{
				PosBean.checkPosFee(input.getString("TRADE_POS_ID"), DataBusManager.getDataBus().getOrderId(), mainBtd.getTradeId(), payMoneyData.getString("MONEY"));
			}
		}
	}

	private void dbProcess(IData input, BusiTradeData mainBtd, IDataset insertDatas) throws Exception
	{
		OrderDataBus dataBus = DataBusManager.getDataBus();
		String orderId = dataBus.getOrderId();
		List<BusiTradeData> btds = dataBus.getBtds();

		// 插入trade表相关数据
		DBProcess.insertTrades(insertDatas);

		// ============购物车
		if (!BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(dataBus.getSubmitType()))
		{
			// 插入tf_b_order订单数据
			MainOrderData mainOrderData = OrderProcess.createOrderData(input, mainBtd);
			// 重写tf_b_order订单数据，供子类继承
			this.resetMainOrderData(mainOrderData, mainBtd);
			DBProcess.insertOrder(mainOrderData);
		}
		// ============购物车

		// 多trade情况下，插入order与trade的关系表
		DBProcess.insertOrderSub(btds, orderId, mainBtd);

		// 如果是异地业务，需要进行中心库trade的记录
		DBProcess.insertCenTradeForYD();
		
		// 登记时调用完工流程，但是不搬台帐  移到  TradeFinishAPIWithOutMove 实现
		for (BusiTradeData btd : btds)
		{
			String tradeTypeCode = btd.getTradeTypeCode();
			String tradeId = btd.getTradeId();
			IDataset param = CommparaInfoQry.getCommPkInfo("CSM","557",tradeTypeCode,"0898");
			if (IDataUtil.isNotEmpty(param)&&param.size()>0)
			{
				if (StringUtils.isNotBlank(tradeId))
				{
					IDataset mainTrades = TradeInfoQry.getMainTradeByTradeId(tradeId);
					if (IDataUtil.isNotEmpty(mainTrades))
					{
						IData mainTrade = mainTrades.getData(0);
						TradeFinish.finishAPIWithOutMove(mainTrade);
					}
				}
			}
		}
	}

	public abstract String getOrderTypeCode() throws Exception;

	public abstract String getTradeTypeCode() throws Exception;

	private void logCrtTradeTrace(String orderId, String tradeId, String acceptMonth) throws Exception
	{
		TradeMag.traceLog(orderId, tradeId, acceptMonth, null, "createTrade", "0", "ok");
	}

	public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
	{

	}

	public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
	{

	}

	public void resetReturn(IData input, IData output, BusiTradeData btd) throws Exception
	{

	}

	/**
	 * 调用build,trade,action层的处理
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public BusiTradeData trade(IData input) throws Exception
	{
		input.put("TRADE_TYPE_CODE", getTradeTypeCode());
		return TradeProcess.acceptOrder(input);
	}

	public IDataset tradeReg(IData input) throws Exception
	{
		this.input = input;
		boolean myOrder = false;

		try
		{
			input.put("X_TRANS_CODE", getVisit().getXTransCode());
			OrderDataBus dataBus = DataBusManager.getDataBus();

			if (StringUtils.isBlank(dataBus.getOrderId()))
			{
				myOrder = true;

				// 获取orderTypeCode并设置到OrderDataBus中
				String orderTypeCode = getOrderTypeCode();
				dataBus.setOrderTypeCode(orderTypeCode);

				// 获取orderId
				String orderId = SeqMgr.getOrderId();
				dataBus.setOrderId(orderId);

				// 设置受理时间
				String acceptTime = SysDateMgr.getSysTime();
				dataBus.setAcceptTime(acceptTime);

				// ===========购物车
				dataBus.setSubmitType(input.getString("SUBMIT_TYPE"));
				// ===========购物车
			}
			
			//对于全网渠道编码需要重新赋值办理工号
			resetLoginStaff(input);
			
			input.put("ORDER_TYPE_CODE", dataBus.getOrderTypeCode());
			input.put("TRADE_TYPE_CODE", getTradeTypeCode());

			// 对serial_number加锁,skip_lock=true跳过锁校验,目前该接口会透传该入参：销售订单同步接口（BIP3B507_T3000510_1_0）,  （相关类：AbilityOpenPlatFormIntfSVC.java）
			String skip_lock=input.getString("SKIP_LOCK","");
			if(!"349".equals(this.getTradeTypeCode())&&(!"true".equals(skip_lock))){
			LockProcess.lock(this.getTradeTypeCode(), input.getString("SERIAL_NUMBER"));
			}
			if (StringUtils.isNotBlank(input.getString("BATCH_ID")))
			{
				dataBus.setBatchId(input.getString("BATCH_ID"));
			}

			BusiTradeData busiTradeData = trade(input);
			
			BomcProcess.recordBomc(busiTradeData);
			
			otherTradeDeal(input, busiTradeData);

			long beginTime = System.currentTimeMillis();

			// 插客户订单表
			if (myOrder)
			{
				String orderId = DataBusManager.getDataBus().getOrderId();
				List<BusiTradeData> btds = DataBusManager.getDataBus().getBtds();

				// ============购物车
				if (!BofConst.SUBMIT_TYPE_SHOPPING_CART.equals(dataBus.getSubmitType()))
				{
					String advancePay = DataBusManager.getDataBus().getAdvanceFee();
					String foreGift = DataBusManager.getDataBus().getForeGift();
					String operFee = DataBusManager.getDataBus().getOperFee();
					// 这个是用户总共缴纳的费用
					int totalPayMoney = Integer.parseInt(advancePay) + Integer.parseInt(foreGift) + Integer.parseInt(operFee);

					if (!StringUtils.equals("CRM_PAGE", busiTradeData.getRD().getSubmitSource())
							|| (("290".equals(getTradeTypeCode()) || "419"
									.equals(getTradeTypeCode())) && totalPayMoney < 0))// 前台过来的才做控制
					{
						createPayMoneyTD(input, busiTradeData);
					}
				}
				// ============购物车

				int btdSize = btds.size();
				IDataset insertDatas = new DatasetList();
				PluginProcess.pluginProcessBefore(btds, input);
				for (int i = 0; i < btdSize; i++)
				{
					BusiTradeData btd = btds.get(i);
					IData insertData = new DataMap();
					insertData.put("ROUTE", btd.getRoute());

					IData tableData = DBProcess.buildInsertDatas(btd);
					if (tableData != null)
					{
						insertData.put("TABLE_DATA", tableData);
						insertDatas.add(insertData);
						
						//初始化提交后规则校验为：校验
						boolean isCheck = true;
						
						//是否跳过规则，如果传入SKIP_RULE=TRUE，则不进行提交后的规则校验
				        if(StringUtils.isNotBlank(input.getString("SKIP_RULE","")) && "TRUE".equals(input.getString("SKIP_RULE","")))
				        {
				        	isCheck = false;
				        }
				        
						if(isCheck){
							// 业务提交后规则校验
							checkAfterRule(tableData, btd);
						}
					}
				}
				if (BofHelper.isNotPreTrade(busiTradeData.getRD()))
				{
					this.dbProcess(input, busiTradeData, insertDatas);
					PluginProcess.pluginProcess(btds, input);
				}

				// 定单标识
				String tradeId = busiTradeData.getTradeId();
				String acceptMonth = StrUtil.getAcceptMonthById(tradeId);

				// 生成集团业务稽核工单
	            actGrpBizBaseAudit(input, busiTradeData);
				
				// 记录订单生成轨迹
				logCrtTradeTrace(orderId, tradeId, acceptMonth);
			}

			if (log.isDebugEnabled())
			{
				log.debug("插台账表表 cost time:" + (System.currentTimeMillis() - beginTime) / 1000.0D + "s");
			}

			IDataset returnSet = new DatasetList();

			IData result = new DataMap();
			result.put("ORDER_ID", dataBus.getOrderId());
			result.put("DB_SOURCE", busiTradeData.getRoute());
			result.put("TRADE_ID", busiTradeData.getTradeId());
			result.put("USER_ID", busiTradeData.getMainTradeData().getUserId());
			result.put("ORDER_TYPE_CODE", dataBus.getOrderTypeCode());

			IFilterOut out = ParamFilterConfig.getOutParamFilter(input.getString("X_TRANS_CODE"), input);

			if (out != null)
			{
				result.putAll(out.transfterDataOut(input, busiTradeData));
			}

			resetReturn(input, result, busiTradeData);
			returnSet.add(result);

			return returnSet;
		}
		catch (Exception e)
		{
			IFilterException exp = ParamFilterConfig.getExceptionParamFilter(input.getString("X_TRANS_CODE"), input);

			if (exp != null)
			{
				IData result = exp.transferException(Utility.getBottomException(e), input);
				IDataset returnSet = new DatasetList();
				returnSet.add(result);
				SessionManager.getInstance().rollback();

				if (log.isDebugEnabled())
				{
					log.error(e.getMessage(), e);
				}

				return returnSet;
			}
			else
			{
				throw e;
			}
		}
		finally
		{
			if (myOrder)
			{
				// 删除总线
				DataBusManager.removeDataBus();
			}
		}
	}
	
	/**
     * REQ201906120013 优化工作手机折扣参数批量修改、达量限速批量修改等功能
     * 生成集团业务稽核工单
     * REQ201804280001集团合同管理界面优化需求
     * @param map
     * @throws Exception
     * @author guonj
     * @date 2019-07-09
     */
	protected void actGrpBizBaseAudit(IData map, BusiTradeData<?> btd)throws Exception {
		// TODO Auto-generated method stub
		
	}
	/**
	 * REQ201909030024  新增了网状网系统接口规范-移动商城业务工号记录修改的需求
	 * 1、先判断19位全网渠道编码入参【UNI_CHANNEL】是否有值
	 * 2、符合1再判断服务接口是否在配置里
	 * 3、符合2再通过UNI_CHANNEL找对应的办理工号及办理部门
	 * @param input
	 * @throws Exception
	 */
	private void resetLoginStaff(IData input)throws Exception {
		if(StringUtils.isNotEmpty(input.getString("UNI_CHANNEL"))){
			String svcName=input.getString("X_TRANS_CODE");
			if(StringUtils.isNotEmpty(svcName)){
				IDataset svcList=CommparaInfoQry.getCommParas("CSM", "7979", "SVC_CODE", svcName, "0898");
				//有配置需要转换工号的服务
				if(IDataUtil.isNotEmpty(svcList)&&svcList.size()>0){
					//查询全网编码对应的办理工号及部门
					IDataset channelList=CommparaInfoQry.getCommParas("CSM", "7979", "UNI_CHANNEL_STAFF", input.getString("UNI_CHANNEL"), "0898");
					if(IDataUtil.isNotEmpty(channelList)&&channelList.size()>0){
						//赋值登录工号
						if(StringUtils.isNotEmpty(channelList.getData(0).getString("PARA_CODE2"))){
							getVisit().setStaffId(channelList.getData(0).getString("PARA_CODE2"));
						}
						//赋值登录部门ID
						if(StringUtils.isNotEmpty(channelList.getData(0).getString("PARA_CODE3"))){
							getVisit().setDepartId(channelList.getData(0).getString("PARA_CODE3"));
						}
						//赋值部门代码
						if(StringUtils.isNotEmpty(channelList.getData(0).getString("PARA_CODE4"))){
							getVisit().setDepartCode(channelList.getData(0).getString("PARA_CODE4"));
						}
						//赋值业务区
						if(StringUtils.isNotEmpty(channelList.getData(0).getString("PARA_CODE5"))){
							getVisit().setCityCode(channelList.getData(0).getString("PARA_CODE5"));
						}
					}
				}
			}
		}
	}
}
