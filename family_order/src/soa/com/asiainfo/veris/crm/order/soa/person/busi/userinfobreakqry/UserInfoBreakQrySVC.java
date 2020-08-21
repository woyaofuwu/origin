
package com.asiainfo.veris.crm.order.soa.person.busi.userinfobreakqry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.monitorinfo.ManageBlackBean;


public class UserInfoBreakQrySVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	 public IDataset getUserInfo(IData param) throws Exception{
		 UserInfoBreakQryBean userInfoQryBreakBean=BeanManager.createBean(UserInfoBreakQryBean.class);
		 IDataset results= userInfoQryBreakBean.qryUserInfo(param);
		 return results;
	 }
	
	public void insertUserData(IData param) throws Exception{
		UserInfoBreakQryBean userInfoQryBreakBean=BeanManager.createBean(UserInfoBreakQryBean.class);
		userInfoQryBreakBean.insertUserData(param);
	 }
	
	public IDataset breQryUserInfo(IData param) throws Exception{
		UserInfoBreakQryBean userInfoQryBreakBean=BeanManager.createBean(UserInfoBreakQryBean.class);
		IDataset results=userInfoQryBreakBean.breQryUserInfo(param);
		return results;
	 }
	
	public IDataset delBlackUser(IData param) throws Exception{
		UserInfoBreakQryBean userInfoQryBreakBean=BeanManager.createBean(UserInfoBreakQryBean.class);
		IDataset results=userInfoQryBreakBean.delBlackUserData(param);
		return results;
	 }
	 
}