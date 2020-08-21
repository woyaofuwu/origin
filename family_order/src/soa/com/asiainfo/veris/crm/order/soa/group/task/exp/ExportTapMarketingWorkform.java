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

public class ExportTapMarketingWorkform extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination pag) throws Exception {
        String[] keys=data.getNames();
        for(String key :keys){
        	if(key.startsWith("cond_")&&key.length()>5){
        		data.put(key.substring(5), data.get(key));
        	}
        }
		if(!getVisit().getCityCode().equals("HNSJ")){
			data.put("CITY_ID",getVisit().getCityCode());
		}
		
		IDataset friendBusinessList=StaticUtil.getStaticList("MARKETING_FRIENDBUSINESS_NAME");
        if (IDataUtil.isEmpty(friendBusinessList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_226,"友商名称参数未配置，请检查参数后继续操作");
        }
        IDataset lineTypeList=StaticUtil.getStaticList("MARKETING_LINETYPE");
        if (IDataUtil.isEmpty(lineTypeList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_226,"专线类型参数未配置，请检查参数后继续操作");
        }
        IDataset resultCodeList=StaticUtil.getStaticList("MARKETING_RESULT_CODE");
        if (IDataUtil.isEmpty(resultCodeList))
        {
        	CSAppException.apperr(VpmnUserException.VPMN_USER_226,"当前状态参数未配置，请检查参数后继续操作");
        }
        	
		IDataset output = CSAppCall.call("SS.TapMarketingSVC.selTapMarketingByCondition", data);
		
		for (int i=0,sizei=output.size();i<sizei;i++){
        	
        	IData dataIn=output.getData(i);
			String friendBusiness = dataIn.getString("FRIENDBUSINESS_NAME", "").trim();
			for(int j=0,sizej=friendBusinessList.size();j<sizej;j++){
			    IData friendBusinessData=friendBusinessList.getData(j);
			    if(friendBusinessData.getString("DATA_ID", "").equals(friendBusiness)){
			    	dataIn.put("FRIENDBUSINESS_NAME_STR", friendBusinessData.getString("DATA_NAME", ""));
			    	break;
			    }
			}
			
			String lineType = dataIn.getString("LINETYPE", "").trim();
			for(int z=0,sizez=lineTypeList.size();z<sizez;z++){
			    IData lineTypeData=lineTypeList.getData(z);
			    if(lineTypeData.getString("DATA_ID", "").equals(lineType)){
			    	dataIn.put("LINETYPE_STR", lineTypeData.getString("DATA_NAME", ""));
			    	break;
			    }
			}
			
			String resultCode = dataIn.getString("RESULT_CODE", "").trim();
			for(int x=0,sizex=resultCodeList.size();x<sizex;x++){
			    IData resultCodeData=resultCodeList.getData(x);
			    if(resultCodeData.getString("DATA_ID", "").equals(resultCode)){
			    	dataIn.put("RESULT_CODE_STR", resultCodeData.getString("DATA_NAME", ""));
			    	break;
			    }
			}
			
			
        }
        return output;
    }

}
