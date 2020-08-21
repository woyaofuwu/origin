package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BindMobilePhoneBean extends CSBizBean{
	/**
	 * 校验入参
	 * @param data
	 * @throws Exception
	 */
	public void checkParam(IData data) throws Exception{
		if (data.getString("OPR_NUMB") == null
				|| data.getString("OPR_NUMB").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_299);
		}
		if (data.getString("CHANNEL_ID") == null
				|| data.getString("CHANNEL_ID").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_1149);
		}
		if (data.getString("IDENT_CODE_TYPE") == null
				|| data.getString("IDENT_CODE_TYPE").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户身份凭证类型不能为空！");
		}
//		if ("03".equals(data.getString("IDENT_CODE_TYPE"))){//密码均不能为空
			if (data.getString("PASSWORD") == null
					|| data.getString("PASSWORD").equals("")) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户密码不能为空！");
			}
			if (data.getString("PWD_TYPE") == null
					|| data.getString("PWD_TYPE").equals("")) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户密码类型不能为空！");
			}
//		}
		if (data.getString("EFFECTIVE_TIME") == null
				|| data.getString("EFFECTIVE_TIME").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户身份凭证时间不能为空！");
		}
		String type = IDataUtil.chkParam(data, "USER_TYPE");
		if (type == null
				|| type.equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_606);
		}
		if (!"01".equals(type)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "标识类型错误");
		}
		if (data.getString("MICRO_ACCOUNT") == null
				|| data.getString("MICRO_ACCOUNT").equals("")) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "微信/微博账号不能为空！");
		}
	}
	
	public IData bindMobilePhone(IData data) throws Exception
	{
		checkParam(data);
		IData info = new DataMap();
		IData temp = new DataMap();
		IData user = new DataMap();
		IDataset userInfos = new DatasetList();
		
		String strDate = DateFormatUtils.format(Calendar.getInstance().getTime(),"yyyyMMdd");
		IDataset out = UserIdentInfoQry.getseqString();
		String seqId = ((IData) out.get(0)).getString("OUTSTR", "");
		String identCode = "IDENTCODE"+strDate+getVisit().getStaffEparchyCode()+seqId;
		String serialNumber = data.getString("SERIAL_NUMBER");//IDITEMRANGE
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo)) {
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}
		String userId = userInfo.getString("USER_ID");
		String partition_id = userId.substring(userId.length() - 4,userId.length());
		String userType = data.getString("USER_TYPE");//01：手机号码 02：固话号码：03 宽带帐号，04 vip卡号 05：集团编码，本期只有01：手机号码
		String identCodeType = data.getString("IDENT_CODE_TYPE");//用户身份凭证类型 01：一般凭证  02：短息密码凭证 03：服务密码凭证
		String passWord = data.getString("PASSWORD");//用户密码
		String pwdType = data.getString("PWD_TYPE");//密码类型
		String microAccount = data.getString("MICRO_ACCOUNT");//微博/微信账号
		
		//校验客户是否已经绑定了手机号码
		IDataset dataset = UserIdentInfoQry.queryMicroAccount(userId, microAccount,serialNumber);
		int errorNum=0;
		if(!IDataUtil.isEmpty(dataset)){
			if(dataset.getData(0).getInt("ERROR_NUMB")==0){//ERROR_NUMB为0则绑定成功，ERROR_NUMB为其他值为输入错误密码次数
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "当前微博/微信用户已经绑定了手机号码！");
			}else{
				errorNum=dataset.getData(0).getInt("ERROR_NUMB");
			}			
		}
		temp.put("USER_ID", userId);
		temp.put("PARTITION_ID",partition_id);
		temp.put("OPR_NUMB", data.getString("OPR_NUMB"));//本次操作的流水号
		temp.put("USER_TYPE", userType);
		if(!"01".equals(userType)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户标识类型错误！");
		}
		temp.put("SERIAL_NUMBER", serialNumber);
		temp.put("CHANNEL_ID", data.getString("CHANNEL_ID"));//渠道标识  62：微信营业厅      76：微博营业厅
		temp.put("IDENT_CODE_TYPE", identCodeType);
//		if("03".equals(identCodeType)){
			if ("".equals(passWord) || passWord == null){//密码均不能为空
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户密码不能为空！");
			}
			if ("".equals(pwdType) || pwdType == null){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "密码类型不能为空！");
			}
//			temp.put("PASSWORD", passWord);//表中无此字段
//			temp.put("PWD_TYPE", pwdType);
//		}
		/**chenxy3 将时间长度通过获取设置 2015-09-10*/
	    String effectiveTime=data.getString("EFFECTIVE_TIME");
		temp.put("EFFECTIVE_TIME", data.getString("EFFECTIVE_TIME"));
		temp.put("ACCOUNT", data.getString("MICRO_ACCOUNT"));//微信/微博账号
		temp.put("IDENT_CODE", identCode);//用户身份凭证
		temp.put("REMOVE_TAG", "0");//账号失效标识
		String registTime= SysDateMgr.getSysTime();
		String endDate = SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime, "yyyy-MM-dd HH:mm:ss"), Integer.parseInt(effectiveTime)),"yyyy-MM-dd HH:mm:ss");
		temp.put("IDENT_START_TIME", registTime);
		temp.put("IDENT_END_TIME", endDate);
		/**
		 * start
		 * chenxy3 2015-08-10
		 * 补充字段
		 * */
		temp.put("EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE",""));
		temp.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID",""));
		temp.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID",""));
		temp.put("UPDATE_TIME", registTime);
		/**end*/
		//生成凭证
		LoginAuthBean loginBean = new LoginAuthBean();
		if("01".equals(pwdType)){//服务密码,校验密码是否正确
			 boolean res = UserInfoQry.checkUserPassWd(userId, passWord);
	         if (res == false)// 密码错误
	         {
	        	 errorNum=errorNum+1;
	        	 temp.put("ERROR_NUMB", errorNum);
	        	 if(!IDataUtil.isEmpty(dataset)){
	        		 UserIdentInfoQry.updateErrorNum(temp);//
	        	 }else{
	        		 loginBean.createIdentInfo(temp); //查不到绑定表数据有必要插一条错误数据吗？是为了校验记录吗？
	        	 }	        	 
	        	 info.put("X_RESULTCODE", "-1");
	        	 info.put("X_RESULTINFO", "服务密码错误!"); 
	        	 return info;
	         }
	         temp.put("ERROR_NUMB", "0");/**?? 密码通过，应该是绑定的标记ERROR_NUMB=0，为什么是取数据库的值？ chenxy3*/
        	 temp.put("BIND_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	         if(!IDataUtil.isEmpty(dataset)){
	        	 /**严重：密码校验通过，如果还存在有效的绑定数据，这里只更新一些状态是不对的
	        	  * 因为凭证已经生成，但是没更新到表里，到后面办理业务会凭证对应不上！
	        	  * 必须使用原来的凭证返回！
	        	  * chenxy3 2015-9-11
	        	  * */
	        	 identCode=dataset.getData(0).getString("IDENT_CODE");//用户身份凭证
	        	 identCodeType=dataset.getData(0).getString("IDENT_CODE_TYPE");//用户身份凭证级别 01：一般凭证 03：服务密码凭证 
        		 UserIdentInfoQry.updateErrorNum(temp);/**如果存在数据，但是前面校验错误，则是1，现在校验通过，还是1？*/
        	 }else{
        		 loginBean.createIdentInfo(temp); 
        	 }	     
		}else if("02".equals(pwdType)){//短信随机密码，校验密码是否正确
			IDataset bb = UserIdentInfoQry.queryPWDLog(data);//先查出短信下发时记录的短信随机密码
			/**chenxy3 要先判断是否存在，否则过期的数据取不到getData(0)就报错。*/
			String pwdActiveTime = "";
			String bb_pwd="";
			if(bb!=null && bb.size()>0){
				pwdActiveTime = bb.getData(0).getString("PWD_END_TIME");
				bb_pwd=bb.getData(0).getString("PASSWORD");
			}else{
				/**chenxy3 密码过期，就当是记录错误的校验历史。*/
				errorNum=errorNum+1;
				temp.put("ERROR_NUMB", errorNum);
				if(!IDataUtil.isEmpty(dataset)){
	        		 UserIdentInfoQry.updateErrorNum(temp);//
	        	 }else{
	        		 loginBean.createIdentInfo(temp); 
	        	 }	  
				info.put("X_RESULTCODE", "-1");
	        	info.put("X_RESULTINFO", "密码已过期"); 
	        	return info;
			}
			/**前面的查询语句带了时间做条件，查不到肯定是过期的，下面这段多余 chenxy3 2015-09-10*/
//			String strNow = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
//			long l1 = Long.parseLong(pwdActiveTime); 
//			long l2 = Long.parseLong(strNow); 
//			if(l1<l2){
//				 errorNum=errorNum+1;
//				 temp.put("ERROR_NUMB", errorNum);
//	        	 if(!IDataUtil.isEmpty(dataset)){
//	        		 UserIdentInfoQry.updateErrorNum(temp);//
//	        	 }else{
//	        		 loginBean.createIdentInfo(temp); 
//	        	 }	  
//				 info.put("X_RESULTCODE", "-1");
//	        	 info.put("X_RESULTINFO", "密码已过期"); 
//	        	 return info;
//			}
			/**密码需要加密后进行比对 chenxy3 2015-09-10*/
			passWord = Encryptor.fnEncrypt(passWord, RandomPWDBean.genUserId(userId));// 密文密码;
			if(passWord.equals(bb_pwd)){
				 temp.put("ERROR_NUMB", "0");/**是否应该为0？ chenxy3 2015-09-10*/
				 temp.put("BIND_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        	 if(!IDataUtil.isEmpty(dataset)){
	        		 /**严重：密码校验通过，如果还存在有效的绑定数据，这里只更新一些状态是不对的
		        	  * 因为凭证已经生成，但是没更新到表里，到后面办理业务会凭证对应不上！
		        	  * 必须使用原来的凭证返回！
		        	  * chenxy3 2015-9-11
		        	  * */
		        	 identCode=dataset.getData(0).getString("IDENT_CODE");//用户身份凭证
		        	 identCodeType=dataset.getData(0).getString("IDENT_CODE_TYPE");//用户身份凭证级别 01：一般凭证 03：服务密码凭证 
	        		 UserIdentInfoQry.updateErrorNum(temp);//
	        	 }else{
	        		 loginBean.createIdentInfo(temp); 
	        	 }	  
	        	 UserIdentInfoQry.updatePWDLog(data);//更新为已验证标识
			}else{
				errorNum=errorNum+1;
				 temp.put("ERROR_NUMB", errorNum);
	        	 if(!IDataUtil.isEmpty(dataset)){
	        		 UserIdentInfoQry.updateErrorNum(temp);//
	        	 }else{
	        		 loginBean.createIdentInfo(temp); 
	        	 }	  
				info.put("X_RESULTCODE", "-1");
	        	info.put("X_RESULTINFO", "短信随机密码错误!"); 
	        	return info;
			}

		}              		
		info.put("IDENT_CODE", identCode);//用户身份凭证
		info.put("IDENT_CODE_LEVEL", identCodeType);//用户身份凭证级别 01：一般凭证 03：服务密码凭证
		info.put("IDENT_CODE_TYPE", identCodeType);//用户身份凭证类型01：一般凭证（绑定不涉及）02：短信密码凭证03：服务密码凭证
		
		user.put("SERIAL_NUMBER", serialNumber);
		user.put("CUSTOM_ID", userInfo.getString("CUST_ID"));//客户标识
		user.put("PROVINCE", userInfo.getString("CITY_CODE"));//客户归属省
		user.put("BRAND", userInfo.getString("BRAND_CODE"));//用户品牌
		user.put("STATUS", userInfo.getString("USER_STATE_CODESET"));//用户状态
		
		userInfos.add(user);
		info.put("USER_INFO", userInfos);//用户信息
		info.put("X_RESULTCODE", "0");
    	info.put("X_RESULTINFO", "OK!");
		return info;
	}
	
	public IData unBindMobilePhone(IData data) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");//手机号码
		String userType = IDataUtil.chkParam(data, "USER_TYPE");//01：手机号码 02：固话号码：03 宽带帐号，04 vip卡号 05：集团编码，本期只有01：手机号码
		String microAccount = IDataUtil.chkParam(data, "MICRO_ACCOUNT");//微博/微信账号
		IData info = new DataMap();
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo)) {
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}
		String userId = userInfo.getString("USER_ID");
		String partition_id = userId.substring(userId.length() - 4,userId.length());
		
		//校验客户是否已经绑定了手机号码
		IDataset dataset = UserIdentInfoQry.queryMicroAccount(userId, microAccount,serialNumber);
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "当前微博/微信用户没有绑定手机号码！");
		}	
				
		data.put("USER_ID", userId);
		data.put("PARTITION_ID",partition_id);
		
		if(!"01".equals(userType)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户标识类型错误！");
		}
		
		data.put("IDENT_END_TIME", SysDateMgr.getSysTime());
		
		Dao.executeUpdateByCodeCode("TF_F_USER_ACCOUNT", "UPD_IDENT_END_TIME_BY_USER_ID", data);//解除绑定
		
		info.put("SERIAL_NUMBER", serialNumber);
		info.put("OPR_NUMB", data.getString("OPR_NUMB"));
		
		return info;
	}
	
	public IData unBindMobilePhoneInform(IData data) throws Exception
	{
		IData outparams = IBossCall.callIbossunBindMobileInform(data).getData(0);
		outparams.put("RELEASE_NOTICE_RSLT", "0");
		return outparams;
	}

}
