package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceAuthBean;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;

public class RandomPWDSVC extends CSBizService
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 生成随机密码
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData applyPWD(IData data)throws Exception
	{
		RandomPWDBean bean = BeanManager.createBean(RandomPWDBean.class);
		/******************************合版本 duhj  2017/5/3 start***************************************/
		String bizCodeType = data.getString("CHANNEL_ID");//渠道编码
		if (CustServiceHelper.isCustomerServiceChannel(bizCodeType))
		{
		    CustServiceAuthBean bean1 = BeanManager.createBean(CustServiceAuthBean.class);
	        return bean1.randomPassword(data);
		}
		/******************************合版本 duhj  2017/5/3 end***************************************/

		return bean.applyPWD(data);
	}
	
	/**
	 * 随机密码校验
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData checkPWD(IData data)throws Exception
	{
		RandomPWDBean bean = BeanManager.createBean(RandomPWDBean.class);
		return bean.checkPWD(data);
	}

}
