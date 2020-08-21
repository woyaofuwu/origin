
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.adjustscore;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryAdjustScoreSVC extends CSBizService
{
    public IDataset getAdjustScore(IData input) throws Exception
    {
        QueryAdjustScoreBean queryAdjustScoreBean = (QueryAdjustScoreBean) BeanManager.createBean(QueryAdjustScoreBean.class);
        return queryAdjustScoreBean.getAdjustScore(input, getPagination());
    }
}
