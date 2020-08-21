package com.asiainfo.veris.crm.iorder.web.igroup.esop.audit;

import java.util.Iterator;

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
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class AuditWorkForm extends CSBasePage{
	/**
     * @Description: 初始化页面方法
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
    	this.queryWorkForm(cycle);
    }
    /**
     * @Description: 根据条件查询
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void queryWorkForm(IRequestCycle cycle) throws Exception
    {
    	IData input =  getData();
    	String ibsysId =  input.getString("IBSYSID");
    	String groupId =  input.getString("GROUP_ID");
    	String auditType = input.getString("AUDITTYPE");
    	String bpmTepletId = input.getString("AUDITBPM");
    	String staffId = getVisit().getStaffId();
    	IData param =  new DataMap();
    	param.put("BPM_TEMPLET_ID", bpmTepletId);
    	if(StringUtils.isNotBlank(ibsysId)){
    		param.put("IBSYSID", ibsysId);
    	}
    	if(StringUtils.isNotBlank(groupId)){
			param.put("GROUP_ID", groupId);
		}
    //	param.put("ATTR_CODE", getVisit().getStaffId());
    	IDataset eweInfos =  CSViewCall.call(this, "SS.EweNodeQrySVC.qryAuditEweByBpmAndIbsysId", param);
    	if(IDataUtil.isEmpty(eweInfos)){
    		CSViewException.apperr(CrmCommException.CRM_COMM_103,"根据业务类型"+bpmTepletId+"未查询到数据！");
    	}
    	IDataset infos =  new DatasetList();
    	for(int i = 0 ;i<eweInfos.size();i++){
    		IData eweInfo = eweInfos.getData(i);
    		String auditStaffId = eweInfo.getString("ATTR_VALUE"); 
    		if("1".equals(auditType) && !staffId.equals(auditStaffId)){
    			continue;
    		}
    		IData stateParam = new DataMap();
    		stateParam.put("IBSYSID",eweInfo.getString("BI_SN"));
    		stateParam.put("ATTR_CODE","AUDITESOPINFO");
    		IData states =  CSViewCall.callone(this, "SS.WorkformOtherSVC.qryOtherByIbsysidAttrCode", stateParam);
    		if("1".equals(auditType) && DataUtils.isNotEmpty(states)){
    			continue;
    		}
    		if(!"1".equals(auditType)){
    			String state = states.getString("ATTR_VALUE","");
        		if(Integer.parseInt(state)!= (Integer.parseInt(auditType)-1)){
        			continue;
        		}
    			IData auditParam =  new DataMap();
    			auditParam.put("IBSYSID", eweInfo.getString("BI_SN"));
    			auditParam.put("ATTR_CODE", "AUDITAUTH");
    			IData auditInfo = CSViewCall.callone(this, "SS.WorkformOtherSVC.qryOtherByIbsysidAttrCode", auditParam);
    			String auditAuth =  auditInfo.getString("ATTR_VALUE");
    			if(!auditAuth.equals(getVisit().getStaffId())){
    				continue;
    			}
    		}
    		String offerCode =  eweInfo.getString("BUSI_CODE");
    		String productName = IUpcViewCall.getOfferInfoByOfferCode(offerCode).getString("OFFER_NAME");
    		eweInfo.put("PRODUCT_NAME", productName);
    		
    		String name =StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
			    	{ "TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[]
			    	{ "AUDITBPM",bpmTepletId}); //查询对应名字
    		eweInfo.put("BPM_TEMPLET_NAME", name);
    		eweInfo.put("AUDITTYPE", auditType);
    		infos.add(eweInfo);
    	}
    	/*if(StringUtils.isNotBlank(productNO)){
    		param.put("PRODUCTNO", productNO);
    	}*/
    	/*IDataset eomsDatas = CSViewCall.call(this, "SS.WorkformEomsStateSVC.qryEomsStateByIbsysidAndProductNo", param);
    	if (IDataUtil.isEmpty(eomsDatas))
        {
    		eomsDatas = CSViewCall.call(this, "SS.WorkformEomsStateSVC.qryEomsStateHByIbsysidAndProductNo", param);
    		if (IDataUtil.isEmpty(eomsDatas))
            {
       			CSViewException.apperr(CrmCommException.CRM_COMM_103,"根据订单流水号"+ibsysId+"和产品实例编号"+"无数据！");
            }
        }
    	IDataset result =  new DatasetList();
    	for(int i=0;i<eomsDatas.size();i++){
    		IData eomsData = eomsDatas.getData(i);
    		String tradeId = eomsData.getString("TRADE_ID");
    		if(StringUtils.isNotBlank(tradeId)){
    			result.add(eomsData);
    		}
    	}
    	if(IDataUtil.isEmpty(result)){
    		CSViewException.apperr(CrmCommException.CRM_COMM_103,"根据订单流水号"+ibsysId+"和产品实例编号"+"查询未生成CRM工单！");
    	}*/
    	setInfos(infos);
    	setInfoCount(infos.size());
    	
    	
    	 // 查询附件
        IData attachInput = new DataMap();
        attachInput.put("IBSYSID", ibsysId);
        attachInput.put("ATTACH_TYPE", "C");
        IDataset filesets = CSViewCall.call(this, "SS.WorkformAttachSVC.qryEopAllAttachByIbsysid", attachInput);
        setAttachInfos(filesets);
    }
    
    /**
     * @Description: 根据条件查询
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void submits(IRequestCycle cycle) throws Exception
    {
    	IData input =  getData();
    	IDataset tables= new DatasetList(input.getString("ROWDATAS"));
    
    	IData param =  new DataMap();
    	param.put("GROUPNME", input.getString("GROUPNME"));
    	param.put("LINE_TYPE", input.getString("LINE_TYPE"));
    	param.put("DISCNT", input.getString("DISCNT"));
    	param.put("WIDTH", input.getString("WIDTH"));
    	param.put("LINE_NUM", input.getString("LINE_NUM"));
    	Boolean getdiff = getdiff(param);
    	if(!getdiff){
    		String auditAuth = input.getString("AUDITSTAFF");
    		if(StringUtils.isBlank(auditAuth)){
    			CSViewException.apperr(CrmCommException.CRM_COMM_103,"稽核结果有不一致时，请选择稽核员工!");
    		}
    	}
    	for(int i=0;i<tables.size();i++){
    		IDataset otherInfos =  new DatasetList();
    		IData table = tables.getData(i);
    		IData other = new DataMap();
    		String auditType = table.getString("AUDIT_TYPE");
    		String ibsysId = table.getString("IBSYSID");
    		other.put("IBSYSID", ibsysId);
    		other.put("NODE_ID", "auditInfo");
    		other.put("ATTR_CODE", "AUDITESOPINFO");
    		if(getdiff){
    			other.put("ATTR_VALUE", Integer.parseInt(auditType));
    		}else{
    			int AUDITTYPE  = Integer.parseInt(auditType)+1;
    			other.put("ATTR_VALUE", Integer.parseInt(auditType)+1);
    			if(AUDITTYPE>5){
    				CSViewException.apperr(CrmCommException.CRM_COMM_103,"二次整改稽核必须所有稽核项为是！");
    			}
    		}
    		otherInfos.add(other);
    		if(!getdiff){
    			String auditAuth = input.getString("AUDITSTAFF");
    			IData otherInfo = new DataMap();
    			otherInfo.put("IBSYSID", ibsysId);
    			otherInfo.put("NODE_ID", "auditInfo");
    			otherInfo.put("ATTR_CODE", "AUDITAUTH");
    			otherInfo.put("ATTR_VALUE", auditAuth);
    			otherInfos.add(otherInfo);
    		}
    		IData map = new DataMap();
        	map.put("OTHER_INFO", otherInfos);
        	map.put("USER_EPARCHY_CODE",  getVisit().getLoginEparchyCode());
    		IData busiNodeId = CSViewCall.callone(this, "SS.WorkformOtherSVC.insertHotherInfo", map);
    		String busiforNodeId = busiNodeId.getString("BUSIFORM_NODE_ID");
    		if(!getdiff){
    				IData result = new DataMap();
    				IData eweMap =  new DataMap();
    				eweMap.put("IBSYSID", ibsysId);
    				eweMap.put("TEMPLET_TYPE", "0");
    				IData eweInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweHByIbsysid",eweMap);
    				if(DataUtils.isEmpty(eweInfo)){
    					eweInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweByIbsysid",eweMap);
    				}
    				String staffId = eweInfo.getString("ACCEPT_STAFF_ID");
    			 	String bpmTempletId = eweInfo.getString("BPM_TEMPLET_ID");
    			 	
    	            String url = "/order/iorder?service=page/igroup.esop.audit.RectificationAudit&listener=initial&IBSYSID=" + ibsysId  + "&BPM_TEMPLET_ID=" + bpmTempletId + "&BUSIFORM_NODE_ID=" + busiforNodeId;
    	            result.put("URL", url);
    	            result.put("BI_SN", ibsysId);
    	            result.put("BUSIFORM_NODE_ID", busiforNodeId);
    	            // result.put("DEAL_STAFF_ID", staffId);
    	            result.put("INFO_AUTH", staffId);
    	            String svcName = "SS.EsopWorkTaskDataSVC.getReadTaskDataInfo";

    	            result.put("BUSI_TYPE_CODE", result.getString("BUSIFORM_OPER_TYPE", ""));
    	            result.put("NODE_DESC", "整改工单");
    	            if("EVIOPDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EDIRECTLINEOPENPBOSS".equals(bpmTempletId)){
    	            	result.put("BUSI_TYPE", "订单开通");
    	            }else{
    	            	result.put("BUSI_TYPE", "订单变更");
    	            }
    	            // result.put("INFO_TYPE",EosStaticData.TASK_TYPE_READ);
    	            IDataset taskDatas = CSViewCall.call(this,svcName, result);
    	            if (DataUtils.isEmpty(taskDatas)) {
    	                CSViewException.apperr(CrmCommException.CRM_COMM_103, "BUSIFORM_NODE_ID:" + result.getString("BUSIFORM_NODE_ID") + "待阅工单生成数据抽取失败！！");
    	            }
    	            IData taskInfoData = taskDatas.getData(0);
    	            taskInfoData.put("INFO_TYPE", "3");
    	            taskInfoData.put("INFO_SIGN", busiforNodeId);
    	            taskInfoData.remove("RECE_OBJS");
    	            taskInfoData.put("RECE_OBJ", staffId);
    	            taskInfoData.put("INFO_CHILD_TYPE", "35");
    	            taskInfoData.put("INFO_SEND_TIME", SysDateMgr.getSysTime());
    	            taskInfoData.put("END_TIME", SysDateMgr.END_DATE_FOREVER);
    	            // 生成待阅工单
    	            CSViewCall.call(this,"SS.WorkTaskMgrSVC.crtWorkTaskInfo", taskInfoData).first();
    	            
    	            IData staff =  new DataMap();
    	    	    staff.put("STAFF_ID", staffId);
    	    	    IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", staff);
    	    	    
    	    	    String smsContent = "尊敬的客户，您有一笔电子工单，工单号：["+ibsysId+"],待处理！";

    	            IData smsParam =  new DataMap();
    	            smsParam.put("STAFF_SN", staffInfo.getString("SERIAL_NUMBER",""));
    	            smsParam.put("SMS_CONTENT", smsContent);
    	            CSViewCall.call(this,"SS.WorkformSendFinishSmsSVC.SendAuditSms", smsParam).first();
    	            
    	            
    		}
    	}
    }
    
    /**
     * @Description: 根据条件查询
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
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
    
    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setInfo(IData info);

    public abstract void setCheckRecordInfos(IDataset checkRecordInfos);
    
    public abstract void setCheckRecordInfo2(IData checkRecordInfo2);
    
    public abstract void setAttachInfos(IDataset checkRecordInfos);
    
    public abstract void setDataLineInfos(IDataset dataLineInfos);
    
    
}
