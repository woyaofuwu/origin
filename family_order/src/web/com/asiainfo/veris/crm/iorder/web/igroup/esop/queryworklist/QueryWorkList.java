package com.asiainfo.veris.crm.iorder.web.igroup.esop.queryworklist;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class QueryWorkList extends CSBasePage
{
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData cond = this.getData();
        cond.put("INFO_STATUS", cond.getString("cond_INFO_STATUS", "1"));
        
        String pdataId = "";
        String queryType = cond.getString("QUERY_TYPE", "");
        if("UN_DONE".equals(queryType) || "DONE".equals(queryType))
        {
            pdataId = "3";
            cond.put("INFO_CHILD_TYPE", "35");
        }
        else if("UN_READ".equals(queryType))
        {
            pdataId = "4";
            cond.put("INFO_CHILD_TYPE", "41");
        }
        
        if(!"".equals(pdataId))
        {
            cond.put("INFOCHILDTYPE", StaticUtil.getStaticListByParent("INFO_CHILD_TYPE", pdataId));
        }
        else
        {
            cond.put("INFOCHILDTYPE", StaticUtil.getStaticList("INFO_CHILD_TYPE"));
        }
        setCondition(cond);
    }

    public void queryWorkList(IRequestCycle cycle) throws Exception
    {
    	IData pageData = getData();
    	String staffID = pageData.getString("CONTACTOR_ID","");
        IData param = this.getData("cond", true);
        if ("41".equals(param.getString("INFO_CHILD_TYPE"))) {
        	param.put("INFO_TYPE", "4");
		}else {
			param.put("INFO_TYPE", "3");
		}
        param.put("RECE_OBJ_TYPE", "2");
        if(StringUtils.isNotEmpty(staffID)){
            param.put("STAFF_ID", staffID);
        }
        else if(!"".equals(param.getString("oldStaffId",""))){
            param.put("STAFF_ID", param.getString("oldStaffId"));
        }
        else{
            param.put("STAFF_ID", getVisit().getStaffId());
        }
        
        if(EcEsopConstants.WORKINFO_STATUS_DONE.equals(param.getString("INFO_STATUS")))
        {//已办
            IDataOutput result = CSViewCall.callPage(this, "SS.WorkTaskMgrSVC.queryOpTaskList", param, this.getPagination());
            setInfos(result.getData());
            setCount(result.getDataCount());
        }
        else        
        {
            IDataOutput result = CSViewCall.callPage(this, "SS.WorkTaskMgrSVC.queryOpTaskList", param, this.getPagination());
            setInfos(result.getData());
            setCount(result.getDataCount());
        }
    }
    
    public void checkInfo(IRequestCycle cycle) throws Exception 
    {
    	String instID = getData().getString("INST_ID");
    	IData data = new DataMap();
    	data.put("INST_ID", instID);
    	data.put("INFO_STATUS", "9");
    	
    	IDataset infos = CSViewCall.call(this, "SS.WorkTaskMgrSVC.qryInfosByInstId", data);
    	
    	if (IDataUtil.isNotEmpty(infos)) {
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "待办已处理，请勿重复提交！");
		}
    }
    
    public void initPageTransfer(IRequestCycle cycle) throws Exception
    {
        IData cond = this.getData();
        cond.put("INFO_STATUS", cond.getString("cond_INFO_STATUS", "1"));
        String oldStaffId = getVisit().getStaffId();
        String cityCode = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "CITY_CODE", oldStaffId);
        cond.put("oldStaffId",oldStaffId);
        if("HNSJ".equals(cityCode)){
            cond.put("oldStaff_flag","0");
        }else{
            cond.put("oldStaff_flag","1");
        }
        setCondition(cond);
    }
    
    public void transferWorkListSubmit(IRequestCycle cycle) throws Exception
    {
	    IData pattr = getData("pattr");
		System.out.println("submit"+pattr);
		String initStaff = getVisit().getStaffId();
		String oldStaffId = pattr.getString("oldStaffId");
		String newStaffId = pattr.getString("newStaffId");
		String cityCode = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "CITY_CODE", oldStaffId);
        String cityCode2 = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "CITY_CODE", newStaffId);
        String cityCode0 = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "CITY_CODE", initStaff);
        if(!"HNSJ".equals(cityCode0) && !cityCode.equals(cityCode2)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "非省局工号仅可转派当前地市员工工号！");
        }
		IDataset newStaffIdList=StaticUtil.getList(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", new String[]{ "STAFF_ID" }, new String[]{ pattr.getString("newStaffId")});
		pattr.put("instList", new DatasetList(pattr.getString("instList","[]")));
		if(IDataUtil.isEmpty(newStaffIdList)){
	        CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工号"+pattr.getString("newStaffId")+"未查出工号记录！");
		}else{
	        CSViewCall.call(this, "SS.StaffTransferSVC.updStaffTransferForinfoList", pattr);
		}

	}
    public void qryStaffinfo(IRequestCycle cycle) throws Exception {
        IData pattr = getData("cond");
        IDataset staffinfo = CSViewCall.call(this, "SS.StaffTransferSVC.qryStaffNameForName", pattr);
        setStaffInfos(staffinfo);

    }
    public void qryStaffIdinfo(IRequestCycle cycle) throws Exception {
        IData pattr = getData("cond");
        IDataset staffinfo = CSViewCall.call(this, "SS.StaffTransferSVC.qryStaffNameForId", pattr);
        setStaffInfos(staffinfo);
        
    }
    
    
    public void queryDatelineCodeType(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        String groupId = data.getString("GROUP_ID");
        String IS_FINISH = data.getString("IS_FINISH","");
        String BPM_TEMPLET_ID = data.getString("BPM_TEMPLET_ID");
        // 查询客户信息
        IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        if (IDataUtil.isNotEmpty(groupInfo)) {
            String custMgrId = groupInfo.getString("CUST_MANAGER_ID");
            IData managerInfo = null;
            if (StringUtils.isNotEmpty(custMgrId)) {
                // 查询客户经理信息
                managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
            }
            if (IDataUtil.isNotEmpty(managerInfo)) {
                groupInfo.put("CUST_MANAGER_NAME", managerInfo.getString("CUST_MANAGER_NAME"));
                groupInfo.put("CUST_SERIAL_NUMBER", managerInfo.getString("SERIAL_NUMBER"));
                groupInfo.put("CUST_MANAGER_ID", custMgrId);
                groupInfo.put("CUST_RSRV_NUM1", managerInfo.getString("RSRV_NUM1"));

            }
            String busiLicenceType = groupInfo.getString("BUSI_LICENCE_TYPE");// 客户服务等级
            String classId = groupInfo.getString("CLASS_ID");// 客户等级
            groupInfo.put("CUST_CLASS", StaticUtil.getStaticValue("BIZ_CUST_CLASS", busiLicenceType));
            groupInfo.put("GROUP_CLASS", StaticUtil.getStaticValue("CUSTGROUP_CLASSID", classId));

        }

        IData datavalue = new DataMap();
        datavalue.put("IBSYSID", data.getString("IBSYSID"));
        datavalue.put("BPM_TEMPLET_ID", data.getString("BPM_TEMPLET_ID"));
        datavalue.put("BUSIFORM_ID", data.getString("BUSIFORM_ID"));

        IDataset workSheet = new DatasetList();

        IDataset PresentNodeInfo = new DatasetList();

        if (IS_FINISH.equals("true")) {
            // 查询已经完成工单的轨迹
            workSheet = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.queryFinishWorksheet", datavalue);
        } else {
            // 查询工单轨迹
            workSheet = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.queryWorksheet", datavalue);
            // 如果是未完成 则查询当前正在处理的节点
            PresentNodeInfo = CSViewCall.call(this, "SS.WorkformPresentNodeSVC.qryPresentNodeByIbsysid", datavalue);
        }

        for (int i = 0; i < workSheet.size(); i++) {
            // 放入参数node_id
            datavalue.put("NODE_ID", workSheet.getData(i).getString("NODE_ID"));
            datavalue.put("VALID_TAG", "0");
            // 查询节点名称
            IDataset DESC = CSViewCall.call(this, "SS.QryFlowNodeDescSVC.qryNodeDescByTempletId", datavalue);

            if (IDataUtil.isNotEmpty(DESC)) {
                workSheet.getData(i).put("NODE_DESC", DESC.getData(0).getString("NODE_DESC"));
            }

            // 将模板id放入集合
            workSheet.getData(i).put("BPM_TEMPLET_ID", BPM_TEMPLET_ID);
            String staffName = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", workSheet.getData(i).getString("UPDATE_STAFF_ID"));
            workSheet.getData(i).put("PERSON", staffName);
        }

        if (IDataUtil.isNotEmpty(PresentNodeInfo)) {

            // 改变li的 class属性 表示工单当前的状态
            PresentNodeInfo.getData(0).put("CLASS", "link on");

            // 查询当前节点的信息
            datavalue.put("BPM_TEMPLET_ID", PresentNodeInfo.getData(0).getString("BPM_TEMPLET_ID"));
            datavalue.put("NODE_ID", PresentNodeInfo.getData(0).getString("NODE_ID"));
            // 查询节点名称
            IDataset DESC = CSViewCall.call(this, "SS.QryFlowNodeDescSVC.qryNodeDescByTempletId", datavalue);

            if (IDataUtil.isNotEmpty(DESC)) {
                PresentNodeInfo.getData(0).put("NODE_DESC", DESC.getData(0).getString("NODE_DESC"));
            }
            workSheet.add(PresentNodeInfo.getData(0));
        }
        // 查询附件
        IDataset fileset = new DatasetList();
        if (IS_FINISH.equals("true")) {
            // 查询已经完成工单的附件信息
            fileset = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishGroupAttach", data);
        } else {
            fileset = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryGroupAttach", data);
        }

    }
    
    public abstract void setCondition(IData condition) throws Exception;
    public abstract void setInfos(IDataset infos) throws Exception;
    public abstract void setStaffInfos(IDataset staffInfos) throws Exception;
    public abstract void setCount(long count) throws Exception;
}
