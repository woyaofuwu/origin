package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizServiceIntf;



public class AbilityPlatReturnOrderExcSVC extends CSBizServiceIntf {
	static Logger logger = Logger.getLogger(AbilityPlatReturnOrderExcSVC.class);
	 
	private static final long serialVersionUID = 1L;
	 

    /**
     * 执行退订业务
     *  
      *  SS.AbilityPlatReturnOrderExcSVC.tradeRegItem   
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset tradeRegItem(IData input) throws Exception
    {
        AbilityPlatReturnOrderExcBean bean = (AbilityPlatReturnOrderExcBean) BeanManager.createBean(AbilityPlatReturnOrderExcBean.class);
        return bean.tradeRegItem(input);
    }
    /**
     * 退订定时任务执行接口
     *  
      *  SS.AbilityPlatReturnOrderExcSVC.processOrder
     * @param input
     * @return
     * @throws Exception
     */
    public IData processOrder(IData input) throws Exception
    {
        AbilityPlatReturnOrderExcBean bean = (AbilityPlatReturnOrderExcBean) BeanManager.createBean(AbilityPlatReturnOrderExcBean.class);
        return bean.processOrder(input);
    }
    
    /**
     * 综合退订接口
     * CIP00063
     * SS.AbilityPlatSVC.returnOrderInfo 
     * @param input
     * @return
     * @throws Exception
     */
    public IData returnOrderInfo(IData input) throws Exception
    {
        AbilityPlatReturnOrderExcBean bean = (AbilityPlatReturnOrderExcBean) BeanManager.createBean(AbilityPlatReturnOrderExcBean.class);
        return bean.returnOrderInfo(input);
    }

    /**
     * 宽带办理资格校验
     * CIP00067
      *  SS.AbilityPlatSVC.checkOrderBroadband   
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkOrderBroadband(IData input) throws Exception
    {
        AbilityPlatReturnOrderExcBean bean = (AbilityPlatReturnOrderExcBean) BeanManager.createBean(AbilityPlatReturnOrderExcBean.class);
        return bean.checkOrderBroadband(input);
    }
}