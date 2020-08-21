package com.asiainfo.veris.crm.iorder.web.person.changeepostinfo;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ModifyEPostInfoNew extends PersonBasePage
{

	public static final String MON_ELEC_INVOICE = "0"; // 月结电子发票

	public static final String CASH_ELEC_INVOICE = "1"; // 现金电子发票

	public static final String BUSINESS_ELEC_INVOICE = "2"; // 营业业务电子发票

	/**
	 * 初始页面装载参数
	 * 
	 */
	public void onInitTrade(IRequestCycle cycle) throws Exception
	{
		IData data = getData();

		data.put("ROUTE_EPARCHY_CODE", data.getString("EPARCHY_CODE", "0898"));
		IData display = new DataMap();
		String serialNumber = ""; // 1
		if (StringUtils.isNotBlank(data.getString("USER_ID")))
		{
			// 根据userid查询三户资料
			IDataset custData = CSViewCall.call(this, "SS.ModifyEPostInfoSVC.qryCustInfos", data);
			if (IDataUtil.isNotEmpty(custData))
			{
				setTradeInfo(custData.getData(0));
				setCustInfoView(custData.getData(0));
				setUserInfoView(custData.getData(0));
				display.put("IS_DISPLAY", "T");
				serialNumber = custData.getData(0).getString("SERIAL_NUMBER"); // 2
			} else
			{
				IDataset personInfos = CSViewCall.call(this, "SS.ModifyEPostInfoSVC.qryTradeInfos", data);
				setTradeInfo(personInfos.getData(0));
				setCustInfoView(personInfos.getData(0));
				setUserInfoView(personInfos.getData(0));
				display.put("IS_DISPLAY", "T");
				serialNumber = personInfos.getData(0).getString("SERIAL_NUMBER"); // 3
			}
			// 新增：显示已设置状态 4
			IData params = new DataMap();
			params.put("SERIAL_NUMBER", serialNumber);
			IDataset dataset = CSViewCall.call(this, "SS.ModifyEPostInfoSVC.qryEPostInfo", params);
			IData busiPost = new DataMap();
			if (!dataset.isEmpty())
			{
				for (int i = 0; i < dataset.size(); i++)
				{
					String postTag = dataset.getData(i).getString("POST_TAG");
					if (MON_ELEC_INVOICE.equals(postTag))
					{ // 月结电子发票
						busiPost.put("POST_TAG_0", postTag);
						busiPost.put("POST_DATE", dataset.getData(i).getString("POST_DATE"));
					} else if (CASH_ELEC_INVOICE.equals(postTag))
					{// 现金电子发票
						busiPost.put("POST_TAG_1", postTag);
					}
					if (BUSINESS_ELEC_INVOICE.equals(postTag))
					{// 营业业务电子发票
						busiPost.put("POST_TAG_2", postTag);
					}
					busiPost.put("POST_CHANNEL", dataset.getData(i).getString("POST_CHANNEL"));
					busiPost.put("RECEIVE_NUMBER", dataset.getData(i).getString("RECEIVE_NUMBER"));
					String post_adr = dataset.getData(i).getString("POST_ADR", "");
					if ("".equals(post_adr)) {
						busiPost.put("POST_ADR", post_adr);
					} else {
						busiPost.put("POST_ADR", post_adr.substring(0, post_adr.indexOf("@")));
					}
				}
			}
			if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "POSTPERMISSIONS"))
			{
				busiPost.put("POST_PERMISSIONS", "1");
			}
			setBusiInfo(busiPost);
			/*
			 * 判断是否为新开户
			 */
			IDataset rs = CSViewCall.call(this, "SS.ModifyEPostInfoSVC.isNewUser", data);
			if (IDataUtil.isNotEmpty(rs))
			{ // 如果是新开户
				display.put("NEW_FLAG", "0");
			} else
			{
				display.put("NEW_FLAG", "1");
			}
		} else
		{
			display.put("IS_DISPLAY", "F");
		}

		setDisplay(display);

	}

	/**
	 * 查询后设置页面信息
	 */
	public void loadChildInfo(IRequestCycle cycle) throws Exception
	{

		IData data = getData();

		IData dataset = new DataMap();

		IData userInfo = new DataMap(data.getString("USER_INFO", ""));

		dataset.put("USER_INFO", userInfo);

		// 查询子业务信息
		getcommInfo(dataset);

	}

	public void getcommInfo(IData allInfo) throws Exception
	{
		IData params = new DataMap();
		params.put("SERIAL_NUMBER", allInfo.getData("USER_INFO").getString("SERIAL_NUMBER"));
		IDataset dataset = CSViewCall.call(this, "SS.ModifyEPostInfoSVC.qryEPostInfo", params);
		IData busiPost = new DataMap();
		if (!dataset.isEmpty())
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				String postTag = dataset.getData(i).getString("POST_TAG");
				if (MON_ELEC_INVOICE.equals(postTag))
				{ // 月结电子发票
					busiPost.put("POST_TAG_0", postTag);
					busiPost.put("POST_DATE", dataset.getData(i).getString("POST_DATE"));
				}
				if (CASH_ELEC_INVOICE.equals(postTag))
				{// 现金电子发票
					busiPost.put("POST_TAG_1", postTag);
				}
				if (BUSINESS_ELEC_INVOICE.equals(postTag))
				{// 营业业务电子发票
					busiPost.put("POST_TAG_2", postTag);
				}
				busiPost.put("POST_CHANNEL", dataset.getData(i).getString("POST_CHANNEL"));
				busiPost.put("RECEIVE_NUMBER", dataset.getData(i).getString("RECEIVE_NUMBER"));
				String post_adr = dataset.getData(i).getString("POST_ADR", "");
				if ("".equals(post_adr)) {
					busiPost.put("POST_ADR", post_adr);
				} else {
					busiPost.put("POST_ADR", post_adr.substring(0, post_adr.indexOf("@")));
				}
				busiPost.put("POST_ADR_SEC", dataset.getData(i).getString("RSRV_STR2"));
			}
		}
		if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "POSTPERMISSIONS"))
		{
			busiPost.put("POST_PERMISSIONS", "1");
		}
		setBusiInfo(busiPost);
	}

	/**
	 * 业务提交
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void onTradeSubmit(IRequestCycle cycle) throws Exception
	{
		IData pdData = getData();
		pdData.put("SERIAL_NUMBER", pdData.getString("AUTH_SERIAL_NUMBER") == null ? pdData.getString("TRADE_AUTH_SERIAL_NUMBER") : pdData.getString("AUTH_SERIAL_NUMBER"));
		pdData.put(Route.ROUTE_EPARCHY_CODE, pdData.getString("EPARCHY_CODE"));
		String post_adr = pdData.getString("postinfo_POST_ADR", "");
		if (!"".equals(post_adr)) {
			pdData.put("postinfo_POST_ADR", post_adr + "@139.com");
		}
		IDataset dataset = CSViewCall.call(this, "SS.ModifyEPostInfoSVC.modiTradeReg", pdData);
		setAjax(dataset);
	}

	/**
	 * 打印电子工单
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void printEdoc(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		IData returnData = new DataMap();
		returnData.put("RESULT_CODE", "0");
		data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset printDataSet = CSViewCall.call(this, "CS.PrintNoteSVC.getCnoteInfo", data);
		if (IDataUtil.isNotEmpty(printDataSet))
		{
			returnData.put("CNOTE_DATA", printDataSet.getData(0));
		} else
		{
			returnData.put("RESULT_CODE", "1");
		}
		setAjax(returnData);
	}

	public abstract void setBusiInfo(IData ePostInfo);

	public abstract void setMonInfo(IData ePostInfo);

	public abstract void setCommInfos(IDataset ePostInfo);

	public abstract void setTradeInfo(IData tradeInfo);

	public abstract void setDisplay(IData isDisplay);
}
