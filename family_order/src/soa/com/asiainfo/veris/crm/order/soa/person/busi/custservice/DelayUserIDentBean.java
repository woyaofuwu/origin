package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;

public class DelayUserIDentBean extends CSBizBean {
	
	/*
	 * 客户身份凭证注销
	 */
	public IData cancelUserIdentUnEffT(IData data) throws Exception
	{
		IData result = new DataMap();
		
		String sn = data.getString("SERIAL_NUMBER");
		
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
    	
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        
        String userid = userInfo.getString("USER_ID");
        String identCode = data.getString("IDENT_CODE");
		String contactId = data.getString("CONTACT_ID");
		
		data.put("USER_ID", userid);
		
		IDataset dataset = UserIdentInfoQry.queryIdentCode(userid, identCode, contactId);
		
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_2998);
		}
		IData temp = dataset.getData(0);
		
		String registTime= temp.getString("REGIST_TIME");
		
		data.putAll(temp);
		
		String identUnEffT = extendIdentUnEffT(data,300000);
		
		result.put("IDENT_UNEFFT", identUnEffT);//用户凭证失效时间
		
		return result;
	}	
	
	/**
	 * 失效时间延时
	 * @param pd
	 * @param data
	 * @param seconds
	 * @return
	 * @throws Exception
	 */
	public String extendIdentUnEffT(IData data,int seconds) throws Exception
	{
		String sysdate=SysDateMgr.getSysTime();
		
		//-------失效旧的
		data.put("IDENT_UNEFFT", sysdate);
		String userId = data.getString("USER_ID");
		String partition_id = userId.substring(userId.length() - 4,userId.length());
		data.put("PARTITION_ID", partition_id);
		Dao.save("TF_F_USER_IDENT_CODE", data);//失效旧的
		
		return sysdate;
	}
	
}
