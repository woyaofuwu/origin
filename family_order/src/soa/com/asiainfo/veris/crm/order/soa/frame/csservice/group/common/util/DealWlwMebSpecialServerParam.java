package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class DealWlwMebSpecialServerParam {

	private static String ELEMENT_ID = "";
	private static String ELEMENT_TYPE_CODE = "S";
	
	public static IDataset dealSpecialServerParam(ElementModel model,IDataset userParamList, String userId, 
    		String relaInstId, String acceptTime) throws Exception
    {
    	ELEMENT_ID = model.getElementId();
    	ELEMENT_TYPE_CODE = model.getElementTypeCode();
    	IDataset retParamList = new DatasetList();
    	
    	// 获取用户白名单参数列表
        IDataset userWhiteNumParamList = UserAttrInfoQry.getUserAttrByUserIdInstidForAll(userId, model.getElementTypeCode(), relaInstId);

    	//特殊处理 99011019	个人智能网语音通信服务(可选)的属性
    	//第一步，判断是否是需要特殊处理的服务
    	IDataset isWlwDel = CommparaInfoQry.getCommparaInfoByCode("CSM", "4002", 
    			 ELEMENT_ID, ELEMENT_TYPE_CODE, "0898");
    	
    	if("99011015".equals(ELEMENT_ID)){
    		retParamList = dealSpecialServerParamForGroup(model,userParamList,userWhiteNumParamList,userId,relaInstId,acceptTime);
    	}
    	//判断不是99011019 个人智能网语音通信服务(可选)，直接退出走正常流程
    	if(IDataUtil.isEmpty(isWlwDel)){
    		return retParamList;
    	}
    	
    	IDataset attrParamDataList = model.getAttrParam();
    	// 根据参数标志处理
        if (TRADE_MODIFY_TAG.DEL.getValue().equals(model.getModifyTag()))
        {
            for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
            {
                IData userParam = userParamList.getData(i);
                String paramKey = userParam.getString("ATTR_CODE","");
                
                String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                String  realCancelDate = SysDateMgr.addYearsNature(cancelDate,1);
                userParam.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                if("PhoneNumber".equals(paramKey)){
                    userParam.put("END_DATE", realCancelDate);
                }else {
                	userParam.put("END_DATE", cancelDate);
                }
              
            }
            retParamList = userParamList;
        }
        else
        {
        	if(IDataUtil.isNotEmpty(attrParamDataList)){
        		
        		boolean isAddWhiteNum = false;
            	boolean isAddAreas = false;
            	boolean isDelWhiteNum = false;
            	boolean isDelAreas = false;
            	
            	IDataset tempUserParamList = (IDataset)Clone.deepClone(userParamList);
            	IDataset tempUserWhiteNumParamList = (IDataset)Clone.deepClone(userWhiteNumParamList);
            	
            	for (int i = 0, iSize = attrParamDataList.size(); i < iSize; i++)
                {
            		IData attrParam = attrParamDataList.getData(i);
            		String paramKey = attrParam.getString("ATTR_CODE","");
            		String paramValue = attrParam.getString("ATTR_VALUE","");
            		
            		if("userWhiteNumOperType".equals(paramKey)){
            			if("2".equals(paramValue)){
                    		isDelWhiteNum = true;
                    	}else if("1".equals(paramValue)){
                    		isAddWhiteNum = true;
                    	}
            		}else if("userAreaOperType".equals(paramKey)){
            			if("2".equals(paramValue)){
            				isDelAreas = true;
            			} else if("1".equals(paramValue)){
            				isAddAreas = true;
            			}
            		}
                }
            	int whitenumflag = 0;
            	int areaflag = 0;
            	for (int i = 0, iSize = attrParamDataList.size(); i < iSize; i++)
                {
            		IData attrParam = attrParamDataList.getData(i);
            		String paramValue = attrParam.getString("ATTR_VALUE","");
            		 
            		for (int j=tempUserWhiteNumParamList.size()-1; j>=0; j--)
                     {
            			IData userParamData = tempUserWhiteNumParamList.getData(j);
                        String attrCode = userParamData.getString("ATTR_CODE", "");
                        String attrValue = userParamData.getString("ATTR_VALUE", "");
                        String rsrvstr4 = userParamData.getString("RSRV_STR4","");
                        
                    	if("userWhiteNum".equals(attrCode)){
                    		if(attrValue.equals(paramValue)){
                        		if(!"DEL".equals(rsrvstr4)){
                        			whitenumflag++;
                        		}
                			}
                		}else if("userArea".equals(attrCode)){
                			if(attrValue.equals(paramValue)){
                				if(!"DEL".equals(rsrvstr4)){
                					areaflag++;
                        		}	
                			}
                		}
                     }
            	
                }
            	
            	 int whiteNumCount  = 0;
            	 int areaCount = 0;
            	
            	 boolean whiteNuminvalidflag = false;
        		for (int i = 0, iSize = attrParamDataList.size(); i < iSize; i++)
                {
        			IData attrParam = attrParamDataList.getData(i);
        			String paramKey = attrParam.getString("ATTR_CODE","");
                    String paramValue = attrParam.getString("ATTR_VALUE","");
                    
                    boolean isExist = false;
                    IData map = new DataMap();
                    boolean breakFlag = false;
                    if("userArea".equals(paramKey) || "userWhiteNum".equals(paramKey)){
                		breakFlag = true;
                	}
                    
                    for (int j=tempUserWhiteNumParamList.size()-1; j>=0; j--)
                    {
                    	IData userParamData = tempUserWhiteNumParamList.getData(j);
                        String attrCode = userParamData.getString("ATTR_CODE", "");
                        String attrValue = userParamData.getString("ATTR_VALUE", "");
                        String rsrvstr4 = userParamData.getString("RSRV_STR4","");

                        
                        if (attrCode.equals(paramKey)) // paramKey相等,值不同
                        {
                        	//只要限制区域操作类型是删除,都把userAreaFlag个人用户/企业客户成员区域限制标识给删除掉
                        	if(("userAreaFlag".equals(attrCode)  &&  isDelAreas)) 
                        	{
                        		paramValue = "";
                        	}
                        	
                        	// 处理传空情况
                            if (StringUtils.isEmpty(paramValue) && StringUtils.isEmpty(attrValue))
                            {
                                isExist = true;
                                break;
                            }
                            
                            if("userArea".equals(attrCode) && isDelAreas){//删除特殊处理多个键值的情况
                            	if(attrValue.equals(paramValue)&&!"DEL".equals(rsrvstr4)){
                            	if(areaCount == 0){
                            	IData modParamData = (IData) Clone.deepClone(userParamData);
                            	String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                            	String  realCancelDate = SysDateMgr.addYearsNature(cancelDate,1);
                            	
                            	String instId = modParamData.getString("INST_ID","");
                            	String filterStr = "INST_ID=" + instId + ",ATTR_CODE=" + attrCode;
                            	IDataset filterData1 = DataHelper.filter(retParamList,  filterStr);
                            	if(IDataUtil.isEmpty(filterData1)){
                            		modParamData.put("RSRV_STR4","DEL");
                            		modParamData.put("END_DATE", realCancelDate);
                                    modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    modParamData.put("ATTR_VALUE", attrValue);//删除的时候填写原来的值
                                    retParamList.add(modParamData);
                            	}
                            	areaCount++;
                        		}
                            	isExist = true;
                            	break;
                            	}else if(areaflag == 0){
                            		CSAppException.apperr(ProductException.CRM_PRODUCT_522
                    						,"该区域不存在无法删除");
                            	}
                            	
                            }else if("userArea".equals(attrCode) && isAddAreas){
                            	if(areaflag==0){
                            	if(areaCount == 0){
                            	IData modParamData = (IData) Clone.deepClone(userParamData);
                            	
                            	String instId = modParamData.getString("INST_ID","");
                            	String filterStr = "INST_ID=" + instId + ",ATTR_CODE=" + attrCode;
                            	IDataset filterData1 = DataHelper.filter(retParamList,  filterStr);
                            	if(IDataUtil.isEmpty(filterData1)){
                            		modParamData.put("INST_ID", SeqMgr.getInstId());
                            		modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                            		modParamData.put("INST_TYPE", model.getElementTypeCode());
                            		modParamData.put("RELA_INST_ID", relaInstId);
                            		modParamData.put("ATTR_CODE", paramKey);
                            		modParamData.put("ATTR_VALUE", paramValue);
                            		modParamData.put("START_DATE", model.getStartDate());
                            		modParamData.put("END_DATE", model.getEndDate());
                            		modParamData.put("USER_ID", userId);
                            		modParamData.put("ELEMENT_ID", model.getElementId());
                                    retParamList.add(modParamData);
                            	}
                            	areaCount++;
                            	}
                            	isExist = true;
                            	break;
                            	}else{
                            		CSAppException.apperr(ProductException.CRM_PRODUCT_522
                    						,"该区域已存在无法添加");
                            	}
                            	
                            }else if("userWhiteNum".equals(attrCode) && isAddWhiteNum){
                            	if(whitenumflag == 0){
                        	        if(attrValue.equals(paramValue)&&"DEL".equals(rsrvstr4)){
                                	if(whiteNumCount == 0){
                                	IData modParamData = (IData) Clone.deepClone(userParamData);
                                	String instId = modParamData.getString("INST_ID","");
                                	String filterStr = "INST_ID=" + instId + ",ATTR_CODE=" + attrCode;
                                	IDataset filterData1 = DataHelper.filter(retParamList,  filterStr);
                                	if(IDataUtil.isEmpty(filterData1)){
	                			        modParamData.put("RSRV_STR4","");
	                			        modParamData.put("END_DATE", model.getEndDate());
	                                    modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
	                                    modParamData.put("ATTR_VALUE", paramValue);//填写原来的值
                
                                		retParamList.add(modParamData);
                                	}
                                	whiteNumCount++;
                                	}
                                	whiteNuminvalidflag =true;
                                	isExist = true;
                                	break;
                        	        }
                        	        else if(!whiteNuminvalidflag && j==0){
                        	        	if(whiteNumCount == 0){
                                        	IData modParamData = (IData) Clone.deepClone(userParamData);
                                        	String instId = modParamData.getString("INST_ID","");
                                        	String filterStr = "INST_ID=" + instId + ",ATTR_CODE=" + attrCode;
                                        	IDataset filterData1 = DataHelper.filter(retParamList,  filterStr);
                                        	if(IDataUtil.isEmpty(filterData1)){
                                        		
                                        		modParamData.put("INST_ID", SeqMgr.getInstId());
                                        		modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                                        		modParamData.put("INST_TYPE", model.getElementTypeCode());
                                        		modParamData.put("RELA_INST_ID", relaInstId);
                                        		modParamData.put("ATTR_CODE", paramKey);
                                        		modParamData.put("ATTR_VALUE", paramValue);
                                        		modParamData.put("START_DATE", model.getStartDate());
                                        		modParamData.put("END_DATE", model.getEndDate());
                                        		modParamData.put("USER_ID", userId);
                                        		modParamData.put("ELEMENT_ID", model.getElementId());
                                        		retParamList.add(modParamData);
                                        	}
                                        	whiteNumCount++;
                                        	}
                                        	isExist = true;
                                        	break;
                                	        }
                        	        }
                                	else {
                                		CSAppException.apperr(ProductException.CRM_PRODUCT_522
                        						,"用户白名单已存在无法添加");
                                	}
                            	
                            }
                            else if("userWhiteNum".equals(attrCode) && isDelWhiteNum){//删除特殊处理多个键值的情况
                            	if(attrValue.equals(paramValue)&&!"DEL".equals(rsrvstr4)){
                            	if(whiteNumCount == 0){
                            	IData modParamData = (IData) Clone.deepClone(userParamData);
                            	String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                            	String  realCancelDate = SysDateMgr.addYearsNature(cancelDate,1);
                            	
                            	String instId = modParamData.getString("INST_ID","");
                            	String filterStr = "INST_ID=" + instId + ",ATTR_CODE=" + attrCode;
                            	IDataset filterData1 = DataHelper.filter(retParamList,  filterStr);
                            	if(IDataUtil.isEmpty(filterData1)){
                            		modParamData.put("RSRV_STR4","DEL");
                            		modParamData.put("END_DATE", realCancelDate);
                                    modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    modParamData.put("ATTR_VALUE", attrValue);//删除的时候填写原来的值
                                    retParamList.add(modParamData);
                            	}
                            	whiteNumCount++;
                            	}
                            	isExist = true;
                            	break;
                            	}else if( whitenumflag == 0){
                            		CSAppException.apperr(ProductException.CRM_PRODUCT_522
                    						,"用户白名单不存在无法删除");
                            	}
                            	
                            } 
                            else 
                            {

                                	IData modParamData = (IData) Clone.deepClone(userParamData);
                                	
                                	// 修改服务参数为修改标识; 修改资费参数为注销原有资费参数, 新增一条新的资费参数
                                    if (StringUtils.isEmpty(paramValue))
                                    {
                                        String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                                        String  realCancelDate = SysDateMgr.addYearsNature(cancelDate,1);
                                        modParamData.put("END_DATE", realCancelDate);
                                        modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                        modParamData.put("ATTR_VALUE", attrValue);//删除的时候填写原来的值
                                        //MODIFY_TAG,填写1时,服务开通不拼,修改为2
                                    	if(("userAreaOperType".equals(attrCode) || "userAreaFlag".equals(attrCode))
                                    			&&  isDelAreas) {
                                    		modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    	} else if("userWhiteNumOperType".equals(attrCode) && isDelWhiteNum){
                                    		modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    	} else if("userWhiteNumFlag".equals(attrCode)){
                                    		modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    	}
                                    }
                                    else
                                    {
                                        modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                        modParamData.put("ATTR_VALUE", paramValue);
                                        
                                        if(("userWhiteNumOperType".equals(attrCode) && isDelWhiteNum) ||
                                        		("userAreaOperType".equals(attrCode)  &&  isDelAreas)){
                                        	String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                                        	String  realCancelDate = SysDateMgr.addYearsNature(cancelDate,1);
                                        	modParamData.put("END_DATE", realCancelDate);
                                        }else if(("userWhiteNumOperType".equals(attrCode) && isAddWhiteNum) ||
                                        		("userAreaOperType".equals(attrCode)  &&  isAddAreas)){
                                        	modParamData.put("END_DATE", model.getEndDate());
                                        }
                                        
                                    }
                                    
                                    retParamList.add(modParamData);

                            }
                            
                            if(!breakFlag){
                            	isExist = true;
                            	break;
                            }
                        }
                    }
                    if (!isExist)
                    {
                        // 属性值为空不入表
                        if (StringUtils.isBlank(paramValue))
                        {
                            continue;
                        }
                        if(("SysSpecialNumOperType".equals(paramKey) && isDelWhiteNum)){
                        	String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                        	String  realCancelDate = SysDateMgr.addYearsNature(cancelDate,1);
                        	map.put("END_DATE", realCancelDate);
                        }else{
                        	 map.put("END_DATE", model.getEndDate());
                        }
                        
                        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        map.put("INST_TYPE", model.getElementTypeCode());
                        map.put("RELA_INST_ID", relaInstId);
                        map.put("INST_ID", SeqMgr.getInstId());
                        map.put("ATTR_CODE", paramKey);
                        map.put("ATTR_VALUE", paramValue);
                        map.put("START_DATE", model.getStartDate());
                        map.put("USER_ID", userId);
                        map.put("ELEMENT_ID", model.getElementId());
                        retParamList.add(map);
                    }
                    
                }
        	}
        }
        
        checkWlwSpecialAttrForMeb(model,userParamList,userWhiteNumParamList,userId,relaInstId,acceptTime);
    	return retParamList;
    }
	
	
	public static IDataset dealSpecialServerParamForGroup(ElementModel model,IDataset userParamList,IDataset userWhiteNumParamList, String userId, 
    		String relaInstId, String acceptTime) throws Exception
    {
    	ELEMENT_ID = model.getElementId();
    	ELEMENT_TYPE_CODE = model.getElementTypeCode();
    	IDataset retParamList = new DatasetList();
    
    	IDataset attrParamDataList = model.getAttrParam();
    	// 根据参数标志处理
        if (TRADE_MODIFY_TAG.DEL.getValue().equals(model.getModifyTag()))
        {
            for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
            {    String time = model.getEndDate().toString();
                IData userParam = userParamList.getData(i);
                String paramKey = userParam.getString("ATTR_CODE","");
                
                String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                userParam.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                if("GroupCode".equals(paramKey)){
                    userParam.put("END_DATE", model.getEndDate());
                }else {
                	userParam.put("END_DATE", cancelDate);
                }
                
            }
            retParamList = userParamList;
        }
        else
        {
        	if(IDataUtil.isNotEmpty(attrParamDataList)){
        		
        		boolean isAddWhiteNum = false;
            
            	boolean isDelWhiteNum = false;
         
            	
            	IDataset tempUserParamList = (IDataset)Clone.deepClone(userParamList);
            	IDataset tempUserWhiteNumParamList = (IDataset)Clone.deepClone(userWhiteNumParamList);
            	
            	for (int i = 0, iSize = attrParamDataList.size(); i < iSize; i++)
                {
            		IData attrParam = attrParamDataList.getData(i);
            		String paramKey = attrParam.getString("ATTR_CODE","");
            		String paramValue = attrParam.getString("ATTR_VALUE","");
            		
            		if("SysSpecialNumOperType".equals(paramKey)){
            			if("2".equals(paramValue)){
                    		isDelWhiteNum = true;
                    	}else if("1".equals(paramValue)){
                    		isAddWhiteNum = true;
                    	}
            		}
                }
            	int whitenumflag = 0;
            	
            	for (int i = 0, iSize = attrParamDataList.size(); i < iSize; i++)
                {
            		IData attrParam = attrParamDataList.getData(i);
            		String paramValue = attrParam.getString("ATTR_VALUE","");
            		 
            		for (int j=tempUserWhiteNumParamList.size()-1; j>=0; j--)
                     {
            			IData userParamData = tempUserWhiteNumParamList.getData(j);
                        String attrCode = userParamData.getString("ATTR_CODE", "");
                        String attrValue = userParamData.getString("ATTR_VALUE", "");
                        String rsrvstr4 = userParamData.getString("RSRV_STR4","");
                        
                    	if("SysSpecialNum".equals(attrCode)){
                			if(attrValue.equals(paramValue)){
                				if(!"DEL".equals(rsrvstr4)){
                					whitenumflag++;
                				}
                			}
                		}
                     }
                }
            	
            	
            	

            	int whiteNumCount = 0;
            	boolean invalidflag = false;
        		for (int i = 0, iSize = attrParamDataList.size(); i < iSize; i++)
                {
        			IData attrParam = attrParamDataList.getData(i);
        			String paramKey = attrParam.getString("ATTR_CODE","");
                    String paramValue = attrParam.getString("ATTR_VALUE","");
                    
                    boolean isExist = false;
                    IData map = new DataMap();
                    boolean breakFlag = false;
                    if("SysSpecialNum".equals(paramKey)){
                		breakFlag = true;
                	}
                    
                    for (int j=tempUserWhiteNumParamList.size()-1; j>=0; j--)
                    {
                    	IData userParamData = tempUserWhiteNumParamList.getData(j);
                        String attrCode = userParamData.getString("ATTR_CODE", "");
                        String attrValue = userParamData.getString("ATTR_VALUE", "");
                        String rsrvstr4 = userParamData.getString("RSRV_STR4","");

                                                       
                        if (attrCode.equals(paramKey)) // paramKey相等,值不同
                        {
                        	// 处理传空情况
                            if (StringUtils.isEmpty(paramValue) && StringUtils.isEmpty(attrValue))
                            {
                                isExist = true;
                                break;
                            }
                            
                           if("SysSpecialNum".equals(attrCode) && isAddWhiteNum  ){    
                        	        if(whitenumflag == 0){
                        	        if(attrValue.equals(paramValue)&&"DEL".equals(rsrvstr4)){
                                	if(whiteNumCount == 0){
                                	IData modParamData = (IData) Clone.deepClone(userParamData);
                                	String instId = modParamData.getString("INST_ID","");
                                	String filterStr = "INST_ID=" + instId + ",ATTR_CODE=" + attrCode;
                                	IDataset filterData1 = DataHelper.filter(retParamList,  filterStr);
                                	if(IDataUtil.isEmpty(filterData1)){
	                			        modParamData.put("RSRV_STR4","");
	                			        modParamData.put("END_DATE", model.getEndDate());
	                                    modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
	                                    modParamData.put("ATTR_VALUE", paramValue);//填写原来的值
                
                                		retParamList.add(modParamData);
                                	}
                                	whiteNumCount++;
                                	}
                                	invalidflag =true;
                                	isExist = true;
                                	break;
                        	        }
                        	        else if(!invalidflag && j==0){
                        	        	if(whiteNumCount == 0){
                                        	IData modParamData = (IData) Clone.deepClone(userParamData);
                                        	String instId = modParamData.getString("INST_ID","");
                                        	String filterStr = "INST_ID=" + instId + ",ATTR_CODE=" + attrCode;
                                        	IDataset filterData1 = DataHelper.filter(retParamList,  filterStr);
                                        	if(IDataUtil.isEmpty(filterData1)){
                                        		
                                        		modParamData.put("INST_ID", SeqMgr.getInstId());
                                        		modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                                        		modParamData.put("INST_TYPE", model.getElementTypeCode());
                                        		modParamData.put("RELA_INST_ID", relaInstId);
                                        		modParamData.put("ATTR_CODE", paramKey);
                                        		modParamData.put("ATTR_VALUE", paramValue);
                                        		modParamData.put("START_DATE", model.getStartDate());
                                        		modParamData.put("END_DATE", model.getEndDate());
                                        		modParamData.put("USER_ID", userId);
                                        		modParamData.put("ELEMENT_ID", model.getElementId());
                                        		retParamList.add(modParamData);
                                        	}
                                        	whiteNumCount++;
                                        	}
                                        	isExist = true;
                                        	break;
                                	        }
                        	        }
                                	else {
                                		CSAppException.apperr(ProductException.CRM_PRODUCT_522
                        						,"用户白名单已存在无法添加");
                                	}
                            }
                            else if("SysSpecialNum".equals(attrCode) && isDelWhiteNum){//删除特殊处理多个键值的情况
                           if(attrValue.equals(paramValue)&&!"DEL".equals(rsrvstr4)){
                            	if(whiteNumCount == 0){
                            	IData modParamData = (IData) Clone.deepClone(userParamData);
                            	String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                            	String  realCancelDate = SysDateMgr.addYearsNature(cancelDate,1);
                            	
                            	String instId = modParamData.getString("INST_ID","");
                            	String filterStr = "INST_ID=" + instId + ",ATTR_CODE=" + attrCode;
                            	IDataset filterData1 = DataHelper.filter(retParamList,  filterStr);
                            	if(IDataUtil.isEmpty(filterData1)){
                            		modParamData.put("RSRV_STR4","DEL");
                            		modParamData.put("END_DATE", realCancelDate);
                                    modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    modParamData.put("ATTR_VALUE", paramValue);//删除的时候填写原来的值
                                    retParamList.add(modParamData);
                            	}
                            	whiteNumCount++;
                            	}
                            	isExist = true; 
                            	break;
                            	}else if( whitenumflag == 0){
                            		CSAppException.apperr(ProductException.CRM_PRODUCT_522
                    						,"用户白名单不存在无法删除");
                            	}
                      	
                            } 
                            else 
                            {

                                	IData modParamData = (IData) Clone.deepClone(userParamData);
                                	
                                	// 修改服务参数为修改标识; 修改资费参数为注销原有资费参数, 新增一条新的资费参数
                                    if (StringUtils.isEmpty(paramValue))
                                    {
                                        String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                                        String  realCancelDate = SysDateMgr.addYearsNature(cancelDate,1);
                                        modParamData.put("END_DATE", realCancelDate);
                                        modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                        modParamData.put("ATTR_VALUE", attrValue);//删除的时候填写原来的值
                                        //MODIFY_TAG,填写1时,服务开通不拼,修改为2
                                    	if(("SysSpecialNumOperType".equals(attrCode))
                                    			&&  isDelWhiteNum) {
                                    		modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    	}
                                    }
                                    else
                                    { 
                                        modParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                        modParamData.put("ATTR_VALUE", paramValue);
                                       
                                        if(("SysSpecialNumOperType".equals(attrCode) && isDelWhiteNum)){
                                        	String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                                        	String  realCancelDate = SysDateMgr.addYearsNature(cancelDate,1);
                                        	modParamData.put("END_DATE", realCancelDate);
                                        }else if(("SysSpecialNumOperType".equals(attrCode) && isAddWhiteNum)){
                                        	modParamData.put("END_DATE",  model.getEndDate());
                                        }
                                    }
                                    
                                    retParamList.add(modParamData);

                            }
                            
                            if(!breakFlag){
                            	isExist = true;
                            	break;
                            }
                        }
                    }
                    if (!isExist)
                    {
                        // 属性值为空不入表
                        if (StringUtils.isBlank(paramValue))
                        {
                            continue;
                        }

                        if(("SysSpecialNumOperType".equals(paramKey) && isDelWhiteNum)){
                        	String cancelDate = ElementUtil.getCancelDate(model, acceptTime);
                        	String  realCancelDate = SysDateMgr.addYearsNature(cancelDate,1);
                        	map.put("END_DATE", realCancelDate);
                        	
                        }else{
                        	 map.put("END_DATE", model.getEndDate());
                        }
                        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        map.put("INST_TYPE", model.getElementTypeCode());
                        map.put("RELA_INST_ID", relaInstId);
                        map.put("INST_ID", SeqMgr.getInstId());
                        map.put("ATTR_CODE", paramKey);
                        map.put("ATTR_VALUE", paramValue);
                        map.put("START_DATE", model.getStartDate());
                        map.put("USER_ID", userId);
                        map.put("ELEMENT_ID", model.getElementId());
                        retParamList.add(map);
                    }
                    
                }
        	}
        }
        checkWlwSpecialAttrForGroup(model,userParamList,userWhiteNumParamList,userId,relaInstId,acceptTime);
    	return retParamList;
    }
	/**
	 * 
	 * REQ201911200001 关于更新智能网语音优化需求支撑系统的改造通知
	 * 成员商品受理页面相关
	 * 
	 */
	private static void checkWlwSpecialAttrForMeb(ElementModel model,IDataset userParamList,IDataset userWhiteNumParamList,String userId, 
    		String relaInstId, String acceptTime) throws Exception{
		IDataset attrParamDataList = model.getAttrParam();
		IData checkMap= new DataMap();
		
		List<String> invalidList = new ArrayList<String>();
		
		if(IDataUtil.isNotEmpty(attrParamDataList)){
			for (int i = 0, iSize = attrParamDataList.size(); i < iSize; i++)
            {
    			IData attrParam = attrParamDataList.getData(i);
    			String paramKey = attrParam.getString("ATTR_CODE","");
                String paramValue = attrParam.getString("ATTR_VALUE","");
                
                if("UserClass".equals(paramKey)){
                	checkMap.put("UserClass",paramValue);
                }else if("userClassFlag".equals(paramKey)){
                	checkMap.put("userClassFlag",paramValue);
                }else if("userWhiteNum".equals(paramKey)){
                	checkMap.put("userWhiteNum",paramValue);
                }else if("userWhiteNumOperType".equals(paramKey)) {
                	checkMap.put("userWhiteNumOperType",paramValue);
                }
            }
			
		}
		
		IDataset tempUserParamList = (IDataset)Clone.deepClone(userWhiteNumParamList);
		
		int count=0;
		for (int i = 0; i < tempUserParamList.size(); i++) {
			IData attrParam = tempUserParamList.getData(i);
			
			//统计白名单数量
			if("userWhiteNum".equals(attrParam.getString("ATTR_CODE",""))){
				String rsrvstr4 = attrParam.getString("RSRV_STR4","");
				if("DEL".equals(rsrvstr4)){
					invalidList.add(attrParam.getString("ATTR_VALUE",""));
				}
				count++;
				}
			}
		
		int invalidcount = 0;
		if(!invalidList.isEmpty()&&invalidList.size()>0){
			 for(int j = 0;j<invalidList.size();j++){
				 String whitenum = invalidList.get(j).toString();
				 if(whitenum.equals(checkMap.getString("userWhiteNum"))){
					 invalidcount++; 
				 }
			 }
		}
		if (TRADE_MODIFY_TAG.MODI.getValue().equals(model.getModifyTag())){
			if(StringUtils.isNotBlank((checkMap.getString("UserClass")))){
				for (int i = 0; i < tempUserParamList.size(); i++) {
					IData attrParam = tempUserParamList.getData(i);
				
					if("UserClass".equals(attrParam.getString("ATTR_CODE",""))){
					
						//a)“企业客户成员个人通话阀值的级别”当月仅支持变更一次；
						if(SysDateMgr.sameMonthCompare(attrParam.getString("UPDATE_TIME",""),SysDateMgr.getSysDate())){
							CSAppException.apperr(ProductException.CRM_PRODUCT_522
									,"通话阀值的级别,当月仅支持变更一次");
						}
					}
				}
			}else if(StringUtils.isNotBlank((checkMap.getString("userClassFlag")))){
				for (int i = 0; i < tempUserParamList.size(); i++) {
					IData attrParam = tempUserParamList.getData(i);
				
				 if("userClassFlag".equals(attrParam.getString("ATTR_CODE",""))){
						
						//b)“企业客户成员个人通话阀值是否立即生效”当月仅支持变更一次；
						if(SysDateMgr.sameMonthCompare(attrParam.getString("UPDATE_TIME",""),SysDateMgr.getSysDate())){
							CSAppException.apperr(ProductException.CRM_PRODUCT_522
									,"企业客户成员个人通话阀值是否立即生效,当月仅支持变更一次");
						}
					}
				}
			}
			//用户最多可设置10个用户白名单；存量用户，若其用户白名单数量已超过10个，则不做处理，但不能再进行新增用户白名单的操作。
			if(StringUtils.isNotBlank(checkMap.getString("userWhiteNum"))&&count>=10){
				if("1".equals(checkMap.getString("userWhiteNumOperType"))){
					 if(invalidcount == 0){
						 CSAppException.apperr(ProductException.CRM_PRODUCT_522
									,"用户最多可设置10个用户白名单,只允许重新添加已删除的用户白名单");
					 }
					 }
				
			}
		}
	}
	
	/**
	 * 
	 * REQ201911200001 关于更新智能网语音优化需求支撑系统的改造通知
	 *  集团商品受理页面相关
	 * 
	 */
	private static void checkWlwSpecialAttrForGroup(ElementModel model,IDataset userParamList,IDataset userWhiteNumParamList,String userId, 
    		String relaInstId, String acceptTime) throws Exception{
		IDataset attrParamDataList = model.getAttrParam();
		IData checkMap= new DataMap();
	
    	List<String> invalidList = new ArrayList<String>();

		
		
		if(IDataUtil.isNotEmpty(attrParamDataList)){
			for (int i = 0, iSize = attrParamDataList.size(); i < iSize; i++)
            {
    			IData attrParam = attrParamDataList.getData(i);
    			String paramKey = attrParam.getString("ATTR_CODE","");
                String paramValue = attrParam.getString("ATTR_VALUE","");
                
              if("SysSpecialNum".equals(paramKey)){
                	checkMap.put("SysSpecialNum",paramValue);
                } 
              if("SysSpecialNumOperType".equals(paramKey)){
                	checkMap.put("SysSpecialNumOperType",paramValue);
                } 
            }
			
		}
		
		IDataset tempUserParamList = (IDataset)Clone.deepClone(userWhiteNumParamList);
		
		int count=0;
		for (int i = 0; i < tempUserParamList.size(); i++) {
			IData attrParam = tempUserParamList.getData(i);
			
			//统计白名单数量
			if("SysSpecialNum".equals(attrParam.getString("ATTR_CODE",""))){
				String enddate =  attrParam.getString("END_DATE","");
				String attrvalue = attrParam.getString("ATTR_VALUE","");
				String rsrvstr4 = attrParam.getString("RSRV_STR4","");
				if("DEL".equals(rsrvstr4)){
					invalidList.add(attrParam.getString("ATTR_VALUE",""));
				}
				count++;
				}
			}
		System.out.println("白名单数："+count);
		int invalidcount = 0;
		if(!invalidList.isEmpty()&&invalidList.size()>0){
			 for(int j = 0;j<invalidList.size();j++){
				 String whitenum = invalidList.get(j).toString();
				 if(whitenum.equals(checkMap.getString("SysSpecialNum"))){
					 invalidcount++; 
				 }
			 }
		}
		if (TRADE_MODIFY_TAG.MODI.getValue().equals(model.getModifyTag())){
			//用户最多可设置10个用户白名单；存量用户，若其用户白名单数量已超过10个，则不做处理，但不能再进行新增用户白名单的操作。
			if(StringUtils.isNotBlank(checkMap.getString("SysSpecialNum"))&&count>=10){
						 if("1".equals(checkMap.getString("SysSpecialNumOperType"))){
							 if(invalidcount == 0){
								 CSAppException.apperr(ProductException.CRM_PRODUCT_522
											,"集团最多只能设置10个用户白名单,只允许重新添加已删除的用户白名单");
							 }
						 } 
			}
		}
	}
}
