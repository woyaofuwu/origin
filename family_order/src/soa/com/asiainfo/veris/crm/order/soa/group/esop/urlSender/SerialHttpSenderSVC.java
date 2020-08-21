package com.asiainfo.veris.crm.order.soa.group.esop.urlSender;

import java.net.URL;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

/**
 * eos
 *
 * @author ckh
 * @date 2018/3/27.
 */
public class SerialHttpSenderSVC extends GroupOrderService
{
	private static final long serialVersionUID = 1L;
	
    public IDataset serialHttpSender(IData param) throws Exception
    {
		String url = param.getString("url");
		String paramName = param.getString("paramName");
		String content = param.getString("paramContent");
		IDataset list=new  DatasetList();
        IData params = new DataMap();
		String svc = param.getString("svc");
        if("ESOP".equals(paramName)){

        	if(content.indexOf("{")==0){
    			IData paramin=new DataMap(content);
    			IData paraminNew=new DataMap(content);
    			String ibsysid=paramin.getString("IBSYSID", "");
    			String busiformId=paramin.getString("BUSIFORM_ID", "");
    			if(ibsysid==null||busiformId==null){
            		params.put("returnVal", "指定参数为空 ibsysid:"+ibsysid+" busiformId:"+busiformId);
            		list.add(params);
                    return list;
    			}
    			String stepId=paramin.getString("STEP_ID", "");

    			IData eweparam = new DataMap();
	            eweparam.put("BI_SN", ibsysid);
	            IDataset eweInfos = Dao.qryByCodeParser("TF_B_EWE", "SEL_BY_BISN", eweparam, Route.getJourDb(Route.CONN_CRM_CG));
	            if(eweInfos.size()==0){
            		params.put("returnVal", "根据IBSYSID"+ibsysid+"未获取到TF_B_EWE数据");
            		list.add(params);
                    return list;
    			}else{
    				int aaa=0;
    				for(int i=0,size=eweInfos.size();i<size;i++){
    					IData data=eweInfos.getData(i);
    					if(data.getString("BUSIFORM_ID").equals(busiformId)){
    						aaa=i;
    						break;
    					}
    				}
    				paraminNew.putAll(eweInfos.getData(aaa));
    			}
    			IData eweNodeparam = new DataMap();
    			eweNodeparam.put("BUSIFORM_ID", busiformId);
    			IDataset eweNode=Dao.qryByCodeParser("TF_B_EWE_NODE", "SEL_BY_BUSIFORM_ID", eweNodeparam, Route.getJourDb(Route.CONN_CRM_CG));
    			if(eweNode.size()==0){
            		params.put("returnVal", "根据IBSYSID"+ibsysid+"未获取到TF_B_EWE数据");
            		list.add(params);
                    return list;
    			}else{
    				int aaa=0;
    				for(int i=0,size=eweNode.size();i<size;i++){
    					IData data=eweNode.getData(i);
    					if("2".equals(data.getString("STATE"))||"M".equals(data.getString("STATE"))){
    						aaa=i;
    						break;
    					}
    				}
    				paraminNew.putAll(eweNode.getData(aaa));
    			}
    			String extId=null;
				IData paramStepUpd = new DataMap();

    			if(stepId!=null&&!"".equals(stepId)){
    				IData paramStep = new DataMap();
    				paramStep.put("BUSIFORM_NODE_ID", paraminNew.getString("BUSIFORM_NODE_ID"));
    				paramStep.put("STEP_ID", stepId);
    		        Dao.qryByCode("TF_B_EWE_STEP", "SEL_BY_BUSIFORMNODEID_STEP", paramStep, Route.getJourDb(Route.CONN_CRM_CG));
    				IDataset stepList=Dao.qryByCodeParser("TF_B_EWE_STEP", "SEL_BY_BUSIFORMNODEID_STATE", paramStep, Route.getJourDb(Route.CONN_CRM_CG));
    				if(stepList.size()>0){
        				paramStepUpd=stepList.getData(0);
        				extId=paramStepUpd.getString("EXT_ID");
        			}else{
                		params.put("returnVal", "STEP_ID在该订单正在执行流程中不存在");
                		list.add(params);
                        return list;
        			}
    			}else{
    				IData paramStep = new DataMap();
    				paramStep.put("BUSIFORM_NODE_ID", paraminNew.getString("BUSIFORM_NODE_ID"));
    				paramStep.put("STATE", "M");
    				IDataset stepList=Dao.qryByCodeParser("TF_B_EWE_STEP", "SEL_BY_BUSIFORMNODEID_STATE", paramStep, Route.getJourDb(Route.CONN_CRM_CG));
        			if(stepList.size()>0){
        				paramStepUpd=stepList.getData(0);
        				stepId=paramStepUpd.getString("STEP_ID");
        				extId=paramStepUpd.getString("EXT_ID");

        			}else{
                		params.put("returnVal", "该订单正在执行流程中不存在错误节点或者使用输入STEP_ID");
                		list.add(params);
                        return list;
        			}
    			}
				paraminNew.putAll(paramStepUpd);

    			IData paramStep = new DataMap();
    			paramStep.put("BPM_TEMPLET_ID", paraminNew.getString("BPM_TEMPLET_ID"));
    			paramStep.put("NODE_ID", paraminNew.getString("NODE_ID"));
    			if(extId!=null){
        			paramStep.put("EXT_ID", extId);
    			}
    			paramStep.put("VALID_TAG", "0");
    			paramStep.put("STEP_ID", stepId);

    			IDataset stepConfig=Dao.qryByCode("TD_B_EWE_STEP", "SEL_BY_STEPBPMNODEEXT_VALID", paramStep, Route.CONN_CRM_CEN);
	    		if(stepConfig.size()>0){
	    			String loadSvc=stepConfig.getData(0).getString("LOAD_SVC");
	    			if(loadSvc!=null){
	    				svc=loadSvc;
	    			}
	    		}
    			paraminNew.put("IBSYSID", ibsysid);
	    		//执行业务
    			IDataset listOut = CSAppCall.call(svc, paraminNew);
    			if(listOut!=null&&listOut.size()>0){
    				
    				paramStepUpd.put("BUSIFORM_ID", busiformId);
    				SQLParser sql = new SQLParser(paramStepUpd);
    				sql.addSQL(" UPDATE TF_B_EWE_STEP W SET W.STATE='9' WHERE W.BUSIFORM_ID = :BUSIFORM_ID ");
    				sql.addSQL(" AND W.BUSIFORM_NODE_ID = :BUSIFORM_NODE_ID  ");
    				sql.addSQL(" AND W.ACCEPT_MONTH = :ACCEPT_MONTH  ");
    				sql.addSQL(" AND W.STEP_ID = :STEP_ID  ");
    				sql.addSQL(" AND W.EXT_ID = :EXT_ID  ");
    				int stepFlag=Dao.executeUpdate(sql, Route.getJourDb(BizRoute.getRouteId()));
    				params.put("returnVal","svc:"+svc+" stepFlag(1成功):"+stepFlag+" return:"+listOut);
    				list.add(params);
    		        return list;
    			}else{
            		params.put("returnVal", "返回为空");
    			}
        	}else{
        		params.put("returnVal", "未定义的入参标识 需使用{}类型参数"+content);
        	}
	        list.add(params);

        }else if(url==null||"".equals(url)){
			IData paramin=new DataMap();
			content = content.replaceAll("&lt;", "<");
			content = content.replaceAll("&gt;", ">");
			paramin.put(paramName, content);
			IDataset listOut = CSAppCall.call(svc, paramin);
			boolean falg=true;
			if(listOut!=null&&listOut.size()>0){
				IData dataOut=listOut.getData(0);
				if(DataUtils.isNotEmpty(dataOut)){
					String xmlInfo=dataOut.getString("XML_INFO","");
					if(!"".equals(xmlInfo)){
						xmlInfo = xmlInfo.replaceAll(" ", "&nbsp;");
						xmlInfo = xmlInfo.replaceAll(">", "&gt;");
						xmlInfo = xmlInfo.replaceAll("<", "&lt;");
						xmlInfo = xmlInfo.replaceAll("\n", "<br />");
				        params.put("returnVal", xmlInfo);
				        list.add(params);
				        falg=false;
					}
				}
			}
			if(falg){
				params.put("returnVal", listOut);
		        list.add(params);
			}
		}
		else {
			String content1=content.replace("&lt;", "<");
			String content2=content1.replace("&gt;", ">");
	   		System.out.print("\n====================content2==========================\n"+content2);

			SerialHttpSender sender = new SerialHttpSender();

			String response = sender.sendHttpData( new URL(url), new String[] { paramName }, new String[] { content2 },"UTF-8");
			
			/*String response = reqBML.toString();*/
			response = response.replaceAll(" ", "&nbsp;");
			response = response.replaceAll(">", "&gt");
			response = response.replaceAll("<", "&lt");
			response = response.replaceAll("\n", "<br />");
	        params.put("returnVal", response);
	        list.add(params);
		}
        return list;
		
    }
}
