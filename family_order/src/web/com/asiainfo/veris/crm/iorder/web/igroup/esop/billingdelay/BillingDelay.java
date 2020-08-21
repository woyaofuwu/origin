package com.asiainfo.veris.crm.iorder.web.igroup.esop.billingdelay;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class BillingDelay extends EopBasePage {

    //流程ID
    private final static String BPM_TEMPLET_ID = "ACCEPTANCEPERIODCHGAUDIT";

    //业务操作类型编码
    private final static String BUSI_OPER_TYPE = "60";

    public abstract void setInAttr(IData inAttr);

    public abstract void setPattrs(IDataset pattrs);

    public abstract void setInfo(IData info);

    public abstract void setBusiInfo(IData busiInfo);

    public abstract void setBilingData(IData bilingData);

    public abstract void setSubscribeDatas(IDataset subscribeDatas);

    public abstract void setSubscribeData(IData SubscribeData);

    public abstract void setProductInfo(IData productInfo);

    public abstract void setOldBilingDatas(IDataset oldBilingDatas);

    public abstract void setOldBilingData(IData oldBilingData);

    public void initPage(IRequestCycle cycle) throws Exception {

    }

    public void queryInfosByIbsysid(IRequestCycle cycle) throws Exception {
        String ibsysid = getData().getString("IBSYSID");
        IData param = new DataMap();
        IDataset pattrs = new DatasetList();
        IData bilingData = new DataMap();
        String groupId = "";
        String busiCode = "";
        String acceptDate = "";
        String startDate = "";
        String endDate = "";
        param.put("IBSYSID", ibsysid);
        IData subscribeData = CSViewCall.callone(this, "SS.WorkformSubscribeSVC.qryScribeInfoByIbsysidForOpen", param);
        if(IDataUtil.isEmpty(subscribeData)) {
            subscribeData = CSViewCall.callone(this, "SS.WorkformSubscribeSVC.qryScribeHInfoByIbsysidForOpen", param);
        }
        if(IDataUtil.isNotEmpty(subscribeData)) {
            busiCode = subscribeData.getString("BUSI_CODE");
            groupId = subscribeData.getString("GROUP_ID");
            acceptDate = subscribeData.getString("ACCEPT_TIME");
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据开通单订单流水号【" + ibsysid + "】与集团编码【" + getData().getString("GROUP_ID") + "】未查询到开通单！");
        }
        if("7010".equals(busiCode)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "语音专线不支持计费延期！");
        }

        IData otherParam = new DataMap();
        otherParam.put("IBSYSID", ibsysid);
        otherParam.put("NODE_ID", "apply");
        IDataset otherList = CSViewCall.call(this, "SS.WorkformOtherSVC.qryByIbsysidNodeId", otherParam);
        if(IDataUtil.isEmpty(otherList)) {
            otherList = CSViewCall.call(this, "SS.WorkformOtherSVC.qryHOtherByIbsysidNodeId", otherParam);
        }
        //取计费方式
        String accptValue = "";
        if(IDataUtil.isNotEmpty(otherList)) {
            for (int i = 0; i < otherList.size(); i++) {
                IData otherData = otherList.getData(i);
                if("ACCEPTTANCE_PERIOD".equals(otherData.getString("ATTR_CODE"))) {
                    if("0".equals(otherData.getString("ATTR_VALUE"))) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "立即计费的开通单不予许延期计费！");
                    }else{
                        accptValue = otherData.getString("ATTR_VALUE");
                    }
                }
            }
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到此开通单的计费方式，无法进行计费延期！");
        }

        IDataset productSubList = CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductByIbsysid", param);
        if(IDataUtil.isEmpty(productSubList)) {
            productSubList = CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductHByIbsysid", param);
        }
        boolean flagDelay=false;//只要有一笔线可以延期时
        for (int i = 0; i < productSubList.size(); i++) {
            String tradeId = productSubList.getData(i).getString("TRADE_ID");
            if(StringUtils.isBlank(tradeId)) {
//                CSViewException.apperr(CrmCommException.CRM_COMM_103, "此开通单存在CRM工单未完工专线，无法进行计费延期！");
            }
            String userId = productSubList.getData(i).getString("USER_ID");
            IData pattr = new DataMap();

            if(StringUtils.isBlank(userId)) {
            	pattr.put("USER_ID", "");
                pattr.put("PRODUCT_ID","");
                pattr.put("PRODUCT_NAME","");
                pattr.put("IS_SEL","1");
                pattr.put("END_DATE","");
                pattr.put("LINE_STATE","未生成");

            }else{
            	IData mebUserInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userId);
                IData input = new DataMap();
                input.put("USER_ID", userId);
                input.put("DISCNT_CODE", EcConstants.ZERO_DISCNT_CODE);
                input.put("ROUTE_EPARCHY_CODE", getVisit().getLoginEparchyCode());
                IDataset userDiscntDatas = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.queryDiscntByUserIdAndDiscntCode", input);
                //存在零元资费
                if(IDataUtil.isNotEmpty(userDiscntDatas)) {
                    //yyyy-mm-dd hh24:mi:ss
                    endDate = userDiscntDatas.first().getString("END_DATE");
                    int compareToResult = SysDateMgr.compareTo(endDate, SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
                    if(compareToResult <= 0) {
//                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "此开通单存在已经开始计费专线，无法进行计费延期！");
                        pattr.put("END_DATE","已计费");
                        pattr.put("IS_SEL","1");
                        pattr.put("LINE_STATE","不可用");
                    }
                    else{
                    	flagDelay=true;
                        pattr.put("END_DATE",endDate);
                        pattr.put("IS_SEL","0");
                        pattr.put("LINE_STATE","可变更");
                    }
                    startDate = userDiscntDatas.first().getString("START_DATE");
                } else {//不存在零元资费
                    pattr.put("END_DATE","已计费");
                    pattr.put("IS_SEL","1");
                    pattr.put("LINE_STATE","不可用");
//                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "此开通单存在已经开始计费专线，无法进行计费延期！");
                    /*userDiscntDatas = CSViewCall.call(this, "SS.UserDiscntInfoQrySVC.queryUserAllDisnctByCodeNow", input);
                    if(IDataUtil.isNotEmpty(userDiscntDatas)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "未查询到此开通单专线计费信息，无法进行计费延期！");
                    }
                    String startTime = userDiscntDatas.first().getString("START_DATE");
                    int compareToResult = SysDateMgr.compareTo(startTime, SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
                    if(compareToResult <= 0) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "此开通单存在已经开始计费专线，无法进行计费延期！");
                    }*/
                }
                IDataset userDataLines = CSViewCall.call(this, "SS.QcsGrpIntfSVC.getProductInfoForPboss", input);
                IData userDataLine = userDataLines.first();
                IDataset esopParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
                Iterator<String> itr = userDataLine.keySet().iterator();
                while (itr.hasNext()) {
                    String attrCode = itr.next();
                    for (int j = 0; j < esopParam.size(); j++) {
                        String paramValue = esopParam.getData(j).getString("PARAMVALUE");
                        if(attrCode.equals(paramValue)) {
                            pattr.put(esopParam.getData(j).getString("PARAMNAME"), userDataLine.getString(attrCode));
                            break;
                        }
                    }
                }
                pattr.put("USER_ID", userDataLine.getString("USER_ID"));
                pattr.put("PRODUCT_ID", mebUserInfo.getString("PRODUCT_ID"));
                pattr.put("PRODUCT_NAME", mebUserInfo.getString("PRODUCT_NAME"));
            }
            pattr.put("IBSYSID", ibsysid);
            pattrs.add(pattr);
        }
        if(!flagDelay){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "此开通单无可变更的专线，无法进行计费延期！");
        }
        setPattrs(pattrs);

        if(StringUtils.isNotBlank(groupId)) {
            queryCustGroupByGroupId(groupId);
        }

        IData busiInfo = new DataMap();
        IData eosBusiData = new DataMap();
        IData input = new DataMap();
        input.put("BUSI_CODE", busiCode);
        input.put("OPER_TYPE", BUSI_OPER_TYPE);
        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
        if(IDataUtil.isEmpty(busiSpecReleList)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未查询到流程配置信息！");
        }
        eosBusiData.put("BUSI_SPEC_RELE", busiSpecReleList.first());

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
        eosBusiData.put("NODE_TEMPLETE", nodeTempletedData);
        busiInfo.put("EOS_BUSI_DATA", eosBusiData);
        setBusiInfo(busiInfo);

        bilingData.put("ACCEPT_DATE", acceptDate);
        bilingData.put("START_DATE", startDate);
        bilingData.put("END_DATE", endDate);
        bilingData.put("IBSYSID", ibsysid);
        bilingData.put("BUSI_CODE", busiCode);
        bilingData.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", busiCode));
        //计费时间月差值
        int monthInterval = SysDateMgr.monthInterval(startDate, endDate);
        bilingData.put("MONTH_INTERVAL", monthInterval + 1);
        setBilingData(bilingData);

        //查延期计费历史记录
        IData oldParam = new DataMap();
        oldParam.put("ATTR_CODE", "IBSYSID");
        oldParam.put("ATTR_VALUE", ibsysid);
        IDataset oldIbsysidList = CSViewCall.call(this, "SS.WorkformAttrSVC.qryHisEopAttrByAttrCodeAttrValue", oldParam);
        if(IDataUtil.isNotEmpty(oldIbsysidList)) {
            IDataset oldBilingList = new DatasetList();
            for (int i = 0, size = oldIbsysidList.size(); i < size; i++) {
                IData oldBilingData = new DataMap();
                IData oldIbsysidData = oldIbsysidList.getData(i);
                String oldIbsysid = oldIbsysidData.getString("IBSYSID");
                oldBilingData.put("IBSYSID", oldIbsysid);
                oldBilingData.put("OPEN_IBSYSID", ibsysid);

                IData attrParam = new DataMap();
                attrParam.put("IBSYSID", oldIbsysid);
                attrParam.put("NODE_ID", "apply");
                IDataset attrs = CSViewCall.call(this, "SS.WorkformAttrSVC.qryNewHisEopAttrByIbsysidAndNodeId", attrParam);
                if(IDataUtil.isNotEmpty(attrs)) {
                    for (int j = 0, sizej = attrs.size(); j < sizej; j++) {
                        IData attr = attrs.getData(j);
                        if("NEW_ACCEPTTANCE_PERIOD".equals(attr.getString("ATTR_CODE"))) {
                            String attrValue = attr.getString("ATTR_VALUE");
                            oldBilingData.put("NEW_ACCEPTTANCE_PERIOD", StaticUtil.getStaticValue("EOP_BILLING_DELAY", attrValue));
                            break;
                        }
                    }
                }
                oldBilingData.put("PRODUCT_ID", busiCode);
                oldBilingData.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", busiCode));
                oldBilingList.add(oldBilingData);
            }
            setOldBilingDatas(oldBilingList);
        }
    }

    public void querySubscribeList(IRequestCycle cycle) throws Exception {
        String groupId = getData().getString("GROUP_ID");
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("START_DATE", getData().getString("START_DATE"));
        param.put("END_DATE", getData().getString("END_DATE"));
        IDataset subscribeList = CSViewCall.call(this, "SS.WorkformSubscribeSVC.qryAllScribeInfoByGroupIdForOpen", param);
        if(IDataUtil.isNotEmpty(subscribeList)) {
            for (int i = 0; i < subscribeList.size(); i++) {
                IData subscribeData = subscribeList.getData(i);
                subscribeData.put("RSRV_STR3", pageutil.getStaticValue("URGENCY_LEVEL", subscribeData.getString("RSRV_STR3")));
                subscribeData.put("PRODUCT_ID", subscribeData.getString("BUSI_CODE"));
                subscribeData.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", subscribeData.getString("BUSI_CODE")));
            }
        }
        setSubscribeDatas(subscribeList);
    }

    private void queryCustGroupByGroupId(String groupId) throws Exception {
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        setGroupInfo(group);

        String custMgrId = group.getString("CUST_MANAGER_ID");
        if(StringUtils.isNotEmpty(custMgrId)) {
            IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
            setCustMgrInfo(managerInfo);
        }
    }

    public void qryByIbsysidProductNo(IRequestCycle cycle) throws Exception {
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        String productNo = param.getString("PRODUCTNO");
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("PRODUCTNO", productNo);
        IData productInfo = CSViewCall.callone(this, "SS.WorkformAttrSVC.qryByIbsysidProductNo", data);
        productInfo.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        productInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", param.getString("PRODUCT_ID")));
        setProductInfo(productInfo);
    }

    @Override
    public void buildOtherSvcParam(IData param) throws Exception {
        IData offerData = param.getData("OFFER_DATA");
        if(IDataUtil.isNotEmpty(offerData)) {
            buildOfferData(offerData);
        }
        IData bilingData = param.getData("BILING_DATA");
        if(IDataUtil.isEmpty(offerData)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到计费延期方式信息！");
        }
        buildOfferData(bilingData, param);
    }

    private void buildOfferData(IData bilingData, IData param) throws Exception {
        String newAccepttancePeriod = bilingData.getString("NEW_ACCEPTTANCE_PERIOD");
        String auditTag = "";
        if("1".equals(newAccepttancePeriod)) {
            String startDate = bilingData.getString("START_DATE");
            String endDate = bilingData.getString("END_DATE");
            int monthInterval = SysDateMgr.monthInterval(startDate, endDate);
            if(monthInterval + 1 >= 2) {
                //在线公司审核
                auditTag = "1";
            } /*else {
                //分公司经理审核
                auditTag = "1";
              }*/
        }
        if(StringUtils.isNotBlank(auditTag)) {
            IData otherData = new DataMap();
            otherData.put("ATTR_CODE", "AUDIT_TAG");
            otherData.put("ATTR_NAME", "审核标志");
            otherData.put("ATTR_VALUE", auditTag);
            otherData.put("RECORD_NUM", "0");
            param.put("OTHER_LIST", new DatasetList(otherData));
        }
    }

    private void buildOfferData(IData offerData) throws Exception {
        IDataset suboffers = offerData.getDataset("SUBOFFERS");
        if(IDataUtil.isNotEmpty(suboffers)) {
            String userIdB = suboffers.first().getString("USER_ID");
            String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, offerData.getString("OFFER_CODE"));
            IData input = new DataMap();
            input.put("USER_ID_B", userIdB);
            input.put("RELATION_TYPE_CODE", relationTypeCode);
            IData relaData = CSViewCall.callone(this, "SS.RelaUUInfoQrySVC.getRelaByUserIdB", input);
            if(IDataUtil.isEmpty(relaData)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "未查询到集团用户！");
            }
            String userIdA = relaData.getString("USER_ID_A");
            String serialNumberA = relaData.getString("SERIAL_NUMBER_A");
            //拼product表信息
            offerData.put("USER_ID", userIdA);
            offerData.put("SERIAL_NUMBER", serialNumberA);
            offerData.put("OFFER_ID", IUpcViewCall.getOfferIdByOfferCode(offerData.getString("OFFER_CODE")));

            //拼productSub表信息
            for (int i = 0; i < suboffers.size(); i++) {
                IData suboffer = suboffers.getData(i);
                suboffer.put("OFFER_ID", IUpcViewCall.getOfferIdByOfferCode(suboffer.getString("OFFER_CODE")));
                IData mebUserInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, suboffer.getString("USER_ID"));
                if(IDataUtil.isNotEmpty(mebUserInfo)) {
                    suboffer.put("SERIAL_NUMBER", mebUserInfo.getString("SERIAL_NUMBER"));
                }
            }
        }
    }

}
