package com.asiainfo.veris.crm.iorder.web.igroup.esop.applyrequirement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userotherinfo.UserOtherInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class ApplyRequirement extends EopBasePage {

    private static final Logger logger = Logger.getLogger(ApplyRequirement.class);

    private IDataset staffList = new DatasetList();

    public void initPage(IRequestCycle cycle) throws Exception {
        String ibsysId = getData().getString("IBSYSID");
        if (StringUtils.isBlank(ibsysId)) {
            return;
        }

        super.initPage(cycle);

        IData repInfo = new DataMap();
        String bpmTemplateId = getData().getString("BPM_TEMPLET_ID");
        String busiformId = getData().getString("BUSIFORM_ID");
        String busiformNodeId = getData().getString("BUSIFORM_NODE_ID");
        String busiOperType = getData().getString("BUSIFORM_OPER_TYPE");
        String offerCode = getData().getString("BUSI_CODE");
        String nodeId = getData().getString("NODE_ID");

        IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysId);
        if (IDataUtil.isEmpty(subscribeData)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工单IBSYSID=【" + ibsysId + "】不存在！");
        }
        subscribeData.put("NODE_ID", nodeId);
        subscribeData.put("BUSIFORM_NODE_ID", busiformNodeId);
        // String bpmTempletId = subscribeData.getString("BPM_TEMPLET_ID");
        IData info = new DataMap();
        // 处理产品信息
        String templetId = subscribeData.getString("RSRV_STR2");
        IData inparam = new DataMap();
        inparam.put("PRODUCT_ID", offerCode);
        inparam.put("BUSI_CODE", templetId);
        inparam.put("BUSI_OPER_TYPE", busiOperType);
        IDataset busiInfos = CSViewCall.call(this, "SS.BusiFlowReleSVC.getOperTypeByBusiCode", inparam);
        if (IDataUtil.isEmpty(busiInfos)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据BUSI_CODE=" + templetId + "未查询到TD_B_EOP_BUSI_FLOW_RELE表信息！");
        }
        String operCode = busiInfos.first().getString("BUSI_TYPE");
        String inModeCode = subscribeData.getString("IN_MODE_CODE", "0");
        IDataset templetIDList = WorkfromViewCall.qryBusiTypeByProductId(this, operCode, offerCode, "ZZZZ", inModeCode);
        info.put("TEMPLET_ID_LIST", templetIDList);
        info.put("IBSYSID", ibsysId);

        dealProductInfo(info);
        String operType = "20";
        if ("change".equals(operCode)) {
            operType = "21";
        } else if ("cancel".equals(operCode)) {
            operType = "23";
        }
        info.put("TEMPLET_ID", templetId);
        info.put("OPER_TYPE", operType);
        String showParamPart = "1";
        if ("7010".equals(offerCode) || "7011".equals(offerCode) || "7012".equals(offerCode)|| "7016".equals(offerCode)
        		|| "70111".equals(offerCode)|| "70112".equals(offerCode)|| "70121".equals(offerCode)|| "70122".equals(offerCode)) {
            showParamPart = "2";
        }
        info.put("SHOW_PARAM_PART", showParamPart);

        // 查询产品信息与ATTR表信息
        queryOfferAttr(info);

        IData input = new DataMap();
        input.put("BUSI_CODE", offerCode);
        input.put("OPER_TYPE", busiOperType);
        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
        busiSpecReleList.first().put("TEMPLET_ID", templetId);
        info.put("BUSI_SPEC_RELE", busiSpecReleList.first());

        IData nodeTempletedData = new DataMap();
        String bpmTempletId = busiSpecReleList.first().getString("BPM_TEMPLET_ID");
        nodeTempletedData.put("BPM_TEMPLET_ID", bpmTempletId);
        nodeTempletedData.put("NODE_ID", nodeId);
        info.put("NODE_TEMPLETE", nodeTempletedData);
        if ("ETAPMARKETINGENTERING".equals(templetId) || "ETAPMARKETINGEXCITATION".equals(templetId)) {
            IDataset pattrList = new DatasetList();
            getEopAttrToList(ibsysId, pattrList, info);
            setPattrList(pattrList);
            if ("ETAPMARKETINGENTERING".equals(templetId)) {
                for (int j = 0; j < pattrList.size(); j++) {
                    IData pattrData = pattrList.getData(j);
                    if ("1".equals(pattrData.getString("C_TAPMARKETING_OPERATION", "")) || "2".equals(pattrData.getString("C_TAPMARKETING_OPERATION", ""))) {
                        bpmTemplateId = "ETAPMARKETINGENTERINGUPD";
                        break;
                    }

                }
            }
        }// 挖抢数据特殊处理

        // 营销活动特殊处理
        if ("EDIRECTLINEMARKETINGADD".equals(bpmTemplateId)) {
            IData commInfo = new DataMap();
            commInfo.put("FLOW_ID", bpmTempletId); // POINT_ONE
            commInfo.put("NODE_ID", nodeId); // POINT_TWO
            commInfo.put("PRODUCT_ID", offerCode);
            commInfo.put("PAGE_LEVE", templetId);
            setComminfo(commInfo);
        }

        // 加载专线用户SERIAL_NUMBER列表
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, getData().getString("GROUP_ID"));
        IData snParam = new DataMap();
        snParam.put("CUST_ID", group.getString("CUST_ID"));
        snParam.put("PRODUCT_ID", offerCode);
        IDataset datalineSerialNumberList = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", snParam);
        info.put("DATALINE_SERIAL_NUMBER_LIST", datalineSerialNumberList);

        info.put("IS_READONLY", "true");
        // 获取填过的项目名称
        IDataset projectName = initProjectName(group);
        info.put("PROJECTNAMES", projectName);

        repInfo.put("USER_ID", info.getString("USER_ID", ""));
        // repInfo.put("SERIAL_NUMBER", mainSerialNumber);
        repInfo.put("BUSIFORM_OPER_TYPE", busiOperType);
        repInfo.put("IBSYSID", ibsysId);
        // repInfo.put("SUB_IBSYSID", sub_ibsysId);
        repInfo.put("NODE_ID", nodeId);
        repInfo.put("BPM_TEMPLET_ID", bpmTemplateId);
        repInfo.put("BUSIFORM_ID", busiformId);
        repInfo.put("BUSIFORM_NODE_ID", busiformNodeId);
        repInfo.put("BUSI_CODE", offerCode);
        repInfo.put("IS_READONLY", "true");// 审核不通过时

        repInfo.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));

        setRepInfo(repInfo);
        setInfo(info);

    }

    private void dealProductInfo(IData info) throws Exception {

        String ibsysId = getData().getString("IBSYSID");
        IData eopProductData = WorkfromViewCall.qryEopProductByIbsysId(this, ibsysId, "0");
        String mainUserId = "";
        String mainSerialNumber = "";
        if (IDataUtil.isNotEmpty(eopProductData)) {
            mainUserId = eopProductData.getString("USER_ID");
            mainSerialNumber = eopProductData.getString("SERIAL_NUMBER");
        }
        if (StringUtils.isBlank(mainSerialNumber)) {
            info.put("DATALINE_OPER_TYPE", "0");
        } else {
            info.put("DATALINE_OPER_TYPE", "1");
            info.put("DATALINE_SERIAL_NUMBER", mainSerialNumber);
        }
        info.put("USER_ID", mainUserId);
        info.put("READONLY", "true");
    }

    /**
     * 查询集团客户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCustGroupByGroupId(IRequestCycle cycle) throws Exception {
        String groupId = getData().getString("GROUP_ID");

        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        String custId = group.getString("CUST_ID");

        setGroupInfo(group);

        String custMgrId = group.getString("CUST_MANAGER_ID");
        if (StringUtils.isNotEmpty(custMgrId)) {
            IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
            setCustMgrInfo(managerInfo);
        }
    }

    public void queryDataLineInfo(IRequestCycle cycle) throws Exception {
        IData input = new DataMap();
        input.put("IBSYSID", getData().getString("IBSYSID"));
        input.put("NODE_ID", getData().getString("NODE_ID"));
        IDataset dataLineInfoList = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewLineInfoList", input);
        if (IDataUtil.isNotEmpty(dataLineInfoList)) {
            setAjax(dataLineInfoList);
        }

    }

    public void queryMarketingLineList(IRequestCycle cycle) throws Exception {
        queryInfo(cycle);
        IData input = new DataMap();
        input.put("IBSYSID", getData().getString("IBSYSID"));
        input.put("NODE_ID", getData().getString("NODE_ID"));
        IData commonIdata = new DataMap();
        IDataset dataset = new DatasetList();
        getEopAttrToList(getData().getString("IBSYSID"), dataset, commonIdata);

        if (IDataUtil.isNotEmpty(dataset)) {
            IDataset renewModeList = StaticUtil.getStaticList("MARKETING_RENEWMODE");
            if (IDataUtil.isEmpty(renewModeList)) {
                CSViewException.apperr(VpmnUserException.VPMN_USER_226, "客户续签参数未配置，请检查参数后继续操作");
            }
            IDataset friendBusinessList = StaticUtil.getStaticList("MARKETING_FRIENDBUSINESS_NAME");
            if (IDataUtil.isEmpty(friendBusinessList)) {
                CSViewException.apperr(VpmnUserException.VPMN_USER_226, "友商名称参数未配置，请检查参数后继续操作");
            }
            for (int i = 0, sizei = dataset.size(); i < sizei; i++) {
                IData dataIn = dataset.getData(i);
                String renewModeStr = dataIn.getString("NOTIN_RENEWMODE", "").trim();
                for (int j = 0, sizej = renewModeList.size(); j < sizej; j++) {
                    IData renewModeData = renewModeList.getData(j);
                    if (renewModeData.getString("DATA_ID", "").equals(renewModeStr)) {
                        dataIn.put("NOTIN_RENEWMODE_STRING", renewModeData.getString("DATA_NAME", ""));
                        break;
                    }
                }
                String friendBusinessStr = dataIn.getString("NOTIN_FRIENDBUSINESS_NAME", "").trim();
                for (int z = 0, sizez = friendBusinessList.size(); z < sizez; z++) {
                    IData friendBusinessData = friendBusinessList.getData(z);
                    if (friendBusinessData.getString("DATA_ID", "").equals(friendBusinessStr)) {
                        dataIn.put("NOTIN_FRIENDBUSINESS_NAME_STRING", friendBusinessData.getString("DATA_NAME", ""));
                        break;
                    }
                }

            }
            setPattrInfo(commonIdata);
            setPattrList(dataset);
            setAjax(dataset);
        }

        // 仅查询公共信息
        IData inputAtt = new DataMap();
        inputAtt.put("IBSYSID", getData().getString("IBSYSID"));
        inputAtt.put("NODE_ID", getData().getString("NODE_ID"));
        inputAtt.put("RECORD_NUM", "0");
        IDataset attrDatas = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", inputAtt);
        if (attrDatas != null && attrDatas.size() > 0) {
            getAllAttach(attrDatas.first().getString("SUB_IBSYSID"), commonIdata);
        }
    }

    private void queryOfferAttr(IData info) throws Exception {

        String mainUserId = info.getString("USER_ID", "");
        String nodeId = getData().getString("NODE_ID");
        String offerCode = getData().getString("BUSI_CODE");
        String ibsysId = getData().getString("IBSYSID");
        String bpmTempletId = getData().getString("BPM_TEMPLET_ID");
        String offerId = IUpcViewCall.getOfferIdByOfferCode(offerCode);
        IData offerData = IUpcViewCall.getOfferInfoByOfferId(offerId);
        if (IDataUtil.isNotEmpty(offerData)) {
            offerData.put("BRAND_CODE", IUpcViewCall.queryBrandCodeByOfferId(offerId));
            offerData.put("OFFER_ID", offerId);
            offerData.put("USER_ID", mainUserId);
            setOffer(offerData);
        }
        info.put("PRODUCT_ID", offerCode);

        // 仅查询公共信息
        IData input = new DataMap();
        input.put("IBSYSID", ibsysId);
        input.put("NODE_ID", nodeId);
        input.put("RECORD_NUM", "0");
        IDataset attrDatas = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", input);
        if (IDataUtil.isEmpty(attrDatas)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "本工单[" + ibsysId + "]资料表TF_B_EOP_ATTR没有数据！");
        }

        for (int i = 0; i < attrDatas.size(); i++) {
            IData attrData = attrDatas.getData(i);
            info.put(attrData.getString("ATTR_CODE"), attrData.getString("ATTR_VALUE"));
        }

        if ("EDIRECTLINEMARKETINGADD".equals(bpmTempletId)) {
            IData paramMarke = new DataMap();
            paramMarke.put("CUSTOMNO", getData().getString("GROUP_ID"));
            paramMarke.put("RESULT_CODE", "0");
            IDataset infosMarke = CSViewCall.call(this, "SS.WorkformMarketingSVC.selMarketingByCondition2", paramMarke);
            if (null != infosMarke && infosMarke.size() > 0) {
                for (int i = 0; i < infosMarke.size(); i++) {
                    IData tempMarke = infosMarke.getData(i);
                    tempMarke.put("TEXT", tempMarke.getString("IBSYSID_MARKETING", "") + "|" + tempMarke.getString("MARKETING_NAME", ""));
                }
            }
            info.put("MARKETING_DATA", infosMarke);
        }

        setBusi(info);

        IData nodeParam = new DataMap();
        nodeParam.put("BUSIFORM_NODE_ID", getData().getString("BUSIFORM_NODE_ID"));
        IData nodeData = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryByBusiformNodeId", nodeParam);

        IData otherparam = new DataMap();
        otherparam.put("IBSYSID", ibsysId);
        otherparam.put("NODE_ID", nodeData.getString("PRE_NODE_ID"));
        IDataset otherDatas = CSViewCall.call(this, "SS.WorkformOtherSVC.qryByIbsysidNodeId", otherparam);
        if (IDataUtil.isNotEmpty(otherDatas)) {
            for (int i = 0; i < otherDatas.size(); i++) {
                IData otherData = otherDatas.getData(i);
                if ("AUDIT_TEXT".equals(otherData.getString("ATTR_CODE"))) {
                    info.put("AUDIT_TEXT", otherData.getString("ATTR_VALUE"));
                }
            }
        }

        // 加载附件
        getAllAttach(attrDatas.first().getString("SUB_IBSYSID"), info);

        // 加载账户信息
        IData acctInfo = new DataMap();
        if ("1".equals(info.getString("ACCT_DEAL", "")) && StringUtils.isNotBlank(info.getString("ACCT_ID", ""))) {
            acctInfo.put("ACCT_ID", info.getString("ACCT_ID", ""));
        }
        acctInfo.put("OPEN_DATE", info.getString("OPEN_DATE", ""));
        acctInfo.put("REGION_ID", info.getString("REGION_ID", ""));
        acctInfo.put("REGION_NAME", info.getString("REGION_NAME", ""));
        acctInfo.put("CITY_CODE", info.getString("CITY_CODE", ""));
        acctInfo.put("ACCT_NAME", info.getString("ACCT_NAME", ""));
        acctInfo.put("RSRV_STR8", info.getString("RSRV_STR8", ""));
        acctInfo.put("RSRV_STR9", info.getString("RSRV_STR9", ""));
        acctInfo.put("ACCT_TYPE", info.getString("ACCT_TYPE", ""));
        acctInfo.put("ACCT_CONTRACT_ID", info.getString("ACCT_CONTRACT_ID", ""));
        acctInfo.put("SUPER_BANK_CODE", info.getString("SUPER_BANK_CODE", ""));
        acctInfo.put("BANK_CODE", info.getString("BANK_CODE", ""));
        acctInfo.put("BANK_NAME", info.getString("BANK_NAME", ""));
        acctInfo.put("BANK_ACCT_NO", info.getString("BANK_ACCT_NO", ""));
        info.put("ACCT_INFO", acctInfo);
    }

    private void getAllAttach(String subIbsysid, IData info) throws Exception {
        String ibsysid = getData().getString("IBSYSID");
        IData inparam = new DataMap();
        inparam.put("IBSYSID", ibsysid);
        inparam.put("SUB_IBSYSID", subIbsysid);
        inparam.put("RECORD_NUM", "0");
        IDataset attachList = CSViewCall.call(this, "SS.WorkFormSVC.queryByAttach", inparam);
        if (IDataUtil.isNotEmpty(attachList)) {
            IData attachInfo = new DataMap();
            String discntFileId = "";
            String discntFileName = "";
            IDataset discntList = new DatasetList();
            String productFileId = "";
            String productFileName = "";
            IDataset productList = new DatasetList();
            String marketingFileId = "";
            String marketingFileName = "";

            IDataset marketingList = new DatasetList();

            for (int i = 0; i < attachList.size(); i++) {
                IData attachData = attachList.getData(i);// ATTACH_TYPE
                String attachType = attachData.getString("ATTACH_TYPE");
                String fileId = attachData.getString("FILE_ID");
                String fileName = attachData.getString("ATTACH_NAME");
                if ("D".equals(attachType)) {// 资费附件
                    discntFileId += fileId + ",";
                    discntFileName += fileName + ",";
                    IData discntFileData = new DataMap();
                    discntFileData.put("FILE_ID", fileId);
                    discntFileData.put("FILE_NAME", fileName);
                    discntFileData.put("ATTACH_TYPE", attachType);
                    discntList.add(discntFileData);
                } else if ("P".equals(attachType)) {// 普通附件
                    productFileId += fileId + ",";
                    productFileName += fileName + ",";
                    IData productFileData = new DataMap();
                    productFileData.put("FILE_ID", fileId);
                    productFileData.put("FILE_NAME", fileName);
                    productFileData.put("ATTACH_TYPE", attachType);
                    productList.add(productFileData);
                } else if ("T".equals(attachType)) {// 计费方式OA审批附件
                    IData speauditFileData = new DataMap();
                    speauditFileData.put("FILE_ID", fileId);
                    speauditFileData.put("FILE_NAME", fileName);
                    speauditFileData.put("ATTACH_TYPE", attachType);
                    info.put("SPEAUDIT_FILE_ID", fileId);
                    info.put("SPEAUDIT_FILE_DATA", speauditFileData);
                } else if ("M".equals(attachType)) {// 挖抢附件
                    marketingFileId += fileId + ",";
                    marketingFileName += fileName + ",";
                    IData marketingData = new DataMap();
                    marketingData.put("FILE_ID", fileId);
                    marketingData.put("FILE_NAME", fileName);
                    marketingData.put("ATTACH_TYPE", attachType);
                    marketingList.add(marketingData);
                }
            }
            if (StringUtils.isNotBlank(discntFileId)) {
                attachInfo.put("DISCNT_FILEID", discntFileId.substring(0, discntFileId.length() - 1));
                attachInfo.put("DISCNT_FILENAME", discntFileName.substring(0, discntFileName.length() - 1));
                attachInfo.put("DISCNT_LIST", discntList);
            }
            if (StringUtils.isNotBlank(productFileId)) {
                attachInfo.put("PRODUCT_FILEID", productFileId.substring(0, productFileId.length() - 1));
                attachInfo.put("PRODUCT_FILENAME", productFileName.substring(0, productFileName.length() - 1));
                attachInfo.put("PRODUCT_LIST", productList);
            }

            if (StringUtils.isNotBlank(marketingFileId)) {
                attachInfo.put("MARKETING_FILEID", marketingFileId.substring(0, marketingFileId.length() - 1));
                attachInfo.put("MARKETING_FILENAME", marketingFileName.substring(0, marketingFileName.length() - 1));
                attachInfo.put("MARKETING_LIST", marketingList);
            }
            setAttachInfo(attachInfo);
        }
    }

    public void queryBanksBySuperBank(IRequestCycle cycle) throws Exception {
        String superBank = getData().getString("SUPER_BANK_CODE");
        IData input = new DataMap();
        input.put("SUPER_BANK_CODE", superBank);
        input.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());
        IDataset datas = CSViewCall.call(this, "CS.BankInfoQrySVC.queryBackCode", input);
        if (datas == null) {
            datas = new DatasetList();
        }
        // IData info = new DataMap();
        // info.put("BANK_CODE_SEL_LIST", datas);

        setAjax(datas);
    }

    public void queryOffer(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        String offerCode = getData().getString("OFFER_CODE");
        IData offerData = UpcViewCall.queryOfferByOfferId(this, UpcConst.ELEMENT_TYPE_CODE_PRODUCT, offerCode, "Y");
        setOffer(offerData);

        String operCode = "open";
        if ("21".equals(getData().getString("OPER_TYPE"))) {
            operCode = "change";
        } else if ("23".equals(getData().getString("OPER_TYPE"))) {
            operCode = "cancel";
        }
        IData info = new DataMap();
        String inModeCode = getVisit().getInModeCode();
        IDataset templetIDList = WorkfromViewCall.qryBusiTypeByProductId(this, operCode, offerCode, "ZZZZ", inModeCode);
        info.put("TEMPLET_ID_LIST", templetIDList);

        setInfo(info);
    }

    public void queryInfo(IRequestCycle cycle) throws Exception {
        IData commInfo = new DataMap();
        IData pageData = getData();
        String offerCode = getData().getString("OFFER_CODE");
        String templetID = pageData.getString("TEMPLET_ID");
        if ("EDIRECTLINECHANGEFEE".equals(templetID)) {
            IData staffMap = new DataMap();
            staffMap.put("RIGHT_CODE", "DIRECTLINECHANGEFEE_PAGE");
            IDataset staffInfos = CSViewCall.call(this, "SS.WorkformProxySVC.getStaffInfo", staffMap);
            Boolean staffFlag = false;
            for (int i = 0; i < staffInfos.size(); i++) {
                IData staffInfo = staffInfos.getData(i);
                if (getVisit().getStaffId().equals(staffInfo.getString("STAFF_ID"))) {
                    staffFlag = true;
                    break;
                }
            }
            if (!staffFlag) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您无集团专线变更资费界面操作权限，如需操作，请联系相关人员下放权限，权限编码：" + "DIRECTLINECHANGEFEE_PAGE");
            }
        }

        if ("EDIRECTLINEOPEN".equals(templetID)) {
            String staffId = getVisit().getStaffId();
            boolean staffFlag = StaffPrivUtil.isFuncDataPriv(staffId, "SYS555");
            if (!staffFlag) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您没有该产品的优惠权限，无法继续办理！权限编码：" + "SYS555");
            }
        }

        IData offerData = UpcViewCall.queryOfferByOfferId(this, UpcConst.ELEMENT_TYPE_CODE_PRODUCT, offerCode, "Y");
        setOffer(offerData);

        // 判断集团是否可办理该产品 begin
        IData checkParam = new DataMap();
        checkParam.put("GROUP_ID", pageData.getString("GROUP_ID"));
        checkParam.put("PRODUCT_ID", pageData.getString("OFFER_CODE"));
        checkParam.put("STAFF_ID", getVisit().getStaffId());
        String checkOperCode = "";
        if ("21".equals(getData().getString("OPER_TYPE"))) {
            checkOperCode = "1";
        } else if ("23".equals(getData().getString("OPER_TYPE"))) {
            checkOperCode = "2";
        } else {
            checkOperCode = "0";
        }
        checkParam.put("OPER_CODE", checkOperCode);
        CSViewCall.call(this, "SS.QcsGrpIntfSVC.checkOperProduct", checkParam);
        // 判断集团是否可办理该产品 end

        String showParamPart = "1";
        if ("7010".equals(offerCode) || "7011".equals(offerCode) || "7012".equals(offerCode)|| "7016".equals(offerCode)
        		|| "70111".equals(offerCode)|| "70112".equals(offerCode)|| "70121".equals(offerCode)|| "70122".equals(offerCode)) {
            showParamPart = "2";
        } else if ("BOSG".equals(offerData.getString("BRAND_CODE"))) {
            showParamPart = "3";
            /* 国内400业务流程产品编码转换 包含流程： 400业务定时全量复核管控,语音专线业务管控,语音专线定时全量复核管控,400业务管控 */
            offerCode = transOfferCodeFor400(templetID, offerCode);
        } else if ("7342".equals(offerCode) || "7343".equals(offerCode) || "7344".equals(offerCode)) {
            showParamPart = "4";
        } else if ("7041".equals(offerCode)|| "7042".equals(offerCode) || "7043".equals(offerCode) || "7044".equals(offerCode) ) {
            showParamPart = "5";
        }

        IData info = new DataMap();
        info.put("GROUP_ID", getData().getString("GROUP_ID"));
        info.put("OPER_TYPE", getData().getString("OPER_TYPE"));
        info.put("SHOW_PARAM_PART", showParamPart);
        info.put("PRODUCT_ID", offerCode);
        info.put("TEMPLET_ID", getData().getString("TEMPLET_ID"));

        String operCode = "open";
        if ("21".equals(getData().getString("OPER_TYPE"))) {
            operCode = "change";
        } else if ("23".equals(getData().getString("OPER_TYPE"))) {
            operCode = "cancel";
        }

        String inModeCode = getVisit().getInModeCode();
        IDataset templetIDList = WorkfromViewCall.qryBusiTypeByProductId(this, operCode, offerCode, "ZZZZ", inModeCode);
        info.put("TEMPLET_ID_LIST", templetIDList);

        if ("2".equals(showParamPart)) {
            if ("20".equals(getData().getString("OPER_TYPE"))) {
                // 获取填过的项目名称
                IDataset projectName = initProjectName(pageData);
                info.put("PROJECTNAMES", projectName);

                IData input = new DataMap();
                input.put("CUST_ID", getData().getString("CUST_ID"));
                input.put("PRODUCT_ID", getData().getString("OFFER_CODE", ""));
                IDataset datalineSerialNumberList = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", input);
                info.put("DATALINE_SERIAL_NUMBER_LIST", datalineSerialNumberList);
                info.put("DATALINE_OPER_TYPE", "0"); // 默认-新增
                if ("EDIRECTLINEOPEN".equals(info.getString("TEMPLET_ID"))) {
                    info.put("ACCT_DEAL", "0"); // 默认-账户新增
                }
                // info.put("IF_NEW_CONTRACT", "0"); //默认-合同新增
                info.put("IF_CHOOSE_CONFCRM", "1"); // 默认-不选择勘察单号
                // info.put("serialNO", getIBsysID());
                info.put("C_CUST_ID", getData().getString("CUST_ID"));
            } else if ("21".equals(getData().getString("OPER_TYPE"))) {
                // 获取填过的项目名称
                IDataset projectName = initProjectName(pageData);
                info.put("PROJECTNAMES", projectName);

                info.put("IF_CHOOSE_CONFCRM", "1"); // 默认-选择勘察单号
                IData input = new DataMap();
                input.put("CUST_ID", getData().getString("CUST_ID"));
                input.put("PRODUCT_ID", getData().getString("OFFER_CODE", ""));
                IDataset datalineSerialNumberList = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", input);
                info.put("DATALINE_SERIAL_NUMBER_LIST", datalineSerialNumberList);
                if ("EDIRECTLINECONTRACTCHANGE".equals(info.getString("TEMPLET_ID"))) {
                    if (pageData.getString("EC_USER_ID") != null) {
                        IData param = new DataMap();
                        param.put("SERIAL_NUMBER", pageData.getString("DATALINE_SERIAL_NUMBER", ""));
                        // IDataset userInfoList = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp", param);
                        // if (IDataUtil.isNotEmpty(userInfoList)) {
                        param.put("USER_ID", pageData.getString("EC_USER_ID"));
                        param.put("PRODUCT_ID", getData().getString("OFFER_CODE", ""));
                        IDataset contract = CSViewCall.call(this, "CM.ConstractGroupSVC.getContractListByUserId", param);
                        if (null != contract && contract.size() > 0) {
                            IData contractData = contract.getData(0);
                            /* info.put("MEB_OFFER_CODE", contractData.getString("MEB_OFFER_CODE", arg1)); */info.putAll(contractData);

                            // }
                            String[] keys = contractData.getNames();
                            for (String key : keys) {
                                if (key.equals("C_CONTRACT_FILE_ID")) {
                                    if (contractData.getString(key) != null && !"".equals(contractData.getString(key))) {
                                        String[] contractFile = contractData.getString(key).split(":");
                                        if (contractFile.length == 2) {
                                            info.put("C_CONTRACT_FILE_ID", contractFile[0]);
                                            info.put("C_FILE_LIST_NAME", contractFile[1]);
                                            IData contractReturnData = new DataMap();

                                            contractReturnData.put("FILE_ID", contractFile[0]);
                                            contractReturnData.put("FILE_NAME", contractFile[1]);
                                            this.setAjax(contractReturnData);
                                        }
                                    }

                                }
                            }

                            if (!"".equals(info.getString("C_CONTRACT_FILE_ID", ""))) {
                                String[] contractFile = info.getString("C_CONTRACT_FILE_ID", "").split(":");

                                if (contractFile.length == 2) {
                                    IData contractReturnData = new DataMap();

                                    contractReturnData.put("CONTRACT_FILE_LIST", contractFile[0]);
                                    contractReturnData.put("CONTRACT_FILE_LIST_name", contractFile[1]);
                                    contractReturnData.put("CONTRACT_FILE_LIST_ALL", info.getString("C_CONTRACT_FILE_ID", ""));

                                    this.setAjax(contractReturnData);
                                }

                            }

                        }
                    }

                }

            }

            IDataset configInfos = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "MEM_PRODUCT_ID");
            String mebOfferCode = "";
            for (int i = 0; i < configInfos.size(); i++) {
                String paramName = configInfos.getData(i).getString("PARAMNAME");
                if (paramName.equals(offerCode)) {
                    mebOfferCode = configInfos.getData(i).getString("PARAMVALUE");
                }
            }
            IData mebOfferData = UpcViewCall.queryOfferByOfferId(this, UpcConst.ELEMENT_TYPE_CODE_PRODUCT, mebOfferCode, "Y");
            if ("7010".equals(offerCode)) {
                mebOfferCode = "-1";
            }
            info.put("MEB_OFFER_CODE", mebOfferCode);
            if (IDataUtil.isNotEmpty(mebOfferData)) {
                info.put("MEB_OFFER_NAME", mebOfferData.getString("OFFER_NAME", ""));
                info.put("MEB_OFFER_ID", mebOfferData.getString("OFFER_ID", ""));
            }

        }else if ("5".equals(showParamPart)) {
        	if ("EDIRECTLINEOPENIDC".equals(info.getString("TEMPLET_ID"))) {
                info.put("ACCT_DEAL", "0"); // 默认-账户新增
            }
        }

        // 查询流程信息
        IData templetInfo = WorkfromViewCall.getOperTypeByTempletId(this, templetID);

        IData input = new DataMap();
        input.put("BUSI_CODE", offerCode);
        input.put("OPER_TYPE", templetInfo.getString("BUSI_OPER_TYPE"));
        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
        if (IDataUtil.isNotEmpty(busiSpecReleList)) {
            busiSpecReleList.first().put("TEMPLET_ID", info.getString("TEMPLET_ID"));
            info.put("BUSI_SPEC_RELE", busiSpecReleList.first());

            // 查询流程节点信息
            input.clear();
            input.put("BPM_TEMPLET_ID", busiSpecReleList.first().getString("BPM_TEMPLET_ID"));
            input.put("NODE_TYPE", "3");
            IDataset nodeTempleteList = CSViewCall.call(this, "SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
            if (IDataUtil.isNotEmpty(nodeTempleteList)) {
                IData nodeTempletedData = new DataMap();
                String bpmTempletId = nodeTempleteList.first().getString("BPM_TEMPLET_ID");
                nodeTempletedData.put("BPM_TEMPLET_ID", bpmTempletId);
                nodeTempletedData.put("NODE_ID", nodeTempleteList.first().getString("NODE_ID"));

                info.put("NODE_TEMPLETE", nodeTempletedData);

                commInfo.put("FLOW_ID", bpmTempletId); // POINT_ONE
                commInfo.put("NODE_ID", nodeTempleteList.first().getString("NODE_ID")); // POINT_TWO
                commInfo.put("PRODUCT_ID", offerCode);
                commInfo.put("PAGE_LEVE", templetID);
                if ("21".equals(templetInfo.getString("BUSI_OPER_TYPE")) || "299".equals(templetInfo.getString("BUSI_OPER_TYPE")) || "23".equals(templetInfo.getString("BUSI_OPER_TYPE"))) {
                    // 做注销操作，选择业务流程时，将专线信息先查询出来保存到隐藏域。
                    IData param = new DataMap();
                    if (bpmTempletId.equals("EDIRECTLINECANCELPBOSS") || bpmTempletId.equals("EVIOPDIRECTLINECANCELPBOSS")) {
                        IDataset dataLines = new DatasetList();
                        String productId = getData().getString("OFFER_CODE");
                        if ("7010".equals(productId)) {
                            param.clear();
                            param.put("USER_ID", getData().getString("EC_USER_ID"));
                            IDataset voipDataLines = CSViewCall.call(this, "SS.QcsGrpIntfSVC.getProductInfoForPboss", param);
                            if (IDataUtil.isEmpty(voipDataLines)) {
                                CSViewException.apperr(CrmCommException.CRM_COMM_103, "集团用户【" + getData().getString("EC_USER_ID") + "】不存在有效的专线，不能办理该业务！");
                            }

                            info.put("DATALINENUM", voipDataLines.size() - 1);
                            setInfo(info);

                            for (int j = 0; j < voipDataLines.size(); j++) {
                                IData temp = new DataMap();
                                IData voipDataLine = voipDataLines.getData(j);
                                Iterator<String> itr = voipDataLine.keySet().iterator();
                                while (itr.hasNext()) {
                                    String attrCode = itr.next();
                                    String attrValue = voipDataLine.getString(attrCode);
                                    if (attrValue == null) {
                                        continue;
                                    } else {
                                        temp.put(attrCode, attrValue);
                                    }
                                }
                                // 专线变更才需要查询tf_f_user_other表(VOIP专线资费信息)，勘察变更不需要
                                if (bpmTempletId.equals("EVIOPDIRECTLINECHANGEPBOSS")) {
                                    IDataset otherInfos = UserOtherInfoIntfViewUtil.qryGrpUserOtherInfosByUserIdAndRsrvValueCode(this, getData().getString("EC_USER_ID"), "N001");
                                    if (IDataUtil.isNotEmpty(otherInfos)) {
                                        for (int i = 0; i < otherInfos.size(); i++) {
                                            IData otherInfo = otherInfos.getData(i);
                                            IDataset disCountParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "VOIP_DISCOUNTPARAM_CRM_ESOP");
                                            Iterator<String> itr2 = otherInfo.keySet().iterator();
                                            while (itr2.hasNext()) {
                                                String attrCode = itr2.next();
                                                for (int k = 0; k < disCountParam.size(); k++) {
                                                    String paramValue = disCountParam.getData(k).getString("PARAMVALUE");
                                                    if (attrCode.equals(paramValue)) {
                                                        temp.put(disCountParam.getData(k).getString("PARAMNAME"), otherInfo.getString(attrCode));
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (StringUtils.isBlank(temp.getString("NOTIN_RSRV_STR9"))) {
                                    temp.put("NOTIN_RSRV_STR9", temp.getString("NOTIN_LINE_NO"));
                                }

                                dataLines.add(temp);
                            }
                        } else {
                            String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
                            param.clear();
                            param.put("USER_ID_A", getData().getString("EC_USER_ID"));
                            param.put("RELATION_TYPE_CODE", relationTypeCode);
                            param.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
                            param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());

                            IDataset directLineList = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getAllRelaUUInfoByUserIda", param);
                            if (IDataUtil.isEmpty(directLineList)) {
                                CSViewException.apperr(CrmCommException.CRM_COMM_103, "集团用户【" + getData().getString("EC_USER_ID") + "】不存在有效的专线成员，不能办理该业务！");
                            }

                            info.put("DATALINENUM", directLineList.size() - 1);
                            setInfo(info);

                            for (int i = 0; i < directLineList.size(); i++) {
                                IData temp = new DataMap();
                                IData directLine = directLineList.getData(i);
                                // 查询tf_f_user_dataline表(专线派单信息)
                                input.clear();
                                input.put("USER_ID", directLine.getString("USER_ID_B"));
                                IDataset userDataLines = CSViewCall.call(this, "SS.QcsGrpIntfSVC.getProductInfoForPboss", input);
                                if (IDataUtil.isNotEmpty(userDataLines)) {
                                    IData userDataLine = userDataLines.getData(0);
                                    Iterator<String> itr = userDataLine.keySet().iterator();
                                    while (itr.hasNext()) {
                                        String attrCode = itr.next();
                                        String attrValue = userDataLine.getString(attrCode);
                                        if (attrValue == null) {
                                            continue;
                                        } else {
                                            temp.put(attrCode, attrValue);
                                        }
                                    }
                                    // 专线变更才需要查询tf_f_user_attr表(专线资费信息)，勘察变更不需要
                                    if (bpmTempletId.equals("EDIRECTLINECHANGEPBOSS") || bpmTempletId.equals("DIRECTLINECHANGESIMPLE") || bpmTempletId.equals("MANUALSTOP") || bpmTempletId.equals("MANUALBACK")) {
                                        IData discounts = new DataMap();
                                        input.put("INST_TYPE", "D");
                                        input.put("PRODUCT_ID", productId);
                                        discounts = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getUserLineInfoByUserId", input);
                                        IDataset disCountParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "DISCOUNTPARAM_CRM_ESOP");
                                        Iterator<String> itr2 = discounts.keySet().iterator();
                                        while (itr2.hasNext()) {
                                            String attrCode = itr2.next();
                                            for (int j = 0; j < disCountParam.size(); j++) {
                                                String paramValue = disCountParam.getData(j).getString("PARAMVALUE");
                                                if (attrCode.equals(paramValue)) {
                                                    temp.put(disCountParam.getData(j).getString("PARAMNAME"), discounts.getString(attrCode));
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    if (StringUtils.isBlank(temp.getString("NOTIN_RSRV_STR9"))) {
                                        temp.put("NOTIN_RSRV_STR9", temp.getString("NOTIN_LINE_NO"));
                                    }

                                    dataLines.add(temp);
                                }
                            }
                        }
                        // 将{key,value}格式转换成{ATTR_CODE:xxx,ATTR_VALUE:xxx}格式
                        IDataset temp3 = new DatasetList();
                        for (int i = 0; i < dataLines.size(); i++) {
                            IDataset temp2 = new DatasetList();
                            IData tempDataLine = dataLines.getData(i);
                            Iterator<String> itr = tempDataLine.keySet().iterator();
                            while (itr.hasNext()) {
                                String key = itr.next();
                                String value = tempDataLine.getString(key);

                                IDataset esopParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
                                for (int j = 0; j < esopParam.size(); j++) {
                                    String paramValue = esopParam.getData(j).getString("PARAMVALUE");
                                    if (key.equals(paramValue)) {
                                        key = esopParam.getData(j).getString("PARAMNAME");
                                        IData temp = new DataMap();
                                        temp.put("ATTR_CODE", key);
                                        temp.put("ATTR_VALUE", value);
                                        temp2.add(temp);
                                        break;
                                    }
                                }
                            }
                            temp3.add(temp2);
                        }

                        IData dataLine = new DataMap();
                        dataLine.put("DATALINEINFO", temp3);
                        this.setAjax(dataLine);
                    }
                }
            } else {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品未配置流程信息，不能办理该业务！");
            }
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品未配置流程信息，不支持该业务办理！");
        }
        if ("EDIRECTLINEOPENIDC".equals(info.getString("TEMPLET_ID")) || "EDIRECTLINECHECKIDC".equals(info.getString("TEMPLET_ID")) || "EDIRECTLINEPREEMPTIONIDC".equals(info.getString("TEMPLET_ID"))) {
            String productId = getData().getString("OFFER_CODE");
            IData paramgrp = new DataMap();
            IData pattrInfo = new DataMap();
            paramgrp.put("PRODUCT_ID", productId);
            paramgrp.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
            paramgrp.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());
            IDataset grpSnDataList = CSViewCall.call(this, "SS.ProductInfoSVC.genGrpSn", paramgrp);
            if (grpSnDataList != null && grpSnDataList.getData(0) != null) {
                pattrInfo.put("IDC_SERIAL_NUMBER", grpSnDataList.getData(0).getString("SERIAL_NUMBER", ""));
            }
            setPattrInfo(pattrInfo);
        }

        // 获取专线批量新增导入模板文件名
        getLineUploadFileName(info, offerCode);
        // 获取驳回时的产品参数
        /* String ibsysId = getData().getString("IBSYSID"); if (StringUtils.isNotBlank(ibsysId)) { queryOfferAttr(pageData); info.put("IS_READONLY", "true");// 驳回时 } */
        setComminfo(commInfo);
        setInfo(info);

        if ("DIRECTLINEMARKETINGADD".equals(info.getString("TEMPLET_ID")) || "DIRECTLINEMARKETINGUPD".equals(info.getString("TEMPLET_ID"))) {// 专线营销活动新增
            IData paramMarke = new DataMap();
            IData busi = new DataMap();
            paramMarke.put("CUSTOMNO", pageData.getString("GROUP_ID"));
            paramMarke.put("RESULT_CODE", "0");
            IDataset infosMarke = CSViewCall.call(this, "SS.WorkformMarketingSVC.selMarketingByCondition2", paramMarke);
            if (null != infosMarke && infosMarke.size() > 0) {
                for (int i = 0; i < infosMarke.size(); i++) {
                    IData tempMarke = infosMarke.getData(i);
                    tempMarke.put("TEXT", tempMarke.getString("IBSYSID_MARKETING", "") + "|" + tempMarke.getString("MARKETING_NAME", ""));
                }
            }
            busi.put("MARKETING_DATA", infosMarke);

            setBusi(busi);
        }
    }

    private String transOfferCodeFor400(String templetID, String offerCode) throws Exception {
        String returnOfferCode = offerCode;
        if ("FOURMANAGE".equals(templetID)) {
            returnOfferCode = "99832";
        } else if ("TIMERREVIEWVOICEMANAGE".equals(templetID)) {
            returnOfferCode = "99835";
        } else if ("VOICEMANAGE".equals(templetID)) {
            returnOfferCode = "99833";
        } else if ("TIMERREVIEWFOURMANAGE".equals(templetID)) {
            returnOfferCode = "99834";
        } else if ("MOBILE400OPEN".equals(templetID) || "MOBILE400CHANGE".equals(templetID) || "MOBILE400CANCEL".equals(templetID)) {
            returnOfferCode = "VP9983";
        }
        return returnOfferCode;
    }

    public void initOfferCha(IRequestCycle cycle) throws Exception {
        String ibsysId = getData().getString("IBSYSID");

        String mOperType = getData().getString("OPER_TYPE");
        String offerId = getData().getString("OFFER_ID");
        String subOfferCode = getData().getString("SUB_OFFER_CODE");
        String offerCode = getData().getString("OFFER_CODE");
        String operType = EcEsopConstants.transOperTypeToNodeId(mOperType);
        String userId = getData().getString("USER_ID");
        String custId = getData().getString("CUST_ID");
        String brandCode = getData().getString("BRAND_CODE");
        String showParamPart = getData().getString("SHOW_PARAM_PART");
        IData busiSpecRele = new DataMap(getData().getString("NODELIST"));
        String bpmTempleteID = busiSpecRele.getString("BPM_TEMPLET_ID", "");
        String nodeId = busiSpecRele.getString("NODE_ID", "");
        String productId = getData().getString("OFFER_CODE");
        String offerChaHiddenId = getData().getString("OFFER_CHA_HIDDEN_ID", "");

        IData inAttr = new DataMap();
        inAttr.put("FLOW_ID", bpmTempleteID); // POINT_ONE
        inAttr.put("NODE_ID", nodeId); // POINT_TWO
        inAttr.put("PRODUCT_ID", productId);
        inAttr.put("PAGE_LEVE", "0");

        setInAttr(inAttr);

        IData busi = new DataMap();
        IData input = new DataMap();

        boolean deve_flag = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "DEVELOPER_RIGHT");
        busi.put("DEVELOPER", getVisit().getStaffId());
        busi.put("LOCAL_DEVELOPER", getVisit().getStaffId());
        busi.put("DEVELOPER_DEPT", getVisit().getDepartId());
        busi.put("LOCAL_DEVELOPER_DEPT", getVisit().getDepartId());
        if (deve_flag) {
            busi.put("DEVELOPER_RIGHT", 1);
        } else {
            busi.put("DEVELOPER_RIGHT", 0);
        }

        // 变更暂时不支持快速变更，仅供简单变更使用
        if (BizCtrlType.ChangeUserDis.equals(operType)
                && (bpmTempleteID.equals("DIRECTLINECHANGESIMPLE") || bpmTempleteID.equals("EVIOPDIRECTLINECHANGESIMPLE") || bpmTempleteID.equals("ECHANGERESOURCECONFIRM") || bpmTempleteID.equals("MANUALSTOP") || bpmTempleteID.equals("MANUALBACK"))) {
            if (!"true".equals(getData().getString("NOT_QRY"))) {
                if (!bpmTempleteID.equals("ECHANGERESOURCECONFIRM")) {
                    IData param = new DataMap();
                    param.put("PRODUCT_NO", getData().getString("PRODUCT_NO"));
                    IDataset dataLines = CSViewCall.call(this, "SS.TradeDataLineAttrInfoQrySVC.qryAllUserDatalineByProductNO", param);
                    IData dataline = dataLines.getData(0);
                    // 添加成员用户USER_ID、SERIAL_NUMBER
                    String mebUserId = dataline.getString("USER_ID");
                    IData mebUserInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, mebUserId);
                    if (IDataUtil.isEmpty(mebUserInfo)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID=" + mebUserId + "未获取到用户信息！");
                    }
                    String mebSerialNumber = mebUserInfo.getString("SERIAL_NUMBER");
                    busi.put("USER_ID", mebUserId);
                    busi.put("SERIAL_NUMBER", mebSerialNumber);

                    IDataset esopParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
                    Iterator<String> itr = dataline.keySet().iterator();
                    while (itr.hasNext()) {
                        String attrCode = itr.next();
                        for (int i = 0; i < esopParam.size(); i++) {
                            String paramValue = esopParam.getData(i).getString("PARAMVALUE");
                            if (attrCode.equals(paramValue)) {
                                busi.put(esopParam.getData(i).getString("PARAMNAME"), dataline.getString(attrCode));
                                break;
                            }
                        }
                    }
                    // 互联网、数据专线变更获取资费
                    if (bpmTempleteID.equals("DIRECTLINECHANGESIMPLE") || bpmTempleteID.equals("MANUALSTOP") || bpmTempleteID.equals("MANUALBACK")) {
                        if (!"7010".equals(offerCode)) {
                            String userIdB = dataline.getString("USER_ID");
                            input.put("USER_ID", userIdB);
                            input.put("INST_TYPE", "D");
                            input.put("PRODUCT_ID", productId);
                            IData discounts = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getDiscountByUserId", input);
                            IDataset disCountParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "DISCOUNTPARAM_CRM_ESOP");
                            Iterator<String> itr2 = discounts.keySet().iterator();
                            while (itr2.hasNext()) {
                                String attrCode = itr2.next();
                                for (int i = 0; i < disCountParam.size(); i++) {
                                    String paramValue = disCountParam.getData(i).getString("PARAMVALUE");
                                    String key = disCountParam.getData(i).getString("PARAMNAME");
                                    if (attrCode.equals(paramValue)) {
                                        String attrValue = discounts.getString(attrCode);
                                        // 处理百分号无法提交问题（这里直接去掉，后面提交会加上）
                                        if ("NOTIN_RSRV_STR6".equals(key) || "NOTIN_RSRV_STR7".equals(key) || "NOTIN_RSRV_STR8".equals(key)) {
                                            if (attrValue != null && attrValue.endsWith("%")) {
                                                attrValue = attrValue.substring(0, attrValue.length() - 1);
                                            }
                                        }
                                        busi.put(key, attrValue);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // VOIP专线变更获取资费
                    if (bpmTempleteID.equals("EVIOPDIRECTLINECHANGEPBOSS") || bpmTempleteID.equals("EVIOPDIRECTLINECHANGESIMPLE") || bpmTempleteID.equals("MANUALSTOP") || bpmTempleteID.equals("MANUALBACK")) {
                        if ("7010".equals(offerCode)) {
                            IData voipParam = new DataMap();
                            voipParam.put("USER_ID", dataline.getString("USER_ID"));
                            voipParam.put("PRODUCTNO", getData().getString("PRODUCT_NO"));
                            IData voipDataLine = CSViewCall.callone(this, "SS.QcsGrpIntfSVC.queryChangeLineInfosForEsop", voipParam);
                            IDataset disCountParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "VOIP_DISCOUNTPARAM_CRM_ESOP");
                            Iterator<String> itr2 = voipDataLine.keySet().iterator();
                            while (itr2.hasNext()) {
                                String attrCode = itr2.next();
                                for (int i = 0; i < disCountParam.size(); i++) {
                                    String paramValue = disCountParam.getData(i).getString("PARAMVALUE");
                                    if (attrCode.equals(paramValue)) {
                                        busi.put(disCountParam.getData(i).getString("PARAMNAME"), voipDataLine.getString(attrCode));
                                        break;
                                    }
                                }
                            }
                        }

                    }
                    // 倒换数据中没有"NOTIN_RSRV_STR9(业务标识)"字段，此处手动加上（出问题找辉哥）
                    if (StringUtils.isBlank(busi.getString("NOTIN_RSRV_STR9"))) {
                        busi.put("NOTIN_RSRV_STR9", busi.getString("NOTIN_LINE_NO"));
                    }
                } else if (bpmTempleteID.equals("ECHANGERESOURCECONFIRM")) {
                    String productNO = getData().getString("PRODUCT_NO");
                    IData inData = new DataMap();
                    inData.put("PRODUCTNO", productNO);
                    IDataset esopParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
                    IData userdatalineData = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryLineByProductNO", inData);
                    if (IDataUtil.isEmpty(userdatalineData)) {
                        return;
                    }
                    Iterator<String> itr = userdatalineData.keySet().iterator();
                    while (itr.hasNext()) {
                        String attrCode = itr.next();
                        for (int j = 0; j < esopParam.size(); j++) {
                            String paramValue = esopParam.getData(j).getString("PARAMVALUE");
                            if (attrCode.equals(paramValue)) {
                                busi.put(esopParam.getData(j).getString("PARAMNAME"), userdatalineData.getString(attrCode));
                                break;
                            }
                        }
                    }
                    busi.put("TRADEID", userdatalineData.getString("PRODUCT_NO", ""));

                    // IData param = new DataMap();
                    // //集团USER_ID
                    // param.put("ATTR_CODE", "PRODUCTNO");
                    // param.put("ATTR_VALUE", getData().getString("PRODUCT_NO"));
                    // param.put("NODE_ID", "archive");
                    // IData dataLine = CSViewCall.callone(this, "SS.WorkformAttrSVC.qryMaxSubIbsysId", param);
                    // if(IDataUtil.isNotEmpty(dataLine)) {
                    // String maxSub = dataLine.getString("SUB_IBSYSID");
                    // String recordNum = dataLine.getString("RECORD_NUM");
                    // param.clear();
                    // param.put("SUB_IBSYSID", maxSub);
                    // param.put("RECORD_NUM", recordNum);
                    // IDataset hisAttrInfos = CSViewCall.call(this, "SS.WorkformAttrSVC.qryEopAttrBySubIbsysidRecordNum", param);
                    // for (int i = 0; i < hisAttrInfos.size(); i++) {
                    // IData hisAttrInfo = hisAttrInfos.getData(i);
                    // String attrCode = hisAttrInfo.getString("ATTR_CODE");
                    // String attrValue = hisAttrInfo.getString("ATTR_VALUE");
                    // busi.put(attrCode, attrValue);
                    // }
                    // }
                }
            }
        }
        if ("EDIRECTLINEMARKETINGADD".equals(bpmTempleteID)) {
            busi.put("NOTIN_LINE_NO", getData().getString("PRODUCT_NO"));
            busi.put("NOTIN_RSRV_STR1", getData().getString("NOTIN_RSRV_STR1"));
            busi.put("NOTIN_PAYBACK_PERIOD", getData().getString("NOTIN_PAYBACK_PERIOD", ""));
            busi.put("NOTIN_COST", getData().getString("NOTIN_COST", ""));
            busi.put("NOTIN_TERM", getData().getString("NOTIN_TERM", ""));
            busi.put("NOTIN_HUNDRED", getData().getString("NOTIN_HUNDRED", ""));
        }

        // 倒换数据中没有"NOTIN_RSRV_STR9(业务标识)"字段，此处手动加上（出问题找辉哥）
        if (StringUtils.isBlank(busi.getString("NOTIN_RSRV_STR9"))) {
            busi.put("NOTIN_RSRV_STR9", busi.getString("NOTIN_LINE_NO"));
        }

        setBusi(busi);
    }

    public void getGrpSN(IData submitData) throws Exception {
        IData commData = new DataMap(submitData.getString("COMMON_DATA"));
        String seralOperType = commData.getString("DATALINE_OPER_TYPE");
        String serailNumber = "";
        if (StringUtils.isNotBlank(seralOperType) && "0".equals(seralOperType)) {
            IData custData = new DataMap(submitData.getString("CUST_DATA"));
            IData offerData = new DataMap(submitData.getString("OFFER_DATA"));
            String groupId = custData.getString("GROUP_ID");
            String productId = offerData.getString("OFFER_CODE");

            if (StringUtils.isEmpty(groupId) || StringUtils.isEmpty(productId)) {
                return;
            }

            // 避免服务号码的重复
            IData param = new DataMap();
            param.put("GROUP_ID", groupId);
            param.put("PRODUCT_ID", productId);
            param.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());

            IData grpSnData = new DataMap();
            for (int i = 0; i < 10; i++) {
                grpSnData = CSViewCall.callone(this, "SS.ProductInfoSVC.genGrpSn", param);

                String serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

                if (StringUtils.isEmpty(serialNumber)) {
                    break;
                }

                IData userList = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber, false);
                param.clear();
                param.put("SERIAL_NUMBER", serialNumber);
                param.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
                IData tradeList = CSViewCall.callone(this, "CS.TradeInfoQrySVC.getTradeInfoBySn", param);

                if (IDataUtil.isEmpty(userList) && IDataUtil.isEmpty(tradeList)) {
                    break;
                }
            }
            serailNumber = grpSnData.getString("SERIAL_NUMBER");
        } else if (StringUtils.isNotBlank(seralOperType) && "1".equals(seralOperType)) {
            serailNumber = commData.getString("DATALINE_SERIAL_NUMBER");
        }
        submitData.put("SERIAL_NUMBER", serailNumber);

        String offerID = commData.getString("OFFER_ID");
        if ("110000006085".equals(offerID)) {
            // 呼叫中心直连专线，一个集团用户下只允许办理一条专线
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serailNumber);
            IDataset userInfoList = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryUserMainProdInfoBySnForGrp", param);
            if (IDataUtil.isNotEmpty(userInfoList)) {
                param.put("USER_ID_A", userInfoList.first().getString("USER_ID"));
                param.put("RELATION_TYPE_CODE", "81");
                IDataset directLineList = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.queryDirectLineList", param);
                if (IDataUtil.isNotEmpty(directLineList)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "集团用户【" + userInfoList.first().getString("USER_ID") + "】已存在语音专线（呼叫中心直连），一个集团用户下只允许办理一条专线！");
                }
            }
        }
    }

    public void excuteImport(IRequestCycle cycle) throws Exception {
        logger.debug("=====执行导入传入参数=======" + getData());
        String fileId = getData().getString("FILE1");
        String offerCode = getData().getString("OFFER_CODE");
        String importXmlPath = getLineImportXmlPath(offerCode);

        ImpExpUtil.getImpExpManager().getFileAction().setVisit(this.getVisit());
        IData fileData = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets(importXmlPath));
        logger.debug("=====获取导入文件数据=======" + fileData);
        // 失败数据
        IDataset failds = new DatasetList();
        failds.addAll(((IDataset[]) fileData.get("error"))[0]);
        int faildNum = failds.size();
        logger.debug("=====获取导入文件失败数据=======" + failds);
        // 成功数据
        IDataset succds = new DatasetList();
        // succds = testSuccList();
        succds.addAll(((IDataset[]) fileData.get("right"))[0]);
        int succdNum = succds.size();
        logger.debug("=====获取导入文件成功数据=======" + succds);
        IData ajaxData = new DataMap();
        if (faildNum > 0) {
            String fileName = "import_failed.xls";
            String exportXmlPath = getLineExportXmlPath(offerCode);
            // String faildUrl = "attach?action=downloadweb&filePath=template/eop/ImsDirectLineImport.xls";//exportExcel(fileName, new IDataset[]{ failds }, exportXmlPath);
            String faildUrl = exportExcel(fileName, new IDataset[] { failds }, exportXmlPath);
            ajaxData.put("FAILD_FILE_URL", faildUrl);
            logger.debug("=====失败数据文件下载地址=======" + faildUrl);
        }
        ajaxData.put("SUCCESS_NUM", succdNum);
        ajaxData.put("FAIL_NUM", faildNum);
        ajaxData.put("TOTAL_NUM", succdNum + faildNum);
        ajaxData.put("SUCC_LIST", transSuccList2OfferChaList(succds));
        logger.debug("=====ajax返回参数=======" + ajaxData);
        setAjax(ajaxData);
    }

    public String exportExcel(String fileName, IDataset[] exportInfos, String xmlPath) throws Exception {
        // 创建导出需要的固定参数
        IData params = new DataMap();
        params.put("posX", "0");
        params.put("posY", "0");
        params.put("ftpSite", "order");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(this.getVisit());
        // 将数据写入文件并返回文件ID
        String fileId = ImpExpUtil.beginExport(null, params, fileName, exportInfos, ExcelConfig.getSheets(xmlPath));
        // 获取文件下载的URL
        String url = ImpExpUtil.getDownloadPath(fileId, fileName);

        return url;
    }

    public void buildOtherSvcParam(IData param) throws Exception {
        // 同楼搬迁增加Changeaddr字段需求、停开机增加ISCONTROL字段
        buildDirectlineData(param);
        
        // 校验附件名称
        checkAttachName(param);

        IData offerData = param.getData("OFFER_DATA");
        IDataset attachLists = param.getDataset("DISCNT_ATTACH_LIST");
        String bpmTempletId = param.getData("NODE_TEMPLETE").getString("BPM_TEMPLET_ID");
        Boolean discntAttach = true;
        if (IDataUtil.isNotEmpty(attachLists)) {
            discntAttach = false;
        }
        if (IDataUtil.isNotEmpty(offerData)) {
            String offerCode = offerData.getString("OFFER_CODE");
            if ("EDIRECTLINECHANGEFEE".equals(bpmTempletId)) {
                IDataset input = new DatasetList();
                IDataset suboffers = param.getDataset("DIRECTLINE_DATA");
                /* if(!suboffers.getData(0).getString("ERROR","").isEmpty()){ CSViewException.apperr(CrmCommException.CRM_COMM_103, "该专线："+suboffers.getData(0).getString("ERROR","")+"未做任何修改请重新选择！"); } */
                if (IDataUtil.isNotEmpty(suboffers)) {
                    for (int i = 0; i < suboffers.size(); i++) {
                        IData map = new DataMap();
                        IDataset suboffer = suboffers.getDataset(i);
                        if (DataUtils.isNotEmpty(suboffer)) {
                            String error = suboffer.getData(0).getString("ERROR", "");
                            if (StringUtils.isNotBlank(error)) {
                                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该专线：" + error + "未做任何修改请重新选择！");
                            }
                            map.put("OFFER_CHA", suboffer);
                        }
                        input.add(map);
                    }
                    if (discntAttach) {
                        if ("7011".equals(offerCode) || "7012".equals(offerCode)
                        		|| "70111".equals(offerCode)|| "70112".equals(offerCode)|| "70121".equals(offerCode)|| "70122".equals(offerCode)) {
                            checkLinePrice(input, offerCode, "pam_NOTIN_LINE_BROADBAND", "pam_NOTIN_LINE_PRICE", "pam_NOTIN_LINE_INSTANCENUMBER");
                        }
                    }

                }
            }
            if ("EDIRECTLINEDATACHANGE".equals(bpmTempletId)) {
                IDataset input = new DatasetList();
                IDataset suboffers = param.getDataset("DIRECTLINE_DATA");
                /* if(!suboffers.getData(0).getString("ERROR","").isEmpty()){ CSViewException.apperr(CrmCommException.CRM_COMM_103, "该专线："+suboffers.getData(0).getString("ERROR","")+"未做任何修改请重新选择！"); } */
                if (IDataUtil.isNotEmpty(suboffers)) {
                    for (int i = 0; i < suboffers.size(); i++) {
                        IData map = new DataMap();
                        IDataset suboffer = suboffers.getDataset(i);
                        if (DataUtils.isNotEmpty(suboffer)) {
                            String error = suboffer.getData(0).getString("ERROR", "");
                            if (StringUtils.isNotBlank(error)) {
                                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该专线：" + error + "未做任何修改请重新选择！");
                            }
                            map.put("OFFER_CHA", suboffer);
                        }
                        input.add(map);
                    }
                }
            }
            if ("EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "DIRECTLINECHANGESIMPLE".equals(bpmTempletId)) {
                if (discntAttach) {
                    boolean marketingTag = false;
                    IDataset suboffers = param.getData("OFFER_DATA").getDataset("SUBOFFERS");
                    if (IDataUtil.isEmpty(suboffers.getData(0).getDataset("OFFER_CHA"))) {
                        IDataset input = new DatasetList();
                        suboffers = param.getDataset("DIRECTLINE_DATA");
                        /* if(!suboffers.getData(0).getString("ERROR","").isEmpty()){ CSViewException.apperr(CrmCommException.CRM_COMM_103, "该专线："+suboffers.getData(0).getString("ERROR","")+"未做任何修改请重新选择！"); } */
                        if (IDataUtil.isNotEmpty(suboffers)) {
                            for (int i = 0; i < suboffers.size(); i++) {
                                IData map = new DataMap();
                                IDataset suboffer = suboffers.getDataset(i);
                                if (DataUtils.isNotEmpty(suboffer)) {
                                    map.put("OFFER_CHA", suboffer);
                                }
                                input.add(map);
                            }
                        }
                        IDataset offerChaInfos = input.getData(0).getDataset("OFFER_CHA");
                        for (int i = 0; i < offerChaInfos.size(); i++) {
                            IData offerChaInfo = offerChaInfos.getData(i);
                            String attrCode = offerChaInfo.getString("ATTR_CODE");
                            String attrValue = offerChaInfo.getString("ATTR_VALUE");
                            if (attrCode.equals("NOTIN_MARKETING_TAG") && attrValue.equals("1")) {
                                marketingTag = true;
                                break;
                            }
                        }
                        if (!marketingTag) {
                            checkLinePrice(input, offerCode, "NOTIN_RSRV_STR1", "NOTIN_RSRV_STR2", "NOTIN_LINE_NO");
                        }
                    } else {
                        IDataset offerChaInfos = suboffers.getData(0).getDataset("OFFER_CHA");
                        for (int i = 0; i < offerChaInfos.size(); i++) {
                            IData offerChaInfo = offerChaInfos.getData(i);
                            String attrCode = offerChaInfo.getString("ATTR_CODE");
                            String attrValue = offerChaInfo.getString("ATTR_VALUE");
                            if (attrCode.equals("NOTIN_MARKETING_TAG") && attrValue.equals("1")) {
                                marketingTag = true;
                                break;
                            }
                        }
                        if (!marketingTag) {
                            checkLinePrice(suboffers, offerCode, "NOTIN_RSRV_STR1", "NOTIN_RSRV_STR2", "NOTIN_LINE_NO");
                        }
                    }
                }
            }

            if ("EDIRECTLINEMARKETINGADD".equals(bpmTempletId)) {
                IDataset suboffers = offerData.getDataset("SUBOFFERS");
                IDataset commonDataset = param.getDataset("EOMS_ATTR_LIST");
                if (IDataUtil.isEmpty(commonDataset)) {
                    return;
                }

                if (IDataUtil.isEmpty(suboffers)) {
                    return;
                }
                String C_MARKETING_IS_SUCC = "";
                for (int k = 0; k < commonDataset.size(); k++) {
                    String attrcode = commonDataset.getData(k).getString("ATTR_CODE");
                    String attrvalue = commonDataset.getData(k).getString("ATTR_VALUE");
                    if ("C_MARKETING_IS_SUCC".equals(attrcode)) {
                        C_MARKETING_IS_SUCC = attrvalue;
                    }
                }
                if ("1".equals(C_MARKETING_IS_SUCC)) {
                    IDataset suboffer = param.getDataset("DIRECTLINE_DATA");
                    if (IDataUtil.isEmpty(suboffer)) {
                        return;
                    }

                    for (int j = 0; j < suboffer.size(); j++) {
                        IDataset subofferList = suboffer.getDataset(j);
                        for (int i = 0; i < subofferList.size(); i++) {

                            String subAttrcode = subofferList.getData(i).getString("ATTR_CODE");
                            String subAattrvalue = subofferList.getData(i).getString("ATTR_VALUE");
                            if ("pattr_NOTIN_TERM".equals(subAttrcode) && StringUtils.isEmpty(subAattrvalue)) {
                                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您选择的专线：体验后是否能促成签约为‘是’，回收期(年)、预计资费(元/月)、协议期限(年)、百元值不能为空！");
                            }
                            if ("pattr_NOTIN_PAYBACK_PERIOD".equals(subAttrcode) && StringUtils.isEmpty(subAattrvalue)) {
                                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您选择的专线：体验后是否能促成签约为‘是’，回收期(年)、预计资费(元/月)、协议期限(年)、百元值不能为空！");
                            }
                            if ("pattr_NOTIN_COST".equals(subAttrcode) && StringUtils.isEmpty(subAattrvalue)) {
                                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您选择的专线：体验后是否能促成签约为‘是’，回收期(年)、预计资费(元/月)、协议期限(年)、百元值不能为空！");
                            }
                            if ("pattr_NOTIN_HUNDRED".equals(subAttrcode) && StringUtils.isEmpty(subAattrvalue)) {
                                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您选择的专线：体验后是否能促成签约为‘是’，回收期(年)、预计资费(元/月)、协议期限(年)、百元值不能为空！");
                            }
                        }
                    }

                }
            }

            // 专线变更时获得已有CONTRACT_ID、CUST_ID、LINE_NO，调用客管接口修改合同表数据
            /* if("EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "DIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "MANUALSTOP".equals(
             * bpmTempletId) || "MANUALBACK".equals(bpmTempletId)) { updateContractDiscount(param); } */

            if ("7010".equals(offerCode) || "7011".equals(offerCode) || "7012".equals(offerCode)|| "7016".equals(offerCode)
            		|| "70111".equals(offerCode)|| "70112".equals(offerCode)|| "70121".equals(offerCode)|| "70122".equals(offerCode)) {
                checkDatalineLimit(param);
                checkProductNo(param.getDataset("DIRECTLINE_DATA"));
                if ("ERESOURCECONFIRMZHZG".equals(bpmTempletId) || "EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "ECHANGERESOURCECONFIRM".equals(bpmTempletId)
                        || "EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || bpmTempletId.equals("EVIOPDIRECTLINECHANGESIMPLE") || bpmTempletId.equals("DIRECTLINECHANGESIMPLE")) {
                    // 校验勘察单批量导入的专线内容
                    IDataset attrLists = param.getDataset("EOMS_ATTR_LIST");
                    if (IDataUtil.isNotEmpty(attrLists)) {
                        for (int i = 0; i < attrLists.size(); i++) {
                            IData attrList = attrLists.getData(i);
                            String attrCode = attrList.getString("ATTR_CODE");
                            if (attrCode.equals("BIZRANGE")) {
                                String bizRange = attrList.getString("ATTR_VALUE");
                                checkBatchDataline(param.getDataset("DIRECTLINE_DATA"), offerCode, bizRange);
                            }
                        }
                    }
                }
                // getGrpSN(param);
                IDataset contractData = param.getDataset("CONTRACT_DATA");
                IData temp = null;
                String isReadonly = param.getString("IS_READONLY", "");
                // 审核通过后会更新合同，所以审核不通过再次提交时，合同ID不用变。
                if (!"true".equals(isReadonly)) {
                    if (IDataUtil.isNotEmpty(contractData)) {
                        for (int i = 0; i < contractData.size(); i++) {
                            temp = contractData.getData(i);
                            if (temp.getString("ATTR_CODE").equals("C_IF_NEW_CONTRACT")) {
                                if (temp.getString("ATTR_VALUE").equals("0")) {
                                    getContractId(param);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    IData commData = param.getData("COMMON_DATA");
                    String ibsysid = commData.getString("IBSYSID");
                    IData inparam = new DataMap();
                    inparam.put("IBSYSID", ibsysid);
                    inparam.put("NODE_ID", commData.getString("NODE_ID"));
                    inparam.put("RECORD_NUM", "0");
                    IDataset attrDatas = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", inparam);
                    if (IDataUtil.isEmpty(attrDatas)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "本工单[" + ibsysid + "]资料表TF_B_EOP_ATTR没有数据！");
                    }
                    boolean notHaveContract = false;
                    for (int i = 0; i < attrDatas.size(); i++) {
                        IData attrData = attrDatas.getData(i);
                        if ("CONTRACT_ID".equals(attrData.getString("ATTR_CODE"))) {
                            String contractId = attrData.getString("ATTR_VALUE");
                            if (StringUtils.isNotBlank(contractId)) {
                                // 查询合同是否存在
                                IData contractInput = new DataMap();
                                contractInput.put("CONTRACT_ID", contractId);
                                IDataset contracts = CSViewCall.call(this, "CS.CustContractInfoQrySVC.qryContractInfoByContractIdForGrp", contractInput);
                                if (IDataUtil.isNotEmpty(contracts)) {

                                    IData tempData = new DataMap();
                                    tempData.put("ATTR_VALUE", contractId);
                                    tempData.put("ATTR_NAME", attrData.getString("ATTR_NAME"));
                                    tempData.put("ATTR_CODE", "CONTRACT_ID");
                                    contractData.add(tempData);
                                    notHaveContract = true;
                                }
                            }
                            break;
                        }
                    }

                    param.put("CONTRACT_DATA", contractData);

                    // 合同不存在时新增合同
                    if (!notHaveContract && IDataUtil.isNotEmpty(contractData)) {
                        for (int i = 0; i < contractData.size(); i++) {
                            temp = contractData.getData(i);
                            if (temp.getString("ATTR_CODE").equals("C_IF_NEW_CONTRACT")) {
                                if (temp.getString("ATTR_VALUE").equals("0")) {
                                    getContractId(param);
                                    break;
                                }
                            }
                        }
                    }

                    // 删除产品表信息,提交后会再次添加
                    /* IData productParam = new DataMap(); productParam.put("IBSYSID", ibsysid); CSViewCall.call(this, "SS.WorkformProductSVC.delProductByIbsysid", productParam); */

                    if ("EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "EDIRECTLINECHANGEPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGEPBOSS".equals(bpmTempletId)
                            || bpmTempletId.equals("EVIOPDIRECTLINECHANGESIMPLE") || bpmTempletId.equals("DIRECTLINECHANGESIMPLE") || "EDIRECTLINECANCELPBOSS".equals(bpmTempletId) || "EVIOPDIRECTLINECANCELPBOSS".equals(bpmTempletId)) {
                        // 校验专线列表
                        IData checkParam = new DataMap();
                        checkParam.put("IBSYSID", ibsysid);
                        checkParam.put("DIRECTLINE_DATA", param.getDataset("DIRECTLINE_DATA"));
                        IData checkData = CSViewCall.callone(this, "SS.EsopOrderQuerySVC.chckeLineData", checkParam);
                        if (!checkData.getBoolean("FLAG", true)) {
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, checkData.getString("ERROR"));
                        }
                    }

                    // 删除产品信息，避免再次添加报错
                    param.remove("OFFER_DATA");
                }

            }

            // 人工停机生成自动复机流程
            if ("MANUALSTOP".equals(bpmTempletId) && !"true".equals(param.getString("IS_READONLY", ""))) {
                creatAutoBackBPM(param, offerCode);
            }
        }

    }

    private void buildDirectlineData(IData param) throws Exception {
        String bpmTempletId = param.getData("NODE_TEMPLETE").getString("BPM_TEMPLET_ID");
        IData offerData = param.getData("OFFER_DATA");
        if (IDataUtil.isNotEmpty(offerData)) {
            String offerCode = offerData.getString("OFFER_CODE");

            IDataset newDirectlineDataList = new DatasetList();
            boolean flag = false;
            if ("DIRECTLINECHANGESIMPLE".equals(bpmTempletId) || "EVIOPDIRECTLINECHANGESIMPLE".equals(bpmTempletId)) {
                IDataset emosAttrList = param.getDataset("EOMS_ATTR_LIST");
                if (IDataUtil.isNotEmpty(emosAttrList)) {
                    for (int i = 0; i < emosAttrList.size(); i++) {
                        if ("CHANGEMODE".equals(emosAttrList.getData(i).getString("ATTR_CODE")) && "同楼搬迁".equals(emosAttrList.getData(i).getString("ATTR_VALUE"))) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    IDataset directlineDataList = param.getDataset("DIRECTLINE_DATA");
                    for (int i = 0; i < directlineDataList.size(); i++) {
                        IDataset directlineDatas = directlineDataList.getDataset(i);
                        String value = "";
                        for (int j = 0; j < directlineDatas.size(); j++) {
                            if ("7012".equals(offerCode)|| "70121".equals(offerCode)|| "70122".equals(offerCode)) {
                                if ("pattr_VILLAGEZ".equals(directlineDatas.getData(j).getString("ATTR_CODE")) || "VILLAGEZ".equals(directlineDatas.getData(j).getString("ATTR_CODE"))) {
                                    value = directlineDatas.getData(j).getString("ATTR_VALUE");
                                    break;
                                }
                            } else if ("7010".equals(offerCode) || "7011".equals(offerCode)|| "7016".equals(offerCode)
                            		|| "70111".equals(offerCode)|| "70112".equals(offerCode)|| "70121".equals(offerCode)|| "70122".equals(offerCode)) {
                                if ("pattr_VILLAGEA".equals(directlineDatas.getData(j).getString("ATTR_CODE")) || "VILLAGEA".equals(directlineDatas.getData(j).getString("ATTR_CODE"))) {
                                    value = directlineDatas.getData(j).getString("ATTR_VALUE");
                                    break;
                                }
                            }

                        }
                        IData changeaddrData = new DataMap();
                        changeaddrData.put("ATTR_CODE", "pattr_CHANGEADDR");
                        changeaddrData.put("ATTR_NAME", "搬迁地址");
                        changeaddrData.put("ATTR_VALUE", value);
                        directlineDatas.add(changeaddrData);
                        newDirectlineDataList.add(directlineDatas);
                    }
                    param.put("DIRECTLINE_DATA", newDirectlineDataList);
                }
            } /* else if("MANUALSTOP".equals(bpmTempletId) || "MANUALBACK".equals(bpmTempletId)) { IDataset directlineDataList = param.getDataset("DIRECTLINE_DATA"); for (int i = 0, size = directlineDataList.size(); i < size; i++) { IDataset
               * directlineDatas = directlineDataList.getDataset(i); IData isControlData = new DataMap(); isControlData.put("ATTR_CODE", "pattr_ISCONTROL"); isControlData.put("ATTR_NAME", "是否自动信控"); isControlData.put("ATTR_VALUE", "否");
               * directlineDatas.add(isControlData); newDirectlineDataList.add(directlineDatas); } param.put("DIRECTLINE_DATA", newDirectlineDataList); } */
        }
    }

    private void creatAutoBackBPM(IData param, String offerCode) throws Exception {
        IDataset otherList = param.getDataset("OTHER_LIST");
        if (IDataUtil.isEmpty(otherList)) {
            return;
        }
        IData otherData = new DataMap();
        for (int i = 0; i < otherList.size(); i++) {
            IData data = otherList.getData(i);
            otherData.put(data.getString("ATTR_CODE"), data.getString("ATTR_VALUE"));
        }
        if ("1".equals(otherData.getString("IS_AUTOBACK"))) {
            IData inparam = (IData) Clone.deepClone(param);
            IData commData = inparam.getData("COMMON_DATA");
            commData.put("AUTO_TIME", otherData.getString("BACK_TIME"));
            commData.put("IS_AUTO_BPM", true);
            inparam.put("COMMON_DATA", commData);

            // 查询流程信息
            IData templetInfo = WorkfromViewCall.getOperTypeByTempletId(this, "MANUALBACK");

            IData input = new DataMap();
            input.put("BUSI_CODE", offerCode);
            input.put("OPER_TYPE", templetInfo.getString("BUSI_OPER_TYPE"));
            IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
            if (IDataUtil.isNotEmpty(busiSpecReleList)) {
                busiSpecReleList.first().put("TEMPLET_ID", "MANUALBACK");
                inparam.put("BUSI_SPEC_RELE", busiSpecReleList.first());

                // 查询流程节点信息
                input.clear();
                input.put("BPM_TEMPLET_ID", busiSpecReleList.first().getString("BPM_TEMPLET_ID"));
                input.put("NODE_TYPE", "3");
                IDataset nodeTempleteList = CSViewCall.call(this, "SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
                if (IDataUtil.isNotEmpty(nodeTempleteList)) {
                    IData nodeTempletedData = new DataMap();
                    String bpmTempletId = nodeTempleteList.first().getString("BPM_TEMPLET_ID");
                    nodeTempletedData.put("BPM_TEMPLET_ID", bpmTempletId);
                    nodeTempletedData.put("NODE_ID", nodeTempleteList.first().getString("NODE_ID"));

                    inparam.put("NODE_TEMPLETE", nodeTempletedData);
                }
            }

            IDataset emosAttrList = inparam.getDataset("EOMS_ATTR_LIST");
            if (IDataUtil.isNotEmpty(emosAttrList)) {
                for (int i = 0; i < emosAttrList.size(); i++) {
                    IData emosAttrDate = emosAttrList.getData(i);
                    if ("CHANGEMODE".equals(emosAttrList.getData(i).getString("ATTR_CODE"))) {
                        emosAttrDate.put("ATTR_VALUE", "复机");
                        break;
                    }
                }
            }
            inparam.put("EOMS_ATTR_LIST", emosAttrList);

            IData backparam = ScrDataTrans.buildWorkformSvcParam(inparam);
            IDataset resultList = CSViewCall.call(this, "SS.WorkformRegisterSVC.register", backparam);
            if (IDataUtil.isEmpty(resultList)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "生成复机流程失败！");
            }
            String backIbsysid = resultList.first().getString("IBSYSID");
            IData ibsysidData = new DataMap();
            ibsysidData.put("ATTR_CODE", "IBSYSID");
            ibsysidData.put("ATTR_NAME", "复机流程ID");
            ibsysidData.put("ATTR_VALUE", backIbsysid);
            ibsysidData.put("RECORD_NUM", "0");
            otherList.add(ibsysidData);
        }
        param.put("OTHER_LIST", otherList);
    }

    /**
     * 限制开通、勘察单最大专线数为100
     * 
     * @param submitData
     * @throws Exception
     */
    private void checkDataLineDiscntLimit(IData submitData) throws Exception {
        String busiformOperType = submitData.getData("BUSI_SPEC_RELE").getString("BUSIFORM_OPER_TYPE", "");
        if (("20".equals(busiformOperType) || "29".equals(busiformOperType))) {
            IData offerData = submitData.getData("OFFER_DATA");
            if (IDataUtil.isNotEmpty(offerData)) {
                int lineSize = submitData.getData("OFFER_DATA").getDataset("SUBOFFERS").size();
                String getConfig = StaticUtil.getStaticValue("DATALINE_LIMIT", "LIMIT");// 从配置中获取限制的条数
                if (lineSize > Integer.parseInt(getConfig)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "该开通/勘察单您提交了" + lineSize + "条专线，最大限制为" + getConfig + "，请重新选择！");
                }
            }

        }
    }

    private IData checkLinePrice(IDataset pattrs, String productId, String bandWidth, String linePrice, String LineNo) throws Exception {
        IData resultData = new DataMap();
        String resCode = "0";
        String resStr = "专线：";
        //云专线按标准专线标准
        if("70111".equals(productId)||"70112".equals(productId)){
        	productId="7011";
        }else if("70121".equals(productId)||"70122".equals(productId)){
        	productId="7012";
        }
        IData[] resultCode = getConfigValue("CHECKLINEPRICE_" + productId);
        logger.info("checkLinePrice-pattrs: " + pattrs);
        logger.info("checkLinePrice-resultCode: " + resultCode.length);
        for (int z = 0, size = pattrs.size(); z < size; z++) {
            IDataset offerCha = pattrs.getData(z).getDataset("OFFER_CHA");
            if (IDataUtil.isEmpty(offerCha)) {
                continue;
            }
            int rsrv_str1Int = 0;
            Double rsrv_str2Double = 0.00;
            String productNo = "";
            for (int i = 0; i < offerCha.size(); i++) {
                IData iData = offerCha.getData(i);
                if (bandWidth.equals(iData.getString("ATTR_CODE"))) {
                    rsrv_str1Int = Integer.parseInt(iData.getString("ATTR_VALUE", ""));
                }
                if (linePrice.equals(iData.getString("ATTR_CODE"))) {
                    rsrv_str2Double = Double.parseDouble(iData.getString("ATTR_VALUE", ""));
                }
                if (LineNo.equals(iData.getString("ATTR_CODE"))) {
                    productNo = iData.getString("ATTR_VALUE", "");
                }

            }
            int keyIntNow = -1;
            double valueDobleNow = 0.00;// 当前价格

            int keyInt1 = 0;
            double valueDoble1 = 0.00;// 低端价格
            int keyInt2 = 999999999;
            double valueDoble2 = 999999999;// 高端价格

            int keyIntMAX = 0;
            double valueDobleMAX = 0.00;// 最高价格

            boolean falg = false;
            logger.info("checkLinePrice-LineNo: " + productNo);
            for (IData resultCodeData : resultCode) {
                int keyInt = Integer.parseInt(resultCodeData.getString("KEY"));
                /* if(keyInt<=rsrv_str1Int&&keyIntNow<keyInt){ keyIntNow=keyInt; valueDobleNow=Double.parseDouble(resultCodeData.getString("VALUE")); } */
                double value = Double.parseDouble(resultCodeData.getString("VALUE"));
                if (keyInt == rsrv_str1Int) {
                    keyIntNow = keyInt;
                    valueDobleNow = value;
                    falg = true;
                    break;
                }
                if (keyInt < rsrv_str1Int && keyInt1 < keyInt) {
                    keyInt1 = keyInt;
                    valueDoble1 = value;
                    logger.info("checkLinePrice-keyInt1: " + keyInt1);
                }// 取临近最小值
                if (keyInt > rsrv_str1Int && keyInt2 > keyInt) {
                    keyInt2 = keyInt;
                    valueDoble2 = value;
                    logger.info("checkLinePrice-keyInt2: " + keyInt2);
                }// 取临近最大值

                if (keyIntMAX < keyInt || keyIntMAX == 0) {
                    keyIntMAX = keyInt;
                    valueDobleMAX = value;
                }// 取最大值

            }
            if (!falg) {
                keyIntNow = rsrv_str1Int;
                valueDobleNow = valueDoble1 + (rsrv_str1Int - keyInt1) * (valueDoble2 - valueDoble1) / (keyInt2 - keyInt1);
                if (valueDobleMAX < valueDobleNow) {// 高于当前值取最大值
                    valueDobleNow = valueDobleMAX;
                }
                if (keyIntNow != -1 && (rsrv_str2Double > valueDobleNow || rsrv_str2Double < valueDobleNow * 0.2)) {
                    resCode = "1";
                    resStr += productNo + "（" + (valueDobleNow * 0.2) + "-" + valueDobleNow + "）、";
                    resStr = resStr.substring(0, resStr.length() - 1) + " 当前设置专线价格高于标准资费或者低于标准资费(2折)!请上传资费附件或者重新填写资费！";
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, resStr);
                }
            } else {
                keyIntNow = rsrv_str1Int;
                if (keyIntNow != -1 && (rsrv_str2Double > valueDobleNow || rsrv_str2Double < valueDobleNow * 0.2)) {
                    resCode = "1";
                    resStr += productNo + "（" + (valueDobleNow * 0.2) + "-" + valueDobleNow + "）、";
                    resStr = resStr.substring(0, resStr.length() - 1) + " 当前设置专线价格高于标准资费或者低于标准资费(2折)!请上传资费附件或者重新填写资费！";
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, resStr);
                }
            }
        }

        return resultData;
    }

    private IData[] getConfigValue(String configName) throws Exception {
        // String[] paramNames = null;//configManager.getParamNamesByConfigName(configName);
        // String[] paramNames = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME"}, "PARAMNAME", new String[]{configName});
        IData input = new DataMap();
        input.put("CONFIG_NAME", configName);
        input.put("STATUS", "0");
        IDataset areas = CSViewCall.call(this, "SS.EweConfigQrySVC.qryEweConfigByConfigName", input); // pageutil.getList("TD_B_EWE_CONFIG", "CONFIGNAME", configName);

        IData[] configDatas = new DataMap[areas.size()];
        for (int i = 0; i < areas.size(); i++) {
            String paramname = areas.getData(i).getString("PARAMNAME");
            String configValue = StaticUtil.getStaticValue(getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { configName, paramname });// configManager.getParamValue(configName, paramname);
            IData temp = new DataMap();
            temp.put("KEY", paramname);
            temp.put("VALUE", configValue);
            configDatas[i] = temp;
        }
        return configDatas;
    }

    /**
     * 限制开通、勘察单最大专线数为100
     * 
     * @param submitData
     * @throws Exception
     */
    private void checkDatalineLimit(IData submitData) throws Exception {
        String busiformOperType = submitData.getData("BUSI_SPEC_RELE").getString("BUSIFORM_OPER_TYPE", "");
        if (("20".equals(busiformOperType) || "29".equals(busiformOperType))
        		||"21".equals(busiformOperType) || "62".equals(busiformOperType) 
        		|| "33".equals(busiformOperType) || "34".equals(busiformOperType)  
        		|| "299".equals(busiformOperType)|| "56".equals(busiformOperType)) {
            IData offerData = submitData.getData("OFFER_DATA");
            if (IDataUtil.isNotEmpty(offerData)) {
                int lineSize = submitData.getData("OFFER_DATA").getDataset("SUBOFFERS").size();
                String getConfig = StaticUtil.getStaticValue("DATALINE_LIMIT", "LIMIT");// 从配置中获取限制的条数
                if (lineSize > Integer.parseInt(getConfig)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "该业务订单您提交了" + lineSize + "条专线，最大限制为" + getConfig + "，请重新选择！");
                }
            }

        }
    }

    private void getLineUploadFileName(IData info, String offerCode) throws Exception {
        String fileName = "";
        if ("5080".equals(offerCode) || "5081".equals(offerCode)) {
            fileName = "LocalDirectLineImport.xls";
        } else if ("5083".equals(offerCode)) {
            fileName = "InternetDirectLineImport.xls";
        } else if ("5093".equals(offerCode)) {
            fileName = "ImsDirectLineImport.xls";
        }
        info.put("fileName", fileName);
    }

    private String getLineImportXmlPath(String offerCode) throws Exception {
        String path = "import/eop/";
        if ("5080".equals(offerCode) || "5081".equals(offerCode)) {
            path = path + "LocalDirectLineImport.xml";
        } else if ("5083".equals(offerCode)) {
            path = path + "InternetDirectLineImport.xml";
        } else if ("5093".equals(offerCode)) {
            path = path + "ImsDirectLineImport.xml";
        } else {
            CSViewException.apperr(GrpException.CRM_GRP_713, "没有获取到商品对应的导入格式文件！");
        }
        return path;
    }

    private String getLineExportXmlPath(String offerCode) throws Exception {
        String path = "export/eop/";
        if ("5080".equals(offerCode) || "5081".equals(offerCode)) {
            path = path + "LocalDirectLineExport.xml";
        } else if ("5083".equals(offerCode)) {
            path = path + "InternetDirectLineExport.xml";
        } else if ("5093".equals(offerCode)) {
            path = path + "ImsDirectLineExport.xml";
        } else {
            CSViewException.apperr(GrpException.CRM_GRP_713, "没有获取到商品对应的导出格式文件！");
        }
        return path;
    }

    private IDataset transSuccList2OfferChaList(IDataset succList) throws Exception {
        IDataset result = new DatasetList();

        String seqId = getIBsysID();

        for (int i = 0, size = succList.size(); i < size; i++) {
            IDataset offerChaList = new DatasetList();
            IData succData = succList.getData(i);

            // 生成订单号和bss工单号
            succData.put("tradeID", seqId + "_" + (i + 1));
            succData.put("confCRMTicketNo", seqId + (i + 1));
            Iterator<String> itr = succData.keySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next();
                String value = succData.getString(key);
                IData offerCha = new DataMap();
                offerCha.put("ATTR_CODE", key);
                offerCha.put("ATTR_VALUE", value);
                offerChaList.add(offerCha);
            }
            result.add(offerChaList);
        }
        return result;
    }

    private String getIBsysID() throws Exception {
        IData seqIbsysIData = CSViewCall.callone(this, "SS.SeqMgrSVC.getIBsysID", new DataMap());
        return seqIbsysIData.getString("seq_id");
    }

    public void download(IRequestCycle cycle) throws Exception {
        logger.debug("=====执行导入传入参数===lz====" + getData());
        String fileId = getData().getString("FILE_ID");
        String offerCode = getData().getString("OFFER_CODE");

        String importXmlPath = "import/eop/";// "upload/attach/";

        if ("5080".equals(offerCode) || "5081".equals(offerCode)) {
            importXmlPath = importXmlPath + "LocalDirectLineImport.xml";
        } else if ("5083".equals(offerCode)) {
            importXmlPath = importXmlPath + "InternetDirectLineImport.xml";
        } else if ("5093".equals(offerCode)) {
            importXmlPath = importXmlPath + "ImsDirectLineImport.xml";
        } else {
            CSViewException.apperr(GrpException.CRM_GRP_713, "没有获取到商品对应的导入格式文件！");
        }
        logger.debug("=====连接ftp主机=========" + importXmlPath);
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(this.getVisit());
        logger.debug("=====ftp主机已连接=========");

        IData fileData = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets(importXmlPath));
        logger.debug("=====fileData=========" + fileData);

        IDataset succds = new DatasetList();
        succds.addAll(((IDataset[]) fileData.get("right"))[0]);

        logger.debug("=====succds=========" + succds);
        IData ftpInfo = new DataMap();
        ftpInfo.put("SUCC_LIST", transSuccList2OfferChaList(succds));
        ftpInfo.put("APPLY_ID", getData().getString("APPLY_ID"));
        logger.debug("=====SUCC_LIST=========" + ftpInfo);
        setFtpInfo(ftpInfo);

    }

    /**
     * 根据合同编码查询合同下的专线信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getContractById(IRequestCycle cycle) throws Exception {
        IData param = new DataMap();
        param.put("CONTRACT_ID", getData().getString("CONTRACT_ID"));
        param.put("PRODUCT_ID", getData().getString("PRODUCT_ID"));
        param.put("CUST_ID", getData().getString("CUST_ID"));
        IDataset contractInfo = CSViewCall.call(this, "CM.ConstractGroupSVC.getDirectlineContractList", param);

        if (IDataUtil.isNotEmpty(contractInfo) && !getData().getString("CONTRACT_ID").isEmpty()) {
            IData info = contractInfo.getData(0);
            IData temp = new DataMap();
            Iterator<String> itr = info.keySet().iterator();
            while (itr.hasNext()) {
                String attrCode = itr.next();
                if (attrCode.contains("_DATE")) {
                    String attrValue = info.getString(attrCode);
                    if (attrValue == null) {
                        temp.put(attrCode, "");
                        continue;
                    } else {
                        String date = attrValue.substring(0, attrValue.indexOf("."));// 截取.0之前的日期
                        temp.put(attrCode, date);
                    }
                } else {
                    String attrValue = info.getString(attrCode, "");
                    temp.put(attrCode, attrValue);
                }
            }
            temp.put("C_PRODUCT_START_DATE_OLD", temp.getString("C_PRODUCT_START_DATE", ""));
            temp.put("C_PRODUCT_END_DATE_OLD", temp.getString("C_PRODUCT_END_DATE", ""));
            setInfo(temp);
            setAjax(temp);
        }
    }

    /**
     * 新增时生成专线实例号
     * 
     * @param cycle
     * @throws Exception
     */
    public void getProductNo(IRequestCycle cycle) throws Exception {
        // 初始化生成专线实例号
        IData inParam = new DataMap();
        IDataset dataLine = CSViewCall.call(this, "CS.SeqMgrSVC.getMaxNumberLine", inParam);
        IData seqData = (IData) dataLine.get(0);
        String productNo = seqData.getString("seq_id");
        IData busi = new DataMap();
        busi.put("LINE_NO", productNo);
        busi.put("RSRV_STR9", productNo);
        setBusi(busi);
        this.setAjax(busi);
    }

    /**
     * 初始化获得该集团下的合同
     * 
     * @param cycle
     * @throws Exception
     */
    public void initContract(IRequestCycle cycle) throws Exception {
        IData info = new DataMap();
        IData param = new DataMap();
        String iFNew = getData().getString("IF_NEW");
        String custId = getData().getString("CUST_ID");
        String serialNumber = getData().getString("SERIAL_NUMBER");
        // 审核不通过，重回APPLY节点记号
        String isReadonly = getData().getString("IS_READONLY");
        if (iFNew.equals("1")) {
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("REMOVE_TAG", "0");
            IDataset userInfos = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.qrySelAllBySn", param);
            if (IDataUtil.isNotEmpty(userInfos)) {
                IData contractInput = new DataMap();
                contractInput.put("CONTRACT_ID", userInfos.getData(0).getString("CONTRACT_ID"));
                IDataset contracts = CSViewCall.call(this, "CS.CustContractInfoQrySVC.qryContractInfoByContractIdForGrp", contractInput);
                IDataset c = new DatasetList();
                if (null != contracts && contracts.size() > 0) {
                    for (int i = 0; i < contracts.size(); i++) {
                        IData temp = contracts.getData(i);
                        if (null != temp.getString("CONTRACT_NAME")) {
                            temp.put("CONTRACT", temp.getString("CONTRACT_ID", "") + "|" + temp.getString("CONTRACT_NAME", ""));
                            c.add(temp);
                        }
                    }
                }
                info.put("CONTRACTS", c);
            }
        } else if (iFNew.equals("0")) {
            String staffId = getVisit().getStaffId();
            String departId = getVisit().getDepartId();
            info.put("C_CUST_ID", custId);
            info.put("C_DEVELOP_STAFF_ID", staffId);
            info.put("C_DEVELOP_DEPART_ID", departId);
        }

        if ("true".equals(isReadonly)) {
            IData input = new DataMap();
            input.put("IBSYSID", getData().getString("IBSYSID"));
            input.put("NODE_ID", "apply");
            input.put("RECORD_NUM", "0");
            IDataset attrDatas = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", input);
            if (IDataUtil.isNotEmpty(attrDatas)) {
                for (int i = 0; i < attrDatas.size(); i++) {
                    info.put(attrDatas.getData(i).getString("ATTR_CODE"), attrDatas.getData(i).getString("ATTR_VALUE"));
                }
            }

            if ("1".equals(info.getString("C_IF_NEW_CONTRACT"))) {

                param.put("SERIAL_NUMBER", serialNumber);
                param.put("REMOVE_TAG", "0");
                IDataset userInfos = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.qrySelAllBySn", param);
                if (IDataUtil.isNotEmpty(userInfos)) {
                    IData contractInput = new DataMap();
                    contractInput.put("CONTRACT_ID", userInfos.getData(0).getString("CONTRACT_ID"));
                    IDataset contracts = CSViewCall.call(this, "CS.CustContractInfoQrySVC.qryContractInfoByContractIdForGrp", contractInput);
                    IDataset c = new DatasetList();
                    if (null != contracts && contracts.size() > 0) {
                        for (int i = 0; i < contracts.size(); i++) {
                            IData temp = contracts.getData(i);
                            if (null != temp.getString("CONTRACT_NAME")) {
                                temp.put("CONTRACT", temp.getString("CONTRACT_ID", "") + "|" + temp.getString("CONTRACT_NAME", ""));
                                c.add(temp);
                            }
                        }
                    }
                    info.put("CONTRACTS", c);
                }

                String contractId = info.getString("CONTRACT_ID");
                IData cparam = new DataMap();
                cparam.put("CONTRACT_ID", contractId);
                cparam.put("CUST_ID", custId);
                IData contractInfo = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryContractInfo", cparam);
                String contractStr = contractInfo.getString("CONTRACT_ID") + "|" + contractInfo.getString("CONTRACT_NAME");
                // contractStr = contractStr.replace(":", "|");
                info.put("CONTRACT", contractStr);
            } else {
                // 获取合同附件
                String subIbsysid = attrDatas.first().getString("SUB_IBSYSID");
                IData inparam = new DataMap();
                inparam.put("IBSYSID", getData().getString("IBSYSID"));
                inparam.put("SUB_IBSYSID", subIbsysid);
                inparam.put("RECORD_NUM", "0");
                IDataset attachList = CSViewCall.call(this, "SS.WorkFormSVC.queryByAttach", inparam);
                if (IDataUtil.isNotEmpty(attachList)) {
                    for (int i = 0; i < attachList.size(); i++) {
                        IData attachData = attachList.getData(i);
                        if ("C".equals(attachData.getString("ATTACH_TYPE"))) {
                            info.put("C_CONTRACT_FILE_ID", attachData.getString("FILE_ID"));
                            IData fileData = new DataMap();
                            fileData.put("FILE_ID", attachData.getString("FILE_ID"));
                            fileData.put("FILE_NAME", attachData.getString("ATTACH_NAME"));
                            fileData.put("ATTACH_TYPE", attachData.getString("ATTACH_TYPE"));
                            // info.put("C_FILE_LIST", fileData.toString());
                            setAjax(fileData);
                        }
                    }
                }
            }

        }

        setInfo(info);
    }

    /**
     * 获得关联的勘察单号
     * 
     * @param cycle
     * @throws Exception
     */
    public void initConfCrm(IRequestCycle cycle) throws Exception {
        IData confCrmInfos = new DataMap();
        IData data = getData();
        // 选完勘察单后，如果是VOIP，将BPM_TEMPLET_ID转为ECHANGERESOURCECONFIRM
        if (data.getString("BPM_TEMPLET_ID").equals("EVIOPDIRECTLINECHANGEPBOSS")) {
            data.put("BPM_TEMPLET_ID", "ECHANGERESOURCECONFIRM");
        }
        setCondition(data);
        String bizrange = data.getString("BIZRANGE");
        // 查询此集团此产品有多少条勘察单号
        IDataset infos = CSViewCall.call(this, "SS.ConfCrmQrySVC.qryIbsysid", data);
        IDataset c = new DatasetList();
        // 变更选择勘察单
        if (data.getString("BPM_TEMPLET_ID").equals("ECHANGERESOURCECONFIRM")) {
            // 通过集团USER_ID查询此变更单对应的开通单的IBSYSID
            String ecUserId = data.getString("EC_USER_ID");
            IData proInput = new DataMap();
            proInput.put("USER_ID", ecUserId);
            IDataset hisProInfos = CSViewCall.call(this, "SS.WorkformProductSVC.qryProductByuserId", proInput);
            if (IDataUtil.isNotEmpty(hisProInfos)) {
                for (int i = 0; i < hisProInfos.size(); i++) {
                    IData hisProInfo = hisProInfos.getData(i);
                    String ibsysId = hisProInfo.getString("IBSYSID");
                    IData subInput = new DataMap();
                    subInput.put("IBSYSID", ibsysId);
                    IData subHInfo = CSViewCall.callone(this, "SS.WorkformSubscribeSVC.qryWorkformHSubscribeByIbsysid", subInput);
                    // BPM_TEMPLET_ID为ECHANGERESOURCECONFIRM的单子
                    if (IDataUtil.isNotEmpty(subHInfo)) {
                        // 去除已撤单的单子
                        String dealState = subHInfo.getString("DEAL_STATE");
                        if (dealState.equals("4")) {
                            continue;
                        }
                        String bpmTempletId = subHInfo.getString("BPM_TEMPLET_ID");
                        if (bpmTempletId.equals("ECHANGERESOURCECONFIRM")) {
                            IData temp = new DataMap();
                            // 获得主题
                            String title = subHInfo.getString("RSRV_STR4");

                            String ibsysid = subHInfo.getString("IBSYSID");
                            // 去除状态不为F的勘察单
                            IData attrHInput = new DataMap();
                            attrHInput.put("IBSYSID", ibsysid);
                            attrHInput.put("NODE_ID", "apply");
                            IDataset attrHInfos = CSViewCall.call(this, "SS.WorkformAttrSVC.qryLineNoByIbsysid", attrHInput);
                            if (IDataUtil.isNotEmpty(attrHInfos)) {
                                int num = 0;
                                for (int k = 0; k < attrHInfos.size(); k++) {
                                    IData attrHInfo2 = attrHInfos.getData(k);
                                    if ((attrHInfo2.getString("ATTR_CODE").equals("PRODUCTNO"))) {
                                        IData poolInput = new DataMap();
                                        String lineNo = attrHInfo2.getString("ATTR_VALUE");
                                        poolInput.put("POOL_VALUE", lineNo);
                                        poolInput.put("REL_IBSYSID", ibsysid);
                                        // 在勘察池表查询此条专线实例号状态是否不为F
                                        IDataset subPoolInfos = CSViewCall.call(this, "SS.ConfCrmQrySVC.qryStateByRelIbsysidPoolValue", poolInput);
                                        if (IDataUtil.isNotEmpty(subPoolInfos)) {
                                            String state = subPoolInfos.getData(0).getString("STATE");
                                            if (state.equals("F")) {
                                                // 获得条数
                                                num++;
                                            }
                                        }
                                    }
                                }
                                if (num != 0) {
                                    temp.put("CONFCRMTICKETNO", "单号:【" + ibsysid + "】;主题:【" + title + "】;条数:【" + num + "】");
                                    c.add(temp);
                                }
                            }
                        }
                    }
                }
            }
        } else {// 开通选择勘察单
            for (int i = 0; i < infos.size(); i++) {
                IData temp = infos.getData(i);
                String ibsysid = temp.getString("IBSYSID");
                data.put("REL_IBSYSID", ibsysid);
                // 查询此勘察单总共有多少条
                IDataset confInfos = CSViewCall.call(this, "SS.ConfCrmQrySVC.qryLineNo", data);
                int isOpenNum = 0;
                int isNotOpenNum = 0;
                if (IDataUtil.isNotEmpty(confInfos)) {
                    // 查询此勘察单已开通多少条
                    for (int j = 0; j < confInfos.size(); j++) {
                        IData confInfo = confInfos.getData(j);
                        String state = confInfo.getString("STATE");
                        if (state.equals("A") || state.equals("9")) {
                            isOpenNum++;
                        }
                    }
                    // 剩下为开通条数
                    isNotOpenNum = confInfos.size() - isOpenNum;
                    if (isNotOpenNum != 0) {
                        // 根据IBSYSID在ATTR历史表查出最大SUB_IBSYSID对应的业务范围
                        IData input = new DataMap();
                        input.put("IBSYSID", ibsysid);
                        input.put("NODE_ID", "apply");
                        input.put("RECORD_NUM", "0");
                        IDataset maxSubIbsysidInfos = CSViewCall.call(this, "SS.WorkformAttrSVC.qryMaxSubibsysyid", input);
                        if (IDataUtil.isNotEmpty(maxSubIbsysidInfos)) {
                            String maxSubIbsysid = maxSubIbsysidInfos.getData(0).getString("SUB_IBSYSID");
                            input.put("SUB_IBSYSID", maxSubIbsysid);
                            IDataset hisAttrInfos = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishEweServiceAttributes", input);
                            if (IDataUtil.isNotEmpty(hisAttrInfos)) {
                                // 获得主题
                                IDataset titles = CSViewCall.call(this, "SS.ConfCrmQrySVC.qrySubscribe", input);
                                String title = titles.getData(0).getString("RSRV_STR4");
                                temp.put("CONFCRMTICKETNO", "工单号为【" + ibsysid + "】,主题【" + title + "】的勘察单共计" + confInfos.size() + "条专线，已发开通单" + isOpenNum + "条,剩余" + isNotOpenNum + "条");
                            }
                        }
                        c.add(temp);
                    }
                }
            }
        }

        confCrmInfos.put("CONFCRMTICKETNOS", c);
        setInfo(confCrmInfos);

        // IData input = new DataMap();
        // if(IDataUtil.isNotEmpty(infos)) {
        // String ibsysid = infos.getData(0).getString("IBSYSID");
        // input.put("IBSYSID", ibsysid);
        // IDataset info3 = CSViewCall.call(this, "SS.WorkformEomsStateSVC.queryHisEomsStateByIbsysidAndProductNo", input);
        // if(IDataUtil.isEmpty(info3)) {
        // IDataset info4 = CSViewCall.call(this, "SS.WorkformEomsStateSVC.qryEomsStateByIbsysidAndProductNo", input);
        // this.setAjax(info4);
        // }else {
        // this.setAjax(info3);
        // }
        // }
    }

    /**
     * 查询快速开通地址
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryLineAddr(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.LineAddrQrySVC.qryQuickOpenLineAddr", data, this.getPagination("olcomnav"));
        IDataset infos = output.getData();
        IDataset infonew = new DatasetList();
        for (int i = 0; i < infos.size(); i++) {
            IData dataold = new DataMap();
            IData datanew = new DataMap();
            dataold = infos.getData(i);
            datanew.put("addrStandard", dataold.getString("STANDARD_ADDR"));
            datanew.put("PROVINCEA", dataold.getString("PROVINCE_ADDR", ""));
            datanew.put("CITYA", dataold.getString("CITY_ADDR", ""));
            datanew.put("AREAA", dataold.getString("AREA_ADDR", ""));
            datanew.put("COUNTYA", dataold.getString("COUNTY_ADDR", ""));
            datanew.put("VILLAGEA", dataold.getString("VILLAGE_ADDR", ""));
            datanew.put("DEVICEID", dataold.getString("DEVICE_ID", ""));
            datanew.put("CAPACITY_UNUSED", dataold.getString("CAPACITY_UNUSED", ""));
            datanew.put("addrAll", dataold.getString("PROVINCE_ADDR", "") + "," + dataold.getString("CITY_ADDR", "") + "," + dataold.getString("AREA_ADDR", "") + "," + dataold.getString("VILLAGE_ADDR", "") + ","
                    + dataold.getString("VILLAGE_ADDR", "") + "," + dataold.getString("STANDARD_ADDR"));
            infonew.add(datanew);
        }
        setInfos(infonew);
        setInfoCount(output.getDataCount());
        setCondition(data);
        setInfo(data);
    }

    /**
     * 查询账户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryEcAccountList(IRequestCycle cycle) throws Exception {
        String custId = this.getData().getString("CUST_ID");
        IDataset accounts = UCAInfoIntfViewUtil.qryGrpAcctInfosByCustId(this, custId);
        setEcAccountList(accounts);

    }

    /**
     * 查询是否厚覆盖
     * 
     * @param cycle
     * @throws Exception
     */
    public void getCoverInfoAddr(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset info = new DatasetList();
        info = CSViewCall.call(this, "SS.LineAddrQrySVC.qryCoverInfoAddr", data);
        IData dataNew = new DataMap();
        dataNew.put("IS_COVER_TAG", "1");
        if (!info.isEmpty()) {
            if (info.getData(0).getString("IS_COVER_TAG", "").equals("厚覆盖")) {
                dataNew.put("IS_COVER_TAG", "0");
            }
        }
        setCondition(dataNew);
        this.setAjax(dataNew);
    }

    public void importFile(IRequestCycle cycle) throws Exception {
        IData data = getData();
        setCondition(data);
        IDataset datalineInfo = (IDataset) SharedCache.get("DATALINE_INFOS");
        setInfos(datalineInfo);
    }

    public void getContractId(IData submitData) throws Exception {
        IData input = new DataMap();
        IDataset contractData = submitData.getDataset("CONTRACT_DATA");
        for (int i = 0; i < contractData.size(); i++) {
            IData temp = new DataMap();
            IData workformAttr = new DataMap();
            temp = contractData.getData(i);
            workformAttr = new DataMap();
            String key = temp.getString("ATTR_CODE");
            if (key.contains("C_")) {
                workformAttr.put(key.substring(key.indexOf("C_")), temp.getString("ATTR_VALUE"));
                input.putAll(workformAttr);
            }
        }
        IDataset directlineDate = submitData.getDataset("DIRECTLINE_DATA");
        IData line = new DataMap();
        IDataset dataLine = new DatasetList();
        if (IDataUtil.isNotEmpty(directlineDate)) {
            for (int i = 0; i < directlineDate.size(); i++) {
                IData temp = new DataMap();
                IData dAttr = new DataMap();
                IDataset temp2 = directlineDate.getDataset(i);
                for (int j = 0; j < temp2.size(); j++) {
                    temp = temp2.getData(j);
                    String key = temp.getString("ATTR_CODE");
                    if (key.contains("NOTIN_")) {
                        dAttr.put(StringUtils.substringAfter(key, "NOTIN_"), temp.getString("ATTR_VALUE"));
                    }
                }
                dataLine.add(dAttr);
            }
        }
        line.put("DIRECTLINE", dataLine);
        input.putAll(line);
        input.put("CUST_ID", input.getString("C_CUST_ID"));
        input.put("PRODUCT_START_DATE", input.getString("C_PRODUCT_START_DATE"));
        input.put("PRODUCT_END_DATE", input.getString("C_PRODUCT_END_DATE"));
        input.put("PRODUCT_ID", submitData.getData("OFFER_DATA").getString("OFFER_CODE"));
        input.put("POATT_TYPE", input.getString("C_POATT_TYPE"));

        IData resultInfo = CSViewCall.callone(this, "CM.ConstractGroupSVC.insertDirectlineContract", input);
        String contractId = resultInfo.getString("CONTRACT_ID");
        IData temp = new DataMap();
        temp.put("ATTR_VALUE", contractId);
        temp.put("ATTR_NAME", "合同编号");
        temp.put("ATTR_CODE", "CONTRACT_ID");
        contractData.add(temp);
        submitData.put("CONTRACT_DATA", contractData);
    }

    public void batchImportFile(IRequestCycle cycle) throws Exception {
        IDataset datalineInfo = (IDataset) SharedCache.get("DATALINE_INFOS");
        IDataset temp = new DatasetList();
        IData dataline = new DataMap();
        for (int i = 0; i < datalineInfo.size(); i++) {
            dataline = datalineInfo.getData(i);
            // 导入时查看专线名称是否被占用（没写完）
            String tradeName = dataline.getString("TRADENAME");

            // 初始化生成专线实例号
            IData inParam = new DataMap();
            IDataset dataLine = CSViewCall.call(this, "CS.SeqMgrSVC.getMaxNumberLine", inParam);
            IData seqData = (IData) dataLine.get(0);
            String productNo = seqData.getString("seq_id");
            dataline.put("NOTIN_LINE_NO", productNo);
            dataline.put("NOTIN_RSRV_STR9", productNo);
            dataline.put("pattr_TRADEID", productNo);
            dataline.put("pattr_PRODUCTNO", productNo);
            temp.add(dataline);
        }
        this.setAjax(temp);
    }

    public void batchImportFileZHZG(IRequestCycle cycle) throws Exception {
        IDataset datalineInfo = (IDataset) SharedCache.get("DATALINE_INFOS");
        IDataset temp = new DatasetList();
        IData dataline = new DataMap();
        for (int i = 0; i < datalineInfo.size(); i++) {
            dataline = datalineInfo.getData(i);
            // 初始化生成专线实例号
            IData inParam = new DataMap();
            IDataset dataLine = CSViewCall.call(this, "CS.SeqMgrSVC.getMaxNumberLine", inParam);
            IData seqData = (IData) dataLine.get(0);
            String productNo = seqData.getString("seq_id");
            dataline.put("NOTIN_LINE_NO", productNo);
            dataline.put("pattr_TRADEID", productNo);
            dataline.put("pattr_PRODUCTNO", productNo);
            temp.add(dataline);
        }
        this.setAjax(temp);
    }

    public void ChangeSimpleImport(IRequestCycle cycle) throws Exception {
        IDataset datalineInfo = (IDataset) SharedCache.get("VOIPDATALINE_INFOS");
        this.setAjax(datalineInfo);
    }

    public void batchImportFileForMarketing(IRequestCycle cycle) throws Exception {
        IDataset Info = (IDataset) SharedCache.get("MARKETING_INFOS");
        this.setAjax(Info);
    }

    public void qryStaffinfo(IRequestCycle cycle) throws Exception {
        IData input = getData();
        IData inParam = new DataMap();
        String staffName = input.getString("cond_StaffName", "");
        /* if (StringUtils.isNotBlank(staffName)) { inParam.put("STAFF_NAME", staffName); } */
        String roleId = pageutil.getStaticValue("TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "AUDIT_ROLE", "ROLE_ID" });
        if (StringUtils.isBlank(roleId)) {
            CSViewException.apperr(GrpException.CRM_GRP_713, "没有获取计费方式审核角色配置！请检查TD_B_EWE_CONFIG表配置！");
        }
        // input.put("DEPART_ID", departId);
        inParam.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());
        inParam.put("START_MAX", "0");
        inParam.put("ROWNUM_", "1000");
        inParam.put("X_GETMODE", "13");
        inParam.put("RIGHT_CODE", roleId);
        IDataset staffList = CSViewCall.call(this, "QSM_ChkSysOrgInfo", inParam);
        if (StringUtils.isNotBlank(staffName)) {
            for (int i = 0; i < staffList.size(); i++) {
                IData staff = staffList.getData(i);
                if (staffName.equals(staff.getString("STAFF_NAME"))) {
                    IDataset staffListName = new DatasetList();
                    staffListName.add(staff);
                    setInfos(staffListName);
                }
            }
        } else {
            setInfos(staffList);
        }
        // inParam.put("FLAG", "1");
        // IDataset info = CSViewCall.call(this, "SS.QcsGrpIntfSVC.qryStaffinfoForESOPNEW", inParam);

    }

    public void qrySpeStaffinfo(IRequestCycle cycle) throws Exception {
        String roleId = pageutil.getStaticValue("TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "ACCEPTTANCE_PERIOD_AUDIT", "ROLE_ID" });
        if (StringUtils.isBlank(roleId)) {
            CSViewException.apperr(GrpException.CRM_GRP_713, "没有获取计费方式审核角色配置！请检查TD_B_EWE_CONFIG表配置！");
        }
        IData input = new DataMap();
        // input.put("DEPART_ID", departId);
        input.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());
        input.put("START_MAX", "0");
        input.put("ROWNUM_", "1000");
        input.put("X_GETMODE", "13");
        input.put("RIGHT_CODE", roleId);
        IDataset staffList = CSViewCall.call(this, "QSM_ChkSysOrgInfo", input);
        this.staffList = staffList;
        setInfos(staffList);
    }

    public void qryStaffinfoByName(IRequestCycle cycle) throws Exception {
        String staffName = getData().getString("STAFF_NAME");
        IDataset result = new DatasetList();
        if (IDataUtil.isNotEmpty(staffList)) {
            for (int i = 0; i < staffList.size(); i++) {
                if (staffList.getData(i).getString("STAFF_NAME", "").contains(staffName)) {
                    result.add(staffList.getData(i));
                }
            }
        }
        setInfos(result);
    }

    /**
     * 专线变更修改合同资费
     * 
     * @param submitData
     * @throws Exception
     */
    public void updateContractDiscount(IData submitData) throws Exception {
        IData input = new DataMap();
        input.put("CUST_ID", "");
        IDataset directlineDate = submitData.getDataset("DIRECTLINE_DATA");
        IData line = new DataMap();
        IDataset dataLine = new DatasetList();
        if (IDataUtil.isNotEmpty(directlineDate)) {
            for (int i = 0; i < directlineDate.size(); i++) {
                IData temp = new DataMap();
                IData dAttr = new DataMap();
                IDataset temp2 = directlineDate.getDataset(i);
                for (int j = 0; j < temp2.size(); j++) {
                    temp = temp2.getData(j);
                    String key = temp.getString("ATTR_CODE");
                    if (key.contains("NOTIN_")) {
                        dAttr.put(StringUtils.substringAfter(key, "NOTIN_"), temp.getString("ATTR_VALUE"));
                    }
                    // 通过专线实例号查询IBSYSID,再通过IBSYSID查询已有的CONTRACT_ID
                    if (key.equals("NOTIN_LINE_NO")) {
                        String lineNo = temp.getString("ATTR_VALUE");
                        IData inParam = new DataMap();
                        inParam.put("ATTR_CODE", "NOTIN_LINE_NO");
                        inParam.put("ATTR_VALUE", lineNo);
                        inParam.put("NODE_ID", "apply");
                        IData attrInfo = CSViewCall.callone(this, "SS.WorkformAttrSVC.getIbsysidByAttrcodeAndAttrvalue", inParam);
                        if (IDataUtil.isNotEmpty(attrInfo)) {
                            String ibsysid = attrInfo.getString("IBSYSID");
                            inParam.clear();
                            inParam.put("IBSYSID", ibsysid);
                            IDataset infos = CSViewCall.call(this, "SS.WorkformAttrSVC.getInfoByIbsysidAttrtype", inParam);
                            for (int k = 0; k < infos.size(); k++) {
                                IData info = infos.getData(k);
                                if ("CONTRACT_ID".equals(info.getString("ATTR_CODE"))) {
                                    input.put("CONTRACT_ID", info.getString("ATTR_VALUE"));
                                }
                                if ("C_PRODUCT_START_DATE".equals(info.getString("ATTR_CODE"))) {
                                    input.put("PRODUCT_START_DATE", info.getString("ATTR_VALUE"));
                                }
                                if ("C_PRODUCT_END_DATE".equals(info.getString("ATTR_CODE"))) {
                                    input.put("PRODUCT_END_DATE", info.getString("ATTR_VALUE"));
                                }
                                if ("C_CUST_ID".equals(info.getString("ATTR_CODE"))) {
                                    input.put("CUST_ID", info.getString("ATTR_VALUE"));
                                }
                            }
                        }
                    }
                }
                dataLine.add(dAttr);
            }
        }
        if (!input.getString("CUST_ID").equals("")) {
            line.put("DIRECTLINE", dataLine);
            input.putAll(line);
            input.put("PRODUCT_ID", submitData.getData("OFFER_DATA").getString("OFFER_CODE"));
            CSViewCall.callone(this, "CM.ConstractGroupSVC.updateDirectlineContract", input);
        }
    }

    /**
     * 获得选择的勘察单下所有专线信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getConfCrmInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        setCondition(data);
        if (data.getString("BPM_TEMPLET_ID").equals("ERESOURCECONFIRMZHZG")) {
            // 查询此集团下有哪些专线办理过营销活动
            IDataset tempLineNos = new DatasetList();
            IData marketingInput = new DataMap();
            marketingInput.put("CUSTOMNO", data.getString("GROUP_ID"));
            IDataset marketingInfos = CSViewCall.call(this, "SS.TapMarketingSVC.selTapMarketingByIbsysidMarketing", marketingInput);
            if (IDataUtil.isNotEmpty(marketingInfos)) {
                for (int k = 0; k < marketingInfos.size(); k++) {
                    IData marketingInfo = marketingInfos.getData(k);
                    String ibsysidMarketing = marketingInfo.getString("IBSYSID_MARKETING");
                    IData attrHInput = new DataMap();
                    attrHInput.put("IBSYSID", ibsysidMarketing);
                    attrHInput.put("NODE_ID", "apply");
                    IDataset attrHInfos = CSViewCall.call(this, "SS.WorkformAttrSVC.qryLineNoByIbsysid", attrHInput);
                    if (IDataUtil.isNotEmpty(attrHInfos)) {
                        for (int m = 0; m < attrHInfos.size(); m++) {
                            IData tempLineNo = new DataMap();
                            IData attrHInfo = attrHInfos.getData(m);
                            String attrCode = attrHInfo.getString("ATTR_CODE");
                            if (attrCode.equals("NOTIN_LINE_NO")) {
                                String attrValue = attrHInfo.getString("ATTR_VALUE");
                                tempLineNo.put("NOTIN_LINE_NO", attrValue);
                                tempLineNos.add(tempLineNo);
                            }
                        }
                    }
                }
            }
            // 从勘察池获得剩余开通单的专线实例号(状态为F)
            IData poolInput = new DataMap();
            poolInput.put("STATE", "F");
            poolInput.put("REL_IBSYSID", data.getString("IBSYSID"));
            IDataset info = CSViewCall.call(this, "SS.ConfCrmQrySVC.qryLineNo", poolInput);
            IDataset input = new DatasetList();
            for (int i = 0; i < info.size(); i++) {
                IData datalineInput = new DataMap();
                String lineNo = info.getData(i).getString("POOL_VALUE");

                // 判断此专线是否办理过营销活动
                if (IDataUtil.isNotEmpty(tempLineNos)) {
                    for (int p = 0; p < tempLineNos.size(); p++) {
                        IData tempLineNo = tempLineNos.getData(p);
                        String notinLineNo = tempLineNo.getString("NOTIN_LINE_NO");
                        if (notinLineNo.equals(lineNo)) {
                            // 办理过营销活动
                            datalineInput.put("NOTIN_MARKETING_TAG", 1);
                            datalineInput.put("NOTIN_RSRV_STR2", 0);// 月租费
                            datalineInput.put("NOTIN_RSRV_STR3", 0);// 一次性费用（安装调试费）
                            datalineInput.put("NOTIN_RSRV_STR10", 0);// IP地址使用费
                            datalineInput.put("NOTIN_RSRV_STR11", 0);// 软件应用服务费
                            datalineInput.put("NOTIN_RSRV_STR12", 0);// 技术支持服务费

                            datalineInput.put("NOTIN_RSRV_STR15", 0);// 语音通信费

                            datalineInput.put("NOTIN_RSRV_STR16", 0);// SLA服务费
                            break;
                        } else {
                            // 没办理过营销活动
                            datalineInput.put("NOTIN_MARKETING_TAG", 0);
                        }
                    }
                } else {
                    // 没办理过营销活动
                    datalineInput.put("NOTIN_MARKETING_TAG", 0);
                }
                IData eomsInput = new DataMap();
                eomsInput.put("PRODUCT_NO", lineNo);
                eomsInput.put("IBSYSID", data.getString("IBSYSID"));
                // 在TF_BH_EOP_EOMS_STATE表查询此条专线实例号record_num
                IDataset eomsStateInfos = CSViewCall.call(this, "SS.WorkformEomsStateSVC.queryHisEomsStateByIbsysidAndProductNo", eomsInput);
                if (IDataUtil.isNotEmpty(eomsStateInfos)) {
                    IData eomsStateInfo = eomsStateInfos.getData(0);
                    // 取SERIALNO
                    String serialNo = eomsStateInfo.getString("SERIALNO");
                    datalineInput.put("SERIALNO", serialNo);

                    String recordNum = eomsStateInfo.getString("RECORD_NUM");
                    // 在TF_BH_EOP_EOMS表查询group_seq、sub_ibsysid、record_num
                    IData eomsHInput = new DataMap();
                    eomsHInput.put("RECORD_NUM", recordNum);
                    eomsHInput.put("IBSYSID", data.getString("IBSYSID"));
                    IDataset eomsInfos = CSViewCall.call(this, "SS.WorkformEomsSVC.qryHisEomsByIbsysIdOperTypeGroupSeq", eomsHInput);
                    if (IDataUtil.isNotEmpty(eomsInfos)) {
                        IData eomsInfo = eomsInfos.getData(0);
                        String groupSeq = eomsInfo.getString("GROUP_SEQ");
                        String subIbsysid = eomsInfo.getString("SUB_IBSYSID");
                        IData subInput = new DataMap();
                        subInput.put("GROUP_SEQ", groupSeq);
                        subInput.put("SUB_IBSYSID", subIbsysid);
                        subInput.put("RECORD_NUM", recordNum);
                        IDataset subInfos = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishAttrByGroupSeqRecordNum", subInput);
                        for (int j = 0; j < subInfos.size(); j++) {
                            IData subInfo = subInfos.getData(j);
                            IData temp = new DataMap();
                            String attrCode = subInfo.getString("ATTR_CODE");
                            String attrValue = subInfo.getString("ATTR_VALUE");
                            temp.put(attrCode, attrValue);
                            datalineInput.putAll(temp);
                        }
                        IData subInput2 = new DataMap();
                        subInput2.put("GROUP_SEQ", groupSeq);
                        subInput2.put("SUB_IBSYSID", subIbsysid);
                        subInput2.put("RECORD_NUM", 0);
                        IDataset subInfos2 = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishAttrByGroupSeqRecordNum", subInput2);
                        for (int k = 0; k < subInfos2.size(); k++) {
                            IData subInfo2 = subInfos2.getData(k);
                            IData temp2 = new DataMap();
                            String attrCode2 = subInfo2.getString("ATTR_CODE");
                            // if(attrCode2.equals("BUILDINGSECTION")||attrCode2.equals("BIZRANGE")||attrCode2.equals("TITLE")||attrCode2.equals("URGENCY_LEVEL")||attrCode2.equals("COUNTYNAME")||attrCode2.equals("COUNTYNAME")) {
                            String attrValue2 = subInfo2.getString("ATTR_VALUE");
                            temp2.put(attrCode2, attrValue2);
                            datalineInput.putAll(temp2);
                            // break;
                            // }
                        }
                    } else {
                        IData subInput = new DataMap();
                        subInput.put("RECORD_NUM", recordNum);
                        subInput.put("IBSYSID", data.getString("IBSYSID"));
                        subInput.put("NODE_ID", "archive");
                        IDataset subInfos = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", subInput);
                        for (int j = 0; j < subInfos.size(); j++) {
                            IData subInfo = subInfos.getData(j);
                            IData temp = new DataMap();
                            String attrCode = subInfo.getString("ATTR_CODE");
                            String attrValue = subInfo.getString("ATTR_VALUE");
                            temp.put(attrCode, attrValue);
                            datalineInput.putAll(temp);
                        }
                        IData subInput2 = new DataMap();
                        subInput2.put("RECORD_NUM", 0);
                        subInput2.put("IBSYSID", data.getString("IBSYSID"));
                        subInput2.put("NODE_ID", "archive");
                        IDataset subInfos2 = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishAttrByGroupSeqRecordNum", subInput2);
                        for (int k = 0; k < subInfos2.size(); k++) {
                            IData subInfo2 = subInfos2.getData(k);
                            IData temp2 = new DataMap();
                            String attrCode2 = subInfo2.getString("ATTR_CODE");
                            String attrValue2 = subInfo2.getString("ATTR_VALUE");
                            temp2.put(attrCode2, attrValue2);
                            datalineInput.putAll(temp2);
                        }
                    }
                }
                input.add(datalineInput);
            }
            this.setAjax(input);
            setInfos(input);
        } else if (data.getString("BPM_TEMPLET_ID").equals("ECHANGERESOURCECONFIRM") || data.getString("BPM_TEMPLET_ID").equals("EVIOPDIRECTLINECHANGEPBOSS")) {
            // 变更关联勘察单时查询资费(这个BPM_TEMPLET_ID是为了区分是开通关联勘察单，还是变更关联勘察单，并不是代表做的业务)
            
        	// 判断是否过户
              IData rela = new DataMap();
              List<String> productNos = new ArrayList<String>();
              String userId = data.getString("EC_USER_ID");
              rela.put("USER_ID_A", userId);
              IDataset relaInfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserida2", rela);
              if (IDataUtil.isNotEmpty(relaInfos)) {
                  for (int j = 0; j < relaInfos.size(); j++) {
                      IData relaInfo = relaInfos.getData(j);
                      String userIdB = relaInfo.getString("USER_ID_B");
                      IData dataLine = new DataMap();
                      dataLine.put("USER_ID", userIdB);
                      IData dataLines = CSViewCall.callone(this, "SS.DatalineOrderSVC.queryDataline", dataLine);
                      if (IDataUtil.isNotEmpty(dataLines)) {
                           productNos.add(dataLines.getString("PRODUCT_NO"));
                      }
                  }
              }
        	
        	IDataset input = new DatasetList();
            String ibsysid = data.getString("IBSYSID");
            IData attrHInput = new DataMap();
            attrHInput.put("IBSYSID", ibsysid);
            attrHInput.put("NODE_ID", "apply");
            IDataset attrHInfos = CSViewCall.call(this, "SS.WorkformAttrSVC.qryLineNoByIbsysid", attrHInput);
            if (IDataUtil.isNotEmpty(attrHInfos)) {
                for (int k = 0; k < attrHInfos.size(); k++) {
                    IData datalineInput = new DataMap();
                    IData attrHInfo2 = attrHInfos.getData(k);
                    if ((attrHInfo2.getString("ATTR_CODE").equals("PRODUCTNO"))) {
                        IData eomsInput = new DataMap();
                        String lineNo = attrHInfo2.getString("ATTR_VALUE");


		                        if(!productNos.isEmpty()&&productNos.size()>0){
		                        	boolean flag = false;
		                        	for(int j = 0;j<productNos.size();j++){
		                        		String productNo = productNos.get(j).toString();
		                        		if(productNo.equals(lineNo)){
		                        			flag = true;
		                        			break;
		                        		}
		                        	}
		                        	if (!flag) {
		                                continue;
		                            } 
		                        }
 
                        IData poolInput = new DataMap();
                        poolInput.put("POOL_VALUE", lineNo);
                        poolInput.put("REL_IBSYSID", ibsysid);
                        // 在勘察池表查询此条专线实例号状态是否不为F
                        IDataset subPoolInfos = CSViewCall.call(this, "SS.ConfCrmQrySVC.qryStateByRelIbsysidPoolValue", poolInput);
                        if (IDataUtil.isNotEmpty(attrHInfos)) {
                            String state = subPoolInfos.getData(0).getString("STATE");
                            if (state.equals("F")) {
                                eomsInput.put("PRODUCT_NO", lineNo);
                                eomsInput.put("IBSYSID", data.getString("IBSYSID"));
                                // 在TF_BH_EOP_EOMS_STATE表查询此条专线实例号record_num
                                IDataset eomsStateInfos = CSViewCall.call(this, "SS.WorkformEomsStateSVC.queryHisEomsStateByIbsysidAndProductNo", eomsInput);
                                if (IDataUtil.isNotEmpty(eomsStateInfos)) {
                                    IData eomsStateInfo = eomsStateInfos.getData(0);
                                    // 取SERIALNO
                                    String serialNo = eomsStateInfo.getString("SERIALNO");
                                    datalineInput.put("SERIALNO", serialNo);

                                    String recordNum = eomsStateInfo.getString("RECORD_NUM");
                                    // 在TF_BH_EOP_EOMS表查询group_seq、sub_ibsysid、record_num
                                    IData eomsHInput = new DataMap();
                                    eomsHInput.put("RECORD_NUM", recordNum);
                                    eomsHInput.put("IBSYSID", data.getString("IBSYSID"));
                                    IDataset eomsInfos = CSViewCall.call(this, "SS.WorkformEomsSVC.qryHisEomsByIbsysIdOperTypeGroupSeq", eomsHInput);
                                    if (IDataUtil.isNotEmpty(eomsInfos)) {
                                        IData eomsInfo = eomsInfos.getData(0);
                                        String groupSeq = eomsInfo.getString("GROUP_SEQ");
                                        String subIbsysid = eomsInfo.getString("SUB_IBSYSID");
                                        IData subInput = new DataMap();
                                        subInput.put("GROUP_SEQ", groupSeq);
                                        subInput.put("SUB_IBSYSID", subIbsysid);
                                        subInput.put("RECORD_NUM", recordNum);
                                        IDataset subInfos = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishAttrByGroupSeqRecordNum", subInput);
                                        for (int j = 0; j < subInfos.size(); j++) {
                                            IData subInfo = subInfos.getData(j);
                                            IData temp = new DataMap();
                                            String attrCode = subInfo.getString("ATTR_CODE");
                                            String attrValue = subInfo.getString("ATTR_VALUE");
                                            temp.put(attrCode, attrValue);
                                            datalineInput.putAll(temp);
                                        }
                                        IData subInput2 = new DataMap();
                                        subInput2.put("GROUP_SEQ", groupSeq);
                                        subInput2.put("SUB_IBSYSID", subIbsysid);
                                        subInput2.put("RECORD_NUM", "0");
                                        IDataset subInfos2 = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishAttrByGroupSeqRecordNum", subInput2);
                                        for (int j = 0; j < subInfos2.size(); j++) {
                                            IData subInfo2 = subInfos2.getData(j);
                                            IData temp = new DataMap();
                                            String attrCode2 = subInfo2.getString("ATTR_CODE");
                                            String attrValue2 = subInfo2.getString("ATTR_VALUE");
                                            temp.put(attrCode2, attrValue2);
                                            datalineInput.putAll(temp);
                                        }
                                    } else {
                                        IData subInput = new DataMap();
                                        subInput.put("RECORD_NUM", recordNum);
                                        subInput.put("IBSYSID", data.getString("IBSYSID"));
                                        subInput.put("NODE_ID", "archive");
                                        IDataset subInfos = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", subInput);
                                        for (int j = 0; j < subInfos.size(); j++) {
                                            IData subInfo = subInfos.getData(j);
                                            IData temp = new DataMap();
                                            String attrCode = subInfo.getString("ATTR_CODE");
                                            String attrValue = subInfo.getString("ATTR_VALUE");
                                            temp.put(attrCode, attrValue);
                                            datalineInput.putAll(temp);
                                        }
                                        IData subInput2 = new DataMap();
                                        subInput2.put("RECORD_NUM", "0");
                                        subInput2.put("IBSYSID", data.getString("IBSYSID"));
                                        subInput2.put("NODE_ID", "archive");
                                        IDataset subInfos2 = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", subInput2);
                                        for (int j = 0; j < subInfos2.size(); j++) {
                                            IData subInfo2 = subInfos2.getData(j);
                                            IData temp = new DataMap();
                                            String attrCode2 = subInfo2.getString("ATTR_CODE");
                                            String attrValue2 = subInfo2.getString("ATTR_VALUE");
                                            temp.put(attrCode2, attrValue2);
                                            datalineInput.putAll(temp);
                                        }
                                    }
                                }
                                IData param = new DataMap();
                                param.put("PRODUCT_NO", lineNo);
                                IDataset dataLines = CSViewCall.call(this, "SS.TradeDataLineAttrInfoQrySVC.qryAllUserDatalineByProductNO", param);
                                IData dataline = dataLines.getData(0);
                                IData input2 = new DataMap();
                                // 互联网、数据专线变更获取资费
                                if (data.getString("BPM_TEMPLET_ID").equals("ECHANGERESOURCECONFIRM")) {
                                    // put成员用户ID
                                    String userIdB = dataline.getString("USER_ID");
                                    IData temp = new DataMap();
                                    temp.put("USER_ID", userIdB);
                                    temp.put("REMOVE_TAG", "0");
                                    datalineInput.putAll(temp);
                                    // 查询serialNumber
                                    IDataset userInfos = CSViewCall.call(this, "CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", temp);
                                    if (IDataUtil.isNotEmpty(userInfos)) {
                                        // put SERIAL_NUMBER
                                        IData userInfo = userInfos.getData(0);
                                        String serialNumber = userInfo.getString("SERIAL_NUMBER");
                                        IData temp2 = new DataMap();
                                        temp2.put("SERIAL_NUMBER", serialNumber);
                                        datalineInput.putAll(temp2);

                                        input2.put("USER_ID", userIdB);
                                        input2.put("INST_TYPE", "D");
                                        input2.put("PRODUCT_ID", data.getString("PRODUCT_ID"));

                                        IData discounts = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getDiscountByUserId", input2);
                                        IDataset disCountParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "DISCOUNTPARAM_CRM_ESOP");
                                        Iterator<String> itr2 = discounts.keySet().iterator();
                                        while (itr2.hasNext()) {
                                            String attrCode = itr2.next();
                                            for (int n = 0; n < disCountParam.size(); n++) {
                                                IData busi = new DataMap();
                                                String paramValue = disCountParam.getData(n).getString("PARAMVALUE");
                                                if (attrCode.equals(paramValue)) {
                                                    String attrValue = null;
                                                    if (attrCode.equals("59701010") || attrCode.equals("59701011") || attrCode.equals("59701012")) {
                                                        attrValue = discounts.getString(attrCode).substring(0, discounts.getString(attrCode).length() - 1);
                                                    } else {
                                                        attrValue = discounts.getString(attrCode);
                                                    }
                                                    busi.put(disCountParam.getData(n).getString("PARAMNAME"), attrValue);
                                                    datalineInput.putAll(busi);
                                                    break;
                                                }
                                            }
                                        }
                                        // 从开通单带入勘察单没有的数据
                                        IDataset lineParamParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
                                        Iterator<String> itr3 = dataline.keySet().iterator();
                                        while (itr3.hasNext()) {
                                            String attrCode = itr3.next();
                                            for (int m = 0; m < lineParamParam.size(); m++) {
                                                IData busi = new DataMap();
                                                String paramValue = lineParamParam.getData(m).getString("PARAMVALUE");
                                                if (attrCode.equals(paramValue)) {
                                                    String attrValue = null;
                                                    if (attrCode.equals("RSRV_STR1") || attrCode.equals("RSRV_NUM1") || attrCode.equals("RSRV_NUM2") || attrCode.equals("CUST_APPSERV_IPADDNUM") || attrCode.equals("DO_MAINNAME")
                                                            || attrCode.equals("MAIN_DO_MAINADD") || attrCode.equals("PORT_INTERFACE_TYPE_A") || attrCode.equals("PORT_INTERFACE_TYPE_Z") || attrCode.equals("IS_CUSTOMER_PROVIDE_EQUIPMENT")
                                                            || attrCode.equals("PHONE_LIST") || attrCode.equals("SUPPORT_MODE") || attrCode.equals("CUSTOMER_DEVICE_MODE") || attrCode.equals("CUSTOMER_DEVICE_TYPE")
                                                            || attrCode.equals("CUSTOMER_DEVICE_VENDOR") || attrCode.equals("DO_MAINNAME") || attrCode.equals("MAIN_DO_MAINADD")) {
                                                        if (attrCode.equals("PORT_INTERFACE_TYPE_A") || attrCode.equals("PORT_INTERFACE_TYPE_Z")) {
                                                            Iterator<String> itr4 = datalineInput.keySet().iterator();
                                                            while (itr4.hasNext()) {
                                                                String attrCode4 = itr4.next();
                                                                if (attrCode4.equals("PORTAINTERFACETYPE") || attrCode4.equals("PORTZINTERFACETYPE")) {
                                                                    String attrValue4 = datalineInput.getString(attrCode4);
                                                                    if (attrValue4 == null) {
                                                                        attrValue = dataline.getString(attrCode);
                                                                        busi.put(lineParamParam.getData(m).getString("PARAMNAME"), attrValue);
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            attrValue = dataline.getString(attrCode);
                                                            busi.put(lineParamParam.getData(m).getString("PARAMNAME"), attrValue);
                                                        }
                                                    }
                                                    datalineInput.putAll(busi);
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                }
                                // VOIP专线变更获取资费
                                if (data.getString("BPM_TEMPLET_ID").equals("EVIOPDIRECTLINECHANGEPBOSS")) {
                                    IData temp = new DataMap();
                                    temp.put("USER_ID", dataline.getString("USER_ID"));
                                    temp.put("REMOVE_TAG", "0");
                                    datalineInput.putAll(temp);

                                    // 查询serialNumber
                                    IDataset userInfos = CSViewCall.call(this, "CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", temp);
                                    if (IDataUtil.isNotEmpty(userInfos)) {
                                        // put SERIAL_NUMBER
                                        IData userInfo = userInfos.getData(0);
                                        String serialNumber = userInfo.getString("SERIAL_NUMBER");
                                        IData temp2 = new DataMap();
                                        temp2.put("SERIAL_NUMBER", serialNumber);
                                        datalineInput.putAll(temp2);

                                        IData voipParam = new DataMap();
                                        voipParam.put("USER_ID", dataline.getString("USER_ID"));
                                        voipParam.put("PRODUCTNO", lineNo);
                                        IData voipDataLine = CSViewCall.callone(this, "SS.QcsGrpIntfSVC.queryChangeLineInfosForEsop", voipParam);
                                        IDataset disCountParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "VOIP_DISCOUNTPARAM_CRM_ESOP");
                                        if (IDataUtil.isEmpty(voipDataLine)) {
                                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据用户【" + dataline.getString("USER_ID") + "】和专线【" + lineNo + " 】查询资费为空！");
                                        }
                                        Iterator<String> itr2 = voipDataLine.keySet().iterator();
                                        while (itr2.hasNext()) {
                                            String attrCode = itr2.next();
                                            for (int m = 0; m < disCountParam.size(); m++) {
                                                IData busi = new DataMap();
                                                String paramValue = disCountParam.getData(m).getString("PARAMVALUE");
                                                if (attrCode.equals(paramValue)) {
                                                    busi.put(disCountParam.getData(m).getString("PARAMNAME"), voipDataLine.getString(attrCode));
                                                    datalineInput.putAll(busi);
                                                    break;
                                                }
                                            }
                                        }
                                        // 从开通单带入勘察单没有的数据
                                        IDataset lineParamParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
                                        Iterator<String> itr3 = dataline.keySet().iterator();
                                        while (itr3.hasNext()) {
                                            String attrCode = itr3.next();
                                            for (int m = 0; m < lineParamParam.size(); m++) {
                                                IData busi = new DataMap();
                                                String paramValue = lineParamParam.getData(m).getString("PARAMVALUE");
                                                if (attrCode.equals(paramValue)) {
                                                    String attrValue = null;
                                                    if (attrCode.equals("IS_CUSTOMER_PROVIDE_EQUIPMENT") || attrCode.equals("CUSTOMER_DEVICE_MODE") || attrCode.equals("CUSTOMER_DEVICE_TYPE") || attrCode.equals("CUSTOMER_DEVICE_VENDOR")
                                                            || attrCode.equals("PHONE_LIST") || attrCode.equals("PHONE_PERMISSION") || attrCode.equals("CON_PRODUCT_NO")) {
                                                        attrValue = dataline.getString(attrCode);
                                                        busi.put(lineParamParam.getData(m).getString("PARAMNAME"), attrValue);
                                                    }
                                                    datalineInput.putAll(busi);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                input.add(datalineInput);
                            }
                        }
                    }
                }
            }
            this.setAjax(input);
            setInfos(input);
        }

        initConfCrm(cycle);
    }

    public void getMarketingById(IRequestCycle cycle) throws Exception {
        queryInfo(cycle);
        IData pageData = getData();
        String ibsysid = pageData.getString("MARKETINGIBSYSID");
        IData idatain = new DataMap();
        idatain.put("IBSYSID", ibsysid);
        IData commonIdata = new DataMap();
        IDataset pattrList = new DatasetList();
        IDataset tapMarketingListNew = new DatasetList();
        IDataset renewModeList = StaticUtil.getStaticList("MARKETING_RENEWMODE");
        IDataset friendBusinessList = StaticUtil.getStaticList("MARKETING_FRIENDBUSINESS_NAME");
        IDataset output = CSViewCall.call(this, "SS.TapMarketingSVC.selTapMarketingByConditionAll", idatain);
        if (IDataUtil.isNotEmpty(output)) {
            for (int i = 0; i < output.size(); i++) {
                IData tapMarketingData = output.getData(i);
                getEopAttrToList(tapMarketingData.getString("IBSYSID_TAPMARKETING", ""), pattrList, commonIdata, "His");
                if (!tapMarketingData.getString("PROVINCEA", "").equals("")) {
                    commonIdata.put("C_PROVINCEA", tapMarketingData.getString("PROVINCEA"));
                }
                if (!tapMarketingData.getString("CITYA", "").equals("")) {
                    commonIdata.put("C_CITYA", tapMarketingData.getString("CITYA"));
                }
                if (!tapMarketingData.getString("RESPONSIBILITY_NAME", "").equals("")) {
                    commonIdata.put("C_RESPONSIBILITY_NAME", tapMarketingData.getString("RESPONSIBILITY_NAME"));
                }

                if (!tapMarketingData.getString("RESPONSIBILITY_ID", "").equals("")) {
                    commonIdata.put("C_RESPONSIBILITY_ID", tapMarketingData.getString("RESPONSIBILITY_ID"));
                }
                if (!tapMarketingData.getString("CITY_ID", "").equals("")) {
                    commonIdata.put("C_RESPONSIBILITY_CITYCODE", tapMarketingData.getString("CITY_ID"));
                }

                if (!tapMarketingData.getString("RESPONSIBILITY_PHONE", "").equals("")) {
                    commonIdata.put("C_RESPONSIBILITY_PHONE", tapMarketingData.getString("RESPONSIBILITY_PHONE"));
                }
                IData tapMarketingDataNew = new DataMap();
                tapMarketingDataNew.put("NOTIN_OPPONENTMARKETINGCONTENT", "");// 此时赋空值避免生成页面参数时无ID生成
                tapMarketingDataNew.put("NOTIN_OPPONENTENDDATE", "");
                tapMarketingDataNew.put("NOTIN_RENEWMODE_STRING", "");
                tapMarketingDataNew.put("NOTIN_CUSTRENEWCONTENT", "");

                for (int j = 0; j < pattrList.size(); j++) {
                    IData pattrData = pattrList.getData(j);
                    if (tapMarketingData.getString("LINENAME").equals(pattrData.getString("NOTIN_LINENAME", ""))) {
                        tapMarketingDataNew = pattrData;
                        break;
                    }

                }
                if (!tapMarketingData.getString("LINENAME", "").equals("")) {
                    tapMarketingDataNew.put("NOTIN_LINENAME", tapMarketingData.getString("LINENAME"));
                }
                if (!tapMarketingData.getString("BANDWIDTH", "").equals("")) {
                    tapMarketingDataNew.put("NOTIN_BANDWIDTH", tapMarketingData.getString("BANDWIDTH"));
                }
                if (!tapMarketingData.getString("MONTHLYFEE_TAP", "").equals("")) {
                    tapMarketingDataNew.put("NOTIN_RSRV_STR2", tapMarketingData.getString("MONTHLYFEE_TAP"));
                }
                if (!tapMarketingData.getString("FRIENDBUSINESS_NAME", "").equals("")) {
                    tapMarketingDataNew.put("NOTIN_FRIENDBUSINESS_NAME", tapMarketingData.getString("FRIENDBUSINESS_NAME"));
                }
                if (!tapMarketingData.getString("CUST_NAME", "").equals("")) {
                    tapMarketingDataNew.put("NOTIN_CUST_NAME", tapMarketingData.getString("CUST_NAME"));
                }
                if (!tapMarketingData.getString("LINEPRICE_TAP", "").equals("")) {
                    tapMarketingDataNew.put("NOTIN_LINEPRICE_TAP", tapMarketingData.getString("LINEPRICE_TAP"));
                }

                if (IDataUtil.isNotEmpty(renewModeList)) {
                    String renewMode = tapMarketingDataNew.getString("NOTIN_RENEWMODE", "");
                    for (int j = 0, sizej = renewModeList.size(); j < sizej; j++) {
                        IData renewModeData = renewModeList.getData(j);
                        if (renewModeData.getString("DATA_ID", "").equals(renewMode)) {
                            tapMarketingDataNew.put("NOTIN_RENEWMODE_STRING", renewModeData.getString("DATA_NAME", ""));
                            break;
                        }
                    }
                }
                if (IDataUtil.isNotEmpty(friendBusinessList)) {
                    String friendBusiness = tapMarketingDataNew.getString("NOTIN_FRIENDBUSINESS_NAME", "").trim();
                    for (int z = 0, sizez = friendBusinessList.size(); z < sizez; z++) {
                        IData friendBusinessData = friendBusinessList.getData(z);
                        if (friendBusinessData.getString("DATA_ID", "").equals(friendBusiness)) {
                            tapMarketingDataNew.put("NOTIN_FRIENDBUSINESS_NAME_STRING", friendBusinessData.getString("DATA_NAME", ""));
                            break;
                        }
                    }
                }
                tapMarketingListNew.add(tapMarketingDataNew);
                if (!"".equals(commonIdata.getString("C_MARKETING_FILE_NAME", "")) && !"".equals(commonIdata.getString("C_MARKETING_FILE_ID", ""))) {
                    IDataset marketingList = new DatasetList();
                    IData attachInfo = new DataMap();
                    IData marketingData = new DataMap();
                    marketingData.put("FILE_ID", commonIdata.getString("C_MARKETING_FILE_ID", ""));
                    marketingData.put("FILE_NAME", commonIdata.getString("C_MARKETING_FILE_NAME", ""));
                    marketingData.put("ATTACH_TYPE", "M");
                    marketingList.add(marketingData);
                    attachInfo.put("MARKETING_FILEID", commonIdata.getString("C_MARKETING_FILE_ID", ""));
                    attachInfo.put("MARKETING_FILENAME", commonIdata.getString("C_MARKETING_FILE_NAME", ""));
                    attachInfo.put("MARKETING_LIST", marketingList);
                    setAttachInfo(attachInfo);
                }

            }
        }

        commonIdata.put("pattrList", tapMarketingListNew);

        String ibsysIdNow = getData().getString("IBSYSID");

        IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysIdNow);
        if (IDataUtil.isEmpty(subscribeData)) {
            commonIdata.remove("C_TAPMARKETING_SELECT");
        }

        setPattrInfo(commonIdata);
        setPattrList(tapMarketingListNew);
        this.setAjax(commonIdata);

    }

    /***
     * 通过选择的地市联动对应的区县
     * 
     * @param cycle
     * @throws Exception
     */
    // public void changeAreaByCity(IRequestCycle cycle) throws Exception
    // {
    // IData pagedata= getData();
    // setCondition(pagedata);
    // IData condition = new DataMap();
    // String city = pagedata.getString("CITY");
    // IDataset areaList = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[]{ "TYPE_ID", "DATA_NAME" }, new String[]{ "CHANGE_AREA_BY_CITY", city});
    // IDataset area = new DatasetList();
    // for (int i = 0; i < areaList.size(); i++) {
    // IData temp = areaList.getData(i);
    // temp.put("DATA_ID", temp.getString("DATA_ID", ""));
    // temp.put("DATA_NAME", temp.getString("DATA_ID", ""));
    // area.add(temp);
    // }
    // condition.put("AREAS", area);
    // setInfo(condition);
    // this.setAjax(condition);
    // }

    public void getMarketingProductNo(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        String productNo = pageData.getString("PRODUCTNO");
        String groupId = pageData.getString("GROUPID");
        String offerCode = pageData.getString("OFFER_CODE");
        IData idatain1 = new DataMap();
        idatain1.put("PRODUCT_NO", productNo);
        IDataset output1 = CSViewCall.call(this, "SS.TapMarketingSVC.selTapMarketingByConditionAll", idatain1);
        if (IDataUtil.isNotEmpty(output1) && output1.size() > 0) {
            CSViewException.apperr(GrpException.CRM_GRP_713, "该专线实例号：" + productNo + " 已进行过激励操作，具体业务订单号为：" + output1.getData(0).getString("IBSYSID_EXCITATION", ""));
        }

        IData idatain = new DataMap();
        idatain.put("PRODUCTNO", productNo);
        idatain.put("GROUPID", groupId);
        idatain.put("OFFER_CODE", offerCode);
        IDataset output = CSViewCall.call(this, "SS.BookTradeSVC.getDatalineforTapMarkting", idatain);
        this.setAjax(output);
    }

    public void queryProductLines(IRequestCycle cycle) throws Exception {
        IData pageData = this.getData();
        IData data = new DataMap();

        String productId = pageData.getString("PRODUCT_ID");
        String productNO = pageData.getString("PRODUCTNO");
        String groupID = pageData.getString("GROUP_ID");

        data.put("PRODUCT_ID", productId);
        data.put("PRODUCTNO", productNO);
        data.put("GROUP_ID", groupID);

        IDataset lineInfo = CSViewCall.call(this, "SS.ContractLinesSVC.getProductLineInfo", data);
        IDataset lineset = new DatasetList();
        if (IDataUtil.isNotEmpty(lineInfo)) {
            for (int i = 0; i < lineInfo.size(); i++) {
                IData line = lineInfo.getData(i);
                line.put("NOTIN_LINE_NO", line.getString("PRODUCTNO"));// 专线实例号
                line.put("LINE_NAME", line.getString("LINE_NAME", ""));// 专线名称
                line.put("NOTIN_RSRV_STR1", line.getString("BANDWIDTH", "").replaceAll("M", "").replaceAll("m", ""));// 专线带宽（兆）
                line.put("NOTIN_RSRV_STR2", line.getString("NOTIN_RSRV_STR2", "0"));// 月租费(元/月)
                line.put("NOTIN_RSRV_STR3", "0");// 一次性费用(安装调试费)(元)
                line.put("NOTIN_RSRV_STR4", "0");// 一次性通信服务费（元）
                line.put("NOTIN_RSRV_STR6", "0.2");// 集团所在市县分成比例
                line.put("NOTIN_RSRV_STR7", "0.4");// A端所在市县分成比例
                line.put("NOTIN_RSRV_STR8", "0.4");// Z端所在市县分成比例
                line.put("NOTIN_RSRV_STR9", line.getString("NOTIN_RSRV_STR9"));// 业务标识
                line.put("NOTIN_RSRV_STR10", "0");// IP地址使用费（元）
                line.put("NOTIN_RSRV_STR11", "0");// 软件应用服务费）
                line.put("NOTIN_RSRV_STR12", "0");// 网络技术支持服务费
                line.put("NOTIN_RSRV_STR15", "0");// 语音通信费（元/分钟）
                line.put("NOTIN_RSRV_STR16", "0");// SLA服务费(元/月)

                lineset.add(line);
            }
        }
        setInfos(lineset);
        setCondition(data);
        this.setAjax(lineset);
    }

    public void initContractLines(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        IData condition = new DataMap();
        condition.put("PRODUCT_ID", pageData.getString("PRODUCT_ID"));
        setInfo(condition);
        this.setAjax(condition);
    }

    public void initMarketing(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        String ibsysidMarketing = pageData.getString("IBSYSID_MARKETING");
        IData param = new DataMap();
        param.put("IBSYSID", ibsysidMarketing);
        param.put("RECORD_NUM", "0");
        param.put("NODE_ID", "apply");
        // 获取公共数据
        IData comAttr = CSViewCall.callone(this, "SS.WorkformMarketingSVC.getMarketingComInfo", param);
        comAttr.put("PRE", "pattr");
        setBusi(comAttr);

        // 获取营销活动属性信息
        param.clear();
        param.put("SUB_IBSYSID", comAttr.getString("SUB_IBSYSID"));
        IDataset eopAttrList = CSViewCall.call(this, "SS.WorkformMarketingSVC.getMarketingAttrInfo", param);

        IData dataLine = new DataMap();
        dataLine.put("DATALINEINFO", eopAttrList);
        dataLine.put("COMINFO", comAttr);
        this.setAjax(dataLine);
    }

    public void initDirectLineList(IRequestCycle cycle) throws Exception {
        IData data = getData();
        if (!"".equals(data.getString("EC_USER_ID", ""))) {
            String productId = data.getString("OFFER_CODE", "");
            String bizRange = data.getString("BIZRANGE", "");

            String condTempletId = data.getString("TEMPLET_ID", "");
            IDataset dataLines = new DatasetList();

            IDataset esopParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
            if ("7010".equals(productId)) {
                IData param = new DataMap();
                param.put("USER_ID", data.getString("EC_USER_ID"));
                // param.put("PRODUCT_ID", productId);VIOP不传产品id直接查
                IDataset voipDataLines = CSViewCall.call(this, "SS.QcsGrpIntfSVC.getProductInfoForPboss", param);
                if (IDataUtil.isEmpty(voipDataLines) && !"9983".equals(productId)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "集团用户【" + data.getString("EC_USER_ID") + "】不存在有效的专线，不能办理该业务！");
                }
                for (int j = 0; j < voipDataLines.size(); j++) {
                    IData temp = new DataMap();
                    IData voipDataLine = voipDataLines.getData(j);
                    // 判断专线为本地市/跨地市,语音专线只有本地市
                    /* boolean isContinue = checkVoipBizRange(voipDataLine, bizRange); if (isContinue) { continue; } */
                    // 停开机判断
                    if ("MANUALSTOP".equals(condTempletId) && "1".equals(voipDataLine.getString("RSRV_STR3"))) {// 已停机专线不允许再次停机
                        continue;
                    } else if ("MANUALBACK".equals(condTempletId) && !"1".equals(voipDataLine.getString("RSRV_STR3"))) {// 不是停机状态专线不允许复机
                        continue;
                    }
                    if ("ECHANGERESOURCECONFIRM".equals(condTempletId)) {
                        IData voipParam = new DataMap();
                        voipParam.put("USER_ID", data.getString("EC_USER_ID"));
                        voipParam.put("PRODUCTNO", voipDataLine.getString("PRODUCT_NO"));
                        IData input = new DataMap();
                        String productNO = voipDataLine.getString("PRODUCT_NO");
                        IData inData = new DataMap();
                        inData.put("PRODUCTNO", productNO);
                        IDataset subscribeinfos = CSViewCall.call(this, "SS.WorkformSubscribeSVC.getSubScribeInfoByProductNo", inData);
                        if (IDataUtil.isNotEmpty(subscribeinfos)) {
                            String subIbsysid = subscribeinfos.first().getString("SUB_IBSYSID", "");
                            String recordNum = subscribeinfos.first().getString("RECORD_NUM", "");
                            inData.clear();
                            inData.put("SUB_IBSYSID", subIbsysid);
                            inData.put("RECORD_NUM", recordNum);
                            IDataset attrList = CSViewCall.call(this, "SS.WorkformAttrSVC.getInfoBySubIbsysidRecordNum", inData);
                            if (IDataUtil.isEmpty(attrList)) {
                                return;
                            }
                            for (int k = 0; k < attrList.size(); k++) {
                                temp.put(attrList.getData(k).getString("ATTR_CODE"), attrList.getData(k).getString("ATTR_VALUE"));
                            }
                        } else {
                            Iterator<String> itr = voipDataLine.keySet().iterator();
                            while (itr.hasNext()) {
                                String attrCode = itr.next();
                                for (int k = 0; k < esopParam.size(); k++) {
                                    String paramValue = esopParam.getData(k).getString("PARAMVALUE");
                                    if (attrCode.equals(paramValue)) {
                                        temp.put(esopParam.getData(k).getString("PARAMNAME"), voipDataLine.getString(attrCode));
                                        break;
                                    }
                                }
                            }
                            IData discounts = new DataMap();
                            input.put("INST_TYPE", "D");
                            input.put("PRODUCT_ID", productId);
                            discounts = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getUserLineInfoByUserId", input);
                            input.clear();
                            input.put("CONFIG_NAME", "DISCOUNTPARAM_CRM_ESOP");
                            input.put("STATUS", "0");
                            IDataset disCountParam = CSViewCall.call(this, "SS.EweConfigQrySVC.qryEweConfigByConfigName", input);
                            Iterator<String> itr2 = discounts.keySet().iterator();
                            while (itr2.hasNext()) {
                                String attrCode = itr2.next();
                                String attrValue = discounts.getString(attrCode);
                                for (int m = 0; m < disCountParam.size(); m++) {
                                    IData disParam = disCountParam.getData(m);
                                    if (attrCode.equals(disParam.getString("PARAMVALUE"))) {
                                        String key = disParam.getString("PARAMNAME");
                                        temp.put(disParam.getString("PARAMNAME"), attrValue);
                                    }
                                }
                            }
                        }
                    } else {
                        Iterator<String> itr = voipDataLine.keySet().iterator();
                        while (itr.hasNext()) {
                            String attrCode = itr.next();
                            for (int k = 0; k < esopParam.size(); k++) {
                                String paramValue = esopParam.getData(k).getString("PARAMVALUE");
                                if (attrCode.equals(paramValue)) {
                                    temp.put(esopParam.getData(k).getString("PARAMNAME"), voipDataLine.getString(attrCode));
                                    break;
                                }
                            }
                        }
                        IData voipParam = new DataMap();
                        voipParam.put("USER_ID", data.getString("EC_USER_ID"));
                        voipParam.put("PRODUCTNO", voipDataLine.getString("PRODUCT_NO"));
                        IData voipOtherInfo = CSViewCall.callone(this, "SS.QcsGrpIntfSVC.queryChangeLineInfosForEsop", voipParam);
                        if (IDataUtil.isNotEmpty(voipOtherInfo)) {
                            IDataset disCountParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "VOIP_DISCOUNTPARAM_CRM_ESOP");
                            Iterator<String> itr2 = voipOtherInfo.keySet().iterator();
                            while (itr2.hasNext()) {
                                String attrCode = itr2.next();
                                for (int m = 0; m < disCountParam.size(); m++) {
                                    String paramValue = disCountParam.getData(m).getString("PARAMVALUE");
                                    if (attrCode.equals(paramValue)) {
                                        temp.put(disCountParam.getData(m).getString("PARAMNAME"), voipOtherInfo.getString(attrCode));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // 倒换数据中没有"NOTIN_RSRV_STR9(业务标识)"字段，此处手动加上（出问题找辉哥）
                    if (StringUtils.isBlank(temp.getString("NOTIN_RSRV_STR9"))) {
                        temp.put("NOTIN_RSRV_STR9", temp.getString("NOTIN_LINE_NO"));
                    }

                    dataLines.add(temp);

                }
            } else {
                String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);

                IData param = new DataMap();
                param.clear();
                param.put("USER_ID_A", data.getString("EC_USER_ID"));
                param.put("RELATION_TYPE_CODE", relationTypeCode);
                param.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
                param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());

                IDataset directLineList = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getAllRelaUUInfoByUserIda", param);
                if (IDataUtil.isEmpty(directLineList) && !"9983".equals(productId)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "集团用户【" + data.getString("EC_USER_ID") + "】不存在有效的专线成员，不能办理该业务！");
                }
                for (int i = 0; i < directLineList.size(); i++) {
                    IData temp = new DataMap();
                    IData directLine = directLineList.getData(i);

                    IData commonParam = new DataMap();
                    commonParam.put("USER_ID", directLine.getString("USER_ID_B"));
                    commonParam.put("ATTR_CODE", "BIZRANGE");
                    commonParam.put("INST_TYPE", "P");
                    IDataset commonList = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserProductAttrValue", commonParam);

                    // 如果是复机则调用不做时间判断的服务
                    if ("MANUALBACK".equals(condTempletId)) {
                        commonList = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserProductAttrValueForManualBack", commonParam);
                    }
                    // 判断专线为本地市/跨地市(只有数据专线有跨地市)
                    if (("7012".equals(productId)|| "70121".equals(productId)|| "70122".equals(productId)) && (IDataUtil.isEmpty(commonList) || !bizRange.equals(commonList.first().getString("ATTR_VALUE")))) {
                        continue;
                    }

                    temp.put("SERIAL_NUMBER", directLine.getString("SERIAL_NUMBER_B"));
                    temp.put("USER_ID", directLine.getString("USER_ID_B"));
                    IData input = new DataMap();
                    input.put("USER_ID", directLine.getString("USER_ID_B"));
                    IDataset userDataLines = CSViewCall.call(this, "SS.QcsGrpIntfSVC.getProductInfoForPboss", input);
                    if (IDataUtil.isNotEmpty(userDataLines)) {
                        IData userDataLine = userDataLines.getData(0);

                        // 停开机判断
                        if ("MANUALSTOP".equals(condTempletId) && "1".equals(userDataLine.getString("RSRV_STR3"))) {// 已停机专线不允许再次停机
                            continue;
                        } else if ("MANUALBACK".equals(condTempletId) && !"1".equals(userDataLine.getString("RSRV_STR3"))) {// 不是停机状态专线不允许复机
                            continue;
                        }

                        String productNO = userDataLine.getString("PRODUCT_NO");

                        if ("ECHANGERESOURCECONFIRM".equals(condTempletId)) {
                            IData inData = new DataMap();
                            inData.put("PRODUCTNO", productNO);
                            Iterator<String> itr = userDataLine.keySet().iterator();
                            while (itr.hasNext()) {
                                String attrCode = itr.next();
                                for (int j = 0; j < esopParam.size(); j++) {
                                    String paramValue = esopParam.getData(j).getString("PARAMVALUE");
                                    if (attrCode.equals(paramValue)) {
                                        temp.put(esopParam.getData(j).getString("PARAMNAME"), userDataLine.getString(attrCode));
                                        break;
                                    }
                                }
                            }
                            IData discounts = new DataMap();
                            input.put("INST_TYPE", "D");
                            input.put("PRODUCT_ID", productId);
                            input.put("USER_ID", userDataLine.getString("USER_ID"));
                            discounts = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getDiscountByUserId", input);
                            input.clear();
                            input.put("CONFIG_NAME", "DISCOUNTPARAM_CRM_ESOP");
                            input.put("STATUS", "0");
                            IDataset disCountParam = CSViewCall.call(this, "SS.EweConfigQrySVC.qryEweConfigByConfigName", input);
                            Iterator<String> itr2 = discounts.keySet().iterator();
                            while (itr2.hasNext()) {
                                String attrCode = itr2.next();
                                String attrValue = discounts.getString(attrCode);
                                for (int j = 0; j < disCountParam.size(); j++) {
                                    IData disParam = disCountParam.getData(j);
                                    if (attrCode.equals(disParam.getString("PARAMVALUE"))) {
                                        String key = disParam.getString("PARAMNAME");
                                        // 处理百分号无法提交问题（这里直接去掉，后面提交会加上）
                                        if ("NOTIN_RSRV_STR6".equals(key) || "NOTIN_RSRV_STR7".equals(key) || "NOTIN_RSRV_STR8".equals(key)) {
                                            if (attrValue != null && attrValue.endsWith("%")) {
                                                attrValue = attrValue.substring(0, attrValue.length() - 1);
                                            }
                                        }
                                        temp.put(disParam.getString("PARAMNAME"), attrValue);
                                    }
                                }
                            }
                        } else {
                            Iterator<String> itr = userDataLine.keySet().iterator();
                            while (itr.hasNext()) {
                                String attrCode = itr.next();
                                for (int j = 0; j < esopParam.size(); j++) {
                                    String paramValue = esopParam.getData(j).getString("PARAMVALUE");
                                    if (attrCode.equals(paramValue)) {
                                        temp.put(esopParam.getData(j).getString("PARAMNAME"), userDataLine.getString(attrCode));
                                        break;
                                    }
                                }
                            }
                            IData discounts = new DataMap();
                            input.put("INST_TYPE", "D");
                            input.put("PRODUCT_ID", productId);
                            input.put("USER_ID", userDataLine.getString("USER_ID"));
                            discounts = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getDiscountByUserId", input);
                            input.clear();
                            input.put("CONFIG_NAME", "DISCOUNTPARAM_CRM_ESOP");
                            input.put("STATUS", "0");
                            IDataset disCountParam = CSViewCall.call(this, "SS.EweConfigQrySVC.qryEweConfigByConfigName", input);
                            Iterator<String> itr2 = discounts.keySet().iterator();
                            while (itr2.hasNext()) {
                                String attrCode = itr2.next();
                                for (int j = 0; j < disCountParam.size(); j++) {
                                    IData disParam = disCountParam.getData(j);
                                    if (attrCode.equals(disParam.getString("PARAMVALUE"))) {
                                        String key = disParam.getString("PARAMNAME");
                                        String attrValue = discounts.getString(attrCode);
                                        // 处理百分号无法提交问题（这里直接去掉，后面提交会加上）
                                        if ("NOTIN_RSRV_STR6".equals(key) || "NOTIN_RSRV_STR7".equals(key) || "NOTIN_RSRV_STR8".equals(key)) {
                                            if (attrValue != null && attrValue.endsWith("%")) {
                                                attrValue = attrValue.substring(0, attrValue.length() - 1);
                                            }
                                        }
                                        temp.put(disParam.getString("PARAMNAME"), attrValue);
                                    }
                                }
                            }
                        }

                        // 倒换数据中没有"NOTIN_RSRV_STR9(业务标识)"字段，此处手动加上（出问题找辉哥）
                        if (StringUtils.isBlank(temp.getString("NOTIN_RSRV_STR9"))) {
                            temp.put("NOTIN_RSRV_STR9", temp.getString("NOTIN_LINE_NO"));
                        }

                        dataLines.add(temp);
                    }
                }
            }
            setAjax(dataLines);
            setInfos(dataLines);
        }
    }

    public void checkLineBandWidth(IRequestCycle cycle) throws Exception {
        String productNo = getData().getString("PRODUCTNO");
        String bandWidth = getData().getString("BANDWIDTH");

        IData param = new DataMap();
        param.put("PRODUCT_NO", productNo);
        IDataset dataLines = CSViewCall.call(this, "SS.TradeDataLineAttrInfoQrySVC.qryAllUserDatalineByProductNO", param);
        if (IDataUtil.isEmpty(dataLines)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据专线实例号【" + productNo + "】未查询到专线信息！");
        }
        IData dataline = dataLines.getData(0);
        String oldBandWidth = dataline.getString("BAND_WIDTH");
        if (StringUtils.isBlank(bandWidth) || StringUtils.isBlank(oldBandWidth)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到专线带宽！");
        }
        IData result = new DataMap();
        if (Integer.valueOf(bandWidth) <= Integer.valueOf(oldBandWidth)) {
            result.put("FLAG", "true");
            result.put("BAND_WIDTH", oldBandWidth);
        } else {
            result.put("FLAG", "false");
            result.put("BAND_WIDTH", oldBandWidth);
        }

        setAjax(result);
    }

    /* private boolean checkVoipBizRange(IData voipDataLine, String bizRange) throws Exception { String productNo = voipDataLine.getString("PRODUCT_NO"); if (StringUtils.isBlank(productNo)) { return true; } IData param = new DataMap();
     * param.put("PRODUCT_NO", productNo); IDataset datalineAttrs = CSViewCall.call(this, "SS.UserOtherInfoQrySVC.queryLineTradeAttr", param); for (int i = 0; i < datalineAttrs.size(); i++) { // BIZRANGE IData datalineAttr = datalineAttrs.getData(i);
     * if ("BIZRANGE".equals(datalineAttr.getString("ATTR_CODE"))) { if (bizRange.equals(datalineAttr.getString("ATTR_VALUE"))) { return false; } break; } } return true; } */

    public void ChangeSimpleImportInternet(IRequestCycle cycle) throws Exception {
        IDataset datalineInfo = (IDataset) SharedCache.get("SIMPLE_CHANGE_DATALINE_INFOS");
        this.setAjax(datalineInfo);
        // 清除缓存
        // SharedCache.delete("SIMPLE_CHANGE_DATALINE_INFOS");
    }

    public void queryAcceptPerByBusinessType(IRequestCycle cycle) throws Exception {
        String businesstype = getData().getString("BUSINESSTYPE");
        IData info = new DataMap();
        IDataset list = null;
        if ("0".equals(businesstype)) {
            list = StaticUtil.getStaticList("ACCEPTANCEDATE");
        } else if ("1".equals(businesstype)) {
            list = StaticUtil.getStaticList("CHANGEACCEPTANCEDATE");
        }

        setAjax(list);
    }

    public void getAddLineInfoMarketing(IRequestCycle cycle) throws Exception {
        String groupID = getData().getString("GROUP_ID");
        IDataset lineList = new DatasetList(getData().getString("LINES", ""));
        if (IDataUtil.isEmpty(lineList)) {
            return;
        }

        for (int i = 0; i < lineList.size(); i++) {
            IData lineInfo = lineList.getData(i);
            IData param = new DataMap();
            param.put("PRODUCT_NO", lineInfo.getString("NOTIN_LINE_NO"));
            param.put("PRODUCTNO", lineInfo.getString("NOTIN_LINE_NO"));
            param.put("GROUP_ID", groupID);
            // param.put("PRODUCT_ID", getData().getString("PRODUCT_ID"));
            IDataset eopSubscribreInfos = CSViewCall.call(this, "SS.WorkformSubscribeSVC.qrySubScribeInfoByProductNo", param);
            if (IDataUtil.isEmpty(eopSubscribreInfos)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线实例: " + lineInfo.getString("NOTIN_LINE_NO") + " 属于其他的集团，不能分配给现在的集团!");
            }

            IDataset marketingInfors = CSViewCall.call(this, "SS.WorkformMarketingSVC.selMarketingByProductNO", param);
            if (IDataUtil.isNotEmpty(marketingInfors)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线：" + lineInfo.getString("NOTIN_LINE_NO") + " 已经申请过相关营销活动，不能再次申请");
            }
        }
    }

    /**
     * 初始化获得该集团填过的项目名称
     * 
     * @param cycle
     * @throws Exception
     */
    public IDataset initProjectName(IData param) throws Exception {
        IDataset list = new DatasetList();
        IDataset projectInfos = CSViewCall.call(this, "SS.GroupProjectNameSVC.qryProjectName", param);
        for (int i = 0; i < projectInfos.size(); i++) {
            IData input = new DataMap();
            IData projectInfo = projectInfos.getData(i);
            String projectName = projectInfo.getString("PROJECT_NAME");
            input.put("PROJECTNAME_OLD", projectName);
            list.add(input);
        }
        return list;
    }

    /**
     * 勘察单载入历史数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryHisConfCrm(IRequestCycle cycle) throws Exception {
        IData param = getData();
        IDataset subscribeInfos = CSViewCall.call(this, "SS.WorkformSubscribeSVC.queryWorkform", param);
        setInfos(subscribeInfos);
    }

    /**
     * 校验勘察单批量导入内容
     * 
     * @param submitData
     * @throws Exception
     */
    private void checkBatchDataline(IDataset datalineDatas, String offerCode, String bizRange) throws Exception {
        for (int i = 0; i < datalineDatas.size(); i++) {
            IDataset datalineData = datalineDatas.getDataset(i);
            String cityA = "";
            String cityZ = "";
            String areaA = "";
            String areaZ = "";
            String countyA = "";
            String countyZ = "";
            String villageA = "";
            String villageZ = "";

            for (int j = 0; j < datalineData.size(); j++) {
                IData dataline = datalineData.getData(j);
                String attrCode = StringUtils.substringAfter(dataline.getString("ATTR_CODE"), "pattr_");
                if (attrCode.isEmpty()) {
                    attrCode = dataline.getString("ATTR_CODE", "");
                }
                String attrValue = dataline.getString("ATTR_VALUE");
                if (offerCode.equals("7012")||offerCode.equals("70121")||offerCode.equals("70122")) {
                    if (attrCode.equals("PROVINCEA")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_PROVINCE", attrValue });
                        if (IDataUtil.isEmpty(result)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【A端所属省份】不符合要求，请修改后再提交！");
                        }
                    } else if (attrCode.equals("CITYA")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_CITY_NEWCODE", attrValue });
                        if (IDataUtil.isEmpty(result)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【A端所属地市】不符合要求，请修改后再提交！");
                        }
                        cityA = attrValue;
                    } else if (attrCode.equals("AREAA")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_COUNTY_CODE", attrValue });
                        if (IDataUtil.isEmpty(result)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【A端所属区县】不符合要求，请修改后再提交！");
                        }
                        areaA = attrValue;
                    } else if (attrCode.equals("PORTAINTERFACETYPE")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "INTERFACE_TYPE", attrValue });
                        if (IDataUtil.isEmpty(result) && !attrValue.isEmpty()) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【A端口类型】不符合要求，请修改后再提交！");
                        }
                    } else if (attrCode.equals("PROVINCEZ")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_PROVINCE", attrValue });
                        if (IDataUtil.isEmpty(result)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【Z端所属省份】不符合要求，请修改后再提交！");
                        }
                    } else if (attrCode.equals("CITYZ")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_CITY_NEWCODE", attrValue });
                        if (IDataUtil.isEmpty(result)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【Z端所属地市】不符合要求，请修改后再提交！");
                        }
                        cityZ = attrValue;
                    } else if (attrCode.equals("AREAZ")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_COUNTY_CODE", attrValue });
                        if (IDataUtil.isEmpty(result)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【Z端所属区县】不符合要求，请修改后再提交！");
                        }
                        areaZ = attrValue;
                    } else if (attrCode.equals("PORTZINTERFACETYPE")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "INTERFACE_TYPE", attrValue });
                        if (IDataUtil.isEmpty(result) && !attrValue.isEmpty()) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【Z端口类型】不符合要求，请修改后再提交！");
                        }
                    } else if (attrCode.equals("COUNTYA")) {
                        countyA = attrValue;
                    } else if (attrCode.equals("COUNTYZ")) {
                        countyZ = attrValue;
                    } else if (attrCode.equals("VILLAGEA")) {
                        villageA = attrValue;
                    } else if (attrCode.equals("VILLAGEZ")) {
                        villageZ = attrValue;
                    }
                    if (bizRange.equals("本地市") && !cityA.equals(cityZ)) {
                        if (!cityA.isEmpty() && !cityZ.isEmpty()) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "业务范围为【" + bizRange + "】，专线【" + productNo + " 】【A、Z地市】不符合要求，请修改后再提交！");
                        }
                    }
                    if (bizRange.equals("跨省") || bizRange.equals("省内跨地市")) {
                        if (cityA.equals(cityZ) && !cityA.isEmpty() && !cityZ.isEmpty()) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "业务范围为【" + bizRange + "】，专线【" + productNo + " 】【A、Z地市】不符合要求，请修改后再提交！");
                        }
                    } else if (!cityA.isEmpty() && !areaA.isEmpty()) {
                        boolean flag = false;
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "CHANGE_AREA_BY_CITY", cityA });
                        for (int k = 0; k < result.size(); k++) {
                            String tempResult = result.getData(k).getString("DATA_ID");
                            if (tempResult.contains(areaA)) {
                                flag = true;
                            }
                        }
                        if (!flag) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【A端地市和A端区县】不对应，请修改后再提交！");
                        }
                    } else if (!cityZ.isEmpty() && !areaZ.isEmpty()) {
                        boolean flag = false;
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "CHANGE_AREA_BY_CITY", cityZ });
                        for (int k = 0; k < result.size(); k++) {
                            String tempResult = result.getData(k).getString("DATA_ID");
                            if (tempResult.contains(areaZ)) {
                                flag = true;
                            }
                        }
                        if (!flag) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【Z端地市和Z端区县】不对应，请修改后再提交！");
                        }
                    }
                } else {
                    if (attrCode.equals("PROVINCEA")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_PROVINCE", attrValue });
                        if (IDataUtil.isEmpty(result)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【所属省份】不符合要求，请修改后再提交！");
                        }
                    } else if (attrCode.equals("CITYA")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_CITY_NEWCODE", attrValue });
                        if (IDataUtil.isEmpty(result)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【所属地市】不符合要求，请修改后再提交！");
                        }
                        cityA = attrValue;
                    } else if (attrCode.equals("AREAA")) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_COUNTY_CODE", attrValue });
                        if (IDataUtil.isEmpty(result)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【所属区县】不符合要求，请修改后再提交！");
                        }
                        areaA = attrValue;
                    } else if (attrCode.equals("PORTAINTERFACETYPE") && !attrValue.isEmpty()) {
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "INTERFACE_TYPE", attrValue });
                        if (IDataUtil.isEmpty(result)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【端口类型】不符合要求，请修改后再提交！");
                        }
                    } else if (!cityA.isEmpty() && !areaA.isEmpty()) {
                        boolean flag = false;
                        IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "CHANGE_AREA_BY_CITY", cityA });
                        for (int k = 0; k < result.size(); k++) {
                            String tempResult = result.getData(k).getString("DATA_ID");
                            if (tempResult.contains(areaA)) {
                                flag = true;
                            }
                        }
                        if (!flag) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【地市和区县】不对应，请修改后再提交！");
                        }
                    }
                }
                if (attrCode.equals("TRANSFERMODE")) {
                    IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "TRANSFER_MODE", attrValue });
                    if (IDataUtil.isEmpty(result) && !attrValue.isEmpty()) {
                        String productNo = getBatchProductNo(datalineData);
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【传输接入方式】不符合要求，请修改后再提交！");
                    }
                } else if (attrCode.equals("ROUTEMODE") && !attrValue.isEmpty()) {
                    IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_NAME", null, new String[] { "TYPE_ID", "DATA_ID" }, new String[] { "ROUTEMODE", attrValue });
                    if (IDataUtil.isEmpty(result)) {
                        String productNo = getBatchProductNo(datalineData);
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【路由保护方式】不符合要求，请修改后再提交！");
                    }
                } else if (attrCode.equals("BIZSECURITYLV")) {
                    IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BIZ_SECURITY_LV", attrValue });
                    if (IDataUtil.isEmpty(result)) {
                        String productNo = getBatchProductNo(datalineData);
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【业务保障等级】不符合要求，请修改后再提交！");
                    }
                } else if (attrCode.equals("ISPREOCCUPY")) {
                    IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_NAME", null, new String[] { "TYPE_ID", "DATA_ID" }, new String[] { "IF_CHOOSE_CONFCRM", attrValue });
                    if (IDataUtil.isEmpty(result)) {
                        String productNo = getBatchProductNo(datalineData);
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【资源是否预占】不符合要求，请修改后再提交！");
                    }
                }
            }
            
            if (offerCode.equals("7012")||offerCode.equals("70121")||offerCode.equals("70122")) {
            	if (bizRange.equals("本地市") && cityA.equals(cityZ)) {
                    String addrA = cityA + areaA + countyA + villageA;
                    String addrZ = cityZ + areaZ + countyZ + villageZ;
                    if (!addrA.isEmpty() && !addrZ.isEmpty()) {
                        if (addrA.equals(addrZ)) {
                            String productNo = getBatchProductNo(datalineData);
                            CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】本对端详细安装地址相同，请修改后再提交！");
                        }
                    }
                }
            }
            
        }

    }

    /**
     * 获取批量导入专线实例号
     * 
     * @param submitData
     * @throws Exception
     */
    private String getBatchProductNo(IDataset datalineData) throws Exception {
        String productNo = null;
        for (int j = 0; j < datalineData.size(); j++) {
            IData dataline2 = datalineData.getData(j);
            String attrCode2 = dataline2.getString("ATTR_CODE");
            if (attrCode2.equals("pattr_PRODUCTNO") || attrCode2.equals("PRODUCTNO")) {
                productNo = dataline2.getString("ATTR_VALUE");
                break;
            }
        }
        return productNo;
    }

    /**
     * 获取从 载入历史数据 中选择的数据
     * 
     * @param submitData
     * @throws Exception
     */
    public void sureHisConfCrm(IRequestCycle cycle) throws Exception {
        IDataset datalines = new DatasetList();
        IData param = getData();
        String state = param.getString("STATE");
        String bpmTempletId = param.getString("BPM_TEMPLET_ID");
        // IData publicInput = new DataMap();
        if (state.equals("否")) {
            IData eomsInput = new DataMap();
            eomsInput.put("IBSYSID", param.getString("IBSYSID"));
            // 在TF_BH_EOP_EOMS_STATE表查询此条专线实例号record_num
            IDataset eomsStateInfos = CSViewCall.call(this, "SS.WorkformEomsStateSVC.queryHisEomsStateByIbsysidAndProductNo", eomsInput);
            if (IDataUtil.isNotEmpty(eomsStateInfos)) {
                for (int i = 0; i < eomsStateInfos.size(); i++) {
                    IData datalineInput = new DataMap();
                    IData eomsStateInfo = eomsStateInfos.getData(i);
                    String recordNum = eomsStateInfo.getString("RECORD_NUM");
                    // 在TF_BH_EOP_EOMS表查询group_seq、sub_ibsysid、record_num
                    IData eomsHInput = new DataMap();
                    eomsHInput.put("RECORD_NUM", recordNum);
                    eomsHInput.put("IBSYSID", param.getString("IBSYSID"));
                    IDataset eomsInfos = CSViewCall.call(this, "SS.WorkformEomsSVC.qryHisEomsByIbsysIdOperTypeGroupSeq", eomsHInput);
                    if (IDataUtil.isNotEmpty(eomsInfos)) {
                        IData eomsInfo = eomsInfos.getData(0);
                        String groupSeq = eomsInfo.getString("GROUP_SEQ");
                        String subIbsysid = eomsInfo.getString("SUB_IBSYSID");
                        IData subInput = new DataMap();
                        subInput.put("GROUP_SEQ", groupSeq);
                        subInput.put("SUB_IBSYSID", subIbsysid);
                        subInput.put("RECORD_NUM", recordNum);
                        IDataset subInfos = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishAttrByGroupSeqRecordNum", subInput);
                        for (int j = 0; j < subInfos.size(); j++) {
                            IData subInfo = subInfos.getData(j);
                            IData temp = new DataMap();
                            String attrCode = subInfo.getString("ATTR_CODE");
                            String attrValue = subInfo.getString("ATTR_VALUE");
                            if (attrCode.equals("ROUTEMODE")) {
                                attrValue = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "ROUTEMODE", attrValue });
                            } else if (attrCode.equals("ISPREOCCUPY")) {
                                attrValue = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "IF_CHOOSE_CONFCRM", attrValue });
                            } else if (attrCode.equals("TRADEID") || attrCode.equals("PRODUCTNO")) {
                                if (bpmTempletId.equals("ERESOURCECONFIRMZHZG")) {
                                    continue;
                                }
                            }
                            temp.put(attrCode, attrValue);
                            datalineInput.putAll(temp);
                        }
                        IData subInput2 = new DataMap();
                        subInput2.put("GROUP_SEQ", groupSeq);
                        subInput2.put("SUB_IBSYSID", subIbsysid);
                        subInput2.put("RECORD_NUM", 0);
                        IDataset subInfos2 = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryFinishAttrByGroupSeqRecordNum", subInput2);
                        for (int k = 0; k < subInfos2.size(); k++) {
                            IData subInfo2 = subInfos2.getData(k);
                            IData temp2 = new DataMap();
                            String attrCode2 = subInfo2.getString("ATTR_CODE");
                            if (attrCode2.equals("BREQUIREMENTDESC") || attrCode2.equals("BUILDINGSECTION") || attrCode2.equals("BIZRANGE") || attrCode2.equals("BUSINESSTYPE") || attrCode2.equals("DIRECTLINE_SCOPE")
                                    || attrCode2.equals("SERVICETYPE") || attrCode2.equals("TITLE") || attrCode2.equals("URGENCY_LEVEL") || attrCode2.equals("COUNTYNAME") || attrCode2.equals("CITYNAME")) {
                                String attrValue2 = subInfo2.getString("ATTR_VALUE");
                                temp2.put(attrCode2, attrValue2);
                                datalineInput.putAll(temp2);
                            }
                        }
                    } else {
                        IData subInput = new DataMap();
                        subInput.put("RECORD_NUM", recordNum);
                        subInput.put("IBSYSID", param.getString("IBSYSID"));
                        subInput.put("NODE_ID", "archive");
                        IDataset subInfos = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", subInput);
                        for (int j = 0; j < subInfos.size(); j++) {
                            IData subInfo = subInfos.getData(j);
                            IData temp = new DataMap();
                            String attrCode = subInfo.getString("ATTR_CODE");
                            String attrValue = subInfo.getString("ATTR_VALUE");
                            if (attrCode.equals("ROUTEMODE")) {
                                attrValue = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "ROUTEMODE", attrValue });
                            } else if (attrCode.equals("ISPREOCCUPY")) {
                                attrValue = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "IF_CHOOSE_CONFCRM", attrValue });
                            } else if (attrCode.equals("TRADEID") || attrCode.equals("PRODUCTNO")) {
                                if (bpmTempletId.equals("ERESOURCECONFIRMZHZG")) {
                                    continue;
                                }
                            }
                            temp.put(attrCode, attrValue);
                            datalineInput.putAll(temp);
                        }
                    }
                    if (bpmTempletId.equals("ERESOURCECONFIRMZHZG")) {
                        // 初始化生成专线实例号
                        IData inParam = new DataMap();
                        IDataset dataLine = CSViewCall.call(this, "CS.SeqMgrSVC.getMaxNumberLine", inParam);
                        IData seqData = (IData) dataLine.get(0);
                        String productNo = seqData.getString("seq_id");
                        datalineInput.put("TRADEID", productNo);
                        datalineInput.put("PRODUCTNO", productNo);
                    }
                    datalines.add(datalineInput);
                }
            }
        } else if (state.equals("是")) {
            IData eomsInput = new DataMap();
            eomsInput.put("IBSYSID", param.getString("IBSYSID"));
            // 在TF_B_EOP_EOMS_STATE表查询此条专线实例号record_num
            IDataset eomsStateInfos = CSViewCall.call(this, "SS.WorkformEomsStateSVC.qryEomsStateByIbsysid", eomsInput);
            if (IDataUtil.isNotEmpty(eomsStateInfos)) {
                for (int i = 0; i < eomsStateInfos.size(); i++) {
                    IData datalineInput = new DataMap();
                    IData eomsStateInfo = eomsStateInfos.getData(i);
                    String recordNum = eomsStateInfo.getString("RECORD_NUM");
                    // 在TF_B_EOP_EOMS表查询group_seq、sub_ibsysid、record_num
                    IData eomsHInput = new DataMap();
                    eomsHInput.put("RECORD_NUM", recordNum);
                    eomsHInput.put("IBSYSID", param.getString("IBSYSID"));
                    IDataset eomsInfos = CSViewCall.call(this, "SS.WorkformEomsSVC.qryEomsByIbsysIdOperTypeGroupSeq", eomsHInput);
                    if (IDataUtil.isNotEmpty(eomsInfos)) {
                        IData eomsInfo = eomsInfos.getData(0);
                        String groupSeq = eomsInfo.getString("GROUP_SEQ");
                        String subIbsysid = eomsInfo.getString("SUB_IBSYSID");
                        IData subInput = new DataMap();
                        subInput.put("GROUP_SEQ", groupSeq);
                        subInput.put("SUB_IBSYSID", subIbsysid);
                        subInput.put("RECORD_NUM", recordNum);
                        IDataset subInfos = CSViewCall.call(this, "SS.WorkformAttrSVC.qryAttrBySubIbsysidAndRecordNumGroupSeq", subInput);
                        for (int j = 0; j < subInfos.size(); j++) {
                            IData subInfo = subInfos.getData(j);
                            IData temp = new DataMap();
                            String attrCode = subInfo.getString("ATTR_CODE");
                            String attrValue = subInfo.getString("ATTR_VALUE");
                            if (attrCode.equals("ROUTEMODE")) {
                                attrValue = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "ROUTEMODE", attrValue });
                            } else if (attrCode.equals("ISPREOCCUPY")) {
                                attrValue = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "IF_CHOOSE_CONFCRM", attrValue });
                            } else if (attrCode.equals("TRADEID") || attrCode.equals("PRODUCTNO")) {
                                if (bpmTempletId.equals("ERESOURCECONFIRMZHZG")) {
                                    continue;
                                }
                            }
                            temp.put(attrCode, attrValue);
                            datalineInput.putAll(temp);
                        }
                        IData subInput2 = new DataMap();
                        subInput2.put("GROUP_SEQ", groupSeq);
                        subInput2.put("SUB_IBSYSID", subIbsysid);
                        subInput2.put("RECORD_NUM", 0);
                        IDataset subInfos2 = CSViewCall.call(this, "SS.WorkformAttrSVC.qryAttrBySubIbsysidAndRecordNumGroupSeq", subInput2);
                        for (int k = 0; k < subInfos2.size(); k++) {
                            IData subInfo2 = subInfos2.getData(k);
                            IData temp2 = new DataMap();
                            String attrCode2 = subInfo2.getString("ATTR_CODE");
                            if (attrCode2.equals("BREQUIREMENTDESC") || attrCode2.equals("BUILDINGSECTION") || attrCode2.equals("BIZRANGE") || attrCode2.equals("BUSINESSTYPE") || attrCode2.equals("DIRECTLINE_SCOPE")
                                    || attrCode2.equals("SERVICETYPE") || attrCode2.equals("TITLE") || attrCode2.equals("URGENCY_LEVEL") || attrCode2.equals("COUNTYNAME") || attrCode2.equals("CITYNAME")) {
                                String attrValue2 = subInfo2.getString("ATTR_VALUE");
                                temp2.put(attrCode2, attrValue2);
                                datalineInput.putAll(temp2);
                            }
                        }
                    }
                    if (bpmTempletId.equals("ERESOURCECONFIRMZHZG")) {
                        // 初始化生成专线实例号
                        IData inParam = new DataMap();
                        IDataset dataLine = CSViewCall.call(this, "CS.SeqMgrSVC.getMaxNumberLine", inParam);
                        IData seqData = (IData) dataLine.get(0);
                        String productNo = seqData.getString("seq_id");
                        datalineInput.put("TRADEID", productNo);
                        datalineInput.put("PRODUCTNO", productNo);
                    }
                    datalines.add(datalineInput);
                }
            }
        }
        setAjax(datalines);
    }

    public void getUserIdByserialNum(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put("REMOVE_TAG", 0);
        IDataset userInfos = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoBySnNoProduct", data);
        if (IDataUtil.isNotEmpty(userInfos)) {
            IData input = new DataMap();
            String userId = userInfos.getData(0).getString("USER_ID");
            input.put("USER_ID", userId);
            setAjax(input);
        }
    }

    public void judgeSeriNumber(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset userInfos = CSViewCall.call(this, "SS.GroupInfoQuerySVC.qryPayRelationByUserId", data);
        if (IDataUtil.isNotEmpty(userInfos)) {
            for (Object obj : userInfos) {
                Integer flag = 0;
                IData info = (IData) obj;
                if ("0".equals(info.getString("DEFAULT_TAG")) && "1".equals(info.getString("LIMIT_TYPE"))) {
                    flag = 1;
                    IData input = new DataMap();
                    input.put("FLAG", flag);
                    setAjax(input);
                    break;
                }
            }
        }
    }

    public void queryChangeLines(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataLines = new DatasetList();
        if (!"".equals(data.getString("EC_USER_ID", ""))) {
            String productId = data.getString("OFFER_CODE", "");
            String bizRange = data.getString("BIZRANGE", "");
            String templetId = data.getString("Templet_Id", "");

            IDataset esopParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
            if ("7010".equals(productId)) {
                IData param = new DataMap();
                param.put("USER_ID", data.getString("EC_USER_ID"));
                // param.put("PRODUCT_ID", productId);VIOP不传产品id直接查
                IDataset voipDataLines = CSViewCall.call(this, "SS.QcsGrpIntfSVC.getProductInfoForPboss", param);
                if (IDataUtil.isEmpty(voipDataLines) && !"9983".equals(productId)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "集团用户【" + data.getString("EC_USER_ID") + "】不存在有效的专线，不能办理该业务！");
                }
                for (int j = 0; j < voipDataLines.size(); j++) {
                    IData temp = new DataMap();
                    IData voipDataLine = voipDataLines.getData(j);
                    // 判断专线为本地市/跨地市,语音专线只有本地市
                    /* boolean isContinue = checkVoipBizRange(voipDataLine, bizRange); if (isContinue) { continue; } */
                    Iterator<String> itr = voipDataLine.keySet().iterator();
                    while (itr.hasNext()) {
                        String attrCode = itr.next();
                        for (int k = 0; k < esopParam.size(); k++) {
                            String paramValue = esopParam.getData(k).getString("PARAMVALUE");
                            if (attrCode.equals(paramValue)) {
                                temp.put(esopParam.getData(k).getString("PARAMNAME"), voipDataLine.getString(attrCode));
                                break;
                            }
                        }
                    }
                    IData voipParam = new DataMap();
                    voipParam.put("USER_ID", data.getString("EC_USER_ID"));
                    voipParam.put("PRODUCTNO", voipDataLine.getString("PRODUCT_NO"));
                    IData voipOtherInfo = CSViewCall.callone(this, "SS.QcsGrpIntfSVC.queryChangeLineInfosForEsop", voipParam);
                    if (IDataUtil.isNotEmpty(voipOtherInfo)) {
                        IDataset disCountParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "VOIP_DISCOUNTPARAM_CRM_ESOP");
                        Iterator<String> itr2 = voipOtherInfo.keySet().iterator();
                        while (itr2.hasNext()) {
                            String attrCode = itr2.next();
                            for (int m = 0; m < disCountParam.size(); m++) {
                                String paramValue = disCountParam.getData(m).getString("PARAMVALUE");
                                if (attrCode.equals(paramValue)) {
                                    temp.put(disCountParam.getData(m).getString("PARAMNAME"), voipOtherInfo.getString(attrCode));
                                    break;
                                }
                            }
                        }
                    }
                    dataLines.add(temp);
                }
            } else {
                String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);

                IData param = new DataMap();
                param.clear();
                param.put("USER_ID_A", data.getString("EC_USER_ID"));
                param.put("RELATION_TYPE_CODE", relationTypeCode);
                param.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
                param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());

                IDataset directLineList = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getAllRelaUUInfoByUserIda", param);
                if (IDataUtil.isEmpty(directLineList) && !"9983".equals(productId)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "集团用户【" + data.getString("EC_USER_ID") + "】不存在有效的专线成员，不能办理该业务！");
                }
                for (int i = 0; i < directLineList.size(); i++) {
                    IData temp = new DataMap();
                    IData directLine = directLineList.getData(i);

                    IData commonParam = new DataMap();
                    commonParam.put("USER_ID", directLine.getString("USER_ID_B"));
                    commonParam.put("ATTR_CODE", "BIZRANGE");
                    commonParam.put("INST_TYPE", "P");
                    IDataset commonList = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserProductAttrValue", commonParam);
                    // 判断专线为本地市/跨地市(只有数据专线有跨地市)
                    if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && (IDataUtil.isEmpty(commonList) || !bizRange.equals(commonList.first().getString("ATTR_VALUE")))) {
                        continue;
                    }

                    temp.put("SERIAL_NUMBER", directLine.getString("SERIAL_NUMBER_B"));
                    temp.put("USER_ID", directLine.getString("USER_ID_B"));
                    IData input = new DataMap();
                    input.put("USER_ID", directLine.getString("USER_ID_B"));
                    IDataset userDataLines = CSViewCall.call(this, "SS.QcsGrpIntfSVC.getProductInfoForPboss", input);
                    if (IDataUtil.isNotEmpty(userDataLines)) {
                        IData userDataLine = userDataLines.getData(0);

                        String productNO = userDataLine.getString("PRODUCT_NO");

                        if ("ECHANGERESOURCECONFIRM".equals(templetId)) {
                            IData inData = new DataMap();
                            inData.put("PRODUCTNO", productNO);
                            IDataset subscribeinfos = CSViewCall.call(this, "SS.WorkformSubscribeSVC.getSubScribeInfoByProductNo", inData);
                            if (IDataUtil.isNotEmpty(subscribeinfos)) {
                                String subIbsysid = subscribeinfos.first().getString("SUB_IBSYSID", "");
                                String recordNum = subscribeinfos.first().getString("RECORD_NUM", "");
                                inData.clear();
                                inData.put("SUB_IBSYSID", subIbsysid);
                                inData.put("RECORD_NUM", recordNum);
                                IDataset attrList = CSViewCall.call(this, "SS.WorkformAttrSVC.getInfoBySubIbsysidRecordNum", inData);
                                if (IDataUtil.isEmpty(attrList)) {
                                    return;
                                }
                                for (int k = 0; k < attrList.size(); k++) {
                                    temp.put(attrList.getData(k).getString("ATTR_CODE"), attrList.getData(k).getString("ATTR_VALUE"));
                                }
                            } else {
                                Iterator<String> itr = userDataLine.keySet().iterator();
                                while (itr.hasNext()) {
                                    String attrCode = itr.next();
                                    for (int j = 0; j < esopParam.size(); j++) {
                                        String paramValue = esopParam.getData(j).getString("PARAMVALUE");
                                        if (attrCode.equals(paramValue)) {
                                            temp.put(esopParam.getData(j).getString("PARAMNAME"), userDataLine.getString(attrCode));
                                            break;
                                        }
                                    }
                                }
                                IData discounts = new DataMap();
                                input.put("INST_TYPE", "D");
                                input.put("PRODUCT_ID", productId);
                                discounts = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getUserLineInfoByUserId", input);
                                input.clear();
                                input.put("CONFIG_NAME", "DISCOUNTPARAM_CRM_ESOP");
                                input.put("STATUS", "0");
                                IDataset disCountParam = CSViewCall.call(this, "SS.EweConfigQrySVC.qryEweConfigByConfigName", input);
                                Iterator<String> itr2 = discounts.keySet().iterator();
                                while (itr2.hasNext()) {
                                    String attrCode = itr2.next();
                                    for (int j = 0; j < disCountParam.size(); j++) {
                                        IData disParam = disCountParam.getData(j);
                                        if (attrCode.equals(disParam.getString("PARAMVALUE"))) {
                                            temp.put(disParam.getString("PARAMNAME"), discounts.getString(attrCode));
                                        }
                                    }
                                }
                            }
                        } else {
                            Iterator<String> itr = userDataLine.keySet().iterator();
                            while (itr.hasNext()) {
                                String attrCode = itr.next();
                                for (int j = 0; j < esopParam.size(); j++) {
                                    String paramValue = esopParam.getData(j).getString("PARAMVALUE");
                                    if (attrCode.equals(paramValue)) {
                                        temp.put(esopParam.getData(j).getString("PARAMNAME"), userDataLine.getString(attrCode));
                                        break;
                                    }
                                }
                            }
                            IData discounts = new DataMap();
                            input.put("INST_TYPE", "D");
                            input.put("PRODUCT_ID", productId);
                            discounts = CSViewCall.callone(this, "CS.UserAttrInfoQrySVC.getUserLineInfoByUserId", input);
                            input.clear();
                            input.put("CONFIG_NAME", "DISCOUNTPARAM_CRM_ESOP");
                            input.put("STATUS", "0");
                            IDataset disCountParam = CSViewCall.call(this, "SS.EweConfigQrySVC.qryEweConfigByConfigName", input);
                            Iterator<String> itr2 = discounts.keySet().iterator();
                            while (itr2.hasNext()) {
                                String attrCode = itr2.next();
                                for (int j = 0; j < disCountParam.size(); j++) {
                                    IData disParam = disCountParam.getData(j);
                                    if (attrCode.equals(disParam.getString("PARAMVALUE"))) {
                                        String attrValue = discounts.getString(attrCode);
                                        if (discounts.getString(attrCode).endsWith("%")) {
                                            temp.put(disParam.getString("PARAMNAME"), attrValue.substring(0, attrValue.length() - 1));
                                        } else {
                                            temp.put(disParam.getString("PARAMNAME"), attrValue);
                                        }
                                    }
                                }
                            }
                        }

                        dataLines.add(temp);
                    }
                }
            }
        }
        // 查询条件不为空的字段保存到一个IData
        if (IDataUtil.isNotEmpty(dataLines)) {
            IData getData = getData("cond");
            IData temp = new DataMap();
            Iterator<String> itr = getData.keySet().iterator();
            while (itr.hasNext()) {
                String attrCode = itr.next();
                String attrValue = getData.getString(attrCode);
                if (attrValue.isEmpty()) {
                    continue;
                } else {
                    temp.put(attrCode, attrValue);
                }
            }
            // 再将查询条件带入到之前已经查询出的专线信息里查询
            IDataset inputDataLines = new DatasetList();
            boolean flag = false;
            for (int i = 0; i < dataLines.size(); i++) {
                IData dataline = dataLines.getData(i);
                Iterator<String> itr2 = temp.keySet().iterator();
                while (itr2.hasNext()) {
                    String attrCode2 = itr2.next();
                    String attrValue2 = temp.getString(attrCode2);
                    String dataAttrValue = dataline.getString(attrCode2);
                    if (attrValue2.equals(dataAttrValue)) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                }
                if (flag) {
                    inputDataLines.add(dataline);
                }
            }
            setInfos(inputDataLines);
            setAjax(inputDataLines);
        }
    }

    public void batchChangeImportZHZG(IRequestCycle cycle) throws Exception {
        IDataset datalineInfo = (IDataset) SharedCache.get("CHANGE_DATALINE_INFOS");
        this.setAjax(datalineInfo);
    }

    public void hintInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String hint = "简单场景变更提示区域，请选择业务场景！";
        String changeMode = data.getString("CHANGEMODE", "");
        String productId = data.getString("PRODUCT_ID", "");
        if ("7010".equals(productId) && "业务保障级别调整".equals(changeMode)) {
            hint = "您受理的VOIP专线变更场景为【业务保障级别调整】,只能对【业务保障等级】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if ("7010".equals(productId) && "减容".equals(changeMode)) {
            hint = "您受理的VOIP专线变更场景为【减容】,只能对【IP带宽】参数进行修改导入，并带宽不能大于当前带宽，如您修改其他或带宽大于当前带宽参数，导入后不会改变！";
        }
        if ("7010".equals(productId) && "异楼搬迁".equals(changeMode)) {
            hint = "您受理的VOIP专线变更场景为【异楼搬迁】,除对【IP带宽，省份】参数不能修改，其他参数都能修改导入，如您修改【IP带宽，省份】，导入后不会改变！";
        }
        if ("7010".equals(productId) && "同楼搬迁".equals(changeMode)) {
            hint = "您受理的VOIP专线变更场景为【同楼搬迁】,除对【IP带宽，省份，业务保障等级】参数不能修改，其他参数都能修改导入，如您修改【IP带宽，省份，业务调整场景】，导入后不会改变！";
        }
        if ("7010".equals(productId) && "扩容".equals(changeMode)) {
            hint = "您受理的VOIP专线变更场景为【扩容】,只能对【IP带宽】参数进行修改导入，并带宽不能大于勘察时的带宽，如您修改其他或带宽大于勘察时的带宽参数，导入后不会改变！";
        }
        if (("7011".equals(productId)|| "70111".equals(productId)|| "70112".equals(productId)) && "业务保障级别调整".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【业务保障级别调整】,只能对【业务保障等级】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if (("7011".equals(productId)|| "70111".equals(productId)|| "70112".equals(productId)) && "IP地址调整".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【IP地址调整】,只能对【IP地址类型，客户申请公网IP地址数，申请公网IPV6地址数，申请公网IPV4地址数，IP地址调整，域名，主域名服务器地址】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if (("7011".equals(productId)|| "70111".equals(productId)|| "70112".equals(productId)) && "减容".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【减容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于当前带宽，如您修改其他或带宽大于当前带宽参数，导入后不会改变！";
        }
        if (("7011".equals(productId)|| "70111".equals(productId)|| "70112".equals(productId)) && "异楼搬迁".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【异楼搬迁】,除对【业务带宽，省份】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份】，导入后不会改变！";
        }
        if (("7011".equals(productId)|| "70111".equals(productId)|| "70112".equals(productId)) && "同楼搬迁".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【同楼搬迁】,除对【业务带宽，省份，业务保障等级】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份，业务保障等级】，导入后不会改变！";
        }
        if (("7011".equals(productId)|| "70111".equals(productId)|| "70112".equals(productId)) && "扩容".equals(changeMode)) {
            hint = "您受理的互联网专线变更场景为【扩容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于勘察时的带宽，如您修改其他或带宽大于勘察时的带宽参数，导入后不会改变！";
        }
        if (("7012".equals(productId)|| "70121".equals(productId)|| "70122".equals(productId)) && "业务保障级别调整".equals(changeMode)) {
            hint = "您受理的数据专线变更场景为【业务保障级别调整】,只能对【业务保障等级，路由保护方式】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if (("7012".equals(productId)|| "70121".equals(productId)|| "70122".equals(productId)) && "减容".equals(changeMode)) {
            hint = "您受理的数据专线变更场景为【减容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于当前带宽，如您修改其他或带宽大于当前带宽参数，导入后不会改变！";
        }
        if (("7012".equals(productId)|| "70121".equals(productId)|| "70122".equals(productId)) && "异楼搬迁".equals(changeMode)) {
            hint = "您受理的数据专线变更场景为【异楼搬迁】,除对【业务带宽，省份】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份】，导入后不会改变！";
        }
        if (("7012".equals(productId)|| "70121".equals(productId)|| "70122".equals(productId)) && "同楼搬迁".equals(changeMode)) {
            hint = "您受理的数据专线变更场景为【同楼搬迁】,除对【业务带宽，省份，业务保障等级】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份，业务保障等级】，导入后不会改变！";
        }
        if (("7012".equals(productId)|| "70121".equals(productId)|| "70122".equals(productId)) && "扩容".equals(changeMode)) {
            hint = "您受理的数据专线变更场景为【扩容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于勘察时的带宽，如您修改其他或带宽大于勘察时的带宽参数，导入后不会改变！";
        }

        if ("7016".equals(productId) && "IP地址调整".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【IP地址调整】,只能对【客户申请公网IP地址数，域名，主域名服务器地址】参数进行修改导入，如您修改其他参数，导入后不会改变！";
        }
        if ("7016".equals(productId) && "减容".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【减容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于当前带宽，如您修改其他或带宽大于当前带宽参数，导入后不会改变！";
        }
        if ("7016".equals(productId) && "异楼搬迁".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【异楼搬迁】,除对【业务带宽，省份】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份】，导入后不会改变！";
        }
        if ("7016".equals(productId) && "同楼搬迁".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【同楼搬迁】,除对【业务带宽，省份，业务保障等级】参数不能修改，其他参数都能修改导入，如您修改【业务带宽，省份，业务保障等级】，导入后不会改变！";
        }
        if ("7016".equals(productId) && "扩容".equals(changeMode)) {
            hint = "您受理的IMS专线变更场景为【扩容】,只能对【业务带宽】参数进行修改导入，并带宽不能大于勘察时的带宽，如您修改其他或带宽大于勘察时的带宽参数，导入后不会改变！";
        }

        data.put("HINT", hint);
        setHintInfo(data);
    }
    
    private void checkAttachName(IData param) throws Exception {
        IDataset attachList = param.getDataset("ATTACH_LIST");
        IDataset discntAttachList = param.getDataset("DISCNT_ATTACH_LIST");
        IDataset allAttachList = new DatasetList();
        if (IDataUtil.isNotEmpty(attachList)) {
            allAttachList.addAll(attachList);
        }
        if (IDataUtil.isNotEmpty(discntAttachList)) {
            allAttachList.addAll(discntAttachList);
        }
        if (IDataUtil.isNotEmpty(allAttachList)) {
            for (int i = 0; i < allAttachList.size(); i++) {
                IData attachDataA = allAttachList.getData(i);
                String fileNameA = attachDataA.getString("FILE_NAME");
                for (int j = 0; j < allAttachList.size(); j++) {
                    if (i == j) {
                        continue;
                    }
                    IData attachDataB = allAttachList.getData(j);
                    String fileNameB = attachDataB.getString("FILE_NAME");
                    if (StringUtils.equals(fileNameA, fileNameB)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "附件【" + fileNameA + "】存在同名附件，请修改后提交！");
                    }
                }
            }
        }
    }

    public void checkProductNo(IDataset datalineDatas) throws Exception {
        IDataset productNos = new DatasetList();
        for (int i = 0; i < datalineDatas.size(); i++) {
            IDataset datalineData = datalineDatas.getDataset(i);
            for (int j = 0; j < datalineData.size(); j++) {
                IData dataline = datalineData.getData(j);
                String attrValue = dataline.getString("ATTR_VALUE");
                String attrCode = dataline.getString("ATTR_CODE");
                if (attrCode.equals("PRODUCTNO") || attrCode.equals("pattr_PRODUCTNO")) {
                    if (productNos.contains(attrValue)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "专线实例号【" + attrValue + " 】重复，请删除后再提交！");
                    } else {
                        productNos.add(attrValue);
                    }
                }
            }
        }
    }

    // 校验专线名称
    public void queryTradename(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String tradeName = data.getString("TRADE_NAME", "");
        String productNo = data.getString("PRODUCTNO", "");
        String bpmTempletId = data.getString("BPM_TEMPLET_ID", "");
        data.put("DATA_LINE_NAME", tradeName);
        data.put("PRODUCTNO", productNo);

        boolean result = false;
        IData paramresult = new DataMap();
        IData results = new DataMap();
        try {
            // 先查DATALINE表的专线名称是否与页面的专线名称一致
            IData userDataNameInfos = CSViewCall.callone(this, "SS.QcsGrpIntfSVC.qryUserDatalineByProductNO", data);
            String datalineName = "";
            if (IDataUtil.isNotEmpty(userDataNameInfos)) {
                datalineName = userDataNameInfos.getString("RSRV_STR5", "");
            }
            // 名称一致直接直接返回
            if (datalineName.equals(tradeName)) {
                result = true;
            } else {
                try {

                    // 不一致再查dataline表与在途ATTR表是否存在相同的专线名称，(取ATTR表最新的数据，且流程不为勘察单的流程)
                    IData datalineNameInfo = CSViewCall.callone(this, "SS.QcsGrpIntfSVC.qryGrpUserDatalineByName", data);
                    if (IDataUtil.isEmpty(datalineNameInfo)) {
                        result = true;
                    } else {
                        String productnos = datalineNameInfo.getString("PRODUCT_NO", "");
                        if (productnos.equals(productNo)) {
                            result = true;
                        } else {
                            result = false;
                            paramresult.put("ERROR_MESSAGE", "您对应的专线实列号：【" + productNo + "】,输入的专线名称：【" + tradeName + "】已在实列号：【" + productnos + "】中使用，不能重复，请修改！");
                        }
                    }
                }
                catch (Exception e) {
                    result = false;
                    paramresult.put("ERROR_MESSAGE", e);
                }
            }
        }
        catch (Exception e) {
            result = false;
            paramresult.put("ERROR_MESSAGE", e);
        }

        paramresult.put("RESULT", result);
        results.put("AJAX_DATA", paramresult);

        String ajaxdatastr = results.getString("AJAX_DATA", "");

        if (StringUtils.isNotBlank(ajaxdatastr)) {
            this.setAjax(new DataMap(ajaxdatastr));
        }
    }

    public void queryTradenameInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        boolean result = true;
        IData paramresult = new DataMap();
        IData results = new DataMap();
        IDataset attachInfos = new DatasetList(data.getString("TRADENAME_INFOS"));
        if (IDataUtil.isNotEmpty(attachInfos)) {
            for (int i = 0; i < attachInfos.size(); i++) {
                IData dataInfo = attachInfos.getData(i);
                String dataNameI = dataInfo.getString("TRADENAME");
                String productnoI = dataInfo.getString("PRODUCTNO");
                if (!result) {
                    break;
                }
                for (int s = 0; s < attachInfos.size(); s++) {
                    IData dataInfoS = attachInfos.getData(s);
                    String dataNameS = dataInfoS.getString("TRADENAME");
                    String productnoS = dataInfoS.getString("PRODUCTNO");
                    if (i != s && dataNameI.equals(dataNameS)) {
                        result = false;
                        paramresult.put("ERROR_MESSAGE", "您对应的专线实列号：【" + productnoS + "】与【" + productnoI + "】,输入的专线名称：【" + dataNameI + "】相同，请检查！");
                        break;
                    }
                }
            }
        }
        if (result) {
            paramresult.put("ERROR_MESSAGE", "OK");
        }
        paramresult.put("RESULT", result);
        results.put("AJAX_DATA", paramresult);

        String ajaxdatastr = results.getString("AJAX_DATA", "");

        if (StringUtils.isNotBlank(ajaxdatastr)) {
            this.setAjax(new DataMap(ajaxdatastr));
        }

    }
    public void checkOrderNumFlagForIdc(IRequestCycle cycle) throws Exception {
    	IData datas=  new DataMap();
        IData data = getData();
        String orderNumFlag = data.getString("ORDER_OrderNumFlag");
        String offerCode = data.getString("OFFER_CODE");
        if("1".equals(orderNumFlag)){
            String chooseOpenType = data.getString("ORDER_ChooseOpenType","");

            if(chooseOpenType.equals(offerCode)){
            	//第一笔即最后一笔
            	datas.put("ORDER_LastIbsysidFlag", "1");
            	setAjax(datas);
            	return;
            }else{
            	datas.put("ORDER_LastIbsysidFlag", "0");
	        	setAjax(datas);
	        	return;
	        }
        	
        }else{
            String chooseFirstIbsysid = data.getString("ORDER_ChooseFirstIbsysid","");
        	IData tempParam = new DataMap();
            tempParam.put("POOL_NAME", "ORDER_OrderNumFlag");
            tempParam.put("STATE", "F");
            tempParam.put("REL_IBSYSID", chooseFirstIbsysid);

            // 查询此勘察单总共有多少条
            IDataset subscribePoolInfos = CSViewCall.call(this, "SS.ConfCrmQrySVC.qrySubscribePool", tempParam);
            IData inparam=new DataMap();
    		inparam.put("IBSYSID", chooseFirstIbsysid);
    		inparam.put("NODE_ID", "apply");
            IDataset attrList = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", inparam);
//    		IDataset attrList=WorkformAttrBean.getNewInfoByIbsysidAndNodeId(inparam);
    		String chooseOpenType =null;
    		if (DataUtils.isNotEmpty(attrList)) {
    			for (int i = 0; i < attrList.size(); i++) {
    				IData attrData=attrList.getData(i);
    				if(DataUtils.isNotEmpty(attrData)){
    					if("ORDER_ChooseOpenType".equals(attrData.getString("ATTR_CODE",""))){
    						chooseOpenType=attrData.getString("ATTR_VALUE","");
    						break;
    					}
    				}
    				
    				
    			}
    		}
    		if(chooseOpenType==null){
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "首笔工单[" + chooseFirstIbsysid + "]查询资料表TF_B_EOP_ATTR字段ORDER_ChooseOpenType没有数据！");

    		}
            String[] chooseOpenTypeList= chooseOpenType.split(",");//2
            IDataset subscribePoolInfos1= new DatasetList(subscribePoolInfos.toString());
            boolean equalsflag2=false;//第一笔单类型是否包括当前产品
            for (int j = 0; j < chooseOpenTypeList.length; j++) {
                boolean equalsflag=false;//是否已添加当前产品
                if(offerCode.equals(chooseOpenTypeList[j])){
                	equalsflag2=true;
                }
                for (int i = 0; i < subscribePoolInfos1.size(); i++) {//1
                    IData subscribePoolInfo = subscribePoolInfos1.getData(i);
                    if(offerCode.equals(subscribePoolInfo.getString("POOL_CODE", ""))){
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "该首笔工单[" + chooseFirstIbsysid + "]已存在该产品类型[" + offerCode + "]，已添加的相应工单为[" + subscribePoolInfo.getString("POOL_VALUE", "") + "]！");
                    }
                    
                	if(subscribePoolInfo.getString("POOL_CODE", "").equals(chooseOpenTypeList[j])){
                		equalsflag=true;
                		subscribePoolInfos1.remove(i);
                		break;
                	}
                }
                
//                if(!equalsflag&&!offerCode.equals(chooseOpenTypeList[j])){//不符合最后一条要求
//                	datas.put("ORDER_LastIbsysidFlag", "0");
//                	setAjax(datas);
//                    return;
//                }
            }
            if(!equalsflag2){
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该首笔工单[" + chooseFirstIbsysid + "]无法关联该产品类型[" + offerCode + "]，已添加产品类型为[" + chooseOpenType + "]！");
            }
            if(subscribePoolInfos1.size()==0&&(subscribePoolInfos.size()+1>=chooseOpenTypeList.length)){//符合要求
            	datas.put("ORDER_LastIbsysidFlag", "1");
            	setAjax(datas);
            	return;
            }
        }
        datas.put("ORDER_LastIbsysidFlag", "0");
    	setAjax(datas);
    }
    public void selectRelIbsysidForIdc(IRequestCycle cycle) throws Exception {
        queryInfo(cycle);
        IData data = getData();
        String ibsysIdNow = data.getString("rel_ibsysid");
        IData input = new DataMap();
        input.put("IBSYSID", ibsysIdNow);
        input.put("NODE_ID", "apply");
        IData info = new DataMap();
        IDataset attrList = CSViewCall.call(this, "SS.WorkformAttrSVC.qryNewHisEopAttrByIbsysidAndNodeId", input);
        if (IDataUtil.isNotEmpty(attrList)) {
            for (int i = 0; i < attrList.size(); i++) {
                IData attrData = attrList.getData(i);
                if (IDataUtil.isNotEmpty(attrData)) {
                    info.put(attrData.getString("ATTR_CODE", ""), attrData.getString("ATTR_VALUE", ""));
                }
            }
        }
        input=new DataMap();
        input.put("IBSYSID", ibsysIdNow);
        input.put("RECORD_NUM", "0");
        IDataset eomsList = CSViewCall.call(this, "SS.WorkformEomsSVC.getHEomsByIbsysidRecordNum", input);
        if(IDataUtil.isNotEmpty(eomsList)){
        	
        	
        	IData eomsData=eomsList.getData(0);
        	if(IDataUtil.isNotEmpty(eomsData)){
        		if("EDIRECTLINECHECKIDC".equals(eomsData.getString("SUB_BUSI_TYPE"))||"EDIRECTLINECHANGECHECKIDC".equals(eomsData.getString("SUB_BUSI_TYPE"))){
        			data.put("IDC_Prospecting", "1");
        			data.put("IDC_ProspectingCase", eomsData.getString("SERIALNO",""));
        		}else if("EDIRECTLINEPREEMPTIONIDC".equals(eomsData.getString("SUB_BUSI_TYPE"))){
        			data.put("IDC_Preemption", "1");
        			data.put("IDC_ResourceCase", eomsData.getString("SERIALNO",""));
        		}
        	}
        }
        
        info.putAll(data);

        setPattrInfo(info);

    }
    public void queryIdcOperType(IRequestCycle cycle) throws Exception {
    	IDataset  productCodeList= StaticUtil.getList(null,"TD_B_EWE_CONFIG","PARAMNAME","VALUEDESC", new String[]{ "CONFIGNAME"}, new String[]{ "IDC_PRODUCTCODE" });
    	setPattrList(productCodeList);
    }
    public void getContractFileByContractId(IRequestCycle cycle) throws Exception {
    	IData data = getData();
        String contractId = data.getString("CONTRACT_ID");
        
    	IData contractInput = new DataMap();
        contractInput.put("CONTRACT_ID", contractId);
        IDataset contracts = CSViewCall.call(this, "CS.CustContractInfoQrySVC.qryContractInfoByContractIdForGrp", contractInput);
        if (IDataUtil.isNotEmpty(contracts)) {
            IData contractInfo = contracts.getData(0);
            if (null != contractInfo.getString("CONTRACT_FILE_ID")) {
                IData fileData = new DataMap();
            	fileData.put("C_CONTRACT_FILE_ID", contractInfo.getString("CONTRACT_FILE_ID").split(":")[0]);
                fileData.put("IDC_C_FILE_LIST", contractInfo.getString("CONTRACT_FILE_ID").split(":")[0]);
                fileData.put("IDC_C_FILE_LIST_NAME", contractInfo.getString("CONTRACT_FILE_ID").split(":")[1]);
                setAttachInfo(fileData);
 
                setAjax(fileData);
            }
        }
    }

    public abstract void setHintInfo(IData hintInfo) throws Exception;

    public abstract void setGroupInfo(IData groupInfo) throws Exception;

    public abstract void setCustMgrInfo(IData custMgrInfo) throws Exception;

    public abstract void setInAttr(IData inAttr) throws Exception;

    public abstract void setComminfo(IData comminfo) throws Exception;

    public abstract void setInfo(IData info) throws Exception;

    public abstract void setInfos(IDataset infos) throws Exception;

    public abstract void setFtpInfo(IData ftpInfo) throws Exception;

    public abstract void setBusi(IData busi) throws Exception;

    public abstract void setOffer(IData offer) throws Exception;

    public abstract void setAreas(IDataset areas) throws Exception;

    public abstract void setWorkformData(IData workformData) throws Exception;

    public abstract void setSerialNO(String serialNO) throws Exception;

    public abstract void setRepInfo(IData repInfo) throws Exception;

    public abstract void setCondition(IData condition);

    public abstract void setEcAccountList(IDataset ecAccountList) throws Exception;

    public abstract void setPattrInfo(IData pattrInfo) throws Exception;

    public abstract void setPattrList(IDataset pattrList) throws Exception;

    public abstract void setAttachInfo(IData attachInfo) throws Exception;

    public abstract void setInfoCount(long infoCount) throws Exception;
}
