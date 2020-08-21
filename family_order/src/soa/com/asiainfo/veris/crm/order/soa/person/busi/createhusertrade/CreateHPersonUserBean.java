package com.asiainfo.veris.crm.order.soa.person.busi.createhusertrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CreateHPersonUserBean extends CSBizBean {
	
	/**
	 * 和校园异网号码开户校验
	 * @param inParam
	 * @return
	 * @throws Exception
	 * @date 2018-1-26 16:39:51
	 */
	public IDataset checkHSerialNumber(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("PROV_CODE", "898");
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset infos = Dao.qryByCode("TD_MSISDN", "SEL_BY_MSISDN_ASP_NOT_1", param, Route.CONN_CRM_CEN);
        return infos;
    }
	
	public IDataset checkNpSerialNumber(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset infos = Dao.qryByCode("TF_F_USER", "SEL_BY_SCHOOL", param);
        return infos;
    }
}
