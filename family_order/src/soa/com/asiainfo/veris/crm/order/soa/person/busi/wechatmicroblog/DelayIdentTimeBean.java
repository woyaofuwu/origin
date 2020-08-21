package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

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

/**
 * 凭证有效期延长
 * @author psy
 *
 */
public class DelayIdentTimeBean extends CSBizBean
{
	/**
	 * 客户身份凭证有效期查询
	 * @param OPR_NUMB(操作的流水号),CONTACT_ID(全网客户接触ID),SERIAL_NUMBER(客户手机号码),IDENT_CODE(客户身份凭证)
	 * @return IDENT_UNEFFT(失效时间)
	 * @throws Exception
	 */
	public IData queryUserIdentUnEffT(IData data) throws Exception{
		IData result = new DataMap();
		String sn = data.getString("SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
		String identCode = data.getString("IDENT_CODE");
		
		IDataset dataset = UserIdentInfoQry.searchIdentCode(identCode, sn);
		
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_938);
		}
		String identUnEffT = dataset.getData(0).getString("IDENT_END_TIME");
		result.put("IDENT_END_TIME", identUnEffT);//用户凭证失效时间
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
	public String extendIdentUnEffT(IData data) throws Exception{
		String identUnEffT="";
		String sysdate=SysDateMgr.getSysTime();
		
		//-------失效旧的
		data.put("IDENT_END_TIME", sysdate); 
		/**
		 * chenxy3 2015-08-10 使用新的无accout条件的语句
		 * */
		Dao.executeUpdateByCodeCode("TF_F_USER_ACCOUNT", "UPD_IDENT_END_TIME_BY_USER_ID2", data);//失效旧的
		
		//-------延时新的
		IData newData=new DataMap();
		String userId = data.getString("USER_ID");
		String partition_id = userId.substring(userId.length() - 4,userId.length());
		newData.put("PARTITION_ID", partition_id);
		newData.put("USER_ID", userId);
		newData.put("IDENT_CODE", data.getString("IDENT_CODE"));
		newData.put("CONTACT_ID", data.getString("CONTACT_ID"));
		newData.put("OPR_NUMB", data.getString("OPR_NUMB"));
		newData.put("ERROR_NUMB", data.getString("ERROR_NUMB"));
		newData.put("USER_TYPE", data.getString("USER_TYPE"));
		newData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		newData.put("EFFECTIVE_TIME", data.getString("EFFECTIVE_TIME"));
		newData.put("REMOVE_TAG", data.getString("REMOVE_TAG"));
		newData.put("CHANNEL_ID", data.getString("CHANNEL_ID",""));
		newData.put("ACCOUNT", data.getString("ACCOUNT",""));
		newData.put("IDENT_CODE_TYPE", data.getString("IDENT_CODE_TYPE",""));
		newData.put("EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE",""));
		newData.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID",""));
		newData.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID",""));
		newData.put("UPDATE_TIME", SysDateMgr.getSysTime());
		
		
		String registTime= SysDateMgr.getSysTime();
		identUnEffT = SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime, "yyyy-MM-dd HH:mm:ss"), 1800),"yyyy-MM-dd HH:mm:ss");
		newData.put("IDENT_END_TIME", identUnEffT);
		newData.put("IDENT_START_TIME", registTime);
		
		//生成凭证
        LoginAuthBean loginBean = new LoginAuthBean();
        loginBean.createIdentInfo(newData);
		
		return identUnEffT;
	}
	
	/**
	 * 客户有效期延期
	 * @param data
	 * @return
	 * @throws Exception
	 */
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
		//String contactId = data.getString("CONTACT_ID");
		
		data.put("USER_ID", userid);
		
		//校验客户凭证
		IDataset dataset = UserIdentInfoQry.searchIdentCode(identCode, sn);
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_938);
		}

		IData temp = dataset.getData(0);
		data.putAll(temp);
		
		String identUnEffT = extendIdentUnEffT(data);
		
		result.put("IDENT_END_TIME", identUnEffT);//用户凭证失效时间
		result.put("SERIAL_NUMBER", sn);
		result.put("OPR_NUMB", data.getString("OPR_NUMB"));
		return result;
	}
	
	
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
		//String contactId = data.getString("CONTACT_ID");
		
		data.put("USER_ID", userid);
		
		//校验客户凭证
		IDataset dataset = UserIdentInfoQry.searchIdentCode(identCode, sn);
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_938);
		}		
		
		IData temp = dataset.getData(0);
		
		data.putAll(temp);
		
		String sysdate=SysDateMgr.getSysTime();
		
		//-------失效旧的
		data.put("IDENT_END_TIME", sysdate);
		String userId = data.getString("USER_ID");
		String partition_id = userId.substring(userId.length() - 4,userId.length());
		data.put("PARTITION_ID", partition_id);
		Dao.update("TF_F_USER_ACCOUNT", data);//失效旧的
		
		result.put("IDENT_END_TIME", sysdate);//用户凭证失效时间
		
		return result;
	}	
}