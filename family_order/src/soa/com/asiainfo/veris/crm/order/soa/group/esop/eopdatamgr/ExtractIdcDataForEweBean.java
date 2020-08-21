package com.asiainfo.veris.crm.order.soa.group.esop.eopdatamgr;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.ConfCrmQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EopParamTransQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttachBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsHBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustContractInfoQrySVC;



public class ExtractIdcDataForEweBean {
	public IDataset returnCommon(IData eomsData) throws Exception
	{
		String subIbsysId = eomsData.getString("SUB_IBSYSID");
        String operType = eomsData.getString("OPER_TYPE");
        IDataset retData = new DatasetList();
        /*IData orderInfoReq= new DataMap();
        IDataset orderInfoReqList = new DatasetList();
        IData customerNumber = new DataMap();
        IData subscribeInfo=new DataMap();
        if(!"".equals(eomsData.getString("IBSYSID",""))){
			IDataset subscribeList=WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(eomsData.getString("IBSYSID",""));
	        if (DataUtils.isNotEmpty(subscribeList))
	        {
	        	subscribeInfo=subscribeList.getData(0);
	        }
		}
        customerNumber.put("CustomerNumber", subscribeInfo.getString("GROUP_ID",""));
        orderInfoReqList.add(customerNumber);
        
        IData orderNumber = new DataMap();
        
        orderNumber.put("OrderNumber", eomsData.getString("SERIALNO",""));
//        orderNumber.put("OrderNumber", eomsData.getString("IBSYSID",""));
        orderInfoReqList.add(orderNumber);
        
		IData productOrderInfo = new DataMap();
        IDataset productOrderInfoList = new DatasetList();
        
        IData productOrderNumber = new DataMap();
        productOrderNumber.put("ProductOrderNumber", eomsData.getString("SERIALNO",""));
        productOrderInfoList.add(productOrderNumber);
        
        IData productOrderID = new DataMap();
        if(!"".equals(subIbsysId)){
        	IDataset productAttrInfos=WorkformAttrBean.qryAttrBySubIbsysidAndRecordNumGroupSeq(subIbsysId,"0", "0");
            for (int z = 0; z < productAttrInfos.size(); z++) {
        		IData productAttrInfo=productAttrInfos.getData(z);
        		if("IDC_SERIAL_NUMBER".equals(productAttrInfo.getString("ATTR_CODE", ""))){
    		        productOrderID.put("ProductOrderID", productAttrInfo.getString("ATTR_VALUE", ""));
    		        break;
        		}
        	}
        }else{
	        productOrderID.put("ProductOrderID", "");
        }
        
        productOrderInfoList.add(productOrderID);
        String RspCodeStr = eomsData.getString("RspCode","99");
        IData RspCode = new DataMap();
        RspCode.put("RspCode", RspCodeStr);
        productOrderInfoList.add(RspCode);
        String RspDescStr = eomsData.getString("RspDesc","ERR");
        IData RspDesc = new DataMap();
        RspDesc.put("RspDesc", RspDescStr);
        productOrderInfoList.add(RspDesc);

        productOrderInfo.put("ProductOrderInfo", productOrderInfoList);
        orderInfoReqList.add(productOrderInfo);
        
        String RspCodeStr = eomsData.getString("RspCode","99");
        IData RspCode = new DataMap();
        RspCode.put("RspCode", RspCodeStr);
        orderInfoReqList.add(RspCode);
        String RspDescStr = eomsData.getString("RspDesc","ERR");
        IData RspDesc = new DataMap();
        RspDesc.put("RspDesc", RspDescStr);
        orderInfoReqList.add(RspDesc);
        
        orderInfoReq.put("OrderInfoReq", orderInfoReqList);
        
        retData.add(orderInfoReq);*/
        
        
        String BIPCodeStr = eomsData.getString("BIPCode","");
        String ActivityCodeStr = eomsData.getString("ActivityCode","");
        String ActionCodeStr = eomsData.getString("ActionCode","1");
        String SessionIDStr = eomsData.getString("SERIALNO","ESOP00000000");
        String TransIDOStr = eomsData.getString("SERIALNO","ESOP00000000");
        String TransIDOTimeStr = eomsData.getString("TransIDOTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        String TransIDHStr = eomsData.getString("TransIDH","");

        String RspTypeStr = eomsData.getString("RspType","2");
        String RspCodeStr = eomsData.getString("RspCode","2999");
        String RspDescStr = eomsData.getString("RspDesc","ERR");
        
        IData InterBOSS= new DataMap();
        IDataset InterBOSSList = new DatasetList();
        
        IData BIPType= new DataMap();
        IDataset BIPTypeList = new DatasetList();
        
        IData BIPCode= new DataMap();
        BIPCode.put("BIPCode", BIPCodeStr);
        BIPTypeList.add(BIPCode);
        IData ActivityCode= new DataMap();
        ActivityCode.put("ActivityCode", ActivityCodeStr);
        BIPTypeList.add(ActivityCode);
        IData ActionCode= new DataMap();
        ActionCode.put("ActionCode", ActionCodeStr);
        BIPTypeList.add(ActionCode);

        
        BIPType.put("BIPType", BIPTypeList);
        InterBOSSList.add(BIPType);
        
        IData TransInfo= new DataMap();
        IDataset TransInfoList = new DatasetList();
        
        IData SessionID= new DataMap();
        SessionID.put("SessionID", SessionIDStr);
        TransInfoList.add(SessionID);
        IData TransIDO= new DataMap();
        TransIDO.put("TransIDO", TransIDOStr);
        TransInfoList.add(TransIDO);
        IData TransIDOTime= new DataMap();
        TransIDOTime.put("TransIDOTime", TransIDOTimeStr);
        TransInfoList.add(TransIDOTime);
        IData TransIDH= new DataMap();
        TransIDH.put("TransIDH", TransIDHStr);
        TransInfoList.add(TransIDH);
        IData TransIDHTime= new DataMap();
        TransIDHTime.put("TransIDHTime", TransIDHStr);
        TransInfoList.add(TransIDHTime);
        
        TransInfo.put("TransInfo", TransInfoList);
        InterBOSSList.add(TransInfo);
        
        IData Response= new DataMap();
        IDataset ResponseList = new DatasetList();
        
        IData RspType= new DataMap();
        RspType.put("RspType", RspTypeStr);
        ResponseList.add(RspType);
        IData RspCode= new DataMap();
        RspCode.put("RspCode", RspCodeStr);
        ResponseList.add(RspCode);
        IData RspDesc= new DataMap();
        RspDesc.put("RspDesc", RspDescStr);
        ResponseList.add(RspDesc);
        
        Response.put("Response", ResponseList);
        InterBOSSList.add(Response);
        
        InterBOSS.put("InterBOSS", InterBOSSList);
        
        retData.add(InterBOSS);

        return retData;

	}
	
	public IDataset checkInData(IData eomsData) throws Exception
	{
		String ibsysId=eomsData.getString("IBSYSID");
//	    IData qrySubscribePoolParam1=new DataMap();
//        qrySubscribePoolParam1.put("POOL_NAME", "ORDER_OrderNumFlag");
////        qrySubscribePoolParam1.put("STATE", "F");
//        qrySubscribePoolParam1.put("POOL_VALUE",ibsysId );
//        IDataset qrySubscribePoolParamList1=ConfCrmQry.qrySubscribePool(qrySubscribePoolParam1);
//        if(IDataUtil.isEmpty(qrySubscribePoolParamList1)||qrySubscribePoolParamList1.size()==0||
//        		IDataUtil.isEmpty(qrySubscribePoolParamList1.getData(0))){
//        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+eomsData.getString("IBSYSID")+"查询tf_b_eop_subscribe_pool表数据不存在!");
//        }
//        String relIbsysid=qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID");
		IData eomsInfoNow=new DataMap();
        IDataset eomsInfoList = WorkformEomsBean.qryworkformEOMSByIbsysidRecordNumSheettype(ibsysId, "0","-1");
        for (int e=0,esize=eomsInfoList.size();e<esize;e++){ 
        	IData eomsInfoData=eomsInfoList.getData(e);        	
        	if("replyIdcWorkSheet".equals(eomsInfoData.getString("OPER_TYPE"))){
        		eomsInfoNow=eomsInfoData;
        		break;
			}
        }
        if (DataUtils.isEmpty(eomsInfoNow)||"".equals(eomsInfoNow.getString("SERIALNO","")))
        {
        	eomsInfoList=WorkformEomsBean.qryFinishEOMSByIbsysidRecordNumSheettype(ibsysId, "0","-1");
        	for (int e=0,esize=eomsInfoList.size();e<esize;e++){
            	IData eomsInfoData=eomsInfoList.getData(e);        	
            	if("replyIdcWorkSheet".equals(eomsInfoData.getString("OPER_TYPE"))){
            		eomsInfoNow=eomsInfoData;
            		break;
    			}
            }
            if (DataUtils.isEmpty(eomsInfoNow)||"".equals(eomsInfoNow.getString("SERIALNO","")))
            {
            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+ibsysId+"查询TF_B_EOP_EOMS异步回复数据不存在!");
            }
        }
        String serialNo=eomsInfoNow.getString("SERIALNO");
        IDataset retData = new DatasetList();
        
        IData StatusReport= new DataMap();
        IDataset StatusReportList = new DatasetList();
        
        IData OrderNumber= new DataMap();
        OrderNumber.put("OrderNumber", serialNo);
        StatusReportList.add(OrderNumber);
        IData ProductStatusReport= new DataMap();
        IDataset ProductStatusReportList = new DatasetList();
        
        IData ProductOrderNumber= new DataMap();
        ProductOrderNumber.put("ProductOrderNumber",ibsysId);
        ProductStatusReportList.add(ProductOrderNumber);
        
        IData Status= new DataMap();
        Status.put("Status",eomsData.getString("Status","0"));
        ProductStatusReportList.add(Status);
        
        String dateStr=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        IData TimeStamp= new DataMap();
        TimeStamp.put("TimeStamp",eomsData.getString("TimeStamp",dateStr));
        ProductStatusReportList.add(TimeStamp);
        
        IData FeeTime= new DataMap();
        FeeTime.put("FeeTime",eomsData.getString("FeeTime",dateStr));
        ProductStatusReportList.add(FeeTime);
        
        ProductStatusReport.put("ProductStatusReport", ProductStatusReportList);
        StatusReportList.add(ProductStatusReport);


        StatusReport.put("StatusReport", StatusReportList);

        retData.add(StatusReport);
        return retData;
	}

	public IDataset makeMainData(IData eomsData) throws Exception
    {
		// 1- 首先查TF_B_EOP_EOMS表
        String subIbsysId = eomsData.getString("SUB_IBSYSID");
        String operType = eomsData.getString("OPER_TYPE");
//        IDataset configInfos = EweConfigQry.qryByConfigName("STATIC_EOMS_URL", "0");
//    	String url = "";
//		url = configInfos.first().getString("PARAMVALUE", "");
        IData eomsInfo = WorkformEomsBean.queryBySubIbsysIdAndOperTypeDesc(subIbsysId, operType);
        if (DataUtils.isEmpty(eomsInfo))
        {
            return new DatasetList();
        }
        
		IData subscribeInfo=new DataMap();
		IDataset subscribeList=WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(eomsData.getString("IBSYSID",""));
        if (DataUtils.isNotEmpty(subscribeList))
        {
        	subscribeInfo=subscribeList.getData(0);
        }
        String nowDate=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        IDataset retData = new DatasetList();
        
        IData orderInfoReq= new DataMap();
        IDataset orderInfoReqList = new DatasetList();
        IDataset orderInfoReqAttachmentList = new DatasetList();

        IData oprTime = new DataMap();
        oprTime.put("OprTime", nowDate);
        orderInfoReqList.add(oprTime);
        
        IData customerNumber = new DataMap();
        customerNumber.put("CustomerNumber", subscribeInfo.getString("GROUP_ID",""));
        orderInfoReqList.add(customerNumber);
        
        IData orderNumber = new DataMap();
        
        orderNumber.put("OrderNumber", eomsInfo.getString("SERIALNO",""));
//        orderNumber.put("OrderNumber", eomsData.getString("IBSYSID",""));
        orderInfoReqList.add(orderNumber);
        
        IData oprType = new DataMap();
        String bpmTempletId=eomsData.getString("BPM_TEMPLET_ID","");
        getEweConfigQry(oprType,"IDC_OPERTYPE","PARAMVALUE",bpmTempletId,"PARAMNAME","OprType");
        orderInfoReqList.add(oprType);
        
        IData emergencyDegree = new DataMap();
//        emergencyDegree.put("EmergencyDegree", eomsData.getString("SUB_IBSYSID",""));
        getEweConfigQry(emergencyDegree,"IDC_EMERGENCYDEGREE","PARAMNAME",subscribeInfo.getString("RSRV_STR3", ""),"PARAMVALUE","EmergencyDegree");
        orderInfoReqList.add(emergencyDegree);
        //TODO
        //多个产品参数 begin
        IData qrySubscribePoolParam1=new DataMap();
        qrySubscribePoolParam1.put("POOL_NAME", "ORDER_OrderNumFlag");
        qrySubscribePoolParam1.put("STATE", "F");
        qrySubscribePoolParam1.put("POOL_VALUE", eomsData.getString("IBSYSID"));
        IDataset qrySubscribePoolParamList1=ConfCrmQry.qrySubscribePool(qrySubscribePoolParam1);
        if(IDataUtil.isEmpty(qrySubscribePoolParamList1)||qrySubscribePoolParamList1.size()==0||
        		IDataUtil.isEmpty(qrySubscribePoolParamList1.getData(0))){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+eomsData.getString("IBSYSID")+"查询tf_b_eop_subscribe_pool表数据不存在!");
        }
        IData qrySubscribePoolParam=new DataMap();
        qrySubscribePoolParam.put("POOL_NAME", "ORDER_OrderNumFlag");
        qrySubscribePoolParam.put("STATE", "F");
        qrySubscribePoolParam.put("REL_IBSYSID", qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID"));
        IDataset qrySubscribePoolParamList=ConfCrmQry.qrySubscribePool(qrySubscribePoolParam);
        if(IDataUtil.isEmpty(qrySubscribePoolParamList)||qrySubscribePoolParamList.size()==0){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID")+"查询tf_b_eop_subscribe_pool 表数据REL_IBSYSID不存在!");
        }
        
        
        IData productOrderInfo = new DataMap();
        IDataset productOrderInfoList = new DatasetList();
        for (int s = 0; s < qrySubscribePoolParamList.size(); s++) {
        	IData qrySubscribePoolParamData=qrySubscribePoolParamList.getData(s);
	    	if (DataUtils.isNotEmpty(qrySubscribePoolParamData)) {
	        	String poolIbsysid=qrySubscribePoolParamData.getString("POOL_VALUE");
	        	String poolbuCode=qrySubscribePoolParamData.getString("POOL_CODE");

	        	//查询TF_B_EWE
	        	IData eweparam = new DataMap();
	            eweparam.put("BI_SN", poolIbsysid);
	            IDataset eweInfos = Dao.qryByCodeParser("TF_B_EWE", "SEL_BY_BISN", eweparam, Route.getJourDb(BizRoute.getRouteId()));
	            if(IDataUtil.isEmpty(eweInfos)||IDataUtil.isEmpty(eweInfos.getData(0))
	            		||"".equals(eweInfos.getData(0).getString("BI_SN",""))){
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EWE表流程信息为空!");
	            }
	            String poolBusiformId=eweInfos.getData(0).getString("BUSIFORM_ID");
	            String poolBusiCode=eweInfos.getData(0).getString("BUSI_CODE");
	            String bpmTempletIdPool=eweInfos.getData(0).getString("BPM_TEMPLET_ID");
	            //查询TF_B_EWE_NODE
	            IData eweNodeparam = new DataMap();
	            eweNodeparam.put("BUSIFORM_ID", poolBusiformId);
	            IDataset eweNodeInfos = Dao.qryByCodeParser("TF_B_EWE_NODE", "SEL_BY_BUSIFORM_ID", eweNodeparam, Route.getJourDb(BizRoute.getRouteId()));
	            if(poolIbsysid.equals(eomsData.getString("IBSYSID"))&&
	            		(IDataUtil.isEmpty(eweNodeInfos)||eweNodeInfos.size()!=2
		            	)
	              )
	            {
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EWE_NODE表数据不正常,强行停止!");
	            }else if(!poolIbsysid.equals(eomsData.getString("IBSYSID"))&&
		            		(IDataUtil.isEmpty(eweNodeInfos)
		            		||eweNodeInfos.size()!=1
		            		||IDataUtil.isEmpty(eweNodeInfos.getData(0))
		            		||!"eomsWait".equals(eweNodeInfos.getData(0).getString("NODE_ID",""))
	        				||"M".equals(eweNodeInfos.getData(0).getString("STATE",""))
	        				)
        				)
	            {
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EWE_NODE表数据不正常,强行停止!");
	            }
	            String poolSubIbsysId=null;
	            for (int r = 0; r < eweNodeInfos.size();r++) {
	            	if(IDataUtil.isNotEmpty(eweNodeInfos.getData(r))&&
	            			("eomsProess".equals(eweNodeInfos.getData(r).getString("NODE_ID",""))||"eomsWait".equals(eweNodeInfos.getData(r).getString("NODE_ID","")))){
	            		poolSubIbsysId=eweNodeInfos.getData(r).getString("SUB_BI_SN","");break;
	            	}
	            }
	            if(poolSubIbsysId==null){
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EWE_NODE表eomsProess数据不正常,强行停止!");
	            }

	            
	    	
//        IDataset recordDataset = WorkformAttrBean.qryRecordNumBySubIbsysid(eomsData.getString("SUB_IBSYSID",""));
//        if (DataUtils.isNotEmpty(recordDataset)) {
//        	for (int j = 0; j < recordDataset.size(); j++) {
//				String recordNum =  recordDataset.getData(j).getString("RECORD_NUM");
		        IDataset transData = new DatasetList();
		        IDataset productAttrInfos = new DatasetList();
//		        setproductAttrInfo(transData,productAttrInfos,operType,subIbsysId,"0",bpmTempletId);
		        setproductAttrInfo(transData,productAttrInfos,operType,poolSubIbsysId,"0",bpmTempletId,poolBusiCode);
	        	
				
		        
		        IData subProductOrderInfo = new DataMap();
		        IDataset subProductOrderInfoList = new DatasetList();

		        IData productOrderNumber = new DataMap();
		        
		        productOrderNumber.put("ProductOrderNumber", poolIbsysid);
//		        productOrderNumber.put("ProductOrderNumber", eomsData.getString("IBSYSID",""));
		        subProductOrderInfoList.add(productOrderNumber);
		        
		        IData productCode = new DataMap();
		        getEweConfigQry(productCode,"IDC_PRODUCTCODE","PARAMNAME",poolbuCode,"PARAMVALUE","ProductCode");
//		        getEweConfigQry(productCode,"IDC_PRODUCTCODE","PARAMNAME",subscribeInfo.getString("BUSI_CODE", ""),"PARAMVALUE","ProductCode");
		        subProductOrderInfoList.add(productCode);
		        
		        IData productOrderID = new DataMap();
		        String productOrderIDStr=null;
		        String contractId=null;
		        for (int z = 0; z < productAttrInfos.size(); z++) {
	        		IData productAttrInfo=productAttrInfos.getData(z);
	        		String productAttrCode= productAttrInfo.getString("ATTR_CODE", "");
	        		if("IDC_SERIAL_NUMBER".equals(productAttrCode)){
	        			productOrderIDStr=productAttrInfo.getString("ATTR_VALUE", "");
//	    		        productOrderID.put("ProductOrderID", productAttrInfo.getString("ATTR_VALUE", ""));
//	    		        break;
	        		}else if("IDC_ProspectingCase".equals(productAttrCode)&&!"".equals(productAttrInfo.getString("ATTR_VALUE", ""))){
	        			productAttrInfo.put("ATTR_VALUE",productAttrInfo.getString("ATTR_VALUE")+"_1");//资源勘察实例_1
	        		}else if("IDC_ResourceCase".equals(productAttrCode)&&!"".equals(productAttrInfo.getString("ATTR_VALUE", ""))){
	        			productAttrInfo.put("ATTR_VALUE",productAttrInfo.getString("ATTR_VALUE")+"_2");//资源预占实例_1
	        		}
	        		else if("IDC_ContractId".equals(productAttrCode)&&!"".equals(productAttrInfo.getString("ATTR_VALUE", ""))){
	        			contractId=productAttrInfo.getString("ATTR_VALUE");
	        		}
	        	}
		        if(productOrderIDStr!=null){//勘察单类派发实例ID为:勘察_1\预占为_2\开通为_3
		        	if("EDIRECTLINECHECKIDC".equals(bpmTempletIdPool)||"EDIRECTLINECHANGECHECKIDC".equals(bpmTempletIdPool)){//勘察类
	    		        productOrderID.put("ProductOrderID", productOrderIDStr+"_1");
		        	}else if("EDIRECTLINEPREEMPTIONIDC".equals(bpmTempletIdPool)){//预占类
	    		        productOrderID.put("ProductOrderID", productOrderIDStr+"_2");
		        	}else{//开通变更注销类
	    		        productOrderID.put("ProductOrderID", productOrderIDStr+"_3");
		        	}
		        	
		        }else{
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+poolIbsysid+"查询TF_B_EOP_ATTR表IDC_SERIAL_NUMBER数据不存在,强行停止!");
		        }
	            

		        subProductOrderInfoList.add(productOrderID);
		        
		        IDataset productOrderCharacterList = new DatasetList();
		        productOrderCharacterList = dealStructure(productAttrInfos, transData);
		        if(productOrderCharacterList!=null){
		        	for(int p=0,psize=productOrderCharacterList.size();p<psize;p++){
		        		subProductOrderInfoList.add(productOrderCharacterList.getData(p));
		        	}
		        }
		        
		        ;
		        String paramCode=null;
		        String paramCodeOld=null;
//		        if("7041".equals(eomsData.getString("BUSI_CODE"))){
		        	for (int i = 0; i < productAttrInfos.size(); i++)
			        {
			            IData productAttrInfo = productAttrInfos.getData(i);
			            if ("IDC_ProductType".equals(productAttrInfo.getString("ATTR_CODE")))
			            {
			            	paramCode=productAttrInfo.getString("ATTR_VALUE");
			            }
			            if ("IDC_ProductType_OLD".equals(productAttrInfo.getString("ATTR_CODE")))
			            {
			            	paramCodeOld=productAttrInfo.getString("ATTR_VALUE");
			            }
			        }
		        	if(paramCodeOld==null&&paramCode!=null){
		        		paramCodeOld=paramCode;
		        	}
//		        }
		        
		        IDataset productOrderRatePlanList=dealStructureForRatePlan(getTransData("IDC_INTERBOSS_OPEN_RATEPLAN"),productAttrInfos,"1",poolbuCode,paramCode);
		        if(productOrderRatePlanList!=null&&productOrderRatePlanList.size()>0){
		        	subProductOrderInfoList.addAll(productOrderRatePlanList);
		        }
		        IDataset productOrderRatePlanList0=dealStructureForRatePlan(getTransData("IDC_INTERBOSS_OPEN_RATEPLAN_OLD"),productAttrInfos,"0",poolbuCode,paramCodeOld);
		        if(productOrderRatePlanList0!=null&&productOrderRatePlanList0.size()>0){
		        	subProductOrderInfoList.addAll(productOrderRatePlanList0);
		        }
		        subProductOrderInfo.put("SubProductOrderInfo", subProductOrderInfoList);
		        
		        productOrderInfoList.add(subProductOrderInfo);

		        
//        	}
//        }
		        
		        
		    	IDataset attachInfos = WorkformAttachBean.qryEopAttrBySubIbsysid(poolSubIbsysId);
		    	
		    	if(DataUtils.isNotEmpty(attachInfos)&&contractId!=null)
		    	{
	        		for(int i = 0 ; i < attachInfos.size() ; i ++)
	            	{
	            		IData attachInfo = attachInfos.getData(i);
	            		String attachName = attachInfo.getString("DISPLAY_NAME","");
//	            		String attachLength = attachInfo.getString("ATTACH_LENGTH","");
//	            		String fileId = attachInfo.getString("FILE_ID","");
	            		String attachType = attachInfo.getString("ATTACH_TYPE","");
	            		
//	            		String fileurl = "FILE_ID="+fileId+"|"+poolSubIbsysId+"|download";
	            		IData attType = new DataMap();
	            		
	            		IData attachment = new DataMap();
	                    IDataset attachmentList = new DatasetList();
	            		if("C".equals(attachType)){//合同附件
		    		        
		    		        
		    		        IData contractInput = new DataMap();
		    		        contractInput.put("CONTRACT_ID", contractId);
		    		        CustContractInfoQrySVC custContractInfoQrySVC=new CustContractInfoQrySVC();
		    		        IDataset contractList= custContractInfoQrySVC.qryContractInfoByContractIdForGrp(contractInput);
		    		        if (IDataUtil.isNotEmpty(contractList)&&IDataUtil.isNotEmpty(contractList.first())) {
		    		            IData contractInfo = contractList.first();
		    		            
		    		            attType.put("AttType", "1");
			    		        attachmentList.add(attType);
		    		            IData contractCode = new DataMap();
			    		        contractCode.put("ContractCode", contractId);
			    		        attachmentList.add(contractCode);
			    		        IData contractName = new DataMap();
			    		        contractName.put("ContractName", contractInfo.getString("CONTRACT_NAME", ""));
			    		        attachmentList.add(contractName);
			    		        IData contractStartTime = new DataMap();
			    		        String contractStrartDate=contractInfo.getString("CONTRACT_START_DATE", "0000:00:00");
			    		        String contractStrartDateNow=DateFormatUtils.format(SysDateMgr.encodeTimestamp(contractStrartDate).getTime(), "yyyyMMdd");
			    		        contractStartTime.put("ContractStartTime", contractStrartDateNow);
			    		        attachmentList.add(contractStartTime);
			    		        IData contractEndTime = new DataMap();
			    		        String contractEndDate=contractInfo.getString("CONTRACT_END_DATE", "0000:00:00");
			    		        String contractEndDateNow=DateFormatUtils.format(SysDateMgr.encodeTimestamp(contractEndDate).getTime(), "yyyyMMdd");
			    		        contractEndTime.put("ContractEndTime", contractEndDateNow);
			    		        attachmentList.add(contractEndTime);
			    		        IData attName = new DataMap();
			    		        attName.put("AttName", attachName);
			    		        attachmentList.add(attName);
			    		        attachment.put("Attachment", attachmentList);
			            		orderInfoReqAttachmentList.add(attachment);
		    		        }
		    		        
		    		        
	            		}else{
	            			attType.put("AttType", "2");
		    		        attachmentList.add(attType);
		    		        IData attName = new DataMap();
		    		        attName.put("AttName", attachName);
		    		        attachmentList.add(attName);
		    		        attachment.put("Attachment", attachmentList);
		            		orderInfoReqAttachmentList.add(attachment);
	            		}
	            		
	            		
	            	}
		    	}
//		        SS.WorkformAttachSVC.qryByIbsysidNode
		        
		        
		        
		        

		        
		        
	    	}
        }
        productOrderInfo.put("ProductOrderInfo", productOrderInfoList);
        orderInfoReqList.add(productOrderInfo);
        //多个产品参数 end
        IData effTime = new DataMap();
        effTime.put("EffTime", nowDate);
        orderInfoReqList.add(effTime);
        
        //员工信息
        IData datadefaultstaff = new DataMap();
		datadefaultstaff.put("STAFF_ID", eomsData.getString("ACCEPT_STAFF_ID"));
		IDataset defaultStaffInfos = CSAppCall.call("CS.StaffInfoQrySVC.qryStaffInfoByStaffId", datadefaultstaff);
		if(DataUtils.isNotEmpty(defaultStaffInfos)&&DataUtils.isNotEmpty(defaultStaffInfos.getData(0)))//查询不到工号信息,直接返回
		{
	        IData operator = new DataMap();
	        IData operatorPhone = new DataMap();

	        operator.put("Operator", eomsData.getString("ACCEPT_STAFF_ID"));
	        operatorPhone.put("OperatorPhone", defaultStaffInfos.getData(0).getString("SERIAL_NUMBER", ""));

	        orderInfoReqList.add(operator);
	        orderInfoReqList.add(operatorPhone);

		}
		
        orderInfoReqList.addAll(orderInfoReqAttachmentList);

        orderInfoReq.put("OrderInfoReq", orderInfoReqList);
       
        retData.add(orderInfoReq);
        return retData;
    }
	

	private IDataset getTransData(String eopOperType) throws Exception
    {
        IDataset paramTransList = EopParamTransQry.queryParamTransByOperType(eopOperType);
        if (DataUtils.isEmpty(paramTransList))
        {
            paramTransList = new DatasetList();
        }
        return paramTransList;
    }
	private IDataset dealStructure(IDataset productAttrInfos, IDataset transData) throws Exception
    {
        IDataset attrInfos = new DatasetList();
//        IData recordInfo = new DataMap();
        for (int i = 0; i < productAttrInfos.size(); i++)
        {
//            IData attrInfo = new DataMap();
//            IData fieldInfo = new DataMap();
            IData productAttrInfo = productAttrInfos.getData(i);
            //由于前台暂时没有给转换value数据，对value数据进行转换，无奈之举╮(╯▽╰)╭
            //从配置变里面取数据，进行转换
            String configkeyValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EsopParamToCrmParamKey", productAttrInfo.getString("ATTR_CODE")});
            if(StringUtils.isNotEmpty(configkeyValue))
            {
                String value = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ configkeyValue, productAttrInfo.getString("ATTR_VALUE")});
                if (StringUtils.isNotEmpty(value))
                {
                    productAttrInfo.put("ATTR_VALUE", value);
                }
            }
            if (DataUtils.isNotEmpty(transData))
            {
               IData transInfo =  doTransData(productAttrInfo,transData);
               
               if (DataUtils.isNotEmpty(transInfo)) {
                   IData productOrderCharacter = new DataMap();
            	   IDataset productOrderCharacterList=new DatasetList();
            	   IData characterID = new DataMap();
            	   characterID.put("CharacterID", transInfo.getString("ATTR_CODE"));
            	   productOrderCharacterList.add(characterID);
            	   
            	   IData characterName = new DataMap();
            	   characterName.put("CharacterName", transInfo.getString("ATTR_NAME"));
            	   productOrderCharacterList.add(characterName);
            	   
            	   IData characterValue = new DataMap();
            	   characterValue.put("CharacterValue", productAttrInfo.getString("ATTR_VALUE",""));
            	   productOrderCharacterList.add(characterValue);
            	   
            	   IData characterGroup = new DataMap();
            	   characterGroup.put("CharacterGroup", "");
            	   productOrderCharacterList.add(characterGroup);
            	   
            	   productOrderCharacter.put("ProductOrderCharacter", productOrderCharacterList);
            	   attrInfos.add(productOrderCharacter);
            	   /*attrInfo.put("fieldChName", transInfo.getString("ATTR_NAME"));
                   attrInfo.put("fieldContent", productAttrInfo.getString("ATTR_VALUE",""));
                   attrInfo.put("fieldEnName", transInfo.getString("ATTR_CODE"));
                   fieldInfo.put("fieldInfo", attrInfo);
                   attrInfos.add(ProductOrderCharacter);*/
               }
            }
            
//            recordInfo.put("recordInfo", attrInfos);
        }
        return attrInfos;
    }
	//对特殊的规格数据做特殊处理
	private IDataset dealStructureForRatePlan(IDataset transDataInfos,IDataset productAttrInfos,String actionStr,String productId,String paramCode) throws Exception
    {
        IDataset attrInfos = new DatasetList();
//        IData recordInfo = new DataMap();
//        IDataset transDataInfos= getTransData("IDC_INTERBOSS_OPEN_RATEPLAN");
        for (int i = 0; i < transDataInfos.size(); i++)
        {
//            IData attrInfo = new DataMap();
//            IData fieldInfo = new DataMap();
            IData transData = transDataInfos.getData(i);
            //由于前台暂时没有给转换value数据，对value数据进行转换，无奈之举╮(╯▽╰)╭
            //从配置变里面取数据，进行转换
//            String configkeyValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EsopParamToCrmParamKey", productAttrInfo.getString("ATTR_CODE")});
//            if(StringUtils.isNotEmpty(configkeyValue))
//            {
//                String value = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ configkeyValue, productAttrInfo.getString("ATTR_VALUE")});
//                if (StringUtils.isNotEmpty(value))
//                {
//                    productAttrInfo.put("ATTR_VALUE", value);
//                }
//            }
            if (DataUtils.isNotEmpty(transData)&&productId.equals(transData.getString("PROD_ID"))
            		&&(paramCode==null||paramCode.equals(transData.getString("PARAM_CODE","")))
            		)
            {
               IData productOrderRatePlan = new DataMap();
        	   IDataset productOrderRatePlanList=new DatasetList();
        	   
        	   IData action = new DataMap();
        	   action.put("Action", actionStr);
        	   productOrderRatePlanList.add(action);
        	   
        	   IData ratePlanID = new DataMap();
        	   ratePlanID.put("RatePlanID", transData.getString("PARAM_CODE",""));
        	   productOrderRatePlanList.add(ratePlanID);
        	   
               IDataset transDataRateParamInfos= getTransData(transData.getString("CHG_PARAM_CODE"));
        	   IDataset productOrderRatePlanList1=new DatasetList();
               for (int j = 0; j < transDataRateParamInfos.size(); j++)
               {
                   IData transDataRateParamInfo = transDataRateParamInfos.getData(j);
                   if (DataUtils.isNotEmpty(transDataRateParamInfo))
                   {
                       IData transInfo =  doTransDataForProductId(productAttrInfos,transDataRateParamInfo);
                       if (DataUtils.isNotEmpty(transInfo)) {
                    	   IData rateParam = new DataMap();
                    	   IDataset rateParamList=new DatasetList();
                    	   
                    	   
                    	   IData paramName = new DataMap();
                    	   paramName.put("ParamName", transInfo.getString("ATTR_DESC"));
                    	   rateParamList.add(paramName);
                    	   
                    	   IData paramID = new DataMap();
                    	   paramID.put("ParamID", transInfo.getString("ATTR_CODE"));
                    	   rateParamList.add(paramID);
                    	   
                    	   IData paramValue = new DataMap();
                    	   paramValue.put("ParamValue", transInfo.getString("ATTR_VALUE"));
                    	   rateParamList.add(paramValue);
                    	   
                    	   rateParam.put("RateParam", rateParamList);
                    	   productOrderRatePlanList1.add(rateParam);
                       }
                   }
                   
               }
               if(productOrderRatePlanList1.size()>0){
            	   productOrderRatePlanList.addAll(productOrderRatePlanList1);
            	   productOrderRatePlan.put("ProductOrderRatePlan", productOrderRatePlanList);
            	   attrInfos.add(productOrderRatePlan);
               }
            }
            
//            recordInfo.put("recordInfo", attrInfos);
        }
        return attrInfos;
    }
	private IData doTransData(IData productAttrInfo, IDataset transDatas)
    {
    	IData transInfo = new DataMap();
        for (int i = 0; i < transDatas.size(); i++)
        {
            IData transData = transDatas.getData(i);
            String transName = transData.getString("PARAM_CODE","");
            if (!transName.equals(productAttrInfo.getString("ATTR_CODE")))
            {
                continue;
            }
            transInfo.put("ATTR_NAME", transData.getString("CHG_PARAM_DESC"));
            transInfo.put("ATTR_CODE", transData.getString("CHG_PARAM_CODE"));
            break;
        }
        
        return transInfo;
    }
	private IData doTransDataForProductId(IDataset productAttrInfos, IData transData)
    {
    	IData transInfo = new DataMap();
        for (int i = 0; i < productAttrInfos.size(); i++)
        {
            IData productAttrInfo = productAttrInfos.getData(i);
            String transName = transData.getString("PARAM_CODE","");
            if (!transName.equals(productAttrInfo.getString("ATTR_CODE")))
            {
                continue;
            }
            transInfo.put("ATTR_DESC", transData.getString("PARAM_DESC"));
            transInfo.put("ATTR_NAME", transData.getString("CHG_PARAM_DESC"));
            transInfo.put("ATTR_CODE", transData.getString("CHG_PARAM_CODE"));
            transInfo.put("ATTR_VALUE", productAttrInfo.getString("ATTR_VALUE"));
            
            break;
        }
        
        return transInfo;
    }
	private void getEweConfigQry(IData oprType,String configName,String inParam,String inParamVal,String outParam,String outParamName) throws Exception{
		IDataset idcOperTypeList = EweConfigQry.qryByConfigName(configName,"0");
        if (DataUtils.isNotEmpty(idcOperTypeList))
        {
        	for(int i=0,size=idcOperTypeList.size();i<size;i++){
        		IData idcOperType=idcOperTypeList.getData(i);
        		if(inParamVal.equals(idcOperType.getString(inParam))){
        	        oprType.put(outParamName, idcOperType.getString(outParam,""));
        			break;
        		}
        	}
        }
	}
	private void setproductAttrInfo(IDataset transDataOld,IDataset productAttrInfosOld,String operType,String subIbsysId,String recordNum,String bpmTempletId,String poolBusiCode)throws Exception{
		IDataset transData=new DatasetList();
		IDataset productAttrInfos=new DatasetList();
		IData eomsInfo = WorkformEomsBean.queryBySubIbsysIdAndOperTypeDesc(subIbsysId, operType);
        if (DataUtils.isEmpty(eomsInfo))
        {
            return ;
        }
        
		// 查下转换配置表，看是否需要转换，如果需要转换，针对TF_B_EOP_ATTR表中的数据进行转换
//        IData configInfo = EweConfigQry.queryEomsTranConfig("SHEETTYPE", eomsInfo.getString("SHEETTYPE"),
//                eomsInfo.getString("SERVICETYPE"), operType);
//        if (DataUtils.isNotEmpty(configInfo))
//        {
//            transData = getTransData(configInfo.getString("PARAMNAME"));
//        }
        
        IDataset configInfoList = EweConfigQry.qryByConfigName("EOMS_INTERNAME","0");
        if (DataUtils.isNotEmpty(configInfoList))
        {
        	for(int i=0,size=configInfoList.size();i<size;i++){
        		IData configInfo=configInfoList.getData(i);
        		if(bpmTempletId.equals(configInfo.getString("PARAMNAME"))&&poolBusiCode.equals(configInfo.getString("RSRV_STR1"))){
                  transData = getTransData(configInfo.getString("PARAMVALUE"));
                  break;
        		}
        	}
        }
        IDataset commonList = new DatasetList();
        commonList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNumGroupSeq(subIbsysId,eomsInfo.getString("GROUP_SEQ"), "0");
//		productAttrInfos = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNumGroupSeq(subIbsysId,eomsInfo.getString("GROUP_SEQ"),recordNum);
		if (DataUtils.isEmpty(productAttrInfos))
        {
            productAttrInfos = new DatasetList();
        }
        productAttrInfos.addAll(commonList);
        transDataOld.addAll(transData);
        productAttrInfosOld.addAll(productAttrInfos);
        
	}
}
