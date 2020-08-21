package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class RelationBankInfoQry {
	
	public static IDataset querySignBankList(String userId,String eparchyCode) throws Exception{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("PARAM_ATTR", "339");
		param.put("SUBSYS_CODE", "CSM");
		
		return Dao.qryByCode("TF_F_RELATION_BANK", "SEL_SIGN_BANK_DATA", param);
	}
	
	public static IDataset querySignBankByUid(String userId) throws Exception{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		
		return Dao.qryByCode("TF_F_RELATION_BANK", "SEL_BANK_INFO_BY_USER_ID", param);
	}
	
	public static IData querySignBankByUidSid(String userId,String subId) throws Exception{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SUB_ID", subId);
		IDataset results = Dao.qryByCode("TF_F_RELATION_BANK", "SEL_BANK_INFO_BY_USER_SUB_ID", param);
		if(IDataUtil.isNotEmpty(results)){
			return results.getData(0);
		}else{
			return null; 
		}
	}

}
