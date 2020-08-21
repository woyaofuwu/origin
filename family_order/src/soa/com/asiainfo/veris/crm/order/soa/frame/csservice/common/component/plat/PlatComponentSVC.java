
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.plat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MSpBizQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

public class PlatComponentSVC extends CSBizService
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 获取已经预约的视频会议
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getBookingViedoMeeting(IData param) throws Exception
    {
        return UserOtherInfoQry.getOtherInfoByCodeUserId(param.getString("USER_ID"), "V2CP");
    }

    public IDataset getPlatSvcByServiceId(IData param) throws Exception
    {
        //IDataset services = PlatSvcInfoQry.queryPlatSvcByServiceId(param.getString("SERVICE_ID"));
    	IDataset services = new DatasetList();
    	try{
    		services = UpcCall.querySpServiceAndInfoAndParamByServiceId(param.getString("SERVICE_ID"));
    	}catch(Exception e)
    	{
    		
    	}
        IData service = new DataMap();
        IDataset result = new DatasetList();
        IDataset attrs = new DatasetList();
        if (services != null && services.size() > 0)
        {
            int size = services.size();
            service = services.getData(0);
            for (int i = 0; i < size; i++)
            {
                IData data = services.getData(i);
                IData attr = new DataMap();
                if (!data.getString("FIELD_NAME", "").equals(""))
                {
                    attr.put("ATTR_CODE", data.getString("FIELD_NAME"));

                    if ("AIOBS_PASSWORD".equals(data.getString("FIELD_NAME")))
                    {
                        attr.put("ATTR_VALUE", PlatUtils.geneComplexRandomPassword());
                    }
                    else
                    {
                        attr.put("ATTR_VALUE", data.getString("ATTR_INIT_VALUE"));
                    }

                    attrs.add(attr);
                }
                data.put("SERVICE_NAME", data.getString("OFFER_NAME"));
            }
            service.remove("FIELD_NAME");
            if (attrs.size() > 0)
            {
                service.put("ATTR_PARAM", attrs);
            }
            service.put("BILL_TYPE", StaticUtil.getStaticValue("SPBIZ_BILL_TYPE", service.getString("BILL_TYPE")));
            result.add(service);
        }
        return result;
    }

    public IDataset getPlatSvcs(IData param) throws Exception
    {
        return MSpBizQry.queryQuickPlatSvcs(param.getString("BIZ_TYPE_CODE"), param.getString("RSRV_STR1"));
    }

    public IDataset getPlatSwitch(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        if (StringUtils.isBlank(userId))
        {
            return null;
        }
        else
        {
            return PlatSvcInfoQry.queryUserSwitch(userId);
        }
    }

    private IDataset getUserAttrByInstId(IDataset userAttrs, String instId)
    {
        IDataset temp = new DatasetList();
        int size = userAttrs.size();
        for (int i = 0; i < size; i++)
        {
            IData userAttr = userAttrs.getData(i);
            if (instId.equals(userAttr.getString("RELA_INST_ID")))
            {
                IData map = new DataMap();
                map.put("ATTR_CODE", userAttr.getString("ATTR_CODE"));
                map.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
                temp.add(map);
            }
        }
        return temp;
    }
    
    /**
     * 获取 TF_F_USER_PLATSVC.BIZ_STATE_CODE 为A-正常，N-暂停状态的平台业务 。 A-正常，N-暂停，E-退订，L-挂失
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getUserPlatSvcs1(IData param) throws Exception
    {
        if ("".equals(param.getString("USER_ID", "")))
        {
            return null;
        }
        IDataset userPlatSvcs = PlatSvcInfoQry.queryUserPlatSvcs1(param.getString("USER_ID"));
		IDataset userPlatSvcList = new DatasetList();
        if(IDataUtil.isNotEmpty(userPlatSvcs)){
        	for(int i = 0 ; i < userPlatSvcs.size() ; i++){
        		IData userPlatSvc = userPlatSvcs.getData(i);
    			userPlatSvc.put("BIZ_TYPE_CODE", "");
    			userPlatSvc.put("ORG_DOMAIN", "");
    			IDataset offerDataList = new DatasetList();
    			try{
    				offerDataList = UpcCall.querySpComprehensiveInfoByServiceId(userPlatSvc.getString("SERVICE_ID",""));
    			}catch(Exception e){
    				
    			}
        		
        		if(IDataUtil.isNotEmpty(offerDataList)){
        			for(int j = 0 ; j < offerDataList.size() - 1; j++){
        				IData userPlatSvccloce = new DataMap(userPlatSvc);
        				userPlatSvccloce.put("SERVICE_NAME", offerDataList.getData(j).getString("OFFER_NAME",""));
        				userPlatSvccloce.putAll(offerDataList.getData(j));
        				userPlatSvccloce.put("BIZ_TYPE_CODE",  offerDataList.getData(j).getString("BIZ_TYPE_CODE",""));
        				userPlatSvccloce.put("ORG_DOMAIN",  offerDataList.getData(j).getString("ORG_DOMAIN",""));
        				userPlatSvcList.add(userPlatSvccloce);
        			}
        			userPlatSvc.put("SERVICE_NAME", offerDataList.getData(offerDataList.size() - 1).getString("OFFER_NAME",""));
        			userPlatSvc.putAll(offerDataList.getData(offerDataList.size() - 1));
        			userPlatSvc.put("BIZ_TYPE_CODE",  offerDataList.getData(offerDataList.size() - 1).getString("BIZ_TYPE_CODE",""));
        			userPlatSvc.put("ORG_DOMAIN",  offerDataList.getData(offerDataList.size() - 1).getString("ORG_DOMAIN",""));
        		}else{
        			userPlatSvcs.remove(i);
        			i--;
        		}
        	}
			userPlatSvcs.addAll(userPlatSvcList);
        }
        IDataset platSvcResult = addProperty(param, userPlatSvcs);
        if(IDataUtil.isNotEmpty(platSvcResult)){
        	for(int i = 0 ; i < platSvcResult.size() ; i++){
        		if(IDataUtil.isEmpty(platSvcResult.getData(i))){
        			platSvcResult.remove(i);
        			i = 0;
        		}else{
	             	if("98001901".equals(platSvcResult.getData(i).getString("SERVICE_ID")) && "19".equals(platSvcResult.getData(i).getString("BIZ_TYPE_CODE"))){
	                 	IDataset DiscntsCode = UserDiscntInfoQry.queryDiscntsCodeByusrid(param.getString("USER_ID"));
	                 	if(!IDataUtil.isEmpty(DiscntsCode)){
	                 		String Code = DiscntsCode.getData(0).getString("DISCNT_CODE");
	                 		if("1237".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "0.0"); 
	                 		}else if("1238".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "5.0");
	                 		}else if("12789".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "6.0");
	                 		}
	                 	}
	                 }
        		}
        	}
        }
        
        return platSvcResult;
    }
    
    public IDataset getUserPlatSvcs11(IData param) throws Exception
    {
        if ("".equals(param.getString("USER_ID", "")))
        {
            return null;
        }
        IDataset userPlatSvcs = PlatSvcInfoQry.queryUserPlatSvcs1(param.getString("USER_ID"));
		IDataset userPlatSvcList = new DatasetList();
        if(IDataUtil.isNotEmpty(userPlatSvcs)){
        	for(int i = 0 ; i < userPlatSvcs.size() ; i++){
        		IData userPlatSvc = userPlatSvcs.getData(i);
    			userPlatSvc.put("BIZ_TYPE_CODE", "");
    			userPlatSvc.put("ORG_DOMAIN", "");
    			IDataset offerDataList = new DatasetList();
    			try{
    				offerDataList = UpcCall.querySpComprehensiveInfoByServiceId(userPlatSvc.getString("SERVICE_ID",""));
    				//System.out.println("----------getUserPlatSvcs1-----------"+offerDataList);
    			}catch(Exception e){
    				
    			}
        		
        		if(IDataUtil.isNotEmpty(offerDataList)){
        			userPlatSvc.put("SERVICE_NAME", offerDataList.getData(0).getString("OFFER_NAME",""));
        			userPlatSvc.putAll(offerDataList.getData(0));
        			userPlatSvc.put("BIZ_TYPE_CODE",  offerDataList.getData(0).getString("BIZ_TYPE_CODE",""));
        			userPlatSvc.put("ORG_DOMAIN",  offerDataList.getData(0).getString("ORG_DOMAIN",""));
        			userPlatSvc.put("SP_NAME", offerDataList.getData(0).getString("SP_NAME", ""));
        		}else{
        			userPlatSvcs.remove(i);
        			i--;
        		}
        	}
			userPlatSvcs.addAll(userPlatSvcList);
        }
        IDataset platSvcResult = addProperty(param, userPlatSvcs);
        if(IDataUtil.isNotEmpty(platSvcResult)){
        	for(int i = 0 ; i < platSvcResult.size() ; i++){
        		if(IDataUtil.isEmpty(platSvcResult.getData(i))){
        			platSvcResult.remove(i);
        			i = 0;
        		}else{
        			if("98001901".equals(platSvcResult.getData(i).getString("SERVICE_ID")) && "19".equals(platSvcResult.getData(i).getString("BIZ_TYPE_CODE"))){
	                 	IDataset DiscntsCode = UserDiscntInfoQry.queryDiscntsCodeByusrid(param.getString("USER_ID"));
	                 	if(!IDataUtil.isEmpty(DiscntsCode)){
	                 		String Code = DiscntsCode.getData(0).getString("DISCNT_CODE");
	                 		if("1237".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "0.0"); 
	                 		}else if("1238".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "5.0");
	                 		}else if("12789".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "6.0");
	                 		}
	                 	}
	                 }
        		}
        	}
        }
        
        return platSvcResult;
    }
    
    /**
     * 获取 TF_F_USER_PLATSVC.BIZ_STATE_CODE 为E-退订 。 A-正常，N-暂停，E-退订，L-挂失
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getUserPlatSvcs2(IData param) throws Exception
    {
        if ("".equals(param.getString("USER_ID", "")))
        {
            return null;
        }
        IDataset userPlatSvcs = PlatSvcInfoQry.queryUserPlatSvcs2(param.getString("USER_ID"));
        IDataset userPlatSvcList = new DatasetList();
        if(IDataUtil.isNotEmpty(userPlatSvcs)){
        	for(int i = 0 ; i < userPlatSvcs.size() ; i++){
        		IData userPlatSvc = userPlatSvcs.getData(i);
    			userPlatSvc.put("BIZ_TYPE_CODE", "");
    			userPlatSvc.put("ORG_DOMAIN", "");
    			IDataset offerDataList = new DatasetList();
    			try{
    				offerDataList = UpcCall.querySpComprehensiveInfoByServiceId(userPlatSvc.getString("SERVICE_ID",""));
    			}catch(Exception e){
    				
    			}
        		
        		if(IDataUtil.isNotEmpty(offerDataList)){
        			for(int j = 0 ; j < offerDataList.size() - 1; j++){
        				IData userPlatSvccloce = new DataMap(userPlatSvc);
        				userPlatSvccloce.put("SERVICE_NAME", offerDataList.getData(j).getString("OFFER_NAME",""));
        				userPlatSvccloce.putAll(offerDataList.getData(j));
        				userPlatSvccloce.put("BIZ_TYPE_CODE",  offerDataList.getData(j).getString("BIZ_TYPE_CODE",""));
        				userPlatSvccloce.put("ORG_DOMAIN",  offerDataList.getData(j).getString("ORG_DOMAIN",""));
        				userPlatSvcList.add(userPlatSvccloce);
        			}
        			userPlatSvc.put("SERVICE_NAME", offerDataList.getData(offerDataList.size() - 1).getString("OFFER_NAME",""));
        			userPlatSvc.putAll(offerDataList.getData(offerDataList.size() - 1));
        			userPlatSvc.put("BIZ_TYPE_CODE",  offerDataList.getData(offerDataList.size() - 1).getString("BIZ_TYPE_CODE",""));
        			userPlatSvc.put("ORG_DOMAIN",  offerDataList.getData(offerDataList.size() - 1).getString("ORG_DOMAIN",""));
        		}else{
        			userPlatSvcs.remove(i);
        			i--;
        		}
        	}
			userPlatSvcs.addAll(userPlatSvcList);
        }
        IDataset platSvcResult = addProperty(param, userPlatSvcs);
        if(IDataUtil.isNotEmpty(platSvcResult)){
        	for(int i = 0 ; i < platSvcResult.size() ; i++){
        		if(IDataUtil.isEmpty(platSvcResult.getData(i))){
        			platSvcResult.remove(i);
        			i = 0;
        		}else{
	             	if("98001901".equals(platSvcResult.getData(i).getString("SERVICE_ID")) && "19".equals(platSvcResult.getData(i).getString("BIZ_TYPE_CODE"))){
	                 	IDataset DiscntsCode = UserDiscntInfoQry.queryDiscntsCodeByusrid(param.getString("USER_ID"));
	                 	if(!IDataUtil.isEmpty(DiscntsCode)){
	                 		String Code = DiscntsCode.getData(0).getString("DISCNT_CODE");
	                 		if("1237".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "0.0"); 
	                 		}else if("1238".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "5.0");
	                 		}else if("12789".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "6.0");
	                 		}
	                 	}
	                 }
        		}
        	}
        }
        return platSvcResult;
    }
    
    public IDataset getUserPlatSvcs12(IData param) throws Exception
    {
        if ("".equals(param.getString("USER_ID", "")))
        {
            return null;
        }
        IDataset userPlatSvcs = PlatSvcInfoQry.queryUserPlatSvcs2(param.getString("USER_ID"));
        IDataset userPlatSvcList = new DatasetList();
        if(IDataUtil.isNotEmpty(userPlatSvcs)){
        	for(int i = 0 ; i < userPlatSvcs.size() ; i++){
        		IData userPlatSvc = userPlatSvcs.getData(i);
    			userPlatSvc.put("BIZ_TYPE_CODE", "");
    			userPlatSvc.put("ORG_DOMAIN", "");
    			IDataset offerDataList = new DatasetList();
    			try{
    				offerDataList = UpcCall.querySpComprehensiveInfoByServiceId(userPlatSvc.getString("SERVICE_ID",""));
    			}catch(Exception e){
    				
    			}
        		
        		if(IDataUtil.isNotEmpty(offerDataList)){
        			userPlatSvc.put("SERVICE_NAME", offerDataList.getData(0).getString("OFFER_NAME",""));
        			userPlatSvc.putAll(offerDataList.getData(0));
        			userPlatSvc.put("BIZ_TYPE_CODE",  offerDataList.getData(0).getString("BIZ_TYPE_CODE",""));
        			userPlatSvc.put("ORG_DOMAIN",  offerDataList.getData(0).getString("ORG_DOMAIN",""));
        		}else{
        			userPlatSvcs.remove(i);
        			i--;
        		}
        	}
			userPlatSvcs.addAll(userPlatSvcList);
        }
        IDataset platSvcResult = addProperty(param, userPlatSvcs);
        if(IDataUtil.isNotEmpty(platSvcResult)){
        	for(int i = 0 ; i < platSvcResult.size() ; i++){
        		if(IDataUtil.isEmpty(platSvcResult.getData(i))){
        			platSvcResult.remove(i);
        			i = 0;
        		}else{
	             	if("98001901".equals(platSvcResult.getData(i).getString("SERVICE_ID")) && "19".equals(platSvcResult.getData(i).getString("BIZ_TYPE_CODE"))){
	                 	IDataset DiscntsCode = UserDiscntInfoQry.queryDiscntsCodeByusrid(param.getString("USER_ID"));
	                 	if(!IDataUtil.isEmpty(DiscntsCode)){
	                 		String Code = DiscntsCode.getData(0).getString("DISCNT_CODE");
	                 		if("1237".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "0.0"); 
	                 		}else if("1238".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "5.0");
	                 		}else if("12789".equals(Code)){
	                 			platSvcResult.getData(i).put("PRICE", "6.0");
	                 		}
	                 	}
	                 }
        		}
        	}
        }
        return platSvcResult;
    }

    public IDataset getUserPlatSvcs(IData param) throws Exception
    {
        if ("".equals(param.getString("USER_ID", "")))
        {
            return null;
        }
        IDataset userPlatSvcs = PlatSvcInfoQry.queryUserPlatSvcs(param.getString("USER_ID"));
        IDataset tempPlatSvcs = new DatasetList();
        for(int i = 0 ; i < userPlatSvcs.size() ; i ++)
        {
        	IData userPlatSvc = userPlatSvcs.getData(i);
        	IDataset upcDatas = new DatasetList();
        	try
            {
        		upcDatas = UpcCall.querySpComprehensiveInfoByServiceId(userPlatSvc.getString("SERVICE_ID"));
            }
            catch (Exception e)
            {
            	
            }
            
        	for(int j = 0 ; j < upcDatas.size() ; j ++)
        	{
        		IData tempPlatSvc = new DataMap();
        		IData upcData = upcDatas.getData(j);
        		upcData.put("SERVICE_NAME", upcData.getString("OFFER_NAME"));
        		upcData.put("ATTR_CODE", upcData.getString("FIELD_NAME"));
        		tempPlatSvc.putAll(userPlatSvc);
        		tempPlatSvc.putAll(upcData);
        		
        		tempPlatSvcs.add(tempPlatSvc);
        	}
        }
        IDataset platSvcResult = addProperty(param, tempPlatSvcs);
        return platSvcResult;
    }

    private IDataset addProperty(IData param, IDataset userPlatSvcs)
	    throws Exception {
	IDataset userAttrs = UserAttrInfoQry.queryUserAllAttrs(param.getString("USER_ID"));
        IDataset platSvcResult = new DatasetList();
        if (userPlatSvcs != null && userPlatSvcs.size() > 0)
        {
            String instId = "";
            int size = userPlatSvcs.size();
            IData tempPlatSvc = null;
            for (int i = 0; i < size; i++)
            {
                IData userPlatSvc = userPlatSvcs.getData(i);
                if (!instId.equals(userPlatSvc.getString("INST_ID")))
                {
                    if (tempPlatSvc != null)
                    {
                        platSvcResult.add(tempPlatSvc);
                        tempPlatSvc = null;
                    }
                    if ("".equals(userPlatSvc.getString("ATTR_CODE", "")))
                    {
                        userPlatSvc.remove("ATTR_CODE");
                        platSvcResult.add(userPlatSvc);
                    }
                    else
                    {
                        tempPlatSvc = userPlatSvc;
                        IDataset attrTempList = new DatasetList();
                        IData attrTemp = new DataMap();
                        attrTemp.put("ATTR_CODE", userPlatSvc.getString("ATTR_CODE"));
                        attrTemp.put("ATTR_VALUE", "");
                        attrTempList.add(attrTemp);
                        tempPlatSvc.remove("ATTR_CODE");
                        tempPlatSvc.put("ATTR_PARAM", attrTempList);
                        if (i == size - 1)
                        {
                            platSvcResult.add(tempPlatSvc);
                        }
                    }
                }
                else
                {
                    IData attrTemp = new DataMap();
                    attrTemp.put("ATTR_CODE", userPlatSvc.getString("ATTR_CODE"));
                    attrTemp.put("ATTR_VALUE", "");

                    if (tempPlatSvc != null && tempPlatSvc.getDataset("ATTR_PARAM") != null)
                    {
                        tempPlatSvc.getDataset("ATTR_PARAM").add(attrTemp);
                    }

                    if (i == size - 1)
                    {
                        platSvcResult.add(tempPlatSvc);
                    }
                }
                instId = userPlatSvc.getString("INST_ID");
            }
            size = platSvcResult.size();
            for (int i = 0; i < size; i++)
            {
                IData platSvc = platSvcResult.getData(i);
                if(platSvc!=null)
                {
                	 IDataset userElementAttrs = this.getUserAttrByInstId(userAttrs, platSvc.getString("INST_ID"));
                     IDataset attrResult = this.makeAttrs(userElementAttrs, platSvc.getDataset("ATTR_PARAM"));
                     if (attrResult != null && attrResult.size() > 0)
                     {
                         platSvc.put("ATTR_PARAM", attrResult);
                     }
                }
               
            }
        }
        return platSvcResult;
    }

    private IDataset makeAttrs(IDataset userAttrs, IDataset elementItemAList)
    {
        if (elementItemAList != null && elementItemAList.size() > 0)
        {
            int size = elementItemAList.size();
            IDataset returnAttrs = new DatasetList();
            for (int i = 0; i < size; i++)
            {
                IData attr = new DataMap();
                IData itemA = elementItemAList.getData(i);
                attr.put("ATTR_CODE", itemA.getString("ATTR_CODE"));
                if (userAttrs != null && userAttrs.size() > 0)
                {
                    int uSize = userAttrs.size();
                    for (int j = 0; j < uSize; j++)
                    {
                        IData userAttr = userAttrs.getData(j);
                        if (itemA.getString("ATTR_CODE").equals(userAttr.getString("ATTR_CODE")))
                        {
                            // xiekl add 对WLAN的密码不显示
                            String attrValue = userAttr.getString("ATTR_VALUE");
                            if ("AIOBS_PASSWORD".equals(userAttr.getString("ATTR_CODE")))
                            {
                                if (StringUtils.isBlank(attrValue))
                                {
                                    attrValue = PlatUtils.geneComplexRandomPassword();
                                }
                                else
                                {
                                    attrValue = "";
                                }

                            }

                            attr.put("ATTR_VALUE", attrValue);
                            break;
                        }
                    }
                }
                else
                {
                    attr.put("ATTR_VALUE", itemA.getString("ATTR_INIT_VALUE"));
                }
                returnAttrs.add(attr);
            }
            return returnAttrs;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 依据旧的局数据查询配置关系
     * author: zhangbo18
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getOffData(IData param) throws Exception
    {
    	if (null != param && param.size() > 0){
    		return PlatSvcInfoQry.queryOfficeData(param);
    	}
    	return null;
    }
    /**
     * 依据新的局数据查询配置关系
     * author: zhangbo18
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getNewOffData(IData param) throws Exception
    {
    	if (null != param && param.size() > 0){
    		return PlatSvcInfoQry.queryNewOfficeData(param);
    	}
    	return null;
    }
    /**
     * 获取重点业务
     * huangzl3
     */  
    public IDataset getKeyBusiness(IData param) throws Exception 
    {
    	return PlatSvcInfoQry.queryKeyBusiness();
    }
}
