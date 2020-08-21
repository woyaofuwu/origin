package com.asiainfo.veris.crm.iorder.web.igroup.esop.accountmanagement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class AccountManagement extends EopBasePage {

    private final static String BPM_TEMPLET_ID = "ACCTMANAGEMENT";

    private final static String BUSI_OPER_TYPE = "64";

    public abstract void setInfo(IData info);

    public abstract void setOfferList(IDataset offerList);

    public abstract void setProductInfo(IData productInfo);

    public abstract void setPattrs(IDataset pattrs);

    public abstract void setAcctList(IDataset acctList);

    //public abstract void setInfoCount(long infoCount);

    @Override
    public void initPage(IRequestCycle cycle) throws Exception {

    }

    public void qryLineInfos(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String groupId = data.getString("GROUP_ID");
        if(StringUtils.isBlank(groupId)) {
            CSViewException.apperr(GrpException.CRM_GRP_713, "未获取到集团编码！");
        }
        //String serialNumber = data.getString("cond_SERIAL_NUMBER");
        //String productNo = data.getString("cond_PRODUCTNO");
        IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        setGroupInfo(groupInfo);
        String acctOperType = data.getString("ACCT_OPERTYPE");

        IData param = new DataMap();
        param.put("GROUP_ID", groupId);

        //param.put("SERIAL_NUMBER", serialNumber);
        //param.put("PRODUCT_NO", productNo);
        param.put("ACCT_OPERTYPE", acctOperType);
        IDataset lineInfos = CSViewCall.call(this, "SS.GrpLineInfoQrySVC.qryLineInfoAndAcctInfo", param/*, getPagination("navbar1")*/);
        setPattrs(lineInfos);
    }

    public void qryAcctList(IRequestCycle cycle) throws Exception {
        IData param = new DataMap();
        param.put("CUST_ID", getData().getString("CUST_ID"));
        IDataset idata = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctUserInfoByCustIDForGrpNoPage", param);
        if(idata != null && idata.size() > 0) {
            for (int i = 0, size = idata.size(); i < size; i++) {
                IData data = idata.getData(i);
                String dataName = data.getString("ACCT_ID") + " | " + data.getString("PAY_NAME") + " | " + data.getString("PAY_MODE_NAME") + " | " + data.getString("EPARCHY_NAME");
                data.put("DATA_NAME", dataName);
            }
        }
        setAcctList(idata);
    }

    public void checkAcctInfo(IRequestCycle cycle) throws Exception {
        String sp = getData().getString("SUBMIT_PARAM");
        IData param = new DataMap(sp);
        IDataset resultList = new DatasetList();
        String operType = param.getString("OPERTYPE");
        String acctId = param.getString("ACCT_ID");
        if("1".equals(operType)){
            IData data1 = new DataMap();
            data1.put("ACCT_ID", acctId);
            IData balaceData = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryAcctInfo", data1);
            if(IDataUtil.isEmpty(balaceData)) {
                CSViewException.apperr(GrpException.CRM_GRP_713, "未查询到账户" + acctId + "余额！");
            }
            String allBalance = balaceData.getString("ALL_BALANCE");//ALL_BALANCE
            IData result = new DataMap();
            if(Double.valueOf(allBalance) > 0) {
                result.put("RESULT", "账户【" + acctId + "】当前有预存款" + allBalance + "，请于账户拆分完后，通过OA调账工单进行预存款转存！");
                resultList.add(result);
            } else if(Double.valueOf(allBalance) < 0) {
                CSViewException.apperr(GrpException.CRM_GRP_713, "账户【" + acctId + "】实时余额小于0元，不允许账户拆分或合并");
            }
        }
        IDataset attrList = param.getDataset("EOMS_ATTR_LIST");
        if(IDataUtil.isEmpty(attrList)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到用户数据！");
        }
        for (int i = 0, size = attrList.size(); i < size; i++) {
            IData attrData = attrList.getData(i);
            String attrCode = attrData.getString("ATTR_CODE");
            String attrValue = attrData.getString("ATTR_VALUE");
            if("SERIAL_NUMBER".equals(attrCode)) {
                IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, attrValue);
                String userId = userInfo.getString("USER_ID");
                if("1".equals(operType)) {//账户拆分
                    IData acctParam = new DataMap();
                    acctParam.put("USER_ID", userId);
                    IData acctInfo = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryDefaultPayRelaByUserId", acctParam);
                    if(IDataUtil.isEmpty(acctInfo)) {
                        CSViewException.apperr(GrpException.CRM_GRP_713, "未查询到用户" + userId + "默认付费关系！");
                    }
                    acctParam.clear();
                    acctParam.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
                    IData balaceData = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryAcctInfo", acctParam);
                    if(IDataUtil.isEmpty(balaceData)) {
                        CSViewException.apperr(GrpException.CRM_GRP_713, "未查询到账户" + acctInfo.getString("ACCT_ID") + "余额！");
                    }
                    String allBalance = balaceData.getString("ALL_BALANCE");//ALL_BALANCE
                    if(Double.valueOf(allBalance) > 0) {
                        IData result = new DataMap();
                        result.put("RESULT", "账户【" + acctInfo.getString("ACCT_ID") + "】当前有预存款" + allBalance + "，请于账户拆分完后，通过OA调账工单进行预存款转存！");
                        resultList.add(result);
                    } else if(Double.valueOf(allBalance) < 0) {
                        CSViewException.apperr(GrpException.CRM_GRP_713, "账户【" + acctInfo.getString("ACCT_ID") + "】实时余额小于0元，不允许账户拆分或合并");
                    }
                } else if("2".equals(operType)) {//账户合并
                    IData grpAcctParam = new DataMap();
                    grpAcctParam.put("USER_ID", userId);
                    IData grpAcctInfo = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryLineGrpAcctByMebUserId", grpAcctParam);
                    if(IDataUtil.isEmpty(grpAcctInfo)) {
                        CSViewException.apperr(GrpException.CRM_GRP_713, "未查询到用户" + userId + "所在集团用户的默认账户信息！");
                    }
                    String grpAcctId = grpAcctInfo.getString("ACCT_ID");
                    IData acctParam = new DataMap();
                    acctParam.put("ACCT_ID", grpAcctId);
                    IData grpBalaceData = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryAcctInfo", acctParam);
                    if(IDataUtil.isEmpty(grpBalaceData)) {
                        CSViewException.apperr(GrpException.CRM_GRP_713, "未查询到账户" + grpAcctId + "余额！");
                    }
                    String allBalance = grpBalaceData.getString("ALL_BALANCE");
                    if(Double.valueOf(allBalance) > 0) {
                        CSViewException.apperr(GrpException.CRM_GRP_713, "被合账户【" + grpAcctId + "】当前有预存款" + allBalance + "，请通过OA调账工单进行预存款转存完成后才能合并！");
                    } else if(Double.valueOf(allBalance) < 0) {
                        CSViewException.apperr(GrpException.CRM_GRP_713, "账户【" + grpAcctId + "】实时余额小于0元，不允许账户拆分或合并");
                    }
                }
            }
        }
        setAjax(resultList);
    }

    @Override
    public void buildOtherSvcParam(IData param) throws Exception {
        //拼productSub表信息
        IDataset attrList = param.getDataset("EOMS_ATTR_LIST");
        String productId = "";
        if(IDataUtil.isNotEmpty(attrList)) {
            IData offerData = new DataMap();
            IDataset suboffers = new DatasetList();
            for (int i = 0; i < attrList.size(); i++) {
                IData attrData = attrList.getData(i);
                String attrCode = attrData.getString("ATTR_CODE");
                String value = attrData.getString("ATTR_VALUE");
                if("SERIAL_NUMBER".equals(attrCode)) {
                    productId = attrData.getString("PRODUCT_ID");
                    IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, value);
                    IData suboffer = new DataMap();
                    suboffer.put("SERIAL_NUMBER", value);
                    suboffer.put("USER_ID", userInfo.getString("USER_ID"));
                    suboffer.put("OFFER_TYPE", "P");
                    suboffer.put("OFFER_CODE", userInfo.getString("PRODUCT_ID"));
                    suboffer.put("OFFER_NAME", userInfo.getString("PRODUCT_NAME"));
                    suboffer.put("OFFER_ID", IUpcViewCall.getOfferIdByOfferCode(userInfo.getString("PRODUCT_ID")));
                    suboffers.add(suboffer);
                }
            }
            //转换产品编码
            if("97011".equals(productId)) {
                productId = "7011";
            } else if("97012".equals(productId)) {
                productId = "7012";
            } else if("97016".equals(productId)) {
                productId = "7016";
            } else if("970111".equals(productId)) {
                productId = "70111";
            } else if("970112".equals(productId)) {
                productId = "70112";
            } else if("970121".equals(productId)) {
                productId = "70121";
            } else if("970122".equals(productId)) {
                productId = "70122";
            }
            offerData.put("SUBOFFERS", suboffers);
            offerData.put("OFFER_CODE", productId);
            offerData.put("OFFER_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", productId));
            param.put("OFFER_DATA", offerData);
        }

        IData commonData = new DataMap();
        commonData.put("PRODUCT_ID", productId);
        param.put("COMMON_DATA", commonData);
        
        IData input = new DataMap();
        input.put("BUSI_CODE", productId);
        input.put("OPER_TYPE", BUSI_OPER_TYPE);
        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
        if(IDataUtil.isEmpty(busiSpecReleList)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未查询到流程配置信息！");
        }
        param.put("BUSI_SPEC_RELE", busiSpecReleList.first());

        input.clear();
        input.put("BPM_TEMPLET_ID", busiSpecReleList.first().getString("BPM_TEMPLET_ID"));
        input.put("NODE_TYPE", "3");
        IDataset nodeTempleteList = CSViewCall.call(this, "SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
        if(IDataUtil.isEmpty(nodeTempleteList)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未查询到流程配置开始节点！");
        }
        IData nodeTempletedData = new DataMap();
        String bpmTempletId = nodeTempleteList.first().getString("BPM_TEMPLET_ID");
        nodeTempletedData.put("BPM_TEMPLET_ID", bpmTempletId);
        nodeTempletedData.put("NODE_ID", nodeTempleteList.first().getString("NODE_ID"));
        param.put("NODE_TEMPLETE", nodeTempletedData);
    }
}
