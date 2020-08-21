
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.hintlog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.HintLogManageQry;

public class HintLogManageBean extends CSBizBean
{

    public IDataset queryHintLog(IData params, Pagination pagination) throws Exception
    {
        IDataset results = HintLogManageQry.queryHintLog(params, pagination);
        return results;
    }

}
