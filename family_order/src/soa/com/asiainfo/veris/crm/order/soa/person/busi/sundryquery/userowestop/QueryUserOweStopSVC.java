
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userowestop;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryUserOweStopSVC extends CSBizService
{

    /**
     * 功能：欠费停机用户查询 作者：GongGuang
     */
    public IDataset queryUserOweStop(IData inparams) throws Exception
    {
        QueryUserOweStopBean bean = BeanManager.createBean(QueryUserOweStopBean.class);
        IDataset results = bean.queryUserOweStop(inparams, getPagination());

        return results;
    }
}
