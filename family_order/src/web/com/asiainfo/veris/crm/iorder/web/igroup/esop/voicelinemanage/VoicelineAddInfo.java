package com.asiainfo.veris.crm.iorder.web.igroup.esop.voicelinemanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class VoicelineAddInfo extends CSBasePage{

	public void submitInfos(IRequestCycle cycle) throws Exception{
		IData data=getData();
		IData result= CSViewCall.callone(this, "SS.BusiCheckVoiceSVC.queryAddInfoByIbsysid", data);
		if(IDataUtil.isNotEmpty(result)){
			 CSViewException.apperr(CrmCommException.CRM_COMM_103, "本工单[" + data.getString("IBSYSID") + "]信息已补录，不要重复补录！");
		}else{
			data.put("IN_STAFF_ID", null);
			data.put("IN_DEPART_ID",null);
			CSViewCall.callone(this, "SS.BusiCheckVoiceSVC.submitAddInfos", data);
			//setCondition(data);
		}
	}
	
	public abstract void setCondition(IData condition);
	public abstract void setInfo(IData info);
	public abstract void setInfos(IDataset infos);
}
