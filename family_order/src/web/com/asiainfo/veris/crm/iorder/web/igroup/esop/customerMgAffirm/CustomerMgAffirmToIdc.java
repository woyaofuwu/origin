package com.asiainfo.veris.crm.iorder.web.igroup.esop.customerMgAffirm;



import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class CustomerMgAffirmToIdc extends EopBasePage {

    public abstract void setInfo(IData info);
    public abstract void setPattr(IData pattr);
    public abstract void setPattrs(IDataset pattrs);
    public abstract void setPattr7041(IData pattr7041);
    public abstract void setPattrs7041(IDataset pattrs7041);
    public void initPage(IRequestCycle cycle) throws Exception {
    	super.initPage(cycle);
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        String nodeId = param.getString("NODE_ID");
        String busiformNodeId = param.getString("BUSIFORM_NODE_ID");
        if (StringUtils.isNotBlank(ibsysid)) {
            queryInfosByIbsysid(cycle);
        }
        IData info = new DataMap();
        String bpmTempletId = param.getString("BPM_TEMPLET_ID");
        IData subscribeData = new DataMap();
        try {
        	subscribeData=WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
            subscribeData.put("NODE_ID", nodeId);
            subscribeData.put("BUSIFORM_NODE_ID", busiformNodeId);
            info.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
            info.put("NODE_ID", nodeId);
            info.put("BPM_TEMPLET_ID", bpmTempletId);
            String staffId = getVisit().getStaffId();
            info.put("STAFF_ID", staffId);
        }
        catch (Exception e) {
            // TODO: handle exception
            info.put("EXCEPTION", "根据IBSYSID=" + ibsysid + "未查询到流程订单主表数据！");

        }
    	IDataset pattrs =new DatasetList();

        IData qrySubscribePoolParam1=new DataMap();
        qrySubscribePoolParam1.put("POOL_NAME", "ORDER_OrderNumFlag");
        qrySubscribePoolParam1.put("STATE", "A");
        qrySubscribePoolParam1.put("POOL_VALUE", ibsysid);
        IDataset qrySubscribePoolParamList1=CSViewCall.call(this, "SS.ConfCrmQrySVC.qrySubscribePool",qrySubscribePoolParam1);
        if(IDataUtil.isEmpty(qrySubscribePoolParamList1)||qrySubscribePoolParamList1.size()==0||
        		IDataUtil.isEmpty(qrySubscribePoolParamList1.getData(0))){
//        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+ibsysid+"查询tf_b_eop_subscribe_pool表数据不存在!");
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据订单号"+ibsysid+"查询tf_b_eop_subscribe_pool表数据不存在!");
        }
        IData qrySubscribePoolParam=new DataMap();
        qrySubscribePoolParam.put("POOL_NAME", "ORDER_OrderNumFlag");
        qrySubscribePoolParam.put("STATE", "A");
        qrySubscribePoolParam.put("REL_IBSYSID", qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID"));
        IDataset qrySubscribePoolParamList=CSViewCall.call(this, "SS.ConfCrmQrySVC.qrySubscribePool",qrySubscribePoolParam);
        if(IDataUtil.isEmpty(qrySubscribePoolParamList)||qrySubscribePoolParamList.size()==0){
//        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号"+qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID")+"查询tf_b_eop_subscribe_pool 表数据REL_IBSYSID不存在!");
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据订单号"+qrySubscribePoolParamList1.getData(0).getString("REL_IBSYSID")+"查询tf_b_eop_subscribe_pool 表数据REL_IBSYSID不存在!");
        }
        for (int s = 0; s < qrySubscribePoolParamList.size(); s++) {
        	IData qrySubscribePoolParamData=qrySubscribePoolParamList.getData(s);
	    	if (DataUtils.isNotEmpty(qrySubscribePoolParamData)) {
	        	String poolIbsysid=qrySubscribePoolParamData.getString("POOL_VALUE");
	        	String poolbuCode=qrySubscribePoolParamData.getString("POOL_CODE");

	        	
	        	IData poolEweParam = new DataMap();
    			poolEweParam.put("BI_SN", poolIbsysid);

        		IDataset poolEweList=CSViewCall.call(this,"SS.WorkformQrySVC.qryWorkFormAllByBiSn", poolEweParam);
        		if(IDataUtil.isEmpty(poolEweList)||IDataUtil.isEmpty(poolEweList.first())
	            		||"".equals(poolEweList.first().getString("BI_SN",""))){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据订单号"+poolIbsysid+"查询TF_B_EWE表流程信息为空!");
	            }
        		IData poolEweData=poolEweList.first();
        		
        		IData poolEweNodeParam = new DataMap();
        		poolEweNodeParam.put("BUSIFORM_ID", poolEweData.getString("BUSIFORM_ID"));

        		IDataset poolEweNodeList=CSViewCall.call(this,"SS.WorkformNodeQyrSVC.qryWorkFormNodeByBusiformId", poolEweNodeParam);
        		if(
	            		(IDataUtil.isEmpty(poolEweNodeList)
	            		||poolEweNodeList.size()!=1
	            		||IDataUtil.isEmpty(poolEweNodeList.getData(0))
//		            		||!"eomsWait".equals(eweNodeInfos.getData(0).getString("NODE_ID",""))
        				||!"0".equals(poolEweNodeList.getData(0).getString("STATE",""))
        				)
    				)
	            {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据订单号"+poolIbsysid+"查询TF_B_EWE_NODE表数据不正常,强行停止!");
	            }
        		IData paramOther=new DataMap();
        		paramOther.put("IBSYSID", poolIbsysid);
        		paramOther.put("ATTR_CODE", "succReplyFlag");
        		IDataset otherList=CSViewCall.call(this,"SS.WorkformOtherSVC.qryLastInfoByIbsysidAndAttrCode", paramOther);
        		if(IDataUtil.isEmpty(otherList)||IDataUtil.isEmpty(otherList.first())||"".equals(otherList.first().getString("ATTR_VALUE",""))){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据订单号"+poolIbsysid+"查询TF_B_EOP_OTHER表数据succReplyFlag不正常,强行停止!");
        		}
        		
	            IData pattr = new DataMap();
	            pattr.put("POOL_IBSYSID", poolIbsysid);
	            pattr.put("POOL_CODE", poolbuCode);
	            pattr.put("POOL_succReplyFlag", otherList.first().getString("ATTR_VALUE",""));
                pattr.put("pattrBpmTempletId", bpmTempletId);
	            if(bpmTempletId.equals("EDIRECTLINEOPENIDC")&&"7041".equals(poolbuCode)){
	            	IDataset idcReturnInfos =new DatasetList();
	                getEopAttrForGroupToIDCReturnList(poolIbsysid,idcReturnInfos,info);
	                pattr.put("pattr7041", idcReturnInfos);
//	                setPattrs7041(idcReturnInfos);
	            }
	            
	            pattrs.add(pattr);
	    	}
        }
        
        
        setPattrs(pattrs);
        setInfo(info);
        
    }
    public void queryInfosByIbsysid(IRequestCycle cycle) throws Exception {
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        IData workformData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
        String groupId = "";
        if (IDataUtil.isNotEmpty(workformData)) {
            groupId = workformData.getString("GROUP_ID");
        } else {
            this.setAjax("error_message", "根据工单号未查到对应工单, 请核实");
            return;
        }
        if (StringUtils.isNotBlank(groupId)) {
            IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
            group.put("IBSYSID", ibsysid);
            setGroupInfo(group);
        }
        String busiCode = workformData.getString("BUSI_CODE");
        // 查询产品属性
        getProductInfos(busiCode, ibsysid);

    }
    private void getProductInfos(String busiCode, String ibsysid) throws Exception {
        IDataset productAttrs = WorkfromViewCall.qryDataLineInfoByIbsysid(this, ibsysid);
        if (productAttrs != null && productAttrs.size() > 0) {
            for (Object obj : productAttrs) {
                IData data = (IData) obj;
                data.put("PRODUCT_ID", busiCode);
                data.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", busiCode));
            }
        }
//        setPattrs(productAttrs);
    }
    
    
    protected void  getEopAttrForGroupToIDCReturnList(String ibsysid,IDataset pattrList,IData commonData) throws Exception{
    	if(pattrList==null){
    		pattrList=new DatasetList();
    	}
    	if(commonData==null){
    		commonData= new DataMap();
    	}
    	if(ibsysid==null){
    		ibsysid="";
    	}
        IData inparam = new DataMap();
    	inparam.put("IBSYSID", ibsysid);
    	IDataset attrtInfo = new DatasetList();

		attrtInfo = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", inparam);
    	if (IDataUtil.isNotEmpty(attrtInfo)) {
        	
            for (int i = 0; i < attrtInfo.size(); i++) {
            	String recordNum=attrtInfo.getData(i).getString("RECORD_NUM","");
            	String groupSeq=attrtInfo.getData(i).getString("GROUP_SEQ","");
            	String key = attrtInfo.getData(i).getString("ATTR_CODE");
                String value = attrtInfo.getData(i).getString("ATTR_VALUE");
                if(groupSeq.matches("^[0-9]*$")){
                	int recordNumInt=Integer.valueOf(groupSeq);
                	if(recordNumInt<=0){
                		commonData.put(key, value);
                	}else{
                		int num =recordNumInt-1;
                		
                		if(pattrList.size()<recordNumInt){
                			for(int size=pattrList.size();size<recordNumInt;size++){
                				pattrList.add(new DataMap());
                			}
                		}
                		pattrList.getData(num).put(key, value);
                	}
                }
            	
            }
        }
    }

    public void submitForIdc(IRequestCycle cycle) throws Exception
    {
    	IData paramIn =new DataMap();
        IData submitData = new DataMap(getData().getString("SUBMIT_PARAM"));
		String ibsysid=submitData.getData("COMMON_DATA").getString("IBSYSID", "");
        IDataset idcList= new DatasetList(submitData.getDataset("IDC_DATA").toString());
        IDataset eomsInfos= new DatasetList();
    	if (IDataUtil.isNotEmpty(idcList)) {
    		for(int i=0;i<idcList.size();i++){
            	IData idcData=idcList.getData(i);
            	if (IDataUtil.isNotEmpty(idcData)) {
            		IDataset idcAttrList=idcData.getDataset("IDCATTR_LIST");
            			String rel_ibsysid=idcData.getString("REL_IBSYSYID", "");
            			IData poolEweParam = new DataMap();
            			poolEweParam.put("BI_SN", rel_ibsysid);

                		IDataset poolEweList=CSViewCall.call(this,"SS.WorkformQrySVC.qryWorkFormAllByBiSn", poolEweParam);
                    	if( IDataUtil.isNotEmpty(poolEweList)){
                    		IData poolEweData=poolEweList.first();
                    		
                    		IData poolEweNodeParam = new DataMap();
                    		poolEweNodeParam.put("BUSIFORM_ID", poolEweData.getString("BUSIFORM_ID"));

                    		IDataset poolEweNodeList=CSViewCall.call(this,"SS.WorkformNodeQyrSVC.qryWorkFormNodeByBusiformId", poolEweNodeParam);
                        	if( IDataUtil.isNotEmpty(poolEweNodeList)){
                        		for(int k=0;k<poolEweNodeList.size();k++){
                        			IData poolEweNodeData=poolEweNodeList.getData(k);
                        			if( IDataUtil.isNotEmpty(poolEweNodeData)){
                        				IDataset otherList=submitData.getDataset("OTHER_LIST");
                        				if(IDataUtil.isEmpty(otherList))
                        		        {
                        					otherList=new DatasetList();
                        					
                        		        }
                        				IData paramOther=new DataMap();
                                		paramOther.put("IBSYSID", rel_ibsysid);
                                		paramOther.put("ATTR_CODE", "succReplyFlag");
                                		IDataset otherListA=CSViewCall.call(this,"SS.WorkformOtherSVC.qryLastInfoByIbsysidAndAttrCode", paramOther);
                                		if(IDataUtil.isEmpty(otherListA)||IDataUtil.isEmpty(otherListA.first())||"".equals(otherListA.first().getString("ATTR_VALUE",""))){
                                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据订单号"+rel_ibsysid+"查询TF_B_EOP_OTHER表数据succReplyFlag不正常,强行停止!");
                                		}
                        				IData otherData=new DataMap();
                        				otherData.put("ATTR_CODE", "succReplyFlag");
                        				otherData.put("ATTR_VALUE", otherListA.first().getString("ATTR_VALUE",""));
                        				otherData.put("ATTR_NAME", "异步反馈成功标识");
                        				otherData.put("RECORD_NUM", "0");
                        				otherList.add(otherData);                  				                        			

                        				submitData.put("IDC_DATA", idcAttrList);
            	                		submitData.getData("COMMON_DATA").put("IBSYSID",rel_ibsysid);
            	                		submitData.getData("COMMON_DATA").put("BUSI_CODE",idcData.getString("REL_BUSI_CODE", ""));
            	                		submitData.getData("COMMON_DATA").put("BUSIFORM_NODE_ID",poolEweNodeData.getString("BUSIFORM_NODE_ID", ""));
            	                		submitData.getData("COMMON_DATA").put("NODE_ID",poolEweNodeData.getString("NODE_ID", ""));
            	                		buildOtherSvcParam(submitData);
        	                			IData param = ScrDataTrans.buildWorkformSvcParam(submitData);
            	                		if(!ibsysid.equals(rel_ibsysid)){//当前订单最后执行
                	                        IDataset result = CSViewCall.call(this, "SS.WorkformRegisterSVC.register", param);
                	                        System.out.println("rel_ibsysid:"+rel_ibsysid+"result:"+result);
            	                		}
            	                		else{
            	                			paramIn=new DataMap(param.toString());
            	                		}
            	                        
                        			}
                        		}
                        	}
                    	}
            			
	                		
//	                        setAjax(result.first());
//	                	}
            	}
            }
    	}
    	
    	IDataset result = CSViewCall.call(this, "SS.WorkformRegisterSVC.register", paramIn);
        System.out.println("ibsysid:"+ibsysid+"result:"+result);
        setAjax(result.first());
//    	if(eomsInfos.size()>0){
//    		IData attrParam = new DataMap();
//    		attrParam.put("ATTR_INFOS", eomsInfos);
//    		CSViewCall.call(this,"SS.WorkformAttrSVC.insertWorkformAttr", attrParam);
//    	}
    	
    	
    	
        
    }

}

