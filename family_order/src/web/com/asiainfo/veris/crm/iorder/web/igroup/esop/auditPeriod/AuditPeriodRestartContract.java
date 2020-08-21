package com.asiainfo.veris.crm.iorder.web.igroup.esop.auditPeriod;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
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

public abstract class AuditPeriodRestartContract extends EopBasePage {

    public abstract void setInAttr(IData inAttr);

    public abstract void setPattr(IData pattr);

    public abstract void setInfo(IData info);

    public abstract void setAuditInfo(IData info);

    public abstract void setPattrs(IDataset pattrs);

    public abstract void setProductInfo(IData productInfo);

    public void initPage(IRequestCycle cycle) throws Exception {
        super.initPage(cycle);
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        String nodeId = param.getString("NODE_ID");
        String busiformNodeId = param.getString("BUSIFORM_NODE_ID");
        IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
        if (IDataUtil.isEmpty(subscribeData)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID=" + ibsysid + "未查询到流程订单主表数据！");

        }
        subscribeData.put("NODE_ID", nodeId);
        subscribeData.put("BUSIFORM_NODE_ID", busiformNodeId);
        subscribeData.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));//子流程bpm_templet_id不一样，改成从入参里取
        IData info = new DataMap();
        info.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
        info.put("NODE_ID", nodeId);
        String bpmTempletId = subscribeData.getString("BPM_TEMPLET_ID");
        info.put("BPM_TEMPLET_ID", bpmTempletId);
        String productId = subscribeData.getString("BUSI_CODE");
        if (bpmTempletId.equals("EDIRECTLINEOPENPBOSS") || bpmTempletId.equals("EVIOPDIRECTLINEOPENPBOSS")) {
            if (productId.equals("7011") || productId.equals("7012") || productId.equals("7010")) {
                info.put("ShowContract", "1");
            }
        }else if(bpmTempletId.equals("EDIRECTLINECONTRACTCHANGE")) {
        	info.put("ShowContract", "1");
        }
        
        IDataset pattrs =new DatasetList();
        getEopAttrToList(ibsysid,pattrs,info);
        

        setInfo(info);
        setPattrs(pattrs);
        getStaffInfo();
    }

    private void getStaffInfo() throws Exception {
        String staff = getVisit().getStaffId();
        IData param = new DataMap();
        param.put("STAFF_ID", staff);
        IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", param);
        IData data1 = new DataMap();
        IData data2 = new DataMap();
        data1.put("DATA_NAME", "通过");
        data1.put("DATA_ID", "1");
        data2.put("DATA_NAME", "不通过");
        data2.put("DATA_ID", "2");
        IDataset list = new DatasetList();
        list.add(data1);
        list.add(data2);
        staffInfo.put("LIST", list);
        setAuditInfo(staffInfo);
    }







    @Override
    public void buildOtherSvcParam(IData param) throws Exception {
        super.buildOtherSvcParam(param);
        buildAuditSvcParam(param);
        IData commonData = param.getData("COMMON_DATA");
        String nodeId = commonData.getString("NODE_ID");
    }

    private void buildAuditSvcParam(IData param) throws Exception {
    	IData commonData = param.getData("COMMON_DATA");
    	String bpmtemtId = commonData.getString("BPM_TEMPLET_ID","");
    	String nodeId = commonData.getString("NODE_ID");
    	if("EOMSSplitflowOpen".equals(bpmtemtId) && "applyConfirm".equals(nodeId)){ //如果是专线挂起申请客户经理审批节点
    		IDataset attrInput =  new DatasetList();
    		IData params = new DataMap();
    		String busiformNodeId = commonData.getString("BUSIFORM_NODE_ID");
    		params.put("BUSIFORM_NODE_ID", busiformNodeId);
    	    IData preInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweByBusiFormNodeId", params);
    	    String productNo = "" ;
    	    if(IDataUtil.isNotEmpty(preInfo)){
    	    	String subIbsysId = preInfo.getString("SUB_BI_SN");
    	    	IData attrNode = new DataMap();
    	    	attrNode.put("SUB_IBSYSID", subIbsysId);
    	    	attrNode.put("GROUP_SEQ", "0");
    	    	IDataset attrs = CSViewCall.call(this, "SS.WorkformAttrSVC.qryAttrBySubIbsysidAndGroupseq", attrNode);
    	    	for(int i = 0;i<attrs.size();i++){
    	    		IData attr = attrs.getData(i);
    	    		if("serialNo".equals(attr.getString("ATTR_CODE"))){
    	    			attrInput.add(attr);
    	    		}
    	    		if("ProductNo".equals(attr.getString("ATTR_CODE"))){
    	    			productNo = attr.getString("ATTR_VALUE");
    	    			attrInput.add(attr);
    	    		}
    	    	}
    	    }
    	    IData input =  new DataMap();
    	    input.put("IBSYSID", commonData.getString("IBSYSID"));
    	    input.put("PRODUCT_NO", productNo);
    	    IData emos = CSViewCall.callone(this,"SS.WorkformEomsStateSVC.qryEomsStateByIbsysidAndProductNo",input);
    		if(IDataUtil.isEmpty(emos)){
    			CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据ESOP工单编号"+commonData.getString("IBSYSID")+"查询无资料，不能办理该业务！");
    		}
    		String recordNum =  emos.getString("RECORD_NUM");
    	    String staffId = "";
    		IDataset otherList =  param.getDataset("OTHER_LIST");
    	    for(int i = 0;i<otherList.size();i++){
	    		IData attr = otherList.getData(i);
	    		if("AUDIT_STAFF_ID".equals(attr.getString("ATTR_CODE"))){
	    			staffId = attr.getString("ATTR_VALUE");
	    			attr.put("ATTR_CODE", "agreePerson");
	    			attr.put("RECORD_NUM", recordNum);
	    			attrInput.add(attr);
	    		}
	    		if("AUDIT_RESULT".equals(attr.getString("ATTR_CODE"))){
	    			attr.put("ATTR_CODE", "agreeResult");
	    			attr.put("RECORD_NUM", recordNum);
	    			attrInput.add(attr);
	    		}
	    		if("AUDIT_TEXT".equals(attr.getString("ATTR_CODE"))){
	    			attr.put("ATTR_CODE", "agreeContent");
	    			attr.put("RECORD_NUM", recordNum);
	    			attrInput.add(attr);
	    		}
	    	}
    	    IData staff =  new DataMap();
    	    staff.put("STAFF_ID", staffId);
    	    IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", staff);
    	    IData attr = new DataMap();
    	    attr.put("ATTR_CODE", "agreePersonContactPhone");
    	    attr.put("ATTR_VALUE", staffInfo.getString("SERIAL_NUMBER",""));
    	    attr.put("RECORD_NUM",recordNum);
    	    attrInput.add(attr);
    	    param.put("CUSTOM_ATTR_LIST", attrInput);
    	    
    	}
    }

}
