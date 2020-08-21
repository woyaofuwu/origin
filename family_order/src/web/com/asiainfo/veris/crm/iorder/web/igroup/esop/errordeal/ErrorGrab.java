package com.asiainfo.veris.crm.iorder.web.igroup.esop.errordeal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class ErrorGrab  extends CSBasePage
{
	public abstract void setInfo(IData info);
	
	public abstract void setNodeInfos(IDataset nodeInfos);
	
	public void queryFlow(IRequestCycle cycle) throws Exception
    {
		String busifromNodeId = getData().getString("BUSIFORM_NODE_ID", "");
		if(StringUtils.isEmpty(busifromNodeId))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "BUSIFORM_NODE_ID不能为空！");
		}
		IData temp = new DataMap();
		temp.put("BUSIFORM_NODE_ID", busifromNodeId);
		ServiceResponse result = BizServiceFactory.call("SS.WorkformNodeQrySVC.getNodeInfos",temp);
		IDataset nodeListSet = result.getData();
		
		setNodeInfos(nodeListSet);
    }
	
	public void submit(IRequestCycle cycle) throws Exception
	{
		String busifromNodeId = this.getData().getString("BUSIFORM_NODE_ID", "");
		String nodeId =  this.getData().getString("NODE_ID", "");
		if(StringUtils.isEmpty(busifromNodeId))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "BUSIFORM_NODE_ID不能为空！");
		}
		if(StringUtils.isEmpty(nodeId))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "NODE_ID不能为空！");
		}
		IData temp = new DataMap();
		temp.put("BUSIFORM_NODE_ID", busifromNodeId);
		temp.put("NODE_ID", nodeId);
		BizServiceFactory.call("SS.WorkformNodeQrySVC.grabNode",temp);
	}
}
