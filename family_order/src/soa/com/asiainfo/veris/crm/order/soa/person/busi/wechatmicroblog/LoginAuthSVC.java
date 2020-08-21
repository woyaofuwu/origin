package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceAuthBean;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;

public class LoginAuthSVC extends CSBizService{
	static final Logger logger = Logger.getLogger(LoginAuthSVC.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 凭证申请
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData applyIdentInfo(IData data)throws Exception
	{
		/**************************合版本  duhj  2017/5/3 start*************************************/
	    String bizCodeType = data.getString("CHANNEL_ID");//渠道编码
        if (CustServiceHelper.isCustomerServiceChannel(bizCodeType))
        {
            CustServiceAuthBean bean1 = BeanManager.createBean(CustServiceAuthBean.class);
            return bean1.certificateRequest(data);
        }
		/**************************合版本  duhj  2017/5/3 end*************************************/

		LoginAuthBean bean = BeanManager.createBean(LoginAuthBean.class);
		return bean.applyIdentInfo(data);
	}
	
	/**
	 * 凭证校验
	 * @param data
	 * @throws Exception
	 */
	public IData accoutIdentAuth(IData data)
			throws Exception {
		/**************************合版本  duhj  2017/5/3 start*************************************/
	    String bizCodeType = data.getString("CHANNEL_ID");//渠道编码
        if (CustServiceHelper.isCustomerServiceChannel(bizCodeType))
        {
            CustServiceAuthBean bean1 = BeanManager.createBean(CustServiceAuthBean.class);
            return bean1.checkCertificate(data);
        }
		/**************************合版本  duhj  2017/5/3 end*************************************/

		LoginAuthBean bean = BeanManager.createBean(LoginAuthBean.class);
		return bean.accoutIdentAuth(data);
	}
	
	 @Override
	 public final void setTrans(IData input)
	 {
		 if ("SS.LoginAuthSVC.accoutIdentAuth".equals(this.getVisit().getXTransCode())){
			 input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
	    	}
	    }
}
