package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CheckUserPWDSVC extends CSBizService{
	static final Logger logger = Logger.getLogger(CheckUserPWDSVC.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 客户密码鉴权
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData checkUserPWD(IData data) throws Exception
	{
		CheckUserPWDBean bean = BeanManager.createBean(CheckUserPWDBean.class);
		return bean.checkUserPWD4HL(data);
	}

}
