
package com.asiainfo.veris.crm.order.soa.person.busi.apnusermgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class ApnUserBindingForOlcomSVC extends CSBizService
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 根据UserId来分页查询专用APN绑定信息
	 * @param data
	 * @return
	 * @throws Exception
	 */
    public IDataset queryUserApnInfoByUserId(IData data) throws Exception
    {
    	ApnUserBindingForOlcomBean bean = BeanManager.createBean(ApnUserBindingForOlcomBean.class);
        return bean.queryUserApnInfoByUserId(data, getPagination());
    }
    
    /**
     * 查询专用APN资料信息
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryAllUserApn(IData data) throws Exception
    {
    	ApnUserBindingForOlcomBean bean = BeanManager.createBean(ApnUserBindingForOlcomBean.class);
        return bean.queryAllUserApn(data);
    }
    
    /**
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryUserApnInfoByOther(IData data) throws Exception
    {
    	ApnUserBindingForOlcomBean bean = BeanManager.createBean(ApnUserBindingForOlcomBean.class);
        return bean.queryUserApnInfoByOther(data);
    }
    
    /**
     * 查询专用APN资料信息
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryUserApnByName(IData data) throws Exception
    {
    	ApnUserBindingForOlcomBean bean = BeanManager.createBean(ApnUserBindingForOlcomBean.class);
        return bean.queryUserApnByName(data);
    }
    
    
}
