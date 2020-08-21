package com.asiainfo.veris.crm.iorder.web.igroup.esop.audit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class RectificationAudit extends CSBasePage{
	String ibsysId = "";
	String busiformNodeId = "";
	/**
     * @Description: 初始化页面方法
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
    	IData data =  getData();
    	ibsysId = data.getString("IBSYSID");
    	busiformNodeId = data.getString("BUSIFORM_NODE_ID");
    	String bpm = data.getString("BPM_TEMPLET_ID");
    	IData info  = new DataMap();
    	info.put("STAFF_ID", getVisit().getStaffId());
    	IData staff =  new DataMap();
 	    staff.put("STAFF_ID", getVisit().getStaffId());
 	    IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", staff);
    	info.put("STAFF_PHONE", staffInfo.getString("SERIAL_NUMBER",""));
    	
    	IData eweInfo =  new DataMap();
    	IData productParam =  new DataMap();
    	productParam.put("IBSYSID", ibsysId);
    	IData product =  CSViewCall.callone(this, "SS.WorkformSubscribeSVC.qryWorkformSubscribeByIbsysid", productParam);
    	if(IDataUtil.isEmpty(product)){
    		product =  CSViewCall.callone(this, "SS.WorkformSubscribeSVC.qryWorkformHSubscribeByIbsysid", productParam);
    	}
    	String offerCode =  product.getString("BUSI_CODE");
		String productName = IUpcViewCall.getOfferInfoByOfferCode(offerCode).getString("OFFER_NAME");
		
    	eweInfo.put("PRODUCT_NAME", productName);
    	eweInfo.put("BI_SN", ibsysId);
    	eweInfo.put("CUST_NAME", product.getString("CUST_NAME"));
    	eweInfo.put("GROUP_ID", product.getString("GROUP_ID"));
    	eweInfo.put("ACCEPT_STAFF_ID", getVisit().getStaffId());
		String name =StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
		    	{ "TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[]
		    	{ "AUDITBPM",bpm}); //查询对应名字
		eweInfo.put("BPM_TEMPLET_NAME", name);
		info.putAll(eweInfo);
    	setInfo(info);
    	
    	 // 查询附件
        IData attachInput = new DataMap();
        attachInput.put("IBSYSID", ibsysId);
        attachInput.put("ATTACH_TYPE", "C");
        IDataset filesets = CSViewCall.call(this, "SS.WorkformAttachSVC.qryEopAllAttachByIbsysid", attachInput);
        if(IDataUtil.isEmpty(filesets)){
        	IData map = new DataMap();
        	map.put("IBSYSID", ibsysId);
     	   	IDataset orders = CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductByIbsysid", map);
     	 	if(DataUtils.isEmpty(orders)){
     	 		orders= CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductHByIbsysid", map);
     	 	}
     	 	String userId = orders.first().getString("USER_ID");
     	 	IData userInfo = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this,userId);
     	 	String contractId =  userInfo.getData("GRP_USER_INFO").getString("CONTRACT_ID");
     	 	String custId = userInfo.getData("GRP_USER_INFO").getString("CUST_ID");
     	 	IData contractInfo = new DataMap();
     	 	contractInfo.put("CONTRACT_ID", contractId);
     	 	contractInfo.put("CUST_ID", custId);
        	filesets = CSViewCall.call(this, "SS.GrpLineInfoQrySVC.queryContractInfo", contractInfo);
        	for(int i=0;i<filesets.size();i++){
        		IData fileset =  filesets.getData(i);
        		fileset.put("FILE_ID", fileset.getString("CONTRACT_FILE_ID"));
        	}
        }
        setAttachInfos(filesets);
    }
    
    public void qryStaffinfo(IRequestCycle cycle) throws Exception {
   	 IData input = getData();
        IData inParam = new DataMap();
        String staffName = input.getString("cond_StaffName", "");
        String roleId = pageutil.getStaticValue("TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "AUDIT_ROLE", "ROLE_ID" });
        if (StringUtils.isBlank(roleId)) {
            CSViewException.apperr(GrpException.CRM_GRP_713, "没有获取计费方式审核角色配置！请检查TD_B_EWE_CONFIG表配置！");
        }
        // input.put("DEPART_ID", departId);
        inParam.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());
        inParam.put("START_MAX", "0");
        inParam.put("ROWNUM_", "1000");
        inParam.put("X_GETMODE", "13");
        inParam.put("RIGHT_CODE", roleId);
        IDataset staffList = CSViewCall.call(this, "QSM_ChkSysOrgInfo", inParam);
        if(StringUtils.isNotBlank(staffName)){
        	for(int i = 0;i<staffList.size();i++){
        		IData staff = staffList.getData(i);
        		if(staffName.equals(staff.getString("STAFF_NAME"))){
        			IDataset staffListName = new DatasetList();
        			staffListName.add(staff);
        			setInfos(staffListName);
        		}
        	}
        }else{
        	setInfos(staffList);
        }
   	/*IData input  = getData();
   	IData inParam = new DataMap();
   	String staffName = input.getString("cond_StaffName","");
   	if(StringUtils.isNotBlank(staffName)){
   		inParam.put("STAFF_NAME", staffName);
   	}
   	inParam.put("FLAG", "1");
   	IDataset info = CSViewCall.call(this, "SS.QcsGrpIntfSVC.qryStaffinfoForESOPNEW", inParam);
   	setInfos(info);*/
   }
    
    public void submits(IRequestCycle cycle) throws Exception {
    	IData data =  getData();
    	IDataset otherInfos =  new DatasetList();
		IData other = new DataMap();
		other.put("IBSYSID", ibsysId);
		other.put("NODE_ID", "auditInfo");
		other.put("ATTR_CODE", "AUDITAUTH");
		other.put("ATTR_VALUE", data.getString("AUDITSTAFF"));
		otherInfos.add(other);
    	IData map = new DataMap();
    	map.put("OTHER_INFO", otherInfos);
    	map.put("USER_EPARCHY_CODE",  getVisit().getLoginEparchyCode());
		CSViewCall.call(this, "SS.WorkformOtherSVC.insertHotherInfo", map);
		
		 IData maps = new DataMap();
		 maps.put("INFO_SIGN", busiformNodeId);
		 maps.put("INFO_CHILD_TYPE", "35");
		 IData infoId = CSViewCall.callone(this, "SS.WorkTaskMgrSVC.queryWorkInfoByInfoSign", maps); //存attr表
		 
		 IData temp =  new DataMap();
		 temp.put("INFO_STATUS", "9");
		 temp.put("INFO_ID", infoId.getString("INFO_ID"));
		 
		 CSViewCall.call(this, "SS.WorkTaskMgrSVC.updateOpTaskInfoStatus", temp); //设置为已读待阅
		 
		 
 	    String smsContent = "尊敬的客户，您有一笔稽核工单，工单号：["+ibsysId+"],待处理！请到工单稽核页面处理！";

         IData smsParam =  new DataMap();
         smsParam.put("STAFF_SN", data.getString("AUDITPHONE"));
         smsParam.put("SMS_CONTENT", smsContent);
         CSViewCall.call(this,"SS.WorkformSendFinishSmsSVC.SendAuditSms", smsParam);
         

    }
    
    public void queryAllDataline(IRequestCycle cycle) throws Exception {
   	 	IData input = getData();
   	 	IData info = new DataMap();
   	 	String ibsysId = input.getString("IBSYSID");
   	 	IData map = new DataMap();
   	 	map.put("IBSYSID", ibsysId);
	   	IDataset orders = CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductByIbsysid", map);
	 	if(DataUtils.isEmpty(orders)){
	 		orders= CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductHByIbsysid", map);
	 	}
	 	IDataset datalineInfo = new DatasetList();
	 	for(int j=0;j< orders.size();j++){
	 		IData order = orders.getData(j); 
	 		String userId =  order.getString("USER_ID");
		 	String productId  = order.getString("PRODUCT_ID");
		 	String tradeId  = 	order.getString("TRADE_ID");
		 	IData lineInfo  = new DataMap();
		 	IData attrParam =  new DataMap();
		 	attrParam.put("TRADE_ID", tradeId);
		 	attrParam.put("INST_TYPE", "D");
		 	IDataset attrInfos = CSViewCall.call(this, "CS.TradeAttrInfoQrySVC.getTradeAttrInfoByInstType", attrParam);
		 	if(IDataUtil.isNotEmpty(attrInfos)){
		 		for(int i=0;i<attrInfos.size();i++){
		 			IData attrInfo =  attrInfos.getData(i);
		 			lineInfo.put(attrInfo.getString("ATTR_CODE"), attrInfo.getString("ATTR_VALUE"));
		 		}
		 	}
		 	else{
		 		IData inputs = new DataMap();
		 		inputs.put("USER_ID", userId);
		 		inputs.put("PRODUCT_ID", productId);
		 		
		 		lineInfo = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getUserLineInfoByUserId", inputs);
		
		 	}
		 		lineInfo.put("USER_ID", userId);
				lineInfo.put("NOTIN_LINE_NO", lineInfo.getString("59701001","0"));
				lineInfo.put("NOTIN_RSRV_STR1", lineInfo.getString("59701002","0"));
				lineInfo.put("NOTIN_RSRV_STR2", lineInfo.getString("59701003","0"));
				lineInfo.put("NOTIN_RSRV_STR3", lineInfo.getString("59701004","0"));
				lineInfo.put("NOTIN_RSRV_STR4", lineInfo.getString("59701005","0"));
				
				lineInfo.put("NOTIN_RSRV_STR10", lineInfo.getString("59701007","0"));
				lineInfo.put("NOTIN_RSRV_STR11", lineInfo.getString("59701008","0"));
				lineInfo.put("NOTIN_RSRV_STR12", lineInfo.getString("59701009","0"));
				
				lineInfo.put("NOTIN_RSRV_STR6", lineInfo.getString("59701010","0"));
				lineInfo.put("NOTIN_RSRV_STR7", lineInfo.getString("59701011","0"));
				lineInfo.put("NOTIN_RSRV_STR8", lineInfo.getString("59701012","0"));
				lineInfo.put("NOTIN_RSRV_STR16", lineInfo.getString("59701013","0"));
				lineInfo.put("PRODUCT_ID", productId);
				info.put("PRODUCT_ID", productId);
				datalineInfo.add(lineInfo);
	 	}
	 	
	 	setInfo(info);
	 	setDataLineInfos(datalineInfo);
   	 	
    }
    
    
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
    public abstract void setAttachInfos(IDataset checkRecordInfos);
    public abstract void setDataLineInfos(IDataset dataLineInfos);
}
