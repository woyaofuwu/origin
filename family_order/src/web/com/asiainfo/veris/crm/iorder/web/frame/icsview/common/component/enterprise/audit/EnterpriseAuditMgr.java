package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.audit;


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

public abstract class EnterpriseAuditMgr extends BizTempComponent {
	private static final Logger logger = LoggerFactory.getLogger(EnterpriseAuditMgr.class);

	public abstract void setInfo(IData paramIData);

	public abstract String getName();

	public abstract void setName(String paramString);

	public abstract void setAcctInfo(IData paramIData);
	
	public abstract String getCallback();

	public abstract boolean getIsCommit();

	public abstract boolean getIsClear();

	public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
			throws Exception {
		
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
            // 添加js
        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {
            includeScript(writer, "scripts/iorder/icsserv/component/enterprise/audit/EnterpriseAuditMgr.js", false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/enterprise/audit/EnterpriseAuditMgr.js", false, false);
            
        }
		
		
		
		/*
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
		}*/
	}

	private void initPage(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
			throws Exception {
		String js1 = "scripts/iorder/icsserv/component/enterprise/audit/EnterpriseAuditMgr.js";
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

		initScript.append("window.").append(id).append(" = new EnterpriseAuditMgr(\"").append(id).append("\", ")
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
		/*info.put("ACCT_TYPE_SEL_LIST", getAcctTypeSelList());
		info.put("SUPER_BANK_CODE_SEL_LIST", getSuperBankSelList());*/
	}







	

	



	
	
}