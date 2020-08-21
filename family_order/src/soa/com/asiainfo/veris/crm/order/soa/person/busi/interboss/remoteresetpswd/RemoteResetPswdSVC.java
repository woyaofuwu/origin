package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remoteresetpswd;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RemoteResetPswdSVC extends CSBizService {

	
private static final long serialVersionUID = 1L;
	
	/**
	 * 查询卡类型接口
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset queryCardType(IData input) throws Exception{
		RemoteResetPswdBean bean = BeanManager.createBean(RemoteResetPswdBean.class);
		return bean.queryCardType(input);
	}
	
	/**
	 * 查询卡类型接口落地接口
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset cardTypeQuery(IData input) throws Exception{
		RemoteResetPswdBean bean = BeanManager.createBean(RemoteResetPswdBean.class);
		return bean.cardTypeQuery(input);
	}
	
	/**
	 * 鉴权
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset openResultAuthF(IData input) throws Exception{
		RemoteResetPswdBean bean = BeanManager.createBean(RemoteResetPswdBean.class);
		return bean.openResultAuthF(input);
	}

	/**
	 * 跨区密码重置受理接口
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset passwordCZ(IData input) throws Exception{
		RemoteResetPswdBean bean = BeanManager.createBean(RemoteResetPswdBean.class);
		return bean.passwordCZ(input);
	}
	
	/**
	 * 跨区密码重置发起
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset passwordCZF(IData input) throws Exception{
		RemoteResetPswdBean bean = BeanManager.createBean(RemoteResetPswdBean.class);
		return bean.passwordCZF(input);
	}
	
	/**
	 * 下发短信验证码
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset simpleCardNotice(IData input) throws Exception{
		RemoteResetPswdBean bean = BeanManager.createBean(RemoteResetPswdBean.class);
		return bean.simpleCardNotice(input);
	}
	/**
	 * 好友号码查询
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset numCheckQuery(IData input) throws Exception{
		RemoteResetPswdBean bean = BeanManager.createBean(RemoteResetPswdBean.class);
		return bean.numCheckQuery(input);
	}
	


}
