package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class DredgeSmartNetworkSVC extends CSBizService{
	
	 /**
     * getDevice
     * @param cycle
     * @throws Exception
     * @author 
     */
	 public IData getDevice(IData param) throws Exception
	    {
	    	IData result = new DataMap();
	    	String typeCode = param.getString("TYPE_CODE");
	    	if(StringUtils.isNotBlank(typeCode)){
	    		IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",typeCode,null);
	    		if(IDataUtil.isNotEmpty(commparaInfos9211)){
	    			result.put("X_RESULTCODE",  "0000");
		    		result.put("X_RESULTINFO",  commparaInfos9211.getData(0).getString("PARA_CODE17"));
		    		
		    		String paraCode12 = commparaInfos9211.getData(0).getString("PARA_CODE12");
	    			if("VIP".equals(paraCode12)){
	    				String resKindCode = typeCode.substring(typeCode.length()-4, typeCode.length());
	    				boolean flag = true;
	    				IDataset activeStockInfos = ActiveStockInfoQry.querySparkPlans("0898",CSBizBean.getVisit().getStaffId(),resKindCode);
	    				if(IDataUtil.isNotEmpty(activeStockInfos)){
	    					String surplusValue = activeStockInfos.first().getString("SURPLUS_VALUE","1");
	    					if(Integer.parseInt(surplusValue)>0){
	    						flag = false;
	    					}
	    				}
	    				if(flag){
	    					CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工号下的礼包数量为0，不能办理！");
	    				}
	    			}
	    		}
	    	}
	    	
	    	return result;
	    }

}
