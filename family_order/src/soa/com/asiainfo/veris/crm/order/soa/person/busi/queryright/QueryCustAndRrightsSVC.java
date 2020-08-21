package com.asiainfo.veris.crm.order.soa.person.busi.queryright;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;

public class QueryCustAndRrightsSVC extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IDataset queryRightIdByClass(IData param) throws Exception {
		QueryCustAndRightsBean bean = BeanManager
				.createBean(QueryCustAndRightsBean.class);
		IDataset inprame = bean.queryRightIdByClass(param);
		return inprame;
	}

	public IDataset queryRightNameByClass(IData param) throws Exception {
		QueryCustAndRightsBean bean = BeanManager
				.createBean(QueryCustAndRightsBean.class);
		IDataset inprame = bean.queryRightINamedByClass(param);
		return inprame;
	}

	public IDataset queryUserClassBySn(IData param) throws Exception {
		QueryCustAndRightsBean bean = BeanManager
				.createBean(QueryCustAndRightsBean.class);
		String sernumber = param.getString("SERIAL_NUMBER");
		IData idata = new DataMap();
		idata.put("SERIAL_NUMBER", sernumber);
		IDataset inprame = bean.queryUserClassBySn(idata);
		if(IDataUtil.isEmpty(inprame)){
			return new DatasetList();
		}
		String userId = inprame.getData(0).getString("USER_ID");
		String discnt_code = param.getString("DISCNT_CODE");
		IDataset inprames = UserDiscntInfoQry.getAllDiscntByUser_3(userId, discnt_code);	
		return inprames;
	}

	// 通过user_id查询权益使用的情况
	public IDataset queryRightByUserid(IData param) throws Exception {
		QueryCustAndRightsBean bean = BeanManager
				.createBean(QueryCustAndRightsBean.class);
		String sernumber = param.getString("SERIAL_NUMBER");
		IData idata = new DataMap();
		idata.put("SERIAL_NUMBER", sernumber);
		IDataset inprame = bean.queryUserClassBySn(idata);
		if(IDataUtil.isEmpty(inprame)){
			return new DatasetList();
		}
		String userId = inprame.getData(0).getString("USER_ID");
		IDataset inprames = UserOtherInfoQrySVC.getRightUseRecordByUserId(userId);
		return inprames;
	}
}
