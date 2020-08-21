package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserMebCenPayQry {
	/**
	 * 国际流量统付首次激活修改截止时间
	 * @param endDate
	 * @param userId
	 * @param productSpecId
	 * @param productDiscntCode
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static int updateEndDateByUserIdSnProductOfferId(String endDate,String userId,String serialNumber,String productOfferId,String routeId) throws Exception{
        IData param = new DataMap();
        param.put("END_DATE", endDate);
        param.put("USER_ID", userId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("PRODUCT_OFFER_ID", productOfferId);
        return Dao.executeUpdateByCodeCode("TF_F_USER_MEB_CENPAY", "UPD_ENDDATE_BY_USERID_SN_PRODUCTOFFERID", param,routeId);
    }
	
	public static IDataset queryMebCenpayInfosByInstId(String userId, String instId) throws Exception{
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("INST_ID", instId);
        return Dao.qryByCode("TF_F_USER_MEB_CENPAY", "SEL_MBCENPAY_BY_INSTID", cond);
    }
	
	public static IDataset queryMebCenpayInfosByInstId(String userId, String instId, String routeId) throws Exception{
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("INST_ID", instId);
        return Dao.qryByCode("TF_F_USER_MEB_CENPAY", "SEL_MBCENPAY_BY_INSTID", cond, routeId);
    }
}
