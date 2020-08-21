
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userowestop;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUserOweStopQry;

/**
 * 功能：欠费停机用户查询 作者：GongGuang
 */
public class QueryUserOweStopBean extends CSBizBean
{
    /**
     * 功能：欠费停机用户查询
     */
    public IDataset queryUserOweStop(IData inparams, Pagination page) throws Exception
    {
        String psptKind = inparams.getString("PSPT_KIND");
        String psptId = inparams.getString("PSPT_ID");
        String custName = inparams.getString("CUST_NAME");
        IDataset resultList = new DatasetList();
        if (psptId.length() > 0)
        {
            resultList = QueryUserOweStopQry.queryUserOweStopByPsptId(psptId, psptKind, page);
        }
        if (custName.length() > 0)
        {
            resultList = QueryUserOweStopQry.queryUserOweStopByCustName(custName, page);
        }
        return resultList;
    }
}
