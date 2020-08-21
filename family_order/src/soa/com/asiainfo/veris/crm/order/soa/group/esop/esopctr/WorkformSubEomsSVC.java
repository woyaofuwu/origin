package com.asiainfo.veris.crm.order.soa.group.esop.esopctr;

import java.util.Iterator;

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
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDataLineAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.common.query.DatalineOrderDAO;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttachBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;


public class WorkformSubEomsSVC extends GroupOrderService
{
	private static final long serialVersionUID = 1L;
	
//	private static String serialNo = "";
//	private static String productNO = "";
//	private static String tradeID = "";

	public IDataset saveNewWorkSheet(IData data) throws Exception
	{
		IDataset resultInfos = new DatasetList();
		String subIbsysid = data.getString("SUB_IBSYSID", "");
//		String attachSubIbsysid = data.getString("ATTACH_SUB_IBSYSID","");
		String releCode = data.getString("RELE_CODE", "");
		String recordNum = data.getString("RELE_VALUE", "");
		String operType = data.getString("OPER_TYPE", "");
		String nodeId = data.getString("NODE_ID", "");
		String busiformId = data.getString("BUSIFORM_ID", "");
		String busiformNodeId = data.getString("BUSIFORM_NODE_ID", "");
		String staffId = data.getString("DEAL_STAFF_ID", "");
		String eparchyCode = data.getString("DEAL_EPARCHY_CODE", "");
		String parentBpmTempletId = data.getString("PARENT_BPM_TEMPLET_ID", "");
		String parentNodeId = data.getString("PARENT_NODE_ID", "");
		String parentBusiCode = data.getString("PARENT_BUSI_CODE", "");
		String ibsysID = data.getString("IBSYSID", "");
		String sysdate = SysDateMgr.getSysTime();
		
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
		if(DataUtils.isEmpty(interInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CONFIGNAME:[SHEETTYPE]，PARAMNAME:["+eomsInterName+"]，RSRV_STR1:["+operType+"]获取发送eoms接口类型配置数据失败。请确认TD_B_EWE_CONFIG表数据是否正确！");
		}
		String sheetType = sheetInfo.getString("PARAMVALUE", "");
		String serviceType = sheetInfo.getString("VALUEDESC", "");
		
		if(!"RECORD_NUM".equalsIgnoreCase(releCode))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "此服务[RECORD_NUM]，必传，请确认子流程关联RELE_CODE是否正确！");
		}
		
		
		IDataset allAttrInfos = new DatasetList();
		
		String newSubIbsysId = SeqMgr.getSubIbsysId();
	    //seq = WorkformAttrBean.getSeq(ibsysID);
//		int seq = 0;
		
		IData productSubInfo = WorkformProductSubBean.qryProductByPk(ibsysID, recordNum);
		
		if(DataUtils.isEmpty(productSubInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:["+ibsysID+"]，RECORD_NUM:["+recordNum+"]获取数据失败。请确认tf_b_eop_product_sub表数据是否正确！");
		}
		IData dataLine =  new DataMap();
		String userID = productSubInfo.getString("USER_ID");
		if("EVIOPDIRECTLINECANCELPBOSS".equals(parentBpmTempletId)){
			IData voipParam  = new DataMap();
			voipParam.put("IBSYSID", ibsysID);
			voipParam.put("RECORD_NUM", recordNum);
			voipParam.put("NODE_ID", "apply");
			voipParam.put("ATTR_CODE", "PRODUCTNO");
			IDataset userInfo =  WorkformAttrBean.qryAttrByIbsysidRecordNumNodeId(voipParam);
			if(DataUtils.isEmpty(userInfo))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID:["+ibsysID+"]，RECORD_NUM:["+recordNum+"]获取PRODUCTNO数据失败。请确认tf_b_eop_attr表数据是否正确！");
			}
			String productNo = userInfo.first().getString("ATTR_VALUE");
			IDataset dataLineInfo = TradeDataLineAttrInfoQry.qryUserDatalineByProductNO(userID,productNo);
			if(DataUtils.isEmpty(dataLineInfo))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID:["+userID+"]，PRODUCT_NO:["+productNo+"]获取数据失败。请确认TF_F_USER_DATALINE表数据是否正确！");
			}
			dataLine = dataLineInfo.first();
		}else{
			dataLine = DatalineOrderDAO.queryDataline(userID);
		}
		IDataset comAttrInfos= WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysid, "0");
		if(DataUtils.isEmpty(comAttrInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SUB_IBSYSID:["+subIbsysid+"]，RECORD_NUM:["+recordNum+"]获取数据失败。请确认tf_b_eop_attr表数据是否正确！");
		}
		
		
		//拼公共数据
		for(int i = 0 ; i < comAttrInfos.size() ; i ++)
		{
			
			IData comAttrInfo = comAttrInfos.getData(i);
			
			if ("CRMNO".equals(comAttrInfo.getString("ATTR_CODE")) && StringUtils.isEmpty(comAttrInfo.getString("ATTR_VALUE"))) {
				comAttrInfo.put("ATTR_VALUE", ibsysID);
			}
			
			comAttrInfo.put("SUB_IBSYSID", newSubIbsysId);
			comAttrInfo.put("NODE_ID", nodeId);
			comAttrInfo.put("SEQ", SeqMgr.getAttrSeq());
			allAttrInfos.add(comAttrInfo);
		}
		
		String productNO = "";
		String tradeID = "";
		if (IDataUtil.isNotEmpty(dataLine)) {
			productNO = dataLine.getString("PRODUCT_NO","");
			tradeID = dataLine.getString("PRODUCT_NO","");
			
			Iterator<String> itr = dataLine.keySet().iterator();
			while(itr.hasNext())
			{
				String key = itr.next();
				String value = dataLine.getString(key);
				
				IDataset esopParam = EweConfigQry.qryByConfigName("LINEPARAM_CRM_ESOP", "0");
				for (int j = 0; j < esopParam.size(); j++) {
					String paramValue = esopParam.getData(j).getString("PARAMVALUE");
					if (key.equals(paramValue)) {
						key = esopParam.getData(j).getString("PARAMNAME");
						break;
					}
				}
				
				
				IData attrInfo = new DataMap();
				attrInfo.put("SUB_IBSYSID",newSubIbsysId);
				attrInfo.put("IBSYSID",ibsysID);
				attrInfo.put("SEQ",SeqMgr.getAttrSeq());
				attrInfo.put("GROUP_SEQ","0");
				attrInfo.put("NODE_ID",nodeId);
				attrInfo.put("ACCEPT_MONTH",SysDateMgr.getCurMonth());
				attrInfo.put("ATTR_CODE",key);
				attrInfo.put("ATTR_NAME","");
				attrInfo.put("ATTR_VALUE",value);
				attrInfo.put("ATTR_TYPE","P");
				attrInfo.put("PARENT_ATTR_CODE","");
				attrInfo.put("RECORD_NUM",recordNum);
				attrInfo.put("UPDATE_TIME",sysdate);
				attrInfo.put("RSRV_STR1","");
				attrInfo.put("RSRV_STR2","");
				
				allAttrInfos.add(attrInfo);
			}
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
		
		int num = Integer.parseInt(recordNum);
		String serialNo ="";
		
		if (num < 1000)
        {
			serialNo = "ESOP"+ibsysID+num;
        }
        if (num < 100)
        {
        	serialNo = "ESOP"+ibsysID + "0" + num;
        }
        if (num < 10)
        {
        	serialNo = "ESOP"+ibsysID + "00" + num;
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
		eomsData.put("IBSYSID", ibsysID);
		eomsData.put("BUSI_ID", busiformNodeId); 
		eomsData.put("GROUP_SEQ", "0");         
		eomsData.put("TRADE_DRIECT","0");//发送    
		eomsData.put("BPM_TEMPLET_ID", "EOMS_INTF");    
		eomsData.put("SUB_BUSI_TYPE",parentBpmTempletId);     
		eomsData.put("BPM_ID", busiformId);           
		eomsData.put("NODE_ID", nodeId);          
		eomsData.put("PRODUCT_ID", parentBusiCode);        
		eomsData.put("PRODUCT_NAME", productSubInfo.getString("PRODUCT_NAME", ""));       
		eomsData.put("OPER_TYPE",operType);         
		eomsData.put("DEAL_STATE",0);        
		eomsData.put("EOMS_ORDER_CODE","");
		eomsData.put("EOMS_TACHE_CODE","");   
		eomsData.put("INSERT_TIME",sysdate);     
		eomsData.put("UPDATE_TIME",sysdate);      
		eomsData.put("SHEETTYPE",sheetType);         
		eomsData.put("SERVICETYPE",serviceType);        
		eomsData.put("SERIALNO", serialNo);          
		eomsData.put("ATTACHREF", attachRef);         
		eomsData.put("OPPERSON",staffInfo.getString("STAFF_NAME", ""));        
		eomsData.put("OPCORP","");          
		eomsData.put("OPDEPART",staffInfo.getString("DEPART_NAME", ""));       
		eomsData.put("OPCONTACT",staffInfo.getString("SERIAL_NUMBER", ""));     
		eomsData.put("OPTIME", sysdate);         
		eomsData.put("OPDETAIL","");         
		eomsData.put("ERRLIST","");//         
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
		
		//插入state表
		this.dealState(serialNo,ibsysID, recordNum, productNO, tradeID);
		
		//更新sub_bi_sn
		EweNodeQry.updWorkformNodeByPk(busiformNodeId, newSubIbsysId, EcEsopConstants.STATE_VALID);
		return resultInfos;
	}

	private void dealState(String serialNo,String ibsysid, String recordNum,String productNO, String tradeID) throws Exception
	{
		
		IData eomsState = new DataMap();
		eomsState.put("INST_ID", SeqMgr.getIntfId());
		eomsState.put("IBSYSID", ibsysid);
		eomsState.put("RECORD_NUM", recordNum);
		eomsState.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		eomsState.put("BUSI_STATE", "-1");
		eomsState.put("SERIALNO", serialNo);
		eomsState.put("PRODUCT_NO", productNO);
		eomsState.put("TRADE_ID", tradeID);
		eomsState.put("DEAL_TYPE", TRADE_MODIFY_TAG.Add.getValue());
		eomsState.put("CREATE_DATE", SysDateMgr.getSysTime());
		eomsState.put("REMARK", "");
		eomsState.put("RSRV_STR1", "");
		eomsState.put("RSRV_STR2", "");
		eomsState.put("RSRV_STR3", "");
		
		WorkformEomsStateBean.insertWorkformEomsState(eomsState);
	}
}
