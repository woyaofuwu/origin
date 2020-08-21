package com.asiainfo.veris.crm.order.soa.person.busi.addgroupmemberbyorder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ReqGroupmemberByOrderSVC extends CSBizService
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 查询集团通讯录信息
	 */
	public IDataset queryGrpBooktInfos(IData input) throws Exception {
		ReqGroupmemberByOrderBean reqGroupmemberInfoQryBean = BeanManager.createBean(ReqGroupmemberByOrderBean.class);
		return reqGroupmemberInfoQryBean.queryGrpBooktInfos(input, getPagination());
	}
	/**
	 * 注销
	 */
	public void submitCancel(IData param) throws Exception {
		ReqGroupmemberByOrderBean groupmemberInfoQryBean = BeanManager.createBean(ReqGroupmemberByOrderBean.class);
		groupmemberInfoQryBean.submitCancel(param);
	}
	/**
	 * 根据file_id查询文件信息
	 */
	 public IDataset queryFileInfos(IData input) throws Exception {
		ReqGroupmemberByOrderBean reqGroupmemberInfoQryBean = BeanManager.createBean(ReqGroupmemberByOrderBean.class);
		return reqGroupmemberInfoQryBean.queryFileInfos(input);
	}
	 /**
	 * 根据userId更新file
	 */
	 public void updateUpload(IData input) throws Exception {
		 ReqGroupmemberByOrderBean groupmemberInfoQryBean = BeanManager.createBean(ReqGroupmemberByOrderBean.class);
		 groupmemberInfoQryBean.updateUpload(input);
	}
	 
	 
}
