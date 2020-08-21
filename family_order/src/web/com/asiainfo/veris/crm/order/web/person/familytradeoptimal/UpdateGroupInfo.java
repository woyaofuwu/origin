
package com.asiainfo.veris.crm.order.web.person.familytradeoptimal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UpdateGroupInfo extends PersonBasePage
{
	public abstract void setHidInfo(IData hidInfo);


	public void init(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		setHidInfo(pageData);
	}
	/**
	 * 群组信息变更
	 */
	public void updateGroup(IRequestCycle cycle) throws Exception {
		IData pageData = getData();
		IDataset rtDataset =CSViewCall.call(this,"SS.FamilyGroupSVC.updateBossGroupInfo", pageData);//群组信息变更
		IData result = new DataMap();//返回结果
		if (IDataUtil.isNotEmpty(rtDataset)){
			String rsp_code = rtDataset.getData(0).getString("RSP_CODE");
			String rsp_desc = rtDataset.getData(0).getString("RSP_DESC");
			if(StringUtils.isNotBlank(rsp_code)){
				result.put("RSP_CODE",rsp_code);
				result.put("RSP_DESC",rsp_desc);
			}
			setAjax(result);
		}
	}
}