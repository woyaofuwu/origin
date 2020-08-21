package com.asiainfo.veris.crm.iorder.web.igroup.esop.datalineInformation;

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class RenewWorkSheetEdite extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setInAttr(IData inAttr);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setCustMgrInfo(IData custMgrInfo);

    public abstract void setFileList(IDataset fileList);

    public abstract void setBusi(IData busi);

    public abstract void setPubilcInfo(IData pubilcInfo);

    public abstract void setAttachInfo(IData attachInfo) throws Exception;

    private static Logger logger = Logger.getLogger(RenewWorkSheetEdite.class);

    public void queryData(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData inAttr = new DataMap();
        inAttr.put("FLOW_ID", "ESOP_" + data.getString("PRODUCT_ID")); // POINT_ONE
        inAttr.put("NODE_ID", data.getString("DEAL_TYPE")); // POINT_TWO
        IDataset infos = new DatasetList();
        // 对专线实列号进行排组查询
        setInAttr(inAttr);
        String nodeId = data.getString("NODE_ID");
        String recordNum = data.getString("RECORD_NUM");
        IData productNoData = new DataMap();
        IData datalineInfo = new DataMap();
        productNoData.put("NODE_ID", nodeId);
        productNoData.put("IBSYSID", data.getString("IBSYSID"));
        productNoData.put("RECORD_NUM", recordNum);
        productNoData.put("FLAG", "1");
        datalineInfo.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        datalineInfo.put("IBSYSID", data.getString("IBSYSID"));
        // 调接口，获取专线信息
        String notinrsrvstr2 = "";
        String notinrsrvstr3 = "";
        String bandwidth = "";
        String hiddenBandwidth = "";
        String subIbsysid = "";
        String groupSeq = "";
        String lineopenTag = "";
        IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.querydataLineAttrInfoList", productNoData);
        if (IDataUtil.isNotEmpty(result)) {
            subIbsysid = result.first().getString("SUB_IBSYSID");
            groupSeq = result.first().getString("GROUP_SEQ");
            for (Object object : result) {
                IData moseSubInfo = (IData) object;
                String attrValue = moseSubInfo.getString("ATTR_VALUE");
                if (StringUtils.isNotEmpty(attrValue) && attrValue.endsWith("%")) {
                    attrValue = attrValue.substring(0, attrValue.length() - 1);
                    moseSubInfo.put("ATTR_VALUE", attrValue);
                }
                if ("NOTIN_RSRV_STR2".equals(moseSubInfo.getString("ATTR_CODE"))) {
                    notinrsrvstr2 = moseSubInfo.getString("ATTR_VALUE");
                }
                if ("NOTIN_RSRV_STR3".equals(moseSubInfo.getString("ATTR_CODE"))) {
                    notinrsrvstr3 = moseSubInfo.getString("ATTR_VALUE");
                }
                if ("BANDWIDTH".equals(moseSubInfo.getString("ATTR_CODE"))) {
                    bandwidth = moseSubInfo.getString("ATTR_VALUE");
                }
                if ("HIDDEN_BANDWIDTH".equals(moseSubInfo.getString("ATTR_CODE"))) {
                    hiddenBandwidth = moseSubInfo.getString("ATTR_VALUE");
                }
                if ("LINEOPENTAG".equals(moseSubInfo.getString("ATTR_CODE"))) {
                    lineopenTag = moseSubInfo.getString("ATTR_VALUE");
                }
                datalineInfo.put(moseSubInfo.getString("ATTR_CODE").toUpperCase(), moseSubInfo.getString("ATTR_VALUE"));

            }
        }

        // 获取公共信息字段
        IData pubilcAttrInfo = new DataMap();
        IData pubilcInfo = new DataMap();
        pubilcInfo.put("NODE_ID", nodeId);
        pubilcInfo.put("IBSYSID", data.getString("IBSYSID"));
        pubilcInfo.put("RECORD_NUM", "0");
        pubilcInfo.put("SUB_IBSYSID", subIbsysid);
        pubilcInfo.put("GROUP_SEQ", groupSeq);
        if ("34".equals(data.getString("DEAL_TYPE"))) {
            pubilcInfo.put("DEAL_TYPE", data.getString("DEAL_TYPE"));
            pubilcInfo.put("RECORD_NUM_A", recordNum);
        }
        String acceprstaffid = "";
        String serialno = "";
        String opType = "";
        String opDesc = "";
        String changeMode = "";
        IDataset pubilcInfos = CSViewCall.call(this, "SS.WorkFormSVC.querydataLineInfoList", pubilcInfo);
        if (IDataUtil.isNotEmpty(pubilcInfos)) {
            acceprstaffid = pubilcInfos.getData(0).getString("ACCEPT_STAFF_ID");
            serialno = pubilcInfos.getData(0).getString("SERIALNO");
            opType = pubilcInfos.getData(0).getString("OP_TYPE");
            opDesc = pubilcInfos.getData(0).getString("OP_DESC");
            for (Object object1 : pubilcInfos) {
                IData moseSubInfo = (IData) object1;
                if ("CHANGEMODE".equals(moseSubInfo.getString("ATTR_CODE", ""))) {
                    changeMode = moseSubInfo.getString("ATTR_VALUE");
                }
                pubilcAttrInfo.put(moseSubInfo.getString("ATTR_CODE"), moseSubInfo.getString("ATTR_VALUE"));
            }
        }
        String serialNumber = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "SERIAL_NUMBER", acceprstaffid);

        // 获取专线条数
        IDataset recordNumInfo = CSViewCall.call(this, "SS.WorkFormSVC.qryEomsStateInfos", data);
        int recordN = recordNumInfo.size() - 1;
        // 比较是否是整单驳回，不是整单驳回不显示附件信息，附件不发送资管
        if (recordN == 1) {
            data.put("RECORD_NUM_FLAG", "TRUE");
        }

        data.put("SUB_IBSYSID", subIbsysid);
        data.put("PUBLIC_ATTR_LIST", pubilcAttrInfo);
        data.put("SERIALNO", serialno);
        data.put("OP_TYPE", opType);
        data.put("OP_DESC", opDesc);
        data.put("CHANGEMODE", changeMode);
        data.put("NOTIN_RSRV_STR2_A", notinrsrvstr2);
        data.put("NOTIN_RSRV_STR3_A", notinrsrvstr3);
        data.put("BANDWIDTH_A", bandwidth);
        data.put("HIDDEN_BANDWIDTH", hiddenBandwidth);
        data.put("LINEOPEN_TAG", lineopenTag);
        pubilcAttrInfo.put("ACCEPT_STAFF_ID", acceprstaffid);
        pubilcAttrInfo.put("ACCEPT_SERIAL_NUMBER", serialNumber);

        // 加载附件
        getAllAttach(subIbsysid);

        // 获取合同信息
        if ("EDIRECTLINEOPENPBOSS".equals(data.getString("BPM_TEMPLET_ID")) || "EVIOPDIRECTLINEOPENPBOSS".equals(data.getString("BPM_TEMPLET_ID"))) {

            // 获取合同附件
            IData inparam = new DataMap();
            inparam.put("IBSYSID", data.getString("IBSYSID"));
            inparam.put("SUB_IBSYSID", subIbsysid);
            inparam.put("RECORD_NUM", "0");
            String fileDataC = "false";
            IDataset attachList = CSViewCall.call(this, "SS.WorkFormSVC.queryByAttach", inparam);
            if (IDataUtil.isNotEmpty(attachList)) {
                for (int i = 0; i < attachList.size(); i++) {
                    IData attachData = attachList.getData(i);
                    if ("C".equals(attachData.getString("ATTACH_TYPE"))) {
                        fileDataC = "true";
                        data.put("CONTRACT_FILE_LIST", attachData.getString("FILE_ID"));
                        IData fileData = new DataMap();
                        fileData.put("FILE_ID", attachData.getString("FILE_ID"));
                        fileData.put("FILE_NAME", attachData.getString("ATTACH_NAME"));
                        fileData.put("ATTACH_TYPE", attachData.getString("ATTACH_TYPE"));
                        // data.put("C_FILE_LIST", fileData);
                        IDataset fileListInfos = new DatasetList();
                        fileListInfos.add(fileData);
                        SharedCache.set("C_FILE_LIST_DATA", fileListInfos);
                        setAjax(fileData);
                    }
                }
            }
            // 未查询到合同附件，选择利旧合同，不显示合同附件
            if ("false".equals(fileDataC)) {
                data.put("CONTRACT_FILE_FLAG", "FALSE");
            } else {
                data.put("CONTRACT_FILE_FLAG", "TRUE");
            }
        }

        this.setBusi(datalineInfo);
        this.setInfo(datalineInfo);
        this.setCondition(data);
        this.setPubilcInfo(pubilcAttrInfo);
    }

    /**
     * 重派
     * 
     * @throws Exception
     */
    public void checkinWorkSheet(IRequestCycle cycle) throws Exception {
        IData info = this.getData();
        // 配重派单数据结构
        String dealType = info.getString("DEAL_TYPE");
        IData upParam = new DataMap();
        IData param = new DataMap();
        if ("34".equals(dealType)) {
            param = makeRenewParamDestroy(info);
            IDataset recordNumberInfos = new DatasetList();
            IData recordNumberInfo = new DataMap();
            recordNumberInfo.put("RECORD_NUM", info.getString("RECORD_NUM"));
            recordNumberInfos.add(recordNumberInfo);
            upParam.put("RECORDNUM_INFOS", recordNumberInfos);
            upParam.put("BUSIFORM_ID", info.getString("BUSIFORM_ID"));
            upParam.put("BUSIFORM_NODE_ID", info.getString("BUSIFORM_NODE_ID"));
        } else {
            param = makeRenewParam(info);
            upParam.put("RECORDNUM_INFOS", param.getDataset("RECORDNUM_INFOS"));
            upParam.put("BUSIFORM_ID", info.getString("BUSIFORM_ID"));
            upParam.put("BUSIFORM_NODE_ID", info.getString("BUSIFORM_NODE_ID"));
        }
        // 记录信息到eop相关表，并重派
        logger.debug("+++++taosx++++: " + param);
        CSViewCall.call(this, "SS.WorkformEomsInteractiveSVC.record", param);

        // 删除代办
        CSViewCall.call(this, "SS.WorkTaskMgrSVC.updWorkTaskByRecordNum", upParam);

    }

    public IData makeRenewParam(IData info) throws Exception {

        IData param = new DataMap();
        IDataset directlineDate = new DatasetList(info.getString("DATALINE_LIST"));// 派单信息
        IData publicAttrInfo = new DataMap(info.getString("PUBLIC_ATTR_LIST"));// 公共信息
        IDataset attachInfos = new DatasetList(info.getString("ATTACH_LIST"));// 附件信息
        IDataset contractAttachInfos = new DatasetList(info.getString("CONTRACTATTACH_LIST"));// 合同信息
        IDataset cfilelist = (IDataset) SharedCache.get("C_FILE_LIST_DATA");// 不修改的合同附件
        IDataset eomsInfos = new DatasetList();
        IDataset attrLists = new DatasetList();// 拼专线信息
        IDataset attrListss = new DatasetList();// 拼公共信息
        IDataset attachInfoss = new DatasetList();// 拼附件信息
        IData eomsInfo = new DataMap();
        String bpmtempletId = info.getString("BPM_TEMPLET_ID");

        // 校验专线名称是否重复
        checkDataNameInfo(directlineDate, info);

        // 校验公共附件是否存在相同名字
        if (IDataUtil.isNotEmpty(attachInfos)) {
            for (int i = 0; i < attachInfos.size(); i++) {
                IData dataInfo = attachInfos.getData(i);
                String fileNameI = dataInfo.getString("FILE_NAME");
                for (int s = 0; s < attachInfos.size(); s++) {
                    IData dataInfoS = attachInfos.getData(s);
                    String fileNameC = dataInfoS.getString("FILE_NAME");
                    if (i != s && fileNameI.equals(fileNameC)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "您上传的公共附件存在相同的名字【" + fileNameI + "】,请修改后再重派！");
                        break;
                    }
                }
            }
        }

        // 校验合同附件与公共附件是否存在相同的附件
        if ("EDIRECTLINEOPENPBOSS".equals(bpmtempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmtempletId)) {
            if (IDataUtil.isEmpty(contractAttachInfos)) {
                contractAttachInfos = cfilelist;
            }
            if (IDataUtil.isNotEmpty(contractAttachInfos) && IDataUtil.isNotEmpty(attachInfos)) {
                String fileNameC = contractAttachInfos.first().getString("FILE_NAME");
                for (Object attachIf : attachInfos) {
                    IData attachData = (IData) attachIf;
                    String fileNameP = attachData.getString("FILE_NAME");
                    if (fileNameC.equals(fileNameP)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "您上传的合同附件与公共附件存在相同的名字【" + fileNameP + "】,请修改后再重派！");
                    }
                }
            }
        }
        // 处理派单信息
        IData temp = null;
        IData dAttr = null;
        String recordNumber = "";
        String subIbsysId = "";
        IDataset recordNumberInfos = new DatasetList();

        for (int j = 0; j < directlineDate.size(); j++) {
            temp = directlineDate.getData(j);
            dAttr = new DataMap();
            // 获取专线的subIbsysId与recordNumber
            if ("pam_BANDWIDTH".equals(temp.getString("ATTR_CODE"))) {
                String changeMode = info.getString("CHANGEMODE");
                if ("减容".equals(changeMode)) {
                    String bandwidthA = info.getString("BANDWIDTH_A");
                    String bandwidth = temp.getString("ATTR_VALUE");
                    int bandWidth = Integer.parseInt(bandwidthA);// 修改前的带宽
                    int bandWidthS = Integer.parseInt(bandwidth);// 修改的带宽
                    if (bandWidthS > bandWidth) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "您修改的减容场景带宽大于修改前带宽【" + bandwidthA + "M】，请重新输入！");
                    }
                }
                if ("扩容".equals(changeMode)) {
                    String hiddenBanwidth = info.getString("HIDDEN_BANDWIDTH");
                    if (StringUtils.isBlank(hiddenBanwidth)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "您受理的扩容业务，未获取到勘察单带宽，请核查数据！");
                    }
                    String bandwidth = temp.getString("ATTR_VALUE");
                    int bandWidth = Integer.parseInt(hiddenBanwidth);// 修改前的带宽
                    int bandWidthS = Integer.parseInt(bandwidth);// 修改的带宽
                    if (bandWidthS > bandWidth) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "您修改的扩容场景带宽大于勘察时的带宽【" + hiddenBanwidth + "M】，请重新输入！");
                    }
                }
            }
            if ("pam_TRADEID".equals(temp.getString("ATTR_CODE"))) {
                IData data = new DataMap();
                data.put("ATTR_VALUE", temp.getString("ATTR_VALUE"));
                data.put("ATTR_CODE", "PRODUCTNO");
                data.put("IBSYSID", info.getString("IBSYSID"));
                IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.queryattrByRecordNum", data);
                if (IDataUtil.isNotEmpty(result)) {
                    IData recordNumberInfo = new DataMap();
                    recordNumber = result.getData(0).getString("RECORD_NUM");
                    subIbsysId = result.getData(0).getString("SUB_IBSYSID");
                    recordNumberInfo.put("RECORD_NUM", recordNumber);
                    recordNumberInfos.add(recordNumberInfo);

                }
            }
            // 去掉配置参数的前缀pam_
            if (temp.getString("ATTR_CODE").contains("pam_")) {
                dAttr.put("ATTR_CODE", StringUtils.substringAfter(temp.getString("ATTR_CODE"), "_"));
            } else {
                dAttr.put("ATTR_CODE", temp.getString("ATTR_CODE"));
            }
            // 数据专线三个比例数据需要加百分号
            String attrCode = temp.getString("ATTR_CODE");
            if (attrCode.equals("NOTIN_RSRV_STR6") || attrCode.equals("NOTIN_RSRV_STR7") || attrCode.equals("NOTIN_RSRV_STR8") || attrCode.equals("pam_NOTIN_A_PERCENT") || attrCode.equals("pam_NOTIN_GROUP_PERCENT")
                    || attrCode.equals("pam_NOTIN_Z_PERCENT")) {
                dAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE") + "%");
            } else {
                dAttr.put("ATTR_VALUE", temp.getString("ATTR_VALUE"));
            }
            dAttr.put("ATTR_NAME", temp.getString("ATTR_NAME"));

            attrLists.add(dAttr);
        }
        // 处理公共信息
        if (IDataUtil.isNotEmpty(publicAttrInfo)) {
            Set<String> attrNames = publicAttrInfo.keySet();
            for (String key : attrNames) {
                IData tp = new DataMap();
                if ("BUILDINGSECTION".equals(key)) {
                    tp.put("ATTR_VALUE", info.getString("BUILDINGSECTION"));
                    tp.put("ATTR_CODE", key);
                    attrListss.add(tp);
                } else {
                    tp.put("ATTR_VALUE", publicAttrInfo.getString(key));
                    tp.put("ATTR_CODE", key);
                    attrListss.add(tp);
                }
            }
            // 传附件信息
            if (IDataUtil.isNotEmpty(attachInfos)) {
                for (Object object : attachInfos) {
                    IData attachInfo = (IData) object;
                    attachInfo.put("IBSYSID", info.getString("IBSYSID"));
                    attachInfo.put("SUB_IBSYSID", info.getString("SUB_IBSYSID"));
                    attachInfo.put("RECORD_NUM", "0");
                    attachInfo.put("NODE_ID", info.getString("NODE_ID"));
                    attachInfo.put("ATTACH_CITY_CODE", getVisit().getCityCode());
                    attachInfo.put("ATTACH_EPARCHY_CODE", getVisit().getLoginEparchyCode());
                    attachInfo.put("ATTACH_DEPART_ID", getVisit().getDepartId());
                    attachInfo.put("ATTACH_DEPART_NAME", getVisit().getDepartName());
                    attachInfo.put("ATTACH_STAFF_ID", getVisit().getStaffId());
                    attachInfo.put("ATTACH_STAFF_NAME", getVisit().getStaffName());
                    attachInfo.put("ATTACH_STAFF_PHONE", getVisit().getSerialNumber());
                    attachInfo.put("ATTACH_NAME", attachInfo.getString("FILE_NAME"));
                    attachInfoss.add(attachInfo);
                }
            }

            // 合同附件
            if ("EDIRECTLINEOPENPBOSS".equals(bpmtempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmtempletId)) {
                if (IDataUtil.isNotEmpty(contractAttachInfos)) {
                    IData contractInfo = contractAttachInfos.first();
                    contractInfo.put("IBSYSID", info.getString("IBSYSID"));
                    contractInfo.put("SUB_IBSYSID", subIbsysId);
                    contractInfo.put("RECORD_NUM", "0");
                    contractInfo.put("NODE_ID", info.getString("NODE_ID"));
                    contractInfo.put("ATTACH_CITY_CODE", getVisit().getCityCode());
                    contractInfo.put("ATTACH_EPARCHY_CODE", getVisit().getLoginEparchyCode());
                    contractInfo.put("ATTACH_DEPART_ID", getVisit().getDepartId());
                    contractInfo.put("ATTACH_DEPART_NAME", getVisit().getDepartName());
                    contractInfo.put("ATTACH_STAFF_ID", getVisit().getStaffId());
                    contractInfo.put("ATTACH_STAFF_NAME", getVisit().getStaffName());
                    contractInfo.put("ATTACH_STAFF_PHONE", getVisit().getSerialNumber());
                    contractInfo.put("ATTACH_NAME", contractInfo.getString("FILE_NAME"));
                    attachInfoss.add(contractInfo);

                    IData datamap = new DataMap();
                    datamap.put("IBSYSID", info.getString("IBSYSID"));
                    datamap.put("ATTR_CODE", "CONTRACT_ID");
                    // 获取合同编码
                    IDataset contractidInfos = CSViewCall.call(this, "SS.WorkFormSVC.querydataLineChangemode", datamap);
                    if (IDataUtil.isEmpty(contractidInfos)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到合同编码，无法重派，请确定开通或变更时，合同信息是否正常！");
                    }
                    String contractid = contractidInfos.first().getString("ATTR_VALUE");
                    // 更新客管合同附件
                    IData contMap = new DataMap();
                    contMap.put("CONTRACT_ID", contractid);
                    contMap.put("FILE_LIST_NAME", contractInfo.getString("FILE_ID") + ":" + contractInfo.getString("FILE_NAME"));
                    CSViewCall.call(this, "SS.WorkFormSVC.updateDatecontractInfo", contMap);

                }
            }

        }
        eomsInfo.put("IBSYSID", info.getString("IBSYSID"));
        eomsInfo.put("SUB_IBSYSID", subIbsysId);
        eomsInfo.put("RECORD_NUM", recordNumber);
        eomsInfo.put("TRADE_DRIECT", "0");
        eomsInfo.put("OPER_TYPE", "renewWorkSheet");
        eomsInfo.put("ATTACHREF", param.getString("attachRef", ""));
        eomsInfo.put("ATTR_INFOS", attrLists);

        eomsInfo.put("SERIALNO", info.getString("SERIALNO"));
        eomsInfo.put("STATE_TAG", "false");
        eomsInfo.put("TRADE_DRIECT", "0");

        eomsInfos.add(eomsInfo);
        param.put("COMMON_INFOS", attrListss);
        param.put("ATTACH_INFOS", attachInfoss);
        param.put("EOMS_INFOS", eomsInfos);
        param.put("WORKSHEET_EDITE", "ONE");
        param.put("RECORDNUM_INFOS", recordNumberInfos);
        return param;
    }

    public IData makeRenewParamDestroy(IData info) throws Exception {
        IData param = new DataMap();
        IDataset eomsInfos = new DatasetList();

        IData publicAttrInfo = new DataMap(info.getString("PUBLIC_ATTR_LIST"));// 公共信息
        IDataset attachInfos = new DatasetList(info.getString("ATTACH_LIST"));// 附件信息
        IDataset contractAttachInfos = new DatasetList(info.getString("CONTRACTATTACH_LIST"));// 合同信息
        IDataset cfilelist = (IDataset) SharedCache.get("C_FILE_LIST_DATA");// 不修改的合同附件
        IDataset attrListss = new DatasetList();// 拼公共信息
        IDataset attachInfoss = new DatasetList();// 拼附件信息
        IData eomsInfo = new DataMap();
        eomsInfo.put("FLAG", "1");
        eomsInfo.put("IBSYSID", info.getString("IBSYSID"));
        eomsInfo.put("RECORD_NUM", info.getString("RECORD_NUM"));
        IDataset attrLists = CSViewCall.call(this, "SS.WorkFormSVC.querydataLineAttrInfoList", eomsInfo);// 拼专线信息
        String bpmtempletId = info.getString("BPM_TEMPLET_ID");
        // 校验公共附件是否存在相同名字
        if (IDataUtil.isNotEmpty(attachInfos)) {
            for (int i = 0; i < attachInfos.size(); i++) {
                IData dataInfo = attachInfos.getData(i);
                String fileNameI = dataInfo.getString("FILE_NAME");
                for (int s = 0; s < attachInfos.size(); s++) {
                    IData dataInfoS = attachInfos.getData(s);
                    String fileNameC = dataInfoS.getString("FILE_NAME");
                    if (i != s && fileNameI.equals(fileNameC)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "您上传的公共附件存在相同的名字【" + fileNameI + "】,请修改后再重派！");
                        break;
                    }
                }
            }
        }

        // 校验合同附件与公共附件是否存在相同的附件
        if ("EDIRECTLINEOPENPBOSS".equals(bpmtempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmtempletId)) {
            if (IDataUtil.isEmpty(contractAttachInfos)) {
                contractAttachInfos = cfilelist;
            }
            if (IDataUtil.isNotEmpty(contractAttachInfos) && IDataUtil.isNotEmpty(attachInfos)) {
                String fileNameC = contractAttachInfos.first().getString("FILE_NAME");
                for (Object attachIf : attachInfos) {
                    IData attachData = (IData) attachIf;
                    String fileNameP = attachData.getString("FILE_NAME");
                    if (fileNameC.equals(fileNameP)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "您上传的合同附件与公共附件存在相同的名字【" + fileNameP + "】,请修改后再重派！");
                    }
                }
            }
        }

        String subIbsysId = "";
        if (IDataUtil.isNotEmpty(attrLists)) {
            subIbsysId = attrLists.first().getString("SUB_IBSYSID");
        }
        // 处理公共信息
        if (IDataUtil.isNotEmpty(publicAttrInfo)) {
            IData publicInfo = new DataMap();
            Set<String> attrNames = publicAttrInfo.keySet();
            for (String key : attrNames) {
                IData tp = new DataMap();
                if ("BUILDINGSECTION".equals(key)) {
                    tp.put("ATTR_VALUE", info.getString("BUILDINGSECTION"));
                    tp.put("ATTR_CODE", key);
                    attrListss.add(tp);
                } else if ("CANCELTIME".equals(key)) {
                    tp.put("ATTR_VALUE", info.getString("CANCELTIME"));
                    tp.put("ATTR_CODE", key);
                    attrListss.add(tp);
                } else if ("BACKOUTCAUSE".equals(key)) {
                    tp.put("ATTR_VALUE", info.getString("BACKOUTCAUSE"));
                    tp.put("ATTR_CODE", key);
                    attrListss.add(tp);
                } else {
                    tp.put("ATTR_VALUE", publicAttrInfo.getString(key));
                    tp.put("ATTR_CODE", key);
                    attrListss.add(tp);
                }
            }
            // 传附件信息
            if (IDataUtil.isNotEmpty(attachInfos)) {
                for (Object object : attachInfos) {
                    IData attachInfo = (IData) object;
                    attachInfo.put("IBSYSID", info.getString("IBSYSID"));
                    attachInfo.put("SUB_IBSYSID", info.getString("SUB_IBSYSID"));
                    attachInfo.put("RECORD_NUM", "0");
                    attachInfo.put("NODE_ID", info.getString("NODE_ID"));
                    attachInfo.put("ATTACH_CITY_CODE", getVisit().getCityCode());
                    attachInfo.put("ATTACH_EPARCHY_CODE", getVisit().getLoginEparchyCode());
                    attachInfo.put("ATTACH_DEPART_ID", getVisit().getDepartId());
                    attachInfo.put("ATTACH_DEPART_NAME", getVisit().getDepartName());
                    attachInfo.put("ATTACH_STAFF_ID", getVisit().getStaffId());
                    attachInfo.put("ATTACH_STAFF_NAME", getVisit().getStaffName());
                    attachInfo.put("ATTACH_STAFF_PHONE", getVisit().getSerialNumber());
                    attachInfo.put("ATTACH_NAME", attachInfo.getString("FILE_NAME"));
                    attachInfoss.add(attachInfo);
                }
            }

            publicInfo.put("IBSYSID", info.getString("IBSYSID"));
            publicInfo.put("SUB_IBSYSID", subIbsysId);
            publicInfo.put("RECORD_NUM", info.getString("RECORD_NUM"));
            publicInfo.put("TRADE_DRIECT", "0");
            publicInfo.put("OPER_TYPE", "renewWorkSheet");
            publicInfo.put("ATTR_INFOS", attrLists);
            publicInfo.put("SERIALNO", info.getString("SERIALNO"));
            publicInfo.put("STATE_TAG", "false");
            publicInfo.put("TRADE_DRIECT", "0");
            eomsInfos.add(publicInfo);
        }

        param.put("COMMON_INFOS", attrListss);
        param.put("ATTACH_INFOS", attachInfoss);
        param.put("EOMS_INFOS", eomsInfos);
        param.put("WORKSHEET_EDITE", "ONE");
        return param;
    }

    private void getAllAttach(String subIbsysid) throws Exception {
        String ibsysid = getData().getString("IBSYSID");
        IData inparam = new DataMap();
        inparam.put("IBSYSID", ibsysid);
        inparam.put("CUSTMG", "true");
        IDataset attachList = CSViewCall.call(this, "SS.WorkFormSVC.qryGroupAttach", inparam);
        if (IDataUtil.isNotEmpty(attachList)) {
            IData attachInfo = new DataMap();
            String productFileId = "";
            String productFileName = "";
            IDataset productList = new DatasetList();

            for (int i = 0; i < attachList.size(); i++) {
                IData attachData = attachList.getData(i);// ATTACH_TYPE
                String attachType = attachData.getString("ATTACH_TYPE");
                String fileId = attachData.getString("FILE_ID");
                String fileName = attachData.getString("ATTACH_NAME");
                if ("P".equals(attachType)) {// 普通附件
                    productFileId += fileId + ",";
                    productFileName += fileName + ",";
                    IData productFileData = new DataMap();
                    productFileData.put("FILE_ID", fileId);
                    productFileData.put("FILE_NAME", fileName);
                    productFileData.put("ATTACH_TYPE", attachType);
                    productList.add(productFileData);
                }
            }

            if (StringUtils.isNotBlank(productFileId)) {
                attachInfo.put("PRODUCT_FILEID", productFileId.substring(0, productFileId.length() - 1));
                attachInfo.put("PRODUCT_FILENAME", productFileName.substring(0, productFileName.length() - 1));
                attachInfo.put("PRODUCT_LIST", productList);
            }
            setAttachInfo(attachInfo);
        }
    }

    private void checkDataNameInfo(IDataset attrList, IData info) throws Exception {
        // 校验专线名称是否存在
        String tradeName = "";
        String productNo = "";
        boolean result = false;
        String bpmTempletId = info.getString("BPM_TEMPLET_ID", "");
        if ("ERESOURCECONFIRMZHZG".equals(bpmTempletId) || "ECHANGERESOURCECONFIRM".equals(bpmTempletId)) {
            result = true;
        } else if (IDataUtil.isNotEmpty(attrList)) {
            for (Object datalineIf : attrList) {
                IData lineData = (IData) datalineIf;
                if ("pam_PRODUCTNO".equals(lineData.getString("ATTR_CODE"))) {
                    productNo = lineData.getString("ATTR_VALUE");
                }
                if ("pam_TRADENAME".equals(lineData.getString("ATTR_CODE"))) {
                    tradeName = lineData.getString("ATTR_VALUE");
                }
            }
            if (StringUtils.isNotBlank(tradeName) && StringUtils.isNotBlank(productNo)) {

                IData data = new DataMap();
                data.put("PRODUCTNO", productNo);
                data.put("DATA_LINE_NAME", tradeName);

                // 先查DATALINE表的专线名称是否与页面的专线名称一致
                IData userDataNameInfos = CSViewCall.callone(this, "SS.QcsGrpIntfSVC.qryUserDatalineByProductNO", data);
                String datalineName = "";
                if (IDataUtil.isNotEmpty(userDataNameInfos)) {
                    datalineName = userDataNameInfos.getString("RSRV_STR5", "");
                }
                // 名称一致直接直接返回
                if (datalineName.equals(tradeName)) {
                    result = true;
                } else {

                    // 不一致再查dataline表与在途ATTR表是否存在相同的专线名称，(取ATTR表最新的数据，且流程不为勘察单的流程)
                    IData datalineNameInfo = CSViewCall.callone(this, "SS.QcsGrpIntfSVC.qryGrpUserDatalineByName", data);
                    if (IDataUtil.isEmpty(datalineNameInfo)) {
                        result = true;
                    } else {
                        String productnos = datalineNameInfo.getString("PRODUCT_NO", "");
                        if (productnos.equals(productNo)) {
                            result = true;
                        } else {
                            result = false;
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您对应的专线实列号：【" + productNo + "】,输入的专线名称：【" + tradeName + "】已在实列号：【" + productnos + "】中使用，不能重复，请修改！");
                        }
                    }

                }

            }
        }
    }
}
