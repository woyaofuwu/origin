package com.asiainfo.veris.crm.iorder.web.igroup.esop.myWorkForm;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class DatelineRemoveOne extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setDatalineInfos(IDataset datalineInfos);

    public abstract void setDatalineInfo(IData datalineInfo);

    public void queryData(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        IDataset eomsInfos = CSViewCall.call(this, "SS.WorkFormSVC.datelineRemoveOne", data);
        IDataset datalineAttrInfo = CSViewCall.call(this, "SS.WorkFormSVC.queryEomsStateInfo", data);

        int datalineSize = datalineAttrInfo.size();
        if (IDataUtil.isEmpty(datalineAttrInfo)) {
            data.put("STATE_INFO", "false");
        } else {
            String sheetType = datalineAttrInfo.getData(0).getString("SHEETTYPE");
            if ("31".equals(sheetType) || "35".equals(sheetType)) {
                data.put("FALSE_TYPE", "false");
            }
            // 简单场景变更，不是同楼搬迁的业务场景都只能整单撤单
            String bptpml = data.getString("BPM_TEMPLET_ID");
            if ("DIRECTLINECHANGESIMPLE".equals(bptpml)) {
                data.put("ATTR_CODE", "CHANGEMODE");
                IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.querydataLineChangemode", data);
                if (IDataUtil.isNotEmpty(result)) {
                    String changeMode = result.first().getString("ATTR_VALUE");
                    if (!"同楼搬迁".equals(changeMode)) {
                        data.put("FALSE_TYPE", "false");
                    }
                }
            }

        }

        data.put("DATALINE_SIZE", datalineSize);
        setDatalineInfos(datalineAttrInfo);
        setInfo(eomsInfos.getData(0));
        setCondition(data);
    }

    public void datelineRemoveOne(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        String ibsysid = data.getString("IBSYSID");
        String remark = data.getString("REMARK");
        String busiFormId = data.getString("BUSIFORM_ID");
        String sheetType = data.getString("SHEETTYPE");
        String dealResult = data.getString("DEAL_RESULT");//
        String bpmTempletId = data.getString("BPM_TEMPLET_ID");//
        IDataset eomsList = new DatasetList(data.getString("DATALINE_LIST"));
        int eomsListSize = eomsList.size();// 勾选的撤单条数
        int datalineSize = Integer.parseInt(data.getString("DATALINE_SIZE"));// 工单的全部专线数
        String flag = "false";// 标记全部勾选且为未派单直接结束主流程
        if (!"34".equals(sheetType) && IDataUtil.isNotEmpty(eomsList)) {
            IData inData = new DataMap();
            IDataset productAllInfos = new DatasetList();
            for (Object object : eomsList) {
                IData productAll = new DataMap();
                IData eomsInfo = (IData) object;
                String busiType = eomsInfo.getString("BUSI_STATE");
                String subBusiFormId = eomsInfo.getString("SUB_BUSIFORM_ID");
                String recordNum = eomsInfo.getString("RECORD_NUM");
                String productNo = eomsInfo.getString("PRODUCT_NO");
                if ("-1".equals(busiType) && datalineSize > eomsListSize) {// 开通或变更单没有全部勾选撤单数，且为未派单状态，则只结束子流程
                    // 调用勘察单解绑接口,只支持专线开通与变更流程
                    if ("EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGEPBOSS".equals(bpmTempletId)) {
                        IData dataMap = new DataMap();
                        dataMap.put("PRODUCTNO", productNo);
                        dataMap.put("STATE", "A");
                        CSViewCall.call(this, "SS.WorkFormSVC.updateSurveyUnbundie", dataMap);
                    }
                    IData inDatas = new DataMap();
                    inDatas.put("BUSIFORM_ID", subBusiFormId);
                    inDatas.put("STATE", "4");
                    inDatas.put("MOVE_ESOP", "false");
                    CSViewCall.call(this, "SS.WorkformMoveHisSVC.history", inDatas);

                    // 修改state表状态并存撤单原因
                    IData param = new DataMap();
                    IDataset eomsInfoss = new DatasetList();
                    IDataset attrLists = new DatasetList();// 拼专线信息
                    IData attrInfo = new DataMap();
                    attrInfo.put("ATTR_CODE", "DEAL_RESULT");
                    attrInfo.put("ATTR_VALUE", dealResult);
                    attrLists.add(attrInfo);
                    eomsInfo.put("IBSYSID", ibsysid);
                    eomsInfo.put("RECORD_NUM", recordNum);
                    eomsInfo.put("OPER_TYPE", "cancelWorkSheet");
                    eomsInfo.put("TRADE_DRIECT", "0");
                    eomsInfo.put("ATTR_INFOS", attrLists);
                    eomsInfoss.add(eomsInfo);
                    param.put("EOMS_INFOS", eomsInfoss);
                    // 调修改表状态接口
                    CSViewCall.call(this, "SS.WorkformEomsInteractiveSVC.record", param);

                } else if ("-1".equals(busiType) && datalineSize == eomsListSize) {// 开通，变更，勘察单全部勾选，且为未派单状态，则直接结束主流程

                    // 调用勘察单解绑接口,只支持专线开通与变更流程
                    if ("EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGEPBOSS".equals(bpmTempletId)) {
                        IData dataMap = new DataMap();
                        dataMap.put("PRODUCTNO", productNo);
                        dataMap.put("STATE", "A");
                        CSViewCall.call(this, "SS.WorkFormSVC.updateSurveyUnbundie", dataMap);
                    }
                    if ("false".equals(flag)) {
                        IData inDatas = new DataMap();
                        inDatas.put("BUSIFORM_ID", busiFormId);
                        inDatas.put("STATE", "4");
                        CSViewCall.call(this, "SS.WorkformMoveHisSVC.history", inDatas);
                        flag = "true";
                    }
                    // 修改state表状态并存撤单原因
                    IData param = new DataMap();
                    IDataset eomsInfoss = new DatasetList();
                    IDataset attrLists = new DatasetList();// 拼专线信息
                    IData attrInfo = new DataMap();
                    attrInfo.put("ATTR_CODE", "DEAL_RESULT");
                    attrInfo.put("ATTR_VALUE", dealResult);
                    attrLists.add(attrInfo);
                    eomsInfo.put("TRADE_DRIECT", "0");
                    eomsInfo.put("ATTR_INFOS", attrLists);
                    eomsInfo.put("OPER_TYPE", "cancelWorkSheet");
                    eomsInfoss.add(eomsInfo);
                    param.put("EOMS_INFOS", eomsInfoss);
                    param.put("CANCLE_TAG", "true");
                    // 调修改表状态接口
                    CSViewCall.call(this, "SS.WorkformEomsInteractiveSVC.record", param);
                } else {
                    productAll.put("ProductNO", eomsInfo.getString("PRODUCT_NO"));
                    productAllInfos.add(productAll);
                }

            }
            if (IDataUtil.isNotEmpty(productAllInfos)) {
                inData.put("serialNo", eomsList.getData(0).getString("SERIALNO"));
                inData.put("OPER_TYPE", "cancelWorkSheet");
                inData.put("opType", "专线主动撤单");
                inData.put("opDesc", remark);
                inData.put("IBSYSID", ibsysid);
                inData.put("CANCEL_INFOS", productAllInfos);
                inData.put("DEAL_RESULT", dealResult);
                inData.put("OPPERSON", getVisit().getStaffId());
                inData.put("OPDEPART", getVisit().getDepartId());
                inData.put("OPCONTACT", getVisit().getSerialNumber());
                inData.put("OPTIME", SysDateMgr.getSysTime());
                CSViewCall.call(this, "SS.EweForCrmSVC.dealCRMData", inData);
            }

        } else if (IDataUtil.isNotEmpty(eomsList) && "34".equals(sheetType)) {

            for (int i = 0; i < eomsList.size(); i++) {
                IData eomsInfo = eomsList.getData(i);
                IDataset productAllInfos = new DatasetList();
                IData productAll = new DataMap();
                String busiType = eomsInfo.getString("BUSI_STATE");
                String subBusiFormId = eomsInfo.getString("SUB_BUSIFORM_ID");
                if ("-1".equals(busiType)) {// 拆机单为未派单状态，结束子流程
                    IData inDatas = new DataMap();
                    inDatas.put("BUSIFORM_ID", subBusiFormId);
                    inDatas.put("STATE", "4");
                    inDatas.put("MOVE_ESOP", "false");
                    CSViewCall.call(this, "SS.WorkformMoveHisSVC.history", inDatas);

                    // 修改state表状态并存撤单原因
                    IData param = new DataMap();
                    IDataset eomsInfoss = new DatasetList();
                    IDataset attrLists = new DatasetList();// 拼专线信息
                    IData attrInfo = new DataMap();
                    attrInfo.put("ATTR_CODE", "DEAL_RESULT");
                    attrInfo.put("ATTR_VALUE", dealResult);
                    attrLists.add(attrInfo);
                    eomsInfo.put("TRADE_DRIECT", "0");
                    eomsInfo.put("ATTR_INFOS", attrLists);
                    eomsInfo.put("OPER_TYPE", "cancelWorkSheet");
                    eomsInfoss.add(eomsInfo);
                    param.put("EOMS_INFOS", eomsInfoss);
                    param.put("CANCLE_TAG", "false");
                    // 调修改表状态接口
                    CSViewCall.call(this, "SS.WorkformEomsInteractiveSVC.record", param);
                } else {
                    productAll.put("ProductNO", eomsInfo.getString("PRODUCT_NO"));
                    productAllInfos.add(productAll);
                    IData inData = new DataMap();
                    inData.put("serialNo", eomsInfo.getString("SERIALNO"));
                    inData.put("OPER_TYPE", "cancelWorkSheet");
                    inData.put("opType", "专线主动撤单");
                    inData.put("opDesc", remark);
                    inData.put("IBSYSID", ibsysid);
                    inData.put("DEAL_RESULT", dealResult);
                    inData.put("CANCEL_INFOS", productAllInfos);
                    inData.put("OPPERSON", getVisit().getStaffId());
                    inData.put("OPDEPART", getVisit().getDepartId());
                    inData.put("OPCONTACT", getVisit().getSerialNumber());
                    inData.put("OPTIME", SysDateMgr.getSysTime());
                    CSViewCall.call(this, "SS.EweForCrmSVC.dealCRMData", inData);

                }
                //
                // if (i + 1 == datalineSize) {
                // IData inDatas = new DataMap();
                // inDatas.put("BUSIFORM_ID", busiFormId);
                // inDatas.put("STATE", "4");
                // CSViewCall.call(this, "SS.WorkformMoveHisSVC.history", inDatas);
                // }

            }
        } else {
            // 调用勘察单解绑接口,只支持专线开通与变更流程
            if ("EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGEPBOSS".equals(bpmTempletId)) {

                IData listdataA = new DataMap();
                listdataA.put("IBSYSID", ibsysid);
                IDataset eomsStateInfos = CSViewCall.call(this, "SS.WorkFormSVC.qryEomsStateInfos", listdataA);
                if (IDataUtil.isNotEmpty(eomsStateInfos)) {
                    for (Object object : eomsStateInfos) {
                        IData recordNumInfo = (IData) object;
                        recordNumInfo.put("IBSYSID", ibsysid);
                        recordNumInfo.put("FLAG", flag);
                        if (!"0".equals(recordNumInfo.getString("RECORD_NUM"))) {
                            IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.querydataLineAttrInfoList", recordNumInfo);
                            if (IDataUtil.isNotEmpty(result)) {
                                for (Object object2 : result) {
                                    IData attrInfo = (IData) object2;
                                    if ("PRODUCTNO".equals(attrInfo.getString("ATTR_CODE", ""))) {
                                        if (StringUtils.isNotBlank(attrInfo.getString("ATTR_VALUE"))) {
                                            IData dataMap = new DataMap();
                                            dataMap.put("PRODUCTNO", attrInfo.getString("ATTR_VALUE", ""));
                                            dataMap.put("STATE", "A");
                                            CSViewCall.call(this, "SS.WorkFormSVC.updateSurveyUnbundie", dataMap);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
            IData inData = new DataMap();
            inData.put("BUSIFORM_ID", busiFormId);
            inData.put("STATE", "4");
            CSViewCall.call(this, "SS.WorkformMoveHisSVC.history", inData);
        }

    }
}
