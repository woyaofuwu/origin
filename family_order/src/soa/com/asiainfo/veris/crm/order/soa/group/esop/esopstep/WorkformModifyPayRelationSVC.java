package com.asiainfo.veris.crm.order.soa.group.esop.esopstep;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;

public class WorkformModifyPayRelationSVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = -5023486579429964093L;
    
    public IData crtModifyTrade(IData data) throws Exception {
        String ibsysid = data.getString("IBSYSID");
        String nodeId = data.getString("NODE_ID");

        IData attrparam = new DataMap();
        attrparam.put("IBSYSID", ibsysid);
        attrparam.put("NODE_ID", "apply");
        IDataset attrList = WorkformAttrBean.getNewInfoByIbsysidAndNodeId(attrparam);
        if(IDataUtil.isEmpty(attrList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据IBSYSID=" + ibsysid + "未查询到TF_B_EOP_ATTR表信息！");
        }
        String operType = "";
        String acctId = "";
        for (int i = 0; i < attrList.size(); i++) {
            IData attrData = attrList.getData(i);
            if("OPERTYPE".equals(attrData.getString("ATTR_CODE"))) {
                operType = attrData.getString("ATTR_VALUE");
            }
            if("ACCT_ID".equals(attrData.getString("ATTR_CODE"))) {
                acctId = attrData.getString("ATTR_VALUE");
            }
        }
        if(StringUtils.isBlank(operType)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到操作类型！");
        }

        IDataset productSubList = WorkformProductSubBean.qryProductByIbsysid(ibsysid);
        if(IDataUtil.isEmpty(productSubList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据IBSYSID=" + ibsysid + "未查询到产品子表TF_B_EOP_PRODUCT_SUB信息！");
        }

        IData eos = new DataMap();
        eos.put("IBSYSID", ibsysid);
        eos.put("NODE_ID", nodeId);

        for (int i = 0; i < productSubList.size(); i++) {
            IData productSub = productSubList.getData(i);
            eos.put("RECORD_NUM", productSub.getString("RECORD_NUM"));
            String userId = productSub.getString("USER_ID");
            IData param = new DataMap();
            param.put("USER_ID", userId);
            param.put("OPERTYPE", operType);
            param.put("ACCT_ID", acctId);
            param.put("ESOP", eos);
            param.put("USER_EPARCHY_CODE", "0898");
            CSAppCall.call("SS.ModifyLinePayRelationSVC.crtOrder", param);
        }
        return null;
        
    }
}
