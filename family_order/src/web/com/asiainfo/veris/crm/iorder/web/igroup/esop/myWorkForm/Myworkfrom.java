package com.asiainfo.veris.crm.iorder.web.igroup.esop.myWorkForm;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class Myworkfrom extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setInfosCount(long infosCount);

    public abstract void setStaffInfo(IData staffInfo);

    public abstract void setProducts(IDataset products);

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void initPage(IRequestCycle cycle) throws Exception {
        IData staffInfo = new DataMap();
        String staffId = getVisit().getStaffId();
        String staffName = getVisit().getStaffName();
        String cityCode = getVisit().getCityCode();
        // String eparchyCode = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "CITY_CODE", staffId);
        staffInfo.put("STAFF_ID", staffId);
        staffInfo.put("STAFF_NAME", staffName);
        staffInfo.put("EPARCHY_CODE", cityCode);
        if ("HNSJ".equals(cityCode) || "HNHN".equals(cityCode) || "HNYD".equals(cityCode)) {
            staffInfo.put("EPARCHY_CODE_TYPE", "false");
        } else {
            staffInfo.put("EPARCHY_CODE_TYPE", "true");
        }
        IData eomsInfos = new DataMap();
        try {
            eomsInfos = CSViewCall.callone(this, "SS.WorkFormSVC.queryProductAndBusiType", staffInfo);
        }
        catch (Exception e) {
            staffInfo.put("EXCEPTION", e.getMessage());
        }

        staffInfo.putAll(eomsInfos);
        this.setStaffInfo(staffInfo);

    }

    public void queryWorkformDetail(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.WorkFormSVC.queryWorkformDetail", data, this.getPagination("olcomnav"));
        IDataset myWorkformInfos = output.getData();
        setInfosCount(output.getDataCount());
        this.setInfos(myWorkformInfos);
        this.setCondition(data);
    }

    public void queryDatelineAttrQueryData(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        CSViewCall.call(this, "SS.WorkFormSVC.queryDatelineAttr", data);
    }

    public void queryDatelineCodeType(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        String groupId = data.getString("GROUP_ID");
        String IS_FINISH = data.getString("IS_FINISH");
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
}
