package com.asiainfo.veris.crm.iorder.soa.family.busi.destroy;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 家庭注销SVC
 * @author wuwangfeng
 */
public class FamilyDestroySVC extends CSBizService {

	private static final long serialVersionUID = 1L;	
	
	/**
	 * 查询家庭成员及其关系，传入手机号码，手机号的USER_ID、路由地州
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData loadFamilyMember(IData input) throws Exception
    {
		FamilyDestroyBean bean = BeanManager.createBean(FamilyDestroyBean.class);
        return bean.loadFamilyMember(input);
    }
	
	/**
	 * 查询家庭虚拟用户、成员及其关系，传入主卡手机号码
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryAllMemberRelas(IData input) throws Exception
    {
		FamilyDestroyBean bean = BeanManager.createBean(FamilyDestroyBean.class);
		String serialNumberB = input.getString("SERIAL_NUMBER");
        return null;// bean.queryAllUserFusionRelasBySnB(serialNumberB);
    }
	
}
