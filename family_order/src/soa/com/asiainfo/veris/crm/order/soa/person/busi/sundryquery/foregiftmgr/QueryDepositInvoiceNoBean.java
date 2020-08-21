
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.foregiftmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ChoosePhoneQry;

public class QueryDepositInvoiceNoBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(QueryDepositInvoiceNoBean.class);

    public IDataset queryIntegrateCustInvoice(IData params, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset results = ChoosePhoneQry.getChoosePhoneInfo(param, pagination);
        return results;
    }
}
