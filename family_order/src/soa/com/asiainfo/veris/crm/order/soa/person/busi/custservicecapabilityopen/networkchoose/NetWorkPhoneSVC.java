package com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.networkchoose;  

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NetWorkPhoneSVC extends CSBizService{
	
	/**一级客服省级业务能力开放
	 * 可售号码查询
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */

	public IData querySalePhone(IData data) throws Exception {
		NetWorkPhoneBean bean = BeanManager.createBean(NetWorkPhoneBean.class);
		return bean.querySalePhone(data);
	}
	/**一级客服省级业务能力开放
	 * 号码预占
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData netSaleNumOccupy(IData data) throws Exception {
		NetWorkPhoneBean bean = BeanManager.createBean(NetWorkPhoneBean.class);
		return bean.netSaleNumOccupy(data);
	}

}
