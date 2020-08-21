package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ImportMobileCloudInfo extends ImportTaskExecutor
{

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception
    {
    	IDataset typeCodeList=StaticUtil.getStaticList("MOBILECLOUD_TYPECODE");
        if (IDataUtil.isEmpty(typeCodeList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_226,"类别编码参数未配置，请检查参数后继续操作");
        }
        
        for (int i=0,sizei=dataset.size();i<sizei;i++){
        	IData dataIn=dataset.getData(i);
        	if("".equals(dataIn.getString("INST_ID",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"唯一标示为必填值，该参数不可删除也不可修改！");
        	}
        	boolean falg=false;
        	for(int z=0,sizez=typeCodeList.size();z<sizez;z++){
    		    IData typeCodeData=typeCodeList.getData(z);
    		    if(typeCodeData.getString("DATA_NAME", "").equals(dataIn.getString("TYPE_CODE_STR"))){
    		    	dataIn.put("TYPE_CODE", typeCodeData.getString("DATA_ID", ""));
    		    	falg=true;
    		    	break;
    		    }
    		}
        	if(!falg){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"类别编码为非法值，该参数请从下拉列表中选择正确参数！");
        	}
        	
        }
        
		IDataset output = CSAppCall.call("SS.MobileCloudInfoSVC.selMobileCloudInfoByCondition", data);
		IDataset newdataset=new DatasetList();
		for(int j=0,sizej=output.size();j<sizej;j++){
			IData outputData=output.getData(j);
			if(IDataUtil.isNotEmpty(outputData)){
				for(int k=0,sizek=dataset.size();k<sizek;k++){
					IData datasetData=dataset.getData(k);
					if(IDataUtil.isNotEmpty(datasetData)){
						if(datasetData.getString("INST_ID","").equals(outputData.getString("INST_ID"))){
							
							if(StringUtils.isNotBlank(datasetData.getString("TYPE_CODE"))
									&&!datasetData.getString("TYPE_CODE").equals(outputData.getString("TYPE_CODE"))){
								datasetData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
								datasetData.put("UPDATE_DEPARTID", getVisit().getDepartId());
								newdataset.add(datasetData);
							}
							break;
						}
					}
				}
			}
			
		}
//        data.put("StaffId", getVisit().getStaffId());
//        data.put("DepartId", getVisit().getDepartId());
        data.put("MobileCloudList", newdataset);
		CSAppCall.call("SS.MobileCloudInfoSVC.updMobileCloudInfo", data);

       
        
//        SharedCache.set("MARKETING_INFOS", dataset);
        return null;
    }

}
