package com.asiainfo.veris.crm.order.soa.group.cenpaygfffesop;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeTempletReleInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class GrpCenpayGfffDriveEsopSvc extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static transient Logger logger = Logger.getLogger(GrpCenpayGfffDriveEsopSvc.class);

    public IDataset updataCrmTrade(IData data) throws Exception {
        
        String ibsysid = data.getString("IBSYSID");
        String nodeId = data.getString("NODE_ID","");
        String recordNum = data.getString("RECORD_NUM", "0");
        
        IDataset returnList = new DatasetList();

        IData param = new DataMap();
        //查询订单表
        IDataset subscriberDatas = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isEmpty(subscriberDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysid + "未查询到订单信息！");
        }
        IData subscriberData = subscriberDatas.first();

        //查询产品表
        IDataset productDatas = WorkformProductBean.qryProductByIbsysid(ibsysid);
        if(DataUtils.isEmpty(productDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysid + "未查询到产品信息！");
        }
        IData productData = productDatas.first();
        param.put("TRADE_ID", productData.getString("TRADE_ID"));

        //查询流程订单表
        IDataset eweDatas = EweNodeQry.qryEweByIbsysid(ibsysid, "0");
        if(DataUtils.isEmpty(eweDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysid + "未查询到流程订单信息！");
        }
        IData eweData = eweDatas.first();
        String busiformId = eweData.getString("BUSIFORM_ID", "");
        IDataset eweNodeDatas = EweNodeQry.qryEweNodeByBusiformIdAndNodeId(busiformId, nodeId);
        if(DataUtils.isEmpty(eweNodeDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流程ID：" + busiformId + "未查询到流程节点信息！");
        }
        IData eweNodeData = eweNodeDatas.first();
        String subIbsysid = eweNodeData.getString("SUB_BI_SN", "");

        IDataset otherDatas = WorkformOtherBean.qryOtherBySubIbsysidRecordNum(subIbsysid, recordNum);
        logger.debug("---------------------GrpCenpayGfffDriveEsopSvc服务other表数据：" + otherDatas.toString());
        if(DataUtils.isEmpty(otherDatas)) {
            return returnList;
        }
        String checkPass = "";
        //审核不通过时，通知CRM删除工单
        for (int i = 0; i < otherDatas.size(); i++) {
            IData otherData = otherDatas.getData(i);
            if("AUDIT_RESULT".equals(otherData.getString("ATTR_CODE"))) {
                if("0".equals(otherData.getString("ATTR_VALUE"))) {
                    checkPass = "false";
                    param.put("CHECK_PASS", checkPass);
                    returnList = CSAppCall.call("SS.GrpCenpayGfffEsopSvc.updateAllGrpCenpayGfff", param);
                    return returnList;
                }
            }
        }
        
        //当最后一个审核节点为审核通过时，通知CRM修改订单执行时间
        String bpmTempletId = subscriberData.getString("BPM_TEMPLET_ID");
        IDataset nodeReleDatas = EweNodeTempletReleInfoQry.qryInfoByBpmTempletNode(bpmTempletId, nodeId, "0");
        if(DataUtils.isEmpty(nodeReleDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流程编码：" + bpmTempletId + "流程节点：" + nodeId + "未查询到流程节点配置信息！");
        }

        for (int i = 0; i < nodeReleDatas.size(); i++) {
            IData nodeReleData = nodeReleDatas.getData(i);
            if("End".equals(nodeReleData.getString("NEXT_NODE_ID"))) {
                checkPass = "true";
                param.put("CHECK_PASS", checkPass);
                returnList = CSAppCall.call("SS.GrpCenpayGfffEsopSvc.updateAllGrpCenpayGfff", param);
                return returnList;
            }
        }
        return returnList;
    }

}
