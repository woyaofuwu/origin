
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CmOnlineUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.util.RealNameMsDesPlus;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.Map;


public class QueryOtherInfoBeanCopy extends CSBizBean
{
	private static transient Logger logger = Logger.getLogger(QueryOtherInfoBean.class);
	/**
	 * 
	 * @Description：查询用户状态
	 * @param:@param input 
	 * @return IData
	 * 0000：操作成功
	 * 1001：操作失败
	 * 2999：其他业务异常，提示的返回消息在客户端上展示
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-22下午04:32:59
	 */
	public IData queryOtherInfo(IData input) {
		logger.debug("---------------"+input);
		
		IData result = new DataMap();
		IData resInfo = new DataMap();
		
		
		try {
			pottingMap(input);
			String serialNumber = IDataUtil.chkParam(input.getData("reqInfo"), "billID");
	
	    	pottingReturnResult(result,input.getData("reqInfo").getString("transactionID"),"0000","操作成功！");
    	
			/*
			 * 封装tf_f_user信息
			 */
			pottingUser(serialNumber,resInfo,result);
			/*
			 * 封装customer信息
			 */
			if("0000".equals(result.get("returnCode"))){
				pottingCustomer(serialNumber,resInfo,result);
			}
			/*
			 * 封装Product信息
			 */
			if("0000".equals(result.get("returnCode"))){
				pottingProduct(serialNumber,resInfo,result);
			}
			/*
			 * 封装Credit信息
			 */
			if("0000".equals(result.get("returnCode"))){
				pottingCredit(serialNumber,resInfo,result);
			}
			/*
			 * 封装Account信息
			 */
			if("0000".equals(result.get("returnCode"))){
				pottingAccount(serialNumber,resInfo,result);
			}
			result.put("resInfo", resInfo);
		} catch (Exception e) {
			pottingReturnResult(result,input.getData("reqInfo").getString("transactionID"),"1001",e.getMessage());
			logger.error(e.getMessage());
			
		}
		return result;
	}
	
	
	/**
	 * @Description：TODO
	 * @param:@param input
	 * @return void
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-24下午07:59:28
	 */
	private void pottingMap(IData input) {
		Map xmListList = (Map) input.get("reqInfo");
		JSONObject json = JSONObject.fromObject(xmListList);
	    IData data = DataMap.fromJSONObject(json);
		input.remove("reqInfo");
		input.put("reqInfo", data);
	}


	/**
	 * @Description：TODO 账户信息
	 * @param:@param serialNumber
	 * @param:@param resInfo
	 * @param:@param result
	 * @return void
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-22下午05:37:14
	 */
	private void pottingAccount(String serialNumber, IData resInfo, IData result) {
		 resInfo.put("oweFee", "");
		 resInfo.put("balance", "");
		 resInfo.put("monthConsume","");
		 
	}
	/**
	 * @Description：封装用户星级信息
	 * @param:serialNumber
	 * @param: resInfo
	 * @param: result
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-22下午04:58:19
	 */
	private void pottingCredit(String serialNumber, IData resInfo, IData result) throws Exception {
		 IData userCreditInfo = CreditCall.queryUserCreditInfos(resInfo.getString("userId"));
		 if (IDataUtil.isEmpty(userCreditInfo))
         {
        	 result.put("returnCode", "2999");
             result.put("returnMessage", "查询用户星级信息时出错!");
             return;
         }
		 
		 resInfo.put("creditLevel", userCreditInfo.getString("CREDIT_CLASS"));
	}
	/**
	 * @Description：封装用户产品信息
	 * @param: serialNumber
	 * @param: resInfo
	 * @param: result
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-22下午04:57:40
	 */
	private void pottingProduct(String serialNumber, IData resInfo, IData result) throws Exception {
		 IData userMainProductInfo = UcaInfoQry.qryMainProdInfoByUserId(resInfo.getString("userId"));
         if (IDataUtil.isEmpty(userMainProductInfo))
         {
        	 result.put("returnCode", "2999");
             result.put("returnMessage", "查询用户主产品信息表时出错!");
             return;
         }
         resInfo.put("prodprcName", userMainProductInfo.getString("PRODUCT_NAME"));
         resInfo.put("prodprcCode", userMainProductInfo.getString("PRODUCT_ID"));
         resInfo.put("brandname", userMainProductInfo.getString("BRAND_NAME"));
	        
	        
	}
	/**
	 * 
	 * @Description：封装customer信息
	 * @param:serialNumber
	 * @param:resInfo
	 * @param:result
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-22下午04:53:36
	 */
	private void pottingCustomer(String serialNumber, IData resInfo,
			IData result) throws Exception {
		IData custInfo = UcaInfoQry.qryCustInfoByCustId(resInfo.getString("custId"));
		if (IDataUtil.isEmpty(custInfo))
         {
        	 result.put("returnCode", "2999");
             result.put("returnMessage", "客户信息不存在!");
             return;
         }
		 resInfo.put("custCertNo", custInfo.getString("PSPT_ID"));
		 resInfo.put("custName", custInfo.getString("CUST_NAME"));
		 resInfo.put("custLevel", "C1");
		 resInfo.put("custCertType", custInfo.getString("PSPT_TYPE_CODE"));
	}
	/**
	 * 
	 * 
	 * @Description：根据手机号码封装用户user表信息
	 * @param:serialNumber
	 * @param:resInfo
	 * @param result 
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-22下午04:50:00
	 */
	private void pottingUser(String serialNumber, IData resInfo, IData result) throws Exception {
		 logger.debug("查询用户begin");
		 IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		 logger.debug("查询用户end");
         if (IDataUtil.isEmpty(userInfo))
         {
        	 result.put("returnCode", "2999");
             result.put("returnMessage", "手机号码不存在!");
             return;
         }
         resInfo.put("lastOpenTime", userInfo.getString("OPEN_DATE"));
         //TODO 用户状态编码
         resInfo.put("stoptypeCode", tansferStateCode(userInfo.getString("USER_STATE_CODESET")));
         resInfo.put("custId", userInfo.getString("CUST_ID"));
         resInfo.put("userId", userInfo.getString("USER_ID"));
         resInfo.put("beLongCityCode", userInfo.getString("CITY_CODE"));
         logger.debug("查询用户归属地begin");
         String area = userInfo.getString("CITY_NAME");
         logger.debug("查询用户归属地end");
         resInfo.put("beLongCity",area);
         resInfo.put("ownerAddress","海南"+area);
         
         
	}


	/**
	 * @Description：TODO
	 * @param:@param string
	 * @param:@return
	 * @return Object
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-12-13上午09:16:19
	 */
	private String tansferStateCode(String code) {
		String result = "9";
		if("0".equals(code)){
			result = "1";
		}
		if("5".equals(code)){
			result = "2";
		}
		if("12347".contains(code)){
			result = "3";
		}
		return result;
	}


	/**
	 * @Description：查询可用号码接口
	 * @param:@param input
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-23上午11:14:06
	 */
	public IData queryAbleResNo(IData input){
		IData result = new DataMap();
		try {
			pottingMap(input);
			pottingReturnResult(result,input.getData("reqInfo").getString("transactionID"),"0000","操作成功！");
			IDataset phoneList = new DatasetList();
			input.put("X_GETMODE", "7");//X_GETMODE=7，查询使用号码信息，多行返回
			input.put("RES_TRADE_CODE", "IGetMphoneCodeInfo");
			input.put("X_CHOICE_TAG", "0");//0:查询普通号码1：查询吉祥号码
			input.put("CITY_CODE","HNHK");//因平台传来的总是 0898，导致查询不出数据，所以暂先默认HNHK
			input.put("PARA_VALUE9","7");//固定7号号码池
			input.put("RSRV_NUM1","150");//查询号码数量
			phoneList = ResCall.getNetWorkPhone(input);
			if(phoneList == null || phoneList.size() == 0 || "0".equals(phoneList.get(0, "X_RECORDNUM"))) 
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取号码资源信息失败！");
			}
			potingResultNo(result,phoneList);
		} catch (Exception e) {
			pottingReturnResult(result,input.getString("transactionID"),"1001",e.getMessage());
			logger.error(e.getMessage());
		}
		return result;
	}


	/**
	 * @Description：TODO
	 * @param:@param resInfo
	 * @param:@param phoneList
	 * @return void
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-23上午11:30:23
	 */
	private void potingResultNo(IData result, IDataset phoneList) {
		IDataset resultList = new DatasetList();
		for (int i = 0; i < phoneList.size(); i++){
			IData ele = phoneList.getData(i);
			IData numberInfo = new DataMap();
			numberInfo.put("svcNum", ele.getString("SERIAL_NUMBER"));//号码
			numberInfo.put("numType","");//TODO numType号码类型
			numberInfo.put("numTypeName","");//TODO 类型名称
			resultList.add(numberInfo);
		}	
		result.put("resInfo", resultList);
	}


	/**
	 * @Description：号码预占
	 * @param:@param input
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-23下午03:07:26
	 */
	public IData preemptionResNo(IData input)  {
		IData result = new DataMap();
		try {
			logger.info("调用本地接口开始");
			pottingMap(input);
			IData data = input.getData("reqInfo");

//			String pubKey = BizEnv.getEnvString("zhongyizaixian.http.pubkey");
//			if(StringUtils.isBlank(pubKey)){
//				result.put("returnCode", "2999");
//				result.put("returnMessage", "找不到对应的公钥信息");
//				result.put("isSuc", "1");
//				return result;
//			}
//			RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

			String targetCode = input.getString("targetCode");
			IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
			if (IDataUtil.isEmpty(pubKeySet)) {
				result.put("returnCode", "2999");
				result.put("returnMessage", "找不到对应的公钥信息");
				result.put("isSuc", "1");
				return result;
			}
			String pubKey = pubKeySet.getData(0).getString("KEY");
			RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

			//对加密数据进行解密
			String preSerialNumber=plus.decrypt(IDataUtil.chkParam(data, "svcNum"));
			String psptId = plus.decrypt(IDataUtil.chkParam(data, "custCertNo"));
			
			logger.info("解密后的手机号码："+preSerialNumber);
			logger.info("解密后的身份证号码："+psptId);
			pottingReturnResult(result,input.getString("transactionID"),"0000","操作成功！");
			result.put("isSuc", "0");
			
			IData inparam = new DataMap();	
			inparam.put("OPR_NUMB", input.getString("transactionID"));//操作的流水号
			inparam.put("CHANNEL_ID", "");//渠道标识
			inparam.put("RES_NO", preSerialNumber );//调资源接口需传预占号码
			inparam.put("RES_TRADE_CODE", "IRes_NetSel_MphoneCode");//普通网上选号 李全修改
			inparam.put("OCCUPY_TYPE_CODE", "1");//选占类型,1：网上选占
			inparam.put("RES_TYPE_CODE", "0");//0-号码
			inparam.put("USER_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));
			inparam.put("ROUTE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));		
			inparam.put("PSPT_ID", psptId);//选占证件号码
			inparam.put("PSPT_TYPE","");//选占证件类型，非必传
			inparam.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE")); // 受理地州
			inparam.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE")); // 受理业务区
			inparam.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID")); // 受理部门
			inparam.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID")); // 受理员工
			inparam.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", "0")); // 接入渠道
			// 网上选号，选占时间为三天  REQ201806040060 关于集中交付号码预占接口24小时改为72小时的开发需求  -BAOZM
			inparam.put("X_CHOICE_TAG", "1"); 
			logger.info("调用号码预占接口开始");
			IDataset res = ResCall.resTempOccupyByNetSel(inparam);
			logger.info("调用号码预占接口结束"+res.toString());
			if(res==null || res.size()<1){
				result.put("returnCode", "2999");
				result.put("returnMessage", "调用资源接口异常！");
				result.put("isSuc", "1");
				return result;
			}
			IData resResult = res.getData(0);
			if(!"0".equals(resResult.getString("X_RESULTCODE"))){
				result.put("returnCode", "2999");
				result.put("returnMessage", resResult.getString("X_RESULTINFO"));
				result.put("isSuc", "1");
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			pottingReturnResult(result,input.getData("reqInfo").getString("transactionID"),"1001",e.getMessage());
			logger.info(e.getMessage());
		}
		
		return result;
	}


	/**
	 * @Description：订单预校验
	 * @param:@param input
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-11-23下午04:24:29
	 */
	public IData checkOrderPre(IData input)  {
		IData result = new DataMap();
		IData resInfo = new DataMap();
		try {
			pottingMap(input);
			IData data = input.getData("reqInfo");
			pottingReturnResult(result,data.getString("transactionID"),"0000","操作成功！");
		
			String serialNumber = IDataUtil.chkParam(data, "svcNum");
			String productId = IDataUtil.chkParam(data, "productCode");
			input.put("SERIAL_NUMBER", serialNumber);
	        input.put("ELEMENT_ID", productId);// 服务ID或优惠ID或产品ID
	        input.put("ELEMENT_TYPE_CODE","P");// S-服务；D-优惠；P-产品
	        input.put("MODIFY_TAG", "2");// 0-新增；1-删除；2-修改属性
	        input.put("BOOKING_TAG", "0");
	        input.put("IN_MODE_CODE", "0");
			input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
			IDataset set=CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", input);
			IData retnData = new DataMap();
			retnData = (set != null && set.size()>0)?set.getData(0):new DataMap();
			if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")))
			{
				 if (logger.isDebugEnabled())
			     {
					 logger.debug(set.get(0).toString());
			     }
				 resInfo.put("isSuc", "0");
				 resInfo.put("reason", "");
			}else {
				result.put("returnCode", "2999");
				result.put("returnMessage", "操作异常！");
				resInfo.put("isSuc", "1");
				resInfo.put("reason", "操作异常！");
			}
			result.put("resInfo", resInfo);
		} catch (Exception e) {
			pottingReturnResult(result,input.getData("reqInfo").getString("transactionID"),"1001",e.getMessage());
			logger.error(e.getMessage());
		}
		return result;
	}
	
	public IData checkOrderPre1(IData input) throws Exception  {
		IData result = new DataMap();
		IData resInfo = new DataMap();
		pottingMap(input);
		IData data = input.getData("reqInfo");
		pottingReturnResult(result,data.getString("transactionID"),"0000","操作成功！");

//		String pubKey = BizEnv.getEnvString("zhongyizaixian.http.pubkey");
//		if(StringUtils.isBlank(pubKey)){
//			result.put("returnCode", "2999");
//			result.put("returnMessage", "找不到对应的公钥信息");
//			result.put("isSuc", "1");
//			return result;
//		}
//		RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);
		String targetCode = input.getString("targetCode");
		IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            result.put("returnCode", "2999");
            result.put("returnMessage", "找不到对应的公钥信息");
            result.put("isSuc", "1");
            return result;
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

		//对加密数据进行解密
		String psptId = plus.decrypt(IDataUtil.chkParam(data, "custCertNo"));
		
		boolean isBlack = UCustBlackInfoQry.isBlackCust(psptId);
		if (isBlack)
		{
			 resInfo.put("isSuc", "0");
			 resInfo.put("reason", "黑名单用户");
		}
		else
		{
			 resInfo.put("isSuc", "1");
			 resInfo.put("reason", "");
		}
		result.put("resInfo", resInfo);
		return result;
	}
	
	private void pottingReturnResult(IData result,String transactionID,String code,String info){
		//将流水号原样返回
    	result.put("transactionID", transactionID);
		result.put("returnCode", code);
		result.put("returnMessage", info);
	}


	/**
	 * @Description：一证多号查询
	 * @param:@param input
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-12-11下午03:23:40
	 */
	public IData queryMoreNum(IData input) throws Exception {
		IData result = new DataMap();
		pottingMap(input);
		IData data = input.getData("reqInfo");
		pottingReturnResult(result,data.getString("transactionID"),"0000","操作成功！");
		String targetCode = input.getString("targetCode");
		String custName = decrypt(IDataUtil.chkParam(data, "custCertName"),result, targetCode);
		String psptId = decrypt(IDataUtil.chkParam(data, "custCertNo"),result, targetCode);
		
		// 调用全网证件号码查验接口
		IData param = new DataMap();
		param.put("CUSTOMER_NAME", custName);
		param.put("IDCARD_TYPE", "0");//默认证件类型为身份证
		param.put("IDCARD_NUM", psptId);
		param.put("CHANNEL_ID", "0000");

		// 调用全网证件号码查验接口
		NationalOpenLimitBean bean = BeanManager.createBean(NationalOpenLimitBean.class);
		IDataset callResult = new DatasetList();
		try
		{
			callResult = bean.idCheckNoPermi(param);
//			callResult = getTestData(0);
		} catch (Exception e)
		{
			logger.error("idCheck-error"+e.getMessage());
			result.put("returnCode", "2999");
			result.put("returnMessage", "校验【全网一证多号】出现异常，请联系系统管理员！");
			
			return result;
		}
		logger.info("idCheck-result:"+callResult.toString());
		if (IDataUtil.isNotEmpty(callResult))
		{
			if ("0".equals(callResult.getData(0).getString("X_RESULTCODE")))
			{
				int openNum = callResult.getData(0).getInt("TOTAL", 0);
				int untrustresult = callResult.getData(0).getInt("UN_TRUST_RESULT", 0);
				result.put("phoneCount", openNum);
				if (openNum > 0)
				{
						if (untrustresult > 0)
						{
							result.put("returnCode", "23043");
							result.put("returnMessage", "开户人有不良信息，不满足开户条件，禁止开户");
							result.put("verifyResult", "1");
							result.put("resInfo", new DataMap());
							return result;
						}
						IData param1 = new DataMap();
						param1.put("PSPT_TYPE_CODE", "0");
						param1.put("PSPT_ID",psptId);
				        param1.put("CHANNEL_ID", "0000");
				        IDataset returnDataset =  bean.queryCustNumber(param1);//getTestData(1);
				        logger.info("queryCustNumber-result:"+returnDataset.toString());
				        if(IDataUtil.isNotEmpty(returnDataset))
				        {
				        	IDataset resDataset = new DatasetList();
				 	
				        	for(int i = 0; i < returnDataset.size(); i++)
				        	{
				        		IData resinfo = new DataMap();
				        		//REQ201905230028 关于一证五号优化需求 wangsc10-在线公司微信实名认证，通过我公司系统调用一证五号平台查询是否符合一证五号，目前系统返回号码清单，由于业务规则无需返回清单，请进行优化，返回已开号码个数即可
				        		//resinfo.put("svcNum", returnDataset.getData(i).getString("IDV"));
				        		//resinfo.put("custName", encrypt(returnDataset.getData(i).getString("CUSTOMER_NAME"), result, targetCode));
				        		resDataset.add(resinfo);
				        	}
				        	result.put("resInfo", resDataset);
				        }
				        return result;
					
				}else{
					result.put("returnCode", "1005");
					result.put("returnMessage", "身份证号码下没有电话号码!");
					result.put("verifyResult", "1");
					result.put("resInfo", new DataMap());
					return result;
				}
			} else
			{
				if ("2998".equals(callResult.getData(0).getString("X_RESULTCODE")))
				{
					if ("ns1:23039".equals(callResult.getData(0).getString(
							"X_RSPCODE", ""))
							|| "23039".equals(callResult.getData(0)
									.getString("X_RSPCODE", ""))) {
						result.put("verifyResult", "2");
						return result;
					}
				}else
				{
					logger.error("idCheck-error"+callResult.toString());
					// 调用接口出现异常
					result.put("returnCode", "2999");
					result.put("returnMessage", "校验【全网一证多号】出现异常，请联系系统管理员！");
					return result;
				}
			}
		}
			
		return result;
		
		
	}
	/**
	 * @Description：构造假数据跳过调用boss
	 * @param:@return
	 * @return IDataset
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-12-13上午11:03:04
	 */
	private IDataset getTestData(int flag) {
		IDataset resultDataset = new DatasetList();
		IData data = new DataMap();
		if(flag==0){
			data.put("X_RESULTCODE", "0");
			data.put("TOTAL", "1");
			resultDataset.add(data);
		}else{
			data.put("IDV", "13912345678");
			data.put("CUSTOMER_NAME", "小红儿");
			resultDataset.add(data);
		}
		
		return resultDataset;
	}


	/**
	 * 
	 * @Description：解密数据
	 * @param:@param encryptString 密文
	 * @param:@return
	 * @return String 解密后的数据
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-12-11下午03:32:55
	 */
	private String decrypt(String encryptString,IData result, String targetCode) throws Exception {
		String resStr = "";

//		String pubKey = BizEnv.getEnvString("zhongyizaixian.http.pubkey");
//		if(StringUtils.isBlank(pubKey)){
//			result.put("returnCode", "2999");
//			result.put("returnMessage", "找不到对应的公钥信息");
//			result.put("isSuc", "1");
//			return resStr;
//		}
//		RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            result.put("returnCode", "2999");
            result.put("returnMessage", "找不到对应的公钥信息");
            result.put("isSuc", "1");
            return resStr;
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

		//对加密数据进行解密
		resStr = plus.decrypt(encryptString);
		return resStr;
	}
	/**
	 * 
	 * @Description：加密数据
	 * @param:@param encryptString 密文
	 * @param:@return
	 * @return String 解密后的数据
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2017-12-11下午03:32:55
	 */
	private String encrypt(String encryptString,IData result, String targetCode) throws Exception {
		String resStr = "";

//		String pubKey = BizEnv.getEnvString("zhongyizaixian.http.pubkey");
//		if(StringUtils.isBlank(pubKey)){
//			result.put("returnCode", "2999");
//			result.put("returnMessage", "找不到对应的公钥信息");
//			result.put("isSuc", "1");
//			return resStr;
//		}
//		RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

        IDataset pubKeySet = CmOnlineUtil.queryPushKey(targetCode);
        if (IDataUtil.isEmpty(pubKeySet)) {
            result.put("returnCode", "2999");
            result.put("returnMessage", "找不到对应的公钥信息");
            result.put("isSuc", "1");
            return resStr;
        }
        String pubKey = pubKeySet.getData(0).getString("KEY");
        RealNameMsDesPlus plus = new RealNameMsDesPlus(pubKey);

		//对加密数据进行解密
		resStr = plus.encrypt(encryptString);
		return resStr;
	}


	/**
	 * @Description：取消号码预占
	 * @param:@param input
	 * @param:@return
	 * @return IData
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-1-2上午09:44:48
	 */
	public IData cancelPreemptionResNo(IData input) throws Exception {
		
		IData result = new DataMap();
		/**
		 * 1、调用预占号码查询接口取到OCCUPY_ID
		 */
		String serialNumber = IDataUtil.chkParam(input,"SERIAL_NUMBER");
		
		IData param = new DataMap();
		param.put("X_GET_MODE", "0");
		param.put("RES_TYPE_CODE", "0");
		param.put("RES_NO", serialNumber);
		IDataset set = ResCall.callRes("RCF.resource.IResPublicIntfOperateSV.releaseResNo", param);
		if(set != null && set.size()>0){
			logger.debug("取消号码预占返回"+set.toString());
			if("0".equals(set.first().getString("X_RESULTCODE"))){
				result.put("X_RESULTCODE", "0");
				result.put("X_RESULTINFO", "取消号码预占成功");
				return result;
			}else{
				result.put("X_RESULTCODE", "2998");
				result.put("X_RESULTINFO", set.first().getString("X_RESULTINFO"));
				return result;
			}
		}else{
			result.put("X_RESULTCODE", "2998");
			result.put("X_RESULTINFO", "取消号码预占异常");
			return result;
		}
	}

}
