package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpBizAuditLogDetail extends GroupBasePage{

	/**
	 * 查询稽核轨迹信息
	 * @param cycle
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-8-29
	 */
    public void queryAuditLogDetail(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	String auditId = data.getString("cond_AUDIT_ID");
    	IData param = new DataMap();
    	param.put("AUDIT_ID", auditId);
    	IDataOutput output = CSViewCall.callPage(this, "SS.QueryGrpBizAuditInfoSVC.queryGrpAuditLogDetail", param, null);  
    	IDataset infos = output.getData();
    	setInfos(infos);
    }
    
    public abstract void setInfos(IDataset datset);
    public abstract void setHintInfo(String str);
}
