package com.asiainfo.veris.crm.iorder.web.igroup.esop.eoms;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSBasePage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class ProcessReminder extends CSBasePage
{
    public abstract void setInfos(IDataset infos);
    
    public abstract void setCondition(IData condition);
    
    public void initPage(IRequestCycle cycle) throws Exception
    {
		IData data = this.getData();
		String ibsysid = data.getString("IBSYSID", "");
		String recordNum = StringUtils.isEmpty(data.getString("RECORD_NUM", ""))? "0" : data.getString("RECORD_NUM", "");//默认取公共数据
		String operType = StringUtils.isEmpty(data.getString("OPER_TYPE", ""))? "notifyWorkSheet" : data.getString("OPER_TYPE", "");//默认取阶段通知的数据
		IData param = new DataMap();
		param.put("IBSYSID", ibsysid);
		param.put("OPER_TYPE", operType);
		param.put("RECORD_NUM", recordNum);
        IDataset siInfos = CSViewCall.call(this, "SS.EopAttrInfoSVC.qryByOperType", param);
        this.setInfos(siInfos);
        
        IData condition = new DataMap();
        condition.put("OPER_TYPE", "suggestWorkSheet");
        condition.put("IBSYSID", data.getString("IBSYSID", ""));
        condition.put("BUSIFORM_ID", data.getString("BUSIFORM_ID", ""));
        condition.put("SUB_IBSYSID", data.getString("SUB_IBSYSID", ""));
        condition.put("RECORD_NUM", data.getString("RECORD_NUM", ""));
        condition.put("SERIALNO", data.getString("SERIALNO", ""));
        
        this.setCondition(condition);
    }
    
    public void submit(IRequestCycle cycle) throws Exception
	{
		IData data = this.getData();
		
		CSViewCall.call(this, "SS.EopAttrInfoSVC.saveInfo", data);
	}
}
