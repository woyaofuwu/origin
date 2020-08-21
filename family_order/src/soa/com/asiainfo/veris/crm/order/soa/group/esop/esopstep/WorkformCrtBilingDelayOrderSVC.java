package com.asiainfo.veris.crm.order.soa.group.esop.esopstep;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;

public class WorkformCrtBilingDelayOrderSVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset crtBilingDelayOrder(IData data) throws Exception {

        String ibsysid = data.getString("IBSYSID");
        String nodeId = data.getString("NODE_ID");
        String busiformNodeId = data.getString("BUSIFORM_NODE_ID");
        IDataset productSubList = WorkformProductSubBean.qryProductByIbsysid(ibsysid);
        if(IDataUtil.isEmpty(productSubList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据IBSYSID=" + ibsysid + "未查询到产品子表TF_B_EOP_PRODUCT_SUB信息！");
        }
        IData attrparam = new DataMap();
        attrparam.put("IBSYSID", ibsysid);
        attrparam.put("NODE_ID", "apply");
        IDataset attrList = WorkformAttrBean.getNewInfoByIbsysidAndNodeId(attrparam);
        String newAccpValue = "";
        if(IDataUtil.isEmpty(attrList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据IBSYSID=" + ibsysid + "未查询到TF_B_EOP_ATTR表信息！");
        }
        IDataset userids=new DatasetList();;
        for (int i = 0; i < attrList.size(); i++) {
            IData attrData = attrList.getData(i);
            if("NEW_ACCEPTTANCE_PERIOD".equals(attrData.getString("ATTR_CODE"))) {
                newAccpValue = attrData.getString("ATTR_VALUE");
            }
            if("USER_ID".equals(attrData.getString("ATTR_CODE"))) {
            	userids.add(attrData.getString("ATTR_VALUE"));
            }
        }
        /*if(!"0".equals(newAccpValue) && "apply".equals(nodeId)) {
            return null;
        }*/
        /*IDataset nodeDatas = EweNodeQry.qryByBusiformNodeId(busiformNodeId);
        if(IDataUtil.isEmpty(nodeDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据BUSIFORM_NODE_ID=" + busiformNodeId + "未查询到节点表TF_B_EWE_NODE信息！");
        }*/
        
        IDataset otherList = WorkformOtherBean.qryByIbsysidNodeId(ibsysid, nodeId);
        if(IDataUtil.isNotEmpty(otherList) && !"apply".equals(nodeId)) {//审核节点已审核
            for (int j = 0; j < otherList.size(); j++) {
                IData otherData = otherList.getData(j);
                if("AUDIT_RESULT".equals(otherData.getString("ATTR_CODE"))) {
                    if("0".equals(otherData.getString("ATTR_VALUE"))) {
                        return null;
                    }
                    //flag = false;
                    break;
                }
            }
        } else if(IDataUtil.isNotEmpty(otherList) && "apply".equals(nodeId)) {//申请节点且需要审核
            return null;
        } else if(IDataUtil.isEmpty(otherList) && !"apply".equals(nodeId)) {//审核节点且未审核
            return null;
        }

        /*boolean flag = true;
        if(flag) {
            return null;
        }*/
        for (int i = 0; i < productSubList.size(); i++) {
            IData productSub = productSubList.getData(i);
            String userId = productSub.getString("USER_ID");
            for (int j = 0; j < userids.size(); j++) {
            	String userId2=(String)userids.get(j);
            	if(userId2.equals(userId)){
            		IData param = new DataMap();
                    param.put("USER_EPARCHY_CODE", "0898");
                    param.put("USER_ID", userId);
                    param.put("CHANGE_TAG", newAccpValue);
                    IDataset orderList = CSAppCall.call("SS.ModifyLineDistinctSVC.crtTrade", param);
                    if(IDataUtil.isNotEmpty(orderList)) {
                        String tradeId = orderList.first().getString("TRADE_ID");
                        WorkformProductSubBean.updByIbsysid(ibsysid, tradeId, userId, productSub.getString("SERIAL_NUMBER"), productSub.getString("RECORD_NUM"));
                    }
                    break;
            	}
            }

            
        }
        return null;
    }
}
