package com.asiainfo.veris.crm.order.web.person.queryuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class queryNewCardIdUser extends PersonBasePage {

	public abstract void setInfo(IData info);

	public abstract void setInfos(IDataset infos);

	public abstract void setCondition(IData info);

	public void queryInfos(IRequestCycle cycle) throws Exception {
		IData condParams = this.getData("cond");

		IDataset queryInfos = CSViewCall.call(this, "SS.queryNewCardIdUserService.Query", condParams);		
		setInfos(queryInfos);
		
	}
}
