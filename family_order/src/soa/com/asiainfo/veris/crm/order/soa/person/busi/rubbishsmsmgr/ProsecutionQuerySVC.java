
package com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ProsecutionQuerySVC extends CSBizService
{

    public IDataset queryProsecution(IData data) throws Exception
    {
        ProsecutionQueryBean bean = new ProsecutionQueryBean();
        return bean.queryProsecution(data, this.getPagination());
    }
}
