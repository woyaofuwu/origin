
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.prosecution;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryProsecutionSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IData queryInitCondition(IData param) throws Exception
    {
        IData result = new DataMap();
        String startDate = SysDateMgr.getFirstDayOfThisMonth();
        String endDate = SysDateMgr.getSysDate();
        result.put("START_DATE", startDate);
        result.put("END_DATE", endDate);
        return result;
    }

    /**
     * 功能：用于查询垃圾短信 作者：GongGuang
     */
    public IDataset queryProsecution(IData data) throws Exception
    {
        QueryProsecutionBean bean = (QueryProsecutionBean) BeanManager.createBean(QueryProsecutionBean.class);
        return bean.queryProsecution(data, getPagination());
    }
}
