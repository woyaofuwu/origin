package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.KjPrintBean;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryInfoUtil;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryListInfo;

public class AbilityOpenPlatFormIntfSVC extends CSBizService {
	static Logger logger = Logger.getLogger(AbilityOpenPlatFormIntfSVC.class); 
	/**
	 * 能力开放平台总接口
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IData abilityOpenPlatSubmit(IData data) throws Exception {

		String bipcode = data.getString("BIPCODE", "");// 业务编码
		String activitycode = data.getString("ACTIVITYCODE", "");// 交易编码
		if ("".equals(bipcode)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_590);
		}
		if ("".equals(activitycode)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_590);
		}
		IData returndata = new DataMap();

		if ("BIP3B506".equals(bipcode) && "T3000506".equals(activitycode)) {// 6.4.4.1
			// 终端产品同步
			returndata = terminalSynchro(data);
		}
		if ("BIP3B506".equals(bipcode) && "T3000507".equals(activitycode)) {// 6.4.4.2
			// 合约产品同步
			returndata = contractProductSynchro(data);
		}
		if ("BIP3B506".equals(bipcode) && "T3000508".equals(activitycode)) {// 6.4.4.3
			// 套餐及增值产品同步
			returndata = productInfoSynchro(data);
		}
		if ("BIP3B506".equals(bipcode) && "T3000509".equals(activitycode)) {// 6.4.4.4
			// 商品信息同步
			returndata = goodsInfoSynchro(data);
		}
		if ("BIP3B507".equals(bipcode) && "T3000510".equals(activitycode)) {// 6-4
			// 销售订单信息同步
			returndata = sellOrderSynchro(data);
		}
		if ("BIP3B512".equals(bipcode) && "T3000515".equals(activitycode)) {// 订单状态同步
			returndata = orderStatusSynchro(data);
		}
		if ("BIP3B513".equals(bipcode) && "T3000516".equals(activitycode)) {// 退货订单校验
			returndata = refundOrderCheck(data);
		}
		if ("BIP3B510".equals(bipcode) && "T3000513".equals(activitycode)) {// 退款订单同步
			returndata = refundOrderSynchro(data);
		}
		if ("BIP3B504".equals(bipcode) && "T3000504".equals(activitycode)) {// 入网资格校验
			AbilityPlatCheckIntf beanIntf = new AbilityPlatCheckIntf();
			returndata = beanIntf.checkComeInNet(data);
		}
		if ("BIP3B505".equals(bipcode) && "T3000505".equals(activitycode)) {// 业务办理校验
			AbilityPlatCheckIntf beanIntf = new AbilityPlatCheckIntf();
			returndata = beanIntf.checkServiceRule(data);
		}

		return returndata;
	}

	/**
	 * 终端产品同步接口
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData terminalSynchro(IData data) throws Exception {

		AbilityOpenPlatformIntfBean bean = BeanManager
				.createBean(AbilityOpenPlatformIntfBean.class);
		chkParamNoStr(data, "TERMINAL_ID", "802127");
		chkParamNoStr(data, "CHANGE_TYPE", "802127");
		chkParamNoStr(data, "CHANGE_TIME", "802127");
		chkParamNoStr(data, "BRAND_NAME", "802127");
		chkParamNoStr(data, "MODEL_NAME", "802127");

		data.put("BOSS_ID", data.getString("BOSS_TERMINAL_ID", ""));
		data.put("SCREEN_SIZE", data.getString("SCREENSIZE", ""));
		data.put("DEVICE_SIZE", data.getString("DEVICESIZE", ""));
		data.put("STATUS", data.getString("CHANGE_TYPE", ""));
		data.put("UPDATE_TIME", SysDateMgr.getSysTime());
		data.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID", ""));

		bean.terminalSynchro(data);

		return null;
	}

	/**
	 * 合约产品同步接口
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData contractProductSynchro(IData input) throws Exception {
		AbilityOpenPlatformIntfBean bean = BeanManager
				.createBean(AbilityOpenPlatformIntfBean.class);

		int contractNum = input.getInt("CONTRACT_NUM");
		for (int i = 0; i < contractNum; i++) {
			IData contractList = new DataMap();
			IDataset contractLists = input.getDataset("CONTRACT_INF" + i);
			if (contractLists != null && contractLists.size() > 0) {
				contractList = contractLists.getData(0);
				String areaStr = "";
				IDataset areas = getIDatasetSpecl("AREA", contractList
						.getString("AREA"));
				IData area = new DataMap();

				for (int j = 0; j < areas.size(); j++) {
					area = (IData) areas.get(j);
					if ("".equals(areaStr)) {
						areaStr = area.getString("AREA");
					} else {
						areaStr = areaStr + "|" + area.getString("AREA");
					}
				}
				contractList.put("CONTRACT_AREA", areaStr);
				contractList.put("STATUS", contractList.getString(
						"CHANGE_TYPE", ""));
				contractList.put("UPDATE_TIME", SysDateMgr.getSysTime());
				contractList.put("UPDATE_STAFF_ID", input
						.getString("TRADE_STAFF_ID"));
				contractList.put("UPDATE_DEPART_ID", input
						.getString("TRADE_DEPART_ID"));
				bean.contractProductSynchro(contractList);

			}
		}

		return null;
	}

	/**
	 * 套餐及增值产品同步接口
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData productInfoSynchro(IData input) throws Exception {

		AbilityOpenPlatformIntfBean bean = BeanManager
				.createBean(AbilityOpenPlatformIntfBean.class);

		int vasNum = input.getInt("VAS_NUM", 0);
		for (int i = 0; i < vasNum; i++) {
			IData vafList = new DataMap();
			IDataset vafLists = input.getDataset("VAS_INF" + i);
			if (vafLists != null && vafLists.size() > 0) {
				vafList = vafLists.getData(0);
				String areaStr = "";
				IDataset areas = getIDatasetSpecl("VAS_AREA", vafList
						.getString("VAS_AREA"));
				IData area = new DataMap();

				for (int j = 0; j < areas.size(); j++) {
					area = (IData) areas.get(j);
					if ("".equals(areaStr)) {
						areaStr = area.getString("VAS_AREA");
					} else {
						areaStr = areaStr + "|" + area.getString("VAS_AREA");
					}
				}

				vafList.put("BOSS_ID", vafList.getString("BOSSID"));
				vafList.put("EFFECT_TIME", vafList.getString("EFFECTTIME"));
				vafList.put("PACKAGE_VIDEOPHONE", vafList
						.getString("PACKAGE_VIDEO_PHONE"));
				vafList.put("VAS_NAME", vafList.getString("VASNAME"));
				vafList.put("VAS_TYPE", vafList.getString("VASTYPE"));

				vafList.put("VAS_AREA", areaStr);
				vafList.put("STATUS", vafList.getString("CHANGE_TYPE", ""));
				vafList.put("VAS_ID", vafList.getString("VASID"));
				vafList.put("UPDATE_TIME", SysDateMgr.getSysTime());
				vafList.put("UPDATE_STAFF_ID", input
						.getString("TRADE_STAFF_ID"));
				vafList.put("UPDATE_DEPART_ID", input
						.getString("TRADE_DEPART_ID"));
				bean.productInfoSynchro(vafList);

			}
		}

		return null;
	}

	/**
	 * 商品信息同步接口
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData goodsInfoSynchro(IData input) throws Exception {
		AbilityOpenPlatformIntfBean bean = BeanManager
				.createBean(AbilityOpenPlatformIntfBean.class);
		int goodsNum = input.getInt("COODS_NUM");
		for (int i = 0; i < goodsNum; i++) {
			IData paramList = new DataMap();
			IDataset paramLists = input.getDataset("COODS_INF" + i);
			if (paramLists != null && paramLists.size() > 0) {
				paramList = paramLists.getData(0);
				String goodsAreaStr = "";
				IDataset goodsAreas = getIDatasetSpecl("GOODS_AREA", paramList
						.getString("GOODS_AREA"));
				IData goodsArea = new DataMap();
				for (int j = 0; j < goodsAreas.size(); j++) {
					goodsArea = (IData) goodsAreas.get(j);
					if ("".equals(goodsAreaStr)) {
						goodsAreaStr = goodsArea.getString("GOODS_AREA");
					} else {
						goodsAreaStr = goodsAreaStr + "|"
								+ goodsArea.getString("GOODS_AREA");
					}
				}
				paramList.put("GOODS_AREA", goodsAreaStr);
				paramList.put("UPDATE_TIME", SysDateMgr.getSysTime());
				paramList.put("UPDATE_STAFF_ID", input
						.getString("TRADE_STAFF_ID"));
				paramList.put("UPDATE_DEPART_ID", input
						.getString("TRADE_DEPART_ID"));
				bean.goodsInfoSynchro(paramList, "g");

				IData productInf = new DataMap();
				IDataset productInfs = new DatasetList(paramList
						.getString("PRODUCT_INF"));
				for (int k = 0; k < productInfs.size(); k++) {
					productInf = productInfs.getData(k);
					productInf.put("GOODS_ID", paramList.getString("GOODS_ID"));
					productInf.put("IS_SINGLECHOICE", productInf
							.getString("IS_SINGLE_CHOICE"));
					productInf.put("IS_MANDATORY", productInf
							.getString("ISMANDATORY"));
					productInf.put("CHANGE_TYPE", paramList
							.getString("CHANGE_TYPE"));
					productInf.put("GROUP_INDEX", productInf
							.getString("PRODUC_GROUP_ID"));
					productInf.put("UPDATE_TIME", SysDateMgr.getSysTime());
					productInf.put("UPDATE_STAFF_ID", input
							.getString("TRADE_STAFF_ID"));
					productInf.put("UPDATE_DEPART_ID", input
							.getString("TRADE_DEPART_ID"));
					bean.goodsInfoSynchro(productInf, "ps");

					IDataset productList = new DatasetList(productInf
							.getString("PRODUCT_LIST"));
					IData productAddInfo = new DataMap();

					for (int L = 0; L < productList.size(); L++) {
						productAddInfo = productList.getData(L);
						IDataset productIds = getIDatasetSpecl("PRODUCT_ID",
								productAddInfo.getString("PRODUCT_ID"));
						IDataset productClass = getIDatasetSpecl(
								"PRODUCT_CLASS", productAddInfo
										.getString("PRODUCT_CLASS"));

						IData productIdData = new DataMap();
						for (int p = 0; p < productIds.size(); p++) {
							productIdData = (IData) productIds.get(p);

							productIdData.put("GOODS_ID", paramList
									.getString("GOODS_ID"));
							productIdData.put("CHANGE_TYPE", paramList
									.getString("CHANGE_TYPE"));
							productIdData.put("GROUP_INDEX", productInf
									.getString("PRODUC_GROUP_ID"));
							productIdData.put("PRODUCT_TYPE", productClass
									.getData(p).getString("PRODUCT_CLASS"));
							productIdData.put("UPDATE_TIME", SysDateMgr
									.getSysTime());
							productIdData.put("UPDATE_STAFF_ID", input
									.getString("TRADE_STAFF_ID"));
							productIdData.put("UPDATE_DEPART_ID", input
									.getString("TRADE_DEPART_ID"));
							bean.goodsInfoSynchro(productIdData, "p");
						}
					}
				}

				IData goodsAdd = new DataMap();
				IDataset goodsAdds = new DatasetList(paramList
						.getString("GOODS_ADD"));
				for (int a = 0; a < goodsAdds.size(); a++) {
					goodsAdd = goodsAdds.getData(a);

					IDataset addIds = getIDatasetSpecl("ADD_ID", goodsAdd
							.getString("ADD_ID"));
					IDataset addNames = getIDatasetSpecl("ADD_NAME", goodsAdd
							.getString("ADD_NAME"));
					IData googsData = new DataMap();
					for (int id = 0; id < addIds.size(); id++) {
						googsData.put("GOODS_ID", paramList
								.getString("GOODS_ID"));
						googsData.put("CHANGE_TYPE", paramList
								.getString("CHANGE_TYPE"));
						googsData.put("ADD_ID", addIds.getData(id).getString(
								"ADD_ID")); // 扩展属性ID
						googsData.put("ADD_NAME", addNames.getData(id)
								.getString("ADD_NAME")); // 扩展属性值
						bean.goodsInfoSynchro(googsData, "attr");
					}
				}
			}
		}

		return null;
	}

	/**
	 * 销售订单信息同步接口
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData sellOrderSynchro(IData input) throws Exception {

		chkParamNoStr(input, "BILL_DAY", "802127"); // 订单对账标识

		int tlistNum = input.getInt("TLIST_NUM");
		IData retnData = new DataMap();

		// 存储多个订单的TID
		List<String> tidList = new ArrayList<String>();

		int nCount = 0;
		if (tlistNum > 0) {
			for (int k = 0; k < tlistNum; k++) {
				IData tlist = new DataMap();
				IDataset tlistNums = input.getDataset("TLIST_INF" + k); // 订单数据
				if (tlistNums != null && tlistNums.size() > 0) {
					tlist = tlistNums.getData(0);
					tlist.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
					tlist.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
					tlist.put("BILL_DAY", input.getString("BILL_DAY"));
					IData idResult = saveListOrderInfo(tlist);
					String strProductStatus = idResult.getString("PRODUCT_STATUS", "");
					if("1".equals(strProductStatus))
					{
						nCount++;
						tidList.add(tlist.getString("TID"));
					}
				}
			}
			logger.error("=================开始处理订单==============");
			logger.error("=================开始处理订单==============388 "+input);
			logger.error("=================开始处理订单==============389 "+tidList);
			
			//REQ201905070016能力开放平台订单BIP3B507接口异步处理
			String syncTag = "false";
			IDataset configElementList = CommparaInfoQry.getCommPkInfo("CSM", "9731", "SYNC_TAG", "0898");
			if(IDataUtil.isNotEmpty(configElementList)){
				syncTag = configElementList.getData(0).getString("PARA_CODE1", "");
			}
			logger.error("=================开始处理订单==============syncTag:"+syncTag);
			// 处理订单
			if("false".equals(syncTag) && nCount > 0)
			//REQ201905070016能力开放平台订单BIP3B507接口异步处理
			{
				dealListOrderInfo(input, tidList);
			}
			logger.error("=================结束处理订单==============");
			if(IDataUtil.isNotEmpty(input.getDataset("TRADE_ID_S"))){
				//--掉账务接口开具发票
				for (int k = 0; k < tlistNum; k++) {
					IData tlist = new DataMap();
					IDataset tlistNums = input.getDataset("TLIST_INF" + k); // 订单数据
					if (IDataUtil.isNotEmpty(tlistNums)){
						tlist = tlistNums.getData(0);
						String phoneOprType = tlist.getString("PHONE_OPR_TYPE");
		                String needInvoice = tlist.getString("NEED_INVOICE");
		                String chargeType = tlist.getString("CHARGE_TYPE");
		                if("02".equals(phoneOprType) && "1".equals(needInvoice) && ("0".equals(chargeType) || "2".equals(chargeType) || "6".equals(chargeType))){
		                	//查询台帐 调用开票接口
		                	IData printData = new DataMap();
		        			//组装数据
		        			buildPrintData(printData,tlist,input);
		        			try {
								KjPrintBean bean = BeanManager.createBean(KjPrintBean.class);
								bean.printKJForSC(printData);
							} catch (Exception e) {
								logger.error(e.getMessage());
							}
		        			//结束--
		                }
					}
				}
			}
		}
		
		retnData.put("X_RESULTINFO", "");
		retnData.put("X_RECORDNUM", "0");
		retnData.put("IS_SUCCESS", "0");
		return null;
	}

	/**
	 * 组装调用开具发票接口的数据
	 * @param printData
	 * @param tlist 
	 * @throws Exception 
	 */
	private void buildPrintData(IData printData, IData tlist,IData retnData) throws Exception {
		String strPrintId = SeqMgr.getPrintId(); // 新生成发票PRINT_ID
		printData.put("PRINT_ID", strPrintId);
		printData.put("TYPE", "P0001"); // 票据类型：发票P0001、收据P0002、免填单(业务受理单)P0003
		printData.put("CUST_TYPE", "PERSON");//客户类型
		printData.put("APPLY_CHANNEL", "0");// 开票发起渠道：：0-营业个人业务;1-集团有ACCTID业务；2-集团无ACCTID业务；3-账务
		printData.put("TOTAL_MONEY", tlist.getString("PAYMENT"));
		printData.put("PAY_NAME", tlist.getString("INVOICE_NAME",""));
		printData.put("ABILITY", "1");
		printData.put("NAME", "销售订单同步");
		printData.put("TAG", "NKFP");
		String tradeId = (String) retnData.getDataset("TRADE_ID_S").get(0);
		printData.put("TRADE_ID", tradeId);
		IDataset mainTradeInfos = TradeInfoQry.getTradeAndBHTradeByTradeId(printData.getString("TRADE_ID"));//查询台账及历史台账
		if(IDataUtil.isNotEmpty(mainTradeInfos)){
			IData mainTradeInfo=mainTradeInfos.getData(0);
			printData.putAll(mainTradeInfo);
		}
	}

	/**
	 * 保存销售订单信息
	 * 
	 * @param pd
	 * @param data
	 * @param conn
	 * @throws Exception
	 */
	public IData saveListOrderInfo(IData data) throws Exception 
	{
        logger.error("=================AbilityOpenPlatFormIntfSVC.java==============410 "+data);
		IDataset orderLists = data.getDataset("ORDER_LIST");
        logger.error("=================AbilityOpenPlatFormIntfSVC.java==============412 "+orderLists);
		String retnCode = "1";
		String msg = "订购成功";
		IData idResult = new DataMap();
		idResult.put("PRODUCT_STATUS", retnCode);
		// 独立事务用来保存接收到的订单信息，以免在登记订单的过程异常而导致订单信息丢失
		DBConnection conn = null;
		try 
		{
			conn = SessionManager.getInstance().getAsyncConnection("cen1");

			AbilityOpenPlatformIntfBean bean = BeanManager.createBean(AbilityOpenPlatformIntfBean.class);

			chkParamNoStr(data, "TID", "802127"); // 订单编码
			chkParamNoStr(data, "OUT_TID", "802127"); // 合作渠道订单编码
			chkParamNoStr(data, "CHANNEL_ID", "802127"); // 渠道ID
			chkParamNoStr(data, "CTEATE_TIME", "802127"); // 订单创建时间
															// CREATE_TIME
															// PAY_MENT
			chkParamNoStr(data, "PAY_TIME", "802127"); // 订单支付时间
			chkParamNoStr(data, "DISTRIBUTION", "802127"); // 是否需省公司配送
			chkParamNoStr(data, "BUYER_NICK", "802127"); // 买家名称
			chkParamNoStr(data, "PAYMENT", "802127"); // 买家名称
			chkParamNoStr(data, "ORDER_LIST", "802127"); // 子订单信息
			 //20170421 视频流量定向包需求新增字段
			chkParamNoStr(data, "PHONE_OPR_TYPE", "802127");//业务号码操作类型
			chkParamNoStr(data, "NEED_INVOICE", "802127");//是否需要开发票

			data.put("PAY_MENT", data.getString("PAYMENT"));
			data.put("UPDATE_TIME", SysDateMgr.getSysTime());
			data.put("CREATE_TIME", data.getString("CTEATE_TIME"));
			data.put("PAY_TIME", data.getString("PAY_TIME"));
	        logger.error("=================AbilityOpenPlatFormIntfSVC.java==============441 "+data);
	        
	        IDataset idsCtrmtList = QueryListInfo.queryCtrmtListByID(data);
	        if(IDataUtil.isEmpty(idsCtrmtList))
	        {
	        	bean.insertIntoCtrmTList(conn, data);
	        }
	        
			logger.error("=================AbilityOpenPlatFormIntfSVC.java==============442 ");
			// 订单中子订单信息信息

			IData orderList = new DataMap();
	        logger.error("=================AbilityOpenPlatFormIntfSVC.java==============447 "+orderLists);
			if (orderLists != null && orderLists.size() > 0) 
			{
				for (int i = 0; i < orderLists.size(); i++) 
				{

					orderList = orderLists.getData(i);
					logger.error("=================AbilityOpenPlatFormIntfSVC.java==============452 "+orderList);
					String eparchyCode = getMofficeBySN(orderList.getString("PHONE", ""));

					orderList.put("TID", data.getString("TID")); // 订单ID
					chkParamNoStr(orderList, "OID", "802127"); // 子订单ID
					chkParamNoStr(orderList, "OUT_OID", "802127"); // 合作渠道子订单编码
					chkParamNoStr(orderList, "GOODS_ID", "802127"); // 商品编码
					chkParamNoStr(orderList, "TITLE", "802127"); // 商品标题
					chkParamNoStr(orderList, "NUM", "802127"); // 购买数量
					chkParamNoStr(orderList, "PRICE", "802127"); // 价格
					chkParamNoStr(orderList, "PROVINCE", "802127"); // 商品所在地省份
					chkParamNoStr(orderList, "CITY", "802127"); // 商品所在地城市
					chkParamNoStr(orderList, "TOTAL", "802127"); // 该子订单总金额
					chkParamNoStr(orderList, "ADJEST_FEE", "802127"); // 手工调整金额
					// chkParamNoStr(orderList, "MOBILE_NO","802127"); //业务号码类型
					chkParamNoStr(orderList, "ORSER_STATUS", "802127"); // 订单状态
					orderList.put("ACCEPT_DATE", SysDateMgr.getSysTime());
					orderList.put("ORDER_STATUS", data.getString("ORSER_STATUS"));
					orderList.put("UPDATE_TIME", SysDateMgr.getSysTime());
					orderList.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID"));
					orderList.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID"));
					logger.error("=================AbilityOpenPlatFormIntfSVC.java==============472 "+orderLists);
					
					IDataset idsCtrmtOrderList = QueryListInfo.queryCtrmtOrderByID(orderList);
			        if(IDataUtil.isEmpty(idsCtrmtOrderList))
			        {
			        	bean.insertIntoCtrmOrder(conn, orderList);
			        }
			        
					/* 根据商品ID查询子订单下面的产品信息 */
					IDataset productIds = orderList.getDataset("PRODUCT_LIST");//getIDatasetSpecl("PRODUCE_ID",orderList.getString("PRODUCE_ID"));orderList.getString("PRODUCE_ID"));
                    logger.error("=================AbilityOpenPlatFormIntfSVC.java==============478 "+productIds);
					if (productIds != null && productIds.size() > 0) 
					{
						// 子订单产品信息
						IData productInfo = new DataMap();

						for (int h = 0; h < productIds.size(); h++) 
						{

							productInfo = productIds.getData(h);
		                    logger.error("=================AbilityOpenPlatFormIntfSVC.java==============486 "+productInfo);
							chkParamNoStr(productInfo, "PRODUCE_ID", "802127");
							 //视频定向流量包add
	                        String productType=IDataUtil.chkParam(productInfo, "PRODUCT_TYPE","");
	                        String serviceIdList=productInfo.getString("SERVICE_ID_LIST","");
	                        productInfo.put("CTRM_PRODUCT_TYPE_CODE",productType);
	                        productInfo.put("CTRM_PRODUCT_SERVICEID",serviceIdList );
							productInfo.put("TID", data.getString("TID"));
							productInfo.put("OID", orderList.getString("OID"));
							productInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
							productInfo.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID"));
							productInfo.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID"));
							productInfo.put("PHONE", orderList.getString("PHONE"));
							productInfo.put("CTRM_PRODUCT_ID", productInfo.getString("PRODUCE_ID"));
							productInfo.put("STATUS", "0");

							/* 查询产品对应关系表信息 */
							IData inparam = new DataMap();
							inparam.put("EPARCHY_CODE", eparchyCode);
							inparam.put("CTRM_PRODUCT_ID", productInfo.getString("PRODUCE_ID"));
                            logger.error("=================AbilityOpenPlatFormIntfSVC.java==============506 "+inparam);
							IDataset relaProductIds = QueryListInfo.queryListInfoForRelation(inparam);
                            logger.error("=================AbilityOpenPlatFormIntfSVC.java==============508 "+relaProductIds);
							if (relaProductIds == null || relaProductIds.size() <= 0) 
							{
								String errors = "订单产品ID【" + productInfo.getString("PRODUCE_ID") + "】不存在本地产品的映射关系，请管理员进行配置！";
								CSAppException.appError("BGU_520", errors);
							}

							for (int k = 0; k < relaProductIds.size(); k++) 
							{
								IData relaMap = relaProductIds.getData(k);
	                            logger.error("=================AbilityOpenPlatFormIntfSVC.java==============518 "+relaMap);
								// relation 表中RSRV_STR1 为改营销活动是否需要IMEI办理
								// 如果为1则表示不需要 默认为空需要
								// RSRV_STR2 为改合约类型，对应TD_B_CTRM_CONTRACT表中
								// CONTRACT_TYPE 值
								// 能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品
								// 其中1和2不做处理，3的时候调用产品变更接口
								if ("1".equals(relaMap.getString("CTRM_PRODUCT_TYPE")) || "3".equals(relaMap.getString("RSRV_STR2"))) 
								{
									productInfo.put("STATUS", "1");// 终端产品默认为已处理完
																	// 号卡类合约也默认处理完
								}
								
								if ("3".equals(relaMap.getString("CTRM_PRODUCT_TYPE"))) 
								{
									chkParamNoStr(orderList, "PHONE", "802127"); // 业务号码
								}
								//productInfo.put("PID", QueryListInfo.getSeqId());
								productInfo.put("PID", SeqMgr.getCtrmProId());
								productInfo.put("CONTRACT_ID", relaMap.getString("CONTRACT_ID", "-1"));
								productInfo.put("PRODUCT_ID", relaMap.getString("PRODUCT_ID", "-1"));
								productInfo.put("PACKAGE_ID", relaMap.getString("PACKAGE_ID", "-1"));
								productInfo.put("ELEMENT_ID", relaMap.getString("ELEMENT_ID", "-1"));
								productInfo.put("ELEMENT_TYPE_CODE", relaMap.getString("ELEMENT_TYPE_CODE"));
								productInfo.put("CTRM_PRODUCT_TYPE", relaMap.getString("CTRM_PRODUCT_TYPE"));
								productInfo.put("RSRV_STR1", relaMap.getString("RSRV_STR1"));
								productInfo.put("RSRV_STR2", relaMap.getString("RSRV_STR2"));
								productInfo.put("RSRV_STR3", relaMap.getString("RSRV_STR3"));
								productInfo.put("RSRV_STR4", relaMap.getString("RSRV_STR4"));
								productInfo.put("RSRV_STR5", relaMap.getString("RSRV_STR5"));
                                logger.error("=================AbilityOpenPlatFormIntfSVC.java==============546 "+productInfo);
                                
                                IDataset idsCtrmtProductList = QueryListInfo.queryCtrmtProductByID(productInfo);
                                if(IDataUtil.isNotEmpty(idsCtrmtProductList))
                                {
                                	IData idCtrmtProductList = idsCtrmtProductList.first();
                                	String strStatus = idCtrmtProductList.getString("STATUS", "");
                                	if(!"0".equals(strStatus))
                                	{
                                		idResult.put("PRODUCT_STATUS", "2");
                                		return idResult;
                                	}
                                }
            			        if(IDataUtil.isEmpty(idsCtrmtProductList))
            			        {
            			        	bean.insertIntoCtrmOrderProduct(conn, productInfo);
            			        }
							}

						}
					}
					// 子订单扩展属性
					String orderAddStr = orderList.getString("ORDER_ADD", "");
                    logger.error("=================AbilityOpenPlatFormIntfSVC.java==============554 "+orderAddStr);
					if (!"".equals(orderAddStr)) 
					{
						IDataset orderAdds = new DatasetList(orderAddStr);
						IData orderAdd = new DataMap();
						logger.error("=================AbilityOpenPlatFormIntfSVC.java==============557 "+orderAdds);
						for (int k = 0; k < orderAdds.size(); k++) 
						{
							orderAdd = orderAdds.getData(k);
							logger.error("=================AbilityOpenPlatFormIntfSVC.java==============561 "+orderAdd);
							IDataset addIds = getIDatasetSpecl("ADD_ID",orderAdd.getString("ADD_ID"));
							IDataset addValues = getIDatasetSpecl("ADD_VALUE",orderAdd.getString("ADD_VALUE"));
							logger.error("=================AbilityOpenPlatFormIntfSVC.java==============563 "+addIds);
							logger.error("=================AbilityOpenPlatFormIntfSVC.java==============565 "+addValues);
							IData orderAddr = new DataMap();

							for (int n = 0; n < addIds.size(); n++) 
							{
								chkParamNoStr(addIds.getData(n), "ADD_ID","802127"); // 订单扩展属性ID
								chkParamNoStr(addValues.getData(n),"ADD_VALUE", "802127"); // 订单扩展属性值
								orderAddr.put("OID", orderList.getString("OID")); // 子订单ID
								orderAddr.put("ADDID", addIds.getData(n).getString("ADD_ID")); // 子订单ID
								orderAddr.put("ADDVALUE", addValues.getData(n).getString("ADD_VALUE")); // 子订单ID
								logger.error("=================AbilityOpenPlatFormIntfSVC.java==============574 "+orderAddr);
								IDataset idsCtrmOrderAttrList = QueryListInfo.queryCtrmOrderAttrByID(orderAddr);
            			        if(IDataUtil.isEmpty(idsCtrmOrderAttrList))
            			        {
            			        	bean.insertIntoCtrmOrderAttr(conn, orderAddr);
            			        }
							}
						}
					}
				}
			}

			conn.commit();

		} 
		catch (Exception e1) 
		{
			if(logger.isInfoEnabled()) logger.info(e1);
			if(conn != null)
			{
				conn.rollback();
			}
			retnCode = "2";
			msg = "订购失败";
			e1.printStackTrace();
			CSAppException.appError("BGU_520",e1.getMessage());
		} 
		finally 
		{
			if(conn != null)
			{
				conn.close();
			}
		}
		return idResult;
	}
	
	/**
	 * 
	 * @param input
	 * @throws Exception
	 */
	public IData dealOrderInfo(IData input) throws Exception 
	{
		chkParamNoStr(input, "TID", "17082101"); 
		String strTID = input.getString("TID");
		input.put("IN_MODE_CODE", getVisit().getInModeCode());
		input.put("TRADE_EPARCHY_CODE", "0898");
		input.put("TRADE_CITY_CODE", getVisit().getCityCode());
		input.put("TRADE_DEPART_ID", getVisit().getDepartId());
		input.put("TRADE_STAFF_ID", getVisit().getStaffId());
		// 存储多个订单的TID
		List<String> tidList = new ArrayList<String>();
		tidList.add(strTID);
		logger.error("=================dealOrderInfo接口，开始处理订单==============");
		// 处理订单
		input.put("X_RESULTCODE", "0");
		input.put("X_RESULTINFO", "OK");
		input.put("DEAL_ORDER_STATUS", "2");
		dealListOrderInfo(input, tidList);
		logger.error("=================dealOrderInfo接口，结束处理订单==============");
		return input;
	}
	
	/**
	 * 
	 * @param input
	 * @throws Exception
	 */
	public IData processOrderInfo(IData input) throws Exception 
	{
		chkParamNoStr(input, "TID", "17082101"); 
		String strTID = input.getString("TID");
		input.put("IN_MODE_CODE", "6");
		input.put("TRADE_EPARCHY_CODE", "INTF");
		input.put("TRADE_CITY_CODE", "INTF");
		input.put("TRADE_DEPART_ID", "00309");
		input.put("TRADE_STAFF_ID", "IBOSS000");
		getVisit().setStaffId("IBOSS000");
		getVisit().setDepartId("00309");
		getVisit().setInModeCode("6");
		getVisit().setCityCode("INTF");
		// 存储多个订单的TID
		List<String> tidList = new ArrayList<String>();
		tidList.add(strTID);
		logger.error("=================dealOrderInfo接口，开始处理订单==============");
		// 处理订单
		input.put("X_RESULTCODE", "0");
		input.put("X_RESULTINFO", "OK");
		dealListOrderInfo(input, tidList);
		
		logger.error("=================账务接口开具发票==============:"+input.getDataset("TRADE_ID_S"));
		
		logger.error("=================账务接口开具发票=input=============:"+input);
		if(IDataUtil.isNotEmpty(input.getDataset("TRADE_ID_S")))
		{
			//--掉账务接口开具发票
			IDataset idsCtrmtList = QueryListInfo.queryCtrmtListByID(input);
	        if(IDataUtil.isNotEmpty(idsCtrmtList))
	        {
	        	IData tlist = idsCtrmtList.getData(0);
	        	String phoneOprType = tlist.getString("PHONE_OPR_TYPE");
	            String needInvoice = tlist.getString("NEED_INVOICE");
	            String chargeType = tlist.getString("CHARGE_TYPE");
	            if("02".equals(phoneOprType) && "1".equals(needInvoice) && ("0".equals(chargeType) 
	            		|| "2".equals(chargeType) || "6".equals(chargeType)))
	            {
	            	//查询台帐 调用开票接口
	            	IData printData = new DataMap();
	    			//组装数据
	    			buildPrintData(printData,tlist,input);
	    			try {
						KjPrintBean bean = BeanManager.createBean(KjPrintBean.class);
						bean.printKJForSC(printData);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
	    			//结束--
		  
	            }
	        }
		}
		logger.error("=================dealOrderInfo接口，结束处理订单==============");
		return input;
	}
	

	/**
	 * 处理订单登记
	 * 
	 * @param pd
	 * @param data
	 * @param tidList
	 * @throws Exception
	 */
	public void dealListOrderInfo(IData data, List tidList) throws Exception 
	{ 
		String productType="";
		IDataset tradeIds = new DatasetList();
		logger.error("=================AbilityOpenPlatFormIntfSVC.java==============604 "+data);
		logger.error("=================AbilityOpenPlatFormIntfSVC.java==============605 "+tidList);
		
		if (tidList != null && tidList.size() > 0) 
		{
			AbilityOpenPlatformIntfBean bean = BeanManager.createBean(AbilityOpenPlatformIntfBean.class);
			IData param = new DataMap();
			// 循环处理订单
			IDataset orderList = null;
			for (int i = 0; i < tidList.size(); i++) 
			{
				String tid = (String) tidList.get(i);				
				param.put("TID", tid);
				orderList = QueryListInfo.queryListInfoForTlist(param);
		         
				logger.error("=================AbilityOpenPlatFormIntfSVC.java==============615 "+orderList);
				
				// 循环处理子订单信息
				IDataset subOrderList = null;

				String strStatus = data.getString("DEAL_ORDER_STATUS", "0");
				for (int j = 0; j < orderList.size(); j++) 
				{
					IData orderInfo = orderList.getData(j);
					String uniChannelId = orderInfo.getString("UNI_CHANNEL_ID","");    //新增一级能开UNI_CHANNEL_ID全网编码
					param.clear();
					param.put("TID", tid);
					param.put("OID", orderInfo.getString("OID"));
					param.put("STATUS", strStatus);
					subOrderList = QueryListInfo.queryListInfoForTidTlist(param);
					 
					logger.error("=================AbilityOpenPlatFormIntfSVC.java==============625 "+subOrderList);
					
					boolean hasVasFlag = false;    // 是否有合约产品
					boolean productFlag = false;   // 是否有产品变更
					boolean mainProFlag = false;   //是否是主产品变更
					boolean fxproductFlag = false;

					List<String> pidList = new ArrayList<String>();// 保存要更新状态的子订单产品表PID
					IDataset productParams = new DatasetList();
					IData saleActiveInfo = null;         //保存调用营销活动的参数

					IDataset eleIdList = new DatasetList();
					IDataset elements = new DatasetList();
					String route = getMofficeBySN(orderInfo.getString("PHONE"));
					IData proSmsInfo = new DataMap();
					
					IData userProInfo = UcaInfoQry.qryUserMainProdInfoBySn(orderInfo.getString("PHONE"));
                    logger.error("=================AbilityOpenPlatFormIntfSVC.java==============642 "+userProInfo);
                    
					String strAcceptDate = SysDateMgr.getSysTime();
					for (int k = 0; k < subOrderList.size(); k++) 
					{
						IData subInfo = subOrderList.getData(k);
						String strAD = subInfo.getString("ACCEPT_DATE", SysDateMgr.getSysTime());
						strAcceptDate = SysDateMgr.decodeTimestamp(strAD, SysDateMgr.PATTERN_STAND);
						IData eleInfo = new DataMap();
						data.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
						
						// 能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品
						// 其中1插表时已处理，2不做处理，3的时候调用产品变更接口
						if ("2".equals(subInfo.getString("CTRM_PRODUCT_TYPE"))) 
						{
							hasVasFlag = true;
							if ("2".equals(orderInfo.getString("DISTRIBUTION"))) 
							{
								saleActiveInfo = new DataMap();
								saleActiveInfo.put("EPARCHY_CODE", route);
								saleActiveInfo.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
								saleActiveInfo.put("%101!",orderInfo.getString("CHANNEL_ID",""));    //业务订购渠道
								saleActiveInfo.put("%103!",subInfo.getString("PRODUCT_ID"));     //本地合约编码
								if(subInfo.getString("ACCEPT_DATE") == null || "".equals(subInfo.getString("ACCEPT_DATE")))
								{
									saleActiveInfo.put("%105!",SysDateMgr.getSysDate()); 
								}
								else
								{
									saleActiveInfo.put("%105!",subInfo.getString("ACCEPT_DATE")); 
								}//合约订购时间
								saleActiveInfo.put("%107!",subInfo.getString("CTRM_PRODUCT_ID"));  //集团的合约编码
								saleActiveInfo.put("PARAM_CODE","ABILITY_CONTRACT"); 
								saleActiveInfo.put("PARA_CODE1",subInfo.getString("PACKAGE_ID"));
								saleActiveInfo.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
								saleActiveInfo.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));
								saleActiveInfo.put(Route.ROUTE_EPARCHY_CODE, route);
								saleActiveInfo.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE"));
								saleActiveInfo.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
								saleActiveInfo.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
								saleActiveInfo.put("PRODUCT_ID", subInfo.getString("PRODUCT_ID"));
								saleActiveInfo.put("PACKAGE_ID", subInfo.getString("PACKAGE_ID"));
								saleActiveInfo.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
								saleActiveInfo.put("ACTION_TYPE", "0");
								saleActiveInfo.put("NO_TRADE_LIMIT", "TRUE");
								saleActiveInfo.put("PID", subInfo.getString("PID"));
							}
							else 
							{
								continue;
							}

						}
						else if ("3".equals(subInfo.getString("CTRM_PRODUCT_TYPE"))) 
						{
							productFlag = true;
							pidList.add(subInfo.getString("PID"));
							
							eleInfo.put("ELEMENT_ID", subInfo.getString("ELEMENT_ID"));
							eleInfo.put("MODIFY_TAG", "0");
							if ("P".equals(subInfo.getString("ELEMENT_TYPE_CODE"))) 
							{
								mainProFlag = true;
								proSmsInfo.put("EPARCHY_CODE", route);
								proSmsInfo.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
								proSmsInfo.put("%101!",orderInfo.getString("CHANNEL_ID")); //业务订购渠道
								proSmsInfo.put("%102!",subInfo.getString("PRODUCT_ID"));       //本地套餐编码
								proSmsInfo.put("%104!",orderInfo.getString("CREATE_TIME",""));   //套餐订购时间
								proSmsInfo.put("%106!",subInfo.getString("CTRM_PRODUCT_ID"));  //集团的产品编码
								proSmsInfo.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
								proSmsInfo.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
								proSmsInfo.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
								proSmsInfo.put("PARAM_CODE","ABILITY_VAS");  
								
								eleInfo.put("ELEMENT_ID", subInfo.getString("PRODUCT_ID"));
								
								if (userProInfo != null && subInfo.getString("PRODUCT_ID","").equals(userProInfo.getString("PRODUCT_ID")) && "10004445".equals(userProInfo.getString("PRODUCT_ID"))) 
								{
									fxproductFlag = true;
									elements = builderElements(orderInfo.getString("PHONE"), subInfo.getString("CTRM_PRODUCT_ID"));
									continue;
								}
								
								if (userProInfo != null && subInfo.getString("PRODUCT_ID","").equals(userProInfo.getString("PRODUCT_ID"))) 
								{
									continue;
								}
							}
							eleInfo.put("ELEMENT_TYPE_CODE", subInfo.getString("ELEMENT_TYPE_CODE"));
							//视频流量包才需要校验    add   
                            productType = subInfo.getString("CTRM_PRODUCT_TYPE_CODE","");
                            if("10200".equals(productType)||"10100".equals(productType))
                            {
                            	try
                            	{
                            		IData productInfo=new DataMap();
                            		productInfo.put("PRODUCT_TYPE", productType);
	                        		productInfo.put("SERVICE_ID_LIST", subInfo.getString("CTRM_PRODUCT_SERVICEID",""));
	                        		productInfo.put("PRODUCE_ID", subInfo.getString("CTRM_PRODUCT_ID",""));
	                        		subInfo.putAll(productInfo);
	                        		if(logger.isDebugEnabled())
	                        		{
	                        			logger.debug("-------subInfo入参--------"+subInfo);	
	                        		}
	                                //校验产品入参之间的关系，productType=102XX
	                                AbilityRuleCheck.checkParamRelation(orderInfo.getString("PHONE",""),subOrderList,route);
	                                //校验互斥关系以及拼数据
	                                IData retData=AbilityRuleCheck.checkVideopckrule(orderInfo.getString("PHONE"), subInfo,route);   
	                                 
	                                if(IDataUtil.isNotEmpty(retData))
	                                { 
	                                	//eleIdList.add(retData);
										eleInfo.putAll(retData);
	                                }
                            	 }
                            	 catch(Exception e)
                            	 {
                            		 IData errorResult = new DataMap();
                                     IDataset result = new DatasetList();
                                     String error = e.getMessage();
                                     errorResult.put("X_RESULTCODE", "1");
                                     errorResult.put("X_RESULTINFO", error);
                                     result.add(errorResult);
                                     logger.debug("-------updateOrderProductStatus--------" + error + " OID=" + orderInfo.getString("OID") + " PID=" + subInfo.getString("PID"));	
                                     //AbilityRuleCheck.updateOrderProductStatus(result,orderInfo.getString("OID"),"");
                            	 }
                            }
                			//视频流量包才需要校验  end 
							eleIdList.add(eleInfo);
							if(logger.isDebugEnabled())
							{
                	        	logger.debug("-------eleIdList--------"+eleIdList);	
                	        }
						}
					}
					
					param.clear();
					
					param.put("ACCEPT_DATE", SysDateMgr.getSysTime());
					param.put("UPDATE_TIME", SysDateMgr.getSysTime());
					param.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
					param.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
					param.put("OID", orderInfo.getString("OID"));
					param.put("TID", tid);
                    logger.error("=================AbilityOpenPlatFormIntfSVC.java==============762 "+fxproductFlag);
                    logger.error("=================AbilityOpenPlatFormIntfSVC.java==============763 "+param);
					IData retnData = new DataMap();
					if(fxproductFlag)
					{
						//elements
						try 
						{// 如果处理失败也更改子订单状态
							IData infoParam = new DataMap();
							infoParam.put("ELEMENTS", elements);
							infoParam.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
							infoParam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
							infoParam.put("KIND_ID", data.getString("KIND_ID"));
							infoParam.put("UNI_CHANNEL", uniChannelId);			//REQ201911120026新增入参UNI_CHANNEL
						    IDataset set  =  CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", infoParam);
							retnData = (set != null && set.size()>0)?set.getData(0):new DataMap();
							tradeIds.add(retnData.getString("TRADE_ID"));
							if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")) && mainProFlag)
		                    {
								proSmsInfo.put("TRADE_ID",retnData.getString("TRADE_ID"));
								QueryInfoUtil.sendSMS(proSmsInfo);//发送短信
		                    }
						} 
						catch (Exception e) 
						{
							data.put("ORDER_ID", "-1");
							data.put("TRADE_ID", "-1");
							data.put("X_RESULTCODE", "-1");
							data.put("X_RESULTINFO", e.getMessage());
							retnData.put("X_CHECK_TAG", "-1");
							retnData.put("X_RESULTINFO", e.getMessage());
							retnData.put("X_RESULTCODE", "-1");
						}
					}
                    logger.error("=================AbilityOpenPlatFormIntfSVC.java==============788 "+productFlag+" "+fxproductFlag);
					if (productFlag&&!fxproductFlag) 
					{
						try 
						{
							// 如果处理失败也更改子订单状态
							IData infoParam = new DataMap();
							infoParam.put("ELEMENTS", eleIdList);
							infoParam.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
							infoParam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
							infoParam.put("KIND_ID", data.getString("KIND_ID"));

							
							//lisw3
							infoParam.put("NEED_CHANNEL_TAG", "ABILITY");//销售订单接口标记//addbylisw320190712
							infoParam.put("CHANNEL_ID", orderInfo.getString("CHANNEL_ID"));//渠道编码addbylisw320190712
							infoParam.put("OID", orderInfo.getString("OID"));//addbylisw320190712
							infoParam.put("TID", tid);//addbylisw320190712

							infoParam.put("SKIP_LOCK", "true");
							infoParam.put("UNI_CHANNEL", uniChannelId);			//REQ201911120026新增入参UNI_CHANNEL
							if(logger.isDebugEnabled())
							{
                	        	logger.debug("-------产品变更入参infoParam--------"+infoParam);	
                	        }
							logger.error("====AbilityOpenPlatFormIntfSVC.java=====产品变更入参infoParam=====999"+infoParam);
						    IDataset set  =  CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", infoParam);
							retnData = (set != null && set.size()>0)?set.getData(0):new DataMap();
							tradeIds.add(retnData.getString("TRADE_ID"));
							if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")) && mainProFlag)
		                    {
								proSmsInfo.put("TRADE_ID",retnData.getString("TRADE_ID"));
								QueryInfoUtil.sendSMS(proSmsInfo);//发送短信
		                    }
						} 
						catch (Exception e) 
						{
							if(logger.isDebugEnabled())
							{
                	        	logger.debug("-------产品变更报错了--------"+e.getMessage());	
                	        }
							data.put("ORDER_ID", "-1");
							data.put("TRADE_ID", "-1");
							data.put("X_RESULTCODE", "-1");
							data.put("X_RESULTINFO", e.getMessage());
							retnData.put("X_CHECK_TAG", "-1");
							retnData.put("X_RESULTINFO", e.getMessage());
							retnData.put("X_RESULTCODE", "-1");
						}
					}
					logger.error("=================AbilityOpenPlatFormIntfSVC.java==============817 "+saleActiveInfo);
					//营销活动放到产品变更后面执行
					if (saleActiveInfo != null) 
					{

						IData cData = new DataMap();
						cData.put("STATUS", "2");
						cData.put("ACCEPT_RESULT", "1");
						IData contractData = new DataMap();
						try 
						{
							saleActiveInfo.put("UNI_CHANNEL", uniChannelId);			//REQ201911120026新增入参UNI_CHANNEL
							IDataset retMap = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf",saleActiveInfo);
							contractData = (retMap != null && retMap.size() > 0) ? retMap.getData(0):new DataMap();
							tradeIds.add(contractData.getString("TRADE_ID"));
							if (StringUtils.isNotBlank(contractData.getString("ORDER_ID")) && !"-1".equals(contractData.getString("ORDER_ID")))
		                    {
								data.put("SA_ORDER_ID", contractData.getString("ORDER_ID"));
								data.put("SA_TRADE_ID", contractData.getString("TRADE_ID"));
								data.put("SA_X_RESULTCODE", "0");
								data.put("SA_X_RESULTINFO", "OK");
								cData.put("STATUS", "1");
								saleActiveInfo.put("TRADE_ID",contractData.getString("TRADE_ID"));
								QueryInfoUtil.sendSMS(saleActiveInfo);//发送短信
		                    }
						} 
						catch (Exception e) 
						{
							data.put("SA_ORDER_ID", "-1");
							data.put("SA_TRADE_ID", "-1");
							data.put("SA_X_RESULTCODE", "-1");
							data.put("SA_X_RESULTINFO", e.getMessage());
							String msg = e.getMessage() == null? "":e.getMessage();
							msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
							cData.put("REMARK", msg);
						}
						
						cData.put("TRADE_ID", contractData.getString("TRADE_ID", "-1"));
						cData.put("PID", saleActiveInfo.getString("PID"));
						cData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
						cData.put("UPDATE_TIME", SysDateMgr.getSysTime());
						cData.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
						cData.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
						productParams.add(cData);
					}
					
					// 如果没有合约产品且子订单都登记成功则修改子订单状态
					if (!hasVasFlag) 
					{
						param.put("ORDER_STATUS", "07");
						param.put("ACCEPT_RESULT", "1");

						if (productFlag && (StringUtils.isBlank(retnData.getString("ORDER_ID")) || "-1".equals(retnData.getString("ORDER_ID")))) 
						{
							param.put("ORDER_STATUS", "90");
							param.put("ACCEPT_RESULT", "2");
						}
						bean.updateInfoForTid(param);
					}
					String retnCode = "2";
					
					if ( productFlag && StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID"))) 
					{
						retnCode = "1";
						data.put("ORDER_ID", retnData.getString("ORDER_ID"));
						data.put("TRADE_ID", retnData.getString("TRADE_ID"));
						data.put("X_RESULTCODE", "0");
						data.put("X_RESULTINFO", "OK");
					}
					String msg = retnData.getString("X_RESULTINFO", "");
					msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
					logger.error("=================AbilityOpenPlatFormIntfSVC.java==============871 "+pidList);
					// 更新子订单产品记录表
					for (int k = 0; k < pidList.size(); k++) 
					{
						String pid = pidList.get(k);
						IData inputData = new DataMap();
						inputData.put("PID", pid);
						inputData.put("TRADE_ID", retnData.getString("TRADE_ID", "-1"));
						if(!"2".equals(strStatus))
						{
							inputData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
						}
						else
						{
							inputData.put("ACCEPT_DATE", strAcceptDate);
						}
						inputData.put("STATUS", retnCode);
						inputData.put("ACCEPT_RESULT", retnCode);
						inputData.put("REMARK", msg);
						inputData.put("UPDATE_TIME", SysDateMgr.getSysTime());
						inputData.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
						inputData.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
						productParams.add(inputData);
					}
	                
					logger.error("=================AbilityOpenPlatFormIntfSVC.java==============887 " + productParams);
					if (productParams != null && productParams.size() > 0)
					{
						bean.updateBatchInfo("TF_B_CTRM_TLIST", "UPD_CTRM_ORDER_PRODUCT", productParams);
					}
				}
			}
		}
		data.put("TRADE_ID_S", tradeIds);
	}

	/**
	 * 订单状态同步接口
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData orderStatusSynchro(IData data) throws Exception {

		if ("".equals(data.getString("TID", ""))) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1146);
		}
		if ("".equals(data.getString("OID", ""))) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1147);
		}

		AbilityOpenPlatformIntfBean bean = BeanManager.createBean(AbilityOpenPlatformIntfBean.class);
		IDataset result = new DatasetList();

		String oldIMEI = "";
		
		IData param = new DataMap();
		// 根据TID和OID查询对应的子订单同步信息,主要的用途是用来获取用户的手机号码
		param.put("TID", data.getString("TID"));
		param.put("OID", data.getString("OID"));
		IDataset orderInfo = QueryListInfo.queryListInfoForOrderTlist(param);
		
		if (orderInfo.size() <= 0) {
			String errors = "订单ID【" + data.getString("TID") + "】,子订单ID【"
					+ data.getString("OID") + "】不存在同步订单记录！";
			CSAppException.appError("", errors);
		}

		// 订单是否需要省公司配送
		data.put("SERIAL_NUMBER", orderInfo.getData(0).getString("PHONE"));

		// 根据TID和OID查询对应的子订单产品同步信息
		param.put("CTRM_PRODUCT_TYPE", "2");
		IDataset orderProductInfos = QueryListInfo.queryListInfoForProorderTlist(param);

		IData orderProductInfo = new DataMap();

		for (int i = 0; i < orderProductInfos.size(); i++) {
			orderProductInfo = orderProductInfos.getData(i);
			// 处理合约 调用更换IMEI接口 OPT_TYPE:1-绑定IMEI；2-更换IMEI
			if ("2".equals(data.getString("OPT_TYPE", ""))
					&& !"".equals(data.getString("IMEI", ""))
					&& "1".equals(orderInfo.getData(0).getString("DISTRIBUTION"))) {
				// 20140906 更换终端 未完成 参数有问题
				// executeExchangeTrade(data);
				String newImei = data.getString("IMEI","");
				
		       CSAppCall.call("SS.ModifySaleActiveIMEIRegSVC.tradeReg", data);
			}
		}
		
		param.clear();
		param.put("ORDER_STATUS", data.getString("STATUS"));
		param.put("UPDATE_TIME", SysDateMgr.getSysTime());
		param.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
		param.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
		param.put("OID", data.getString("OID"));
		param.put("TID", data.getString("TID"));
		param.put("RSRV_STR1", orderInfo.getData(0).getString("RSRV_STR1"));
		param.put("RSRV_STR2", orderInfo.getData(0).getString("RSRV_STR2"));
		
		oldIMEI = orderInfo.getData(0).getString("RSRV_STR1");
		//绑定IMEI 插RSRV_STR1保存最新的IMEI号  RSRV_STR2保存旧的IMEI号
		if ("1".equals(data.getString("OPT_TYPE",""))&& !"".equals(data.getString("IMEI",""))) {
			param.put("RSRV_STR1", data.getString("IMEI"));
			data.put("PARAM_CODE", "ADD_IMEI");
		}else if("2".equals(data.getString("OPT_TYPE",""))&& !"".equals(data.getString("IMEI",""))) {
			param.put("RSRV_STR1", data.getString("IMEI"));
			param.put("RSRV_STR2", orderInfo.getData(0).getString("RSRV_STR1"));
			data.put("PARAM_CODE", "MOD_IMEI");
		}
		
		bean.updateInfoById(param);
		
		//把同步过来的IMEI保存到TF_F_USER_IMEI表中
		if (!"".equals(data.getString("IMEI",""))) {
			param.clear();
			IData userinfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));
			//把用户旧的IMEI终止
			param.put("USER_ID", userinfo.getString("USER_ID"));
			param.put("END_DATE", SysDateMgr.getSysTime());
			QueryInfoUtil.updateUserIMEI(param, userinfo.getString("EPARCHY_CODE"));
			
			//插入新的IMEI
			param.clear();
			param.put("USER_ID", userinfo.getString("USER_ID"));
			param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
			param.put("IMEI", data.getString("IMEI"));
			param.put("START_DATE", SysDateMgr.getSysTime());
			param.put("END_DATE", SysDateMgr.getTheLastTime());
			QueryInfoUtil.insertUserIMEI(param, userinfo.getString("EPARCHY_CODE"));
			
			data.put("%101!", data.getString("CHANNEL_ID",""));
			data.put("%108!", data.getString("IMEI"));
			data.put("%109!", oldIMEI);
			data.put("EPARCHY_CODE", userinfo.getString("EPARCHY_CODE"));
			QueryInfoUtil.sendSMS(data);
		}

		return null;
	}

	/**
	 * 退款接口ITF_CRM_RefundOrderCheck 目前只支持合约计划的退款
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData refundOrderCheck(IData data) throws Exception {

		AbilityOpenPlatformIntfBean bean = BeanManager
				.createBean(AbilityOpenPlatformIntfBean.class);
		IData retnMap = new DataMap();
		retnMap.put("OPR_NUMB", data.getString("OPR_NUMB"));

		chkParamNoStr(data, "CHANNEL_ID", "802127");
		chkParamNoStr(data, "TID", "802127");
		chkParamNoStr(data, "OID", "802127");
		chkParamNoStr(data, "OPR_NUMB", "802127");

		IData param = new DataMap();
		param.put("TID", data.getString("TID"));
		param.put("OID", data.getString("OID"));

		IDataset oidset = QueryListInfo.queryListInfoForOrderTlist(param);
		
		

		if (oidset == null || oidset.size() <= 0) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1148);
		}

		param.clear();
		param.put("TID", data.getString("TID"));
		param.put("OID", data.getString("OID"));
		param.put("CTRM_PRODUCT_TYPE", "2");
		String tradeId = QueryListInfo.queryColumnValueByType(param, "TRADE_ID");

		String stauts = QueryListInfo.queryColumnValueByType(param, "STATUS");

		if (stauts == null || "0".equals(stauts) || "2".equals(tradeId)) {

			retnMap.put("IS_UNSUBSUCCESS", "1");
			return retnMap;
		}

		if (tradeId == null || "".equals(tradeId) || "-1".equals(tradeId)) {

			retnMap.put("IS_UNSUBSUCCESS", "3");
			retnMap.put("UNSUB_COMMENT", "该订单不能退订！");
			return retnMap;
		}
		param.clear();
		param.put("TRADE_ID", tradeId);
		param.put("MODIFY_TAG", "0");
		String actionCode = QueryListInfo.queryColumnValueById(param, "CAMPN_ID");

		data.put("SERIAL_NUMBER", oidset.getData(0).getString("PHONE"));
		// bean.getTradeData(data);
		param.clear();
		param.put("ACTION_CODE", actionCode);
		param.put("TRADE_ID", tradeId);
		param.put("IDTYPE", "1");
		param.put("IDVALUE", oidset.getData(0).getString("PHONE"));
		param.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
		param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
		param.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE"));
		param.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
		param.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
		
		param.put("SERIAL_NUMBER", oidset.getData(0).getString("PHONE"));//新增参数,因为调用账管接口SERIAL_NUMBER不能为空, duhj

		// 调账务接口查询营销活动剩余金额 20140906
		// IData intfMap = (IData)HttpHelper.callHttpSvc( "QAM_CRM_RETURNFREE",
		// param,true);
		IDataset intfMap = CSAppCall.call("AM_CRM_GetDiscntLeftFee", param);

		if (intfMap != null
				&& "0".equals(((IData) intfMap.get(0))
						.getString("X_RESULTCODE"))) {

			retnMap.put("UNSUB_COMMENT", ((IData) intfMap.get(0))
					.getString("X_RESULTINFO"));
			retnMap.put("RETURN_FEE", ((IData) intfMap.get(0))
					.getString("LEFT_MONEY"));

			float leftMoney = Float.valueOf(((IData) intfMap.get(0)).getString(
					"LEFT_MONEY", "0"));
			float money = Float.valueOf(((IData) intfMap.get(0)).getString(
					"MONEY", "0"));
			if (Float.floatToRawIntBits(leftMoney) == 0 && Float.floatToRawIntBits(money) == 0) {
				retnMap.put("IS_UNSUBSUCCESS", "1");
			} else if (Float.floatToRawIntBits(leftMoney) == 0 && money > 0) {
				retnMap.put("IS_UNSUBSUCCESS", "3");
				retnMap.put("UNSUB_COMMENT", "合约已到期");
			} else {
				if (leftMoney < money) {
					retnMap.put("IS_UNSUBSUCCESS", "2");
				} else if (Float.floatToRawIntBits(leftMoney) == Float.floatToRawIntBits(money)) {
					retnMap.put("IS_UNSUBSUCCESS", "1");
				}
			}
		} else {
			retnMap.put("IS_UNSUBSUCCESS", "3");
			retnMap.put("UNSUB_COMMENT", ((IData) intfMap.get(0))
					.getString("X_RESULTINFO"));
		}

		return retnMap;
	}

	/**
	 * 退款订单同步接口
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData refundOrderSynchro(IData data) throws Exception {
		AbilityOpenPlatformIntfBean bean = BeanManager
				.createBean(AbilityOpenPlatformIntfBean.class);

		chkParamNoStr(data, "CHANNEL_ID", "802127");
		chkParamNoStr(data, "REFUND_ID", "802127");
		chkParamNoStr(data, "REFUND_TYPE", "802127");
		chkParamNoStr(data, "OUT_REFUND_ID", "802127");
		chkParamNoStr(data, "TID", "802127");
		chkParamNoStr(data, "TRADE_MONEY", "802127");
		chkParamNoStr(data, "REFUND_TIME", "802127");
		chkParamNoStr(data, "REFUND_MONEY", "802127");

		bean.refundOrderSynchro(data);
		return null;
	}

	/** *********************************************************************************************** */
	/**
	 * 校验传入在是否为空
	 * 
	 * @param data
	 * @param keys
	 * @throws Exception
	 */
	public void chkParamNoStr(IData data, String keys, String errorCode)
			throws Exception {
		String key = data.getString(keys, "");
		if ("".equals(key)) {
			CSAppException.appError(errorCode, "传入在字段" + keys + "值不能为空！");
		}
	}

	/**
	 * 通过关键字获取Str 特别解析 [["", ""]]的字符串到 IDataset
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @author hud
	 */
	public static IDataset getIDatasetSpecl(String key, String str)
			throws Exception {
		IDataset dataset = new DatasetList();
		str = str.replace(",", "rex");
		if (str.startsWith("[") && str.endsWith("]")) {
			str = str.replaceAll("\"", "");
			String[] param = str.substring(2, str.length() - 2).split("rex");
			for (int i = 0; i < param.length; i++) {
				IData data = new DataMap();
				data.put(key, param[i]);
				dataset.add(data);
				data = null;
			}
		} else {
			CSAppException.apperr(CrmCommException.CRM_COMM_648);

		}
		return dataset;
	}

	public String getMofficeBySN(String serialNumber) throws Exception {
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);

		IDataset tmp = UserInfoQry.getMofficeBySN(data);
		if (IDataUtil.isNotEmpty(tmp)) {
			IData data2 = tmp.getData(0);
			return data2.getString("EPARCHY_CODE");
		} else {// 携转号码无moffice信息
			IDataset out = TradeNpQry.getValidTradeNpBySn(serialNumber);
			if (IDataUtil.isNotEmpty(out)) {
				return out.getData(0).getString("AREA_CODE");
			} else {
				return null;
			}
		}
	}
	
	/***********************************************************************************
     * 针对飞享套餐特殊处理<BR/>
     * 飞享套餐传入的产品ID为"qwc + 数字"，需要转换为省内产品编码<BR/>
     * 1.如果用户主产品和转换后的产品不一致，那么需要进行主产品变更<BR/>
     * 2.如果用户主产品和转换后的产品一致，只需要进行产品内元素变更<BR/>
     * 
     * @param serialNumber	用户号码
     * @param newProductID	变更产品
     * @return
     * @throws Exception
     */
    private IDataset builderElements(String serialNumber, String newProductID)throws Exception{
		//1.根据用户号码查询用户信息
        IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        if(IDataUtil.isEmpty(userInfos))
        	CSAppException.apperr(CrmUserException.CRM_USER_1);
        //2.查询用户主产品信息
        String userID = userInfos.getData(0).getString("USER_ID");
        IDataset userMainProduct = UserProductInfoQry.queryUserMainProduct(userID);
        if(IDataUtil.isEmpty(userMainProduct))
        	CSAppException.apperr(CrmUserException.CRM_USER_45, userID);
        //3.对传入的产品进行转换
		IDataset configElementList = qryConfigElements(newProductID, userInfos.getData(0).getString("EPARCHY_CODE"));
		String productID = configElementList.getData(0).getString("PRODUCT_ID");
		
		//4.1.如果是主产品变更，需要设置参数进行主产品变更
		if(! productID.equals(userMainProduct.getData(0).getString("PRODUCT_ID"))){
			IData item = new DataMap();
			item.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			item.put("ELEMENT_ID", productID);
			item.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
			configElementList.add(item);
			
		//4.2.如果是元素变更，需要删除原产品下指定优惠
		}else{
			//4.2.1.查询用户已订购的飞享套餐优惠
			IDataset orderDiscnt = UserDiscntInfoQry.getFXDiscntByUserId(userID);
			if(IDataUtil.isEmpty(orderDiscnt))
				CSAppException.apperr(CrmUserException.CRM_USER_914, productID);
			//4.2.2.处理相同的元素：如果用户已订购了变更后套餐的元素，不用给予删除、添加操作
			dealDuplicateElements(configElementList, orderDiscnt);
			if(IDataUtil.isEmpty(configElementList))
				return configElementList;
			//4.2.2.退订这些优惠
			for(int i = 0; i < orderDiscnt.size(); i++){
				//如果是GPRS优惠，不能退订
				if(IDataUtil.isNotEmpty(DiscntInfoQry.getDiscntIsValid("5", orderDiscnt.getData(i).getString("DISCNT_CODE"))))
					continue;
				IData item = new DataMap();
				item.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
				item.put("ELEMENT_ID", orderDiscnt.getData(i).getString("DISCNT_CODE"));
				item.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
				item.put("INST_ID", orderDiscnt.getData(i).getString("INST_ID"));
				configElementList.add(item);
			}
		}
		return configElementList;
	}
	/************************************************************************************
	 * 将传入的产品编码转换为省内的产品编码<BR/>
	 * 使用TD_S_COMMPARA表PARAM_ATTR为2801配置进行产品转换<BR/>
	 * 
	 * @param newProductID	转换前的产品编码
	 * @param eparchyCode	地州
	 * @return
	 * @throws Exception
	 */
	private IDataset qryConfigElements(String newProductID, String eparchyCode)throws Exception{
		//1.查询产品转换关系
		IDataset configElementList = CommparaInfoQry.getCommPkInfo("CSM", "2801", newProductID, eparchyCode);
		//2.没有查询到转换关系，抛出异常
		if(configElementList.isEmpty())
			CSAppException.apperr(ParamException.CRM_PARAM_359);
		
		IData configElement = configElementList.getData(0);
		String countStr = configElement.getString("PARA_CODE2");//必选元素个数
		//3.如果【必选元素个数】，不在5个内，配置已经有问题，抛出异常
		if(! countStr.matches("[1-5]"))
			CSAppException.apperr(ParamException.CRM_PARAM_145);
		String productID = configElement.getString("PARA_CODE1"), elementItem = null;
		//4.解析配置元素信息：ELEMENT_ID + '_' + ELEMENT_TYPE_CODE
		IDataset result = new DatasetList();
		IData item = null;
		for(int i = 0, len = Integer.parseInt(countStr); i < len; i++){
			elementItem = configElement.getString("PARA_CODE" + (i + 3));//取 PARA_CODE3（包括在内）后的PARA_CODE2个元素
			String[] elementInfo = elementItem.split("_");
			if(elementInfo.length != 2)
				CSAppException.apperr(ParamException.CRM_PARAM_146);
				
			item = new DataMap();
			item.put("PRODUCT_ID", productID);
			item.put("ELEMENT_TYPE_CODE", elementInfo[1]);
			item.put("ELEMENT_ID", elementInfo[0]);
			item.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
			result.add(item);
		}
		return result;
	}
	/*****************************************************************************
	 * 去重：新增列表和删除列表中相同的元素<BR/>
	 * @param addList	新增列表
	 * @param delList	删除列表
	 */
	private void dealDuplicateElements(IDataset addList, IDataset delList){
		if(IDataUtil.isEmpty(addList) || IDataUtil.isEmpty(delList))
			return;
		
		for(int i = 0; i < addList.size(); i++){
			String addTypeCode = addList.getData(i).getString("ELEMENT_TYPE_CODE", ""), addID = addList.getData(i).getString("ELEMENT_ID", "");
			
			for(int j = 0; j < delList.size(); j++){
				if(addTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT) && addID.equals(delList.getData(j).getString("DISCNT_CODE"))){
					addList.remove(i--);
					delList.remove(j--);
				}
			}
		}
	}

}
