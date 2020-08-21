
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyuserinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;

public class CustListSVC extends CSBizService
{
    public IDataset queryCustList(IData input) throws Exception
    {
        IDataset infos = CustomerInfoQry.getCustomerAndPersonInfoByPspt(input.getString("CUST_TYPE"), input.getString("PSPT_TYPE_CODE"), input.getString("PSPT_ID"));
        return infos;
    }
}
