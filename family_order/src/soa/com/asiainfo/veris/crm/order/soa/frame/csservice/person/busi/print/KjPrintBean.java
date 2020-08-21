package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print;

import com.ailk.common.config.GlobalCfg;
import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PrintException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ForeGiftInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.PayMentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;

/**
 * 
 * Bean: 电子发票开具
 * 
 */
public class KjPrintBean extends CSBizBean
{

	private static transient Logger logger = Logger.getLogger(KjPrintBean.class);

	private static final String tax_hid_char = "0";

	/**
	 * 打印电子发票
	 * 
	 * @param printData
	 * @return
	 * @throws Exception
	 */
	public IDataset printKJ(IData printData) throws Exception
	{
		IData inParam = new DataMap();

		String tradeId = printData.getString("TRADE_ID", "");
		IDataset mainTradeInfos = TradeInfoQry.getTradeAndBHTradeByTradeId(tradeId);
		if (null == mainTradeInfos || mainTradeInfos.isEmpty())
		{
			CSAppException.apperr(PrintException.CRM_PRINT_16);
		}
		IData mainTradeInfo = mainTradeInfos.getData(0);

		// 台账信息
		inParam.put("TRADE_ID", tradeId);
		inParam.put("ACCEPT_MONTH", mainTradeInfo.getString("ACCEPT_MONTH"));
		inParam.put("TRADE_DATE", mainTradeInfo.getString("ACCEPT_DATE"));
		inParam.put("TRADE_STAFF_ID", mainTradeInfo.getString("TRADE_STAFF_ID"));
		inParam.put("TRADE_DEPART_ID", mainTradeInfo.getString("TRADE_DEPART_ID"));
		inParam.put("TRADE_CITY_CODE", mainTradeInfo.getString("TRADE_CITY_CODE"));
		String routeId = mainTradeInfo.getString("TRADE_EPARCHY_CODE");
		String tradeTypeCode = mainTradeInfo.getString("TRADE_TYPE_CODE");

		/*
		 * 判断是否为开户业务
		 */
		IData input = new DataMap();
		inParam.put("NEW_FLAG", "1"); // 默认非新开户
		input.put("PARAM_CODE", "IS_NEW_USER");
		input.put("PARA_CODE1", tradeTypeCode);
		input.put("EPARCHY_CODE", "ZZZZ");
		if (IDataUtil.isNotEmpty(getOtherTradeParam(input)))
		{ // 新开户
			inParam.put("NEW_FLAG", "0");
		}
		inParam.put("TRADE_EPARCHY_CODE", routeId);
		String custType = printData.getString("CUST_TYPE", "");
		String USER_ID = mainTradeInfo.getString("USER_ID", "");

		inParam.put("SERIAL_NUMBER", mainTradeInfo.getString("SERIAL_NUMBER"));
		inParam.put("DSPTBM", "13310101"); // 平台编码(取渠道)
		inParam.put("IN_MODE_CODE", mainTradeInfo.getString("IN_MODE_CODE")); // 平台编码(取渠道)

		String chanlId = printData.getString("APPLY_CHANNEL", "0"); // 开票发起渠道：：0-营业个人业务;1-集团有ACCTID业务；2-集团无ACCTID业务；3-账务
		inParam.put("APPLY_CHANNEL", chanlId);

		String acctId = mainTradeInfo.getString("ACCT_ID");

		if ("1".equals(chanlId) && StringUtils.isBlank(acctId))
		{
			chanlId = "2";
			inParam.put("APPLY_CHANNEL", "2"); // 如集团营业费用一次性费用收取，收现金不与产品和账户挂钩的情形
		}

		inParam.put("DKBZ", "0"); // 1、 自开(0) 2、 代开(1)
		inParam.put("BMB_BBH", "1.0"); // 编码表版本号

		// 发票信息
		String assembFlag = "";
		String containOperFlag = printData.getString("MERGE_FEE_LIST_OPER_FLAG");
		assembFlag += "1".equals(containOperFlag) ? "1" : "0".equals(containOperFlag) ? "0" : "X";
		String containForegiftFlag = printData.getString("MERGE_FEE_LIST_FOREGIFT_FLAG");
		assembFlag += "1".equals(containForegiftFlag) ? "1" : "0".equals(containForegiftFlag) ? "0" : "X";
		String containAdvanceFlag = printData.getString("MERGE_FEE_LIST_ADVANCE_FLAG");
		assembFlag += "1".equals(containAdvanceFlag) ? "1" : "0".equals(containAdvanceFlag) ? "0" : "X";
		inParam.put("ASSEMB_FLAG", assembFlag);
		inParam.put("ORDER_ID", "".equals(printData.getString("ORDER_ID", "")) ? mainTradeInfo.getString("ORDER_ID") : printData.getString("ORDER_ID"));
		inParam.put("TYPE", printData.getString("TYPE")); // 票据类型：
		// 发票P0001、收据P0002、免填单(业务受理单)P0003
		inParam.put("PRINT_ID_BAK", printData.getString("PRINT_ID", ""));// 打印数据时的数据源备案日志ID
		// int totalFee = (0 == printData.getInt("TOTAL_MONEY", 0)) ?
		// printData.getInt("TOTAL_FEE", 0) : printData.getInt("TOTAL_MONEY",
		// 0);
		// inParam.put("TOTAL_FEE", totalFee); //发票的总额
		inParam.put("NOTE_NO", printData.getString("NOTE_NO")); // 票号
		inParam.put("TAX_NO", printData.getString("TAX_NO")); // 税务号(单联票输入的两个参数)
		inParam.put("PAY_NAME", "".equals(printData.getString("PAY_NAME", "")) ? mainTradeInfo.get("CUST_NAME") : printData.getString("PAY_NAME"));

		// 判断是否为特殊业务打印（无三户资料）2016/9/23
		String eflag = printData.getString("EFLAG");
		String postEmail = printData.getString("POST_EMAIL");
		if ("Y".equals(eflag))
		{// 特殊业务--无三户资料
			inParam.put("NEW_FLAG", "2");
			inParam.put("EMAIL", postEmail);
			inParam.put("RSRV_INFO2", "0"); // 0：无资料业务
			if (StringUtils.isBlank(mainTradeInfo.getString("SERIAL_NUMBER")))
			{
				inParam.put("PSPT_ID", mainTradeInfo.getString("ORDER_ID", tradeId)); // 0：无资料业务
			} else
			{
				inParam.put("PSPT_ID", mainTradeInfo.getString("SERIAL_NUMBER")); // 0：无资料业务
			}
			inParam.put("EPARCHY_CODE", "0898");
		} else
		{// 正常业务
			inParam.put("PARTITION_ID", USER_ID.substring(USER_ID.length() - 4));
			inParam.put("USER_ID", USER_ID);
			inParam.put("EPARCHY_CODE", mainTradeInfo.getString("EPARCHY_CODE", routeId));
			if (!"GRP".equals(custType))
			{
				inParam.put("ACCT_ID", mainTradeInfo.getString("ACCT_ID"));
				inParam.put("CUST_ID", mainTradeInfo.getString("CUST_ID"));
			}
		}

		// 推送信息
		inParam.put("PRINT_FLAG", "0"); // 0，正常发票；1冲红发票
		inParam.put("POST_TAG", printData.getString("RECEIVER_SENDWAY")); // 邮寄方式，0手机，1邮箱，2短信+邮箱
		// //从用户当前配置中读取
		inParam.put("POST_MOBILE", printData.getString("RECEIVER_MOBILE")); // 邮寄邮箱
		inParam.put("POST_EMAIL", printData.getString("RECEIVER_EMAIL")); // 邮寄手机号

		String inDate = SysDateMgr.getSysTime();
		inParam.put("IN_DATE", inDate);
		inParam.put("IN_STAFF_ID", getVisit().getStaffId());
		inParam.put("IN_CITY_CODE", getVisit().getCityCode());
		inParam.put("IN_DEPART_ID", getVisit().getDepartId());

		inParam.put("KPXM", printData.getString("NAME", "发票")); // 主要开票项目(取发票的名称)

		// 销售方信息
		inParam.put("XHF_NSRSBH", ""); // 销售方的编码 暂时以营业厅编码
		inParam.put("XHFMC", "".equals(printData.getString("DEPT_NAME", "")) ? StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", mainTradeInfo.getString("TRADE_DEPART_ID")) : printData.getString("DEPT_NAME", "")); // 销货方名称
		inParam.put("XHF_DZ", ""); // 销货方地址
		inParam.put("XHF_DH", ""); // 销货方电话 固定电话
		inParam.put("XHF_YHZH", ""); // 销货方银行账号

		// 购货方信息(从客户资料或者台账资料里获取)
		inParam.put("GHF_NSRSBH", ""); // 购货方识别号
		inParam.put("GHFMC", "".equals(printData.getString("CUST_NAME", "")) ? mainTradeInfo.getString("CUST_NAME", "") : printData.getString("CUST_NAME", "")); // 购货方名称

		if ("416".equals(tradeTypeCode) && StringUtils.isBlank(inParam.getString("GHFMC")))
		{
			inParam.put("GHFMC", "有价卡销售");
		}

		inParam.put("GHF_DZ", ""); // 购货方地址
		inParam.put("GHF_SF", ""); // 购货方省份
		inParam.put("GHF_GDDH", mainTradeInfo.getString("SERIAL_NUMBER")); // 购货方固定电话
		inParam.put("GHF_SJ", ""); // 购货方手机
		inParam.put("GHF_SJ", ""); // 购货方邮箱
		inParam.put("GHFQYLX", "01"); // 购货方企业类型
		inParam.put("GHF_YHZH", ""); // 购货方银行账号

		// ? inParam.put("HY_DM", printData.getString("HY_DM")); //行业代码
		// ? inParam.put("HY_MC", printData.getString("HY_MC")); //行业名称
		inParam.put("KPY", getVisit().getStaffName()); // 开票员
		inParam.put("SKY", StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", mainTradeInfo.getString("TRADE_STAFF_ID"))); // 收款员
		inParam.put("FHR", getVisit().getStaffName()); // 复核人
		inParam.put("KPRQ", inDate); // 开票日期yyyy-MM-dd HH:mm:ss
		inParam.put("KPRQ_YEAR", inDate.substring(0, 4));// 开票日期yyyy-MM-dd
		// HH:mm:ss
		inParam.put("KPRQ_MONTH", inDate.substring(5, 7)); // 开票日期
		inParam.put("KPRQ_DAY", inDate.substring(8, 9)); // 开票日期
		inParam.put("KPLX", "1"); // 开票类型
		inParam.put("CZDM", "10"); // 操作代码： 10正票正常开具

		// 项目明细列表
		IDataset mergeFeeList = generFeeListTax(printData, tradeId, tradeTypeCode, routeId);
		boolean isShowFeeTax = printData.getBoolean("IS_SHOW_FEE_TAX");
		IDataset XM_DETAIL_S = new DatasetList();
		inParam.put("XM_DETAIL", XM_DETAIL_S);
		for (int z = 0; z < mergeFeeList.size(); z++)
		{
			IData mergeFeeData = mergeFeeList.getData(z);
			IData XM_DETAIL = new DataMap();
			XM_DETAIL_S.add(XM_DETAIL);
			XM_DETAIL.put("FPHXZ", "0"); // 发票行性质
			XM_DETAIL.put("XMMC", mergeFeeData.getString("FEE_TYPE_DES", "营业费用")); // 项目名称
			inParam.put("KPXM", mergeFeeData.getString("FEE_TYPE_DES", "营业费用")); // 主要开票项目(取发票的名称)
			XM_DETAIL.put("XMDW", mergeFeeData.getString("XMDW", "项")); // 项目单位
			XM_DETAIL.put("GGXH", mergeFeeData.getString("GGXH", "项")); // 规格型号
			XM_DETAIL.put("XMSL", mergeFeeData.getString("XMSL", "1")); // 项目数量
			XM_DETAIL.put("HSBZ", mergeFeeData.getString("HSBZ", "1")); // 含税标志
			XM_DETAIL.put("FPHXZ", "0"); // 发票性质
			XM_DETAIL.put("XMDJ", mergeFeeData.getString("FEE", "0")); // 项目单价
			XM_DETAIL.put("SPBM", mergeFeeData.getString("FEE_TYPE_CODE")); // 商品编码
			XM_DETAIL.put("ZXBM", ""); // 自行编码
			XM_DETAIL.put("YHZCBS", mergeFeeData.getString("YHZCBS", "0")); // 优惠政策标识
			XM_DETAIL.put("LSLBS", mergeFeeData.getString("LSLBS", "3")); // 零税率标识
			XM_DETAIL.put("ZZSTSGL", ""); // 增值税特殊处理
			XM_DETAIL.put("XMJE", mergeFeeData.getString("FEE", "0")); // 项目金额
			XM_DETAIL.put("SL", isShowFeeTax ? mergeFeeData.getString("FEE_TAX") : tax_hid_char); // 税率
			XM_DETAIL.put("SE", isShowFeeTax ? mergeFeeData.getString("FEE_TAX_MONEY") : tax_hid_char); // 税额
			XM_DETAIL.put("REMARK", mergeFeeData.getString("REMARK")); // 备注
		}

		// 获取发票对应的类型
		int TOTAL_MONEY = printData.getInt("TOTAL_FEE_MONEY_CHECK", 0);// 这个不准确，是总的费用，不是每个发票的费用int
		// totalFee
		// = (0
		// ==
		// printData.getInt("TOTAL_MONEY",
		// 0)) ?
		// printData.getInt("TOTAL_FEE",
		// 0) :
		// printData.getInt("TOTAL_MONEY",
		// 0);
		inParam.put("TOTAL_FEE", TOTAL_MONEY); // 发票的总额
		int TOTAL_FEE_TAX_MONEY = printData.getInt("TOTAL_FEE_TAX_MONEY", 0);
		int EXCLUDE_TAX_MONEY = TOTAL_MONEY - TOTAL_FEE_TAX_MONEY;
		inParam.put("KPHJJE", TOTAL_MONEY); // 开票合计金额：价税合计金额
		inParam.put("HJBHSJE", isShowFeeTax ? EXCLUDE_TAX_MONEY : TOTAL_MONEY); // 合计不含税金额
		inParam.put("HJSE", isShowFeeTax ? printData.get("TOTAL_FEE_TAX_MONEY") : tax_hid_char); // 合计税额
		inParam.put("KPSL", isShowFeeTax ? printData.get("FEE_TAX_RATE") : tax_hid_char); // 开票税率

		// 订单信息
		inParam.put("DDH", inParam.getString("ORDER_ID")); // 订单号
		inParam.put("THDH", printData.getString("THDH")); // 退货单号
		inParam.put("DDDATE", mainTradeInfo.getString("ACCEPT_DATE")); // 订单时间

		// 订单明细列表
		// IDataset ORDER_DETAIL_S = new DatasetList();
		// inParam.put("ORDER_DETAIL", ORDER_DETAIL_S);
		// IData ORDER_DETAIL = new DataMap();
		// ORDER_DETAIL_S.add(ORDER_DETAIL);
		// ORDER_DETAIL.put("DDH_MX", printData.getString("DDH_MX")); //订单明细信息编号
		// ORDER_DETAIL.put("DDMC", printData.getString("DDMC")); //订单明细信息 名称
		// ORDER_DETAIL.put("DW", printData.getString("DW")); //订单明细信息 单位
		// ORDER_DETAIL.put("GGXH", printData.getString("GGXH")); //订单明细信息 规格型号
		// ORDER_DETAIL.put("SL", printData.getString("SL")); //订单明细信息 数量
		// ORDER_DETAIL.put("DJ", printData.getString("DJ")); //订单明细信息 单价
		// ORDER_DETAIL.put("JE", printData.getString("JE")); //订单明细信息 金额
		//		
		// inParam.put("ZFFS", printData.getString("PAY_MODE")); //支付方式 如现金
		// inParam.put("ZFLSH", printData.getString("ZFLSH")); //支付流水号
		// inParam.put("ZFPT", printData.getString("ZFPT")); //支付平台 如支付宝
		// inParam.put("CYGS", printData.getString("CYGS")); //承运公司
		// inParam.put("SHSJ", printData.getString("SHSJ")); //送货时间
		// inParam.put("WLDH", printData.getString("WLDH")); //物流单号
		// inParam.put("SHDZ", printData.getString("SHDZ")); //送货地址

		inParam.put("PRINT_ID", printData.getString("PRINT_ID")); // 打印的发票的备案日志TD_B_PRINTNOTLOG表

		// 调开具接口
		if ("Y".equals(eflag))
		{
			inParam.put("PRINT_ID", callKJNEW(inParam));
		} else
		{
			inParam.put("PRINT_ID", callKJ(inParam));
			// 记录开具结果日志
			recordKJLog(inParam);
		}

		IDataset rs = new DatasetList();
		IData d = new DataMap();
		d.put("RESULT_CODE", "0000");
		rs.add(d);

		return rs;
	}

	/**
	 * 开具
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private String callKJ(IData data) throws Exception
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("callKJ in parmas:" + data.toString());
		}

		String url = GlobalCfg.getProperty("service.router.addr", null);

		IDataset result = CSAppCall.call(url,"TAM_ELECNOTE_GENNOTE", data, true, 12000);
		if (null == result || result.isEmpty())
		{
			CSAppException.apperr(PrintException.CRM_PRINT_25, "电子打印开具返回空!");
		}

		if (logger.isDebugEnabled())
		{
			logger.debug("callKJ result:" + result.toString());
		}

		String resultCode = result.getData(0).getString("RESULT_CODE");
		String resultInfo = result.getData(0).getString("RESULT_INFO", "");
		String requestId = result.getData(0).getString("REQUEST_ID");
		if ("0000".equals(resultCode))
		{
			return requestId;
		} else
		{
			CSAppException.apperr(PrintException.CRM_PRINT_25, "电子打印开具报错：" + resultInfo);
		}
		return null;
	}

	private String callKJNEW(IData data) throws Exception
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("callKJ in parmas:" + data.toString());
		}

		String url = GlobalCfg.getProperty("service.router.addr", null);
		IDataset result = CSAppCall.call(url,"AM_CUSTOM_ELECNOTE_GENNOTE", data, true, 12000);
		if (null == result || result.isEmpty())
		{
			CSAppException.apperr(PrintException.CRM_PRINT_25, "电子打印开具返回空!");
		}

		if (logger.isDebugEnabled())
		{
			logger.debug("callKJ result:" + result.toString());
		}

		String resultCode = result.getData(0).getString("RESULT_CODE");
		String resultInfo = result.getData(0).getString("RESULT_INFO", "");
		String requestId = result.getData(0).getString("REQUEST_ID");
		if ("0000".equals(resultCode))
		{
			return requestId;
		} else
		{
			CSAppException.apperr(PrintException.CRM_PRINT_25, "电子打印开具报错：" + resultInfo);
		}
		return null;
	}

	/**
	 * 记录开具日志
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private boolean recordKJLog(IData data) throws Exception
	{
		return Dao.insert("TF_B_PRINTPDF_LOG", data, Route.getJourDb(BizRoute.getRouteId()));
	}

	private IDataset generFeeListTax(IData printData, String tradeId, String tradeTypeCode, String routeId) throws Exception
	{
		int reCountTotalFeeMoney = 0; // 重算费用：用于检查是否与传递过来的费用总额相同
		int totalFeeTaxMoney = 0; // 总税额

		IDataset mergeFeeList = printData.getDataset("MERGE_FEE_LIST");
		String containOperFlag = printData.getString("MERGE_FEE_LIST_OPER_FLAG");
		String containForegiftFlag = printData.getString("MERGE_FEE_LIST_FOREGIFT_FLAG");
		String containAdvanceFlag = printData.getString("MERGE_FEE_LIST_ADVANCE_FLAG");

		// 获取设置的税率
		boolean isShowFeeTax = true;
		String rateStr = tax_hid_char;
		IDataset rateConfs = getERecptFeeTax(null);
		if (null != rateConfs && rateConfs.size() > 0)
		{
			rateStr = rateConfs.getData(0).getString("PARA_CODE1", tax_hid_char);
		}
		if (tax_hid_char.equals(rateStr))
		{
			isShowFeeTax = false;
		}
		
		//五一以后税率更改
		IDataset rateConstants = CommparaInfoQry.getCommPkInfo("CSM", "2016", "ERECPT_FEE_TAX2", "ZZZZ");
		int rateConstant = 0;
		if(IDataUtil.isNotEmpty(rateConstants)){
			rateConstant = Integer.parseInt(rateConstants.getData(0).getString("PARA_CODE1"));
		}
		
		int rate = tax_hid_char.equals(rateStr) ? rateConstant : Integer.valueOf(rateStr);
		
//		int rate = tax_hid_char.equals(rateStr) ? 1700 : Integer.valueOf(rateStr);

		// 重新获取组装费用明细列表
		if (mergeFeeList == null || mergeFeeList.isEmpty())
		{
			mergeFeeList = new DatasetList();
			IDataset feeListTmp = TradefeeSubInfoQry.qryTradeFeeSubByTradeId(tradeId, BizRoute.getRouteId());
			if (feeListTmp != null && feeListTmp.size() > 0)
			{
				for (int i = 0; i < feeListTmp.size(); i++)
				{
					IData feeData = feeListTmp.getData(i);
					String feeMode = feeData.getString("FEE_MODE");
					int payFee = feeData.getInt("FEE", 0);
					// PS:存在为payFee=0时也需要打印的情况。
					if ("1".equals(containOperFlag) && "0".equals(feeMode) && payFee > 0) // 营业费
					{
						mergeFeeList.add(feeData);
					}
					if ("1".equals(containForegiftFlag) && "1".equals(feeMode) && payFee > 0) // 押金
					{
						mergeFeeList.add(feeData);
					}
					if ("1".equals(containAdvanceFlag) && "2".equals(feeMode) && payFee > 0) // 预存
					{
						mergeFeeList.add(feeData);
					}
				}
			}
		}

		// 遍历费用明细列表，获取费用名目
		if (null != mergeFeeList && !mergeFeeList.isEmpty())
		{
			for (int i = 0; i < mergeFeeList.size(); i++)
			{
				IData feeData = mergeFeeList.getData(i);
				String feeMode = feeData.getString("FEE_MODE");
				String feeTypeCode = feeData.getString("FEE_TYPE_CODE");
				int feecheck = feeData.getInt("FEE", 0);
				if (0 == feecheck)
				{
					mergeFeeList.remove(i);// 0费用小项电子票打不了
					i--;
					continue;
				}
				IDataset temp = null;

				if ("0".equals(feeMode))
				{
					// TD_B_FEEITEM
					temp = FeeItemInfoQry.getFeeItem(CSBizBean.getTradeEparchyCode());
				} else if ("1".equals(feeMode))
				{
					// TD_S_FOREGIFT
					temp = ForeGiftInfoQry.getForegift();
				} else if ("2".equals(feeMode))
				{
					// TD_B_PAYMENT
					temp = PayMentInfoQry.getPayment();
				}

				boolean contains = false;
				if (temp != null && temp.size() > 0)
				{
					for (int j = 0; j < temp.size(); j++)
					{
						IData tmp = (IData) temp.get(j);
						if (tmp.getString("CODE").equals(feeTypeCode))
						{
							contains = true;
							int fee = feeData.getInt("FEE", 0);
							int feeTaxMoney = getFeeTaxMoney(fee, rate);
							reCountTotalFeeMoney += fee;
							totalFeeTaxMoney += feeTaxMoney;
							feeData.put("FEE_TYPE_DES", tmp.getString("NAME"));
							feeData.put("FEE_TAX", rate);
							feeData.put("FEE_TAX_MONEY", feeTaxMoney);
							break;
						}
					}
				}

				if (!contains)
				{
					int fee = feeData.getInt("FEE", 0);
					int feeTaxMoney = getFeeTaxMoney(fee, rate);
					reCountTotalFeeMoney += fee;
					totalFeeTaxMoney += feeTaxMoney;
					feeData.put("FEE_TYPE_DES", "0".equals(feeMode) ? "营业费" : "1".equals(feeMode) ? "押金" : "2".equals(feeMode) ? "预存" : "其他未知项");
					feeData.put("FEE_TAX", rate);
					feeData.put("FEE_TAX_MONEY", feeTaxMoney);
				}
			}
		}

		printData.put("MERGE_FEE_LIST", mergeFeeList);
		printData.put("TOTAL_FEE_TAX_MONEY", totalFeeTaxMoney);
		printData.put("TOTAL_FEE_MONEY_CHECK", reCountTotalFeeMoney);
		printData.put("FEE_TAX_RATE", rate);
		printData.put("IS_SHOW_FEE_TAX", isShowFeeTax);
		return mergeFeeList;
	}

	/**
	 * 得到税率设置
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private IDataset getERecptFeeTax(IData input) throws Exception
	{
		return CommparaInfoQry.getCommPkInfo("CSM", "2016", "ERECPT_FEE_TAX", "ZZZZ");
	}

	/**
	 * 税率计算公式,计算税费
	 * 
	 * @param fee
	 * @param rate
	 * @return
	 * @throws Exception
	 */
	private int getFeeTaxMoney(int fee, int rate) throws Exception
	{
		if (fee == 0)
			return 0;
		if (rate == 0)
			return 0;
		int rateMoney = (int) (fee - fee / (1 + rate / 10000.00));
		return rateMoney;
	}

	public IDataset hadPrintedKJ(IData input) throws Exception
	{
		// 下面这段代码逻辑是根据主台账的PROCESS_TAG_SET的打印标志位来判断是否已打印。
		IDataset rtnDataset = new DatasetList();
		IData rtnData = new DataMap();
		rtnDataset.add(rtnData);
		rtnData.put("RESULT_CODE", "0000");
		rtnData.put("HAD_PRINTED", "0");

		String tradeId = input.getString("TRADE_ID");
		IDataset mainTradeInfos = TradeInfoQry.getTradeAndBHTradeByTradeId(tradeId);
		if (mainTradeInfos == null || mainTradeInfos.isEmpty())
		{
			CSAppException.apperr(PrintException.CRM_PRINT_16);
		}
		IData mainTradeInfo = mainTradeInfos.getData(0);
		String processTagSet = mainTradeInfo.getString("PROCESS_TAG_SET", "");
		if (processTagSet.length() >= 30)
		{
			if ("1".equals(processTagSet.charAt(30)) || "2".equals(processTagSet.charAt(30)))
			{
				rtnData.put("HAD_PRINTED", "1");
			}
		}

		return rtnDataset;
	}

	/**
	 * 业务类型是否需要电子开具设置
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset isNeedKjSetiByTradeType(IData input) throws Exception
	{
		return CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "2016", "TRADE_TYPE_KJ_CONF", input.getString("TRADE_TYPE_CODE"), input.getString(Route.ROUTE_EPARCHY_CODE));
	}

	/**
	 * 其他参数设置
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset getOtherTradeParam(IData input) throws Exception
	{
		return CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "2016", input.getString("PARAM_CODE"), input.getString("PARA_CODE1"), input.getString("EPARCHY_CODE"));
	}
	
	/**
	 * 虚拟流量和流量充值（费用特殊和没有台账的情况打发票调用，前面拼数据过来）
	 * @param printData
	 * @return
	 * @throws Exception
	 */
	public IDataset printKJForSC(IData printData) throws Exception
	{
		logger.error("=======printKJForSC======begin=====");
		IData inParam = new DataMap();
		String tradeId = printData.getString("TRADE_ID", "");		
		// 台账信息
		inParam.put("TRADE_ID", tradeId);   
		inParam.put("ACCEPT_MONTH", printData.getString("ACCEPT_MONTH",""));
		inParam.put("TRADE_DATE", printData.getString("ACCEPT_DATE",""));
		inParam.put("TRADE_STAFF_ID", printData.getString("TRADE_STAFF_ID", getVisit().getStaffId()));
		inParam.put("TRADE_DEPART_ID", printData.getString("TRADE_DEPART_ID",getVisit().getDepartId()));
		inParam.put("TRADE_CITY_CODE", printData.getString("TRADE_CITY_CODE",getVisit().getCityCode()));
		String routeId = printData.getString("TRADE_EPARCHY_CODE",getVisit().getStaffEparchyCode());
		String tradeTypeCode = printData.getString("TRADE_TYPE_CODE","");
        
		inParam.put("TRADE_EPARCHY_CODE", routeId);
		String custType = printData.getString("CUST_TYPE", "");
		String USER_ID = printData.getString("USER_ID", "");

		inParam.put("SERIAL_NUMBER", printData.getString("SERIAL_NUMBER"));
		inParam.put("DSPTBM", "13310101"); // 平台编码(取渠道)
		inParam.put("IN_MODE_CODE", printData.getString("IN_MODE_CODE")); // 平台编码(取渠道)

		String chanlId = printData.getString("APPLY_CHANNEL", "0"); // 开票发起渠道：：0-营业个人业务;1-集团有ACCTID业务；2-集团无ACCTID业务；3-账务
		inParam.put("APPLY_CHANNEL", chanlId);

		String acctId = printData.getString("ACCT_ID");

		if ("1".equals(chanlId) && StringUtils.isBlank(acctId))
		{
			chanlId = "2";
			inParam.put("APPLY_CHANNEL", "2"); // 如集团营业费用一次性费用收取，收现金不与产品和账户挂钩的情形
		}

		inParam.put("DKBZ", "0"); // 1、 自开(0) 2、 代开(1)
		inParam.put("BMB_BBH", "1.0"); // 编码表版本号

		// 发票信息
		String assembFlag = "";
		String containOperFlag = printData.getString("MERGE_FEE_LIST_OPER_FLAG");
		assembFlag += "1".equals(containOperFlag) ? "1" : "0".equals(containOperFlag) ? "0" : "X";
		String containForegiftFlag = printData.getString("MERGE_FEE_LIST_FOREGIFT_FLAG");
		assembFlag += "1".equals(containForegiftFlag) ? "1" : "0".equals(containForegiftFlag) ? "0" : "X";
		String containAdvanceFlag = printData.getString("MERGE_FEE_LIST_ADVANCE_FLAG");
		assembFlag += "1".equals(containAdvanceFlag) ? "1" : "0".equals(containAdvanceFlag) ? "0" : "X";
		inParam.put("ASSEMB_FLAG", assembFlag);
		inParam.put("ORDER_ID", printData.getString("ORDER_ID"));
		inParam.put("TYPE", printData.getString("TYPE")); // 票据类型：
		// 发票P0001、收据P0002、免填单(业务受理单)P0003
		inParam.put("PRINT_ID_BAK", printData.getString("PRINT_ID", ""));// 打印数据时的数据源备案日志ID
		// int totalFee = (0 == printData.getInt("TOTAL_MONEY", 0)) ?
		// printData.getInt("TOTAL_FEE", 0) : printData.getInt("TOTAL_MONEY",
		// 0);
		// inParam.put("TOTAL_FEE", totalFee); //发票的总额
		inParam.put("NOTE_NO", printData.getString("NOTE_NO")); // 票号
		inParam.put("TAX_NO", printData.getString("TAX_NO")); // 税务号(单联票输入的两个参数)
		if("1".equals(printData.getString("ABILITY","0")) || "".equals(printData.getString("PAY_NAME"))){
			inParam.put("PAY_NAME", printData.getString("PAY_NAME"));
		}else{
			inParam.put("PAY_NAME", printData.getString("CUST_NAME",""));
		}
		// 判断是否为特殊业务打印（无三户资料）2016/9/23
		String eflag = printData.getString("EFLAG");
		String postEmail = printData.getString("POST_EMAIL");
		if ("Y".equals(eflag))
		{// 特殊业务--无三户资料
			inParam.put("NEW_FLAG", "2");
			inParam.put("EMAIL", postEmail);
			inParam.put("RSRV_INFO2", "0"); // 0：无资料业务
			if (StringUtils.isBlank(printData.getString("SERIAL_NUMBER")))
			{
				inParam.put("PSPT_ID", tradeId.substring(0, 16)); // 0：无资料业务
			} else
			{
				inParam.put("PSPT_ID", printData.getString("SERIAL_NUMBER")); // 0：无资料业务
			}
			inParam.put("EPARCHY_CODE", "0898");
		} 
		else
		{// 正常业务
			inParam.put("PARTITION_ID", USER_ID.substring(USER_ID.length() - 4));
			inParam.put("USER_ID", USER_ID);
			inParam.put("EPARCHY_CODE", printData.getString("EPARCHY_CODE", routeId));
			if (!"GRP".equals(custType))
			{
				inParam.put("ACCT_ID", printData.getString("ACCT_ID"));
				inParam.put("CUST_ID", printData.getString("CUST_ID"));
			}
		}

		// 推送信息
		inParam.put("PRINT_FLAG", "0"); // 0，正常发票；1冲红发票
		inParam.put("POST_TAG", printData.getString("RECEIVER_SENDWAY")); // 邮寄方式，0手机，1邮箱，2短信+邮箱
		// //从用户当前配置中读取
		inParam.put("POST_MOBILE", printData.getString("RECEIVER_MOBILE")); // 邮寄邮箱
		inParam.put("POST_EMAIL", printData.getString("RECEIVER_EMAIL")); // 邮寄手机号

		String inDate = SysDateMgr.getSysTime();
		inParam.put("IN_DATE", inDate);
		inParam.put("IN_STAFF_ID", printData.getString("TRADE_STAFF_ID", getVisit().getStaffId()));
		inParam.put("IN_CITY_CODE", printData.getString("TRADE_CITY_CODE",getVisit().getCityCode()));
		inParam.put("IN_DEPART_ID", printData.getString("TRADE_DEPART_ID",getVisit().getDepartId()));

		inParam.put("KPXM", printData.getString("NAME", "发票")); // 主要开票项目(取发票的名称)

		// 销售方信息
		inParam.put("XHF_NSRSBH", ""); // 销售方的编码 暂时以营业厅编码
		inParam.put("XHFMC", StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", printData.getString("TRADE_DEPART_ID")) ); // 销货方名称
		inParam.put("XHF_DZ", ""); // 销货方地址
		inParam.put("XHF_DH", ""); // 销货方电话 固定电话
		inParam.put("XHF_YHZH", ""); // 销货方银行账号

		// 购货方信息(从客户资料或者台账资料里获取)
		inParam.put("GHF_NSRSBH", printData.getString("TAX_PAYER_ID","")); // 购货方识别号,平台传过来有就赋值，没有就为空
		inParam.put("GHFMC", StringUtils.isBlank(printData.getString("TITLE",""))?printData.getString("CUST_NAME", ""):printData.getString("TITLE","")); // 购货方名称

		if ("416".equals(tradeTypeCode) && StringUtils.isBlank(inParam.getString("GHFMC")))
		{
			inParam.put("GHFMC", "有价卡销售");
		}
		 // 购货方标识：0-取配置文件，1-取报文
        inParam.put("GHF_FLAG", printData.getInt("GHF_FLAG",1));
		inParam.put("GHF_DZ", ""); // 购货方地址
		inParam.put("GHF_SF", ""); // 购货方省份
		inParam.put("GHF_GDDH", printData.getString("SERIAL_NUMBER")); // 购货方固定电话
		inParam.put("GHF_SJ", ""); // 购货方手机
		inParam.put("GHF_SJ", ""); // 购货方邮箱
		inParam.put("GHFQYLX", "01"); // 购货方企业类型
		inParam.put("GHF_YHZH", ""); // 购货方银行账号

		// ? inParam.put("HY_DM", printData.getString("HY_DM")); //行业代码
		// ? inParam.put("HY_MC", printData.getString("HY_MC")); //行业名称
		inParam.put("KPY","系统"); // 开票员
		inParam.put("SKY", StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", printData.getString("TRADE_STAFF_ID"))); // 收款员
		inParam.put("FHR", getVisit().getStaffName()); // 复核人
		inParam.put("KPRQ", inDate); // 开票日期yyyy-MM-dd HH:mm:ss
		inParam.put("KPRQ_YEAR", inDate.substring(0, 4));// 开票日期yyyy-MM-dd
		// HH:mm:ss
		inParam.put("KPRQ_MONTH", inDate.substring(5, 7)); // 开票日期
		inParam.put("KPRQ_DAY", inDate.substring(8, 9)); // 开票日期
		inParam.put("KPLX", "1"); // 开票类型
		inParam.put("CZDM", "10"); // 操作代码： 10正票正常开具

		int totalFee = (0 ==Integer.valueOf(printData.getString("TOTAL_MONEY","0"))) ? Integer.valueOf(printData.getString("TOTAL_FEE","0")) : Integer.valueOf(printData.getString("TOTAL_MONEY", "0"));
		// 项目明细列表
		boolean isShowFeeTax = false;
		IDataset XM_DETAIL_S = new DatasetList();
		inParam.put("XM_DETAIL", XM_DETAIL_S);	
		IData XM_DETAIL = new DataMap();
		XM_DETAIL_S.add(XM_DETAIL);
		XM_DETAIL.put("FPHXZ", "0"); // 发票行性质
		XM_DETAIL.put("XMMC", printData.getString("NAME", "发票")); // 项目名称
		inParam.put("KPXM", printData.getString("NAME", "发票")); // 主要开票项目
		XM_DETAIL.put("XMDW",  "元"); // 项目单位
		XM_DETAIL.put("GGXH", ""); // 规格型号
		XM_DETAIL.put("XMSL", "1"); // 项目数量
		XM_DETAIL.put("HSBZ", "1"); // 含税标志
		XM_DETAIL.put("XMDJ", totalFee); // 项目单价
		XM_DETAIL.put("SPBM", "3030000000000000000"); // 商品编码通信服务费 -- 3030000000000000000（电信服务） 有价卡费    -- 6010000000000000000（预付卡销售和充值）设备购机费 --1090505000000000000（移动通信终端设备及零部件）
		XM_DETAIL.put("ZXBM", tradeId.substring(0, 16)); // 自行编码
		XM_DETAIL.put("YHZCBS", "0"); // 优惠政策标识
		XM_DETAIL.put("LSLBS", "3"); // 零税率标识
		XM_DETAIL.put("ZZSTSGL", ""); // 增值税特殊处理
		XM_DETAIL.put("XMJE", totalFee); // 项目金额
		XM_DETAIL.put("SL",  tax_hid_char); // 税率0税率
		XM_DETAIL.put("SE", tax_hid_char); // 税额0税率
	
		// 获取发票对应的类型
		int TOTAL_MONEY = totalFee;
		inParam.put("TOTAL_FEE", TOTAL_MONEY); // 发票的总额
		int TOTAL_FEE_TAX_MONEY = printData.getInt("TOTAL_FEE_TAX_MONEY", 0);
		int EXCLUDE_TAX_MONEY = TOTAL_MONEY - TOTAL_FEE_TAX_MONEY;
		inParam.put("KPHJJE", totalFee); // 开票合计金额：价税合计金额
		inParam.put("HJBHSJE", TOTAL_MONEY); // 合计不含税金额
		inParam.put("HJSE",  tax_hid_char); // 合计税额 0税率
		inParam.put("KPSL",  tax_hid_char); // 开票税率0税率

		// 订单信息
		inParam.put("DDH", inParam.getString("ORDER_ID")); // 订单号
		inParam.put("THDH", printData.getString("THDH")); // 退货单号
		inParam.put("DDDATE", printData.getString("ACCEPT_DATE")); // 订单时间
		inParam.put("PRINT_ID", printData.getString("PRINT_ID")); // 打印的发票的备案日志TD_B_PRINTNOTLOG表

		// 调开具接口
		inParam.put("TAG", printData.getString("TAG",""));//虚拟流量和流量直冲的标志
		logger.error("=======printKJForSC======eflag="+eflag);
		logger.error("=======printKJForSC======inParam="+inParam);

		if ("Y".equals(eflag))
		{	
			inParam.put("PRINT_ID", callKJNEW(inParam));
		}
		else
		{
			inParam.put("PRINT_ID", callKJ(inParam));			
			inParam.put("REMARK", printData.getString("TAG",""));
			// 记录开具结果日志
			recordKJLog(inParam);
		}
			
		IDataset rs = new DatasetList();
		IData d = new DataMap();
		d.put("RESULT_CODE", "0000");
		d.put("PRINT_ID", inParam.getString("PRINT_ID",""));
		rs.add(d);
		return rs;
	}
}
