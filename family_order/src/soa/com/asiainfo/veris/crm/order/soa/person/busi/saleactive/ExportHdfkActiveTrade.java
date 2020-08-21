package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;


import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ExportHdfkActiveTrade extends ExportTaskExecutor{

	@Override
	public IDataset executeExport(IData input, Pagination pagination) throws Exception {
		
		input.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		IDataset set=CSAppCall.call("SS.HdfkActiveTradeQuerySVC.queryTrade", input);
		if(IDataUtil.isNotEmpty(set)){
			for(int i=0,size=set.size();i<size;i++){
				IData data=set.getData(i);
				data.put("DEAL_STATE_CODE", StaticUtil.getStaticValue("HDFK_DEAL_STATE_CODE", data.getString("DEAL_STATE_CODE","")));
				
			}
		}
		
		
        return set;
	}
	
	
}
