package com.asiainfo.veris.crm.iorder.web.igroup.esop.myWorkForm;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.EscapeUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class Summarize extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setFileList(IDataset fileList);

    public abstract IDataset getOrders();

    public abstract IData getOrderDetail();

    public abstract void setOrderDetail(IData orderDetail);

    public abstract void setOrders(IDataset orders);

    public abstract void setProductInfos(IDataset productInfos);

    public abstract void setDatalistInfos(IDataset datalistInfos);

    public abstract void setPulictlistInfos(IDataset pulictlistInfos);

    public abstract void setArchiveInfos(IDataset archiveInfos);

    public abstract void setWorkSheet(IDataset worksheet);

    public abstract void setFileInfos(IDataset fileInfos);

    public abstract void setFilePublic(IDataset filePublic);

    private static String[] noShowNodeId = { "4" };// 不显示在页面上的节点类型

    public void queryData(IRequestCycle cycle) throws Exception {

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
            if(!"".equals(workSheet.getData(i).getString("DEAL_STAFF_ID",""))){
            	String staffName = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", workSheet.getData(i).getString("DEAL_STAFF_ID"));
                workSheet.getData(i).put("PERSON", staffName);
            }else{
                workSheet.getData(i).put("PERSON", "");
            }
            
            // 是否是送资管的节点
            String nodeIdInfo = StaticUtil.getStaticValue(getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "RSRV_STR1" }, "PARAMVALUE", new String[] { "DATA_TRANS", workSheet.getData(i).getString("NODE_ID") });
            if (StringUtils.isNotBlank(nodeIdInfo)) {
                workSheet.getData(i).put("FLAG", "1");
            }
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
        dealWorksheet(workSheet);
        setWorkSheet(workSheet);
        // 查询附件
        // IDataset fileset = new DatasetList();
        // if (IS_FINISH.equals("true")) {
        // // 查询已经完成工单的附件信息
        // fileset = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishGroupAttach", data);
        // } else {
        // fileset = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryGroupAttach", data);
        // }
        // // 资管附件分开展示
        // IDataset filesetZf = new DatasetList();
        // if (IDataUtil.isNotEmpty(fileset)) {
        // Iterator<Object> filesetInfos = fileset.iterator();
        // while (filesetInfos.hasNext()) {
        // IData filesetInfo = (IData) filesetInfos.next();
        // if ("Z".equals(filesetInfo.getString("ATTACH_TYPE"))) {
        // filesetZf.add(filesetInfo);
        // filesetInfos.remove();
        // }
        //
        // }
        // }

        // 查询附件
        IDataset fileset = new DatasetList();
        if (IS_FINISH.equals("true")) {
            // 查询已经完成工单的附件信息
            fileset = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishGroupAttach", data);
        } else {
            fileset = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryGroupAttach", data);
        }
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

        data.putAll(groupInfo);
        setInfo(data);
        setCondition(data);

    }

    // 查询工单节点详情
    public void queryNodeDetail(IRequestCycle cycle) throws Exception {

        IData data = getData();

        String is_finish = data.getString("IS_FINISH", "");
        String ibsysid = data.getString("IBSYSID", "");
        String sub_ibsysid = data.getString("SUB_IBSYSID", "");
        String bpm_templet_id = data.getString("BPM_TEMPLET_ID", "");
        String node_desc = data.getString("NODE_DESC", "");
        String node_id = data.getString("NODE_ID", "");
        String record_num = data.getString("RECORD_NUM", "");
        // 取到节点的处理时间，eop_node表里没有处理时间再取这个
        String deal_time = data.getString("DEAL_TIME");

        IDataset orderInfo = new DatasetList();
        // 等待资管回复时多条数据的集合
        IDataset list = new DatasetList();

        /**
         * 1.先判断 node_id 是否是等待资管回复的状态，查配置表td_b_ewe_config 条件：paramname = （模板id）EDIRECTLINEOPENNEW valuedesc = （NODE_ID）waitConfirm1 configname = 'EOMS_INTERNAME' 2.取1的数据的第一条的 paramvalue 作为 paramname的值 加上 configname = 'SHEETTYPE' 条件 取第一条的 paramvalue 值
         * 得到sheettype 3 在tf_b_eop_eoms t 中 条件： ibsysid + record_num + sheettype 一条sub_ibsysid ---多条oper_type group_seq 4. tf_b_eop_attr ：最后根据sub_ibsysid + group_seq 查询属性
         */
        IData datatmep = new DataMap();

        datatmep.put("CONFIGNAME", "EOMS_INTERNAME");
        datatmep.put("PARAMNAME", bpm_templet_id);
        datatmep.put("VALUEDESC", node_id);

        IDataset eomsdata = qryeomsdata(datatmep, is_finish, ibsysid, record_num);

        IData dataval = new DataMap();

        dataval.put("IBSYSID", ibsysid);
        dataval.put("RECORD_NUM", record_num);
        dataval.put("NODE_ID", node_id);

        IDataset eomsStateInfos = new DatasetList();
        // 是否是送资管的节点，EOMS表存在数据
        String flag = "";
        String nodeIdInfo = StaticUtil.getStaticValue(getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "RSRV_STR1" }, "PARAMVALUE", new String[] { "DATA_TRANS", node_id });
        if (StringUtils.isNotBlank(nodeIdInfo)) {
            flag = "1";
        }
        IData listdataA = new DataMap();
        listdataA.put("IBSYSID", ibsysid);
        eomsStateInfos = CSViewCall.call(this, "SS.WorkFormSVC.qryEomsStateInfos", listdataA);

        IDataset datalistInfos = new DatasetList();// 当前节点专线派单信息
        IDataset pulictlistInfos = new DatasetList();// 当前节点公共信息
        if (IDataUtil.isNotEmpty(eomsStateInfos)) {
            for (Object object : eomsStateInfos) {
                IData map = new DataMap();
                IData recordNumInfo = (IData) object;
                recordNumInfo.put("IBSYSID", ibsysid);
                recordNumInfo.put("NODE_ID", node_id);
                recordNumInfo.put("SUB_IBSYSID", sub_ibsysid);
                recordNumInfo.put("FLAG", flag);
                if (!"0".equals(recordNumInfo.getString("RECORD_NUM"))) {
                    IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.querydataLineAttrInfoList", recordNumInfo);
                    if (IDataUtil.isNotEmpty(result)) {
                        // 对属性进行转译
                        translationData(result);
                        // 获取ATTRNAME可能全部为空
                        if (IDataUtil.isNotEmpty(result)) {
                            map.put("LIST", result);
                            map.put("TITLE", "专线" + recordNumInfo.getString("RECORD_NUM"));
                            datalistInfos.add(map);
                        }
                    }
                } else {
                    IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.querydataLineAttrInfoList", recordNumInfo);
                    if (IDataUtil.isNotEmpty(result)) {
                        // 对属性进行转译
                        pulictlistInfos = translationData(result);
                    }
                }
            }
        }
        setDatalistInfos(datalistInfos);
        setPulictlistInfos(pulictlistInfos);
        // 获取审核客户经理确认信息
        if (IDataUtil.isEmpty(pulictlistInfos)) {
            IData archiveInfo = new DataMap();
            archiveInfo.put("IBSYSID", ibsysid);
            archiveInfo.put("NODE_ID", node_id);
            IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.queryEopotherInfos", archiveInfo);
            if (IDataUtil.isNotEmpty(result)) {
                result = translationData(result);
            }
            setArchiveInfos(result);
        }
        // 查询轨迹详情
        IDataset orderDetail = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishWorkformNodeByIbsysid", dataval);
        if (IDataUtil.isNotEmpty(orderDetail)) {
            String dealnode_time = orderDetail.getData(0).getString("DEAL_TIME");
            if (StringUtils.isBlank(dealnode_time)) {
                orderDetail.getData(0).put("DEAL_TIME", deal_time);
            }
            orderDetail.getData(0).put("NODE_DESC", node_desc);
            setOrderDetail(orderDetail.getData(0));
        } else if ("1".equals(flag)) {// 送资管节点，拼轨迹详情
            IData orderMap = new DataMap();
            String staffId = data.getString("PERSON");
            if (StringUtils.isBlank(staffId) || "undefined".equals(staffId)) {
                flag = "";
            } else {
                String staffName = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
                String serialNumber = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "SERIAL_NUMBER", staffId);
                String eparchyCode = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "EPARCHY_CODE", staffId);
                orderMap.put("NODE_DESC", "资管受理");
                orderMap.put("STAFF_ID", staffId);
                orderMap.put("STAFF_NAME", staffName);
                orderMap.put("STAFF_PHONE", serialNumber);
                orderMap.put("DEAL_TIME", data.getString("DEAL_TIME"));
                orderMap.put("EPARCHY_CODE", eparchyCode);
                orderMap.put("DEPART_NAME", "");
                setOrderDetail(orderMap);
            }
        }

        boolean detailFlag = false;
        boolean orderFlag = false;

        IDataset orders = getOrders();
        if (DataUtils.isEmpty(orders)) {
            orderFlag = true;
        } else {
            for (int i = 0; i < orders.size(); i++) {
                IData order = orders.getData(i);
                IDataset orderList = order.getDataset("list");
                if (DataUtils.isEmpty(orderList)) {
                    orderFlag = true;
                }
            }
        }
        IData orderDetailS = getOrderDetail();
        if (DataUtils.isEmpty(orderDetailS)) {
            detailFlag = true;
        }

        IData json = new DataMap();
        if (detailFlag && orderFlag && !"1".equals(flag)) {
            // 如果是通知等待节点，查询产品表数据
            boolean productFlag = dealProductInfos(data);
            if (productFlag) {
                json.put("OPEN_FLAG", "true");
            } else {
                json.put("OPEN_FLAG", "false");
            }
        } else {
            json.put("OPEN_FLAG", "true");
        }

        this.setAjax(json);
    }

    /**
     * 通过opertype作为paramname EOMS_BUSI_STATE_拼接 sheettype 查询valueDesc
     * 
     * @param opertype
     * @return
     * @throws Exception
     */
    public String qryEomsTitle(String opertype, String sheettype) throws Exception {

        if (StringUtils.isBlank(opertype) || StringUtils.isBlank(sheettype)) {
            return "";
        } else {
            // 拼接EOMS_BUSI_STATE_和sheettype
            StringBuilder sheettypes = new StringBuilder(sheettype);
            StringBuilder eomsstate = new StringBuilder("EOMS_BUSI_STATE_");
            StringBuilder eoms_sheettype = eomsstate.append(sheettypes);
            String configname = eoms_sheettype.toString();

            // 查询valuedesc
            IData eomsdata = new DataMap();
            eomsdata.put("CONFIGNAME", configname);
            eomsdata.put("PARAMNAME", opertype);

            IDataset dataset = CSViewCall.call(this, "SS.IsspConfigQrySVC.getParamValue", eomsdata);

            if (DataUtils.isNotEmpty(dataset)) {
                String title = dataset.getData(0).getString("VALUEDESC");
                return title;
            } else {
                return "";
            }
        }
    }

    // 流程图查询
    public void intiLiuCheng(IRequestCycle cycle) throws Exception {

        IData data = getData();

        String bpm_templer_id = data.getString("BPM_TEMPLET_ID", "");
        String ibsysid = data.getString("IBSYSID", "");
        String is_finish = data.getString("is_finish", "");

        if (StringUtils.isEmpty(is_finish)) {
            is_finish = "false";
        }

        IData charInfo = new DataMap();
        charInfo.put("BPM_TEMPLET_ID", bpm_templer_id);
        charInfo.put("IBSYSID", ibsysid);
        charInfo.put("IS_HIS", is_finish);

        IData resultCharInfo = CSViewCall.callone(this, "SS.WorkformChartSVC.dealWorkformChart", charInfo);

        String xml = resultCharInfo.getString("XML_INFO", "");
        charInfo.put("XML_INFO", xml);
        this.setAjax(charInfo);

    }

    /**
     * 查询是否是等待资管回复环节
     * 
     * @param datatmep
     * @param is_finish
     * @param ibsysid
     * @param record_num
     * @return
     * @throws Exception
     */
    public IDataset qryeomsdata(IData datatmep, String is_finish, String ibsysid, String record_num) throws Exception {

        IDataset eomsdata = new DatasetList();

        IDataset valueset = CSViewCall.call(this, "SS.IsspConfigQrySVC.getParamValueByDesc", datatmep);

        if (DataUtils.isNotEmpty(valueset)) {
            // 从集合中取得paramvalue用于查PARAMNAME是sheettype的值
            datatmep.put("CONFIGNAME", "SHEETTYPE");
            datatmep.put("PARAMNAME", valueset.getData(0).getString("PARAMVALUE"));

            IDataset sheets = CSViewCall.call(this, "SS.IsspConfigQrySVC.getParamValue", datatmep);

            String sheettype = "";
            if (DataUtils.isNotEmpty(sheets)) {
                // 取得sheettype 值一般是：31
                sheettype = sheets.getData(0).getString("PARAMVALUE");
            }

            // 查询tf_b_eop_eoms 表
            if (StringUtils.isNotBlank(sheettype)) {
                datatmep.put("IBSYSID", ibsysid);
                datatmep.put("RECORD_NUM", record_num);
                datatmep.put("SHEETTYPE", sheettype);
                if (is_finish.equals("true")) {
                    // 已完成的查tf_bh_eop_eoms
                    eomsdata = CSViewCall.call(this, "SS.WorkformEomsSVC.qryFinishEOMSByIbsysidRecordNumSheettype", datatmep);
                } else {
                    // 未完成的查tf_b_eop_eoms表
                    eomsdata = CSViewCall.call(this, "SS.WorkformEomsSVC.qryworkformEOMSByIbsysidRecordNumSheettype", datatmep);
                }
                if (DataUtils.isNotEmpty(eomsdata)) {
                    eomsdata.getData(0).put("SHEETTYPE", sheettype);
                }
            }

        }
        return eomsdata;
    }

    private void otherDeal(IDataset otherInfos) throws Exception {
        if (DataUtils.isEmpty(otherInfos)) {
            return;
        }

        for (int i = 0; i < otherInfos.size(); i++) {
            IData otherInfo = otherInfos.getData(i);
            String attrCode = otherInfo.getString("ATTR_CODE", "");
            String attrValue = otherInfo.getString("ATTR_VALUE", "");
            if ("AUDIT_OPTION".equals(attrCode)) {
                String newValue = StaticUtil.getStaticValue("HOLDPOLICYAPPROVE_APPRRESULT", attrValue);
                otherInfo.put("ATTR_VALUE", newValue);
            }
        }
    }

    private boolean dealProductInfos(IData data) throws Exception {
        boolean falg = false;
        IData param = new DataMap();
        param.put("BPM_TEMPLET_ID", data.getString("BPM_TEMPLET_ID", ""));
        param.put("NODE_ID", data.getString("NODE_ID", ""));
        String is_finish = data.getString("IS_FINISH", "");
        param.put("VALID_TAG", "0");
        String ibsysid = data.getString("IBSYSID", "");
        IDataset dataset = CSViewCall.call(this, "SS.QryFlowNodeDescSVC.qryNodeDescByTempletId", param);
        if (DataUtils.isEmpty(dataset)) {
            return falg;
        }
        if ("1".equals(dataset.first().getString("NODE_TYPE", ""))) {
            return falg;
        }
        if (is_finish.equals("true")) {
            IData temp = new DataMap();
            temp.put("IBSYSID", ibsysid);
            temp.put("IS_FINISH", "true");
            IDataset productInfos = CSViewCall.call(this, "SS.WorkformProductSVC.qryProductByIbsysid", temp);
            if (DataUtils.isNotEmpty(productInfos)) {
                setProductInfos(productInfos);
                falg = true;
            }
        } else {
            IData temp = new DataMap();
            temp.put("IBSYSID", ibsysid);
            IDataset productInfos = CSViewCall.call(this, "SS.WorkformProductSVC.qryProductByIbsysid", temp);
            if (DataUtils.isNotEmpty(productInfos)) {
                setProductInfos(productInfos);
                falg = true;
            }
        }

        return falg;
    }

    private void dealWorksheet(IDataset sheets) throws Exception {
        for (int i = sheets.size() - 1; i >= 0; i--) {
            IData sheet = sheets.getData(i);
            String nodeId = sheet.getString("NODE_ID", "");
            String bpmTempletId = sheet.getString("BPM_TEMPLET_ID", "");
            IData param = new DataMap();
            param.put("NODE_ID", nodeId);
            param.put("BPM_TEMPLET_ID", bpmTempletId);
            param.put("VALID_TAG", "0");
            IDataset nodeInfos = CSViewCall.call(this, "SS.QryFlowNodeDescSVC.qryNodeDescByTempletId", param);
            if (DataUtils.isEmpty(nodeInfos)) {
                continue;
            }
            String nodeType = nodeInfos.first().getString("NODE_TYPE", "");
            for (int j = 0; j < noShowNodeId.length; j++) {
                if (nodeType.equals(noShowNodeId[j])) {
                    sheets.remove(sheet);
                }
            }
        }
    }

    public void queryDatelineAttrQueryData(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        CSViewCall.call(this, "SS.WorkFormSVC.queryDatelineAttr", data);
    }

    public IDataset translationData(IDataset result) throws Exception {
        Iterator<Object> results = result.iterator();
        while (results.hasNext()) {
            IData resultsInfo = (IData) results.next();
            if (StringUtils.isBlank(resultsInfo.getString("ATTR_NAME"))) {
                results.remove();
            } else if ("DIRECTLINE_SCOPE".equals(resultsInfo.getString("ATTR_CODE"))) {
                results.remove();
            } else if ("QUICKADDRA".equals(resultsInfo.getString("ATTR_CODE"))) {
                results.remove();
            } else if ("QUICKADDRZ".equals(resultsInfo.getString("ATTR_CODE"))) {
                results.remove();
            } else if ("ISCOVER".equals(resultsInfo.getString("ATTR_CODE"))) {
                results.remove();
            } else if ("IS_MODIFY_TAG".equals(resultsInfo.getString("ATTR_CODE"))) {
                results.remove();
            } else if ("TRADENAMEOLD".equals(resultsInfo.getString("ATTR_CODE"))) {
                results.remove();
            } else if ("TRADEID".equals(resultsInfo.getString("ATTR_CODE"))) {
                results.remove();
            } else if ("PRODUCTNO".equals(resultsInfo.getString("ATTR_CODE"))) {
                results.remove();
            } else {
                if ("BUSINESSTYPE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("CONTRACT_POATT_TYPE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("BUILDINGSECTION".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("BUILDING_SECTION", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("CHANGEMODE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("CHANGEMODE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("BIZRANGE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("BIZRANGE_LIST", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("ISCUSTOMERPE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("ISCUSTOMERPE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("C_POATT_TYPE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("CONTRACT_POATT_TYPE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("C_CONTRACT_IS_AUTO_RENEW".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("CONTRACT_IS_AUTO_RENEW", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("C_CONTRACT_AUTO_RENEW_CYCLE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("CONTRACT_SS_AUTO_RENEW_CYCLE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("C_CONTRACT_TYPE_CODE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("CONTRACT_TYPE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("C_CONTRACT_WRITE_TYPE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("GROUPCONTRACT_WRITETYPE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("C_CONTRACT_WRITE_CITY".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("GLOBAL_PROVINCE_CODE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("C_CONTRACT_STATE_CODE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("CONTRACT_STATE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("ACCEPTTANCE_PERIOD".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("ACCEPTANCEDATE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("SERVICETYPE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("RESOURCECONFIRM_SERVICETYPE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("RENTOUTTYPE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("RENTOUTTYPE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("IF_CHOOSE_CONFCRM".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("IF_CHOOSE_CONFCRM", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("URGENCY_LEVEL".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("URGENCY_LEVEL", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("LINEOPENTAG".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("LINE_OPEN_FLAG", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("IPTYPE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("IP_TYPE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("NOTIN_MARKETING_TAG".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("CONTRACT_IS_AUTO_RENEW", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("CUSTOMERDEVICEMODE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("CUSTOMER_DEVICE_MODEL_LIST", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("C_IF_NEW_CONTRACT".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("IF_NEW_CONTRACT", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("C_CONTRACT_LEVEL".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("GROUPCONTRACT_CONTRACTLEVEL", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("IF_NEW_PROJECTNAME".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("IF_NEW_CONTRACT", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("ACCT_DEAL".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("IF_CHOOSE_ACCTCRM", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("ISPREOCCUPY".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("IF_CHOOSE_CONFCRM", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("ROUTEMODE".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("ROUTEMODE", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("AUDIT_RESULT".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("EOP_AUDIT_RESULT", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
                if ("AUDIT_OPTION".equals(resultsInfo.getString("ATTR_CODE"))) {
                    String staticDataName = StaticUtil.getStaticValue("EOP_AUDIT_RESULT", resultsInfo.getString("ATTR_VALUE", ""));
                    resultsInfo.put("ATTR_VALUE", staticDataName);
                }
            }

        }

        return result;
    }

}
