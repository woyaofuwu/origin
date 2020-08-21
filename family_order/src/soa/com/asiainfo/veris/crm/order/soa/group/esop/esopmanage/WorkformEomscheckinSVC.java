package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsStateBean;

public class WorkformEomscheckinSVC extends CSBizService {
    private static final long serialVersionUID = 1L;

    public void checkin(IData inparam) throws Exception {
        String ibsysid = inparam.getString("IBSYSID");
        String busiFromId = inparam.getString("BUSIFORM_ID");
        String recordNum = inparam.getString("RECORD_NUM", "");
        String subIbsysId = inparam.getString("SUB_IBSYSID");
        String groupId = inparam.getString("GROUP_ID");
        String productId = inparam.getString("PRODUCT_ID");
        String dealType = inparam.getString("DEAL_TYPE");
        // 勘查归档
        IDataset details = WorkformEomsStateBean.queryEomsStateByIbsysid(ibsysid);
        if (DataUtils.isEmpty(details)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID[" + ibsysid + "]+查询无EOMS_DETAIL数据！");
        }
        int isAllOver = 0;
        for (Object object : details) {
            IData detailInfo = (IData) object;
            String dealState = detailInfo.getString("DEAL_STATE");
            if ("2".equals(dealState) || "K".equals(dealState)) {
                isAllOver++;
            }
        }
        // 判断是否存在归档成功的专线，没有则驱动验收期子流程，勘察归档不走验收期
        if (isAllOver == 0 && "32".equals(dealType)) {
            IData workformSubscriberData = new DataMap();
            IData scrData = new DataMap();
            IData eosCommonData = new DataMap();
            IData param = new DataMap();
            IData custInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
            workformSubscriberData.put("GROUP_ID", groupId);
            workformSubscriberData.put("CUST_NAME", custInfo.getString("CUST_NAME", ""));
            workformSubscriberData.put("RSRV_STR5", "");
            workformSubscriberData.put("BPM_TEMPLET_ID", "EFILELINE");
            workformSubscriberData.put("BUSI_TYPE", "P");
            workformSubscriberData.put("BUSI_CODE", productId);

            scrData.put(EcEsopConstants.TABLE_EOP_SUBSCRIBE, workformSubscriberData);
            eosCommonData.put("BPM_TEMPLET_ID", "EFILELINE");
            eosCommonData.put("BUSI_TYPE", "P");
            eosCommonData.put("BUSI_CODE", productId);
            eosCommonData.put("IN_MODE_CODE", "0");
            eosCommonData.put("BUSIFORM_OPER_TYPE", "");

            param.put("EOSAttr", scrData);
            param.put("EOSCom", eosCommonData);
            // IData submitParam = ScrDataTrans.buildWorkformSvcParam(submitData);
            CSAppCall.call("SS.WorkformRegisterSVC.register", param);
        } else if (isAllOver == details.size())// 判断是否全部归档完成，则调用流程驱动接口
        {
            IDataset detailInfo = WorkformEomsStateBean.quryEweReleCode(recordNum, busiFromId);
            if (DataUtils.isEmpty(detailInfo)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "+查询tf_b_ewe_rele表无数据！");

            }
            IData param = new DataMap();
            param.put("BUSIFORM_NODE_ID", detailInfo.getData(0).getString("BUSIFORM_NODE_ID"));
            param.put("SUB_IBSYSID", subIbsysId);

            CSAppCall.call("SS.WorkformDriveSVC.execute", param);
        }

    }

}
