package com.asiainfo.veris.crm.order.soa.person.busi.jrbank;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.RelationBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class PreSignbankSVC extends CSBizService {
	
	/**
	 * 预签约接口
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData preSignbank(IData data) throws Exception{
		
		IDataUtil.chkParam(data, "SERIAL_NUMBER");//USER_ID相当于SERIAL_NUMBER  
		IDataUtil.chkParam(data, "USER_ID_TYPE");//中国移动用户表示类型 01:手机号  飞信号
		IDataUtil.chkParam(data, "PROVINCE_CODE");
		data.put("IS_INTF", "1");
		
		String userId = getUserIdBySn(data.getString("SERIAL_NUMBER"));
		
		IData returnData = new DataMap(); 
//		IData param = new DataMap();
//		param.put("USER_ID", input.getString("SERIAL_NUMBER"));
		IDataset userRelationSet = RelationBankInfoQry.querySignBankByUid(userId);
		if(IDataUtil.isNotEmpty(userRelationSet)){
			String bankIdTemp = userRelationSet.getData(0).getString("BANK_ID","");
			if(StringUtils.isEmpty(bankIdTemp)){
	    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取BANK_ID为空!");
			}
			String bankId = "0000" + bankIdTemp; 
			returnData.put("BANK_ID", bankId.substring(bankId.length()-4));//---BANK_ID
			returnData.put("X_RESULTCODE", "711003");
			return returnData;
		}
		
		IDataset resultvip = CommparaInfoQry.getCommPkInfo("CSM", "193", "1", getTradeEparchyCode());
		if(IDataUtil.isEmpty(resultvip)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取预签约接口参数配置失败!");
		}
		returnData.put("DEF_RECH_THRESHOLD", resultvip.getData(0).getString("PARA_CODE1"));
		returnData.put("MAX_RECH_THRESHOLD", resultvip.getData(0).getString("PARA_CODE2"));
		returnData.put("DEF_RECH_AMOUNT", resultvip.getData(0).getString("PARA_CODE3"));
		returnData.put("MAX_RECH_AMOUNT", resultvip.getData(0).getString("PARA_CODE4"));
		
		
		//台帐处理
		IDataset dataset = CSAppCall.call("SS.CreateSignBankRegSVC.tradeReg", data);
		//ystem.out.println("out dataset=========="+dataset);
		if(IDataUtil.isNotEmpty(dataset)){
			returnData.put("SESSION_ID", dataset.getData(0).getString("TRADE_ID")+"0000");
		}
		
		return returnData;
	}
	
	/**
	 * 通知签约接口
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData noticeSignContract(IData data) throws Exception{
		IDataUtil.chkParam(data, "SESSION_ID");//TRADE_ID
		IDataUtil.chkParam(data, "SUB_ID");//签约协议号
		IDataUtil.chkParam(data, "SUB_TIME");//签约关系生成时间	格式为YYYYMMDDHHmmSS
		IDataUtil.chkParam(data, "ACCOUNT_CAT");//0-借记卡	1-信用卡
		IDataUtil.chkParam(data, "PAY_TYPE");//0-主动缴费  1-自动缴费
		IDataUtil.chkParam(data, "BANK_ID");
		
		IData inparam = new DataMap();
		String tradeId = data.getString("SESSION_ID");
		if(tradeId.length() != 20){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "SESSION_ID的长度出错!");
		}
		inparam.put("TRADE_ID", tradeId.substring(0, tradeId.length()-4));
		
		//如果用户为预付费并且设置自动缴费方式，携带该字段
		inparam.put("RECH_THRESHOLD", data.getString("RECH_THRESHOLD",""));//充值阀值，单位为 分
		inparam.put("RECH_AMOUNT", data.getString("RECH_AMOUNT",""));//充值额度，单位为 分
		inparam.put("SUB_ID", data.getString("SUB_ID"));
		inparam.put("SUB_TIME", data.getString("SUB_TIME"));
		inparam.put("ACCOUNT_CAT", data.getString("ACCOUNT_CAT"));
		inparam.put("PAY_TYPE", data.getString("PAY_TYPE"));
		inparam.put("BANK_ID", data.getString("BANK_ID"));
		inparam.put("USER_ACCOUNT", data.getString("USER_ACCOUNT",""));//银行账号信息
		
		int count = Dao.executeUpdateByCodeCode("TF_B_TRADE_RELATION_BANK", "UPD_BANK_DATA_BY_TRADE_ID", inparam, Route.getJourDb(BizRoute.getTradeEparchyCode()));
		if(count == 0){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "没找到TF_B_TRADE_RELATION_BANK表对应的台账信息!!");
		}
		//更新主台账里面的执行时间为立即执行
		inparam.clear();
		inparam.put("TRADE_ID", tradeId.substring(0, tradeId.length()-4));
		inparam.put("EXEC_TIME", SysDateMgr.getSysTime());
		count = Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_EXEC_TIME_BY_TRADE_ID", inparam, Route.getJourDb(BizRoute.getTradeEparchyCode()));
		if(count == 0){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "没找到主台账,请确认是否已完工!");
		}
		
		return new DataMap();
	}
	/**
	 * 解约接口  直接更新表中的数据，结束时间
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception 
	 * @throws Exception
	 */
	 public IData cancelSignContract(IData data) throws Exception{
		IDataUtil.chkParam(data, "SERIAL_NUMBER");//USER_ID相当于SERIAL_NUMBER  
		IDataUtil.chkParam(data, "SUB_ID");//签约协议号
		IDataUtil.chkParam(data, "USER_TYPE");
		IDataUtil.chkParam(data, "PROVINCE_CODE");
		data.put("IS_INTF", "1");
		data.put("SELECT_VALUES", data.getString("SUB_ID"));
		
		String userId = getUserIdBySn(data.getString("SERIAL_NUMBER"));
		
		IData returnData = new DataMap();
		IDataset bankList = RelationBankInfoQry.querySignBankList(userId, getTradeEparchyCode());
    	if(IDataUtil.isEmpty(bankList)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户没有签约!");
    	}else{
    		//判断是否是传来的这个签约协议号
			boolean flag = true;
			for(int i=0;i<bankList.size();i++){
				String subId = bankList.getData(i).getString("SUB_ID");
				if(subId.equals(data.getString("SUB_ID"))){
					flag = false;
					break;
				}
			}
			if(flag){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该签约信息为其它银行账号关联!");
			}
    	}
    	returnData.put("BANK_ID", bankList.getData(0).getString("BANK_ID"));
    	
    	//台帐处理
		IDataset dataset = CSAppCall.call("SS.CancelSignBankRegSVC.tradeReg", data);
		
		return returnData;
	 }
	
	 public String getUserIdBySn(String sn) throws Exception{
		 IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", sn);
		 if(IDataUtil.isEmpty(userinfo)){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户信息不存在!");
		 }
		 return userinfo.getData(0).getString("USER_ID");
	 }
	

}
