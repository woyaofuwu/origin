package com.asiainfo.veris.crm.iorder.web.igroup.esop.audit;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class RectificationBpmCheckAudit extends CSBasePage{
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
    	IData input =  getData();
    	String relaibsysId =  input.getString("IBSYSID");
    	String busiformNodeId =  input.getString("BUSIFORM_NODE_ID");
    	IData eweNodeMap  = new DataMap();
    	eweNodeMap.put("BUSIFORM_NODE_ID", busiformNodeId);
    	IData eweNodeInfo  = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweByBusiFormNodeId", eweNodeMap);		
    	String preNodeId =  eweNodeInfo.getString("NODE_ID");
    	IData nodeMap  = new DataMap();
    	nodeMap.put("IBSYSID", relaibsysId);
    	nodeMap.put("ATTR_CODE", "REMARK");
    	nodeMap.put("RECORD_NUM", "0");
    	nodeMap.put("NODE_ID", preNodeId);
    	IData nodeInfo  = CSViewCall.callone(this, "SS.WorkformAttrSVC.qryMaxAttrByAttrCode", nodeMap);		
    	String remark = nodeInfo.getString("ATTR_VALUE");
    	
    	IData mainMap = new DataMap();
    	mainMap.put("IBSYSID", relaibsysId);
    	IData traceInfo  = CSViewCall.callone(this, "SS.WorkformModiTraceSVC.qryModiTraceByIbsysid", mainMap);	
    	if(IDataUtil.isEmpty(traceInfo)) {
            traceInfo  = CSViewCall.callone(this, "SS.WorkformModiTraceSVC.qryModiTraceHByIbsysid", mainMap);
        }
    	String ibsysId = traceInfo.getString("MAIN_IBSYSID");
    	IData eweMap = new DataMap();
		eweMap.put("IBSYSID", ibsysId);
		eweMap.put("TEMPLET_TYPE", "0");
		IData eweInfos = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweByIbsysid", eweMap);
		if(IDataUtil.isEmpty(eweInfos)){
			eweInfos = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweHByIbsysid", eweMap);
		}
    	String bpmTepletId = eweInfos.getString("BPM_TEMPLET_ID");
    	IData param =  new DataMap();
    	param.put("BPM_TEMPLET_ID", bpmTepletId);
    	param.put("IBSYSID", ibsysId);
    	
    //	param.put("ATTR_CODE", getVisit().getStaffId());
    	IData eweInfo =  CSViewCall.callone(this, "SS.EweNodeQrySVC.qryAuditInfoEweByBpmAndIbsysId", param);
    	if(IDataUtil.isEmpty(eweInfo)){
    		CSViewException.apperr(CrmCommException.CRM_COMM_103,"根据业务类型"+bpmTepletId+"和订单号"+ibsysId+"未查询到数据！");
    	}
    	IDataset infos =  new DatasetList();
		String offerCode =  eweInfo.getString("BUSI_CODE");
		String productName = IUpcViewCall.getOfferInfoByOfferCode(offerCode).getString("OFFER_NAME");
		eweInfo.put("PRODUCT_NAME", productName);
		eweInfo.put("PRODUCT_ID", offerCode);
		String name =StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
		    	{ "TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[]
		    	{ "AUDITBPM",bpmTepletId}); //查询对应名字
		eweInfo.put("BPM_TEMPLET_NAME", name);
		IData params =  new DataMap();
		params.put("IBSYSID", relaibsysId);
		IDataset datalineInfos =  CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductByIbsysid", params);
    	if(IDataUtil.isEmpty(datalineInfos)){
    		datalineInfos =  CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductHByIbsysid", params);
    	}
		for(int i =0 ;i<datalineInfos.size();i++){
			IData datalineInfo = datalineInfos.getData(i);
			datalineInfo.putAll(eweInfo);
			String userId =  datalineInfo.getString("USER_ID");
			IData record = new DataMap();
			record.put("IBSYSID", ibsysId);
			record.put("USER_ID", userId);
			IData recordINfo =  CSViewCall.callone(this, "SS.WorkformProductSubSVC.queryHisEopProInfoByuserId", record);
			datalineInfo.put("RECORD_NUM", recordINfo.getString("RECORD_NUM"));
			eweInfo.put("RECORD_NUM", recordINfo.getString("RECORD_NUM"));
			IData inputs = new DataMap();
	 		inputs.put("USER_ID", userId);
	 		inputs.put("PRODUCT_ID", offerCode);
	 		IData lineInfo = new DataMap();
	 		lineInfo = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getUserLineInfoByUserId", inputs);
	 		datalineInfo.put("PRODUCT_NO", lineInfo.getString("59701001","0"));
			infos.add(datalineInfo);
			
		}
		String nodeId =  input.getString("NODE_ID");
		eweInfo.put("IBSYSID", input.getString("IBSYSID"));
		eweInfo.put("NODE_ID", input.getString("NODE_ID"));
		eweInfo.put("BUSIFORM_NODE_ID", input.getString("BUSIFORM_NODE_ID"));
		eweInfo.put("BUSIFORM_ID", input.getString("BUSIFORM_ID"));
		eweInfo.put("BPM_TEMPLET_ID", input.getString("BPM_TEMPLET_ID"));
		eweInfo.put("IN_MODE_CODE", "0");
		eweInfo.put("DEAL_STATE", "0");
		eweInfo.put("BUSI_CODE", input.getString("BUSI_CODE"));
		eweInfo.put("BUSI_TYPE", input.getString("BUSI_TYPE"));
		eweInfo.put("GROUP_ID", input.getString("GROUP_ID"));
		eweInfo.put("NODE_ID", nodeId);
		eweInfo.put("AUDIT_TEXT", remark);
		eweInfo.put("MAIN_IBSYSID", ibsysId);
		setDataLineInfo(eweInfo);
    	setInfos(infos);
    	
    	// 查询附件
        IData attachInput = new DataMap();
        attachInput.put("IBSYSID", ibsysId);
        attachInput.put("ATTACH_TYPE", "C");
        IDataset filesets = CSViewCall.call(this, "SS.SubReleInfoSVC.qryEopAllAttachByIbsysid", attachInput);
        IData attachInput1 = new DataMap();
        attachInput1.put("IBSYSID", relaibsysId);
        attachInput1.put("ATTACH_TYPE", "P");
        IDataset filesets1 = CSViewCall.call(this, "SS.SubReleInfoSVC.qryEopAllAttachByIbsysid", attachInput1);
        filesets.addAll(filesets1);
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
    	
    	IData data = getData();
    	String  mainIbsysId= data.getString("MAIN_IBSYSID");
    	IData param =  new DataMap();
     	param.put("GROUPNME", data.getString("GROUPNME"));
     	param.put("LINE_TYPE", data.getString("LINE_TYPE"));
     	param.put("DISCNT", data.getString("DISCNT"));
     	param.put("WIDTH", data.getString("WIDTH"));
     	Boolean getdiff = getdiff(param);
        String nodeId = data.getString("NODE_ID");
		IData submitData = new DataMap();
		IData nodeInfo = new DataMap();
		nodeInfo.put("NODE_ID", nodeId);
	
		submitData.put("NODE_TEMPLETE", nodeInfo);
		IData  commData = new DataMap();
		commData.put("IBSYSID", data.getString("IBSYSID"));
		commData.put("NODE_ID", data.getString("NODE_ID"));
		 commData.put("BUSIFORM_NODE_ID", data.getString("BUSIFORM_NODE_ID"));
		 commData.put("BPM_TEMPLET_ID", data.getString("BPM_TEMPLET_ID"));
		 commData.put("BUSI_CODE", data.getString("BUSI_CODE"));
		 commData.put("BUSI_TYPE", data.getString("BUSI_TYPE"));
		submitData.put("COMMON_DATA", commData);
		//submitData.put("EOMS_ATTR_LIST", );
		IDataset atttDatas = new DatasetList();
		IData atttData =  new DataMap();
		atttData.put("REMARK", data.getString("REMARK"));
		atttDatas = saveProductParamInfoFrontData(atttData);
		submitData.put("EOMS_ATTR_LIST", atttDatas);
		 IData busiSpecRele = new DataMap();
		 busiSpecRele.put("BPM_TEMPLET_ID", data.getString("BPM_TEMPLET_ID",""));
		 busiSpecRele.put("BUSI_TYPE", "P");
		 busiSpecRele.put("BUSI_CODE", data.getString("BUSI_CODE",""));
		 submitData.put("BUSI_SPEC_RELE", busiSpecRele);
		 IDataset otherInfo = new DatasetList();
		 IData others  =  new DataMap();
	     others.put("ATTR_CODE", "IS_PASS");
	     others.put("ATTR_NAME", "是否稽核通过");
	     if(!getdiff){
	        others.put("ATTR_VALUE", "1");
	     }
	     otherInfo.add(others);
	     submitData.put("OTHER_LIST", otherInfo);
		//EOSCom
		IData submitParam = ScrDataTrans.buildWorkformSvcParam(submitData);
		
		IDataset otherInfos =  new DatasetList();
		IData other = new DataMap();
		other.put("IBSYSID", mainIbsysId);
		other.put("NODE_ID", "auditInfo");
		other.put("ATTR_CODE", "AUDITAUTH");
		if(getdiff){
		    //稽核通过  一次稽核
			if("aCheckRectification".equals(data.getString("NODE_ID"))){
				other.put("ATTR_VALUE", "4");
			}else{//稽核通过  二次稽核
				other.put("ATTR_VALUE", "8");
			}
		}else{//稽核不通过   一次稽核
			if("aCheckRectification".equals(data.getString("NODE_ID"))){
				other.put("ATTR_VALUE", "5");
			}else{//稽核不通过  二次稽核
				other.put("ATTR_VALUE", "7");
			}
		}
		//存入稽核信息
        IDataset auditParams = saveauditParamInfoFrontData(param, data.getString("IBSYSID"));
        for(int i=0;i<auditParams.size();i++) {
            IData auditData = auditParams.getData(i);
            CSViewCall.callone(this, "SS.WorkformAttrSVC.insertAttrAudtiInfo", auditData);
        }
		IData params =  new DataMap();
		params.put("IBSYSID", data.getString("IBSYSID"));
		IDataset datalineInfos =  CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductByIbsysid", params);
    	if(IDataUtil.isEmpty(datalineInfos)){
    		datalineInfos =  CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductHByIbsysid", params);
    	}
		for(int i =0 ;i<datalineInfos.size();i++){
		    IData cloneOther = (IData) Clone.deepClone(other);
			IData datalineInfo = datalineInfos.getData(i);
			String userId =  datalineInfo.getString("USER_ID");
			IData record = new DataMap();
			record.put("IBSYSID", mainIbsysId);
			record.put("USER_ID", userId);
			IData recordINfo =  CSViewCall.callone(this, "SS.WorkformProductSubSVC.queryHisEopProInfoByuserId", record);
			datalineInfo.put("RECORD_NUM", recordINfo.getString("RECORD_NUM"));
			cloneOther.put("RECORD_NUM", datalineInfo.getString("RECORD_NUM"));
			datalineInfo.putAll(other);
			otherInfos.add(cloneOther);
		}
    	IData map = new DataMap();
    	map.put("OTHER_INFO", otherInfos);
    	map.put("USER_EPARCHY_CODE",  getVisit().getLoginEparchyCode());
		CSViewCall.call(this, "SS.WorkformOtherSVC.insertHotherInfo", map);  
		IDataset result = CSViewCall.call(this,
				"SS.WorkformRegisterSVC.register", submitParam);
		this.setAjax(result.first());
    	
		

    }
    
    public void queryAllDataline(IRequestCycle cycle) throws Exception {
   	 	IData input = getData();
   	 	IData info = new DataMap();
   	 	String userId = input.getString("USER_ID");
   	 	IDataset datalineInfo =  new DatasetList();
   	 	String productId  =  input.getString("PRODUCT_ID");
		IData lineInfo = new DataMap();
 		IData inputs = new DataMap();
 		inputs.put("USER_ID", userId);
 		inputs.put("PRODUCT_ID", productId);
 		lineInfo = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getUserLineInfoByUserId", inputs);
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
	 	setInfo(info);
	 	setDataLineInfos(datalineInfo);
   	 	
    }
    public Boolean getdiff(IData param) throws Exception
    {
    	Iterator<String> itr = param.keySet().iterator();
		while(itr.hasNext())
		{
			String attrCode = itr.next();
			String attrValue = param.getString(attrCode);
			if("0".equals(attrValue)){
				return false;
			}
		}
		return true;
    	
    }
    
    /**
     * 转换产品参数信息
     */
    public static IDataset saveProductParamInfoFrontData(IData resultSetDataset) throws Exception
    {
    	IDataset productParamAttrset = new DatasetList();
    	Iterator<String> iterator = resultSetDataset.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productParamAttr = new DataMap();
            String key = iterator.next();
            Object value = resultSetDataset.get(key);
            productParamAttr.put("ATTR_CODE", key);
            productParamAttr.put("ATTR_VALUE", value);
            productParamAttr.put("RECORD_NUM", "0");
            productParamAttrset.add(productParamAttr);

        }
        
		return productParamAttrset;
    	
    }
    /**
     * 转换稽核信息
     */
    public static IDataset saveauditParamInfoFrontData(IData resultSetDataset, String ibsysid) throws Exception
    {
        IDataset auditParamAttrset = new DatasetList();
        Iterator<String> iterator = resultSetDataset.keySet().iterator();
        while (iterator.hasNext())
        {
            IData auditParamAttr = new DataMap();
            String key = iterator.next();
            Object value = resultSetDataset.get(key);
            auditParamAttr.put("ATTR_CODE", key);
            auditParamAttr.put("ATTR_VALUE", value);
            auditParamAttr.put("RECORD_NUM", "0");
            auditParamAttr.put("NODE_ID", "aCheckRectification");
            auditParamAttr.put("IBSYSID", ibsysid);
            auditParamAttrset.add(auditParamAttr);

        }
        return auditParamAttrset;
        
    }
    
    
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
    public abstract void setAttachInfos(IDataset checkRecordInfos);
    public abstract void setDataLineInfos(IDataset dataLineInfos);
    public abstract void setDataLineInfo(IData dataLineInfo);
}
