package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SynchronizeCommoditySVC extends CSBizService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 产品信息同步
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public IData synchronizeAllCommodity(IData inparam) throws Exception
	{
		SynchronizeCommodityBean bean = BeanManager.createBean(SynchronizeCommodityBean.class);
		return bean.synchronizeAllCommodity(inparam);
	}

}
