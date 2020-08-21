package com.asiainfo.veris.crm.order.web.person.pccbusiness;

import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

/**
 * 关于配合做好全网五项PCC策略
 */
public abstract class AddPccInfoView extends PersonQueryPage {
	
	public abstract void setCondition(IData condition);
	public abstract void setInfo(IData userinfo);
	public abstract void setInfos(IDataset a);

	public void addPccUserInfo(IRequestCycle cycle) throws Exception {
		
	}
}