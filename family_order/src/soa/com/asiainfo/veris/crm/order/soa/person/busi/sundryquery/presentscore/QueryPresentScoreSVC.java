
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.presentscore;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;

public class QueryPresentScoreSVC extends CSBizService
{
    public IDataset queryArea(IData input) throws Exception
    {
        return UAreaInfoQry.qryAreaByParentAreaCode(CSBizBean.getVisit().getStaffEparchyCode());
    }

    public IDataset queryPresentScore(IData input) throws Exception
    {
        QueryPresentScoreBean queryPresentScoreBean = (QueryPresentScoreBean) BeanManager.createBean(QueryPresentScoreBean.class);
        return queryPresentScoreBean.queryPresentScore(input, getPagination());
    }
}
