
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DelFamilyNetMemberSVC extends CSBizService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 100529★★亲亲网成员批量删除校验接口 @add yanwu
	 * @param input
	 * @return IData
	 * @throws Exception
	 */
	public IData familyNetMemBatCheck(IData input) throws Exception
    {
		DelFamilyNetMemberBean bean = BeanManager.createBean(DelFamilyNetMemberBean.class);
        return bean.familyNetMemBatCheck(input);
    }
	
	/**
	 * 100166★★亲亲网成员批量删除接口
	 * @param input
	 * @return IData
	 * @throws Exception
	 */
    public IData familyNetMemBatDeal(IData input) throws Exception
    {
        DelFamilyNetMemberBean bean = BeanManager.createBean(DelFamilyNetMemberBean.class);
        return bean.familyNetMemBatDeal(input);
    }

}
