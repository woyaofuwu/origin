package com.asiainfo.veris.crm.iorder.web.igroup.esop.busicheck;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class BusiWorkformVoice extends CSBasePage{
	public abstract void setInfo(IData info);
	public abstract void setInfos(IDataset infos);
	public abstract void setCondition(IData condition);
	public abstract void setRowIndex(int rowIndex);
    public abstract void setInfoCount(long infoCount);
	public void initPage(IRequestCycle cycle) throws Exception{
		
	}
	
	public void queryBusiWorkformInfos(IRequestCycle cycle) throws Exception{
		IData condData = getData();
		IDataset busiInfos = CSViewCall.call(this, "SS.BusiCheckVoiceSVC.getWorkformNewAttrList", condData);
		setInfos(busiInfos);
		setInfoCount(busiInfos.size());
	}
	
	public void queryBusiWorkformReviewDetailInfos(IRequestCycle cycle) throws Exception{
		IData condData = getData();
		IDataset busiInfos = CSViewCall.call(this, "SS.WorkFormSVC.queryBusiWorkformReviewDetailInfos", condData);
		
		this.setInfos(busiInfos);
		this.setCondition(condData);
	}

}
