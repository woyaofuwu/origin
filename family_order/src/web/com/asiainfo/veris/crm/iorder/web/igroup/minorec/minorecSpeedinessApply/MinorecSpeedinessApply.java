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
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public abstract class MinorecSpeedinessApply extends EopBasePage {

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
        setGroupInfo(group);

        String custMgrId = group.getString("CUST_MANAGER_ID");
        String custId = group.getString("CUST_ID");
        qryCustManagerByCustManagerId(custMgrId,custId);
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

    public void qryCustManagerByCustManagerId(String custMgrId, String custId) throws Exception
    {
        if (StringUtils.isNotEmpty(custMgrId))
        {
            IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);

            if(IDataUtil.isEmpty(managerInfo))
            {
                managerInfo = new DataMap();
            }

            // 获取部分客户资料
            IData param = new DataMap();
            param.put("CUST_ID", custId);
            IData grpCustInfo = CSViewCall.callone(this, "CS.CustomerInfoQrySVC.getCustInfoByCustID", param);
            IData custInfoData = new DataMap();
            custInfoData.put("PSPT_ID", grpCustInfo.getString("PSPT_ID"));
            custInfoData.put("PSPT_TYPE_CODE", grpCustInfo.getString("PSPT_TYPE_CODE"));
            managerInfo.put("grpCustInfo", custInfoData);

            setCustMgrInfo(managerInfo);
        }

    }

    public void queryInfo(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        IDataset offerCodeList = new DatasetList();
        IDataset offerCodeListVw = new DatasetList();
        String templetId = pageData.getString("TEMPLET_ID");
        String templetName = StaticUtil.getStaticValue("MINOREC_BPM_TEPMENTID", templetId);
        String productList = StaticUtil.getStaticValue("MINOREC_BPM_PRODUCTID_LIST", templetId);
        IData accthInfo = new DataMap(pageData.getString("DATEMAP"));// 合同信息
        String productIdInfo = pageData.getString("PRODUCTID_INFO");// 获取合同的产品
        String[] offerCodeInfo = productIdInfo.split(",");
        String[] productLists = productList.split(",");
        String offerCodeZ = "";
        String offerCode = "";
        String productIds = "";

        // 冒泡排序,把多媒体桌面电话放到V网前面
        int[] intOfferCode = intOfferCodeSort(offerCodeInfo);

        IData productInfo = new DataMap();
        for (int i = 0; i < intOfferCode.length; i++) {
            boolean flag = false;
            IData offerCInfo = new DataMap();
            offerCode = Integer.toString(intOfferCode[i]);
            // 合同产品只能包含该流程下的产品，存在其他产品则不能选择该合同
            for (int j = 0; j < productLists.length; j++) {
                productIds = productLists[j];
                if (offerCode.equals(productIds)) {
                    flag = true;
                }
            }
            if (!flag) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "您选择的合同不包含【" + templetName + "】产品，或包含其他产品，请重新选择合同！");
            }

            // 判断集团规则
            IData checkResult = checkOperGrp(offerCode, pageData.getString("GROUP_ID"), productIdInfo);
            if(IDataUtil.isNotEmpty(checkResult))
            {
                setAjax(checkResult);
                return;
            }

            // 给界面赋值
            IData offer = IUpcViewCall.getOfferInfoByOfferCode(offerCode);
            String offerId = IUpcViewCall.getOfferIdByOfferCode(offerCode);// 获取OFFERID
            String brandCode = IUpcViewCall.queryBrandCodeByOfferId(offerId);// 获取品牌
            if ("7341".equals(offerCode)) {
                offerCodeZ = offerCode;
                mergeWideUserStyleCheck(offerCodeZ);
            }
            IData agreementElementData = queryContractOfferChaList(accthInfo.getString("CONTRACT_ID"), offerCode);// 电子协议信息回填接口
            offerCInfo.put("OFFER_CODE", offer.getString("OFFER_CODE"));
            offerCInfo.put("OFFER_ID", offerId);
            offerCInfo.put("OFFER_NAME", offer.getString("OFFER_NAME"));
            offerCInfo.put("AGREEMENT_ELEMENT_DATA", agreementElementData);
            offerCInfo.put("BRAND_CODE", brandCode);
            if ("8000".equals(offerCode)) {
                offerCodeListVw.add(offerCInfo);
            } else {
                offerCodeList.add(offerCInfo);
            }
            if ("7341".equals(offerCode)) {
                IDataset ecOfferList = agreementElementData.getDataset("EC_OFFER");
                IDataset ecOfferChaList = ecOfferList.getData(0).getDataset("OFFER_CHA_SPECS");
                IData wideInfo = new DataMap();
                if (IDataUtil.isNotEmpty(ecOfferChaList)) {
                    if ("EP_WITH".equals(ecOfferChaList.getData(0).getString("ATTR_CODE"))) {
                        wideInfo.put("EP_WITH", ecOfferChaList.getData(0).getString("ATTR_VALUE"));
                        setAgreementElement(wideInfo);
                    }
                }
            }

            if ("FUSECOMMUNICATIONOPEN".equals(templetId)) {
                offerCode = "VP998001";
            } else if ("YIDANQINGSHANGPU".equals(templetId) || "8000".equals(offerCode)) {
                offerCode = "VP99999";
            } else if ("YIDANQINGJIUDIAAN".equals(templetId)) {
                offerCode = "VP66666";
            }
        }

        // 查询流程信息
        operTypeByTempletId(templetId, offerCode, templetName);

        productInfo.put("CONTRACT_NAME", accthInfo.getString("CONTRACT_NAME"));
        productInfo.put("ENTERPRISEBROADBAND", offerCodeZ);
        productInfo.put("CONTRACT_ID", accthInfo.getString("CONTRACT_ID"));
        productInfo.put("CONTRACT_END_DATE", accthInfo.getString("CONTRACT_END_DATE"));
        productInfo.put("CONTRACT_WRITE_DATE", accthInfo.getString("CONTRACT_WRITE_DATE"));
        productInfo.put("OFFER_IDS", accthInfo.getString("OFFER_IDS"));

        setPattrInfo(productInfo);
        setOfferCodeList(offerCodeList);
        setOfferCodeListVw(offerCodeListVw);
        this.setAjax(productInfo);
    }

    /**
     * 电子协议信息回填接口
     * 
     * @param
     * @throws Exception
     */
    public IData queryContractOfferChaList(String contractId, String offerCode) throws Exception {

        IData param = new DataMap();
        param.put("CONTRACT_ID", contractId);
        param.put("OFFER_CODE", offerCode);
        IData agreementElementData = CSViewCall.callone(this, "SS.ContractOfferChaListSVC.queryContractOfferChaList", param);
        return agreementElementData;
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
     * 判断集团是否可办理该产品
     * 
     * @param
     * @throws Exception
     */
    public IData checkOperGrp(String offerCode, String groupId, String productLists) throws Exception {
        IData checkResult = new DataMap();

        IData checkParam = new DataMap();
        checkParam.put("GROUP_ID", groupId);
        checkParam.put("PRODUCT_ID", offerCode);
        checkParam.put("STAFF_ID", getVisit().getStaffId());
        checkParam.put("OPER_CODE", "0");

        try{
            CSViewCall.call(this, "SS.QcsGrpIntfSVC.checkOperProduct", checkParam);
        }
        catch (Exception e)
        {
            checkResult.put("ERROR_MESSAGE",e.getMessage());
            return checkResult;
        }

        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        String custId = result.getString("CUST_ID", "");
        // 调用集团受理规则
        if ("8001".equals(offerCode) && productLists.indexOf("2222") >= 0) {// 如果页面同时选择了多媒体桌面电话及融合V网，不做校验

        } else if ("8001".equals(offerCode) && productLists.indexOf("2222") < 0) {
            checkParam.clear();
            checkParam.put("CUST_ID", custId);
            checkParam.put("PRODUCT_ID", "2222");
            IDataset results = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getGrpProductByGrpCustIdProID", checkParam);
            if (IDataUtil.isEmpty(results)) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "集团客户未订购多媒体桌面电话,请先订购多媒体桌面电话或同时受理多媒体桌面电话及融合V网!");
            }
        } else if ("2222".equals(offerCode) && productLists.indexOf("7341") < 0) {
            checkParam.clear();
            checkParam.put("CUST_ID", custId);
            checkParam.put("PRODUCT_ID", "7341");
            IDataset results = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getGrpProductByGrpCustIdProID", checkParam);
            if (IDataUtil.isEmpty(results)) {
                checkParam.clear();
                checkParam.put("CUST_ID", custId);
                checkParam.put("PRODUCT_ID", "7016");
                IDataset resultsIms = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getGrpProductByGrpCustIdProID", checkParam);
                if (IDataUtil.isEmpty(resultsIms)) {
                    IData widePhone = new DataMap();
                    widePhone.put("WIDE_PHONE", "true");
                    setWidePhone(widePhone);
                }
            }
        } else {
            checkParam.clear();
            checkParam.put("CHK_FLAG", "BaseInfo");
            checkParam.put("EPARCHY_CODE", "0898");
            checkParam.put("CUST_ID", custId);
            checkParam.put("PRODUCT_ID", offerCode);

            try{
                CSViewCall.call(this, "CS.chkGrpUserOpen", checkParam);
            }
            catch (Exception e)
            {
                checkResult.put("ERROR_MESSAGE",e.getMessage());
                return checkResult;
            }
        }
        return checkResult;
    }

    /**
     * 判断集团是否可办理该产品
     * 
     * @param
     * @throws Exception
     */
    public void checkOperProducts(IRequestCycle cycle) throws Exception {
        IData param = this.getData();
        String templetId = param.getString("TEMPLET_ID");
        String productList = StaticUtil.getStaticValue("MINOREC_BPM_PRODUCTID_LIST", templetId);
        String[] productLists = productList.split(",");
        StringBuilder offerNameAll = new StringBuilder();
        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, param.getString("GROUP_ID"));
        String custId = result.getString("CUST_ID", "");
        IData productInfo = new DataMap();
        for (int i = 0; i < productLists.length; i++) {
            String productCode = productLists[i];
            IData userInfo = new DataMap();
            userInfo.put("CUST_ID", custId);
            userInfo.put("PRODUCT_ID", productCode);
            IDataset userList = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", userInfo);
            if (IDataUtil.isNotEmpty(userList)) {
                IData offer = IUpcViewCall.getOfferInfoByOfferCode(productCode);
                StringBuilder offerName = new StringBuilder(offer.getString("OFFER_NAME"));
                IDataset checkOperProduct = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(this, "CGM", "5", productCode, "ZZZZ");
                if (IDataUtil.isEmpty(checkOperProduct)) {
                    if (StringUtils.isBlank(offerNameAll)) {
                        offerNameAll = offerName;
                    } else {

                        offerNameAll = offerNameAll.append(",").append(offerName);

                    }
                }
            }
        }

        String offerNameAlls = offerNameAll.toString();
        if (StringUtils.isNotBlank(offerNameAlls)) {
            offerNameAlls = "提示 : 您选择的流程在该集团已经订购了【" + offerNameAlls + "】产品，不能重复订购！";
        }
        productInfo.put("TIP_OFFERNAME", offerNameAlls);
        setInfo(productInfo);
        this.setAjax(productInfo);
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

        info.put("OPER_TYPE", templetInfo.getString("BUSI_OPER_TYPE"));

        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
        if (IDataUtil.isNotEmpty(busiSpecReleList)) {
            busiSpecReleList.first().put("TEMPLET_ID", info.getString("TEMPLET_ID"));
            info.put("BUSI_SPEC_RELE", busiSpecReleList.first());
            info.put("TEMPLET_BUSI_CODE", busiSpecReleList.first().getString("BUSI_CODE"));

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
                commInfo.put("PAGE_LEVE", templetId);
            } else {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品【" + templetName + "】未配置未配置主流程或流程节点信息，不能办理该业务！");
            }
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品【" + templetName + "】未配置业务流程关系信息，不能办理该业务！");
        }
        setComminfo(commInfo);
        setInfo(info);
    }

    /**
     * 查询是否受理了宽带
     * 
     * @Author taosx
     * @throws Exception
     */
    public void queryWidePhone(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        IData ucaInfos = UCAInfoIntfViewUtil.qryMebUCAInfoBySn(this, pageData.getString("WIDE_PHONE"));
        IData widePhoneInfo = new DataMap();
        Boolean flag = false;
        if (IDataUtil.isNotEmpty(ucaInfos)) {
            IData userInfo = ucaInfos.getData("MEB_USER_INFO", new DataMap());
            if (IDataUtil.isNotEmpty(userInfo)) {
                IData data = new DataMap();
                data.put("USER_ID", userInfo.getString("USER_ID"));
                data.put("ROUTE_EPARCHY_CODE", "0898");
                if (pageData.getString("WIDE_PHONE").indexOf("KD_") >= 0) {// 输入的是宽带号码，直接查宽带资料表
                    IDataset userWideList = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);// 查询宽带信息
                    if (IDataUtil.isNotEmpty(userWideList)) {
                        flag = true;
                    }
                } else {
                    IDataset memUUAInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(this, userInfo.getString("USER_ID"), "47");// 查询无手机宽带
                    if (IDataUtil.isNotEmpty(memUUAInfos)) {
                        flag = true;
                    } else {
                        IDataset memUUBInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(this, userInfo.getString("USER_ID"), "47", "0898");// 有手机宽带的情况下查询
                        if (IDataUtil.isNotEmpty(memUUBInfos)) {
                            flag = true;
                        }
                    }
                }
            }
        }
        if (flag) {
            widePhoneInfo.put("WIDE_FLAG", "true");
        } else {
            widePhoneInfo.put("WIDE_FLAG", "");
        }
        this.setAjax(widePhoneInfo);
    }

    /**
     * 展示地址信息
     * 
     * @Author taosx
     * @throws Exception
     */
    public void queryWidenetTable(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        IData map = new DataMap();
        IDataset addressList = new DatasetList(pageData.getString("DETAIL_ADDRESS_LIST"));// 地址信息
        IDataset addressList1 = new DatasetList(pageData.getString("DETAIL_ADDRESS_LIST1"));// 表格里面存在的地址信息
        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, pageData.getString("GROUP_ID"));
        String serNumber = pageData.getString("SERIAL_NUMBER");
        String productType = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "WIDE_PRODUCT_TYPE", pageData.getString("WIDE_PRODUCT_TYPE") });
        String custId = result.getString("CUST_ID", "");
        if (IDataUtil.isEmpty(addressList1)) { // 表格不存在信息，直接返回
            String openType = addressList.first().getString("OPEN_TYPE", "");
            String wideOpenType = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[] { "WIDE_PRODUCT_TYPE", openType });
            map.put("WIDE_PRODUCT_TYPE", wideOpenType);
            setInfo(map);
            changeWideProductType(openType, serNumber);// 获取宽带产品
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
                    infoData.put("REGION_NAME", regionNames);
                    if (regionNames.equals(regionName)) {
                        CSViewException.apperr(CrmCommException.CRM_COMM_103, "您选择的地址【" + regionName + "】已经勾选，不能重复勾选，请重新选择!");
                    }
                }
                if (!flag) {
                    attachInfo.put("WIDE_PRODUCT_ID", wideProductId);
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
                    if (StringUtils.isNotBlank(SerialNumberJ) && (SerialNumberJ.compareTo(serialNumber) > 0)) {
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
            data.put("OPER_TYPE", "crtUs");
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
        input.put("USER_ID", "");
        input.put("OFFER_INS_ID", "");
        input.put("RELA_INST_ID", "");
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
                    // String paramData = param.getString(key);
                    // String value = param.getString(key);
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
     * 宽带产品资费属性，获取必选的元素
     * 
     * @Author taosx
     * @throws Exception
     */
    public void getWidenetUserOpenElement(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData map = new DataMap();
        map.put("NEW_PRODUCT_ID", data.getString("PRODUCT_ID"));
        map.put("ROUTE_EPARCHY_CODE", "0898");

        IDataset dataset = CSViewCall.call(this, "CS.SelectedElementSVC.getWidenetUserOpenElements", map);
        String hint = "";
        if (IDataUtil.isNotEmpty(dataset)) {
            IData elementInfo = new DataMap();
            IDataset selectedElements = new DatasetList(dataset.getData(0).getString("SELECTED_ELEMENTS"));
            for (Object object : selectedElements) {
                IData elementsInfo = (IData) object;
                if ("D".equals(elementsInfo.getString("ELEMENT_TYPE_CODE"))) {
                    String productId = elementsInfo.getString("PRODUCT_ID");

                    IData rateData = getData();
                    rateData.put(Route.ROUTE_EPARCHY_CODE, "0898");
                    rateData.put("NEW_PRODUCT_ID", productId);
                    IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.getProductRateByProductId", rateData);

                    if (IDataUtil.isNotEmpty(resultData)) {
                        String epWith = data.getString("EP_WITH");// 协议填写宽带兆数
                        if (StringUtils.isBlank(epWith))
                        {
                            hint = "您未填写电子协议里的宽带兆数！";
                        }
                        else
                        {
                            String wideWith = resultData.getString("NEW_RATE", "");
                            if (StringUtils.isBlank(wideWith))
                            {
                                hint = "TD_S_COMMPARA表未找到产品【" + productId + "】宽带兆数对应关系！";
                            }
                            else
                            {
                                int wideWithi = Integer.parseInt(wideWith);
                                int is = 1024;
                                int widei = wideWithi / is;
                                if (!String.valueOf(widei).equals(epWith))
                                {
                                    hint = "您电子协议输入的宽带兆数为:【" + epWith + "M】,选择的宽带产品是:【" + widei + "M】";
                                }
                                else
                                {
                                    hint = "您电子协议输入的宽带兆数与产品选择的兆数相同，都为【" + epWith + "M】";
                                }
                            }
                        }
                    } else {
                        hint = "TD_S_COMMPARA表未找到产品【" + productId + "】宽带兆数对应关系！";
                    }
                }
            }

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

            IData hintData = new DataMap();
            hintData.put("HINT", hint);
            this.setAjax(hintData);
        }

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
        MinorecIntegrateTrans.transformCreateHotelIntegrationSubmitData(submmitParam);
    }

    public void qryEspCustCity(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String provinceId = data.getString("PROVINCE");
        IDataset cityList = StaticUtil.getList(getVisit(), "TD_S_STATIC", "DATA_ID", "DATA_NAME", new String[] { "TYPE_ID", "PDATA_ID" }, new String[] { "ESP_AREA_CODE", provinceId });
        setAjax(cityList);
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
        for (Object object : addressList1) {
            IData addressData = (IData) object;
            addressData.put("REGION_NAME", addressData.getString("STAND_ADDRESS"));
        }
        setWidenetInfos(addressList1);

    }

    public void querySynchrostateESP(IRequestCycle cycle) throws Exception {
        IData cond = new DataMap();
        cond.put("CUST_ID", getData().getString("CUST_ID"));
        IData result = CSViewCall.callone(this, "CC.group.IGroupQuerySV.queryGroupSynchrostateESP", cond);
        IDataset resultEsp = result.getDataset("DATAS");

        IData custinfo = new DataMap();
        if (resultEsp != null && resultEsp.size() != 0) {
            IData synchroinfo = (IData) resultEsp.get(0);
            custinfo.putAll(synchroinfo);
        } else {
            custinfo.put("SYN_ESP", "0");
            custinfo.put("SYN_ESPSTATE", "未同步");
        }
        setAjax(custinfo);
    }

    public void isPrivFiveDiscount(IRequestCycle cycle) throws Exception {
        /* 判断工号是否有套餐折扣权限 */
        String tradeStaffId = getVisit().getStaffId();
        IData data = getData();
        data.put("X_RESULTCODE", "0");
        if (StaffPrivUtil.isPriv(tradeStaffId, "PRIV_BROADBAND_DISCOUNT", "1")) {
            data.put("X_RESULTCODE", "00");
        }
        setAjax(data);
    }

    /**
     * 人像信息比对
     * 
     * @author chenhh6
     * @param clcle
     * @throws Exception
     */
    public void cmpPicInfo(IRequestCycle clcle) throws Exception {
        IData data = getData();
        IData param = new DataMap();

        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
    }

    /**
     * 身份证在线校验
     * 
     * @author chenhh6
     * @param clcle
     * @throws Exception
     */
    public void verifyIdCard(IRequestCycle clcle) throws Exception {
        IData data = getData();
        IData param = new DataMap();

        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyIdCard", param);
        setAjax(dataset.getData(0));
    }

    /**
     * 全网一证5号
     * 
     * @author chenhh6
     * @param cycle
     * @throws Exception
     */
    public void checkGlobalMorePsptId(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.checkGlobalMorePsptId", data);
        setAjax(dataset.getData(0));
    }

    /**
     * 营业执照验证
     * 
     * @author chenhh6
     * @param cycle
     * @throws Exception
     */
    public void verifyEnterpriseCard(IRequestCycle cycle) throws Exception {
        IData data = getData();
        System.out.println("chenhh==营业执照验证：" + data);
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyEnterpriseCard", data);
        System.out.println("chenhh==营业执照验证1：" + dataset);
        setAjax(dataset.getData(0));
    }

    /**
     * 组织机构代码证验证
     * 
     * @author chenhh6
     * @param cycle
     * @throws Exception
     */
    public void verifyOrgCard(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyOrgCard", data);
        setAjax(dataset.getData(0));
    }

    public void verifyIdCardName(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WideRealnameSupplementSVC.verifyIdCardName", data);
        setAjax(dataset.getData(0));
    }

    /**
     * 手动输入权限
     * 
     * @author chenhh6
     * @param cycle
     * @throws Exception
     */
    public void getInput(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put("X_RESULTCODE", "0");
        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "INPUT_PERMISSION")) {
            data.put("X_RESULTCODE", "00");
        }
        setAjax(data);
    }

    /**
     * 免人像比对权限
     * 
     * @author chenhh6
     * @param cycle
     * @throws Exception
     */
    public void getNotcmp(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put("X_RESULTCODE", "0");
        if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "NOTCMP_STFPRV")) {
            data.put("X_RESULTCODE", "00");
        }
        setAjax(data);
    }

    //集团号码同步ESP
    public void synESPInfoForESOP(IRequestCycle cycle) throws Exception {
    	 IData cond = new DataMap();
         cond.put("GROUP_ID", getData().getString("GROUP_ID"));
         cond.put("ESP_OPCODE", "1");
         IData results = CSViewCall.callone(this, "CC.group.IGroupOperateSV.synESPInfoForESOP", cond);
         IDataset resultEsp = results.getDataset("DATAS");
         
         IData custinfo = new DataMap();
         if (resultEsp != null && resultEsp.size() != 0) {
             IData synchroinfo = (IData) resultEsp.get(0);
             custinfo.putAll(synchroinfo);
         } else {
             custinfo.put("SUCCESS", "false");
             custinfo.put("SYN_ESPSTATE", "同步失败");
         }
         setAjax(custinfo);
    }
    
    public abstract void setSaleActiveList(IData saleActiveList) throws Exception;

    public abstract void setSaleActiveListAttr(IDataset saleActiveListAttr) throws Exception;

    public abstract void setMergeWideUserStyleList(IDataset mergeWideUserStyleList) throws Exception;

    public abstract void setWidenetPayModeList(IDataset widenetPayModeList) throws Exception;

    public abstract void setProductList(IDataset productList) throws Exception;

    public abstract void setOffer(IData offer) throws Exception;

    public abstract void setInfo(IData info) throws Exception;

    public abstract void setInfos(IDataset infos) throws Exception;

    public abstract void setComminfo(IData comminfo) throws Exception;

    public abstract void setBusi(IData busi) throws Exception;

    public abstract void setPattrInfo(IData pattrInfo) throws Exception;

    public abstract void setAgreementElement(IData pattrInfo) throws Exception;

    public abstract void setOfferCodeList(IDataset offerCodeList) throws Exception;

    public abstract void setWidenetInfos(IDataset widenetInfos) throws Exception;

    public abstract void setEcAccountList(IDataset ecAccountList) throws Exception;

    public abstract void setInAttr(IData inAttr) throws Exception;

    public abstract void setWidePhone(IData widePhone) throws Exception;

    public abstract void setOfferCodeListVw(IDataset offerCodeList) throws Exception;

    public abstract void setCustGroupList(IDataset custGroupList) throws Exception;

    public abstract void setCustGroupInfo(IData custGroupInfo) throws Exception;

}
