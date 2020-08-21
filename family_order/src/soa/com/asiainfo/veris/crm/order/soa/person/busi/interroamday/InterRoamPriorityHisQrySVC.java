
package com.asiainfo.veris.crm.order.soa.person.busi.interroamday;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;


public class InterRoamPriorityHisQrySVC extends CSBizService
{
	private static final long serialVersionUID = 1716091969123399467L;


	/**
	 * 订购关系历史优先级查询
	 * @param input
	 * @param roamInfoList
	 * @return
	 * @throws Exception
	 */
	public IDataset interRoamPriorityHisQry(IData input) throws Exception
	{
		InterRoamPriorityHisQryBean prioHisBean = BeanManager.createBean(InterRoamPriorityHisQryBean.class);
		return prioHisBean.interRoamPriorityHisQry(input);
	}
}
