package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ExportMobileCloudInfo extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination pag) throws Exception {
        String[] keys=data.getNames();
        for(String key :keys){
        	if(key.startsWith("cond_")&&key.length()>5){
        		data.put(key.substring(5), data.get(key));
        	}
        }
		
		IDataset typeCodeList=StaticUtil.getStaticList("MOBILECLOUD_TYPECODE");
        if (IDataUtil.isEmpty(typeCodeList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_226,"类别编码参数未配置，请检查参数后继续操作");
        }

        	
		IDataset output = CSAppCall.call("SS.MobileCloudInfoSVC.selMobileCloudInfoByCondition", data);
		
		for (int i=0,sizei=output.size();i<sizei;i++){
        	
        	IData dataIn=output.getData(i);
			String friendBusiness = dataIn.getString("TYPE_CODE", "").trim();
			for(int j=0,sizej=typeCodeList.size();j<sizej;j++){
			    IData typeCodeData=typeCodeList.getData(j);
			    if(typeCodeData.getString("DATA_ID", "").equals(friendBusiness)){
			    	dataIn.put("TYPE_CODE_STR", typeCodeData.getString("DATA_NAME", ""));
			    	break;
			    }
			}

			
			
        }
        return output;
    }

}
