package com.asiainfo.veris.crm.order.soa.person.busi.pccbusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * PCC业务
 */
public class PCCBusinessSVC extends CSBizService {

	private static final long serialVersionUID = 3109066901465171662L;
	/**
	 * 批量操作任务执行状态查询
	 */
	public IDataset qryPCCBatTaskStatus(IData input) throws Exception {
		PCCBusinessBean bean = (PCCBusinessBean) BeanManager.createBean(PCCBusinessBean.class);
		IDataset resultList = bean.qryPCCBatTaskStatus(input);
		return resultList;
	}
	
	/**
	 * 批量操作任务执行状态查询
	 */
	public IData qryPCCBatTaskStatusIboss(IData input) throws Exception {
		PCCBusinessBean bean = (PCCBusinessBean) BeanManager.createBean(PCCBusinessBean.class);
		IData result = bean.qryPCCBatTaskToIboss(input);
		return result;
	}
	
	/**
	 * 数据导入
	 */
	public IData pccDateInsert(IData input) throws Exception {
		PCCBusinessBean bean = (PCCBusinessBean) BeanManager.createBean(PCCBusinessBean.class);
		IData result = bean.pccDateInsert(input);
		return result;
	}
	
}