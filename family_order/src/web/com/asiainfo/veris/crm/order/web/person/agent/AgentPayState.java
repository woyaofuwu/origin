package com.asiainfo.veris.crm.order.web.person.agent;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class AgentPayState extends PersonBasePage {

	public void modAgentPayInfoState(IRequestCycle cycle) throws Exception {
		IData data = this.getData();
		//System.out.println("queryAgentPayInfosxxxxxxxxxxxxxxxxxxxxxxxxxxxxxAgentPayState17 "+data);

		data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset results = CSViewCall.call(this, "SS.AgentPayStateSVC.modAgentPayInfoState", data);
		this.setAjax(results.getData(0));
	}
 
	public void onInitTrade(IRequestCycle cycle) throws Exception {
		
	}
    
	public void queryAgentPayInfos(IRequestCycle cycle) throws Exception {
		//System.out.println("queryAgentPayInfosxxxxxxxxxxxxxxxxxxxxxxxxxxxxxAgentPayState29 "+"yyyyyyyyyyyyyyyyyyyyyyyy");
		IData data = getData();
		
//		data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		//System.out.println("queryAgentPayInfosxxxxxxxxxxxxxxxxxxxxxxxxxxxxxAgentPayState33"+data);
		Pagination page = getPagination("recordNav");
		
		//System.out.println("queryAgentPayInfosxxxxxxxxxxxxxxxxxxxxxxxxxxxxxAgentPayState36 "+data);
		//System.out.println("queryAgentPayInfosxxxxxxxxxxxxxxxxxxxxxxxxxxxxxAgentPayState37 "+page);
		IDataOutput result = CSViewCall.callPage(this, "SS.AgentPayStateSVC.queryAgentPayInfos", data, page);
		//System.out.println("queryAgentPayInfosxxxxxxxxxxxxxxxxxxxxxxxxxxxxxAgentPayState39 "+result);
		//System.out.println("queryAgentPayInfosxxxxxxxxxxxxxxxxxxxxxxxxxxxxxAgentPayState40 "+result.getDataCount());
		setRecordCount(result.getDataCount());
		//System.out.println("queryAgentPayInfosxxxxxxxxxxxxxxxxxxxxxxxxxxxxxAgentPayState42 "+result.getData());
		setCond(data); // 保留查询条件
		
		setInfos(result.getData());
	}
	
	public abstract void setCond(IData cond);

	public abstract void setInfo(IData info);

	public abstract void setInfos(IDataset infos);

	public abstract void setRecordCount(long recordCount);
}
