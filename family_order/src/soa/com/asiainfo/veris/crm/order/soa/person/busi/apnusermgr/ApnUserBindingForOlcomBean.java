
package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;



public class ApnUserBindingForOlcomBean extends CSBizBean
{
	/**
	 * 根据UserId来分页查询专用APN绑定信息
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public IDataset queryUserApnInfoByUserId(IData param,Pagination pagination) throws Exception
	{
		return ApnUserBindingForOlcomQry.queryUserApnInfoByUserId(param, pagination);
	}
	
	/**
	 * 查询专用APN资料信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset queryAllUserApn(IData param) throws Exception
	{
		return ApnUserBindingForOlcomQry.queryAllUserApn(param);
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset queryUserApnInfoByOther(IData param) throws Exception
	{
		return ApnUserBindingForOlcomQry.queryUserApnInfoByOther(param);
	}
	
	/**
	 * 根据APN_NAME查询专网APN的信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset queryUserApnByName(IData param) throws Exception
	{
		return ApnUserBindingForOlcomQry.queryUserApnByName(param);
	}
	
	
}
