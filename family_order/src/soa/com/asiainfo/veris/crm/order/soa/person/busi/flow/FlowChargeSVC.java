
package com.asiainfo.veris.crm.order.soa.person.busi.flow;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class FlowChargeSVC extends CSBizService
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 流量经营业务接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryBalanceDetail(IData data) throws Exception
    {
    	FlowChargeBean bean = BeanManager.createBean(FlowChargeBean.class);
        return bean.queryBalanceDetail(data);
    }
    
    /**
     * 流量商品接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset flowProdQry(IData data) throws Exception
    {
    	FlowChargeBean bean = BeanManager.createBean(FlowChargeBean.class);
        return bean.flowProdQry(data);
    }
    
    /**
     * 流量商品快速充值查询接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryFastPayInfos(IData data) throws Exception
    {
    	FlowChargeBean bean = BeanManager.createBean(FlowChargeBean.class);
        return bean.qryFastPayInfos(data);
    }
    
    /**
     * 流量提取返销业务接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryCancelableFlowInfos(IData data) throws Exception
    {
    	FlowChargeBean bean = BeanManager.createBean(FlowChargeBean.class);
        return bean.queryCancelableFlowInfos(data);
    }
    
    /***
     * 流量阀值通知 <boss->crm->iboss->能力开放平台>
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset flowThresholdNotice(IData param) throws Exception
    {
    	FlowChargeBean bean = BeanManager.createBean(FlowChargeBean.class);

        return bean.flowThresholdNotice(param);
    }
}
