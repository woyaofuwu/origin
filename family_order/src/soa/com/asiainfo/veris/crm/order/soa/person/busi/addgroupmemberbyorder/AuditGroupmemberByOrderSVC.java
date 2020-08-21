package com.asiainfo.veris.crm.order.soa.person.busi.addgroupmemberbyorder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AuditGroupmemberByOrderSVC extends CSBizService
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 查询集团
	 */
	public IDataset queryGrps(IData input) throws Exception {
		AuditGroupmemberByOrderBean groupBean = BeanManager.createBean(AuditGroupmemberByOrderBean.class);
		return groupBean.queryGrps(input, getPagination());
	}
	/**
	 * 查询集团下的所有通讯录成员
	 */
	public IDataset queryGrpBooktInfos(IData param) throws Exception {
		AuditGroupmemberByOrderBean groupBean = BeanManager.createBean(AuditGroupmemberByOrderBean.class);
		return groupBean.queryGrpBooktInfos(param);
	}
	/**
	 * 提交稽核
	 */
	public void submitAudit(IData param) throws Exception {
		AuditGroupmemberByOrderBean groupBean = BeanManager.createBean(AuditGroupmemberByOrderBean.class);
		groupBean.submitAudit(param);
	}
	/**
	 * 更新成员表的判断是否通过字段updateMember
	 */
	public void updateMember(IData param) throws Exception {
		AuditGroupmemberByOrderBean groupBean = BeanManager.createBean(AuditGroupmemberByOrderBean.class);
		groupBean.updateMember(param);
	}
}
