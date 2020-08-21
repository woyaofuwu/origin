package com.asiainfo.veris.crm.iorder.web.igroup.esop.acceptanceperiodandchange400;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class AcceptanceperiodAndChange400 extends EopBasePage {

    public abstract void setAuditInfo(IData auditInfo) throws Exception;

    public abstract void setInfo(IData info) throws Exception;

    public abstract void setInAttr(IData inAttr) throws Exception;

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

        IData info = new DataMap();
        info.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
        info.put("NODE_ID", nodeId);

        // 节点特殊处理
        dealSpeNode(info, nodeId);

        String bpmTempletId = subscribeData.getString("BPM_TEMPLET_ID");
        info.put("BPM_TEMPLET_ID", bpmTempletId);

        String recordNum = param.getString("RECORD_NUM", "0");
        IData eopProductData = WorkfromViewCall.qryEopProductByIbsysId(this, ibsysid, recordNum);
        if (IDataUtil.isEmpty(eopProductData)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID=" + ibsysid + "未查询到产品表TF_B_EOP_PRODUCT数据！");
        }

        String busifromNodeId = param.getString("BUSIFORM_NODE_ID");
        // 获取订单Attr表信息
        IData attrData = getEopAttrDatas(busifromNodeId, ibsysid);
        info.putAll(attrData);

        IData inAttr = new DataMap();
        inAttr.put("FLOW_ID", bpmTempletId); // POINT_ONE
        inAttr.put("NODE_ID", nodeId); // POINT_TWO
        inAttr.put("PRODUCT_ID", eopProductData.getString("PRODUCT_ID"));
        // 如果是申请节点，则取前节点的页面配置
        if ("apply".equals(nodeId)) {
            IData input = new DataMap();
            input.put("BUSIFORM_NODE_ID", busifromNodeId);
            IDataset nodeDatas = CSViewCall.call(this, "SS.EweNodeQrySVC.qryByBusiformNodeId", input);
            if (IDataUtil.isEmpty(nodeDatas)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据BUSIFORM_NODE_ID=" + busifromNodeId + "未查询到节点表数据！");
            }
            IData nodeData = nodeDatas.first();
            String preNodeId = nodeData.getString("PRE_NODE_ID");
            inAttr.put("NODE_ID", preNodeId);
        }
        setInAttr(inAttr);

        setInfo(info);
        getStaffInfo();
    }

    private void getStaffInfo() throws Exception {
        String staff = getVisit().getStaffId();
        IData param = new DataMap();
        param.put("STAFF_ID", staff);
        IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", param);
        setAuditInfo(staffInfo);
    }

    private void dealSpeNode(IData info, String nodeId) throws Exception {
        if ("infoPrvApprove".equals(nodeId) || "infoApprove".equals(nodeId) || "infoReview".equals(nodeId) || "applyAuditReview".equals(nodeId)) {
            info.put("ELEMENT_DISPLAY", "1");// 审核信息是否显示
            info.put("ELEMENT_DISABLED", "0");// 订单信息是否可编辑
        } else if ("reportInfo".equals(nodeId)) {
            info.put("ELEMENT_DISPLAY", "0");// 审核信息是否显示
            info.put("ELEMENT_DISABLED", "0");// 订单信息是否可编辑
        } else if ("apply".equals(nodeId) || "checkInfo".equals(nodeId)) {
            info.put("ELEMENT_DISPLAY", "0");// 审核信息是否显示
            info.put("ELEMENT_DISABLED", "1");// 订单信息是否可编辑
        } else if ("domainApply".equals(nodeId)) {
            info.put("ELEMENT_DOMAIN", "1");// 移动400域名申请显示
            info.put("ELEMENT_DISABLED", "0");// 订单信息是否可编辑
            info.put("ELEMENT_MOBILE400", "1");// 订单信息是否可编辑
            info.put("AUDIT_STAFF", getVisit().getStaffName());// 处理人
        } else if ("domainConfirm".equals(nodeId)) {
            info.put("ELEMENT_DOMAIN", "2");// 移动400域名申请显示
            info.put("ELEMENT_DISABLED", "0");// 订单信息是否可编辑
            info.put("ELEMENT_MOBILE400", "2");// 订单信息是否可编辑
            info.put("DOMAIN_CONFIRM_NAME", getVisit().getStaffName());// 处理人
        }
    }

    private IData getEopAttrDatas(String busifromNodeId, String ibsysid) throws Exception {
        IData inparam = new DataMap();
        inparam.put("BUSIFORM_NODE_ID", busifromNodeId);
        IDataset nodeDatas = CSViewCall.call(this, "SS.EweNodeQrySVC.qryByBusiformNodeId", inparam);
        if (IDataUtil.isEmpty(nodeDatas)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据BUSIFORM_NODE_ID=" + busifromNodeId + "未查询到节点表数据！");
        }
        IData nodeData = nodeDatas.first();
        String preNodeId = nodeData.getString("PRE_NODE_ID");
        IData attrData = new DataMap();
        if (!"N/A".equals(preNodeId)) {// 非开始节点
            inparam.clear();
            inparam.put("IBSYSID", ibsysid);
            inparam.put("NODE_ID", preNodeId);
            IDataset attrDatas = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", inparam);
            if (IDataUtil.isNotEmpty(attrDatas)) {
                for (int i = 0, size = attrDatas.size(); i < size; i++) {
                    IData data = attrDatas.getData(i);
                    if (!"0".equals(data.getString("RECORD_NUM"))) {
                        attrData.put(data.getString("ATTR_CODE"), data.getString("ATTR_VALUE"));
                    }
                }
            }
        }
        return attrData;
    }

    @Override
    public void buildOtherSvcParam(IData param) throws Exception {
        super.buildOtherSvcParam(param);
        IData commonData = param.getData("COMMON_DATA");
        IDataset attrDatas = param.getDataset("CUSTOM_ATTR_LIST");
        if (IDataUtil.isEmpty(attrDatas)) {
            IDataset thisAttrData = new DatasetList();
            IData preAttrData = getEopAttrDatas(commonData.getString("BUSIFORM_NODE_ID"), commonData.getString("IBSYSID"));
            if (IDataUtil.isNotEmpty(preAttrData)) {
                Iterator<String> it = preAttrData.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    IData data = new DataMap();
                    data.put("ATTR_CODE", key);
                    data.put("ATTR_VALUE", preAttrData.getString(key));
                    data.put("RECORD_NUM", "1");
                    thisAttrData.add(data);
                }
            }
            param.put("CUSTOM_ATTR_LIST", thisAttrData);
        }
    }
}
