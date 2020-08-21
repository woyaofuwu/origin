package com.asiainfo.veris.crm.order.soa.group.minorec.step;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.group.param.minorec.elecagreement.AgreementInfoBean;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class ChangeContractInfoSVC extends CSBizService{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final transient Logger log = Logger.getLogger(ChangeContractInfoSVC.class);

    /**
     * 审核后处理电子协议状态
     * @param data
     * @return
     * @throws Exception
     */
    public IData dealContractInfo(IData data) throws Exception{
        String ibsysid = data.getString("IBSYSID");
        String nodeId = data.getString("NODE_ID");
        String state = "";
        IDataset otherList = WorkformOtherBean.qryByIbsysidNodeId(ibsysid,nodeId);
        if(DataUtils.isEmpty(otherList)){
            CSAppException.apperr(GrpException.CRM_GRP_713, "未查询到审核结果！");
        }
        for(int i=0;i<otherList.size();i++){
            IData otherData = otherList.getData(i);
            if("ADULT_RESULT".equals(otherData.getString("ATTR_CODE"))){
                if("1".equals(otherData.getString("ATTR_VALUE"))){//审核不通过
                    state = "2";
                }else{
                    state = "3";
                }
            }
        }
        
        IData input = new DataMap();
        input.put("IBSYSID", ibsysid);
        IDataset offerList = CSAppCall.call("SS.QuickOrderMemberSVC.queryAuditQuickMemberData", input);
        Set<String> contractSet = new HashSet<String>();
        for (Object object : offerList) {
            IData offerData = (IData) object;
            IData ecCommonInfo = offerData.getData("EC_COMMON_INFO");
            IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
            contractSet.add(contractInfo.getString("CONTRACT_ID"));

        }

        if(contractSet.size() <= 0){
            CSAppException.apperr(GrpException.CRM_GRP_713, "未查询到合同编码！");
        }

        for(String contractId : contractSet){

            log.debug("CONTRACT_ID:"+contractId);

            IData param = new DataMap();
            param.put("AGREEMENT_ID", contractId);
            param.put("ARCHIVES_STATE", state);
            CSAppCall.call("SS.AgreementInfoSVC.updateAgreementFinish", param);
        }
        return new DataMap();
    }

    /**
     * 更新电子协议关联表
     * @param data
     * @return
     * @throws Exception
     */
    public IData dealContractRel(IData data) throws Exception{
        String ibsysid = data.getString("IBSYSID");
        String nodeId = data.getString("NODE_ID");

        IData input = new DataMap();
        input.put("IBSYSID", ibsysid);
        IDataset productList = WorkformProductBean.qryProductByIbsysid(ibsysid);
        if(DataUtils.isNotEmpty(productList)){
            for(int i =0;i<productList.size();i++){
                IData productData = productList.getData(i);
                String userId = productData.getString("USER_ID");
                if(StringUtils.isBlank(userId)){
                    continue;
                }
                IData userinfo = UcaInfoQry.qryUserInfoByUserIdForGrp(userId);
                if(DataUtils.isNotEmpty(userinfo)){

                    if(StringUtils.isBlank(userinfo.getString("PRODUCT_ID"))){
                        continue;
                        //CSAppException.apperr(GrpException.CRM_GRP_713, "未查询到用户["+userId+"]主产品！");
                    }

                    String contractId = userinfo.getString("CONTRACT_ID");
                    String productId = userinfo.getString("PRODUCT_ID");
                    String custId = userinfo.getString("CUST_ID");
                    if(StringUtils.isNotBlank(contractId)){
                        IData param = new DataMap();
                        param.put("CONTRACT_ID",contractId);
                        param.put("USER_ID",userId);
                        param.put("PRODUCT_ID",productId);
                        param.put("CUST_ID",custId);
                        AgreementInfoBean.updateArchivesRel(param,getVisit());
                    }

                }

            }
        }

        return new DataMap();
    }

    /**
     * 关联流程与电子协议
     * @param data
     * @return
     * @throws Exception
     */
    public IData addContractIBsysid(IData data) throws Exception{

        String ibsysid = data.getString("IBSYSID");

        IData input = new DataMap();
        input.put("IBSYSID", ibsysid);
        IDataset offerList = CSAppCall.call("SS.QuickOrderMemberSVC.queryAuditQuickMemberData", input);
        Set<String> contractSet = new HashSet<String>();
        for (Object object : offerList) {
            IData offerData = (IData) object;
            IData ecCommonInfo = offerData.getData("EC_COMMON_INFO");
            IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
            contractSet.add(contractInfo.getString("CONTRACT_ID"));

        }

        for(String contractId : contractSet){

            log.debug("IBSYSID_CONTRACT_ID:"+contractId);

            AgreementInfoBean.updateIbsysid(contractId,ibsysid);
        }

        return new DataMap();
    }
}
