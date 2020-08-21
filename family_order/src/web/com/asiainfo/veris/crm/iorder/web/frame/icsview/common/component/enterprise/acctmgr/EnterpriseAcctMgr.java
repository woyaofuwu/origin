/*** Eclipse Class Decompiler plugin, copyright (c) 2016 Chen Chao (cnfree2000@hotmail.com) ***/
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.acctmgr;


import org.apache.tapestry.IBinding;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class EnterpriseAcctMgr extends BizTempComponent {
	private static final Logger logger = LoggerFactory.getLogger(EnterpriseAcctMgr.class);

	public abstract void setInfo(IData paramIData);

	public abstract String getName();

	public abstract void setName(String paramString);

	public abstract void setAcctInfo(IData paramIData);

	public abstract String getCallback();

	public abstract boolean getIsCommit();

	public abstract boolean getIsClear();

	public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
			throws Exception {
		boolean isAjax = isAjaxServiceReuqest(cycle);
		String listener = getPage().getData().getString("ajaxListener", "");

		String id = getId();
		String name = getName();
		IBinding idBinding = getBinding("id");
		if (idBinding != null) {
			id = idBinding.getString();
		}
		if ((name != null) && (!("".equals(name.trim())))) {
			id = name;
		}
		setName(id);

		if ((isAjax) && (StringUtils.isNotBlank(listener))) {
			if (getPage().getData().containsKey("SUBMIT_DATA")) {
				IData submitData = new DataMap(getPage().getData().getString("SUBMIT_DATA", ""));
				getPage().getData().put("SUBMIT_DATA", submitData);
			}

			if ("accountDetial".equals(listener))
				accountDetial();
			else if ("addAccount".equals(listener))
				addAccount();
			else if ("modifyAccount".equals(listener))
				modifyAccount();
			else if ("deleteAccount".equals(listener))
				deleteAccount();
			else if ("queryBanksBySuperBank".equals(listener))
				queryBanksBySuperBank();
			else if ("queryContactMedium".equals(listener))
				queryContactMedium();
		} else {
			initPage(informalParametersBuilder, writer, cycle);
		}
	}

	private void initPage(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
			throws Exception {
		String js1 = "scripts/iorder/icsserv/component/enterprise/acctmgr/EnterpriseAcctMgr.js";
		boolean isAjax = isAjaxServiceReuqest(cycle);
		if (isAjax)
			includeScript(writer, js1, false, false);
		else {
			getPage().addResAfterBodyBegin(js1, false, false);
		}

		String id = getName();
		String callback = getCallback();
		boolean isCommit = getIsCommit();
		boolean isClear = getIsClear();

		StringBuilder config = new StringBuilder();
		config.append("{");
		config.append("isCommit:").append(isCommit);
		config.append(",").append("isClear:").append(isClear);
		if (StringUtils.isNotBlank(callback)) {
			config.append(",").append("callback:").append(callback);
		}
		config.append("}");
		StringBuilder initScript = new StringBuilder();

		initScript.append("window.").append(id).append(" = new EnterpriseAcctMgr(\"").append(id).append("\", ")
				.append(config.toString()).append(");\n");

		if (isAjax)
			addScriptContent(writer, initScript.toString());
		else {
			getPage().addScriptBeforeBodyEnd(new StringBuilder().append(id).append("_init").toString(),
					initScript.toString());
		}

		IData acctInfo = new DataMap();
		acctInfo.put("ACCT_TYPE", Integer.valueOf(0));
		setAcctInfo(acctInfo);
		
		IData info = new DataMap();
		initSelectList(info);
		setInfo(info);
	}

	private void initSelectList(IData info) throws Exception {
		info.put("ACCT_TYPE_SEL_LIST", getAcctTypeSelList());
		info.put("SUPER_BANK_CODE_SEL_LIST", getSuperBankSelList());
	}

	private static IDataset getAcctTypeSelList() throws Exception {
		IDataset result = StaticUtil.getStaticList("PAY_MODE");
		if (result == null) {
			result = new DatasetList();
		}
		return result;
	}

	private IDataset getSuperBankSelList() throws Exception {
		IDataset datas = CSViewCall.call(this,"CS.BankInfoQrySVC.queryBankList", new DataMap());

		if (datas == null) {
			datas = new DatasetList();
		}
		return datas;
	}

	private IDataset getBankSelList(String superBank,String mgmtDistrict) throws Exception {
        IData info = new DataMap();
        info.put("SUPER_BANK_CODE", superBank);
        info.put("EPARCHY_CODE", mgmtDistrict);
        IDataset datas  = CSViewCall.call(this,"CS.BankInfoQrySVC.queryBackCode", info);

		if (datas == null) {
			datas = new DatasetList();
		}

		logger.debug("====================queryBankSelList 查询结果: {}", datas);

		return datas;
	}

	public void queryBanksBySuperBank() throws Exception {
		String superBank = getPage().getData().getString("SUPER_BANK_CODE", "");

		setBankCodeSelListBySuperBank(superBank);
	}

	public void queryContactMedium() throws Exception {
		String contNum = getPage().getData().getString("CONTACT_PHONE", "");

		IDataset result = queryContactMediumByContNumber(contNum);

		getPage().setAjax(result);
	}

	private void setBankCodeSelListBySuperBank(String superBank) throws Exception {
		if (StringUtils.isNotBlank(superBank)) {
			IDataset result = getBankSelList(superBank,getVisit().getLoginEparchyCode());

			IData info = new DataMap();
			info.put("BANK_CODE_SEL_LIST", result);

			setInfo(info);
		}
	}

	public void accountDetial() throws Exception {
		String acctId = getPage().getData().getString("ACCT_ID", "");

		IData input = new DataMap();
		input.put("ACCT_ID", acctId);
		
		IData result = queryAcctByAcctId(acctId);

		String eparchyCode = result.getString("EPARCHY_CODE", "");
		String regionName = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", eparchyCode);
		result.put("REGION_NAME", regionName);

		

		IData cont = queryPaymentMethodByAcctId(acctId);
		result.put("CONTRACT_ID", cont.getString("CONTRACT_ID"));
		result.put("CONTACT_PHONE", cont.getString("CONTACT_PHONE"));
		result.put("CONTACT", cont.getString("CONTACT"));
		result.put("POST_ADDRESS", cont.getString("ADDR_DETAIL_NAME"));
		result.put("POST_CODE", cont.getString("POSTAL_CODE"));
		
		String superBank = cont.getString("UP_CARD_BANK_CODE");

		result.put("BANK_ACCT_NO", cont.getString("CARD_BANK_ACCT"));
		result.put("SUPER_BANK_CODE", superBank);
		result.put("BANK_CODE", cont.getString("CARD_BANK_CODE"));
		result.put("BANK_NAME", cont.getString("CARD_BANK_ACCT_NAME"));
		setBankCodeSelListBySuperBank(superBank);
		setAcctInfo(result);
		logger.debug("=============queryAcctByAcctId：{}", result);

		getPage().setAjax(result);
	}
	
	public IData queryAcctByAcctId(String acctId)throws Exception{
		IData result = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId);
		return result;
	}

	public void addAccount() throws Exception {
		IData param = new DataMap();
		
		param.putAll(getPage().getData());
		param.put("STATE", "1");
		param.put("ACCT_IS_ADD", "true");
		IData input = assembleOrderDataNew(this, param, "4500");
		IDataset datas  = CSViewCall.call(this,"OC.ICreateEnterpriseAcctSV.createEnterpriseAcct", input);

		getPage().setAjax(datas);
	}
	

	public void modifyAccount() throws Exception {
		IData param = new DataMap();

		param.putAll(getPage().getData());
		param.put("STATE", "2");
		param.put("ACCT_IS_ADD", "true");
		IData input = assembleOrderDataNew(this, param, "4500");

//		IData result = ServiceCaller.call("OrderCentre.enterprise.IGroupManageSV.grpAccountOrder", input);
//		IData result = testGrpAccountOrder();
		IDataset result = CSViewCall.call(this,"OC.ICreateEnterpriseAcctSV.createEnterpriseAcct", input);
		getPage().setAjax(result);
	}

	public void deleteAccount() throws Exception {
		IData param = new DataMap();
		param.putAll(getPage().getData());
		param.put("STATE", "3");
		param.put("ACCT_IS_ADD", "true");
		IData input = assembleOrderDataNew(this, param, "4500");

//		IData result = ServiceCaller.call("OrderCentre.enterprise.IGroupManageSV.grpAccountOrder", input);
//		IData result = testGrpAccountOrder();
		IDataset result = CSViewCall.call(this,"OC.ICreateEnterpriseAcctSV.createEnterpriseAcct", input);
		getPage().setAjax(result);
	}
	
	public IData testGrpAccountOrder()throws Exception{
		IData data = new DataMap();
		data.put("flowId", "1212121212");
		return data;
	}

	private IDataset queryContactMediumByContNumber(String contNum) throws Exception {
		IDataset datas = null;
		IData input = new DataMap();
		input.put("SERIAL_NUMBER", contNum);
		IDataset result = CSViewCall.call(this,"OC.enterprise.IUmSubscriberQuerySV.querySubByAccessNum", input);

		if (datas == null) {
			datas = new DatasetList();
		}

		logger.debug("====================queryContactMediumByContNumber 查询结果: {}", datas);

		return datas;
	}

	private IData queryPaymentMethodByAcctId(String acctId) throws Exception {
		IData data = null;
		IData input = new DataMap();
		input.put("ACCT_ID", acctId);
		IDataset datas = CSViewCall.call(this,"OC.IAmPaymentMthodSV.queryAmAmPaymentMthodByAcctId", input);
		if (!(DataUtils.isEmpty(datas))) {
			data = datas.first();
		}
		if (data == null) {
			data = new DataMap();
		}

		logger.debug("====================queryContactMediumByAcctId 查询结果: {}", data);

		return data;
	}
	
	
	private static IData assembleOrderDataNew(BizTempComponent comp, IData param, String busiType) throws Exception {
		IData submitData = param.getData("SUBMIT_DATA");

		IData result = new DataMap();
		result.putAll(param);
		result.put("CUST_ID", submitData.getData("CUST_INFO").getString("CUST_ID"));
		result.put("PARTY_ID", submitData.getData("CUST_INFO").getString("PARTY_ID"));
		result.put("BUSI_TYPE", busiType);

		IData custInfo = submitData.getData("CUST_INFO");
		result.put("CUST_INFO", custInfo);
		custInfo.put("OPER_CODE", "3");

		String acctId = param.getString("ACCT_ID", "");
		IData acctInfo = submitData.getData("ACCT_INFO", new DataMap());
		acctInfo.put("OPER_CODE", "1");
		acctInfo.put("REGION_ID", comp.getPage().getVisit().getStaffEparchyCode());
		acctInfo.put("CITY_CODE", comp.getPage().getVisit().getCityCode());
		if (StringUtils.isNotBlank(acctId)) {
			acctInfo.put("ACCT_ID", acctId);
		}
		result.put("ACCT_ID", acctId);
		result.put("ACCT_INFO", acctInfo);		
		result.put("ROUTE_CODE", comp.getPage().getVisit().getStaffEparchyCode());


		return result;
	}
}