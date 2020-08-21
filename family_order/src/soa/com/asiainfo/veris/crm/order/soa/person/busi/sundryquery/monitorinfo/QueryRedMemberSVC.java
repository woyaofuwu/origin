
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.monitorinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SmsRedmemberQry;

public class QueryRedMemberSVC extends CSBizService
{
    /**
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryRedMemberInfos(IData input) throws Exception
    {
        IDataset result = new DatasetList();

        result = SmsRedmemberQry.queryListByCodeCodeParser(input, getPagination());

        return result;
    }
}
