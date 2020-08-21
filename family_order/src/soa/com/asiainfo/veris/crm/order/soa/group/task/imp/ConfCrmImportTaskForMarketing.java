
package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.PageUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ConfCrmImportTaskForMarketing extends ImportTaskExecutor
{

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception
    {
//    	System.out.print("ConfCrmImportTaskForMarketing-executeImport data:"+data);
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_138);
        }
        IDataset renewModeList=StaticUtil.getStaticList("MARKETING_RENEWMODE");
        if (IDataUtil.isEmpty(renewModeList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_226,"客户续签参数未配置，请检查参数后继续操作");
        }
        IDataset friendBusinessList= StaticUtil.getStaticList("MARKETING_FRIENDBUSINESS_NAME");
        if (IDataUtil.isEmpty(friendBusinessList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_226,"友商名称参数未配置，请检查参数后继续操作");
        }
        for (int i=0,sizei=dataset.size();i<sizei;i++){
        	IData dataIn=dataset.getData(i);
        	if("".equals(dataIn.getString("NOTIN_LINENAME",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"专线名称为必填值，请检查该参数是否填入！");
        	}
        	if("".equals(dataIn.getString("NOTIN_BANDWIDTH",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"专线带宽为必填值，请检查该参数是否填入！");
        	}
        	if("".equals(dataIn.getString("NOTIN_RSRV_STR2",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"专线资费为必填值，请检查该参数是否填入！");
        	}
        	if("".equals(dataIn.getString("NOTIN_OPPONENTMARKETINGCONTENT",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"对手营销活动内容为必填值，请检查该参数是否填入！");
        	}
        	if("".equals(dataIn.getString("NOTIN_OPPONENTENDDATE",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"对手签约到期时间为必填值，请检查该参数是否填入！");
        	}
        	if("".equals(dataIn.getString("NOTIN_RENEWMODE_STRING",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"续签方式为必填值，请检查该参数是否填入！");
        	}
        	if("".equals(dataIn.getString("NOTIN_CUSTRENEWCONTENT",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"客户续签需求为必填值，请检查该参数是否填入！");
        	}
        	if("".equals(dataIn.getString("NOTIN_FRIENDBUSINESS_NAME_STRING",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"友商名称为必填值，请检查该参数是否填入！");
        	}
        	if("".equals(dataIn.getString("NOTIN_CUST_NAME",""))){
                CSAppException.apperr(VpmnUserException.VPMN_USER_226,"客户名称为必填值，请检查该参数是否填入！");
        	}
        	 String renewModeStr = dataIn.getString("NOTIN_RENEWMODE_STRING", "").trim();
        	 for(int j=0,sizej=renewModeList.size();j<sizej;j++){
             	IData renewModeData=renewModeList.getData(j);
             	if(renewModeData.getString("DATA_NAME", "").equals(renewModeStr)){
             		dataIn.put("NOTIN_RENEWMODE", renewModeData.getString("DATA_ID", ""));
             		break;
             	}
        	 }
        	 String friendBusinessStr = dataIn.getString("NOTIN_FRIENDBUSINESS_NAME_STRING", "").trim();
        	 for(int z=0,sizez=friendBusinessList.size();z<sizez;z++){
              	IData friendBusinessData=friendBusinessList.getData(z);
              	if(friendBusinessData.getString("DATA_NAME", "").equals(friendBusinessStr)){
              		dataIn.put("NOTIN_FRIENDBUSINESS_NAME", friendBusinessData.getString("DATA_ID", ""));
              		break;
              	}
         	 }
        	 if (dataIn.getString("NOTIN_RENEWMODE")==null)
             {
                 CSAppException.apperr(VpmnUserException.VPMN_USER_226,"客户续签方式'"+renewModeStr+"'不符合要求");
             	break;
             }
        	 if (dataIn.getString("NOTIN_FRIENDBUSINESS_NAME")==null)
             {
                 CSAppException.apperr(VpmnUserException.VPMN_USER_226,"友商名称'"+friendBusinessStr+"'不符合要求");
             	break;
             }
        	 
        }
        
       
        
        SharedCache.set("MARKETING_INFOS", dataset);
        return null;
    }

}
