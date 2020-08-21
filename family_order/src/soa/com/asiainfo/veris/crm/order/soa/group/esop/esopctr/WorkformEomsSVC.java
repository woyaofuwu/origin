package com.asiainfo.veris.crm.order.soa.group.esop.esopctr;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttachBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;

public class WorkformEomsSVC extends GroupOrderService
{
	private static final long serialVersionUID = 1L;

	public IDataset saveNewWorkSheet(IData data) throws Exception
	{
		IDataset resultInfos = new DatasetList();
		String subIbsysid = data.getString("SUB_IBSYSID", "");
		String attachSubIbsysid = data.getString("ATTACH_SUB_IBSYSID","");
		String operType = data.getString("OPER_TYPE", "");
		String nodeId = data.getString("NODE_ID", "");
		String busiformId = data.getString("BUSIFORM_ID", "");
		String busiformNodeId = data.getString("BUSIFORM_NODE_ID", "");
		String staffId = data.getString("DEAL_STAFF_ID", "");
		String eparchyCode = data.getString("DEAL_EPARCHY_CODE", "");
		String parentBpmTempletId = data.getString("PARENT_BPM_TEMPLET_ID", "");
		String parentNodeId = data.getString("PARENT_NODE_ID", "");
		String parentBusiCode = data.getString("PARENT_BUSI_CODE", "");
		
		String lineType = "1";//默认本地市
		//查询是否跨地市
		IData lineTypeInfo = WorkformAttrBean.qryAttrBySubIbsysidRecordCode(subIbsysid, "DIRECTLINE_SCOPE", "0");
		if(DataUtils.isNotEmpty(lineTypeInfo))
		{
			lineType = lineTypeInfo.getString("ATTR_VALUE", "");
		}
		//查询流程节点配置，是发什么类型报文
		IData interInfo = EweConfigQry.queryEomsInterName("EOMS_INTERNAME", parentBpmTempletId, parentNodeId, parentBusiCode, lineType, EcEsopConstants.STATE_VALID);
		if(DataUtils.isEmpty(interInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CONFIGNAME:[EOMS_INTERNAME]，PARAMNAME:["+parentBpmTempletId+"]，VALUEDESC:["+parentNodeId+"]，RSRV_STR1:["+parentBusiCode+"]，RSRV_STR2:["+lineType+"]获取发送eoms接口配置数据失败。请确认TD_B_EWE_CONFIG表数据是否正确！");
		}
		String eomsInterName = interInfo.getString("PARAMVALUE", "");
		IData sheetInfo = EweConfigQry.qryByConfigParamNameRsrv1("SHEETTYPE", eomsInterName, operType, EcEsopConstants.STATE_VALID);
		if(DataUtils.isEmpty(sheetInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CONFIGNAME:[SHEETTYPE]，PARAMNAME:["+eomsInterName+"]，RSRV_STR1:["+operType+"]获取发送eoms接口类型配置数据失败。请确认TD_B_EWE_CONFIG表数据是否正确！");
		}
		String sheetType = sheetInfo.getString("PARAMVALUE", "");
		String serviceType = sheetInfo.getString("VALUEDESC", "");
		
		IDataset attrInfos = WorkformAttrBean.qryAttrRecodeNumBySubIbsysid(subIbsysid);
		if(DataUtils.isEmpty(attrInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SUB_IBSYSID:["+subIbsysid+"]获取数据失败。请确认tf_b_eop_attr表数据是否正确！");
		}
		
		IDataset allAttrInfos = new DatasetList();
		//int seq = 0;
		String newSubIbsysId = SeqMgr.getSubIbsysId();
		
		String ibsysid = attrInfos.first().getString("IBSYSID", "");
		String serialNo = "ESOP"+ibsysid+1;
		
		//拼业务数据
		for(int i = 0 ; i < attrInfos.size() ; i ++)
		{
			IData attrInfo = attrInfos.getData(i);
			attrInfo.put("SUB_IBSYSID", newSubIbsysId);
			attrInfo.put("NODE_ID", nodeId);
			attrInfo.put("SEQ", SeqMgr.getAttrSeq());
			//seq++;
			
			String attrCode = attrInfo.getString("ATTR_CODE");
			String attrValue = attrInfo.getString("ATTR_VALUE");
			
			if ("CRMNO".equals(attrCode) && StringUtils.isEmpty(attrValue)) {
				attrInfo.put("ATTR_VALUE", ibsysid);
			}
			
			if ("CONFCRMTICKETNO".equals(attrCode) && StringUtils.isEmpty(attrValue)) {
				attrInfo.put("ATTR_VALUE", serialNo);
			}
			
			allAttrInfos.add(attrInfo);
		}
		//存tf_b_eop_attr新节点
		WorkformAttrBean.insertWorkformAttr(allAttrInfos);
		
		//处理附件
		IDataset attachInfos = WorkformAttachBean.qryEopAttrBySubIbsysid(subIbsysid);

		IDataset allAttachInfos = new DatasetList();
		if(DataUtils.isNotEmpty(attachInfos))
		{
			//拼公共数据
			for(int i = 0 ; i < attachInfos.size() ; i ++)
			{
				IData attachInfo = attachInfos.getData(i);
				attachInfo.put("SUB_IBSYSID", newSubIbsysId);
				attachInfo.put("NODE_ID", nodeId);
				attachInfo.put("SEQ", SeqMgr.getAttrSeq());
				allAttachInfos.add(attachInfo);
			}
		}
		if(DataUtils.isNotEmpty(allAttachInfos))
		{
			WorkformAttachBean.insertWorkformAttach(allAttachInfos);
		}

		String attachRef = "";
		
		//查询员工信息
	    IDataset staffInfos = StaffInfoQry.queryValidStaffById(staffId);
		if(DataUtils.isEmpty(staffInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据工号["+staffId+"]查询员工失败！");
		}
		IData staffInfo = staffInfos.first();
		
		//拼tf_b_eop_eoms表数据
		IData eomsData = new DataMap();
		eomsData.put("SUB_IBSYSID",newSubIbsysId);         
		eomsData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		eomsData.put("IBSYSID", ibsysid);
		eomsData.put("BUSI_ID", busiformNodeId); 
		eomsData.put("GROUP_SEQ", "0");         
		eomsData.put("TRADE_DRIECT","0");//发送    
		eomsData.put("BPM_TEMPLET_ID", "EOMS_INTF");    
		eomsData.put("SUB_BUSI_TYPE",parentBpmTempletId);     
		eomsData.put("BPM_ID", busiformId);           
		eomsData.put("NODE_ID", nodeId);          
		eomsData.put("PRODUCT_ID", parentBusiCode);        
		eomsData.put("OPER_TYPE",operType);         
		eomsData.put("DEAL_STATE",0);        
		eomsData.put("EOMS_ORDER_CODE","");
		eomsData.put("EOMS_TACHE_CODE","");   
		eomsData.put("INSERT_TIME",SysDateMgr.getSysTime());     
		eomsData.put("UPDATE_TIME",SysDateMgr.getSysTime());      
		eomsData.put("SHEETTYPE",sheetType);         
		eomsData.put("SERVICETYPE",serviceType);        
		eomsData.put("SERIALNO", serialNo);          
		eomsData.put("ATTACHREF", attachRef);         
		eomsData.put("OPPERSON",staffInfo.getString("STAFF_NAME", ""));        
		eomsData.put("OPCORP","");          
		eomsData.put("OPDEPART",staffInfo.getString("DEPART_NAME", ""));       
		eomsData.put("OPCONTACT",staffInfo.getString("SERIAL_NUMBER", ""));     
		eomsData.put("OPTIME", SysDateMgr.getSysTime());         
		eomsData.put("OPDETAIL","");         
		eomsData.put("ERRLIST","");         
		eomsData.put("RESULT_TIME","");     
		eomsData.put("PLAN_DEAL_TIME","");
		eomsData.put("REAL_DEAL_TIME",""); 
		eomsData.put("CITY_CODE",staffInfo.getString("CITY_CODE", ""));
		eomsData.put("EPARCHY_CODE", eparchyCode);    
		eomsData.put("DEPART_ID",staffInfo.getString("DEPART_ID", ""));
		eomsData.put("DEPART_NAME",staffInfo.getString("DEPART_NAME", ""));       
		eomsData.put("STAFF_ID",staffInfo.getString("STAFF_ID", ""));  
		eomsData.put("STAFF_NAME",staffInfo.getString("STAFF_NAME", ""));
		eomsData.put("STAFF_PHONE",staffInfo.getString("SERIAL_NUMBER", ""));       
		eomsData.put("RSRV_STR1","");         
		eomsData.put("RSRV_STR2","");           
		eomsData.put("RSRV_STR3",eomsInterName);            
		eomsData.put("RSRV_STR4","");            
		eomsData.put("RSRV_STR5","");            
		eomsData.put("RSRV_STR6","");            
		eomsData.put("RSRV_STR7",parentBpmTempletId);            
		eomsData.put("REMARK","");      
		eomsData.put("RECORD_NUM", "0");
		
		WorkformEomsBean.insertWorkformEoms(eomsData);
		
		IDataset productSubInfos = WorkformProductSubBean.qryProductByIbsysid(ibsysid);
		if(DataUtils.isEmpty(productSubInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID["+ibsysid+"]查询TF_B_EOP_PRODUCT_SUB表数据失败！");
		}
		for (int i = 0; i < productSubInfos.size(); i++) {
			IData productSubInfo = productSubInfos.getData(i);
			String record_num = productSubInfo.getString("RECORD_NUM");
			eomsData.put("RECORD_NUM", record_num);
			WorkformEomsBean.insertWorkformEoms(eomsData);
		}
		
		//插入tf_f_eop_eoms_state表
		this.dealDetail(sheetType, ibsysid, subIbsysid, busiformId, busiformNodeId, serialNo, nodeId);
		
		//拼tf_b_eop_eoms_sub表
//		this.saveEomsSub(allAttrInfos);
		
		//更新sub_bi_sn
		EweNodeQry.updWorkformNodeByPk(busiformNodeId, newSubIbsysId, EcEsopConstants.STATE_VALID);
		return resultInfos;
	}

	private void dealDetail(String sheetType, String ibsysid, String subIbsysid, String busiformId, String busiformNodeId, String serialNo, String nodeId) throws Exception
	{
		String tradeId = "";
		String busiState = "";
		String productNo = "";
		
		IDataset proSubInfos = WorkformProductSubBean.qryProductByIbsysid(ibsysid);
		if (IDataUtil.isEmpty(proSubInfos)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID="+ibsysid+"获取TF_B_EOP_PRODUCT_SUB表数据失败！");
		}
		
		IDataset eomsStatelist = new DatasetList();
		for (int i = 0; i < proSubInfos.size(); i++) {
			String recordNum = proSubInfos.getData(i).getString("RECORD_NUM");
			IData tradeInfo = WorkformAttrBean.qryAttrBySubIbsysidRecordCode(subIbsysid, "TRADEID", recordNum);
			tradeId = tradeInfo.getString("ATTR_VALUE", "");
			IData productInfo = WorkformAttrBean.qryAttrBySubIbsysidRecordCode(subIbsysid, "PRODUCTNO", recordNum);
			productNo = productInfo.getString("ATTR_VALUE", "");
			
			busiState = "-1";//StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EOMS_BUSI_STATE", nodeId});
			if(StringUtils.isEmpty(busiformId))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CONFIGNAME:[EOMS_BUSI_STATE]，PARAMNAME:["+nodeId+"]获取发送eoms接口配置数据失败。请确认TD_B_EWE_CONFIG表数据是否正确！");
			}
			
			//查询是否有勘察单
			IDataset detailInfos = WorkformEomsStateBean.qryEomsStateByIbsysidAndRecordNum(subIbsysid, recordNum);
			if(DataUtils.isNotEmpty(detailInfos))
			{
				WorkformEomsStateBean.updEomsStateByPk(ibsysid, recordNum, tradeId, productNo, busiState);
			}
			else
			{
				IData eomsState = new DataMap();
				eomsState.put("INST_ID", SeqMgr.getIntfId());
				eomsState.put("IBSYSID", ibsysid);
				eomsState.put("RECORD_NUM", recordNum);
				eomsState.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
				eomsState.put("BUSI_STATE", busiState);
				eomsState.put("SERIALNO", serialNo);
				eomsState.put("PRODUCT_NO", productNo);
				eomsState.put("TRADE_ID", tradeId);
				eomsState.put("DEAL_TYPE", TRADE_MODIFY_TAG.Add.getValue());
				eomsState.put("CREATE_DATE", SysDateMgr.getSysTime());
				eomsState.put("REMARK", "");
				eomsState.put("RSRV_STR1", "");
				eomsState.put("RSRV_STR2", "");
				eomsState.put("RSRV_STR3", "");
				eomsStatelist.add(eomsState);
			}
		}
		
		WorkformEomsStateBean.insertWorkformEomsState(eomsStatelist);
	}
	
	public void updateEomsState(IData param) throws Exception {
		
		String ibsysId = IDataUtil.chkParam(param, "IBSYSID");
        String operType = IDataUtil.chkParam(param, "OPER_TYPE");
        String recordNum = param.getString("RECORD_NUM","");
        
        IDataset eomsStateInfos = WorkformEomsStateBean.qryEomsStateByIbsysid(ibsysId);//WorkformEomsBean.queryBySubIbsysIdAndOperTypeDesc(subIbsysId, operType);
        if (DataUtils.isEmpty(eomsStateInfos))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:["+ibsysId+"]获取TF_B_EOP_EOMS_STATE数据失败！");
        }
        
        String busiState = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EOMS_BUSI_STATE", operType});
        param.put("BUSI_STATE", busiState);
        
        if (StringUtils.isNotEmpty(recordNum)) {
        	param.put("RECORD_NUM", recordNum);
        	WorkformEomsStateBean.updateStateByRecordNum(param);
		}else{
			WorkformEomsStateBean.updateStateInfo(param);
		}
	}
	

	public IDataset saveUnHangWorkSheet(IData data) throws Exception
	{
		IDataset resultInfos = new DatasetList();
		String recordNum = data.getString("RECORD_NUM", "");
		String operType = data.getString("OPER_TYPE", "");
		String nodeId = data.getString("NODE_ID", "");
		String busiformId = data.getString("BUSIFORM_ID", "");
		String busiformNodeId = data.getString("BUSIFORM_NODE_ID", "");
		String eparchyCode = data.getString("DEAL_EPARCHY_CODE", "");
		String parentBpmTempletId = data.getString("PARENT_BPM_TEMPLET_ID", "");
		String parentBusiCode = data.getString("PARENT_BUSI_CODE", "");
		String ibsysId =  data.getString("IBSYSID","");
		String staffId =  data.getString("STAFF_ID");
		String productNo =  data.getString("PRODUCT_NO");
		
		String lineType = "1";//默认本地市
		//查询流程节点配置，是发什么类型报文
		IData interInfo = EweConfigQry.queryEomsInterName("EOMS_INTERNAME", parentBpmTempletId, nodeId, parentBusiCode, lineType, EcEsopConstants.STATE_VALID);
		if(DataUtils.isEmpty(interInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CONFIGNAME:[EOMS_INTERNAME]，PARAMNAME:["+parentBpmTempletId+"]，VALUEDESC:["+nodeId+"]，RSRV_STR1:["+parentBusiCode+"]，RSRV_STR2:["+lineType+"]获取发送eoms接口配置数据失败。请确认TD_B_EWE_CONFIG表数据是否正确！");
		}
		String eomsInterName = interInfo.getString("PARAMVALUE", "");
		IData sheetInfo = EweConfigQry.qryByConfigParamNameRsrv1("SHEETTYPE", eomsInterName, operType, EcEsopConstants.STATE_VALID);
		if(DataUtils.isEmpty(sheetInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CONFIGNAME:[SHEETTYPE]，PARAMNAME:["+eomsInterName+"]，RSRV_STR1:["+operType+"]获取发送eoms接口类型配置数据失败。请确认TD_B_EWE_CONFIG表数据是否正确！");
		}
		String sheetType = sheetInfo.getString("PARAMVALUE", "");
		String serviceType = sheetInfo.getString("VALUEDESC", "");
		//拼写attr数据
		IData insertData =  new DataMap();
		insertData = data.getData("ATTR_DATA");
		IDataset attrInfos = new DatasetList();
    	Iterator<String> iterator = insertData.keySet().iterator();
        while (iterator.hasNext())
        {
        	IData productParamAttr = new DataMap();
            String key = iterator.next();
            
	    	Object value = insertData.get(key);
	    	productParamAttr.put("ATTR_CODE", key);
	    	productParamAttr.put("ATTR_VALUE", value);
	    	productParamAttr.put("RECORD_NUM", recordNum);
	        
	        attrInfos.add(productParamAttr);
        }
//		int seq = 0;
		//String newSubIbsysId = SeqMgr.getSubIbsysId();
		IDataset eomsInfo = WorkformEomsBean.qryworkformEOMSByIbsysidRecordNum(ibsysId,recordNum);
		if(IDataUtil.isEmpty(eomsInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:["+ibsysId+"]和RECORD_NUM:["+recordNum+"]获取数据失败。请确认TF_B_EOP_EOMS表数据是否正确！");
		}
		String newSubIbsysId = eomsInfo.first().getString("SUB_IBSYSID");
		String grougseqs  = eomsInfo.first().getString("GROUP_SEQ");
		int groupseq = Integer.parseInt(grougseqs)+1;
//		IDataset oldAttrInfo = WorkformAttrBean.qryAttrRecodeNumBySubIbsysid(newSubIbsysId);
//		if(IDataUtil.isNotEmpty(oldAttrInfo)){
//			seq = oldAttrInfo.size();
//		}
		//拼业务数据
		for(int i = 0 ; i < attrInfos.size() ; i ++)
		{
			IData attrInfo = attrInfos.getData(i);
			attrInfo.put("SUB_IBSYSID", newSubIbsysId);
			attrInfo.put("NODE_ID", nodeId);
			attrInfo.put("IBSYSID", ibsysId);
			attrInfo.put("GROUP_SEQ", groupseq);
			attrInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
			attrInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
			attrInfo.put("SEQ", SeqMgr.getAttrSeq());
//			seq++;
		}
		//存tf_b_eop_attr新节点
		WorkformAttrBean.insertWorkformAttr(attrInfos);
	
		
		String serialNo = "ESOP"+ibsysId+1;
		String attachRef = "";
		
		//查询员工信息
	    IDataset staffInfos = StaffInfoQry.queryValidStaffById(staffId);
		if(DataUtils.isEmpty(staffInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据工号["+staffId+"]查询员工失败！");
		}
		IData staffInfo = staffInfos.first();
		
		//拼tf_b_eop_eoms表数据
		IData eomsData = new DataMap();
		eomsData.put("SUB_IBSYSID",newSubIbsysId);         
		eomsData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		eomsData.put("IBSYSID", ibsysId);
		eomsData.put("BUSI_ID", busiformNodeId); 
		eomsData.put("GROUP_SEQ", groupseq);         
		eomsData.put("TRADE_DRIECT","0");//发送    
		eomsData.put("BPM_TEMPLET_ID", "EOMS_INTF");    
		eomsData.put("SUB_BUSI_TYPE",parentBpmTempletId);     
		eomsData.put("BPM_ID", busiformId);           
		eomsData.put("NODE_ID", nodeId);          
		eomsData.put("PRODUCT_ID", parentBusiCode);        
		eomsData.put("OPER_TYPE",operType);         
		eomsData.put("DEAL_STATE",0);        
		eomsData.put("EOMS_ORDER_CODE","");
		eomsData.put("EOMS_TACHE_CODE","");   
		eomsData.put("INSERT_TIME",SysDateMgr.getSysTime());     
		eomsData.put("UPDATE_TIME",SysDateMgr.getSysTime());      
		eomsData.put("SHEETTYPE",sheetType);         
		eomsData.put("SERVICETYPE",serviceType);        
		eomsData.put("SERIALNO", serialNo);          
		eomsData.put("ATTACHREF", attachRef);         
		eomsData.put("OPPERSON",staffInfo.getString("STAFF_NAME", ""));        
		eomsData.put("OPCORP","");          
		eomsData.put("OPDEPART",staffInfo.getString("DEPART_NAME", ""));       
		eomsData.put("OPCONTACT",staffInfo.getString("SERIAL_NUMBER", ""));     
		eomsData.put("OPTIME", SysDateMgr.getSysTime());         
		eomsData.put("OPDETAIL","");         
		eomsData.put("ERRLIST","");         
		eomsData.put("RESULT_TIME","");     
		eomsData.put("PLAN_DEAL_TIME","");
		eomsData.put("REAL_DEAL_TIME",""); 
		eomsData.put("CITY_CODE",staffInfo.getString("CITY_CODE", ""));
		eomsData.put("EPARCHY_CODE", eparchyCode);    
		eomsData.put("DEPART_ID",staffInfo.getString("DEPART_ID", ""));
		eomsData.put("DEPART_NAME",staffInfo.getString("DEPART_NAME", ""));       
		eomsData.put("STAFF_ID",staffInfo.getString("STAFF_ID", ""));  
		eomsData.put("STAFF_NAME",staffInfo.getString("STAFF_NAME", ""));
		eomsData.put("STAFF_PHONE",staffInfo.getString("SERIAL_NUMBER", ""));       
		eomsData.put("RSRV_STR1","");         
		eomsData.put("RSRV_STR2","");           
		eomsData.put("RSRV_STR3",eomsInterName);            
		eomsData.put("RSRV_STR4","");            
		eomsData.put("RSRV_STR5","");            
		eomsData.put("RSRV_STR6","");            
		eomsData.put("RSRV_STR7",parentBpmTempletId);            
		eomsData.put("REMARK","");      
		eomsData.put("RECORD_NUM", recordNum);
		
		WorkformEomsBean.insertWorkformEoms(eomsData);
		
		//插入tf_f_eop_eoms_state表
		//this.dealDetail(sheetType, ibsysId, subIbsysid, busiformId, busiformNodeId, serialNo, nodeId);
		String operTypes = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EOMS_BUSI_STATE", operType});
		WorkformEomsStateBean.updEomsStateByProductNo(ibsysId,recordNum,productNo,operTypes);		
		//拼tf_b_eop_eoms_sub表
		//this.saveEomsSub(AttrInfos);
		
		//更新sub_bi_sn
		EweNodeQry.updWorkformNodeByPk(busiformNodeId, newSubIbsysId, EcEsopConstants.STATE_VALID);
		IData result = new DataMap();
		result.put("SUB_IBSYSID", newSubIbsysId);
		resultInfos.add(result);
		return resultInfos;
		
	}
	
	public IDataset saveAgreeHangWorkSheet(IData data) throws Exception
	{
		IDataset resultInfos = new DatasetList();
		String subIbsysid = data.getString("SUB_IBSYSID", "");
		String attachSubIbsysid = data.getString("ATTACH_SUB_IBSYSID","");
		String operType = data.getString("OPER_TYPE", "");
		String nodeId = data.getString("NODE_ID", "");
		String busiformId = data.getString("BUSIFORM_ID", "");
		String busiformNodeId = data.getString("BUSIFORM_NODE_ID", "");
		String staffId = data.getString("DEAL_STAFF_ID", "");
		String eparchyCode = data.getString("DEAL_EPARCHY_CODE", "");
		String parentBpmTempletId = data.getString("PARENT_BPM_TEMPLET_ID", "");
		String parentNodeId = data.getString("PARENT_NODE_ID", "");
		String parentBusiCode = data.getString("PARENT_BUSI_CODE", "");
		
		String lineType = "1";//默认本地市
		//查询是否跨地市
		IData lineTypeInfo = WorkformAttrBean.qryAttrBySubIbsysidRecordCode(subIbsysid, "DIRECTLINE_SCOPE", "0");
		if(DataUtils.isNotEmpty(lineTypeInfo))
		{
			lineType = lineTypeInfo.getString("ATTR_VALUE", "");
		}
		//查询流程节点配置，是发什么类型报文
		IData interInfo = EweConfigQry.queryEomsInterName("EOMS_INTERNAME", parentBpmTempletId, parentNodeId, parentBusiCode, lineType, EcEsopConstants.STATE_VALID);
		if(DataUtils.isEmpty(interInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CONFIGNAME:[EOMS_INTERNAME]，PARAMNAME:["+parentBpmTempletId+"]，VALUEDESC:["+parentNodeId+"]，RSRV_STR1:["+parentBusiCode+"]，RSRV_STR2:["+lineType+"]获取发送eoms接口配置数据失败。请确认TD_B_EWE_CONFIG表数据是否正确！");
		}
		String eomsInterName = interInfo.getString("PARAMVALUE", "");
		IData sheetInfo = EweConfigQry.qryByConfigParamNameRsrv1("SHEETTYPE", eomsInterName, operType, EcEsopConstants.STATE_VALID);
		if(DataUtils.isEmpty(sheetInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CONFIGNAME:[SHEETTYPE]，PARAMNAME:["+eomsInterName+"]，RSRV_STR1:["+operType+"]获取发送eoms接口类型配置数据失败。请确认TD_B_EWE_CONFIG表数据是否正确！");
		}
		String sheetType = sheetInfo.getString("PARAMVALUE", "");
		String serviceType = sheetInfo.getString("VALUEDESC", "");
		
		IDataset attrInfos = WorkformAttrBean.qryAttrRecodeNumBySubIbsysid(subIbsysid);
		if(DataUtils.isEmpty(attrInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SUB_IBSYSID:["+subIbsysid+"]获取数据失败。请确认tf_b_eop_attr表数据是否正确！");
		}
		
		IDataset allAttrInfos = new DatasetList();
//		int seq = 0;
		
		String ibsysid = attrInfos.first().getString("IBSYSID", "");
		String serialNo = "ESOP"+ibsysid+1;
		IDataset relaInfo = EweNodeQry.qryBySubBusiformId(busiformId);
		if(IDataUtil.isEmpty(relaInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SUB_BUSIFORM_ID:["+busiformId+"]获取数据失败。请确认TF_B_EWE_RELE表数据是否正确！");
		}
		String bpmTempletId = data.getString("BPM_TEMPLET_ID", "");
		if("eomsUnhangProess".equals(bpmTempletId)){//子子流程
			relaInfo = EweNodeQry.qryBySubBusiformId(relaInfo.first().getString("BUSIFORM_ID"));
			if(IDataUtil.isEmpty(relaInfo)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SUB_BUSIFORM_ID:["+relaInfo.first().getString("BUSIFORM_ID")+"]获取数据失败。请确认TF_B_EWE_RELE表数据是否正确！");
			}
		}
		String recordNum = relaInfo.first().getString("RELE_VALUE");
		IDataset eomsInfo = WorkformEomsBean.qryworkformEOMSByIbsysidRecordNum(ibsysid,recordNum);
		if(IDataUtil.isEmpty(eomsInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:["+ibsysid+"]和RECORD_NUM:["+recordNum+"]获取数据失败。请确认TF_B_EOP_EOMS表数据是否正确！");
		}
		String newSubIbsysId = eomsInfo.first().getString("SUB_IBSYSID");
		String grougseqs  = eomsInfo.first().getString("GROUP_SEQ");
		int groupseq = Integer.parseInt(grougseqs)+1;
//		IDataset oldAttrInfo = WorkformAttrBean.qryAttrRecodeNumBySubIbsysid(newSubIbsysId);
//		if(IDataUtil.isNotEmpty(oldAttrInfo)){
//			seq = oldAttrInfo.size();
//		}
		//拼业务数据
		for(int i = 0 ; i < attrInfos.size() ; i ++)
		{
			IData attrInfo = attrInfos.getData(i);
			attrInfo.put("SUB_IBSYSID", newSubIbsysId);
			attrInfo.put("NODE_ID", nodeId);
			attrInfo.put("SEQ", SeqMgr.getAttrSeq());
			attrInfo.put("GROUP_SEQ", groupseq);
//			seq++;
			
			String attrCode = attrInfo.getString("ATTR_CODE");
			String attrValue = attrInfo.getString("ATTR_VALUE");
			
			if ("CRMNO".equals(attrCode) && StringUtils.isEmpty(attrValue)) {
				attrInfo.put("ATTR_VALUE", ibsysid);
			}
			
			if ("CONFCRMTICKETNO".equals(attrCode) && StringUtils.isEmpty(attrValue)) {
				attrInfo.put("ATTR_VALUE", serialNo);
			}
			
			allAttrInfos.add(attrInfo);
		}
		//存tf_b_eop_attr新节点
		WorkformAttrBean.insertWorkformAttr(allAttrInfos);

		String attachRef = "";
		
		//查询员工信息
	    IDataset staffInfos = StaffInfoQry.queryValidStaffById(staffId);
		if(DataUtils.isEmpty(staffInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据工号["+staffId+"]查询员工失败！");
		}
		IData staffInfo = staffInfos.first();
		IDataset productSubInfos = WorkformProductSubBean.qryProductByIbsysid(ibsysid);
		if(DataUtils.isEmpty(productSubInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID["+ibsysid+"]查询TF_B_EOP_PRODUCT_SUB表数据失败！");
		}
		//拼tf_b_eop_eoms表数据
		IData eomsData = new DataMap();
		eomsData.put("SUB_IBSYSID",newSubIbsysId);         
		eomsData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		eomsData.put("IBSYSID", ibsysid);
		eomsData.put("BUSI_ID", busiformNodeId); 
		eomsData.put("GROUP_SEQ", groupseq);         
		eomsData.put("TRADE_DRIECT","0");//发送    
		eomsData.put("BPM_TEMPLET_ID", "EOMS_INTF");    
		eomsData.put("SUB_BUSI_TYPE",parentBpmTempletId);     
		eomsData.put("BPM_ID", busiformId);           
		eomsData.put("NODE_ID", nodeId);          
		eomsData.put("PRODUCT_ID", parentBusiCode);        
		eomsData.put("OPER_TYPE",operType);         
		eomsData.put("DEAL_STATE",0);        
		eomsData.put("EOMS_ORDER_CODE","");
		eomsData.put("EOMS_TACHE_CODE","");   
		eomsData.put("INSERT_TIME",SysDateMgr.getSysTime());     
		eomsData.put("UPDATE_TIME",SysDateMgr.getSysTime());      
		eomsData.put("SHEETTYPE",sheetType);         
		eomsData.put("SERVICETYPE",serviceType);        
		eomsData.put("SERIALNO", serialNo);          
		eomsData.put("ATTACHREF", attachRef);         
		eomsData.put("OPPERSON",staffInfo.getString("STAFF_NAME", ""));        
		eomsData.put("OPCORP","");          
		eomsData.put("OPDEPART",staffInfo.getString("DEPART_NAME", ""));       
		eomsData.put("OPCONTACT",staffInfo.getString("SERIAL_NUMBER", ""));     
		eomsData.put("OPTIME", SysDateMgr.getSysTime());         
		eomsData.put("OPDETAIL","");         
		eomsData.put("ERRLIST","");         
		eomsData.put("RESULT_TIME","");     
		eomsData.put("PLAN_DEAL_TIME","");
		eomsData.put("REAL_DEAL_TIME",""); 
		eomsData.put("CITY_CODE",staffInfo.getString("CITY_CODE", ""));
		eomsData.put("EPARCHY_CODE", eparchyCode);    
		eomsData.put("DEPART_ID",staffInfo.getString("DEPART_ID", ""));
		eomsData.put("DEPART_NAME",staffInfo.getString("DEPART_NAME", ""));       
		eomsData.put("STAFF_ID",staffInfo.getString("STAFF_ID", ""));  
		eomsData.put("STAFF_NAME",staffInfo.getString("STAFF_NAME", ""));
		eomsData.put("STAFF_PHONE",staffInfo.getString("SERIAL_NUMBER", ""));       
		eomsData.put("RSRV_STR1","");         
		eomsData.put("RSRV_STR2","");           
		eomsData.put("RSRV_STR3",eomsInterName);            
		eomsData.put("RSRV_STR4","");            
		eomsData.put("RSRV_STR5","");            
		eomsData.put("RSRV_STR6","");            
		eomsData.put("RSRV_STR7",parentBpmTempletId);            
		eomsData.put("REMARK","");
		eomsData.put("RECORD_NUM", recordNum);
		WorkformEomsBean.insertWorkformEoms(eomsData);
		/*for (int i = 0; i < productSubInfos.size(); i++) {
			IData productSubInfo = productSubInfos.getData(i);
			String record_num = productSubInfo.getString("RECORD_NUM");
			eomsData.put("RECORD_NUM", record_num);
			WorkformEomsBean.insertWorkformEoms(eomsData);
		}	*/
		//拼tf_b_eop_eoms_sub表
//		this.saveEomsSub(allAttrInfos);
		
		//更新sub_bi_sn
		EweNodeQry.updWorkformNodeByPk(busiformNodeId, newSubIbsysId, EcEsopConstants.STATE_VALID);
		return resultInfos;
	}
	public void updateReplyhangWorkSheet(IData data) throws Exception
	{
		String ibsysId = data.getString("IBSYSID");
		String busiformId  = data.getString("BUSIFORM_ID");
		IDataset releInfo = EweNodeQry.qryBySubBusiformId(busiformId);
		if(IDataUtil.isEmpty(releInfo)){
	        CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单"+busiformId+"无子流程!");
	    }
		String bpmTempletId = data.getString("BPM_TEMPLET_ID", "");
		if("eomsUnhangProess".equals(bpmTempletId)){//子子流程
			releInfo = EweNodeQry.qryBySubBusiformId(releInfo.first().getString("BUSIFORM_ID"));
			if(IDataUtil.isEmpty(releInfo)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单"+releInfo.first().getString("BUSIFORM_ID")+"无子流程!");
			}
		}
		String recordNum =  releInfo.first().getString("RELE_VALUE");
		IDataset productInfo = WorkformEomsStateBean.qryEomsStateByIbsysidTradeId(ibsysId,recordNum);
		if(IDataUtil.isEmpty(productInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID["+ibsysId+"]查询TF_B_EOP_EOMS_STATE表数据失败！");
		}
		String productNo = productInfo.first().getString("PRODUCT_NO");
		IData agree = WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysId,"agreeResult",recordNum);
		String agreeResult = agree.getString("ATTR_VALUE");
		String operType = "replyhangWorkSheet";
		if("同意".equals(agreeResult)){
			operType = "replyhangWorkSheet";
		}else{
			operType = "confirmWorkSheet";
		}
		String operTypes = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EOMS_BUSI_STATE", operType});
		WorkformEomsStateBean.updEomsStateByProductNo(ibsysId,recordNum,productNo,operTypes);		
		
	}
	public void updateEomshangProess(IData data) throws Exception
	{
		updateEomshang(data,true);
	}
	public void updateEomsUnhangProess(IData data) throws Exception
	{
		updateEomshang(data,false);
	}
	public void updateEomshang(IData data,boolean falg) throws Exception
	{
		String ibsysId = data.getString("IBSYSID");
		String busiformId  = data.getString("BUSIFORM_ID");
		IDataset releInfo = EweNodeQry.qryBySubBusiformId(busiformId);
		if(IDataUtil.isEmpty(releInfo)){
	        CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单"+busiformId+"无子流程!");
	    }
		String bpmTempletId = data.getString("BPM_TEMPLET_ID", "");
		if("eomsUnhangProess".equals(bpmTempletId)){//子子流程
			releInfo = EweNodeQry.qryBySubBusiformId(releInfo.first().getString("BUSIFORM_ID"));
			if(IDataUtil.isEmpty(releInfo)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工单"+releInfo.first().getString("BUSIFORM_ID")+"无子流程!");
			}
		}
		String recordNum =  releInfo.first().getString("RELE_VALUE");
		IDataset productInfo = WorkformEomsStateBean.qryEomsStateByIbsysidTradeId(ibsysId,recordNum);
		if(IDataUtil.isEmpty(productInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID["+ibsysId+"]查询TF_B_EOP_EOMS_STATE表数据失败！");
		}
		String productNo = productInfo.first().getString("PRODUCT_NO");
//		IData agree = WorkformAttrBean.qryAttrByIbsysidRecordCode(ibsysId,"agreeResult",recordNum);
//		String agreeResult = agree.getString("ATTR_VALUE");
		String operType = "eomshangProess";
		if(!falg){
			operType = "eomsUnhangProess";
		}
		String operTypes = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EOMS_BUSI_STATE", operType});
		WorkformEomsStateBean.updEomsStateByProductNo(ibsysId,recordNum,productNo,operTypes);		
		
	}
	
}

