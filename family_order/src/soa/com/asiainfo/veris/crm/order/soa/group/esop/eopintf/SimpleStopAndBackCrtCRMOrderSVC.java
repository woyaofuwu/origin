package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformReleBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class SimpleStopAndBackCrtCRMOrderSVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset crtCRMOrder(IData data) throws Exception {
        String ibsysid = data.getString("IBSYSID");
        String busiformId = data.getString("BUSIFORM_ID");
        String nodeId = data.getString("NODE_ID", "");
        String bpmTempletId = data.getString("BPM_TEMPLET_ID");

        IDataset releInfos = WorkformReleBean.qryBySubBusiformId(busiformId);

        if(DataUtils.isEmpty(releInfos)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据BUSIFORM_ID:" + busiformId + ",查询主流程失败！");
        }
        String recodeNum = releInfos.first().getString("RELE_VALUE", "");

        IData param = new DataMap();
        IData eos = new DataMap();
        eos.put(releInfos.first().getString("RELE_CODE", ""), releInfos.first().getString("RELE_VALUE", ""));
        eos.put("IBSYSID", ibsysid);
        eos.put("NODE_ID", nodeId);
        param.put("ESOP", eos);
        param.put("RECORD_NUM", recodeNum);

        IDataset subscriberDatas = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(DataUtils.isEmpty(subscriberDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysid + "未查询到订单信息！");
        }
        IData subscriberData = subscriberDatas.first();
        String mainBpmTempletId = subscriberData.getString("BPM_TEMPLET_ID");
        
        String operType = "";
        if("MANUALSTOP".equals(mainBpmTempletId)) {
            operType = "stop";
        } else if("MANUALBACK".equals(mainBpmTempletId)) {
            operType = "back";
        } else {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未知主流程ID[" + mainBpmTempletId + "]");
        }
        param.put("OPER_TYPE", operType);

        IData productInfo = WorkformProductBean.qryProductByPk(ibsysid, "0");
        if(DataUtils.isEmpty(productInfo)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysid + "未查询到订单产品表信息！");
        }
        String productId = productInfo.getString("PRODUCT_ID");
        param.put("PRODUCT_ID", productId);
        if("7010".equals(productId)) {
            param.put("USER_ID", productInfo.getString("USER_ID"));
        } else {
            IData productSubInfo = WorkformProductSubBean.qryProductByPk(ibsysid, recodeNum);
            if(DataUtils.isEmpty(productSubInfo)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysid + "与RECORD_NUM=" + recodeNum + "未查询到订单产品子表信息！");
            }
            param.put("USER_ID", productSubInfo.getString("USER_ID"));
        }

        IData input = new DataMap();
        input.put("IBSYSID", ibsysid);
        input.put("NODE_ID", "apply");
        input.put("RECORD_NUM", recodeNum);
        IDataset attrList = WorkformAttrBean.getNewInfoByIbsysidAndNodeId(input);
        if(DataUtils.isEmpty(attrList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到TF_B_EOP_ATTR表数据！");
        }
        String productNo = "";
        for (int i = 0; i < attrList.size(); i++) {
            IData attrData = attrList.getData(i);
            if("PRODUCTNO".equals(attrData.getString("ATTR_CODE"))) {
                productNo = attrData.getString("ATTR_VALUE");
                break;
            }
        }
        if(StringUtils.isBlank(productNo)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到专线实例号！");
        }
        param.put("PRODUCTNO", productNo);
        param.put("USER_EPARCHY_CODE", "0898");

        IDataset otherList = WorkformOtherBean.qryByIbsysidNodeId(ibsysid, "apply");
        String accpValue = "";
        if(DataUtils.isNotEmpty(otherList)) {
            for (int i = 0; i < otherList.size(); i++) {
                if("ACCEPTTANCE_PERIOD".equals(otherList.getData(i).getString("ATTR_CODE"))) {
                    accpValue = otherList.getData(i).getString("ATTR_VALUE");
                    break;
                }
            }
        }
        param.put("ACCEPTTANCE_PERIOD", accpValue);

        //生成CRM工单
        IDataset result = CSAppCall.call("SS.StopOrBackLineRegSVC.crtOrder", param);
        if(DataUtils.isEmpty(result)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "生成CRM工单失败！");
        }
        return result;

    }

}
