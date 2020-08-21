package com.asiainfo.veris.crm.order.soa.person.busi.sparkplansmgr;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;

public class QueryAssignLog extends CSExportTaskExecutor{

	@Override
    public IDataset executeExport(IData paramIData, Pagination paramPagination) throws Exception
    {
        // TODO Auto-generated method stub
		
        IDataset result = CSAppCall.call("SS.SparkPlansMgrSVC.queryAssignLog", paramIData);
        for(int i = 0, size = result.size(); i < size; i++)
        {
        	IData data = result.getData(i);
        	data.put("CITY_NAME", StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", data.getString("CITY_CODE")));
        	data.put("DEPART_NAME", StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", data.getString("DEPART_ID")));
        }

        return result;
    }
}
