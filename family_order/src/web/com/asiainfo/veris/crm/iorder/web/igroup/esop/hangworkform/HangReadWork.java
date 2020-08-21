package com.asiainfo.veris.crm.iorder.web.igroup.esop.hangworkform;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class HangReadWork extends CSBasePage
{

    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
      
        queryUnReadWork(data);
        
        String busiFormId = data.getString("BUSIFORM_NODE_ID");
        
        updateWorkForm(busiFormId);

    }
   
    private void queryUnReadWork(IData data) throws Exception
    {
    	IData workInfo = new DataMap();
    	/*IData input = new DataMap();
    	input.put("INFO_SIGN", data.getString("BUSIFORM_NODE_ID",""));
    	IDataset workList = CSViewCall.call(this, "SS.WorkTaskMgrSVC.queryWorkInfoByInfoSign", data);
    	if(IDataUtil.isEmpty(workList))
        {
           // CSViewException.apperr(GrpException.CRM_GRP_713, "该待阅任务已被删除，请重新查询!");
        }
    	workInfo = workList.first();*/
    	IData param = new DataMap();
    	param.put("BUSIFORM_NODE_ID", data.getString("BUSIFORM_NODE_ID",""));
    	IData eweNodeInfo = new DataMap();
    	eweNodeInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryByBusiformNodeId", param);
    	if(IDataUtil.isEmpty(eweNodeInfo)){
    		eweNodeInfo = CSViewCall.callone(this, "SS.EweNodeTraSVC.qryEweNodeTraByBusiformNodeId", param);
    		if(IDataUtil.isEmpty(eweNodeInfo)){
    			eweNodeInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweHNodeByBusiformIdState", param);
    		}
    	}
    	param.put("BUSIFORM_NODE_ID", eweNodeInfo.getString("PRE_BUSIFORM_NODE_ID")); 
    	
    	
    	//param.put("STATE", "0");
    	IDataset eweAsynInfos = CSViewCall.call(this, "SS.EweAsynSVC.qryInfosByBusiformNodeId", param);
    	if(IDataUtil.isEmpty(eweAsynInfos)){
    		param.put("BUSIFORM_ID", data.getString("BUSIFORM_ID",""));
    		param.put("NODE_ID", data.getString("NODE_ID",""));
    		eweAsynInfos = CSViewCall.call(this, "SS.EweAsynSVC.queryByBusiformNode", param);
    	}
    	IData work = new DataMap();
    	if(IDataUtil.isNotEmpty(eweAsynInfos)){
    		for(int i = 0;i < eweAsynInfos.size();i++){
    			IData eweAsynInfo = eweAsynInfos.getData(i);
    			work.put(eweAsynInfo.getString("ATTR_CODE"), eweAsynInfo.getString("ATTR_VALUE"));
    			String mebOfferCode = pageutil.getStaticValue("TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "HANGCHANGE_CRM_ESOP", eweAsynInfo.getString("ATTR_CODE")});
    			if(StringUtils.isNotEmpty(mebOfferCode)){
    				work.put(mebOfferCode,eweAsynInfo.getString("ATTR_VALUE"));
    			}
    		}
    	}
    	if(StringUtils.isEmpty(work.getString("applyResult"))){
    		work.put("applyResult", "1");
    		work.put("applyContent", "");
    	}
    	if(IDataUtil.isEmpty(work)){
    		
    	}
    	param.put("STATE", "0");
    	String nodeId =  data.getString("NODE_ID","");
    	String topic =  "";
    	if("waitConfirm".equals(nodeId)){
    		topic = "专线解挂";
    	}else{
    		topic = "专线挂起";
    	}
    	workInfo.put("INFO_TOPIC", "您有一笔待阅工单："+topic);
    	workInfo.putAll(work);
        setWorkInfo(workInfo);
    }
    
    public void updateWorkForm(String busiFormId)throws Exception{
   	 IData map = new DataMap();
   	 map.put("INFO_SIGN", busiFormId);
   	 map.put("INFO_CHILD_TYPE", "41");
   	 IData infoId = CSViewCall.callone(this, "SS.WorkTaskMgrSVC.queryWorkInfoByInfoSign", map); //存attr表
   	 IData temp = new DataMap();
   	 temp.put("INFO_STATUS", "9");
   	 temp.put("INFO_ID", infoId.getString("INFO_ID"));
   	 
   	 CSViewCall.call(this, "SS.WorkTaskMgrSVC.updateOpTaskInfoStatus", temp); //设置为已读待阅
   }
    public abstract void setWorkInfo(IData workInfo) throws Exception;
}
