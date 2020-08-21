package com.asiainfo.veris.crm.order.soa.person.busi.createhytusertrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CreateHYTPersonUserBean extends CSBizBean {
	


	/**
	 * @Description：查询船只是否已经有船东套餐
	 * @param:@param input
	 * @param:@return
	 * @return IDataset
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-5-28下午05:41:39
	 */
	public IDataset queryShipInfo(IData input) throws Exception {
		String sql="  select *  from TF_F_SHIP_INFO t where t.ship_id=:SHIP_ID and T.END_DATE >sysdate AND T.IS_OWNER='1' AND IS_OWNER_DISCNT='1'" ;
		IDataset res = Dao.qryBySql(new StringBuilder(sql), input); 
		return res;
	}
}
