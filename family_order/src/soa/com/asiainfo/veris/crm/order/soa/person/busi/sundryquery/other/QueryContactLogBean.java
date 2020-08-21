
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log.ContactLogQry;

public class QueryContactLogBean extends CSBizBean
{
    public IDataset queryContactLog(IData inparams, Pagination pagination) throws Exception
    {
        if (inparams.getString("QUERY_TYPE", "").equals("0"))
        {
            return ContactLogQry.getContactInfosById(inparams.getString("CUST_CONTACT_ID"), pagination);
        }
        else if (inparams.getString("QUERY_TYPE", "").equals("1"))
        {
            return ContactLogQry.getContactInfosByLog(inparams.getString("STAFF_ID"), inparams.getString("START_DATE") + SysDateMgr.START_DATE_FOREVER, inparams.getString("END_DATE") + SysDateMgr.END_DATE, pagination);
        }
        return null;
    }
}
