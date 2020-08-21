package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class GrpDisAttrTransBean{
	private static transient final Logger logger = Logger.getLogger(GrpDisAttrTransBean.class);
	
	public static void disDataToAttrData(IData map,String merchOperType)throws Exception{
		disDataToAttrData(map);
	}
	
    /**
     *	1、将资费转化为集团属性(同步集团)
     * 		由于集团产品资费需要同步给本地账务，且在集团公司是以集团产品参数形式存在，
     * 		所以需要集团资费需要在本地以资费形式存在，在集团公司侧需要以集团产品属性形式存在将前台资费转化为产品参数方便发服开生成报文 
     * 		(1)传入 map 含有 ELEMENT_INFO 和PRODUCT_PARAM_INFO 
     * 		(2)传出参数中的 PRODUCT_PARAM_INFO 包含ELEMENT_INFO 里面的信息，
     * 			即资费信息要入discnt表，也需要入attr表 如果集团产品属性 attr_value 为集团下发指定值，
     * 			则每个指定值在td_s_static表有对应本地资费编码 
     * 			如果集团产品属性 attr_value为用户填写值，则该attr_code对应在td_s_static有一个本地资费编码，且attr_value为ICB参数值
     * @author chenmw3
     * @date 2017-06-05
     * @throws Exception
     */
    public static IData disDataToAttrData(IData map) throws Exception{
    	
    	if(logger.isDebugEnabled()){
    		logger.debug("GrpDisAttrTransBean disDataToAttrData IN map="+map);
    	}
    	
        // 1-先获取资费信息和产品参数信息
    	IDataset orderInfos = map.getDataset("ORDER_INFO");
    	if (IDataUtil.isEmpty(orderInfos)){
            return map;
        }
    	IData orderInfo = orderInfos.getData(0);
        IDataset elementInfos = orderInfo.getDataset("ELEMENT_INFO");
        if (IDataUtil.isEmpty(elementInfos)){
            return map;
        }
        
        // 2-组装标准格式的报文
        IDataset productParamInfos = orderInfo.getDataset("PRODUCT_PARAM_INFO",new DatasetList());
        if (IDataUtil.isEmpty(productParamInfos)){
        	IData productParamInfo = new DataMap();
        	productParamInfo.put("PRODUCT_ID", orderInfo.getString("PRODUCT_ID"));
        	productParamInfos.add(productParamInfo);
        }
        IDataset productParam = productParamInfos.getData(0).getDataset("PRODUCT_PARAM");
        if(IDataUtil.isEmpty(productParam)){
        	productParam = new DatasetList();
        	productParamInfos.getData(0).put("PRODUCT_PARAM", productParam);
        }
        
        if(logger.isDebugEnabled()){
    		logger.debug("GrpDisAttrTransBean disDataToAttrData IN elementInfos="+elementInfos);
    		logger.debug("GrpDisAttrTransBean disDataToAttrData IN productParamList="+productParamInfos);
        }

        // 3-循环资费信息 判断资费信息是不是对应的集团产品产品参数如果是将信息加入产品参数
        for (int i = 0, size = elementInfos.size(); i < size; i++){
            // 3.1-资费转换成产品参数
        	IData elementInfo = elementInfos.getData(i);
            String elementId = elementInfo.getString("ELEMENT_ID");
            String modifyTag = elementInfo.getString("MODIFY_TAG");
            boolean isDisAdd = addNewAttrData(productParam, "BBOSS_ATTRTODIS", elementId, null, modifyTag);
            if(!isDisAdd){
            	continue;
            }
            // 3.2-资费属性转换成产品参数
            IDataset disAttrParamList = elementInfo.getDataset("ATTR_PARAM");
            for(int j = 0, sizeJ = disAttrParamList.size(); j < sizeJ; j++){
            	IData disAttrParam = disAttrParamList.getData(j);
            	String attrCode = disAttrParam.getString("ATTR_CODE");
            	String attrValue = disAttrParam.getString("ATTR_VALUE","");
                boolean isDisAttrAdd = addNewAttrData(productParam, "BBOSS_ATTRTODISATTR", attrCode, attrValue, modifyTag);
                if(isDisAttrAdd && (attrValue.endsWith("Mb") || attrValue.endsWith("MB"))){
                	attrValue = attrValue.replaceAll("Mb", "");
                	attrValue = attrValue.replaceAll("MB", "");
                	disAttrParam.put("ATTR_VALUE", attrValue);
                }
            }
        }
        
        // 4-如果发现 productOperType=55(修改产品本地资费),则将 替换 成 productOperType=9(修改订购产品属性)
        String productOperType = orderInfo.getString("PRODUCT_OPER_TYPE");
        if(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_LOCALDISCNT.getValue().equals(productOperType)){
        	orderInfo.put("PRODUCT_OPER_TYPE",GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue());
        }
        IData merchInfo = map.getData("MERCH_INFO");
        if(IDataUtil.isNotEmpty(merchInfo)){
        	productOperType = merchInfo.getString("PRODUCT_OPER_TYPE");
        	if(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_LOCALDISCNT.getValue().equals(productOperType)){
        		merchInfo.put("PRODUCT_OPER_TYPE",GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue());
            }
        	IData goodInfo = merchInfo.getData("GOOD_INFO"); 
            if(IDataUtil.isNotEmpty(goodInfo)){
            	String merchOperType = goodInfo.getString("MERCH_OPER_CODE");
            	if(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_LOCALDISCNT.getValue().equals(merchOperType)){
            		goodInfo.put("MERCH_OPER_CODE",GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue());
                }
            }
        }
        
        // 5-设置BBOSS产品属性信息
        orderInfo.put("PRODUCT_PARAM_INFO", productParamInfos);
        
        if(logger.isDebugEnabled()){
    		logger.debug("GrpDisAttrTransBean disDataToAttrData OUT map="+map);
        }

        // 6-返回新构造好的信息
        return map;
    }
    
    private static boolean addNewAttrData(IDataset productParam, String typeId, String dataId, String attrValue, String modifyTag) throws Exception{
    	 if(logger.isDebugEnabled()){
     		logger.debug("GrpDisAttrTransBean addNewAttrData IN productParam="+productParam);
     		logger.debug("GrpDisAttrTransBean addNewAttrData IN typeId="+typeId);
     		logger.debug("GrpDisAttrTransBean addNewAttrData IN dataId="+dataId);
     		logger.debug("GrpDisAttrTransBean addNewAttrData IN attrValue="+attrValue);
     		logger.debug("GrpDisAttrTransBean addNewAttrData IN modifyTag="+modifyTag);
         }
    	
    	//获取此元素对应的属性编码
        String attrCode = getBbossAttrToDisInfo("PDATA_ID", typeId, dataId);
        if (StringUtils.isEmpty(attrCode)){
            return false;
        }
        if(logger.isDebugEnabled()){
     		logger.debug("GrpDisAttrTransBean addNewAttrData attrCode="+attrCode);
        }
        //获取本地属性配置信息
        IDataset attrDataset = BBossAttrQry.qryBBossAttrByAttrCode(attrCode);
        if (IDataUtil.isEmpty(attrDataset)){
            CSAppException.apperr(GrpException.CRM_GRP_837);
        }
        String attrName = attrDataset.getData(0).getString("ATTR_NAME");
        //查询资费的转换成产品属性的属性值
        if(StringUtils.isBlank(attrValue)){
        	attrValue = getBbossAttrToDisInfo("SUBSYS_CODE", typeId, dataId);
        }
        if(logger.isDebugEnabled()){
     		logger.debug("GrpDisAttrTransBean addNewAttrData attrDataset="+attrDataset);
     		logger.debug("GrpDisAttrTransBean addNewAttrData attrValue="+attrValue);
        }
        
        IData newAttrData = new DataMap();
        newAttrData.put("ATTR_VALUE", attrValue);
        newAttrData.put("ATTR_NAME", attrName);
        newAttrData.put("ATTR_CODE", attrCode);
        // 根据资费状态判断新拼属性状态
        if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag)){
            newAttrData.put("STATE", "ADD");
        }else if (TRADE_MODIFY_TAG.EXIST.getValue().equals(modifyTag)){
            newAttrData.put("STATE", "EXIST");
        }else if (TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag)){
        	if("BBOSS_ATTRTODIS".equals(typeId)){
        		newAttrData.put("STATE", "MODI");
        	}else{
        		//如果是修改，则需要新增一条删除一条
            	newAttrData.put("STATE", "ADD");
            	
            	IData oldAttrData = new DataMap();
            	oldAttrData.put("ATTR_VALUE", attrValue);
            	oldAttrData.put("ATTR_NAME", attrName);
            	oldAttrData.put("ATTR_CODE", attrCode);
            	oldAttrData.put("STATE", "DEL");
            	productParam.add(oldAttrData);
        	}
        }else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag)){
            newAttrData.put("STATE", "DEL");
        }
        productParam.add(newAttrData);
        
        if(logger.isDebugEnabled()){
     		logger.debug("GrpDisAttrTransBean addNewAttrData OUT newAttrData="+newAttrData);
     		logger.debug("GrpDisAttrTransBean addNewAttrData OUT productParam="+productParam);
        }
        
    	return true;
    }
    
    private static String getBbossAttrToDisInfo(String qryKey, String typeId, String dataId) throws Exception{
    	String value = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]{ "TYPE_ID", "DATA_ID" }, qryKey, new java.lang.String[]{ typeId, dataId});
        return value;
    }
    
	
    
    
    public static IData attrDataToDisData(IData map, String merchOperType) throws Exception{
    	IDataset orderInfos = map.getDataset("ORDER_INFO");
    	if (IDataUtil.isEmpty(orderInfos)){
    		return map;
    	}
    	for(int i=0,sizeI=orderInfos.size();i<sizeI;i++){
    		IData orderInfo = orderInfos.getData(i);
    		//前台传过来的本地资费
    		IDataset elementInfos = orderInfo.getDataset("ELEMENT_INFO",new DatasetList());
    		IDataset productParamInfos = orderInfo.getDataset("PRODUCT_PARAM_INFO");
    		if (IDataUtil.isEmpty(productParamInfos)){
    			continue;
    		}
    		IDataset productParam = productParamInfos.getData(0).getDataset("PRODUCT_PARAM");
    		if(logger.isDebugEnabled()){
         		logger.debug("GrpDisAttrTransBean attrDataToDisData OUT productParam="+productParam);

            }
    		if(IDataUtil.isEmpty(productParam)){
    			continue;
    		}
    		//查找产品属性中的需要转换成本地资费
    		for(int j=0,sizeJ=productParam.size();j<sizeJ;j++){
    			IData proAttrData = productParam.getData(j);
    			String proAttrCode = proAttrData.getString("ATTR_CODE");
    			String proAttrValue = proAttrData.getString("ATTR_VALUE");
    			String proState = proAttrData.getString("STATE");
    			if(!"ADD".equals(proState)){
    				continue;
    			}
    			
    			//查找产品属性中的需要转换成本地资费的config
    			IDataset disConfigs = StaticUtil.getStaticList("BBOSS_PROATTRTOLOCDIS", proAttrCode);
    			if(logger.isDebugEnabled()){
    	     		logger.debug("GrpDisAttrTransBean attrDataToDisData OUT disConfigs="+disConfigs);

    	        }
    			if(IDataUtil.isEmpty(disConfigs)){
    				continue;
    			}
    			String cfgElementId = disConfigs.getData(0).getString("DATA_NAME");
    			String cfgProductId = disConfigs.getData(0).getString("PDATA_ID");
    			String cfgPackageId = disConfigs.getData(0).getString("SUBSYS_CODE");
    			
    			//查找产品属性中的需要转换成本地资费属性的config
    			IDataset disAttrConfigs = StaticUtil.getStaticList("BBOSS_PROATTRTOLOCDISATTR", cfgElementId);
    			if(logger.isDebugEnabled()){
    	     		logger.debug("GrpDisAttrTransBean attrDataToDisData OUT disAttrConfigs="+disAttrConfigs);

    	        }
    			if(IDataUtil.isEmpty(disAttrConfigs)){
    				continue;
    			}
    			String cfgDisAttrCode = disAttrConfigs.getData(0).getString("DATA_NAME");
    			if(logger.isDebugEnabled()){
    	     		logger.debug("GrpDisAttrTransBean attrDataToDisData OUT cfgDisAttrCode="+cfgDisAttrCode);

    	        }
    			// 1-商品订购/修改商品组成关系
    			if(GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue().equals(merchOperType)){
    				if (IDataUtil.isEmpty(elementInfos)){
    					CSAppException.apperr(GrpException.CRM_GRP_915, cfgElementId);
    				}
    				for(int m=0,sizeM=elementInfos.size();m<sizeM;m++){
    					IData elementInfo = elementInfos.getData(m);
    					String elementId = elementInfo.getString("ELEMENT_ID");
    					if(!StringUtils.equals(cfgElementId, elementId)){
    						continue;
    					}
    					IDataset disAttrParamList = elementInfo.getDataset("ATTR_PARAM");
    					if(logger.isDebugEnabled()){
    			     		logger.debug("GrpDisAttrTransBean attrDataToDisData OUT disAttrParamList="+disAttrParamList);

    			        }
    					if(IDataUtil.isEmpty(disAttrParamList)){
    						CSAppException.apperr(GrpException.CRM_GRP_915 , cfgElementId);
    					}
    					for(int n = 0, sizeN = disAttrParamList.size(); n < sizeN; n++){
    						IData disAttrParam = disAttrParamList.getData(n);
    						String disAttrCode = disAttrParam.getString("ATTR_CODE");
    						if(logger.isDebugEnabled()){
    				     		logger.debug("GrpDisAttrTransBean attrDataToDisData OUT disAttrCode="+disAttrCode);

    				        }
    						if(StringUtils.equals(cfgDisAttrCode, disAttrCode)){
    							disAttrParam.put("ATTR_VALUE", proAttrValue);
    						}
    					}
    				}
    				continue;
    			}
    			
    			// 2-修改商品属性
    			if(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue().equals(merchOperType)){
    				IData elementInfo = new DataMap();
    				elementInfo.put("PRODUCT_ID", cfgProductId);
    				elementInfo.put("PACKAGE_ID", cfgPackageId);
    				elementInfo.put("ELEMENT_ID", cfgElementId);
    				String userId = orderInfo.getString("USER_ID");
    		    	IDataset userDisInfos = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, cfgElementId);
    		    	elementInfo.put("START_DATE", userDisInfos.getData(0).getString("START_DATE"));
    		    	elementInfo.put("END_DATE", userDisInfos.getData(0).getString("END_DATE"));
    				elementInfo.put("ELEMENT_TYPE_CODE", "D");
    				elementInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
    				IDataset disAttrParamList =  new DatasetList();
    				IData disAttrParam = new DataMap();
    				disAttrParam.put("ATTR_CODE", cfgDisAttrCode);
    				disAttrParam.put("ATTR_VALUE", proAttrValue);
    				disAttrParamList.add(disAttrParam);
    				elementInfo.put("ATTR_PARAM", disAttrParamList);
    				
    				elementInfos.add(elementInfo);
    				orderInfo.put("ELEMENT_INFO", elementInfos);
    				continue;
    			}
    		}
    	}
    	return map;
    }
}