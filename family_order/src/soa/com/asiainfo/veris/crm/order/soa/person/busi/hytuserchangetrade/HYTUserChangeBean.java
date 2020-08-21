package com.asiainfo.veris.crm.order.soa.person.busi.hytuserchangetrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class HYTUserChangeBean extends CSBizBean {
	
	/**
	 * @Description：查询用户的other记录
	 * @param:@param string
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-6-4下午04:20:42
	 */
	public IDataset checkIsHYTUser(String userId) throws Exception {
		return UserOtherInfoQry.getAllOtherInfoByUserIdValueCode(userId,"HYT");
		
	}

	/**
	 * @Description：获取海洋通用户办理的有效套餐列表
	 * @param:@param string
	 * @param:@return
	 * @return IDataset
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-6-8上午11:31:44
	 */
	public IDataset getDiscnt(String userId) throws Exception {
		 IData cond = new DataMap();
	        cond.put("USER_ID", userId);
	        return Dao.qryByCode("TF_F_SHIP_INFO", "SEL_DISCNT_BY_USERID", cond);
	}
}
