package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


public class IsOrderSaleActiveQryBean {
	
	static Map<String, IDataset> labelInfoCache = new HashMap<String, IDataset>();
	static Map<String, IDataset> commParamCache = new HashMap<String, IDataset>();

	public IDataset queryUserInfoBySerialNumber(IData param) throws Exception {
		
		return Dao.qryByCode("TF_F_USER","SEL_USERINFO_BY_SERIALNUMBER", param);	
	}
	
	public IDataset isIphone6Cons(IData inparam) throws Exception {
		
//		return Dao.qryByCode("TD_B_LABEL","SEL_BY_LABEL_ID", inparam,Route.CONN_CRM_CEN);
		String labelId = inparam.getString("LABEL_ID","");
		IDataset result = new DatasetList();
		IDataset labelInfos = UpcCall.qryChildrenCatalogsByIdLevel("4", labelId);
		for(int i = 0 ; i < labelInfos.size() ; i++)
		{
		    IData info = labelInfos.getData(i);
		    info.put("LABEL_ID", info.getString("CATALOG_ID"));
		    info.put("LABEL_NAME", info.getString("CATALOG_NAME"));
		    info.put("PARENT_LABEL_ID", info.getString("UP_CATALOG_ID",""));
		    info.put("CLASS_LEVEL", "-1");
		    result.add(info);
		}
		return result;
	}
	
	public IDataset queryProductIdByLabelid(String labelId) throws Exception {
		
//		return Dao.qryByCode("TD_B_LABEL","SEL_PRODUCT_LABEL", inparam,Route.CONN_CRM_CEN);	
		IDataset result = new DatasetList();
		IDataset commParamInfos = CommparaInfoQry.getCommByParaAttr("CSM", "522", "0898");
		IDataset labelInfos = UpcCall.qrySaleActiveProductByLabelId(labelId);
		if(IDataUtil.isNotEmpty(labelInfos)){
			for(Object obj : labelInfos){
				Boolean isAdd = true;
				IData labelInfo = (IData) obj;
				for(int i = 0 ; i < commParamInfos.size() ; i++){
					if(StringUtils.equals(commParamInfos.getData(i).getString("PARAM_CODE"),labelInfo.getString("PRODUCT_ID"))){
						isAdd = false;
						break;
					}
				}
				if(isAdd){
					result.add(labelInfo);
				}
			}
		}
		return result;
		
	}

	public IDataset queryUserSaleActivePackageInfos(IData paramdata) throws Exception {
		
//		return Dao.qryByCode("TD_B_PACKAGE","SEL_SALEACTIVE_PACKAGEINFO", paramdata,Route.CONN_CRM_CEN);
		
		IDataset result = new DatasetList();
		String labelId = paramdata.getString("LABEL_ID","-1");
		String productId = paramdata.getString("PRODUCT_ID","");
		IDataset labelInfos = labelInfoCache.get(labelId);
		if(IDataUtil.isEmpty(labelInfos)){
			labelInfos = UpcCall.qrySaleActivePackageByLabelId("-1".equals(labelId) ? "" : labelId);
			labelInfoCache.put(labelId, labelInfos);
		}
		IDataset commParamInfos = commParamCache.get("526");
		if(IDataUtil.isEmpty(commParamInfos)){
			commParamInfos = CommparaInfoQry.getCommByParaAttr("CSM", "526", "0898");
			commParamCache.put("526", commParamInfos);
		}
				
		if(IDataUtil.isNotEmpty(labelInfos)){
			for(Object obj : labelInfos){
				Boolean isAdd = true;
				IData labelInfo = (IData) obj;
				if(!StringUtils.equals(productId, labelInfo.getString("PRODUCT_ID",""))){
					isAdd = false;
				}
				for(int i = 0 ; i < commParamInfos.size() ; i++){
					if(StringUtils.equals(commParamInfos.getData(i).getString("PARAM_CODE"),labelInfo.getString("PACKAGE_ID"))){
						isAdd = false;
						break;
					}
				}
				if(isAdd){
					result.add(labelInfo);
				}
			}
		}
		return result;
	}

	


}
