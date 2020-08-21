package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform.AbilityPlatCheckRelativeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryListInfo;

public class AbilityPlatCheckIntf {
	/**
	 * 6.2 入网资格校验
	 * 
	 * @param pd
	 * @param td
	 * @return
	 * @throws Exception
	 */
	public IData checkComeInNet(IData input) throws Exception {
		// 该bean中 有判断 黑名单, 和 用户数量的限制....
		IData info = this.checkCustomerIntoNet(input);
		String instseq = input.getString("OPRNUMB", "");
		IData resultMap = new DataMap();
		if (info != null) {
			resultMap.put("OPRNUMB", instseq);
			resultMap.put("BIZORDERRESULT", info.getString("CHECK_TAG"));
			resultMap.put("RESERVE", info.getString("CHECK_RESUTT"));
			
			if(!info.getString("CHECK_TAG").equals("0000")){
				
			resultMap.put("X_RSPDESC", info.getString("CHECK_RESUTT"));
			resultMap.put("X_RESULTINFO", info.getString("CHECK_RESUTT"));
			resultMap.put("X_RSPTYPE", "2");
			resultMap.put("X_RSPCODE", "2998");
			resultMap.put("X_RESULTCODE", info.getString("CHECK_TAG"));
			}
		}
		// 封装数据
		IData dataMap = new DataMap();
		dataMap.put("OPRNUMB", instseq); // 流水号
		dataMap.put("IDTYPE", input.getString("IDTYPE")); // 证件类型
		dataMap.put("IDVALUE", input.getString("IDVALUE")); // 证件号码
		dataMap.put("OPRT",DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")); // 处理时间
		// dataMap.put("OPRRESULT", resultMap);
		dataMap.putAll(resultMap);
		return dataMap;
	}

	/**
	 * 入网资格检查
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkCustomerIntoNet(IData input) throws Exception {
		IData rDualInfo;
		// 读取配置的客户开户限制数
		String strTagNumber = "";
		// 全身配置一个统一限制数
		rDualInfo = AbilityPlatCheckRelativeQry.getTagInfo();
		strTagNumber = rDualInfo.getString("TAG_NUMBER");
		IData data = new DataMap();
		data.put("CHECK_TAG", "0");
		String pspt_type_code = input.getString("IDTYPE");
		String pspt_id = input.getString("IDVALUE");
		boolean codebool = checkPostCard(pspt_type_code);
		// 检查证件类型是否合法
		if (!codebool) {
			data.put("CHECK_TAG", "3A11");
			data.put("CHECK_RESUTT", "证件类型不正确");// 证件类型不对
		
			return data;
		}
		// 检查证件号码是否合法
		IDataset custList = getCustInfoByPspt(pspt_type_code, pspt_id); // 该证件下的用户资料
		if (null == custList || custList.size() == 0) {
			data.put("CHECK_TAG", "3A11");
			data.put("CHECK_RESUTT", "证件号码不正确");// 证件号码不对
			
			return data;
		}
		// 检查黑名单
		//UCustBlackInfoQry.isBlackCust(pspt_type_code_ab, pspt_id);
		boolean checkBlackCust = UCustBlackInfoQry.isBlackCust(custList.getData(0).getString("PSPT_TYPE_CODE"),
				pspt_id);
		if (checkBlackCust) {
			data.put("CHECK_TAG", "3000");
			data.put("CHECK_RESUTT", "该用户有黑名单信息");// 黑名单
			
			
			return data;
		}

		// 检查是否超过了数量
		IDataset custInfo = CustPersonInfoQry.qryPerInfoByPsptId(
				custList.getData(0).getString("PSPT_TYPE_CODE"), pspt_id);

		if (!custInfo.isEmpty()) {
			if (!"0".equals(strTagNumber)) {
				if (custInfo.size() >= Integer.parseInt(strTagNumber)) {
					data.put("CHECK_TAG", "3006"); // 订购限制数
					data.put("CHECK_RESUTT", "同一证件号码下面客户开户数不能超过" + strTagNumber
							+ "个");
			
					return data;
				}
			}
		}
		// 存在欠费号码
		// 根据客户标记获取用户是否有欠费判断
		if (custList != null && custList.size() > 0) {
			IData oweFeeData = this.getOweFeeUserById(custList);
			if (!oweFeeData.isEmpty()) {
				data.put("CHECK_TAG", "3003"); // 订购限制数
				data.put("CHECK_RESUTT", "该客户有号码【"
						+ oweFeeData.getString("OWE_FEE_SERIAL_NUMBER")
						+ "】有往月欠费【" + oweFeeData.getString("OWE_FEE")
						+ "】元，不能再次使用该证件办理开户业务！");
				return data;
			}
		}
		// 验证正确
		if ("0".equals(data.getString("CHECK_TAG"))) {
			data.put("CHECK_TAG", "0000");
			data.put("CHECK_RESUTT", "校验通过客户");
			return data;
		}
		return data;
	}

	/*
	 * 00 居民身份证 01 临时居民身份证 02 户口簿（仅用于未成年客户） 03 军人身份证件 04 武装警察身份证件 05 港澳居民往来内地通行证
	 * 06 台湾居民来往大陆通行证 07 护照 99 其他证件
	 */
	public boolean checkPostCard(String param) throws Exception {
		IDataset psptSet = AbilityPlatCheckRelativeQry.getAbilityPsptTypeCode();

		for (int i = 0; i < psptSet.size(); i++) {
			if (psptSet.getData(i).getString("DATA_ID").equals(param)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据证件类型和证件号码获取客户资料
	 * 
	 * @author
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset getCustInfoByPspt(String psptTypeCode, String psptId)
			throws Exception {

		IData custData = new DataMap();
		IDataset custList = new DatasetList();// 先给初始值，便于后面判断
		IDataset codes = this.getRealyPsptTypeCode(psptTypeCode);
		if (codes != null) {
			if (codes.size() > 1) {
				StringBuilder sb = new StringBuilder(20);
				for (int j = 0; j < codes.size(); j++) {
					if (j == codes.size() - 1) {
						sb.append("").append(
								codes.getData(j)
										.getString("PSPT_TYPE_CODE", ""))
								.append("");
					} else {
						sb.append("").append(
								codes.getData(j)
										.getString("PSPT_TYPE_CODE", ""))
								.append(",");
					}
				}
				psptTypeCode = sb.toString(); // 身份证的模糊查询
			} else {
				psptTypeCode = codes.getData(0).getString("PSPT_TYPE_CODE");
			}
		}
		// custList =
		// AbilityPlatCheckRelativeQry.getCustInfoByPsptPnet(custData);
		custList = QueryListInfo.getCustInfoByPsptCustType("0", psptTypeCode,
				psptId);
		return custList;
	}

	/**
	 * 根据中间表 得到真实的证件编码
	 * 
	 * @param pd
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset getRealyPsptTypeCode(String param) throws Exception {
		IData pnetTypeCode = new DataMap();
		pnetTypeCode.put("PNET_TYPE_CODE", param);
		IDataset dataSet = AbilityPlatCheckRelativeQry
				.getRealyPsptTypeCodeByPnet(pnetTypeCode);
		return dataSet;
	}

	/**
	 * 查询欠费信息
	 * 
	 * @return
	 * @throws Exception
	 */

	public IData getOweFeeUserById(IDataset custList) throws Exception {
		IData oweFeeData = new DataMap();
		IData custData = null;
		IData userData = null;
		IDataset userList = null;
		IData owefeeData = null;
		double dFee = 0;// 往月欠费
		int iOnlineNum = 0;// 当前证件下在网用户数
		boolean isExistsOweFeeFlag = false;// 存在欠费用户标记
		String oweFeeSerialNumber = "";// 欠费号码
		for (int i = 0; i < custList.size(); i++) {
			custData = custList.getData(i);
			String cust_id = custData.getString("CUST_ID");
			IData oweCustData = new DataMap();
			oweCustData.put("CUST_ID", cust_id);
			userList = QueryListInfo.getUserInfoByCustId(cust_id, null);
			if (userList != null && userList.size() > 0) {
				iOnlineNum += userList.size();// 统计在网用户数
				// 未找到欠费用户时，才查欠费信息，找到一条则不查询，提示第一条欠费信息即可
				if (!isExistsOweFeeFlag) {
					// 根据用户标识查询欠费信息
					for (int j = 0; j < userList.size(); j++) {
						userData = userList.getData(j);
						String userId = userData.getString("USER_ID");

						IData iparam = new DataMap();

						iparam.put("EPARCHY_CODE", userData.getString(
								"EPARCHY_CODE", ""));
						iparam.put("USER_ID", userId);
						iparam.put("ID", userId);
						iparam.put("ID_TYPE", "1");
						iparam.put(Route.ROUTE_EPARCHY_CODE, userData.getString(
								Route.ROUTE_EPARCHY_CODE, ""));

						// 调用账户流程查询欠费信息
						// IData doweFee = UserInfoQry.getOweFeeCTT(iparam);
						IData doweFee = AcctCall.getOweFeeByUserId(userId);
						dFee = Double.parseDouble(doweFee
								.getString("LAST_OWE_FEE"));
						if (dFee > 0) {// 找到有往月欠费用户则退出循环，提示欠费信息
							isExistsOweFeeFlag = true;
							oweFeeSerialNumber = userData
									.getString("SERIAL_NUMBER");
							break;
						}
					}
				}
			}
		}
		// 存在欠费用户时，返回欠费号码，欠费金额，在网用户数
		if (isExistsOweFeeFlag) {
			String strFee = String.valueOf(((float) dFee) / 100);
			oweFeeData.put("OWE_FEE_SERIAL_NUMBER", oweFeeSerialNumber);
			oweFeeData.put("OWE_FEE", strFee);
			oweFeeData.put("ONLINE_NUM", iOnlineNum);
			oweFeeData.put("IS_EXISTS_OWE_FEE_FLAG", true);
		}
		return oweFeeData;

	}

	/**
	 * 6.3 业务办理资格校验接口
	 * 
	 * @param pd
	 * @param td
	 * @return
	 * @throws Exception
	 */
	public IData checkServiceRule(IData input) throws Exception {
		// 该bean中 判断用户状态是否正常...
		IData info = this.checkCustomerDoService(input);
		String instseq = input.getString("OPRNUMB", ""); // 业务流水号
		IData resultMap = new DataMap();
		if (info != null) {
			resultMap.put("OPRNUMB", instseq);
			resultMap.put("NUMTYPE", input.getString("NUMTYPE", ""));
			resultMap.put("MOBILENO", input.getString("MOBILENO", ""));
			resultMap.put("OPRTIME", DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")); // 处理时间
			resultMap.put("BIZORDERRESULT", info.getString("BIZORDERRESULT",
					"3004"));
			resultMap.put("RESERVE", info.getString("RESERVE", ""));
			
			if(!"0000".equals(info.getString("BIZORDERRESULT"))){
				
				resultMap.put("X_RSPDESC", info.getString("RESERVE"));
				resultMap.put("X_RESULTINFO", info.getString("RESERVE"));
				resultMap.put("X_RSPTYPE", "2");
				resultMap.put("X_RSPCODE", "2998");
				resultMap.put("X_RESULTCODE", info.getString("BIZORDERRESULT"));
				}
			
		}
		return resultMap;
	}

	/**
	 * 入网资格检查
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkCustomerDoService(IData input) throws Exception {
		
		// 验证黑名单
		// 判断产品用户状态是否正常，非正常状态不能添加成员
		String number = input.getString("MOBILENO", ""); // 服务号码
		
		//校验未完工单
		IDataset tradeInfo = TradeInfoQry.CheckIsExistNotBBSSFinishedTrade(number);
        if (IDataUtil.isNotEmpty(tradeInfo))
        {
            int count = tradeInfo.getData(0).getInt("ROW_COUNT");
            if (count > 0)
            {
            	IData result = new  DataMap();
            	result.put("OPRNUMB", input.getString("OPRNUMB",""));
            	result.put("NUMTYPE", input.getString("NUMTYPE", ""));
            	result.put("MOBILENO", number);
            	result.put("OPRTIME", DateFormatUtils.format(new  Date(), "yyyyMMddHHmmss")); //处理时间
            	result.put("BIZORDERRESULT", "3012");
            	result.put("RESERVE", "用户有未完工单");
                return result;
            }
        }
		
		input.put("SERIAL_NUMBER", number);
		AbilityPlatCheckBean  checkBean=BeanManager.createBean(AbilityPlatCheckBean.class);
		IData retnData = checkBean.checkCustomerDoService(input);
		
		IData  resultMap=new  DataMap();
		if(retnData!=null){
				resultMap.put("OPRNUMB", input.getString("OPRNUMB",""));
				resultMap.put("NUMTYPE", input.getString("NUMTYPE", ""));
				resultMap.put("MOBILENO", number);
				resultMap.put("OPRTIME", DateFormatUtils.format(new  Date(), "yyyyMMddHHmmss")); //处理时间
				resultMap.put("BIZORDERRESULT", retnData.getString("BIZORDERRESULT"));
				resultMap.put("RESERVE", retnData.getString("RESERVE",""));
		}
		return resultMap;
	}

	

	/**
	 * 合约计划的检查
	 * 
	 * @param pd
	 * @param number
	 * @param numType
	 * @return
	 * @throws Exception
	 */
	public boolean checkContact(String number, String numType) throws Exception {

		boolean bool = false;
		if ("1".equals(numType)) {
			// 由手机号码得到用户信息
			IData iparam = new DataMap();
		    SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class); //预约的检查
		    IDataset  bookSet=saleActiveBean.checkSaleBook(number);
		    IData  bookData=new DataMap();
		    
		    if(IDataUtil.isNotEmpty(bookSet)){
		    	bookData=bookSet.getData(0);
		    }
		    if(StringUtils.isEmpty(bookData.getString("AUTH_BOOK_SALE",""))){
		    	if("1".equals(bookData.getString("AUTH_BOOK_SALE",""))){
		    		bool=false;
		    		return bool;
		    	}
		    }
			
			iparam.put("SERIAL_NUMBER", number);
			iparam.put("REMOVE_TAG", "0");
			IDataset dataSet = AbilityPlatCheckRelativeQry.getUserInfoBySn(iparam);
			for (int i = 0; i < dataSet.size(); i++) {
				String userId = dataSet.getData(i).getString("USER_ID");
				IData contractInfo = AbilityPlatCheckRelativeQry
						.queryContractInfo(userId);
				if (contractInfo != null && !contractInfo.isEmpty()) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}

}
