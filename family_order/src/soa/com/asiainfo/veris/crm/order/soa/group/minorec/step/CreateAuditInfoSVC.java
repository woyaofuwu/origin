package com.asiainfo.veris.crm.order.soa.group.minorec.step;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformReleBean;
import com.asiainfo.veris.crm.order.soa.group.minorec.queryAudit.QryAuditInfoBean;
import com.asiainfo.veris.crm.order.soa.group.minorec.queryAudit.QryAuditInfoSVC;
import com.asiainfo.veris.crm.order.soa.group.minorec.queryAudit.QryEopOtherInfoSVC;

public class CreateAuditInfoSVC extends CSBizService {

    private static final transient Logger logger = Logger.getLogger(CreateAuditInfoSVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * @Title: createAuditMessageInfo
     * @Description: 生成稽核待阅
     * @param data
     * @return void
     * @throws Exception
     * @author zhangzg
     * @date 2019年11月11日上午11:15:30
     */
    public void createAuditMessageInfo(IData data) throws Exception {
        String ibsysid = data.getString("IBSYSID");
        String nodeId = data.getString("NODE_ID");
        String busiformId = data.getString("BUSIFORM_ID");
        String busiCode = data.getString("BUSI_CODE");
        String productId ="";
        String productName ="";
        // 根据ibsysId查询稽核员工工号
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
		param.put("BUSIFORM_ID", busiformId);
        IDataset staffInfo = QryEopOtherInfoSVC.qryAuditStaffInfoByIbsysid(param);
        if (IDataUtil.isEmpty(staffInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查询到稽核人员信息！");
        }
        String staffId = staffInfo.getData(0).getString("ATTR_VALUE");
     // 获取节点信息 查询BUSIFORM_NODE_ID
        IDataset eweNodeInfo = EweNodeQry.qryEweNodeByBusiformIdAndNodeId(busiformId, nodeId);
        // TEST
//         IData tparam = new DataMap();
//         tparam.put("BUSIFORM_ID", busiformId);
//         tparam.put("NODE_ID", nodeId);
//         IDataset eweNodeInfo = AuditTestSVC.qryHEweNodeInfos(tparam);
        if (IDataUtil.isEmpty(eweNodeInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取节点信息为空！");
        }
        String busiforNodeId = eweNodeInfo.first().getString("BUSIFORM_NODE_ID");
        // 获取bpmtmplteId
        IDataset eweInfo = EweNodeQry.qryEweByBusiformId(busiformId);
//         IDataset eweInfo = AuditTestSVC.qryHEweInfos(tparam);
        if (IDataUtil.isEmpty(eweInfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取流程信息为空！");
        }
        String bpmTempletId = eweInfo.first().getString("BPM_TEMPLET_ID");
        // 若当前流程为子流程 则查询父流程
        IData tmpParam = new DataMap();
        tmpParam.put("BPM_TEMPLET_ID", bpmTempletId);
        IDataset tmpInfos = QryAuditInfoBean.qrySubRelaInfoByTemplet(tmpParam);
        String parentBpmTepletId = bpmTempletId;
        if (IDataUtil.isNotEmpty(tmpInfos))
        {
            tmpParam.put("IBSYSID", ibsysid);
            IDataset parentBpmtempletId = QryAuditInfoBean.qryParentBpmtempletIdByIbsysId(tmpParam);
            if(IDataUtil.isNotEmpty(parentBpmtempletId)) {
                parentBpmTepletId = parentBpmtempletId.first().getString("BPM_TEMPLET_ID");
            }
        }
      //除 新增融合V网外不稽核
        if ("MINORECSPEEDINESSCHANGE".equals(parentBpmTepletId))
        {
            // 新增融合V网稽核
            IDataset eopProductInfos = QryAuditInfoSVC.qryEopProductByIbsysidAndBusiformId(param);
            if (IDataUtil.isEmpty(eopProductInfos))
            {
                return;
            }
            productId = eopProductInfos.first().getString("PRODUCT_ID");
            productName = eopProductInfos.first().getString("PRODUCT_NAME");
            if (!"8001".equals(productId))
            {
                return;
            }
        }
        //一单清酒店ESP产品处理
        if("VP66666".equals(busiCode)) {
            IDataset releInfos = WorkformReleBean.qryBySubBusiformId(busiformId);
//            IDataset releInfos = AuditTestBean.qryBySubBusiformId(busiformId);
            if(IDataUtil.isEmpty(releInfos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据BUSIFORM_ID:" + busiformId + ",查询主流程失败！");
            }

            String recodeNum = releInfos.first().getString("RELE_VALUE", "");
            IData productInfo = WorkformProductBean.qryProductByPk(ibsysid,recodeNum);
//            IData productInfo = AuditTestBean.qryProductByPk(ibsysid,recodeNum);
            if (IDataUtil.isEmpty(productInfo))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibsysid+"找不到TF_B_EOP_PRODUCT表的记录！");
            }
            productId = productInfo.getString("PRODUCT_ID");
            productName = productInfo.getString("PRODUCT_NAME");
        }else if("380700".equals(busiCode) || "380300".equals(busiCode) || "921015".equals(busiCode)) {
            //ESP单产品处理
            productId = busiCode;
            IData productInfo = WorkformProductBean.qryProductByPk(ibsysid,"1");
//            IData productInfo = AuditTestBean.qryProductByPk(ibsysid,"1");
            productName = productInfo.getString("PRODUCT_NAME");
        }else {
            //非ESP产品处理
            param.put("BUSIFORM_ID", busiformId);
            IDataset eopProductInfos = QryAuditInfoSVC.qryEopProductByIbsysidAndBusiformId(param);
            if (IDataUtil.isEmpty(eopProductInfos)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID【"+ibsysid+"】和BUSIFORM_ID【"+busiformId+ "】在TF_B_EOP_PRODUCT表未查询到产品信息！");
            }
            //7341 集团商务宽带产品 2222 多媒体桌面电话  8001 融合V网 8000 集团V网
            productId = eopProductInfos.first().getString("PRODUCT_ID");
            productName = eopProductInfos.first().getString("PRODUCT_NAME");
            if (!"7341".equals(productId) && !"2222".equals(productId) && !"8001".equals(productId) && !"8000".equals(productId)) {
                //非 集团商务宽带产品和多媒体桌面电话  产品不生成待阅
                return;
            }
        }
        
        // 拼接生成待阅所需参数
        String url = "/order/iorder?service=page/igroup.minorec.MinorecAudit&listener=initial&IBSYSID=" + ibsysid + "&BPM_TEMPLET_ID=" + parentBpmTepletId 
                + "&BUSIFORM_NODE_ID=" + busiforNodeId + "&BUSIFORM_ID=" + busiformId + "&SUB_BPM_TEMPLET_ID=" + bpmTempletId + "&PRODUCT_ID=" + productId;
        IData result = new DataMap();
        // esop待办/待阅生成数据抽取服务
        String svcName = "SS.EsopWorkTaskDataSVC.getReadTaskMinorecDataInfo";
        result.put("URL", url);
        result.put("BI_SN", ibsysid);
        result.put("BUSIFORM_NODE_ID", busiforNodeId);
        result.put("INFO_AUTH", staffId);
        result.put("BUSI_TYPE_CODE", result.getString("BUSIFORM_OPER_TYPE", ""));
        result.put("NODE_DESC", "中小企业快速开通稽核");
        result.put("BUSI_TYPE", "快速开通");
        result.put("PRODUCT_ID", productId);
        result.put("PRODUCT_NAME", productName);
        IDataset taskDatas = CSAppCall.call(svcName, result);
        if (DataUtils.isEmpty(taskDatas)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "BUSIFORM_NODE_ID:" + result.getString("BUSIFORM_NODE_ID") + "待阅工单生成数据抽取失败！！");
        }
        IData taskInfoData = taskDatas.getData(0);
        taskInfoData.put("INFO_SIGN", busiforNodeId);
        taskInfoData.remove("RECE_OBJS");
        taskInfoData.put("RECE_OBJ", staffId);
        taskInfoData.put("INFO_SEND_TIME", SysDateMgr.getSysTime());
        taskInfoData.put("END_TIME", SysDateMgr.END_DATE_FOREVER);
        // 生成待阅工单
        logger.debug(">>>>>>>>>>>>>>>>>生成待办时入参>>>>>>>>>>>>>>>>>>" + taskInfoData.toString());
        CSAppCall.call("SS.WorkTaskMgrSVC.crtWorkTaskInfo", taskInfoData);
    }
}
