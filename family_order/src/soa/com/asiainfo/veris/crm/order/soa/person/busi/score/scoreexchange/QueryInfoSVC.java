
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryInfoSVC extends CSBizService
{
    public IData queryHbdzq(IData input) throws Exception
    {
        ScoreExchangeBean scoreExchangeBean = (ScoreExchangeBean) BeanManager.createBean(ScoreExchangeBean.class);
        return scoreExchangeBean.queryHbdzq(input);
    }
}
