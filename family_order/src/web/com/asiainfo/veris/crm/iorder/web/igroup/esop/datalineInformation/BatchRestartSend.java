package com.asiainfo.veris.crm.iorder.web.igroup.esop.datalineInformation;

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class BatchRestartSend extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setCustMgrInfo(IData custMgrInfo);

    public abstract void setInAttr(IData inAttr);

    public abstract void setPubilcInfo(IData pubilcInfo);

    public abstract void setAttachInfo(IData attachInfo) throws Exception;

    private static Logger logger = Logger.getLogger(BatchRestartSend.class);

    public void querydataLineInfoList(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        IDataset infos = new DatasetList();
        IData pubilcAttrInfo = new DataMap();
        // IDataset eomsInfos = new DatasetList(data.getString("DATALINE_LIST"));
        IDataset eomsInfos = (IDataset) SharedCache.get("DATALINE_ATTRINFOS");
        String dealType = "";
        String productId = "";
        IData pubilcInfo = new DataMap();
        if (IDataUtil.isNotEmpty(eomsInfos)) {
            dealType = eomsInfos.getData(0).getString("DEAL_TYPE");
            productId = eomsInfos.getData(0).getString("PRODUCT_ID");
            // 公共信息查询条件
            pubilcInfo.put("NODE_ID", eomsInfos.getData(0).getString("NODE_ID"));
            pubilcInfo.put("IBSYSID", eomsInfos.getData(0).getString("IBSYSID"));
            pubilcInfo.put("RECORD_NUM", "0");
            // 隐藏域赋值
            data.put("IBSYSID", eomsInfos.getData(0).getString("IBSYSID"));
            data.put("PRODUCT_ID", eomsInfos.getData(0).getString("PRODUCT_ID"));
            data.put("SERIALNO", eomsInfos.getData(0).getString("SERIALNO"));
            data.put("NODE_ID", eomsInfos.getData(0).getString("NODE_ID"));

        }

        // 获取驳回条数
        int eomsSize = eomsInfos.size();
        // 获取专线条数
        IDataset recordNumInfo = CSViewCall.call(this, "SS.WorkFormSVC.qryEomsStateInfos", data);
        int recordN = recordNumInfo.size() - 1;
        // 比较是否是整单驳回，不是整单驳回不显示附件信息，附件不发送资管
        if (eomsSize == recordN) {
            data.put("RECORD_NUM_FLAG", "TRUE");
        }

        // 获取页面配置信息
        IData inAttr = new DataMap();
        inAttr.put("FLOW_ID", "RESTART_" + productId); // POINT_ONE
        inAttr.put("NODE_ID", dealType); // POINT_TWO
        setInAttr(inAttr);
        // 查询专线属性
        String productNoAll = "";
        String recodeNumAll = "";
        for (Object object : eomsInfos) {
            IData eomsInfo = (IData) object;
            // 拼导入的查询参数
            productNoAll += eomsInfo.getString("PRODUCT_NO") + ",";
            recodeNumAll += eomsInfo.getString("RECORD_NUM") + ",";
            eomsInfo.put("FLAG", "1");
            IData datalineInfo = new DataMap();
            IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.querydataLineAttrInfoList", eomsInfo);
            if (IDataUtil.isNotEmpty(result)) {
                for (Object object1 : result) {
                    IData moseSubInfo = (IData) object1;
                    String attrValue = moseSubInfo.getString("ATTR_VALUE");
                    if (StringUtils.isNotEmpty(attrValue) && attrValue.endsWith("%")) {
                        attrValue = attrValue.substring(0, attrValue.length() - 1);
                        moseSubInfo.put("ATTR_VALUE", attrValue);
                    }
                    if ("ISPREOCCUPY".equals(moseSubInfo.getString("ATTR_CODE", ""))) {
                        String staticDataName = StaticUtil.getStaticValue("IF_CHOOSE_CONFCRM", moseSubInfo.getString("ATTR_VALUE", ""));
                        moseSubInfo.put("ATTR_VALUE", staticDataName);
                    }
                    if ("ROUTEMODE".equals(moseSubInfo.getString("ATTR_CODE", ""))) {
                        String staticDataName = StaticUtil.getStaticValue("ROUTEMODE", moseSubInfo.getString("ATTR_VALUE", ""));
                        moseSubInfo.put("ATTR_VALUE", staticDataName);
                    }
                    datalineInfo.put(moseSubInfo.getString("ATTR_CODE"), moseSubInfo.getString("ATTR_VALUE"));
                }
            }
            infos.add(datalineInfo);

        }
        // 设置专线重派所需最新数据
        SharedCache.set("DATALINE_ATTRINFOS_LIST", infos);
        // 获取公共信息字段
        String subIbsysid = "";
        String acceprstaffid = "";
        String serialno = "";
        String opType = "";
        String opDesc = "";
        String changeMode = "";
        if (IDataUtil.isNotEmpty(pubilcInfo)) {
            IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.querydataLineInfoList", pubilcInfo);
            if (IDataUtil.isNotEmpty(result)) {
                subIbsysid = result.getData(0).getString("SUB_IBSYSID");
                acceprstaffid = result.getData(0).getString("ACCEPT_STAFF_ID");
                serialno = result.getData(0).getString("SERIALNO");
                opType = result.getData(0).getString("OP_TYPE");
                opDesc = result.getData(0).getString("OP_DESC");
                for (Object object1 : result) {
                    IData moseSubInfo = (IData) object1;
                    if ("CHANGEMODE".equals(moseSubInfo.getString("ATTR_CODE", ""))) {
                        changeMode = moseSubInfo.getString("ATTR_VALUE");
                    }
                    pubilcAttrInfo.put(moseSubInfo.getString("ATTR_CODE"), moseSubInfo.getString("ATTR_VALUE"));
                }
            }
        }
        String serialNumber = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "SERIAL_NUMBER", acceprstaffid);
        data.put("SUB_IBSYSID", subIbsysid);
        data.put("PUBLIC_ATTR_LIST", pubilcAttrInfo);
        data.put("SERIALNO", serialno);
        data.put("OP_TYPE", opType);
        data.put("OP_DESC", opDesc);
        data.put("DEAL_TYPE", dealType);
        data.put("CHANGEMODE", changeMode);
        data.put("PRODUCT_NO_ALL", productNoAll);
        data.put("RECORD_NUM_ALL", recodeNumAll);
        pubilcAttrInfo.put("ACCEPT_STAFF_ID", acceprstaffid);
        pubilcAttrInfo.put("ACCEPT_SERIAL_NUMBER", serialNumber);

        // 加载附件
        getAllAttach(subIbsysid);

        String hint = "";
        if ("7010".equals(productId) && "业务保障级别调整".equals(changeMode)) {
            hint = "您受理的VOIP专线变更场景为【业务保障级别调整】,只能对【业务保障等级】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if ("7010".equals(productId) && "减容".equals(changeMode)) {
            hint = "您受理的VOIP专线变更场景为【减容】,只能对【IP带宽】参数进行修改导入，并带宽不能大于当前带宽，如您修改其他或带宽大于当前带宽参数，导入后不会改变！";
        }
        if ("7010".equals(productId) && "异楼搬迁".equals(changeMode)) {
            hint = "您受理的VOIP专线变更场景为【异楼搬迁】,除对【IP带宽，省份】参数不能修改，其他参数都能修改导入，如您修改【IP带宽，省份】，导入后不会改变！";
        }
        if ("7010".equals(productId) && "同楼搬迁".equals(changeMode)) {
            hint = "您受理的VOIP专线变更场景为【同楼搬迁】,除对【IP带宽，省份，业务保障等级】参数不能修改，其他参数都能修改导入，如您修改【IP带宽，省份，业务调整场景】，导入后不会改变！";
        }
        if ("7010".equals(productId) && "扩容".equals(changeMode)) {
            hint = "您受理的VOIP专线变更场景为【扩容】,只能对【IP带宽】参数进行修改导入，并带宽不能大于勘察时的带宽，如您修改其他或带宽大于勘察时的带宽参数，导入后不会改变！";
        }
        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "业务保障级别调整".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【业务保障级别调整】,只能对【业务保障等级】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "IP地址调整".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【IP地址调整】,只能对【IP地址类型，客户申请公网IP地址数，申请公网IPV6地址数，申请公网IPV4地址数，IP地址调整，域名，主域名服务器地址】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "减容".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【减容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于当前带宽，如您修改其他或带宽大于当前带宽参数，导入后不会改变！";
        }
        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "异楼搬迁".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【异楼搬迁】,除对【业务带宽，省份】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份】，导入后不会改变！";
        }
        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "同楼搬迁".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【同楼搬迁】,除对【业务带宽，省份，业务保障等级】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份，业务调整场景】，导入后不会改变！";
        }
        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "扩容".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【扩容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于勘察时的带宽，如您修改其他或带宽大于勘察时的带宽参数，导入后不会改变！";
        }
        if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "业务保障级别调整".equals(changeMode)) {
            hint = "您受理的数据专线变更场景为【业务保障级别调整】,只能对【业务保障等级，路由保护方式】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "减容".equals(changeMode)) {
            hint = "您受理的数据专线变更场景为【减容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于当前带宽，如您修改其他或带宽大于当前带宽参数，导入后不会改变！";
        }
        if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "异楼搬迁".equals(changeMode)) {
            hint = "您受理的数据专线变更场景为【异楼搬迁】,除对【业务带宽，省份】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份】，导入后不会改变！";
        }
        if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "同楼搬迁".equals(changeMode)) {
            hint = "您受理的数据专线变更场景为【同楼搬迁】,除对【业务带宽，省份，业务保障等级】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份，业务调整场景】，导入后不会改变！";
        }
        if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "扩容".equals(changeMode)) {
            hint = "您受理的数据专线变更场景为【扩容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于勘察时的带宽，如您修改其他或带宽大于勘察时的带宽参数，导入后不会改变！";
        }
        
        if ("7016".equals(productId) && "业务保障级别调整".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【业务保障级别调整】,只能对【业务保障等级】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if ("7016".equals(productId) && "IP地址调整".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【IP地址调整】,只能对【客户申请公网IP地址数，域名，主域名服务器地址】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if ("7016".equals(productId) && "减容".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【减容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于当前带宽，如您修改其他或带宽大于当前带宽参数，导入后不会改变！";
        }
        if ("7016".equals(productId) && "异楼搬迁".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【异楼搬迁】,除对【业务带宽，省份】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份】，导入后不会改变！";
        }
        if ("7016".equals(productId) && "同楼搬迁".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【同楼搬迁】,除对【业务带宽，省份，业务保障等级】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份，业务调整场景】，导入后不会改变！";
        }
        if ("7016".equals(productId) && "扩容".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【扩容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于勘察时的带宽，如您修改其他或带宽大于勘察时的带宽参数，导入后不会改变！";
        }
        data.put("HINT", hint);

        // 获取合同信息
        if ("EDIRECTLINEOPENPBOSS".equals(data.getString("BPM_TEMPLET_ID")) || "EVIOPDIRECTLINEOPENPBOSS".equals(data.getString("BPM_TEMPLET_ID"))) {

            // 获取合同附件
            IData inparam = new DataMap();
            inparam.put("IBSYSID", eomsInfos.getData(0).getString("IBSYSID"));
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
                        // info.put("C_FILE_LIST", fileData.toString());
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

        this.setInfos(infos);
        this.setCondition(data);
        this.setPubilcInfo(pubilcAttrInfo);
    }

    public void queryData(IRequestCycle cycle) throws Exception {
        IData data = getData("cond", true);
        IData pagedata = this.getData();

        data.put("CUST_ID", pagedata.getString("CUST_ID"));
        data.put("CUST_NAME", pagedata.getString("CUST_NAME"));
        IDataOutput output = CSViewCall.callPage(this, "SS.GroupPartnerInfoManagerSVC.queryData", data, getPagination("queryNav"));
        setCount(output.getDataCount());
        setInfos(output.getData());
    }

    /**
     * 重派
     * 
     * @throws Exception
     */
    public void checkinWorkSheet(IRequestCycle cycle) throws Exception {
        IData info = this.getData();
        String lengthsNum = info.getString("LENGTHS_SUM");
        int lengthsNums = Integer.parseInt(lengthsNum);
        IDataset attrList = (IDataset) SharedCache.get("DATALINE_INFOS");// 专线信息,获取导入的专线信息

        if (IDataUtil.isEmpty(attrList)) {
            // 批量重派修改区域方式修改
            IDataset infos = (IDataset) SharedCache.get("DATALINE_ATTRINFOS_LIST");
            attrList = builderDataLineInfo(infos, new DatasetList(info.getString("DATALINE_LIST_INFO")), info);

        }
        int dataLineSize = attrList.size();
        if (dataLineSize != lengthsNums) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您导入专线条数与重派的专线条数不一致，请核查！");
        }

        // 重派单
        String serialNo = info.getString("SERIALNO");
        String ibsysid = info.getString("IBSYSID");
        String subIbsysid = info.getString("SUB_IBSYSID");
        String buildingsection = info.getString("BUILDINGSECTION");
        String nodeId = info.getString("NODE_ID");
        String bpmtempletId = info.getString("BPM_TEMPLET_ID");

        // 校验专线名称是否重复
        checkDataNameInfo(attrList, info);

        // IDataset attrList = new DatasetList(info.getString("DATALINE_LIST_ATTR"));// 专线信息
        IData publicAttrInfo = new DataMap(info.getString("PUBLIC_ATTR_LIST"));// 公共信息
        IDataset attachInfos = new DatasetList(info.getString("ATTACH_LIST"));// 附件信息
        IDataset contractAttachInfos = new DatasetList(info.getString("CONTRACTATTACH_LIST"));// 合同信息
        IDataset cfilelist = (IDataset) SharedCache.get("C_FILE_LIST_DATA");// 不修改的合同附件
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

        // 判断合同附件与公共附件是否存在相同名字
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
        IData param = makeRenewParam(bpmtempletId, serialNo, nodeId, ibsysid, subIbsysid, buildingsection, attrList, publicAttrInfo, attachInfos, contractAttachInfos);
        // 记录信息到eop相关表，并重派
        logger.debug("+++++taosx++++: " + param);
        CSViewCall.call(this, "SS.WorkformEomsInteractiveSVC.record", param);

        // 删除代办
        IData upParam = new DataMap();
        upParam.put("RECORDNUM_INFOS", param.getDataset("RECORDNUM_INFOS"));
        upParam.put("BUSIFORM_ID", info.getString("BUSIFORM_ID"));
        upParam.put("BUSIFORM_NODE_ID", info.getString("BUSIFORM_NODE_ID"));
        CSViewCall.call(this, "SS.WorkTaskMgrSVC.updWorkTaskByRecordNum", upParam);

    }

    public IData makeRenewParam(String bpmtempletId, String serialNo, String nodeId, String ibsysid, String subIbsysid, String buildingsection, IDataset attrList, IData publicAttrInfo, IDataset attachInfos, IDataset contractAttachInfos)
            throws Exception {

        IData param = new DataMap();
        IDataset attrListss = new DatasetList();
        IDataset attachInfoss = new DatasetList();
        IDataset eomsInfos = new DatasetList();

        IDataset recordNumberInfos = new DatasetList();

        if (IDataUtil.isNotEmpty(attrList)) {
            for (Object object : attrList) {
                IDataset attrLists = new DatasetList();
                IData attrinfo = (IData) object;
                IData eomsInfo = new DataMap();
                IData data = new DataMap();
                String recordNumber = "";
                String subIbsysId = "";
                String productNo = attrinfo.getString("PRODUCTNO");
                data.put("ATTR_VALUE", productNo);
                data.put("ATTR_CODE", "PRODUCTNO");
                data.put("IBSYSID", ibsysid);
                IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.queryattrByRecordNum", data);
                if (IDataUtil.isNotEmpty(result)) {
                    IData recordNumberInfo = new DataMap();
                    recordNumber = result.getData(0).getString("RECORD_NUM");
                    subIbsysId = result.getData(0).getString("SUB_IBSYSID");
                    recordNumberInfo.put("RECORD_NUM", recordNumber);
                    recordNumberInfos.add(recordNumberInfo);
                }
                Set<String> attrNames = attrinfo.keySet();
                for (String key : attrNames) {
                    IData tp = new DataMap();
                    if ("ISPREOCCUPY".equals(key)) {
                        String staticDataName = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[] { "IF_CHOOSE_CONFCRM", attrinfo.getString(key) });
                        tp.put("ATTR_VALUE", staticDataName);
                        tp.put("ATTR_CODE", key);
                        attrLists.add(tp);
                    } else if ("ROUTEMODE".equals(key)) {
                        String staticDataName = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[] { "ROUTEMODE", attrinfo.getString(key) });
                        tp.put("ATTR_VALUE", staticDataName);
                        tp.put("ATTR_CODE", key);
                        attrLists.add(tp);
                    } else {
                        tp.put("ATTR_VALUE", attrinfo.getString(key));
                        tp.put("ATTR_CODE", key);
                        attrLists.add(tp);
                    }
                }

                eomsInfo.put("IBSYSID", ibsysid);
                eomsInfo.put("SUB_IBSYSID", subIbsysId);
                eomsInfo.put("RECORD_NUM", recordNumber);
                eomsInfo.put("TRADE_DRIECT", "0");
                eomsInfo.put("OPER_TYPE", "renewWorkSheet");
                eomsInfo.put("ATTACHREF", param.getString("attachRef", ""));
                eomsInfo.put("ATTR_INFOS", attrLists);
                eomsInfo.put("SERIALNO", serialNo);
                eomsInfo.put("STATE_TAG", "false");
                eomsInfo.put("TRADE_DRIECT", "0");
                eomsInfos.add(eomsInfo);
            }
            if (IDataUtil.isNotEmpty(publicAttrInfo)) {
                Set<String> attrNames = publicAttrInfo.keySet();
                for (String key : attrNames) {
                    IData tp = new DataMap();
                    if ("BUILDINGSECTION".equals(key)) {
                        tp.put("ATTR_VALUE", buildingsection);
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
                        attachInfo.put("IBSYSID", ibsysid);
                        attachInfo.put("SUB_IBSYSID", subIbsysid);
                        attachInfo.put("RECORD_NUM", "0");
                        attachInfo.put("NODE_ID", nodeId);
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
                        contractInfo.put("IBSYSID", ibsysid);
                        contractInfo.put("SUB_IBSYSID", subIbsysid);
                        contractInfo.put("RECORD_NUM", "0");
                        contractInfo.put("NODE_ID", nodeId);
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
                        datamap.put("IBSYSID", ibsysid);
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
        }

        // 如果没有修改附件，不传资管
        // Iterator<Object> attachDatas = attachInfoss.iterator();
        // while (attachDatas.hasNext()) {
        // IData attachData = (IData) attachDatas.next();
        // IDataset attachDatelist = CSViewCall.call(this, "SS.WorkFormSVC.queryAttachList", attachData);
        // if (IDataUtil.isNotEmpty(attachDatelist)) {
        // attachDatas.remove();
        // }
        //
        // }
        param.put("COMMON_INFOS", attrListss);
        param.put("ATTACH_INFOS", attachInfoss);
        param.put("EOMS_INFOS", eomsInfos);
        param.put("WORKSHEET_EDITE", "MORE");
        param.put("RECORDNUM_INFOS", recordNumberInfos);
        return param;
    }

    public void importFile(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        IDataset datalineInfo = (IDataset) SharedCache.get("DATALINE_INFOS");
        setAjax(datalineInfo);
        this.setCondition(data);

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

        String ibsysid = info.getString("IBSYSID");
        String bpmTempletId = info.getString("BPM_TEMPLET_ID");
        IDataset datalineNameInfos = new DatasetList();
        boolean result = false;
        if ("ERESOURCECONFIRMZHZG".equals(bpmTempletId) || "ECHANGERESOURCECONFIRM".equals(bpmTempletId)) {
            result = true;
        } else if (IDataUtil.isNotEmpty(attrList)) {
            // 校验专线名称是否重复
            for (Object object : attrList) {
                IData attrData = (IData) object;
                String tradeName = attrData.getString("TRADENAME");
                String productNo = attrData.getString("PRODUCTNO");
                IData data = new DataMap();
                data.put("PRODUCTNO", productNo);
                data.put("DATA_LINE_NAME", tradeName);
                datalineNameInfos.add(data);

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
            if (IDataUtil.isNotEmpty(datalineNameInfos)) {
                for (int i = 0; i < datalineNameInfos.size(); i++) {
                    IData dataInfo = datalineNameInfos.getData(i);
                    String dataNameI = dataInfo.getString("DATA_LINE_NAME");
                    String productnoI = dataInfo.getString("PRODUCTNO");
                    if (StringUtils.isBlank(dataNameI) && ("ERESOURCECONFIRMZHZG".equals(bpmTempletId) || "ECHANGERESOURCECONFIRM".equals(bpmTempletId))) {
                        break;
                    } else {
                        for (int s = 0; s < datalineNameInfos.size(); s++) {
                            IData dataInfoS = datalineNameInfos.getData(s);
                            String dataNameS = dataInfoS.getString("DATA_LINE_NAME");
                            String productnoS = dataInfoS.getString("PRODUCTNO");
                            if (i != s && dataNameI.equals(dataNameS)) {
                                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您对应的专线实列号：【" + productnoS + "】与【" + productnoI + "】,输入的专线名称：【" + dataNameI + "】相同，请检查！");
                            }
                        }
                    }

                }

            }
        }
    }

    private IDataset builderDataLineInfo(IDataset infos, IDataset attrList, IData data) throws Exception {

        String productId = data.getString("PRODUCT_ID");
        String changeMode = data.getString("CHANGEMODE");

        for (Object object : attrList) {
            // 处理空值
            IData dataMap = (IData) object;
            if (StringUtils.isBlank(dataMap.getString("PORTAINTERFACETYPE", ""))) {
                dataMap.put("PORTAINTERFACETYPE", "");
            }
            if (StringUtils.isBlank(dataMap.getString("PORTACUSTOM", ""))) {
                dataMap.put("PORTACUSTOM", "");
            }
            if (StringUtils.isBlank(dataMap.getString("TRADENAME", ""))) {
                dataMap.put("TRADENAME", "");
            }
            if (StringUtils.isBlank(dataMap.getString("PREREASON", ""))) {
                dataMap.put("PREREASON", "");
            }
            if (StringUtils.isBlank(dataMap.getString("TRANSFERMODE", ""))) {
                dataMap.put("TRANSFERMODE", "");
            }
            if (StringUtils.isBlank(dataMap.getString("PORTZINTERFACETYPE", "")) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId))) {
                dataMap.put("PORTZINTERFACETYPE", "");
            }
            if (StringUtils.isBlank(dataMap.getString("PORTZCUSTOM", "")) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId))) {
                dataMap.put("PORTZCUSTOM", "");
            }
            if (StringUtils.isBlank(dataMap.getString("ROUTEMODE", ""))) {
                dataMap.put("ROUTEMODE", "");
            }
            if (StringUtils.isBlank(dataMap.getString("ISPREOCCUPY", ""))) {
                dataMap.put("ISPREOCCUPY", "");
            }
        }

        for (int i = 0, len = infos.size(); i < len; i++) {

            IData infoData = infos.getData(i);
            IData datasetData = attrList.getData(i);

            if (infoData.getString("PRODUCTNO", "").equals(datasetData.getString("PRODUCTNO", ""))) {
                if (StringUtils.isNotBlank(changeMode)) {
                    if ("7010".equals(productId) && "业务保障级别调整".equals(changeMode)) {
                        infoData.put("BIZSECURITYLV", datasetData.getString("BIZSECURITYLV"));
                    }
                    if ("7010".equals(productId) && "减容".equals(changeMode)) {
                        int bandWidth = Integer.parseInt(infoData.getString("BANDWIDTH"));// 修改前的带宽
                        int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                        if (bandWidth >= bandWidthS) {
                            infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                        }
                    }
                    if ("7010".equals(productId) && "异楼搬迁".equals(changeMode)) {
                        datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                        datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                        infoData.putAll(datasetData);
                    }
                    if ("7010".equals(productId) && "同楼搬迁".equals(changeMode)) {
                        datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                        datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                        datasetData.put("BIZSECURITYLV", infoData.getString("BIZSECURITYLV"));
                        infoData.putAll(datasetData);
                    }
                    if ("7010".equals(productId) && "扩容".equals(changeMode)) {
                        if (StringUtils.isBlank(infoData.getString("BANDWIDTH"))) {
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您受理的扩容业务，未获取到勘察单带宽，请核查数据！");
                        }
                        int bandWidth = Integer.parseInt(infoData.getString("BANDWIDTH"));// 勘察单带宽
                        int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                        if (bandWidth >= bandWidthS) {
                            infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                        }
                    }
                    if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "业务保障级别调整".equals(changeMode)) {
                        infoData.put("BIZSECURITYLV", datasetData.getString("BIZSECURITYLV"));
                    }
                    if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "IP地址调整".equals(changeMode)) {
                        infoData.put("IPTYPE", datasetData.getString("IPTYPE"));
                        infoData.put("CUSAPPSERVIPV6ADDNUM", datasetData.getString("CUSAPPSERVIPV6ADDNUM"));
                        infoData.put("CUSAPPSERVIPADDNUM", datasetData.getString("CUSAPPSERVIPADDNUM"));
                        infoData.put("CUSAPPSERVIPV4ADDNUM", datasetData.getString("CUSAPPSERVIPV4ADDNUM"));
                        infoData.put("DOMAINNAME", datasetData.getString("DOMAINNAME"));
                        infoData.put("MAINDOMAINADD", datasetData.getString("MAINDOMAINADD"));
                    }
                    if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "减容".equals(changeMode)) {
                        int bandWidth = Integer.parseInt(infoData.getString("BANDWIDTH"));// 修改前的带宽
                        int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                        if (bandWidth >= bandWidthS) {
                            infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                        } else {
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您受理的减容业务，修改的带宽大于原有带宽，请重新修改数据！");
                        }
                    }
                    if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "异楼搬迁".equals(changeMode)) {
                        datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                        datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                        infoData.putAll(datasetData);
                    }
                    if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "同楼搬迁".equals(changeMode)) {
                        datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                        datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                        datasetData.put("BIZSECURITYLV", infoData.getString("BIZSECURITYLV"));
                        infoData.putAll(datasetData);
                    }
                    if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "扩容".equals(changeMode)) {
                        if (StringUtils.isBlank(infoData.getString("BANDWIDTH"))) {
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您受理的扩容业务，未获取到勘察单带宽，请核查数据！");
                        }
                        int bandWidth = Integer.parseInt(infoData.getString("BANDWIDTH"));// 勘察单带宽
                        int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                        if (bandWidth >= bandWidthS) {
                            infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                        } else {
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您受理的扩容业务，修改的带宽大于原有勘察单带宽，请重新修改数据！");
                        }
                    }
                    if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "业务保障级别调整".equals(changeMode)) {
                        infoData.put("ROUTEMODE", datasetData.getString("ROUTEMODE"));
                        infoData.put("BIZSECURITYLV", datasetData.getString("BIZSECURITYLV"));
                    }
                    if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "减容".equals(changeMode)) {
                        int bandWidth = Integer.parseInt(infoData.getString("BANDWIDTH"));// 修改前的带宽
                        int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                        if (bandWidth >= bandWidthS) {
                            infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                        } else {
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您受理的减容业务，修改的带宽大于原有带宽，请重新修改数据！");
                        }
                    }
                    if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "异楼搬迁".equals(changeMode)) {
                        datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                        datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                        datasetData.put("PROVINCEZ", infoData.getString("PROVINCEZ"));
                        infoData.putAll(datasetData);
                    }
                    if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "同楼搬迁".equals(changeMode)) {
                        datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                        datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                        datasetData.put("PROVINCEZ", infoData.getString("PROVINCEZ"));
                        datasetData.put("BIZSECURITYLV", infoData.getString("BIZSECURITYLV"));
                        infoData.putAll(datasetData);
                    }
                    if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "扩容".equals(changeMode)) {
                        if (StringUtils.isBlank(infoData.getString("BANDWIDTH"))) {
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您受理的扩容业务，未获取到勘察单带宽，请核查数据！");
                        }
                        int bandWidth = Integer.parseInt(infoData.getString("BANDWIDTH"));// 勘察单带宽
                        int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                        if (bandWidth >= bandWidthS) {
                            infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                        } else {
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您受理的扩容业务，修改的带宽大于原有勘察单带宽，请重新修改数据！");
                        }
                    }
                } else {
                    infoData.putAll(datasetData);
                }
            }
        }
        return infos;

    }

}
