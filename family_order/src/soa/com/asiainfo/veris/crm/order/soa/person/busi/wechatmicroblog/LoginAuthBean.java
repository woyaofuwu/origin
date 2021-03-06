package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import java.io.PrintWriter;
import java.io.StringWriter;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class LoginAuthBean extends CSBizBean {
	
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
		if ("03".equals(data.getString("IDENT_CODE_TYPE"))){
			if (data.getString("PASSWORD") == null
					|| data.getString("PASSWORD").equals("")) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户密码不能为空！");
			}
			if (data.getString("PWD_TYPE") == null
					|| data.getString("PWD_TYPE").equals("")) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户密码类型不能为空！");
			}
		}
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
	}
	
	/**
	 * 凭证申请
	 * @param data
	 * @throws Exception
	 */
	public void createIdentInfo(IData data) throws Exception{
		if(!Dao.insert("TF_F_USER_ACCOUNT", data)){
			CSAppException.apperr(CrmUserException.CRM_USER_2999);
		}
	}
	
	/**
	 * 凭证申请
	 * @param data
	 * @throws Exception
	 */
	public IData applyIdentInfo(IData data)throws Exception
	{
		checkParam(data);
		IData temp=new DataMap();
		IData info = new DataMap();
		IData user = new DataMap();
		IDataset userInfos = new DatasetList();
		
		String serialNumber = data.getString("SERIAL_NUMBER");//IDITEMRANGE
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userInfo)) {
			CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
		}
		String userId = userInfo.getString("USER_ID");
		String partition_id = userId.substring(userId.length() - 4,userId.length());
		String userType = data.getString("USER_TYPE");//01：手机号码 02：固话号码：03 宽带帐号，04 vip卡号 05：集团编码，本期只有01：手机号码
		String identCodeType = data.getString("IDENT_CODE_TYPE");//用户身份凭证类型 01：一般凭证  03：服务密码凭证
		String passWord = data.getString("PASSWORD");//用户密码
		String pwdType = data.getString("PWD_TYPE");//密码类型
		
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
		if("03".equals(identCodeType) || "02".equals(identCodeType)){
			if ("".equals(passWord) || passWord == null){//密码均不能为空
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户密码不能为空！");
			}
			if ("".equals(pwdType) || pwdType == null){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "密码类型不能为空！");
			} 
		}
			
		temp.put("EFFECTIVE_TIME", data.getString("EFFECTIVE_TIME"));
		/**
		 * start
		 * chenxy3 2015-08-10
		 * 延时是30分钟，入参数据有传，取不到默认1800秒
		 * */
		String EFFECTIVE_TIME=data.getString("EFFECTIVE_TIME","");
		int effctTime=1800;
		if(EFFECTIVE_TIME!=null && !"".equals(EFFECTIVE_TIME)){
			effctTime=Integer.parseInt(EFFECTIVE_TIME);
		}
		String registTime= SysDateMgr.getSysTime();
		String endDate = SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime, "yyyy-MM-dd HH:mm:ss"), effctTime),"yyyy-MM-dd HH:mm:ss");
		
		temp.put("IDENT_START_TIME", registTime);
		temp.put("IDENT_END_TIME", endDate);
		
		temp.put("EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE",""));
		temp.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID",""));
		temp.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID",""));
		temp.put("UPDATE_TIME", registTime);
		/**end*/
			
		String strDate = DateFormatUtils.format(Calendar.getInstance().getTime(),"yyyyMMdd");
		IDataset out = UserIdentInfoQry.getseqString();
		String seqId = ((IData) out.get(0)).getString("OUTSTR", "");
		String identCode = "IDENTCODE"+strDate+getVisit().getStaffEparchyCode()+seqId;
		temp.put("IDENT_CODE", identCode);//用户身份凭证
		temp.put("REMOVE_TAG", "0");//账号失效标识

		//校验客户是否已经绑定了手机号码
		IDataset dataset = UserIdentInfoQry.queryAccountBySerialNumber(userId,serialNumber);
		int errorNum=0;
		//if ("01".equals(pwdType)) {// 服务密码,校验密码是否正确
		if("03".equals(identCodeType) ){
			boolean res = UserInfoQry.checkUserPassWd(userId, passWord);
			if (res == false)// 密码错误
			{
				errorNum = errorNum + 1;
				temp.put("ERROR_NUMB", errorNum);
				if (!IDataUtil.isEmpty(dataset)) {
					UserIdentInfoQry.updateErrorNum2(temp);//
				} else {
					temp.put("IDENT_END_TIME", registTime);//如果不存在，记录历史的时候，应该置为无效记录。而不是插入一条有效的。
					createIdentInfo(temp);
				}
				info.put("X_RESULTCODE", "-1");
				info.put("X_RESULTINFO", "服务密码错误!");
				return info;
			}
			temp.put("ERROR_NUMB", "0");
			if (!IDataUtil.isEmpty(dataset)) {
				/**严重：密码校验通过，如果还存在有效的绑定数据，这里只更新一些状态是不对的
	        	  * 因为凭证已经生成，但是没更新到表里，到后面办理业务会凭证对应不上！
	        	  * 必须使用原来的凭证返回！
	        	  * chenxy3 2015-9-11
	        	  * */
	        	identCode=dataset.getData(0).getString("IDENT_CODE");//用户身份凭证
	        	identCodeType=dataset.getData(0).getString("IDENT_CODE_TYPE");//用户身份凭证级别 01：一般凭证 03：服务密码凭证 
       		    UserIdentInfoQry.updateErrorNum2(temp);
			} else {
				createIdentInfo(temp);
			}
		//} else if ("02".equals(pwdType)) {// 短信随机密码，校验密码是否正确
		} else if ("02".equals(identCodeType)) {
			IDataset bb = UserIdentInfoQry.queryPWDLog(data);// 先查出短信下发时记录的短信随机密码
			String pwdActiveTime = "";
			String bb_pwd="";
			if(bb!=null && bb.size()>0){
				pwdActiveTime = bb.getData(0).getString("PWD_END_TIME");
				bb_pwd=bb.getData(0).getString("PASSWORD");
			}else{
				/** chenxy3 密码过期，就当是记录错误的校验历史。*/
				errorNum=errorNum+1;
				temp.put("ERROR_NUMB", errorNum);
				if(!IDataUtil.isEmpty(dataset)){
	        		 UserIdentInfoQry.updateErrorNum2(temp);//
	        	 }else{
	        		 temp.put("IDENT_END_TIME", registTime);//如果不存在，记录历史的时候，应该置为无效记录。而不是插入一条有效的。
	        		 this.createIdentInfo(temp); 
	        	 }	  
				info.put("X_RESULTCODE", "-1");
	        	info.put("X_RESULTINFO", "密码已过期"); 
	        	return info;
			}
//			String strNow = new SimpleDateFormat("yyyyMMddHHmmss")
//					.format(Calendar.getInstance().getTime());
//			long l1 = Long.parseLong(pwdActiveTime);
//			long l2 = Long.parseLong(strNow);
//			if (l1 < l2) {
//				errorNum = errorNum + 1;
//				temp.put("ERROR_NUMB", errorNum);
//				if (!IDataUtil.isEmpty(dataset)) {
//					UserIdentInfoQry.updateErrorNum2(temp);//
//				} else {
//					createIdentInfo(temp);
//				}
//				info.put("X_RESULTCODE", "-1");
//				info.put("X_RESULTINFO", "密码已过期");
//				return info;
//			}
			/**密码需要加密后进行比对 chenxy3 2015-09-10*/
			passWord = Encryptor.fnEncrypt(passWord, RandomPWDBean.genUserId(userId));// 密文密码;
			if(passWord.equals(bb_pwd)){
				temp.put("ERROR_NUMB", "0");
				if (!IDataUtil.isEmpty(dataset)) {
					/**严重：密码校验通过，如果还存在有效的绑定数据，这里只更新一些状态是不对的
		        	  * 因为凭证已经生成，但是没更新到表里，到后面办理业务会凭证对应不上！
		        	  * 必须使用原来的凭证返回！
		        	  * chenxy3 2015-9-11
		        	  * */
		        	identCode=dataset.getData(0).getString("IDENT_CODE");//用户身份凭证
		        	identCodeType=dataset.getData(0).getString("IDENT_CODE_TYPE");//用户身份凭证级别 01：一般凭证 03：服务密码凭证 
	        		UserIdentInfoQry.updateErrorNum2(temp);//
				} else {
					createIdentInfo(temp);
				}
				UserIdentInfoQry.updatePWDLog(data);// 更新为已验证标识
			} else {
				errorNum = errorNum + 1;
				temp.put("ERROR_NUMB", errorNum);
				if (!IDataUtil.isEmpty(dataset)) {
					UserIdentInfoQry.updateErrorNum2(temp);//
				} else {
					createIdentInfo(temp);
				}
				temp.put("X_RESULTCODE", "-1");
				temp.put("X_RESULTINFO", "短信随机密码错误!");
				return temp;
			}
		}else{
			temp.put("ERROR_NUMB", "0");
			if (!IDataUtil.isEmpty(dataset)) {  
       		    UserIdentInfoQry.updateErrorNum3(temp);//终止原有的有效凭证  
       		    createIdentInfo(temp);
			} else {
				createIdentInfo(temp);
			}
		}
		
		String customId = data.getString("CUSTOM_ID");
		
		if ("".equals(customId) || customId == null){
			info.put("CUSTOM_ID_STATUS","N");//客户标识是否变更   N 为未变更,Y为已变更
		}else{
			info.put("CUSTOM_ID_STATUS","Y");
		}
		info.put("IDENT_CODE", identCode);//用户身份凭证
		info.put("IDENT_CODE_LEVEL", identCodeType);//用户身份凭证级别 01：一般凭证 03：服务密码凭证
		info.put("IDENT_CODE_TYPE", identCodeType);
		
		user.put("SERIAL_NUMBER", serialNumber);
		user.put("CUSTOM_ID", userInfo.getString("CUST_ID"));//客户标识
		user.put("PROVINCE", userInfo.getString("CITY_CODE"));//客户归属省
		user.put("BRAND", userInfo.getString("BRAND_CODE"));//用户品牌
		user.put("STATUS", userInfo.getString("USER_STATE_CODESET"));//用户状态
		
		userInfos.add(user);
		info.put("USER_INFO", userInfos);//用户信息
		
		return info;
	}
	
	/**
	 * 凭证校验
	 * @param data
	 * @throws Exception
	 */
	public IData accoutIdentAuth(IData data)
			throws Exception {
		IData result = new DataMap();
		try{
			IDataset dataSet = new DatasetList();
			IDataset identCodeInfoLst = (IDataset)data.get("IDENT_CODE_INFO");
			if(IDataUtil.isEmpty(identCodeInfoLst)){
				 CSAppException.apperr(CrmCommException.CRM_COMM_103, "接口参数检查，输入参数[IDENT_CODE_INFO]的数据集合为空");
			}
			
			String identCode = identCodeInfoLst.getData(0).getString("IDENT_CODE","");
			String serialNumber = data.getString("SERIAL_NUMBER");
			
			dataSet = UserIdentInfoQry.searchIdentCode(identCode, serialNumber);
			if(IDataUtil.isEmpty(dataSet)){
				CSAppException.apperr(CrmUserException.CRM_USER_938);
			}
			
			result.put("X_RSPCODE", "0");
    		result.put("X_RSPDESC", "OK");
		
			return result;
		}catch(Exception e){
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			System.err.println("exception:"+e.getMessage()+",stack:"+writer.getBuffer().toString());
			if(e.getMessage().indexOf("`") >= 1)//预防抛出的异常信息格式不规范
				result.put("X_RSPCODE", e.getMessage().substring(0,e.getMessage().indexOf("`")));
			else
				result.put("X_RSPCODE","20501230");//出现异常时,返回给一级boss的的默认code
			if(e.getMessage().indexOf("`") >= 1 && e.getMessage().length() >= 2)
				result.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf("`")+1));
			else
				result.put("X_RSPDESC", "业务处理中出现异常");
			
			return result;
		}
	}
}
