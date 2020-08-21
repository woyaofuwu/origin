
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.whiteCardChoiceSn;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 白开放号
 * @author zhengkai5
 * 
 * */
public class WhiteCardChoiceSnSVC extends CSBizService
{
	
	/**
	 * 第一步：网上选号确认开通接口
	 * @throws Exception 
	 * */
	public IData sureChoiceSn(IData param) throws Exception
	{
		WhiteCardChoiceSnBean bean = BeanManager.createBean(WhiteCardChoiceSnBean.class);
		return bean.sureChoiceSn(param) ;
	}
	
	/**
	 * 
	 * 第二步：开户请求校验接口
	 * @throws Exception 
	 * */
	public IData checkUserRequest(IData param) throws Exception
	{
		WhiteCardChoiceSnBean bean = BeanManager.createBean(WhiteCardChoiceSnBean.class);
		return bean.checkUserRequest(param);
	}
	
	/**
	 * 第三步：实时写卡数据获取接口
	 * @throws Exception 
	 * */
	public IData getWriteCartInfo (IData param) throws Exception
	{
		WhiteCardChoiceSnBean bean = BeanManager.createBean(WhiteCardChoiceSnBean.class);
		return bean.getWriteCartInfo(param);
	} 
	
	/**
	 * 第四步：用户确认激活接口
	 * @throws Exception 
	 * */
	public IData surePersonUserCreate (IData param) throws Exception 
	{
		WhiteCardChoiceSnBean bean = BeanManager.createBean(WhiteCardChoiceSnBean.class);
		return bean.surePersonUserCreate(param);
	}
	
}