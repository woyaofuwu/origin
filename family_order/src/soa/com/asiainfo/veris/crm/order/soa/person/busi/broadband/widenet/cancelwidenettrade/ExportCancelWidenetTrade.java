package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.cancelwidenettrade;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class ExportCancelWidenetTrade extends ExportTaskExecutor{

	@Override
    public IDataset executeExport(IData input, Pagination pagination) throws Exception
    {
		IData data = input.subData("cond", true);
		String startDate = data.getString("START_DATE", "");
        if (StringUtils.isNotBlank(startDate))
        {
        	data.put("ACCEPT_MONTH", startDate.substring(5, 7));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        
        return CSAppCall.call("SS.CancelWidenetTradeService.queryWidenetCancelTradeInfo", data);

    }
	
}
