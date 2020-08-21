package com.asiainfo.veris.crm.iorder.web.igroup.esop.readwork;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class CancelRead extends CSBasePage
{
	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData info);
	
    public void initPage(IRequestCycle cycle) throws Exception
    {
    	IData data = this.getData();
    	IData temp = new DataMap();
    	temp.put("IBSYSID", data.getString("IBSYSID", ""));
    	temp.put("OPER_TYPE", data.getString("OPER_TYPE", ""));
    	temp.put("RECORD_NUM", data.getString("RECORD_NUM", ""));
    	IDataset eomsInfos = CSViewCall.call(this, "SS.WorkformEomsVC.qryEomsByIbsysIdOperType", temp);
    	
    	if(DataUtils.isNotEmpty(eomsInfos))
    	{
    		IData param = new DataMap();
    		param.put("SUB_IBSYSID", eomsInfos.first().getString("SUB_IBSYSID", ""));
    		param.put("GROUP_SEQ", eomsInfos.first().getString("GROUP_SEQ", ""));
    		IDataset eomsAttrInfos = CSViewCall.call(this, "SS.WorkformAttrSVC.qryAttrBySubIbsysidAndGroupseq", param);
    		
    		setInfos(eomsAttrInfos);
    	}
    }
}
