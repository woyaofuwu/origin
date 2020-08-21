package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.realname;
 
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.cmonline.selfterminal.SelfTerminalBean;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.ChangeCardSVC;


public class RealNameJudgeBean extends CSBizBean {
    
    protected static Logger log = Logger.getLogger(RealNameJudgeBean.class);

//	protected static final Logger log = Logger.getLogger(RealNameJudgeBean.class);
	/**
	 * @Description 用户实名补录号码校验
	 * @param data
	 * @return result
	 * @throws Exception
	 */
	public IData checkRealNameState(IData data) throws Exception {
		IData result = new DataMap();
        
        IDataset userList =  UserInfoQry.getUserInfoBySn(data.getString("SERIAL_NUMBER"), "0");
		
		if (userList == null || userList.size() < 1) {
			result.put("RETURN_CODE", "1005");
			result.put("RETURN_MESSAGE", "预销号状态！");
			result.put("IS_REG", "0");
			return result;
		}
		
		String user_diff_code = userList.getData(0).getString("USER_DIFF_CODE","");
		//校验是否为集团客户
		if (!"0".equals(user_diff_code)) {
			result.put("RETURN_CODE", "2999");
			result.put("TYPE", "2");
			result.put("RETURN_MESSAGE", "集团客户号码");
			result.put("IS_REG", "0");
			return result;
		}
		
		UcaData uca = null;
		uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
		
		if("1".equals(uca.getCustomer().getIsRealName())/* && "1".equals(uca.getCustomer().getRsrvTag3())*/)
		{
			result.put("RETURN_CODE", "1004");
			result.put("RETURN_MESSAGE", "用户已经是实名制用户！ ");
			result.put("IS_REG", "0");
			return result;
		}
		
		/*
		IData param =new DataMap();
		param.put("USER_ID", userList.getData(0).getString("USER_ID"));
		param.put("SERIAL_NUMBER", userList.getData(0).getString("SERIAL_NUMBER"));
		param.put("STATE", "0");
		
		IDataset userRealnameInfos = RealNameJudgeQry.qryRealNameInfo(param);

		 if(userRealnameInfos.size()>0 || !userRealnameInfos.isEmpty() ){
			 
			 result.put("RETURN_CODE", "0000");  
			 result.put("RETURN_MESSAGE", "success");
			 result.put("IS_REG", "1");
		 }else{
			 result.put("RETURN_CODE", "2999");  
			 result.put("RETURN_MESSAGE", "用户查找无记录或已经进行过补登录操作 ");
			 result.put("IS_REG", "0");
		 }
		 */
		result.put("RETURN_CODE", "0000");  
		result.put("RETURN_MESSAGE", "success");
		result.put("IS_REG", "1");
		
		return result;
	}
	
	/**
	 * @Description 自助卡校验
	 * @param data
	 * @return result
	 * @throws Exception
	 */
	public IData checkSimCardState(IData data) throws Exception {
		IData result = new DataMap();
       
//		//log.info("("linsl checkSimCardState start!");
		// 获取用户资料
		IDataset userList =  UserInfoQry.getUserInfoBySn(data.getString("SERIAL_NUMBER"), "0");
		
		if (userList == null || userList.size() < 1) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "用户资料不存在！");
			
			return result;
		}
		
		UcaData uca = null;
		uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
		
		if("1".equals(uca.getCustomer().getIsRealName()) /*&& "1".equals(uca.getCustomer().getRsrvTag3())*/)
		{
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "用户已经是实名制用户！ ");
			
			return result;
		}
		
//		//log.info("("linsl check sim card start:");
		//获取SIM卡号，效验接口入参传入的SIM卡号信息是否正确
		IDataset userRes = UserResInfoQry.getUserResource(uca.getUserId());
		
		if(userRes != null && userRes.size() > 0)
		{
			if(!data.getString("SIM_CARD_NO").equals(userRes.getData(0).getString("RES_CODE")))
			{
//				//log.info("("linsl sim card no = " + data.getString("SIM_CARD_NO"));
//				//log.info("("linsl res code = " + userRes.getData(0).getString("RES_CODE"));
				result.put("RETURN_CODE", "2999"); 
				result.put("RETURN_MESSAGE", "手机号和SIM卡号不匹配！");
				
				
				return result;
			}
		}else
		{
			result.put("RETURN_CODE", "2999"); 
			result.put("RETURN_MESSAGE", "获取不到用户SIM卡号信息！");
			
			return result;
		}
		
		
		
		/*
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		param.put("PREOPEN_TAG", "1");
		param.put("EPARCHY_CODE", RouteInfoQry.getEparchyCodeBySn(data.getString("SERIAL_NUMBER")));
       IDataset resList= RealNameJudgeQry.getMphoneUse(param);
		
       if(resList.size()<1 || resList.isEmpty()){
    	    result.put("RETURN_CODE", "2999"); // 实名等级获取失败
			result.put("RETURN_MESSAGE", "用户资源数据不存在！");
       }else{
    	   if(data.getString("SIM_CARD_NO").equals(resList.getData(0).getString("SIM_CARD_NO"))){
    		   result.put("RETURN_CODE", "0000"); // 实名等级获取失败
    		   result.put("RETURN_MESSAGE", "success");
    	   }else{
	    		result.put("RETURN_CODE", "2999"); // 实名等级获取失败
	   			result.put("RETURN_MESSAGE", "用户号码和SIM卡号不匹配！");
    	   }
    	   
       }
*/
		result.put("RETURN_CODE", "0000"); // 实名等级获取失败
		result.put("RETURN_MESSAGE", "success");
		
		
		return result;
	}
	

	/**
	 * @Description 用户号码校验（号码+服务密码），针对已激活的用户
	 * @param data
	 * @return result
	 * @throws Exception
	 */
	public IData verifyUserNumber(IData data) throws Exception {
		IData result = new DataMap();
		result.put("RETURN_CODE", "0000"); // 默认用户号码校验成功
		result.put("RETURN_MESSAGE", "success");
		
		// 获取用户资料
		IDataset userList =  UserInfoQry.getUserInfoBySn(data.getString("SERIAL_NUMBER"), "0");
		
		if (userList == null || userList.size() < 1) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "用户资料不存在");
			return result;
		}	
		
		String userid = userList.getData(0).getString("USER_ID");
		String userpasswd = userList.getData(0).getString("USER_PASSWD");
		data.put("USER_ID", userid);

		// 判断用户密码
		if (null == userpasswd || "".equals(userpasswd))// 用户服务密码不存在
		{
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "用户服务密码不存在");
			return result;
		}
		
		boolean flag = UserInfoQry.checkUserPassWd(userid, data.getString("PASS_WORD"));
		if (!flag)// 密码错误
		{
			result.put("RETURN_CODE", "1002");//按照实名平台规范，将错误编码2999改为21002
			result.put("RETURN_MESSAGE", "服务密码错误");
			return result;
		} 
		
		UcaData uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
		SvcStateTradeData svcStateData = uca.getUserSvcsStateByServiceId("0");
		if(svcStateData != null){
			String stateCode = svcStateData.getStateCode();
			if("B".equals(stateCode) || "A".equals(stateCode) || "G".equals(stateCode)){
				result.put("RETURN_CODE", "2999");
				result.put("RETURN_MESSAGE", "号码已欠费停机，请缴清欠费后再办理");
				return result;
			}
		}
		
	    return result;
	}
	
	public IData realNameRegiste(IData input) throws Exception{
//		 3、实名登记接口（1、修改客户资料  2、修改实名登记、3、对于自助开卡用户做激活。）

		IData data = new DataMap();
		IData result = new DataMap();
		
		if(!"99".equals(input.getString("BUSI_TYPE")))
		{
		
			IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
			if(IDataUtil.isEmpty(userInfo)){
				result.put("RETURN_CODE", "1006");
				result.put("RETURN_MESSAGE", "预销号");
				result.put("IS_REG", "0");
				return result;
			}
			
			String user_diff_code = userInfo.getString("USER_DIFF_CODE");
			//校验是否为集团客户
			if (!"0".equals(user_diff_code)) {
				result.put("RETURN_CODE", "1008");
				result.put("RETURN_MESSAGE", "集团客户号码");
				result.put("IS_REG", "0");
				return result;
			}
			
	        IData  custInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
	        
			if ("1".equals(custInfo.getString("IS_REAL_NAME"))) {
				result.put("RETURN_CODE", "1004");  
				result.put("RETURN_MESSAGE", "号码已经实名");
				result.put("IS_REG", "0");
				return result;
			}
		}
		
		UcaData uca = null;
		uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
		data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		data.put("USER_ID", uca.getUserId());
		data.put("CUST_ID", uca.getCustId());
		
		IData custInfos = CSAppCall.callOne("SS.ModifyCustInfoSVC.getCustInfo", data);

		IData params = new DataMap(custInfos.getData("CUST_INFO"));
		params.put("PSPT_TYPE_CODE", "0");
		params.put("PSPT_ID", input.getString("CUST_CERT_NO"));
		params.put("PSPT_ADDR", input.getString("CUST_CERT_ADDR"));
/*		params.put("RSRV_STR2", "1");
		params.put("RSRV_STR8", "REALNAMEWEIXIN");*/
		params.put("CUST_CITY_CODE", input.getString("TRADE_CITY_CODE"));
				
		if("1".equals(input.getString("GENDER", "")))
		{
			params.put("SEX", "M");
		}else if("0".equals(input.getString("GENDER", "")))
		{
			params.put("SEX", "F");
		}else
		{
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "性别入参不正确！");	
			return result;
		}
		
		IData tmp = new DataMap();
		tmp.put("NATION", input.getString("NATION"));
		SQLParser parser = new SQLParser(tmp);
		parser.addSQL("  SELECT * FROM TD_S_STATIC A " );
		parser.addSQL("  WHERE A.TYPE_ID= 'TD_S_FOLK' " );
		parser.addSQL("  AND A.DATA_NAME like '%' || :NATION || '%'" );
	
		IDataset tempIdata = new DatasetList();
		tempIdata = Dao.qryByParse(parser,Route.CONN_CRM_CEN);
		
		if(tempIdata != null && tempIdata.size() > 0)
		{
			params.put("FOLK_CODE", tempIdata.getData(0).getString("DATA_ID"));
		}
			
		
		try{
			params.put("CUST_NAME", input.getString("CUST_NAME"));
			params.put("PAY_NAME", input.getString("CUST_NAME"));
			params.put("PSPT_END_DATE", input.getString("CERT_EXPDATE"));
			params.put("PAY_MODE_CODE","0");
			params.put("REMARK", "微信实名认证");
			params.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
	        params.put("TRADE_TYPE_CODE", input.getString("TRADE_TYPE_CODE"));
	        params.put("CHANNEL_ID", input.getString("CHANNEL_ID"));
	        params.put("ISSUING_AUTHORITY", input.getString("ISSUING_AUTHORITY"));
	        params.put("BIRTHDAY", input.getString("BIRTHDAY"));
	        params.put("TRANSACTION_ID", input.getString("TRANSACTION_ID"));
	        params.put("PSPT_START_DATE", input.getString("CERT_VALIDDATE"));
	        params.put("IS_REAL_NAME", "1");
	        
	        //信用购机增加
	        if("1".equals(input.getString("CREDIT_PAY_TAG"))){
	        	 IDataset orderInfos=SelfTerminalBean.queryPreOrderInfo(input.getString("CREDIT_ORDER_ID"), input.getString("CREDIT_SUBORDER_ID"));
	        	 if(IDataUtil.isNotEmpty(orderInfos)){
	        		 String saleProductId=orderInfos.getData(0).getString("RSRV_STR3");
	        		 String salePackageId=orderInfos.getData(0).getString("RSRV_STR4");
	        		 if(StringUtils.isNotEmpty(saleProductId)&&StringUtils.isNotEmpty(salePackageId)){
	        			 params.put("CREDIT_PAY_TAG", "1");
	        			 params.put("CREDIT_PRODUCT_ID", saleProductId);
	        			 params.put("CREDIT_PACKAGE_ID", salePackageId);
	        		 }
	        	 }
	        }
	        
			IDataset outparams = CSAppCall.call("SS.ModifyCustInfoIntfRegSVC.tradeReg", params);
				
			if(IDataUtil.isEmpty(outparams) || "-1".equals(outparams.getData(0).getString("ORDER_ID","-1")))
			{
				result.put("RETURN_CODE", "2999");
				result.put("RETURN_MESSAGE", "办理失败");	
				return result;
			}
			result.put("ORDER_ID", outparams.getData(0).getString("ORDER_ID","-1"));
			result.put("TRADE_ID", outparams.getData(0).getString("TRADE_ID","-1"));
				
		}
		catch (Exception e){
		        log.equals(e);
			result.put("RETURN_CODE", "1001");
			result.put("RETURN_MESSAGE", "办理失败" + e.getMessage());
			return result;	
		}
		
		result.put("RETURN_CODE", "0000");
		result.put("RETURN_MESSAGE", "success");
		
		return result;
		
	}
	
}
