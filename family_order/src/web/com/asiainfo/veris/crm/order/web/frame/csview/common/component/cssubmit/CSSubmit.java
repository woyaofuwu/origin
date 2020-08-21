package com.asiainfo.veris.crm.order.web.frame.csview.common.component.cssubmit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class CSSubmit extends CSBizTempComponent
{
	private static Logger logger = Logger.getLogger(CSSubmit.class);
	public boolean isPrint = true; // 打印控制标记

	public boolean edocPrint = false; // 仅仅打印电子工单

	public boolean bothPrint = false; // 打印免填单和电子工单

	public boolean edocSecond = false; // 电子工单版本

	public boolean closeNoPrint = false;// 不打印免填单

	public boolean mustPrintTag = false;// 办理业务必须打印免填单开关

	protected void cleanupAfterRender(IRequestCycle cycle)
	{
		super.cleanupAfterRender(cycle);
	}

	/**
	 * 业务受理成功后，点击确定按钮触发事件
	 */
	public abstract String getAffirmAction();

	/**
	 * 数据提交区块
	 */
	public abstract String getArea();

	/**
	 * 业务受理前调用事件
	 */
	public abstract String getBeforeAction();

	/**
	 * 后台调用处理类 如果传入该参数，组合listener侦听方法，后台调用服务，将采用hhSubmit方式 否则采用普通的$.ajax.submit
	 */
	public abstract String getCallBean();

	/**
	 * 取消规则校验
	 */
	public abstract String getCancelRule();

	/**
	 * 是否禁用提交
	 */
	public abstract String getDisabledBtn();

	/**
	 * 获取电子工单数据
	 * 
	 * @param data
	 * @throws Exception
	 */
	private IData getElecPrintInfo(IData data) throws Exception
	{
		IData returnData = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset printDataSet = CSViewCall.call(this, "CS.PrintNoteSVC.getCnoteInfo", data);
		if (IDataUtil.isNotEmpty(printDataSet))
		{
			returnData = printDataSet.getData(0);
			if (printDataSet.size() > 1)
			{// 有其他业务交易的记录
				IDataset otherTradeList = printDataSet.getDataset(1);
				returnData.put("OTHER_TRADE_LIST", otherTradeList);
			}
		}
		return returnData;
	}

	/**
	 * 是否集团提交
	 */
	public abstract String getIsGrp();

	/**
	 * 是否渲染提交排列样式
	 */
	public abstract String getIsRender();

	/**
	 * 是否生成重置按钮
	 */
	public abstract String getIsReset();

	/**
	 * 侦听方法，业务受理方法
	 */
	public abstract String getListener();

	/**
	 * 业务受理静态参数
	 */
	public abstract String getParams();

	/**
	 * 获取打印数据
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset getPrintData(IData data) throws Exception
	{
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset printDataSet = CSViewCall.call(this, "CS.PrintNoteSVC.getPrintData", data);
		if (printDataSet == null || printDataSet.isEmpty())
		{
			printDataSet = new DatasetList();
		}

		return printDataSet;
	}

	/**
	 * 获取打印信息
	 * 
	 * @param data
	 *            [ORDER_ID,EPARCHY_CODE]
	 * @throws Exception
	 */
	public void getPrintInfo(IData data) throws Exception
	{
		IData returnData = new DataMap();
		String prtTicket = data.getString("PRINT_TICKET", "0");
		String prtEdoc = data.getString("PRINT_EDOCUMENT", "0");
		String prtTag = getPrintTag(data);
		returnData.put("PRINT_TAG", prtTag); // 打印标记

		IDataset printInfos = null;
		// 如果业务设置需要打印业务受理单，或产生了费用信息，那么去加载费用数据
		if (StringUtils.equals("1", prtTag) || StringUtils.equals("1", prtTicket))
		{
			printInfos = getPrintData(data);
			if (IDataUtil.isNotEmpty(printInfos))
			{// 免填单排前面
				DataHelper.sort(printInfos, "TYPE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
			}
			returnData.put("PRINT_DATA", printInfos); // 打印数据
		}
		// 如果业务有打印电子工单权限，且已经打印过业务受理单，则生成电子工单数据
		if (StringUtils.equals("1", prtEdoc) && StringUtils.equals("1", prtTag) && IDataUtil.isNotEmpty(printInfos))
		{
			IData printData = null;
			for (int i = 0, size = printInfos.size(); i < size; i++)
			{
				printData = printInfos.getData(i);
				if (!StringUtils.equals(printData.getString("TYPE"), "P0003"))
				{
					continue;
				}
				if (StringUtils.isNotBlank(printData.getString("TRADE_ID")))
				{
					IData param = new DataMap();
					param.put("TRADE_ID", printData.getString("TRADE_ID"));

					IData cnoteInfo = getElecPrintInfo(param);
					if (IDataUtil.isNotEmpty(cnoteInfo))
					{
						returnData.put("CNOTE_DATA", cnoteInfo);
					}
				}
				break;
			}
		}
		// 检查是否生成受理单，如果没有就更新SUBSCRIBE_STATE=0
		checkReceiptAndSetTradeState(data);
		getPage().setAjax(returnData);
	}

	/**
	 * 获取打印标记
	 * 
	 * @param data
	 *            [TRADE_TYPE_CODE，EPARCHY_CODE]
	 * @throws Exception
	 */
	private String getPrintTag(IData data) throws Exception
	{
		String prtTradeTag = "0";
		if (!StringUtils.equals(data.getString("TRADE_TYPE_CODE", ""), ""))
		{
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

		return prtTradeTag;
	}

	/**
	 * 配置该业务是否必须打印免填单
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private boolean getTradePrintParam(IData data) throws Exception
	{
		boolean prtTradeTag = true;
		if (!StringUtils.equals(data.getString("TRADE_TYPE_CODE", ""), ""))
		{
			if (!data.containsKey("EPARCHY_CODE") || "".equals(data.getString("EPARCHY_CODE", "")))
			{
				data.put("EPARCHY_CODE", getTradeEparchyCode());
			}
			IDataset tradeTypes = CSViewCall.call(this, "CS.TradeTypeInfoQrySVC.getTradePrintParam", data);
			if (IDataUtil.isNotEmpty(tradeTypes))
			{
				prtTradeTag = false;
			}
		}

		return prtTradeTag;
	}

	/**
	 * 获取单联票据提示框数据
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void getPrintTicketInfo(IData data) throws Exception
	{
		IData params = new DataMap();
		params.putAll(data);
		params.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		// 接口返回数据[TICKET_ID,TAX_NO]
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.getStaffPrintTicket", params);
		if (IDataUtil.isNotEmpty(infos))
		{
			data.putAll(infos.getData(0));
		}
		getPage().setAjax(data);
	}

	/**
	 * 业务提交后刷新区块
	 */
	public abstract String getRefreshPart();

	/**
	 * 重置按钮事件
	 */
	public abstract String getResetAction();

	// -------购物车
	public IData getShoppingTag(String orderTypeCode) throws Exception
	{
		IData data = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		data.put("TRADE_TYPE_CODE", orderTypeCode);
		IDataset tradeTypes = CSViewCall.call(this, "SS.ShoppingCartSVC.getShoppingTag", data);
		return CollectionUtils.isEmpty(tradeTypes) ? null : tradeTypes.getData(0);
	}

	// -------购物车

	/**
	 * 提交按钮ICON
	 */
	public abstract String getSubmitIcon();

	/**
	 * 提交按钮样式
	 */
	public abstract String getSubmitStyle();

	/**
	 * 提交按钮文字Label
	 */
	public abstract String getSubmitText();

	/**
	 * 票据确认和占用，以及相关日志信息记录
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void regPrintTicketLog(IData data) throws Exception
	{
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.regPrintTicketLog", data);
		getPage().setAjax(infos.getData(0));
	}

	public void renderComponent(StringBuilder infoParamsBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
	{
		IData data = getPage().getData();
		// 区分组件自身初始化还是后面页面触发组件业务方法

		String action = data.getString("ACTION", "");
		// 初始化组件
		if (null == action || "".equals(action))
		{
			getPage().addResAfterBodyBegin("scripts/csserv/component/cssubmit/CSSubmit.js");
			getPage().addResAfterBodyBegin("scripts/csserv/component/print/PrintMgr.js");

			if (null == getSubmitText() || "".equals(getSubmitText()))
			{
				setSubmitText("提交(Y)");
			}
			String inModeCode = getVisit().getInModeCode();
			String staffId = getVisit().getStaffId();

			setMustPrintTag(StringUtils.equals(StaticUtil.getStaticValue("MUST_PRINT_SWITCH", "0"), "on"));

			setCloseNoPrint(StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CLOSE_NO_PRINT"));
			// 打印免填单和电子工单
			setBothPrint(StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "BOTH_DOCUMENT"));
			// 仅仅打印电子工单
			setEdocPrint(StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "ONLY_EDOCUMENT"));
			// 设置打印电子工单版本
			setEdocSecond(StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "EDOCUMENT_SECOND"));
			// 打印控制标记
			setIsPrint((StringUtils.equals(inModeCode, "0") && !staffId.startsWith("HNYD")) || StringUtils.equals(inModeCode, "3") || StringUtils.equals(inModeCode, "SD") ? true : false);

			// ------购物车
			String orderTypeCode = data.getString("orderTypeCode4ShoppingCart");
			boolean hasShoppingPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SHOPPING_CART");
			if (hasShoppingPriv)
			{
				if (StringUtils.isNotBlank(orderTypeCode))
				{
					IData shoppingTag = getShoppingTag(orderTypeCode);
					if (shoppingTag != null && MapUtils.isNotEmpty(shoppingTag))
					{
						if ("1".equals(shoppingTag.getString("SHOPPING_TAG")))
						{
							this.setIsShoppingCart("true");
						}
					}
				}
			}
			// ------购物车
		} else
		{
			// 内部刷新组件
			if ("PRINT".equals(action))
			{
				getPrintInfo(data);
			} else if ("GET_TICKET".equals(action))
			{
				getPrintTicketInfo(data);
			} else if ("CHECK_TICKET".equals(action))
			{
				regPrintTicketLog(data);
			} else if ("UPD_PRINT_TAG".equals(action))
			{
				updataPrintTag(data);
			} else if ("HAS_PRINT_ALL".equals(action))
			{
				hasPrintAll(data);
			} else if ("ERROR_LOG".equals(action)) // 插入错误日记表
			{
				errorLog(data);
			} else if ("GET_ERECEPT_SEND_CONF".equals(action))
			{
				getERecptSendConf(data); // 获取开具营业电子发票打印的推送信息
			} else if ("PRINT_KJ".equals(action))
			{
				printKJ(data); // 调开具营业电子发票打印接口
			} else if ("HAD_PRINTED_KJ".equals(action))
			{
				hadPrintedKJ(data); // 调是否已打印接口(纸质、电子)
			} else if ("GET_TRADE_TYPE_KJ_CONF".equals(action))
			{
				getTradeTypeKjConf(data); // 获取业务类型是否支持营业电子发票设置
			} else if ("GET_ERECEPT_GRP_CONF".equals(action)) // 集团电子发票设置
			{
				getERecptGrpConf(data); // 获取开具营业电子发票打印的推送信息
			} else if ("CHECKHASFEE".equals(action)) // 校验订单是否有费用
			{
				checkOrderHasFee(data); //       
			} else if ("PAYMENT".equals(action)) // 支付下单
			{
				ceatePaymentOrder(data); //       
			} else if ("PAY_BACK".equals(action)) // 支付成功后处理
			{
				payOrder(data); //       
			} else if ("CANCEL_ORDER".equals(action)) // 未打印免填单之前取消订单
			{
				// cancelOrder(data); //屏蔽，直接取消订单风险很大，很多资源占用reg的时候就做了
			} else if ("NO_PRINT_EXCUTE".equals(action)) // 拥有免打印权限工号，不打印执行工单
			{
				excuteOrderNoPrint(data); //       
			} else if ("GET_PIC_TAG".equals(action))
			{
				getPicTag(data); // 获取业务类型是否支持营业电子发票设置
			}
			/**
			 * REQ201805090016_在线公司电子稽核报表优化需求
			 * <br/>
			 * 
			 * @author zhuoyingzhi
			 * @date 20180518
			 */
			else if("IS_MAIN_PRODUCT_CHANGE_OR_SALE_NAME".equals(action)){
					//判断是否是主产品变更或获取当做营销活动受理时获取营销活动名称
				isMainProductChangeOrSaleActionName(data);
			}else if("IS_HYYYK_BAT_CHOPEN".equals(action)){
				//判断是否为行业应用卡
				 isHyyykBatChopen(data);
			}
			setRenderContent(false); // 不刷新组件
		}
	}

	public abstract void setAffirmAction(String affirmAction);

	public abstract void setArea(String area);

	public abstract void setBeforeAction(String beforeAction);

	public void setBothPrint(boolean bothPrint)
	{
		this.bothPrint = bothPrint;
	}

	public abstract void setCallBean(String callBean);

	public abstract void setCancelRule(String cancelRule);

	public abstract void setDisabledBtn(String disabledBtn);

	public void setEdocPrint(boolean edocPrint)
	{
		this.edocPrint = edocPrint;
	}

	public void setEdocSecond(boolean edocSecond)
	{
		this.edocSecond = edocSecond;
	}

	public void setCloseNoPrint(boolean closeNoPrint)
	{
		this.closeNoPrint = closeNoPrint;
	}

	public void setMustPrintTag(boolean mustPrintTag)
	{
		this.mustPrintTag = mustPrintTag;
	}

	public abstract void setIsGrp(String isGrp);

	public void setIsPrint(boolean isPrint)
	{
		this.isPrint = isPrint;
	}

	public abstract void setIsRender(String isRender);

	public abstract void setIsReset(String isReset);

	// ------购物车
	public abstract void setIsShoppingCart(String isShoppingCart);

	public abstract void setListener(String listener);

	public abstract void setParams(String params);

	public abstract void setRefreshPart(String refreshPart);

	public abstract void setResetAction(String resetAction);

	public abstract void setSubmitIcon(String submitIcon);

	public abstract void setSubmitStyle(String submitStyle);

	// ------购物车

	public abstract void setSubmitText(String submitText);

	/**
	 * 更新打印标记
	 * 
	 * @param data
	 * @throws java.lang.Exception
	 */
	public void updataPrintTag(IData data) throws java.lang.Exception
	{
		IData info = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.updataPrintTag", data);
		if (null != infos && infos.size() > 0)
		{
			info = infos.getData(0);
		}
		getPage().setAjax(info);
	}

	/**
	 * 是否打印了所有免填单
	 * 
	 * @param data
	 * @throws java.lang.Exception
	 */
	public void hasPrintAll(IData data) throws java.lang.Exception
	{
		IData info = new DataMap();
		boolean tradePrintTag = getTradePrintParam(data);// 业务是否配置了可以不打印
		if (!tradePrintTag)
		{
			info.put("PRINT_ALL", "true");
		} else
		{
			data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
			IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.getNotPrintedInfo", data);
			if (null != infos && infos.size() > 0)
			{
				info.put("PRINT_ALL", "false");
			} else
			{
				info.put("PRINT_ALL", "true");
			}
		}
		getPage().setAjax(info);
	}

	/**
	 * 记录解析打印受理单模板错误日记表
	 * 
	 * @param data
	 * @author wukw3
	 * @throws java.lang.Exception
	 */
	public void errorLog(IData data) throws java.lang.Exception
	{
		IData info = new DataMap();
		IData returnData = new DataMap();
		String prtTicket = data.getString("PRINT_TICKET", "0");
		String prtTag = getPrintTag(data);
		returnData.put("PRINT_TAG", prtTag); // 打印标记
		if (StringUtils.equals("1", prtTag) || StringUtils.equals("1", prtTicket))
		{
			data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
			IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.errorLog", data);
			if (null != infos && infos.size() > 0)
			{
				info = infos.getData(0);
				returnData.put("LOG_DATA", info); // 日记信息数据
			}
		}
		getPage().setAjax(returnData);
	}

	/**
	 * 获取开具营业电子发票打印的推送信息
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void getERecptSendConf(IData data) throws Exception
	{
		IData info = new DataMap();
		IData inparam = new DataMap();
		inparam.put("USER_ID", data.get("USER_ID"));
		inparam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		inparam.put(Route.ROUTE_EPARCHY_CODE, inparam.getString("EPARCHY_CODE"));

		IDataset ttinfos = CSViewCall.call(this, "SS.ModifyEPostInfoSVC.qryEPostInfoByUserId", inparam);
		if (null != ttinfos && ttinfos.size() > 0)
		{
			for (int i = 0; i < ttinfos.size(); i++)
			{
				IData tmp = ttinfos.getData(i);
				if ("2".equals(tmp.getString("POST_TAG")))
				{ // 日常营业电子设置
					info.put("RECEIVER_SENDWAY", tmp.getString("POST_CHANNEL"));
					info.put("RECEIVER_MOBILE", tmp.getString("RECEIVE_NUMBER"));
					info.put("RECEIVER_EMAIL", tmp.getString("POST_ADR"));
					break;
				}
			}
		} else
		{
			if ("416".equals(data.getString("TRADE_TYPE_CODE", "")) || "149".equals(data.getString("TRADE_TYPE_CODE", "")))
			{
				info.put("RECEIVER_SENDWAY", "6");
			}
		}

		// info.put("RECEIVER_SENDWAY", "1");
		// info.put("RECEIVER_MOBILE", "15273164011");
		// info.put("RECEIVER_EMAIL", "a@163.com");

		getPage().setAjax(info);
	}

	/**
	 * 调开具打印电子发票接口
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void printKJ(IData data) throws Exception
	{
		data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.printKJ", data);
		IData info = new DataMap();
		if (null != infos && infos.size() > 0)
		{
			info = infos.getData(0);
		}

		getPage().setAjax(info);
	}

	/**
	 * 添加是否拍照标记
	 * 
	 * @param data
	 * @throws java.lang.Exception
	 */
	public void getPicTag(IData data) throws java.lang.Exception
	{
		IData info = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.getPicTag", data);
		if (null != infos && infos.size() > 0)
		{
			info = infos.getData(0);
		}
		getPage().setAjax(info);
	}

	public void hadPrintedKJ(IData data) throws Exception
	{
		data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.hadPrintedKJ", data);
		IData info = new DataMap();
		if (null != infos && infos.size() > 0)
		{
			info = infos.getData(0);
		}

		getPage().setAjax(info);
	}

	public void checkReceiptAndSetTradeState(IData data) throws Exception
	{
		IData info = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.checkReceiptAndSetTradeState", data);
		if (null != infos && infos.size() > 0)
		{
			info = infos.getData(0);
		}
		getPage().setAjax(info);
	}

	public void ceatePaymentOrder(IData data) throws Exception
	{
		data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));

		IDataset unPaidTradeFees = CSViewCall.call(this, "CS.PaymentSVC.getOrderFees", data);

		int fee = 0;
		IData info = new DataMap();
		IDataset goodLists = new DatasetList();
		if (IDataUtil.isNotEmpty(unPaidTradeFees))
		{
			for (int i = 0; i < unPaidTradeFees.size(); i++)
			{
				IData paidData = unPaidTradeFees.getData(i);
				fee += paidData.getInt("FEE", 0);
				IData temp = new DataMap();
				temp.put("PEER_TRADE_ID", paidData.getString("TRADE_ID"));
				temp.put("GOODS_NAME", paidData.getString("TRADE_NAME", "业务费用"));
				temp.put("GOODS_PRICE", paidData.getString("FEE"));
				temp.put("GOODS_NUM", "1");
				temp.put("TOTAL_MONEY", paidData.getString("FEE"));
				goodLists.add(temp);
			}
			IData input = new DataMap();
			input.put("PEER_ORDER_ID", data.getString("ORDER_ID"));
			input.put("ORDER_FEE", fee + "");
			String sn = data.getString("SERIAL_NUMBER");
			if ("undefined".equals(sn) || StringUtils.isBlank(sn))
			{
				sn = "无号码";
			}
			input.put("SERIAL_NUMBER", sn);
			input.put("ORDER_DESC", "订单总费用");
			input.put("MERCHANT_ID", "1000");
			input.put("GOODS_LIST", goodLists.toString());

			IDataset dataset = CSViewCall.call(this, "payment.order.IPayAccessSV.createPayDetail", input);

			if (null != dataset && dataset.size() > 0)
			{
				info = dataset.getData(0);
			}
		}
		getPage().setAjax(info);
	}

	public void checkOrderHasFee(IData data) throws Exception
	{
		data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));

		IDataset orderFees = CSViewCall.call(this, "CS.PaymentSVC.getOrderFees", data);
		IData ajaxData = new DataMap();
		if (IDataUtil.isNotEmpty(orderFees))
		{
			ajaxData.put("ORDER_HAS_FEE", "true");
		} else
		{
			ajaxData.put("ORDER_HAS_FEE", "false");
		}

		getPage().setAjax(ajaxData);
	}

	public void payOrder(IData data) throws Exception
	{
		data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
		IDataset infos = CSViewCall.call(this, "CS.PaymentSVC.payOrder", data);
		IData info = new DataMap();
		if (null != infos && infos.size() > 0)
		{
			info = infos.getData(0);
		}
		getPage().setAjax(info);
	}

	public void cancelOrder(IData data) throws Exception
	{
		IData info = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.cancelOrder", data);
		if (null != infos && infos.size() > 0)
		{
			info = infos.getData(0);
		}
		getPage().setAjax(info);
	}

	public void excuteOrderNoPrint(IData data) throws Exception
	{
		IData info = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.updateOrderState", data);
		if (null != infos && infos.size() > 0)
		{
			info = infos.getData(0);
		}
		getPage().setAjax(info);
	}

	/** 获取业务类型是否支持营业电子发票设置 */
	public void getTradeTypeKjConf(IData data) throws Exception
	{
		data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.getTradeTypeKjConf", data);
		IData info = new DataMap();
		if (null != infos && infos.size() > 0)
		{
			info.put("CAN_SET_EPRINTCEPT_CONF", "TRUE");
		}

		getPage().setAjax(info);
	}

	/**
	 * 获取开具营业电子发票打印的推送信息
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void getERecptGrpConf(IData data) throws Exception
	{
		IData info = new DataMap();
		IData inparam = new DataMap();
		inparam.put("CUST_ID", data.get("CUST_ID"));
		inparam.put("USER_ID", data.getString("USER_ID"));

		IDataset ttinfos = CSViewCall.call(this, "CS.SetGrpElecInvoiceSVC.getERecptGrpConf", inparam);
		if (null != ttinfos && ttinfos.size() > 0)
		{
			for (int i = 0; i < ttinfos.size(); i++)
			{
				IData tmp = ttinfos.getData(i);
				if ("2".equals(tmp.getString("POST_TAG")))
				{ // 日常营业电子设置
					info.put("RECEIVER_SENDWAY", tmp.getString("POST_CHANNEL"));
					info.put("RECEIVER_MOBILE", tmp.getString("RECEIVE_NUMBER"));
					info.put("RECEIVER_EMAIL", tmp.getString("POST_ADR"));
					break;
				}
			}
		}

		// info.put("RECEIVER_SENDWAY", "1");
		// info.put("RECEIVER_MOBILE", "15273164011");
		// info.put("RECEIVER_EMAIL", "a@163.com");

		getPage().setAjax(info);
	}
	
	/**
	 * REQ201805090016_在线公司电子稽核报表优化需求
	 * <br/>
	 * 判断是否是主产品变更
	 * @author zhuoyingzhi
	 * @date 20180518
	 * @param data
	 * @throws java.lang.Exception
	 */
	public void isMainProductChangeOrSaleActionName(IData data) throws java.lang.Exception
	{
		IData info = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.isMainProductChange", data);
		if (null != infos && infos.size() > 0)
		{
			info = infos.getData(0);
		}
		getPage().setAjax(info);
	}
	
    /**
     * REQ201805090016_在线公司电子稽核报表优化需求
     * <br/>
     * IS_HYYYK_BAT_CHOPEN  0:是行业应用卡    1：非行业应用卡
     * <br/>
     * 判断是否为行业应用卡
     * @author zhuoyingzhi
     * @date 20180523
     * @param data
     * @return
     * @throws Exception
     */
	public void isHyyykBatChopen(IData data) throws java.lang.Exception
	{
		IData info = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.isHyyykBatChopen", data);
		if (null != infos && infos.size() > 0)
		{
			info = infos.getData(0);
		}
		getPage().setAjax(info);
	}
}
