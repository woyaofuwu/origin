package com.asiainfo.veris.crm.order.soa.group.esop.esopctr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.ScrData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.BusiFlowReleInfoQuery;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweBusiSpecReleInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformModiTraceBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class WorkformCtrRelaSubscribeSVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset ctrRelaSubscribe(IData data) throws Exception {
        IDataset results = new DatasetList();
        String ibsysid = data.getString("IBSYSID");
        String busiformId = data.getString("BUSIFORM_ID");
        String nodeId = data.getString("NODE_ID");
        IData input = new DataMap();
        input.put("IBSYSID", ibsysid);
        IDataset relaSubscribeData = WorkformModiTraceBean.qryModiTraceInfos(ibsysid);
        //不为空则表示已生成计费方式审核流程，直接跳过
        if(DataUtils.isNotEmpty(relaSubscribeData)) {
            return results;
        }

        IDataset eweDatas = EweNodeQry.qryEweByIbsysid(ibsysid, "0");
        if(DataUtils.isEmpty(eweDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysid + "未查询到TF_B_EWE信息！");
        }
        IData eweData = eweDatas.first();
        //拼创建流程参数
        IData param = buildWorkformSvcParam(ibsysid);
        //创建流程
        IDataset result = CSAppCall.call("SS.WorkformRegisterSVC.register", param);
        if(DataUtils.isEmpty(result)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "创建计费方式审核流程失败！");
        }
        //插流程关系表
        String relaIbsysid = result.first().getString("IBSYSID");
        String relaBusiformId = result.first().getString("BUSIFORM_ID");
        if(StringUtils.isBlank(relaBusiformId)) {
            IDataset busiformList = EweNodeQry.qryEweByIbsysid(relaIbsysid, "0");
            relaBusiformId = busiformList.first().getString("BUSIFORM_ID");
        }
        IData modiTraceData = new DataMap();
        modiTraceData.put("IBSYSID", relaIbsysid);
        modiTraceData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        modiTraceData.put("MAIN_IBSYSID", ibsysid);
        modiTraceData.put("ATTR_CODE", "BUSIFORM_ID");
        modiTraceData.put("ATTR_TYPE", "F");
        modiTraceData.put("ATTR_VALUE", relaBusiformId);
        modiTraceData.put("ATTR_MAIN_VALUE", busiformId);
        modiTraceData.put("STAFF_ID", eweData.getString("ACCEPT_STAFF_ID"));
        modiTraceData.put("CITY_CODE", eweData.getString("CITY_CODE"));
        modiTraceData.put("DEPART_ID", eweData.getString("ACCEPT_DEPART_ID"));
        modiTraceData.put("EPARCHY_CODE", eweData.getString("EPARCHY_CODE"));
        modiTraceData.put("ACCEPT_DATE", SysDateMgr.getSysDate());

        boolean modiTraceResult = WorkformModiTraceBean.insertModiTrace(modiTraceData);
        if(!modiTraceResult) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "插入TF_B_EOP_MODI_TRACE表信息失败！");
        }

        return results;

    }

    private IData buildWorkformSvcParam(String ibsysid) throws Exception {
        /*
         * 需要表数据
         * EcEsopConstants.TABLE_EOP_SUBSCRIBE
         * EcEsopConstants.TABLE_EOP_NODE
         * EcEsopConstants.TABLE_EOP_PRODUCT
         * EcEsopConstants.TABLE_EOP_OTHER
         */
        IDataset otherList = WorkformOtherBean.qryByIbsysidNodeId(ibsysid, "apply");
        if(DataUtils.isEmpty(otherList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到tf_b_eop_other表信息！");
        }
        //查询审核流程BPM_TEMPLET_ID
        IDataset cfgList = EweConfigQry.qryDistinctValueDescByParamName("ACCEPTTANCE_PERIOD_AUDIT", "BPM_TEMPLET_ID", EcEsopConstants.STATE_VALID);
        if(DataUtils.isEmpty(cfgList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到审核流程配置！");
        }
        String bpmTempleId = cfgList.first().getString("PARAMVALUE");
        IData productData = WorkformProductBean.qryProductByPk(ibsysid, "0");
        if(DataUtils.isEmpty(productData)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据IBSYSID=" + ibsysid + "未查询到TF_F_EOP_PRODUCT表信息！");
        }
        String productId = productData.getString("PRODUCT_ID");
        IData bfrParam = new DataMap();
        bfrParam.put("PRODUCT_ID", productId);
        bfrParam.put("BUSI_CODE", bpmTempleId);
        IDataset bfrList = BusiFlowReleInfoQuery.getOperTypeByBusiCode(bfrParam);
        if(DataUtils.isEmpty(bfrList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据BUSI_TYPE=" + bpmTempleId + "未查询到TD_B_EOP_BUSI_FLOW_RELE表配置信息！");
        }
        String busiformOperType = bfrList.first().getString("BUSI_OPER_TYPE");
        IDataset bsrList = EweBusiSpecReleInfoQry.qryBusiSpecReleByOperTypeProdId(busiformOperType, productId);
        if(DataUtils.isEmpty(bsrList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据BUSIFORM_OPER_TYPE=" + busiformOperType + "未查询到TD_B_EWE_BUSI_SPEC_RELE表配置信息！");
        }
        IData busiSpecRele = bsrList.first();
        IDataset subscriberDatas = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isEmpty(subscriberDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysid + "未查询到订单信息！");
        }
        IData subscriberData = subscriberDatas.first();
        IDataset nodeDataList = EweNodeQry.qryInfoByBpmTempletType(bpmTempleId, "3", "0");
        if(DataUtils.isEmpty(subscriberDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流程ID：" + bpmTempleId + "未流程开始节点！");
        }
        IData nodeData = nodeDataList.first();
        IData param = new DataMap();
        ScrData scrData = new ScrData();
        IData eosCommonData = new DataMap();
        eosCommonData.put("BPM_TEMPLET_ID", busiSpecRele.getString("BPM_TEMPLET_ID"));
        eosCommonData.put("BUSI_TYPE", busiSpecRele.getString("BUSI_TYPE"));
        eosCommonData.put("BUSI_CODE", busiSpecRele.getString("BUSI_CODE"));
        eosCommonData.put("IN_MODE_CODE", busiSpecRele.getString("IN_MODE_CODE"));
        eosCommonData.put("BUSIFORM_OPER_TYPE", busiSpecRele.getString("BUSIFORM_OPER_TYPE"));
        eosCommonData.put("NODE_ID", nodeData.getString("NODE_ID"));
        eosCommonData.put("PRODUCT_ID", productId);

        //EcEsopConstants.TABLE_EOP_SUBSCRIBE表数据
        IData workformSubscriberData = new DataMap();
        workformSubscriberData.put("GROUP_ID", subscriberData.getString("GROUP_ID"));
        workformSubscriberData.put("CUST_NAME", subscriberData.getString("CUST_NAME"));
        workformSubscriberData.put("BPM_TEMPLET_ID", busiSpecRele.getString("BPM_TEMPLET_ID"));
        workformSubscriberData.put("BUSI_TYPE", busiSpecRele.getString("BUSI_TYPE"));
        workformSubscriberData.put("BUSI_CODE", busiSpecRele.getString("BUSI_CODE"));
        workformSubscriberData.put("RSRV_STR2", busiSpecRele.getString("BPM_TEMPLET_ID"));
        workformSubscriberData.put("RSRV_STR3", "一般");
        workformSubscriberData.put("RSRV_STR4", "专线开通延期计费审核");
        scrData.put(EcEsopConstants.TABLE_EOP_SUBSCRIBE, workformSubscriberData);

        //EcEsopConstants.TABLE_EOP_NODE表数据
        IData workformNodeData = new DataMap();
        workformNodeData.put("NODE_ID", nodeData.getString("NODE_ID"));
        workformNodeData.put("PRODUCT_ID", productId);
        scrData.put(EcEsopConstants.TABLE_EOP_NODE, workformNodeData);

        //EcEsopConstants.TABLE_EOP_PRODUCT表数据
        IDataset workformProductList = new DatasetList();
        IData workformProduct = new DataMap();
        workformProduct.put("PRODUCT_ID", productId);
        workformProduct.put("PRODUCT_NAME", productData.getString("PRODUCT_NAME"));
        workformProduct.put("RSRV_STR1", productData.getString("RSRV_STR1"));
        workformProduct.put("RSRV_STR2", productData.getString("RSRV_STR2", ""));
        workformProduct.put("RECORD_NUM", "0");
        workformProduct.put("PRODUCT_TYPE_CODE", productData.getString("PRODUCT_TYPE_CODE", ""));
        workformProductList.add(workformProduct);
        scrData.put(EcEsopConstants.TABLE_EOP_PRODUCT, workformProductList);

        //EcEsopConstants.TABLE_EOP_OTHER表数据
        IDataset workformOtherList = new DatasetList();
        for (int i = 0; i < otherList.size(); i++) {
            IData otherData = otherList.getData(i);
            IData workformOther = new DataMap();
            workformOther.put("ATTR_CODE", otherData.getString("ATTR_CODE"));
            workformOther.put("ATTR_NAME", otherData.getString("ATTR_NAME"));
            workformOther.put("ATTR_VALUE", otherData.getString("ATTR_VALUE"));
            workformOther.put("RECORD_NUM", otherData.getString("RECORD_NUM"));
            workformOtherList.add(workformOther);
        }
        scrData.put(EcEsopConstants.TABLE_EOP_OTHER, workformOtherList);
        scrData.put(EcEsopConstants.TABLE_EOP_ATTR, workformOtherList);

        param.put("EOSAttr", scrData);
        param.put("EOSCom", eosCommonData);

        return param;
    }

}
