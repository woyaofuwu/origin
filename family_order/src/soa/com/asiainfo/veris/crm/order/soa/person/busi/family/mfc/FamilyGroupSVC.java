package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;


import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class FamilyGroupSVC extends CSBizService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 省侧查boss群组信息接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset qryBossGroupInfo(IData input) throws Exception{
		FamilyGroupBean bean = new FamilyGroupBean();
		return bean.qryBossGroupInfo(input);
	}
	/**
	 * 省组信息变更
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset updateBossGroupInfo(IData input) throws Exception{
		FamilyGroupBean bean = new FamilyGroupBean();
		return bean.updateBossGroupInfo(input);//这里要改  切记！
	}

	/**
	 * 用户状态变更接口
	 * @param input
	 * @return
	 */
	public IDataset changStateSendBBoss(IData input) throws Exception{
		FamilyGroupBean bean = new FamilyGroupBean();
		return bean.changStateSendBBoss(input);
	}
	/**
	 * 群组信息同步落地接口
	 * @param input
	 * @return
	 */
	public IData changeGruopInfoSync(IData input) throws Exception{
		FamilyGroupBean bean = new FamilyGroupBean();
		return bean.changeGruopInfoSync(input);
	}
}
