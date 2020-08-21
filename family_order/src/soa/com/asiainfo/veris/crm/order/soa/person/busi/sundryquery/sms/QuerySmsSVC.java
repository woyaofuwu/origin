
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.sms;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QuerySmsSVC extends CSBizService
{

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
     * 功能：用于短信查询 作者：GongGuang
     */
    public IDataset querySms(IData inparams) throws Exception
    {
        QuerySmsBean bean = BeanManager.createBean(QuerySmsBean.class);
        IDataset results = bean.querySms(inparams, getPagination());
        return results;
    }
}
