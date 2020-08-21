package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.WlwGrpBusiUtils;

public class ChkServiceCodeAttrCodeForWlwMeb extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkServiceCodeAttrCodeForWlwMeb.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkServiceCodeAttrCodeForWlwMeb() >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */

        String productId = databus.getString("PRODUCT_ID");// 集团产品
        String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        //
        if(!"20161124".equals(productId) && !"20005015".equals(productId) 
        		&& !"20005013".equals(productId) && !"20161122".equals(productId))
        {
        	return true;
        }
        
        IDataset userElements = null;
        String userElementsStr = "";
        String subTransCode = databus.getString("X_SUBTRANS_CODE","");
        
        
        //批量进来的
        if(StringUtils.isNotBlank(subTransCode) 
        		&& StringUtils.equals(subTransCode, "GrpBat"))
        {
            userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
            if (StringUtils.isNotBlank(userElementsStr))
            {
            	userElements = new DatasetList(userElementsStr);
            }            
        }
        else 
        {
            userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
            
            if(StringUtils.isBlank(userElementsStr))
            {
            	userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
            }
            
            if (StringUtils.isNotBlank(userElementsStr))
            {
            	userElements = new DatasetList(userElementsStr);
            }
        }
        				
        IDataset configResult = new DatasetList();
        
        IData attrData = new DataMap();
        IDataset discntLists = new DatasetList();
        IDataset svcDelList = new DatasetList();
        
        if (StringUtils.isNotBlank(userElementsStr))
        {
        	userElements = new DatasetList(userElementsStr);
        	if(IDataUtil.isNotEmpty(userElements))
        	{
        		IData configDatas = WlwGrpBusiUtils.loadConfigPolicyData();
        		
        		int size = userElements.size();
        		for (int i = 0; i < size; i++)
                {
        			IData element = userElements.getData(i);
        			String eleTypeCode = element.getString("ELEMENT_TYPE_CODE","");
        			String modifyTag = element.getString("MODIFY_TAG","");
        			String elementId = element.getString("ELEMENT_ID","");
        			String packageId = element.getString("PACKAGE_ID","");
        			
        			if((BofConst.MODIFY_TAG_ADD.equals(modifyTag))
        					&& BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode))
        			{
        				IData configData = configDatas.getData(elementId);
        				if(IDataUtil.isNotEmpty(configData))
        				{
        					configResult.add(configData);
        					attrData.put(elementId, element.getString("ATTR_PARAM",""));
        				}
        			}
        			else if((BofConst.MODIFY_TAG_ADD.equals(modifyTag))
        					&& BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode))
        			{
        				discntLists.add(element);
        			}
        			else if((BofConst.MODIFY_TAG_DEL.equals(modifyTag))
        					&& BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode))
        			{
        				svcDelList.add(element);
        			}
                }
        	}
        }
        
        //校验策略服务属性的完整性
        boolean svcCheck = this.checkSvcAttrCode(userElements,databus,errCode);
        if(svcCheck)
        {
        	return false;
        }
        
        if("20005013".equals(productId) || "20161124".equals(productId) || "20161122".equals(productId))
        {
        	//成员新增时,有双策略服务,则校验对应的属性有没有填写
        	if(IDataUtil.isNotEmpty(configResult) && configResult.size() == 2)
        	{
        		for (int i = 0; i < configResult.size(); i++) 
        		{
        			IData config = configResult.getData(i);
        			String keyObj = config.getString("PARA_CODE1");
					
					String attrParamStr = attrData.getString(keyObj,"");
					if(StringUtils.isNotBlank(attrParamStr))
					{
						//获取对应的属性
	        			IDataset attrDataset = null;
						attrDataset = new DatasetList(attrParamStr);
						
						boolean attrFlag = this.checkAttrCode(attrDataset);
						if(!attrFlag)
						{
							boolean discntFlag = false;
							discntFlag = this.checkDiscnt20GCode(keyObj,discntLists);
							if(!discntFlag)
							{
								err = "开通双策略,必须填写双策略服务的属性或者选择20G以上的套餐!否则业务不能继续办理!";
		            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
		            			return false;
							}
							
						}
					}
        		}
        		
        	}
        	//成员新增时,有策略服务,则校验对应用户原来的策略服务属性有没有填写，
        	//另外还要校验新增的服务对应的策略有没有填写
        	else if(IDataUtil.isNotEmpty(configResult) && configResult.size() == 1)
        	{
        		IDataset userInfos = UserSvcInfoQry.queryUserSvcPolicyByUserId(userIdB);
        		
        		//剔除掉当前业务删除的服务
        		if(IDataUtil.isNotEmpty(svcDelList) && IDataUtil.isNotEmpty(userInfos)){
        			for(int h =0; h < svcDelList.size(); h++)
            		{
        				IData svcDelInfo = svcDelList.getData(h);
        				String delSvcId = svcDelInfo.getString("ELEMENT_ID","");
        				if(IDataUtil.isNotEmpty(userInfos)){
        					for(int j = 0; j < userInfos.size(); j++){
        						IData userInfo = userInfos.getData(j);
        						String userSvcId = userInfo.getString("SERVICE_ID","");
        						if(delSvcId.equals(userSvcId) && !"".equals(delSvcId) && !"".equals(userSvcId)){
        							userInfos.remove(j);
        							j--;
        						}
            				}
        				}
            		}
        		}
        		
        		if(IDataUtil.isNotEmpty(userInfos))
        		{
        			//先校验用户原来服务的策略属性是否填写
        			for(int i = 0; i < userInfos.size(); i++)
        			{
        				IData userInfo = userInfos.getData(i);
        				String instId = userInfo.getString("INST_ID","");
        				String serviceId = userInfo.getString("SERVICE_ID","");
        				IDataset userAttrInfos = UserAttrInfoQry.getUserAttrByInstID(userIdB,instId);
        				
        				boolean attrFlag = this.checkUserAttrCode(userAttrInfos);
        				if(!attrFlag)
						{
        					//策略属性没有填写,才校验20G以上的套餐
        					boolean discntFlag = false;
        					discntFlag = this.checkUserDiscntCode(serviceId, userIdB);
        					if(!discntFlag)
        					{
        						err = "开通双策略,用户原服务未填写策略属性或用户原来未订购20G以上的套餐!业务不能继续1!";
    	            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
    	            			return false;
        					}
        					
        					//下面写法逻辑暂时先不用
        					//boolean tradeDiscntFlag = false;
        					//tradeDiscntFlag = this.checkDiscnt20GCode(serviceId,discntLists);
        					//if((!discntFlag) && (!tradeDiscntFlag))
        					//{
        					//	err = "开通双策略,用户原服务未填写策略属性或用户原来未订购20G以上的套餐!业务不能继续2!";
    	            		//	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
    	            		//	return false;
        					//}
						} 
        				//else 
        				//{
        				//	boolean discntFlag = false;
        				//	discntFlag = this.checkUserDiscntCode(serviceId, userIdB);
        				//	if(!discntFlag)
        				//	{
        				//		err = "开通双策略,用户原服务未填写策略属性或用户原来未订购20G以上的套餐!业务不能继续3!";
    	            	//		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
    	            	//		return false;
        				//	}
        				//}
        			}
        			
        			//再校验订购的
        			for (int i = 0; i < configResult.size(); i++) 
            		{
            			IData config = configResult.getData(i);
            			String keyObj = config.getString("PARA_CODE1");
    					
    					String attrParamStr = attrData.getString(keyObj,"");
    					if(StringUtils.isNotBlank(attrParamStr))
    					{
    						//获取对应的属性
    	        			IDataset attrDataset = null;
    						attrDataset = new DatasetList(attrParamStr);
    						
    						boolean attrFlag = this.checkAttrCode(attrDataset);
    						if(!attrFlag)
    						{
    							boolean discntFlag = false;
    							discntFlag = this.checkDiscnt20GCode(keyObj,discntLists);
    							if(!discntFlag)
    							{
    								err = "开通双策略,必须填写双策略服务的属性或者选择20G以上的套餐!否则业务不能继续4!";
        	            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
        	            			return false;
    							}
    							
    						}
    					}
            		}
        			
        		}
        	}
        }        
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ChkServiceCodeAttrCodeForWlwMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}

	/**
	 * 
	 * @param attrDataset
	 * @return
	 * @throws Exception
	 */
	private boolean checkAttrCode(IDataset attrDataset) throws Exception
	{
		if(IDataUtil.isNotEmpty(attrDataset))
		{
			String serviceCode = "";
			String operType = "";
			String serviceUsageState = "";
			String serviceBillingType  = "";
			String serviceStartDateTime = "";
			String serviceEndDateTime = "";
			for (int j = 0; j < attrDataset.size(); j++) 
			{
				IData attrParam = attrDataset.getData(j);
				String attrCode = attrParam.getString("ATTR_CODE","");
				String attrValue = attrParam.getString("ATTR_VALUE","");
				if("ServiceCode".equals(attrCode))
				{
					serviceCode = attrValue;
				}
				else if("OperType".equals(attrCode))
				{
					operType = attrValue;
				}
				else if("ServiceUsageState".equals(attrCode))
				{
					serviceUsageState = attrValue;
				}
				else if("ServiceBillingType".equals(attrCode))
				{
					serviceBillingType = attrValue;
				}
				else if("ServiceStartDateTime".equals(attrCode))
				{
					serviceStartDateTime = attrValue;
				}
				else if("ServiceEndDateTime".equals(attrCode))
				{
					serviceEndDateTime = attrValue;
				}
			}
			if(StringUtils.isBlank(serviceCode) || StringUtils.isBlank(operType) ||
					StringUtils.isBlank(serviceUsageState) || StringUtils.isBlank(serviceBillingType))
			{
    			return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param userAttrInfos
	 * @return
	 * @throws Exception
	 */
	private boolean checkUserAttrCode(IDataset userAttrInfos) throws Exception
	{
		if(IDataUtil.isNotEmpty(userAttrInfos))
		{
			String serviceCode = "";
			String operType = "";
			String serviceUsageState = "";
			String serviceBillingType  = "";
			String serviceStartDateTime = "";
			String serviceEndDateTime = "";
			for(int j = 0; j < userAttrInfos.size(); j++)
			{
				IData userAttr = userAttrInfos.getData(j);
				String attrCode = userAttr.getString("ATTR_CODE","");
				String attrValue = userAttr.getString("ATTR_VALUE","");
				if("ServiceCode".equals(attrCode))
				{
					serviceCode = attrValue;
				}
				else if("OperType".equals(attrCode))
				{
					operType = attrValue;
				}
				else if("ServiceUsageState".equals(attrCode))
				{
					serviceUsageState = attrValue;
				}
				else if("ServiceBillingType".equals(attrCode))
				{
					serviceBillingType = attrValue;
				}
				else if("ServiceStartDateTime".equals(attrCode))
				{
					serviceStartDateTime = attrValue;
				}
				else if("ServiceEndDateTime".equals(attrCode))
				{
					serviceEndDateTime = attrValue;
				}
			}
			
			if(StringUtils.isBlank(serviceCode) || StringUtils.isBlank(operType) ||
					StringUtils.isBlank(serviceUsageState) || StringUtils.isBlank(serviceBillingType))
			{
    			return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param keyObj
	 * @param discntLists
	 * @return
	 * @throws Exception
	 */
	private boolean checkDiscnt20GCode(String keyObj,IDataset discntLists) throws Exception
	{
		boolean discntFlag = false;
		//通用流量4G_GPRS服务,策略属性没有填写,校验一下是否有选择20G以上的套餐
		if("99011022".equals(keyObj) || "99011028".equals(keyObj) || "99011025".equals(keyObj)) 
		{
			if(IDataUtil.isNotEmpty(discntLists))
			{
				IData commpData = WlwGrpBusiUtils.loadConfigData("9024", "0");
				for (int k = 0; k < discntLists.size(); k++) 
				{
					IData discntData = discntLists.getData(k);
					String keyStr = discntData.getString("ELEMENT_ID","");
					if(IDataUtil.isNotEmpty(commpData))
					{
						IData keyData = commpData.getData(keyStr);
						if(IDataUtil.isNotEmpty(keyData))
						{
							discntFlag = true;//有订购20G套餐
							break;
						}
					}
				}
			}
		}
		//物联网专用数据通信服务(可选)
		else if("99011021".equals(keyObj) || "99011029".equals(keyObj) || "99011024".equals(keyObj))
		{
			if(IDataUtil.isNotEmpty(discntLists))
			{
				IData commpData = WlwGrpBusiUtils.loadConfigData("9024", "1");
				for (int k = 0; k < discntLists.size(); k++) 
				{
					IData discntData = discntLists.getData(k);
					String keyStr = discntData.getString("ELEMENT_ID","");
					if(IDataUtil.isNotEmpty(commpData))
					{
						IData keyData = commpData.getData(keyStr);
						if(IDataUtil.isNotEmpty(keyData))
						{
							discntFlag = true;//有订购20G套餐
							break;
						}
					}
				}
			}
		}
		
		return discntFlag;
	}
	
	/**
	 * 
	 * @param keyObj
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private boolean checkUserDiscntCode(String keyObj,String userId) throws Exception
	{
		boolean discntFlag = false;
		if(StringUtils.isNotBlank(keyObj) && StringUtils.isNotBlank(userId))
		{
			if("99011022".equals(keyObj) || "99011028".equals(keyObj))//通用
			{
				IDataset userInfos = UserDiscntInfoQry.getUser20GDiscntByUserId(userId,"0");
				if(IDataUtil.isNotEmpty(userInfos))
				{
					discntFlag = true;
				}
			}
			else if("99011021".equals(keyObj) || "99011029".equals(keyObj)) //专用
			{
				IDataset userInfos = UserDiscntInfoQry.getUser20GDiscntByUserId(userId,"1");
				if(IDataUtil.isNotEmpty(userInfos))
				{
					discntFlag = true;
				}
			}
		}
		return discntFlag;
	}
	
	
	/**
	 * 校验策略属性的完整性
	 * @param userElements
	 * @param databus
	 * @param errCode
	 * @return
	 * @throws Exception
	 */
	private boolean checkSvcAttrCode(IDataset userElements,IData databus,String errCode) throws Exception
	{
		boolean checkFlag = false;
		if(IDataUtil.isNotEmpty(userElements))
		{
			int size = userElements.size();
    		for (int i = 0; i < size; i++)
            {
    			IData element = userElements.getData(i);
    			String eleTypeCode = element.getString("ELEMENT_TYPE_CODE","");
    			String modifyTag = element.getString("MODIFY_TAG","");
    			String elementId = element.getString("ELEMENT_ID","");
    			if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode) &&
    					(BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag)))
    			{
    				if("99011021".equals(elementId) || "99011022".equals(elementId) 
    						|| "99011024".equals(elementId) || "99011025".equals(elementId)
    						|| "99011028".equals(elementId) || "99011029".equals(elementId))
    				{
    					IDataset attrDataset = null;
    					attrDataset = new DatasetList(element.getString("ATTR_PARAM",""));
    					
    					if(IDataUtil.isNotEmpty(attrDataset))
    					{
    						String serviceCode = "";
    						String operType = "";
    						String serviceUsageState = "";
    						String serviceBillingType  = "";
    						String serviceStartDateTime = "";
    						String serviceEndDateTime = "";
    						
    						int attrSize = attrDataset.size();
    		        		for (int j = 0; j < attrSize; j++)
    		                {
    		        			IData attr = attrDataset.getData(j);
    		        			String attrCode = attr.getString("ATTR_CODE","");
    		        			String attrValue = attr.getString("ATTR_VALUE","");
    		        			if("ServiceCode".equals(attrCode))
    		    				{
    		        				if(StringUtils.isNotBlank(attrValue))
    		        				{
    		        					serviceCode = attrValue;
    		        				}
    		    				}
    		    				else if("OperType".equals(attrCode))
    		    				{
    		    					if(StringUtils.isNotBlank(attrValue))
    		        				{
    		    						operType = attrValue;
    		        				}
    		    				}
    		    				else if("ServiceUsageState".equals(attrCode))
    		    				{
    		    					if(StringUtils.isNotBlank(attrValue))
    		        				{
    		    						serviceUsageState = attrValue;
    		        				}
    		    				}
    		    				else if("ServiceBillingType".equals(attrCode))
    		    				{
    		    					if(StringUtils.isNotBlank(attrValue))
    		        				{
    		    						serviceBillingType = attrValue;
    		        				}
    		    				}
    		    				else if("ServiceStartDateTime".equals(attrCode))
    		    				{
    		    					if(StringUtils.isNotBlank(attrValue))
    		        				{
    		    						serviceStartDateTime = attrValue;
    		        				}
    		    				}
    		    				else if("ServiceEndDateTime".equals(attrCode))
    		    				{
    		    					if(StringUtils.isNotBlank(attrValue))
    		        				{
    		    						serviceEndDateTime = attrValue;
    		        				}
    		    				}
    		                }
    		        		
    		        		if(StringUtils.isNotBlank(serviceCode) || StringUtils.isNotBlank(operType) 
    		        				|| StringUtils.isNotBlank(serviceUsageState) || StringUtils.isNotBlank(serviceBillingType) 
    		        				|| StringUtils.isNotBlank(serviceStartDateTime) || StringUtils.isNotBlank(serviceEndDateTime))
    		        		{
    		        			if(!(StringUtils.isNotBlank(serviceCode) && StringUtils.isNotBlank(operType) 
    		        					&& StringUtils.isNotBlank(serviceUsageState) && StringUtils.isNotBlank(serviceBillingType)))
    		        			{
    		        				String err = "请把该服务" + elementId + "策略的属性值填写完,订购业务唯一代码、操作类型、计费方式、配额状态!";
        	            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
        	            			checkFlag = true;
    		        			}
    		        		}
    					}
    					
    					if(checkFlag)
    					{
    						break;
    					}
    				}
    			}
            }
		}
		return checkFlag;
	}
}
