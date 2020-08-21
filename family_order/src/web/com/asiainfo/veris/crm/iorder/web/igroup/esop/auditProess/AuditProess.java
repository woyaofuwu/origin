package com.asiainfo.veris.crm.iorder.web.igroup.esop.auditProess;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class AuditProess extends EopBasePage
{

    public void initPage(IRequestCycle cycle) throws Exception
    {
    	super.initPage(cycle);
    	IData busi = new DataMap();
        IData workformData = new DataMap();
        IData param = getData();
        
        String ibsysid = param.getString("IBSYSID");
        String nodeId = param.getString("NODE_ID");
        String bpmTemplateId = param.getString("BPM_TEMPLET_ID");
        String busiformNodeId = param.getString("BUSIFORM_NODE_ID");//节点流水
        String busiformOperType = param.getString("BUSIFORM_OPER_TYPE");//节点流水
        IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
        if (IDataUtil.isEmpty(subscribeData)) 
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID="+ibsysid+"未查询到流程订单主表数据！");

		}
        subscribeData.put("NODE_ID", nodeId);
        subscribeData.put("BUSIFORM_NODE_ID", busiformNodeId);
        IData info = new DataMap();
        info.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
    	info.put("OPER_TYPE",busiformOperType);
        setInfo(info);
        //处理附件
        this.dealAttachInfo(info, ibsysid, busiformNodeId);

        String offerCode = subscribeData.getString("BUSI_CODE");
        IDataset offerList = UpcViewCall.queryOfferByOfferCodeAndType(this, offerCode, UpcConst.ELEMENT_TYPE_CODE_PRODUCT);
        if(IDataUtil.isNotEmpty(offerList))
        {
            setOffer(offerList.first());
        }
        
        IData inpuData = new DataMap();
        inpuData.put("IBSYSID", ibsysid);
        inpuData.put("NODE_ID", "apply");
        
        IDataset attrInfos  = CSViewCall.call(this, "SS.WorkformAttrSVC.qryreAuditAttrByIbsysId", inpuData);
        System.out.println("====attrInfos======"+attrInfos);
        IDataset productAttrList = DataHelper.filter(attrInfos, "RECORD_NUM=0");
        if(IDataUtil.isNotEmpty(productAttrList))
        {

            for(int i = 0, size = productAttrList.size(); i < size; i++)
            {
                IData productAttr = productAttrList.getData(i);
                busi.put(productAttr.getString("ATTR_CODE"), productAttr.getString("ATTR_VALUE"));
            }
        }
        
        IDataset mebProductAttrList = new DatasetList();
        IDataset edits = new DatasetList();
        if (IDataUtil.isNotEmpty(attrInfos)) {
        	String specialType = "";
    		if ("EDIRECTLINEMARKETINGADD".equals(bpmTemplateId)) {
    			specialType = "marketingTrList"; // 营销活动
    		}else if ("EDIRECTLINECANCELPBOSS".equals(bpmTemplateId)) {// 拆机
    			if ("7012".equals(offerCode)||"70121".equals(offerCode)||"70122".equals(offerCode)) {
    				specialType = "cancelTrList";
				}else if ("7011".equals(offerCode)||"70111".equals(offerCode)||"70112".equals(offerCode)) {
					specialType = "cancelTrListNet";
				}else if ("7010".equals(offerCode)){
					specialType = "cancelTrListSound"; 
				}else if ("7016".equals(offerCode)) {
					specialType = "cancelTrListIms";
				}
			}
        	
    		IData data = new DataMap();
        	data.put("CONFIGNAME", specialType);
            IDataset trDetails = CSViewCall.call(this, "SS.IsspConfigQrySVC.getIsspConfig", data);
            setTrDetails(trDetails);
            attrInfos.removeAll(DataHelper.filter(attrInfos, "RECORD_NUM=0"));
			mebProductAttrList.add(attrInfos);
				
			if (IDataUtil.isNotEmpty(attrInfos)) {
				
				IData inData = new DataMap();
				inData.put("SUB_IBSYSID", attrInfos.first().getString("SUB_IBSYSID",""));
				IDataset recordInfos  = CSViewCall.call(this, "SS.WorkformAttrSVC.qryRecordNumBySubIbsysid", inData);
				
				for (int j = 0; j < recordInfos.size(); j++) {
					IData opdetail = new DataMap();
					IDataset attrList = DataHelper.filter(attrInfos, "RECORD_NUM="+recordInfos.getData(j).getString("RECORD_NUM"));
					for (int k = 0; k < attrList.size(); k++) {
						opdetail.put(attrList.getData(k).getString("ATTR_CODE"),attrList.getData(k).getString("ATTR_VALUE"));
					}
					edits.add(opdetail);
				}
			}
			workformData.put("mebProductAttrList", mebProductAttrList);
		}
        setEdits(edits);

        workformData.put("AUDITING_OPINION", WorkfromViewCall.getLastAttrValue(this, ibsysid, "AUDITING_OPINION"));  //审核意见
        setWorkformData(workformData);
        
        //加载审核页面
        IData inAttr = new DataMap();
        inAttr.put("FLOW_ID", param.getString("BPM_TEMPLET_ID"));
        inAttr.put("NODE_ID", param.getString("NODE_ID"));
        inAttr.put("PRODUCT_ID", param.getString("BUSI_CODE"));  
        inAttr.put("PAGE_LEVE", "0"); 
        setInAttr(inAttr);
        
        IData comninfo = new DataMap();
        comninfo.put("FLOW_ID", param.getString("BPM_TEMPLET_ID"));
        comninfo.put("NODE_ID", param.getString("NODE_ID"));
        comninfo.put("PRODUCT_ID", param.getString("BUSI_CODE"));  
        comninfo.put("PAGE_LEVE", "1"); 
        setComminfo(comninfo);
        
        //加载审核人信息
        busi.put("AUDIT_TIME", SysDateMgr4Web.getSysTime());//审核时间
        busi.put("AUDIT_DEPART_NAME", getVisit().getDepartName());//审核部门名称
        busi.put("AUDIT_STAFF", getVisit().getStaffName());//审核人名称
        busi.put("AUDIT_PHONE", getVisit().getSerialNumber());//审核人电话
        busi.put("IBSYSID", ibsysid);
        setBusi(busi);
    }
    
    private void dealAttachInfo(IData info, String ibsysid, String nodeId) throws Exception
    {
    	IData inParam = new DataMap();
        inParam.put("IBSYSID", ibsysid);
        inParam.put("NODE_ID", "apply");
        IDataset attachList = new DatasetList();
        IDataset attachInfos = CSViewCall.call(this, "SS.WorkformAttachSVC.qryByIbsysidNode", inParam);
        if(DataUtils.isNotEmpty(attachInfos))
        {
        	for(int i = 0 ; i < attachInfos.size() ; i ++)
        	{
        		IData fileInfo = attachInfos.getData(i);
        		String attachType = attachInfos.getData(i).getString("ATTACH_TYPE", "");
        		if ("D".equals(attachType)) {
        			fileInfo.put("FILE_TYPE", "资费附件");
				}
        		if ("C".equals(attachType)) {
        			fileInfo.put("FILE_TYPE", "合同附件");
				}
        		if ("P".equals(attachType)) {
        			fileInfo.put("FILE_TYPE", "普通附件");
				}
        		if ("Z".equals(attachType)) {
        			fileInfo.put("FILE_TYPE", "资管附件");
				}
        		attachList.add(fileInfo);
        	}
        	
        }
        
        setFileInfos(attachList);
        
    }
    
    public abstract void setWorkformData(IData workformData) throws Exception;
    public abstract void setOffer(IData offer) throws Exception;
    public abstract void setInAttr(IData inAttr) throws Exception;
    public abstract void setBusi(IData busi) throws Exception;
    public abstract void setInfo(IData info) throws Exception;
    public abstract void setFileInfos(IDataset attachInfos) throws Exception;
    public abstract void setComminfo(IData comminfo) throws Exception;
    public abstract void setTrDetails(IDataset trDetails)throws Exception;
	public abstract void setEdits(IDataset edits)throws Exception;
	public abstract void setEdit(IData edit)throws Exception;
	public abstract void setTrDetail(IData trDetail)throws Exception;
}
