package com.asiainfo.veris.crm.order.soa.group.esop.workform;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateBean;

public class WorkFormSVC extends GroupOrderService {

    private static final long serialVersionUID = 1L;

    /**
     * 查询EOMS_BUSI_STATE状态编码
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getWorkfromEoms(IData param) throws Exception {
        String ibsysId = param.getString("IBSYSID", "");
        return WorkFormBean.getWorkfromEoms(ibsysId);
    }

    /**
     * 查询专线工单状态
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRenewWorkSheet(IData param) throws Exception {
        String ibsysId = param.getString("IBSYSID", "");
        String configName = param.getString("CONFIGNAME", "");
        IDataset workSeehtInfos = WorkFormBean.getRenewWorkSheet(ibsysId);
        if (IDataUtil.isNotEmpty(workSeehtInfos)) {
            for (Object object : workSeehtInfos) {
                IData workSeehtInfo = (IData) object;
                String busiState = workSeehtInfo.getString("BUSI_STATE");
                IDataset configInfos = WorkFormBean.queryEweConfigInfo(busiState, configName);
                if (IDataUtil.isNotEmpty(configInfos)) {
                    IData configInfo = configInfos.getData(0);
                    workSeehtInfo.put("PARAMNAME", configInfo.getString("PARAMNAME"));
                    workSeehtInfo.put("VALUEDESC", configInfo.getString("VALUEDESC"));
                    workSeehtInfo.put("PARAMVALUE", configInfo.getString("PARAMVALUE"));
                    workSeehtInfo.put("EOMS_OPDESC", workSeehtInfo.getString("ATTR_VALUE"));
                }
            }

        }
        // 如果存在驳回单，只显示驳回，不存在则显示全部
        IDataset workSeehtList = new DatasetList();
        Iterator<Object> workSeehtInfoss = workSeehtInfos.iterator();
        while (workSeehtInfoss.hasNext()) {
            IData workSeehtInfo = (IData) workSeehtInfoss.next();
            String busiState = workSeehtInfo.getString("BUSI_STATE");
            if ("P".equals(busiState)) {
                workSeehtList.add(workSeehtInfo);
            }

        }
        if (IDataUtil.isNotEmpty(workSeehtList)) {
            return workSeehtList;
        } else {
            return workSeehtInfos;
        }
    }

    /**
     * 查询专线主题等
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getWorkfromProductAttr(IData param) throws Exception {
        return WorkFormBean.getWorkfromProductAttr(param);
    }

    /**
     * 查询批量重派专线信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querydataLineInfoList(IData param) throws Exception {
        return WorkFormBean.querydataLineInfoList(param);
    }

    /**
     * 查询集团归属专线信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryDatalineTradeAttrInfos(IData param) throws Exception {
        IDataset relauus = new DatasetList();
        String productId = param.getString("PRODUCT_ID", "");
        String userIdA = param.getString("USER_ID", "");
        // 根据user_id查询账户
        IData acctInfoA = UcaInfoQry.qryDefaultPayRelaByUserId(userIdA);
        if (IDataUtil.isEmpty(acctInfoA)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID" + userIdA + "查询默认付费账户为空！");
        }
        String acctIdA = acctInfoA.getString("ACCT_ID");
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
        IDataset relauuInfo = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, relationTypeCode);
        if (IDataUtil.isEmpty(relauuInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID" + userIdA + "查询无有效专线！");
        }
        if (IDataUtil.isNotEmpty(relauuInfo)) {
            for (Object relauu : relauuInfo) {
                IData lineInfo = new DataMap();
                IData relauuInfos = (IData) relauu;
                String userIdB = relauuInfos.getString("USER_ID_B");
                String serialNum = relauuInfos.getString("SERIAL_NUMBER_B");
                lineInfo.put("SERIAL_NUMBER_B", serialNum);
                IData input = new DataMap();
                input.put("USER_ID", userIdB);
                IDataset lineInfos = CSAppCall.call("SS.QcsGrpIntfSVC.getProductInfoForPboss", input);
                // IDataset lineInfos = WorkFormBean.qryDatalineTradeAttrInfos(userIdB);
                if (IDataUtil.isNotEmpty(lineInfos)) {
                    for (Object line : lineInfos) {
                        IData lines = (IData) line;
                        lineInfo.put("START_DATE", lines.getString("START_DATE"));
                        lineInfo.put("END_DATE", lines.getString("END_DATE"));
                        lineInfo.put("TITLE", lines.getString("RSRV_STR5"));

                        lineInfo.put("PRODUCTNO", lines.getString("PRODUCT_NO"));

                        lineInfo.put("USER_ID_B", userIdB);
                    }
                }
                // 根据user_id查询账户
                /* IData acctInfoB = UcaInfoQry.qryDefaultPayRelaByUserId(userIdB); if (IDataUtil.isEmpty(acctInfoB)) { CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID" + userIdB + "查询默认付费账户为空！"); } String acctIdB =
                 * acctInfoB.getString("ACCT_ID"); if (!acctIdB.equals(acctIdA)) { relauus.add(lineInfo); } */
                relauus.add(lineInfo);
            }
        }
        /* if (IDataUtil.isEmpty(relauus)) { CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID" + userIdA + "查询无可变更专线,请至专线账户管理界面进行分账再过户!"); } */
        return relauus;

    }

    /**
     * 查询异常工单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getExpWorkForms(IData param) throws Exception {
        return WorkFormBean.getExpWorkForms(param, getPagination());
    }

    /**
     * 跨省平均处理时长
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryStmtDirect(IData param) throws Exception {
        String paramName = param.getString("OPER_TYPE", "");
        // 根据 td_b_ewe_config 表获取对应的流程节点ID
        IDataset configInfos = WorkFormBean.queryStmtDirect(paramName);
        IDataset eopNodeInfo = new DatasetList();
        if (IDataUtil.isNotEmpty(configInfos)) {
            for (Object object : configInfos) {
                IData configInfo = (IData) object;
                String configName = configInfo.getString("CONFIGNAME");
                // 根据流程节点ID查询 tf_b_eop_subscribe（在途）
                IDataset eopSubscribe = WorkFormBean.queryEopSubscribe(configName);
                // 同时在td_b_ewe_node_templet 获取节点的nodeId
                IDataset nodeTemplet = WorkFormBean.queryNodetemplet(configName);

                if (IDataUtil.isNotEmpty(eopSubscribe) && IDataUtil.isNotEmpty(nodeTemplet)) {
                    String nodeId = nodeTemplet.getData(0).getString("NODE_ID");
                    for (Object objects : eopSubscribe) {
                        IData subscribeInfo = (IData) objects;
                        String ibsysId = subscribeInfo.getString("IBSYSID");
                        // 通过ibsysId与nodeId查询tf_b_eop_node表，获取相关信息
                        IDataset eopNodeInfos = WorkFormBean.queryeopNode(ibsysId, nodeId);
                        if (IDataUtil.isNotEmpty(eopNodeInfos)) {
                            eopNodeInfo.add(eopNodeInfos.getData(0));
                        }
                    }
                }
            }
        }
        return eopNodeInfo;
    }

    /**
     * 超时工单查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryAllWorkFormInfos(IData param) throws Exception {
        String busiFromId = param.getString("BUSIFORM_ID", "");
        return WorkFormBean.queryAllWorkFormInfos(busiFromId, getPagination());
    }

    /**
     * 归档方式变更工单查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryArchiveWay(IData param) throws Exception {
        String groupId = param.getString("GROUP_ID");
        String idsysId = param.getString("IBSYSID");
        String productId = param.getString("PRODUCT_ID");
        IDataset archiveInfo = new DatasetList();
        // 查询eopsubscribe表
        IDataset archiveWayInfos = WorkFormBean.queryArchiveWay(groupId, idsysId, productId);
        if (IDataUtil.isNotEmpty(archiveWayInfos)) {
            for (Object object : archiveWayInfos) {
                IData archiveWayInfo = (IData) object;
                String nodeId = archiveWayInfo.getString("NODE_ID");
                String staffId = archiveWayInfo.getString("ACCEPT_STAFF_ID");
                // 获取员工姓名
                String staffName = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", staffId);
                if ("apply".equals(nodeId) || "applyAudit".equals(nodeId) || "applyConfirm".equals(nodeId) || "creatGrpUser".equals(nodeId) || "customerCheck".equals(nodeId)) {
                    // 通过节点获取节点名字
                    IDataset nodeInfos = WorkFormBean.queryNodeTempletInfo(nodeId);
                    if (IDataUtil.isNotEmpty(nodeInfos)) {
                        String nodeDesc = nodeInfos.getData(0).getString("NODE_DESC");
                        archiveWayInfo.put("NODE_DESC", "专线开通_" + nodeDesc);
                        archiveWayInfo.put("STAFF_NAME", staffName);
                        archiveInfo.add(archiveWayInfo);
                    }
                }
            }
        }

        return archiveInfo;
    }

    /**
     * 获取归档方式
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryArchiveWay(IData param) throws Exception {
        String idsysId = param.getString("IBSYSID");
        return WorkFormBean.qryArchiveWay(idsysId);
    }

    /**
     * 专线开通单清单报表查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryClearList(IData param) throws Exception {

        return WorkFormBean.queryClearList(param, getPagination());

    }

    /**
     * 专线勘察单清单报表查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySurveyList(IData param) throws Exception {

        return WorkFormBean.querySurveyList(param, getPagination());

    }

    /**
     * 400业务订单审核流程查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryBusiWorkformInfos(IData param) throws Exception {
        String ibsysId = param.getString("IBSYSID");
        String busiType = param.getString("BUSI_TYPE");
        String cityCode = param.getString("CITY_CODE");

        return WorkFormBean.getWorkformNewAttrList400Busi(ibsysId, busiType, cityCode, getPagination());

    }

    /**
     * 400业务订单审核流程查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryBusiWorkformReviewDetailInfos(IData param) throws Exception {
        String ibsysId = param.getString("IBSYSID");
        String subIbsysId = param.getString("SUB_IBSYSID");

        return WorkFormBean.queryBusiWorkformReviewDetailInfos(ibsysId, subIbsysId);

    }

    /**
     * 我的工单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryWorkformDetail(IData param) throws Exception {
        return WorkFormBean.queryWorkformDetail(param, getPagination());

    }

    /**
     * 专线详情
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryDatelineAttr(IData param) throws Exception {
        return WorkFormBean.queryDatelineAttr(param);

    }

    /**
     * 工单类型
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset summarize(IData param) throws Exception {
        return null;// WorkFormBean.queryWorkformDetail(param, getPagination());

    }

    /**
     * 历史处理人
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset viewHisStaff(IData param) throws Exception {

        return WorkFormBean.viewHisStaff(param);

    }

    /**
     * 撤单接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset datelineRemoveOne(IData param) throws Exception {
        IDataset staffInfo = new DatasetList();
        String staffId = param.getString("DEAL_STAFF_ID");
        if ("undefined".equals(staffId)) {
            param.put("DEAL_STAFF_ID", "");
        }
        String serialNumber = StaticUtil.getStaticValueDataSource(CSBizBean.getVisit(), Route.CONN_SYS, "TD_M_STAFF", "STAFF_ID", "SERIAL_NUMBER", staffId);
        param.put("SERIAL_NUMBER", serialNumber);
        staffInfo.add(param);
        return staffInfo;

    }

    /**
     * 初始化业务类型与产品选择
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryProductAndBusiType(IData param) throws Exception {
        return WorkFormBean.queryProductAndBusiType(param);

    }

    /**
     * 历史错误信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryHistoryMistakeInfo(IData param) throws Exception {
        return WorkFormBean.queryHistoryMistakeInfo(param, getPagination());

    }

    /**
     * 专线策反清单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryMarktingWorkFormList(IData param) throws Exception {
        return WorkFormBean.queryMarktingWorkFormList(param, getPagination());

    }

    /**
     * 查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryattrByRecordNum(IData param) throws Exception {
        return WorkFormBean.queryattrByRecordNum(param);

    }

    /**
     * 查询附件信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryByAttach(IData param) throws Exception {
        return WorkFormBean.queryByAttach(param);

    }

    /**
     * 专线开通单统计报表查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryReportClear(IData param) throws Exception {
        return WorkFormBean.queryReportClear(param, getPagination());

    }

    /**
     * 专线勘察单统计报表查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryReportSurvey(IData param) throws Exception {
        return WorkFormBean.queryReportSurvey(param, getPagination());

    }

    /**
     * 查询eoms轨迹信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySummarizeEoms(IData param) throws Exception {
        return WorkFormBean.querySummarizeEoms(param);

    }

    /**
     * 查询eoms轨迹信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryEomsNodeDetail(IData param) throws Exception {
        return WorkFormBean.queryEomsNodeDetail(param);

    }

    /**
     * 查询400业务责任追究考核信息等
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryBusiInfos(IData param) throws Exception {
        return WorkFormBean.queryBusiInfos(param, getPagination());

    }

    /**
     * 查询eomsState表信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryEomsStateInfo(IData param) throws Exception {
        return WorkFormBean.queryEomsStateInfo(param);

    }

    /**
     * 查询400业务监督检查及400业务责任追究考核详情
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getTimerTaskWorkformNewAttrInfo(IData param) throws Exception {
        return WorkFormBean.getTimerTaskWorkformNewAttrInfo(param);

    }

    /**
     * 查询业务监督检查和责任追究考核记录
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryCheckRecordInfos(IData param) throws Exception {
        return WorkFormBean.queryCheckRecordInfos(param);

    }

    /**
     * 修改监督检查和责任追究考核记录
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void updateCheckRecordInfo(IData param) throws Exception {
        WorkFormBean.updateCheckRecordInfo(param);

    }

    /**
     * 新增监督检查和责任追究考核记录
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void addCheckRecordInfo(IData param) throws Exception {
        WorkFormBean.addCheckRecordInfo(param);

    }

    /**
     * 定时全量复核查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryTimerTaskWorkformInfos(IData param) throws Exception {
        return WorkFormBean.queryTimerTaskWorkformInfos(param);

    }

    /**
     * 定时全量复核
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getTimerTaskWorkformNewAttr(IData param) throws Exception {
        return WorkFormBean.getTimerTaskWorkformNewAttr(param);

    }

    /**
     * 查询拆机的attr专线信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryDestroyAttrInfoList(IData param) throws Exception {
        return WorkFormBean.queryDestroyAttrInfoList(param);

    }

    /**
     * 修改异常工单状态
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void updateEweStepByState(IData param) throws Exception {
        WorkFormBean.updateEweStepByState(param);

    }

    /**
     * 修改异常工单状态
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void insertAttachList(IData param) throws Exception {
        WorkFormBean.insertAttachList(param);

    }

    /**
     * 查询资管归档信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryReadSourceconfirm(IData param) throws Exception {
        return WorkFormBean.queryReadSourceconfirm(param);

    }

    /**
     * 查询State表，获取专线条数
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryEomsStateInfos(IData param) throws Exception {
        return WorkFormBean.qryEomsStateInfos(param);

    }

    /**
     * 查询新老表的Attr属性
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querydataLineAttrInfoList(IData param) throws Exception {
        return WorkFormBean.querydataLineAttrInfoList(param);

    }

    /**
     * 查询审核信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryEopotherInfos(IData param) throws Exception {
        return WorkFormBean.queryEopotherInfos(param);

    }

    /**
     * 勘察单解绑 =
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void updateSurveyUnbundie(IData param) throws Exception {
        WorkFormBean.updateSurveyUnbundie(param);

    }

    /**
     * 查询附件是否存在
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryAttachList(IData param) throws Exception {
        return WorkFormBean.queryAttachList(param);

    }

    /**
     * 查询重派附件
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryGroupAttach(IData param) throws Exception {
        return WorkFormBean.qryGroupAttach(param);

    }

    /**
     * 查询业务场景
     * 
     * @param input
     * @return querydataLineChangemode
     * @throws Exception
     */
    public IDataset querydataLineChangemode(IData param) throws Exception {
        return WorkFormBean.querydataLineChangemode(param);

    }

    /**
     * 勘察单解绑 =
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void updatePoolState(IData param) throws Exception {
        String ibsysID = param.getString("IBSYSID");
        String recordNum = param.getString("RECORD_NUM");
        IDataset emosInfos = new DatasetList();
        if (Integer.parseInt(recordNum) > 0) {
            emosInfos = WorkformEomsStateBean.qryEomsStateByIbsysidTradeId(ibsysID, recordNum);
        } else {
            emosInfos = WorkformEomsStateBean.qryEomsStateByIbsysid(ibsysID);
        }

        if (IDataUtil.isEmpty(emosInfos)) {
            return;
        }

        for (int i = 0; i < emosInfos.size(); i++) {
            IData dataMap = new DataMap();
            dataMap.put("PRODUCTNO", emosInfos.getData(i).getString("PRODUCT_NO"));
            dataMap.put("STATE", "A");
            WorkFormBean.updateSurveyUnbundie(dataMap);
        }
    }

    /**
     * 更新客管合同附件
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void updateDatecontractInfo(IData param) throws Exception {
        WorkFormBean.updateDatecontractInfo(param);

    }
    
    /**
     * 稽核工单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryAuditWorkform(IData param) throws Exception {
        return WorkFormBean.queryAuditWorkform(param, getPagination());

    }
}
