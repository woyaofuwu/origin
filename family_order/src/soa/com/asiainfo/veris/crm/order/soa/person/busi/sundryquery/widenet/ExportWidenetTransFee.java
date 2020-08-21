
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.widenet;

import org.apache.log4j.Logger;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * 
 */
public class ExportWidenetTransFee extends ExportTaskExecutor
{
    protected static Logger log = Logger.getLogger(ExportWidenetTransFee.class);

    public IDataset executeExport(IData data, Pagination pagination) throws Exception
    {
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset res = CSAppCall.call("SS.QueryWidenetTransFeeSVC.queryTransFee", data);
        return res;
    }
}
