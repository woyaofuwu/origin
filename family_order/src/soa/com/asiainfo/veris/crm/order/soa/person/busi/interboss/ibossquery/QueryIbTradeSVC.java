package com.asiainfo.veris.crm.order.soa.person.busi.interboss.ibossquery;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryIbTradeSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;

	public IDataset qryIbTradeBySNAndReqTime(IData data) throws Exception
    {
    	QueryIbTradeBean bean = BeanManager.createBean(QueryIbTradeBean.class);
        return bean.qryIbTradeBySNAndReqTime(data, this.getPagination());
    }
	
	public IDataset qryTradeTypeList(IData data) throws Exception
	{
		QueryIbTradeBean bean = BeanManager.createBean(QueryIbTradeBean.class);
		return bean.qryTradeTypeList(data,this.getPagination());
	}
	
	public IDataset qryTradelogbyTRANSIDO(IData data) throws Exception
	{
		QueryIbTradeBean bean = BeanManager.createBean(QueryIbTradeBean.class);
		return bean.qryTradelogbyTRANSIDO(data,this.getPagination());
	}
}
