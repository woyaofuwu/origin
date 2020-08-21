
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querycusttdinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.TdCustInfoQry;

public class QueryCustTDInfoBean extends CSBizBean
{

    public IDataset getCustTDTradeInfo(IData param, Pagination pagination) throws Exception
    {
        String serial_number = param.getString("SERIAL_NUMBER");
        return TdCustInfoQry.qryTdCustInfos(serial_number, pagination);
    }
}
