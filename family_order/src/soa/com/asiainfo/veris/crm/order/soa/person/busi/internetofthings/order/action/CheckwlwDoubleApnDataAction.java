package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;

/**
 * 
 * @author xurf3
 * 同时订购通用、专用APN时,校验是否填写订购业务唯一代码值或订购各自对应的20G以上流量套餐
 *
 */
public class CheckwlwDoubleApnDataAction implements ITradeAction {
	private static final Logger logger = Logger.getLogger(CheckwlwDoubleApnDataAction.class);			
	@SuppressWarnings("rawtypes")
	public void executeAction(BusiTradeData btd) throws Exception {
		BaseReqData reqData = btd.getRD();
		UcaData uca = reqData.getUca();
		String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
		if (!"PWLW".equals(uca.getBrandCode())) {
			return;
		}
		if ("110".equals(tradeTypeCode)) {// 产品变更
        	//校验双APN
			checkPwlwDoubleApn(uca,btd);
		}
		if ("10".equals(tradeTypeCode)) {// 物联网开户
        	//校验双APN
			checkPwlwDoubleApn(uca,btd);
		} 
	}
	
	//同时订购通用、专用APN时,校验是否填写订购业务唯一代码值或订购各自对应的20G以上流量套餐
	public void checkPwlwDoubleApn(UcaData uca,BusiTradeData btd) throws Exception
    {	
		List<SvcTradeData> userSvcs = uca.getUserSvcs();
		List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
    	//用于办理的APN数量统计
    	int countApn = 0;
    	//用于校验通用服务的订购业务唯一代码是否正确（ServiceCode）
    	boolean checkServiceCode_0 = false;
    	//用于校验专用服务的订购业务唯一代码是否正确
    	boolean checkServiceCode_1 = false;
    	//通用服务的订购业务唯一代码值
    	String serviceCodeValue_0 = "";
    	//专用服务的订购业务唯一代码值
    	String serviceCodeValue_1 = "";
        	
        if (userSvcs != null && userSvcs.size() > 0)
        {
        	for (int i = 0; i < userSvcs.size(); i++) 
        	{	
        		if(countApn == 2)
        		{
        			break;
        		}
        		SvcTradeData userSvcItem = userSvcs.get(i);
        		String strSvcID = userSvcItem.getElementId();
            	IDataset compara9023 =BreQryForCommparaOrTag.getCommpara("CSM",9023,strSvcID,"ZZZZ");
            	if(IDataUtil.isNotEmpty(compara9023)  && compara9023.size()>0 && !"1".equals(userSvcItem.getModifyTag()))
            	{	
            		countApn = countApn + 1;
            		String apnFlag = ((IData)compara9023.get(0)).getString("PARA_CODE1");
            		String serviceCodeValue = ((IData)compara9023.get(0)).getString("PARA_CODE4");
            		List<AttrTradeData> attrs  = uca.getUserAttrs();
                    if (attrs != null && attrs.size() > 0)
                    {
                    	for (int j = 0; j < attrs.size(); j++)
                        {
                    		AttrTradeData attr = attrs.get(j);
                    		String strAttrCode = attr.getAttrCode();
                    		String strAttrValue = attr.getAttrValue();
                    		String elementId = attr.getElementId();
                    		String strAttrModifyTag = attr.getModifyTag();
                    		
                    		if("1".equals(strAttrModifyTag)){
                    			continue;
                    		}
                    		
                    		if("0".equals(apnFlag) && strSvcID.equals(elementId) 
                    				&& "ServiceCode".equals(strAttrCode) && strAttrValue != null)
                    		{
                    			serviceCodeValue_0 = strAttrValue;
                    		}else if("1".equals(apnFlag) && strSvcID.equals(elementId) 
                    				&& "ServiceCode".equals(strAttrCode) && strAttrValue != null)
                    		{
                    			serviceCodeValue_1 = strAttrValue;
                    		}

                    		if(strSvcID.equals(elementId) && "0".equals(apnFlag) 
                    				&& "ServiceCode".equals(strAttrCode) && serviceCodeValue.equals(strAttrValue))
                    		{
                    			serviceCodeValue_0 = serviceCodeValue;
                    			checkServiceCode_0 = true;
                    			break;
                    		}
                    		
                    		if(strSvcID.equals(elementId) && "1".equals(apnFlag) 
                    				&& "ServiceCode".equals(strAttrCode) && serviceCodeValue.equals(strAttrValue))
                    		{
                    			serviceCodeValue_1 = serviceCodeValue;
                    			checkServiceCode_1 = true;
                    			break;
                    		}
                        }
                    	
                    }
            	}
			}
        }
        
        logger.debug("---serviceCodeValue_0 value is---serviceCodeValue_0="+serviceCodeValue_0);
        logger.debug("---serviceCodeValue_1 value is---serviceCodeValue_1="+serviceCodeValue_1);
        
        if(countApn == 2){
        	
        	 if (userSvcs != null && userSvcs.size() > 0)
             {
             	for (int i = 0; i < userSvcs.size(); i++) 
             	{	
             		SvcTradeData userSvcItem = userSvcs.get(i);
             		String strSvcID = userSvcItem.getElementId();
                 	IDataset compara9023 =BreQryForCommparaOrTag.getCommpara("CSM",9023,strSvcID,"ZZZZ");
                 	if(IDataUtil.isNotEmpty(compara9023)  && compara9023.size()>0 && !"1".equals(userSvcItem.getModifyTag()))
                 	{	
                 		List<AttrTradeData> attrs  = uca.getUserAttrs();
                 		boolean servcodeFlag = false;
     					boolean opertypeFlag = false;
     					boolean stateFlag = false;
     					boolean serviceBillingTypeFlag = false;
                         if (attrs != null && attrs.size() > 0)
                         {
                         	for (int j = 0; j < attrs.size(); j++)
                            {
                         		AttrTradeData attr = attrs.get(j);
                         		String strAttrCode = attr.getAttrCode();
                         		String strAttrValue = attr.getAttrValue();
                         		String elementId = attr.getElementId();
                         		String strAttrModifyTag = attr.getModifyTag();
                         		
         						if("0".equals(strAttrModifyTag) || "1".equals(strAttrModifyTag))
         						{
         							if(strSvcID.equals(elementId))
         							{
         								if("ServiceCode".equals(strAttrCode) && StringUtils.isNotBlank(strAttrValue))
         								{
         									servcodeFlag = true;
         								}
         								if("OperType".equals(strAttrCode) && StringUtils.isNotBlank(strAttrValue))
         								{
         									opertypeFlag = true;
         								}
         								if("ServiceUsageState".equals(strAttrCode) && StringUtils.isNotBlank(strAttrValue))
         								{
         									stateFlag = true;
         								}
         								if("ServiceBillingType".equals(strAttrCode) && StringUtils.isNotBlank(strAttrValue))
         								{
         									serviceBillingTypeFlag = true;
         								}
         								
         							}
         						}
                             }
                         	
                         	if(servcodeFlag || opertypeFlag || stateFlag || serviceBillingTypeFlag)
         					{
         						int nCount = 0;
         						List<AttrTradeData> lsServiceCode = uca.getUserAttrsByAttrCode("ServiceCode");
         						if(lsServiceCode.size() > 0 && lsServiceCode != null)
         						{
         							for (int k = 0; k < lsServiceCode.size(); k++) 
         							{
         								AttrTradeData atServiceCode = lsServiceCode.get(k);
         								String strMT = atServiceCode.getModifyTag();
         								String strAttrValue = atServiceCode.getAttrValue();
         								String strElementId = atServiceCode.getElementId();
         								if("0".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
         								{
         									servcodeFlag = true;
         									break;
         								}
         								else if("1".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
         								{
         									servcodeFlag = false;
         									nCount++;
         								}
         								else if(strSvcID.equals(strElementId))
         								{
         									servcodeFlag = true;
         								}
         							}
         						}
         						List<AttrTradeData> lsOperType = uca.getUserAttrsByAttrCode("OperType");
         						if(lsOperType.size() > 0 && lsOperType != null)
         						{
         							for (int k = 0; k < lsOperType.size(); k++) 
         							{
         								AttrTradeData atOperType = lsOperType.get(k);
         								String strMT = atOperType.getModifyTag();
         								String strAttrValue = atOperType.getAttrValue();
         								String strElementId = atOperType.getElementId();
         								if("0".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
         								{
         									opertypeFlag = true;
         									break;
         								}
         								else if("1".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
         								{
         									opertypeFlag = false;
         									nCount++;
         								}
         								else if(strSvcID.equals(strElementId))
         								{
         									opertypeFlag = true;
         								}
         							}
         						}
         						List<AttrTradeData> lsServiceUsageState = uca.getUserAttrsByAttrCode("ServiceUsageState");
         						if(lsServiceUsageState.size() > 0 && lsServiceUsageState != null)
         						{
         							for (int k = 0; k < lsServiceUsageState.size(); k++) 
         							{
         								AttrTradeData atServiceUsageState = lsServiceUsageState.get(k);
         								String strMT = atServiceUsageState.getModifyTag();
         								String strAttrValue = atServiceUsageState.getAttrValue();
         								String strElementId = atServiceUsageState.getElementId();
         								if("0".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
         								{
         									stateFlag = true;
         									break;
         								}
         								else if("1".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
         								{
         									stateFlag = false;
         									nCount++;
         								}
         								else if(strSvcID.equals(strElementId))
         								{
         									stateFlag = true;
         								}
         							}
         						}
         						
         						List<AttrTradeData> lsServiceBillingType = uca.getUserAttrsByAttrCode("ServiceBillingType");
         						if(lsServiceBillingType.size() > 0 && lsServiceBillingType != null)
         						{
         							for (int k = 0; k < lsServiceBillingType.size(); k++) 
         							{
         								AttrTradeData atServiceBillingType = lsServiceBillingType.get(k);
         								String strMT = atServiceBillingType.getModifyTag();
         								String strAttrValue = atServiceBillingType.getAttrValue();
         								String strElementId = atServiceBillingType.getElementId();
         								if("0".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
         								{
         									serviceBillingTypeFlag = true;
         									break;
         								}
         								else if("1".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
         								{
         									serviceBillingTypeFlag = false;
         									nCount++;
         								}
         								else if(strSvcID.equals(strElementId))
         								{
         									serviceBillingTypeFlag = true;
         								}
         							}
         						}
         						
         						if((!servcodeFlag || !opertypeFlag || !stateFlag || !serviceBillingTypeFlag) && (nCount != 4))
         						{
         							CSAppException.apperr(CrmCommException.CRM_COMM_103,"若订购通用服务和专用服务双APN，对应的属性值:订购业务唯一代码、操作类型、计费方式、业务配额状态只能全部不为空或者全部为空！");
         						}
         					}
                         }
                 	}
     			}
             }
        	
			if(!"".equals(serviceCodeValue_0) && !"".equals(serviceCodeValue_1))
			{
				if(!checkServiceCode_0)
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "通用服务的订购业务唯一代码不正确！！！");
				}
				if(!checkServiceCode_1)
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "专用服务的订购业务唯一代码不正确！！！");
				}
			}else{
				//订购20G以上流量统计
		    	int countDiscnt = 0;
		    	//校验是否订购20G以上通用流量
		    	boolean checkDiscnt_0 = false;
		    	//校验是否订购20G以上专用流量
		    	boolean checkDiscnt_1 = false;
		    	
		    	if(userDiscnts != null && userDiscnts.size() > 0)
		    	{
		    		for (int i = 0; i < userDiscnts.size(); i++) 
		        	{	
		        		if(countDiscnt == 2)
		        		{
		        			break;
		        		}
		        		DiscntTradeData userDiscntItem = userDiscnts.get(i);
		        		String discntCode = userDiscntItem.getDiscntCode();
		            	IDataset compara9024 =BreQryForCommparaOrTag.getCommpara("CSM",9024,discntCode,"ZZZZ");
		            	if(IDataUtil.isNotEmpty(compara9024)  && compara9024.size()>0 && !"1".equals(userDiscntItem.getModifyTag()))
		            	{	
		            		countDiscnt = countDiscnt + 1;
		            		String discntFlag = ((IData)compara9024.get(0)).getString("PARA_CODE1");
		            		if("0".equals(discntFlag))
		            		{
		            			checkDiscnt_0 = true;
		            		}else if("1".equals(discntFlag))
		            		{
		            			checkDiscnt_1 = true;
		            		}
		            	}
					}
		    	}
		    	
		    	if(!checkDiscnt_0 || !checkDiscnt_1)
		    	{
		    		CSAppException.apperr(CrmCommException.CRM_COMM_103, 
		    				"若订购通用服务和专用服务双APN，请订购20G以上的流量套餐或者填写对应的属性值（订购业务唯一代码，操作类型，计费方式，配额状态）!!!");
		    	}
		    	
			}
		}
        
    }
}
