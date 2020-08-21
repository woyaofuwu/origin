package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.notstopmigrantuser;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NotStopMigrantUserSVC extends CSBizService
{

	private static final long serialVersionUID = 1L;

	/**
     * 查询是否登记
     */
    public IData queryInfo(IData params) throws Exception
    {
    	NotStopMigrantUserBean notStopMigrantUserBean = BeanManager.createBean(NotStopMigrantUserBean.class);
        IData data = notStopMigrantUserBean.queryInfo(params);
        return data;
    }
    
    /**
     * 提交登记
     */
    public IData onTradeSubmit(IData params) throws Exception
    {
    	NotStopMigrantUserBean notStopMigrantUserBean = BeanManager.createBean(NotStopMigrantUserBean.class);
        IData data = notStopMigrantUserBean.onTradeSubmit(params);
        return data;
    }


}
