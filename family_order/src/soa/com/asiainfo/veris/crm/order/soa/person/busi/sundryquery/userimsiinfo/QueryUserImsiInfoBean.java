
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userimsiinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUserImsiInfoQry;

public class QueryUserImsiInfoBean extends CSBizBean
{

    /**
     * 功能：获取IMSI数据文件FTP配置参数 作者：GongGuang
     */
    public IDataset getImsiFtpParams(IData data, Pagination page) throws Exception
    {
        IDataset dataSet = QueryUserImsiInfoQry.getImsiFtpParams(page);
        return dataSet;
    }
}
