package com.asiainfo.veris.crm.iorder.web.igroup.minorec.acceptanceperiodAudit;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.FrontProdConverter;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.dataTrans.MinorecIntegrateTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

public abstract class AuditFailedChgMinorec extends EopBasePage {

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void initPage(IRequestCycle cycle) throws Exception {
        super.initPage(cycle);
        IData param = getData();

        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, param.getString("GROUP_ID"));
        String custId = group.getString("CUST_ID");
        param.put("CUST_ID", custId);
        param.put("EPARCHY_CODE", group.getString("EPARCHY_CODE"));
        // 查询attr和other表，获取公共信息
        queryEopAttrOtherData(param);

        // 获取EOP_SUBSCRIBE表数据
        IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, param.getString("IBSYSID"));
        if (IDataUtil.isEmpty(subscribeData)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工单IBSYSID=【" + param.getString("IBSYSID") + "】不存在！");
        }
        subscribeData.put("NODE_ID", param.getString("NODE_ID"));
        subscribeData.put("BUSIFORM_NODE_ID", param.getString("BUSIFORM_NODE_ID"));
        param.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
        // 获取节点ID
        IData input = new DataMap();
        input.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
        input.put("NODE_TYPE", "3");
        IDataset nodeTempleteList = CSViewCall.call(this, "SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
        if (IDataUtil.isNotEmpty(nodeTempleteList)) {
            param.put("NODE_ID", nodeTempleteList.first().getString("NODE_ID"));
        }
        input.clear();
        input.put("BUSI_CODE", param.getString("PRODUCT_ID"));
        input.put("OPER_TYPE", "20");
        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
        if (IDataUtil.isNotEmpty(busiSpecReleList)) {
            param.put("BUSI_SPEC_RELE", busiSpecReleList.first());
        }
        IData nodeTempletedData = new DataMap();
        nodeTempletedData.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
        nodeTempletedData.put("NODE_ID", param.getString("NODE_ID"));
        param.put("NODE_TEMPLETE", nodeTempletedData);

        setCondition(param);
    }

    /**
     * 查询attr和other表，获取公共信息
     * 
     * @param
     * @throws Exception
     */
    public void queryEopAttrOtherData(IData param) throws Exception {
        // 查询attr和other表，获取公共信息
        IDataset attrOtherList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryEopAttrOtherData", param);
        if (IDataUtil.isNotEmpty(attrOtherList)) {
            IData pulicInfo = new DataMap();
            IDataset attrInfo = attrOtherList.getData(0).getDataset("ATTR_LIST");// 获取ATTR表参数
            IDataset otherInfo = attrOtherList.getData(0).getDataset("OTHER_LIST");// 获取OTHER表参数
            IDataset otherAttrList = new DatasetList();
            otherAttrList.addAll(otherInfo);
            otherAttrList.addAll(attrInfo);
            for (Object object : otherAttrList) {
                IData oaInfo = (IData) object;
                pulicInfo.put(oaInfo.getString("ATTR_CODE"), oaInfo.getString("ATTR_VALUE"));
            }
            String productId = param.getString("PRODUCT_ID");
            String bpmTempletId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[] { "MINOREC_XN_PRODUCT", productId });
            pulicInfo.put("TEMPLET_ID", bpmTempletId);

            setInfo(pulicInfo);

            // 查询集团与成员入表信息
            queryAuditQuickMemberData(param, pulicInfo.getString("OPER_TYPE"), bpmTempletId);

        }
    }

    /**
     * 查询集团与成员入表信息
     * 
     * @param
     * @throws Exception
     */
    public void queryAuditQuickMemberData(IData param, String operType, String bpmTempletId) throws Exception {

        IDataset offerList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryAuditQuickMemberData", param);
        if (IDataUtil.isNotEmpty(offerList)) {
            queryOffer(offerList, operType);// 获取订购产品
            contractData(offerList, param, bpmTempletId);// 获取电子协议合同信息

        }

    }

    /**
     * 回显合同信息
     * 
     * @param
     * @throws Exception
     */
    public void contractData(IDataset offerList, IData param, String bpmTempletId) throws Exception {
        IData productInfo = new DataMap();
        if ("YIDANQINGSHANGPUCHANGE".equals(bpmTempletId)) {// 判断是否是商铺类产品，是的话是否包含集团V网的电子协议
            for (Object object : offerList) {
                IData offerData = (IData) object;
                IData ecCommonInfo = offerData.getData("EC_COMMON_INFO");
                IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
                if ("8000".equals(contractInfo.getString("OFFER_IDS"))) {// 回显集团V网的电子协议信息
                    productInfo.put("CONTRACT_NAME_VW", contractInfo.getString("CONTRACT_NAME"));
                    productInfo.put("CONTRACT_ID_VW", contractInfo.getString("CONTRACT_ID"));
                    productInfo.put("CONTRACT_END_DATE_VW", contractInfo.getString("CONTRACT_END_DATE"));
                    productInfo.put("CONTRACT_WRITE_DATE_VW", contractInfo.getString("CONTRACT_WRITE_DATE"));
                    productInfo.put("OFFER_IDS_VW", contractInfo.getString("OFFER_IDS"));
                    productInfo.put("GROUP_VW", "true");
                } else {// 回显其他产品的电子协议信息
                    productInfo.put("CONTRACT_NAME", contractInfo.getString("CONTRACT_NAME"));
                    productInfo.put("CONTRACT_ID", contractInfo.getString("CONTRACT_ID"));
                    productInfo.put("CONTRACT_END_DATE", contractInfo.getString("CONTRACT_END_DATE"));
                    productInfo.put("CONTRACT_WRITE_DATE", contractInfo.getString("CONTRACT_WRITE_DATE"));
                    productInfo.put("OFFER_IDS", contractInfo.getString("OFFER_IDS"));
                    productInfo.put("CONTRACT_IDS", "true");
                }
            }
            setPattrInfo(productInfo);

        } else {// 其他流程只需获取其中一个电子协议信息即可
            IData ecCommonInfo = offerList.first().getData("EC_COMMON_INFO");
            IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
            productInfo.put("CONTRACT_NAME", contractInfo.getString("CONTRACT_NAME"));
            productInfo.put("CONTRACT_ID", contractInfo.getString("CONTRACT_ID"));
            productInfo.put("CONTRACT_END_DATE", contractInfo.getString("CONTRACT_END_DATE"));
            productInfo.put("CONTRACT_WRITE_DATE", contractInfo.getString("CONTRACT_WRITE_DATE"));
            productInfo.put("OFFER_IDS", contractInfo.getString("OFFER_IDS"));
            productInfo.put("CONTRACT_IDS", "true");
            setPattrInfo(productInfo);
        }
        String groupId = param.getString("GROUP_ID");
        String productId = param.getString("PRODUCT_ID");
        String templetId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[] { "MINOREC_XN_PRODUCT", productId });
        // 获取虚拟的产品ID
        String xnProductId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "PDATA_ID" }, "DATA_ID", new String[] { "MINOREC_XN_PRODUCT", templetId });
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        String custId = group.getString("CUST_ID");
        param.put("CUST_ID", custId);
        param.put("XN_PRODUCTID", xnProductId);
        IDataset archivesList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryArchivesInfo", param);
        if (IDataUtil.isNotEmpty(archivesList)) {
            String contId = productInfo.getString("CONTRACT_ID", "");
            String contIdVw = productInfo.getString("CONTRACT_ID_VW", "");
            IData archiveData = new DataMap();
            IData archiveDataVw = new DataMap();
            for (Object object : archivesList) {
                IData archiveInfo = (IData) object;
                String archiceContId = archiveInfo.getString("CONTRACT_ID", "");
                if (archiceContId.equals(contId)) {
                    archiveData.put("SERIAL_NUMBER", archiveInfo.getString("SERIAL_NUMBER"));
                    archiveData.put("PRODUCT_ID", xnProductId);
                    archiveData.put("EC_PRODUCT_ID", archiveInfo.getString("PRODUCT_ID"));
                    archiveData.put("CONTRACT_ID", contId);
                    archiveData.put("USER_ID", archiveInfo.getString("USER_ID"));
                }
                if (archiceContId.equals(contIdVw)) {
                    archiveDataVw.put("SERIAL_NUMBER_AS_VW", archiveInfo.getString("SERIAL_NUMBER"));
                    archiveDataVw.put("PRODUCT_ID_AS_VW", xnProductId);
                    archiveDataVw.put("EC_PRODUCT_ID_AS_VW", archiveInfo.getString("PRODUCT_ID"));
                    archiveDataVw.put("CONTRACT_ID_AS_VW", contIdVw);
                    archiveDataVw.put("USER_ID_AS_VW", archiveInfo.getString("USER_ID"));
                }
            }
            archiveData.putAll(archiveDataVw);
            setArchiveData(archiveData);
        }

    }

    /**
     * 展示产品信息
     * 
     * @param
     * @throws Exception
     */
    public void queryOffer(IDataset offerList, String operType) throws Exception {

        IDataset offerCodeList = new DatasetList();
        IDataset delEcOfferList = new DatasetList();// 存放已删除的集团信息
        IData delEcecOffer = new DataMap();// 把已删除的集团信息赋值到界面
        StringBuilder productIdAs = new StringBuilder();
        String offerCodeZ = "";
        String templetId = "";

        for (Object object : offerList) {
            IData offerCInfo = new DataMap();
            IData offerChaSpecInfo = new DataMap();
            IData selGroupOfferInfo = new DataMap();
            IData offerData = (IData) object;
            StringBuilder offerCode = new StringBuilder(offerData.getString("PRODUCT_ID"));
            if (StringUtils.isBlank(productIdAs)) {
                productIdAs = offerCode;
            } else {
                productIdAs = productIdAs.append(",").append(offerCode);
            }
            IData offer = IUpcViewCall.getOfferInfoByOfferCode(offerCode.toString());
            String offerId = IUpcViewCall.getOfferIdByOfferCode(offerCode.toString());// 获取OFFERID
            String brandCode = IUpcViewCall.queryBrandCodeByOfferId(offerId);// 获取品牌

            if (BizCtrlType.MinorecDestroyUser.equals(operType)) {// 判断是否是集团删除
                IData ecOffer = new DataMap();
                ecOffer.put("OFFER_CODE", offerCode.toString());
                ecOffer.put("OFFER_ID", offerId);
                ecOffer.put("OFFER_NAME", offer.getString("OFFER_NAME"));
                ecOffer.put("BRAND_CODE", brandCode);
                ecOffer.put("PRODUCT_ID", offerCode.toString());
                ecOffer.put("OPER_TYPE", operType);
                ecOffer.put("SERIAL_NUMBER", offerData.getString("SERIAL_NUMBER"));
                ecOffer.put("USER_ID", offerData.getData("EC_OFFER").getString("USER_ID"));
                delEcOfferList.add(ecOffer);
            }

            IData mebOffer = offerData.getData("MEB_OFFER");// 拼MEB_SelGroupOffer参数
            if (IDataUtil.isNotEmpty(mebOffer)) {
                IDataset subOffers = mebOffer.getDataset("SUBOFFERS");
                IData memberGroupInfo = new DataMap();
                if (IDataUtil.isNotEmpty(subOffers)) {
                    for (Object object2 : subOffers) {
                        IData subOfferData = (IData) object2;
                        IDataset selOfferList = new DatasetList();
                        String memberGroupId = subOfferData.getString("GROUP_ID");
                        if (StringUtils.isNotBlank(memberGroupId)) {
                            IData selOffer = new DataMap();
                            selOffer.put("OFFER_ID", subOfferData.getString("OFFER_ID"));
                            selOffer.put("OFFER_TYPE", subOfferData.getString("OFFER_TYPE"));
                            selOffer.put("SELECT_FLAG", subOfferData.getString("SELECT_FLAG"));
                            selOfferList.add(selOffer);
                            subOfferData.put("SEL_OFFER", selOfferList);
                            memberGroupInfo.put(memberGroupId, subOfferData);
                        }

                    }
                    selGroupOfferInfo.put("MEB_SelGroupOffer", memberGroupInfo);
                }
            }
            if ("380300".equals(offerCode.toString()) || "380700".equals(offerCode.toString()) || "921015".equals(offerCode.toString())) {
                IData orderSatffInfo = new DataMap();
                orderSatffInfo.put("ORDER_STAFF_ID", offerData.getString("ORDER_STAFF_ID", ""));
                orderSatffInfo.put("ORDER_STAFF_PHONE", offerData.getString("ORDER_STAFF_PHONE", ""));
                offerChaSpecInfo.put("ORDER_SATFF_INFO", orderSatffInfo);// ESP订购员工信息
            }

            offerCInfo.put("OFFER_CODE", offer.getString("OFFER_CODE"));
            offerCInfo.put("OFFER_ID", offerId);
            offerCInfo.put("OFFER_NAME", offer.getString("OFFER_NAME"));
            offerCInfo.put("SERIAL_NUMBER", offerData.getString("SERIAL_NUMBER"));
            offerCInfo.put("BRAND_CODE", brandCode);
            offerCInfo.put("USER_ID", offerData.getData("EC_OFFER").getString("USER_ID"));
            offerCInfo.put("OFFER_MEMBER", offerData.getDataset("OFFER_MEMBER"));// 所有的成员手机号码
            offerCInfo.put("MEB_OFFER", offerData.getData("MEB_OFFER"));// 成员产品资费等信息
            offerCInfo.put("EC_OFFER", offerData.getData("EC_OFFER"));// 集团产品资费等信息
            offerCInfo.put("EC_COMMON_INFO", offerData.getData("EC_COMMON_INFO"));// 账户及付费关系信息
            offerChaSpecInfo.put("EC_OFFER", offerData.getData("EC_OFFER"));// 集团产品资费等信息
            offerChaSpecInfo.put("EC_COMMON_INFO", offerData.getData("EC_COMMON_INFO"));// 账户及付费关系信息
            if ("CrtUser".equals(operType)) {
                offerChaSpecInfo.put("OPER_CODE", "0");
            } else {
                offerChaSpecInfo.put("OPER_CODE", offerData.getString("OPER_CODE"));// 账户及付费关系信息
            }
            offerChaSpecInfo.put("MEB_OFFER", offerData.getData("MEB_OFFER"));// 成员产品资费等信息
            offerChaSpecInfo.put("MEB_FILE", "");// 成员号码
            offerChaSpecInfo.put("MEB_LIST", offerData.getDataset("OFFER_MEMBER"));// 成员号码
            offerChaSpecInfo.put("MEB_COMMON_INFO", offerData.getData("MEB_COMMON_INFO"));// 付费关系等
            if ("DelMeb".equals(operType)) {
                offerCInfo.put("CHILD_DELMEB_DATA", offerData.getDataset("OFFER_MEMBER"));// 成员资费信息
                offerChaSpecInfo.put("MEB_LIST", "");// 成员号码
            }
            offerCInfo.put("CHILD_OFFER_DATA", offerChaSpecInfo);// 账户及付费关系信息
            offerCInfo.put("CHILD_OFFER_SELGROUPOFFER", selGroupOfferInfo);// 成员资费信息
            if ("110000007341".equals(offerId)) {
                IData userInfo = UCAInfoIntfViewUtil.qryUserInfoBySn(this, offerData.getString("SERIAL_NUMBER"));
                changeWideMebrData(userInfo.getString("USER_ID"), operType, offerData.getDataset("OFFER_MEMBER"));
                offerCodeZ = "7341";
                templetId = "ENTERPRISEBROADBANDCHANGE";
            }
            offerCodeList.add(offerCInfo);
        }

        if (IDataUtil.isNotEmpty(delEcOfferList)) {
            delEcecOffer.put("SUB_DELECOFFER_LIST", delEcOfferList);
            setDelEcOffer(delEcecOffer);
        }
        pattrOfferData(productIdAs.toString(), operType, offerCodeZ, templetId, "PC");

        setOfferList(offerCodeList);
    }

    // 根据操作类型，界面展示不同的信息
    public void pattrOfferData(String productIdAs, String operType, String offerCodeZ, String templetId, String flag) throws Exception {

        IData productInfo = new DataMap();
        productInfo.put("PARAM_OFFER", "2");
        productInfo.put("OFFER_CODES", productIdAs);
        if (BizCtrlType.MinorecAddMember.equals(operType)) {
            productInfo.put("ENTERPRISEBROADBAND", offerCodeZ);
            productInfo.put("PARAM_OFFER_TYPE", BizCtrlType.MinorecAddMember);
            productInfo.put("HAS_CHILD", true);
            productInfo.put("ADDMEBSUB", true);
            if ("PHONE".equals(flag)) {
                productInfo.put("PAM_URGENCY_LEVEL", "ADD");
            }
            if (productIdAs.indexOf("7341") >= 0) {
                productInfo.put("WIDEADDMEBSUB_TABLE", true);
                productInfo.put("WIDEPRODUCT", "wideChange");
            }
        } else if (BizCtrlType.MinorecDestroyMember.equals(operType)) {
            productInfo.put("PARAM_OFFER_TYPE", "DelMeb");
            productInfo.put("HAS_CHILD", true);
            productInfo.put("ADDMEBSUB", true);
            if ("PHONE".equals(flag)) {
                productInfo.put("PAM_URGENCY_LEVEL", "DEL");
            }
            if (productIdAs.indexOf("7341") >= 0) {
                productInfo.put("WIDEADDMEBSUB_TABLE", true);
            }
        } else if (BizCtrlType.MinorecCreateUser.equals(operType)) {
            productInfo.put("PARAM_OFFER_TYPE", "CrtUser");
            productInfo.put("HAS_CHILD", false);
        } else if (BizCtrlType.MinorecDestroyUser.equals(operType)) {
            productInfo.put("PARAM_OFFER_TYPE", "DstUser");
            productInfo.put("WIDEADDMEBSUB_TABLE", false);
            productInfo.put("ADDMEBSUB", false);

        } else if (BizCtrlType.MinorecChangeWideNet.equals(operType)) {
            productInfo.put("PARAM_OFFER_TYPE", "ChgWn");
            productInfo.put("ADDMEBSUB", false);
            productInfo.put("WIDEADDMEBSUB_TABLE", true);
            productInfo.put("WIDEPRODUCT", "wideChange");
        }
        if ("ENTERPRISEBROADBANDCHANGE".equals(templetId)) {
            productInfo.put("PAM_URGENCY_LEVEL", "DSB");
        }

        setOperTypeMap(productInfo);
    }

    // 获取宽带产品的开通参数
    public void changeWideMebrData(String userId, String operType, IDataset offerMeb) throws Exception {
        StringBuilder address = new StringBuilder();// 所有已开通 的地址，方便新增成员校验
        if (IDataUtil.isNotEmpty(offerMeb)) {
            IData wideInfo = new DataMap();
            IDataset wideDelMberList = new DatasetList();
            wideInfo = offerMeb.first();// 界面展示宽带的公共信息，只需要展示一个，所有公共信息都一样
            for (Object object2 : offerMeb) {
                IData userWideInfo = (IData) object2;
                userWideInfo.put("USER_ID", userWideInfo.getString("MEB_USER_ID"));
                userWideInfo.put("RSRV_NUM1", userWideInfo.getString("DEVICE_ID"));
                userWideInfo.put("RSRV_STR4", userWideInfo.getString("AREA_CODE"));
                StringBuilder userAddress = new StringBuilder(userWideInfo.getString("STAND_ADDRESS"));
                if (StringUtils.isNotBlank(address)) {
                    address = userAddress.append(",").append(address);
                } else {
                    address = userAddress;
                }

                if (BizCtrlType.MinorecDestroyMember.equals(operType)) {
                    IData wideDelMberInfo = new DataMap();
                    wideDelMberInfo.put("WIDE_PRODUCT_ID", userWideInfo.getString("WIDE_PRODUCT_ID"));
                    wideDelMberInfo.put("WIDE_PRODUCT_NAME", userWideInfo.getString("WIDE_PRODUCT_NAME"));
                    wideDelMberInfo.put("USER_ID", userWideInfo.getString("MEB_USER_ID"));
                    wideDelMberInfo.put("WIDE_SERIAL_NUMBER", userWideInfo.getString("WIDE_SERIAL_NUMBER"));
                    wideDelMberInfo.put("STAND_ADDRESS", userWideInfo.getString("STAND_ADDRESS"));
                    wideDelMberInfo.put("RSRV_NUM1", userWideInfo.getString("DEVICE_ID"));
                    wideDelMberInfo.put("RSRV_STR4", userWideInfo.getString("AREA_CODE"));
                    wideDelMberList.add(wideDelMberInfo);
                }

            }
            String wideProductId = "";
            if (IDataUtil.isNotEmpty(offerMeb)) {
                wideProductId = offerMeb.first().getString("PRODUCT_ID");
                wideInfo.put("PRODUCT_ID", wideProductId);
            }
            if (BizCtrlType.MinorecAddMember.equals(operType)) {
                String wideTypeName = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "WIDE_PRODUCT_TYPE", wideInfo.getString("WIDE_PRODUCT_TYPE") });
                wideInfo.put("WIDE_ADDERSS", address.toString());
                changeWideProductType(wideTypeName, wideInfo.getString("EC_SERIAL_NUMBER"));// 获取宽带产品
                setWidenetInfos(offerMeb);
                setWideInfo(wideInfo);
                mergeWideUserStyleCheck("7341");// 获取宽带初始化值
                changeSaleActiveList(offerMeb.first().getString("WIDE_PRODUCT_ID"));// 获取宽带资费信息
            } else if (BizCtrlType.MinorecDestroyMember.equals(operType)) {
                IDataset memInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(this, userId, "MR");// 获取UU关系表，集团下订购了多少个成员
                IDataset widenetTable = new DatasetList();
                if (IDataUtil.isNotEmpty(memInfos)) {
                    IData data = new DataMap();
                    for (Object object : memInfos) {
                        IData memberUserInfo = (IData) object;
                        String userIdB = memberUserInfo.getString("USER_ID_B");
                        String serialNumberB = memberUserInfo.getString("SERIAL_NUMBER_B");
                        data.put("USER_ID", userIdB);
                        data.put("ROUTE_EPARCHY_CODE", "0898");
                        IDataset userWideList = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);// 查询宽带信息
                        if (IDataUtil.isNotEmpty(userWideList)) {
                            for (Object object2 : userWideList) {
                                IData userWideInfo = (IData) object2;
                                IData wideMebInfo = new DataMap();
                                // 查询成员产品信息
                                IDataset mebProductList = CSViewCall.call(this, "CS.UserProductInfoQrySVC.queryUserMainProductByUserId", data);
                                if (IDataUtil.isNotEmpty(mebProductList)) {
                                    wideMebInfo.put("WIDE_PRODUCT_ID", mebProductList.first().getString("PRODUCT_ID"));
                                    wideMebInfo.put("WIDE_PRODUCT_NAME", mebProductList.first().getString("RSRV_STR5"));
                                }
                                wideMebInfo.put("USER_ID", userIdB);
                                wideMebInfo.put("WIDE_SERIAL_NUMBER", serialNumberB);
                                wideMebInfo.put("STAND_ADDRESS", userWideInfo.getString("STAND_ADDRESS"));
                                wideMebInfo.put("RSRV_NUM1", userWideInfo.getString("RSRV_NUM1"));
                                wideMebInfo.put("RSRV_STR4", userWideInfo.getString("RSRV_STR4"));
                                widenetTable.add(wideMebInfo);
                            }
                        }
                    }
                }

                Iterator<Object> wideTabInfo = widenetTable.iterator();
                while (wideTabInfo.hasNext()) {
                    IData userData = (IData) wideTabInfo.next();
                    String wideSerialNumber = userData.getString("WIDE_SERIAL_NUMBER");
                    for (Object object3 : offerMeb) {
                        IData offerInfos = (IData) object3;
                        String serNumber = offerInfos.getString("WIDE_SERIAL_NUMBER");
                        if (wideSerialNumber.equals(serNumber)) {
                            wideTabInfo.remove();
                        }
                    }
                }
                setWidenetInfos(widenetTable);
                IData wideDelMberInfo = new DataMap();
                wideDelMberInfo.put("WIDE_DELMBR_LIST", wideDelMberList);
                setWideInfo(wideDelMberInfo);
            } else if (BizCtrlType.MinorecChangeWideNet.equals(operType)) {
                String wideTypeName = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "WIDE_PRODUCT_TYPE", wideInfo.getString("WIDE_PRODUCT_TYPE") });
                changeWideProductType(wideTypeName, offerMeb.first().getString("EC_SERIAL_NUMBER"));// 获取宽带产品
                setWideInfo(wideInfo);
                setWidenetInfos(offerMeb);
                changeSaleActiveList(offerMeb.first().getString("WIDE_PRODUCT_ID"));// 获取宽带资费信息

            }

        }
    }

    public void setOperType(IRequestCycle cycle) throws Exception {
        IData pageData = getData();

        IData dataOfferCode = new DataMap(pageData.getString("DATE_OFFERCODE"));// 通过选择合同获取的offerCode
        IData contractInfo = new DataMap();
        contractInfo.put("CONTRACT_NAME", dataOfferCode.getString("CONTRACT_NAME"));
        contractInfo.put("CONTRACT_ID", dataOfferCode.getString("CONTRACT_ID"));
        contractInfo.put("CONTRACT_END_DATE", dataOfferCode.getString("CONTRACT_END_DATE"));
        contractInfo.put("CONTRACT_WRITE_DATE", dataOfferCode.getString("CONTRACT_WRITE_DATE"));
        contractInfo.put("OFFER_IDS", dataOfferCode.getString("OFFER_IDS"));

        if ("8000".equals(dataOfferCode.getString("OFFER_IDS"))) {// 回显集团V网的电子协议信息
            contractInfo.put("CONTRACT_NAME_VW", dataOfferCode.getString("CONTRACT_NAME"));
            contractInfo.put("CONTRACT_ID_VW", dataOfferCode.getString("CONTRACT_ID"));
            contractInfo.put("CONTRACT_END_DATE_VW", dataOfferCode.getString("CONTRACT_END_DATE"));
            contractInfo.put("CONTRACT_WRITE_DATE_VW", dataOfferCode.getString("CONTRACT_WRITE_DATE"));
            contractInfo.put("OFFER_IDS_VW", dataOfferCode.getString("OFFER_IDS"));
            contractInfo.put("GROUP_VW", "true");
        } else {// 回显其他产品的电子协议信息
            contractInfo.put("CONTRACT_NAME", dataOfferCode.getString("CONTRACT_NAME"));
            contractInfo.put("CONTRACT_ID", dataOfferCode.getString("CONTRACT_ID"));
            contractInfo.put("CONTRACT_END_DATE", dataOfferCode.getString("CONTRACT_END_DATE"));
            contractInfo.put("CONTRACT_WRITE_DATE", dataOfferCode.getString("CONTRACT_WRITE_DATE"));
            contractInfo.put("OFFER_IDS", dataOfferCode.getString("OFFER_IDS"));
        }
        setPattrInfo(contractInfo);
    }

    public int[] intOfferCodeSort(String[] offerCodeInfo) throws Exception {

        int intOfferCode[] = new int[offerCodeInfo.length];
        for (int i = 0; i < offerCodeInfo.length; i++) {
            intOfferCode[i] = Integer.parseInt(offerCodeInfo[i]);
        }
        for (int i = 0; i < intOfferCode.length - 1; i++) {
            for (int j = 0; j < intOfferCode.length - 1 - i; j++) {
                if (intOfferCode[j] > intOfferCode[j + 1]) {
                    int temp = intOfferCode[j];
                    intOfferCode[j] = intOfferCode[j + 1];
                    intOfferCode[j + 1] = temp;
                }
            }
        }

        return intOfferCode;

    }

    public void changeSaleActiveList(String productId) throws Exception {

        IData map = new DataMap();
        map.put("NEW_PRODUCT_ID", productId);
        map.put("ROUTE_EPARCHY_CODE", "0898");
        IDataset dataset = CSViewCall.call(this, "CS.SelectedElementSVC.getWidenetUserOpenElements", map);
        if (IDataUtil.isNotEmpty(dataset)) {
            IData elementInfo = new DataMap();
            IDataset selectedElements = new DatasetList(dataset.getData(0).getString("SELECTED_ELEMENTS"));
            // 写死资费属性
            IDataset saleActiveInfos = new DatasetList();
            IData saleActiveInfo = new DataMap();

            saleActiveInfo.put("ELEMENT_ID", "20001088");
            saleActiveInfo.put("ELEMENT_TYPE_CODE", "D");
            saleActiveInfo.put("PRODUCT_ID", "20150150");
            saleActiveInfo.put("PACKAGE_ID", "20150153");
            saleActiveInfo.put("MODIFY_TAG", "0");
            saleActiveInfo.put("START_DATE", SysDateMgr.getSysDate());
            saleActiveInfo.put("END_DATE", "2050-12-31 00:00:00 ");
            saleActiveInfo.put("INST_ID", "");
            saleActiveInfo.put("ELEMENT_NAME", "宽带套餐(0元包月-集团商务宽带使用)");
            saleActiveInfos.add(saleActiveInfo);
            selectedElements.addAll(saleActiveInfos);
            elementInfo.put("SELECTED_ELEMENTS", selectedElements);
            setSaleActiveListAttr(saleActiveInfos);
            setSaleActiveList(elementInfo);
        }

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

    /**
     * 获取宽带产品
     * 
     * @param taosx
     * @throws Exception
     */
    public void changeWideProductType(String openType, String serNumber) throws Exception {

        IData data = new DataMap();
        data.put("ROUTE_EPARCHY_CODE", "0898");
        data.put("FLAG", "MINOREC");
        data.put("SERIAL_NUMBER", serNumber);
        if ("铁通ADSL".equals(openType)) {
            data.put("wideProductType", "2");
        } else if ("移动FTTH".equals(openType) || "铁通FTTH".equals(openType)) {
            data.put("wideProductType", "5");
        } else if ("移动FTTB".equals(openType) || "铁通FTTB".equals(openType)) {
            data.put("wideProductType", "6");
        }
        IDataset dataset = CSViewCall.call(this, "SS.MergeWideUserCreateSVC.getWidenetProductInfoByWideType", data);
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), dataset);

        setProductList(dataset);
    }

    /**
     * 获取支付方式等
     * 
     * @param taosx
     * @throws Exception
     */
    public void mergeWideUserStyleCheck(String offerCodeZ) throws Exception {

        // 付费模式权限控制
        IDataset widenetPayMode = StaticUtil.getStaticList("WIDENET_PAY_MODE");
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "WIDENET_PAY_MODE")) {
            // log.info("("*******cxy******FTTH_FREE_RIGHT="+StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT"));
            if (IDataUtil.isNotEmpty(widenetPayMode)) {
                for (int k = 0; k < widenetPayMode.size(); k++) {
                    if ("A".equals(widenetPayMode.getData(k).getString("DATA_ID"))) {
                        widenetPayMode.remove(k);
                        break;
                    }
                }
            }
        }

        // 宽带开户方式权限控制
        IDataset mergeWideUserStyleInfos = StaticUtil.getStaticList("HGS_WIDE");
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "HGS_WIDE")) {
            // log.info("("*******cxy******FTTH_FREE_RIGHT="+StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT"));
            if (IDataUtil.isNotEmpty(mergeWideUserStyleInfos)) {
                for (int k = 0; k < mergeWideUserStyleInfos.size(); k++) {
                    if ("1".equals(mergeWideUserStyleInfos.getData(k).getString("DATA_ID"))) {
                        mergeWideUserStyleInfos.remove(k);
                        break;
                    }
                }
            }
        }

        setMergeWideUserStyleList(mergeWideUserStyleInfos);
        setWidenetPayModeList(widenetPayMode);
    }

    /**
     * 宽带产品资费属性，获取必选的元素
     * 
     * @param taosx
     * @throws Exception
     * @author yuyj3
     */
    public void getWidenetUserOpenElement(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData map = new DataMap();
        map.put("NEW_PRODUCT_ID", data.getString("PRODUCT_ID"));
        map.put("ROUTE_EPARCHY_CODE", "0898");

        IDataset dataset = CSViewCall.call(this, "CS.SelectedElementSVC.getWidenetUserOpenElements", map);

        if (IDataUtil.isNotEmpty(dataset)) {
            IData elementInfo = new DataMap();
            IDataset selectedElements = new DatasetList(dataset.getData(0).getString("SELECTED_ELEMENTS"));
            // 写死资费属性
            IDataset saleActiveInfos = new DatasetList();
            IData saleActiveInfo = new DataMap();

            saleActiveInfo.put("ELEMENT_ID", "20001088");
            saleActiveInfo.put("ELEMENT_TYPE_CODE", "D");
            saleActiveInfo.put("PRODUCT_ID", "20150150");
            saleActiveInfo.put("PACKAGE_ID", "20150153");
            saleActiveInfo.put("MODIFY_TAG", "0");
            saleActiveInfo.put("START_DATE", SysDateMgr.getSysDate());
            saleActiveInfo.put("END_DATE", "2050-12-31 00:00:00 ");
            saleActiveInfo.put("INST_ID", "");
            saleActiveInfo.put("ELEMENT_NAME", "宽带套餐(0元包月-集团商务宽带使用)");
            saleActiveInfos.add(saleActiveInfo);
            selectedElements.addAll(saleActiveInfos);
            elementInfo.put("SELECTED_ELEMENTS", selectedElements);
            setSaleActiveListAttr(saleActiveInfos);
            setSaleActiveList(elementInfo);
        }

    }

    public void initOfferCha(IRequestCycle cycle) throws Exception {
        String offerId = getData().getString("OFFER_ID");
        String ecMebType = getData().getString("EC_MEB_TYPE");
        String operCode = getData().getString("OPER_CODE");

        String operType = PageDataTrans.transOperCodeToOperType(operCode, ecMebType);

        IData offerInfo = IUpcViewCall.queryOfferByOfferId(offerId, UpcConst.QUERY_COM_CHA_YES);
        if (IDataUtil.isEmpty(offerInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据OFFER_ID" + offerId + "没有查询到商品信息！");
        }

        String ifCentreType = getData().getString("IF_CENTRETYPE", "");
        if ("2".equals(ifCentreType)) {// 8000产品即可为普通v网也可为融合V网，两者参数不一样
                                       // 如果是融合V网，为了跟普通V网区分，加Centre
            operType = "Centre" + operType;
        }

        IData inAttr = new DataMap();
        inAttr.put("FLOW_ID", offerId); // POINT_ONE
        inAttr.put("NODE_ID", operType); // POINT_TWO

        if ("BOSG".equals(offerInfo.getString("BRAND_CODE"))) {
            // BBOSS本地产品编码转换为全网产品编码
            IData input = new DataMap();
            String merchOperType = this.getData().getString("MERCHP_OPER_TYPE");
            input.put("OPER_TYPE", inAttr.getString("NODE_ID"));
            input.put("PROD_SPEC_ID", offerInfo.getString("OFFER_CODE"));
            input.put("MERCHP_OPER_TYPE", merchOperType);
            FrontProdConverter.prodConverter(this, input, false);

            // 操作类型转换为全网操作类型
            inAttr.put("FLOW_ID", input.getString("PROD_SPEC_ID"));// POINT_ONE
            inAttr.put("NODE_ID", input.getString("OPER_TYPE"));// POINT_TWO
        }

        setInAttr(inAttr);

        if ("EC".equals(ecMebType)) {
            queryEcOfferChaValue(getData(), operType, offerInfo);
        } else if ("MEB".equals(ecMebType)) {
            queryMebOfferChaValue(getData(), operType, offerInfo);
        }
    }

    private void queryEcOfferChaValue(IData pageData, String operType, IData curOffer) throws Exception {
        String ecOfferCode = pageData.getString("EC_OFFER_CODE");
        String offerCode = pageData.getString("OFFER_CODE");

        String curOfferCode = curOffer.getString("OFFER_CODE"); // 当前设置属性的商品
        String curOfferType = curOffer.getString("OFFER_TYPE"); // 当前设置属性的商品
        String brandCode = curOffer.getString("BRAND_CODE");

        String idType = "S";
        if (((curOfferCode.equals(offerCode)) && !("S".equals(curOfferType))) || "BOSG".equals(brandCode)) {
            idType = "P";
        }
        String svcName = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, idType, operType, "InitOfferCha");

        IDataset grpItemInfo = new DatasetList();
        if (StringUtils.isBlank(svcName)) {// 没有配置，取默认服务初始化
            if (BizCtrlType.CreateUser.equals(operType)) {
                svcName = "SS.QueryAttrParamSVC.queryOfferChaForInit";
            } else if (BizCtrlType.ChangeUserDis.equals(operType)) {
                svcName = "SS.QueryAttrParamSVC.queryUserAttrForChgInit";
            } else {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作类型异常！OPER_TYPE=" + operType);
            }
        }

        String offerId = pageData.getString("OFFER_ID");
        String offerType = pageData.getString("OFFER_TYPE");
        String custId = pageData.getString("CUST_ID");
        IData busi = new DataMap();
        IData input = new DataMap();
        input.put("OFFER_ID", offerId);
        input.put("ATTR_OBJ", "0");
        input.put("EPARCHY_CODE", getTradeEparchyCode());
        // input.put("USER_ID", subscriberInsId);
        // input.put("OFFER_INS_ID", offerInsId);
        input.put("INST_TYPE", offerType);// USER_ATTR表中的INST_TYPE属性
        input.put("IS_MEB", "false");
        input.put("CUST_ID", custId);
        input.put("PRODUCT_ID", offerCode);
        input.put("OFFER_CODE", ecOfferCode);// 方便ADC对特殊产品进行判断,对逻辑无影响
        input.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset result = CSViewCall.call(this, svcName, input);
        if (IDataUtil.isNotEmpty(result)) {
            IData groupParamData = result.getData(0);
            IData attrGroupData = groupParamData.getData("ATTR_GROUP_MAP");
            if ("BOSG".equals(brandCode) && IDataUtil.isNotEmpty(attrGroupData)) {
                if (IDataUtil.isNotEmpty(attrGroupData)) {
                    Iterator itr = attrGroupData.keySet().iterator();
                    while (itr.hasNext()) {
                        String key = itr.next().toString();
                        IDataset attrInfos = attrGroupData.getDataset(key);
                        for (int i = 0; i < attrInfos.size(); i++) {
                            IData attrInfo = attrInfos.getData(i);
                            IData paramInfo = new DataMap();

                            paramInfo.put("CHA_SPEC_ID", attrInfo.getString("FIELD_NAME").split("_")[0]);
                            paramInfo.put("ATTR_VALUE", attrInfo.getString("ATTR_VALUE"));
                            paramInfo.put("ATTR_GROUP", attrInfo.getString("ATTR_GROUP"));
                            paramInfo.put("ATTR_CODE", attrInfo.getString("FIELD_NAME").split("_")[0]);

                            grpItemInfo.add(paramInfo);
                        }
                    }
                }
            } else {
                IData param = result.getData(0);
                Iterator itr = param.keySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().toString();
                    if (key.contains("_")) {
                        String[] keys = key.split("♂♂");
                        if (keys.length > 1) {
                            key = keys[0];
                        }
                    }
                    IData paramData = param.getData(key);
                    String value = param.getString(key);
                    // 针对属性组的情况进行特殊处理
                    if (value.contains("♂♂")) {
                        value = paramData.getString("VALUE");
                        String[] groupItem = value.split("♂♂");
                        if (groupItem.length > 1) {
                            value = groupItem[0];
                            String groupAttr = groupItem[1];
                            IData paramInfo = new DataMap();
                            paramInfo.put("CHA_SPEC_ID", key);
                            paramInfo.put("CHA_VALUE", value);
                            paramInfo.put("GROUP_ATTR", groupAttr);
                            grpItemInfo.add(paramInfo);
                        }
                    }
                    busi.put(key, paramData);
                }
            }
        }
        setBusi(busi);
    }

    private void queryMebOfferChaValue(IData pageData, String operType, IData curOffer) throws Exception {
        String ecOfferCode = pageData.getString("EC_OFFER_CODE");
        String offerId = pageData.getString("OFFER_ID");
        String offerCode = pageData.getString("OFFER_CODE");
        String brandCode = pageData.getString("BRAND_CODE");

        // BBoss 静态表加载的数据 通过 产品编码 拿到商品编码
        if ("BOSG".equals(brandCode)) {
            IDataset upOfferIdList = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(null, offerId, "4");
            if (IDataUtil.isNotEmpty(upOfferIdList)) {
                String upOfferId = upOfferIdList.first().getString("OFFER_ID");
                offerCode = IUpcViewCall.getOfferCodeByOfferId(upOfferId);
            }
        }

        // 初始化产品特征(非静态表加载的数据)
        // 存在既有特殊产品参数又有服务参数的情况,产品参数配置P,服务参数配置S,通过当前设置的商品类型来做区分
        String idType = "S".equals(curOffer.getString("OFFER_TYPE")) ? "S" : "P";
        String svcName = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, idType, operType, "InitOfferCha");
        if (StringUtils.isBlank(svcName)) {// 没有配置，取默认服务初始化
            if (BizCtrlType.CreateMember.equals(operType)) {
                svcName = "SS.QueryAttrParamSVC.queryOfferChaForInit";
            } else if (BizCtrlType.ChangeMemberDis.equals(operType)) {
                svcName = "SS.QueryAttrParamSVC.queryUserAttrForChgInit";
            } else {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作类型异常！OPER_TYPE=" + operType);
            }
        }

        IData busi = new DataMap();
        IData input = new DataMap();
        input.put("EC_OFFER_ID", pageData.getString("EC_OFFER_ID")); // 集团主商品编码
        input.put("OFFER_ID", offerId);
        input.put("OFFER_CODE", ecOfferCode);
        input.put("ATTR_OBJ", "1"); // 成员是1
        // input.put("EPARCHY_CODE", userEparchyCode);
        input.put("MEM_OFFER_ID", pageData.getString("MEB_OFFER_ID"));
        // input.put("USER_ID", subscriberInsId);
        // input.put("EC_USER_ID", ecSubscriberInsId);
        input.put("CUST_ID", pageData.getString("CUST_ID"));
        // input.put("OFFER_INS_ID", pageData.getString("OFFER_INS_ID"));
        input.put("INST_TYPE", pageData.getString("OFFER_TYPE"));// USER_ATTR表中的INST_TYPE属性
        input.put("IS_MEB", true);
        input.put("GROUP_ID", pageData.getString("GROUP_ID"));
        input.put("SUB_OFFER_CODE", offerCode);
        input.put(Route.ROUTE_EPARCHY_CODE, "0898");
        input.put("OPER_TYPE", operType);
        input.put("EC_OFFER_CODE", ecOfferCode);
        input.put("EcIntegrateOrder", true);
        IDataset result = CSViewCall.call(this, svcName, input);

        if (IDataUtil.isNotEmpty(result)) {
            IData param = result.getData(0);
            Iterator itr = param.keySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().toString();
                IData paramData = param.getData(key);

                busi.put(key, paramData);
            }
        }
        setBusi(busi);
    }

    // 删除宽带成员
    public void removeaddMebSub(IRequestCycle cycle) throws Exception {

        IData pageData = getData();
        IDataset addressList = new DatasetList(pageData.getString("DETAIL_ADDRESS_LIST"));// 删除的数据
        IDataset addressList1 = new DatasetList(pageData.getString("DETAIL_ADDRESS_LIST1"));// 表格里面存在的地址信息

        Iterator<Object> addressInfo = addressList1.iterator();
        while (addressInfo.hasNext()) {
            IData userData = (IData) addressInfo.next();
            String standAddress1 = userData.getString("STAND_ADDRESS");
            for (Object object : addressList) {
                IData addressIf = (IData) object;
                String standAddress = addressIf.getString("STAND_ADDRESS");
                if (standAddress.equals(standAddress1)) {
                    addressInfo.remove();
                    break;
                }
            }
        }
        setWidenetInfos(addressList1);
    }

    @Override
    public void buildOtherSvcParam(IData submmitParam) throws Exception {
        String operType = submmitParam.getString("OPER_TYPE");
        if (BizCtrlType.MinorecAddMember.equals(operType) || BizCtrlType.MinorecCreateUser.equals(operType) || BizCtrlType.MinorecChangeWideNet.equals(operType) || BizCtrlType.MinorecDestroyMember.equals(operType)) {
            MinorecIntegrateTrans.transformCreateHotelIntegrationSubmitData(submmitParam);
        } else if (BizCtrlType.MinorecDestroyUser.equals(operType)) {
            // 删除集团
            MinorecIntegrateTrans.transformDstSubByChangeApply(submmitParam);
        }

    }

    public abstract void setContractInfo(IData contractInfo) throws Exception;

    public abstract void setWideInfo(IData wideInfo) throws Exception;

    public abstract void setCondition(IData cond) throws Exception;

    public abstract void setSaleActiveList(IData saleActiveList) throws Exception;

    public abstract void setSaleActiveListAttr(IDataset saleActiveListAttr) throws Exception;

    public abstract void setMergeWideUserStyleList(IDataset mergeWideUserStyleList) throws Exception;

    public abstract void setWidenetPayModeList(IDataset widenetPayModeList) throws Exception;

    public abstract void setProductList(IDataset productList) throws Exception;

    public abstract void setGroupInfo(IData groupInfo) throws Exception;

    public abstract void setCustMgrInfo(IData custMgrInfo) throws Exception;

    public abstract void setOffer(IData offer) throws Exception;

    public abstract void setInfo(IData info) throws Exception;

    public abstract void setInfos(IDataset infos) throws Exception;

    public abstract void setComminfo(IData comminfo) throws Exception;

    public abstract void setBusi(IData busi) throws Exception;

    public abstract void setPattrInfo(IData pattrInfo) throws Exception;

    public abstract void setOfferCodeList(IDataset offerCodeList) throws Exception;

    public abstract void setWidenetInfos(IDataset widenetInfos) throws Exception;

    public abstract void setEcAccountList(IDataset ecAccountList) throws Exception;

    public abstract void setInAttr(IData inAttr) throws Exception;

    public abstract void setOperTypeMap(IData operTypeMap) throws Exception;

    public abstract void setOperTypeList(IDataset operTypeList) throws Exception;

    public abstract void setOfferList(IDataset operTypeList) throws Exception;

    public abstract void setArchiveData(IData archiveData) throws Exception;

    public abstract void setDelEcOffer(IData delEcecOffer) throws Exception;

}
