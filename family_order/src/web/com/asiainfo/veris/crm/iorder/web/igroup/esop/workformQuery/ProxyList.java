package com.asiainfo.veris.crm.iorder.web.igroup.esop.workformQuery;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class ProxyList extends GroupBasePage {
	public abstract void setInfos(IDataset infos);   
	public abstract void setRoleList(IDataset roleList);
	public abstract void setDepartlist(IDataset departlist);
	public abstract void setStafflist(IDataset stafflist);
	public abstract void setTemplteList(IDataset templteList);
	public abstract void setOldTemplteList(IDataset templteList);
	public abstract void setCount(long count) throws Exception; 
	public void queryProxy(IRequestCycle cycle) throws Exception {
		String staffId =  getVisit().getStaffId();
		IData staffParam  = new DataMap();
		staffParam.put("STAFF_ID_B", staffId);
		IDataOutput group = CSViewCall.callPage(this, "SS.WorkformProxySVC.qryProxyByStaffIdB", staffParam,this.getPagination("myNavbar"));
		setInfos(group.getData());
		setCount(group.getDataCount());
		
		IData templetParam = new DataMap();
		templetParam.put("TEMPLET_TYPE", "0");
		templetParam.put("VALID_TAG", "0");
		IDataset departInfo  =  CSViewCall.call(this, "SS.QryFlowNodeDescSVC.qryFlowDescByTemplettype", templetParam);
		setTemplteList(departInfo);
		
	}
	
	
	public void queryProxyhistory(IRequestCycle cycle) throws Exception {
		String staffId =  getVisit().getStaffId();
		IData staffParam  = new DataMap();
		staffParam.put("STAFF_ID_B", staffId);
		IDataOutput group = CSViewCall.callPage(this, "SS.WorkformProxySVC.getProxyshistoryByStaffb", staffParam,this.getPagination("myNavbar"));
		setInfos(group.getData());
		setCount(group.getDataCount());
		
		
	}
	
	public void deleteProxy(IRequestCycle cycle) throws Exception {
		IData data  = getData();
		String proxyIds =  data.getString("PROXY_IDS");
		String[] proxyId = proxyIds.split("\\|");
		for(int i= 0;i<proxyId.length;i++){
			String proxy = proxyId[i];
			IData param  = new DataMap();
			param.put("PROXY_ID", proxy);
			CSViewCall.call(this, "SS.WorkformProxySVC.deleteProxy", param);
		}
		String staffId =  getVisit().getStaffId();
		IData staffParam  = new DataMap();
		staffParam.put("STAFF_ID_B", staffId);
		IDataOutput group = CSViewCall.callPage(this, "SS.WorkformProxySVC.qryProxyByStaffIdB", staffParam,this.getPagination("myNavbar"));
		setInfos(group.getData());
		setCount(group.getDataCount());
		
	}
	
	public void changeRole(IRequestCycle cycle) throws Exception {
		IDataset departlist = new DatasetList();
		IDataset stafflist = new DatasetList();
		IData data  = getData();
		String roleId =  data.getString("ROLE_ID");
		IData departParam =  new DataMap();
		departParam.put("RIGHT_CODE", roleId);
		IDataset departInfo  =  CSViewCall.call(this, "SS.WorkformProxySVC.getStaffInfo", departParam);
		List<String> departs = new ArrayList<String>();
		List<String> staffs = new ArrayList<String>();
		for(int i=0; i<departInfo.size() ;i++){
			IData tmp = (IData) departInfo.get(i);
			if(departs.contains(tmp.getString("DEPART_ID")))
				continue;
			IData depart = new DataMap();
			depart.put("DEPART_ID", tmp.getString("DEPART_ID"));
			depart.put("DEPART_NAME", tmp.getString("DEPART_ID")+"|"+tmp.getString("DEPART_NAME"));
			departlist.add(depart);
			departs.add(tmp.getString("DEPART_ID"));
		}
		
		for(int i=0; i<departInfo.size() ;i++){
			IData tmp = (IData) departInfo.get(i);
			if(staffs.contains(tmp.getString("STAFF_ID")))
				continue;
			IData depart = new DataMap();
			depart.put("STAFF_ID", tmp.getString("STAFF_ID"));
			depart.put("STAFF_NAME", tmp.getString("STAFF_ID")+"|"+tmp.getString("STAFF_NAME"));
			stafflist.add(depart);
			staffs.add(tmp.getString("STAFF_ID"));
		}
		setDepartlist(departlist);
		setStafflist(stafflist);
	}
	
	
	public void reloadStaff(IRequestCycle cycle) throws Exception {
		IData data  = getData();
		String departId =  data.getString("DEPART_ID");
		IDataset staffList = new DatasetList(data.getString("staffList"));
		IDataset filterStaffs = new DatasetList();
		for(int i=0; i<staffList.size(); i++) {
			IData tmp = (IData) staffList.get(i);
			if(departId.equals(tmp.getString("DEPART_ID")) &&
					!getVisit().getStaffId().equals(tmp.getString("STAFF_ID"))){
				filterStaffs.add(tmp);
			}
		}
		List<String> staffs = new ArrayList<String>();
		for(int i=0; i<filterStaffs.size() ;i++){
			IData tmp = (IData) filterStaffs.get(i);
			if(staffs.contains(tmp.getString("STAFF_ID")))
				continue;
			IData depart = new DataMap();
			depart.put("STAFF_ID", tmp.getString("STAFF_ID"));
			depart.put("STAFF_NAME", tmp.getString("STAFF_ID")+"|"+tmp.getString("STAFF_NAME"));
			staffList.add(depart);
			staffs.add(tmp.getString("STAFF_ID"));
		}
		setStafflist(staffList);
	}
	
	public void submitProxy(IRequestCycle cycle) throws Exception {
		IData data  = getData();
		data.put("START_DATE", data.getString("START_DATE"));
		data.put("END_DATE", data.getString("END_DATE"));
		data.put("STAFF_ID_B", getVisit().getStaffId());
		data.put("STAFF_NAME_B", getVisit().getStaffName());
		data.put("STAFF_PHONE_B", getVisit().getSerialNumber());
		if("0".equals(data.getString("PROXY_TYPE"))){
			data.put("INFO_LIST", "");
		}else{
			data.put("INFO_LIST", data.getString("INFO_LIST").replace("|", "&"));
		}
		//增加代理说明RSRV_STR3
		data.put("RSRV_STR3", data.getString("STAFF_NAME_A")+"(代理"+data.getString("STAFF_NAME_B")+":"+data.getString("STAFF_ID_B")+")");
		data.put("STAFF_ID_A", data.getString("STAFF_ID_A").split(",")[0]);
		if(data.getString("STAFF_ID_B").equals(data.getString("STAFF_ID_A"))){
			setAjax("ERROR", "无法指派自己为代理人！");
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "无法指派自己为代理人！");
		}
		String roleId =  data.getString("ROLE_ID");
		roleId = StaticUtil.getStaticValue(getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "BOSS_ROLE-BPM_ROLE",roleId});
		data.put("ROLE_ID", roleId);
		IData staffBinfo  =  CSViewCall.callone(this, "SS.WorkformProxySVC.isExistsStaffB", data);
		if(!"0".equals(staffBinfo.getString("NUM"))){
			setAjax("ERROR", "代理角色["+data.getString("ROLE_ID")+"]已存在有效的代理冲突!");
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "代理角色["+data.getString("ROLE_ID")+"]已存在有效的代理冲突!");
		}
		
		//
        IData param = new DataMap();
        param.put("STAFF_ID", data.getString("STAFF_ID_A"));
        IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", param);
        data.put("STAFF_NAME_A", staffInfo.getString("STAFF_NAME",""));
        data.put("STAFF_PHONE_A", staffInfo.getString("STAFF_PHONE",""));
		CSViewCall.call(this, "SS.WorkformProxySVC.insertProxyInfo", data);
		
		String staffId =  getVisit().getStaffId();
		IData staffParam  = new DataMap();
		staffParam.put("STAFF_ID_B", staffId);
		IDataOutput group = CSViewCall.callPage(this, "SS.WorkformProxySVC.qryProxyByStaffIdB", staffParam,this.getPagination("myNavbar"));
		setInfos(group.getData());
		setCount(group.getDataCount());

		
	}
	
	public void queryProxyList(IRequestCycle cycle) throws Exception {
		IData data  = getData();
		String proxyId = data.getString("PROXY_ID");
		IData map =  new DataMap();
		map.put("PROXY_ID", proxyId);
		IData proxyINfo =  CSViewCall.callone(this, "SS.WorkformProxySVC.queryProxy", map);
		String infoList = proxyINfo.getString("INFO_LIST");
		String[] infoLists  = infoList.split("&");
		IDataset departInfo = new DatasetList();
		 for (int i = 0; i < infoLists.length; i++)
         {
			 String tempte = infoLists[i];
			 if(StringUtils.isNotEmpty(tempte)){
				 IData  param = new DataMap();
				 param.put("BPM_TEMPLET_ID", tempte);
				 param.put("VALID_TAG", "0");
				 IData templte =  CSViewCall.callone(this, "SS.QryFlowNodeDescSVC.qryFlowDescByTempletId", param);
				 departInfo.add(templte);
			 }

         }
		 setOldTemplteList(departInfo);
	}

	

}
