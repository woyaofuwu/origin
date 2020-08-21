package com.asiainfo.veris.crm.order.soa.person.busi.custservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;

public class DelayUserIDentTimeBean extends CSBizBean {
	
	
	public IData updateUserIdentUnEffT(IData data) throws Exception{
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
		
		String identUnEffT=extendIdentUnEffT(data,300000);
		
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
	public String extendIdentUnEffT(IData data,int seconds) throws Exception{
		String identUnEffT="";
		String sysdate=SysDateMgr.getSysTime();
		
		//-------失效旧的
		data.put("IDENT_UNEFFT", sysdate);
		data.put("UPDATE_TIME", sysdate);
		String userId = data.getString("USER_ID");
		String partition_id = userId.substring(userId.length() - 4,userId.length());
		data.put("PARTITION_ID", partition_id);
		Dao.save("TF_F_USER_IDENT_CODE", data);//失效旧的
		
		//-------延时新的
		IData newData=new DataMap();
		newData.put("USER_ID", data.getString("USER_ID"));
		newData.put("IDENT_CODE", data.getString("IDENT_CODE"));
		newData.put("CONTACT_ID", data.getString("CONTACT_ID"));
		newData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		String registTime= SysDateMgr.getSysTime();
		identUnEffT = SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime, "yyyy-MM-dd HH:mm:ss"), 300),"yyyy-MM-dd HH:mm:ss");
		newData.put("IDENT_UNEFFT", identUnEffT);
		newData.put("REGIST_TIME", registTime);
		
		createIdentInfo(newData);
		
		return identUnEffT;
	}

	
	public void createIdentInfo(IData data)throws Exception{
		IData temp=new DataMap();
		String userId = data.getString("USER_ID");
		String partition_id = userId.substring(userId.length() - 4,userId.length());
		temp.put("USER_ID", userId);
		temp.put("PARTITION_ID",partition_id);
		temp.put("CONTACT_ID", data.getString("CONTACT_ID"));
		temp.put("REGIST_TIME", data.getString("REGIST_TIME"));
		temp.put("IDENT_UNEFFT", data.getString("IDENT_UNEFFT"));
		temp.put("IDENT_CODE", data.getString("IDENT_CODE"));
		temp.put("UPDATE_TIME",SysDateMgr.getSysDate());
		temp.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		if(!Dao.insert("TF_F_USER_IDENT_CODE", temp)){
			CSAppException.apperr(CrmUserException.CRM_USER_2999);
		}
	}
}
