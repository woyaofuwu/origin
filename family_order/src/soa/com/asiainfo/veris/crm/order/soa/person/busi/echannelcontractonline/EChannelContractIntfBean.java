package com.asiainfo.veris.crm.order.soa.person.busi.echannelcontractonline;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform.AbilityPlatCheckRelativeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class EChannelContractIntfBean extends CSBizBean{
	
	Logger logger = Logger.getLogger(EChannelContractIntfBean.class); 

	/**
	 * 检查用户状态是否正常
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean checkUserStates(String number) throws Exception 
	{
		// 根据号码找地州
		IData iparam = new DataMap();
		iparam.put("SERIAL_NUMBER", number);
		iparam.put("REMOVE_TAG", "0");
		boolean bool = false;
		IDataset idsDataSet = AbilityPlatCheckRelativeQry.getUserInfoBySn(iparam);
		if(IDataUtil.isNotEmpty(idsDataSet))
		{
			for (int i = 0; i < idsDataSet.size(); i++) 
			{
				IData idDataSet = idsDataSet.getData(i);
				String stats = idDataSet.getString("USER_STATE_CODESET");
				if (StringUtils.isNotBlank(stats)) 
				{
					if ("0".equals(stats)) 
					{
						bool = true;
						break;
					}
				}
			}
		}
		return bool;
	}
	
	/**
	 * 合约办理资格校验
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkCustomerDoService(IData input) throws Exception 
	{
		
		// 判断产品用户状态是否正常，非正常状态不能添加成员
		String number = input.getString("SERIAL_NUMBER");  	//服务号码 
		String checkType = input.getString("CHECK_TYPE"); 	//校验类型 
		String bossID = input.getString("BOSS_ID"); 		//校验类型 	
		
		input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK); //只校验，不生成台帐
		
		String eparchyCode = RouteInfoQry.getEparchyCodeBySnForCrm(number);		
		IData data = new DataMap();
		data.put("X_RESULTCODE", "-1");
		data.put("X_RESULTINFO", "尊敬的客户，您好！您办理合约业务失败。");
		/*data.put("CP_RESULTCODE", "-1");
		data.put("CP_RESULTINFO", "尊敬的客户，您好！您的产品办理失败。");
		data.put("CP_MESSAGE", "尊敬的客户，您好！您的产品办理失败。");
		data.put("SA_RESULTCODE", "-1");
		data.put("SA_RESULTINFO", "尊敬的客户，您好！您的合约办理失败。");
		data.put("SA_MESSAGE", "尊敬的客户，您好！您的合约办理失败。");*/
		input.putAll(data);
		//3-业务办理资格校验
        if("3".equals(checkType))
        {
        	boolean bool= this.checkUserStates(number);
        	if(!bool)
        	{
        		data.put("X_RESULTCODE", "-1");
        		data.put("X_RESULTINFO", "尊敬的客户，您好！您办理合约业务失败。");
        		/*data.put("CP_RESULTCODE", "2001");
        		data.put("CP_RESULTINFO", "尊敬的客户，您好！您的号码状态不正常，不允许办理该业务。");
        		data.put("CP_MESSAGE", "尊敬的客户，您好！您的号码状态不正常，不允许办理该业务。");
        		data.put("SA_RESULTCODE", "2001");
        		data.put("SA_RESULTINFO", "尊敬的客户，您好！您的号码状态不正常，不允许办理该业务。");
        		data.put("SA_MESSAGE", "尊敬的客户，您好！您的号码状态不正常，不允许办理该业务。");*/
        		return data;
        	}
        	IDataset dataSet = this.getBossProByCtrmId(bossID, eparchyCode);
        	IData product = new DataMap();
        	IDataset eleIdList = new DatasetList();
        	boolean flag = false;
        	if(IDataUtil.isNotEmpty(dataSet))
        	{
        		for (int i = 0; i < dataSet.size(); i++) 
        		{
        			product = dataSet.getData(i);
        			IData eleInfo = new DataMap();
        			if("1".equals(product.getString("CTRM_PRODUCT_TYPE")))
        			{
						continue; //终端产品
					}
        			else if ("2".equals(product.getString("CTRM_PRODUCT_TYPE"))) 
					{
        				data = executeContract(product, input, eparchyCode, number);
        				if (!"0".equals(data.getString("SA_RESULTCODE", "-1")) && !"0".equals(data.getString("CP_RESULTCODE", "-1"))) 
        				{
							return data;
						}
					}
					else if ("3".equals(product.getString("CTRM_PRODUCT_TYPE"))) 
					{
						eleInfo.put("ELEMENT_ID", product.getString("ELEMENT_ID"));
						eleInfo.put("MODIFY_TAG", "0");
						if ("P".equals(product.getString("ELEMENT_TYPE_CODE")))
						{
							eleInfo.put("ELEMENT_ID", product.getString("PRODUCT_ID"));	
							IData userProInfo = UcaInfoQry.qryUserMainProdInfoBySn(number);
							if (IDataUtil.isNotEmpty(userProInfo) && 
								product.getString("PRODUCT_ID","").equals(userProInfo.getString("PRODUCT_ID")) && 
								"10004445".equals(userProInfo.getString("PRODUCT_ID"))) 
							{
								flag = true;
							}
						}
						eleInfo.put("ELEMENT_TYPE_CODE", product.getString("ELEMENT_TYPE_CODE"));
						eleIdList.add(eleInfo);
					}
				}
        		if(flag)
        		{
        			//超享卡优惠内变更，重新做下元素处理
  	  				IDataset elements = builderElements(number, bossID);
  	  				eleIdList.clear();
  	  				eleIdList.addAll(elements);
  	  			}
        		if (IDataUtil.isNotEmpty(eleIdList)) 
        		{
        			data = executeProduct(eleIdList, input, eparchyCode, number);
        			if (!"0".equals(data.getString("SA_RESULTCODE", "-1")) && !"0".equals(data.getString("CP_RESULTCODE", "-1"))) 
        			{
						return data;
					}
				}       		
        	}
        	else
        	{
        		/*data.put("CP_RESULTCODE", "2001");
        		data.put("CP_RESULTINFO", "尊敬的客户，您好！订购关系不存在，不允许办理该业务。");
        		data.put("CP_MESSAGE", "尊敬的客户，您好！订购关系不存在，不允许办理该业务。");
        		data.put("SA_RESULTCODE", "2001");
        		data.put("SA_RESULTINFO", "尊敬的客户，您好！订购关系不存在，不允许办理该业务。");
        		data.put("SA_MESSAGE", "尊敬的客户，您好！订购关系不存在，不允许办理该业务。");*/
        		data.put("X_RESULTCODE", "2001");
	    		data.put("X_RESULTINFO", "商品或者产品的映射关系没有配置.请联系管理员!");
        	}
        }
		return data;		
	}
	
	/***********************************************************************************
     * 针对飞享套餐特殊处理<BR/>
     * 飞享套餐传入的产品ID为"wtc + 数字"，需要转换为省内产品编码<BR/>
     * 1.如果用户主产品和转换后的产品不一致，那么需要进行主产品变更<BR/>
     * 2.如果用户主产品和转换后的产品一致，只需要进行产品内元素变更<BR/>
     * 
     * @param serialNumber	用户号码
     * @param newProductID	变更产品
     * @return
     * @throws Exception
     */
    public IDataset builderElements(String serialNumber, String newProductID)throws Exception
    {
		//1.根据用户号码查询用户信息
        IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        if(IDataUtil.isEmpty(userInfos))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_1);
        }	
        //2.查询用户主产品信息
        IData userInfo = userInfos.getData(0);
        String strUserID = userInfo.getString("USER_ID");
        String strEparchyCode = userInfo.getString("EPARCHY_CODE");
        IDataset userMainProduct = UserProductInfoQry.queryUserMainProduct(strUserID);
        if(IDataUtil.isEmpty(userMainProduct))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_45, strUserID);
        }
        //3.对传入的产品进行转换
		IDataset configElementList = qryConfigElements(newProductID, strEparchyCode);
		String productID = "";
		if(IDataUtil.isNotEmpty(configElementList))
		{
			productID = configElementList.getData(0).getString("PRODUCT_ID", "");
		}
		
		//4.1.如果是主产品变更，需要设置参数进行主产品变更
		if(!productID.equals(userMainProduct.getData(0).getString("PRODUCT_ID")))
		{
			IData item = new DataMap();
			item.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			item.put("ELEMENT_ID", productID);
			item.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
			configElementList.add(item);
			
		
		}//4.2.如果是元素变更，需要删除原产品下指定优惠
		else
		{
			//4.2.1.查询用户已订购的飞享套餐优惠
			IDataset orderDiscnt = UserDiscntInfoQry.getFXDiscntByUserIdA(strUserID);
			if(IDataUtil.isEmpty(orderDiscnt))
			{
				CSAppException.apperr(CrmUserException.CRM_USER_914, productID);
			}
			//4.2.2.处理相同的元素：如果用户已订购了变更后套餐的元素，不用给予删除、添加操作
			dealDuplicateElements(configElementList, orderDiscnt);
			if(IDataUtil.isEmpty(configElementList))
			{
				return configElementList;
			}	
			//4.2.2.退订这些优惠
			for(int i = 0; i < orderDiscnt.size(); i++)
			{
				//如果是GPRS优惠，不能退订 
				if(IDataUtil.isNotEmpty(DiscntInfoQry.getDiscntIsValid("5", orderDiscnt.getData(i).getString("DISCNT_CODE"))))
				{
					continue;
				}	
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
		if(IDataUtil.isEmpty(configElementList))
		{
			CSAppException.apperr(ParamException.CRM_PARAM_359);
		}	
		
		IData configElement = configElementList.getData(0);
		String countStr = configElement.getString("PARA_CODE2");//必选元素个数
		//3.如果【必选元素个数】，不在5个内，配置已经有问题，抛出异常
		if(!countStr.matches("[1-5]"))
		{
			CSAppException.apperr(ParamException.CRM_PARAM_145);
		}	
		String productID = configElement.getString("PARA_CODE1"), elementItem = null;
		//4.解析配置元素信息：ELEMENT_ID + '_' + ELEMENT_TYPE_CODE
		IDataset result = new DatasetList();
		IData item = null;
		for(int i = 0, len = Integer.parseInt(countStr); i < len; i++)
		{
			elementItem = configElement.getString("PARA_CODE" + (i + 3));//取 PARA_CODE3（包括在内）后的PARA_CODE2个元素
			String[] elementInfo = elementItem.split("_");
			if(elementInfo.length != 2)
			{
				CSAppException.apperr(ParamException.CRM_PARAM_146);
			}
			
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
	 * @throws Exception 
	 */
	private void dealDuplicateElements(IDataset addList, IDataset delList) throws Exception
	{
		if(IDataUtil.isEmpty(addList) || IDataUtil.isEmpty(delList))
		{
			return;
		}
			
		for(int i = 0; i < addList.size(); i++)
		{
			IData idAdd = addList.getData(i);
			String strAddTypeCode = idAdd.getString("ELEMENT_TYPE_CODE", "");
			String strAddID = idAdd.getString("ELEMENT_ID", "");
			
			for(int j = 0; j < delList.size(); j++)
			{
				IData idDel = delList.getData(j);
				String strDelID = idDel.getString("DISCNT_CODE");
				/*String strAddPrice = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "PRICE", strAddID);
				String strDelPrice = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "PRICE", strDelID);*/
				if(strAddTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT) && strAddID.equals(strDelID))
				{
					addList.remove(i--);
					delList.remove(j--);
					/*Integer nAddP = Integer.parseInt(strAddPrice);
					Integer nDelP = Integer.parseInt(strDelPrice);
					if(nDelP > nAddP)
					{
						CSAppException.apperr(CrmUserException.CRM_USER_783, "尊敬的客户，您好！您办理的套餐档次不能比现有套餐低。");
					}
					else if(strAddID.equals(strDelID))
					{
						addList.remove(i--);
						delList.remove(j--);
					}*/
				}
			}
		}
	}
	
	/**
	 * 执行合约计划
	 * 
	 */
	public IData executeContract(IData input, IData paramInput, String eparchyCode, String number) throws Exception 
	{
		
		IData data = new DataMap();
		
		/*data.put("CP_RESULTCODE", paramInput.getString("CP_RESULTCODE", "-1"));
		data.put("CP_RESULTINFO", paramInput.getString("CP_RESULTINFO", "尊敬的客户，您好！您的产品办理失败。"));
		data.put("CP_MESSAGE", paramInput.getString("CP_MESSAGE", "尊敬的客户，您好！您的产品办理失败。"));
		data.put("SA_RESULTCODE", paramInput.getString("SA_RESULTCODE", "-1"));
		data.put("SA_RESULTINFO", paramInput.getString("SA_RESULTINFO", "尊敬的客户，您好！您的合约办理失败。"));
		data.put("SA_MESSAGE", paramInput.getString("SA_MESSAGE", "尊敬的客户，您好！您的合约办理失败。"));*/
		// String number=input.getString("SERIAL_NUMBER","");
		IDataset midRes = UserInfoQry.getUserInfoBySn(number, "0");
		if (midRes == null || midRes.size() == 0) 
		{
			//CSAppException.apperr(CrmUserException.CRM_USER_17, number);// ("12117", "获取用户资料失败:%s");
			data.put("X_RESULTCODE", "1005");
			data.put("X_RESULTINFO", "尊敬的客户，您好！您的号码状态不正常，不允许办理该业务。");
			return data;
			
			/*data.put("CP_RESULTCODE", "2001");
    		data.put("CP_RESULTINFO", "尊敬的客户，您好！您的号码状态不正常，不允许办理该业务。");
    		data.put("CP_MESSAGE", "尊敬的客户，您好！您的号码状态不正常，不允许办理该业务。");
    		data.put("SA_RESULTCODE", "2001");
    		data.put("SA_RESULTINFO", "尊敬的客户，您好！您的号码状态不正常，不允许办理该业务。");
    		data.put("SA_MESSAGE", "尊敬的客户，您好！您的号码状态不正常，不允许办理该业务。");*/
		}
		String userId = midRes.getData(0).getString("USER_ID");
		// 有一个字段检查的类型. 检查的产品ID
		// String currentProId = midRes.getData(0).getString("PRODUCT_ID"); //
		// 用户当前的产品Id
		boolean bool = false;
		String passProId = "";
		// 产品信息
		try 
		{
			String checkProId = input.getString("CHECK_ELEMENT_ID"); // 需要验证的产品id
			String checkProType = input.getString("CHECK_ELEMENT_TYPE"); // 需要验证的产品类型

			if (StringUtils.isNotBlank(checkProId) && StringUtils.isNotBlank(checkProType)) 
			{ 	// 这两个不为空，
				// 才需要验证下面的内容
				if ("D".equalsIgnoreCase(checkProType)) 
				{ // 产品的
					// 找出用户预约的优惠
					IDataset distcntSet = AbilityPlatCheckRelativeQry.getUserProductById(userId);
					if (IDataUtil.isNotEmpty(distcntSet)) 
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

				if ("K".equalsIgnoreCase(checkProType))
				{
					bool = true;
				}

				if (!bool)
				{ // 当月和次月都不满足
					if (changeInfo != null && changeInfo.size() > 1) 
					{ // 有预约的产品变更
						// 验证失败
						data.put("X_RESULTCODE", "1006");
						data.put("X_RESULTINFO", "尊敬的客户，您好！您已有预约的产品变更，不允许办理该业务。");
						return data;
						/*data.put("CP_RESULTCODE", "3004");
			    		data.put("CP_RESULTINFO", "尊敬的客户，您好！您已有预约的产品变更，不允许办理该业务。");
			    		data.put("CP_MESSAGE", "尊敬的客户，您好！您已有预约的产品变更，不允许办理该业务。");*/
					}
					else 
					{
						//IDataset productInfos = new DatasetList(); // 产品元素
						String[] strs = checkProId.split("\\|");
						for (int i = 0; i < strs.length; i++) 
						{
							String strWtc = strs[i];
							if (strWtc.startsWith("wtc")) 
							{
								IDataset ids4GOnlineProduct = this.getBossProByCtrmId(strWtc, eparchyCode);
								if(IDataUtil.isNotEmpty(ids4GOnlineProduct))
					        	{
									int n4GOnlineCount = 0;
					        		for (int j = 0; j < ids4GOnlineProduct.size(); j++) 
					        		{
					        			IData id4GOnlineProduct = ids4GOnlineProduct.getData(j);
					        			String strElemtTypeCode = id4GOnlineProduct.getString("ELEMENT_TYPE_CODE", "");
					        			if("D".equals(strElemtTypeCode))
					        			{
					        				String str4GOnlineDC = id4GOnlineProduct.getString("ELEMENT_ID");
											//String strUserPrice = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "PRICE", str4GOnlineDC);
											IDataset userPriceDataset = UpcCall.queryTempChaByOfferTableField("D", str4GOnlineDC, "PRICE", "PRICE");
											String strUserPrice = "0";
											if(IDataUtil.isNotEmpty(userPriceDataset))
											{
												strUserPrice = userPriceDataset.getData(0).getString("FIELD_VALUE","0");
											}
											
											n4GOnlineCount += Integer.valueOf(strUserPrice);
					        			}
									}
					        		
					        		IDataset idsUserDiscnt = UserDiscntInfoQry.getFXDiscntByUserIdA(userId);
									if(IDataUtil.isNotEmpty(idsUserDiscnt))
									{
										int nUserCount = 0;
										for (int j = 0; j < idsUserDiscnt.size(); j++)
										{
											IData idUserDiscnt = idsUserDiscnt.getData(j);
											String strUserDC = idUserDiscnt.getString("DISCNT_CODE");
											//String strUserPrice = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "PRICE", strUserDC);
											IDataset userPriceDataset = UpcCall.queryTempChaByOfferTableField("D", strUserDC, "PRICE", "PRICE");
											String strUserPrice = "0";
											if(IDataUtil.isNotEmpty(userPriceDataset))
											{
												strUserPrice = userPriceDataset.getData(0).getString("FIELD_VALUE","0");
											}
											nUserCount += Integer.valueOf(strUserPrice);
										}
										if(n4GOnlineCount > nUserCount)
										{
											IDataset elements = builderElements(number, strWtc);
											IData subInfo = executeProduct(elements, paramInput, eparchyCode, number);
											data.putAll(subInfo);
											if (!"0".equals(data.getString("X_RESULTCODE"))) // 验证产品变更失败
											{
												return data;
											}
										}
									}
									else 
									{
										IDataset elements = builderElements(number, strWtc);
										IData subInfo = executeProduct(elements, paramInput, eparchyCode, number);
										data.putAll(subInfo);
										if (!"0".equals(data.getString("X_RESULTCODE"))) // 验证产品变更失败
										{
											return data;
										}
									}
					        	}
							} 
							else 
							{
								data.put("X_RESULTCODE", "1009");
					    		data.put("X_RESULTINFO", "商品或者产品的映射关系没有配置.请联系管理员!");
					    		return data;
								/*productInfos.clear();
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
								IData subInfo = executeProduct(productInfos, paramInput, eparchyCode, number);
								data.putAll(subInfo);*/

							}
							/*if (!"0000".equals(data.getString("X_RESULTCODE"))) // 验证产品变更失败
							{
								return data;
							}*/
						}

					}

				}

			}

			try 
			{
				IData saleActiveInfo = new DataMap();
				//saleActiveInfo.put("TRADE_TYPE_CODE", "240");
				//saleActiveInfo.put("ORDER_TYPE_CODE", "110");
				saleActiveInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
				saleActiveInfo.put("NO_TRADE_LIMIT", "TRUE");
				/*saleActiveInfo.put("TRADE_EPARCHY_CODE", eparchyCode);
				saleActiveInfo.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
				saleActiveInfo.put("TRADE_CITY_CODE",CSBizBean.getVisit().getCityCode());
				saleActiveInfo.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
				saleActiveInfo.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());*/
				saleActiveInfo.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
				saleActiveInfo.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
				saleActiveInfo.put("SERIAL_NUMBER", number);
				//saleActiveInfo.put("ACTION_TYPE", "0");
				saleActiveInfo.put("PRE_TYPE", paramInput.getString("PRE_TYPE", ""));// 预受理校验，不写台账
				IDataset retnInfo = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleActiveInfo);

				if (IDataUtil.isNotEmpty(retnInfo) && StringUtils.isBlank(retnInfo.first().getString("ORDER_ID"))) 
				{
					data.put("X_RESULTCODE", "1009");
					data.put("X_RESULTINFO", "尊敬的客户，您好！您的合约办理失败2");
					data.put("SA_RESULTINFO", "尊敬的客户，您好！您的合约办理成功2。");
					//return data;
				}
				else
				{
					data.put("SA_TRADE_ID", retnInfo.first().getString("TRADE_ID", "-1"));
					data.put("SA_ORDER_ID", retnInfo.first().getString("ORDER_ID", "-1"));
					data.put("X_RESULTCODE", "0");
					data.put("X_RESULTINFO", "尊敬的客户，您好！您的合约办理成功。");
		    		data.put("SA_RESULTINFO", "尊敬的客户，您好！您的合约办理成功。");
		    		//return data;
				}
			} 
			catch (Exception e) 
			{
				//String error =  Utility.parseExceptionMessage(e); 
				//data.put("X_RESULTCODE", "808294");
				//data.put("X_RESULTINFO", error);
				/*data.put("SA_RESULTCODE", "-1");
				data.put("SA_RESULTINFO", "尊敬的客户，您好！您的合约办理失败。");
				data.put("SA_MESSAGE", error);*/
				
				String error =  Utility.parseExceptionMessage(e);
				String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
				if(errorArray.length >= 2)
				{
					String strException = errorArray[0];
					String strExceptionMessage = errorArray[1];
					data.put("X_RESULTCODE", strException);
					data.put("X_RESULTINFO", strExceptionMessage);
					data.put("SA_RESULTINFO", strExceptionMessage);
					//return data;
				}
				else
				{
					String msg = e.getMessage();
					msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
					data.put("X_RESULTCODE", "1010");
					data.put("X_RESULTINFO", msg);
					data.put("SA_RESULTINFO", msg);
					//return data;
				}
			}
			
		} 
		catch (Exception ex) 
		{
			String error =  Utility.parseExceptionMessage(ex); 
			String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
			if(errorArray.length >= 2)
			{
				String strException = errorArray[0];
				String strExceptionMessage = errorArray[1];
				data.put("X_RESULTCODE", strException);
				data.put("X_RESULTINFO", strExceptionMessage);
				data.put("CP_RESULTINFO", strExceptionMessage);
				data.put("SA_RESULTINFO", strExceptionMessage);
				//return data;
			}
			else
			{
				String msg = ex.getMessage();
				msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
				data.put("X_RESULTCODE", "1011");
				data.put("X_RESULTINFO", msg);
				data.put("CP_RESULTINFO", msg);
				data.put("SA_RESULTINFO", msg);
				//return data;
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
		//data.put("X_RESULTCODE",  input.getString("X_RESULTCODE", "-1"));
		//data.put("X_RESULTINFO",  input.getString("X_RESULTINFO", "尊敬的客户，您好！您的业务办理失败。"));
		/*data.put("CP_RESULTCODE", input.getString("CP_RESULTCODE", "-1"));
		data.put("CP_RESULTINFO", input.getString("CP_RESULTINFO", "尊敬的客户，您好！您的产品办理失败。"));
		data.put("CP_MESSAGE", input.getString("CP_MESSAGE", "尊敬的客户，您好！您的产品办理失败。"));
		data.put("SA_RESULTCODE", input.getString("SA_RESULTCODE", "-1"));
		data.put("SA_RESULTINFO", input.getString("SA_RESULTINFO", "尊敬的客户，您好！您的合约办理失败。"));
		data.put("SA_MESSAGE", input.getString("SA_MESSAGE", "尊敬的客户，您好！您的合约办理失败。"));	*/

		// 产品信息
		if (IDataUtil.isNotEmpty(productInfos)) 
		{
			try 
			{
				IData infoParam = new DataMap();
				//infoParam.put("TRADE_TYPE_CODE", "110");
				//infoParam.put("ORDER_TYPE_CODE", "110");
				infoParam.put("NO_TRADE_LIMIT", "TRUE");
				infoParam.put("ELEMENTS", productInfos);
				infoParam.put("SERIAL_NUMBER", number);
				infoParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
				infoParam.put("BOOKING_TAG", "1");
				infoParam.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
				infoParam.put("PRE_TYPE", input.getString("PRE_TYPE", ""));// 预受理校验，不写台账
				IDataset retnInfo = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", infoParam);

				if (IDataUtil.isNotEmpty(retnInfo) && StringUtils.isBlank(retnInfo.first().getString("ORDER_ID"))) 
				{
					//data.put("X_RESULTCODE", "808294");
					//data.put("X_RESULTINFO", retnInfo.first().getString("X_RESULTINFO"));
					data.put("X_RESULTCODE", "1007");
					data.put("X_RESULTINFO", "尊敬的客户，您好！您的产品办理失败2");
					data.put("CP_RESULTINFO", "尊敬的客户，您好！您的产品办理失败2");
				}
				else
				{
					data.put("CP_TRADE_ID", retnInfo.first().getString("TRADE_ID"));
					data.put("CP_ORDER_ID", retnInfo.first().getString("ORDER_ID"));
					data.put("X_RESULTCODE", "0");
					data.put("X_RESULTINFO", "尊敬的客户，您好！您的产品办理成功。");
					data.put("CP_RESULTINFO", "尊敬的客户，您好！您的产品办理成功.");
				}

			} 
			catch (Exception e) 
			{
				//String error =  Utility.parseExceptionMessage(e); 
				//data.put("X_RESULTCODE", "808294");
				//data.put("X_RESULTINFO", error);
				//data.put("X_RESULTCODE", "-1");
				//data.put("X_RESULTINFO", "尊敬的客户，您好！您的产品办理失败。");
				//data.put("CP_MESSAGE", error);
				
				String error =  Utility.parseExceptionMessage(e); 
				String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
				if(errorArray.length >= 2)
				{
					String strException = errorArray[0];
					String strExceptionMessage = errorArray[1];
					data.put("X_RESULTCODE", strException);
					data.put("X_RESULTINFO", strExceptionMessage);
					data.put("CP_RESULTINFO", strExceptionMessage);
					return data;
				}
				else
				{
					String msg = e.getMessage();
					msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
					data.put("X_RESULTCODE", "1008");
					data.put("X_RESULTINFO", msg);
					data.put("CP_RESULTINFO", msg);
					return data;
				}
			}
		}
		return data;
	}
	
	public void insertIntoCtrmTList(DBConnection conn,IData data) throws Exception
	{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		dao.insert(conn, "TF_B_CTRM_TLIST", data);
	}
	
	public void insertIntoCtrmOrder(DBConnection conn,IData data) throws Exception
	{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		dao.insert(conn, "TF_B_CTRM_ORDER", data);
	}
	
	public void insertIntoCtrmOrderProduct(DBConnection conn,IData data) throws Exception
	{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		dao.insert(conn, "TF_B_CTRM_ORDER_PRODUCT", data);
	}
	
	public void insertIntoCtrmOrderAttr(DBConnection conn,IData data) throws Exception
	{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		dao.insert(conn, "TF_B_CTRM_ORDER_ATTR", data);
	}
	
	/**
	 * 批量更新表数据
	 * @param updateOrder
	 * @throws Exception
	 */
	public void updateBatchInfo(String tabName,String sqlRef,IDataset params) throws Exception
	{
		Dao.executeBatchByCodeCode(tabName, sqlRef, params,Route.CONN_CRM_CEN);
	}

	/**
	 * 更新订单表中的状态
	 * @param updateOrder
	 * @throws Exception
	 */
	public void updateInfoById(IData param) throws Exception
	{
		Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST", "UPD_CTRM_ORDER_BYID", param,Route.CONN_CRM_CEN);
		
	}
	
	/**
	 * 更新订单表中的状态
	 * @param updateOrder
	 * @throws Exception
	 */
	public void updateInfoForTid(IData param) throws Exception
	{
		Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST","UPD_CTRM_ORDER_TID_OID", param,Route.CONN_CRM_CEN);
		
	}
	
	/**
	 * 填充字符串 例：数据库生成了5位的数字sequence，但构成流水号需要8位，那么可通过此方法，在左边补零
	 * 
	 * @param srcStr 原始字符串
	 * @param fillStr  要填充到原始字符串中的字符串
	 * @param totalLen 填充后的总长度
	 * @param leftFlag   左填充标志 为true在srcStr左边填充fillStr 为false在srcStr右边填充fillStr
	 * @return
	 */
	public  String fillStr(String srcStr, String fillStr, int totalLen,boolean leftFlag)
	{
		if (srcStr == null)
			return null;
		if (srcStr.length() > totalLen || fillStr == null
				|| fillStr.length() == 0)
			return srcStr;
		if (((totalLen - srcStr.length()) % fillStr.length()) != 0)
			return srcStr;
		String result = srcStr;
		
		int i = totalLen - srcStr.length();
		while (i > 0)
		{
			result = leftFlag ? (fillStr + result) : (result + fillStr);
			i = i - fillStr.length();
		}
		return result;
	}
	
	/**
	 * 订单退款同步
	 * @param input
	 * @throws Exception
	 */
	public IData refundOrderSynchro(IData input) throws Exception
	{
		IData data = new DataMap();
		data.put("X_RESULTCODE", "-1");
		data.put("X_RESULTINFO", "尊敬的客户，您好！您的合约返销失败。");
		data.put("SA_RESULTCODE", "-1");
		data.put("SA_RESULTINFO", "尊敬的客户，您好！您的合约返销失败。");
		//查询合约信息
		IData param = new DataMap();
		param.put("TID", input.getString("ORDER_ID"));
		param.put("OID", input.getString("ORDER_ID"));
		param.put("CTRM_PRODUCT_TYPE", "2");
		IDataset orderContractInfos = CSAppCall.call("SS.ShoppingOrderSVC.queryProOrderByCtrmType", param);
		if (IDataUtil.isNotEmpty(orderContractInfos))
		{
			//IData orderContractInfo = new DataMap();
			IData idOrderContractInfo = orderContractInfos.first();
			String strSerialNumber = idOrderContractInfo.getString("PHONE");
			String strPID = idOrderContractInfo.getString("PID");
			String strTradeID = idOrderContractInfo.getString("TRADE_ID");
			String strProductID = idOrderContractInfo.getString("PRODUCT_ID");
			String strPackageID = idOrderContractInfo.getString("PACKAGE_ID");
			
			data.put("SERIAL_NUMBER", strSerialNumber);
			data.put("RSP_TIME", SysDateMgr.getSysTime());
			
			//调终止营销活动流程,确定TRADE_ID不为空的时候才调用终止营销活动流程，否则认为营销活动还未执行.
			if(StringUtils.isNotBlank(strTradeID) && !"-1".equals(strTradeID))
			{				
				IData endActiveParam = new DataMap();
                endActiveParam.put("SERIAL_NUMBER", strSerialNumber);
                endActiveParam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
                endActiveParam.put("PRODUCT_ID", strProductID);
                endActiveParam.put("PACKAGE_ID", strPackageID);
                endActiveParam.put("RELATION_TRADE_ID", strTradeID);             
                endActiveParam.put("IS_RETURN", "0");
                endActiveParam.put("FORCE_END_DATE", SysDateMgr.getSysDate());
                endActiveParam.put("END_DATE_VALUE", "7"); //强制终止
                endActiveParam.put("TRADE_STAFF_ID", getVisit().getStaffId());
                endActiveParam.put("TRADE_DEPART_ID", getVisit().getDepartId());
                endActiveParam.put("TRADE_CITY_CODE", getVisit().getCityCode());
                endActiveParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                endActiveParam.put("REMARK", "取消一级自有电渠合约订单");
            	try
            	{
            		IDataset retnInfo = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
            		if (IDataUtil.isNotEmpty(retnInfo) && StringUtils.isBlank(retnInfo.first().getString("ORDER_ID"))) 
    				{
    					data.put("X_RESULTCODE", "-1");
    					data.put("X_RESULTINFO", "尊敬的客户，您好！您的合约返销失败。");
    					data.put("SA_RESULTCODE", "-1");
    					data.put("SA_RESULTINFO", retnInfo.first().getString("X_RESULTINFO"));
    				}
    				else
    				{
    					data.put("SA_TRADE_ID", retnInfo.first().getString("TRADE_ID"));
    					data.put("SA_ORDER_ID", retnInfo.first().getString("ORDER_ID"));
    					data.put("X_RESULTCODE", "0");
        				data.put("X_RESULTINFO", "尊敬的客户，您好！您的合约返销成功。");
    					data.put("SA_RESULTCODE", "0");
    					data.put("SA_RESULTINFO", "尊敬的客户，您好！您的合约返销成功。");
    				}
    			}
            	catch(Exception ex)
            	{
    				SessionManager.getInstance().rollback();
    				logger.error("lixm6refundOrderSynchro= "+ex.getMessage());
    				String error =  Utility.parseExceptionMessage(ex); 
    				data.put("X_RESULTCODE", "-1");
    				data.put("X_RESULTINFO", "尊敬的客户，您好！您的合约返销失败。");
    				data.put("SA_RESULTCODE", "-1");
    				data.put("SA_RESULTINFO", error);
    			}
			}
			
			String strXResultInfo = data.getString("SA_RESULTINFO", "尊敬的客户，您好！您的合约返销失败。");
			
			IData inputData = new DataMap();
			inputData.put("PID", strPID);
			inputData.put("TRADE_ID", strTradeID);
			inputData.put("ACCEPT_DATE", SysDateMgr.getSysDate());
			inputData.put("STATUS", "6");
			inputData.put("ACCEPT_RESULT", "6");
			inputData.put("REMARK", strXResultInfo);
			inputData.put("UPDATE_TIME", SysDateMgr.getSysDate());
			inputData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
			inputData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
			CSAppCall.call("SS.ShoppingOrderSVC.updCtrmOrderProduct", inputData);
		}
		return data;
	}
	
	public String getMessage(Exception e) 
	{
    	Throwable t = Utility.getBottomException(e);
    	String s = "";
    	if(t != null)
    	{
    		s = t.getMessage();
    	}

    	if(StringUtils.isNotBlank(s))
    	{
    		if(s.length() > 120)
    		{
    			s = s.substring(0, 120);
    		}
    	}
    	return s;
    }
	
	
	/**
	 * 通过中间映射表, 找到对应关系
	 * 
	 * @param pd
	 * @param goodsID
	 * @param checkID
	 * @return
	 * @throws Exception
	 */
	public IDataset getBossProByCtrmId( String bossID,String eparchyCode) throws Exception {
		IData ctrmId = new DataMap();
		ctrmId.put("CTRM_PRODUCT_ID", bossID);
		ctrmId.put("EPARCHY_CODE", eparchyCode);
		IDataset result = Dao.qryByCode("TD_B_CTRM_RELATION","SEL_BY_CTRM_PRODUCT_ID", ctrmId,Route.CONN_CRM_CEN);
		return result;
	}
	
	   /**
     * OID,TID,CTRM_PRODUCT_TYPE
     */
    public static IDataset queryProOrderBytradeType(String tradeID, String crmProductType) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeID);
        param.put("CTRM_PRODUCT_TYPE", crmProductType);
        return Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_PROORDER_BY_ORDERCTRMTYPE", param,Route.CONN_CRM_CEN);
    }
    
    /*
	 * 更新订单表
	 * @param updateOrder
	 * @throws Exception
	 */
	public void updateCmrOrderForTID(IData input) throws Exception
	{
		Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST","UPD_CTRM_ORDER", input, Route.CONN_CRM_CEN);
	}
}
