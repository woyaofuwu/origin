package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceAuthBean;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;

public class BindMobilePhoneSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IData bindMobilePhone(IData data) throws Exception
	{
		/**************************合版本  duhj  2017/5/3 start*************************************/
	    String bizCodeType = data.getString("CHANNEL_ID");//渠道编码
        if (CustServiceHelper.isCustomerServiceChannel(bizCodeType))
        {
            CustServiceAuthBean bean1 = BeanManager.createBean(CustServiceAuthBean.class);
            return bean1.bindMobilePhone(data);
        }
		/**************************合版本  duhj  2017/5/3 end*************************************/
		BindMobilePhoneBean bean = BeanManager.createBean(BindMobilePhoneBean.class);
		return bean.bindMobilePhone(data);
	}
	
	public IData unBindMobilePhone(IData data) throws Exception
	{
		/**************************合版本  duhj  2017/5/3 start*************************************/
	    String bizCodeType = data.getString("CHANNEL_ID");//渠道编码
        if (CustServiceHelper.isCustomerServiceChannel(bizCodeType))
        {
            CustServiceAuthBean bean1 = BeanManager.createBean(CustServiceAuthBean.class);
            return bean1.unBindMobilePhone(data);
        }
		/**************************合版本  duhj  2017/5/3 end*************************************/
		BindMobilePhoneBean bean = BeanManager.createBean(BindMobilePhoneBean.class);
		return bean.unBindMobilePhone(data);
	}
	
	public IData releaseNotice(IData data) throws Exception
	{
		BindMobilePhoneBean bean = BeanManager.createBean(BindMobilePhoneBean.class);
		return bean.unBindMobilePhoneInform(data);
	}
}
