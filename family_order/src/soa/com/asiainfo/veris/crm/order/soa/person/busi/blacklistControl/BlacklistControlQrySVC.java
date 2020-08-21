package com.asiainfo.veris.crm.order.soa.person.busi.blacklistControl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class BlacklistControlQrySVC extends CSBizService {


	// 查询
	public IDataset getUserInfo(IData param) throws Exception {
		BlacklistControlQryBean userInfoQryBreakBean = BeanManager.createBean(BlacklistControlQryBean.class);
		IDataset results = userInfoQryBreakBean.qryUserInfo(param,getPagination());
		return results;
	}

	// 新增
	public IData insertUserData(IData param) throws Exception {
		boolean isNetBatOpenUser = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "VOLTE_BLACK_USER");
		IData data = new DataMap();
		if(isNetBatOpenUser){
			BlacklistControlQryBean userInfoQryBreakBean = BeanManager
					.createBean(BlacklistControlQryBean.class);
			data = userInfoQryBreakBean.insertUserData(param);
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_28);
		}
		return data;
	}
	// 删除
	public void delBlackUser(IData param) throws Exception {
		boolean isNetBatOpenUser = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "VOLTE_BLACK_USER");
		if(isNetBatOpenUser){
			BlacklistControlQryBean userInfoQryBreakBean = BeanManager
					.createBean(BlacklistControlQryBean.class);
			userInfoQryBreakBean.delBlackUserData(param);
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_28);
		}
	}
	// 批量新增
	public IData batInsertUserData(IData param) throws Exception {
		boolean isNetBatOpenUser = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "VOLTE_BLACK_USER");
		IData idada = new DataMap();
		if(isNetBatOpenUser){
			BlacklistControlQryBean userInfoQryBreakBean = BeanManager
					.createBean(BlacklistControlQryBean.class);
			idada = userInfoQryBreakBean.batInsertUserData(param);
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_28);
		}
		return idada;
	}

}