
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userimei;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUserImeiQry;

public class QueryUserImeiBean extends CSBizBean
{
    /**
     * 功能：用于查询用户IMEI信息 作者：GongGuang
     */
    public IDataset queryUserImei(IData data, Pagination page) throws Exception
    {
        String serialNum = data.getString("SERIAL_NUMBER", "");

        IDataset dataSet = QueryUserImeiQry.queryUserImeiBySN(serialNum, "UM", "YX03", page);

        return dataSet;
    }
}
