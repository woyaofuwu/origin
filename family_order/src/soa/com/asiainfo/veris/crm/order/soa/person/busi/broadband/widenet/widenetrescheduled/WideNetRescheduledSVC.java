package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetrescheduled;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author zhengkai
 *宽带改期
 */
public class WideNetRescheduledSVC extends CSBizService
{	
	 //查询按钮
	 public IDataset queryWideNetInfo(IData input) throws Exception
	 {
		 WideNetRescheduledBean bean = (WideNetRescheduledBean) BeanManager.createBean(WideNetRescheduledBean.class);
		 return bean.queryWideNetInfo(input);
	 }
	 
	 //提交按钮
	 public IDataset onTradeSubmit(IData input) throws Exception
	 {
		 WideNetRescheduledBean bean = (WideNetRescheduledBean) BeanManager.createBean(WideNetRescheduledBean.class);
		 return bean.onTradeSubmit(input);
	 } 
}
