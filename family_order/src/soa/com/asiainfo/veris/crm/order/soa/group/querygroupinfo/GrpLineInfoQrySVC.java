package com.asiainfo.veris.crm.order.soa.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;

public class GrpLineInfoQrySVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset qryGroupLineInfos(IData param) throws Exception {
        return GrpLineInfoQryBean.qryGroupLineInfos(param);
    }

    public IDataset qryLineInfoByUserId(IData param) throws Exception {
        // String groupId = param.getString("GROUP_ID");
        // String serialNumber = param.getString("SERIAL_NUMBER");
        // String productNo = param.getString("PRODUCT_NO");
        // String sheetType = param.getString("SHEET_TYPE");
        //
        // IData inparam = new DataMap();
        // if(StringUtils.isNotBlank(serialNumber) && NumberUtils.isNumber(arg0)) {
        //
        // }
        return GrpLineInfoQryBean.qryLineInfoByUserId(param/*, this.getPagination()*/);
    }

    public IDataset qryLineInfoAndAcctInfo(IData param) throws Exception {
        return GrpLineInfoQryBean.qryLineInfoAndAcctInfo(param);
    }

    public IDataset qryLineInfoStopOrBack(IData param) throws Exception {
        GrpLineInfoQryBean grpLineInfoQryBean = new GrpLineInfoQryBean();
        return grpLineInfoQryBean.qryLineInfoStopOrBack(param, null);
    }

    public IDataset qryLineInfosForRedList(IData param) throws Exception {
        String isRed = param.getString("IS_RED");
        if(StringUtils.isBlank(isRed)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "请先选择是否已添加红名单！");
        }
        IDataset results = new DatasetList();
        IDataset lineInfos = GrpLineInfoQryBean.qryLineInfoByUserId(param);
        if(DataUtils.isNotEmpty(lineInfos)) {
            for (int i = 0; i < lineInfos.size(); i++) {
                IData data = lineInfos.getData(i);
                IData inparam = new DataMap();
                inparam.put("USER_ID", data.getString("USER_ID"));
                inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                IData isRedData = null;

                //如果调信控接口报错则直接跳过该用户
                //try{
                    //若为空，或者ACT_TAG为0则不是红名单
                    isRedData = CreditCall.queryIsRedList(inparam);
                //} catch (Exception e) {
                //isRedData = new DataMap();
                //isRedData.put("EXP_TAG", "true");
                //}
                //0为非红名单，1是红名单
                if("0".equals(isRed)&&(IDataUtil.isEmpty(isRedData)||"0".equals(isRedData.getString("ACT_TAG")))) {
                    results.add(data);
                } else if("1".equals(isRed) && IDataUtil.isNotEmpty(isRedData) && "1".equals(isRedData.getString("ACT_TAG"))) {
                    results.add(data);
                }
            }
        }
        return results;
    }

    public IData queryDataLineUserInfo(IData param) throws Exception {
        String userId = param.getString("USER_ID");
        return GrpLineInfoQryBean.queryDataLineUserInfo(userId);
    }

    public IData queryLineGrpAcctByMebUserId(IData param) throws Exception {
        String userId = param.getString("USER_ID");
        return GrpLineInfoQryBean.queryLineGrpAcctByMebUserId(userId);
    }

    public IData queryDefaultPayRelaByUserId(IData param) throws Exception {
        String userId = param.getString("USER_ID");
        return UcaInfoQry.qryDefaultPayRelaByUserIdForGrp(userId);
    }

    public IData queryAcctInfo(IData param) throws Exception {
        String acctId = param.getString("ACCT_ID");
        return GrpLineInfoQryBean.queryAcctInfo(acctId);
    }

    public IData queryContractInfo(IData param) throws Exception {
        String contractId = param.getString("CONTRACT_ID");
        String custId = param.getString("CUST_ID");
        return GrpLineInfoQryBean.queryContractInfo(contractId, custId);
    }
    
    public IData queryLineByProductNO(IData param) throws Exception {
        String productNO = param.getString("PRODUCTNO");
        return GrpLineInfoQryBean.queryLineByProductNO(productNO);
    }

    public IDataset queryLinePayrelation(IData param) throws Exception {
        return GrpLineInfoQryBean.queryLinePayrelation(param, getPagination());
    }
    
    public IDataset queryLineByGroupIdSnProductNo(IData param) throws Exception{
        return GrpLineInfoQryBean.queryLineByGroupIdSnProductNo(param);
    }
}
