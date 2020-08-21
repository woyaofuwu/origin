package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;

public class CheckUserIDentUnEffTBean extends CSBizBean {
	
	
	public IData queryUserIdentUnEffT(IData data) throws Exception{
		IData result = new DataMap();
		String sn = data.getString("SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        String userId = userInfo.getString("USER_ID");
		String identCode = data.getString("IDENT_CODE");
		String contactId = data.getString("CONTACT_ID");
		IDataset dataset = UserIdentInfoQry.queryIdentCode(userId, identCode, contactId);
		
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_2998);
		}
		String identUnEffT = dataset.getData(0).getString("IDENT_UNEFFT");
		result.put("IDENT_UNEFFT", identUnEffT);//用户凭证失效时间
		return result;
	}
}
