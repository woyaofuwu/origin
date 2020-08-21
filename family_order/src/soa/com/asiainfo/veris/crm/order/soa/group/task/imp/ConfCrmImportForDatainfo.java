
package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ConfCrmImportForDatainfo extends ImportTaskExecutor
{

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception
    {
        for (int i=0,sizei=dataset.size();i<sizei;i++){
        	IData dataIn=dataset.getData(i);
        	if("".equals(dataIn.getString("PRODUCT_NO",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"专线实例号为必填值，请检查该参数是否填入！");
        	}
        	
        	if("7012".equals(data.getString("cond_OFFER_CODE", ""))||"70121".equals(data.getString("cond_OFFER_CODE", ""))||"70122".equals(data.getString("cond_OFFER_CODE", ""))){
        		if("".equals(dataIn.getString("PORT_CONTACT_A",""))){
                    CSAppException.apperr(VpmnUserException.VPMN_USER_226,"A端用户技术联系人为必填值，请检查该参数是否填入！");
            	}
            	if("".equals(dataIn.getString("PORT_CONTACT_PHONE_A",""))){
                    CSAppException.apperr(VpmnUserException.VPMN_USER_226,"A端用户技术联系人电话为必填值，请检查该参数是否填入！");
            	}
        		if("".equals(dataIn.getString("PORT_CONTACT_Z",""))){
                    CSAppException.apperr(VpmnUserException.VPMN_USER_226,"Z端用户技术联系人为必填值，请检查该参数是否填入！");
            	}
            	if("".equals(dataIn.getString("PORT_CONTACT_PHONE_Z",""))){
                    CSAppException.apperr(VpmnUserException.VPMN_USER_226,"Z端用户技术联系人电话为必填值，请检查该参数是否填入！");
            	}
        	}else{
        		if("".equals(dataIn.getString("PORT_CONTACT_A",""))){
                    CSAppException.apperr(VpmnUserException.VPMN_USER_226,"用户技术联系人为必填值，请检查该参数是否填入！");
            	}
            	if("".equals(dataIn.getString("PORT_CONTACT_PHONE_A",""))){
                    CSAppException.apperr(VpmnUserException.VPMN_USER_226,"用户技术联系人电话为必填值，请检查该参数是否填入！");
            	}
        	}
        }
        
       
        
        SharedCache.set("MARKETING_INFOS", dataset);
        return null;
    }

}
