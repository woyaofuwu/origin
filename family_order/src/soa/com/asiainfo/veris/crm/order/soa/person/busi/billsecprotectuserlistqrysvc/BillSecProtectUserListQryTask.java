
package com.asiainfo.veris.crm.order.soa.person.busi.billsecprotectuserlistqrysvc;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;  
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall; 

public class BillSecProtectUserListQryTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	IData input = param.subData("cond", true); 
    	input.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
    	  
        IDataset dataset = CSAppCall.call("SS.BillSecProtectUserListQrySVC.qryBillSecProtectUserList", input); 
        return dataset;
    } 
}
