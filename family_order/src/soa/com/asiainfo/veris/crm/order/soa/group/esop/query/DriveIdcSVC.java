package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizService;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class DriveIdcSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;
	
	public void driveIdcToPool(IData param) throws Exception
	{
		String ibsysid = IDataUtil.chkParam(param, "IBSYSID");
		IData qrySubscribePoolParam1=new DataMap();
        qrySubscribePoolParam1.put("POOL_NAME", "ORDER_OrderNumFlag");
        qrySubscribePoolParam1.put("STATE", "A");
        qrySubscribePoolParam1.put("POOL_VALUE", ibsysid);
        IDataset qrySubscribePoolParamList1=ConfCrmQry.qrySubscribePool(qrySubscribePoolParam1);
        if(IDataUtil.isEmpty(qrySubscribePoolParamList1)||qrySubscribePoolParamList1.size()==0||
        		IDataUtil.isEmpty(qrySubscribePoolParamList1.getData(0))){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+ibsysid+"查询tf_b_eop_subscribe_pool表数据不存在(driveIdcToPool)!");
        }
        IData qrySubscribePoolParam=new DataMap();
        qrySubscribePoolParam.put("POOL_NAME", "ORDER_OrderNumFlag");
        qrySubscribePoolParam.put("STATE", "A");
        qrySubscribePoolParam.put("REL_IBSYSID", qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID"));
        IDataset qrySubscribePoolParamList=ConfCrmQry.qrySubscribePool(qrySubscribePoolParam);
        if(IDataUtil.isEmpty(qrySubscribePoolParamList)||qrySubscribePoolParamList.size()==0){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID")+"查询tf_b_eop_subscribe_pool 表数据REL_IBSYSID不存在(driveIdcToPool)!");
        }
        for (int s = 0; s < qrySubscribePoolParamList.size(); s++) {
        	IData qrySubscribePoolParamData=qrySubscribePoolParamList.getData(s);
	    	if (DataUtils.isNotEmpty(qrySubscribePoolParamData)) {
	        	String poolIbsysid=qrySubscribePoolParamData.getString("POOL_VALUE");
	        	if(!ibsysid.equals(poolIbsysid)){
	        		
	        		
	        		
	        		
	        		//查询TF_B_EWE
		        	IData eweparam = new DataMap();
		            eweparam.put("BI_SN", poolIbsysid);
		            IDataset eweInfos = Dao.qryByCodeParser("TF_B_EWE", "SEL_BY_BISN", eweparam, Route.getJourDb(BizRoute.getRouteId()));
		            if(IDataUtil.isEmpty(eweInfos)||IDataUtil.isEmpty(eweInfos.getData(0))
		            		||"".equals(eweInfos.getData(0).getString("BI_SN",""))){
		            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EWE表流程信息为空(driveIdcToPool)!");
		            }
		            String poolBusiformId=eweInfos.getData(0).getString("BUSIFORM_ID");
		            //查询TF_B_EWE_NODE
		            IData eweNodeparam = new DataMap();
		            eweNodeparam.put("BUSIFORM_ID", poolBusiformId);
		            IDataset eweNodeInfos = Dao.qryByCodeParser("TF_B_EWE_NODE", "SEL_BY_BUSIFORM_ID", eweNodeparam, Route.getJourDb(BizRoute.getRouteId()));
		            if(IDataUtil.isEmpty(eweNodeInfos)
			            		||eweNodeInfos.size()!=1
			            		||IDataUtil.isEmpty(eweNodeInfos.getData(0))
		        				||!"0".equals(eweNodeInfos.getData(0).getString("STATE",""))
		        				
	        				)
		            {
		            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EWE_NODE表数据不正常,强行停止(driveIdcToPool)!");
		            }
                	String busiformNodeId = eweNodeInfos.getData(0).getString("BUSIFORM_NODE_ID", "");
                	DriveIdcBean.updAutoTime(busiformNodeId, SysDateMgr.getOtherSecondsOfSysDate(60));//其他流程延迟1分钟处理
	        	}
	    	}
        }
        
	}
	
	public void updAutoTime(IData param) throws Exception{
		String busiformNodeId=param.getString("BUSIFORM_NODE_ID", "");
		String autoTime=param.getString("AUTO_TIME", "");
    	DriveIdcBean.updAutoTime(busiformNodeId, autoTime);
	}

	
	
}