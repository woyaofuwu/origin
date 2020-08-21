package com.asiainfo.veris.crm.iorder.soa.group.param.wlw;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.iorder.soa.group.param.adc.QueryAdcAttrParamBean;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class QueryWlwAttrParamBean extends QueryAttrParamBean{

	private static final Logger logger = LoggerFactory.getLogger(QueryAdcAttrParamBean.class);
	
	public static IData queryWlwParamAttrForCrtUsInit(IData param) throws Exception{
		IData results = new DataMap();
		
		IData platsvc = new DataMap();
		
		String serviceId = param.getString("PRODUCT_ID");
		//物联网产品有多个服务,且分别有不同的界面,所以需要用不同逻辑分支处理
		if("9013".equals(serviceId)){//物联网必选服务
			IDataset commpara = CommparaInfoQry.getCommparaAllCol("CSM", "9017", "bizincode", "ZZZZ");
			if(IDataUtil.isEmpty(commpara)){
				CSAppException.apperr(ParamException.CRM_PARAM_410);
			}else{
				IData bizData = new DataMap();
	            bizData.put("DATA_VAL", commpara.getData(0).getString("PARA_CODE1"));
	            results.put("SI_BASE_IN_CODE", bizData);
			}
	        
	        //判断增删改操作
	        IData modifyTag = new DataMap();
	        modifyTag.put("DATA_VAL", "0");
	        results.put("MODIFY_TAG", modifyTag);
	      
	        //服务ID
	        IData service_Id = new DataMap();
	        service_Id.put("DATA_VAL", serviceId);
	        results.put("SERVICE_ID", service_Id);
			
		}else if("99011013".equals(serviceId)){
			platsvc.put("APNNAME","CMMIOT");
		}else if("99011014".equals(serviceId)){
			platsvc.put("APNNAME","AUDI.JL");
		}else if("99010023".equals(serviceId)){
			platsvc.put("APNNAME","CMMIOT");
		}else if("99010024".equals(serviceId)){
			platsvc.put("APNNAME","AUDI.JL");
		}else if("99011015".equals(serviceId)){//如果下拉框是查的static表,可以在取值的时候用data_val,如果是从产商品那边查出的值或者自己塞的,下拉用字段对应的data_val,取值用attr_val
			IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(serviceId, "S", "1", null);
	        IData attrItemA = new DataMap();

	        if (IDataUtil.isNotEmpty(dataset))
	        {
	            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
	            
	        }  

	        if(IDataUtil.isNotEmpty(attrItemA)){
	            Set<String> propNames = attrItemA.keySet();
	            for(String key : propNames)
	            {
	                IData attrCodeInfo = attrItemA.getData(key);
	                IData attrItem = new DataMap();
	                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
	                if(IDataUtil.isNotEmpty(workTypeCodeInfo)){
	                    attrItem.put("DATA_VAL", workTypeCodeInfo);
	                    if(StringUtils.isNotBlank(attrCodeInfo.getString("ATTR_VALUE"))){
	                    	attrItem.put("ATTR_VAL", attrCodeInfo.getString("ATTR_VALUE"));
	                    }
	                    results.put(key, attrItem);
	                }
	            }
	        }
		}else{//其他服务原来都是直接走配置,所以新增的时候直接读数据库就可以保证数据了,不需要单独初始化,变更的时候可能需要单独初始化 serviceId=99010018 or 99010028
			
		}
		
		if(IDataUtil.isNotEmpty(platsvc)){
		  Set<String> wlwparam = platsvc.keySet();
          for(String key:wlwparam){
        	  String attrCode = platsvc.getString(key);
        	  IData attrItem = new DataMap();
        	  attrItem.put("DATA_VAL", attrCode);
        	  results.put(key, attrItem);
          }
        }
		
		return results;
	}
	
//	input.put("OFFER_ID", curOfferId);
//    input.put("ATTR_OBJ", "0");
//    input.put("EPARCHY_CODE", getTradeEparchyCode());
//    input.put("USER_ID", subscriberInsId);
//    input.put("OFFER_INS_ID", offerInsId);
//    input.put("RELA_INST_ID", offerInsId);
//    input.put("INST_TYPE", curOfferType);//USER_ATTR表中的INST_TYPE属性
//    input.put("IS_MEB", "false");
//    input.put("CUST_ID", custId);
//    input.put("PRODUCT_ID",curOfferCode);
//    input.put("OFFER_CODE", offerCode);//方便ADC对特殊产品进行判断,对逻辑无影响
//    input.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
	
	
	//因为一开始参数已经配置好了,懒得再大改了,就集团服务而言,除了99011015这个服务,其他全部取值全是DATA_VAL,9013这个服务下拉框是读取的static表,所以值全放到DATA_VAL中
	//99011015这个服务,下拉框配的是产商品的数据,查询出来的,虽然static表肯定也有,但由于它没有配置单独的界面,所以不好找,而且正确的逻辑也应该是读产商品的数据,现在就是这个服务的所有参数取值都配为attr_val,下拉配为data_val
	public static IData queryWlwParamAttrForChgUsInit(IData param) throws Exception{
		IData results= new DataMap();
		IData platsvc= new DataMap();
		String userId = param.getString("USER_ID");
		String serviceId = param.getString("PRODUCT_ID");
		IDataset userAttrInfo = UserAttrInfoQry.getuserAttrBySvcId(userId, serviceId);
		
		if(IDataUtil.isEmpty(userAttrInfo)){
			results = queryWlwParamAttrForCrtUsInit(param);
			return results;
		}else{
		for(int i=0;i<userAttrInfo.size();i++){
			IData attrinfo = userAttrInfo.getData(i);
			String attrCode = attrinfo.getString("ATTR_CODE");
			String attrValue= attrinfo.getString("ATTR_VALUE");
			platsvc.put(attrCode, attrValue);
		}
		
		if((IDataUtil.isNotEmpty(platsvc))&&("99011015".equals(serviceId))){
			
			IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(serviceId, "S", "1", null);
	        IData attrItemA = new DataMap();

	        if (IDataUtil.isNotEmpty(dataset))
	        {
	            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
	            
	        }  

	        if(IDataUtil.isNotEmpty(attrItemA)){
	            Set<String> propNames = attrItemA.keySet();
	            for(String key : propNames)
	            {
	                IData attrCodeInfo = attrItemA.getData(key);
	                IData attrItem = new DataMap();
	                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
	                if(IDataUtil.isNotEmpty(workTypeCodeInfo)){
	                    attrItem.put("DATA_VAL", workTypeCodeInfo);    
	                }
	                if(StringUtils.isNotBlank(platsvc.getString(key))){
                    	attrItem.put("ATTR_VAL", platsvc.getString(key));
                    }
	                results.put(key, attrItem);
	            }
	        }
			
		}else if(IDataUtil.isNotEmpty(platsvc)){
			Set<String> wlwparam = platsvc.keySet();
	          for(String key:wlwparam){
	        	  String attrCode = platsvc.getString(key);
	        	  IData attrItem = new DataMap();
	        	  attrItem.put("DATA_VAL", attrCode);
	        	  results.put(key, attrItem);
	          }
		}
		return results;
		}
	}
	
	//成员所有的服务参数 初始化值都去attr_val 下拉框配置data_val
	public static IData queryWlwParamAttrForCrtMbInit(IData param) throws Exception{

		IData results = new DataMap();
		String serviceId = param.getString("OFFER_CODE");//这里取值暂时可能是不对的,到时候打断点看一下
		
		IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(serviceId, "S",null, null, null);
        IData attrItemA = new DataMap();

        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            
        }  
		
        if(IDataUtil.isNotEmpty(attrItemA)){
            Set<String> propNames = attrItemA.keySet();
            for(String key : propNames)
            {
                IData attrCodeInfo = attrItemA.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                if(IDataUtil.isNotEmpty(workTypeCodeInfo)){
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                }
                if(StringUtils.isNotBlank(attrCodeInfo.getString("ATTR_VALUE"))){
                	attrItem.put("ATTR_VAL", attrCodeInfo.getString("ATTR_VALUE"));
                }
                results.put(key, attrItem);
            }
        }
        
		return results;
	}
	
	public static IData queryWlwParamAttrForChgMbInit(IData param) throws Exception{
		IData results = new DataMap();
		String serviceId = param.getString("OFFER_CODE");//这里取值暂时可能是不对的,到时候打断点看一下
		String mebUserId = param.getString("USER_ID");
		IDataset userAttrInfo = UserAttrInfoQry.getuserAttrBySvcId(mebUserId, serviceId);
		
		IData platsvc = new DataMap();
		
		if(IDataUtil.isEmpty(userAttrInfo)){
			results = queryWlwParamAttrForCrtMbInit(param);
			return results;
		}else{
			
		  for(int i=0;i<userAttrInfo.size();i++){
			  IData attrinfo = userAttrInfo.getData(i);
			  String attrCode = attrinfo.getString("ATTR_CODE");
			  String attrValue= attrinfo.getString("ATTR_VALUE");
			  platsvc.put(attrCode, attrValue);
		  }
		  
		  IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(serviceId, "S",null, null, null);
	      IData attrItemA = new DataMap();
		  
		  if (IDataUtil.isNotEmpty(dataset))
	        {
	            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
	        }  

	        if(IDataUtil.isNotEmpty(attrItemA)){
	            Set<String> propNames = attrItemA.keySet();
	            for(String key : propNames)
	            {
	                IData attrCodeInfo = attrItemA.getData(key);
	                IData attrItem = new DataMap();
	                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
	                if(IDataUtil.isNotEmpty(workTypeCodeInfo)){
	                    attrItem.put("DATA_VAL", workTypeCodeInfo);    
	                }
	                if(StringUtils.isNotBlank(platsvc.getString(key))){
                  	attrItem.put("ATTR_VAL", platsvc.getString(key));
                  }
	                results.put(key, attrItem);
	            }
	        }
		}
		
		return results;
	}
	
//	public static IData queryWlwProductParam(IData param) throws Exception{
//		IData results = new DataMap();
//		
//		IDataset dataVal = new DatasetList();
//		IDataset dataset = CommparaInfoQry.getCommparaAllCol("CSM", "9980", "1", "0898");
//		for(int i=0;i<dataset.size();i++){
//			IData data = dataset.getData(i);
//			String paraCode1 = data.getString("PARA_CODE1");
//			IData para = new DataMap();
//			para.put("VALUE", paraCode1);
//			para.put("TEXT", paraCode1);
//			dataVal.add(para);
//		}
//		
//		IData applyTypeA = new DataMap();
//		applyTypeA.put("DATA_VAL", dataVal);
//		results.put("APPLY_TYPE_A", applyTypeA);
//		
//		return results;
//	}
}
