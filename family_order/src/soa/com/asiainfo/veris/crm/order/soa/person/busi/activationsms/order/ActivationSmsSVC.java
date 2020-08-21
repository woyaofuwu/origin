package com.asiainfo.veris.crm.order.soa.person.busi.activationsms.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import org.apache.commons.collections.CollectionUtils;

/**
 * 系统自动触发号卡激活提醒短信
 * @author zlx
 * 2018.9.13
 */
public class ActivationSmsSVC extends CSBizService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 给操作员工号下面未激活的号码发短信
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData activationSms(IData data) throws Exception
	{
		ActivationSmsBean bean = BeanManager.createBean(ActivationSmsBean.class);
		return bean.execute(data);
	}


}
