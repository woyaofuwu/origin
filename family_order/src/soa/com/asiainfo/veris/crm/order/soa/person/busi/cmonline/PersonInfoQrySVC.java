package com.asiainfo.veris.crm.order.soa.person.busi.cmonline;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ComFuncUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.fee.FeeListMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.view360.Qry360THInfoBean;
import com.asiainfo.veris.crm.order.soa.person.busi.view360.QryUserInfoBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.FuzzyPsptUtil;

import org.apache.log4j.Logger;

public class PersonInfoQrySVC extends CSBizService{
	
	private static final long serialVersionUID = 1L;
	private static transient final Logger log = Logger.getLogger(PersonInfoQrySVC.class);
	
	/*
	 * 用户信息查询
	 */
	public IData C898HQQueryCustInfo(IData inParamStr) throws Exception{
		IData inParam = new DataMap(inParamStr.toString());
		IDataset dataset = new DatasetList();
		IData data = new DataMap();
		data.put("FIELD", "userMobile");
		data.put("TYPE", "String");
		dataset.add(data);
		IData inParamResult = ComFuncUtil.checkInParam(inParam.getData("params"), dataset);
		if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
			return inParamResult;
		}
		
		String  serialNumber = inParam.getData("params").getString("userMobile");
		IData input = new DataMap();
		input.put("SERIAL_NUMBER", serialNumber);
		/*QryUserInfoBean qryUserInfoBean = BeanManager.createBean(QryUserInfoBean.class);
		IDataset userInfos = qryUserInfoBean.qryUserInfoBySerialNumber(input, getPagination());
		IData userInfo = userInfos.getData(0);
		if (IDataUtil.isEmpty(userInfos)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到"+serialNumber + "相关的用户信息");
		}*/
		String stoptypeCode = "";
		String userId = "";
		String custId = "";
		String userStateCodeset = "";
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber,RouteInfoQry.getEparchyCodeBySn(serialNumber));//加上remove_tag 的条件
        IDataset mebNumInfoS = UserInfoQry.getAllDestroyUserInfoBySn(serialNumber);//加上remove_tag 的条件
        
        if(IDataUtil.isNotEmpty(mebNumInfoS)&&IDataUtil.isEmpty(userInfo)){
        	userId = mebNumInfoS.getData(0).getString("USER_ID");
        	custId = mebNumInfoS.getData(0).getString("CUST_ID");
        	String removeTag = mebNumInfoS.getData(0).getString("REMOVE_TAG");
        	if("1".equals(removeTag)||"3".equals(removeTag)){
        		stoptypeCode="03";
        	}else{
        		stoptypeCode="04";
        	}
        }else if(IDataUtil.isEmpty(mebNumInfoS)&&IDataUtil.isNotEmpty(userInfo)){
        	userId=userInfo.getString("USER_ID");
        	custId=userInfo.getString("CUST_ID");
        	userStateCodeset=userInfo.getString("USER_STATE_CODESET");
        	stoptypeCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]{"TYPE_ID","DATA_ID"}, "PDATA_ID", new String[]{"USER_STATE_K",userStateCodeset});
        }else if(IDataUtil.isEmpty(mebNumInfoS)&&IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到"+serialNumber + "相关的用户信息");
        }
		
		IData creditInfo = CreditCall.queryUserCreditInfos(userId);
		if(IDataUtil.isEmpty(creditInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到用户相关的信用信息");
		}
		IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
		if(IDataUtil.isEmpty(custInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到客户信息");
		}
		float balance = 0.0f;
		IData oweFeeData = AcctCall.getOweFeeByUserId(userId);
		if (IDataUtil.isNotEmpty(oweFeeData)) {
			String ACCT_BALANCE = oweFeeData.getString("ACCT_BALANCE", "");
			if (StringUtils.isNotBlank(ACCT_BALANCE)) {
				balance = Float.parseFloat(ACCT_BALANCE) / 100.0f; //acct_balance < 0表示欠费
			}
		}
		/*if ("0".equals(userStateCodeset) && "0".equals(removeTag) && balance > 0) {
			stoptypeCode = "1";
			stoptypeDesc = "正常";
		} else if (balance < 0) {
			stoptypeCode = "2";
			stoptypeDesc = "欠费";
		} else if (!"0".equals(userStateCodeset) && "0".equals(removeTag)) {
			stoptypeCode = "3";
			stoptypeDesc = "停机";
		} else if (!"0".equals(removeTag)) {
			stoptypeCode = "4";
			stoptypeDesc = "销号";
		} else {
			stoptypeCode = "9";
			stoptypeDesc = "其他";
		}*/
		
		// 中高端标识
        IDataset highCust = CustVipInfoQry.queryHighCustByUserId(userInfo.getString("USER_ID"), "*");
        String highLevelId = "";
        if (IDataUtil.isNotEmpty(highCust))
        {
            highLevelId = "1";
        }
        else
        {
            highLevelId = "0";
        }
        
        //查询用户SIM卡号
        String simCardNo = "";
        IDataset userRes = UserResInfoQry.getUserResInfoByUserId(userId);
        if(IDataUtil.isNotEmpty(userRes)){
        	for(int i=0;i<userRes.size();i++){
            	if("1".equals(userRes.getData(i).getString("RES_TYPE_CODE"))){
            		simCardNo = userRes.getData(i).getString("RES_CODE");
            	}
            }
    		
        }
        
        //省编码
        String provinceCode = StaticUtil.getStaticValue("PROVINCE_CODE", ProvinceUtil.getProvinceCode());
		IDataset result = new DatasetList();
		IData returnObj = new DataMap();
		IData returnMap = new DataMap();
		IData resultInfo = new DataMap();
		IData returnUserInfo = new DataMap();

		resultInfo.put("userName", FuzzyPsptUtil.fuzzyName(custInfo.getString("CUST_NAME")));
		resultInfo.put("userStatus", stoptypeCode);//用户状态编码
		resultInfo.put("userBegin", userInfo.getString("IN_DATE"));//入网时间
		resultInfo.put("userID", userInfo.getString("USER_TYPE_CODE"));//用户标识
		resultInfo.put("starLevel", creditInfo.getString("CREDIT_CLASS"));//客户星级编码
		resultInfo.put("starScore", creditInfo.getString("STAR_SCORE"));//星级得分
		
		resultInfo.put("starTime", creditInfo.getString("END_DATE",""));//星级有效期
		resultInfo.put("realNameInfo", custInfo.getString("IS_REAL_NAME"));
		resultInfo.put("provCode", provinceCode);
		resultInfo.put("cityCode", userInfo.getString("CITY_CODE"));
		resultInfo.put("highLevelId", highLevelId);//中高端标识
		resultInfo.put("simCardNo", simCardNo);
		
		returnUserInfo.put("userInfo", resultInfo);
		result.add(returnUserInfo);
		returnObj.put("result", result);
		returnObj.put("respCode", ComFuncUtil.CODE_RIGHT);
		returnObj.put("respDesc", "success");
		returnMap.put("object", returnObj);
		returnMap.put("rtnCode", "0");
		returnMap.put("rtnMsg", "成功！");
		return returnMap;
	}
	
	/*
	 * 亲情号码查询
	 */
	public IData C898HQQueryFamNumber(IData inParamStr) throws Exception{
		IData inParam = new DataMap(inParamStr.toString());
		//入参非空判断
		IDataset dataset = new DatasetList();
		IData data = new DataMap();
		data.put("FIELD", "userMobile");
		data.put("TYPE", "String");
		dataset.add(data);
		IData inParamResult = ComFuncUtil.checkInParam(inParam.getData("params"), dataset);
		if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
			return inParamResult;
		}
		
		String serialNumber = inParam.getData("params").getString("userMobile");
		IData input = new DataMap();
		input.put("SERIAL_NUMBER", serialNumber);
		QryUserInfoBean qryUserInfoBean = BeanManager.createBean(QryUserInfoBean.class);
		IDataset userInfos = qryUserInfoBean.qryUserInfoBySerialNumber(input, getPagination());
		IData userInfo = userInfos.getData(0);
		
		String userId = userInfo.getString("USER_ID");
		String userIdA = "";
		String relationTypeCode = "75";
		IDataset relationInfo = RelaUUInfoQry.isMasterAccount(userId,relationTypeCode);
		if(IDataUtil.isEmpty(relationInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到用户的亲情关系信息");
		}
		for(int i=0;i<relationInfo.size();i++){
			if("1".equals(relationInfo.getData(i).getString("ROLE_CODE_B"))){
				userIdA = relationInfo.getData(i).getString("USER_ID_A");
			}
		}
		
		if(StringUtils.isBlank(userIdA)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户未设置过亲情号码");
		}
		IDataset QQRelationInfo = RelaUUInfoQry.getUserRelationAll(userIdA,relationTypeCode);
		if(IDataUtil.isEmpty(QQRelationInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户未设置过亲情号码");
		}
		
		//取亲情关系的serial_number_b副号码
		IData famNumber = new DataMap();
		for(int j=0;j<QQRelationInfo.size();j++){
			if(!"1".equals(QQRelationInfo.getData(j).getString("ROLE_CODE_B"))){
				famNumber.put("famNumber"+j, QQRelationInfo.getData(j).getString("SERIAL_NUMBER_B"));
			}
		}
		
		IDataset famNumList = new DatasetList();
		IDataset result = new DatasetList();
		IData famNumMap = new DataMap();
		IData resultMap = new DataMap();
		IData returnObj = new DataMap();
		IData object = new DataMap();
		
		
		famNumList.add(famNumber);
		famNumMap.put("famNumList", famNumList);
		result.add(famNumMap);
		resultMap.put("result", result);
		resultMap.put("respCode", "0");
		resultMap.put("respDesc", "success");
		returnObj.put("object", resultMap);
		returnObj.put("rtnCode", "0");
		returnObj.put("rtnMsg", "成功!");
		return returnObj;
	}
	
	/*
	 * 业务历史查询
	 */
	public IData C898HQQueryTradeInfo(IData inParamStr) throws Exception{
		IData inParam = new DataMap(inParamStr.toString());
		//入参非空判断
		IDataset dataset = new DatasetList();
		IData dataMap1 = new DataMap();
		dataMap1.put("FIELD", "userMobile");
		dataMap1.put("TYPE", "String");
		dataset.add(dataMap1);
		
		IData dataMap2 = new DataMap();
		dataMap2.put("FIELD", "startTime");
		dataMap2.put("TYPE", "String");
		dataset.add(dataMap2);
		
		IData dataMap3 = new DataMap();
		dataMap3.put("FIELD", "endTime");
		dataMap3.put("TYPE", "String");
		dataset.add(dataMap3);
		
		IData inParamResult = ComFuncUtil.checkInParam(inParam.getData("params"), dataset);
		if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
			return inParamResult;
		}	
		
		
		String serialNumber = inParam.getData("params").getString("userMobile");
		String tradeTypeCode = inParam.getData("params").getString("tradeTypeCode","");
		String startTime = inParam.getData("params").getString("startTime");
		String endTime = inParam.getData("params").getString("endTime");
		String trade_staff_id = inParam.getData("params").getString("operatorId");//k3
		String carryTag = inParam.getData("params").getString("carryTag","0");//是否查询携出地业务历史  0表示否，1表示是   默认不查
		
		IDataset tradeInfo = new DatasetList();
		IDataset tempInfo = new DatasetList();
		IDataset resultInfo = new DatasetList();
		IData tradeMap2 = new DataMap(); 
		IData returnMap = new DataMap();
		IData object = new DataMap();
		IData result = new DataMap();
		if("0".equals(carryTag)||"".equals(carryTag)){
			try {
				//加返销业务查询
				String queryType= inParam.getData("params").getString("queryType");
				if("1".equals(queryType)){
					IData inParams=new DataMap();
					inParams.put("SERIAL_NUMBER", serialNumber);
					tradeInfo=CSAppCall.call("SS.CancelChangeProductSVC.queryChangeProductTrade", inParams);
				}else{
					tradeInfo = this.qryTradeHisInfo(serialNumber, tradeTypeCode, startTime, endTime,trade_staff_id);
				}
				if(IDataUtil.isNotEmpty(tradeInfo)){
					for(int i=0;i<tradeInfo.size();i++){
						
						String tradeId = tradeInfo.getData(i).getString("TRADE_ID","");
						String typeCode = tradeInfo.getData(i).getString("TRADE_TYPE_CODE","");
						String tradeTypeName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", typeCode);//业务类型名称
						String userMobile = tradeInfo.getData(i).getString("SERIAL_NUMBER","");
						String opStaffId = tradeInfo.getData(i).getString("TRADE_STAFF_ID","");
						
						String opStaffName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", opStaffId);//受理员工名
						String opOrgId = tradeInfo.getData(i).getString("UPDATE_DEPART_ID", "");
						String opOrgName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", opOrgId);//受理部门名
						String opChannel =tradeInfo.getData(i).getString("IN_MODE_CODE", "");
						String opChannelName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_INMODE", "IN_MODE_CODE", "IN_MODE", opChannel);//受理渠道名称
						
						
						if("6".equals(opChannel)){
							IDataset commparaSet = CommparaInfoQry.getCommparaInfoByCode2("CSM", "7979", "UNI_CHANNEL_STAFF", opStaffId, "0898");
			                if(IDataUtil.isNotEmpty(commparaSet)&&commparaSet.size()>0){
			                	opStaffName = commparaSet.getData(0).getString("PARA_CODE2");
			                	opOrgName = commparaSet.getData(0).getString("PARA_CODE6");
			                }
						}
						
						String opTime =tradeInfo.getData(i).getString("ACCEPT_DATE","");
						String opRegionCode = tradeInfo.getData(i).getString("TRADE_EPARCHY_CODE", "");
						//String tradeState = tradeInfo.getData(i).getString("SUBSCRIBE_STATE","");
						String type = tradeInfo.getData(i).getString("RSRV_STR1","");
						String strContent = tradeInfo.getData(i).getString("PROCESS_TAG_SET","");
						String authType = "密码校验";
						if(strContent.length()>=20){
							strContent = strContent.substring(19, 20);
							if("0".equals(strContent)){
								authType="证件号码校验";
							}else if("1".equals(strContent)){
								authType="服务密码校验";
							}else if("2".equals(strContent)){
								authType="SIM卡号+服务密码校验";
							}else if("3".equals(strContent)){
								authType="服务号码+证件号码校验";
							}else if("4".equals(strContent)){
								authType="证件号码+服务密码校验";
							}else if("5".equals(strContent)){
								authType="SIM卡号+短信验证码校验";
							}else if("6".equals(strContent)){
								authType="服务密码+验证码";
							}else if("7".equals(strContent)){
								authType="验证码";
							}else if("8".equals(strContent)){
								authType="SIM卡号(或白卡号)";
							}else if("9".equals(strContent)){
								authType="有效证件+验证码";
							}else if("Z".equals(strContent)){
								authType="未知认证";
							}else if("F".equals(strContent)){
								authType="免认证";
							}else{
								authType="未知校验方式";
							}
						}
						if("77".equals(typeCode)){
							type=type.substring(0, 1);
							if("2".equals(type)){
								authType="证件资料解锁";
							}
						}
						String cancleTag = tradeInfo.getData(i).getString("CANCEL_TAG","");
						if("0".equals(cancleTag)){
							cancleTag="未返销";
						}else if("1".equals(cancleTag)){
							cancleTag="已返销";
						}
						String remark = tradeInfo.getData(i).getString("REMARK", "");
						
						int operFee = Integer.parseInt(tradeInfo.getData(i).getString("OPER_FEE", "0"));
						int foreGift = Integer.parseInt(tradeInfo.getData(i).getString("FOREGIFT", "0"));
						int advancePay = Integer.parseInt(tradeInfo.getData(i).getString("ADVANCE_PAY", "0"));
						int tradeFee = operFee + foreGift +advancePay;//受理总费用
						
						IData tradeMap =new DataMap();
						tradeMap.put("tradeId", tradeId);
						tradeMap.put("tradeTypeName", tradeTypeName);
						tradeMap.put("userMobile", userMobile);
						tradeMap.put("opStaffId", opStaffId);
						
						tradeMap.put("opStaffName", opStaffName);
						tradeMap.put("opOrgId", opOrgId);
						tradeMap.put("opOrgName", opOrgName);
						tradeMap.put("opChannel", opChannel);
						tradeMap.put("opChannelName", opChannelName);
						tradeMap.put("opTime", opTime);
						
						tradeMap.put("opRegionCode", opRegionCode);
						tradeMap.put("tradeFee", String.valueOf(tradeFee));
						tradeMap.put("authType", authType);
						tradeMap.put("isPrivilege", "");
						tradeMap.put("tradeState", "已完工");
						tradeMap.put("cancelTag", cancleTag);
						tradeMap.put("remark", remark);
						tradeMap.put("ext1", tradeInfo.getData(i).getString("ORDER_ID",""));
						tradeMap.put("ext2", "");
						tradeMap.put("ext3", "");
						tradeMap.put("ext4", "");
						tradeMap.put("ext5", "");
						tradeMap.put("ext6", "");
						tradeMap.put("ext7", "");
						tradeMap.put("ext8", "");
						tradeMap.put("ext9", "");
						tradeMap.put("ext10", "");
						tempInfo.add(tradeMap);
					}
				}else{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到业务历史信息");
				}
			} catch (Exception e) {
				
				tradeMap2.put("tradeInfoList", tempInfo);
				resultInfo.add(tradeMap2);
				returnMap.put("result", resultInfo);
				returnMap.put("resultRows", tradeInfo.size());
				returnMap.put("respCode", "-9999");
				returnMap.put("respDesc",e.getMessage());
				object.put("object", returnMap);
				object.put("rtnCode", "-9999");
				object.put("rtnMsg",e.getMessage());
				return object;
			}
		}
		tradeMap2.put("tradeInfoList", tempInfo);
		resultInfo.add(tradeMap2);
		returnMap.put("result", resultInfo);
		returnMap.put("resultRows", tradeInfo.size());
		returnMap.put("respCode", "0");
		returnMap.put("respDesc", "success");
		object.put("object", returnMap);
		object.put("rtnCode", "0");
		object.put("rtnMsg", "成功!");
		return object;
	}
	
	
	/*
	 * 业务类型参数查询
	 */
	public IData C898HQQueryTradeType(IData inParamStr)throws Exception{
		IData inParam = new DataMap(inParamStr.toString());
		//入参非空判断
		IDataset dataset = new DatasetList();
		IData dataMap1 = new DataMap();
		dataMap1.put("FIELD", "provCode");
		dataMap1.put("TYPE", "String");
		dataset.add(dataMap1);
		
		IData inParamResult = ComFuncUtil.checkInParam(inParam.getData("params"), dataset);
		if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
			return inParamResult;
		} 
		
		
		String provCode = inParam.getData("params").getString("provCode");
		IData input = new DataMap();
		input.put("EPARCHY_CODE", "0"+provCode);
		SQLParser parser = new SQLParser(input);
        parser.addSQL(" select TRADE_TYPE_CODE, TRADE_TYPE from td_s_tradetype a ");
        parser.addSQL(" Where a.eparchy_code = :EPARCHY_CODE ");
		IDataset tradeTypeList = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
		
		IDataset tempList = new DatasetList();
		if(IDataUtil.isNotEmpty(tradeTypeList)){
			for(int i=0;i<tradeTypeList.size();i++){
				IData tradeTypeMap = new DataMap();
				tradeTypeMap.put("tradeTypeCode", tradeTypeList.getData(i).getString("TRADE_TYPE_CODE"));
				tradeTypeMap.put("tradeTypeName", tradeTypeList.getData(i).getString("TRADE_TYPE"));
				tempList.add(tradeTypeMap);
			}
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到业务类型参数信息");
		}
		
		IData tradeType = new DataMap();
		IData result = new DataMap();
		IData object = new DataMap();
		IDataset resultList = new DatasetList();
		
		tradeType.put("tradeTypeList", tempList);
		resultList.add(tradeType);
		result.put("result", resultList);
		result.put("respCode", "0");
		result.put("respDesc", "success");
		
		object.put("object", result);
		object.put("rtnCode", "0");
		object.put("rtnMsg", "成功!");
		return object;
	}
	
	/*
	 * 业务费用明细查询
	 */
	public IData C898HQQueryTradeFeeInfo(IData inParamStr)throws Exception{
    	IData result = new DataMap();
    	IData object = new DataMap();
    	IData data = new DataMap();
		try{
			IData inParam = new DataMap(inParamStr.toString());
			//入参非空判断
			IDataset dataset = new DatasetList();
			IData dataMap1 = new DataMap();
			dataMap1.put("FIELD", "tradeId");
			dataMap1.put("TYPE", "String");
			dataset.add(dataMap1);
			
			IData dataMap2 = new DataMap();
			dataMap2.put("FIELD", "opRegionCode");
			dataMap2.put("TYPE", "String");
			dataset.add(dataMap2);
			
			IData inParamResult = ComFuncUtil.checkInParam(inParam.getData("params"), dataset);
			if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
				return inParamResult;
			}
			
			
			String tradeId = inParam.getData("params").getString("tradeId");
			String eparchyCode = inParam.getData("params").getString("opRegionCode");
			String userMobile = inParam.getData("params").getString("userMobile");
	
			IData input = new DataMap();
			IData feeMap = new DataMap();
			IDataset tradeFeeList = new DatasetList();
			input.put("TRADE_ID", tradeId);
			
			IDataset feeList = Dao.qryByCodeParser("TF_B_TRADEFEE_SUB", "SEL_BY_TRADE_ID", input, Route.getJourDb(eparchyCode));
			if(IDataUtil.isNotEmpty(feeList))
	    	{
	    		for (int i = 0; i < feeList.size(); i++)  
	    		{
	    			feeMap = feeList.getData(i);
	    			String feeTypeCode = feeMap.getString("FEE_TYPE_CODE","");
	    			String feeModeCode = feeMap.getString("FEE_MODE","");
	    			String fee = feeMap.getString("FEE","");
	    			String remark = feeMap.getString("REMARK","");
	    			String feeTypeName = FeeListMgr.getFeeTypeCodeDesc(feeModeCode, feeTypeCode);
	    			feeMap.clear();
	    			feeMap.put("feeTypeCode", feeTypeCode);
	    			feeMap.put("feeTypeName", feeTypeName);
	    			feeMap.put("fee",fee);
	    			feeMap.put("remark",remark);
	    			feeMap.put("ext1","");
	    			feeMap.put("ext2","");
	    			feeMap.put("ext3","");
	    			feeMap.put("ext4","");
	    			feeMap.put("ext5","");
	    			tradeFeeList.add(feeMap);
	    		}	
	    	}else{
		    	object.put("result", new DatasetList());
		        object.put("respCode", "-9999");
		        object.put("respDesc", "未查到费用明细信息");
		        
		        result.put("object", object);
				result.put("rtnCode", "0");	
				result.put("rtnMsg", "成功");	
		        
		        return result;
	    	}
			
			IData tradeFeeMap = new DataMap();
			IData resultMap = new DataMap();
			IData objectMap = new DataMap();
			IDataset resultList = new DatasetList();
			
			tradeFeeMap.put("tradeFeeList", tradeFeeList);
			resultList.add(tradeFeeMap);
			resultMap.put("result", resultList);
			resultMap.put("respCode", "0");
			resultMap.put("respDesc", "success");
			
			objectMap.put("object", resultMap);
			objectMap.put("rtnCode", "0");
			objectMap.put("rtnMsg", "成功");
			return objectMap;
		}
	    catch (Exception e)
	    {
	    	object.put("result", new DatasetList());
	        object.put("respCode", "-9999");
	        object.put("respDesc", "业务费用明细查询异常！");
	        
	        result.put("object", object);
			result.put("rtnCode", "-1");	
			result.put("rtnMsg", "失败");	
	        
	        return result;
	    }
		
	}
	
	
	/*
	 * 业务商品变更明细查询
	 */
	public static IData C898HQQueryTradeOfferInfo(IData inParamStr) throws Exception{
		IData result = new DataMap();
    	IData object = new DataMap();
		try{
			IData inParam = new DataMap(inParamStr.toString());
			//入参非空判断
			IDataset dataset = new DatasetList();
			IData dataMap1 = new DataMap();
			dataMap1.put("FIELD", "tradeId");
			dataMap1.put("TYPE", "String");
			dataset.add(dataMap1);
			
			IData dataMap2 = new DataMap();
			dataMap2.put("FIELD", "opRegionCode");
			dataMap2.put("TYPE", "String");
			dataset.add(dataMap2);
			
			IData inParamResult = ComFuncUtil.checkInParam(inParam.getData("params"), dataset);
			if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
				return inParamResult;
			}
			
			IData input = new DataMap();
			IDataset offerList = new DatasetList();
			String tradeId = inParam.getData("params").getString("tradeId");
			input.put("TRADE_ID", tradeId);
			
			//查询平台业务
			IDataset platInfo = Dao.qryByCode("TF_B_TRADE_PLATSVC", "SEL_BY_TRADEID", input, Route.getJourDb());;
			if(IDataUtil.isNotEmpty(platInfo)){
				for(int i=0;i<platInfo.size();i++){
					IData tempInfo = new DataMap();
					IData platMap = new DataMap();
					tempInfo = platInfo.getData(i);
					String serviceId = tempInfo.getString("SERVICE_ID");
					platMap.put("offerId", serviceId);
					IDataset serviceInfo = PlatSvcInfoQry.queryPlatSvcByServiceId(serviceId);
	        		IDataset spServiceInfo = UpcCall.qrySpServiceSpInfo(serviceId,"Z");
	        		if (IDataUtil.isNotEmpty(spServiceInfo)){
	        			platMap.put("spCode", spServiceInfo.getData(0).getString("SP_CODE", ""));
	        			platMap.put("bizCode", spServiceInfo.getData(0).getString("BIZ_CODE", ""));
	        		}
	        		else
	        		{
						platMap.put("spCode", "");
						platMap.put("bizCode", "");
	        		}
					if (IDataUtil.isNotEmpty(serviceInfo)){
						platMap.put("offerName", serviceInfo.getData(0).getString("OFFER_NAME",""));
					}
					else{
						platMap.put("offerName", "");
					}
					platMap.put("offerType", "平台业务");
					platMap.put("action", StaticUtil.getStaticValue("MODIFY_TAG", tempInfo.getString("MODIFY_TAG","")));
					platMap.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(tempInfo.getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					platMap.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(tempInfo.getString("END_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					platMap.put("remark", tempInfo.getString("REMARK",""));
					platMap.put("ext1", "");
			    	platMap.put("ext2", "");
			    	platMap.put("ext3", "");
			    	platMap.put("ext4", "");
			    	platMap.put("ext5", "");
					offerList.add(platMap);
				}
			}
			
			//查询产品变化信息
			Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
			IDataset productInfo = bean.qryThProductInfo(input);
			if(IDataUtil.isNotEmpty(productInfo)){
				for(int i=0;i<productInfo.size();i++){
					IData tempInfo = new DataMap();
					IData productMap = new DataMap();
					tempInfo =productInfo.getData(i);
					
					String productId = tempInfo.getString("PRODUCT_ID");
					if(StringUtils.isNotBlank(productId)){
						IDataset offerInfos = UpcCall.queryOfferNameByOfferCodeAndType("P", productId);
						if(IDataUtil.isNotEmpty(offerInfos)){
							productMap.put("PRODUCT_NAME", offerInfos.getData(0).getString("OFFER_NAME"));
						}	
					}
					
					productMap.put("offerId", tempInfo.getString("PRODUCT_ID"));
					productMap.put("offerName", productMap.getString("PRODUCT_NAME",""));
					productMap.put("offerType", "产品");
					productMap.put("spCode", "");
					productMap.put("bizCode", "");
					productMap.put("action",StaticUtil.getStaticValue("MODIFY_TAG", tempInfo.getString("MODIFY_TAG","")) );
					productMap.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(tempInfo.getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					productMap.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(tempInfo.getString("END_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
					productMap.put("remark", tempInfo.getString("REMARK",""));
					productMap.put("ext1", "");
				    productMap.put("ext2", "");
					productMap.put("ext3", "");
					productMap.put("ext4", "");
					productMap.put("ext5", "");
					offerList.add(productMap);
				}
			}
			
			//查询服务信息
			IDataset svcInfo = bean.qryThSvcInfo(input);
			if(IDataUtil.isNotEmpty(svcInfo)){
				for (int i = 0; i < svcInfo.size(); i++) {
					IData tempInfo = svcInfo.getData(i);
					String serviceId = tempInfo.getString("SERVICE_ID", "");
					if(StringUtils.isNotBlank(serviceId)){
						IDataset offerInfos = UpcCall.queryOfferNameByOfferCodeAndType("S", serviceId);
			    		if(IDataUtil.isNotEmpty(offerInfos)){
			    			tempInfo.put("SERVICE_NAME", offerInfos.getData(0).getString("OFFER_NAME"));
			    		}
			    		IData svcMap= new DataMap();
			    		svcMap.put("offerId", tempInfo.getString("SERVICE_ID"));
			    		svcMap.put("offerName", tempInfo.getString("SERVICE_NAME"));
			    		svcMap.put("spCode", "");
				   		svcMap.put("bizCode", "");
			    		svcMap.put("offerType", "服务");
			    		svcMap.put("action",StaticUtil.getStaticValue("MODIFY_TAG", tempInfo.getString("MODIFY_TAG")));
			    		svcMap.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(tempInfo.getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			    		svcMap.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(tempInfo.getString("END_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
			    		svcMap.put("remark", tempInfo.getString("REMARK",""));
			    		svcMap.put("ext1", "");
			    		svcMap.put("ext2", "");
			    		svcMap.put("ext3", "");
			    		svcMap.put("ext4", "");
			    		svcMap.put("ext5", "");
			        	offerList.add(svcMap);
					}
				}
			}
			
			//查询优惠
			IDataset discntInfo = bean.qryThDiscntInfo(input);
			if(IDataUtil.isNotEmpty(discntInfo)){
				for (int i = 0; i < discntInfo.size(); i++) {
					//根据优惠编码查询优惠名称
					IData discnt = discntInfo.getData(i);
					String discnt_code = discnt.getString("DISCNT_CODE","");
					if(StringUtils.isNotBlank(discnt_code)){
						IDataset offerInfos = UpcCall.queryOfferNameByOfferCodeAndType("D", discnt_code);
				   		if(IDataUtil.isNotEmpty(offerInfos)){
				   			discnt.put("DISCNT_NAME", offerInfos.getData(0).getString("OFFER_NAME"));
				   		}
				   		IData discntMap = new DataMap();
				   		discntMap.put("offerId", discnt.getString("DISCNT_CODE"));
				   		discntMap.put("offerName", discnt.getString("DISCNT_NAME"));
				   		discntMap.put("offerType", "优惠");
				   		discntMap.put("spCode", "");
				   		discntMap.put("bizCode", "");
				   		discntMap.put("action",StaticUtil.getStaticValue("MODIFY_TAG", discnt.getString("MODIFY_TAG","-1")) );
				   		discntMap.put("effTime", SysDateMgr.date2String((SysDateMgr.string2Date(discnt.getString("START_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
				   		discntMap.put("expTime", SysDateMgr.date2String((SysDateMgr.string2Date(discnt.getString("END_DATE",""), SysDateMgr.PATTERN_STAND)),SysDateMgr.PATTERN_STAND_SHORT));
				   		discntMap.put("remark", discnt.getString("REMARK",""));
				   		discntMap.put("ext1", "");
				   		discntMap.put("ext2", "");
				   		discntMap.put("ext3", "");
				   		discntMap.put("ext4", "");
				   		discntMap.put("ext5", "");
				       	offerList.add(discntMap);
					}
				}
			}
	        
			if(IDataUtil.isEmpty(offerList)){
		    	object.put("result", new DatasetList());
		        object.put("respCode", "-9999");
		        object.put("respDesc", "未查到业务商品明细变更信息");
		        
		        result.put("object", object);
				result.put("rtnCode", "0");	
				result.put("rtnMsg", "成功");	
		        
		        return result;
			}
			
			IData resultMap = new DataMap();
			IData returnMap = new DataMap();
			IDataset resultList = new DatasetList();
			
			resultMap.put("offerList", offerList);
			resultList.add(resultMap);
			object.put("result", resultList);
			object.put("respCode", "0");
			object.put("respDesc", "success");
			returnMap.put("object", object);
			returnMap.put("rtnCode", "0");
			returnMap.put("rtnMsg", "成功");
			return returnMap;
		}
		catch (Exception e)
	    {
	    	object.put("result", new DatasetList());
	        object.put("respCode", "-9999");
	        object.put("respDesc", "业务商品变更明细查询！");
	        
	        result.put("object", object);
			result.put("rtnCode", "-1");	
			result.put("rtnMsg", "失败");	
	        
	        return result;
	    }
	}
	
	
	
	/*
	 * 台账历史记录查询
	 */
	public static IDataset qryTradeHisInfo(String serialNumber,String tradeTypeCode,String startTime,String endTime,String trade_staff_id) throws Exception{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);
		if(StringUtils.isNotBlank(tradeTypeCode)){
			inparams.put("TRADE_TYPE_CODE", tradeTypeCode);
		}
		if(StringUtils.isNotBlank(trade_staff_id)){
			inparams.put("TRADE_STAFF_ID", trade_staff_id);
		}
		inparams.put("START_TIME", startTime);
		inparams.put("END_TIME", endTime);
		SQLParser parser = new SQLParser(inparams);
		parser.addSQL(" SELECT /*+INDEX(A,IDX_TF_BH_TRADE_SN)*/ TRADE_ID, ACCEPT_MONTH, BATCH_ID, ORDER_ID, PROD_ORDER_ID, BPM_ID, CAMPN_ID, ");
		parser.addSQL(" TRADE_TYPE_CODE, PRIORITY, SUBSCRIBE_TYPE, SUBSCRIBE_STATE, NEXT_DEAL_TAG, ");
		parser.addSQL(" IN_MODE_CODE, CUST_ID, CUST_NAME, USER_ID, ACCT_ID, SERIAL_NUMBER, NET_TYPE_CODE, ");
		parser.addSQL(" EPARCHY_CODE, CITY_CODE, PRODUCT_ID, BRAND_CODE, CUST_ID_B, USER_ID_B, ACCT_ID_B, ");
		parser.addSQL(" SERIAL_NUMBER_B, CUST_CONTACT_ID, SERV_REQ_ID, INTF_ID, ACCEPT_DATE, TRADE_STAFF_ID, ");
		parser.addSQL(" TRADE_DEPART_ID, TRADE_CITY_CODE, TRADE_EPARCHY_CODE, TERM_IP, OPER_FEE, FOREGIFT, ");
		parser.addSQL(" ADVANCE_PAY, INVOICE_NO, FEE_STATE, FEE_TIME, FEE_STAFF_ID, PROCESS_TAG_SET, ");
		parser.addSQL(" OLCOM_TAG, FINISH_DATE, EXEC_TIME, EXEC_ACTION, EXEC_RESULT, EXEC_DESC, CANCEL_TAG, ");
		parser.addSQL(" CANCEL_DATE, CANCEL_STAFF_ID, CANCEL_DEPART_ID, CANCEL_CITY_CODE, CANCEL_EPARCHY_CODE, ");
		parser.addSQL(" UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2, ");
		parser.addSQL(" RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
		parser.addSQL(" RSRV_STR10, IS_NEED_HUMANCHECK, PF_TYPE, FREE_RESOURCE_TAG, PF_WAIT "); 
		parser.addSQL(" FROM TF_BH_TRADE A ");
		parser.addSQL(" WHERE A.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" AND A.TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");
		parser.addSQL(" AND A.TRADE_TYPE_CODE<>2101 ");
		parser.addSQL(" AND A.TRADE_STAFF_ID = :TRADE_STAFF_ID ");
		parser.addSQL(" AND A.ACCEPT_DATE > TO_DATE(:START_TIME, 'YYYY-MM-DD HH24:MI:SS') ");
		parser.addSQL(" AND A.ACCEPT_DATE < TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS') ");
		String sYear = "";
        String sMonth = "";
        String eYear = "";
        String eMonth = "";
        if (!"".equals(startTime))
        {
            startTime = SysDateMgr.chgFormat(startTime,"yyyyMMddHHmmss","yyyy-MM-dd HH:mm:ss");
        	sMonth = SysDateMgr.getTheMonth(startTime);
            sYear = SysDateMgr.date2String(SysDateMgr.string2Date(startTime, "yyyy-MM-dd"), "yyyy");
        }
        if (!"".equals(endTime))
        {
        	endTime = SysDateMgr.chgFormat(endTime,"yyyyMMddHHmmss","yyyy-MM-dd HH:mm:ss");
        	eMonth = SysDateMgr.getTheMonth(endTime);
        	eYear = SysDateMgr.date2String(SysDateMgr.string2Date(endTime, "yyyy-MM-dd"), "yyyy");
        }
        if (sYear.equals(eYear) && !sYear.equals("") && !eYear.equals(""))
        {
            parser.addSQL(" AND A.ACCEPT_MONTH >=  " + sMonth + " ");
            parser.addSQL(" AND A.ACCEPT_MONTH <=  " + eMonth + " ");
        }
		return Dao.qryByParse(parser,Route.getJourDb());
	}

}
