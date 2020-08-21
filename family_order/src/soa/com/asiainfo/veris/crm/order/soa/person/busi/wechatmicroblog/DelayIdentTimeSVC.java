package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceAuthBean;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;

public class DelayIdentTimeSVC extends CSBizService{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 客户身份凭证有效期查询
	 * @param OPR_NUMB(操作的流水号),CONTACT_ID(全网客户接触ID),SERIAL_NUMBER(客户手机号码),IDENT_CODE(客户身份凭证)
	 * @return IDENT_UNEFFT(失效时间)
	 * @throws Exception
	 */
	public IData queryUserIdentUnEffT(IData input) throws Exception{
		IData data = new DataMap();
		DelayIdentTimeBean bean = (DelayIdentTimeBean) BeanManager.createBean(DelayIdentTimeBean.class);
		data = bean.queryUserIdentUnEffT(input);
		return data;
	}
	
	/**
	 * 客户身份凭证延时
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData updateUserIdentUnEffT(IData input) throws Exception{
		/**************************合版本  duhj  2017/5/3 start*************************************/
	    String bizCodeType = input.getString("CHANNEL_ID");//渠道编码
        if (CustServiceHelper.isCustomerServiceChannel(bizCodeType))
        {
            CustServiceAuthBean bean1 = BeanManager.createBean(CustServiceAuthBean.class);
            return bean1.certificateDelay(input);
        }
		/**************************合版本  duhj  2017/5/3 end*************************************/
		IData result = new DataMap();
		DelayIdentTimeBean bean = (DelayIdentTimeBean) BeanManager.createBean(DelayIdentTimeBean.class);
		result = bean.updateUserIdentUnEffT(input);
		return result;
	}
	
	/**
	 * 客户身份凭证延时接口
	 * @param OPR_NUMB(操作流水)，CONTACT_ID(全网客户接触ID)，MSISDN，IDENT_CODE(客户身份凭证)
	 * @return IDENT_UNEFFT(失效时间)
	 * @author yf
	 * @throws Exception 
	 */
	public IData cancelUserIdentUnEffT(IData input) throws Exception{
		IData result = new DataMap();
		DelayIdentTimeBean bean = (DelayIdentTimeBean) BeanManager.createBean(DelayIdentTimeBean.class);
		result = bean.cancelUserIdentUnEffT(input);
		return result;
	}
}
