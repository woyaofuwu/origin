
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

@SuppressWarnings("serial")
public class ScoreExchangeSVC extends CSBizService
{
    public IData getCommInfo(IData input) throws Exception
    {
        ScoreExchangeBean scoreExchangeBean = (ScoreExchangeBean) BeanManager.createBean(ScoreExchangeBean.class);
        return scoreExchangeBean.getCommInfo(input);
    }

    public IDataset queryCardRes(IData input) throws Exception
    {
        ScoreExchangeBean scoreExchangeBean = (ScoreExchangeBean) BeanManager.createBean(ScoreExchangeBean.class);
        return scoreExchangeBean.queryCardState(input);
    }

    public IData queryObjectBySN(IData input) throws Exception
    {
        ScoreExchangeBean scoreExchangeBean = (ScoreExchangeBean) BeanManager.createBean(ScoreExchangeBean.class);
        return scoreExchangeBean.queryObjectBySN(input);
    }
    
    public IDataset getEmzData(IData input) throws Exception
    {
    	ScoreExchangeBean scoreExchangeBean = (ScoreExchangeBean) BeanManager.createBean(ScoreExchangeBean.class);
        return scoreExchangeBean.getEmzData(input);
    }
}
