package com.asiainfo.veris.crm.order.soa.person.busi.einvoicehistory;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class EInvoiceHistoryBean extends CSBizBean
{

	/**
	 * 电子发票历史查询业务
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryEInvoiceTrade(IData data) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		param.put("PRINT_FLAG", data.getString("PRINT_FLAG"));
		param.put("START_DATE", data.getString("cond_START_DATE"));
		param.put("END_DATE", data.getString("cond_END_DATE"));
		return Dao.qryByCodeParser("TF_B_PRINTPDF_LOG", "SEL_EINVOICETRADEINFO", param, Route.getJourDb(Route.CONN_CRM_CG));

	}

	/**
	 * 根据TradeId查询未冲红的电子发票信息
	 * 
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryPrintPDFLogByTradeId(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCode("TF_B_PRINTPDF_LOG", "SELECT_PRINTPDFLOG_BY_TRADEID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	}

	/**
	 * 电子发票冲红
	 */
	public IDataset modifyEInvoiceTrade(IData data) throws Exception
	{
		IData param = new DataMap();
		String serialNumber = data.getString("SERIAL_NUMBER");
		IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
		if (IDataUtil.isEmpty(userInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户资料不存在");
		}
		IDataset acctInfos = AcctInfoQry.getAcctInfoByCustId(userInfos.getData(0).getString("CUST_ID"), userInfos.getData(0).getString("EPARCHY_CODE"));
		if (IDataUtil.isEmpty(acctInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "账户资料不存在");
		}
		param.put("USER_ID", userInfos.getData(0).getString("USER_ID"));
		param.put("ACCT_ID", acctInfos.getData(0).getString("ACCT_ID"));
		param.put("PRINT_ID", data.getString("PRINT_ID"));
		param.put("TRADE_ID", data.getString("TRADE_ID"));
		modifyEInvoice(param);
		IDataset results = new DatasetList();
		IData result = new DataMap();
		result.put("result", "OK");
		results.add(result);
		return results;
	}

	/**
	 * 公共方法：电子发票冲红
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static void modifyEInvoice(IData data) throws Exception
	{
		IData param = new DataMap();
		String chPrintId = SeqMgr.getPrintId(); // 新生成红字发票PRINT_ID
		param.put("PRINT_ID", chPrintId);
		param.put("REQUEST_ID", data.getString("PRINT_ID"));
		param.put("ACCT_ID", data.getString("ACCT_ID"));
		param.put("USER_ID", data.getString("USER_ID"));
		IDataset resultset = AcctCall.toCreditNote(param); // 发票冲红接口
		if (IDataUtil.isEmpty(resultset))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "账务冲红接口返回空！");
		} else
		{
			if ("0000".equals(resultset.getData(0).getString("RESULT_CODE")))
			{
				if (StringUtils.isNotBlank(resultset.getData(0).getString("REQUEST_ID")))
				{
					IData updParam = new DataMap();
					// 电子发票日志表中没有票据日志表中类似cancel等字段，所以只需修改reprint_flag字段
					updParam.put("TRADE_ID", data.getString("TRADE_ID"));
					updParam.put("RSRV_INFO1", chPrintId);
					updParam.put("PRINT_ID", resultset.getData(0).getString("REQUEST_ID"));
					Dao.executeUpdateByCodeCode("TF_B_PRINTPDF_LOG", "UPD_BY_TRADE_ID", updParam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
				} else
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "冲红失败:账务返回冲红发票唯一流水为空");
				}
			}
		}
	}

	/**
	 * 业务类型是否支持打印电子发票
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static IDataset isEInvoicePrint(String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PARAM_CODE", "IS_PRINTEINVOICE");
		param.put("PARA_CODE1", tradeTypeCode);
		param.put("EPARCHY_CODE", getTradeEparchyCode());
		return CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "2016", param.getString("PARAM_CODE"), param.getString("PARA_CODE1"), param.getString("EPARCHY_CODE"));
	}

}
