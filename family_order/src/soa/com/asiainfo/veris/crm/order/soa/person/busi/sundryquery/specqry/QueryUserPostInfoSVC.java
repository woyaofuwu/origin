
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.specqry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPostInfoQry;

public class QueryUserPostInfoSVC extends CSBizService
{
    /**
     * 执行邮寄信息查询
     * 
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryUserPostInfo(IData input) throws Exception
    {
        return UserPostInfoQry.qryUserPostInfo(input, this.getPagination());
    }

}
