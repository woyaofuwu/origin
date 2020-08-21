package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform.AbilityPlatCheckRelativeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * @author yangsh6
 *  第三方电商对接能力开放平台省公司系统改造
 */
public class AbilityPlatCheckBean extends CSBizBean {

	static Logger log = Logger.getLogger(AbilityPlatCheckBean.class);

	/**
	 * 6.3	业务办理资格校验
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkCustomerDoService(IData input) throws Exception {
		
		// 判断产品用户状态是否正常，非正常状态不能添加成员
		String  numType = input.getString("NUMTYPE", ""); //类型  1-手机号；2-宽带号码
		String  number=input.getString("MOBILENO","");  //服务号码 
		String  checkType=input.getString("CHECKTYPE",""); //校验类型 
		String  goodsID=input.getString("GOODSID","");//商品id 
		//String  checkID=input.getString("CHECKID",""); //办理的产品编码
		IDataset productList=input.getDataset("PRODUCT_LIST");
		String eparchyCode = RouteInfoQry.getEparchyCodeBySnForCrm(number);
		
		IData  data=new DataMap();
 		    
		//宽带验证 
		if("2".equals(numType)){
			IData iparam = new DataMap();
			iparam.put("SERIAL_NUMBER", number);
		    IDataset  dataSet= AbilityPlatCheckRelativeQry.getUserInfoBySn(iparam);
  		     if(("1".equals(checkType))&&(dataSet==null||dataSet.size()==0)){
	  		    	 data.put("BIZORDERRESULT","2009");
		   		     data.put("RESERVE","用户号码状态不正常");
		   		     return data;
	  		 }
  		     if(("2".equals(checkType)||"3".equals(checkType))&&(dataSet!=null&&dataSet.size()>0))
  		     {
	  		     for (int i = 0; i < dataSet.size(); i++) {
				    IData  datas=dataSet.getData(i);
				    boolean  bool=  AbilityPlatCheckRelativeQry.getWidenetAcctList(datas);
				    if(bool){
			    	     data.put("BIZORDERRESULT","3004");
			   		     data.put("RESERVE","用户已经办理了宽带");
			   		     return  data;
				    }
			     }
  		     }
  		    data.put("BIZORDERRESULT","0000");
   		    data.put("RESERVE","业务办理验证正确");
			return  data;
		}
		else if("1".equals(numType)){
				//1-号码状态校验;
		        if("1".equals(checkType)){
		        	if(!"".equals(number)){
				        	boolean  bool= this.checkUserStates(number);
				        	if(bool){
				        		data.put("BIZORDERRESULT","0000");
				        		data.put("RESERVE","用户号码状态正常");
				        	}
				        	else{
				        		data.put("BIZORDERRESULT","2009");
				        		data.put("RESERVE","用户号码状态不正常");
				        	}
				    }else{
				    	data.put("BIZORDERRESULT","2009");
		        		data.put("RESERVE","用户服务号码为空");
				    }
		        	
		        	/**
		        	 * REQ201612010011_能力开放平台业务
		        	 * @author zhuoyingzhi
		        	 * 20170207
		        	 * 增加对用户的实名制校验
		        	 */
		         //手机号码
		         String serial_number=input.getString("SERIAL_NUMBER", "");
		         
		         IData userInfo=UcaInfoQry.qryUserInfoBySn(serial_number);
		         if(IDataUtil.isNotEmpty(userInfo)){
		        	 String custId=userInfo.getString("CUST_ID", "");
		        	 IData  custInfo=UcaInfoQry.qryCustomerInfoByCustId(custId);
		        	 if(IDataUtil.isNotEmpty(custInfo)){
		        		 //实名制标识
		        		 String isRealName=custInfo.getString("IS_REAL_NAME", "");
		        		 if("".equals(isRealName)||"0".equals(isRealName)){
		        			 //非实名制
						    	data.put("BIZORDERRESULT","4034");
				        		data.put("RESERVE","实名制验证本地库验证不通过");
		        		 }
		        	 }
		         }
		         /**********************结束******************************/
		        }
		        //2-能否订购新合约；
		        if("2".equals(checkType)){
				    data.put("BIZORDERRESULT","0000");
		    		data.put("RESERVE","业务办理验证正确");
		    	 if(IDataUtil.isNotEmpty(productList)){
		    	  for(int j=0;j<productList.size();j++){
		    		String checkID=productList.getData(j).getString("CHECK_ID");
		    		String qwproductType=productList.getData(j).getString("PRODUCT_TYPE","");
		    		IDataset  dataSet = AbilityPlatCheckRelativeQry.getBossProByCtrmId(goodsID,checkID,eparchyCode);
		        	IData dataMap = new DataMap();
		        	if(IDataUtil.isNotEmpty(dataSet)){
		        		for(int i=0;i<dataSet.size();i++){
		        			dataMap = dataSet.getData(i);
		        			if("10200".equals(qwproductType)||"10100".equals(qwproductType)){//视频流量定向流量包（话费扣费-用户自选ServiceID）
	                               String serviceIdList= productList.getData(j).getString("SERVICE_ID_LIST","");
	                               dataMap.put("PRODUCT_TYPE", qwproductType);
	                               dataMap.put("SERVICE_ID_LIST", serviceIdList);
	                           }
		        			if("1".equals(dataMap.getString("CTRM_PRODUCT_TYPE"))){   //终端不做校验
		        				continue;
		        			}else if("2".equals(dataMap.getString("CTRM_PRODUCT_TYPE"))){   //合约
		        				dataMap.put("SERIAL_NUMBER", number);
		        				data = executeContract(dataMap,input,eparchyCode,number);
		        			}else if("3".equals(dataMap.getString("CTRM_PRODUCT_TYPE"))){   //套餐
		        				//这里只是判断是否订购新合约，不做校验
								
		        			}
		        		}
		        	}
		        }
		    	}
		        }
		        //3-业务办理资格校验
		        if("3".equals(checkType)){
		        	data.put("BIZORDERRESULT","0000");
		     		data.put("RESERVE","业务办理验证正确");
		     	 if(IDataUtil.isNotEmpty(productList)){
		     		//校验产品入参之间的关系，productType=102XX
		     		try{
		     			 AbilityRuleCheck.checkParamRelation(number,productList,eparchyCode);
		            	}catch(Exception ex){
		            		  if(log.isDebugEnabled()){
		            			  log.debug("-------入参校验错误--------"+ex.getMessage());	
		            	        }
		            		 data.put("BIZORDERRESULT", "3004");
		                     data.put("RESERVE", ex.getMessage()); 
		                     return data;
		            	}		

		     			//REQ201910220040_关于一级能力开放平台新增5G个人及家庭会员权益年包的支撑改造方案  
			    		boolean verifyFlag = BizEnv.getEnvBoolean("SS.AbilityOpenPlatFormIntfSVC.abilityOpenPlatSubmit.switch");
			    		if (verifyFlag)
			    		{
			    			if(StringUtils.isNotEmpty(number)){
			     				IDataset userInfo = UserInfoQry.getUsersBySn(number);
			     				if(IDataUtil.isNotEmpty(userInfo)){
			     					String PREPAY_TAG = userInfo.getData(0).getString("PREPAY_TAG","");
									UcaData uca = UcaDataFactory.getNormalUcaForGrp(number);
									if("0".equals(PREPAY_TAG)){
										//后付费用户
										int CREDIT_VALUE = uca.getUserCreditValue();
										if(CREDIT_VALUE < 0){
											data.put("BIZORDERRESULT","3002");
											data.put("RESERVE","用户信用额度不足,无法订购");
											return data;
										}
									}else if("1".equals(PREPAY_TAG)){
										//预付费用户
										String AccBlance = uca.getAcctBlance();
										int AccB = Integer.parseInt(AccBlance);
										if(AccB < 0){
											data.put("BIZORDERRESULT","3002");
											data.put("RESERVE","用户话费余额不足,无法订购");
											return data;
										}
									}
			     				}
			     			}
			    		}
		     			
						
		     	   for(int h=0;h<productList.size();h++){
		     		String checkID=productList.getData(h).getString("CHECK_ID");
		     		String qwproductType=productList.getData(h).getString("PRODUCT_TYPE","");
		        	IDataset  dataSet=AbilityPlatCheckRelativeQry.getBossProByCtrmId(goodsID,checkID,eparchyCode);
		        	IData product = new DataMap();
		        	IDataset eleIdList = new DatasetList();
		        	boolean flag = false;
		        	if(IDataUtil.isNotEmpty(dataSet)){
		        		for (int i = 0; i < dataSet.size(); i++) {
		        			product = dataSet.getData(i);
		        			IData eleInfo = new DataMap();
		        			if("10200".equals(qwproductType)||"10100".equals(qwproductType)){//视频流量定向流量包（话费扣费-用户自选ServiceID）		                
                                String serviceIdList= productList.getData(h).getString("SERVICE_ID_LIST","");
                                eleInfo.put("PRODUCT_TYPE", qwproductType);
                                eleInfo.put("SERVICE_ID_LIST", serviceIdList);
	                        }
		        			if("1".equals(product.getString("CTRM_PRODUCT_TYPE"))){ //终端产品
								continue; 
							}else if ("2".equals(product.getString("CTRM_PRODUCT_TYPE"))) {
								
								data = executeContract(product,input,eparchyCode,number);
								if (!"0000".equals(data.getString("BIZORDERRESULT"))) {
									return data;
								}

							} else if ("3".equals(product.getString("CTRM_PRODUCT_TYPE"))) {
								
								eleInfo.put("ELEMENT_ID", product.getString("ELEMENT_ID"));
								eleInfo.put("MODIFY_TAG", "0");
								if ("P".equals(product.getString("ELEMENT_TYPE_CODE"))) {
									eleInfo.put("ELEMENT_ID", product.getString("PRODUCT_ID"));
									//判断是不是超享卡内优惠变更
									IData userProInfo = UcaInfoQry.qryUserMainProdInfoBySn(number);
									if (checkID.startsWith("qwc") && userProInfo != null && product.getString("PRODUCT_ID","").equals(userProInfo.getString("PRODUCT_ID"))&&"10004445".equals(userProInfo.getString("PRODUCT_ID"))) {
										flag = true;
									}
								}
								eleInfo.put("ELEMENT_TYPE_CODE", product.getString("ELEMENT_TYPE_CODE"));
								eleIdList.add(eleInfo);
							}
						}
		        		if(flag){ //超享卡优惠内变更，重新做下元素处理
		  	  				IDataset elements = builderElements(number, checkID);
		  	  				eleIdList.clear();
		  	  				eleIdList.addAll(elements);
		  	  			}
		        		if (IDataUtil.isNotEmpty(eleIdList)) {
		        			 if(log.isDebugEnabled()){
		        				 log.debug("-------进入changProductCheck--------"+eleIdList+"----"+input);	
	                	        }
		        			data = executeProduct(eleIdList,input,eparchyCode,number);
		        			if (!"0000".equals(data.getString("BIZORDERRESULT"))) {
								return data;
							}
						}
		        		
		        	} else{
		        		data.put("BIZORDERRESULT","9999");
			    		data.put("RESERVE","商品或者产品的映射关系没有配置.请联系管理员!");
			    		return data;
		        	}
		     		}
		        	data.put("BIZORDERRESULT","0000");
		    		data.put("RESERVE","业务办理验证正确");
		     		}else{
		        		data.put("BIZORDERRESULT","9999");
			    		data.put("RESERVE","商品或者产品信息不存在!");
		        	}
		        }
				return data;
		}
		else{
			//只有产品编码---得到boss编码 
        	data.put("BIZORDERRESULT","2001");
    		data.put("RESERVE","不存在您指定的验证类型. (1为手机号；2为宽带号码)");
    		return  data;
		}
	}
	
	/**
	 * 执行合约计划
	 * 
	 */
	public IData executeContract(IData input, IData paramInput, String eparchyCode, String number) throws Exception {
		
		IData data = new DataMap();
		data.put("BIZORDERRESULT", "0000");
		data.put("RESERVE", "能订购新合约");
		// String number=input.getString("SERIAL_NUMBER","");
		IDataset midRes = UserInfoQry.getUserInfoBySn(number, "0");
		if (midRes == null || midRes.size() == 0) 
		{
			CSAppException.apperr(CrmUserException.CRM_USER_17);// ("830013", "查询用户信息无资料！");
		}
		String userId = midRes.getData(0).getString("USER_ID");
		// 有一个字段检查的类型. 检查的产品ID
		// String currentProId = midRes.getData(0).getString("PRODUCT_ID"); //
		// 用户当前的产品Id
		boolean bool = false;
		String passProId = "";
		// 产品信息
		try {

			String checkProId = input.getString("CHECK_ELEMENT_ID"); // 需要验证的产品id
			String checkProType = input.getString("CHECK_ELEMENT_TYPE"); // 需要验证的产品类型

			if (StringUtils.isNotBlank(checkProId) && StringUtils.isNotBlank(checkProType)) 
			{ 	// 这两个不为空，
				// 才需要验证下面的内容
				if ("D".equalsIgnoreCase(checkProType)) 
				{ // 产品的
					// 找出用户预约的优惠
					IDataset distcntSet = AbilityPlatCheckRelativeQry.getUserProductById(userId);
					if (distcntSet != null) 
					{
						for (int i = 0; i < distcntSet.size(); i++) 
						{
							IData disdata = new DataMap();
							disdata = distcntSet.getData(i);
							String disCode = disdata.getString("DISCNT_CODE");
							// 开始业务判断 --
							if (checkProId.indexOf(disCode) >= 0) 
							{ // 当月或者次月满足
								bool = true;
								// 继续往下判断
								break;
							}
						}
						
					}
				}
				IDataset changeInfo = AbilityPlatCheckRelativeQry.getUserChangeInfo(userId); // 预约的变更的套餐
				if ("P".equalsIgnoreCase(checkProType)) 
				{ // 产品
					if (changeInfo != null) 
					{
						for (int i = 0; i < changeInfo.size(); i++) 
						{
							passProId = changeInfo.getData(i).getString("PRODUCT_ID");
							// 开始业务判断 --
							if (checkProId.indexOf(passProId) >= 0) 
							{ // 当月或者次月满足
								bool = true;
								// 继续往下判断
								break;
							}
						}
					}
				}

				if (!bool) 
				{ // 当月和次月都不满足
					if (changeInfo != null && changeInfo.size() > 1) 
					{ // 有预约的产品变更
						// 验证失败
						// data.put("BIZORDERRESULT", "3004");
						data.put("BIZORDERRESULT", "2000");
						data.put("RESERVE", "业务办理验证失败,已有预约的产品变更！");
						return data;
					} 
					else 
					{
						IDataset productInfos = new DatasetList(); // 产品元素
						String[] strs = checkProId.split("\\|");
						for (int i = 0; i < strs.length; i++) 
						{
							if (strs[i].startsWith("qwc")) 
							{
								IDataset elements = builderElements(number, strs[i]);
								data = executeProduct(elements, paramInput, eparchyCode, number);
							} 
							else 
							{
								productInfos.clear();
								input.put("MODIFY_TAG", "0");
								if ("P".equalsIgnoreCase(checkProType)) 
								{
									input.put("PRODUCT_ID", strs[i]);
									input.put("ELEMENT_TYPE_CODE", checkProType);
								}
								else 
								{
									input.put("ELEMENT_ID", strs[i]);
									input.put("ELEMENT_TYPE_CODE", checkProType);
								}

								productInfos.add(input);
								data = executeProduct(productInfos, paramInput, eparchyCode, number);

								// 没有预约的产品变更
								// 校验产品变更

							}

							if (!"0000".equals(data.getString("BIZORDERRESULT"))) // 验证产品变更失败
							{
								return data;
							}
						}

					}

				}

			}

			IData saleActiveInfo = new DataMap();
			saleActiveInfo.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
			saleActiveInfo.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));
			saleActiveInfo.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
			saleActiveInfo.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE"));
			saleActiveInfo.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
			saleActiveInfo.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
			saleActiveInfo.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
			saleActiveInfo.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
			saleActiveInfo.put("SERIAL_NUMBER", number);
			saleActiveInfo.put("ACTION_TYPE", "0");
			saleActiveInfo.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
			IDataset retMap = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleActiveInfo);

			IData contractData = (retMap != null && retMap.size() > 0) ? retMap.getData(0) : null;

			if (StringUtils.isBlank(contractData.getString("ORDER_ID")))
			{
				data.put("BIZORDERRESULT", "3004");
				data.put("RESERVE", contractData.getString("X_RESULTINFO", "不能办理此合约"));
			}

		} catch (Exception ex) {

			String errorStr = getMessage(ex);
			if (StringUtils.isNotEmpty(errorStr)) {
				// errorStr = errorStr.substring(errorStr.indexOf("@"));
				// errorStr = errorStr.substring(0,errorStr.indexOf(":"));
				String[] errorMessage = errorStr.split("`");
				data.put("BIZORDERRESULT", "3004");
				data.put("RESERVE", errorMessage[1]);
			}

		}

		return data;

	}
	
	/**
	 * 执行产品信息
	 * 
	 * @author tanjl
	 * @param productInfos
	 * @param platSvcInfos
	 * @throws Exception
	 */
	public IData executeProduct(IDataset productInfos, IData input, String eparchyCode, String number) throws Exception 
	{

		IData data = new DataMap();
		data.put("BIZORDERRESULT", "0000");
		data.put("RESERVE", "可以受理改业务");

		// 产品信息
		if (IDataUtil.isNotEmpty(productInfos)) 
		{
			try {

				IData infoParam = new DataMap();
				 if("10200".equals(productInfos.getData(0).getString("PRODUCT_TYPE",""))||"10100".equals(productInfos.getData(0).getString("PRODUCT_TYPE",""))){ 			
		           IData returnData=AbilityRuleCheck.checkVideopckrule(number, productInfos.getData(0),eparchyCode);   
		           if(IDataUtil.isNotEmpty(returnData)){
		              //productInfos.add(returnData);
					   productInfos.getData(0).putAll(returnData);
		           }
			     }
				infoParam.put("ELEMENTS", productInfos);
				infoParam.put("SERIAL_NUMBER", number);
				infoParam.put("IN_MODE_CODE", input.getString("IN_MODE_CODE"));
				infoParam.put("KIND_ID", input.getString("KIND_ID"));
				infoParam.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
				if(log.isDebugEnabled()){
      			  log.debug("-------产品变更入参--------"+infoParam);	
      	        }
				IDataset retnInfo = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", infoParam);

				if (retnInfo != null && retnInfo.size() > 0 && StringUtils.isBlank(retnInfo.getData(0).getString("ORDER_ID"))) 
				{
					// data.put("BIZORDERRESULT","3004");
					data.put("BIZORDERRESULT", "2013");
					data.put("RESERVE", retnInfo.getData(0).getString("X_RESULTINFO").substring(0, 100));
				}

			} 
			catch (Exception e) 
			{				
				String errorStr = getMessage(e);
				if(log.isDebugEnabled()){
	      			  log.debug("-------产品变更入报错--------"+errorStr);	
	      	        }
				if (StringUtils.isNotEmpty(errorStr)) 
				{
					// errorStr = errorStr.substring(errorStr.indexOf("@"));
					// errorStr = errorStr.substring(0,errorStr.indexOf(":"));
					String[] errorMessage = errorStr.split("`");
					if (errorMessage.length >= 2) 
					{
						String strExceptionMessage = errorMessage[1];
						boolean bEM = strExceptionMessage.contains("产品无法办理");
						if (bEM) 
						{
							data.put("BIZORDERRESULT", "2013");
							data.put("RESERVE", errorMessage[1]);
						} 
						else 
						{
							data.put("BIZORDERRESULT", "3004");
							data.put("RESERVE", errorMessage[1]);
						}
					} 
					else 
					{
						data.put("BIZORDERRESULT", "3004");
						data.put("RESERVE", errorStr);
					}
				}
			}
		}
		return data;
	}
	
	/**
	 * 处理异常
	 * @param e
	 * @return
	 */
	private String getMessage(Exception e) {
    	Throwable t = Utility.getBottomException(e);
    	String s = "";
    	if(t != null){
    		s = t.getMessage();
    	}

    	if(StringUtils.isNotBlank(s)){
    		if(s.length() > 120){
    			s = s.substring(0, 120);
    		}
    	}
    	return s;
    }
	
	/**
	 * 检查用户状态是否正常
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean checkUserStates(String number) throws Exception {
		// 根据号码找地州
		IData iparam = new DataMap();
		iparam.put("SERIAL_NUMBER", number);
		iparam.put("REMOVE_TAG", "0");
		boolean bool = false;
		IDataset dataSet = AbilityPlatCheckRelativeQry.getUserInfoBySn(iparam);
		for (int i = 0; i < dataSet.size(); i++) {
			String stats = dataSet.getData(i).getString("USER_STATE_CODESET");
			if (null != stats && "" != stats) {
				if ("0".equals(stats)) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}
	
	/***********************************************************************************
     * 针对飞享套餐特殊处理<BR/>
     * 飞享套餐传入的产品ID为"qwc + 数字"，需要转换为省内产品编码<BR/>
     * 1.如果用户主产品和转换后的产品不一致，那么需要进行主产品变更<BR/>
     * 2.如果用户主产品和转换后的产品一致，只需要进行产品内元素变更<BR/>
     * 
     * @param serialNumber	用户号码
     * @param newProductID	变更产品
     * @return
     * @throws Exception
     */
    private IDataset builderElements(String serialNumber, String newProductID)throws Exception{
		//1.根据用户号码查询用户信息
        IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        if(IDataUtil.isEmpty(userInfos))
        	CSAppException.apperr(CrmUserException.CRM_USER_1);
        //2.查询用户主产品信息
        String userID = userInfos.getData(0).getString("USER_ID");
        IDataset userMainProduct = UserProductInfoQry.queryUserMainProduct(userID);
        if(IDataUtil.isEmpty(userMainProduct))
        	CSAppException.apperr(CrmUserException.CRM_USER_45, userID);
        //3.对传入的产品进行转换
		IDataset configElementList = qryConfigElements(newProductID, userInfos.getData(0).getString("EPARCHY_CODE"));
		String productID = configElementList.getData(0).getString("PRODUCT_ID");
		
		//4.1.如果是主产品变更，需要设置参数进行主产品变更
		if(! productID.equals(userMainProduct.getData(0).getString("PRODUCT_ID"))){
			IData item = new DataMap();
			item.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			item.put("ELEMENT_ID", productID);
			item.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
			configElementList.add(item);
			
		//4.2.如果是元素变更，需要删除原产品下指定优惠
		}else{
			//4.2.1.查询用户已订购的飞享套餐优惠
			IDataset orderDiscnt = UserDiscntInfoQry.getFXDiscntByUserId(userID);
			if(IDataUtil.isEmpty(orderDiscnt))
				CSAppException.apperr(CrmUserException.CRM_USER_914, productID);
			//4.2.2.处理相同的元素：如果用户已订购了变更后套餐的元素，不用给予删除、添加操作
			dealDuplicateElements(configElementList, orderDiscnt);
			if(IDataUtil.isEmpty(configElementList))
				return configElementList;
			//4.2.2.退订这些优惠
			for(int i = 0; i < orderDiscnt.size(); i++){
				//如果是GPRS优惠，不能退订 
				if(IDataUtil.isNotEmpty(DiscntInfoQry.getDiscntIsValid("5", orderDiscnt.getData(i).getString("DISCNT_CODE"))))
					continue;
				IData item = new DataMap();
				item.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
				item.put("ELEMENT_ID", orderDiscnt.getData(i).getString("DISCNT_CODE"));
				item.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
				item.put("INST_ID", orderDiscnt.getData(i).getString("INST_ID"));
				configElementList.add(item);
			}
		}
		return configElementList;
	}
	/************************************************************************************
	 * 将传入的产品编码转换为省内的产品编码<BR/>
	 * 使用TD_S_COMMPARA表PARAM_ATTR为2801配置进行产品转换<BR/>
	 * 
	 * @param newProductID	转换前的产品编码
	 * @param eparchyCode	地州
	 * @return
	 * @throws Exception
	 */
	private IDataset qryConfigElements(String newProductID, String eparchyCode)throws Exception{
		//1.查询产品转换关系
		IDataset configElementList = CommparaInfoQry.getCommPkInfo("CSM", "2801", newProductID, eparchyCode);
		//2.没有查询到转换关系，抛出异常
		if(configElementList.isEmpty())
			CSAppException.apperr(ParamException.CRM_PARAM_359);
		
		IData configElement = configElementList.getData(0);
		String countStr = configElement.getString("PARA_CODE2");//必选元素个数
		//3.如果【必选元素个数】，不在5个内，配置已经有问题，抛出异常
		if(! countStr.matches("[1-5]"))
			CSAppException.apperr(ParamException.CRM_PARAM_145);
		String productID = configElement.getString("PARA_CODE1"), elementItem = null;
		//4.解析配置元素信息：ELEMENT_ID + '_' + ELEMENT_TYPE_CODE
		IDataset result = new DatasetList();
		IData item = null;
		for(int i = 0, len = Integer.parseInt(countStr); i < len; i++){
			elementItem = configElement.getString("PARA_CODE" + (i + 3));//取 PARA_CODE3（包括在内）后的PARA_CODE2个元素
			String[] elementInfo = elementItem.split("_");
			if(elementInfo.length != 2)
				CSAppException.apperr(ParamException.CRM_PARAM_146);
				
			item = new DataMap();
			item.put("PRODUCT_ID", productID);
			item.put("ELEMENT_TYPE_CODE", elementInfo[1]);
			item.put("ELEMENT_ID", elementInfo[0]);
			item.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
			result.add(item);
		}
		return result;
	}
	/*****************************************************************************
	 * 去重：新增列表和删除列表中相同的元素<BR/>
	 * @param addList	新增列表
	 * @param delList	删除列表
	 */
	private void dealDuplicateElements(IDataset addList, IDataset delList){
		if(IDataUtil.isEmpty(addList) || IDataUtil.isEmpty(delList))
			return;
		
		for(int i = 0; i < addList.size(); i++){
			String addTypeCode = addList.getData(i).getString("ELEMENT_TYPE_CODE", ""), addID = addList.getData(i).getString("ELEMENT_ID", "");
			
			for(int j = 0; j < delList.size(); j++){
				if(addTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT) && addID.equals(delList.getData(j).getString("DISCNT_CODE"))){
					addList.remove(i--);
					delList.remove(j--);
				}
			}
		}
	}
	
}
