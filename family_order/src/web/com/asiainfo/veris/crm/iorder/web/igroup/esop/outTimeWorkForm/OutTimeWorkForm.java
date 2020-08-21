package com.asiainfo.veris.crm.iorder.web.igroup.esop.outTimeWorkForm;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class OutTimeWorkForm extends EopBasePage{
	
	public abstract void setInfos(IDataset infos);
	 
	public abstract void setCondition(IData info);
	
	public abstract void setInfo(IData info);

	public void query(IRequestCycle cycle) throws Exception {
		IData condData = getData();
        IDataset setInfo = new DatasetList();
        IDataOutput dataOutput = CSViewCall.callPage(this, "SS.WorkFormSVC.queryAllWorkFormInfos", condData,getPagination("pageNav"));
        setInfo = dataOutput.getData();
        setInfos(setInfo);
        setCondition(condData);
	}	

}
