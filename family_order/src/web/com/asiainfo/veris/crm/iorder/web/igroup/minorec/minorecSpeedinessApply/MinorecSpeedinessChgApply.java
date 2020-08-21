package com.asiainfo.veris.crm.iorder.web.igroup.minorec.minorecSpeedinessApply;

import java.util.Iterator;

import com.ailk.common.data.IDataOutput;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.FrontProdConverter;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.dataTrans.MinorecIntegrateTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

public abstract class MinorecSpeedinessChgApply extends EopBasePage {

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void initPage(IRequestCycle cycle) throws Exception {

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
     * 通过客户名称模糊查询集团客户信息
     * @param cycle
     * @throws Exception
     */
    public void queryCustGroupByCustName(IRequestCycle cycle) throws Exception
    {
        String custName = getData().getString("CUST_NAME");

        IDataOutput output = UCAInfoIntfViewUtil.qryGrpCustInfoByCustName(this, custName, getPagination());
        IDataset datas  = output.getData();

        if(IDataUtil.isEmpty(datas))
        {
            CSViewException.apperr(GrpException.CRM_GRP_1, custName);
        }
        setCustGroupList(datas);

    }


    public void setOperType(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        String flag = pageData.getString("FLAG");// 登陆标记，电脑还是手机端
        String operType = pageData.getString("OPER_TYPE");// 操作类型
        String groupId = pageData.getString("GROUP_ID");// 集团编码
        IData dataOfferCode = new DataMap(getData().getString("DATE_OFFERCODE"));// 通过选择合同获取的offerCode
        String userIdAs = pageData.getString("USER_ID_AS");// 已有协议的所有userId
        String productIdAs = pageData.getString("PRODUCT_ID_AS");// 已有协议的所有产品ID
        String offerCodes = pageData.getString("CONTRACT_PRODUCT_ID");// 合同传过来的产品ID
        String templetId = pageData.getString("TEMPLET_ID");// 流程ID
        String templetName = StaticUtil.getStaticValue("MINOREC_BPM_TEPMENTID_CHANGE", templetId);// 流程名
        IDataset offerCodeList = new DatasetList();
        String[] offerCodeInfo = offerCodes.split(",");
        String[] userIdAsInfo = userIdAs.split(",");
        String[] productIdAsInfo = productIdAs.split(",");
        String offerCode = "";
        String offerCodeZ = "";

        if (BizCtrlType.MinorecCreateUser.equals(operType) && offerCodes.indexOf("8001") < 0) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您电子协议未勾选融合V网产品，请勾选！");
        }

        // 冒泡排序,把多媒体桌面电话放到V网前面
        int[] intOfferCode = intOfferCodeSort(offerCodeInfo);

        for (int i = 0; i < intOfferCode.length; i++) {
            offerCode = Integer.toString(intOfferCode[i]);
            String userId = "";
            for (int j = 0; j < productIdAsInfo.length; j++) {// 通过已有协议，获取产品的userId
                String offerCodeAs = productIdAsInfo[j];
                if (offerCode.equals(offerCodeAs)) {
                    userId = userIdAsInfo[j];
                }
            }
            if (!BizCtrlType.MinorecCreateUser.equals(operType) && StringUtils.isBlank(userId)) {
                IData offer = IUpcViewCall.getOfferInfoByOfferCode(offerCode);
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您选择的操作类型不是新增集团，在协议里面不能勾选" + offer.getString("OFFER_NAME") + "产品进行新增！");
            } else if (BizCtrlType.MinorecCreateUser.equals(operType) && !"8001".equals(offerCode) && StringUtils.isBlank(userId)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "新增集团只能新增融合V网，不允许新增其他集团产品！");

            }

            if (BizCtrlType.MinorecChangeWideNet.equals(operType) && "8000".equals(offerCode)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您选择的是宽带变更业务，不能选择集团V网协议，请修改！");
            }

            if (BizCtrlType.MinorecCreateUser.equals(operType) && productIdAs.indexOf(offerCode) >= 0 && !"2222".equals(offerCode)) {
                IData offer = IUpcViewCall.getOfferInfoByOfferCode(offerCode);
                String offerName = offer.getString("OFFER_NAME");
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "新增集团只能新增融合V网，不能新增【" + offerName + "】产品，或该集团已经订单融合V网，请修改！");
            }

            if (BizCtrlType.MinorecDestroyMember.equals(operType) && "7341".equals(offerCode)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "暂不支持商务宽带的成员删除，请修改协议！");
            }

            if ("7341".equals(offerCode) && StringUtils.isNotBlank(operType)) {
                offerCodeZ = offerCode;
                mergeWideUserStyleCheck(offerCodeZ);// 获取宽带初始化值
            }

            IData grpUserProduct = new DataMap();
            if (StringUtils.isBlank(userId) && BizCtrlType.MinorecCreateUser.equals(operType)) {
                IData offer = IUpcViewCall.getOfferInfoByOfferCode(offerCode);
                grpUserProduct.put("OFFER_CODE", offer.getString("OFFER_CODE"));
                grpUserProduct.put("OFFER_ID", offer.getString("OFFER_ID"));
                grpUserProduct.put("OFFER_NAME", offer.getString("OFFER_NAME"));
                grpUserProduct.put("OFFER_OPER_TYPE", "CrtUser");
                offerCodeList.add(grpUserProduct);

                if ("8001".equals(offerCode) && productIdAs.indexOf("2222") < 0) {// 判断该集团是否已订购多媒体桌面电话
                    IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
                    String custId = result.getString("CUST_ID", "");
                    IData checkParam = new DataMap();
                    checkParam.put("CUST_ID", custId);
                    checkParam.put("PRODUCT_ID", "2222");
                    IDataset results = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getGrpProductByGrpCustIdProID", checkParam);
                    if (IDataUtil.isEmpty(results) && results.size() <= 0) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "集团客户未订购多媒体桌面电话,请先订购多媒体桌面电话或同时受理多媒体桌面电话及融合V网!");
                    }
                }
            } else if (StringUtils.isNotBlank(userId) && BizCtrlType.MinorecCreateUser.equals(operType)) {
                continue;
            } else {
                if ("DstUser".equals(operType)) {// 注销集团先查询集团下是否还存在有效的成员，存在成员，不展示集团产品信息
                    IData data = new DataMap();
                    IDataset reluuList = null;
                    // ESP产品查询成员需查BB表
                    if (offerCodes.indexOf("380300") >= 0 || offerCodes.indexOf("380700") >= 0) {
                        data.put("USER_ID_A", userId);
                        reluuList = CSViewCall.call(this, "CS.RelaBBInfoQrySVC.getAllMebByUSERIDA", data);
                    } else {
                        data.put("USER_ID", userId);
                        reluuList = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.qryUUInfoAllCrmByUserIdA", data);
                    }
                    if (IDataUtil.isNotEmpty(reluuList)) {// 如果存在成员，不展示产品删除
                        continue;
                    }
                }
                grpUserProduct = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userId);
                IData offer = IUpcViewCall.getOfferInfoByOfferCode(grpUserProduct.getString("PRODUCT_ID"));
                grpUserProduct.put("OFFER_CODE", offer.getString("OFFER_CODE"));
                grpUserProduct.put("OFFER_ID", offer.getString("OFFER_ID"));
                grpUserProduct.put("OFFER_NAME", offer.getString("OFFER_NAME"));
                IData childOfferData = new DataMap();
                IData ecOffer = new DataMap();
                ecOffer.put("PRODUCT_ID", grpUserProduct.getString("PRODUCT_ID"));
                ecOffer.put("PRODUCT_NAME", grpUserProduct.getString("PRODUCT_NAME"));
                ecOffer.put("OFFER_CODE", grpUserProduct.getString("OFFER_CODE"));
                ecOffer.put("OFFER_ID", grpUserProduct.getString("OFFER_ID"));
                ecOffer.put("OFFER_NAME", grpUserProduct.getString("OFFER_NAME"));
                ecOffer.put("OFFER_TYPE", "P");
                ecOffer.put("USER_ID", grpUserProduct.getString("USER_ID"));
                ecOffer.put("BRAND_CODE", grpUserProduct.getString("BRAND_CODE"));
                ecOffer.put("SERIAL_NUMBER", grpUserProduct.getString("SERIAL_NUMBER"));
                childOfferData.put("EC_OFFER", ecOffer);
                grpUserProduct.put("CHILD_OFFER_DATA", childOfferData);
                offerCodeList.add(grpUserProduct);

                changeMebrData(offerCode, userId, operType, grpUserProduct);
            }

        }

        if ("DstUser".equals(operType) && IDataUtil.isEmpty(offerCodeList)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "您选择的流程产品，没有可以注销的集团产品，注销集团产品，需要先删除该集团下的所有成员！");

        }
        if ("FUSECOMMUNICATIONCHANGE".equals(templetId)) {
            offerCode = "VP998001";
        } else if ("YIDANQINGSHANGPUCHANGE".equals(templetId) || "8000".equals(offerCode)) {
            offerCode = "VP99999";
        } else if ("YIDANQINGJIUDIAANCHANGE".equals(templetId)) {
            offerCode = "VP66666";
        }

        // 查询流程信息
        operTypeByTempletId("MINORECSPEEDINESSCHANGE", offerCode, templetName);

        // 根据操作类型，界面展示不同的信息
        pattrOfferData(offerCodes, operType, offerCodeZ, templetId, flag);

        IData contractInfo = new DataMap();
        contractInfo.put("CONTRACT_NAME", dataOfferCode.getString("CONTRACT_NAME"));
        contractInfo.put("ENTERPRISEBROADBAND", offerCodeZ);
        contractInfo.put("CONTRACT_ID", dataOfferCode.getString("CONTRACT_ID"));
        contractInfo.put("CONTRACT_END_DATE", dataOfferCode.getString("CONTRACT_END_DATE"));
        contractInfo.put("CONTRACT_WRITE_DATE", dataOfferCode.getString("CONTRACT_WRITE_DATE"));
        contractInfo.put("OFFER_IDS", dataOfferCode.getString("OFFER_IDS"));

        setContractInfo(contractInfo);
        setOfferList(offerCodeList);
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

    /**
     * 查询流程信息
     * 
     * @Author taosx
     * @throws Exception
     */
    public void operTypeByTempletId(String templetId, String offerCode, String templetName) throws Exception {
        IData commInfo = new DataMap();
        IData info = new DataMap();
        IData templetInfo = WorkfromViewCall.getOperTypeByTempletId(this, templetId);
        IData input = new DataMap();
        input.put("BUSI_CODE", offerCode);
        input.put("OPER_TYPE", templetInfo.getString("BUSI_OPER_TYPE"));

        commInfo.put("OPER_TYPE", templetInfo.getString("BUSI_OPER_TYPE"));

        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
        if (IDataUtil.isNotEmpty(busiSpecReleList)) {
            IData busiSpecRele = new DataMap();
            for (int i = 0, len = busiSpecReleList.size(); i < len; i++) {
                IData busiSpecReleData = (IData) busiSpecReleList.get(i);
                if (templetInfo.getString("BPM_TEMPLET_ID").equals(busiSpecReleData.getString("BPM_TEMPLET_ID"))) {
                    busiSpecRele = busiSpecReleData;
                }
            }
            busiSpecRele.put("TEMPLET_ID", info.getString("TEMPLET_ID"));
            info.put("BUSI_SPEC_RELE", busiSpecRele);
            info.put("TEMPLET_BUSI_CODE", busiSpecRele.getString("BUSI_CODE"));

            // 查询流程节点信息
            input.clear();
            input.put("BPM_TEMPLET_ID", busiSpecRele.getString("BPM_TEMPLET_ID"));
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
                commInfo.put("PAGE_LEVE", templetId);
            } else {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品【" + templetName + "】未配置未配置主流程或流程节点信息，不能办理该业务！");
            }
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品【" + templetName + "】未配置业务流程关系信息，不能办理该业务！");
        }
        info.put("CHANGE_BPM_TEMPLET_ID", templetId);
        setComminfo(commInfo);
        setInfo(info);
    }

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
                productInfo.put("WIDEADDMEBSUB_TABLE", false);
            }
        } else if (BizCtrlType.MinorecCreateUser.equals(operType)) {
            productInfo.put("PARAM_OFFER_TYPE", "CrtUser");
            productInfo.put("HAS_CHILD", false);
            if ("PHONE".equals(flag)) {
                productInfo.put("PAM_URGENCY_LEVEL", "ADD");
            }
        } else if (BizCtrlType.MinorecDestroyUser.equals(operType)) {
            productInfo.put("PARAM_OFFER_TYPE", "DstUser");
            productInfo.put("WIDEADDMEBSUB_TABLE", false);
            productInfo.put("ADDMEBSUB", false);

        } else if (BizCtrlType.MinorecChangeWideNet.equals(operType) && !"8000".equals(productIdAs)) {
            productInfo.put("PARAM_OFFER_TYPE", "ChgWn");
            productInfo.put("ADDMEBSUB", false);
            productInfo.put("WIDEADDMEBSUB_TABLE", true);
            productInfo.put("WIDEPRODUCT", "wideChange");
        }
        if ("ENTERPRISEBROADBANDCHANGE".equals(templetId)) {
            productInfo.put("PAM_URGENCY_LEVEL", "DSB");
        }

        setPattrInfo(productInfo);
    }

    public void changeMebrData(String offerCode, String userId, String operType, IData grpUserProduct) throws Exception {
        IData data = new DataMap();
        data.put("OFFER_CODE", offerCode);
        data.put("USER_ID", userId);
        data.put("OPER_TYPE", operType);
        IDataset hisProInfos = CSViewCall.call(this, "SS.WorkformProductSVC.qryProductByuserId", data);// 查询主流程是否完工
        if (IDataUtil.isNotEmpty(hisProInfos)) {
            if ("7341".equals(offerCode)) {
                changeWideMebrData(userId, operType);// 获取宽带开通数据
            }
            if ("DelMeb".equals(operType)) {
                IData membrOffer = new DataMap();
                String membOfferCode = "";
                if ("7341".equals(offerCode)) {
                    data.clear();
                    data.put("ROUTE_EPARCHY_CODE", "0898");
                    IDataset memInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(this, userId, "MR");// 获取UU关系表，集团下订购了多少个成员
                    if (IDataUtil.isNotEmpty(memInfos)) {
                        String userIdB = memInfos.first().getString("USER_ID_B");
                        data.put("USER_ID", userIdB);
                        IDataset mebProductList = CSViewCall.call(this, "CS.UserProductInfoQrySVC.queryUserMainProductByUserId", data);
                        if (IDataUtil.isNotEmpty(mebProductList)) {
                            for (Object object : mebProductList) {
                                IData membPdInfo = (IData) object;
                                String brandCode = membPdInfo.getString("BRAND_CODE");
                                if ("WDBD".equals(brandCode)) {
                                    membOfferCode = membPdInfo.getString("PRODUCT_ID");
                                    break;
                                }
                            }
                        }
                    }
                } else if ("2222".equals(offerCode)) {
                    IDataset memInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(this, userId, "S1");// 获取UU关系表，集团下订购了多少个成员
                    if (IDataUtil.isNotEmpty(memInfos)) {
                        String userIdB = memInfos.first().getString("USER_ID_B");
                        data.put("USER_ID", userIdB);
                        IDataset mebProductList = CSViewCall.call(this, "CS.ProductInfoQrySVC.getUserProductByUserIdForGrp", data);
                        if (IDataUtil.isNotEmpty(mebProductList)) {
                            for (Object object : mebProductList) {
                                IData membPdInfo = (IData) object;
                                String brandCode = membPdInfo.getString("BRAND_CODE");
                                if ("CTRX".equals(brandCode)) {
                                    membOfferCode = membPdInfo.getString("PRODUCT_ID");
                                    break;
                                }
                            }
                        }

                    }
                } else if ("8001".equals(offerCode) || "8000".equals(offerCode)) {
                    IDataset memInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(this, userId, "20");// 获取UU关系表，集团下订购了多少个成员
                    if (IDataUtil.isNotEmpty(memInfos)) {
                        String userIdB = memInfos.first().getString("USER_ID_B");
                        data.put("USER_ID", userIdB);
                        IDataset mebProductList = CSViewCall.call(this, "CS.ProductInfoQrySVC.getUserProductByUserIdForGrp", data);
                        if (IDataUtil.isNotEmpty(mebProductList)) {
                            for (Object object : mebProductList) {
                                IData membPdInfo = (IData) object;
                                String brandCode = membPdInfo.getString("BRAND_CODE");
                                if ("VPMN".equals(brandCode)) {
                                    membOfferCode = membPdInfo.getString("PRODUCT_ID");
                                    break;
                                }
                            }
                        }
                    }
                } else if ("380300".equals(offerCode) || "380700".equals(offerCode) || "921015".equals(offerCode)) {
                    membOfferCode = offerCode;
                }
                if (StringUtils.isNotBlank(membOfferCode)) {

                    IData offer = IUpcViewCall.getOfferInfoByOfferCode(membOfferCode);
                    membrOffer.put("OFFER_CODE", offer.getString("OFFER_CODE"));
                    membrOffer.put("OFFER_ID", offer.getString("OFFER_ID"));
                    membrOffer.put("OFFER_NAME", offer.getString("OFFER_NAME"));
                }
                grpUserProduct.put("MEB_OFFER", membrOffer);

            }

        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该业务的受理流程未完工，不能办理变更业务！");
        }
    }

    // 获取宽带产品的开通参数
    public void changeWideMebrData(String userId, String operType) throws Exception {
        IData data = new DataMap();
        IDataset memInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(this, userId, "MR");// 获取UU关系表，集团下订购了多少个成员
        IDataset widenetTable = new DatasetList();
        String address = "";// 所有已开通 的地址，方便新增成员校验
        if (IDataUtil.isNotEmpty(memInfos)) {
            IData wideInfo = new DataMap();
            for (Object object : memInfos) {
                IData memberUserInfo = (IData) object;
                String userIdB = memberUserInfo.getString("USER_ID_B");
                String serialNumberB = memberUserInfo.getString("SERIAL_NUMBER_B");
                data.put("USER_ID", userIdB);
                data.put("ROUTE_EPARCHY_CODE", "0898");
                IDataset userWideList = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);// 查询宽带信息
                if (IDataUtil.isNotEmpty(userWideList)) {
                    if (IDataUtil.isEmpty(wideInfo)) {
                        wideInfo = userWideList.first();// 界面展示宽带的公共信息，只需要展示一个，所有公共信息都一样
                    }
                    for (Object object2 : userWideList) {
                        IData userWideInfo = (IData) object2;
                        // 查询成员产品信息
                        IDataset mebProductList = CSViewCall.call(this, "CS.UserProductInfoQrySVC.queryUserMainProductByUserId", data);
                        if (IDataUtil.isNotEmpty(mebProductList)) {
                            IData offer = IUpcViewCall.getOfferInfoByOfferCode(mebProductList.first().getString("PRODUCT_ID"));
                            userWideInfo.put("WIDE_PRODUCT_ID", mebProductList.first().getString("PRODUCT_ID"));
                            userWideInfo.put("WIDE_PRODUCT_NAME", offer.getString("OFFER_NAME", ""));
                        }
                        address = userWideInfo.getString("STAND_ADDRESS") + "," + address;
                        userWideInfo.put("WIDE_SERIAL_NUMBER", serialNumberB);
                        widenetTable.add(userWideInfo);
                    }
                }
            }
            if (IDataUtil.isNotEmpty(widenetTable)) {
                String wideProductId = widenetTable.first().getString("WIDE_PRODUCT_ID");
                wideInfo.put("PRODUCT_ID", wideProductId);
                wideInfo.put("PRODUCT_NAME", widenetTable.first().getString("WIDE_PRODUCT_NAME"));
            }
            if (BizCtrlType.MinorecAddMember.equals(operType)) {
                changeMinorecAddMember(wideInfo, address, userId, widenetTable, data);// 宽带新增加载参数
            } else if (BizCtrlType.MinorecDestroyMember.equals(operType)) {
                setWidenetInfos(widenetTable);
                setWideInfo(wideInfo);
            } else if (BizCtrlType.MinorecChangeWideNet.equals(operType)) {
                String wideTypeName = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "WIDE_PRODUCT_TYPE", widenetTable.first().getString("RSRV_STR2") });
                wideInfo.put("WIDE_PRODUCT_TYPE", widenetTable.first().getString("RSRV_STR2"));
                changeWideProductType(wideTypeName, widenetTable.first().getString("PHONE"));
                setWideInfo(wideInfo);
                setWidenetInfos(widenetTable);
                changeSaleActiveList(widenetTable.first().getString("WIDE_PRODUCT_ID"));// 获取宽带资费信息

            }

        }
    }

    public void changeMinorecAddMember(IData wideInfo, String address, String userId, IDataset widenetTable, IData data) throws Exception {

        String wideTypeName = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "WIDE_PRODUCT_TYPE", wideInfo.getString("RSRV_STR2") });
        wideInfo.put("WIDE_PRODUCT_TYPE", wideInfo.getString("RSRV_STR2"));
        wideInfo.put("WIDE_ADDERSS", address);
        wideInfo.put("WIDE_OPEN_SIZE", widenetTable.size());
        data.clear();
        data.put("USER_ID", userId);
        data.put("RSRV_VALUE_CODE", "N002");
        data.put("SERIAL_NUMBER", wideInfo.getString("PHONE"));
        IDataset mebProductList = CSViewCall.call(this, "CS.UserOtherQrySVC.getUserOtherUserId", data);// 获取集团最大受理宽带条数
        if (IDataUtil.isNotEmpty(mebProductList)) {
            String wideNum = mebProductList.first().getString("RSRV_STR1", "");
            int openSize = widenetTable.size();
            String wideopenSize = Integer.toString(openSize);
            if (wideNum.equals(wideopenSize)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品成员宽带受理的最大数已经等于已开通的宽带数，不能再新增成员宽带！");
            }
            wideInfo.put("WIDE_SIZE", wideNum);
        }
        changeWideProductType(wideTypeName, wideInfo.getString("PHONE"));
        changeSaleActiveList(widenetTable.first().getString("WIDE_PRODUCT_ID"));// 获取宽带资费信息
        setWideInfo(wideInfo);

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
     * 获取支付方式等
     * 
     * @Author taosx
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
     * 展示地址信息
     * 
     * @Author taosx
     * @throws Exception
     */
    public void queryWidenetTable(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        IDataset addressList = new DatasetList(pageData.getString("DETAIL_ADDRESS_LIST"));// 地址信息
        IDataset addressList1 = new DatasetList(pageData.getString("DETAIL_ADDRESS_LIST1"));// 表格里面存在的地址信息
        String wideAdderss = pageData.getString("WIDE_ADDERSS");// 开通时的所有地址
        String[] wideAdderssInfo = wideAdderss.split(",");
        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, pageData.getString("GROUP_ID"));
        String serNumber = pageData.getString("SERIAL_NUMBER");
        String productType = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "WIDE_PRODUCT_TYPE", pageData.getString("WIDE_PRODUCT_TYPE") });
        String custId = result.getString("CUST_ID", "");
        for (int i = 0; i < addressList.size(); i++) {
            IData attachInfo = addressList.getData(i);
            String regionName = attachInfo.getString("REGION_NAME");
            String operType = attachInfo.getString("OPEN_TYPE");
            if (!operType.equals(productType)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您开通时已选择了【" + productType + "】宽带类型，变更新增不能勾选【" + operType + "】的类型，请重新选择!");
            }
            for (int j = 0; j < wideAdderssInfo.length; j++) {
                String adderss = wideAdderssInfo[j];
                if (regionName.equals(adderss)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "您新增成员的地址【" + regionName + "】已经开通成员宽带，不能重复开通!");
                }
            }

            attachInfo.put("STAND_ADDRESS", attachInfo.getString("REGION_NAME"));
            attachInfo.put("RSRV_NUM1", attachInfo.getString("DEVICE_ID"));
            attachInfo.put("RSRV_STR4", attachInfo.getString("AREA_CODE"));
            attachInfo.put("WIDE_PRODUCT_NAME", pageData.getString("WIDE_PRODUCT_NAME_LS"));
            attachInfo.put("WIDE_PRODUCT_ID", pageData.getString("WIDE_PRODUCT_ID_LS"));
        }

        if (IDataUtil.isEmpty(addressList1)) { // 表格不存在信息，直接返回
            getWideSerialNumber(addressList, serNumber, custId);
        } else {
            IDataset infos = new DatasetList();
            // 过滤表格与新增存在相同的地址信息
            for (int i = 0; i < addressList.size(); i++) {
                IData attachInfo = addressList.getData(i);
                String regionName = attachInfo.getString("REGION_NAME");
                String operType = attachInfo.getString("OPEN_TYPE");
                if (!operType.equals(productType)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "您已选择了【" + productType + "】宽带类型，不能勾选【" + operType + "】的类型，请重新选择!");
                }
                String wideProductId = "";
                boolean flag = false;
                for (int j = 0; j < addressList1.size(); j++) {
                    IData infoData = addressList1.getData(j);
                    String regionNames = infoData.getString("STAND_ADDRESS");
                    wideProductId = infoData.getString("WIDE_PRODUCT_ID");
                    if (regionNames.equals(regionName)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    attachInfo.put("WIDE_PRODUCT_ID", wideProductId);
                    attachInfo.put("STAND_ADDRESS", attachInfo.getString("REGION_NAME"));
                    attachInfo.put("FLOOR_AND_ROOM_NUM", attachInfo.getString("GIS8"));
                    attachInfo.put("WIDE_PRODUCT_NAME", pageData.getString("WIDE_PRODUCT_NAME_LS"));
                    attachInfo.put("WIDE_PRODUCT_ID", pageData.getString("WIDE_PRODUCT_ID_LS"));
                    infos.add(attachInfo);
                }
            }
            addressList1.addAll(infos);
            getWideSerialNumber(addressList1, serNumber, custId);

        }

    }

    /**
     * 获取宽商务宽带开户特殊处理，号码需要在原号码后加0000，并递增
     * 
     * @Author taosx
     * @throws Exception
     */
    public void getWideSerialNumber(IDataset addressList, String serNumber, String custId) throws Exception {

        IDataset wideAddressList = new DatasetList();// 把没有宽带号码的放到一个集合里面
        IDataset wideAddressList1 = new DatasetList();// 把已有宽带号码的放到一个集合里面
        IData data = new DataMap();
        String serialNumb = "";// 获取已有的最大宽带号码
        for (int i = 0; i < addressList.size(); i++) {
            IData adderssInfo = addressList.getData(i);
            String serialNumber = adderssInfo.getString("WIDE_SERIAL_NUMBER", "");
            if (StringUtils.isBlank(serialNumber)) {
                wideAddressList.add(adderssInfo);
                continue;
            } else {
                wideAddressList1.add(adderssInfo);
                for (int j = 0; j < addressList.size(); j++) {
                    IData adderssInfoJ = addressList.getData(j);
                    String SerialNumberJ = adderssInfoJ.getString("WIDE_SERIAL_NUMBER", "");
                    if (StringUtils.isNotBlank(SerialNumberJ) && SerialNumberJ.compareTo(serialNumber) > 0) {
                        serialNumb = SerialNumberJ;
                    } else if (SerialNumberJ.equals(serialNumber)) {
                        serialNumb = SerialNumberJ;
                    }
                }
            }
        }
        if (IDataUtil.isNotEmpty(wideAddressList)) {
            int serNumberSize = wideAddressList.size();
            data.put("WIDE_SERIAL_NUMBER", serialNumb);
            data.put("SERIAL_NUMBER", serNumber);
            data.put("SERNUMBER_SIZE", serNumberSize);
            data.put("OPER_TYPE", "crtCg");
            data.put("CUST_ID", custId);
            // 获取新的宽带号码
            IDataset wideSNdataset = CSViewCall.call(this, "SS.WideUserCreateSVC.getWideSerialNumberMinorec", data);
            // 给新增号码赋值
            if (IDataUtil.isNotEmpty(wideSNdataset)) {
                for (int i = 0; i < wideSNdataset.size(); i++) {
                    IData sideSnInfo = wideSNdataset.getData(i);
                    String sideSerNumber = sideSnInfo.getString("SERIAL_NUMBER");
                    IData wideAddressInfo = wideAddressList.getData(i);
                    wideAddressInfo.put("WIDE_SERIAL_NUMBER", sideSerNumber);
                }
            }
            wideAddressList1.addAll(wideAddressList);// 拼接已有宽带与新增宽带号码
            setWidenetInfos(wideAddressList1);
        } else {
            // 如果都有宽带号码直接返回
            setWidenetInfos(addressList);
        }
    }

    /**
     * 获取宽带产品
     * 
     * @Author taosx
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

    // 删除宽带成员
    public void removeaddMebSub(IRequestCycle cycle) throws Exception {

        IData pageData = getData();
        IDataset addressList = new DatasetList(pageData.getString("DETAIL_ADDRESS_LIST"));// 删除的数据
        IDataset addressList1 = new DatasetList(pageData.getString("DETAIL_ADDRESS_LIST1"));// 表格里面存在的地址信息
        String operType = pageData.getString("OPER_TYPE");
        if (BizCtrlType.MinorecDestroyMember.equals(operType)) {// 删除成员走规矩校验
            for (Object object : addressList) {
                IData addInfo = (IData) object;
                IData data = new DataMap();
                data.put(Route.ROUTE_EPARCHY_CODE, "0898");
                data.put("USER_ID", addInfo.getString("MEB_USER_ID"));
                IData grpUserInfo = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, addInfo.getString("MEB_USER_ID"));
                data.put("CUST_ID", grpUserInfo.getData("GRP_USER_INFO").getString("CUST_ID", ""));
                data.put("PRODUCT_ID", addInfo.getString("WIDE_PRODUCT_ID"));
                data.put("BRAND_CODE", "BNBD");
                data.put("X_CHOICE_TAG", "0");
                data.put("EPARCHY_CODE", "0898");
                data.put("SERIAL_NUMBER", addInfo.getString("WIDE_SERIAL_NUMBER"));
                data.put("TRADE_TYPE_CODE", "605");

                // 宽带地址信息
                IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
                String widetype = dataset.getData(0).getString("RSRV_STR2");
                data.put("WIDE_TYPE_CODE", widetype);

                // 删除宽带，规则校验
                IDataset infos = CSViewCall.call(this, "CS.CheckTradeSVC.checkBeforeTrade", data);
                if (IDataUtil.isNotEmpty(infos)) {
                    IData info = infos.getData(0);
                    IDataset tipsInfos = info.getDataset("TIPS_TYPE_ERROR");
                    if (IDataUtil.isNotEmpty(tipsInfos)) {
                        String tipsInfo = tipsInfos.getData(0).getString("TIPS_INFO");
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "您删除的宽带【 " + addInfo.getString("WIDE_SERIAL_NUMBER") + " 】有规则校验：" + tipsInfo);
                    }
                }
            }
        }

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

        for (Object object1 : addressList1) {
            IData addrsInfo = (IData) object1;
            addrsInfo.put("USER_ID", addrsInfo.getString("MEB_USER_ID"));
            addrsInfo.put("RSRV_NUM1", addrsInfo.getString("DEVICE_ID"));
            addrsInfo.put("RSRV_STR4", addrsInfo.getString("AREA_CODE"));
        }
        setWidenetInfos(addressList1);

    }

    /**
     * 宽带产品资费属性，获取必选的元素
     * 
     * @Author taosx
     * @throws Exception
     * @author yuyj3
     */
    public void getWidenetUserOpenElement(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset wideDataList = new DatasetList(data.getString("WIDE_DATA_LIST"));
        String newProductId = data.getString("PRODUCT_ID");
        boolean flag = true;
        IData logs = new DataMap();
        if (IDataUtil.isNotEmpty(wideDataList)) {
            for (Object object : wideDataList) {
                IData wideInfo = (IData) object;

                String wideProductId = wideInfo.getString("WIDE_PRODUCT_ID");
                if (newProductId.equals(wideProductId)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "您选择变更的产品与之前的产品相同，无法变更，请重新选择！");

                }
                IData map = new DataMap();
                map.put("TRADE_TYPE_CODE", "601");
                map.put("NEW_PRODUCT_ID", newProductId);
                map.put("BOOKING_DATE", SysDateMgr4Web.getFirstDayOfNextMonth());
                map.put("USER_PRODUCT_ID", data.getString("USER_PRODUCT_ID"));
                map.put("USER_ID", wideInfo.getString("MEB_USER_ID"));
                map.put("EPARCHY_CODE", "0898");
                map.put("SERIAL_NUMBER", wideInfo.getString("WIDE_SERIAL_NUMBER"));
                map.put("ROUTE_EPARCHY_CODE", "0898");
                try {
                    CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.loadProductInfo", map);
                }
                catch (Exception e) {
                    logs.put("MESSAGE", "您变更的宽带【 " + wideInfo.getString("WIDE_SERIAL_NUMBER") + "】规则报错，报错如下: " + e.getMessage().substring(e.getMessage().indexOf("`") + 1, e.getMessage().length() - 1));
                    // this.setAjax(logs);
                    flag = false;
                    break;
                }

                IDataset dataset = CSViewCall.call(this, "CS.SelectedElementSVC.getUserElements", map);
                if (IDataUtil.isNotEmpty(dataset)) {
                    IDataset selectedElements = new DatasetList(dataset.getData(0).getString("SELECTED_ELEMENTS"));
                    map.put("SELECTED_ELEMENTS", selectedElements);
                    if (IDataUtil.isNotEmpty(selectedElements)) {
                        Iterator<Object> selectedInfo = selectedElements.iterator();
                        while (selectedInfo.hasNext()) {
                            IData selectedInfoData = (IData) selectedInfo.next();
                            String productId = selectedInfoData.getString("PRODUCT_ID");
                            if ("2010".equals(productId)) {
                                selectedInfo.remove();
                            }
                            if (newProductId.equals(productId)) {
                                wideInfo.put("WIDE_PRODUCT_NAME", selectedInfoData.getString("ELEMENT_NAME"));
                            }
                        }
                    }
                    map.put("BASIC_START_DATE", SysDateMgr4Web.getFirstDayOfNextMonth());// 获取下个月第一天
                    map.put("BASIC_CANCEL_DATE", SysDateMgr4Web.getLastDateThisMonth());// 获取本月最后一天

                    map.put("ELEMENTS", selectedElements);
                    map.put("FIRST_DATE", SysDateMgr4Web.getFirstDayOfNextMonth());// 获取下个月第一天
                    map.put("TRADE_TYPE_CODE", "601");
                    map.put("ACCT_DAY", "1");
                    IDataset callSelectedElement = CSViewCall.call(this, "CS.SelectedElementSVC.dealWidenetSelectedElementsForChg", map);
                    if (IDataUtil.isNotEmpty(callSelectedElement)) {
                        IDataset selectedElementList = new DatasetList();
                        for (Object object1 : callSelectedElement) {
                            IData selectedInfo = (IData) object1;
                            IData selElementInfo = new DataMap();
                            selElementInfo.put("ELEMENT_ID", selectedInfo.getString("ELEMENT_ID", ""));
                            selElementInfo.put("ELEMENT_TYPE_CODE", selectedInfo.getString("ELEMENT_TYPE_CODE", ""));
                            selElementInfo.put("END_DATE", selectedInfo.getString("END_DATE", ""));
                            selElementInfo.put("INST_ID", selectedInfo.getString("INST_ID", ""));
                            if ("exist".equals(selectedInfo.getString("MODIFY_TAG", "")) && !"2010".equals(selectedInfo.getString("ELEMENT_ID", ""))) {
                                selElementInfo.put("MODIFY_TAG", "2");
                            } else if ("exist".equals(selectedInfo.getString("MODIFY_TAG", "")) && "2010".equals(selectedInfo.getString("ELEMENT_ID", ""))) {
                                selElementInfo.put("MODIFY_TAG", "U");
                            } else {
                                selElementInfo.put("MODIFY_TAG", selectedInfo.getString("MODIFY_TAG", ""));
                            }
                            selElementInfo.put("PACKAGE_ID", selectedInfo.getString("PACKAGE_ID", ""));
                            selElementInfo.put("PRODUCT_ID", selectedInfo.getString("PRODUCT_ID", ""));
                            selElementInfo.put("START_DATE", selectedInfo.getString("START_DATE", ""));
                            selectedElementList.add(selElementInfo);
                        }
                        wideInfo.put("SELECTED_ELEMENTS", selectedElementList);
                    }
                }
                wideInfo.put("WIDE_PRODUCT_ID", data.getString("PRODUCT_ID"));
                wideInfo.put("RSRV_NUM1", wideInfo.getString("DEVICE_ID"));
                wideInfo.put("USER_ID", wideInfo.getString("MEB_USER_ID"));
                wideInfo.put("RSRV_STR4", wideInfo.getString("AREA_CODE"));

            }
            if (!flag) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, logs.getString("MESSAGE"));
            }
            setWidenetInfos(wideDataList);
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
            setSaleActiveListAttr(saleActiveInfos);

        }

    }

    public void queryEcAccountList(IRequestCycle cycle) throws Exception {
        String custId = this.getData().getString("CUST_ID");
        IDataset accounts = UCAInfoIntfViewUtil.qryGrpAcctInfosByCustId(this, custId);
        setEcAccountList(accounts);
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

    public void queryArchivesInfo(IRequestCycle cycle) throws Exception {
        IData param = this.getData();
        String groupId = param.getString("GROUP_ID");
        String templetId = param.getString("TEMPLET_ID");
        // 获取虚拟的产品ID
        String xnProductId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "PDATA_ID" }, "DATA_ID", new String[] { "MINOREC_XN_PRODUCT", templetId });
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        String custId = group.getString("CUST_ID");
        param.put("CUST_ID", custId);
        param.put("XN_PRODUCTID", xnProductId);
        IDataset archivesList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryArchivesInfo", param);

        setArchivesList(archivesList);

    }

    public void changOperTypeSet(IRequestCycle cycle) throws Exception {
        IData param = this.getData();
        String templetId = param.getString("TEMPLET_ID");
        IDataset operTypeList = pageutil.getList("TD_S_STATIC", "DATA_ID", "DATA_NAME", "TYPE_ID", "MINOREC_CHANGE_OPER_TYPE");
        Iterator<Object> operTypeInfo = operTypeList.iterator();
        while (operTypeInfo.hasNext()) {
            IData operTypeData = (IData) operTypeInfo.next();
            String dataId = operTypeData.getString("DATA_ID");
            if ("ENTERPRISEBROADBANDCHANGE".equals(templetId) && BizCtrlType.MinorecCreateUser.equals(dataId)) {
                operTypeInfo.remove();
            } else if ("FUSECOMMUNICATIONCHANGE".equals(templetId) && "ChgWn".equals(dataId)) {
                operTypeInfo.remove();
            } else if ((BizCtrlType.MinorecChangeWideNet.equals(dataId) || BizCtrlType.MinorecCreateUser.equals(dataId)) && ("SUMBUSINESSTVCHANGE".equals(templetId) || "CLOUDWIFICHANGE".equals(templetId) || "CLOUDTAVERNCHANGE".equals(templetId))) {
                operTypeInfo.remove();
            } else if ("CLOUDTAVERNCHANGE".equals(templetId) && (BizCtrlType.MinorecAddMember.equals(dataId) || BizCtrlType.MinorecDestroyMember.equals(dataId))) {
                operTypeInfo.remove();
            } else if ("ENTERPRISEBROADBANDCHANGE".equals(templetId) && BizCtrlType.MinorecDestroyMember.equals(dataId)) {
                operTypeInfo.remove();
            }
        }
        setOperTypeList(operTypeList);
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

    // ESP订购人员查询
    public void qryOrderStaffinfo(IRequestCycle cycle) throws Exception {
        IData input = getData();
        IData inParam = new DataMap();
        String staffName = input.getString("cond_OrderStaffName", "");
        String roleId = StaticUtil.getStaticValue(getVisit(), "TD_B_EWE_CONFIG", "CONFIGNAME", "PARAMVALUE", "ESP_ORDER_ROLE");
        if (StringUtils.isBlank(roleId)) {
            CSViewException.apperr(GrpException.CRM_GRP_713, "没有指定ESP订购角色配置！请检查TD_B_EWE_CONFIG表配置！");
        }
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
                    setStaffInfos(staffListName);
                }
            }
        } else {
            setStaffInfos(staffList);
        }
    }

    public abstract void setContractInfo(IData contractInfo) throws Exception;

    public abstract void setSaleActiveList(IData elementInfo) throws Exception;

    public abstract void setWideInfo(IData wideInfo) throws Exception;

    public abstract void setSaleActiveListAttr(IDataset selectedElement) throws Exception;

    public abstract void setOperTypeList(IDataset operTypeList) throws Exception;

    public abstract void setArchivesList(IDataset archivesList) throws Exception;

    public abstract void setMergeWideUserStyleList(IDataset mergeWideUserStyleList) throws Exception;

    public abstract void setWidenetPayModeList(IDataset templetId) throws Exception;

    public abstract void setProductList(IDataset productList) throws Exception;

    public abstract void setOffer(IData offer) throws Exception;

    public abstract void setInfo(IData info) throws Exception;

    public abstract void setInfos(IDataset infos) throws Exception;

    public abstract void setComminfo(IData comminfo) throws Exception;

    public abstract void setBusi(IData busi) throws Exception;

    public abstract void setPattrInfo(IData pattrInfo) throws Exception;

    public abstract void setOfferList(IDataset offerList) throws Exception;

    public abstract void setWidenetInfos(IDataset widenetInfos) throws Exception;

    public abstract void setEcAccountList(IDataset ecAccountList) throws Exception;

    public abstract void setInAttr(IData inAttr) throws Exception;

    public abstract void setStaffInfos(IDataset infos) throws Exception;

    public abstract void setCustGroupList(IDataset custGroupList) throws Exception;

    public abstract void setCustGroupInfo(IData custGroupInfo) throws Exception;
}
