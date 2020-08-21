package com.asiainfo.veris.crm.iorder.web.igroup.esop.customerMgAffirm;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.EscapeUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class CustomerMgAffirm extends EopBasePage {

    public abstract void setInAttr(IData inAttr);

    public abstract void setPattr(IData pattr);

    public abstract void setInfo(IData info);

    public abstract void setAuditInfo(IData info);

    public abstract void setPattrs(IDataset pattrs);

    public abstract void setProductInfo(IData productInfo);

    public abstract void setOrders(IDataset orders);

    public abstract void setFileInfos(IDataset fileInfos);

    public abstract void setFilePublic(IDataset filePublic);

    public void initPage(IRequestCycle cycle) throws Exception {
        super.initPage(cycle);
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        String nodeId = param.getString("NODE_ID");
        String busiformNodeId = param.getString("BUSIFORM_NODE_ID");
        if (StringUtils.isNotBlank(ibsysid)) {
            queryInfosByIbsysid(cycle);
        }
        IData info = new DataMap();

        try {
            IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
            subscribeData.put("NODE_ID", nodeId);
            subscribeData.put("BUSIFORM_NODE_ID", busiformNodeId);
            info.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
            info.put("NODE_ID", nodeId);
            String bpmTempletId = param.getString("BPM_TEMPLET_ID");
            info.put("BPM_TEMPLET_ID", bpmTempletId);
            IDataset productAttrInfos = CSViewCall.call(this, "SS.WorkFormSVC.getWorkfromProductAttr", param);
            setPattrs(productAttrInfos);
            if (bpmTempletId.equals("ETAPMARKETINGENTERING") || bpmTempletId.equals("ETAPMARKETINGEXCITATION") || bpmTempletId.equals("EDIRECTLINEDATACHANGE")) {
                IDataset pattrs = new DatasetList();
                getEopAttrToList(ibsysid, pattrs, info);
                setPattrs(pattrs);
                info.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
            }
            if (bpmTempletId.equals("EDIRECTLINECHANGEFEE")) {
                info.put("IBSYSID", ibsysid);
                info.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
                IDataset pattrs = new DatasetList();
                pattrs = getdataLineList(ibsysid);
                setPattrs(pattrs);
            }
            // 获取登陆工号
            String staffId = getVisit().getStaffId();
            info.put("STAFF_ID", staffId);
        }
        catch (Exception e) {
            // TODO: handle exception
            info.put("EXCEPTION", "根据IBSYSID=" + ibsysid + "未查询到流程订单主表数据！");

        }
        setInfo(info);
        /* IData inAttr = new DataMap(); inAttr.put("FLOW_ID", "ADUIT_7011" + param.getString("PRODUCT_ID")); // POINT_ONE inAttr.put("NODE_ID", param.getString("DEAL_TYPE")"ADUIT"); // POINT_TWO setInAttr(inAttr); */
        getStaffInfo();

        // 查询附件
        param.put("CUSTMG", "true");
        IDataset fileset = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryGroupAttach", param);
        IDataset zhzgFileDatas = new DatasetList();
        if (IDataUtil.isNotEmpty(fileset)) {
            for (Object object : fileset) {
                IDataset zhzgFileInfos = new DatasetList();
                IData zhzgFiles = new DataMap();
                IData fileInfo = (IData) object;
                IDataset zhzgInfos = fileInfo.getDataset("LIST");
                for (Object object2 : zhzgInfos) {
                    // 拼资管附件，给超链接地址到页面跳转
                    IData zhzgFile = new DataMap();
                    IData fileInfo1 = (IData) object2;
                    if ("0".equals(fileInfo1.getString("FILE_ID")) && "Z".equals(fileInfo1.getString("ATTACH_TYPE"))) {
                        zhzgFile.put("ATTACH_URL", EscapeUtil.unescape(fileInfo1.getString("ATTACH_URL")));
                        zhzgFile.put("ATTACH_NAME", fileInfo1.getString("ATTACH_NAME"));
                        zhzgFileInfos.add(zhzgFile);
                    }
                }
                if (IDataUtil.isNotEmpty(zhzgFileInfos)) {
                    zhzgFiles.put("LIST", zhzgFileInfos);
                    zhzgFiles.put("TITLE", fileInfo.getString("TITLE"));
                    zhzgFileDatas.add(zhzgFiles);
                }
            }
        }
        // 删除资管的附件，不放到公共附件里面
        Iterator<Object> filesetInfo = fileset.iterator();
        while (filesetInfo.hasNext()) {
            IData filesetIf = (IData) filesetInfo.next();
            String types = filesetIf.getString("TYPE");
            if ("Z".equals(types)) {
                filesetInfo.remove();
            }
        }
        setFilePublic(fileset);
        setFileInfos(zhzgFileDatas);
        // 查询资管回复信息
        IDataset sourceconfirmInfos = CSViewCall.call(this, "SS.WorkFormSVC.queryReadSourceconfirm", param);
        if (IDataUtil.isNotEmpty(sourceconfirmInfos)) {
            Iterator<Object> results = sourceconfirmInfos.iterator();
            while (results.hasNext()) {
                IData resultsInfo = (IData) results.next();
                if (StringUtils.isBlank(resultsInfo.getString("ATTR_NAME"))) {
                    results.remove();
                }
            }
        }
        setOrders(sourceconfirmInfos);
    }

    private IDataset getdataLineList(String ibsysid) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        IDataset productSubs = CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductByIbsysid", param);
        IDataset infos = new DatasetList();
        for (int i = 0; i < productSubs.size(); i++) {
            IData productSub = productSubs.getData(i);
            String reCordeNum = productSub.getString("RECORD_NUM");
            param.put("RECORD_NUM", reCordeNum);
            param.put("NODE_ID", "apply");
            IDataset atts = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", param);
            IData attr = new DataMap();
            for (int j = 0; j < atts.size(); j++) {
                String key = atts.getData(j).getString("ATTR_CODE");
                String value = atts.getData(j).getString("ATTR_VALUE");
                attr.put(key, value);
            }
            infos.add(attr);
        }
        return infos;
    }

    private void getStaffInfo() throws Exception {
        String staff = getVisit().getStaffId();
        IData param = new DataMap();
        param.put("STAFF_ID", staff);
        IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", param);
        IData data1 = new DataMap();
        IData data2 = new DataMap();
        data1.put("DATA_NAME", "通过");
        data1.put("DATA_ID", "1");
        data2.put("DATA_NAME", "不通过");
        data2.put("DATA_ID", "2");
        IDataset list = new DatasetList();
        list.add(data1);
        list.add(data2);
        staffInfo.put("LIST", list);
        setAuditInfo(staffInfo);
    }

    public void queryInfosByIbsysid(IRequestCycle cycle) throws Exception {
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        IData workformData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
        String groupId = "";
        if (IDataUtil.isNotEmpty(workformData)) {
            groupId = workformData.getString("GROUP_ID");
        } else {
            this.setAjax("error_message", "根据工单号未查到对应工单, 请核实");
            return;
        }
        if (StringUtils.isNotBlank(groupId)) {
            IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
            group.put("IBSYSID", ibsysid);
            setGroupInfo(group);
        }
        String busiCode = workformData.getString("BUSI_CODE");
        // 查询产品属性
        getProductInfos(busiCode, ibsysid);

    }

    public void qryByIbsysidProductNo(IRequestCycle cycle) throws Exception {
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        String productNo = param.getString("PRODUCTNO");
        String nodeId = param.getString("NODE_ID");
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("PRODUCTNO", productNo);
        data.put("NODE_ID", nodeId);

        IData productInfo = CSViewCall.callone(this, "SS.WorkformAttrSVC.qryByIbsysidProductNoNodeId", data);
        productInfo.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        productInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", param.getString("PRODUCT_ID")));
        setProductInfo(productInfo);
    }

    private void getProductInfos(String busiCode, String ibsysid) throws Exception {
        IDataset productAttrs = WorkfromViewCall.qryDataLineInfoByIbsysid(this, ibsysid);
        if (productAttrs != null && productAttrs.size() > 0) {
            for (Object obj : productAttrs) {
                IData data = (IData) obj;
                data.put("PRODUCT_ID", busiCode);
                data.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", busiCode));
            }
        }
        setPattrs(productAttrs);
    }

}
